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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstBinExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final ISwc4jAstExpr left;
    protected final Swc4jAstBinaryOp op;
    protected final ISwc4jAstExpr right;

    public Swc4jAstBinExpr(
            Swc4jAstBinaryOp op,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            Swc4jAstSpan span) {
        super(span);
        this.left = AssertionUtils.notNull(left, "Left");
        this.op = AssertionUtils.notNull(op, "Op");
        this.right = AssertionUtils.notNull(right, "Right");
        children = SimpleList.immutableOf(left, right);
        updateParent();
    }

    public ISwc4jAstExpr getLeft() {
        return left;
    }

    public Swc4jAstBinaryOp getOp() {
        return op;
    }

    public ISwc4jAstExpr getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BinExpr;
    }
}