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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
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
public class Swc4jAstTsMethodSignature
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected final List<ISwc4jAstTsFnParam> params;
    protected boolean computed;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr key;
    protected boolean optional;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    @Jni2RustMethod
    public Swc4jAstTsMethodSignature(
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            Swc4jSpan span) {
        super(span);
        setComputed(computed);
        setKey(key);
        setOptional(optional);
        setTypeAnn(typeAnn);
        setTypeParams(typeParams);
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsMethodSignature create(ISwc4jAstExpr key) {
        return create(key, SimpleList.of());
    }

    public static Swc4jAstTsMethodSignature create(
            ISwc4jAstExpr key,
            List<ISwc4jAstTsFnParam> params) {
        return create(key, false, params);
    }

    public static Swc4jAstTsMethodSignature create(
            ISwc4jAstExpr key,
            boolean computed,
            List<ISwc4jAstTsFnParam> params) {
        return create(key, computed, false, params);
    }

    public static Swc4jAstTsMethodSignature create(
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params) {
        return create(key, computed, optional, params, null);
    }

    public static Swc4jAstTsMethodSignature create(
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn) {
        return create(key, computed, optional, params, typeAnn, null);
    }

    public static Swc4jAstTsMethodSignature create(
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jAstTsTypeParamDecl typeParams) {
        return new Swc4jAstTsMethodSignature(key, computed, optional, params, typeAnn, typeParams, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        childNodes.add(key);
        typeAnn.ifPresent(childNodes::add);
        typeParams.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getKey() {
        return key;
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsMethodSignature;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    @Jni2RustMethod
    public boolean isComputed() {
        return computed;
    }

    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (key == oldNode && newNode instanceof ISwc4jAstExpr) {
            setKey((ISwc4jAstExpr) newNode);
            return true;
        }
        if (!params.isEmpty() && newNode instanceof ISwc4jAstTsFnParam) {
            final int size = params.size();
            for (int i = 0; i < size; i++) {
                if (params.get(i) == oldNode) {
                    params.set(i, (ISwc4jAstTsFnParam) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        if (typeParams.isPresent() && typeParams.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamDecl)) {
            setTypeParams((Swc4jAstTsTypeParamDecl) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsMethodSignature setComputed(boolean computed) {
        this.computed = computed;
        return this;
    }

    public Swc4jAstTsMethodSignature setKey(ISwc4jAstExpr key) {
        this.key = AssertionUtils.notNull(key, "Key");
        this.key.setParent(this);
        return this;
    }

    public Swc4jAstTsMethodSignature setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Swc4jAstTsMethodSignature setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstTsMethodSignature setTypeParams(Swc4jAstTsTypeParamDecl typeParams) {
        this.typeParams = Optional.ofNullable(typeParams);
        this.typeParams.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsMethodSignature(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
