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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast private prop.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstPrivateProp
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    /**
     * The Decorators.
     */
    protected final List<Swc4jAstDecorator> decorators;
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
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Definite.
     */
    protected boolean definite;
    /**
     * The Key.
     */
    protected Swc4jAstPrivateName key;
    /**
     * The Optional.
     */
    @Jni2RustField(name = "is_optional")
    protected boolean optional;
    /**
     * The Readonly.
     */
    protected boolean readonly;
    /**
     * The Type ann.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;
    /**
     * The Value.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> value;

    /**
     * Instantiates a new swc4j ast private prop.
     *
     * @param ctxt          the ctxt
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param _override     the override
     * @param readonly      the readonly
     * @param definite      the definite
     * @param span          the span
     */
    @Jni2RustMethod
    public Swc4jAstPrivateProp(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            Swc4jAstPrivateName key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(name = "is_static") boolean _static,
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            boolean optional,
            @Jni2RustParam(name = "is_override") boolean _override,
            boolean readonly,
            boolean definite,
            Swc4jSpan span) {
        super(span);
        setAccessibility(accessibility);
        setCtxt(ctxt);
        setDefinite(definite);
        setKey(key);
        setOptional(optional);
        setOverride(_override);
        setReadonly(readonly);
        setStatic(_static);
        setTypeAnn(typeAnn);
        setValue(value);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.decorators.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key the key
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(Swc4jAstPrivateName key) {
        return create(key, null);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key   the key
     * @param value the value
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(Swc4jAstPrivateName key, ISwc4jAstExpr value) {
        return create(key, value, null);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key     the key
     * @param value   the value
     * @param typeAnn the type ann
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(Swc4jAstPrivateName key, ISwc4jAstExpr value, Swc4jAstTsTypeAnn typeAnn) {
        return create(key, value, typeAnn, false);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key     the key
     * @param value   the value
     * @param typeAnn the type ann
     * @param _static the static
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static) {
        return create(key, value, typeAnn, _static, SimpleList.of());
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key        the key
     * @param value      the value
     * @param typeAnn    the type ann
     * @param _static    the static
     * @param decorators the decorators
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators) {
        return create(key, value, typeAnn, _static, decorators, null);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility) {
        return create(key, value, typeAnn, _static, decorators, accessibility, false);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional) {
        return create(key, value, typeAnn, _static, decorators, accessibility, optional, false);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param _override     the override
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            boolean _override) {
        return create(key, value, typeAnn, _static, decorators, accessibility, optional, _override, false);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param _override     the override
     * @param readonly      the readonly
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            boolean _override,
            boolean readonly) {
        return create(
                key, value, typeAnn, _static, decorators,
                accessibility, optional, _override, readonly, false);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param _override     the override
     * @param readonly      the readonly
     * @param definite      the definite
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            boolean _override,
            boolean readonly,
            boolean definite) {
        return create(
                0, key, value, typeAnn, _static,
                decorators, accessibility, optional, _override, readonly,
                definite);
    }

    /**
     * Create swc4j ast private prop.
     *
     * @param ctxt          the ctxt
     * @param key           the key
     * @param value         the value
     * @param typeAnn       the type ann
     * @param _static       the static
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param _override     the override
     * @param readonly      the readonly
     * @param definite      the definite
     * @return the swc4j ast private prop
     */
    public static Swc4jAstPrivateProp create(
            int ctxt,
            Swc4jAstPrivateName key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            boolean _override,
            boolean readonly,
            boolean definite) {
        return new Swc4jAstPrivateProp(
                ctxt, key, value, typeAnn, _static,
                decorators, accessibility, optional, _override, readonly,
                definite, Swc4jSpan.DUMMY);
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
        List<ISwc4jAst> childNodes = SimpleList.copyOf(decorators);
        childNodes.add(key);
        value.ifPresent(childNodes::add);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets ctxt.
     *
     * @return the ctxt
     */
    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    /**
     * Gets decorators.
     *
     * @return the decorators
     */
    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    @Jni2RustMethod
    public Swc4jAstPrivateName getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.PrivateProp;
    }

    /**
     * Gets type ann.
     *
     * @return the type ann
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getValue() {
        return value;
    }

    /**
     * Is definite boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isDefinite() {
        return definite;
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
     * Is readonly boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
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
        if (!decorators.isEmpty() && newNode instanceof Swc4jAstDecorator newDecorator) {
            final int size = decorators.size();
            for (int i = 0; i < size; i++) {
                if (decorators.get(i) == oldNode) {
                    decorators.set(i, newDecorator);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (key == oldNode && newNode instanceof Swc4jAstPrivateName) {
            setKey((Swc4jAstPrivateName) newNode);
            return true;
        }
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        if (value.isPresent() && value.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setValue((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets accessibility.
     *
     * @param accessibility the accessibility
     * @return the accessibility
     */
    public Swc4jAstPrivateProp setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstPrivateProp setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets definite.
     *
     * @param definite the definite
     * @return the definite
     */
    public Swc4jAstPrivateProp setDefinite(boolean definite) {
        this.definite = definite;
        return this;
    }

    /**
     * Sets key.
     *
     * @param key the key
     * @return the key
     */
    public Swc4jAstPrivateProp setKey(Swc4jAstPrivateName key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    /**
     * Sets optional.
     *
     * @param optional the optional
     * @return the optional
     */
    public Swc4jAstPrivateProp setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * Sets override.
     *
     * @param _override the override
     * @return the override
     */
    public Swc4jAstPrivateProp setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    /**
     * Sets readonly.
     *
     * @param readonly the readonly
     * @return the readonly
     */
    public Swc4jAstPrivateProp setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * Sets static.
     *
     * @param _static the static
     * @return the static
     */
    public Swc4jAstPrivateProp setStatic(boolean _static) {
        this._static = _static;
        return this;
    }

    /**
     * Sets type ann.
     *
     * @param typeAnn the type ann
     * @return the type ann
     */
    public Swc4jAstPrivateProp setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets value.
     *
     * @param value the value
     * @return the value
     */
    public Swc4jAstPrivateProp setValue(ISwc4jAstExpr value) {
        this.value = Optional.ofNullable(value);
        this.value.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitPrivateProp(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
