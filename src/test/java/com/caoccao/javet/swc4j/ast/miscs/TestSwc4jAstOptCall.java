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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstOptCall extends BaseTestSuiteSwc4jAst {
    @Test
    public void test() throws Swc4jCoreException {
        String code = "a?.b(c,d)";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 9);
        Swc4jAstOptChainExpr optChainExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstOptChainExpr.class), Swc4jAstType.OptChainExpr, 0, 9);
        assertThat(optChainExpr.isOptional()).isFalse();
        Swc4jAstOptCall optCall = assertAst(
                optChainExpr, optChainExpr.getBase().as(Swc4jAstOptCall.class), Swc4jAstType.OptCall, 0, 9);
        assertThat(optCall.getTypeArgs().isPresent()).isFalse();
        assertThat(optCall.getArgs().size()).isEqualTo(2);
        Swc4jAstExprOrSpread exprOrSpread = assertAst(
                optCall, optCall.getArgs().get(0), Swc4jAstType.ExprOrSpread, 5, 6);
        Swc4jAstIdent ident = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 5, 6);
        assertThat(ident.getSym()).isEqualTo("c");
        exprOrSpread = assertAst(
                optCall, optCall.getArgs().get(1), Swc4jAstType.ExprOrSpread, 7, 8);
        ident = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 7, 8);
        assertThat(ident.getSym()).isEqualTo("d");
        Swc4jAstOptChainExpr childOptChainExpr = assertAst(
                optCall, optCall.getCallee().as(Swc4jAstOptChainExpr.class), Swc4jAstType.OptChainExpr, 0, 4);
        assertThat(childOptChainExpr.isOptional()).isTrue();
        Swc4jAstMemberExpr memberExpr = assertAst(
                childOptChainExpr, childOptChainExpr.getBase().as(Swc4jAstMemberExpr.class), Swc4jAstType.MemberExpr, 0, 4);
        ident = assertAst(
                memberExpr, memberExpr.getObj().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstIdentName identName = assertAst(
                memberExpr, memberExpr.getProp().as(Swc4jAstIdentName.class), Swc4jAstType.IdentName, 3, 4);
        assertThat(identName.getSym()).isEqualTo("b");
        assertSpan(code, script);
    }
}
