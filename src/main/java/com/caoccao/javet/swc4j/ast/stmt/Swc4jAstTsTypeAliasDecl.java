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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
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
public class Swc4jAstTsTypeAliasDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected boolean declare;
    protected Swc4jAstIdent id;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType typeAnn;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstTsTypeAliasDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn,
            Swc4jSpan span) {
        super(span);
        setDeclare(declare);
        setId(id);
        setTypeAnn(typeAnn);
        setTypeParams(typeParams);
    }

    public static Swc4jAstTsTypeAliasDecl create(Swc4jAstIdent id, ISwc4jAstTsType typeAnn) {
        return create(id, null, typeAnn);
    }

    public static Swc4jAstTsTypeAliasDecl create(
            Swc4jAstIdent id,
            Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn) {
        return create(id, false, typeParams, typeAnn);
    }

    public static Swc4jAstTsTypeAliasDecl create(
            Swc4jAstIdent id,
            boolean declare,
            Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn) {
        return new Swc4jAstTsTypeAliasDecl(id, declare, typeParams, typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(id, typeAnn);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeAliasDecl;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
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
        if (id == oldNode && newNode instanceof Swc4jAstIdent) {
            setId((Swc4jAstIdent) newNode);
            return true;
        }
        if (typeAnn == oldNode && newNode instanceof ISwc4jAstTsType) {
            setTypeAnn((ISwc4jAstTsType) newNode);
            return true;
        }
        if (typeParams.isPresent() && typeParams.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamDecl)) {
            setTypeParams((Swc4jAstTsTypeParamDecl) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTypeAliasDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstTsTypeAliasDecl setId(Swc4jAstIdent id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    public Swc4jAstTsTypeAliasDecl setTypeAnn(ISwc4jAstTsType typeAnn) {
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        this.typeAnn.setParent(this);
        return this;
    }

    public Swc4jAstTsTypeAliasDecl setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTypeAliasDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
