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
import com.caoccao.javet.swc4j.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
        String startSign = "fn test_structs() {";
        byte[] originalBuffer = Files.readAllBytes(rustTestFilePath);
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        assertTrue(startPosition > 0, "Start position is invalid");
        List<String> lines = new ArrayList<>();
        Swc4jAstStore.getInstance().getStructMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Path filePath = entry.getValue();
                    try {
                        Path relativeFilePath = Swc4jAstStore.SOURCE_PATH.relativize(filePath);
                        String className = relativeFilePath.toString()
                                .replace('/', '.')
                                .replace('\\', '.');
                        className = className.substring(0, className.length() - 5);
                        Class<?> clazz = Class.forName(className);
                        assertNotNull(clazz, className + " should exist");
                        Optional<Jni2RustClass> jni2RustClass =
                                Optional.ofNullable(clazz.getAnnotation(Jni2RustClass.class));
                        if (!jni2RustClass.map(Jni2RustClass::ignore).orElse(false)
                                && Swc4jAst.class.isAssignableFrom(clazz)) {
                            String structName = jni2RustClass.map(Jni2RustClass::name).filter(StringUtils::isNotEmpty)
                                    .orElse(entry.getKey());
                            lines.add(String.format("  let _ = %s {", structName));
                            if (jni2RustClass.map(Jni2RustClass::span).orElse(true)) {
                                lines.add("    span: Default::default(),");
                            }
                            for (Field field : clazz.getDeclaredFields()) {
                                Optional<Jni2RustField> jni2RustField =
                                        Optional.ofNullable(field.getAnnotation(Jni2RustField.class));
                                if (!Modifier.isStatic(field.getModifiers())
                                        && !jni2RustField.map(Jni2RustField::ignore).orElse(false)) {
                                    String fieldName;
                                    if (jni2RustField.map(Jni2RustField::name).map(StringUtils::isNotEmpty)
                                            .orElse(false)) {
                                        fieldName = jni2RustField.get().name();
                                    } else {
                                        fieldName = field.getName();
                                        while (fieldName.startsWith("_")) {
                                            fieldName = fieldName.substring(1);
                                        }
                                        fieldName = StringUtils.toSnakeCase(fieldName);
                                    }
                                    boolean fieldGenerated = false;
                                    if (jni2RustField.map(Jni2RustField::value).map(StringUtils::isNotEmpty)
                                            .orElse(false)) {
                                        lines.add(String.format("    %s: %s,", fieldName, jni2RustField.get().value()));
                                        fieldGenerated = true;
                                    } else {
                                        Class<?> fieldClass = field.getType();
                                        if ((fieldClass.isInterface()
                                                && ISwc4jAst.class.isAssignableFrom(fieldClass)
                                                && ISwc4jAst.class != fieldClass)
                                                || (Swc4jAst.class.isAssignableFrom(fieldClass)
                                                && Swc4jAst.class != fieldClass)) {
                                            String subTypeName = fieldClass.getSimpleName().substring(
                                                    fieldClass.isInterface() ? 9 : 8);
                                            if (jni2RustField.map(Jni2RustField::box).orElse(false)) {
                                                lines.add(String.format("    %s: Box::new(%s::dummy()),", fieldName, subTypeName));
                                            } else {
                                                lines.add(String.format("    %s: %s::dummy(),", fieldName, subTypeName));
                                            }
                                            fieldGenerated = true;
                                        }
                                    }
                                    if (!fieldGenerated) {
                                        lines.add(String.format("    %s: Default::default(),", fieldName));
                                    }
                                }
                            }
                            lines.add("  };");
                        }
                    } catch (Exception e) {
                        fail(e);
                    }
                });
        StringBuilder sb = new StringBuilder(fileContent.length());
        sb.append(fileContent, 0, startPosition);
        sb.append("\n");
        String code = StringUtils.join("\n", lines);
        sb.append(code);
        sb.append("\n}\n");
        byte[] newBuffer = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(rustTestFilePath, newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
