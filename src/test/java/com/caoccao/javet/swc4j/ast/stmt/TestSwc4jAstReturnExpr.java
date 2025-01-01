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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstReturnExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testReturnBinExpr() throws Swc4jCoreException {
        String code = "return a+b";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstReturnStmt returnStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstReturnStmt.class), Swc4jAstType.ReturnStmt, 0, 10);
        assertTrue(returnStmt.getArg().isPresent());
        Swc4jAstBinExpr binExpr = assertAst(
                returnStmt, returnStmt.getArg().get().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 7, 10);
        Swc4jAstIdent ident = assertAst(
                binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 7, 8);
        assertEquals("a", ident.getSym());
        assertEquals(Swc4jAstBinaryOp.Add, binExpr.getOp());
        ident = assertAst(
                binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 9, 10);
        assertEquals("b", ident.getSym());
        assertSpan(code, script);
    }
}
