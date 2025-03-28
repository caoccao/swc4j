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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstRegex extends BaseTestSuiteSwc4jAst {
    @Test
    public void testExpAndFlags() throws Swc4jCoreException {
        String code = "/abc/ig";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 7);
        Swc4jAstRegex regex = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstRegex.class), Swc4jAstType.Regex, 0, 7);
        assertEquals("abc", regex.getExp());
        assertEquals("ig", regex.getFlags());
        assertEquals(code, regex.toString());
        assertSpan(code, script);
    }

    @Test
    public void testExpOnly() throws Swc4jCoreException {
        String code = "/abc/";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstRegex regex = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstRegex.class), Swc4jAstType.Regex, 0, 5);
        assertEquals("abc", regex.getExp());
        assertEquals("", regex.getFlags());
        assertEquals(code, regex.toString());
        assertSpan(code, script);
    }
}
