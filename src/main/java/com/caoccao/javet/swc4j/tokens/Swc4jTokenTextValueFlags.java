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

package com.caoccao.javet.swc4j.tokens;

import com.caoccao.javet.swc4j.enums.Swc4jTokenType;

/**
 * The type Swc4j token text value flags.
 *
 * @since 0.2.0
 */
public class Swc4jTokenTextValueFlags<T> extends Swc4jTokenTextValue<T> {
    /**
     * The Flags.
     */
    protected final String flags;

    /**
     * Instantiates a new Swc4j token text value flags.
     *
     * @param text           the text
     * @param value          the value
     * @param flags          the flags
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jTokenTextValueFlags(
            Swc4jTokenType type,
            String text,
            T value,
            String flags,
            int startPosition,
            int endPosition,
            boolean lineBreakAhead) {
        super(type, text, value, startPosition, endPosition, lineBreakAhead);
        this.flags = flags;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     * @since 0.2.0
     */
    public String getFlags() {
        return flags;
    }
}
