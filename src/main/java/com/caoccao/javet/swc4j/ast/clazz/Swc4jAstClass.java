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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstClass
        extends Swc4jAst {
    @Jni2RustField(name = "is_abstract")
    protected final boolean _abstract;
    protected final List<Swc4jAstTsExprWithTypeArgs> _implements;
    protected final List<ISwc4jAstClassMember> body;
    protected final List<Swc4jAstDecorator> decorators;
    protected final Optional<ISwc4jAstExpr> superClass;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> superTypeParams;
    protected final Optional<Swc4jAstTsTypeParamDecl> typeParams;

    public Swc4jAstClass(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            ISwc4jAstExpr superClass,
            boolean _abstract,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeParamInstantiation superTypeParams,
            List<Swc4jAstTsExprWithTypeArgs> _implements,
            Swc4jAstSpan span) {
        super(span);
        this._abstract = _abstract;
        this._implements = _implements;
        this.body = AssertionUtils.notNull(body, "Body");
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.superClass = Optional.ofNullable(superClass);
        this.superTypeParams = Optional.ofNullable(superTypeParams);
        this.typeParams = Optional.ofNullable(typeParams);
        childNodes = SimpleList.copyOf(_implements);
        childNodes.addAll(body);
        childNodes.addAll(decorators);
        childNodes.add(superClass);
        childNodes.add(superTypeParams);
        childNodes.add(typeParams);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public List<ISwc4jAstClassMember> getBody() {
        return body;
    }

    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    public List<Swc4jAstTsExprWithTypeArgs> getImplements() {
        return _implements;
    }

    public Optional<ISwc4jAstExpr> getSuperClass() {
        return superClass;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getSuperTypeParams() {
        return superTypeParams;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Class;
    }

    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    public boolean isAbstract() {
        return _abstract;
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
