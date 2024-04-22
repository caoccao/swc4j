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

import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.JsonUtils;

/**
 * The type Base swc4j token text value.
 *
 * @param <T> the type parameter
 * @since 0.2.0
 */
public class Swc4jTokenTextValue<T> extends Swc4jTokenText {
    /**
     * The Text.
     *
     * @since 0.2.0
     */
    protected final T value;

    /**
     * Instantiates a new Base swc4j token text value.
     *
     * @param type           the type
     * @param text           the text
     * @param value          the value
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jTokenTextValue(
            Swc4jTokenType type,
            String text,
            T value,
            Swc4jSpan span,
            boolean lineBreakAhead) {
        super(type, text, span, lineBreakAhead);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"lineBreakAhead\": ").append(lineBreakAhead);
        sb.append(", \"start\": ").append(span.getStart());
        sb.append(", \"end\": ").append(span.getEnd());
        sb.append(", \"type\": \"").append(type.name()).append("\"");
        sb.append(", \"text\": \"").append(JsonUtils.escape(getText())).append("\"");
        sb.append(", \"value\": \"").append(JsonUtils.escape(String.valueOf(getValue()))).append("\"");
        sb.append(" }");
        return sb.toString();
    }
}
