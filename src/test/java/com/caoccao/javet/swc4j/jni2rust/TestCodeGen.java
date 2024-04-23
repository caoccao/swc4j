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

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstFactory;
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
    public void testAstCreation() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n  /* AST Creation Begin */\n";
        String endSign = "  /* AST Creation End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger enumCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .filter(entry -> !new Jni2RustClassUtils<>(entry.getValue()).isCustomCreation())
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
                    List<String> args = new ArrayList<>();
                    List<String> javaVars = new ArrayList<>();
                    List<String> javaOptionalVars = new ArrayList<>();
                    String enumName = jni2RustClassUtils.getName();
                    lines.add(String.format("  fn create_%s<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &%s) -> JObject<'a>",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    lines.add("  where");
                    lines.add("    'local: 'a,");
                    lines.add("  {");
                    lines.add("    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };");
                    String spanCall = jni2RustClassUtils.isSpan() ? "" : "()";
                    lines.add(String.format("    let java_span_ex = map.get_span_ex_by_span(&node.span%s).to_jni_type(env);", spanCall));
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
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("    let %s = node.%s.as_ref().map(|node| enum_create_%s(env, map, node));",
                                                            javaOptionalVar,
                                                            StringUtils.toSnakeCase(fieldName),
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("    let %s = node.%s.as_ref().map(|node| create_%s(env, map, node));",
                                                            javaOptionalVar,
                                                            StringUtils.toSnakeCase(fieldName),
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                                String javaOptionalVar = String.format("java_optional_%s", StringUtils.toSnakeCase(fieldName));
                                                args.add("&" + javaOptionalVar);
                                                javaOptionalVars.add(javaOptionalVar);
                                                lines.add(String.format("    let %s = node.%s.as_ref().map(|node| map.get_span_ex_by_span(node).to_jni_type(env));",
                                                        javaOptionalVar,
                                                        StringUtils.toSnakeCase(fieldName)));
                                            } else if (innerClass == String.class) {
                                                String optionalVar = String.format("optional_%s", StringUtils.toSnakeCase(fieldName));
                                                args.add("&" + optionalVar);
                                                lines.add(String.format("    let %s = node.%s.as_ref().map(|node| node.to_string());",
                                                        optionalVar,
                                                        StringUtils.toSnakeCase(fieldName)));
                                            } else if (innerClass.isEnum()) {
                                                String optionalVar = String.format("optional_%s", StringUtils.toSnakeCase(fieldName));
                                                args.add(optionalVar);
                                                lines.add(String.format("    let %s = node.%s.as_ref().map_or(-1, |node| node.get_id());",
                                                        optionalVar,
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
                                            String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                            lines.add(String.format("    let %s = node.%s.as_ref().map(|nodes| {",
                                                    javaOptionalVar,
                                                    StringUtils.toSnakeCase(fieldName)));
                                            lines.add(String.format("      let java_%s = list_new(env, nodes.len());",
                                                    StringUtils.toSnakeCase(fieldName)));
                                            lines.add("      nodes.iter().for_each(|node| {");
                                            if (innerClass2.isInterface()) {
                                                lines.add(String.format("        let java_node = enum_create_%s(env, map, node);",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                lines.add(String.format("        let java_node = create_%s(env, map, node);",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else {
                                                fail(innerClass2.getName() + " is not expected");
                                            }
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
                                        lines.add(String.format("    let %s = list_new(env, node.%s.len());",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldName)));
                                        lines.add(String.format("    node.%s.iter().for_each(|node| {",
                                                StringUtils.toSnakeCase(fieldName)));
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            Class<?> innerClass = (Class<?>) innerType;
                                            if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("      let java_node = enum_create_%s(env, map, node);",
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("      let java_node = create_%s(env, map, node);",
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                                lines.add(String.format("      list_add(env, &%s, &java_node);", javaVar));
                                                lines.add("      delete_local_ref!(env, java_node);");
                                            } else {
                                                fail(innerClass.getName() + " is not expected");
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            Class<?> innerClass2 = (Class<?>) innerType2;
                                            lines.add("      let java_node = node.as_ref().map_or_else(");
                                            lines.add("        || Default::default(),");
                                            if (innerClass2.isInterface()) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("        |node| enum_create_%s(env, map, node));",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("        |node| create_%s(env, map, node));",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else {
                                                fail(innerClass2.getName() + " is not expected");
                                            }
                                            lines.add(String.format("      list_add(env, &%s, &java_node);", javaVar));
                                            lines.add("      delete_local_ref!(env, java_node);");
                                        } else {
                                            fail(innerType.getTypeName() + " is not expected");
                                        }
                                        lines.add("    });");
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                    if (fieldType.isInterface()) {
                                        String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        String fieldTypeName = new Jni2RustClassUtils<>(fieldType).getName();
                                        lines.add(String.format("    let %s = enum_create_%s(env, map, &node.%s);",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else if (Swc4jAst.class.isAssignableFrom(fieldType)) {
                                        String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                        args.add("&" + javaVar);
                                        javaVars.add(javaVar);
                                        String fieldTypeName = new Jni2RustClassUtils<>(fieldType).getName();
                                        lines.add(String.format("    let %s = create_%s(env, map, &node.%s);",
                                                javaVar,
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else {
                                        fail(fieldType.getName() + " is not expected");
                                    }
                                } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                    String javaVar = String.format("java_%s", StringUtils.toSnakeCase(fieldName));
                                    args.add("&" + javaVar);
                                    javaVars.add(javaVar);
                                    lines.add(String.format("    let %s = map.get_span_ex_by_span(&node.%s).to_jni_type(env);",
                                            javaVar,
                                            StringUtils.toSnakeCase(fieldName)));
                                } else if (fieldType.isPrimitive()) {
                                    String arg = StringUtils.toSnakeCase(fieldName);
                                    args.add(arg);
                                    lines.add(String.format("    let %s = node.%s;", arg, arg));
                                } else if (fieldType == String.class) {
                                    String arg = StringUtils.toSnakeCase(fieldName);
                                    args.add(arg);
                                    lines.add(String.format("    let %s = node.%s.as_str();", arg, arg));
                                } else if (fieldType.isEnum()) {
                                    String arg = StringUtils.toSnakeCase(fieldName);
                                    args.add(arg);
                                    lines.add(String.format("    let %s = node.%s.get_id();", arg, arg));
                                } else {
                                    fail(field.getGenericType().getTypeName() + " is not expected");
                                }
                            });
                    args.add("&java_span_ex");
                    javaVars.add("java_span_ex");
                    lines.add(String.format("    let return_value = java_ast_factory.create_%s(env, %s);",
                            StringUtils.toSnakeCase(jni2RustClassUtils.getName()),
                            StringUtils.join(", ", args)));
                    javaOptionalVars.forEach(javaOptionalVar -> lines.add(String.format("    delete_local_optional_ref!(env, %s);", javaOptionalVar)));
                    javaVars.forEach(javaVar -> lines.add(String.format("    delete_local_ref!(env, %s);", javaVar)));
                    lines.add("    return_value");
                    lines.add("  }\n");
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
    public void testAstEnumCreation() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n  /* AST Enum Creation Begin */\n";
        String endSign = "  /* AST Enum Creation End */\n";
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
                    lines.add(String.format("  %sfn enum_create_%s<'local, 'a>(",
                            jni2RustClassUtils.isOpen() ? "pub " : "",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    lines.add("    env: &mut JNIEnv<'local>,");
                    lines.add("    map: &ByteToIndexMap,");
                    lines.add(String.format("    node: &%s,", enumName));
                    lines.add("  ) -> JObject<'a>");
                    lines.add("  where");
                    lines.add("    'local: 'a,");
                    lines.add("  {");
                    lines.add("    match node {");
                    Stream.of(jni2RustClassUtils.getMappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                String typeName = new Jni2RustClassUtils<>(mapping.type()).getName();
                                if (mapping.type().isInterface()) {
                                    lines.add(String.format("      %s::%s(node) => enum_create_%s(env, map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                } else {
                                    lines.add(String.format("      %s::%s(node) => create_%s(env, map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                }
                            });
                    lines.add("    }");
                    lines.add("  }\n");
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
    public void testSpanEnumRegistration() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n  /* Span Enum Registration Begin */\n";
        String endSign = "  /* Span Enum Registration End */\n";
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
                    lines.add(String.format("  %sfn enum_register_%s(map: &mut ByteToIndexMap, node: &%s) {",
                            jni2RustClassUtils.isOpen() ? "pub " : "",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    lines.add("    match node {");
                    Stream.of(jni2RustClassUtils.getMappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                if (mapping.type().isInterface()) {
                                    String typeName = new Jni2RustClassUtils<>(mapping.type()).getName();
                                    lines.add(String.format("      %s::%s(node) => enum_register_%s(map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                } else {
                                    String typeName = new Jni2RustClassUtils<>(mapping.type()).getName();
                                    lines.add(String.format("      %s::%s(node) => register_%s(map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                }
                            });
                    lines.add("    }");
                    lines.add("  }\n");
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
    public void testSpanRegistration() throws IOException {
        Path rustFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve(Jni2RustFilePath.AstUtils.getFilePath());
        File rustFile = rustFilePath.toFile();
        assertTrue(rustFile.exists());
        assertTrue(rustFile.isFile());
        assertTrue(rustFile.canRead());
        assertTrue(rustFile.canWrite());
        String startSign = "\n  /* Span Registration Begin */\n";
        String endSign = "  /* Span Registration End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger enumCounter = new AtomicInteger();
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
                    lines.add(String.format("  fn register_%s(map: &mut ByteToIndexMap, node: &%s) {",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    String spanCall = jni2RustClassUtils.isSpan() ? "" : "()";
                    lines.add(String.format("    map.register_by_span(&node.span%s);", spanCall));
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
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("    node.%s.as_ref().map(|node| enum_register_%s(map, node));",
                                                            StringUtils.toSnakeCase(fieldName),
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("    node.%s.as_ref().map(|node| register_%s(map, node));",
                                                            StringUtils.toSnakeCase(fieldName),
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                                lines.add(String.format("    node.%s.as_ref().map(|node| map.register_by_span(node));",
                                                        StringUtils.toSnakeCase(fieldName)));
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            Class<?> innerClass2 = (Class<?>) innerType2;
                                            if (innerClass2.isInterface()) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("    node.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| enum_register_%s(map, node)));",
                                                        StringUtils.toSnakeCase(fieldName),
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("    node.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| register_%s(map, node)));",
                                                        StringUtils.toSnakeCase(fieldName),
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else {
                                                fail(innerClass2.getName() + " is not expected");
                                            }
                                        } else {
                                            fail(field.getGenericType().getTypeName() + " is not expected");
                                        }
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (List.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        lines.add(String.format("    node.%s.iter().for_each(|node| {",
                                                StringUtils.toSnakeCase(fieldName)));
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            Class<?> innerClass = (Class<?>) innerType;
                                            if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("      enum_register_%s(map, node);",
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = new Jni2RustClassUtils<>(innerClass).getName();
                                                    lines.add(String.format("      register_%s(map, node);",
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else {
                                                fail(innerClass.getName() + " is not expected");
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(Optional.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            Class<?> innerClass2 = (Class<?>) innerType2;
                                            if (innerClass2.isInterface()) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("      node.as_ref().map(|node| enum_register_%s(map, node));",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                String fieldTypeName = new Jni2RustClassUtils<>(innerClass2).getName();
                                                lines.add(String.format("      node.as_ref().map(|node| register_%s(map, node));",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else {
                                                fail(innerClass2.getName() + " is not expected");
                                            }
                                        } else {
                                            fail(innerType.getTypeName() + " is not expected");
                                        }
                                        lines.add("    });");
                                    } else {
                                        fail(field.getGenericType().getTypeName() + " is not expected");
                                    }
                                } else if (ISwc4jAst.class.isAssignableFrom(fieldType)) {
                                    if (fieldType.isInterface()) {
                                        String fieldTypeName = new Jni2RustClassUtils<>(fieldType).getName();
                                        lines.add(String.format("    enum_register_%s(map, &node.%s);",
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else if (Swc4jAst.class.isAssignableFrom(fieldType)) {
                                        String fieldTypeName = new Jni2RustClassUtils<>(fieldType).getName();
                                        lines.add(String.format("    register_%s(map, &node.%s);",
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                StringUtils.toSnakeCase(fieldName)));
                                    } else {
                                        fail(fieldType.getName() + " is not expected");
                                    }
                                } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    map.register_by_span(&node.%s);",
                                            StringUtils.toSnakeCase(fieldName)));
                                }
                            });
                    lines.add("  }\n");
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
    public void testSwc4jAstFactory() throws IOException {
        Jni2Rust<Swc4jAstFactory> jni2Rust = new Jni2Rust<>(Swc4jAstFactory.class);
        jni2Rust.updateFile();
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
