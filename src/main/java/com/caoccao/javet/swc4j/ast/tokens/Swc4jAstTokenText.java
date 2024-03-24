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

package com.caoccao.javet.swc4j.ast.tokens;

import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;

/**
 * The type swc4j ast token text.
 *
 * @since 0.2.0
 */
public class Swc4jAstTokenText extends Swc4jAstToken {
    /**
     * The Text.
     *
     * @since 0.2.0
     */
    protected final String text;

    /**
     * Instantiates a new swc4j ast token text.
     *
     * @param type           the type
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jAstTokenText(
            Swc4jAstTokenType type,
            String text,
            int startPosition,
            int endPosition,
            boolean lineBreakAhead) {
        super(type, startPosition, endPosition, lineBreakAhead);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
