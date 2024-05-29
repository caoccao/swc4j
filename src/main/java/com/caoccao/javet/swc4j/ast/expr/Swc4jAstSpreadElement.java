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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropOrSpread;
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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, span = false)
public class Swc4jAstSpreadElement
        extends Swc4jAst
        implements ISwc4jAstPropOrSpread, ISwc4jAstJsxAttrOrSpread {
    protected Swc4jSpan dot3Token;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr expr;

    @Jni2RustMethod
    public Swc4jAstSpreadElement(
            Swc4jSpan dot3Token,
            ISwc4jAstExpr expr,
            Swc4jSpan span) {
        super(span);
        setDot3Token(dot3Token);
        setExpr(expr);
    }

    public static Swc4jAstSpreadElement create(ISwc4jAstExpr expr) {
        return create(Swc4jSpan.DUMMY, expr);
    }

    public static Swc4jAstSpreadElement create(Swc4jSpan dot3Token, ISwc4jAstExpr expr) {
        return new Swc4jAstSpreadElement(dot3Token, expr, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(expr);
    }

    @Jni2RustMethod
    public Swc4jSpan getDot3Token() {
        return dot3Token;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SpreadElement;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (expr == oldNode && newNode instanceof ISwc4jAstExpr) {
            setExpr((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstSpreadElement setDot3Token(Swc4jSpan dot3Token) {
        this.dot3Token = AssertionUtils.notNull(dot3Token, "Dot3 token");
        return this;
    }

    public Swc4jAstSpreadElement setExpr(ISwc4jAstExpr expr) {
        this.expr = AssertionUtils.notNull(expr, "Expr");
        this.expr.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitSpreadElement(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
