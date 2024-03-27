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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.enums.Swc4jAstType;

/**
 * The type Swc4j ast.
 *
 * @since 0.2.0
 */
public abstract class Swc4jAst {
    /**
     * The End position.
     *
     * @since 0.2.0
     */
    protected final int endPosition;
    /**
     * The Start position.
     *
     * @since 0.2.0
     */
    protected final int startPosition;
    /**
     * The Ast type.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstType type;

    /**
     * Instantiates a new Swc4j ast.
     *
     * @param type          the type
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    protected Swc4jAst(Swc4jAstType type, int startPosition, int endPosition) {
        this.endPosition = endPosition;
        this.startPosition = startPosition;
        this.type = type;
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
     * Gets ast type.
     *
     * @return the ast type
     * @since 0.2.0
     */
    public Swc4jAstType getType() {
        return type;
    }
}