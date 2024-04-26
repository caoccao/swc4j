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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
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
    protected Optional<ISwc4jAstExpr> superClass;
    protected Optional<Swc4jAstTsTypeParamInstantiation> superTypeParams;
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstClass(
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
        setSuperClass(superClass);
        setSuperTypeParams(superTypeParams);
        setTypeParams(typeParams);
        this._implements = AssertionUtils.notNull(_implements, "Implements");
        this.body = AssertionUtils.notNull(body, "Body");
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        updateParent();
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

    public Swc4jAstClass setAbstract(boolean _abstract) {
        this._abstract = _abstract;
        return this;
    }

    public Swc4jAstClass setSuperClass(ISwc4jAstExpr superClass) {
        this.superClass = Optional.ofNullable(superClass);
        return this;
    }

    public Swc4jAstClass setSuperTypeParams(Swc4jAstTsTypeParamInstantiation superTypeParams) {
        this.superTypeParams = Optional.ofNullable(superTypeParams);
        return this;
    }

    public Swc4jAstClass setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitClass(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
