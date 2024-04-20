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

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxElement;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstJsxOpening extends BaseTestSuiteSwc4jAst {
    @Test
    public void testNoSelfClosing() throws Swc4jCoreException {
        String code = "<h1>test</h1>";
        Swc4jParseOutput output = swc4j.parse(code, jsxScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 13);
        Swc4jAstJsxElement jsxElement = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstJsxElement.class), Swc4jAstType.JsxElement, 0, 13);
        Swc4jAstJsxOpeningElement jsxOpeningElement = assertAst(
                jsxElement, jsxElement.getOpening(), Swc4jAstType.JsxOpeningElement, 0, 4);
        assertFalse(jsxOpeningElement.isSelfClosing());
        Swc4jAstIdent ident = assertAst(
                jsxOpeningElement, jsxOpeningElement.getName().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1, 3);
        assertEquals("h1", ident.getSym());
        assertFalse(ident.isOptional());
        assertSpan(code, script);
    }

    @Test
    public void testSelfClosing() throws Swc4jCoreException {
        String code = "<h1/>";
        Swc4jParseOutput output = swc4j.parse(code, jsxScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstJsxElement jsxElement = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstJsxElement.class), Swc4jAstType.JsxElement, 0, 5);
        Swc4jAstJsxOpeningElement jsxOpeningElement = assertAst(
                jsxElement, jsxElement.getOpening(), Swc4jAstType.JsxOpeningElement, 0, 5);
        assertTrue(jsxOpeningElement.isSelfClosing());
        assertSpan(code, script);
    }
}
