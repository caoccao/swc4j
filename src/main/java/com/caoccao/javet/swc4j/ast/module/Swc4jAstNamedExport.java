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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExportSpecifier;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstNamedExport
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    protected final List<ISwc4jAstExportSpecifier> specifiers;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstStr> src;
    protected boolean typeOnly;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstObjectLit> with;

    @Jni2RustMethod
    public Swc4jAstNamedExport(
            List<ISwc4jAstExportSpecifier> specifiers,
            @Jni2RustParam(optional = true) Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            Swc4jSpan span) {
        super(span);
        setSrc(src);
        setTypeOnly(typeOnly);
        setWith(with);
        this.specifiers = AssertionUtils.notNull(specifiers, "Specifiers");
        this.specifiers.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstNamedExport create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstNamedExport create(List<ISwc4jAstExportSpecifier> specifiers) {
        return create(specifiers, null);
    }

    public static Swc4jAstNamedExport create(List<ISwc4jAstExportSpecifier> specifiers, Swc4jAstStr src) {
        return create(specifiers, src, false);
    }

    public static Swc4jAstNamedExport create(
            List<ISwc4jAstExportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly) {
        return create(specifiers, src, typeOnly, null);
    }

    public static Swc4jAstNamedExport create(
            List<ISwc4jAstExportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstObjectLit with) {
        return new Swc4jAstNamedExport(specifiers, src, typeOnly, with, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(specifiers);
        src.ifPresent(childNodes::add);
        with.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstExportSpecifier> getSpecifiers() {
        return specifiers;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstStr> getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.NamedExport;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstObjectLit> getWith() {
        return with;
    }

    @Jni2RustMethod
    public boolean isTypeOnly() {
        return typeOnly;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!specifiers.isEmpty() && newNode instanceof ISwc4jAstExportSpecifier) {
            final int size = specifiers.size();
            for (int i = 0; i < size; i++) {
                if (specifiers.get(i) == oldNode) {
                    specifiers.set(i, (ISwc4jAstExportSpecifier) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (src.isPresent() && src.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstStr)) {
            setSrc((Swc4jAstStr) newNode);
            return true;
        }
        if (with.isPresent() && with.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstObjectLit)) {
            setWith((Swc4jAstObjectLit) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstNamedExport setSrc(Swc4jAstStr src) {
        this.src = Optional.ofNullable(src);
        this.src.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstNamedExport setTypeOnly(boolean typeOnly) {
        this.typeOnly = typeOnly;
        return this;
    }

    public Swc4jAstNamedExport setWith(Swc4jAstObjectLit with) {
        this.with = Optional.ofNullable(with);
        this.with.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitNamedExport(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
