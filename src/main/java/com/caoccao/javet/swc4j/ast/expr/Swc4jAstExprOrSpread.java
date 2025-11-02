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
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, span = false)
public class Swc4jAstExprOrSpread
        extends Swc4jAst {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr expr;
    protected Optional<Swc4jSpan> spread;

    @Jni2RustMethod
    public Swc4jAstExprOrSpread(
            @Jni2RustParam(optional = true) Swc4jSpan spread,
            ISwc4jAstExpr expr,
            Swc4jSpan span) {
        super(span);
        setExpr(expr);
        setSpread(spread);
    }

    public static Swc4jAstExprOrSpread create(ISwc4jAstExpr expr) {
        return create(null, expr);
    }

    public static Swc4jAstExprOrSpread create(Swc4jSpan spread, ISwc4jAstExpr expr) {
        return new Swc4jAstExprOrSpread(spread, expr, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(expr);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    @Jni2RustMethod
    public Optional<Swc4jSpan> getSpread() {
        return spread;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExprOrSpread;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (expr == oldNode && newNode instanceof ISwc4jAstExpr newExpr) {
            setExpr(newExpr);
            return true;
        }
        return false;
    }

    public Swc4jAstExprOrSpread setExpr(ISwc4jAstExpr expr) {
        this.expr = AssertionUtils.notNull(expr, "Expr");
        this.expr.setParent(this);
        return this;
    }

    public Swc4jAstExprOrSpread setSpread(Swc4jSpan spread) {
        this.spread = Optional.ofNullable(spread);
        return this;
    }

    @Override
    public String toString() {
        String str = spread.map(span -> "...").orElse(StringUtils.EMPTY);
        str += expr.toString();
        return str;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitExprOrSpread(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
