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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsEnumMember;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsEnumDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    @Jni2RustField(name = "is_const")
    protected final boolean _const;
    protected final boolean declare;
    protected final Swc4jAstIdent id;
    protected final List<Swc4jAstTsEnumMember> members;

    @Jni2RustMethod
    public Swc4jAstTsEnumDecl(
            boolean declare,
            @Jni2RustParam(name = "is_const") boolean _const,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members,
            Swc4jSpan span) {
        super(span);
        this._const = _const;
        this.declare = declare;
        this.id = AssertionUtils.notNull(id, "Id");
        this.members = SimpleList.immutable(AssertionUtils.notNull(members, "TypeAnn"));
        childNodes = SimpleList.copyOf(members);
        childNodes.add(id);
        childNodes = SimpleList.immutable(childNodes);
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

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsEnumDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
