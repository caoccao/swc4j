/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.plugins;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jPluginVisitors extends BaseTestSuite {
    @Test
    public void testTransformAssignExpr() throws Swc4jCoreException {
        String code = "a = b; c = d;";
        String expectedCode = "b=a;d=c;";
        TestAssignExprVisitor visitor = new TestAssignExprVisitor();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(visitor);
        Swc4jTransformOutput output = swc4j.transform(code, jsScriptTransformOptions
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None)
                .setPluginHost(new Swc4jPluginHost().add(pluginVisitors)));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
    }

    @Test
    public void testTransformIfStmt() throws Swc4jCoreException {
        String code = "if (a) { b; } else { c; }";
        String expectedCode = "if(a){c;}else{b;}";
        TestIfStmtVisitor visitor = new TestIfStmtVisitor();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(visitor);
        Swc4jTransformOutput output = swc4j.transform(code, jsScriptTransformOptions
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None)
                .setPluginHost(new Swc4jPluginHost().add(pluginVisitors)));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
    }

    @Test
    public void testTranspileAssignExpr() throws Swc4jCoreException {
        String code = "a = b; c = d;";
        String expectedCode = "b = a;\n" +
                "d = c;\n";
        TestAssignExprVisitor visitor = new TestAssignExprVisitor();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(visitor);
        Swc4jTranspileOutput output = swc4j.transpile(code, jsScriptTranspileOptions
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None)
                .setPluginHost(new Swc4jPluginHost().add(pluginVisitors)));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
    }

    @Test
    public void testTranspileIfStmt() throws Swc4jCoreException {
        String code = "if (a) { b; } else { c; }";
        String expectedCode = "if (a) {\n" +
                "  c;\n" +
                "} else {\n" +
                "  b;\n" +
                "}\n";
        TestIfStmtVisitor visitor = new TestIfStmtVisitor();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(visitor);
        Swc4jTranspileOutput output = swc4j.transpile(code, jsScriptTranspileOptions
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None)
                .setPluginHost(new Swc4jPluginHost().add(pluginVisitors)));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
    }

    /**
     * It swaps the left and right.
     */
    static class TestAssignExprVisitor extends Swc4jAstVisitor {
        @Override
        public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
            Swc4jAstBindingIdent leftBindingIdent = node.getLeft().as(Swc4jAstBindingIdent.class);
            Swc4jAstIdent leftIdent = leftBindingIdent.getId().as(Swc4jAstIdent.class);
            Swc4jAstIdent rightIdent = node.getRight().as(Swc4jAstIdent.class);
            leftBindingIdent.setId(rightIdent);
            node.setRight(leftIdent);
            return super.visitAssignExpr(node);
        }
    }

    /**
     * It swaps the cons and alt.
     */
    static class TestIfStmtVisitor extends Swc4jAstVisitor {
        @Override
        public Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node) {
            ISwc4jAstStmt cons = node.getCons().as(ISwc4jAstStmt.class);
            assertTrue(node.getAlt().isPresent());
            ISwc4jAstStmt alt = node.getAlt().get().as(ISwc4jAstStmt.class);
            node.setCons(alt);
            node.setAlt(cons);
            return super.visitIfStmt(node);
        }
    }
}
