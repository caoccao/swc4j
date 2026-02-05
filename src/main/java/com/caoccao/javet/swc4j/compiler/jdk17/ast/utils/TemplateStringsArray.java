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

/**
 * Represents a template strings array for tagged template literals.
 * <p>
 * In JavaScript, tag functions receive a special array-like object with both cooked (processed)
 * and raw (unprocessed) string values:
 * <pre>{@code
 * function tag(strings) {
 *   strings[0];     // cooked string (escape sequences processed)
 *   strings.raw[0]; // raw string (escape sequences preserved)
 * }
 * tag`Hello\nWorld`;
 * }**</pre>
 * <p>
 * This class provides the Java equivalent, with:
 * <ul>
 *   <li>{@link #get(int)} - Access cooked strings (escape sequences processed)</li>
 *   <li>{@link #raw} - Access raw strings (escape sequences preserved as literal characters)</li>
 *   <li>{@link #length} - Number of template string parts</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * // For template: tag`Hello\nWorld`
 * // Cooked: "Hello" + newline + "World" (actual newline character)
 * // Raw: "Hello\\nWorld" (backslash-n as two characters)
 * }**</pre>
 */
public final class TemplateStringsArray {
    /**
     * The number of template string parts.
     */
    public final int length;
    /**
     * The raw template strings with escape sequences preserved.
     * <p>
     * For example, {@code \n} in the source remains as the two characters
     * backslash and 'n', rather than being converted to a newline.
     */
    public final String[] raw;
    private final String[] cooked;

    /**
     * Creates a new TemplateStringsArray with the given cooked and raw strings.
     *
     * @param cooked the cooked (processed) strings
     * @param raw    the raw (unprocessed) strings
     */
    public TemplateStringsArray(String[] cooked, String[] raw) {
        this.cooked = cooked;
        this.raw = raw;
        this.length = cooked.length;
    }

    /**
     * Gets the cooked string at the specified index.
     *
     * @param index the index
     * @return the cooked string at the index
     */
    public String get(int index) {
        return cooked[index];
    }
}
