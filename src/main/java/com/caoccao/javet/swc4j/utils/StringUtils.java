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

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The type String utils.
 *
 * @since 0.1.0
 */
public final class StringUtils {
    /**
     * The constant EMPTY.
     *
     * @since 0.2.0
     */
    public static final String EMPTY = "";

    private StringUtils() {
    }

    /**
     * Is empty.
     *
     * @param str the str
     * @return true : empty, false : not empty
     * @since 0.2.0
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param str the str
     * @return true : not empty, false : empty
     * @since 0.2.0
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * To snake case string.
     *
     * @param str the str
     * @return the string
     * @since 0.2.0
     */
    public static String toSnakeCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        boolean isFirst = true;
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!isFirst) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
            isFirst = false;
        }
        return sb.toString();
    }

    /**
     * Join string.
     *
     * @param delimiter the delimiter
     * @param elements  the elements
     * @return the string
     * @since 0.1.0
     */
    public static String join(CharSequence delimiter, List<String> elements) {
        StringJoiner stringJoiner = new StringJoiner(Objects.requireNonNull(delimiter));
        Objects.requireNonNull(elements).forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    /**
     * Join string.
     *
     * @param delimiter the delimiter
     * @param elements  the elements
     * @return the string
     * @since 0.1.0
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        StringJoiner stringJoiner = new StringJoiner(Objects.requireNonNull(delimiter));
        for (CharSequence cs : Objects.requireNonNull(elements)) {
            stringJoiner.add(cs);
        }
        return stringJoiner.toString();
    }
}
