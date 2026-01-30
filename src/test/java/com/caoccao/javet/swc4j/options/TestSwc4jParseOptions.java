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
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestSwc4jParseOptions extends BaseTestSuite {

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
        Swc4jParseOutput output = swc4j.parse(code, jsxModuleParseOptions);
        assertThat(output).isNotNull();
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
        assertThat(output.getSourceText()).isEqualTo(code);
        assertThat(output.getMediaType()).isEqualTo(Swc4jMediaType.Jsx);
        assertThat(output.getProgram()).isNull();
        assertThat(output.getTokens()).isNull();
    }

    @Test
    public void testTypeScriptWithComments() throws Swc4jCoreException {
        String code = "let a: /* Comment 1 */ number = 1; // Comment 2";
        Swc4jParseOutput output = swc4j.parse(code, tsModuleParseOptions
                .setCaptureAst(true)
                .setCaptureComments(true));
        assertThat(output).isNotNull();
        assertThat(output.getParseMode()).isEqualTo(Swc4jParseMode.Module);
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
    public void testWrongMediaType() {
        String code = "function add(a:number, b:number) { return a+b; }";
        assertThatThrownBy(() -> swc4j.parse(code, jsModuleParseOptions))
                .isInstanceOf(Swc4jCoreException.class)
                .hasMessageContaining("Expected ','")
                .hasMessageContaining("file:///main.js:1:15")
                .hasMessageContaining("function add(a:number, b:number) { return a+b; }");
    }
}
