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

package com.caoccao.javet.swc4j.options;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.comments.Swc4jComment;
import com.caoccao.javet.swc4j.comments.Swc4jCommentKind;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.tokens.Swc4jToken;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestSwc4jTranspileOptions extends BaseTestSuite {
    @Test
    public void testJsxWithCustomJsxFactory() throws Swc4jCoreException {
        String code = """
                import React from 'react';
                import './App.css';
                function App() {
                    return (
                        <h1> Hello World! </h1>
                    );
                }
                export default App;""";
        String expectedCode = """
                import React from 'react';
                import './App.css';
                function App() {
                  return CustomJsxFactory.createElement("h1", null, " Hello World! ");
                }
                export default App;
                """;
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, jsxModuleTranspileOptions
                .setJsx(Swc4jJsxRuntimeOption.Classic().setFactory("CustomJsxFactory.createElement")));
        assertThat(output).isNotNull();
        assertThat(output.getCode().substring(0, expectedCode.length())).isEqualTo(expectedCode);
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length())
        ).isEqualTo(
                expectedSourceMapPrefix
        );
        assertThat(output.getSourceMap()).isNull();
    }

    @Test
    public void testJsxWithDefaultOptions() throws Swc4jCoreException {
        String code = """
                import React from 'react';
                import './App.css';
                function App() {
                    return (
                        <h1> Hello World! </h1>
                    );
                }
                export default App;""";
        String expectedCode = """
                import React from 'react';
                import './App.css';
                function App() {
                  return React.createElement("h1", null, " Hello World! ");
                }
                export default App;
                """;
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, jsxModuleTranspileOptions
                .setJsx(Swc4jJsxRuntimeOption.Classic()));
        assertThat(output).isNotNull();
        assertThat(output.getSourceText()).isEqualTo(code);
        assertThat(output.getCode().substring(0, expectedCode.length())).isEqualTo(expectedCode);
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(output.getMediaType()).isEqualTo(Swc4jMediaType.Jsx);
        assertThat(
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length())
        ).isEqualTo(
                expectedSourceMapPrefix
        );;
        assertThat(output.getProgram()).isNull();
        assertThat(output.getSourceMap()).isNull();
    }

    @Test
    public void testTypeScriptWithCaptureTokens() throws Swc4jCoreException {
        String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setCaptureTokens(true));
        assertThat(output).isNotNull();
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        List<Swc4jToken> tokens = output.getTokens();
        assertThat(tokens).isNotNull();
        assertThat(tokens.size()).isEqualTo(18);
        assertThat(tokens.get(0).getType()).isEqualTo(Swc4jTokenType.Function);
        assertThat(tokens.get(0).isLineBreakAhead()).isTrue();
        assertThat(tokens.get(12).getType()).isEqualTo(Swc4jTokenType.Return);
        assertThat(tokens.get(12).isLineBreakAhead()).isFalse();
        tokens.forEach(token -> assertThat(token.getText())
                .isEqualTo(code.substring(token.getSpan().getStart(), token.getSpan().getEnd())));
    }

    @Test
    public void testTypeScriptWithComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setCaptureComments(true)
                .setKeepComments(true)
                .setSourceMap(Swc4jSourceMapOption.None));
        assertThat(output).isNotNull();
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(output.getCode()).isEqualTo("let a = 1; // Comment 2\n");
        assertThat(output.getComments().getLeading().size()).isEqualTo(1);
        assertThat(output.getComments().getTrailing().size()).isEqualTo(1);
        List<Swc4jComment> comments = output.getComments().getLeading(23);
        assertThat(comments.size()).isEqualTo(1);
        Swc4jComment comment = comments.get(0);
        assertThat(comment.getKind()).isEqualTo(Swc4jCommentKind.Block);
        assertThat(comment.getSpan().getStart()).isEqualTo(7);
        assertThat(comment.getSpan().getEnd()).isEqualTo(22);
        assertThat(comment.getSpan().getLine()).isEqualTo(1);
        assertThat(comment.getSpan().getColumn()).isEqualTo(8);
        assertThat(comment.getText()).isEqualTo(" Comment 1 ");
        comments = output.getComments().getTrailing(34);
        assertThat(comments.size()).isEqualTo(1);
        comment = comments.get(0);
        assertThat(comment.getKind()).isEqualTo(Swc4jCommentKind.Line);
        assertThat(comment.getSpan().getStart()).isEqualTo(35);
        assertThat(comment.getSpan().getEnd()).isEqualTo(47);
        assertThat(comment.getSpan().getLine()).isEqualTo(1);
        assertThat(comment.getSpan().getColumn()).isEqualTo(36);
        assertThat(comment.getText()).isEqualTo(" Comment 2");
        assertThat(output.getComments().hasLeading(23)).isTrue();
        assertThat(output.getComments().hasTrailing(34)).isTrue();
        comments = output.getComments().getComments();
        assertThat(comments.size()).isEqualTo(2);
    }

    @Test
    public void testTypeScriptWithInlineSourceMap() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = """
                function add(a, b) {
                  return a + b;
                }
                """;
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions);
        assertThat(output).isNotNull();
        assertThat(output.getCode().substring(0, expectedCode.length())).isEqualTo(expectedCode);
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length())
        ).isEqualTo(
                expectedSourceMapPrefix
        );;
        assertThat(output.getSourceMap()).isNull();
    }

    @Test
    public void testTypeScriptWithoutComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setKeepComments(false)
                .setSourceMap(Swc4jSourceMapOption.None));
        assertThat(output).isNotNull();
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(output.getCode()).isEqualTo("let a = 1;\n");
        assertThat(output.getComments()).isNull();
    }

    @ParameterizedTest
    @EnumSource(Swc4jParseMode.class)
    public void testTypeScriptWithoutInlineSourceMap(Swc4jParseMode parseMode)
            throws Swc4jCoreException, MalformedURLException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = """
                function add(a, b) {
                  return a + b;
                }
                """;
        String filePath = "file:///abc.ts";
        URL specifier = URI.create(filePath).toURL();
        String[] expectedProperties = new String[]{
                "version", "sources", "sourcesContent", filePath, "names", "mappings"};
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setParseMode(parseMode)
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                .setSourceMap(Swc4jSourceMapOption.Separate));
        assertThat(output).isNotNull();
        assertThat(output.getCode()).isEqualTo(expectedCode);
        Swc4jParseMode expectedParseMode = parseMode;
        if (expectedParseMode == Swc4jParseMode.Program) {
            expectedParseMode = Swc4jParseMode.Script;
        }
        assertThat(output.getParseMode()).isEqualTo(expectedParseMode);
        assertThat(output.getSourceMap()).isNotNull();
        Stream.of(expectedProperties).forEach(p -> assertThat(output.getSourceMap())
                .as(p + " should exist in the source map")
                .contains("\"" + p + "\""));
        assertThat(output.getComments()).isNull();
    }

    @Test
    public void testWrongMediaType() {
        String code = "function add(a:number, b:number) { return a+b; }";
        assertThatThrownBy(() -> swc4j.transpile(code, jsModuleTranspileOptions))
                .isInstanceOf(Swc4jCoreException.class)
                .hasMessageContaining("Expected ','")
                .hasMessageContaining("file:///main.js:1:15")
                .hasMessageContaining("function add(a:number, b:number) { return a+b; }");
    }
}
