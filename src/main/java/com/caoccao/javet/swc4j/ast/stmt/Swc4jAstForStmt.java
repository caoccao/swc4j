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

import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;

import java.util.Optional;

public class Swc4jAstForStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    @Jni2RustField(box = true)
    protected final ISwc4jAstStmt body;
    protected final Optional<ISwc4jAstVarDeclOrExpr> init;
    protected final Optional<ISwc4jAstExpr> test;
    protected final Optional<ISwc4jAstExpr> update;

    public Swc4jAstForStmt(
            ISwc4jAstVarDeclOrExpr init,
            ISwc4jAstExpr test,
            ISwc4jAstExpr update,
            ISwc4jAstStmt body,
            Swc4jAstSpan span) {
        super(span);
        this.body = AssertionUtils.notNull(body, "Body");
        this.init = Optional.ofNullable(init);
        this.test = Optional.ofNullable(test);
        this.update = Optional.ofNullable(update);
        childNodes = SimpleList.immutableOf(init, test, update, body);
        updateParent();
    }

    public ISwc4jAstStmt getBody() {
        return body;
    }

    public Optional<ISwc4jAstVarDeclOrExpr> getInit() {
        return init;
    }

    public Optional<ISwc4jAstExpr> getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ForStmt;
    }

    public Optional<ISwc4jAstExpr> getUpdate() {
        return update;
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
