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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
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
    @Jni2RustField(name = "is_generator")
    protected boolean generator;
    protected Optional<Swc4jAstTsTypeAnn> returnType;
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstFunction(
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
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        setGenerator(generator);
        this.params = AssertionUtils.notNull(params, "Params");
        setReturnType(returnType);
        setTypeParams(typeParams);
        updateParent();
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

    public Swc4jAstFunction setAsync(boolean _async) {
        this._async = _async;
        return this;
    }

    public Swc4jAstFunction setBody(Swc4jAstBlockStmt body) {
        this.body = Optional.ofNullable(body);
        return this;
    }

    public Swc4jAstFunction setGenerator(boolean generator) {
        this.generator = generator;
        return this;
    }

    public Swc4jAstFunction setReturnType(Swc4jAstTsTypeAnn returnType) {
        this.returnType = Optional.ofNullable(returnType);
        return this;
    }

    public Swc4jAstFunction setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitFunction(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
