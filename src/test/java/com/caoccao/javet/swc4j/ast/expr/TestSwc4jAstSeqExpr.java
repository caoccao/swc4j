/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstSeqExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void test() throws Swc4jCoreException {
        String code = "() => { return a,b,c; }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 23);
        Swc4jAstArrowExpr arrowExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrowExpr.class), Swc4jAstType.ArrowExpr, 0, 23);
        Swc4jAstBlockStmt blockStmt = assertAst(
                arrowExpr, arrowExpr.getBody().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 6, 23);
        assertEquals(1, blockStmt.getStmts().size());
        Swc4jAstReturnStmt returnStmt = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstReturnStmt.class), Swc4jAstType.ReturnStmt, 8, 21);
        assertTrue(returnStmt.getArg().isPresent());
        Swc4jAstSeqExpr seqExpr = assertAst(
                returnStmt, returnStmt.getArg().get().as(Swc4jAstSeqExpr.class), Swc4jAstType.SeqExpr, 15, 20);
        assertEquals(3, seqExpr.getExprs().size());
        assertEquals("a", seqExpr.getExprs().get(0).as(Swc4jAstIdent.class).getSym());
        assertEquals("b", seqExpr.getExprs().get(1).as(Swc4jAstIdent.class).getSym());
        assertEquals("c", seqExpr.getExprs().get(2).as(Swc4jAstIdent.class).getSym());
        assertSpan(code, script);
    }
}
