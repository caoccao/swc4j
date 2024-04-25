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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsNamespaceDecl
        extends Swc4jAst
        implements ISwc4jAstTsNamespaceBody {
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsNamespaceBody body;
    protected final boolean declare;
    protected final boolean global;
    protected final Swc4jAstIdent id;

    @Jni2RustMethod
    public Swc4jAstTsNamespaceDecl(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body,
            Swc4jSpan span) {
        super(span);
        this.declare = declare;
        this.global = global;
        this.body = AssertionUtils.notNull(body, "Body");
        this.id = AssertionUtils.notNull(id, "Id");
        childNodes = SimpleList.immutableOf(id, body);
        updateParent();
    }

    public ISwc4jAstTsNamespaceBody getBody() {
        return body;
    }

    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsNamespaceDecl;
    }

    public boolean isDeclare() {
        return declare;
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsNamespaceDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
