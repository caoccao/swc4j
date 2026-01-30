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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstBigInt extends BaseTestSuiteSwc4jAst {
    @Test
    public void testExtraLongNumber() throws Swc4jCoreException {
        String code = "1234567890123456789012345678901234567890n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 41);
        Swc4jAstBigInt bigInt = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstBigInt.class), Swc4jAstType.BigInt, 0, 41);
        assertThat(bigInt.getRaw().get()).isEqualTo("1234567890123456789012345678901234567890n");
        assertThat(bigInt.getValue().toString()).isEqualTo("1234567890123456789012345678901234567890");
        assertThat(bigInt.getSign()).isEqualTo(Swc4jAstBigIntSign.Plus);
        assertSpan(code, script);
    }

    @Test
    public void testSignMinus() throws Swc4jCoreException {
        String code = "-1n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstUnaryExpr unaryExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstUnaryExpr.class), Swc4jAstType.UnaryExpr, 0, 3);
        assertThat(unaryExpr.getOp()).isEqualTo(Swc4jAstUnaryOp.Minus);
        Swc4jAstBigInt bigInt = assertAst(
                unaryExpr, unaryExpr.getArg().as(Swc4jAstBigInt.class), Swc4jAstType.BigInt, 1, 3);
        assertThat(bigInt.getRaw().get()).isEqualTo("1n");
        assertThat(bigInt.getValue()).isEqualTo(BigInteger.ONE);
        assertThat(bigInt.getSign()).isEqualTo(Swc4jAstBigIntSign.Plus);
        assertSpan(code, script);
    }

    @Test
    public void testSignPlus() throws Swc4jCoreException {
        String code = "+1n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstUnaryExpr unaryExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstUnaryExpr.class), Swc4jAstType.UnaryExpr, 0, 3);
        assertThat(unaryExpr.getOp()).isEqualTo(Swc4jAstUnaryOp.Plus);
        Swc4jAstBigInt bigInt = assertAst(
                unaryExpr, unaryExpr.getArg().as(Swc4jAstBigInt.class), Swc4jAstType.BigInt, 1, 3);
        assertThat(bigInt.getRaw().get()).isEqualTo("1n");
        assertThat(bigInt.getValue()).isEqualTo(BigInteger.ONE);
        assertThat(bigInt.getSign()).isEqualTo(Swc4jAstBigIntSign.Plus);
        assertSpan(code, script);
    }

    @Test
    public void testVisitor() {
        assertVisitor(tsScriptParseOptions, SimpleList.of(
                new VisitorCase("0n", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.ExprStmt, 1,
                        Swc4jAstType.BigInt, 1)),
                new VisitorCase("-1n", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.ExprStmt, 1,
                        Swc4jAstType.UnaryExpr, 1,
                        Swc4jAstType.BigInt, 1)),
                new VisitorCase("+1n", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.ExprStmt, 1,
                        Swc4jAstType.UnaryExpr, 1,
                        Swc4jAstType.BigInt, 1))));
    }

    @Test
    public void testZero() throws Swc4jCoreException {
        String code = "0n";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 2);
        Swc4jAstBigInt bigInt = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstBigInt.class), Swc4jAstType.BigInt, 0, 2);
        assertThat(bigInt.getRaw().isPresent()).isTrue();
        assertThat(bigInt.getRaw().get()).isEqualTo("0n");
        assertThat(bigInt.getValue()).isEqualTo(BigInteger.ZERO);
        assertThat(bigInt.getSign()).isEqualTo(Swc4jAstBigIntSign.NoSign);
        assertSpan(code, script);
    }
}
