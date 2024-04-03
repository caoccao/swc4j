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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUpdateOp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstUpdateExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final ISwc4jAstExpr arg;
    protected final Swc4jAstUpdateOp op;
    protected final boolean prefix;

    public Swc4jAstUpdateExpr(
            Swc4jAstUpdateOp op,
            boolean prefix,
            ISwc4jAstExpr arg,
            Swc4jAstSpan span) {
        super(span);
        this.arg = AssertionUtils.notNull(arg, "Arg");
        this.op = AssertionUtils.notNull(op, "Op");
        this.prefix = prefix;
        children = SimpleList.immutableOf(arg);
        updateParent();
    }

    public ISwc4jAstExpr getArg() {
        return arg;
    }

    public Swc4jAstUpdateOp getOp() {
        return op;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.UpdateExpr;
    }

    public boolean isPrefix() {
        return prefix;
    }
}
