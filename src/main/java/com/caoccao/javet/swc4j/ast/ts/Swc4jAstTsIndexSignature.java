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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
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
public class Swc4jAstTsIndexSignature
        extends Swc4jAst
        implements ISwc4jAstClassMember, ISwc4jAstTsTypeElement {
    protected final List<ISwc4jAstTsFnParam> params;
    @Jni2RustField(name = "is_static")
    protected boolean _static;
    protected boolean readonly;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstTsIndexSignature(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            @Jni2RustParam(name = "is_static") boolean _static,
            Swc4jSpan span) {
        super(span);
        setReadonly(readonly);
        setStatic(_static);
        setTypeAnn(typeAnn);
        this.params = AssertionUtils.notNull(params, "Params");
        this.params.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsIndexSignature create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstTsIndexSignature create(List<ISwc4jAstTsFnParam> params) {
        return create(params, null);
    }

    public static Swc4jAstTsIndexSignature create(List<ISwc4jAstTsFnParam> params, Swc4jAstTsTypeAnn typeAnn) {
        return create(params, typeAnn, false);
    }

    public static Swc4jAstTsIndexSignature create(
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn,
            boolean readonly) {
        return create(params, typeAnn, readonly, false);
    }

    public static Swc4jAstTsIndexSignature create(
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            boolean _static) {
        return new Swc4jAstTsIndexSignature(params, typeAnn, readonly, _static, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(params);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsIndexSignature;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
    }

    @Jni2RustMethod
    public boolean isStatic() {
        return _static;
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
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsIndexSignature setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public Swc4jAstTsIndexSignature setStatic(boolean _static) {
        this._static = _static;
        return this;
    }

    public Swc4jAstTsIndexSignature setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsIndexSignature(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
