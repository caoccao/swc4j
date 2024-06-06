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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsNamespaceDecl
        extends Swc4jAst
        implements ISwc4jAstTsNamespaceBody {
    @Jni2RustField(box = true)
    protected ISwc4jAstTsNamespaceBody body;
    protected boolean declare;
    protected boolean global;
    protected Swc4jAstIdent id;

    @Jni2RustMethod
    public Swc4jAstTsNamespaceDecl(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setDeclare(declare);
        setGlobal(global);
        setId(id);
    }

    public static Swc4jAstTsNamespaceDecl create(Swc4jAstIdent id, ISwc4jAstTsNamespaceBody body) {
        return create(false, id, body);
    }

    public static Swc4jAstTsNamespaceDecl create(boolean declare, Swc4jAstIdent id, ISwc4jAstTsNamespaceBody body) {
        return create(declare, false, id, body);
    }

    public static Swc4jAstTsNamespaceDecl create(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body) {
        return new Swc4jAstTsNamespaceDecl(declare, global, id, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public ISwc4jAstTsNamespaceBody getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(id, body);
    }

    @Jni2RustMethod
    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsNamespaceDecl;
    }

    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Jni2RustMethod
    public boolean isGlobal() {
        return global;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstTsNamespaceBody) {
            setBody((ISwc4jAstTsNamespaceBody) newNode);
            return true;
        }
        if (id == oldNode && newNode instanceof Swc4jAstIdent) {
            setId((Swc4jAstIdent) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsNamespaceDecl setBody(ISwc4jAstTsNamespaceBody body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    public Swc4jAstTsNamespaceDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstTsNamespaceDecl setGlobal(boolean global) {
        this.global = global;
        return this;
    }

    public Swc4jAstTsNamespaceDecl setId(Swc4jAstIdent id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsNamespaceDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
