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
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstBlockStmt
        extends Swc4jAst
        implements ISwc4jAstStmt, ISwc4jAstBlockStmtOrExpr {
    protected final List<ISwc4jAstStmt> stmts;

    public Swc4jAstBlockStmt(
            List<ISwc4jAstStmt> stmts,
            Swc4jAstSpan span) {
        super(span);
        this.stmts = SimpleList.immutableCopyOf(AssertionUtils.notNull(stmts, "Stmts"));
        childNodes = SimpleList.immutableCopyOf(stmts);
        updateParent();
    }

    public List<ISwc4jAstStmt> getStmts() {
        return stmts;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BlockStmt;
    }
}
