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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstArrayLit extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyArray() throws Swc4jCoreException {
        String code = "[]";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 2);
        Swc4jAstArrayLit arrayLit = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrayLit.class), Swc4jAstType.ArrayLit, 0, 2);
        assertTrue(arrayLit.getElems().isEmpty());
        assertSpan(code, script);
    }

    @Test
    public void testNonEmptyArray() throws Swc4jCoreException {
        String code = "[1,'a',true]";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 12);
        Swc4jAstArrayLit arrayLit = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrayLit.class), Swc4jAstType.ArrayLit, 0, 12);
        assertEquals(3, arrayLit.getElems().size());
        Swc4jAstExprOrSpread exprOrSpread = assertAst(
                arrayLit, arrayLit.getElems().get(0).get(), Swc4jAstType.ExprOrSpread, 1, 2);
        // Number
        Swc4jAstNumber number = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstNumber.class), Swc4jAstType.Number, 1, 2);
        assertEquals(1, number.getValue());
        assertTrue(number.getRaw().isPresent());
        assertEquals("1", number.getRaw().get());
        // Str
        exprOrSpread = assertAst(
                arrayLit, arrayLit.getElems().get(1).get(), Swc4jAstType.ExprOrSpread, 3, 6);
        Swc4jAstStr str = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstStr.class), Swc4jAstType.Str, 3, 6);
        assertTrue(str.getRaw().isPresent());
        assertEquals("a", str.getValue());
        assertEquals("'a'", str.getRaw().get());
        // Bool
        exprOrSpread = assertAst(
                arrayLit, arrayLit.getElems().get(2).get(), Swc4jAstType.ExprOrSpread, 7, 11);
        Swc4jAstBool b = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstBool.class), Swc4jAstType.Bool, 7, 11);
        assertTrue(b.isValue());
        assertSpan(code, script);
    }

    @Test
    public void testVisitor() {
        assertVisitor(tsScriptParseOptions, SimpleList.of(
                new VisitorCase("[]", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.ExprStmt, 1,
                        Swc4jAstType.ArrayLit, 1)),
                new VisitorCase("[1,'a',true]", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.ExprStmt, 1,
                        Swc4jAstType.ArrayLit, 1,
                        Swc4jAstType.ExprOrSpread, 3,
                        Swc4jAstType.Number, 1,
                        Swc4jAstType.Str, 1,
                        Swc4jAstType.Bool, 1))));
    }
}
