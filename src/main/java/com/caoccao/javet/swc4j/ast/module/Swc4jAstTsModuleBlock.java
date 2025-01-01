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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
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
public class Swc4jAstTsModuleBlock
        extends Swc4jAst
        implements ISwc4jAstTsNamespaceBody {
    protected final List<ISwc4jAstModuleItem> body;

    @Jni2RustMethod
    public Swc4jAstTsModuleBlock(
            List<ISwc4jAstModuleItem> body,
            Swc4jSpan span) {
        super(span);
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstTsModuleBlock create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstTsModuleBlock create(List<ISwc4jAstModuleItem> body) {
        return new Swc4jAstTsModuleBlock(body, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public List<ISwc4jAstModuleItem> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(body);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsModuleBlock;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!body.isEmpty() && newNode instanceof ISwc4jAstModuleItem) {
            final int size = body.size();
            for (int i = 0; i < size; i++) {
                if (body.get(i) == oldNode) {
                    body.set(i, (ISwc4jAstModuleItem) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsModuleBlock(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
