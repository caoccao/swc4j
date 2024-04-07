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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.ReflectionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAst {
    @Test
    public void testCodeGen() throws IOException {
        Path rustTestFilePath = new File(OSUtils.WORKING_DIRECTORY).toPath()
                .resolve("rust/tests/test_ast_utils.rs");
        File rustTestFile = rustTestFilePath.toFile();
        assertTrue(rustTestFile.exists());
        assertTrue(rustTestFile.isFile());
        assertTrue(rustTestFile.canRead());
        assertTrue(rustTestFile.canWrite());
        String startSign = "\n/* GetDefault Begin */\n";
        String endSign = "\n/* GetDefault End */\n";
        byte[] originalBuffer = Files.readAllBytes(rustTestFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.indexOf(endSign);
        assertTrue(startPosition > 0, "Start position is invalid");
        assertTrue(endPosition > startPosition, "End position is invalid");
        final AtomicInteger enumCounter = new AtomicInteger();
        final AtomicInteger structCounter = new AtomicInteger();
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getEnumMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Optional<Jni2RustClass> jni2RustClass =
                            Optional.ofNullable(clazz.getAnnotation(Jni2RustClass.class));
                    String enumName = jni2RustClass.map(Jni2RustClass::name)
                            .filter(StringUtils::isNotEmpty)
                            .orElse(entry.getKey());
                    String defaultLine = jni2RustClass.map(Jni2RustClass::getDefault)
                            .filter(StringUtils::isNotEmpty)
                            .orElse(clazz.isEnum()
                                    ? String.format("%s::parse_by_id(0)", enumName)
                                    : String.format("%s::dummy()", enumName));
                    lines.add(String.format("impl GetDefault<%s> for %s {", enumName, enumName));
                    lines.add(String.format("  fn get_default() -> %s {", enumName));
                    lines.add(String.format("    %s", defaultLine));
                    lines.add("  }");
                    lines.add("}\n");
                    enumCounter.incrementAndGet();
                });
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Class<?> clazz = entry.getValue();
                    Optional<Jni2RustClass> jni2RustClass = Optional.ofNullable(clazz.getAnnotation(Jni2RustClass.class));
                    String structName = jni2RustClass.map(Jni2RustClass::name)
                            .filter(StringUtils::isNotEmpty)
                            .orElse(entry.getKey());
                    lines.add(String.format("impl GetDefault<%s> for %s {", structName, structName));
                    lines.add(String.format("  fn get_default() -> %s {", structName));
                    lines.add(String.format("    %s {", structName));
                    if (jni2RustClass.map(Jni2RustClass::span).orElse(true)) {
                        lines.add("      span: DUMMY_SP,");
                    }
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
                                String fieldName;
                                if (jni2RustField.map(Jni2RustField::name)
                                        .map(StringUtils::isNotEmpty)
                                        .orElse(false)) {
                                    fieldName = jni2RustField.get().name();
                                } else {
                                    fieldName = field.getName();
                                    while (fieldName.startsWith("_")) {
                                        fieldName = fieldName.substring(1);
                                    }
                                    fieldName = StringUtils.toSnakeCase(fieldName);
                                }
                                Class<?> fieldClass = field.getType();
                                if ((fieldClass.isInterface()
                                        && ISwc4jAst.class.isAssignableFrom(fieldClass)
                                        && ISwc4jAst.class != fieldClass)
                                        || fieldClass.isEnum()
                                        || (Swc4jAst.class.isAssignableFrom(fieldClass)
                                        && !Modifier.isAbstract(fieldClass.getModifiers()))) {
                                    String subTypeName =
                                            Optional.ofNullable(fieldClass.getAnnotation(Jni2RustClass.class))
                                                    .map(Jni2RustClass::name)
                                                    .filter(StringUtils::isNotEmpty)
                                                    .orElse(fieldClass.getSimpleName().substring(
                                                            fieldClass.isInterface() ? 9 : 8));
                                    if (jni2RustField.map(Jni2RustField::box).orElse(false)) {
                                        lines.add(String.format("      %s: Box::new(%s::get_default()),", fieldName, subTypeName));
                                    } else {
                                        lines.add(String.format("      %s: %s::get_default(),", fieldName, subTypeName));
                                    }
                                } else {
                                    lines.add(String.format("      %s: Default::default(),", fieldName));
                                }
                            });
                    lines.add("    }");
                    lines.add("  }");
                    lines.add("}\n");
                    structCounter.incrementAndGet();
                });
        assertTrue(enumCounter.get() > 0);
        assertTrue(structCounter.get() > 0);
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        String code = StringUtils.join("\n", lines);
        sb.append(code);
        sb.append(fileContent, endPosition, fileContent.length());
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(rustTestFilePath, newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
