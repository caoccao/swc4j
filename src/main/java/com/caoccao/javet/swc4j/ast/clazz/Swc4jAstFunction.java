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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstFunction
        extends Swc4jAst {
    protected final List<Swc4jAstDecorator> decorators;
    protected final List<Swc4jAstParam> params;
    @Jni2RustField(name = "is_async")
    protected boolean _async;
    protected Optional<Swc4jAstBlockStmt> body;
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    @Jni2RustField(name = "is_generator")
    protected boolean generator;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> returnType;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

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

    public static Swc4jAstFunction create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstFunction create(List<Swc4jAstParam> params) {
        return create(params, SimpleList.of());
    }

    public static Swc4jAstFunction create(List<Swc4jAstParam> params, List<Swc4jAstDecorator> decorators) {
        return create(params, decorators, null);
    }

    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body) {
        return create(params, decorators, body, false);
    }

    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator) {
        return create(params, decorators, body, generator, false, null, null);
    }

    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeParamDecl typeParams) {
        return create(params, decorators, body, generator, _async, typeParams, null);
    }

    public static Swc4jAstFunction create(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeAnn returnType) {
        return create(params, decorators, body, generator, _async, null, returnType);
    }

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

    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    @Jni2RustMethod
    public List<Swc4jAstParam> getParams() {
        return params;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getReturnType() {
        return returnType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Function;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    @Jni2RustMethod
    public boolean isAsync() {
        return _async;
    }

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

    public Swc4jAstFunction setAsync(boolean _async) {
        this._async = _async;
        return this;
    }

    public Swc4jAstFunction setBody(Swc4jAstBlockStmt body) {
        this.body = Optional.ofNullable(body);
        this.body.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstFunction setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    public Swc4jAstFunction setGenerator(boolean generator) {
        this.generator = generator;
        return this;
    }

    public Swc4jAstFunction setReturnType(Swc4jAstTsTypeAnn returnType) {
        this.returnType = Optional.ofNullable(returnType);
        this.returnType.ifPresent(node -> node.setParent(this));
        return this;
    }

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
