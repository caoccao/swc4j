/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ArrayStaticApiUtils {
    private ArrayStaticApiUtils() {
        // Prevent instantiation
    }

    public static ArrayList<Object> from(Object value) {
        ArrayList<Object> result = new ArrayList<>();
        if (value == null) {
            return result;
        }
        if (value instanceof CharSequence sequence) {
            for (int i = 0; i < sequence.length(); i++) {
                result.add(String.valueOf(sequence.charAt(i)));
            }
            return result;
        }
        if (value instanceof Map<?, ?> map) {
            for (var entry : map.entrySet()) {
                ArrayList<Object> pair = new ArrayList<>(2);
                pair.add(entry.getKey());
                pair.add(entry.getValue());
                result.add(pair);
            }
            return result;
        }
        if (value instanceof Iterable<?> iterable) {
            for (Object element : iterable) {
                result.add(element);
            }
            return result;
        }
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                result.add(Array.get(value, i));
            }
            return result;
        }
        return result;
    }

    public static boolean isArray(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof List) {
            return true;
        }
        return value.getClass().isArray();
    }
}
