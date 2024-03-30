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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j ast ident.
 *
 * @since 0.2.0
 */
public class Swc4jAstIdent
        extends Swc4jAst
        implements ISwc4jAstExpr {
    /**
     * The constant QUESTION_MARK.
     *
     * @since 0.2.0
     */
    protected static final String QUESTION_MARK = "?";
    /**
     * The Optional.
     *
     * @since 0.2.0
     */
    protected final boolean optional;
    /**
     * The Sym.
     *
     * @since 0.2.0
     */
    protected final String sym;

    /**
     * Instantiates a new Swc4j ast ident.
     *
     * @param sym           the sym
     * @param optional      the optional
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstIdent(String sym, boolean optional, int startPosition, int endPosition) {
        super(startPosition, endPosition);
        this.optional = optional;
        this.sym = AssertionUtils.notNull(sym, "sym");
    }

    /**
     * Gets sym.
     *
     * @return the sym
     * @since 0.2.0
     */
    public String getSym() {
        return sym;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Ident;
    }

    /**
     * Is optional.
     *
     * @return the optional
     * @since 0.2.0
     */
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(sym);
        if (optional) {
            sb.append(QUESTION_MARK);
        }
        return sb.toString();
    }
}
