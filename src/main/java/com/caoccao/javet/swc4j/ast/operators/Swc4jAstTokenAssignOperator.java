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

package com.caoccao.javet.swc4j.ast.operators;

import com.caoccao.javet.swc4j.ast.BaseSwc4jAstToken;
import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j ast token assign operator.
 *
 * @since 0.2.0
 */
public class Swc4jAstTokenAssignOperator extends BaseSwc4jAstToken {
    /**
     * The Type.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstTokenType type;

    /**
     * Instantiates a new Swc4j ast token assign operator.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jAstTokenAssignOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        super(startPosition, endPosition, lineBreakAhead);
        AssertionUtils.notNull(type, "Ast token type");
        AssertionUtils.notTrue(type.getSubType().isAssignOperator(), "Assign operator is expected");
        this.type = type;
    }

    @Override
    public String getText() {
        return type.getName();
    }

    @Override
    public Swc4jAstTokenType getType() {
        return type;
    }
}
