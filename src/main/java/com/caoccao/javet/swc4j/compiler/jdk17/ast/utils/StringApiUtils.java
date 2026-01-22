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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for String operations matching JavaScript String API semantics.
 * <p>
 * This class provides helper methods for String operations where Java's
 * built-in String methods have different semantics than JavaScript, or
 * where no direct Java equivalent exists.
 */
public final class StringApiUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private StringApiUtils() {
    }

    /**
     * JavaScript-compatible charAt that returns a String (not char).
     * Returns empty string for out-of-bounds indices instead of throwing exception.
     *
     * @param str   the string
     * @param index the character index
     * @return 1-character string, or empty string if out of bounds
     */
    public static String charAt(String str, int index) {
        if (str == null || index < 0 || index >= str.length()) {
            return "";
        }
        return String.valueOf(str.charAt(index));
    }

    /**
     * JavaScript-compatible charCodeAt that returns Unicode code point.
     * Returns -1 for out-of-bounds indices (JavaScript returns NaN).
     *
     * @param str   the string
     * @param index the character index
     * @return Unicode code point, or -1 if out of bounds
     */
    public static int charCodeAt(String str, int index) {
        if (str == null || index < 0 || index >= str.length()) {
            return -1; // Represent NaN as -1
        }
        return (int) str.charAt(index);
    }

    /**
     * JavaScript-compatible padEnd that pads string at the end to reach target length.
     *
     * @param str          the string to pad
     * @param targetLength the target length
     * @param padString    the padding string
     * @return padded string
     */
    public static String padEnd(String str, int targetLength, String padString) {
        if (str == null) {
            str = "";
        }

        if (padString == null || padString.isEmpty()) {
            padString = " ";
        }

        int currentLength = str.length();
        if (currentLength >= targetLength) {
            return str;
        }

        int padLength = targetLength - currentLength;
        StringBuilder result = new StringBuilder(str);

        while (result.length() < targetLength) {
            result.append(padString);
        }

        // Truncate to exact target length
        return result.substring(0, targetLength);
    }

    /**
     * JavaScript-compatible padStart that pads string at the start to reach target length.
     *
     * @param str          the string to pad
     * @param targetLength the target length
     * @param padString    the padding string
     * @return padded string
     */
    public static String padStart(String str, int targetLength, String padString) {
        if (str == null) {
            str = "";
        }

        if (padString == null || padString.isEmpty()) {
            padString = " ";
        }

        int currentLength = str.length();
        if (currentLength >= targetLength) {
            return str;
        }

        int padLength = targetLength - currentLength;
        StringBuilder padding = new StringBuilder();

        while (padding.length() < padLength) {
            padding.append(padString);
        }

        // Truncate padding to exact length needed
        return padding.substring(0, padLength) + str;
    }

    /**
     * Replace first occurrence only (JavaScript behavior).
     * Treats search string as literal, not regex.
     *
     * @param str         the string
     * @param search      the search string (literal)
     * @param replacement the replacement string
     * @return string with first occurrence replaced
     */
    public static String replace(String str, String search, String replacement) {
        if (str == null || search == null || replacement == null) {
            return str;
        }

        int index = str.indexOf(search);
        if (index == -1) {
            return str;
        }

        return str.substring(0, index) + replacement + str.substring(index + search.length());
    }

    /**
     * JavaScript-compatible slice with negative index support.
     *
     * @param str   the string
     * @param start start index (negative counts from end)
     * @return substring from start to end
     */
    public static String slice(String str, int start) {
        if (str == null) {
            return "";
        }
        return slice(str, start, str.length());
    }

    /**
     * JavaScript-compatible slice with negative index support.
     *
     * @param str   the string
     * @param start start index (negative counts from end)
     * @param end   end index (exclusive, negative counts from end)
     * @return substring
     */
    public static String slice(String str, int start, int end) {
        if (str == null) {
            return "";
        }

        int length = str.length();

        // Handle negative indices
        int actualStart = start < 0 ? Math.max(0, length + start) : Math.min(start, length);
        int actualEnd = end < 0 ? Math.max(0, length + end) : Math.min(end, length);

        // If start >= end, return empty string
        if (actualStart >= actualEnd) {
            return "";
        }

        return str.substring(actualStart, actualEnd);
    }

    /**
     * Split string into ArrayList (JavaScript returns Array).
     * Handles empty separator (split into characters) and limit.
     *
     * @param str       the string to split
     * @param separator the separator (empty string splits into chars)
     * @return ArrayList of string parts
     */
    public static ArrayList<String> split(String str, String separator) {
        return split(str, separator, -1);
    }

    /**
     * Split string into ArrayList (JavaScript returns Array).
     * Handles empty separator (split into characters) and limit.
     *
     * @param str       the string to split
     * @param separator the separator (empty string splits into chars)
     * @param limit     maximum number of splits (-1 for unlimited)
     * @return ArrayList of string parts
     */
    public static ArrayList<String> split(String str, String separator, int limit) {
        ArrayList<String> result = new ArrayList<>();

        if (str == null) {
            return result;
        }

        // Empty separator - split into characters
        if (separator != null && separator.isEmpty()) {
            int count = (limit > 0) ? Math.min(limit, str.length()) : str.length();
            for (int i = 0; i < count; i++) {
                result.add(String.valueOf(str.charAt(i)));
            }
            return result;
        }

        // Use Java's split, but escape regex metacharacters
        String[] parts;
        if (separator == null) {
            // No separator - return array with whole string
            result.add(str);
            return result;
        }

        // Escape regex special characters in separator
        String escapedSep = java.util.regex.Pattern.quote(separator);

        // JavaScript limit is max number of elements, not max splits
        // So we split without limit, then take only the first 'limit' elements
        parts = str.split(escapedSep);

        if (limit > 0 && parts.length > limit) {
            // Take only first 'limit' elements
            for (int i = 0; i < limit; i++) {
                result.add(parts[i]);
            }
        } else {
            result.addAll(Arrays.asList(parts));
        }
        return result;
    }

    /**
     * JavaScript-compatible substring from start to end of string.
     *
     * @param str   the string
     * @param start start index
     * @return substring from start to end
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return "";
        }
        return substring(str, start, str.length());
    }

    /**
     * JavaScript-compatible substring that handles edge cases.
     * - Swaps start and end if start &gt; end
     * - Clamps negative values to 0
     * - Clamps values beyond length to length
     *
     * @param str   the string
     * @param start start index
     * @param end   end index (exclusive)
     * @return substring
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return "";
        }

        int length = str.length();

        // Clamp negative values to 0
        int actualStart = Math.max(0, start);
        int actualEnd = Math.max(0, end);

        // Clamp to length
        actualStart = Math.min(actualStart, length);
        actualEnd = Math.min(actualEnd, length);

        // Swap if start > end (JavaScript behavior)
        if (actualStart > actualEnd) {
            int temp = actualStart;
            actualStart = actualEnd;
            actualEnd = temp;
        }

        return str.substring(actualStart, actualEnd);
    }
}
