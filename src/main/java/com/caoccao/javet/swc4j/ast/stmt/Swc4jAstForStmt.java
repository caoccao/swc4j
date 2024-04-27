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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
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
    protected Optional<ISwc4jAstExpr> test;
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
        updateParent();
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

    public Swc4jAstForStmt setBody(ISwc4jAstStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        return this;
    }

    public Swc4jAstForStmt setInit(ISwc4jAstVarDeclOrExpr init) {
        this.init = Optional.ofNullable(init);
        return this;
    }

    public Swc4jAstForStmt setTest(ISwc4jAstExpr test) {
        this.test = Optional.ofNullable(test);
        return this;
    }

    public Swc4jAstForStmt setUpdate(ISwc4jAstExpr update) {
        this.update = Optional.ofNullable(update);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitForStmt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
