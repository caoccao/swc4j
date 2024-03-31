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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropOrSpread;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstSpreadElement
        extends Swc4jAst
        implements ISwc4jAstPropOrSpread {
    protected final int dot3TokenEndPosition;
    protected final int dot3TokenStartPosition;
    protected final ISwc4jAstExpr expr;

    public Swc4jAstSpreadElement(
            int dot3TokenStartPosition,
            int dot3TokenEndPosition,
            ISwc4jAstExpr expr,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.dot3TokenEndPosition = dot3TokenEndPosition;
        this.dot3TokenStartPosition = dot3TokenStartPosition;
        this.expr = AssertionUtils.notNull(expr, "Expr");
        children = SimpleList.immutableOf(expr);
        updateParent();
    }

    public int getDot3TokenEndPosition() {
        return dot3TokenEndPosition;
    }

    public int getDot3TokenStartPosition() {
        return dot3TokenStartPosition;
    }

    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SpreadElement;
    }
}
