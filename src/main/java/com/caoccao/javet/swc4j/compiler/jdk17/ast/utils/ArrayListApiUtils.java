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
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * Utility class for ArrayList operations similar to JavaScript Array methods.
 */
public final class ArrayListApiUtils {
    private ArrayListApiUtils() {
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
                sb.append(element);
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

    public static ArrayList<Object> entries(ArrayList<?> list) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (int i = 0; i < list.size(); i++) {
            ArrayList<Object> entry = new ArrayList<>(2);
            entry.add(i);
            entry.add(list.get(i));
            result.add(entry);
        }
        return result;
    }

    /**
     * Determine if every element matches a predicate.
     * JavaScript equivalent: arr.every(callback)
     */
    public static boolean every(ArrayList<?> list, Predicate<Object> callback) {
        if (callback == null) {
            return false;
        }
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (Object element : list) {
            if (!callback.test(element)) {
                return false;
            }
        }
        return true;
    }

    public static boolean every(ArrayList<?> list, IntPredicate callback) {
        if (callback == null) {
            return false;
        }
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (Object element : list) {
            if (!callback.test(toInt(element))) {
                return false;
            }
        }
        return true;
    }

    public static boolean every(ArrayList<?> list, LongPredicate callback) {
        if (callback == null) {
            return false;
        }
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (Object element : list) {
            if (!callback.test(toLong(element))) {
                return false;
            }
        }
        return true;
    }

