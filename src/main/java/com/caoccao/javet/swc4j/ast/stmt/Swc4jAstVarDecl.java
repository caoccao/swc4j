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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstVarDecl
        extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstVarDeclOrExpr, ISwc4jAstForHead {
    protected final boolean declare;
    protected final List<Swc4jAstVarDeclarator> decls;
    protected final Swc4jAstVarDeclKind kind;

    @Jni2RustMethod
    public Swc4jAstVarDecl(
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            Swc4jSpan span) {
        super(span);
        this.declare = declare;
        this.decls = AssertionUtils.notNull(decls, "Decls");
        this.kind = AssertionUtils.notNull(kind, "Kind");
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(decls);
    }

    public List<Swc4jAstVarDeclarator> getDecls() {
        return decls;
    }

    public Swc4jAstVarDeclKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.VarDecl;
    }

    public boolean isDeclare() {
        return declare;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitVarDecl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
