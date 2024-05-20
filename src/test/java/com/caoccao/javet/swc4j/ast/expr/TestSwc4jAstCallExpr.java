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

public class TestSwc4jAstCallExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testCalleeAsIdent() throws Swc4jCoreException {
        String code = "a.b(c,d)";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 8);
        Swc4jAstCallExpr callExpr = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstCallExpr.class), Swc4jAstType.CallExpr, 0, 8);
        assertEquals(2, callExpr.getArgs().size());
        Swc4jAstExprOrSpread exprOrSpread = assertAst(
                callExpr, callExpr.getArgs().get(0).as(Swc4jAstExprOrSpread.class), Swc4jAstType.ExprOrSpread, 4, 5);
        Swc4jAstIdent ident = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 4, 5);
        assertEquals("c", ident.getSym());
        exprOrSpread = assertAst(
                callExpr, callExpr.getArgs().get(1).as(Swc4jAstExprOrSpread.class), Swc4jAstType.ExprOrSpread, 6, 7);
        ident = assertAst(
                exprOrSpread, exprOrSpread.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 6, 7);
        assertEquals("d", ident.getSym());
        Swc4jAstMemberExpr memberExpr = assertAst(
                callExpr, callExpr.getCallee().as(Swc4jAstMemberExpr.class), Swc4jAstType.MemberExpr, 0, 3);
        ident = assertAst(
                memberExpr, memberExpr.getObj().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
        assertEquals("a", ident.getSym());
        ident = assertAst(
                memberExpr, memberExpr.getProp().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 2, 3);
        assertEquals("b", ident.getSym());
        assertSpan(code, script);
    }

    @Test
    public void testEval() throws Swc4jCoreException {
        Map<String, String> testCaseMap = SimpleMap.of(
                "'a'.fontcolor('b')", "'<font color=\"b\">a</font>'",
                "'a'.fontcolor('b', 'c')", "'<font color=\"b\">a</font>'",
                "'a'.italics()", "\"<i>a</i>\"",
                "'a'.italics('b')", "\"<i>a</i>\"",
                "/a/['constructor']()", "/(?:)/",
                "/a/['constructor']('abc')", "/abc/",
                "/a/['constructor']('a/b', 'i')", "/a\\/b/i",
                "Function('return \"abc\"')()", "\"abc\"");
        ISwc4jPluginHost pluginHost = program -> {
            program.visit(new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
                    node.eval().ifPresent(n -> node.getParent().replaceNode(node, n));
                    return super.visitCallExpr(node);
                }
            });
            return true;
        };
        assertTransformJs(testCaseMap, pluginHost);
    }
}
