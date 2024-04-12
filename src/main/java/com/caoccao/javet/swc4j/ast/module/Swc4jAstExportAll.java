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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

import java.util.Optional;

public class Swc4jAstExportAll
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    @Jni2RustField(box = true)
    protected final Swc4jAstStr src;
    protected final boolean typeOnly;
    protected final Optional<Swc4jAstObjectLit> with;

    public Swc4jAstExportAll(
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstObjectLit with,
            Swc4jSpan span) {
        super(span);
        this.src = AssertionUtils.notNull(src, "Src");
        this.typeOnly = typeOnly;
        this.with = Optional.ofNullable(with);
        childNodes = SimpleList.immutableOf(src, with);
        updateParent();
    }

    public Swc4jAstStr getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExportAll;
    }

    public Optional<Swc4jAstObjectLit> getWith() {
        return with;
    }

    public boolean isTypeOnly() {
        return typeOnly;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitExportAll(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
