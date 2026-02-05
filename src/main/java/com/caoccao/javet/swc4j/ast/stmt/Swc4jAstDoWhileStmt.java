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
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type swc4j ast do while stmt.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstDoWhileStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    /**
     * The Body.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt body;
    /**
     * The Test.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr test;

    /**
     * Instantiates a new swc4j ast do while stmt.
     *
     * @param test the test
     * @param body the body
     * @param span the span
     */
    @Jni2RustMethod
    public Swc4jAstDoWhileStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setTest(test);
    }

    /**
     * Create swc4j ast do while stmt.
     *
     * @param test the test
     * @return the swc4j ast do while stmt
     */
    public static Swc4jAstDoWhileStmt create(ISwc4jAstExpr test) {
        return create(test, ISwc4jAstStmt.createDefault());
    }

    /**
     * Create swc4j ast do while stmt.
     *
     * @param test the test
     * @param body the body
     * @return the swc4j ast do while stmt
     */
    public static Swc4jAstDoWhileStmt create(ISwc4jAstExpr test, ISwc4jAstStmt body) {
        return new Swc4jAstDoWhileStmt(test, body, Swc4jSpan.DUMMY);
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
        return SimpleList.of(test, body);
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
        return Swc4jAstType.DoWhileStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstStmt newBody) {
            setBody(newBody);
            return true;
        }
        if (test == oldNode && newNode instanceof ISwc4jAstExpr newTest) {
            setTest(newTest);
            return true;
        }
        return false;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstDoWhileStmt setBody(ISwc4jAstStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    /**
     * Sets test.
     *
     * @param test the test
     * @return the test
     */
    public Swc4jAstDoWhileStmt setTest(ISwc4jAstExpr test) {
        this.test = AssertionUtils.notNull(test, "Test");
        this.test.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitDoWhileStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
