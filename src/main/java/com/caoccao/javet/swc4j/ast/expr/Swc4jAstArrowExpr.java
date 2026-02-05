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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
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
 * The type swc4j ast arrow expr.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstArrowExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    /**
     * The Params.
     */
    protected final List<ISwc4jAstPat> params;
    /**
     * The Async.
     */
    @Jni2RustField(name = "is_async")
    protected boolean _async;
    /**
     * The Body.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstBlockStmtOrExpr body;
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
     * Instantiates a new swc4j ast arrow expr.
     *
     * @param ctxt       the ctxt
     * @param params     the params
     * @param body       the body
     * @param _async     the async
     * @param generator  the generator
     * @param typeParams the type params
     * @param returnType the return type
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstArrowExpr(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            @Jni2RustParam(name = "is_async") boolean _async,
            boolean generator,
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
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param body the body
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(ISwc4jAstBlockStmtOrExpr body) {
        return create(SimpleList.of(), body);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param params the params
     * @param body   the body
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(List<ISwc4jAstPat> params, ISwc4jAstBlockStmtOrExpr body) {
        return create(params, body, false);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param params the params
     * @param body   the body
     * @param _async the async
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async) {
        return create(params, body, _async, false);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param params    the params
     * @param body      the body
     * @param _async    the async
     * @param generator the generator
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async,
            boolean generator) {
        return create(params, body, _async, generator, null);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param params     the params
     * @param body       the body
     * @param _async     the async
     * @param generator  the generator
     * @param typeParams the type params
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async,
            boolean generator,
            Swc4jAstTsTypeParamDecl typeParams) {
        return create(params, body, _async, generator, typeParams, null);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param params     the params
     * @param body       the body
     * @param _async     the async
     * @param generator  the generator
     * @param typeParams the type params
     * @param returnType the return type
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async,
            boolean generator,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType) {
        return create(0, params, body, _async, generator, typeParams, returnType);
    }

    /**
     * Create swc4j ast arrow expr.
     *
     * @param ctxt       the ctxt
     * @param params     the params
     * @param body       the body
     * @param _async     the async
     * @param generator  the generator
     * @param typeParams the type params
     * @param returnType the return type
     * @return the swc4j ast arrow expr
     */
    public static Swc4jAstArrowExpr create(
            int ctxt,
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async,
            boolean generator,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType) {
        return new Swc4jAstArrowExpr(ctxt, params, body, _async, generator, typeParams, returnType, Swc4jSpan.DUMMY);
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    @Jni2RustMethod
    public ISwc4jAstBlockStmtOrExpr getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        childNodes.add(body);
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
     * Gets params.
     *
     * @return the params
     */
    @Jni2RustMethod
    public List<ISwc4jAstPat> getParams() {
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
        return Swc4jAstType.ArrowExpr;
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
        if (body == oldNode && newNode instanceof ISwc4jAstBlockStmtOrExpr newBody) {
            setBody(newBody);
            return true;
        }
        if (!params.isEmpty() && newNode instanceof ISwc4jAstPat newParam) {
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
    public Swc4jAstArrowExpr setAsync(boolean _async) {
        this._async = _async;
        return this;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstArrowExpr setBody(ISwc4jAstBlockStmtOrExpr body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstArrowExpr setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets generator.
     *
     * @param generator the generator
     * @return the generator
     */
    public Swc4jAstArrowExpr setGenerator(boolean generator) {
        this.generator = generator;
        return this;
    }

    /**
     * Sets return type.
     *
     * @param returnType the return type
     * @return the return type
     */
    public Swc4jAstArrowExpr setReturnType(Swc4jAstTsTypeAnn returnType) {
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
    public Swc4jAstArrowExpr setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitArrowExpr(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
