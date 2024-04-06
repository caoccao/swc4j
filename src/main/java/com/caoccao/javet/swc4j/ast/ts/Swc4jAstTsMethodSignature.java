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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstTsMethodSignature
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected final boolean computed;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr key;
    protected final boolean optional;
    protected final List<ISwc4jAstTsFnParam> params;
    protected final boolean readonly;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;
    protected final Optional<Swc4jAstTsTypeParamDecl> typeParams;

    public Swc4jAstTsMethodSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstSpan span) {
        super(span);
        this.computed = computed;
        this.key = AssertionUtils.notNull(key, "Key");
        this.optional = optional;
        this.readonly = readonly;
        this.params = SimpleList.immutableCopyOf(AssertionUtils.notNull(params, "Params"));
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeParams = Optional.ofNullable(typeParams);
        childNodes = SimpleList.copyOf(params);
        childNodes.add(key);
        childNodes.add(typeAnn);
        childNodes.add(typeParams);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public ISwc4jAstExpr getKey() {
        return key;
    }

    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsMethodSignature;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    public boolean isComputed() {
        return computed;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
