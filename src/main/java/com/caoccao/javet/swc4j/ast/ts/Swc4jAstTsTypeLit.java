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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsTypeLit
        extends Swc4jAst
        implements ISwc4jAstTsType {
    protected final List<ISwc4jAstTsTypeElement> members;

    @Jni2RustMethod
    public Swc4jAstTsTypeLit(
            List<ISwc4jAstTsTypeElement> members,
            Swc4jSpan span) {
        super(span);
        this.members = AssertionUtils.notNull(members, "Members");
        this.members.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsTypeLit create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstTsTypeLit create(List<ISwc4jAstTsTypeElement> members) {
        return new Swc4jAstTsTypeLit(members, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(members);
    }

    @Jni2RustMethod
    public List<ISwc4jAstTsTypeElement> getMembers() {
        return members;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeLit;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!members.isEmpty() && newNode instanceof ISwc4jAstTsTypeElement) {
            final int size = members.size();
            for (int i = 0; i < size; i++) {
                if (members.get(i) == oldNode) {
                    members.set(i, (ISwc4jAstTsTypeElement) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTypeLit(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
