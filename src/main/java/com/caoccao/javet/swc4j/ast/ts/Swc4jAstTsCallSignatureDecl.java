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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsCallSignatureDecl
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected final List<ISwc4jAstTsFnParam> params;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstTsCallSignatureDecl(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            Swc4jSpan span) {
        super(span);
        setTypeAnn(typeAnn);
        setTypeParams(typeParams);
        this.params = AssertionUtils.notNull(params, "Params");
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        typeAnn.ifPresent(childNodes::add);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsCallSignatureDecl;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    public Swc4jAstTsCallSignatureDecl setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        return this;
    }

    public Swc4jAstTsCallSignatureDecl setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsCallSignatureDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
