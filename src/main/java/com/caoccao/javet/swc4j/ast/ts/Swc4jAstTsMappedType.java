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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstTruePlusMinus;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsMappedType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> nameType;
    protected Optional<Swc4jAstTruePlusMinus> optional;
    protected Optional<Swc4jAstTruePlusMinus> readonly;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> typeAnn;
    protected Swc4jAstTsTypeParam typeParam;

    @Jni2RustMethod
    public Swc4jAstTsMappedType(
            @Jni2RustParam(optional = true) Swc4jAstTruePlusMinus readonly,
            Swc4jAstTsTypeParam typeParam,
            @Jni2RustParam(optional = true) ISwc4jAstTsType nameType,
            @Jni2RustParam(optional = true) Swc4jAstTruePlusMinus optional,
            @Jni2RustParam(optional = true) ISwc4jAstTsType typeAnn,
            Swc4jSpan span) {
        super(span);
        setNameType(nameType);
        setOptional(optional);
        setReadonly(readonly);
        setTypeAnn(typeAnn);
        setTypeParam(typeParam);
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(typeParam);
        nameType.ifPresent(childNodes::add);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getNameType() {
        return nameType;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTruePlusMinus> getOptional() {
        return optional;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTruePlusMinus> getReadonly() {
        return readonly;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsMappedType;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public Swc4jAstTsTypeParam getTypeParam() {
        return typeParam;
    }

    public Swc4jAstTsMappedType setNameType(ISwc4jAstTsType nameType) {
        this.nameType = Optional.ofNullable(nameType);
        return this;
    }

    public Swc4jAstTsMappedType setOptional(Swc4jAstTruePlusMinus optional) {
        this.optional = Optional.ofNullable(optional);
        return this;
    }

    public Swc4jAstTsMappedType setReadonly(Swc4jAstTruePlusMinus readonly) {
        this.readonly = Optional.ofNullable(readonly);
        return this;
    }

    public Swc4jAstTsMappedType setTypeAnn(ISwc4jAstTsType typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        return this;
    }

    public Swc4jAstTsMappedType setTypeParam(Swc4jAstTsTypeParam typeParam) {
        this.typeParam = AssertionUtils.notNull(typeParam, "TypeParam");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsMappedType(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
