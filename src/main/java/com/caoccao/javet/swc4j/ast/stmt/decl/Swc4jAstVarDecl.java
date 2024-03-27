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

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Swc4j ast var decl.
 *
 * @since 0.2.0
 */
public class Swc4jAstVarDecl extends Swc4jAstDecl {
    /**
     * The Declare.
     *
     * @since 0.2.0
     */
    protected final boolean declare;
    /**
     * The Init.
     *
     * @since 0.2.0
     */
    protected final List<Swc4jAstVarDeclarator> decls;
    /**
     * The Name.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstVarDeclKind kind;

    /**
     * Instantiates a new Swc4j ast decl.
     *
     * @param kind          the kind
     * @param declare       the definite
     * @param decls         the decls
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstVarDecl(
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            int startPosition,
            int endPosition) {
        super(Swc4jAstType.VarDeclarator, startPosition, endPosition);
        this.declare = declare;
        this.decls = AssertionUtils.notNull(decls, "Decls");
        this.kind = AssertionUtils.notNull(kind, "Kind");
        updateParent();
    }

    @Override
    public List<Swc4jAst> getChildren() {
        return new ArrayList<>(decls);
    }

    /**
     * Gets decls.
     *
     * @return the decls
     * @since 0.2.0
     */
    public List<Swc4jAstVarDeclarator> getDecls() {
        return decls;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     * @since 0.2.0
     */
    public Swc4jAstVarDeclKind getKind() {
        return kind;
    }

    /**
     * Is declare.
     *
     * @return the declare
     * @since 0.2.0
     */
    public boolean isDeclare() {
        return declare;
    }
}
