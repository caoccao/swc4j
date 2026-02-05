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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast if stmt.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstIfStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    /**
     * The Alt.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstStmt> alt;
    /**
     * The Cons.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt cons;
    /**
     * The Test.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr test;

    /**
     * Instantiates a new swc4j ast if stmt.
     *
     * @param test the test
     * @param cons the cons
     * @param alt  the alt
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstIfStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt cons,
            @Jni2RustParam(optional = true) ISwc4jAstStmt alt,
            Swc4jSpan span) {
        super(span);
        setAlt(alt);
        setCons(cons);
        setTest(test);
    }

    /**
     * Create swc4j ast if stmt.
     *
     * @param test the test
     * @return the swc4j ast if stmt
     */
    public static Swc4jAstIfStmt create(ISwc4jAstExpr test) {
        return create(test, ISwc4jAstStmt.createDefault());
    }

    /**
     * Create swc4j ast if stmt.
     *
     * @param test the test
     * @param cons the cons
     * @return the swc4j ast if stmt
     */
    public static Swc4jAstIfStmt create(ISwc4jAstExpr test, ISwc4jAstStmt cons) {
        return create(test, cons, null);
    }

    /**
     * Create swc4j ast if stmt.
     *
     * @param test the test
     * @param cons the cons
     * @param alt  the alt
     * @return the swc4j ast if stmt
     */
    public static Swc4jAstIfStmt create(ISwc4jAstExpr test, ISwc4jAstStmt cons, ISwc4jAstStmt alt) {
        return new Swc4jAstIfStmt(test, cons, alt, Swc4jSpan.DUMMY);
    }

    /**
     * Gets alt.
     *
     * @return the alt
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstStmt> getAlt() {
        return alt;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(test, cons);
        alt.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets cons.
     *
     * @return the cons
     */
    @Jni2RustMethod
    public ISwc4jAstStmt getCons() {
        return cons;
    }

    /**
     * Gets test.
     *
     * @return the test
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.IfStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (alt.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstStmt)) {
            setAlt((ISwc4jAstStmt) newNode);
            return true;
        }
        if (cons == oldNode && newNode instanceof ISwc4jAstStmt newCons) {
            setCons(newCons);
            return true;
        }
        if (test == oldNode && newNode instanceof ISwc4jAstExpr newTest) {
            setTest(newTest);
            return true;
        }
        return false;
    }

    /**
     * Sets alt.
     *
     * @param alt the alt
     * @return the alt
     */
    public Swc4jAstIfStmt setAlt(ISwc4jAstStmt alt) {
        this.alt = Optional.ofNullable(alt);
        this.alt.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets cons.
     *
     * @param cons the cons
     * @return the cons
     */
    public Swc4jAstIfStmt setCons(ISwc4jAstStmt cons) {
        this.cons = AssertionUtils.notNull(cons, "Body");
        this.cons.setParent(this);
        return this;
    }

    /**
     * Sets test.
     *
     * @param test the test
     * @return the test
     */
    public Swc4jAstIfStmt setTest(ISwc4jAstExpr test) {
        this.test = AssertionUtils.notNull(test, "Right");
        this.test.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitIfStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
