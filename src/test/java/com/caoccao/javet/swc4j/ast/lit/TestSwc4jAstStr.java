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

package com.caoccao.javet.swc4j.ast.lit;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstStr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testValueAndRaw() throws Swc4jCoreException {
        String code = "'abc'";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) script.getBody().get(0);
        assertEquals(script, exprStmt.getParent());
        assertEquals(0, exprStmt.getStartPosition());
        assertEquals(5, exprStmt.getEndPosition());
        Swc4jAstStr str = (Swc4jAstStr) exprStmt.getExpr();
        assertEquals(exprStmt, str.getParent());
        assertEquals(Swc4jAstType.Str, str.getType());
        assertEquals("abc", str.getValue());
        assertEquals("'abc'", str.getRaw());
        assertEquals(0, str.getStartPosition());
        assertEquals(5, str.getEndPosition());
    }
}
