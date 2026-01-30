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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstIfStmt extends BaseTestSuiteSwc4jAst {
    @Test
    public void testNoAltWithParenthesis() throws Swc4jCoreException {
        String code = "if (a) { b; }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstIfStmt ifStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstIfStmt.class), Swc4jAstType.IfStmt, 0, 13);
        Swc4jAstIdent ident = assertAst(
                ifStmt, ifStmt.getTest().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstBlockStmt blockStmt = assertAst(
                ifStmt, ifStmt.getCons().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 7, 13);
        Swc4jAstExprStmt exprStmt = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 9, 11);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("b");
        assertThat(ifStmt.getAlt().isPresent()).isFalse();
        assertSpan(code, script);
    }

    @Test
    public void testNoAltWithoutParenthesis() throws Swc4jCoreException {
        String code = "if (a) b;";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstIfStmt ifStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstIfStmt.class), Swc4jAstType.IfStmt, 0, 9);
        Swc4jAstIdent ident = assertAst(
                ifStmt, ifStmt.getTest().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstExprStmt exprStmt = assertAst(
                ifStmt, ifStmt.getCons().as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 7, 9);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 7, 8);
        assertThat(ident.getSym()).isEqualTo("b");
        assertThat(ifStmt.getAlt().isPresent()).isFalse();
        assertSpan(code, script);
    }

    @Test
    public void testWithParenthesis() throws Swc4jCoreException {
        String code = "if (a) { b; } else { c; }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstIfStmt ifStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstIfStmt.class), Swc4jAstType.IfStmt, 0, 25);
        Swc4jAstIdent ident = assertAst(
                ifStmt, ifStmt.getTest().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstBlockStmt blockStmt = assertAst(
                ifStmt, ifStmt.getCons().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 7, 13);
        Swc4jAstExprStmt exprStmt = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 9, 11);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("b");
        assertThat(ifStmt.getAlt().isPresent()).isTrue();
        blockStmt = assertAst(
                ifStmt, ifStmt.getAlt().get().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 19, 25);
        exprStmt = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 21, 23);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 21, 22);
        assertThat(ident.getSym()).isEqualTo("c");
        assertSpan(code, script);
    }

    @Test
    public void testWithoutParenthesis() throws Swc4jCoreException {
        String code = "if (a) b; else c;";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstIfStmt ifStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstIfStmt.class), Swc4jAstType.IfStmt, 0, 17);
        Swc4jAstIdent ident = assertAst(
                ifStmt, ifStmt.getTest().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstExprStmt exprStmt = assertAst(
                ifStmt, ifStmt.getCons().as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 7, 9);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 7, 8);
        assertThat(ident.getSym()).isEqualTo("b");
        assertThat(ifStmt.getAlt().isPresent()).isTrue();
        exprStmt = assertAst(
                ifStmt, ifStmt.getAlt().get().as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 15, 17);
        ident = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 15, 16);
        assertThat(ident.getSym()).isEqualTo("c");
        assertSpan(code, script);
    }
}
