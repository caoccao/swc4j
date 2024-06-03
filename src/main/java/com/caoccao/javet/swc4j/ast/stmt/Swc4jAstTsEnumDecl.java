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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsEnumMember;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsEnumDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected final List<Swc4jAstTsEnumMember> members;
    @Jni2RustField(name = "is_const")
    protected boolean _const;
    protected boolean declare;
    protected Swc4jAstIdent id;

    @Jni2RustMethod
    public Swc4jAstTsEnumDecl(
            boolean declare,
            @Jni2RustParam(name = "is_const") boolean _const,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members,
            Swc4jSpan span) {
        super(span);
        setConst(_const);
        setDeclare(declare);
        setId(id);
        this.members = AssertionUtils.notNull(members, "Type ann");
        this.members.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsEnumDecl create(Swc4jAstIdent id) {
        return create(id, SimpleList.of());
    }

    public static Swc4jAstTsEnumDecl create(Swc4jAstIdent id, List<Swc4jAstTsEnumMember> members) {
        return create(false, id, members);
    }

    public static Swc4jAstTsEnumDecl create(
            boolean declare,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members) {
        return create(declare, false, id, members);
    }

    public static Swc4jAstTsEnumDecl create(
            boolean declare,
            boolean _const,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members) {
        return new Swc4jAstTsEnumDecl(declare, _const, id, members, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(members);
        childNodes.add(id);
        return childNodes;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getId() {
        return id;
    }

    @Jni2RustMethod
    public List<Swc4jAstTsEnumMember> getMembers() {
        return members;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsEnumDecl;
    }

    @Jni2RustMethod
    public boolean isConst() {
        return _const;
    }

    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (id == oldNode && newNode instanceof Swc4jAstIdent) {
            setId((Swc4jAstIdent) newNode);
            return true;
        }
        if (!members.isEmpty() && newNode instanceof Swc4jAstTsEnumMember) {
            final int size = members.size();
            for (int i = 0; i < size; i++) {
                if (members.get(i) == oldNode) {
                    members.set(i, (Swc4jAstTsEnumMember) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    public Swc4jAstTsEnumDecl setConst(boolean _const) {
        this._const = _const;
        return this;
    }

    public Swc4jAstTsEnumDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstTsEnumDecl setId(Swc4jAstIdent id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
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
