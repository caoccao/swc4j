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

package com.caoccao.javet.swc4j.compiler.utils.json;

/**
 * Interface for JSON serialization and parsing.
 * Implementations can be injected via {@link JsonUtils#setProvider(JsonProvider)}
 * to replace the default hand-rolled implementation with alternatives (e.g., Jackson, Gson).
 */
public interface JsonProvider {
    /**
     * Parses a JSON string into Java objects.
     * Objects become {@link java.util.LinkedHashMap}, arrays become {@link java.util.ArrayList},
     * strings become {@link String}, numbers become {@link Integer}, {@link Long}, or {@link Double},
     * booleans become {@link Boolean}, and null becomes {@code null}.
     *
     * @param json the JSON string to parse
     * @return the parsed Java object
     * @throws RuntimeException if the JSON is malformed
     */
    Object parse(String json);

    /**
     * Serializes a Java object to a JSON string.
     * Shorthand for {@code stringify(value, null, null)}.
     *
     * @param value the value to serialize
     * @return the JSON string
     */
    String stringify(Object value);

    /**
     * Serializes a Java object to a JSON string with optional replacer and space parameters.
     *
     * @param value    the value to serialize
     * @param replacer a {@link java.util.function.BiFunction} replacer function, an {@link java.util.ArrayList} of property names (whitelist), or null
     * @param space    indentation: {@link Number} (clamped 0-10 spaces) or {@link String} (first 10 chars), or null
     * @return the JSON string
     */
    String stringify(Object value, Object replacer, Object space);
}
