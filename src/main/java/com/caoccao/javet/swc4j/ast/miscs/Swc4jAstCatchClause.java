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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstCatchClause
        extends Swc4jAst {
    protected Swc4jAstBlockStmt body;
    protected Optional<ISwc4jAstPat> param;

    @Jni2RustMethod
    public Swc4jAstCatchClause(
            @Jni2RustParam(optional = true) ISwc4jAstPat param,
            Swc4jAstBlockStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setParam(param);
    }

    public static Swc4jAstCatchClause create(Swc4jAstBlockStmt body) {
        return create(null, body);
    }

    public static Swc4jAstCatchClause create(ISwc4jAstPat param, Swc4jAstBlockStmt body) {
        return new Swc4jAstCatchClause(param, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public Swc4jAstBlockStmt getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(body);
        param.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstPat> getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.CatchClause;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof Swc4jAstBlockStmt) {
            setBody((Swc4jAstBlockStmt) newNode);
            return true;
        }
        if (param.isPresent() && param.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstPat)) {
            setParam((ISwc4jAstPat) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstCatchClause setBody(Swc4jAstBlockStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    public Swc4jAstCatchClause setParam(ISwc4jAstPat param) {
        this.param = Optional.ofNullable(param);
        this.param.ifPresent(node -> node.setParent(this));
        return this;
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
