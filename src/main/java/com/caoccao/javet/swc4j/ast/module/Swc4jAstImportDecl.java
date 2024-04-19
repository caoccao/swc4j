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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstImportPhase;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstImportSpecifier;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.span.Swc4jSpan;

import java.util.List;
import java.util.Optional;

public class Swc4jAstImportDecl
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    protected final Swc4jAstImportPhase phase;
    protected final List<ISwc4jAstImportSpecifier> specifiers;
    @Jni2RustField(box = true)
    protected final Swc4jAstStr src;
    protected final boolean typeOnly;
    protected final Optional<Swc4jAstObjectLit> with;

    public Swc4jAstImportDecl(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstObjectLit with,
            Swc4jAstImportPhase phase,
            Swc4jSpan span) {
        super(span);
        this.specifiers = AssertionUtils.notNull(specifiers, "Specifiers");
        this.src = AssertionUtils.notNull(src, "Src");
        this.typeOnly = typeOnly;
        this.with = Optional.ofNullable(with);
        this.phase = AssertionUtils.notNull(phase, "Phase");
        childNodes = SimpleList.copyOf(specifiers);
        childNodes.add(src);
        childNodes.add(with);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public Swc4jAstImportPhase getPhase() {
        return phase;
    }

    public List<ISwc4jAstImportSpecifier> getSpecifiers() {
        return specifiers;
    }

    public Swc4jAstStr getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ImportDecl;
    }

    public Optional<Swc4jAstObjectLit> getWith() {
        return with;
    }

    public boolean isTypeOnly() {
        return typeOnly;
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
