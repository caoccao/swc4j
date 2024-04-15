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
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenFactory;
import com.caoccao.javet.swc4j.utils.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
                .filter(entry -> ArrayUtils.isNotEmpty(entry.getValue().getAnnotation(Jni2RustClass.class).mappings()))
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Jni2RustClass jni2RustClass = clazz.getAnnotation(Jni2RustClass.class);
                    String enumName = StringUtils.isNotEmpty(jni2RustClass.name())
                            ? jni2RustClass.name()
                            : entry.getKey();
                    lines.add(String.format("  %sfn enum_create_%s<'local, 'a>(",
                            jni2RustClass.open() ? "pub " : "",
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
                    Stream.of(jni2RustClass.mappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                if (mapping.type().isInterface()) {
                                    String typeName = Optional.ofNullable(mapping.type().getAnnotation(Jni2RustClass.class))
                                            .map(Jni2RustClass::name)
                                            .filter(StringUtils::isNotEmpty)
                                            .orElse(mapping.type().getSimpleName().substring(9));
                                    lines.add(String.format("      %s::%s(node) => enum_create_%s(env, map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                } else {
                                    String typeName = Optional.ofNullable(mapping.type().getAnnotation(Jni2RustClass.class))
                                            .map(Jni2RustClass::name)
                                            .filter(StringUtils::isNotEmpty)
                                            .orElse(mapping.type().getSimpleName().substring(8));
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
                .filter(entry -> ArrayUtils.isNotEmpty(entry.getValue().getAnnotation(Jni2RustClass.class).mappings()))
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Jni2RustClass jni2RustClass = clazz.getAnnotation(Jni2RustClass.class);
                    String enumName = StringUtils.isNotEmpty(jni2RustClass.name())
                            ? jni2RustClass.name()
                            : entry.getKey();
                    lines.add(String.format("  %sfn enum_register_%s(map: &mut ByteToIndexMap, node: &%s) {",
                            jni2RustClass.open() ? "pub " : "",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    lines.add("    match node {");
                    Stream.of(jni2RustClass.mappings())
                            .sorted(Comparator.comparing(Jni2RustEnumMapping::name))
                            .forEach(mapping -> {
                                assertTrue(
                                        clazz.isAssignableFrom(mapping.type()),
                                        mapping.type().getSimpleName() + " should implement " + clazz.getSimpleName());
                                if (mapping.type().isInterface()) {
                                    String typeName = Optional.ofNullable(mapping.type().getAnnotation(Jni2RustClass.class))
                                            .map(Jni2RustClass::name)
                                            .filter(StringUtils::isNotEmpty)
                                            .orElse(mapping.type().getSimpleName().substring(9));
                                    lines.add(String.format("      %s::%s(node) => enum_register_%s(map, node),",
                                            enumName,
                                            mapping.name(),
                                            StringUtils.toSnakeCase(typeName)));
                                } else {
                                    String typeName = Optional.ofNullable(mapping.type().getAnnotation(Jni2RustClass.class))
                                            .map(Jni2RustClass::name)
                                            .filter(StringUtils::isNotEmpty)
                                            .orElse(mapping.type().getSimpleName().substring(8));
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
                    Optional<Jni2RustClass> jni2RustClass = Optional.ofNullable(clazz.getAnnotation(Jni2RustClass.class));
                    String enumName = jni2RustClass
                            .map(Jni2RustClass::name)
                            .filter(StringUtils::isNotEmpty)
                            .orElse(entry.getKey());
                    lines.add(String.format("  fn register_%s(map: &mut ByteToIndexMap, node: &%s) {",
                            StringUtils.toSnakeCase(enumName),
                            enumName));
                    String spanCall = jni2RustClass
                            .map(Jni2RustClass::span)
                            .filter(span -> !span)
                            .map(noSpan -> "()")
                            .orElse("");
                    lines.add(String.format("    map.register_by_span(&node.span%s);", spanCall));
                    ReflectionUtils.getDeclaredFields(clazz).entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(Map.Entry::getValue)
                            .filter(field -> !Modifier.isStatic(field.getModifiers()))
                            .filter(field -> !Optional.ofNullable(field.getAnnotation(Jni2RustField.class))
                                    .map(Jni2RustField::ignore)
                                    .orElse(false))
                            .forEach(field -> {
                                Optional<Jni2RustField> jni2RustField =
                                        Optional.ofNullable(field.getAnnotation(Jni2RustField.class));
                                String fieldName = jni2RustField
                                        .map(Jni2RustField::name)
                                        .filter(StringUtils::isNotEmpty)
                                        .orElse(StringUtils.toSnakeCase(field.getName()));
                                Class<?> fieldType = field.getType();
                                if (Optional.class.isAssignableFrom(fieldType)) {
                                    if (field.getGenericType() instanceof ParameterizedType) {
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            Class<?> innerClass = (Class<?>) innerType;
                                            if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = Optional.ofNullable(innerClass.getAnnotation(Jni2RustField.class))
                                                            .map(Jni2RustField::name)
                                                            .filter(StringUtils::isNotEmpty)
                                                            .orElse(innerClass.getSimpleName().substring(9));
                                                    lines.add(String.format("    node.%s.as_ref().map(|node| enum_register_%s(map, node));",
                                                            fieldName,
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = Optional.ofNullable(innerClass.getAnnotation(Jni2RustField.class))
                                                            .map(Jni2RustField::name)
                                                            .filter(StringUtils::isNotEmpty)
                                                            .orElse(innerClass.getSimpleName().substring(8));
                                                    lines.add(String.format("    node.%s.as_ref().map(|node| register_%s(map, node));",
                                                            fieldName,
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else {
                                                    fail(innerClass.getName() + " is not expected");
                                                }
                                            } else if (Swc4jSpan.class.isAssignableFrom(innerClass)) {
                                                lines.add(String.format("    node.%s.as_ref().map(|node| map.register_by_span(node));",
                                                        fieldName));
                                            }
                                        } else if (innerType instanceof ParameterizedType) {
                                            assertTrue(List.class.isAssignableFrom((Class<?>) ((ParameterizedType) innerType).getRawType()));
                                            Type innerType2 = ((ParameterizedType) innerType).getActualTypeArguments()[0];
                                            assertInstanceOf(Class.class, innerType2);
                                            Class<?> innerClass2 = (Class<?>) innerType2;
                                            if (innerClass2.isInterface()) {
                                                String fieldTypeName = Optional.ofNullable(innerClass2.getAnnotation(Jni2RustField.class))
                                                        .map(Jni2RustField::name)
                                                        .filter(StringUtils::isNotEmpty)
                                                        .orElse(innerClass2.getSimpleName().substring(9));
                                                lines.add(String.format("    node.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| enum_register_%s(map, node)));",
                                                        fieldName,
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                String fieldTypeName = Optional.ofNullable(innerClass2.getAnnotation(Jni2RustField.class))
                                                        .map(Jni2RustField::name)
                                                        .filter(StringUtils::isNotEmpty)
                                                        .orElse(innerClass2.getSimpleName().substring(8));
                                                lines.add(String.format("    node.%s.as_ref().map(|nodes| nodes.iter().for_each(|node| register_%s(map, node)));",
                                                        fieldName,
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
                                        lines.add(String.format("    node.%s.iter().for_each(|node| {", fieldName));
                                        Type innerType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                        if (innerType instanceof Class) {
                                            Class<?> innerClass = (Class<?>) innerType;
                                            if (ISwc4jAst.class.isAssignableFrom(innerClass)) {
                                                if (innerClass.isInterface()) {
                                                    String fieldTypeName = Optional.ofNullable(innerClass.getAnnotation(Jni2RustField.class))
                                                            .map(Jni2RustField::name)
                                                            .filter(StringUtils::isNotEmpty)
                                                            .orElse(innerClass.getSimpleName().substring(9));
                                                    lines.add(String.format("      enum_register_%s(map, node);",
                                                            StringUtils.toSnakeCase(fieldTypeName)));
                                                } else if (Swc4jAst.class.isAssignableFrom(innerClass)) {
                                                    String fieldTypeName = Optional.ofNullable(innerClass.getAnnotation(Jni2RustField.class))
                                                            .map(Jni2RustField::name)
                                                            .filter(StringUtils::isNotEmpty)
                                                            .orElse(innerClass.getSimpleName().substring(8));
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
                                                String fieldTypeName = Optional.ofNullable(innerClass2.getAnnotation(Jni2RustField.class))
                                                        .map(Jni2RustField::name)
                                                        .filter(StringUtils::isNotEmpty)
                                                        .orElse(innerClass2.getSimpleName().substring(9));
                                                lines.add(String.format("      node.as_ref().map(|node| enum_register_%s(map, node));",
                                                        StringUtils.toSnakeCase(fieldTypeName)));
                                            } else if (Swc4jAst.class.isAssignableFrom(innerClass2)) {
                                                String fieldTypeName = Optional.ofNullable(innerClass2.getAnnotation(Jni2RustField.class))
                                                        .map(Jni2RustField::name)
                                                        .filter(StringUtils::isNotEmpty)
                                                        .orElse(innerClass2.getSimpleName().substring(8));
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
                                        String fieldTypeName = Optional.ofNullable(fieldType.getAnnotation(Jni2RustField.class))
                                                .map(Jni2RustField::name)
                                                .filter(StringUtils::isNotEmpty)
                                                .orElse(fieldType.getSimpleName().substring(9));
                                        lines.add(String.format("    enum_register_%s(map, &node.%s);",
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                fieldName));
                                    } else if (Swc4jAst.class.isAssignableFrom(fieldType)) {
                                        String fieldTypeName = Optional.ofNullable(fieldType.getAnnotation(Jni2RustField.class))
                                                .map(Jni2RustField::name)
                                                .filter(StringUtils::isNotEmpty)
                                                .orElse(fieldType.getSimpleName().substring(8));
                                        lines.add(String.format("    register_%s(map, &node.%s);",
                                                StringUtils.toSnakeCase(fieldTypeName),
                                                fieldName));
                                    } else {
                                        fail(fieldType.getName() + " is not expected");
                                    }
                                } else if (Swc4jSpan.class.isAssignableFrom(fieldType)) {
                                    lines.add(String.format("    map.register_by_span(&node.%s);", fieldName));
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
    public void testSwc4jTokenFactory() throws IOException {
        Jni2Rust<Swc4jTokenFactory> jni2Rust = new Jni2Rust<>(Swc4jTokenFactory.class);
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
