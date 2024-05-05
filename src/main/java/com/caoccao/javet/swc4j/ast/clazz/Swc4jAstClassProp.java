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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstClassProp
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final List<Swc4jAstDecorator> decorators;
    @Jni2RustField(name = "is_abstract")
    protected boolean _abstract;
    @Jni2RustField(name = "is_override")
    protected boolean _override;
    @Jni2RustField(name = "is_static")
    protected boolean _static;
    protected Optional<Swc4jAstAccessibility> accessibility;
    protected boolean declare;
    protected boolean definite;
    protected ISwc4jAstPropName key;
    @Jni2RustField(name = "is_optional")
    protected boolean optional;
    protected boolean readonly;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> value;

    @Jni2RustMethod
    public Swc4jAstClassProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(name = "is_static") boolean _static,
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            @Jni2RustParam(name = "is_abstract") boolean _abstract,
            boolean optional,
            @Jni2RustParam(name = "is_override") boolean _override,
            boolean readonly,
            boolean declare,
            boolean definite,
            Swc4jSpan span) {
        super(span);
        setAbstract(_abstract);
        setAccessibility(accessibility);
        setDeclare(declare);
        setDefinite(definite);
        setKey(key);
        setOptional(optional);
        setOverride(_override);
        setReadonly(readonly);
        setStatic(_static);
        setTypeAnn(typeAnn);
        setValue(value);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        updateParent();
    }

    @Jni2RustMethod
    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(decorators);
        childNodes.add(key);
        value.ifPresent(childNodes::add);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassProp;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getValue() {
        return value;
    }

    @Jni2RustMethod
    public boolean isAbstract() {
        return _abstract;
    }

    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Jni2RustMethod
    public boolean isDefinite() {
        return definite;
    }

    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    @Jni2RustMethod
    public boolean isOverride() {
        return _override;
    }

    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
    }

    @Jni2RustMethod
    public boolean isStatic() {
        return _static;
    }

    public Swc4jAstClassProp setAbstract(boolean _abstract) {
        this._abstract = _abstract;
        return this;
    }

    public Swc4jAstClassProp setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    public Swc4jAstClassProp setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstClassProp setDefinite(boolean definite) {
        this.definite = definite;
        return this;
    }

    public Swc4jAstClassProp setKey(ISwc4jAstPropName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        return this;
    }

    public Swc4jAstClassProp setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Swc4jAstClassProp setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    public Swc4jAstClassProp setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public Swc4jAstClassProp setStatic(boolean _static) {
        this._static = _static;
        return this;
    }

    public Swc4jAstClassProp setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        return this;
    }

    public Swc4jAstClassProp setValue(ISwc4jAstExpr value) {
        this.value = Optional.ofNullable(value);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitClassProp(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
