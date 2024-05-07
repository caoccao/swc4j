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

package com.caoccao.javet.swc4j.plugins;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSwc4jPluginVisitors extends BaseTestSuite {
    @Test
    public void testTransformAssignExpr() throws Swc4jCoreException {
        String code = "a = b; c = d;";
        String expectedCode = "b=a;d=c;";
        class Visitor extends Swc4jAstVisitor {
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
        Visitor visitor = new Visitor();
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
        String expectedCode = "b = a;\nd = c;\n";
        class Visitor extends Swc4jAstVisitor {
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
        Visitor visitor = new Visitor();
        Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(visitor);
        Swc4jTranspileOutput output = swc4j.transpile(code, jsScriptTranspileOptions
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None)
                .setPluginHost(new Swc4jPluginHost().add(pluginVisitors)));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
    }
}
