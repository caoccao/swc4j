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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsModuleDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected final Optional<ISwc4jAstTsNamespaceBody> body;
    protected final boolean declare;
    protected final boolean global;
    @Jni2RustField(value = "TsModuleName::Ident(Ident::dummy())")
    protected final ISwc4jAstTsModuleName id;

    public Swc4jAstTsModuleDecl(
            boolean declare,
            boolean global,
            ISwc4jAstTsModuleName id,
            ISwc4jAstTsNamespaceBody body,
            Swc4jAstSpan span) {
        super(span);
        this.declare = declare;
        this.global = global;
        this.body = Optional.ofNullable(body);
        this.id = AssertionUtils.notNull(id, "Id");
        childNodes = SimpleList.immutableOf(id, body);
        updateParent();
    }

    public Optional<ISwc4jAstTsNamespaceBody> getBody() {
        return body;
    }

    public ISwc4jAstTsModuleName getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsModuleDecl;
    }

    public boolean isDeclare() {
        return declare;
    }

    public boolean isGlobal() {
        return global;
    }
}
