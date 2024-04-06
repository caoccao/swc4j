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

import com.caoccao.javet.swc4j.utils.OSUtils;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.fail;

public final class Swc4jAstStore {
    public static final Path SOURCE_PATH = new File(OSUtils.WORKING_DIRECTORY).toPath()
            .resolve("src/main/java");
    public static final Pattern PATTERN_FOR_ENUM_NAME = Pattern.compile("^ISwc4jAst(\\w+)\\.java$");
    public static final Pattern PATTERN_FOR_STRUCT_NAME = Pattern.compile("^Swc4jAst(\\w+)\\.java$");
    private static final Swc4jAstStore instance = new Swc4jAstStore();
    private final Map<String, Path> enumMap;
    private final Map<String, Path> structMap;

    private Swc4jAstStore() {
        enumMap = new HashMap<>();
        structMap = new HashMap<>();
        init();
    }

    public static Swc4jAstStore getInstance() {
        return instance;
    }

    public Map<String, Path> getEnumMap() {
        return enumMap;
    }

    public Map<String, Path> getStructMap() {
        return structMap;
    }

    private void init() {
        try {
            Files.walk(SOURCE_PATH.resolve("com/caoccao/javet/swc4j/ast"), FileVisitOption.FOLLOW_LINKS)
                    .forEach(filePath -> {
                        try {
                            File file = filePath.toFile();
                            if (file.isFile()) {
                                String fileName = file.getName();
                                Matcher matcherFileName = PATTERN_FOR_STRUCT_NAME.matcher(fileName);
                                if (matcherFileName.matches()) {
                                    String className = matcherFileName.group(1);
                                    structMap.put(className, filePath);
                                } else {
                                    matcherFileName = PATTERN_FOR_ENUM_NAME.matcher(fileName);
                                    if (matcherFileName.matches()) {
                                        String className = matcherFileName.group(1);
                                        enumMap.put(className, filePath);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            fail(e);
                        }
                    });
        } catch (Exception e) {
            fail(e);
        }
    }
}
