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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
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
public class Swc4jAstTsModuleDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected Optional<ISwc4jAstTsNamespaceBody> body;
    protected boolean declare;
    protected boolean global;
    protected ISwc4jAstTsModuleName id;

    @Jni2RustMethod
    public Swc4jAstTsModuleDecl(
            boolean declare,
            boolean global,
            ISwc4jAstTsModuleName id,
            @Jni2RustParam(optional = true) ISwc4jAstTsNamespaceBody body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setDeclare(declare);
        setGlobal(global);
        setId(id);
    }

    public static Swc4jAstTsModuleDecl create(ISwc4jAstTsModuleName id) {
        return create(id, null);
    }

    public static Swc4jAstTsModuleDecl create(ISwc4jAstTsModuleName id, ISwc4jAstTsNamespaceBody body) {
        return create(false, id, body);
    }

    public static Swc4jAstTsModuleDecl create(
            boolean declare,
            ISwc4jAstTsModuleName id,
            ISwc4jAstTsNamespaceBody body) {
        return create(declare, false, id, body);
    }

    public static Swc4jAstTsModuleDecl create(
            boolean declare,
            boolean global,
            ISwc4jAstTsModuleName id,
            ISwc4jAstTsNamespaceBody body) {
        return new Swc4jAstTsModuleDecl(declare, global, id, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstTsNamespaceBody> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(id);
        body.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstTsModuleName getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsModuleDecl;
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
        if (body.isPresent() && body.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstTsNamespaceBody)) {
            setBody((ISwc4jAstTsNamespaceBody) newNode);
            return true;
        }
        if (id == oldNode && newNode instanceof ISwc4jAstTsModuleName) {
            setId((ISwc4jAstTsModuleName) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsModuleDecl setBody(ISwc4jAstTsNamespaceBody body) {
        this.body = Optional.ofNullable(body);
        this.body.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstTsModuleDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstTsModuleDecl setGlobal(boolean global) {
        this.global = global;
        return this;
    }

    public Swc4jAstTsModuleDecl setId(ISwc4jAstTsModuleName id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsModuleDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
