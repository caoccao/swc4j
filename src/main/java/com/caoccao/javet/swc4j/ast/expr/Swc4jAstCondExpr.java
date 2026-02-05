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
 * The type swc4j ast cond expr.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstCondExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    /**
     * The Alt.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr alt;
    /**
     * The Cons.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr cons;
    /**
     * The Test.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr test;

    /**
     * Instantiates a new swc4j ast cond expr.
     *
     * @param test the test
     * @param cons the cons
     * @param alt  the alt
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstCondExpr(
            ISwc4jAstExpr test,
            ISwc4jAstExpr cons,
            ISwc4jAstExpr alt,
            Swc4jSpan span) {
        super(span);
        setAlt(alt);
        setCons(cons);
        setTest(test);
    }

    /**
     * Create swc4j ast cond expr.
     *
     * @param test the test
     * @param cons the cons
     * @param alt  the alt
     * @return the swc4j ast cond expr
     */
    public static Swc4jAstCondExpr create(ISwc4jAstExpr test, ISwc4jAstExpr cons, ISwc4jAstExpr alt) {
        return new Swc4jAstCondExpr(test, cons, alt, Swc4jSpan.DUMMY);
    }

    /**
     * Gets alt.
     *
     * @return the alt
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getAlt() {
        return alt;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(alt, cons, test);
    }

    /**
     * Gets cons.
     *
     * @return the cons
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getCons() {
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
        return Swc4jAstType.CondExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (alt == oldNode && newNode instanceof ISwc4jAstExpr newAlt) {
            setAlt(newAlt);
            return true;
        }
        if (cons == oldNode && newNode instanceof ISwc4jAstExpr newCons) {
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
    public Swc4jAstCondExpr setAlt(ISwc4jAstExpr alt) {
        this.alt = AssertionUtils.notNull(alt, "Alt");
        this.alt.setParent(this);
        return this;
    }

    /**
     * Sets cons.
     *
     * @param cons the cons
     * @return the cons
     */
    public Swc4jAstCondExpr setCons(ISwc4jAstExpr cons) {
        this.cons = AssertionUtils.notNull(cons, "Cons");
        this.cons.setParent(this);
        return this;
    }

    /**
     * Sets test.
     *
     * @param test the test
     * @return the test
     */
    public Swc4jAstCondExpr setTest(ISwc4jAstExpr test) {
        this.test = AssertionUtils.notNull(test, "Test");
        this.test.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitCondExpr(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
