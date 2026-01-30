/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.jni2rust;

import com.caoccao.javet.swc4j.ast.Swc4jAstStore;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.comments.Swc4jComment;
import com.caoccao.javet.swc4j.comments.Swc4jComments;
import com.caoccao.javet.swc4j.options.*;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenFactory;
import com.caoccao.javet.swc4j.utils.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class TestCodeGen {
    @Test
    public void testEnum() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertThat(rustFile.exists()).isTrue();
        assertThat(rustFile.isFile()).isTrue();
        assertThat(rustFile.canRead()).isTrue();
        assertThat(rustFile.canWrite()).isTrue();
        String startSign = "\n/* Enum Begin */\n";
        String endSign = "/* Enum End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertThat(startPosition).as("Start position is invalid").isPositive();
        assertThat(endPosition).as("End position is invalid").isGreaterThan(startPosition);
        final AtomicInteger enumCounter = new AtomicInteger();
        final List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getEnumMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(Class::isInterface)
                .filter(clazz -> clazz.isAnnotationPresent(Jni2RustClass.class))
                .filter(clazz -> ArrayUtils.isNotEmpty(new Jni2RustClassUtils<>(clazz).getMappings()))
                .forEach(clazz -> {
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String enumName = jni2RustClassUtils.getName();
                    final List<String> registrationLines = new ArrayList<>();
                    final List<String> toJavaLines = new ArrayList<>();
                    final List<String> fromJavaLines = new ArrayList<>();
                    registrationLines.add(String.format("impl RegisterWithMap<ByteToIndexMap> for %s {", enumName));
                    registrationLines.add("  fn register_with_map<'local>(&self, map: &'_ mut ByteToIndexMap) {");
                    registrationLines.add("    match self {");
                    toJavaLines.add(String.format("impl ToJavaWithMap<ByteToIndexMap> for %s {", enumName));
                    toJavaLines.add("  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> Result<JObject<'a>>");
                    toJavaLines.add("  where");
                    toJavaLines.add("    'local: 'a,");
                    toJavaLines.add("  {");
                    toJavaLines.add("    match self {");
                    fromJavaLines.add(String.format("impl<'local> FromJava<'local> for %s {", enumName));
                    fromJavaLines.add("  #[allow(unused_variables)]");
                    fromJavaLines.add("  fn from_java(env: &mut JNIEnv<'local>, jobj: &JObject<'_>) -> Result<Box<Self>> {");
                    fromJavaLines.add("    let return_value = ");
                    final AtomicInteger mappingCounter = new AtomicInteger();
                    Stream.of(jni2RustClassUtils.getMappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertThat(clazz.isAssignableFrom(mapping.type()))
                                        .as(mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName())
                                        .isTrue();
                                Jni2RustClassUtils<?> mappingJni2RustClassUtils = new Jni2RustClassUtils<>(mapping.type());
                                registrationLines.add(String.format("      %s::%s(node) => node.register_with_map(map),",
                                        enumName,
                                        mapping.name()));
                                toJavaLines.add(String.format("      %s::%s(node) => node.to_java_with_map(env, map),",
                                        enumName,
                                        mapping.name()));
                                String rawMappingName = mappingJni2RustClassUtils.getRawName();
                                String mappingTypeName = mappingJni2RustClassUtils.getName();
                                String prefix = "";
                                if (mappingCounter.getAndIncrement() > 0) {
                                    prefix = "} else ";
                                }
                                fromJavaLines.add(String.format("      %sif env.is_instance_of(jobj, &(JAVA_CLASS_%s.get().unwrap().class)).unwrap_or(false) {",
                                        prefix,
                                        StringUtils.toSnakeCase(rawMappingName).toUpperCase()));
                                if (mapping.box()) {
                                    fromJavaLines.add(String.format("        %s::%s(%s::from_java(env, jobj)?)",
                                            enumName,
                                            mapping.name(),
                                            mappingTypeName));
                                } else {
                                    fromJavaLines.add(String.format("        %s::%s(*%s::from_java(env, jobj)?)",
                                            enumName,
                                            mapping.name(),
                                            mappingTypeName));
                                }
                            });
                    fromJavaLines.add("      } else {");
                    fromJavaLines.add("        let java_ast_type = JAVA_CLASS_.get().unwrap().get_type(env, jobj)?;");
                    fromJavaLines.add("        let ast_type = AstType::from_java(env, &java_ast_type);");
                    fromJavaLines.add("        delete_local_ref!(env, java_ast_type);");
                    fromJavaLines.add(String.format("        panic!(\"Type {:?} is not supported by %s\", ast_type);", enumName));
                    fromJavaLines.add("      };");
                    fromJavaLines.add("    Ok(Box::new(return_value))");
                    registrationLines.add("    }");
                    registrationLines.add("  }");
                    registrationLines.add("}\n");
                    lines.addAll(registrationLines);
                    toJavaLines.add("    }");
                    toJavaLines.add("  }");
                    toJavaLines.add("}\n");
                    lines.addAll(toJavaLines);
                    fromJavaLines.add("  }");
                    fromJavaLines.add("}\n");
                    lines.addAll(fromJavaLines);
                    enumCounter.incrementAndGet();
                });
        assertThat(enumCounter.get()).isPositive();
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        String code = StringUtils.join("\n", lines);
        sb.append(code);
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(rustFilePath, newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @Test
    public void testJNI() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertThat(rustFile.exists()).isTrue();
        assertThat(rustFile.isFile()).isTrue();
        assertThat(rustFile.canRead()).isTrue();
        assertThat(rustFile.canWrite()).isTrue();
        String startSign = "\n/* JNI Begin */\n";
        String endSign = "/* JNI End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertThat(startPosition).as("Start position is invalid").isPositive();
        assertThat(endPosition).as("End position is invalid").isGreaterThan(startPosition);
        final AtomicInteger enumCounter = new AtomicInteger();
        final AtomicInteger structCounter = new AtomicInteger();
        final List<String> lines = new ArrayList<>();
        final List<String> declarationLines = new ArrayList<>();
        final List<String> initLines = new ArrayList<>();
        final List<Class<?>> enumClasses = SimpleList.of(ISwc4jAst.class);
        Swc4jAstStore.getInstance().getEnumMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(Class::isInterface)
                .filter(clazz -> clazz.isAnnotationPresent(Jni2RustClass.class))
                .filter(clazz -> ArrayUtils.isNotEmpty(new Jni2RustClassUtils<>(clazz).getMappings()))
                .forEach(enumClasses::add);
        enumClasses.forEach(clazz -> {
            Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
            String className = clazz.getSimpleName();
            String enumName = jni2RustClassUtils.getName();
            Jni2Rust<?> jni2Rust = new Jni2Rust<>(clazz);
            lines.addAll(jni2Rust.getLines());
            lines.add("");
            declarationLines.add(String.format("static JAVA_CLASS_%s: OnceLock<Java%s> = OnceLock::new();",
                    StringUtils.toSnakeCase(enumName).toUpperCase(),
                    className));
            initLines.add(String.format("    JAVA_CLASS_%s.set(Java%s::new(env)).unwrap_unchecked();",
                    StringUtils.toSnakeCase(enumName).toUpperCase(),
                    className));
            enumCounter.incrementAndGet();
        });
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .forEach(clazz -> {
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String className = clazz.getSimpleName();
                    String structName = jni2RustClassUtils.getName();
                    Jni2Rust<?> jni2Rust = new Jni2Rust<>(clazz);
                    lines.addAll(jni2Rust.getLines());
                    lines.add("");
                    declarationLines.add(String.format("static JAVA_CLASS_%s: OnceLock<Java%s> = OnceLock::new();",
                            StringUtils.toSnakeCase(structName).toUpperCase(),
                            className));
                    initLines.add(String.format("    JAVA_CLASS_%s.set(Java%s::new(env)).unwrap_unchecked();",
                            StringUtils.toSnakeCase(structName).toUpperCase(),
                            className));
                    structCounter.incrementAndGet();
                });
        assertThat(structCounter.get()).isPositive();
        lines.addAll(declarationLines);
        lines.add("\npub fn init<'local>(env: &mut JNIEnv<'local>) {");
        lines.add("  log::debug!(\"init()\");");
        lines.add("  unsafe {");
        lines.addAll(initLines);
        lines.add("  }");
        lines.add("}\n");
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        String code = StringUtils.join("\n", lines);
        sb.append(code);
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(rustFilePath, newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @Test
    public void testStructRegistrationAndCreation() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertThat(rustFile.exists()).isTrue();
        assertThat(rustFile.isFile()).isTrue();
        assertThat(rustFile.canRead()).isTrue();
        assertThat(rustFile.canWrite()).isTrue();
        String startSign = "\n/* AST Begin */\n";
        String endSign = "/* AST End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertThat(startPosition).as("Start position is invalid").isPositive();
        assertThat(endPosition).as("End position is invalid").isGreaterThan(startPosition);
        final AtomicInteger structCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .forEach(clazz -> {
                    Constructor<?>[] constructors = clazz.getConstructors();
                    assertThat(constructors.length).isEqualTo(1);
                    Constructor<?> constructor = constructors[0];
                    final Map<String, Integer> fieldOrderMap = new HashMap<>(constructor.getParameterCount());
                    int fieldOrder = 0;
                    for (Parameter parameter : constructor.getParameters()) {
                        fieldOrderMap.put(parameter.getName(), fieldOrder);
                        ++fieldOrder;
                    }
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String className = jni2RustClassUtils.getName();
                    String rawName = jni2RustClassUtils.getRawName();
                    String spanCall = jni2RustClassUtils.isSpan() ? "" : "()";
                    // Registration
                    lines.add(String.format("impl RegisterWithMap<ByteToIndexMap> for %s {", className));
                    lines.add("  fn register_with_map<'local>(&self, map: &'_ mut ByteToIndexMap) {");
                    lines.add(String.format("    map.register_by_span(&self.span%s);", spanCall));
                    ReflectionUtils.getDeclaredFields(clazz).values().stream()
                            .filter(field -> !Modifier.isStatic(field.getModifiers()))
                            .filter(field -> !new Jni2RustFieldUtils(field).isIgnore())
                            .sorted(Comparator.comparingInt(field -> fieldOrderMap.getOrDefault(field.getName(), Integer.MAX_VALUE)))
                            .forEach(field -> {
                                Jni2RustFieldUtils jni2RustFieldUtils = new Jni2RustFieldUtils(field);
                                String fieldName = jni2RustFieldUtils.getName();
                                Class<?> fieldType = field.getType();
                                String arg = StringUtils.toSnakeCase(fieldName);
                                if (Optional.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            if (ISwc4jAst.class.isAssignableFrom((Class<?>) innerType)) {
                                                lines.add(String.format("    self.%s.as_ref().map(|node| node.register_with_map(map));",
                                                        arg));
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertThat(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertThat(innerType2).isInstanceOf(Class.class);
                                            lines.add(String.format("    self.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| node.register_with_map(map)));",
                                                    arg));
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (List.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        lines.add(String.format("    for node in self.%s.iter() {", arg));
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            lines.add("      node.register_with_map(map);");
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertThat(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertThat(innerType2).isInstanceOf(Class.class);
                                            lines.add("      node.as_ref().map(|node| node.register_with_map(map));");
                                        } else {
                                            fail(innerType.getTypeName() + " is not expected");
                                        }
                                        lines.add("    }");
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    self.%s.register_with_map(map);", arg));
                                } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    map.register_by_span(&self.%s);", arg));
                                }
                            });
                    lines.add("  }");
                    lines.add("}\n");
                    // AST
                    if (!jni2RustClassUtils.isCustomToJava()) {
                        final List<String> args = new ArrayList<>();
                        final List<String> javaVars = new ArrayList<>();
                        final List<String> javaOptionalVars = new ArrayList<>();
                        lines.add(String.format("impl ToJavaWithMap<ByteToIndexMap> for %s {", className));
                        lines.add("  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> Result<JObject<'a>>");
                        lines.add("  where");
                        lines.add("    'local: 'a,");
                        lines.add("  {");
                        lines.add(String.format("    let java_span_ex = map.get_span_ex_by_span(&self.span%s).to_java(env)?;", spanCall));
                        ReflectionUtils.getDeclaredFields(clazz).values().stream()
                                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                .filter(field -> !new Jni2RustFieldUtils(field).isIgnore())
                                .sorted(Comparator.comparingInt(field -> fieldOrderMap.getOrDefault(field.getName(), Integer.MAX_VALUE)))
                                .forEach(field -> {
                                    Jni2RustFieldUtils jni2RustFieldUtils = new Jni2RustFieldUtils(field);
                                    String fieldName = jni2RustFieldUtils.getName();
                                    Class<?> fieldType = field.getType();
                                    String arg = StringUtils.toSnakeCase(fieldName);
                                    if (Optional.class.isAssignableFrom(fieldType)) {
                                        if (field.getGenericType() instanceof ParameterizedType) {
                                            Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                            if (innerType instanceof Class<?> innerClass) {
                                                if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String javaOptionalVar = String.format("java_optional_%s", arg);
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = match self.%s.as_ref() {",
                                                            javaOptionalVar,
                                                            arg));
                                                    lines.add("      Some(node) => Some(node.to_java_with_map(env, map)?),");
                                                    lines.add("      None => None,");
                                                    lines.add("    };");
                                                } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                                    String javaOptionalVar = String.format("java_optional_%s", arg);
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = match self.%s.as_ref() {",
                                                            javaOptionalVar,
                                                            arg));
                                                    lines.add("      Some(node) => Some(map.get_span_ex_by_span(node).to_java(env)?),");
                                                    lines.add("      None => None,");
                                                    lines.add("    };");
                                                } else if (innerClass == String.class) {
                                                    String optionalVar = String.format("optional_%s", arg);
                                                    args.add("&" + optionalVar);
                                                    lines.add(String.format("    let %s = self.%s.as_ref().map(|node| node.to_string());",
                                                            optionalVar,
                                                            arg));
                                                } else if (innerClass.isEnum()) {
                                                    String javaOptionalVar = String.format("java_optional_%s", arg);
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = match self.%s.as_ref() {",
                                                            javaOptionalVar,
                                                            arg));
                                                    lines.add("      Some(node) => Some(node.to_java(env)?),");
                                                    lines.add("      None => None,");
                                                    lines.add("    };");
                                                } else {
                                                    fail(field.getGenericType().getTypeName() + " is not expected");
                                                }
                                            } else if (innerType instanceof ParameterizedType) {
                                                assertThat(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                                Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                                assertThat(innerType2).isInstanceOf(Class.class);
                                                String javaOptionalVar = String.format("java_optional_%s", arg);
                                                args.add("&" + javaOptionalVar);
                                                javaOptionalVars.add(javaOptionalVar);
                                                lines.add(String.format("    let %s = match self.%s.as_ref() {",
                                                        javaOptionalVar,
                                                        arg));
                                                lines.add("      Some(nodes) => {");
                                                lines.add(String.format("        let java_%s = list_new(env, nodes.len())?;",
                                                        arg));
                                                lines.add("        for node in nodes.iter() {");
                                                lines.add("          let java_node = node.to_java_with_map(env, map)?;");
                                                lines.add(String.format("          list_add(env, &java_%s, &java_node)?;",
                                                        arg));
                                                lines.add("          delete_local_ref!(env, java_node);");
                                                lines.add("        }");
                                                lines.add(String.format("        Some(java_%s)", arg));
                                                lines.add("      }");
                                                lines.add("      None => None,");
                                                lines.add("    };");
                                            } else {
                                                fail(field.getGenericType().getTypeName() + " is not expected");
                                            }
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else if (List.class.isAssignableFrom(fieldType)) {
                                        if (field.getGenericType() instanceof ParameterizedType) {
                                            String javaVar = String.format("java_%s", arg);
                                            args.add("&" + javaVar);
                                            javaVars.add(javaVar);
                                            lines.add(String.format("    let %s = list_new(env, self.%s.len())?;",
                                                    javaVar,
                                                    arg));
                                            lines.add(String.format("    for node in self.%s.iter() {",
                                                    arg));
                                            Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                            if (innerType instanceof Class<?> innerClass) {
                                                if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                    lines.add("      let java_node = node.to_java_with_map(env, map)?;");
                                                    lines.add(String.format("      list_add(env, &%s, &java_node)?;", javaVar));
                                                    lines.add("      delete_local_ref!(env, java_node);");
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else if (innerType instanceof ParameterizedType) {
                                                assertThat(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                                Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                                assertThat(innerType2).isInstanceOf(Class.class);
                                                lines.add("      let java_node = match node.as_ref() {");
                                                lines.add("        Some(node) => node.to_java_with_map(env, map)?,");
                                                lines.add("        None => Default::default(),");
                                                lines.add("      };");
                                                lines.add(String.format("      list_add(env, &%s, &java_node)?;", javaVar));
                                                lines.add("      delete_local_ref!(env, java_node);");
                                            } else {
                                                fail(innerType.getTypeName() + " is not expected");
                                            }
                                            lines.add("    }");
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                        String javaVar = String.format("java_%s", arg);
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = self.%s.to_java_with_map(env, map)?;",
                                                javaVar,
                                                arg));
                                    } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                        String javaVar = String.format("java_%s", arg);
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = map.get_span_ex_by_span(&self.%s).to_java(env)?;",
                                                javaVar,
                                                arg));
                                    } else if (fieldType.isPrimitive()) {
                                        args.add(arg);
                                        lines.add(String.format("    let %s = self.%s;", arg, arg));
                                    } else if (fieldType == String.class) {
                                        args.add(arg);
                                        lines.add(String.format("    let %s = self.%s.as_str();", arg, arg));
                                    } else if (fieldType.isEnum()) {
                                        String javaVar = String.format("java_%s", arg);
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = self.%s.to_java(env)?;",
                                                javaVar,
                                                arg));
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                });
                        args.add("&java_span_ex");
                        javaVars.add("java_span_ex");
                        lines.add(String.format("    let return_value = JAVA_CLASS_%s.get().unwrap()",
                                StringUtils.toSnakeCase(jni2RustClassUtils.getName()).toUpperCase()));
                        lines.add(String.format("      .construct(env, %s)?;",
                                StringUtils.join(", ", args)));
                        javaOptionalVars.forEach(javaOptionalVar -> lines.add(String.format("    delete_local_optional_ref!(env, %s);", javaOptionalVar)));
                        javaVars.forEach(javaVar -> lines.add(String.format("    delete_local_ref!(env, %s);", javaVar)));
                        lines.add("    Ok(return_value)");
                        lines.add("  }");
                        lines.add("}\n");
                    }
                    if (!jni2RustClassUtils.isCustomFromJava()) {
                        lines.add(String.format("impl<'local> FromJava<'local> for %s {", className));
                        lines.add("  #[allow(unused_variables)]");
                        lines.add("  fn from_java(env: &mut JNIEnv<'local>, jobj: &JObject<'_>) -> Result<Box<Self>> {");
                        List<Field> fields = ReflectionUtils.getDeclaredFields(clazz).values().stream()
                                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                .filter(field -> !new Jni2RustFieldUtils(field).isIgnore())
                                .sorted(Comparator.comparingInt(field -> fieldOrderMap.getOrDefault(field.getName(), Integer.MAX_VALUE)))
                                .toList();
                        final List<String> processLines = new ArrayList<>();
                        final List<String> initLines = new ArrayList<>();
                        if (!fields.isEmpty()) {
                            lines.add(String.format("    let java_class = JAVA_CLASS_%s.get().unwrap();",
                                    StringUtils.toSnakeCase(rawName).toUpperCase()));
                        }
                        if (jni2RustClassUtils.isSpan()) {
                            processLines.add("    let span = DUMMY_SP;");
                            initLines.add("      span,");
                        }
                        fields.forEach(field -> {
                            Jni2RustFieldUtils jni2RustFieldUtils = new Jni2RustFieldUtils(field);
                            String fieldName = jni2RustFieldUtils.getName();
                            Class<?> fieldType = field.getType();
                            String getterName = field.getName();
                            if (getterName.startsWith("_")) {
                                getterName = getterName.substring(1);
                            }
                            getterName = (fieldType == boolean.class ? "is" : "get") + "_" + StringUtils.toSnakeCase(getterName);
                            String arg = StringUtils.toSnakeCase(fieldName);
                            String javaVar = String.format("java_%s", arg);
                            if (Optional.class.isAssignableFrom(fieldType)) {
                                String javaOptionalVar = String.format("java_optional_%s", arg);
                                if (field.getGenericType() instanceof ParameterizedType) {
                                    Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                    processLines.add(String.format("    let %s = java_class.%s(env, jobj)?;",
                                            javaOptionalVar,
                                            getterName));
                                    processLines.add(String.format("    let %s = if optional_is_present(env, &%s)? {",
                                            arg,
                                            javaOptionalVar));
                                    if (innerType instanceof Class<?> innerClass) {
                                        if (ISwc4jAst.class.isAssignableFrom(innerClass) || innerClass.isEnum()) {
                                            processLines.add(String.format("      let %s = optional_get(env, &%s)?;",
                                                    javaVar,
                                                    javaOptionalVar));
                                            processLines.add(String.format("      let %s = *%s::from_java(env, &%s)?;",
                                                    arg,
                                                    new Jni2RustClassUtils<>(innerClass).getName(),
                                                    javaVar));
                                            processLines.add(String.format("      delete_local_ref!(env, %s);",
                                                    javaVar));
                                            processLines.add(String.format("      Some(%s)", arg));
                                        } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                            processLines.add("      Some(DUMMY_SP)");
                                        } else if (innerClass == String.class) {
                                            processLines.add(String.format("      let %s = optional_get(env, &%s)?;",
                                                    javaVar,
                                                    javaOptionalVar));
                                            processLines.add(String.format("      let %s: Result<String> = jstring_to_string!(env, %s.as_raw());",
                                                    arg,
                                                    javaVar));
                                            processLines.add(String.format("      let %s = %s?;",
                                                    arg,
                                                    arg));
                                            processLines.add(String.format("      delete_local_ref!(env, %s);",
                                                    javaVar));
                                            processLines.add(String.format("      Some(%s)", arg));
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else if (innerType instanceof ParameterizedType) {
                                        assertThat(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                        Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                        assertThat(innerType2).isInstanceOf(Class.class);
                                        Class<?> innerClass = (Class<?>) innerType2;
                                        Jni2RustClassUtils<?> innerJni2RustClassUtils = new Jni2RustClassUtils<>(innerClass);
                                        processLines.add(String.format("      let %s = optional_get(env, &%s)?;",
                                                javaVar,
                                                javaOptionalVar));
                                        processLines.add(String.format("      let length = list_size(env, &%s)?;",
                                                javaVar));
                                        processLines.add(String.format("      let mut %s: Vec<%s> = Vec::with_capacity(length);",
                                                arg,
                                                innerJni2RustClassUtils.getName()));
                                        processLines.add("      for i in 0..length {");
                                        processLines.add(String.format("        let java_item = list_get(env, &%s, i)?;",
                                                javaVar));
                                        processLines.add(String.format("        let element = *%s::from_java(env, &java_item)?;",
                                                innerJni2RustClassUtils.getName()));
                                        processLines.add("        delete_local_ref!(env, java_item);");
                                        processLines.add(String.format("      %s.push(element);", arg));
                                        processLines.add("      }");
                                        processLines.add(String.format("      Some(%s)", arg));
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                    processLines.add("    } else {");
                                    processLines.add("      None");
                                    processLines.add("    };");
                                    processLines.add(String.format("    delete_local_ref!(env, %s);",
                                            javaOptionalVar));
                                } else {
                                    fail(field.getGenericType().getTypeName() + " is not expected");
                                }
                                if (jni2RustFieldUtils.isComponentBox()) {
                                    if (jni2RustFieldUtils.isComponentAtom()) {
                                        processLines.add(String.format("    let %s = %s.map(|%s| Box::new(%s.into()));",
                                                arg, arg, arg, arg));
                                    } else {
                                        processLines.add(String.format("    let %s = %s.map(|%s| Box::new(%s));",
                                                arg, arg, arg, arg));
                                    }
                                } else if (jni2RustFieldUtils.isComponentAtom()) {
                                    processLines.add(String.format("    let %s = %s.map(|%s| %s.into());",
                                            arg, arg, arg, arg));
                                }
                            } else if (List.class.isAssignableFrom(fieldType)) {
                                if (field.getGenericType() instanceof ParameterizedType) {
                                    processLines.add(String.format("    let %s = java_class.%s(env, jobj)?;",
                                            javaVar,
                                            getterName));
                                    processLines.add(String.format("    let length = list_size(env, &%s)?;",
                                            javaVar));
                                    Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                    if (innerType instanceof Class<?> innerClass) {
                                        if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                            Jni2RustClassUtils<?> innerJni2RustClassUtils = new Jni2RustClassUtils<>(innerClass);
                                            processLines.add(String.format("    let mut %s: Vec<%s> = Vec::with_capacity(length);",
                                                    arg,
                                                    innerJni2RustClassUtils.getName()));
                                            processLines.add("    for i in 0..length {");
                                            processLines.add(String.format("      let java_item = list_get(env, &%s, i)?;",
                                                    javaVar));
                                            processLines.add(String.format("      let element = *%s::from_java(env, &java_item)?;",
                                                    innerJni2RustClassUtils.getName()));
                                            processLines.add("      delete_local_ref!(env, java_item);");
                                            processLines.add(String.format("      %s.push(element)", arg));
                                            processLines.add("    }");
                                        } else {
                                            fail(innerClass.getName() + " is not expected");
                                        }
                                    } else if (innerType instanceof ParameterizedType) {
                                        assertThat(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType())).isTrue();
                                        Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                        assertThat(innerType2).isInstanceOf(Class.class);
                                        Class<?> innerClass = (Class<?>) innerType2;
                                        Jni2RustClassUtils<?> innerJni2RustClassUtils = new Jni2RustClassUtils<>(innerClass);
                                        processLines.add(String.format("    let mut %s: Vec<Option<%s>> = Vec::with_capacity(length);",
                                                arg,
                                                innerJni2RustClassUtils.getName()));
                                        processLines.add("    for i in 0..length {");
                                        processLines.add(String.format("      let java_item = list_get(env, &%s, i)?;",
                                                javaVar));
                                        processLines.add("      let element = if optional_is_present(env, &java_item)? {");
                                        processLines.add("        let java_inner_item = optional_get(env, &java_item)?;");
                                        processLines.add(String.format("        let element = *%s::from_java(env, &java_inner_item)?;",
                                                innerJni2RustClassUtils.getName()));
                                        processLines.add("        delete_local_ref!(env, java_inner_item);");
                                        processLines.add("        Some(element)");
                                        processLines.add("      } else {");
                                        processLines.add("        None");
                                        processLines.add("      };");
                                        processLines.add("      delete_local_ref!(env, java_item);");
                                        processLines.add(String.format("      %s.push(element)", arg));
                                        processLines.add("    }");
                                    } else {
                                        fail(innerType.getTypeName() + " is not expected");
                                    }
                                    if (jni2RustFieldUtils.isComponentBox()) {
                                        if (jni2RustFieldUtils.isComponentAtom()) {
                                            processLines.add(String.format("    let %s = %s.into_iter().map(|%s| Box::new(%s.into())).collect();",
                                                    arg, arg, arg, arg));
                                        } else {
                                            processLines.add(String.format("    let %s = %s.into_iter().map(|%s| Box::new(%s)).collect();",
                                                    arg, arg, arg, arg));
                                        }
                                    } else if (jni2RustFieldUtils.isComponentAtom()) {
                                        processLines.add(String.format("    let %s = %s.into_iter().map(|%s| %s.into()).collect();",
                                                arg, arg, arg, arg));
                                    }
                                } else {
                                    fail(field.getGenericType().getTypeName() + " is not expected");
                                }
                            } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                processLines.add(String.format("    let %s = java_class.%s(env, jobj)?;",
                                        javaVar,
                                        getterName));
                                processLines.add(String.format("    let %s = *%s::from_java(env, &%s)?;",
                                        arg,
                                        new Jni2RustClassUtils<>(fieldType).getName(),
                                        javaVar));
                                processLines.add(String.format("    delete_local_ref!(env, %s);", javaVar));
                            } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                processLines.add(String.format("    let %s = DUMMY_SP;", arg));
                            } else if (fieldType.isPrimitive() || fieldType == String.class) {
                                processLines.add(String.format("    let %s = java_class.%s(env, jobj)?;",
                                        arg,
                                        getterName));
                                if (fieldType == int.class && jni2RustFieldUtils.isSyntaxContext()) {
                                    processLines.add(String.format("    let %s = SyntaxContext::from_u32(%s as u32);", arg, arg));
                                }
                            } else if (fieldType.isEnum()) {
                                processLines.add(String.format("    let %s = java_class.%s(env, jobj)?;",
                                        javaVar,
                                        getterName));
                                processLines.add(String.format("    let %s = *%s::from_java(env, &%s)?;",
                                        arg,
                                        new Jni2RustClassUtils<>(fieldType).getName(),
                                        javaVar));
                                processLines.add(String.format("    delete_local_ref!(env, %s);", javaVar));
                            } else {
                                fail(field.getGenericType().getTypeName() + " is not expected");
                            }
                            if (jni2RustFieldUtils.isBox()) {
                                if (jni2RustFieldUtils.isAtom()) {
                                    processLines.add(String.format("    let %s = Box::new(%s.into());", arg, arg));
                                } else {
                                    processLines.add(String.format("    let %s = Box::new(%s);", arg, arg));
                                }
                            } else if (jni2RustFieldUtils.isAtom()) {
                                processLines.add(String.format("    let %s = %s.into();", arg, arg));
                            }
                            initLines.add(String.format("      %s,", arg));
                        });
                        lines.addAll(processLines);
                        lines.add(String.format("    Ok(Box::new(%s {", className));
                        lines.addAll(initLines);
                        lines.add("    }))");
                        lines.add("  }");
                        lines.add("}\n");
                    }
                    structCounter.incrementAndGet();
                });
        assertThat(structCounter.get()).isPositive();
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        String code = StringUtils.join("\n", lines);
        sb.append(code);
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(rustFilePath, newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @Test
    public void testSwc4jComment() throws IOException {
        Jni2Rust<Swc4jComment> jni2Rust = new Jni2Rust<>(Swc4jComment.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jComments() throws IOException {
        Jni2Rust<Swc4jComments> jni2Rust = new Jni2Rust<>(Swc4jComments.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jParseOptions() throws IOException {
        Jni2Rust<Swc4jParseOptions> jni2Rust = new Jni2Rust<>(Swc4jParseOptions.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jParseOutput() throws IOException {
        Jni2Rust<Swc4jParseOutput> jni2Rust = new Jni2Rust<>(Swc4jParseOutput.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jPluginHost() throws IOException {
        Jni2Rust<ISwc4jPluginHost> jni2Rust = new Jni2Rust<>(ISwc4jPluginHost.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jSpan() throws IOException {
        Jni2Rust<Swc4jSpan> jni2Rust = new Jni2Rust<>(Swc4jSpan.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTokenFactory() throws IOException {
        Jni2Rust<Swc4jTokenFactory> jni2Rust = new Jni2Rust<>(Swc4jTokenFactory.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTransformOptions() throws IOException {
        Jni2Rust<Swc4jTransformOptions> jni2Rust = new Jni2Rust<>(Swc4jTransformOptions.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTransformOutput() throws IOException {
        Jni2Rust<Swc4jTransformOutput> jni2Rust = new Jni2Rust<>(Swc4jTransformOutput.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTranspileOptions() throws IOException {
        for (Class<?> clazz : SimpleList.of(
                Swc4jTranspileOptions.class,
                Swc4jDecoratorsTranspileOptionNone.class,
                Swc4jDecoratorsTranspileOptionEcma.class,
                Swc4jDecoratorsTranspileOptionLegacyTypeScript.class,
                Swc4jJsxRuntimeOptionAutomatic.class,
                Swc4jJsxRuntimeOptionClassic.class,
                Swc4jJsxRuntimeOptionPrecompile.class)) {
            Jni2Rust<?> jni2Rust = new Jni2Rust<>(clazz);
            jni2Rust.updateFile();
        }
    }

    @Test
    public void testSwc4jTranspileOutput() throws IOException {
        Jni2Rust<Swc4jTranspileOutput> jni2Rust = new Jni2Rust<>(Swc4jTranspileOutput.class);
        jni2Rust.updateFile();
    }
}
