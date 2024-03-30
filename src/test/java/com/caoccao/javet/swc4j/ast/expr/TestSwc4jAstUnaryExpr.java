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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstUnaryExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testBangAndMinusAndPlusAndTilde() throws Swc4jCoreException {
        List<Swc4jAstUnaryOp> opList = SimpleList.of(
                Swc4jAstUnaryOp.Bang, Swc4jAstUnaryOp.Minus, Swc4jAstUnaryOp.Plus, Swc4jAstUnaryOp.Tilde);
        for (Swc4jAstUnaryOp op : opList) {
            String code = op.getValue() + "a";
            Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
            Swc4jAstScript script = output.getProgram().asScript();
            Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                    script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, code.length());
            Swc4jAstUnaryExpr unaryExpr = (Swc4jAstUnaryExpr) assertAst(
                    exprStmt, exprStmt.getExpr(), Swc4jAstType.UnaryExpr, 0, code.length());
            assertEquals(op, unaryExpr.getOp());
            Swc4jAstIdent ident = (Swc4jAstIdent) assertAst(
                    unaryExpr, unaryExpr.getArg(), Swc4jAstType.Ident, code.length() - 1, code.length());
            assertEquals("a", ident.getSym());
        }
    }

    @Test
    public void testDeleteAndTypeOfAndVoid() throws Swc4jCoreException {
        List<Swc4jAstUnaryOp> opList = SimpleList.of(
                Swc4jAstUnaryOp.Delete, Swc4jAstUnaryOp.TypeOf, Swc4jAstUnaryOp.Void);
        for (Swc4jAstUnaryOp op : opList) {
            String code = op.getValue() + " a";
            Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
            Swc4jAstScript script = output.getProgram().asScript();
            Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                    script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, code.length());
            Swc4jAstUnaryExpr unaryExpr = (Swc4jAstUnaryExpr) assertAst(
                    exprStmt, exprStmt.getExpr(), Swc4jAstType.UnaryExpr, 0, code.length());
            assertEquals(op, unaryExpr.getOp());
            Swc4jAstIdent ident = (Swc4jAstIdent) assertAst(
                    unaryExpr, unaryExpr.getArg(), Swc4jAstType.Ident, code.length() - 1, code.length());
            assertEquals("a", ident.getSym());
        }
    }
}
