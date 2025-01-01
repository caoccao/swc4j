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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstIfStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstStmt> alt;
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt cons;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr test;

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

    public static Swc4jAstIfStmt create(ISwc4jAstExpr test) {
        return create(test, ISwc4jAstStmt.createDefault());
    }

    public static Swc4jAstIfStmt create(ISwc4jAstExpr test, ISwc4jAstStmt cons) {
        return create(test, cons, null);
    }

    public static Swc4jAstIfStmt create(ISwc4jAstExpr test, ISwc4jAstStmt cons, ISwc4jAstStmt alt) {
        return new Swc4jAstIfStmt(test, cons, alt, Swc4jSpan.DUMMY);
    }

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

    @Jni2RustMethod
    public ISwc4jAstStmt getCons() {
        return cons;
    }

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
        if (alt.isPresent() && alt.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstStmt)) {
            setAlt((ISwc4jAstStmt) newNode);
            return true;
        }
        if (cons == oldNode && newNode instanceof ISwc4jAstStmt) {
            setCons((ISwc4jAstStmt) newNode);
            return true;
        }
        if (test == oldNode && newNode instanceof ISwc4jAstExpr) {
            setTest((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstIfStmt setAlt(ISwc4jAstStmt alt) {
        this.alt = Optional.ofNullable(alt);
        this.alt.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstIfStmt setCons(ISwc4jAstStmt cons) {
        this.cons = AssertionUtils.notNull(cons, "Body");
        this.cons.setParent(this);
        return this;
    }

    public Swc4jAstIfStmt setTest(ISwc4jAstExpr test) {
        this.test = AssertionUtils.notNull(test, "Right");
        this.test.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitIfStmt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
