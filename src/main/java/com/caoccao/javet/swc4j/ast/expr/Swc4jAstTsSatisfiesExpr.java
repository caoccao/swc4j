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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSimpleAssignTarget;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
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
public class Swc4jAstTsSatisfiesExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstSimpleAssignTarget {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr expr;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType typeAnn;

    @Jni2RustMethod
    public Swc4jAstTsSatisfiesExpr(
            ISwc4jAstExpr expr,
            ISwc4jAstTsType typeAnn,
            Swc4jSpan span) {
        super(span);
        setExpr(expr);
        setTypeAnn(typeAnn);
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(expr, typeAnn);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsSatisfiesExpr;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }

    public Swc4jAstTsSatisfiesExpr setExpr(ISwc4jAstExpr expr) {
        this.expr = AssertionUtils.notNull(expr, "Expr");
        return this;
    }

    public Swc4jAstTsSatisfiesExpr setTypeAnn(ISwc4jAstTsType typeAnn) {
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsSatisfiesExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
