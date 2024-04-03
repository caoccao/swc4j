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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstAssignPatProp
        extends Swc4jAst
        implements ISwc4jAstObjectPatProp {
    protected final Swc4jAstIdent key;
    protected final Optional<ISwc4jAstExpr> value;

    public Swc4jAstAssignPatProp(
            Swc4jAstIdent key,
            ISwc4jAstExpr value,
            Swc4jAstSpan span) {
        super(span);
        this.key = AssertionUtils.notNull(key, "Key");
        this.value = Optional.ofNullable(value);
        children = SimpleList.immutableOf(key, value);
        updateParent();
    }

    public Swc4jAstIdent getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.AssignPatProp;
    }

    public Optional<ISwc4jAstExpr> getValue() {
        return value;
    }
}
