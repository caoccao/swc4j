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
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 6);
        Swc4jAstArrowExpr arrowExpr = (Swc4jAstArrowExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.ArrowExpr, 0, 6);
        assertFalse(arrowExpr.isAsync());
        assertFalse(arrowExpr.isGenerator());
        assertFalse(arrowExpr.getReturnType().isPresent());
        assertFalse(arrowExpr.getTypeParams().isPresent());
        Swc4jAstBlockStmt blockStmt = (Swc4jAstBlockStmt) assertAst(
                arrowExpr, arrowExpr.getBody(), Swc4jAstType.BlockStmt, 4, 6);
        assertTrue(blockStmt.getStmts().isEmpty());
        assertSpan(code, script);
    }

    @Test
    public void testNonEmptyFunction() throws Swc4jCoreException {
        String code = "(a,b)=>{ return a+b; }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 22);
        Swc4jAstArrowExpr arrowExpr = (Swc4jAstArrowExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.ArrowExpr, 0, 22);
        assertFalse(arrowExpr.isAsync());
        assertFalse(arrowExpr.isGenerator());
        assertFalse(arrowExpr.getReturnType().isPresent());
        assertFalse(arrowExpr.getTypeParams().isPresent());
        Swc4jAstBlockStmt blockStmt = (Swc4jAstBlockStmt) assertAst(
                arrowExpr, arrowExpr.getBody(), Swc4jAstType.BlockStmt, 7, 22);
        assertEquals(1, blockStmt.getStmts().size());
        Swc4jAstReturnStmt returnStmt = (Swc4jAstReturnStmt) assertAst(
                blockStmt, blockStmt.getStmts().get(0), Swc4jAstType.ReturnStmt, 9, 20);
        assertTrue(returnStmt.getArg().isPresent());
        Swc4jAstBinExpr binExpr = (Swc4jAstBinExpr) assertAst(
                returnStmt, returnStmt.getArg().get(), Swc4jAstType.BinExpr, 16, 19);
        Swc4jAstIdent ident = (Swc4jAstIdent) assertAst(
                binExpr, binExpr.getLeft(), Swc4jAstType.Ident, 16, 17);
        assertEquals("a", ident.getSym());
        assertEquals(Swc4jAstBinaryOp.Add, binExpr.getOp());
        ident = (Swc4jAstIdent) assertAst(
                binExpr, binExpr.getRight(), Swc4jAstType.Ident, 18, 19);
        assertEquals("b", ident.getSym());
        assertEquals(2, arrowExpr.getParams().size());
        Swc4jAstBindingIdent bindingIdent = (Swc4jAstBindingIdent) assertAst(
                arrowExpr, arrowExpr.getParams().get(0), Swc4jAstType.BindingIdent, 1, 2);
        ident = assertAst(bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 1, 2);
        assertEquals("a", ident.getSym());
        bindingIdent = (Swc4jAstBindingIdent) assertAst(
                arrowExpr, arrowExpr.getParams().get(1), Swc4jAstType.BindingIdent, 3, 4);
        ident = assertAst(bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 3, 4);
        assertEquals("b", ident.getSym());
        assertSpan(code, script);
    }
}
