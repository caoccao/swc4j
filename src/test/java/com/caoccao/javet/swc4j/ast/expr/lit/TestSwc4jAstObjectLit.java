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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstKeyValueProp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAssignOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstObjectLit extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyObject() throws Swc4jCoreException {
        String code = "a={}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 4);
        Swc4jAstAssignExpr assignExpr = (Swc4jAstAssignExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.AssignExpr, 0, 4);
        assertEquals(Swc4jAstAssignOp.Assign, assignExpr.getOp());
        Swc4jAstBindingIdent bindingIdent = (Swc4jAstBindingIdent) assertAst(
                assignExpr, assignExpr.getLeft(), Swc4jAstType.BindingIdent, 0, 1);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        Swc4jAstObjectLit objectLit = (Swc4jAstObjectLit) assertAst(
                assignExpr, assignExpr.getRight(), Swc4jAstType.ObjectLit, 2, 4);
        assertTrue(objectLit.getProps().isEmpty());
        assertSpan(code, script);
    }

    @Test
    public void testNonEmptyObject() throws Swc4jCoreException {
        String code = "a={a:1,b:'x',c:true}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 20);
        Swc4jAstAssignExpr assignExpr = (Swc4jAstAssignExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.AssignExpr, 0, 20);
        assertEquals(Swc4jAstAssignOp.Assign, assignExpr.getOp());
        Swc4jAstBindingIdent bindingIdent = (Swc4jAstBindingIdent) assertAst(
                assignExpr, assignExpr.getLeft(), Swc4jAstType.BindingIdent, 0, 1);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        Swc4jAstObjectLit objectLit = (Swc4jAstObjectLit) assertAst(
                assignExpr, assignExpr.getRight(), Swc4jAstType.ObjectLit, 2, 20);
        // Number
        Swc4jAstKeyValueProp keyValueProp = (Swc4jAstKeyValueProp) assertAst(
                objectLit, objectLit.getProps().get(0), Swc4jAstType.KeyValueProp, 3, 6);
        ident = (Swc4jAstIdent) assertAst(
                keyValueProp, keyValueProp.getKey(), Swc4jAstType.Ident, 3, 4);
        assertFalse(ident.isOptional());
        assertEquals("a", ident.getSym());
        Swc4jAstNumber number = (Swc4jAstNumber) assertAst(
                keyValueProp, keyValueProp.getValue(), Swc4jAstType.Number, 5, 6);
        assertEquals(1, number.getValue());
        assertTrue(number.getRaw().isPresent());
        assertEquals("1", number.getRaw().get());
        // Str
        keyValueProp = (Swc4jAstKeyValueProp) assertAst(
                objectLit, objectLit.getProps().get(1), Swc4jAstType.KeyValueProp, 7, 12);
        ident = (Swc4jAstIdent) assertAst(
                keyValueProp, keyValueProp.getKey(), Swc4jAstType.Ident, 7, 8);
        assertFalse(ident.isOptional());
        assertEquals("b", ident.getSym());
        Swc4jAstStr str = (Swc4jAstStr) assertAst(
                keyValueProp, keyValueProp.getValue(), Swc4jAstType.Str, 9, 12);
        assertTrue(str.getRaw().isPresent());
        assertEquals("x", str.getValue());
        assertEquals("'x'", str.getRaw().get());
        // Bool
        keyValueProp = (Swc4jAstKeyValueProp) assertAst(
                objectLit, objectLit.getProps().get(2), Swc4jAstType.KeyValueProp, 13, 19);
        ident = (Swc4jAstIdent) assertAst(
                keyValueProp, keyValueProp.getKey(), Swc4jAstType.Ident, 13, 14);
        assertFalse(ident.isOptional());
        assertEquals("c", ident.getSym());
        Swc4jAstBool b = (Swc4jAstBool) assertAst(
                keyValueProp, keyValueProp.getValue(), Swc4jAstType.Bool, 15, 19);
        assertTrue(b.getValue());
        assertSpan(code, script);
    }
}
