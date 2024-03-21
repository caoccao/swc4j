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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.ast.BaseSwc4jAstToken;
import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4j {
    protected Swc4j swc4j;

    public TestSwc4j() {
        swc4j = new Swc4j();
    }

    protected BaseSwc4jAstToken parseAndAssert(
            String code,
            Swc4jParseOptions options,
            Swc4jAstTokenType type,
            String text,
            int startPosition,
            int endPosition)
            throws Swc4jCoreException {
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output, code + " should be parsed successfully");
        assertNotNull(output.getTokens(), code + " tokens shouldn't be null");
        assertEquals(1, output.getTokens().size(), code + " token size should be 1");
        BaseSwc4jAstToken token = output.getTokens().get(0);
        assertEquals(type, token.getType(), code + " type should match");
        assertEquals(text, token.getText(), code + " text should match");
        assertEquals(startPosition, token.getStartPosition(), code + " start position should match");
        assertEquals(endPosition, token.getEndPosition(), code + " end position should match");
        return token;
    }

    @Test
    public void testGetVersion() {
        assertEquals("0.2.0", swc4j.getVersion());
    }

    @Test
    public void testParseJsxWithDefaultOptions() throws Swc4jCoreException {
        String code = "import React from 'react';\n" +
                "import './App.css';\n" +
                "function App() {\n" +
                "    return (\n" +
                "        <h1> Hello World! </h1>\n" +
                "    );\n" +
                "}\n" +
                "export default App;";
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.Jsx);
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output);
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        assertEquals(code, output.getSourceText());
        assertEquals(Swc4jMediaType.Jsx, output.getMediaType());
        assertNull(output.getTokens());
    }

    @Test
    public void testParseTypeScriptWithCaptureTokens() throws Swc4jCoreException {
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setCaptureTokens(true);
        {
            String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
            Swc4jParseOutput output = swc4j.parse(code, options);
            assertNotNull(output);
            assertTrue(output.isModule());
            assertFalse(output.isScript());
            assertNotNull(output.getTokens());
            assertEquals(18, output.getTokens().size());
            assertEquals(Swc4jAstTokenType.Function, output.getTokens().get(0).getType());
            assertEquals(Swc4jAstTokenType.Return, output.getTokens().get(12).getType());
            output.getTokens().forEach(token ->
                    assertEquals(
                            code.substring(token.getStartPosition(), token.getEndPosition()),
                            token.getText()));
        }
        parseAndAssert("null", options, Swc4jAstTokenType.Null, "null", 0, 4);
        parseAndAssert("true", options, Swc4jAstTokenType.True, "true", 0, 4);
        parseAndAssert("false", options, Swc4jAstTokenType.False, "false", 0, 5);
        parseAndAssert("as", options, Swc4jAstTokenType.IdentKnown, "as", 0, 2);
    }

    @Test
    public void testParseWrongMediaType() {
        String code = "function add(a:number, b:number) { return a+b; }";
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.JavaScript);
        assertEquals(
                "Expected ',', got ':' at file:///main.js:1:15\n" +
                        "\n" +
                        "  function add(a:number, b:number) { return a+b; }\n" +
                        "                ~",
                assertThrows(
                        Swc4jCoreException.class,
                        () -> swc4j.parse(code, options))
                        .getMessage());
    }

    @Test
    public void testTranspileJsxWithCustomJsxFactory() throws Swc4jCoreException {
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
                "  return /*#__PURE__*/ CustomJsxFactory.createElement(\"h1\", null, \" Hello World! \");\n" +
                "}\n" +
                "export default App;\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setJsxFactory("CustomJsxFactory.createElement")
                .setMediaType(Swc4jMediaType.Jsx);
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getSourceMap());
    }

    @Test
    public void testTranspileJsxWithDefaultOptions() throws Swc4jCoreException {
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
                "  return /*#__PURE__*/ React.createElement(\"h1\", null, \" Hello World! \");\n" +
                "}\n" +
                "export default App;\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setMediaType(Swc4jMediaType.Jsx);
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        assertNotNull(output);
        assertEquals(code, output.getSourceText());
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        assertEquals(Swc4jMediaType.Jsx, output.getMediaType());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getSourceMap());
    }

    @Test
    public void testTranspileTypeScriptWithInlineSourceMap() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a, b) {\n" +
                "  return a + b;\n" +
                "}\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setMediaType(Swc4jMediaType.TypeScript);
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
        assertNull(output.getSourceMap());
    }

    @ParameterizedTest
    @EnumSource(Swc4jParseMode.class)
    public void testTranspileTypeScriptWithoutInlineSourceMap(Swc4jParseMode parseMode) throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a, b) {\n" +
                "  return a + b;\n" +
                "}\n";
        String specifier = "file:///abc.ts";
        String[] expectedProperties = new String[]{"version", "sources", "sourcesContent", specifier, "names", "mappings"};
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setParseMode(parseMode)
                .setInlineSourceMap(false)
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                .setSourceMap(true);
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        assertNotNull(output);
        assertEquals(expectedCode, output.getCode());
        switch (parseMode) {
            case Script:
                assertFalse(output.isModule());
                assertTrue(output.isScript());
                break;
            default:
                assertTrue(output.isModule());
                assertFalse(output.isScript());
                break;
        }
        assertNotNull(output.getSourceMap());
        Stream.of(expectedProperties).forEach(p -> assertTrue(
                output.getSourceMap().contains("\"" + p + "\""),
                p + " should exist in the source map"));
    }

    @Test
    public void testTranspileWrongMediaType() {
        String code = "function add(a:number, b:number) { return a+b; }";
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setMediaType(Swc4jMediaType.JavaScript);
        assertEquals(
                "Expected ',', got ':' at file:///main.js:1:15\n" +
                        "\n" +
                        "  function add(a:number, b:number) { return a+b; }\n" +
                        "                ~",
                assertThrows(
                        Swc4jCoreException.class,
                        () -> swc4j.transpile(code, options))
                        .getMessage());
    }
}
