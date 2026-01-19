/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Reflection utils.
 *
 * @since 0.2.0
 */
public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    /**
     * Gets declared fields recursively.
     * The built-in getDeclaredFields() doesn't look up the declared fields in the super classes.
     * This method is a fix to the built-in one.
     *
     * @param clazz the clazz
     * @return the declared fields
     * @since 0.2.0
     */
    public static Map<String, Field> getDeclaredFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            fieldMap.put(field.getName(), field);
        }
        if (clazz != Object.class) {
            for (Map.Entry<String, Field> entry : getDeclaredFields(clazz.getSuperclass()).entrySet()) {
                if (!fieldMap.containsKey(entry.getKey())) {
                    fieldMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return fieldMap;
    }
}
