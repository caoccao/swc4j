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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstMethodKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstClassMethod
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    @Jni2RustField(name = "is_abstract")
    protected boolean _abstract;
    @Jni2RustField(name = "is_override")
    protected boolean _override;
    @Jni2RustField(name = "is_static")
    protected boolean _static;
    protected Optional<Swc4jAstAccessibility> accessibility;
    @Jni2RustField(box = true)
    protected Swc4jAstFunction function;
    protected ISwc4jAstPropName key;
    protected Swc4jAstMethodKind kind;
    @Jni2RustField(name = "is_optional")
    protected boolean optional;

    @Jni2RustMethod
    public Swc4jAstClassMethod(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            @Jni2RustParam(name = "is_static") boolean _static,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            @Jni2RustParam(name = "is_abstract") boolean _abstract,
            boolean optional,
            @Jni2RustParam(name = "is_override") boolean _override,
            Swc4jSpan span) {
        super(span);
        setAbstract(_abstract);
        setAccessibility(accessibility);
        setFunction(function);
        setKey(key);
        setKind(kind);
        setOptional(optional);
        setOverride(_override);
        setStatic(_static);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind) {
        return create(key, function, kind, false);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static) {
        return create(key, function, kind, _static, null);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility) {
        return create(key, function, kind, _static, accessibility, false);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility,
            boolean _abstract) {
        return create(key, function, kind, _static, accessibility, _abstract, false);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility,
            boolean _abstract,
            boolean optional) {
        return create(key, function, kind, _static, accessibility, _abstract, optional, false);
    }

    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility,
            boolean _abstract,
            boolean optional,
            boolean _override) {
        return new Swc4jAstClassMethod(
                key, function, kind, _static, accessibility,
                _abstract, optional, _override, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(key, function);
    }

    @Jni2RustMethod
    public Swc4jAstFunction getFunction() {
        return function;
    }

    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    @Jni2RustMethod
    public Swc4jAstMethodKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassMethod;
    }

    @Jni2RustMethod
    public boolean isAbstract() {
        return _abstract;
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
    public boolean isStatic() {
        return _static;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (function == oldNode && newNode instanceof Swc4jAstFunction) {
            setFunction((Swc4jAstFunction) newNode);
            return true;
        }
        if (key == oldNode && newNode instanceof ISwc4jAstPropName) {
            setKey((ISwc4jAstPropName) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstClassMethod setAbstract(boolean _abstract) {
        this._abstract = _abstract;
        return this;
    }

    public Swc4jAstClassMethod setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    public Swc4jAstClassMethod setFunction(Swc4jAstFunction function) {
        this.function = AssertionUtils.notNull(function, "Function");
        this.function.setParent(this);
        return this;
    }

    public Swc4jAstClassMethod setKey(ISwc4jAstPropName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstClassMethod setKind(Swc4jAstMethodKind kind) {
        this.kind = AssertionUtils.notNull(kind, "Kind");
        return this;
    }

    public Swc4jAstClassMethod setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Swc4jAstClassMethod setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    public Swc4jAstClassMethod setStatic(boolean _static) {
        this._static = _static;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitClassMethod(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
