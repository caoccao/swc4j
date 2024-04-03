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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDefaultDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstClassExpr
        extends Swc4jAst
        implements ISwc4jAstDefaultDecl, ISwc4jAstExpr {
    protected final Swc4jAstClass clazz;
    protected final Optional<Swc4jAstIdent> ident;

    public Swc4jAstClassExpr(
            Swc4jAstIdent ident,
            Swc4jAstClass clazz,
            Swc4jAstSpan span) {
        super(span);
        this.clazz = AssertionUtils.notNull(clazz, "Class");
        this.ident = Optional.ofNullable(ident);
        children = SimpleList.immutableOf(ident, clazz);
        updateParent();
    }

    public Swc4jAstClass getClazz() {
        return clazz;
    }

    public Optional<Swc4jAstIdent> getIdent() {
        return ident;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassExpr;
    }
}
