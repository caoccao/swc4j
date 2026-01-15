/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

import java.util.List;

/**
 * Runtime helper methods for array operations in compiled bytecode.
 */
public final class ArrayJoinUtils {
    private ArrayJoinUtils() {
        // Prevent instantiation
    }

    /**
     * Join ArrayList elements into a string with the specified separator.
     * JavaScript equivalent: arr.join(separator)
     *
     * @param list      the ArrayList to join
     * @param separator the separator string (default is "," if null)
     * @return the joined string
     */
    public static String join(List<?> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        // Use "," as default separator if not provided
        String sep = (separator != null) ? separator : ",";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(sep);
            }
            // Convert element to string (null becomes "null" in JavaScript)
            sb.append(list.get(i));
        }

        return sb.toString();
    }
}
