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

/**
 * The type swc4j ast ts mapped type.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsMappedType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    /**
     * The Name type.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> nameType;
    /**
     * The Optional.
     */
    protected Optional<Swc4jAstTruePlusMinus> optional;
    /**
     * The Readonly.
     */
    protected Optional<Swc4jAstTruePlusMinus> readonly;
    /**
     * The Type ann.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> typeAnn;
    /**
     * The Type param.
     */
    protected Swc4jAstTsTypeParam typeParam;

    /**
     * Instantiates a new swc4j ast ts mapped type.
     *
     * @param readonly  the readonly
     * @param typeParam the type param
     * @param nameType  the name type
     * @param optional  the optional
     * @param typeAnn   the type ann
     * @param span      the span
     */
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
    }

    /**
     * Create swc4j ast ts mapped type.
     *
     * @param typeParam the type param
     * @return the swc4j ast ts mapped type
     */
    public static Swc4jAstTsMappedType create(Swc4jAstTsTypeParam typeParam) {
        return create(null, typeParam);
    }

    /**
     * Create swc4j ast ts mapped type.
     *
     * @param readonly  the readonly
     * @param typeParam the type param
     * @return the swc4j ast ts mapped type
     */
    public static Swc4jAstTsMappedType create(Swc4jAstTruePlusMinus readonly, Swc4jAstTsTypeParam typeParam) {
        return create(readonly, typeParam, null);
    }

    /**
     * Create swc4j ast ts mapped type.
     *
     * @param readonly  the readonly
     * @param typeParam the type param
     * @param nameType  the name type
     * @return the swc4j ast ts mapped type
     */
    public static Swc4jAstTsMappedType create(
            Swc4jAstTruePlusMinus readonly,
            Swc4jAstTsTypeParam typeParam,
            ISwc4jAstTsType nameType) {
        return create(readonly, typeParam, nameType, null);
    }

    /**
     * Create swc4j ast ts mapped type.
     *
     * @param readonly  the readonly
     * @param typeParam the type param
     * @param nameType  the name type
     * @param optional  the optional
     * @return the swc4j ast ts mapped type
     */
    public static Swc4jAstTsMappedType create(
            Swc4jAstTruePlusMinus readonly,
            Swc4jAstTsTypeParam typeParam,
            ISwc4jAstTsType nameType,
            Swc4jAstTruePlusMinus optional) {
        return create(readonly, typeParam, nameType, optional, null);
    }

    /**
     * Create swc4j ast ts mapped type.
     *
     * @param readonly  the readonly
     * @param typeParam the type param
     * @param nameType  the name type
     * @param optional  the optional
     * @param typeAnn   the type ann
     * @return the swc4j ast ts mapped type
     */
    public static Swc4jAstTsMappedType create(
            Swc4jAstTruePlusMinus readonly,
            Swc4jAstTsTypeParam typeParam,
            ISwc4jAstTsType nameType,
            Swc4jAstTruePlusMinus optional,
            ISwc4jAstTsType typeAnn) {
        return new Swc4jAstTsMappedType(readonly, typeParam, nameType, optional, typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(typeParam);
        nameType.ifPresent(childNodes::add);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets name type.
     *
     * @return the name type
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getNameType() {
        return nameType;
    }

    /**
     * Gets optional.
     *
     * @return the optional
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTruePlusMinus> getOptional() {
        return optional;
    }

    /**
     * Gets readonly.
     *
     * @return the readonly
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTruePlusMinus> getReadonly() {
        return readonly;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsMappedType;
    }

    /**
     * Gets type ann.
     *
     * @return the type ann
     */
    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getTypeAnn() {
        return typeAnn;
    }

    /**
     * Gets type param.
     *
     * @return the type param
     */
    @Jni2RustMethod
    public Swc4jAstTsTypeParam getTypeParam() {
        return typeParam;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (nameType.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstTsType)) {
            setNameType((ISwc4jAstTsType) newNode);
            return true;
        }
        if (typeAnn.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstTsType)) {
            setTypeAnn((ISwc4jAstTsType) newNode);
            return true;
        }
        if (typeParam == oldNode && newNode instanceof Swc4jAstTsTypeParam newTypeParam) {
            setTypeParam(newTypeParam);
            return true;
        }
        return false;
    }

    /**
     * Sets name type.
     *
     * @param nameType the name type
     * @return the name type
     */
    public Swc4jAstTsMappedType setNameType(ISwc4jAstTsType nameType) {
        this.nameType = Optional.ofNullable(nameType);
        this.nameType.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets optional.
     *
     * @param optional the optional
     * @return the optional
     */
    public Swc4jAstTsMappedType setOptional(Swc4jAstTruePlusMinus optional) {
        this.optional = Optional.ofNullable(optional);
        return this;
    }

    /**
     * Sets readonly.
     *
     * @param readonly the readonly
     * @return the readonly
     */
    public Swc4jAstTsMappedType setReadonly(Swc4jAstTruePlusMinus readonly) {
        this.readonly = Optional.ofNullable(readonly);
        return this;
    }

    /**
     * Sets type ann.
     *
     * @param typeAnn the type ann
     * @return the type ann
     */
    public Swc4jAstTsMappedType setTypeAnn(ISwc4jAstTsType typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    /**
     * Sets type param.
     *
     * @param typeParam the type param
     * @return the type param
     */
    public Swc4jAstTsMappedType setTypeParam(Swc4jAstTsTypeParam typeParam) {
        this.typeParam = AssertionUtils.notNull(typeParam, "TypeParam");
        this.typeParam.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsMappedType(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
