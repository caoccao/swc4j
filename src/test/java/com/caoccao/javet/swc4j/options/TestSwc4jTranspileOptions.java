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
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jTranspileOptions extends BaseTestSuite {
    @Test
    public void testJsxWithCustomJsxFactory() throws Swc4jCoreException {
        String code = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "    return (\n" +
                "        <h1> Hello World! </h1>\n" +
                "    );\n" +
                "}\n" +
                "export default App;";
        String expectedCode = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "  return CustomJsxFactory.createElement(\"h1\", null, \" Hello World! \");\n" +
                "}\n" +
                "export default App;\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, jsxModuleTranspileOptions
                .setJsxFactory("CustomJsxFactory.createElement"));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getSourceMap());
    }

    @Test
    public void testJsxWithDefaultOptions() throws Swc4jCoreException {
        String code = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "    return (\n" +
                "        <h1> Hello World! </h1>\n" +
                "    );\n" +
                "}\n" +
                "export default App;";
        String expectedCode = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "  return React.createElement(\"h1\", null, \" Hello World! \");\n" +
                "}\n" +
                "export default App;\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, jsxModuleTranspileOptions);
        assertNotNull(output);
        assertEquals(code, output.getSourceText());
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.Jsx, output.getMediaType());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getProgram());
        assertNull(output.getSourceMap());
    }

    @Test
    public void testTypeScriptWithCaptureTokens() throws Swc4jCoreException {
        String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setCaptureTokens(true));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        List<Swc4jToken> tokens = output.getTokens();
        assertNotNull(tokens);
        assertEquals(18, tokens.size());
        assertEquals(Swc4jTokenType.Function, tokens.get(0).getType());
        assertTrue(tokens.get(0).isLineBreakAhead());
        assertEquals(Swc4jTokenType.Return, tokens.get(12).getType());
        assertFalse(tokens.get(12).isLineBreakAhead());
        tokens.forEach(token ->
                assertEquals(
                        code.substring(token.getSpan().getStart(), token.getSpan().getEnd()),
                        token.getText()));
    }

    @Test
    public void testTypeScriptWithComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setCaptureComments(true)
                .setKeepComments(true)
                .setSourceMap(Swc4jSourceMapOption.None));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals("let a = 1; // Comment 2\n", output.getCode());
        assertEquals(1, output.getComments().getLeading().size());
        assertEquals(1, output.getComments().getTrailing().size());
        List<Swc4jComment> comments = output.getComments().getLeading(23);
        assertEquals(1, comments.size());
        Swc4jComment comment = comments.get(0);
        assertEquals(Swc4jCommentKind.Block, comment.getKind());
        assertEquals(7, comment.getSpan().getStart());
        assertEquals(22, comment.getSpan().getEnd());
        assertEquals(1, comment.getSpan().getLine());
        assertEquals(8, comment.getSpan().getColumn());
        assertEquals(" Comment 1 ", comment.getText());
        comments = output.getComments().getTrailing(34);
        assertEquals(1, comments.size());
        comment = comments.get(0);
        assertEquals(Swc4jCommentKind.Line, comment.getKind());
        assertEquals(35, comment.getSpan().getStart());
        assertEquals(47, comment.getSpan().getEnd());
        assertEquals(1, comment.getSpan().getLine());
        assertEquals(36, comment.getSpan().getColumn());
        assertEquals(" Comment 2", comment.getText());
        assertTrue(output.getComments().hasLeading(23));
        assertTrue(output.getComments().hasTrailing(34));
        comments = output.getComments().getComments();
        assertEquals(2, comments.size());
    }

    @Test
    public void testTypeScriptWithInlineSourceMap() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a, b) {\n" +
                "  return a + b;\n" +
                "}\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions);
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getSourceMap());
    }

    @Test
    public void testTypeScriptWithoutComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setKeepComments(false)
                .setSourceMap(Swc4jSourceMapOption.None));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals("let a = 1;\n", output.getCode());
        assertNull(output.getComments());
    }

    @ParameterizedTest
    @EnumSource(Swc4jParseMode.class)
    public void testTypeScriptWithoutInlineSourceMap(Swc4jParseMode parseMode)
            throws Swc4jCoreException, MalformedURLException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a, b) {\n" +
                "  return a + b;\n" +
                "}\n";
        URL specifier = new URL("file://abc.ts");
        String[] expectedProperties = new String[]{
                "version", "sources", "sourcesContent", specifier + "/", "names", "mappings"};
        Swc4jTranspileOutput output = swc4j.transpile(code, tsModuleTranspileOptions
                .setParseMode(parseMode)
                .setInlineSourceMap(false)
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                .setSourceMap(Swc4jSourceMapOption.Separate));
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
        assertEquals(parseMode, output.getParseMode());
        assertNotNull(output.getSourceMap());
        Stream.of(expectedProperties).forEach(p -> assertTrue(
                output.getSourceMap().contains("\"" + p + "\""),
                p + " should exist in the source map"));
        assertNull(output.getComments());
    }

    @Test
    public void testWrongMediaType() {
        String code = "function add(a:number, b:number) { return a+b; }";
        assertEquals(
                "Expected ',', got ':' at file://main.js/:1:15\n" +
                        "\n" +
                        "  function add(a:number, b:number) { return a+b; }\n" +
                        "                ~",
                assertThrows(
                        Swc4jCoreException.class,
                        () -> swc4j.transpile(code, jsModuleTranspileOptions))
                        .getMessage());
    }
}
