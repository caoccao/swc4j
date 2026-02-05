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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstDecorator;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsParamPropParam;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast ts param prop.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsParamProp
        extends Swc4jAst
        implements ISwc4jAstParamOrTsParamProp {
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
     * The Accessibility.
     */
    protected Optional<Swc4jAstAccessibility> accessibility;
    /**
     * The Param.
     */
    protected ISwc4jAstTsParamPropParam param;
    /**
     * The Readonly.
     */
    protected boolean readonly;

    /**
     * Instantiates a new swc4j ast ts param prop.
     *
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param _override     the override
     * @param readonly      the readonly
     * @param param         the param
     * @param span          the span
     */
    @Jni2RustMethod
    public Swc4jAstTsParamProp(
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            @Jni2RustParam(name = "is_override") boolean _override,
            boolean readonly,
            ISwc4jAstTsParamPropParam param,
            Swc4jSpan span) {
        super(span);
        setAccessibility(accessibility);
        setOverride(_override);
        setParam(param);
        setReadonly(readonly);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.decorators.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast ts param prop.
     *
     * @param param the param
     * @return the swc4j ast ts param prop
     */
    public static Swc4jAstTsParamProp create(ISwc4jAstTsParamPropParam param) {
        return create(SimpleList.of(), param);
    }

    /**
     * Create swc4j ast ts param prop.
     *
     * @param decorators the decorators
     * @param param      the param
     * @return the swc4j ast ts param prop
     */
    public static Swc4jAstTsParamProp create(List<Swc4jAstDecorator> decorators, ISwc4jAstTsParamPropParam param) {
        return create(decorators, null, param);
    }

    /**
     * Create swc4j ast ts param prop.
     *
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param param         the param
     * @return the swc4j ast ts param prop
     */
    public static Swc4jAstTsParamProp create(
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            ISwc4jAstTsParamPropParam param) {
        return create(decorators, accessibility, false, param);
    }

    /**
     * Create swc4j ast ts param prop.
     *
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param _override     the override
     * @param param         the param
     * @return the swc4j ast ts param prop
     */
    public static Swc4jAstTsParamProp create(
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean _override,
            ISwc4jAstTsParamPropParam param) {
        return create(decorators, accessibility, _override, false, param);
    }

    /**
     * Create swc4j ast ts param prop.
     *
     * @param decorators    the decorators
     * @param accessibility the accessibility
     * @param _override     the override
     * @param readonly      the readonly
     * @param param         the param
     * @return the swc4j ast ts param prop
     */
    public static Swc4jAstTsParamProp create(
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean _override,
            boolean readonly,
            ISwc4jAstTsParamPropParam param) {
        return new Swc4jAstTsParamProp(decorators, accessibility, _override, readonly, param, Swc4jSpan.DUMMY);
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
        childNodes.add(param);
        return childNodes;
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
     * Gets param.
     *
     * @return the param
     */
    @Jni2RustMethod
    public ISwc4jAstTsParamPropParam getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsParamProp;
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
        if (param == oldNode && newNode instanceof ISwc4jAstTsParamPropParam) {
            setParam((ISwc4jAstTsParamPropParam) newNode);
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
    public Swc4jAstTsParamProp setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    /**
     * Sets override.
     *
     * @param _override the override
     * @return the override
     */
    public Swc4jAstTsParamProp setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    /**
     * Sets param.
     *
     * @param param the param
     * @return the param
     */
    public Swc4jAstTsParamProp setParam(ISwc4jAstTsParamPropParam param) {
        this.param = AssertionUtils.notNull(param, "Param");
        this.param.setParent(this);
        return this;
    }

    /**
     * Sets readonly.
     *
     * @param readonly the readonly
     * @return the readonly
     */
    public Swc4jAstTsParamProp setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsParamProp(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
