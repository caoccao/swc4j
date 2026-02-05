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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast break stmt.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstBreakStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    /**
     * The Label.
     */
    protected Optional<Swc4jAstIdent> label;

    /**
     * Instantiates a new swc4j ast break stmt.
     *
     * @param label the label
     * @param span  the span
     */
    @Jni2RustMethod
    public Swc4jAstBreakStmt(
            @Jni2RustParam(optional = true) Swc4jAstIdent label,
            Swc4jSpan span) {
        super(span);
        setLabel(label);
    }

    /**
     * Create swc4j ast break stmt.
     *
     * @return the swc4j ast break stmt
     */
    public static Swc4jAstBreakStmt create() {
        return create(null);
    }

    /**
     * Create swc4j ast break stmt.
     *
     * @param label the label
     * @return the swc4j ast break stmt
     */
    public static Swc4jAstBreakStmt create(Swc4jAstIdent label) {
        return new Swc4jAstBreakStmt(label, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of();
        label.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    @Jni2RustMethod
    public Optional<Swc4jAstIdent> getLabel() {
        return label;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BreakStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (label.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstIdent)) {
            setLabel((Swc4jAstIdent) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets label.
     *
     * @param label the label
     * @return the label
     */
    public Swc4jAstBreakStmt setLabel(Swc4jAstIdent label) {
        this.label = Optional.ofNullable(label);
        this.label.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitBreakStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
