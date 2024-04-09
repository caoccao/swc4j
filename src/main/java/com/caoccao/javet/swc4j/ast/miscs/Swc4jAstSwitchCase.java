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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstSwitchCase
        extends Swc4jAst {
    protected final List<ISwc4jAstStmt> cons;
    protected final Optional<ISwc4jAstExpr> test;

    public Swc4jAstSwitchCase(
            ISwc4jAstExpr test,
            List<ISwc4jAstStmt> cons,
            Swc4jAstSpan span) {
        super(span);
        this.test = Optional.ofNullable(test);
        this.cons = SimpleList.immutableCopyOf(AssertionUtils.notNull(cons, "Cons"));
        childNodes = SimpleList.copyOf(cons);
        childNodes.add(test);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public List<ISwc4jAstStmt> getCons() {
        return cons;
    }

    public Optional<ISwc4jAstExpr> getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SwitchCase;
    }
}
