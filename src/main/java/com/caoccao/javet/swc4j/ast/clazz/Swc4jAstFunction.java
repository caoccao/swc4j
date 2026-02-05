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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast function.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstFunction
        extends Swc4jAst {
    /**
     * The Decorators.
     */
    protected final List<Swc4jAstDecorator> decorators;
    /**
     * The Params.
     */
    protected final List<Swc4jAstParam> params;
    /**
     * The Async.
     */
    @Jni2RustField(name = "is_async")
    protected boolean _async;
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
     * The Generator.
     */
    @Jni2RustField(name = "is_generator")
    protected boolean generator;
    /**
     * The Return type.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> returnType;
    /**
     * The Type params.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    /**
     * Instantiates a new swc4j ast function.
     *
     * @param ctxt       the ctxt
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @param _async     the async
     * @param typeParams the type params
     * @param returnType the return type
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstFunction(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam(name = "is_generator") boolean generator,
            @Jni2RustParam(name = "is_async") boolean _async,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn returnType,
            Swc4jSpan span) {
        super(span);
        setAsync(_async);
        setBody(body);
        setCtxt(ctxt);
        setGenerator(generator);
        setReturnType(returnType);
        setTypeParams(typeParams);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.decorators.forEach(node -> node.setParent(this));
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast function.
     *
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create() {
        return create(SimpleList.of());
    }

    /**
     * Create swc4j ast function.
     *
     * @param params the params
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(List<Swc4jAstParam> params) {
        return create(params, SimpleList.of());
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(List<Swc4jAstParam> params, List<Swc4jAstDecorator> decorators) {
        return create(params, decorators, null);
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body) {
        return create(params, decorators, body, false);
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator) {
        return create(params, decorators, body, generator, false, null, null);
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @param _async     the async
     * @param typeParams the type params
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeParamDecl typeParams) {
        return create(params, decorators, body, generator, _async, typeParams, null);
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @param _async     the async
     * @param returnType the return type
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeAnn returnType) {
        return create(params, decorators, body, generator, _async, null, returnType);
    }

    /**
     * Create swc4j ast function.
     *
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @param _async     the async
     * @param typeParams the type params
     * @param returnType the return type
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType) {
        return create(0, params, decorators, body, generator, _async, typeParams, returnType);
    }

    /**
     * Create swc4j ast function.
     *
     * @param ctxt       the ctxt
     * @param params     the params
     * @param decorators the decorators
     * @param body       the body
     * @param generator  the generator
     * @param _async     the async
     * @param typeParams the type params
     * @param returnType the return type
     * @return the swc4j ast function
     */
    public static Swc4jAstFunction create(
            int ctxt,
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType) {
        return new Swc4jAstFunction(
                ctxt, params, decorators, body, generator,
                _async, typeParams, returnType, Swc4jSpan.DUMMY);
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
        List<ISwc4jAst> childNodes = SimpleList.copyOf(decorators);
        childNodes.addAll(params);
        body.ifPresent(childNodes::add);
        typeParams.ifPresent(childNodes::add);
        returnType.ifPresent(childNodes::add);
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
     * Gets params.
     *
     * @return the params
     */
    @Jni2RustMethod
    public List<Swc4jAstParam> getParams() {
        return params;
    }

    /**
     * Gets return type.
     *
     * @return the return type
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getReturnType() {
        return returnType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Function;
    }

    /**
     * Gets type params.
     *
     * @return the type params
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    /**
     * Is async boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isAsync() {
        return _async;
    }

    /**
     * Is generator boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isGenerator() {
        return generator;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstBlockStmt)) {
            setBody((Swc4jAstBlockStmt) newNode);
            return true;
        }
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
        if (!params.isEmpty() && newNode instanceof Swc4jAstParam newParam) {
            final int size = params.size();
            for (int i = 0; i < size; i++) {
                if (params.get(i) == oldNode) {
                    params.set(i, newParam);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (returnType.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setReturnType((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        if (typeParams.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamDecl)) {
            setTypeParams((Swc4jAstTsTypeParamDecl) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets async.
     *
     * @param _async the async
     * @return the async
     */
    public Swc4jAstFunction setAsync(boolean _async) {
        this._async = _async;
        return this;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstFunction setBody(Swc4jAstBlockStmt body) {
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
    public Swc4jAstFunction setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets generator.
     *
     * @param generator the generator
     * @return the generator
     */
    public Swc4jAstFunction setGenerator(boolean generator) {
        this.generator = generator;
        return this;
    }

    /**
     * Sets return type.
     *
     * @param returnType the return type
     * @return the return type
     */
    public Swc4jAstFunction setReturnType(Swc4jAstTsTypeAnn returnType) {
        this.returnType = Optional.ofNullable(returnType);
        this.returnType.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets type params.
     *
     * @param typeParams the type params
     * @return the type params
     */
    public Swc4jAstFunction setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitFunction(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
