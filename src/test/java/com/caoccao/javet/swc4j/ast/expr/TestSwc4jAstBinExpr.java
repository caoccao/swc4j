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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestSwc4jAstBinExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testOpWithSpace() {
        SimpleList.of(Swc4jAstBinaryOp.In, Swc4jAstBinaryOp.InstanceOf).forEach(op -> {
            try {
                String code = "a " + op.getName() + " b";
                Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
                Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
                Swc4jAstExprStmt exprStmt = assertAst(
                        script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 4 + op.getName().length());
                Swc4jAstBinExpr binExpr = assertAst(
                        exprStmt, exprStmt.getExpr().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 0, 4 + op.getName().length());
                Swc4jAstIdent ident = assertAst(
                        binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
                assertEquals("a", ident.getSym());
                assertEquals(op, binExpr.getOp());
                ident = assertAst(
                        binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 3 + op.getName().length(), 4 + op.getName().length());
                assertEquals("b", ident.getSym());
                assertSpan(code, script);
            } catch (Throwable e) {
                fail(e);
            }
        });
    }

    @Test
    public void testOpWithoutSpace() {
        SimpleList.of(
                Swc4jAstBinaryOp.Add,
                Swc4jAstBinaryOp.BitAnd,
                Swc4jAstBinaryOp.BitOr,
                Swc4jAstBinaryOp.BitXor,
                Swc4jAstBinaryOp.Div,
                Swc4jAstBinaryOp.EqEq,
                Swc4jAstBinaryOp.EqEqEq,
                Swc4jAstBinaryOp.Exp,
                Swc4jAstBinaryOp.Gt,
                Swc4jAstBinaryOp.GtEq,
                Swc4jAstBinaryOp.LogicalAnd,
                Swc4jAstBinaryOp.LogicalOr,
                Swc4jAstBinaryOp.LShift,
                Swc4jAstBinaryOp.Lt,
                Swc4jAstBinaryOp.LtEq,
                Swc4jAstBinaryOp.Mod,
                Swc4jAstBinaryOp.Mul,
                Swc4jAstBinaryOp.NotEq,
                Swc4jAstBinaryOp.NotEqEq,
                Swc4jAstBinaryOp.NullishCoalescing,
                Swc4jAstBinaryOp.RShift,
                Swc4jAstBinaryOp.Sub,
                Swc4jAstBinaryOp.ZeroFillRShift
        ).forEach(op -> {
            try {
                String code = "a" + op.getName() + "b";
                Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
                Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
                Swc4jAstExprStmt exprStmt = assertAst(
                        script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 2 + op.getName().length());
                Swc4jAstBinExpr binExpr = assertAst(
                        exprStmt, exprStmt.getExpr().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 0, 2 + op.getName().length());
                Swc4jAstIdent ident = assertAst(
                        binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
                assertEquals("a", ident.getSym());
                assertEquals(op, binExpr.getOp());
                ident = assertAst(
                        binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1 + op.getName().length(), 2 + op.getName().length());
                assertEquals("b", ident.getSym());
                assertSpan(code, script);
            } catch (Throwable e) {
                fail(e);
            }
        });
    }
}
