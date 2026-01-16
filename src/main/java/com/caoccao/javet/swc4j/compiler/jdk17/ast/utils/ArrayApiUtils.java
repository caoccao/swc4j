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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for ArrayList operations similar to JavaScript Array methods.
 */
public final class ArrayApiUtils {
    private ArrayApiUtils() {
        // Prevent instantiation
    }

    /**
     * Convert ArrayList to locale-specific string representation.
     * JavaScript equivalent: arr.toLocaleString()
     * JavaScript arrays use comma-separated values with locale-specific formatting: "1,2,3"
     * Note: This implementation uses the default locale for formatting.
     * Future enhancement: Support custom locale parameter.
     *
     * @param list the ArrayList to convert
     * @return the locale-specific string representation (comma-separated values)
     */
    public static String arrayToLocaleString(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            Object element = list.get(i);
            if (element != null) {
                // Use String.valueOf for basic conversion
                // Future enhancement: Use locale-specific formatting for numbers/dates
                sb.append(String.valueOf(element));
            }
        }

        return sb.toString();
    }

    /**
     * Convert ArrayList to string representation.
     * JavaScript equivalent: arr.toString()
     * JavaScript arrays use comma-separated values without brackets: "1,2,3"
     * This differs from Java's ArrayList.toString() which returns "[1, 2, 3]"
     *
     * @param list the ArrayList to convert
     * @return the string representation (comma-separated values)
     */
    public static String arrayToString(List<?> list) {
        // JavaScript's toString() is equivalent to join(",")
        return join(list, ",");
    }

    /**
     * Concatenate two ArrayLists into a new ArrayList.
     * JavaScript equivalent: arr1.concat(arr2)
     *
     * @param list1 the first ArrayList
     * @param list2 the second ArrayList to concatenate
     * @return a new ArrayList containing all elements from both lists
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> concat(ArrayList<?> list1, ArrayList<?> list2) {
        ArrayList<Object> result = new ArrayList<>();

        if (list1 != null) {
            result.addAll(list1);
        }

        if (list2 != null) {
            result.addAll(list2);
        }

        return result;
    }

    /**
     * Copy part of ArrayList to another location within the same ArrayList.
     * JavaScript equivalent: arr.copyWithin(target, start)
     *
     * @param list   the ArrayList to modify (mutated in place)
     * @param target the index at which to copy the sequence to (negative values count from end)
     * @param start  the beginning index to start copying from (negative values count from end)
     * @return the modified ArrayList (same reference as input)
     */
    public static ArrayList<Object> copyWithin(ArrayList<Object> list, int target, int start) {
        return copyWithin(list, target, start, list == null ? 0 : list.size());
    }

    /**
     * Copy part of ArrayList to another location within the same ArrayList.
     * JavaScript equivalent: arr.copyWithin(target, start, end)
     *
     * @param list   the ArrayList to modify (mutated in place)
     * @param target the index at which to copy the sequence to (negative values count from end)
     * @param start  the beginning index to start copying from (negative values count from end)
     * @param end    the ending index to stop copying from (exclusive, negative values count from end)
     * @return the modified ArrayList (same reference as input)
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> copyWithin(ArrayList<Object> list, int target, int start, int end) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        int length = list.size();

        // Handle negative indices
        int actualTarget = target < 0 ? Math.max(0, length + target) : Math.min(target, length);
        int actualStart = start < 0 ? Math.max(0, length + start) : Math.min(start, length);
        int actualEnd = end < 0 ? Math.max(0, length + end) : Math.min(end, length);

        // Calculate copy length
        int copyLength = Math.min(actualEnd - actualStart, length - actualTarget);

        if (copyLength <= 0) {
            return list;
        }

        // Copy elements to temporary list to avoid overwriting during copy
        ArrayList<Object> temp = new ArrayList<>();
        for (int i = 0; i < copyLength; i++) {
            temp.add(list.get(actualStart + i));
        }

        // Write elements to target position
        for (int i = 0; i < copyLength; i++) {
            list.set(actualTarget + i, temp.get(i));
        }

        return list;
    }

    /**
     * Fill all or part of an ArrayList with a static value.
     * JavaScript equivalent: arr.fill(value, start, end)
     *
     * @param list  the ArrayList to modify (mutated in place)
     * @param value the value to fill
     * @param start the beginning index (inclusive), negative values count from end
     * @param end   the ending index (exclusive), negative values count from end
     * @return the modified ArrayList (same reference as input)
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> fill(ArrayList<Object> list, Object value, int start, int end) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        int length = list.size();

        // Handle negative indices
        int actualStart = start < 0 ? Math.max(0, length + start) : Math.min(start, length);
        int actualEnd = end < 0 ? Math.max(0, length + end) : Math.min(end, length);

        // Fill elements from start to end (exclusive)
        for (int i = actualStart; i < actualEnd; i++) {
            list.set(i, value);
        }

        return list;
    }

    /**
     * Fill entire ArrayList with a static value.
     * JavaScript equivalent: arr.fill(value)
     *
     * @param list  the ArrayList to modify (mutated in place)
     * @param value the value to fill
     * @return the modified ArrayList (same reference as input)
     */
    public static ArrayList<Object> fill(ArrayList<Object> list, Object value) {
        return fill(list, value, 0, list == null ? 0 : list.size());
    }

    /**
     * Fill ArrayList from start index to end with a static value.
     * JavaScript equivalent: arr.fill(value, start)
     *
     * @param list  the ArrayList to modify (mutated in place)
     * @param value the value to fill
     * @param start the beginning index (inclusive), negative values count from end
     * @return the modified ArrayList (same reference as input)
     */
    public static ArrayList<Object> fill(ArrayList<Object> list, Object value, int start) {
        return fill(list, value, start, list == null ? 0 : list.size());
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

    /**
     * Extract a section of an ArrayList and return it as a new ArrayList.
     * JavaScript equivalent: arr.slice(start, end)
     *
     * @param list  the ArrayList to slice
     * @param start the beginning index (inclusive), negative values count from end
     * @param end   the ending index (exclusive), negative values count from end
     * @return a new ArrayList containing the extracted elements
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> slice(ArrayList<?> list, int start, int end) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        int length = list.size();

        // Handle negative indices
        int actualStart = start < 0 ? Math.max(0, length + start) : Math.min(start, length);
        int actualEnd = end < 0 ? Math.max(0, length + end) : Math.min(end, length);

        // If start >= end, return empty array
        if (actualStart >= actualEnd) {
            return new ArrayList<>();
        }

        // Create new ArrayList from subList
        return new ArrayList<>(list.subList(actualStart, actualEnd));
    }

    /**
     * Remove and/or insert elements at a specific position in an ArrayList.
     * JavaScript equivalent: arr.splice(start, deleteCount, ...items)
     *
     * @param list        the ArrayList to modify (mutated in place)
     * @param start       the beginning index, negative values count from end
     * @param deleteCount the number of elements to remove
     * @param items       the ArrayList of items to insert (can be null or empty)
     * @return a new ArrayList containing the removed elements
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> splice(ArrayList<Object> list, int start, int deleteCount, ArrayList<?> items) {
        if (list == null) {
            return new ArrayList<>();
        }

        int length = list.size();

        // Handle negative start index
        int actualStart = start < 0 ? Math.max(0, length + start) : Math.min(start, length);

        // Clamp deleteCount to valid range
        int actualDeleteCount = Math.max(0, Math.min(deleteCount, length - actualStart));

        // Collect removed elements
        ArrayList<Object> removed = new ArrayList<>();
        for (int i = 0; i < actualDeleteCount; i++) {
            removed.add(list.remove(actualStart));
        }

        // Insert new items at the position
        if (items != null && !items.isEmpty()) {
            list.addAll(actualStart, items);
        }

        return removed;
    }

    /**
     * Create a reversed copy of the ArrayList without modifying the original.
     * JavaScript equivalent: arr.toReversed() (ES2023)
     *
     * @param list the ArrayList to reverse
     * @return a new ArrayList with elements in reversed order
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> toReversed(ArrayList<?> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // Create a copy of the list
        ArrayList<Object> result = new ArrayList<>(list);

        // Reverse the copy
        java.util.Collections.reverse(result);

        return result;
    }
}
