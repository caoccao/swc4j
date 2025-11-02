/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClassUtils;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAstToSvg {
    @SuppressWarnings("unchecked")
    @Test
    public void testGenerate() throws IOException {
        /*
         * 1. Install graphviz
         * 2. Execute: dot -Tsvg ast.dot > ast.svg
         */
        AstToSvg astToSvg = new AstToSvg();
        Swc4jAstStore.getInstance().getEnumMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(Class::isInterface)
                .forEach(type -> {
                    assertTrue(ISwc4jAst.class.isAssignableFrom(type));
                    astToSvg.process((Class<ISwc4jAst>) type);
                });
        File file = new File(OSUtils.WORKING_DIRECTORY, "docs/features/ast.dot");
        byte[] originalBuffer = Files.readAllBytes(file.toPath());
        String content = "digraph {\n" +
                "  rankdir=\"LR\"\n" +
                StringUtils.join("\n", astToSvg.getLines()) +
                "\n}";
        byte[] newBuffer = content.getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(file.toPath(), newBuffer, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    static class AstToSvg {
        private final List<String> lines;
        private final Set<Class<? extends ISwc4jAst>> typeSet;

        public AstToSvg() {
            lines = new ArrayList<>();
            typeSet = new HashSet<>();
        }

        public List<String> getLines() {
            return lines;
        }

        public Set<Class<? extends ISwc4jAst>> getTypeSet() {
            return typeSet;
        }

        public void process(Class<? extends ISwc4jAst> type) {
            if (!typeSet.contains(type)) {
                typeSet.add(type);
                Jni2RustClassUtils<?> jni2RustClassUtils = new Jni2RustClassUtils<>(type);
                String name = jni2RustClassUtils.getName();
                if (type.isInterface()) {
                    assertTrue(type.isAnnotationPresent(Jni2RustClass.class));
                    String targets = Arrays.stream(jni2RustClassUtils.getMappings())
                            .map(Jni2RustEnumMapping::type)
                            .map(Jni2RustClassUtils::new)
                            .map(Jni2RustClassUtils::getName)
                            .collect(Collectors.joining(" "));
                    lines.add(String.format("  %s -> { %s }", name, targets));
                    Arrays.stream(jni2RustClassUtils.getMappings())
                            .map(Jni2RustEnumMapping::type)
                            .forEach(this::process);
                }
            }
        }
    }
}
