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

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstVarDeclarator
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected final boolean definite;
    protected final Optional<ISwc4jAstExpr> init;
    protected final ISwc4jAstPat name;

    public Swc4jAstVarDeclarator(
            ISwc4jAstPat name,
            ISwc4jAstExpr init,
            boolean definite,
            Swc4jAstSpan span) {
        super(span);
        this.definite = definite;
        this.init = Optional.ofNullable(init);
        this.name = AssertionUtils.notNull(name, "Name");
        children = SimpleList.immutableOf(init, name);
        updateParent();
    }

    public Optional<ISwc4jAstExpr> getInit() {
        return init;
    }

    public ISwc4jAstPat getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.VarDeclarator;
    }

    public boolean isDefinite() {
        return definite;
    }
}