    public static boolean every(ArrayList<?> list, DoublePredicate callback) {
        if (callback == null) {
            return false;
        }
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (Object element : list) {
            if (!callback.test(toDouble(element))) {
                return false;
            }
        }
        return true;
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
     * Filter elements based on a predicate.
     * JavaScript equivalent: arr.filter(callback)
     */
    public static ArrayList<Object> filter(ArrayList<?> list, Predicate<Object> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            if (callback.test(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static ArrayList<Object> filter(ArrayList<?> list, IntPredicate callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            if (callback.test(toInt(element))) {
                result.add(element);
            }
        }
        return result;
    }

    public static ArrayList<Object> filter(ArrayList<?> list, LongPredicate callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            if (callback.test(toLong(element))) {
                result.add(element);
            }
        }
        return result;
    }

    public static ArrayList<Object> filter(ArrayList<?> list, DoublePredicate callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            if (callback.test(toDouble(element))) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Find the first element matching a predicate.
     * JavaScript equivalent: arr.find(callback)
     */
    public static Object find(ArrayList<?> list, Predicate<Object> callback) {
        if (list == null || callback == null) {
            return null;
        }
        for (Object element : list) {
            if (callback.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static Object find(ArrayList<?> list, IntPredicate callback) {
        if (list == null || callback == null) {
            return null;
        }
        for (Object element : list) {
            if (callback.test(toInt(element))) {
                return element;
            }
        }
        return null;
    }

    public static Object find(ArrayList<?> list, LongPredicate callback) {
        if (list == null || callback == null) {
            return null;
        }
        for (Object element : list) {
            if (callback.test(toLong(element))) {
                return element;
            }
        }
        return null;
    }

    public static Object find(ArrayList<?> list, DoublePredicate callback) {
        if (list == null || callback == null) {
            return null;
        }
        for (Object element : list) {
            if (callback.test(toDouble(element))) {
                return element;
            }
        }
        return null;
    }

    /**
     * Find the index of the first element matching a predicate.
     * JavaScript equivalent: arr.findIndex(callback)
     */
    public static int findIndex(ArrayList<?> list, Predicate<Object> callback) {
        if (list == null || callback == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (callback.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndex(ArrayList<?> list, IntPredicate callback) {
        if (list == null || callback == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (callback.test(toInt(list.get(i)))) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndex(ArrayList<?> list, LongPredicate callback) {
        if (list == null || callback == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (callback.test(toLong(list.get(i)))) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndex(ArrayList<?> list, DoublePredicate callback) {
        if (list == null || callback == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (callback.test(toDouble(list.get(i)))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Flatten nested arrays by a given depth.
     * JavaScript equivalent: arr.flat(depth)
     */
    public static ArrayList<Object> flat(ArrayList<?> list, int depth) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || depth < 0) {
            return result;
        }
        flattenInto(list, depth, result);
        return result;
    }

    /**
     * Flatten nested arrays by one level.
     * JavaScript equivalent: arr.flat()
     */
    public static ArrayList<Object> flat(ArrayList<?> list) {
        return flat(list, 1);
    }

    /**
     * Map and flatten one level.
     * JavaScript equivalent: arr.flatMap(callback)
     */
    public static ArrayList<Object> flatMap(ArrayList<?> list, Function<Object, Object> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            Object mapped = callback.apply(element);
            if (mapped instanceof List) {
                result.addAll((List<?>) mapped);
            } else {
                result.add(mapped);
            }
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, IntFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            Object mapped = callback.apply(toInt(element));
            if (mapped instanceof List) {
                result.addAll((List<?>) mapped);
            } else {
                result.add(mapped);
            }
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, IntUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsInt(toInt(element)));
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, LongFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            Object mapped = callback.apply(toLong(element));
            if (mapped instanceof List) {
                result.addAll((List<?>) mapped);
            } else {
                result.add(mapped);
            }
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, LongUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsLong(toLong(element)));
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, DoubleFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            Object mapped = callback.apply(toDouble(element));
            if (mapped instanceof List) {
                result.addAll((List<?>) mapped);
            } else {
                result.add(mapped);
            }
        }
        return result;
    }

    public static ArrayList<Object> flatMap(ArrayList<?> list, DoubleUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsDouble(toDouble(element)));
        }
        return result;
    }

    private static void flattenInto(List<?> source, int depth, List<Object> target) {
        if (source == null) {
            return;
        }
        for (Object element : source) {
            if (depth > 0 && element instanceof List) {
                flattenInto((List<?>) element, depth - 1, target);
            } else {
                target.add(element);
            }
        }
    }

    /**
     * Execute a callback for each element.
     * JavaScript equivalent: arr.forEach(callback)
     *
     * @param list     the ArrayList to iterate
     * @param callback the callback to invoke
     */
    public static void forEach(ArrayList<?> list, Consumer<Object> callback) {
        if (list == null || callback == null) {
            return;
        }
        for (Object element : list) {
            callback.accept(element);
        }
    }

    public static void forEach(ArrayList<?> list, IntConsumer callback) {
        if (list == null || callback == null) {
            return;
        }
        for (Object element : list) {
            callback.accept(toInt(element));
        }
    }

    public static void forEach(ArrayList<?> list, LongConsumer callback) {
        if (list == null || callback == null) {
            return;
        }
        for (Object element : list) {
            callback.accept(toLong(element));
        }
    }

    public static void forEach(ArrayList<?> list, DoubleConsumer callback) {
        if (list == null || callback == null) {
            return;
        }
        for (Object element : list) {
            callback.accept(toDouble(element));
        }
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

    public static ArrayList<Object> keys(ArrayList<?> list) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (int i = 0; i < list.size(); i++) {
            result.add(i);
        }
        return result;
    }

    /**
     * Map elements to a new array.
     * JavaScript equivalent: arr.map(callback)
     */
    public static ArrayList<Object> map(ArrayList<?> list, Function<Object, Object> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.apply(element));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, IntFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.apply(toInt(element)));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, IntUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsInt(toInt(element)));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, LongFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.apply(toLong(element)));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, LongUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsLong(toLong(element)));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, DoubleFunction<?> callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.apply(toDouble(element)));
        }
        return result;
    }

    public static ArrayList<Object> map(ArrayList<?> list, DoubleUnaryOperator callback) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null || callback == null) {
            return result;
        }
        for (Object element : list) {
            result.add(callback.applyAsDouble(toDouble(element)));
        }
        return result;
    }

