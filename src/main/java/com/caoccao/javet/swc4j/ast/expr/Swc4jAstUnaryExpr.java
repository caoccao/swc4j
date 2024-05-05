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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
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
public class Swc4jAstUnaryExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr arg;
    protected Swc4jAstUnaryOp op;

    @Jni2RustMethod
    public Swc4jAstUnaryExpr(
            Swc4jAstUnaryOp op,
            ISwc4jAstExpr arg,
            Swc4jSpan span) {
        super(span);
        setArg(arg);
        setOp(op);
        updateParent();
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getArg() {
        return arg;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(arg);
    }

    @Jni2RustMethod
    public Swc4jAstUnaryOp getOp() {
        return op;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.UnaryExpr;
    }

    public Swc4jAstUnaryExpr setArg(ISwc4jAstExpr arg) {
        this.arg = AssertionUtils.notNull(arg, "Arg");
        return this;
    }

    public Swc4jAstUnaryExpr setOp(Swc4jAstUnaryOp op) {
        this.op = AssertionUtils.notNull(op, "Op");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitUnaryExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
