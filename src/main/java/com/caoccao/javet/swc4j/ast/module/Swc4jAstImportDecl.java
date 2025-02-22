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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstImportPhase;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstImportSpecifier;
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
public class Swc4jAstImportDecl
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    protected final List<ISwc4jAstImportSpecifier> specifiers;
    protected Swc4jAstImportPhase phase;
    @Jni2RustField(box = true)
    protected Swc4jAstStr src;
    protected boolean typeOnly;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstObjectLit> with;

    @Jni2RustMethod
    public Swc4jAstImportDecl(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            Swc4jAstImportPhase phase,
            Swc4jSpan span) {
        super(span);
        setSrc(src);
        setPhase(phase);
        setTypeOnly(typeOnly);
        setWith(with);
        this.specifiers = AssertionUtils.notNull(specifiers, "Specifiers");
        this.specifiers.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstImportDecl create(Swc4jAstStr src, Swc4jAstImportPhase phase) {
        return create(SimpleList.of(), src, phase);
    }

    public static Swc4jAstImportDecl create(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            Swc4jAstImportPhase phase) {
        return create(specifiers, src, false, phase);
    }

    public static Swc4jAstImportDecl create(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstImportPhase phase) {
        return create(specifiers, src, typeOnly, null, phase);
    }

    public static Swc4jAstImportDecl create(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstObjectLit with,
            Swc4jAstImportPhase phase) {
        return new Swc4jAstImportDecl(specifiers, src, typeOnly, with, phase, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(specifiers);
        childNodes.add(src);
        with.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Swc4jAstImportPhase getPhase() {
        return phase;
    }

    @Jni2RustMethod
    public List<ISwc4jAstImportSpecifier> getSpecifiers() {
        return specifiers;
    }

    @Jni2RustMethod
    public Swc4jAstStr getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ImportDecl;
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
        if (!specifiers.isEmpty() && newNode instanceof ISwc4jAstImportSpecifier) {
            final int size = specifiers.size();
            for (int i = 0; i < size; i++) {
                if (specifiers.get(i) == oldNode) {
                    specifiers.set(i, (ISwc4jAstImportSpecifier) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (src == oldNode && newNode instanceof Swc4jAstStr) {
            setSrc((Swc4jAstStr) newNode);
            return true;
        }
        if (with.isPresent() && with.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstObjectLit)) {
            setWith((Swc4jAstObjectLit) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstImportDecl setPhase(Swc4jAstImportPhase phase) {
        this.phase = AssertionUtils.notNull(phase, "Phase");
        return this;
    }

    public Swc4jAstImportDecl setSrc(Swc4jAstStr src) {
        this.src = AssertionUtils.notNull(src, "Src");
        this.src.setParent(this);
        return this;
    }

    public Swc4jAstImportDecl setTypeOnly(boolean typeOnly) {
        this.typeOnly = typeOnly;
        return this;
    }

    public Swc4jAstImportDecl setWith(Swc4jAstObjectLit with) {
        this.with = Optional.ofNullable(with);
        this.with.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitImportDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
