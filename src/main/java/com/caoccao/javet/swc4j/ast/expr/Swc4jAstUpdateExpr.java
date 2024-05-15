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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUpdateOp;
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
public class Swc4jAstUpdateExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr arg;
    protected Swc4jAstUpdateOp op;
    protected boolean prefix;

    @Jni2RustMethod
    public Swc4jAstUpdateExpr(
            Swc4jAstUpdateOp op,
            boolean prefix,
            ISwc4jAstExpr arg,
            Swc4jSpan span) {
        super(span);
        setArg(arg);
        setOp(op);
        setPrefix(prefix);
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
    public Swc4jAstUpdateOp getOp() {
        return op;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.UpdateExpr;
    }

    @Jni2RustMethod
    public boolean isPrefix() {
        return prefix;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (arg == oldNode && newNode instanceof ISwc4jAstExpr) {
            setArg((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstUpdateExpr setArg(ISwc4jAstExpr arg) {
        this.arg = AssertionUtils.notNull(arg, "Arg");
        this.arg.setParent(this);
        return this;
    }

    public Swc4jAstUpdateExpr setOp(Swc4jAstUpdateOp op) {
        this.op = AssertionUtils.notNull(op, "Op");
        return this;
    }

    public Swc4jAstUpdateExpr setPrefix(boolean prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitUpdateExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
