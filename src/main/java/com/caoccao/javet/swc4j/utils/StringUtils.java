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

import java.util.ArrayList;
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
     * Is blank.
     *
     * @param str the str
     * @return true : blank, false : not blank
     * @since 0.7.0
     */
    public static boolean isBlank(String str) {
        if (!isEmpty(str)) {
            final int length = str.length();
            for (int i = 0; i < length; i++) {
                char c = str.charAt(i);
                if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Is digit.
     *
     * @param str the str
     * @return true : yes, false : no
     * @since 0.8.0
     */
    public static boolean isDigit(String str) {
        if (isNotEmpty(str)) {
            for (char c : str.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
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

    /**
     * Repeat string.
     *
     * @param str   the str
     * @param count the count
     * @return the string
     * @since 0.2.0
     */
    public static String repeat(String str, int count) {
        if (count <= 0 || isEmpty(str)) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; ++i) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * String.prototype.slice()
     * The slice() method of String values extracts a section of this string and returns it as a new string,
     * without modifying the original string.
     *
     * @param str        the str
     * @param indexStart The index of the first character to include in the returned substring.
     * @param indexEnd   The index of the first character to exclude from the returned substring.
     * @return A new string containing the extracted section of the string.
     * @since 0.8.0
     */
    public static String slice(String str, int indexStart, int indexEnd) {
        if (isEmpty(str)) {
            return str;
        }
        if (indexStart >= str.length()) {
            return EMPTY;
        }
        if (indexEnd > str.length()) {
            indexEnd = str.length();
        }
        if (indexStart < 0) {
            indexStart = Math.max(indexStart + str.length(), 0);
        }
        if (indexEnd < 0) {
            indexEnd = Math.max(indexEnd + str.length(), 0);
        }
        if (indexEnd <= indexStart) {
            return EMPTY;
        }
        return str.substring(indexStart, indexEnd);
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
        List<String> tokens = new ArrayList<>();
        int startIndex = 0;
        int index = 0;
        int upperCaseCount = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (Character.isUpperCase(c)) {
                    if (upperCaseCount == 0) {
                        if (startIndex < index) {
                            tokens.add(str.substring(startIndex, index));
                        }
                        startIndex = index;
                    }
                    ++upperCaseCount;
                } else {
                    if (upperCaseCount > 1) {
                        if (startIndex < index - 1) {
                            tokens.add(str.substring(startIndex, index - 1).toLowerCase());
                            upperCaseCount = 1;
                        }
                        startIndex = index - 1;
                    }
                }
            } else {
                if (startIndex < index) {
                    tokens.add(str.substring(startIndex, index).toLowerCase());
                }
                upperCaseCount = 0;
                startIndex = index + 1;
            }
            ++index;
        }
        if (startIndex < str.length()) {
            tokens.add(str.substring(startIndex).toLowerCase());
        }
        return join("_", tokens);
    }
}
