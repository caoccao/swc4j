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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstCatchClause;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTryStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    protected final Swc4jAstBlockStmt block;
    protected final Optional<Swc4jAstBlockStmt> finalizer;
    protected final Optional<Swc4jAstCatchClause> handler;

    public Swc4jAstTryStmt(
            Swc4jAstBlockStmt block,
            Swc4jAstCatchClause handler,
            Swc4jAstBlockStmt finalizer,
            Swc4jSpan span) {
        super(span);
        this.block = AssertionUtils.notNull(block, "Block");
        this.finalizer = Optional.ofNullable(finalizer);
        this.handler = Optional.ofNullable(handler);
        childNodes = SimpleList.immutableOf(block, handler, finalizer);
        updateParent();
    }

    public Swc4jAstBlockStmt getBlock() {
        return block;
    }

    public Optional<Swc4jAstBlockStmt> getFinalizer() {
        return finalizer;
    }

    public Optional<Swc4jAstCatchClause> getHandler() {
        return handler;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TryStmt;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTryStmt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
