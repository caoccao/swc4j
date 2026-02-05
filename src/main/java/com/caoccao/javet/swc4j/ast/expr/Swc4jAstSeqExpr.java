/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDefaultDecl;
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

/**
 * The type swc4j ast seq expr.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstSeqExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstDefaultDecl {
    /**
     * The Exprs.
     */
    @Jni2RustField(componentBox = true)
    protected final List<ISwc4jAstExpr> exprs;

    /**
     * Instantiates a new swc4j ast seq expr.
     *
     * @param exprs the exprs
     * @param span  the span
     */
    @Jni2RustMethod
    public Swc4jAstSeqExpr(
            List<ISwc4jAstExpr> exprs,
            Swc4jSpan span) {
        super(span);
        this.exprs = AssertionUtils.notNull(exprs, "Exprs");
        this.exprs.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast seq expr.
     *
     * @return the swc4j ast seq expr
     */
    public static Swc4jAstSeqExpr create() {
        return create(SimpleList.of());
    }

    /**
     * Create swc4j ast seq expr.
     *
     * @param exprs the exprs
     * @return the swc4j ast seq expr
     */
    public static Swc4jAstSeqExpr create(List<ISwc4jAstExpr> exprs) {
        return new Swc4jAstSeqExpr(exprs, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(exprs);
    }

    /**
     * Gets exprs.
     *
     * @return the exprs
     */
    @Jni2RustMethod
    public List<ISwc4jAstExpr> getExprs() {
        return exprs;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SeqExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!exprs.isEmpty() && newNode instanceof ISwc4jAstExpr newExpr) {
            final int size = exprs.size();
            for (int i = 0; i < size; i++) {
                if (exprs.get(i) == oldNode) {
                    exprs.set(i, newExpr);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitSeqExpr(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
