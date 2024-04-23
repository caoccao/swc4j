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
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jTransformOptions extends BaseTestSuite {

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
        String expectedCode = "import React from\"react\";import\"./App.css\";function App(){return(<h1> Hello World! </h1>);}export default App;\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTransformOutput output = swc4j.transform(code, jsxModuleTransformOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.Jsx, output.getMediaType());
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
    }

    @Test
    public void testTypeScriptWithDefaultOptions() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a:number,b:number){return a+b;}\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTransformOutput output = swc4j.transform(code, tsModuleTransformOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.TypeScript, output.getMediaType());
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
    }

    @Test
    public void testTypeScriptWithoutMinifyAndSourceMap() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a: number, b: number) {\n" +
                "  return a + b;\n" +
                "}\n";
        Swc4jTransformOutput output = swc4j.transform(code, tsModuleTransformOptions
                .setMinify(false)
                .setInlineSourceMap(false)
                .setSourceMap(Swc4jSourceMapOption.None));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.TypeScript, output.getMediaType());
        assertEquals(expectedCode, output.getCode());
        assertNull(output.getSourceMap());
    }

    @Test
    public void testTypeScriptWithoutMinifyWithSeparateSourceMap() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a: number, b: number) {\n" +
                "  return a + b;\n" +
                "}\n";
        Swc4jTransformOutput output = swc4j.transform(code, tsModuleTransformOptions
                .setMinify(false)
                .setInlineSourceMap(false)
                .setSourceMap(Swc4jSourceMapOption.Separate));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.TypeScript, output.getMediaType());
        assertEquals(expectedCode, output.getCode());
        assertNotNull(output.getSourceMap());
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
                        () -> swc4j.transform(code, jsModuleTransformOptions))
                        .getMessage());
    }
}
