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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstAssignPat extends BaseTestSuiteSwc4jAst {
    @Test
    public void testWithTsTypeAnn() throws Swc4jCoreException {
        String code = "(a: number = 1) => {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 21);
        Swc4jAstArrowExpr arrowExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstArrowExpr.class), Swc4jAstType.ArrowExpr, 0, 21);
        assertThat(arrowExpr.isAsync()).isFalse();
        assertThat(arrowExpr.isGenerator()).isFalse();
        assertThat(arrowExpr.getReturnType().isPresent()).isFalse();
        assertThat(arrowExpr.getTypeParams().isPresent()).isFalse();
        assertThat(arrowExpr.getParams().size()).isEqualTo(1);
        Swc4jAstAssignPat assignPat = assertAst(
                arrowExpr, arrowExpr.getParams().get(0).as(Swc4jAstAssignPat.class), Swc4jAstType.AssignPat, 1, 14);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                assignPat, assignPat.getLeft().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 1, 10);
        // TODO There is a bug that the span of Ident is the same with the BindingIdent.
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 1, 10);
        assertThat(ident.getSym()).isEqualTo("a");
        assertThat(bindingIdent.getTypeAnn().isPresent()).isTrue();
        assertAst(bindingIdent, bindingIdent.getTypeAnn().get(), Swc4jAstType.TsTypeAnn, 2, 10);
        Swc4jAstNumber number = assertAst(
                assignPat, assignPat.getRight().as(Swc4jAstNumber.class), Swc4jAstType.Number, 13, 14);
        assertThat(number.getRaw().isPresent()).isTrue();
        assertThat(number.getRaw().get()).isEqualTo("1");
        assertThat(number.getValue()).isEqualTo(1);
        assertSpan(code, script);
    }
}
