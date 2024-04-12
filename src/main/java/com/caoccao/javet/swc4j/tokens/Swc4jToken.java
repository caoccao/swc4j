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

import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.JsonUtils;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

/**
 * The type swc4j token.
 *
 * @since 0.2.0
 */
public class Swc4jToken {
    /**
     * The Line break ahead.
     *
     * @since 0.2.0
     */
    protected final boolean lineBreakAhead;
    /**
     * The span of the token.
     * It is zero-based.
     *
     * @since 0.2.0
     */
    protected final Swc4jSpan span;
    /**
     * The token Type.
     *
     * @since 0.2.0
     */
    protected final Swc4jTokenType type;

    /**
     * Instantiates a new swc4j token.
     *
     * @param type           the type
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jToken(
            Swc4jTokenType type,
            Swc4jSpan span,
            boolean lineBreakAhead) {
        this.lineBreakAhead = lineBreakAhead;
        this.span = span;
        this.type = AssertionUtils.notNull(type, "Ast token type");
    }

    /**
     * Gets span.
     *
     * @return the span
     * @since 0.2.0
     */
    public Swc4jSpan getSpan() {
        return span;
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
    public Swc4jTokenType getType() {
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
        sb.append(", \"span\": ").append(span);
        sb.append(", \"type\": \"").append(type.name()).append("\"");
        sb.append(", \"text\": \"").append(JsonUtils.escape(getText())).append("\"");
        sb.append(" }");
        return sb.toString();
    }
}
