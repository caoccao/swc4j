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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstMetaPropKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type swc4j ast meta prop expr.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstMetaPropExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    /**
     * The Kind.
     */
    protected Swc4jAstMetaPropKind kind;

    /**
     * Instantiates a new swc4j ast meta prop expr.
     *
     * @param kind the kind
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstMetaPropExpr(
            Swc4jAstMetaPropKind kind,
            Swc4jSpan span) {
        super(span);
        setKind(kind);
    }

    /**
     * Create swc4j ast meta prop expr.
     *
     * @param kind the kind
     * @return the swc4j ast meta prop expr
     */
    public static Swc4jAstMetaPropExpr create(Swc4jAstMetaPropKind kind) {
        return new Swc4jAstMetaPropExpr(kind, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    @Jni2RustMethod
    public Swc4jAstMetaPropKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.MetaPropExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    /**
     * Sets kind.
     *
     * @param kind the kind
     * @return the kind
     */
    public Swc4jAstMetaPropExpr setKind(Swc4jAstMetaPropKind kind) {
        this.kind = AssertionUtils.notNull(kind, "Kind");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitMetaPropExpr(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
