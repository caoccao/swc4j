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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstVarDecl
        extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstVarDeclOrExpr, ISwc4jAstForHead {
    protected final boolean declare;
    protected final List<Swc4jAstVarDeclarator> decls;
    @Jni2RustField(value = "VarDeclKind::Const")
    protected final Swc4jAstVarDeclKind kind;

    public Swc4jAstVarDecl(
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            Swc4jAstSpan span) {
        super(span);
        this.declare = declare;
        this.decls = SimpleList.immutableCopyOf(AssertionUtils.notNull(decls, "Decls"));
        this.kind = AssertionUtils.notNull(kind, "Kind");
        childNodes = SimpleList.immutableCopyOf(decls);
        updateParent();
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
}
