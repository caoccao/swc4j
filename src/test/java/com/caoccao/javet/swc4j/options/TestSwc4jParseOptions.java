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
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertNull(output.getProgram());
        assertNull(output.getTokens());
    }

    @Test
    public void testTypeScriptWithComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jParseOutput output = swc4j.parse(code, tsModuleParseOptions
                .setCaptureAst(true)
                .setCaptureComments(true));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
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
