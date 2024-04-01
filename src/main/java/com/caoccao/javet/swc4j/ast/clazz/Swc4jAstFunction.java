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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Collections;
import java.util.List;

public class Swc4jAstFunction
        extends Swc4jAst {
    protected final boolean _async;
    @Nullable
    protected final Swc4jAstBlockStmt body;
    protected final List<Swc4jAstDecorator> decorators;
    protected final boolean generator;
    protected final List<Swc4jAstParam> params;
    @Nullable
    protected final Swc4jAstTsTypeAnn returnType;
    @Nullable
    protected final Swc4jAstTsTypeParamDecl typeParams;

    public Swc4jAstFunction(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstBlockStmt body,
            boolean generator,
            boolean _async,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this._async = _async;
        this.body = body;
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.generator = generator;
        this.params = AssertionUtils.notNull(params, "Params");
        this.returnType = returnType;
        this.typeParams = typeParams;
        children = SimpleList.copyOf(decorators);
        children.addAll(params);
        children.add(body);
        children.add(typeParams);
        children.add(returnType);
        children = Collections.unmodifiableList(children);
        updateParent();
    }

    public Swc4jAstBlockStmt getBody() {
        return body;
    }

    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    public List<Swc4jAstParam> getParams() {
        return params;
    }

    public Swc4jAstTsTypeAnn getReturnType() {
        return returnType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Function;
    }

    public Swc4jAstTsTypeParamDecl getTypeParams() {
        return typeParams;
    }

    public boolean isAsync() {
        return _async;
    }

    public boolean isGenerator() {
        return generator;
    }
}
