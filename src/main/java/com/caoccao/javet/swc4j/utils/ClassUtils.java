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

package com.caoccao.javet.swc4j.utils;

import java.util.Map;
import java.util.Objects;

public final class ClassUtils {
    private static final Map<String, String> PRIMITIVE_MAP = SimpleMap.of(
            "int", "I",
            "long", "J",
            "short", "S",
            "char", "C",
            "byte", "B",
            "boolean", "Z",
            "float", "F",
            "double", "D");

    private ClassUtils() {
    }

    public static String toJniClassName(String className) {
        String jniClassName = PRIMITIVE_MAP.get(Objects.requireNonNull(className));
        if (jniClassName == null) {
            if (className.endsWith("[]")) {
                String baseClassName = className.substring(0, className.length() - 2);
                String jniBaseClassName = toJniClassName(baseClassName);
                if (jniBaseClassName != null) {
                    jniClassName = "[" + jniBaseClassName;
                }
            } else {
                jniClassName = "L" + className.replace('.', '/') + ";";
            }
        }
        return jniClassName;
    }
}
