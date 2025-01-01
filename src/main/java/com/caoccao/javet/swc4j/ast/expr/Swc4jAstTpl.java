/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
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
public class Swc4jAstTpl
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(componentBox = true)
    protected final List<ISwc4jAstExpr> exprs;
    protected final List<Swc4jAstTplElement> quasis;

    @Jni2RustMethod
    public Swc4jAstTpl(
            List<ISwc4jAstExpr> exprs,
            List<Swc4jAstTplElement> quasis,
            Swc4jSpan span) {
        super(span);
        this.exprs = AssertionUtils.notNull(exprs, "Exprs");
        this.exprs.forEach(node -> node.setParent(this));
        this.quasis = AssertionUtils.notNull(quasis, "Quasis");
        this.quasis.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTpl create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstTpl create(List<ISwc4jAstExpr> exprs) {
        return create(exprs, SimpleList.of());
    }

    public static Swc4jAstTpl create(List<ISwc4jAstExpr> exprs, List<Swc4jAstTplElement> quasis) {
        return new Swc4jAstTpl(exprs, quasis, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(exprs);
        childNodes.addAll(quasis);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstExpr> getExprs() {
        return exprs;
    }

    @Jni2RustMethod
    public List<Swc4jAstTplElement> getQuasis() {
        return quasis;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Tpl;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!exprs.isEmpty() && newNode instanceof ISwc4jAstExpr) {
            final int size = exprs.size();
            for (int i = 0; i < size; i++) {
                if (exprs.get(i) == oldNode) {
                    exprs.set(i, (ISwc4jAstExpr) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (!quasis.isEmpty() && newNode instanceof Swc4jAstTplElement) {
            final int size = quasis.size();
            for (int i = 0; i < size; i++) {
                if (quasis.get(i) == oldNode) {
                    quasis.set(i, (Swc4jAstTplElement) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTpl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
