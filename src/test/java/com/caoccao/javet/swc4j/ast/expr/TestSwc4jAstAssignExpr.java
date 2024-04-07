/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstKeyValueProp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAssignOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstAssignExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testNonEmptyObject() throws Swc4jCoreException {
        String code = "a=1";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstAssignExpr assignExpr = (Swc4jAstAssignExpr) assertAst(
                exprStmt, exprStmt.getExpr(), Swc4jAstType.AssignExpr, 0, 3);
        assertEquals(Swc4jAstAssignOp.Assign, assignExpr.getOp());
        Swc4jAstBindingIdent bindingIdent = (Swc4jAstBindingIdent) assertAst(
                assignExpr, assignExpr.getLeft(), Swc4jAstType.BindingIdent, 0, 1);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        Swc4jAstNumber number = (Swc4jAstNumber) assertAst(
                assignExpr, assignExpr.getRight(), Swc4jAstType.Number, 2, 3);
        assertTrue(number.getRaw().isPresent());
        assertEquals(1, number.getValue());
        assertEquals("1", number.getRaw().get());
    }
}
