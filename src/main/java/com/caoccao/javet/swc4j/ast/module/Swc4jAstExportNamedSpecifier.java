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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExportSpecifier;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleExportName;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstExportNamedSpecifier
        extends Swc4jAst
        implements ISwc4jAstExportSpecifier {
    protected Optional<ISwc4jAstModuleExportName> exported;
    protected ISwc4jAstModuleExportName orig;
    @Jni2RustField(name = "is_type_only")
    protected boolean typeOnly;

    @Jni2RustMethod
    public Swc4jAstExportNamedSpecifier(
            ISwc4jAstModuleExportName orig,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName exported,
            boolean typeOnly,
            Swc4jSpan span) {
        super(span);
        setExported(exported);
        setOrig(orig);
        setTypeOnly(typeOnly);
    }

    public static Swc4jAstExportNamedSpecifier create(ISwc4jAstModuleExportName orig) {
        return create(orig, null);
    }

    public static Swc4jAstExportNamedSpecifier create(
            ISwc4jAstModuleExportName orig,
            ISwc4jAstModuleExportName exported) {
        return create(orig, exported, false);
    }

    public static Swc4jAstExportNamedSpecifier create(
            ISwc4jAstModuleExportName orig,
            ISwc4jAstModuleExportName exported,
            boolean typeOnly) {
        return new Swc4jAstExportNamedSpecifier(orig, exported, typeOnly, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(orig);
        exported.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstModuleExportName> getExported() {
        return exported;
    }

    @Jni2RustMethod
    public ISwc4jAstModuleExportName getOrig() {
        return orig;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExportNamedSpecifier;
    }

    @Jni2RustMethod
    public boolean isTypeOnly() {
        return typeOnly;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (exported.isPresent() && exported.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstModuleExportName)) {
            setExported((ISwc4jAstModuleExportName) newNode);
            return true;
        }
        if (orig == oldNode && newNode instanceof ISwc4jAstModuleExportName) {
            setOrig((ISwc4jAstModuleExportName) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstExportNamedSpecifier setExported(ISwc4jAstModuleExportName exported) {
        this.exported = Optional.ofNullable(exported);
        this.exported.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstExportNamedSpecifier setOrig(ISwc4jAstModuleExportName orig) {
        this.orig = AssertionUtils.notNull(orig, "Orig");
        this.orig.setParent(this);
        return this;
    }

    public Swc4jAstExportNamedSpecifier setTypeOnly(boolean typeOnly) {
        this.typeOnly = typeOnly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitExportNamedSpecifier(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
