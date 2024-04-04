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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamDecl;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsTypeAliasDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected final boolean declare;
    protected final Swc4jAstIdent id;
    @Jni2RustField(value = "Box::new(TsType::TsThisType(TsThisType { span: Default::default() }))")
    protected final ISwc4jAstTsType typeAnn;
    protected final Optional<Swc4jAstTsTypeParamDecl> typeParams;

    public Swc4jAstTsTypeAliasDecl(
            Swc4jAstIdent id,
            boolean declare,
            Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.declare = declare;
        this.id = AssertionUtils.notNull(id, "Id");
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        this.typeParams = Optional.ofNullable(typeParams);
        children = SimpleList.immutableOf(id, typeParams, typeAnn);
        updateParent();
    }

    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeAliasDecl;
    }

    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }

    public Optional<Swc4jAstTsTypeParamDecl> getTypeParams() {
        return typeParams;
    }

    public boolean isDeclare() {
        return declare;
    }
}
