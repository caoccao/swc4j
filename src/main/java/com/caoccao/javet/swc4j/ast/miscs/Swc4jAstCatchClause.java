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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

import java.util.Optional;

public class Swc4jAstCatchClause
        extends Swc4jAst {
    protected final Swc4jAstBlockStmt body;
    protected final Optional<ISwc4jAstPat> param;

    public Swc4jAstCatchClause(
            ISwc4jAstPat param,
            Swc4jAstBlockStmt body,
            Swc4jSpan span) {
        super(span);
        this.param = Optional.ofNullable(param);
        this.body = AssertionUtils.notNull(body, "Body");
        childNodes = SimpleList.immutableOf(param, body);
        updateParent();
    }

    public Swc4jAstBlockStmt getBody() {
        return body;
    }

    public Optional<ISwc4jAstPat> getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.CatchClause;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitCatchClause(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
