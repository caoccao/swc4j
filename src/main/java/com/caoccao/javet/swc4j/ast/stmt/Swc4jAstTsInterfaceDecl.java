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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDefaultDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsInterfaceBody;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsInterfaceDecl
        extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstDefaultDecl {
    protected final List<Swc4jAstTsExprWithTypeArgs> _extends;
    protected Swc4jAstTsInterfaceBody body;
    protected boolean declare;
    protected Swc4jAstIdent id;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstTsInterfaceDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setDeclare(declare);
        setId(id);
        setTypeParams(typeParams);
        this._extends = AssertionUtils.notNull(_extends, "Extends");
        this._extends.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsInterfaceDecl create(Swc4jAstIdent id, Swc4jAstTsInterfaceBody body) {
        return create(id, SimpleList.of(), body);
    }

    public static Swc4jAstTsInterfaceDecl create(
            Swc4jAstIdent id,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body) {
        return create(id, null, _extends, body);
    }

    public static Swc4jAstTsInterfaceDecl create(
            Swc4jAstIdent id,
            Swc4jAstTsTypeParamDecl typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body) {
        return create(id, false, typeParams, _extends, body);
    }

    public static Swc4jAstTsInterfaceDecl create(
            Swc4jAstIdent id,
            boolean declare,
            Swc4jAstTsTypeParamDecl typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body) {
        return new Swc4jAstTsInterfaceDecl(id, declare, typeParams, _extends, body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public Swc4jAstTsInterfaceBody getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(_extends);
        childNodes.add(id);
        typeParams.ifPresent(childNodes::add);
        childNodes.add(body);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Swc4jAstTsExprWithTypeArgs> getExtends() {
        return _extends;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsInterfaceDecl;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof Swc4jAstTsInterfaceBody newBody) {
            setBody(newBody);
            return true;
        }
        if (!_extends.isEmpty() && newNode instanceof Swc4jAstTsExprWithTypeArgs newExtend) {
            final int size = _extends.size();
            for (int i = 0; i < size; i++) {
                if (_extends.get(i) == oldNode) {
                    _extends.set(i, newExtend);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (id == oldNode && newNode instanceof Swc4jAstIdent newId) {
            setId(newId);
            return true;
        }
        if (typeParams.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamDecl)) {
            setTypeParams((Swc4jAstTsTypeParamDecl) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsInterfaceDecl setBody(Swc4jAstTsInterfaceBody body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    public Swc4jAstTsInterfaceDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstTsInterfaceDecl setId(Swc4jAstIdent id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    public Swc4jAstTsInterfaceDecl setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsInterfaceDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
