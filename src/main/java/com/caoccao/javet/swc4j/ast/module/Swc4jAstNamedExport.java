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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExportSpecifier;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleDecl;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstNamedExport
        extends Swc4jAst
        implements ISwc4jAstModuleDecl {
    protected final List<ISwc4jAstExportSpecifier> specifiers;
    protected final Swc4jAstStr src;
    protected final boolean typeOnly;
    @Nullable
    protected final Swc4jAstObjectLit with;

    public Swc4jAstNamedExport(
            List<ISwc4jAstExportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            Swc4jAstObjectLit with,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.specifiers = AssertionUtils.notNull(specifiers, "Specifiers");
        this.src = src;
        this.typeOnly = typeOnly;
        this.with = with;
        children = SimpleList.copyOf(specifiers);
        children.add(src);
        children.add(with);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public List<ISwc4jAstExportSpecifier> getSpecifiers() {
        return specifiers;
    }

    public Swc4jAstStr getSrc() {
        return src;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.NamedExport;
    }

    public Swc4jAstObjectLit getWith() {
        return with;
    }

    public boolean isTypeOnly() {
        return typeOnly;
    }
}
