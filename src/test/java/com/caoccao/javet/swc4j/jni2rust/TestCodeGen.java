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

import com.caoccao.javet.swc4j.ast.Swc4jAstFactory;
import com.caoccao.javet.swc4j.ast.Swc4jAstStore;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenFactory;
import com.caoccao.javet.swc4j.utils.ArrayUtils;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCodeGen {
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
