/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstNumber extends BaseTestSuiteSwc4jAst {
    @Test
    public void testCoercion() {
        assertEquals("0", Swc4jAstNumber.create(0).getRaw().get());
        assertEquals("1", Swc4jAstNumber.create(1).getRaw().get());
        assertEquals("-1", Swc4jAstNumber.create(-1).getRaw().get());
        assertEquals("-1", Swc4jAstNumber.create(-1, "-1.0").toString());
        assertEquals("1", Swc4jAstNumber.create(1, "+1.0").toString());
        assertEquals("1.1", Swc4jAstNumber.create(1.1D).toString());
        assertEquals("-1.1", Swc4jAstNumber.create(-1.1D).toString());
        assertEquals("1.1e+20", Swc4jAstNumber.create(1.1e20D).toString());
        assertEquals("1.23e+21", Swc4jAstNumber.create(12.30e20D).toString());
        assertEquals("1.234e+21", Swc4jAstNumber.create(12.340e20D, "12.340e20").toString());
        assertEquals("-1.234e+21", Swc4jAstNumber.create(12.340e20D, "-12.340e20").toString());
        assertEquals("1.234e+21", Swc4jAstNumber.create(12.340e20D, "12.34000e20").toString());
        assertEquals("Infinity", Swc4jAstNumber.create(Double.POSITIVE_INFINITY).toString());
        assertEquals("Infinity", Swc4jAstNumber.create(Double.POSITIVE_INFINITY, "1e309").toString());
        assertEquals("Infinity", Swc4jAstNumber.create(Double.POSITIVE_INFINITY, "+1e309").toString());
        assertEquals("Infinity", Swc4jAstNumber.create(Double.POSITIVE_INFINITY, "1.23e309").toString());
        assertEquals("Infinity", Swc4jAstNumber.create(Double.POSITIVE_INFINITY, "+1.23e309").toString());
        assertEquals("-Infinity", Swc4jAstNumber.create(Double.NEGATIVE_INFINITY).toString());
        assertEquals("-Infinity", Swc4jAstNumber.create(Double.NEGATIVE_INFINITY, "-1e309").toString());
        assertEquals("-Infinity", Swc4jAstNumber.create(Double.NEGATIVE_INFINITY, "-1.23e309").toString());
        assertEquals("1.1e-20", Swc4jAstNumber.create(1.1e-20D).toString());
        assertEquals("-1.1e-20", Swc4jAstNumber.create(-1.1e-20D).toString());
        assertEquals(1, Swc4jAstNumber.create(1.1D).asInt());
        assertEquals(-1, Swc4jAstNumber.create(-1.1D).asInt());
        assertEquals("NaN", Swc4jAstNumber.create(Double.NaN).toString());
        assertEquals(0, Swc4jAstNumber.create(Double.NaN).asInt());
    }

    @Test
    public void testDouble() throws Swc4jCoreException {
        String code = "12.34";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstNumber number = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstNumber.class), Swc4jAstType.Number, 0, 5);
        assertEquals(12.34D, number.getValue(), 0.0001D);
        assertEquals("12.34", number.getRaw().get());
        assertSpan(code, script);
    }

    @Test
    public void testGetMinusCount() throws Swc4jCoreException {
        Map<String, Integer> testCaseMap = SimpleMap.of(
                "12345", 0,
                "-12345", 1,
                "(-12345)", 1,
                "-(-12345)", 2,
                "-(-(-12345))", 3);
        for (Map.Entry<String, Integer> entry : testCaseMap.entrySet()) {
            String code = entry.getKey();
            int minusCount = entry.getValue();
            Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
            List<Swc4jAstNumber> nodes = output.getProgram().find(Swc4jAstNumber.class);
            assertEquals(1, nodes.size());
            Swc4jAstNumber number = nodes.get(0);
            assertEquals(12345, number.asInt());
            assertEquals("12345", number.getRaw().get());
            assertEquals(minusCount, number.getMinusCount());
        }
    }

    @Test
    public void testInt() throws Swc4jCoreException {
        String code = "12345";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 5);
        Swc4jAstNumber number = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstNumber.class), Swc4jAstType.Number, 0, 5);
        assertEquals(12345, number.asInt());
        assertEquals("12345", number.getRaw().get());
        assertSpan(code, script);
    }
}
