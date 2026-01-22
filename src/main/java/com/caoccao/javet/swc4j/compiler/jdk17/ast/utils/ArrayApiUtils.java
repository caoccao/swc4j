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

import java.util.Arrays;
import java.util.Objects;

/**
 * Utility class for Java array operations similar to JavaScript Array methods.
 * Handles both primitive arrays (int[], double[], etc.) and reference arrays (Object[]).
 */
public final class ArrayApiUtils {
    private ArrayApiUtils() {
        // Prevent instantiation
    }

    /**
     * Fill an int array with a static value.
     * JavaScript equivalent: arr.fill(value)
     *
     * @param array the int array to modify (mutated in place)
     * @param value the value to fill
     * @return the modified array (same reference as input)
     */
    public static int[] fill(int[] array, int value) {
        if (array != null) {
            Arrays.fill(array, value);
        }
        return array;
    }

    /**
     * Fill a double array with a static value.
     * JavaScript equivalent: arr.fill(value)
     *
     * @param array the double array to modify (mutated in place)
     * @param value the value to fill
     * @return the modified array (same reference as input)
     */
    public static double[] fill(double[] array, double value) {
        if (array != null) {
            Arrays.fill(array, value);
        }
        return array;
    }

    /**
     * Fill a boolean array with a static value.
     * JavaScript equivalent: arr.fill(value)
     *
     * @param array the boolean array to modify (mutated in place)
     * @param value the value to fill
     * @return the modified array (same reference as input)
     */
    public static boolean[] fill(boolean[] array, boolean value) {
        if (array != null) {
            Arrays.fill(array, value);
        }
        return array;
    }

    /**
     * Fill an Object array with a static value.
     * JavaScript equivalent: arr.fill(value)
     *
     * @param array the Object array to modify (mutated in place)
     * @param value the value to fill
     * @return the modified array (same reference as input)
     */
    public static Object[] fill(Object[] array, Object value) {
        if (array != null) {
            Arrays.fill(array, value);
        }
        return array;
    }

