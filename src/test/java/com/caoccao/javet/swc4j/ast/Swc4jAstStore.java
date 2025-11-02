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

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClassUtils;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFieldUtils;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.utils.AnnotationUtils;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        try (Stream<Path> stream = Files.walk(SOURCE_PATH.resolve("com/caoccao/javet/swc4j/ast"), FileVisitOption.FOLLOW_LINKS)) {
            stream
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
                    .filter(clazz -> clazz.isInterface() || !Modifier.isAbstract(clazz.getModifiers()))
                    .filter(clazz -> clazz != ISwc4jAst.class)
                    .filter(clazz -> !new Jni2RustClassUtils<>(clazz).isIgnore())
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
                        } else if (Swc4jAst.class.isAssignableFrom(clazz) && Swc4jAst.class != clazz) {
                            Matcher matcherFileName = PATTERN_FOR_CLASS_NAME.matcher(clazz.getSimpleName());
                            assertTrue(
                                    matcherFileName.matches(),
                                    "Class " + clazz + " violates the naming convention");
                            String className = matcherFileName.group(1);
                            structMap.put(className, clazz);
                            assertTrue(
                                    AnnotationUtils.isAnnotationPresent(clazz, Jni2RustClass.class),
                                    "Class " + clazz.getSimpleName() + " is not annotated by Jni2RustClass");
                            assertTrue(
                                    Stream.of(clazz.getConstructors())
                                            .findFirst()
                                            .map(c -> c.isAnnotationPresent(Jni2RustMethod.class))
                                            .orElse(false),
                                    "Class " + clazz.getSimpleName() + " constructor is not annotated by Jni2RustMethod");
                            Map<String, Method> methodMap = Stream.of(clazz.getDeclaredMethods())
                                    .filter(method -> !Modifier.isStatic(method.getModifiers()))
                                    .filter(method -> !Modifier.isAbstract(method.getModifiers()))
                                    .collect(Collectors.toMap(Method::getName, Function.identity(), (n1, n2) -> n1));
                            ReflectionUtils.getDeclaredFields(clazz).values().stream()
                                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                    .filter(field -> !new Jni2RustFieldUtils(field).isIgnore())
                                    .filter(field -> !(field.getName().equals("value") && clazz == Swc4jAstBool.class))
                                    .forEach(field -> {
                                        String fieldName = field.getName();
                                        if (fieldName.startsWith("_")) {
                                            fieldName = fieldName.substring(1);
                                        }
                                        String getterMethodPrefix = field.getType() == boolean.class ? "is" : "get";
                                        String getterMethodName = getterMethodPrefix +
                                                fieldName.substring(0, 1).toUpperCase() +
                                                fieldName.substring(1);
                                        Method getterMethod = methodMap.get(getterMethodName);
                                        assertNotNull(
                                                getterMethod,
                                                getterMethodName + " is not found from " + clazz.getSimpleName());
                                        assertTrue(
                                                getterMethod.isAnnotationPresent(Jni2RustMethod.class),
                                                getterMethodName + " is not exported from " + clazz.getSimpleName());
                                        String setterMethodName = "set" +
                                                fieldName.substring(0, 1).toUpperCase() +
                                                fieldName.substring(1);
                                        if (field.getType() != List.class) {
                                            Method setterMethod = methodMap.get(setterMethodName);
                                            assertNotNull(
                                                    setterMethod,
                                                    setterMethodName + " is not found from " + clazz.getSimpleName());
                                            assertEquals(clazz, setterMethod.getReturnType());
                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            fail(e);
        }
        assertFalse(enumMap.isEmpty());
        assertFalse(structMap.isEmpty());
    }
}
