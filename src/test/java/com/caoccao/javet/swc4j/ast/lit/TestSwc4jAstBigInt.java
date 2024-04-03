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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstBigInt extends BaseTestSuiteSwc4jAst {
    @Test
    public void testExtraLongNumber() throws Swc4jCoreException {
        String code = "1234567890123456789012345678901234567890n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 41);
        Swc4jAstBigInt bigInt = (Swc4jAstBigInt) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.BigInt, 0, 41);
        assertEquals("1234567890123456789012345678901234567890n", bigInt.getRaw().get());
        assertEquals("1234567890123456789012345678901234567890", bigInt.getValue().toString());
        assertEquals(Swc4jAstBigIntSign.Plus, bigInt.getSign());
    }

    @Test
    public void testSignMinus() throws Swc4jCoreException {
        String code = "-1n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstUnaryExpr unaryExpr = (Swc4jAstUnaryExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.UnaryExpr, 0, 3);
        assertEquals(Swc4jAstUnaryOp.Minus, unaryExpr.getOp());
        Swc4jAstBigInt bigInt = (Swc4jAstBigInt) assertAst(
                unaryExpr, unaryExpr.getArg(), Swc4jAstType.BigInt, 1, 3);
        assertEquals("1n", bigInt.getRaw().get());
        assertEquals(BigInteger.ONE, bigInt.getValue());
        assertEquals(Swc4jAstBigIntSign.Plus, bigInt.getSign());
    }

    @Test
    public void testSignPlus() throws Swc4jCoreException {
        String code = "+1n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstUnaryExpr unaryExpr = (Swc4jAstUnaryExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.UnaryExpr, 0, 3);
        assertEquals(Swc4jAstUnaryOp.Plus, unaryExpr.getOp());
        Swc4jAstBigInt bigInt = (Swc4jAstBigInt) assertAst(
                unaryExpr, unaryExpr.getArg(), Swc4jAstType.BigInt, 1, 3);
        assertEquals("1n", bigInt.getRaw().get());
        assertEquals(BigInteger.ONE, bigInt.getValue());
        assertEquals(Swc4jAstBigIntSign.Plus, bigInt.getSign());
    }

    @Test
    public void testZero() throws Swc4jCoreException {
        String code = "0n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 2);
        Swc4jAstBigInt bigInt = (Swc4jAstBigInt) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.BigInt, 0, 2);
        assertEquals("0n", bigInt.getRaw().get());
        assertEquals(BigInteger.ZERO, bigInt.getValue());
        assertEquals(Swc4jAstBigIntSign.NoSign, bigInt.getSign());
    }
}
