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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
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
 * The type swc4j ast ts type ref.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsTypeRef
        extends Swc4jAst
        implements ISwc4jAstTsType {
    /**
     * The Type name.
     */
    protected ISwc4jAstTsEntityName typeName;
    /**
     * The Type params.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeParams;

    /**
     * Instantiates a new swc4j ast ts type ref.
     *
     * @param typeName   the type name
     * @param typeParams the type params
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstTsTypeRef(
            ISwc4jAstTsEntityName typeName,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jSpan span) {
        super(span);
        setTypeName(typeName);
        setTypeParams(typeParams);
    }

    /**
     * Create swc4j ast ts type ref.
     *
     * @param typeName the type name
     * @return the swc4j ast ts type ref
     */
    public static Swc4jAstTsTypeRef create(ISwc4jAstTsEntityName typeName) {
        return create(typeName, null);
    }

    /**
     * Create swc4j ast ts type ref.
     *
     * @param typeName   the type name
     * @param typeParams the type params
     * @return the swc4j ast ts type ref
     */
    public static Swc4jAstTsTypeRef create(
            ISwc4jAstTsEntityName typeName,
            Swc4jAstTsTypeParamInstantiation typeParams) {
        return new Swc4jAstTsTypeRef(typeName, typeParams, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(typeName);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeRef;
    }

    /**
     * Gets type name.
     *
     * @return the type name
     */
    @Jni2RustMethod
    public ISwc4jAstTsEntityName getTypeName() {
        return typeName;
    }

    /**
     * Gets type params.
     *
     * @return the type params
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeParams() {
        return typeParams;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (typeName == oldNode && newNode instanceof ISwc4jAstTsEntityName newTypeName) {
            setTypeName(newTypeName);
            return true;
        }
        if (typeParams.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeParams((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets type name.
     *
     * @param typeName the type name
     * @return the type name
     */
    public Swc4jAstTsTypeRef setTypeName(ISwc4jAstTsEntityName typeName) {
        this.typeName = AssertionUtils.notNull(typeName, "ExprName");
        this.typeName.setParent(this);
        return this;
    }

    /**
     * Sets type params.
     *
     * @param typeParams the type params
     * @return the type params
     */
    public Swc4jAstTsTypeRef setTypeParams(Swc4jAstTsTypeParamInstantiation typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsTypeRef(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
