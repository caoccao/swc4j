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
import com.caoccao.javet.swc4j.utils.OSUtils;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public final class Swc4jAstStore {
    public static final String JAVA_FILE_EXT = ".java";
    public static final Path SOURCE_PATH = new File(OSUtils.WORKING_DIRECTORY).toPath()
            .resolve("src/main/java");
    private static final Pattern PATTERN_FOR_CLASS_NAME = Pattern.compile("^Swc4jAst(\\w+)$");
    private static final Pattern PATTERN_FOR_INTERFACE_NAME = Pattern.compile("^ISwc4jAst(\\w+)$");
    private static final Swc4jAstStore instance = new Swc4jAstStore();
    private final Map<String, Class<?>> enumMap;
    private final Map<String, Class<?>> structMap;

    private Swc4jAstStore() {
        enumMap = new HashMap<>();
        structMap = new HashMap<>();
        init();
    }

    public static Swc4jAstStore getInstance() {
        return instance;
    }

    public Map<String, Class<?>> getEnumMap() {
        return enumMap;
    }

    public Map<String, Class<?>> getStructMap() {
        return structMap;
    }

    private void init() {
        try {
            Files.walk(SOURCE_PATH.resolve("com/caoccao/javet/swc4j/ast"), FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> p.toFile().isFile())
                    .filter(p -> p.toFile().getName().endsWith(JAVA_FILE_EXT))
                    .map(SOURCE_PATH::relativize)
                    .map(Path::toString)
                    .map(p -> p.substring(0, p.length() - JAVA_FILE_EXT.length()))
                    .map(p -> p.replace('/', '.'))
                    .map(p -> p.replace('\\', '.'))
                    .map(n -> {
                        try {
                            return Class.forName(n);
                        } catch (ClassNotFoundException e) {
                            fail(e);
                            return null;
                        }
                    })
                    .filter(c -> c.isInterface() || !Modifier.isAbstract(c.getModifiers()))
                    .filter(c -> c != ISwc4jAst.class)
                    .filter(c -> !Optional.ofNullable(c.getAnnotation(Jni2RustClass.class))
                            .map(Jni2RustClass::ignore)
                            .orElse(false))
                    .forEach(clazz -> {
                        if (clazz.isEnum()) {
                            Matcher matcherFileName = PATTERN_FOR_CLASS_NAME.matcher(clazz.getSimpleName());
                            if (matcherFileName.matches()) {
                                String className = matcherFileName.group(1);
                                enumMap.put(className, clazz);
                            }
                        } else if (clazz.isInterface() && ISwc4jAst.class.isAssignableFrom(clazz)) {
                            Matcher matcherFileName = PATTERN_FOR_INTERFACE_NAME.matcher(clazz.getSimpleName());
                            if (matcherFileName.matches()) {
                                String className = matcherFileName.group(1);
                                enumMap.put(className, clazz);
                            }
                        } else if (Swc4jAst.class.isAssignableFrom(clazz)) {
                            Matcher matcherFileName = PATTERN_FOR_CLASS_NAME.matcher(clazz.getSimpleName());
                            if (matcherFileName.matches()) {
                                String className = matcherFileName.group(1);
                                structMap.put(className, clazz);
                            }
                        }
                    });
        } catch (Exception e) {
            fail(e);
        }
        assertFalse(enumMap.isEmpty());
        assertFalse(structMap.isEmpty());
    }
}
