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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstForStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    @Jni2RustField(box = true)
    protected ISwc4jAstStmt body;
    protected Optional<ISwc4jAstVarDeclOrExpr> init;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> test;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> update;

    @Jni2RustMethod
    public Swc4jAstForStmt(
            @Jni2RustParam(optional = true) ISwc4jAstVarDeclOrExpr init,
            @Jni2RustParam(optional = true) ISwc4jAstExpr test,
            @Jni2RustParam(optional = true) ISwc4jAstExpr update,
            ISwc4jAstStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setInit(init);
        setTest(test);
        setUpdate(update);
    }

    public static Swc4jAstForStmt create() {
        return create(ISwc4jAstStmt.createDefault());
    }

    public static Swc4jAstForStmt create(ISwc4jAstStmt body) {
        return create(null, body);
    }

    public static Swc4jAstForStmt create(ISwc4jAstVarDeclOrExpr init, ISwc4jAstStmt body) {
        return create(init, null, body);
    }

    public static Swc4jAstForStmt create(ISwc4jAstVarDeclOrExpr init, ISwc4jAstExpr test, ISwc4jAstStmt body) {
        return create(init, test, null, body);
    }

    public static Swc4jAstForStmt create(
            ISwc4jAstVarDeclOrExpr init,
            ISwc4jAstExpr test,
            ISwc4jAstExpr update,
            ISwc4jAstStmt body) {
        return new Swc4jAstForStmt(init, test, update, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public ISwc4jAstStmt getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(body);
        init.ifPresent(childNodes::add);
        test.ifPresent(childNodes::add);
        update.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstVarDeclOrExpr> getInit() {
        return init;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ForStmt;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getUpdate() {
        return update;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstStmt newBody) {
            setBody(newBody);
            return true;
        }
        if (init.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstVarDeclOrExpr)) {
            setInit((ISwc4jAstVarDeclOrExpr) newNode);
            return true;
        }
        if (test.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setTest((ISwc4jAstExpr) newNode);
            return true;
        }
        if (update.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setUpdate((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstForStmt setBody(ISwc4jAstStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    public Swc4jAstForStmt setInit(ISwc4jAstVarDeclOrExpr init) {
        this.init = Optional.ofNullable(init);
        this.init.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstForStmt setTest(ISwc4jAstExpr test) {
        this.test = Optional.ofNullable(test);
        this.test.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstForStmt setUpdate(ISwc4jAstExpr update) {
        this.update = Optional.ofNullable(update);
        this.update.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitForStmt(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
