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
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestSwc4jAstBinExpr extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEval() throws Swc4jCoreException {
        Map<String, String> testCaseMap = SimpleMap.of(
                "true+true", "2",
                "true+false", "1",
                "true+[]", "\"true\"",
                "false+[1]", "\"false1\"",
                "[1,2]+true", "\"1,2true\"",
                "1+[]", "\"1\"",
                "1+[1,2]", "\"11,2\"",
                "1+['a','b']", "\"1a,b\"",
                "true+/abc/i", "\"true/abc/i\"",
                "'x'+/abc/i", "\"x/abc/i\"",
                "1+/abc/i", "\"1/abc/i\"",
                "1+'a'", "\"1a\"",
                "'a'+'b'", "\"ab\"",
                "[]+undefined", "\"undefined\"",
                "''+undefined", "\"undefined\"",
                "0+undefined", "NaN",
                "0+NaN", "NaN",
                "NaN+undefined", "NaN",
                "NaN+[]['flat']", "NaNfunction flat() { [native code] }",
                "Infinity+[]['flat']", "Infinityfunction flat() { [native code] }",
                "Infinity+undefined", "NaN",
                "Infinity+0", "Infinity",
                "Infinity+[]", "\"Infinity\"",
                "undefined+undefined", "NaN",
                "1+1", "2");
        testCaseMap.clear();
        ISwc4jPluginHost pluginHost = program -> {
            program.visit(new Swc4jAstVisitor() {
                @Override
                public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
                    node.eval().ifPresent(n -> node.getParent().replaceNode(node, n));
                    return super.visitBinExpr(node);
                }
            });
            return true;
        };
        assertTransformJs(testCaseMap, pluginHost);
    }

    @Test
    public void testGetBangCount() throws Swc4jCoreException {
        Map<String, Integer> testCaseMap = SimpleMap.of(
                "a>b", 0,
                "!(a>b)", 1,
                "!(!(a>b))", 2,
                "!(!(!(a>b)))", 3);
        for (Map.Entry<String, Integer> entry : testCaseMap.entrySet()) {
            String code = entry.getKey();
            int bangCount = entry.getValue();
            Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
            List<Swc4jAstBinExpr> nodes = output.getProgram().find(Swc4jAstBinExpr.class);
            assertEquals(1, nodes.size());
            Swc4jAstBinExpr binExpr = nodes.get(0);
            assertEquals("a", binExpr.getLeft().as(Swc4jAstIdent.class).getSym());
            assertEquals("b", binExpr.getRight().as(Swc4jAstIdent.class).getSym());
            assertEquals(bangCount, binExpr.getBangCount());
        }
    }

    @Test
    public void testGetLogicalOperatorCount() throws Swc4jCoreException {
        Map<String, List<Object>> testCaseMap = SimpleMap.of(
                "a==b", SimpleList.of(
                        0, Swc4jAstBinaryOp.EqEq),
                "a>b||c<d", SimpleList.of(
                        0, Swc4jAstBinaryOp.LogicalOr,
                        1, Swc4jAstBinaryOp.Gt,
                        1, Swc4jAstBinaryOp.Lt),
                "a>b||(c==d&&(e+f)<=g)", SimpleList.of(
                        0, Swc4jAstBinaryOp.LogicalOr,
                        1, Swc4jAstBinaryOp.Gt,
                        1, Swc4jAstBinaryOp.LogicalAnd,
                        2, Swc4jAstBinaryOp.EqEq,
                        2, Swc4jAstBinaryOp.LtEq,
                        3, Swc4jAstBinaryOp.Add),
                "a>b||((c+d)?e:f)", SimpleList.of(
                        0, Swc4jAstBinaryOp.LogicalOr,
                        1, Swc4jAstBinaryOp.Gt,
                        0, Swc4jAstBinaryOp.Add));
        for (Map.Entry<String, List<Object>> entry : testCaseMap.entrySet()) {
            String code = entry.getKey();
            int size = entry.getValue().size() >> 1;
            Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
            List<Swc4jAstBinExpr> nodes = output.getProgram().find(Swc4jAstBinExpr.class);
            assertEquals(size, nodes.size());
            for (int i = 0; i < size; i++) {
                Swc4jAstBinExpr binExpr = nodes.get(i);
                int index = i << 1;
                assertEquals(entry.getValue().get(index + 1), binExpr.getOp());
                assertEquals(entry.getValue().get(index), binExpr.getLogicalOperatorCount());
            }
        }
    }

    @Test
    public void testOpWithSpace() {
        Stream.of(Swc4jAstBinaryOp.values())
                .filter(Swc4jAstBinaryOp::isSpaceRequired)
                .forEach(op -> {
                    try {
                        String code = "a " + op.getName() + " b";
                        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
                        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
                        Swc4jAstExprStmt exprStmt = assertAst(
                                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 4 + op.getName().length());
                        Swc4jAstBinExpr binExpr = assertAst(
                                exprStmt, exprStmt.getExpr().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 0, 4 + op.getName().length());
                        Swc4jAstIdent ident = assertAst(
                                binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
                        assertEquals("a", ident.getSym());
                        assertEquals(op, binExpr.getOp());
                        ident = assertAst(
                                binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 3 + op.getName().length(), 4 + op.getName().length());
                        assertEquals("b", ident.getSym());
                        assertSpan(code, script);
                    } catch (Throwable e) {
                        fail(e);
                    }
                });
    }

    @Test
    public void testOpWithoutSpace() {
        Stream.of(Swc4jAstBinaryOp.values())
                .filter(op -> !op.isSpaceRequired())
                .forEach(op -> {
                    try {
                        String code = "a" + op.getName() + "b";
                        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
                        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
                        Swc4jAstExprStmt exprStmt = assertAst(
                                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 2 + op.getName().length());
                        Swc4jAstBinExpr binExpr = assertAst(
                                exprStmt, exprStmt.getExpr().as(Swc4jAstBinExpr.class), Swc4jAstType.BinExpr, 0, 2 + op.getName().length());
                        Swc4jAstIdent ident = assertAst(
                                binExpr, binExpr.getLeft().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, 1);
                        assertEquals("a", ident.getSym());
                        assertEquals(op, binExpr.getOp());
                        ident = assertAst(
                                binExpr, binExpr.getRight().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1 + op.getName().length(), 2 + op.getName().length());
                        assertEquals("b", ident.getSym());
                        assertSpan(code, script);
                    } catch (Throwable e) {
                        fail(e);
                    }
                });
    }
}
