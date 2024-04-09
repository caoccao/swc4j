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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstArrowExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(name = "is_async")
    protected final boolean _async;
    @Jni2RustField(box = true)
    protected final ISwc4jAstBlockStmtOrExpr body;
    @Jni2RustField(name = "is_generator")
    protected final boolean generator;
    protected final List<ISwc4jAstPat> params;
    protected final Optional<Swc4jAstTsTypeAnn> returnType;
    protected final Optional<Swc4jAstTsTypeParamDecl> typeParams;

    public Swc4jAstArrowExpr(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean _async,
            boolean generator,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn returnType,
            Swc4jAstSpan span) {
        super(span);
        this._async = _async;
        this.body = AssertionUtils.notNull(body, "Body");
        this.generator = generator;
        this.params = SimpleList.immutableCopyOf(AssertionUtils.notNull(params, "Params"));
        this.returnType = Optional.ofNullable(returnType);
        this.typeParams = Optional.ofNullable(typeParams);
        childNodes = SimpleList.copyOf(params);
        childNodes.add(body);
        childNodes.add(typeParams);
        childNodes.add(returnType);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public ISwc4jAstBlockStmtOrExpr getBody() {
        return body;
    }

    public List<ISwc4jAstPat> getParams() {
        return params;
    }

    public Optional<Swc4jAstTsTypeAnn> getReturnType() {
        return returnType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ArrowExpr;
    }

    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    public boolean isAsync() {
        return _async;
    }

    public boolean isGenerator() {
        return generator;
    }
}
