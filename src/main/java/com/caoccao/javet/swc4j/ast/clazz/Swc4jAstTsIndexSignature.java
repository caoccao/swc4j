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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstTsIndexSignature
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final boolean _static;
    protected final List<ISwc4jAstTsFnParam> params;
    protected final boolean readonly;
    @Nullable
    protected final Swc4jAstTsTypeAnn typeAnn;

    public Swc4jAstTsIndexSignature(
            List<ISwc4jAstTsFnParam> params,
            Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            boolean _static,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this._static = _static;
        this.params = AssertionUtils.notNull(params, "Params");
        this.readonly = readonly;
        this.typeAnn = typeAnn;
        children = SimpleList.copyOf(params);
        children.add(typeAnn);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public List<ISwc4jAstTsFnParam> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsIndexSignature;
    }

    public Swc4jAstTsTypeAnn getTypeAnn() {
        return typeAnn;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isStatic() {
        return _static;
    }
}
