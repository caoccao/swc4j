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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstMetaPropKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

public class Swc4jAstMetaPropExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(value = "MetaPropKind::NewTarget")
    protected final Swc4jAstMetaPropKind kind;

    public Swc4jAstMetaPropExpr(
            Swc4jAstMetaPropKind kind,
            Swc4jAstSpan span) {
        super(span);
        this.kind = AssertionUtils.notNull(kind, "Kind");
        children = EMPTY_CHILDREN;
    }

    public Swc4jAstMetaPropKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.MetaPropExpr;
    }
}