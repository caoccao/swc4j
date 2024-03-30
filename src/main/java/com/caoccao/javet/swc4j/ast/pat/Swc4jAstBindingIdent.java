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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type Swc4j ast ident.
 *
 * @since 0.2.0
 */
public class Swc4jAstBindingIdent
        extends Swc4jAst
        implements ISwc4jAstPat {
    /**
     * The Id.
     *
     * @since 0.2.0
     */
    protected final Swc4jAstIdent id;
    /**
     * The Type ann.
     *
     * @since 0.2.0
     */
    @Nullable
    protected final Swc4jAstTsTypeAnn typeAnn;

    /**
     * Instantiates a new Swc4j ast ident.
     *
     * @param id            the id
     * @param typeAnn       the type ann
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstBindingIdent(
            Swc4jAstIdent id,
            Swc4jAstTsTypeAnn typeAnn,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.id = AssertionUtils.notNull(id, "Id");
        this.typeAnn = typeAnn;
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildren() {
        return SimpleList.of(id, typeAnn);
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 0.2.0
     */
    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BindingIdent;
    }

    /**
     * Gets type ann.
     *
     * @return the type ann
     * @since 0.2.0
     */
    public Swc4jAstTsTypeAnn getTypeAnn() {
        return typeAnn;
    }
}
