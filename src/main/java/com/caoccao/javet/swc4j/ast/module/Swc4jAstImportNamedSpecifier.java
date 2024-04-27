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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstImportSpecifier;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleExportName;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstImportNamedSpecifier
        extends Swc4jAst
        implements ISwc4jAstImportSpecifier {
    protected Optional<ISwc4jAstModuleExportName> imported;
    protected Swc4jAstIdent local;
    @Jni2RustField(name = "is_type_only")
    protected boolean typeOnly;

    @Jni2RustMethod
    public Swc4jAstImportNamedSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName imported,
            boolean typeOnly,
            Swc4jSpan span) {
        super(span);
        setImported(imported);
        setLocal(local);
        setTypeOnly(typeOnly);
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(local);
        imported.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstModuleExportName> getImported() {
        return imported;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getLocal() {
        return local;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ImportNamedSpecifier;
    }

    @Jni2RustMethod
    public boolean isTypeOnly() {
        return typeOnly;
    }

    public Swc4jAstImportNamedSpecifier setImported(ISwc4jAstModuleExportName imported) {
        this.imported = Optional.ofNullable(imported);
        return this;
    }

    public Swc4jAstImportNamedSpecifier setLocal(Swc4jAstIdent local) {
        this.local = AssertionUtils.notNull(local, "Local");
        return this;
    }

    public Swc4jAstImportNamedSpecifier setTypeOnly(boolean typeOnly) {
        this.typeOnly = typeOnly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitImportNamedSpecifier(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
