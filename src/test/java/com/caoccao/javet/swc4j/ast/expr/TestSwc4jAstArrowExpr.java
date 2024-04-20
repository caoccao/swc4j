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
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstArrowExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyFunction() throws Swc4jCoreException {
        String code = "()=>{}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 6);
        Swc4jAstArrowExpr arrowExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrowExpr.class), Swc4jAstType.ArrowExpr, 0, 6);
        assertFalse(arrowExpr.isAsync());
        assertFalse(arrowExpr.isGenerator());
        assertFalse(arrowExpr.getReturnType().isPresent());
        assertFalse(arrowExpr.getTypeParams().isPresent());
        Swc4jAstBlockStmt blockStmt = assertAst(
                arrowExpr, arrowExpr.getBody().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 4, 6);
        assertTrue(blockStmt.getStmts().isEmpty());
        assertSpan(code, script);
    }

    @Test
    public void testNonEmptyFunction() throws Swc4jCoreException {
        String code = "(a,b)=>{ return a+b; }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 22);
        Swc4jAstArrowExpr arrowExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrowExpr.class), Swc4jAstType.ArrowExpr, 0, 22);
        assertFalse(arrowExpr.isAsync());
        assertFalse(arrowExpr.isGenerator());
        assertFalse(arrowExpr.getReturnType().isPresent());
        assertFalse(arrowExpr.getTypeParams().isPresent());
        Swc4jAstBlockStmt blockStmt = assertAst(
                arrowExpr, arrowExpr.getBody().as(Swc4jAstBlockStmt.class), Swc4jAstType.BlockStmt, 7, 22);
        assertEquals(1, blockStmt.getStmts().size());
        Swc4jAstReturnStmt returnStmt = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstReturnStmt.class), Swc4jAstType.ReturnStmt, 9, 20);
        assertTrue(returnStmt.getArg().isPresent());
        Swc4jAstBinExpr binExpr = assertAst(
                returnStmt, returnStmt.getArg().get().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 16, 19);
        Swc4jAstIdent ident = assertAst(
                binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 16, 17);
        assertEquals("a", ident.getSym());
        assertEquals(Swc4jAstBinaryOp.Add, binExpr.getOp());
        ident = assertAst(
                binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 18, 19);
        assertEquals("b", ident.getSym());
        assertEquals(2, arrowExpr.getParams().size());
        Swc4jAstBindingIdent bindingIdent = assertAst(
                arrowExpr, arrowExpr.getParams().get(0).as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 1, 2);
        ident = assertAst(bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 1, 2);
        assertEquals("a", ident.getSym());
        bindingIdent = assertAst(
                arrowExpr, arrowExpr.getParams().get(1).as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 3, 4);
        ident = assertAst(bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 3, 4);
        assertEquals("b", ident.getSym());
        assertSpan(code, script);
    }
}
