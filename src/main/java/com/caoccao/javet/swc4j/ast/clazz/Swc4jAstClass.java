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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstClass
        extends Swc4jAst {
    @Jni2RustField(name = "implements")
    protected final List<Swc4jAstTsExprWithTypeArgs> _implements;
    protected final List<ISwc4jAstClassMember> body;
    protected final List<Swc4jAstDecorator> decorators;
    @Jni2RustField(name = "is_abstract")
    protected boolean _abstract;
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> superClass;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> superTypeParams;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstClass(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            @Jni2RustParam(optional = true) ISwc4jAstExpr superClass,
            @Jni2RustParam(name = "is_abstract") boolean _abstract,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation superTypeParams,
            @Jni2RustParam(name = "implements") List<Swc4jAstTsExprWithTypeArgs> _implements,
            Swc4jSpan span) {
        super(span);
        setAbstract(_abstract);
        setCtxt(ctxt);
        setSuperClass(superClass);
        setSuperTypeParams(superTypeParams);
        setTypeParams(typeParams);
        this._implements = AssertionUtils.notNull(_implements, "Implements");
        this._implements.forEach(node -> node.setParent(this));
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.forEach(node -> node.setParent(this));
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.decorators.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstClass create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstClass create(List<ISwc4jAstClassMember> body) {
        return create(SimpleList.of(), body);
    }

    public static Swc4jAstClass create(List<Swc4jAstDecorator> decorators, List<ISwc4jAstClassMember> body) {
        return create(decorators, body, null);
    }

    public static Swc4jAstClass create(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass) {
        return create(decorators, body, superClass, false);
    }

    public static Swc4jAstClass create(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract) {
        return create(decorators, body, superClass, _abstract, null);
    }

    public static Swc4jAstClass create(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract,
            Swc4jAstTsTypeParamDecl typeParams) {
        return create(decorators, body, superClass, _abstract, typeParams, null);
    }

    public static Swc4jAstClass create(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeParamInstantiation superTypeParams) {
        return create(decorators, body, superClass, _abstract, typeParams, superTypeParams, SimpleList.of());
    }

    public static Swc4jAstClass create(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeParamInstantiation superTypeParams,
            List<Swc4jAstTsExprWithTypeArgs> _implements) {
        return create(0, decorators, body, superClass, _abstract, typeParams, superTypeParams, _implements);
    }

    public static Swc4jAstClass create(
            int ctxt,
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeParamInstantiation superTypeParams,
            List<Swc4jAstTsExprWithTypeArgs> _implements) {
        return new Swc4jAstClass(
                ctxt, decorators, body, superClass, _abstract,
                typeParams, superTypeParams, _implements, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public List<ISwc4jAstClassMember> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(_implements);
        childNodes.addAll(body);
        childNodes.addAll(decorators);
        superClass.ifPresent(childNodes::add);
        superTypeParams.ifPresent(childNodes::add);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    @Jni2RustMethod
    public List<Swc4jAstTsExprWithTypeArgs> getImplements() {
        return _implements;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getSuperClass() {
        return superClass;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getSuperTypeParams() {
        return superTypeParams;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Class;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    @Jni2RustMethod
    public boolean isAbstract() {
        return _abstract;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!_implements.isEmpty() && newNode instanceof Swc4jAstTsExprWithTypeArgs newImplement) {
            final int size = _implements.size();
            for (int i = 0; i < size; i++) {
                if (_implements.get(i) == oldNode) {
                    _implements.set(i, newImplement);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (!body.isEmpty() && newNode instanceof ISwc4jAstClassMember) {
            final int size = body.size();
            for (int i = 0; i < size; i++) {
                if (body.get(i) == oldNode) {
                    body.set(i, (ISwc4jAstClassMember) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (!decorators.isEmpty() && newNode instanceof Swc4jAstDecorator) {
            final int size = decorators.size();
            for (int i = 0; i < size; i++) {
                if (decorators.get(i) == oldNode) {
                    decorators.set(i, (Swc4jAstDecorator) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (superClass.isPresent() && superClass.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setSuperClass((ISwc4jAstExpr) newNode);
            return true;
        }
        if (superTypeParams.isPresent() && superTypeParams.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setSuperTypeParams((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        if (typeParams.isPresent() && typeParams.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamDecl)) {
            setTypeParams((Swc4jAstTsTypeParamDecl) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstClass setAbstract(boolean _abstract) {
        this._abstract = _abstract;
        return this;
    }

    public Swc4jAstClass setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    public Swc4jAstClass setSuperClass(ISwc4jAstExpr superClass) {
        this.superClass = Optional.ofNullable(superClass);
        this.superClass.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstClass setSuperTypeParams(Swc4jAstTsTypeParamInstantiation superTypeParams) {
        this.superTypeParams = Optional.ofNullable(superTypeParams);
        this.superTypeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstClass setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitClass(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
