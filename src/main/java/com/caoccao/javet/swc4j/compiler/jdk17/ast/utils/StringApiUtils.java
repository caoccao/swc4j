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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    public static void appendOperandToStringBuilder(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String operandType,
            int appendString,
            int appendInt,
            int appendChar) throws Swc4jByteCodeCompilerException {
        switch (operandType) {
            case "Ljava/lang/String;" -> code.invokevirtual(appendString);
            case "I", "B", "S" -> code.invokevirtual(appendInt); // int, byte, short all use append(int)
            case "C" -> code.invokevirtual(appendChar);
            case "J" -> {
                // long
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "F" -> {
                // float
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "D" -> {
                // double
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Z" -> {
                // boolean
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            case "Ljava/lang/Character;" -> {
                // Unbox Character to char
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                code.invokevirtual(charValueRef);
                code.invokevirtual(appendChar);
            }
            case "Ljava/lang/Byte;", "Ljava/lang/Short;", "Ljava/lang/Integer;" -> {
                // Unbox to int, then append
                String wrapperClass = operandType.substring(1, operandType.length() - 1); // Remove L and ;
                String methodName = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "byteValue";
                    case "Ljava/lang/Short;" -> "shortValue";
                    case "Ljava/lang/Integer;" -> "intValue";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                String returnType = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "B";
                    case "Ljava/lang/Short;" -> "S";
                    case "Ljava/lang/Integer;" -> "I";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                int unboxRef = cp.addMethodRef(wrapperClass, methodName, "()" + returnType);
                code.invokevirtual(unboxRef);
                code.invokevirtual(appendInt); // byte, short, int all use append(int)
            }
            case "Ljava/lang/Long;" -> {
                // Unbox Long to long
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "Ljava/lang/Float;" -> {
                // Unbox Float to float
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "Ljava/lang/Double;" -> {
                // Unbox Double to double
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Ljava/lang/Boolean;" -> {
                // Unbox Boolean to boolean
                int booleanValueRef = cp.addMethodRef("java/lang/Boolean", "booleanValue", "()Z");
                code.invokevirtual(booleanValueRef);
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            default -> {
                // For any other object type, use append(Object)
                int appendObject = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendObject);
            }
        }
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
        return str.charAt(index);
    }

    public static void collectOperands(
            ByteCodeCompiler compiler,
            ISwc4jAstExpr expr,
            List<ISwc4jAstExpr> operands,
            List<String> operandTypes) throws Swc4jByteCodeCompilerException {
        // If this expression is a binary Add that results in a String, collect its operands
        if (expr instanceof Swc4jAstBinExpr binExpr && binExpr.getOp() == Swc4jAstBinaryOp.Add) {
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            if ("Ljava/lang/String;".equals(exprType)) {
                // This is a string concatenation - collect operands recursively
                collectOperands(compiler, binExpr.getLeft(), operands, operandTypes);
                collectOperands(compiler, binExpr.getRight(), operands, operandTypes);
                return;
            }
        }
        // Not a string concatenation - add this expression as an operand
        operands.add(expr);
        String operandType = compiler.getTypeResolver().inferTypeFromExpr(expr);
        // If type is null (e.g., for null literal), default to Object
        operandTypes.add(operandType != null ? operandType : "Ljava/lang/Object;");
    }

    public static void generateConcat(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            String leftType,
            String rightType) throws Swc4jByteCodeCompilerException {
        // Use StringBuilder for string concatenation
        // new StringBuilder
        int stringBuilderClass = cp.addClass("java/lang/StringBuilder");
        int stringBuilderInit = cp.addMethodRef("java/lang/StringBuilder", "<init>", "()V");
        int appendString = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int appendInt = cp.addMethodRef("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
        int appendChar = cp.addMethodRef("java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        int toString = cp.addMethodRef("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

        code.newInstance(stringBuilderClass)
                .dup()
                .invokespecial(stringBuilderInit);

        // Flatten the operands - if left is also a string concatenation, collect all operands
        List<ISwc4jAstExpr> operands = new ArrayList<>();
        List<String> operandTypes = new ArrayList<>();

        // Collect operands from left side
        collectOperands(compiler, left, operands, operandTypes);

        // Add right operand
        operands.add(right);
        operandTypes.add(rightType);

        // Append all operands
        for (int i = 0; i < operands.size(); i++) {
            compiler.getExpressionGenerator().generate(code, cp, operands.get(i), null);
            appendOperandToStringBuilder(code, cp, operandTypes.get(i), appendString, appendInt, appendChar);
        }

        // Call toString()
        code.invokevirtual(toString);
    }

    /**
     * JavaScript-compatible match() that finds matches using a regex pattern.
     * Returns ArrayList of matches, or null if no match found.
     * Note: JavaScript match() with global flag returns all matches,
     * without global flag returns match with groups. This implementation
     * returns the first match with all captured groups.
     *
     * @param str     the string to search in
     * @param pattern the regex pattern
     * @return ArrayList of matched groups (index 0 is full match), or null if no match
     */
    public static ArrayList<String> match(String str, String pattern) {
        if (str == null || pattern == null) {
            return null;
        }

        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);

            if (m.find()) {
                ArrayList<String> result = new ArrayList<>();
                // Add the full match (group 0)
                result.add(m.group(0));
                // Add all captured groups
                for (int i = 1; i <= m.groupCount(); i++) {
                    result.add(m.group(i));
                }
                return result;
            }
            return null;
        } catch (PatternSyntaxException e) {
            // Invalid regex pattern - return null
            return null;
        }
    }

    /**
     * JavaScript-compatible matchAll() that finds all matches using a regex pattern.
     * Returns ArrayList of all matches, where each match is an ArrayList containing
     * the full match and all captured groups.
     *
     * @param str     the string to search in
     * @param pattern the regex pattern
     * @return ArrayList of matches, each containing [fullMatch, group1, group2, ...]
     */
    public static ArrayList<ArrayList<String>> matchAll(String str, String pattern) {
        if (str == null || pattern == null) {
            return new ArrayList<>();
        }

        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);
            ArrayList<ArrayList<String>> results = new ArrayList<>();

            while (m.find()) {
                ArrayList<String> match = new ArrayList<>();
                // Add the full match (group 0)
                match.add(m.group(0));
                // Add all captured groups
                for (int i = 1; i <= m.groupCount(); i++) {
                    match.add(m.group(i));
                }
                results.add(match);
            }

            return results;
        } catch (PatternSyntaxException e) {
            // Invalid regex pattern - return empty list
            return new ArrayList<>();
        }
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
     * JavaScript-compatible search() that returns the index of the first regex match.
     * Returns -1 if no match found.
     *
     * @param str     the string to search in
     * @param pattern the regex pattern
     * @return index of first match, or -1 if no match
     */
    public static int search(String str, String pattern) {
        if (str == null || pattern == null) {
            return -1;
        }

        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);

            if (m.find()) {
                return m.start();
            }
            return -1;
        } catch (PatternSyntaxException e) {
            // Invalid regex pattern - return -1
            return -1;
        }
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
            result.addAll(Arrays.asList(parts).subList(0, limit));
        } else {
            result.addAll(Arrays.asList(parts));
        }
        return result;
    }

    /**
     * JavaScript-compatible substr that extracts substring from start with given length.
     * Note: This method is deprecated in JavaScript but still widely used.
     * - Negative start counts from end
     * - Negative length is treated as 0
     * - Length beyond string end is clamped
     *
     * @param str    the string
     * @param start  start index (negative counts from end)
     * @param length number of characters to extract
     * @return substring
     */
    public static String substr(String str, int start, int length) {
        if (str == null) {
            return "";
        }

        int strLength = str.length();

        // Handle negative start (count from end)
        int actualStart = start < 0 ? Math.max(0, strLength + start) : start;

        // If start is beyond string length, return empty string
        if (actualStart >= strLength) {
            return "";
        }

        // Negative or zero length returns empty string
        if (length <= 0) {
            return "";
        }

        // Calculate end position, handling potential overflow
        int end;
        if (length >= strLength - actualStart) {
            // Length is large enough to go to end of string (or would overflow)
            end = strLength;
        } else {
            end = actualStart + length;
        }

        return str.substring(actualStart, end);
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

    /**
     * JavaScript-compatible test() that tests if a regex pattern matches.
     * Note: In JavaScript, test() is actually a RegExp method, but we implement
     * it as a String method for convenience.
     *
     * @param str     the string to test
     * @param pattern the regex pattern
     * @return true if pattern matches, false otherwise
     */
    public static boolean test(String str, String pattern) {
        if (str == null || pattern == null) {
            return false;
        }

        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);
            return m.find();
        } catch (PatternSyntaxException e) {
            // Invalid regex pattern - return false
            return false;
        }
    }

    /**
     * JavaScript-compatible trimEnd (also known as trimRight) that removes trailing whitespace.
     * Uses Java 11+ stripTrailing() for proper Unicode whitespace handling.
     *
     * @param str the string
     * @return string with trailing whitespace removed
     */
    public static String trimEnd(String str) {
        if (str == null) {
            return "";
        }
        // Use stripTrailing() which handles Unicode whitespace properly (JDK 11+)
        return str.stripTrailing();
    }

    /**
     * JavaScript-compatible trimStart (also known as trimLeft) that removes leading whitespace.
     * Uses Java 11+ stripLeading() for proper Unicode whitespace handling.
     *
     * @param str the string
     * @return string with leading whitespace removed
     */
    public static String trimStart(String str) {
        if (str == null) {
            return "";
        }
        // Use stripLeading() which handles Unicode whitespace properly (JDK 11+)
        return str.stripLeading();
    }
}
