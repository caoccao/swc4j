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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEnumMemberId;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsEnumMember
        extends Swc4jAst {
    @Jni2RustField(value = "TsEnumMemberId::Ident(Ident::dummy())")
    protected final ISwc4jAstTsEnumMemberId id;
    protected final Optional<ISwc4jAstExpr> init;

    public Swc4jAstTsEnumMember(
            ISwc4jAstTsEnumMemberId id,
            ISwc4jAstExpr init,
            Swc4jAstSpan span) {
        super(span);
        this.id = AssertionUtils.notNull(id, "Id");
        this.init = Optional.ofNullable(init);
        childNodes = SimpleList.immutableOf(id, init);
        updateParent();
    }

    public ISwc4jAstTsEnumMemberId getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsEnumMember;
    }
}
