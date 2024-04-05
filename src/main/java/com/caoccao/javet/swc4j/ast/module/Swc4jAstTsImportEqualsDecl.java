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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleRef;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstTsImportEqualsDecl
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    @Jni2RustField(name = "is_export")
    protected final boolean export;
    protected final Swc4jAstIdent id;
    @Jni2RustField(value = "TsModuleRef::TsEntityName(TsEntityName::Ident(Ident::dummy()))")
    protected final ISwc4jAstTsModuleRef moduleRef;
    @Jni2RustField(name = "is_type_only")
    protected final boolean typeOnly;

    public Swc4jAstTsImportEqualsDecl(
            boolean export,
            boolean typeOnly,
            Swc4jAstIdent id,
            ISwc4jAstTsModuleRef moduleRef,
            Swc4jAstSpan span) {
        super(span);
        this.export = export;
        this.id = AssertionUtils.notNull(id, "Id");
        this.moduleRef = AssertionUtils.notNull(moduleRef, "ModuleRef");
        this.typeOnly = typeOnly;
        childNodes = SimpleList.immutableOf(id, moduleRef);
        updateParent();
    }

    public Swc4jAstIdent getId() {
        return id;
    }

    public ISwc4jAstTsModuleRef getModuleRef() {
        return moduleRef;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsImportEqualsDecl;
    }

    public boolean isExport() {
        return export;
    }

    public boolean isTypeOnly() {
        return typeOnly;
    }
}
