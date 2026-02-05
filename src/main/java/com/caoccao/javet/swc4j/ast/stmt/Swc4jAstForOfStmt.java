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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type swc4j ast for of stmt.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstForOfStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    /**
     * The Await.
     */
    @Jni2RustField(name = "is_await")
    protected boolean _await;
    /**
     * The Body.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt body;
    /**
     * The Left.
     */
    protected ISwc4jAstForHead left;
    /**
     * The Right.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr right;

    /**
     * Instantiates a new swc4j ast for of stmt.
     *
     * @param _await the await
     * @param left   the left
     * @param right  the right
     * @param body   the body
     * @param span   the span
     */
    @Jni2RustMethod
    public Swc4jAstForOfStmt(
            @Jni2RustParam(name = "is_await") boolean _await,
            ISwc4jAstForHead left,
            ISwc4jAstExpr right,
            ISwc4jAstStmt body,
            Swc4jSpan span) {
        super(span);
        setAwait(_await);
        setBody(body);
        setLeft(left);
        setRight(right);
    }

    /**
     * Create swc4j ast for of stmt.
     *
     * @param left  the left
     * @param right the right
     * @return the swc4j ast for of stmt
     */
    public static Swc4jAstForOfStmt create(ISwc4jAstForHead left, ISwc4jAstExpr right) {
        return create(false, left, right, ISwc4jAstStmt.createDefault());
    }

    /**
     * Create swc4j ast for of stmt.
     *
     * @param left  the left
     * @param right the right
     * @param body  the body
     * @return the swc4j ast for of stmt
     */
    public static Swc4jAstForOfStmt create(ISwc4jAstForHead left, ISwc4jAstExpr right, ISwc4jAstStmt body) {
        return create(false, left, right, body);
    }

    /**
     * Create swc4j ast for of stmt.
     *
     * @param _await the await
     * @param left   the left
     * @param right  the right
     * @param body   the body
     * @return the swc4j ast for of stmt
     */
    public static Swc4jAstForOfStmt create(
            boolean _await,
            ISwc4jAstForHead left,
            ISwc4jAstExpr right,
            ISwc4jAstStmt body) {
        return new Swc4jAstForOfStmt(_await, left, right, body, Swc4jSpan.DUMMY);
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    @Jni2RustMethod
    public ISwc4jAstStmt getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(left, right, body);
    }

    /**
     * Gets left.
     *
     * @return the left
     */
    @Jni2RustMethod
    public ISwc4jAstForHead getLeft() {
        return left;
    }

    /**
     * Gets right.
     *
     * @return the right
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ForOfStmt;
    }

    /**
     * Is await boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isAwait() {
        return _await;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstStmt newBody) {
            setBody(newBody);
            return true;
        }
        if (left == oldNode && newNode instanceof ISwc4jAstForHead newLeft) {
            setLeft(newLeft);
            return true;
        }
        if (right == oldNode && newNode instanceof ISwc4jAstExpr newRight) {
            setRight(newRight);
            return true;
        }
        return false;
    }

    /**
     * Sets await.
     *
     * @param _await the await
     * @return the await
     */
    public Swc4jAstForOfStmt setAwait(boolean _await) {
        this._await = _await;
        return this;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstForOfStmt setBody(ISwc4jAstStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    /**
     * Sets left.
     *
     * @param left the left
     * @return the left
     */
    public Swc4jAstForOfStmt setLeft(ISwc4jAstForHead left) {
        this.left = AssertionUtils.notNull(left, "Left");
        this.left.setParent(this);
        return this;
    }

    /**
     * Sets right.
     *
     * @param right the right
     * @return the right
     */
    public Swc4jAstForOfStmt setRight(ISwc4jAstExpr right) {
        this.right = AssertionUtils.notNull(right, "Right");
        this.right.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitForOfStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
