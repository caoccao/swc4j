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

/**
 * The type swc4j ast class method.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstClassMethod
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    /**
     * The Abstract.
     */
    @Jni2RustField(name = "is_abstract")
    protected boolean _abstract;
    /**
     * The Override.
     */
    @Jni2RustField(name = "is_override")
    protected boolean _override;
    /**
     * The constant _static.
     */
    @Jni2RustField(name = "is_static")
    protected boolean _static;
    /**
     * The Accessibility.
     */
    protected Optional<Swc4jAstAccessibility> accessibility;
    /**
     * The Function.
     */
    @Jni2RustField(box = true)
    protected Swc4jAstFunction function;
    /**
     * The Key.
     */
    protected ISwc4jAstPropName key;
    /**
     * The Kind.
     */
    protected Swc4jAstMethodKind kind;
    /**
     * The Optional.
     */
    @Jni2RustField(name = "is_optional")
    protected boolean optional;

    /**
     * Instantiates a new swc4j ast class method.
     *
     * @param key           the key
     * @param function      the function
     * @param kind          the kind
     * @param _static       the static
     * @param accessibility the accessibility
     * @param _abstract     the abstract
     * @param optional      the optional
     * @param _override     the override
     * @param span          the span
     */
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

    /**
     * Create swc4j ast class method.
     *
     * @param key      the key
     * @param function the function
     * @param kind     the kind
     * @return the swc4j ast class method
     */
    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind) {
        return create(key, function, kind, false);
    }

    /**
     * Create swc4j ast class method.
     *
     * @param key      the key
     * @param function the function
     * @param kind     the kind
     * @param _static  the static
     * @return the swc4j ast class method
     */
    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static) {
        return create(key, function, kind, _static, null);
    }

    /**
     * Create swc4j ast class method.
     *
     * @param key           the key
     * @param function      the function
     * @param kind          the kind
     * @param _static       the static
     * @param accessibility the accessibility
     * @return the swc4j ast class method
     */
    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility) {
        return create(key, function, kind, _static, accessibility, false);
    }

    /**
     * Create swc4j ast class method.
     *
     * @param key           the key
     * @param function      the function
     * @param kind          the kind
     * @param _static       the static
     * @param accessibility the accessibility
     * @param _abstract     the abstract
     * @return the swc4j ast class method
     */
    public static Swc4jAstClassMethod create(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility,
            boolean _abstract) {
        return create(key, function, kind, _static, accessibility, _abstract, false);
    }

    /**
     * Create swc4j ast class method.
     *
     * @param key           the key
     * @param function      the function
     * @param kind          the kind
     * @param _static       the static
     * @param accessibility the accessibility
     * @param _abstract     the abstract
     * @param optional      the optional
     * @return the swc4j ast class method
     */
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

    /**
     * Create swc4j ast class method.
     *
     * @param key           the key
     * @param function      the function
     * @param kind          the kind
     * @param _static       the static
     * @param accessibility the accessibility
     * @param _abstract     the abstract
     * @param optional      the optional
     * @param _override     the override
     * @return the swc4j ast class method
     */
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

    /**
     * Gets accessibility.
     *
     * @return the accessibility
     */
    @Jni2RustMethod
    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(key, function);
    }

    /**
     * Gets function.
     *
     * @return the function
     */
    @Jni2RustMethod
    public Swc4jAstFunction getFunction() {
        return function;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    @Jni2RustMethod
    public Swc4jAstMethodKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassMethod;
    }

    /**
     * Is abstract boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isAbstract() {
        return _abstract;
    }

    /**
     * Is optional boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    /**
     * Is override boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isOverride() {
        return _override;
    }

    /**
     * Is static boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isStatic() {
        return _static;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (function == oldNode && newNode instanceof Swc4jAstFunction newFunction) {
            setFunction(newFunction);
            return true;
        }
        if (key == oldNode && newNode instanceof ISwc4jAstPropName newKey) {
            setKey(newKey);
            return true;
        }
        return false;
    }

    /**
     * Sets abstract.
     *
     * @param _abstract the abstract
     * @return the abstract
     */
    public Swc4jAstClassMethod setAbstract(boolean _abstract) {
        this._abstract = _abstract;
        return this;
    }

    /**
     * Sets accessibility.
     *
     * @param accessibility the accessibility
     * @return the accessibility
     */
    public Swc4jAstClassMethod setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    /**
     * Sets function.
     *
     * @param function the function
     * @return the function
     */
    public Swc4jAstClassMethod setFunction(Swc4jAstFunction function) {
        this.function = AssertionUtils.notNull(function, "Function");
        this.function.setParent(this);
        return this;
    }

    /**
     * Sets key.
     *
     * @param key the key
     * @return the key
     */
    public Swc4jAstClassMethod setKey(ISwc4jAstPropName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    /**
     * Sets kind.
     *
     * @param kind the kind
     * @return the kind
     */
    public Swc4jAstClassMethod setKind(Swc4jAstMethodKind kind) {
        this.kind = AssertionUtils.notNull(kind, "Kind");
        return this;
    }

    /**
     * Sets optional.
     *
     * @param optional the optional
     * @return the optional
     */
    public Swc4jAstClassMethod setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * Sets override.
     *
     * @param _override the override
     * @return the override
     */
    public Swc4jAstClassMethod setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    /**
     * Sets static.
     *
     * @param _static the static
     * @return the static
     */
    public Swc4jAstClassMethod setStatic(boolean _static) {
        this._static = _static;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitClassMethod(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
