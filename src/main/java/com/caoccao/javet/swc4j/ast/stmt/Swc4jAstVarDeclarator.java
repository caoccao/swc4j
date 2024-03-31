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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

/**
 * The type Swc4j ast var declarator.
 *
 * @since 0.2.0
 */
public class Swc4jAstVarDeclarator
        extends Swc4jAst
        implements ISwc4jAstDecl {
    /**
     * The Definite.
     *
     * @since 0.2.0
     */
    protected final boolean definite;
    /**
     * The Init.
     *
     * @since 0.2.0
     */
    @Nullable
    protected final ISwc4jAstExpr init;
    /**
     * The Name.
     *
     * @since 0.2.0
     */
    protected final ISwc4jAstPat name;

    /**
     * Instantiates a new Swc4j ast var declarator.
     *
     * @param name          the name
     * @param init          the init
     * @param definite      the definite
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstVarDeclarator(
            ISwc4jAstPat name,
            ISwc4jAstExpr init,
            boolean definite,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.definite = definite;
        this.init = init;
        this.name = AssertionUtils.notNull(name, "Name");
        children = SimpleList.immutableOf(init, name);
        updateParent();
    }

    /**
     * Gets init.
     *
     * @return the init
     * @since 0.2.0
     */
    public ISwc4jAstExpr getInit() {
        return init;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.2.0
     */
    public ISwc4jAstPat getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.VarDeclarator;
    }

    /**
     * Is definite.
     *
     * @return the definite
     * @since 0.2.0
     */
    public boolean isDefinite() {
        return definite;
    }
}
