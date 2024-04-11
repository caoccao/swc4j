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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstCounterVisitor;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseTestSuiteSwc4jAst extends BaseTestSuite {
    protected Swc4jParseOptions jsxModuleOptions;
    protected Swc4jParseOptions jsxScriptOptions;
    protected Swc4jParseOptions tsModuleOptions;
    protected Swc4jParseOptions tsScriptOptions;

    public BaseTestSuiteSwc4jAst() {
        super();
        this.jsxModuleOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.Jsx)
                .setParseMode(Swc4jParseMode.Module)
                .setCaptureAst(true);
        this.jsxScriptOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.Jsx)
                .setParseMode(Swc4jParseMode.Script)
                .setCaptureAst(true);
        this.tsModuleOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Module)
                .setCaptureAst(true);
        this.tsScriptOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Script)
                .setCaptureAst(true);
    }

    protected <AST extends ISwc4jAst> AST assertAst(
            ISwc4jAst parentNode,
            AST node,
            Swc4jAstType type,
            int start,
            int end) {
        assertEquals(parentNode, node.getParent(), "Parent node mismatches");
        assertEquals(type, node.getType(), "Type mismatches");
        assertEquals(start, node.getSpan().getStart(), "Start mismatches");
        assertEquals(end, node.getSpan().getEnd(), "End mismatches");
        return node;
    }

    protected void assertSpan(String code, ISwc4jAst node) {
        if (node != null) {
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
                case Str:
                case ThisExpr:
                case TsKeywordType:
                    text = node.toString();
                    break;
                default:
                    break;
            }
            if (text != null) {
                Swc4jAstSpan span = node.getSpan();
                String expectedText = code.substring(span.getStart(), span.getEnd());
                String errorMessage = "Text mismatches at " + span;
                assertEquals(expectedText, text, errorMessage);
            }
            node.getChildNodes().forEach(childNode -> assertSpan(code, childNode));
        }
    }

    protected void assertVisitor(Swc4jParseOptions options, List<VisitorCase> visitorCases) {
        assertVisitor(options, visitorCases, false);
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
