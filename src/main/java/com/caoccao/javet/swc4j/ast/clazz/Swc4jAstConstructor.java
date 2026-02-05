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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast constructor.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstConstructor
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    /**
     * The Params.
     */
    protected final List<ISwc4jAstParamOrTsParamProp> params;
    /**
     * The Accessibility.
     */
    protected Optional<Swc4jAstAccessibility> accessibility;
    /**
     * The Body.
     */
    protected Optional<Swc4jAstBlockStmt> body;
    /**
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Key.
     */
    protected ISwc4jAstPropName key;
    /**
     * The Optional.
     */
    @Jni2RustField(name = "is_optional")
    protected boolean optional;

    /**
     * Instantiates a new swc4j ast constructor.
     *
     * @param ctxt          the ctxt
     * @param key           the key
     * @param params        the params
     * @param body          the body
     * @param accessibility the accessibility
     * @param optional      the optional
     * @param span          the span
     */
    @Jni2RustMethod
    public Swc4jAstConstructor(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            boolean optional,
            Swc4jSpan span) {
        super(span);
        setAccessibility(accessibility);
        setBody(body);
        setCtxt(ctxt);
        setKey(key);
        setOptional(optional);
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param key the key
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(ISwc4jAstPropName key) {
        return create(key, SimpleList.of());
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param key    the key
     * @param params the params
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params) {
        return create(key, params, null);
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param key    the key
     * @param params the params
     * @param body   the body
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body) {
        return create(key, params, body, null);
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param key           the key
     * @param params        the params
     * @param body          the body
     * @param accessibility the accessibility
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body,
            Swc4jAstAccessibility accessibility) {
        return create(key, params, body, accessibility, false);
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param key           the key
     * @param params        the params
     * @param body          the body
     * @param accessibility the accessibility
     * @param optional      the optional
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body,
            Swc4jAstAccessibility accessibility,
            boolean optional) {
        return create(0, key, params, body, accessibility, optional);
    }

    /**
     * Create swc4j ast constructor.
     *
     * @param ctxt          the ctxt
     * @param key           the key
     * @param params        the params
     * @param body          the body
     * @param accessibility the accessibility
     * @param optional      the optional
     * @return the swc4j ast constructor
     */
    public static Swc4jAstConstructor create(
            int ctxt,
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body,
            Swc4jAstAccessibility accessibility,
            boolean optional) {
        return new Swc4jAstConstructor(ctxt, key, params, body, accessibility, optional, Swc4jSpan.DUMMY);
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

    /**
     * Gets body.
     *
     * @return the body
     */
    @Jni2RustMethod
    public Optional<Swc4jAstBlockStmt> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        body.ifPresent(childNodes::add);
        childNodes.add(key);
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
     * Gets key.
     *
     * @return the key
     */
    @Jni2RustMethod
    public ISwc4jAstPropName getKey() {
        return key;
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    @Jni2RustMethod
    public List<ISwc4jAstParamOrTsParamProp> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Constructor;
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

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstBlockStmt)) {
            setBody((Swc4jAstBlockStmt) newNode);
            return true;
        }
        if (key == oldNode && newNode instanceof ISwc4jAstPropName newKey) {
            setKey(newKey);
            return true;
        }
        if (!params.isEmpty() && newNode instanceof ISwc4jAstParamOrTsParamProp newParam) {
            final int size = params.size();
            for (int i = 0; i < size; i++) {
                if (params.get(i) == oldNode) {
                    params.set(i, newParam);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets accessibility.
     *
     * @param accessibility the accessibility
     * @return the accessibility
     */
    public Swc4jAstConstructor setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstConstructor setBody(Swc4jAstBlockStmt body) {
        this.body = Optional.ofNullable(body);
        this.body.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstConstructor setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets key.
     *
     * @param key the key
     * @return the key
     */
    public Swc4jAstConstructor setKey(ISwc4jAstPropName key) {
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
    public Swc4jAstConstructor setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitConstructor(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
