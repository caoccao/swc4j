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
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstNewExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testNewWithParenthesis() throws Swc4jCoreException {
        String code = "new A()";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 7);
        Swc4jAstNewExpr newExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstNewExpr.class), Swc4jAstType.NewExpr, 0, 7);
        assertTrue(newExpr.getArgs().isPresent());
        assertTrue(newExpr.getArgs().get().isEmpty());
        assertFalse(newExpr.getTypeArgs().isPresent());
        Swc4jAstIdent ident = assertAst(
                newExpr, newExpr.getCallee().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertEquals("A", ident.getSym());
        assertSpan(code, script);
    }

    @Test
    public void testNewWithoutParenthesis() throws Swc4jCoreException {
        String code = "new A";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstNewExpr newExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstNewExpr.class), Swc4jAstType.NewExpr, 0, 5);
        assertFalse(newExpr.getArgs().isPresent());
        assertFalse(newExpr.getTypeArgs().isPresent());
        Swc4jAstIdent ident = assertAst(
                newExpr, newExpr.getCallee().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertEquals("A", ident.getSym());
        assertSpan(code, script);
    }
}
