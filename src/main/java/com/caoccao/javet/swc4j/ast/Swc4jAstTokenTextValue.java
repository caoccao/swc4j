/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;

/**
 * The type Base swc4j ast token text value.
 *
 * @param <T> the type parameter
 * @since 0.2.0
 */
public class Swc4jAstTokenTextValue<T> extends Swc4jAstTokenText {
    /**
     * The Text.
     *
     * @since 0.2.0
     */
    protected final T value;

    /**
     * Instantiates a new Base swc4j ast token text value.
     *
     * @param type           the type
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jAstTokenTextValue(
            Swc4jAstTokenType type,
            String text,
            T value,
            int startPosition,
            int endPosition,
            boolean lineBreakAhead) {
        super(type, text, startPosition, endPosition, lineBreakAhead);
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     * @since 0.2.0
     */
    public T getValue() {
        return value;
    }
}
