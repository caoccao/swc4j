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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstTpl
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final List<ISwc4jAstExpr> exprs;
    protected final List<Swc4jAstTplElement> quasis;

    public Swc4jAstTpl(
            List<ISwc4jAstExpr> exprs,
            List<Swc4jAstTplElement> quasis,
            Swc4jAstSpan span) {
        super(span);
        this.exprs = SimpleList.immutableCopyOf(AssertionUtils.notNull(exprs, "Exprs"));
        this.quasis = SimpleList.immutableCopyOf(AssertionUtils.notNull(quasis, "Quasis"));
        children = SimpleList.copyOf(exprs);
        children.addAll(quasis);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public List<ISwc4jAstExpr> getExprs() {
        return exprs;
    }

    public List<Swc4jAstTplElement> getQuasis() {
        return quasis;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Tpl;
    }
}