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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstFnDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected final boolean declare;
    protected final Swc4jAstFunction function;
    protected final Swc4jAstIdent ident;

    public Swc4jAstFnDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstFunction function,
            Swc4jAstSpan span) {
        super(span);
        this.function = AssertionUtils.notNull(function, "Function");
        this.declare = declare;
        this.ident = AssertionUtils.notNull(ident, "Ident");
        children = SimpleList.immutableOf(function, ident);
        updateParent();
    }

    public Swc4jAstFunction getFunction() {
        return function;
    }

    public Swc4jAstIdent getIdent() {
        return ident;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.FnDecl;
    }

    public boolean isDeclare() {
        return declare;
    }
}
