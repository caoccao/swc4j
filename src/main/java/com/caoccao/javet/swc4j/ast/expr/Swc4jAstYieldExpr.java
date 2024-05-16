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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstYieldExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> arg;
    protected boolean delegate;

    @Jni2RustMethod
    public Swc4jAstYieldExpr(
            @Jni2RustParam(optional = true) ISwc4jAstExpr arg,
            boolean delegate,
            Swc4jSpan span) {
        super(span);
        setArg(arg);
        setDelegate(delegate);
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getArg() {
        return arg;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of();
        arg.ifPresent(childNodes::add);
        return childNodes;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.YieldExpr;
    }

    @Jni2RustMethod
    public boolean isDelegate() {
        return delegate;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (arg.isPresent() && arg.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setArg((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstYieldExpr setArg(ISwc4jAstExpr arg) {
        this.arg = Optional.ofNullable(arg);
        this.arg.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstYieldExpr setDelegate(boolean delegate) {
        this.delegate = delegate;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitYieldExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
