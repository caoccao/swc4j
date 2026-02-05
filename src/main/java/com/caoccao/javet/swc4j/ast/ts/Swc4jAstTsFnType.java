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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnOrConstructorType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast ts fn type.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsFnType
        extends Swc4jAst
        implements ISwc4jAstTsFnOrConstructorType {
    /**
     * The Params.
     */
    protected final List<ISwc4jAstTsFnParam> params;
    /**
     * The Type ann.
     */
    @Jni2RustField(box = true)
    protected Swc4jAstTsTypeAnn typeAnn;
    /**
     * The Type params.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    /**
     * Instantiates a new swc4j ast ts fn type.
     *
     * @param params     the params
     * @param typeParams the type params
     * @param typeAnn    the type ann
     * @param span       the span
     */
    @Jni2RustMethod
    public Swc4jAstTsFnType(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jSpan span) {
        super(span);
        setTypeAnn(typeAnn);
        setTypeParams(typeParams);
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast ts fn type.
     *
     * @param typeAnn the type ann
     * @return the swc4j ast ts fn type
     */
    public static Swc4jAstTsFnType create(Swc4jAstTsTypeAnn typeAnn) {
        return create(SimpleList.of(), typeAnn);
    }

    /**
     * Create swc4j ast ts fn type.
     *
     * @param params  the params
     * @param typeAnn the type ann
     * @return the swc4j ast ts fn type
     */
    public static Swc4jAstTsFnType create(List<ISwc4jAstTsFnParam> params, Swc4jAstTsTypeAnn typeAnn) {
        return create(params, null, typeAnn);
    }

    /**
     * Create swc4j ast ts fn type.
     *
     * @param params     the params
     * @param typeParams the type params
     * @param typeAnn    the type ann
     * @return the swc4j ast ts fn type
     */
    public static Swc4jAstTsFnType create(
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn typeAnn) {
        return new Swc4jAstTsFnType(params, typeParams, typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        childNodes.add(typeAnn);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    @Jni2RustMethod
    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsFnType;
    }

    /**
     * Gets type ann.
     *
     * @return the type ann
     */
    @Jni2RustMethod
    public Swc4jAstTsTypeAnn getTypeAnn() {
        return typeAnn;
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

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!params.isEmpty() && newNode instanceof ISwc4jAstTsFnParam newParam) {
            final int size = params.size();
            for (int i = 0; i < size; i++) {
                if (params.get(i) == oldNode) {
                    params.set(i, newParam);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (typeAnn == oldNode && newNode instanceof Swc4jAstTsTypeAnn newTypeAnn) {
            setTypeAnn(newTypeAnn);
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
     * Sets type ann.
     *
     * @param typeAnn the type ann
     * @return the type ann
     */
    public Swc4jAstTsFnType setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        this.typeAnn.setParent(this);
        return this;
    }

    /**
     * Sets type params.
     *
     * @param typeParams the type params
     * @return the type params
     */
    public Swc4jAstTsFnType setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsFnType(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
