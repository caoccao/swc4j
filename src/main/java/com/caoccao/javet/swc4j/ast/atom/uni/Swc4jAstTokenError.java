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

package com.caoccao.javet.swc4j.ast.atom.uni;

import com.caoccao.javet.swc4j.ast.atom.BaseSwc4jAstTokenUniAtom;
import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;

/**
 * The type Swc4j ast token error.
 *
 * @since 0.2.0
 */
public class Swc4jAstTokenError extends BaseSwc4jAstTokenUniAtom {
    /**
     * The Error.
     *
     * @since 0.2.0
     */
    protected final String error;

    /**
     * Instantiates a new Swc4j ast token error.
     *
     * @param text           the text
     * @param error          the error
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @since 0.2.0
     */
    public Swc4jAstTokenError(String text, String error, int startPosition, int endPosition, boolean lineBreakAhead) {
        super(text, startPosition, endPosition, lineBreakAhead);
        this.error = error;
    }

    /**
     * Gets error.
     *
     * @return the error
     * @since 0.2.0
     */
    public String getError() {
        return error;
    }

    @Override
    public Swc4jAstTokenType getType() {
        return Swc4jAstTokenType.Error;
    }
}
