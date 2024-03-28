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

package com.caoccao.javet.swc4j.ast.stmt.decl;

import com.caoccao.javet.swc4j.ast.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExpr;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstPat;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type Swc4j ast var declarator.
 *
 * @since 0.2.0
 */
public class Swc4jAstVarDeclarator extends Swc4jAstDecl {
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
    protected final Swc4jAstExpr init;
    /**
     * The Name.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstPat name;

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
            Swc4jAstPat name,
            Swc4jAstExpr init,
            boolean definite,
            int startPosition,
            int endPosition) {
        super(Swc4jAstType.VarDeclarator, startPosition, endPosition);
        this.definite = definite;
//        this.init = AssertionUtils.notNull(init, "Init");
        this.init = init;
        this.name = AssertionUtils.notNull(name, "Name");
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildren() {
        return SimpleList.of(init, name);
    }

    /**
     * Gets init.
     *
     * @return the init
     * @since 0.2.0
     */
    public Swc4jAstExpr getInit() {
        return init;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.2.0
     */
    public Swc4jAstPat getName() {
        return name;
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
