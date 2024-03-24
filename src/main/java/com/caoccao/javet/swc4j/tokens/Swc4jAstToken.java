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

import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.JsonUtils;

/**
 * The type swc4j ast token.
 *
 * @since 0.2.0
 */
public class Swc4jAstToken {
    /**
     * The End position of the token.
     * It is zero-based.
     *
     * @since 0.2.0
     */
    protected final int endPosition;
    /**
     * The Line break ahead.
     *
     * @since 0.2.0
     */
    protected final boolean lineBreakAhead;
    /**
     * The Start position of the token.
     * It is zero-based.
     *
     * @since 0.2.0
     */
    protected final int startPosition;
    /**
     * The token Type.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstTokenType type;

    /**
     * Instantiates a new swc4j ast token.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jAstToken(Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        this.endPosition = endPosition;
        this.lineBreakAhead = lineBreakAhead;
        this.startPosition = startPosition;
        this.type = AssertionUtils.notNull(type, "Ast token type");
    }

    /**
     * Gets end position.
     *
     * @return the end position
     * @since 0.2.0
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Gets start position.
     *
     * @return the start position
     * @since 0.2.0
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Gets text.
     *
     * @return the text
     * @since 0.2.0
     */
    public String getText() {
        return getType().getName();
    }

    /**
     * Gets type.
     *
     * @return the type
     * @since 0.2.0
     */
    public Swc4jAstTokenType getType() {
        return type;
    }

    /**
     * Is line break ahead.
     *
     * @return true : has a line break before the token, false : has no line break before the token
     * @since 0.2.0
     */
    public boolean isLineBreakAhead() {
        return lineBreakAhead;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"lineBreakAhead\": ").append(lineBreakAhead);
        sb.append(", \"start\": ").append(startPosition);
        sb.append(", \"end\": ").append(endPosition);
        sb.append(", \"type\": \"").append(type.name()).append("\"");
        sb.append(", \"text\": \"").append(JsonUtils.escape(getText())).append("\"");
        sb.append(" }");
        return sb.toString();
    }
}