    /**
     * Check if an int array contains a value.
     * JavaScript equivalent: arr.includes(value)
     *
     * @param array the int array to search
     * @param value the value to find
     * @return true if the value exists in the array, false otherwise
     */
    public static boolean includes(int[] array, int value) {
        if (array == null) {
            return false;
        }
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a double array contains a value.
     * JavaScript equivalent: arr.includes(value)
     *
     * @param array the double array to search
     * @param value the value to find
     * @return true if the value exists in the array, false otherwise
     */
    public static boolean includes(double[] array, double value) {
        if (array == null) {
            return false;
        }
        for (double element : array) {
            if (Double.compare(element, value) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a boolean array contains a value.
     * JavaScript equivalent: arr.includes(value)
     *
     * @param array the boolean array to search
     * @param value the value to find
     * @return true if the value exists in the array, false otherwise
     */
    public static boolean includes(boolean[] array, boolean value) {
        if (array == null) {
            return false;
        }
        for (boolean element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if an Object array contains a value.
     * JavaScript equivalent: arr.includes(value)
     *
     * @param array the Object array to search
     * @param value the value to find
     * @return true if the value exists in the array, false otherwise
     */
    public static boolean includes(Object[] array, Object value) {
        if (array == null) {
            return false;
        }
        for (Object element : array) {
            if (Objects.equals(element, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find the first index of a value in an int array.
     * JavaScript equivalent: arr.indexOf(value)
     *
     * @param array the int array to search
     * @param value the value to find
     * @return the index of the first occurrence, or -1 if not found
     */
    public static int indexOf(int[] array, int value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the first index of a value in a double array.
     * JavaScript equivalent: arr.indexOf(value)
     *
     * @param array the double array to search
     * @param value the value to find
     * @return the index of the first occurrence, or -1 if not found
     */
    public static int indexOf(double[] array, double value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (Double.compare(array[i], value) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the first index of a value in a boolean array.
     * JavaScript equivalent: arr.indexOf(value)
     *
     * @param array the boolean array to search
     * @param value the value to find
     * @return the index of the first occurrence, or -1 if not found
     */
    public static int indexOf(boolean[] array, boolean value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the first index of a value in an Object array.
     * JavaScript equivalent: arr.indexOf(value)
     *
     * @param array the Object array to search
     * @param value the value to find
     * @return the index of the first occurrence, or -1 if not found
     */
    public static int indexOf(Object[] array, Object value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Join int array elements into a string with the specified separator.
     * JavaScript equivalent: arr.join(separator)
     *
     * @param array     the int array to join
     * @param separator the separator string (default is "," if null)
     * @return the joined string
     */
    public static String join(int[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }

        String sep = (separator != null) ? separator : ",";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * Join double array elements into a string with the specified separator.
     * JavaScript equivalent: arr.join(separator)
     *
     * @param array     the double array to join
     * @param separator the separator string (default is "," if null)
     * @return the joined string
     */
    public static String join(double[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }

        String sep = (separator != null) ? separator : ",";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * Join boolean array elements into a string with the specified separator.
     * JavaScript equivalent: arr.join(separator)
     *
     * @param array     the boolean array to join
     * @param separator the separator string (default is "," if null)
     * @return the joined string
     */
    public static String join(boolean[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }

        String sep = (separator != null) ? separator : ",";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * Join Object array elements into a string with the specified separator.
     * JavaScript equivalent: arr.join(separator)
     *
     * @param array     the Object array to join
     * @param separator the separator string (default is "," if null)
     * @return the joined string
     */
    public static String join(Object[] array, String separator) {
        if (array == null || array.length == 0) {
            return "";
        }

        String sep = (separator != null) ? separator : ",";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * Find the last index of a value in an int array.
     * JavaScript equivalent: arr.lastIndexOf(value)
     *
     * @param array the int array to search
     * @param value the value to find
     * @return the index of the last occurrence, or -1 if not found
     */
    public static int lastIndexOf(int[] array, int value) {
        if (array == null) {
            return -1;
        }
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the last index of a value in a double array.
     * JavaScript equivalent: arr.lastIndexOf(value)
     *
     * @param array the double array to search
     * @param value the value to find
     * @return the index of the last occurrence, or -1 if not found
     */
    public static int lastIndexOf(double[] array, double value) {
        if (array == null) {
            return -1;
        }
        for (int i = array.length - 1; i >= 0; i--) {
            if (Double.compare(array[i], value) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the last index of a value in a boolean array.
     * JavaScript equivalent: arr.lastIndexOf(value)
     *
     * @param array the boolean array to search
     * @param value the value to find
     * @return the index of the last occurrence, or -1 if not found
     */
    public static int lastIndexOf(boolean[] array, boolean value) {
        if (array == null) {
            return -1;
        }
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the last index of a value in an Object array.
     * JavaScript equivalent: arr.lastIndexOf(value)
     *
     * @param array the Object array to search
     * @param value the value to find
     * @return the index of the last occurrence, or -1 if not found
     */
    public static int lastIndexOf(Object[] array, Object value) {
        if (array == null) {
            return -1;
        }
        for (int i = array.length - 1; i >= 0; i--) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reverse an int array in place.
     * JavaScript equivalent: arr.reverse()
     *
     * @param array the int array to reverse (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static int[] reverse(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            int temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
        return array;
    }

    /**
     * Reverse a double array in place.
     * JavaScript equivalent: arr.reverse()
     *
     * @param array the double array to reverse (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static double[] reverse(double[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            double temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
        return array;
    }

    /**
     * Reverse a boolean array in place.
     * JavaScript equivalent: arr.reverse()
     *
     * @param array the boolean array to reverse (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static boolean[] reverse(boolean[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            boolean temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
        return array;
    }

    /**
     * Reverse an Object array in place.
     * JavaScript equivalent: arr.reverse()
     *
     * @param array the Object array to reverse (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static Object[] reverse(Object[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            Object temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
        return array;
    }

    /**
     * Sort an int array in place.
     * JavaScript equivalent: arr.sort()
     *
     * @param array the int array to sort (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static int[] sort(int[] array) {
        if (array != null) {
            Arrays.sort(array);
        }
        return array;
    }

    /**
     * Sort a double array in place.
     * JavaScript equivalent: arr.sort()
     *
     * @param array the double array to sort (mutated in place)
     * @return the modified array (same reference as input)
     */
    public static double[] sort(double[] array) {
        if (array != null) {
            Arrays.sort(array);
        }
        return array;
    }

    /**
     * Sort an Object array in place.
     * JavaScript equivalent: arr.sort()
     * Note: Elements must be Comparable, otherwise ClassCastException will be thrown.
     *
     * @param array the Object array to sort (mutated in place)
     * @return the modified array (same reference as input)
     */
    @SuppressWarnings("unchecked")
    public static Object[] sort(Object[] array) {
        if (array != null && array.length > 0) {
            Arrays.sort(array, (o1, o2) -> {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;
                // Convert to strings and compare (JavaScript behavior)
                return o1.toString().compareTo(o2.toString());
            });
        }
        return array;
    }

    /**
     * Create a reversed copy of an int array.
     * JavaScript equivalent: arr.toReversed() (ES2023)
     *
     * @param array the int array to reverse
     * @return a new reversed array
     */
    public static int[] toReversed(int[] array) {
        if (array == null) {
            return null;
        }
        int[] result = array.clone();
        return reverse(result);
    }

    /**
     * Create a reversed copy of a double array.
     * JavaScript equivalent: arr.toReversed() (ES2023)
     *
     * @param array the double array to reverse
     * @return a new reversed array
     */
    public static double[] toReversed(double[] array) {
        if (array == null) {
            return null;
        }
        double[] result = array.clone();
        return reverse(result);
    }

    /**
     * Create a reversed copy of a boolean array.
     * JavaScript equivalent: arr.toReversed() (ES2023)
     *
     * @param array the boolean array to reverse
     * @return a new reversed array
     */
    public static boolean[] toReversed(boolean[] array) {
        if (array == null) {
            return null;
        }
        boolean[] result = array.clone();
        return reverse(result);
    }

    /**
     * Create a reversed copy of an Object array.
     * JavaScript equivalent: arr.toReversed() (ES2023)
     *
     * @param array the Object array to reverse
     * @return a new reversed array
     */
    public static Object[] toReversed(Object[] array) {
        if (array == null) {
            return null;
        }
        Object[] result = array.clone();
        return reverse(result);
    }

    /**
     * Create a sorted copy of an int array.
     * JavaScript equivalent: arr.toSorted() (ES2023)
     *
     * @param array the int array to sort
     * @return a new sorted array
     */
    public static int[] toSorted(int[] array) {
        if (array == null) {
            return null;
        }
        int[] result = array.clone();
        return sort(result);
    }

    /**
     * Create a sorted copy of a double array.
     * JavaScript equivalent: arr.toSorted() (ES2023)
     *
     * @param array the double array to sort
     * @return a new sorted array
     */
    public static double[] toSorted(double[] array) {
        if (array == null) {
            return null;
        }
        double[] result = array.clone();
        return sort(result);
    }

    /**
     * Create a sorted copy of an Object array.
     * JavaScript equivalent: arr.toSorted() (ES2023)
     *
     * @param array the Object array to sort
     * @return a new sorted array
     */
    public static Object[] toSorted(Object[] array) {
        if (array == null) {
            return null;
        }
        Object[] result = array.clone();
        return sort(result);
    }

    /**
     * Convert an int array to string representation.
     * JavaScript equivalent: arr.toString()
     * JavaScript arrays use comma-separated values without brackets: "1,2,3"
     *
     * @param array the int array to convert
     * @return the string representation (comma-separated values)
     */
    public static String toString(int[] array) {
        return join(array, ",");
    }

    /**
     * Convert a double array to string representation.
     * JavaScript equivalent: arr.toString()
     *
     * @param array the double array to convert
     * @return the string representation (comma-separated values)
     */
    public static String toString(double[] array) {
        return join(array, ",");
    }

    /**
     * Convert a boolean array to string representation.
     * JavaScript equivalent: arr.toString()
     *
     * @param array the boolean array to convert
     * @return the string representation (comma-separated values)
     */
    public static String toString(boolean[] array) {
        return join(array, ",");
    }

    /**
     * Convert an Object array to string representation.
     * JavaScript equivalent: arr.toString()
     *
     * @param array the Object array to convert
     * @return the string representation (comma-separated values)
     */
    public static String toString(Object[] array) {
        return join(array, ",");
    }
}
