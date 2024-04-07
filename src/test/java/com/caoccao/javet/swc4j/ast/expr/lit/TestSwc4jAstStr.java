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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstStr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyString() throws Swc4jCoreException {
        String code = "''";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 2);
        Swc4jAstStr str = (Swc4jAstStr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.Str, 0, 2);
        assertEquals("", str.getValue());
        assertEquals("''", str.getRaw().get());
    }

    @Test
    public void testValueAndRaw() throws Swc4jCoreException {
        String code = "'abc'";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstStr str = (Swc4jAstStr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.Str, 0, 5);
        assertEquals("abc", str.getValue());
        assertEquals("'abc'", str.getRaw().get());
    }
}
