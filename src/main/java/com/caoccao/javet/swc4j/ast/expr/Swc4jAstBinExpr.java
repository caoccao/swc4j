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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstBinExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr left;
    protected Swc4jAstBinaryOp op;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr right;

    @Jni2RustMethod
    public Swc4jAstBinExpr(
            Swc4jAstBinaryOp op,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            Swc4jSpan span) {
        super(span);
        setLeft(left);
        setOp(op);
        setRight(right);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(left, right);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getLeft() {
        return left;
    }

    @Jni2RustMethod
    public Swc4jAstBinaryOp getOp() {
        return op;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BinExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (left == oldNode && newNode instanceof ISwc4jAstExpr) {
            setLeft((ISwc4jAstExpr) newNode);
            return true;
        }
        if (right == oldNode && newNode instanceof ISwc4jAstExpr) {
            setRight((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstBinExpr setLeft(ISwc4jAstExpr left) {
        this.left = AssertionUtils.notNull(left, "Left");
        this.left.setParent(this);
        return this;
    }

    public Swc4jAstBinExpr setOp(Swc4jAstBinaryOp op) {
        this.op = AssertionUtils.notNull(op, "Op");
        return this;
    }

    public Swc4jAstBinExpr setRight(ISwc4jAstExpr right) {
        this.right = AssertionUtils.notNull(right, "Right");
        this.right.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitBinExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