    /**
     * Reduce elements with an accumulator.
     * JavaScript equivalent: arr.reduce(callback)
     */
    public static Object reduce(ArrayList<?> list, BiFunction<Object, Object, Object> callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduce() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        Object accumulator = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            accumulator = callback.apply(accumulator, list.get(i));
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, IntBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduce() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        int accumulator = toInt(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            accumulator = callback.applyAsInt(accumulator, toInt(list.get(i)));
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, LongBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduce() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        long accumulator = toLong(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            accumulator = callback.applyAsLong(accumulator, toLong(list.get(i)));
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, DoubleBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduce() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        double accumulator = toDouble(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            accumulator = callback.applyAsDouble(accumulator, toDouble(list.get(i)));
        }
        return accumulator;
    }

    /**
     * Reduce elements with an accumulator and initial value.
     * JavaScript equivalent: arr.reduce(callback, initialValue)
     */
    public static Object reduce(ArrayList<?> list, BiFunction<Object, Object, Object> callback, Object initial) {
        if (callback == null) {
            return null;
        }
        Object accumulator = initial;
        if (list != null) {
            for (Object element : list) {
                accumulator = callback.apply(accumulator, element);
            }
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, IntBinaryOperator callback, int initial) {
        if (callback == null) {
            return null;
        }
        int accumulator = initial;
        if (list != null) {
            for (Object element : list) {
                accumulator = callback.applyAsInt(accumulator, toInt(element));
            }
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, LongBinaryOperator callback, long initial) {
        if (callback == null) {
            return null;
        }
        long accumulator = initial;
        if (list != null) {
            for (Object element : list) {
                accumulator = callback.applyAsLong(accumulator, toLong(element));
            }
        }
        return accumulator;
    }

    public static Object reduce(ArrayList<?> list, DoubleBinaryOperator callback, double initial) {
        if (callback == null) {
            return null;
        }
        double accumulator = initial;
        if (list != null) {
            for (Object element : list) {
                accumulator = callback.applyAsDouble(accumulator, toDouble(element));
            }
        }
        return accumulator;
    }

    /**
     * Reduce elements from right to left.
     * JavaScript equivalent: arr.reduceRight(callback)
     */
    public static Object reduceRight(ArrayList<?> list, BiFunction<Object, Object, Object> callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduceRight() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        int index = list.size() - 1;
        Object accumulator = list.get(index);
        for (int i = index - 1; i >= 0; i--) {
            accumulator = callback.apply(accumulator, list.get(i));
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, IntBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduceRight() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        int index = list.size() - 1;
        int accumulator = toInt(list.get(index));
        for (int i = index - 1; i >= 0; i--) {
            accumulator = callback.applyAsInt(accumulator, toInt(list.get(i)));
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, LongBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduceRight() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        int index = list.size() - 1;
        long accumulator = toLong(list.get(index));
        for (int i = index - 1; i >= 0; i--) {
            accumulator = callback.applyAsLong(accumulator, toLong(list.get(i)));
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, DoubleBinaryOperator callback) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("reduceRight() requires a non-empty array when no initial value is provided");
        }
        if (callback == null) {
            return null;
        }
        int index = list.size() - 1;
        double accumulator = toDouble(list.get(index));
        for (int i = index - 1; i >= 0; i--) {
            accumulator = callback.applyAsDouble(accumulator, toDouble(list.get(i)));
        }
        return accumulator;
    }

    /**
     * Reduce elements from right to left with an initial value.
     * JavaScript equivalent: arr.reduceRight(callback, initialValue)
     */
    public static Object reduceRight(ArrayList<?> list, BiFunction<Object, Object, Object> callback, Object initial) {
        if (callback == null) {
            return null;
        }
        Object accumulator = initial;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                accumulator = callback.apply(accumulator, list.get(i));
            }
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, IntBinaryOperator callback, int initial) {
        if (callback == null) {
            return null;
        }
        int accumulator = initial;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                accumulator = callback.applyAsInt(accumulator, toInt(list.get(i)));
            }
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, LongBinaryOperator callback, long initial) {
        if (callback == null) {
            return null;
        }
        long accumulator = initial;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                accumulator = callback.applyAsLong(accumulator, toLong(list.get(i)));
            }
        }
        return accumulator;
    }

    public static Object reduceRight(ArrayList<?> list, DoubleBinaryOperator callback, double initial) {
        if (callback == null) {
            return null;
        }
        double accumulator = initial;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                accumulator = callback.applyAsDouble(accumulator, toDouble(list.get(i)));
            }
        }
        return accumulator;
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
     * Determine if any element matches a predicate.
     * JavaScript equivalent: arr.some(callback)
     */
    public static boolean some(ArrayList<?> list, Predicate<Object> callback) {
        if (list == null || callback == null) {
            return false;
        }
        for (Object element : list) {
            if (callback.test(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(ArrayList<?> list, IntPredicate callback) {
        if (list == null || callback == null) {
            return false;
        }
        for (Object element : list) {
            if (callback.test(toInt(element))) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(ArrayList<?> list, LongPredicate callback) {
        if (list == null || callback == null) {
            return false;
        }
        for (Object element : list) {
            if (callback.test(toLong(element))) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(ArrayList<?> list, DoublePredicate callback) {
        if (list == null || callback == null) {
            return false;
        }
        for (Object element : list) {
            if (callback.test(toDouble(element))) {
                return true;
            }
        }
        return false;
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

    private static double toDouble(Object value) {
        if (value == null) {
            return 0.0d;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof Character) {
            return ((Character) value).charValue();
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1.0d : 0.0d;
        }
        return 0.0d;
    }

    private static int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof Character) {
            return ((Character) value).charValue();
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return 0;
    }

    private static long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof Character) {
            return ((Character) value).charValue();
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        }
        return 0L;
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
        Collections.reverse(result);

        return result;
    }

    /**
     * Create a sorted copy of the ArrayList without modifying the original.
     * JavaScript equivalent: arr.toSorted() (ES2023)
     *
     * @param list the ArrayList to sort
     * @return a new ArrayList with elements in sorted order
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ArrayList<Object> toSorted(ArrayList<?> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // Create a copy of the list
        ArrayList<Object> result = new ArrayList<>(list);

        // Sort the copy (requires elements to be Comparable)
        // Using raw type to avoid compilation error
        Collections.sort((List) result);

        return result;
    }

    /**
     * Create a copy of the ArrayList with elements removed and/or inserted.
     * JavaScript equivalent: arr.toSpliced(start, deleteCount, ...items) (ES2023)
     * This is the non-mutating version of splice() that returns the modified array.
     *
     * @param list        the ArrayList to copy
     * @param start       the beginning index, negative values count from end
     * @param deleteCount the number of elements to remove
     * @param items       the ArrayList of items to insert (can be null or empty)
     * @return a new ArrayList with the modifications applied
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> toSpliced(ArrayList<?> list, int start, int deleteCount, ArrayList<?> items) {
        if (list == null) {
            return new ArrayList<>();
        }

        // Create a copy of the list
        ArrayList<Object> result = new ArrayList<>(list);

        // Call splice on the copy (splice mutates and returns removed elements)
        splice(result, start, deleteCount, items);

        // Return the modified copy (not the removed elements)
        return result;
    }

    public static ArrayList<Object> values(ArrayList<?> list) {
        ArrayList<Object> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        result.addAll(list);
        return result;
    }

    /**
     * Create a copy of the ArrayList with a single element changed.
     * JavaScript equivalent: arr.with(index, value) (ES2023)
     *
     * @param list  the ArrayList to copy
     * @param index the index to change (negative values count from end)
     * @param value the new value for the element
     * @return a new ArrayList with the element at index changed to value
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> with(ArrayList<?> list, int index, Object value) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        int length = list.size();

        // Handle negative indices
        int actualIndex = index < 0 ? length + index : index;

        // Check bounds
        if (actualIndex < 0 || actualIndex >= length) {
            // JavaScript throws RangeError for out of bounds
            // We'll return a copy without modification for now
            return new ArrayList<>(list);
        }

        // Create a copy of the list
        ArrayList<Object> result = new ArrayList<>(list);

        // Set the element at the specified index
        result.set(actualIndex, value);

        return result;
    }
}
