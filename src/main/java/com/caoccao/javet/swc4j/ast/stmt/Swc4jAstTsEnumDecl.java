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
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsEnumMember;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstTsEnumDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    @Jni2RustField(name = "is_const")
    protected final boolean _const;
    protected final boolean declare;
    protected final Swc4jAstIdent id;
    protected final List<Swc4jAstTsEnumMember> members;

    public Swc4jAstTsEnumDecl(
            boolean declare,
            boolean _const,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members,
            Swc4jAstSpan span) {
        super(span);
        this._const = _const;
        this.declare = declare;
        this.id = AssertionUtils.notNull(id, "Id");
        this.members = SimpleList.immutableCopyOf(AssertionUtils.notNull(members, "TypeAnn"));
        children = SimpleList.copyOf(members);
        children.add(id);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public Swc4jAstIdent getId() {
        return id;
    }

    public List<Swc4jAstTsEnumMember> getMembers() {
        return members;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsEnumDecl;
    }

    public boolean isConst() {
        return _const;
    }

    public boolean isDeclare() {
        return declare;
    }
}
