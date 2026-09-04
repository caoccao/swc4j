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

package com.caoccao.javet.swc4j.compiler.utils;

import java.math.BigDecimal;

/**
 * Converts numbers to strings the way JavaScript does.
 * <p>
 * JavaScript renders a number without a fractional part as an integer ({@code 1.0} becomes
 * {@code "1"}), keeps the fraction otherwise ({@code 1.5} becomes {@code "1.5"}), and switches to
 * exponent notation outside of {@code [1e-6, 1e21)} ({@code 1e21} becomes {@code "1e+21"}).
 * Java's own {@code Double.toString} disagrees on every one of those points.
 */
public final class JsNumberUtils {
    private static final double EXPONENT_LOWER_BOUND = 1e-6;
    private static final double EXPONENT_UPPER_BOUND = 1e21;

    private JsNumberUtils() {
    }

    private static String toExponentString(double value) {
        String text = Double.toString(value); // for example 1.0E21 or 1.0E-7
        int exponentIndex = text.indexOf('E');
        if (exponentIndex < 0) {
            return text;
        }
        String mantissa = text.substring(0, exponentIndex);
        if (mantissa.endsWith(".0")) {
            mantissa = mantissa.substring(0, mantissa.length() - 2);
        }
        String exponent = text.substring(exponentIndex + 1);
        return mantissa + "e" + (exponent.startsWith("-") ? exponent : "+" + exponent);
    }

    /**
     * Converts a double to its JavaScript string representation.
     *
     * @param value the value
     * @return the JavaScript string representation
     */
    public static String toString(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (value == Double.POSITIVE_INFINITY) {
            return "Infinity";
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return "-Infinity";
        }
        if (value == 0) {
            // JavaScript renders both zeroes as "0"
            return "0";
        }
        double absoluteValue = Math.abs(value);
        if (absoluteValue >= EXPONENT_LOWER_BOUND && absoluteValue < EXPONENT_UPPER_BOUND) {
            if (value == Math.rint(value) && absoluteValue < 1e18) {
                return Long.toString((long) value);
            }
            String text = Double.toString(value);
            if (text.indexOf('E') >= 0) {
                return new BigDecimal(text).stripTrailingZeros().toPlainString();
            }
            return text.endsWith(".0") ? text.substring(0, text.length() - 2) : text;
        }
        return toExponentString(value);
    }
}
