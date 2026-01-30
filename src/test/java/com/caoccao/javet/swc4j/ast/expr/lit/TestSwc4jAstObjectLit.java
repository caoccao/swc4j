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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstKeyValueProp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAssignOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstObjectLit extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyObject() throws Swc4jCoreException {
        String code = "a={}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 4);
        Swc4jAstAssignExpr assignExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstAssignExpr.class), Swc4jAstType.AssignExpr, 0, 4);
        assertThat(assignExpr.getOp()).isEqualTo(Swc4jAstAssignOp.Assign);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                assignExpr, assignExpr.getLeft().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 0, 1);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 0, 1);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstObjectLit objectLit = assertAst(
                assignExpr, assignExpr.getRight().as(Swc4jAstObjectLit.class), Swc4jAstType.ObjectLit, 2, 4);
        assertThat(objectLit.getProps().isEmpty()).isTrue();
        assertSpan(code, script);
    }

    @Test
    public void testNonEmptyObject() throws Swc4jCoreException {
        String code = "a={a:1,b:'x',c:true}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 20);
        Swc4jAstAssignExpr assignExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstAssignExpr.class), Swc4jAstType.AssignExpr, 0, 20);
        assertThat(assignExpr.getOp()).isEqualTo(Swc4jAstAssignOp.Assign);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                assignExpr, assignExpr.getLeft().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 0, 1);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 0, 1);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstObjectLit objectLit = assertAst(
                assignExpr, assignExpr.getRight().as(Swc4jAstObjectLit.class), Swc4jAstType.ObjectLit, 2, 20);
        // Number
        Swc4jAstKeyValueProp keyValueProp = assertAst(
                objectLit, objectLit.getProps().get(0).as(Swc4jAstKeyValueProp.class), Swc4jAstType.KeyValueProp, 3, 6);
        Swc4jAstIdentName identName = assertAst(
                keyValueProp, keyValueProp.getKey().as(Swc4jAstIdentName.class), Swc4jAstType.IdentName, 3, 4);
        assertThat(identName.getSym()).isEqualTo("a");
        Swc4jAstNumber number = assertAst(
                keyValueProp, keyValueProp.getValue().as(Swc4jAstNumber.class), Swc4jAstType.Number, 5, 6);
        assertThat(number.getValue()).isEqualTo(1);
        assertThat(number.getRaw().isPresent()).isTrue();
        assertThat(number.getRaw().get()).isEqualTo("1");
        // Str
        keyValueProp = assertAst(
                objectLit, objectLit.getProps().get(1).as(Swc4jAstKeyValueProp.class), Swc4jAstType.KeyValueProp, 7, 12);
        identName = assertAst(
                keyValueProp, keyValueProp.getKey().as(Swc4jAstIdentName.class), Swc4jAstType.IdentName, 7, 8);
        assertThat(identName.getSym()).isEqualTo("b");
        Swc4jAstStr str = assertAst(
                keyValueProp, keyValueProp.getValue().as(Swc4jAstStr.class), Swc4jAstType.Str, 9, 12);
        assertThat(str.getRaw().isPresent()).isTrue();
        assertThat(str.getValue()).isEqualTo("x");
        assertThat(str.getRaw().get()).isEqualTo("'x'");
        // Bool
        keyValueProp = assertAst(
                objectLit, objectLit.getProps().get(2).as(Swc4jAstKeyValueProp.class), Swc4jAstType.KeyValueProp, 13, 19);
        identName = assertAst(
                keyValueProp, keyValueProp.getKey().as(Swc4jAstIdentName.class), Swc4jAstType.IdentName, 13, 14);
        assertThat(identName.getSym()).isEqualTo("c");
        Swc4jAstBool b = assertAst(
                keyValueProp, keyValueProp.getValue().as(Swc4jAstBool.class), Swc4jAstType.Bool, 15, 19);
        assertThat(b.isValue()).isTrue();
        assertSpan(code, script);
    }
}
