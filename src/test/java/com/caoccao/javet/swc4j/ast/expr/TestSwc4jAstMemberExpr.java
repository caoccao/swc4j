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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstMemberExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEval() throws Swc4jCoreException {
        Map<String, String> testCaseMap = SimpleMap.of(
                "'abc'[1]", "\"b\"",
                "'abc'['1']", "\"b\"",
                "[]['at']['constructor']", "Function");
        ISwc4jPluginHost pluginHost = program -> {
            program.visit(new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
                    node.eval().ifPresent(n -> node.getParent().replaceNode(node, n));
                    return super.visitMemberExpr(node);
                }
            });
            return true;
        };
        assertTransformJs(testCaseMap, pluginHost);
    }

    @Test
    public void testPropAsComputedPropName() throws Swc4jCoreException {
        String code = "a['b']";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 6);
        Swc4jAstMemberExpr memberExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstMemberExpr.class), Swc4jAstType.MemberExpr, 0, 6);
        Swc4jAstIdent ident = assertAst(
                memberExpr, memberExpr.getObj().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        Swc4jAstComputedPropName computedPropName = assertAst(
                memberExpr, memberExpr.getProp().as(Swc4jAstComputedPropName.class), Swc4jAstType.ComputedPropName, 1, 6);
        Swc4jAstStr str = assertAst(
                computedPropName, computedPropName.getExpr().as(Swc4jAstStr.class), Swc4jAstType.Str, 2, 5);
        assertEquals("b", str.getValue());
        assertSpan(code, script);
    }

    @Test
    public void testPropAsIdent() throws Swc4jCoreException {
        String code = "a.b";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 3);
        Swc4jAstMemberExpr memberExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstMemberExpr.class), Swc4jAstType.MemberExpr, 0, 3);
        Swc4jAstIdent ident = assertAst(
                memberExpr, memberExpr.getObj().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        ident = assertAst(
                memberExpr, memberExpr.getProp().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 2, 3);
        assertEquals("b", ident.getSym());
        assertSpan(code, script);
    }
}
