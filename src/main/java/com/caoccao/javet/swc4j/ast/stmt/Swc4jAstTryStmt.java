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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstCatchClause;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTryStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    protected Swc4jAstBlockStmt block;
    protected Optional<Swc4jAstBlockStmt> finalizer;
    protected Optional<Swc4jAstCatchClause> handler;

    @Jni2RustMethod
    public Swc4jAstTryStmt(
            Swc4jAstBlockStmt block,
            @Jni2RustParam(optional = true) Swc4jAstCatchClause handler,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt finalizer,
            Swc4jSpan span) {
        super(span);
        setBlock(block);
        setFinalizer(finalizer);
        setHandler(handler);
        updateParent();
    }

    @Jni2RustMethod
    public Swc4jAstBlockStmt getBlock() {
        return block;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(block);
        finalizer.ifPresent(childNodes::add);
        handler.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstBlockStmt> getFinalizer() {
        return finalizer;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstCatchClause> getHandler() {
        return handler;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TryStmt;
    }

    public Swc4jAstTryStmt setBlock(Swc4jAstBlockStmt block) {
        this.block = AssertionUtils.notNull(block, "Block");
        return this;
    }

    public Swc4jAstTryStmt setFinalizer(Swc4jAstBlockStmt finalizer) {
        this.finalizer = Optional.ofNullable(finalizer);
        return this;
    }

    public Swc4jAstTryStmt setHandler(Swc4jAstCatchClause handler) {
        this.handler = Optional.ofNullable(handler);
        return this;
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
