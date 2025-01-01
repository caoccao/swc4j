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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
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
public class Swc4jAstStaticBlock
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected Swc4jAstBlockStmt body;

    @Jni2RustMethod
    public Swc4jAstStaticBlock(
            Swc4jAstBlockStmt body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
    }

    public static Swc4jAstStaticBlock create(Swc4jAstBlockStmt body) {
        return new Swc4jAstStaticBlock(body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public Swc4jAstBlockStmt getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(body);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.StaticBlock;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof Swc4jAstBlockStmt) {
            setBody((Swc4jAstBlockStmt) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstStaticBlock setBody(Swc4jAstBlockStmt body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitStaticBlock(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
