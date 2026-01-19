/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

public class Swc4jTokenText extends Swc4jToken {
    protected final String text;

    public Swc4jTokenText(
            Swc4jTokenType type,
            String text,
            Swc4jSpan span,
            boolean lineBreakAhead) {
        super(type, span, lineBreakAhead);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
