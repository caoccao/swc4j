/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstExprOrSpread
        extends Swc4jAst {
    protected final ISwc4jAstExpr expr;
    @Nullable
    protected final int spreadEndPosition;
    @Nullable
    protected final int spreadStartPosition;

    public Swc4jAstExprOrSpread(
            int spreadStartPosition,
            int spreadEndPosition,
            ISwc4jAstExpr expr,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.spreadEndPosition = spreadEndPosition;
        this.spreadStartPosition = spreadStartPosition;
        this.expr = AssertionUtils.notNull(expr, "Expr");
        children = SimpleList.immutableOf(expr);
        updateParent();
    }

    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    public int getSpreadEndPosition() {
        return spreadEndPosition;
    }

    public int getSpreadStartPosition() {
        return spreadStartPosition;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ExprOrSpread;
    }
}
