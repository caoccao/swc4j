/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenFactory;
import com.caoccao.javet.swc4j.utils.ArrayUtils;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.ReflectionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
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

import static org.junit.jupiter.api.Assertions.*;

public class TestCodeGen {
    @Test
    public void testEnum() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n/* Enum Begin */\n";
        String endSign = "/* Enum End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger enumCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getEnumMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(entry -> entry.getValue().isInterface())
                .filter(entry -> entry.getValue().isAnnotationPresent(Jni2RustClass.class))
                .filter(entry -> ArrayUtils.isNotEmpty(new Jni2RustClassUtils<>(entry.getValue()).getMappings()))
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String enumName = jni2RustClassUtils.getName();
                    // Span
                    lines.add(String.format("impl RegisterWithMap<ByteToIndexMap> for %s {", enumName));
                    lines.add("  fn register_with_map<'local>(&self, map: &'_ mut ByteToIndexMap) {");
                    lines.add("    match self {");
                    Stream.of(jni2RustClassUtils.getMappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                lines.add(String.format("      %s::%s(node) => node.register_with_map(map),",
                                        enumName,
                                        mapping.name()));
                            });
                    lines.add("    }");
                    lines.add("  }");
                    lines.add("}\n");
                    // AST
                    lines.add(String.format("impl ToJavaWithMap<ByteToIndexMap> for %s {", enumName));
                    lines.add("  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> JObject<'a>");
                    lines.add("  where");
                    lines.add("    'local: 'a,");
                    lines.add("  {");
                    lines.add("    match self {");
                    Stream.of(jni2RustClassUtils.getMappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                lines.add(String.format("      %s::%s(node) => node.to_java_with_map(env, map),",
                                        enumName,
                                        mapping.name()));
                            });
                    lines.add("    }");
                    lines.add("  }");
                    lines.add("}\n");
                    enumCounter.incrementAndGet();
                });
        assertTrue(enumCounter.get() > 0);
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
    public void testStructJNI() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n/* JNI Begin */\n";
        String endSign = "/* JNI End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger structCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        List<String> declarationLines = new ArrayList<>();
        List<String> initLines = new ArrayList<>();
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Constructor<?>[] constructors = clazz.getConstructors();
                    assertEquals(1, constructors.length);
                    Constructor<?> constructor = constructors[0];
                    final Map<String, Integer> fieldOrderMap = new HashMap<>(constructor.getParameterCount());
                    int fieldOrder = 0;
                    for (Parameter parameter : constructor.getParameters()) {
                        fieldOrderMap.put(parameter.getName(), fieldOrder);
                        ++fieldOrder;
                    }
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String className = clazz.getSimpleName();
                    String structName = jni2RustClassUtils.getName();
                    Jni2Rust<?> jni2Rust = new Jni2Rust<>(clazz);
                    lines.addAll(jni2Rust.getLines());
                    lines.add("");
                    declarationLines.add(String.format("static mut JAVA_CLASS_%s: Option<Java%s> = None;",
                            StringUtils.toSnakeCase(structName).toUpperCase(),
                            className));
                    initLines.add(String.format("    JAVA_CLASS_%s = Some(Java%s::new(env));",
                            StringUtils.toSnakeCase(structName).toUpperCase(),
                            className));
                    structCounter.incrementAndGet();
                });
        lines.addAll(declarationLines);
        lines.add("\npub fn init<'local>(env: &mut JNIEnv<'local>) {");
        lines.add("  unsafe {");
        lines.addAll(initLines);
        lines.add("  }");
        lines.add("}\n");
        assertTrue(structCounter.get() > 0);
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
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n/* Node Begin */\n";
        String endSign = "/* Node End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger structCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Constructor<?>[] constructors = clazz.getConstructors();
                    assertEquals(1, constructors.length);
                    Constructor<?> constructor = constructors[0];
                    final Map<String, Integer> fieldOrderMap = new HashMap<>(constructor.getParameterCount());
                    int fieldOrder = 0;
                    for (Parameter parameter : constructor.getParameters()) {
                        fieldOrderMap.put(parameter.getName(), fieldOrder);
                        ++fieldOrder;
                    }
                    Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(clazz);
                    String enumName = jni2RustClassUtils.getName();
                    String spanCall = jni2RustClassUtils.isSpan() ? "" : "()";
                    // Span
                    lines.add(String.format("impl RegisterWithMap<ByteToIndexMap> for %s {", enumName));
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
                                if (Optional.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            if (ISwc4jAst.class.isAssignableFrom((Class<?>) innerType)) {
                                                lines.add(String.format("    self.%s.as_ref().map(|node| node.register_with_map(map));",
                                                        StringUtils.toSnakeCase(fieldName)));
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            lines.add(String.format("    self.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| node.register_with_map(map)));",
                                                    StringUtils.toSnakeCase(fieldName)));
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (List.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        lines.add(String.format("    self.%s.iter().for_each(|node| {",
                                                StringUtils.toSnakeCase(fieldName)));
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            lines.add("      node.register_with_map(map);");
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            lines.add("      node.as_ref().map(|node| node.register_with_map(map));");
                                        } else {
                                            fail(innerType.getTypeName() + " is not expected");
                                        }
                                        lines.add("    });");
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    self.%s.register_with_map(map);",
                                            StringUtils.toSnakeCase(fieldName)));
                                } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    map.register_by_span(&self.%s);",
                                            StringUtils.toSnakeCase(fieldName)));
                                }
                            });
                    lines.add("  }");
                    lines.add("}\n");
                    // AST
                    if (!jni2RustClassUtils.isCustomCreation()) {
                        List<String> args = new ArrayList<>();
                        List<String> javaVars = new ArrayList<>();
                        List<String> javaOptionalVars = new ArrayList<>();
                        lines.add(String.format("impl ToJavaWithMap<ByteToIndexMap> for %s {", enumName));
                        lines.add("  fn to_java_with_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, map: &'_ ByteToIndexMap) -> JObject<'a>");
                        lines.add("  where");
                        lines.add("    'local: 'a,");
                        lines.add("  {");
                        lines.add(String.format("    let java_span_ex = map.get_span_ex_by_span(&self.span%s).to_java(env);", spanCall));
                        ReflectionUtils.getDeclaredFields(clazz).values().stream()
                                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                .filter(field -> !new Jni2RustFieldUtils(field).isIgnore())
                                .sorted(Comparator.comparingInt(field -> fieldOrderMap.getOrDefault(field.getName(), Integer.MAX_VALUE)))
                                .forEach(field -> {
                                    Jni2RustFieldUtils jni2RustFieldUtils = new Jni2RustFieldUtils(field);
                                    String fieldName = jni2RustFieldUtils.getName();
                                    Class<?> fieldType = field.getType();
                                    if (Optional.class.isAssignableFrom(fieldType)) {
                                        if (field.getGenericType() instanceof ParameterizedType) {
                                            Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                            if (innerType instanceof Class) {
                                                Class<?> innerClass = (Class<?>) innerType;
                                                if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String javaOptionalVar = String.format("java_optional_%s", StringUtils.toSnakeCase(fieldName));
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = self.%s.as_ref().map(|node| node.to_java_with_map(env, map));",
                                                            javaOptionalVar,
                                                            StringUtils.toSnakeCase(fieldName)));
                                                } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                                    String javaOptionalVar = String.format("java_optional_%s", StringUtils.toSnakeCase(fieldName));
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = self.%s.as_ref().map(|node| map.get_span_ex_by_span(node).to_java(env));",
                                                            javaOptionalVar,
                                                            StringUtils.toSnakeCase(fieldName)));
                                                } else if (innerClass == String.class) {
                                                    String optionalVar = String.format("optional_%s", StringUtils.toSnakeCase(fieldName));
                                                    args.add("&" + optionalVar);
                                                    lines.add(String.format("    let %s = self.%s.as_ref().map(|node| node.to_string());",
                                                            optionalVar,
                                                            StringUtils.toSnakeCase(fieldName)));
                                                } else if (innerClass.isEnum()) {
                                                    String javaOptionalVar = String.format("java_optional_%s", StringUtils.toSnakeCase(fieldName));
                                                    args.add("&" + javaOptionalVar);
                                                    javaOptionalVars.add(javaOptionalVar);
                                                    lines.add(String.format("    let %s = self.%s.as_ref().map(|node| node.to_java(env));",
                                                            javaOptionalVar,
                                                            StringUtils.toSnakeCase(fieldName)));
                                                } else {
                                                    fail(field.getGenericType().getTypeName() + " is not expected");
                                                }
                                            } else if (innerType instanceof ParameterizedType) {
                                                assertTrue(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                                Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                                assertInstanceOf(Class.class, innerType2);
                                                Class<?> innerClass2 = (Class<?>) innerType2;
                                                String javaOptionalVar = String.format("java_optional_%s", StringUtils.toSnakeCase(fieldName));
                                                args.add("&" + javaOptionalVar);
                                                javaOptionalVars.add(javaOptionalVar);
                                                lines.add(String.format("    let %s = self.%s.as_ref().map(|nodes| {",
                                                        javaOptionalVar,
                                                        StringUtils.toSnakeCase(fieldName)));
                                                lines.add(String.format("      let java_%s = list_new(env, nodes.len());",
                                                        StringUtils.toSnakeCase(fieldName)));
                                                lines.add("      nodes.iter().for_each(|node| {");
                                                lines.add("        let java_node = node.to_java_with_map(env, map);");
                                                lines.add(String.format("        list_add(env, &java_%s, &java_node);",
                                                        StringUtils.toSnakeCase(fieldName)));
                                                lines.add("        delete_local_ref!(env, java_node);");
                                                lines.add("      });");
                                                lines.add(String.format("      java_%s",
                                                        StringUtils.toSnakeCase(fieldName)));
                                                lines.add("    });");
                                            } else {
                                                fail(field.getGenericType().getTypeName() + " is not expected");
                                            }
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else if (List.class.isAssignableFrom(fieldType)) {
                                        if (field.getGenericType() instanceof ParameterizedType) {
                                            String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                            args.add("&" + javaVar);
                                            javaVars.add(javaVar);
                                            lines.add(String.format("    let %s = list_new(env, self.%s.len());",
                                                    javaVar,
                                                    StringUtils.toSnakeCase(fieldName)));
                                            lines.add(String.format("    self.%s.iter().for_each(|node| {",
                                                    StringUtils.toSnakeCase(fieldName)));
                                            Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                            if (innerType instanceof Class) {
                                                Class<?> innerClass = (Class<?>) innerType;
                                                if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                    lines.add("      let java_node = node.to_java_with_map(env, map);");
                                                    lines.add(String.format("      list_add(env, &%s, &java_node);", javaVar));
                                                    lines.add("      delete_local_ref!(env, java_node);");
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else if (innerType instanceof ParameterizedType) {
                                                assertTrue(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                                Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                                assertInstanceOf(Class.class, innerType2);
                                                lines.add("      let java_node = node.as_ref().map_or_else(");
                                                lines.add("        || Default::default(),");
                                                lines.add("        |node| node.to_java_with_map(env, map));");
                                                lines.add(String.format("    list_add(env, &%s, &java_node);", javaVar));
                                                lines.add("      delete_local_ref!(env, java_node);");
                                            } else {
                                                fail(innerType.getTypeName() + " is not expected");
                                            }
                                            lines.add("    });");
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                        String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = self.%s.to_java_with_map(env, map);",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                        String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = map.get_span_ex_by_span(&self.%s).to_java(env);",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else if (fieldType.isPrimitive()) {
                                        String arg = StringUtils.toSnakeCase(fieldName);
                                        args.add(arg);
                                        lines.add(String.format("    let %s = self.%s;", arg, arg));
                                    } else if (fieldType == String.class) {
                                        String arg = StringUtils.toSnakeCase(fieldName);
                                        args.add(arg);
                                        lines.add(String.format("    let %s = self.%s.as_str();", arg, arg));
                                    } else if (fieldType.isEnum()) {
                                        String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        lines.add(String.format("    let %s = self.%s.to_java(env);",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                });
                        args.add("&java_span_ex");
                        javaVars.add("java_span_ex");
                        lines.add(String.format("    let return_value = unsafe { JAVA_CLASS_%s.as_ref().unwrap() }",
                                StringUtils.toSnakeCase(jni2RustClassUtils.getName()).toUpperCase()));
                        lines.add(String.format("      .construct(env, %s);",
                                StringUtils.join(", ", args)));
                        javaOptionalVars.forEach(javaOptionalVar -> lines.add(String.format("    delete_local_optional_ref!(env, %s);", javaOptionalVar)));
                        javaVars.forEach(javaVar -> lines.add(String.format("    delete_local_ref!(env, %s);", javaVar)));
                        lines.add("    return_value");
                        lines.add("  }");
                        lines.add("}\n");
                    }
                    structCounter.incrementAndGet();
                });
        assertTrue(structCounter.get() > 0);
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
        Jni2Rust<Swc4jTranspileOptions> jni2Rust = new Jni2Rust<>(Swc4jTranspileOptions.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTranspileOutput() throws IOException {
        Jni2Rust<Swc4jTranspileOutput> jni2Rust = new Jni2Rust<>(Swc4jTranspileOutput.class);
        jni2Rust.updateFile();
    }
}
