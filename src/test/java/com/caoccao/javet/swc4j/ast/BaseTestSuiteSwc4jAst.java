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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstCounterVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseTestSuiteSwc4jAst extends BaseTestSuite {

    protected <AST extends ISwc4jAst> AST assertAst(
            ISwc4jAst parentNode,
            AST node,
            Swc4jAstType type,
            int start,
            int end) {
        return assertAst(parentNode, node, type, start, end, 1, start + 1);
    }

    protected <AST extends ISwc4jAst> AST assertAst(
            ISwc4jAst parentNode,
            AST node,
            Swc4jAstType type,
            int start,
            int end,
            int line,
            int column) {
        assertEquals(parentNode, node.getParent(), "Parent node mismatches");
        assertEquals(type, node.getType(), "Type mismatches");
        assertEquals(start, node.getSpan().getStart(), "Start mismatches");
        assertEquals(end, node.getSpan().getEnd(), "End mismatches");
        assertEquals(line, node.getSpan().getLine(), "Line mismatches");
        assertEquals(column, node.getSpan().getColumn(), "Column mismatches");
        return node;
    }

    protected void assertSpan(String code, ISwc4jAst node) {
        if (node != null) {
            if (node instanceof ISwc4jAstProgram) {
                assertNull(node.getParent(), node.getClass().getSimpleName() + "'s parent shouldn be null");
            } else {
                assertNotNull(node.getParent(), node.getClass().getSimpleName() + "'s parent shouldn't be null");
            }
            String text = null;
            switch (node.getType()) {
                case BigInt:
                case Bool:
                case DebuggerStmt:
                    // There is a bug in swc.
                    // case Ident:
                case JsxClosingElement:
                case JsxOpeningElement:
                case JsxText:
                case Number:
                case Null:
                case Regex:
                case ThisExpr:
                case TsKeywordType:
                    text = node.toString();
                    break;
                case Str: {
                    Swc4jAstStr str = node.as(Swc4jAstStr.class);
                    text = str.getRaw().orElse(str.getValue());
                    break;
                }
                default:
                    break;
            }
            if (text != null) {
                Swc4jSpan span = node.getSpan();
                String expectedText = code.substring(span.getStart(), span.getEnd());
                String errorMessage = "Text mismatches at " + span;
                assertEquals(expectedText, text, errorMessage);
            }
            node.getChildNodes().forEach(childNode -> assertSpan(code, childNode));
        }
    }

    protected void assertTransformJs(Map<String, String> testCaseMap, ISwc4jPluginHost pluginHost) throws Swc4jCoreException {
        for (Map.Entry<String, String> entry : testCaseMap.entrySet()) {
            jsScriptTransformOptions
                    .setOmitLastSemi(true)
                    .setSourceMap(Swc4jSourceMapOption.None)
                    .setPluginHost(pluginHost);
            Swc4jTransformOutput output = swc4j.transform(entry.getKey(), jsScriptTransformOptions);
            assertEquals(entry.getValue(), output.getCode(), "Failed to evaluate " + entry.getKey());
        }
    }

    protected void assertVisitor(Swc4jParseOptions options, List<VisitorCase> visitorCases, boolean debugEnabled) {
        try {
            for (VisitorCase visitorCase : visitorCases) {
                final Swc4jParseOutput output = swc4j.parse(visitorCase.getCode(), options);
                if (debugEnabled) {
                    logger.info(output.getProgram().toDebugString());
                }
                final Swc4jAstCounterVisitor visitor = new Swc4jAstCounterVisitor();
                assertEquals(Swc4jAstVisitorResponse.OkAndContinue, output.getProgram().visit(visitor));
                visitorCase.getVisitorMap().forEach((type, count) ->
                        assertEquals(
                                count,
                                visitor.get(type),
                                type.name() + " count mismatches with code: " + visitorCase.getCode()));
            }
        } catch (Throwable t) {
            fail(t);
        }
    }

    protected void assertVisitor(Swc4jParseOptions options, List<VisitorCase> visitorCases) {
        assertVisitor(options, visitorCases, false);
    }

    @BeforeEach
    protected void beforeEach() {
        super.beforeEach();
        jsModuleParseOptions.setCaptureAst(true);
        jsScriptParseOptions.setCaptureAst(true);
        jsxModuleParseOptions.setCaptureAst(true);
        jsxScriptParseOptions.setCaptureAst(true);
        tsModuleParseOptions.setCaptureAst(true);
        tsScriptParseOptions.setCaptureAst(true);
    }

    public static final class VisitorCase {
        private final String code;
        private final Map<Swc4jAstType, Integer> visitorMap;

        public VisitorCase(String code, Map<Swc4jAstType, Integer> visitorMap) {
            this.code = Objects.requireNonNull(code);
            this.visitorMap = Objects.requireNonNull(visitorMap);
        }

        public String getCode() {
            return code;
        }

        public Map<Swc4jAstType, Integer> getVisitorMap() {
            return visitorMap;
        }
    }
}
