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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstCondExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final ISwc4jAstExpr alt;
    protected final ISwc4jAstExpr cons;
    protected final ISwc4jAstExpr test;

    public Swc4jAstCondExpr(
            ISwc4jAstExpr test,
            ISwc4jAstExpr cons,
            ISwc4jAstExpr alt,
            Swc4jAstSpan span) {
        super(span);
        this.alt = AssertionUtils.notNull(alt, "Alt");
        this.cons = AssertionUtils.notNull(cons, "Cons");
        this.test = AssertionUtils.notNull(test, "Test");
        children = SimpleList.immutableOf(alt, cons, test);
        updateParent();
    }

    public ISwc4jAstExpr getAlt() {
        return alt;
    }

    public ISwc4jAstExpr getCons() {
        return cons;
    }

    public ISwc4jAstExpr getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.CondExpr;
    }
}