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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
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
public class Swc4jAstExportAll
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    @Jni2RustField(box = true)
    protected Swc4jAstStr src;
    protected boolean typeOnly;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstObjectLit> with;

    @Jni2RustMethod
    public Swc4jAstExportAll(
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            Swc4jSpan span) {
        super(span);
        setSrc(src);
        setTypeOnly(typeOnly);
        setWith(with);
    }

    public static Swc4jAstExportAll create(Swc4jAstStr src) {
        return create(src, false);
    }

    public static Swc4jAstExportAll create(Swc4jAstStr src, boolean typeOnly) {
        return create(src, typeOnly, null);
    }

    public static Swc4jAstExportAll create(Swc4jAstStr src, boolean typeOnly, Swc4jAstObjectLit with) {
        return new Swc4jAstExportAll(src, typeOnly, with, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(src);
        with.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Swc4jAstStr getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExportAll;
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
        if (src == oldNode && newNode instanceof Swc4jAstStr newSrc) {
            setSrc(newSrc);
            return true;
        }
        if (with.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstObjectLit)) {
            setWith((Swc4jAstObjectLit) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstExportAll setSrc(Swc4jAstStr src) {
        this.src = AssertionUtils.notNull(src, "Src");
        this.src.setParent(this);
        return this;
    }

    public Swc4jAstExportAll setTypeOnly(boolean typeOnly) {
        this.typeOnly = typeOnly;
        return this;
    }

    public Swc4jAstExportAll setWith(Swc4jAstObjectLit with) {
        this.with = Optional.ofNullable(with);
        this.with.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitExportAll(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
