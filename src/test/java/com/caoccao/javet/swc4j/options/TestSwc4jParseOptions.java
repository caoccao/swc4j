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
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jParseOptions extends BaseTestSuite {

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
        Swc4jParseOutput output = swc4j.parse(code, jsxModuleParseOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(code, output.getSourceText());
        assertEquals(Swc4jMediaType.Jsx, output.getMediaType());
        assertNull(output.getTokens());
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
                        () -> swc4j.parse(code, jsModuleParseOptions))
                        .getMessage());
    }
}
