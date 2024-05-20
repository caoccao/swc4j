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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstUnaryExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testBangAndMinusAndPlusAndTilde() throws Swc4jCoreException {
        List<Swc4jAstUnaryOp> opList = SimpleList.of(
                Swc4jAstUnaryOp.Bang, Swc4jAstUnaryOp.Minus, Swc4jAstUnaryOp.Plus, Swc4jAstUnaryOp.Tilde);
        for (Swc4jAstUnaryOp op : opList) {
            String code = op.getName() + "a";
            Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
            Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
            Swc4jAstExprStmt exprStmt = assertAst(
                    script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, code.length());
            Swc4jAstUnaryExpr unaryExpr = assertAst(
                    exprStmt, exprStmt.getExpr().as(Swc4jAstUnaryExpr.class), Swc4jAstType.UnaryExpr, 0, code.length());
            assertEquals(op, unaryExpr.getOp());
            Swc4jAstIdent ident = assertAst(
                    unaryExpr, unaryExpr.getArg().as(Swc4jAstIdent.class), Swc4jAstType.Ident, code.length() - 1, code.length());
            assertEquals("a", ident.getSym());
            assertSpan(code, script);
        }
    }

    @Test
    public void testDeleteAndTypeOfAndVoid() throws Swc4jCoreException {
        List<Swc4jAstUnaryOp> opList = SimpleList.of(
                Swc4jAstUnaryOp.Delete, Swc4jAstUnaryOp.TypeOf, Swc4jAstUnaryOp.Void);
        for (Swc4jAstUnaryOp op : opList) {
            String code = op.getName() + " a";
            Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
            Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
            Swc4jAstExprStmt exprStmt = assertAst(
                    script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, code.length());
            Swc4jAstUnaryExpr unaryExpr = assertAst(
                    exprStmt, exprStmt.getExpr().as(Swc4jAstUnaryExpr.class), Swc4jAstType.UnaryExpr, 0, code.length());
            assertEquals(op, unaryExpr.getOp());
            Swc4jAstIdent ident = assertAst(
                    unaryExpr, unaryExpr.getArg().as(Swc4jAstIdent.class), Swc4jAstType.Ident, code.length() - 1, code.length());
            assertEquals("a", ident.getSym());
            assertSpan(code, script);
        }
    }

    @Test
    public void testEval() throws Swc4jCoreException {
        Map<String, String> testCaseMap = SimpleMap.of(
                "!true", "false",
                "!false", "true",
                "!1", "false",
                "!1.1", "false",
                "!0", "true",
                "!0e10", "true",
                "![]", "false",
                "![0]", "false",
                "![false]", "false",
                "!{}", "false",
                "!{a:1}", "false",
                "!'a'", "false",
                "!''", "true",
                "+[]", "0",
                "+[1]", "1",
                "+[1.1]", "1.1",
                "+[1,2]", "NaN",
                "+['a']", "NaN",
                "+true", "1",
                "+false", "0",
                "+1", "1",
                "+1.1", "1.1",
                "+1e5", "1e5",
                "+1.23e5", "123e3",
                "+NaN", "NaN",
                "+Infinity", "Infinity",
                "+'a'", "NaN",
                "+'1.1'", "1.1",
                "+'1.23e5'", "123e3",
                "+'1'", "1");
        ISwc4jPluginHost pluginHost = program -> {
            program.visit(new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
                    node.eval().ifPresent(n -> node.getParent().replaceNode(node, n));
                    return super.visitUnaryExpr(node);
                }
            });
            return true;
        };
        for (Map.Entry<String, String> entry : testCaseMap.entrySet()) {
            jsScriptTransformOptions
                    .setOmitLastSemi(true)
                    .setSourceMap(Swc4jSourceMapOption.None)
                    .setPluginHost(pluginHost);
            Swc4jTransformOutput output = swc4j.transform(entry.getKey(), jsScriptTransformOptions);
            assertEquals(entry.getValue(), output.getCode(), "Failed to evaluate " + entry.getKey());
        }
    }
}
