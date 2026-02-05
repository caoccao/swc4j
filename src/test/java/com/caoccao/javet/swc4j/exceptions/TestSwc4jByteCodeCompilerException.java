/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.exceptions;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Swc4jByteCodeCompilerException#getDetailedMessage()}.
 */
public class TestSwc4jByteCodeCompilerException extends BaseTestSuite {

    /**
     * Test detailed message with both source code and AST being null.
     */
    @Test
    public void testDetailedMessageBothNull() {
        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(null, null, "Both null");
        assertThat(ex.getDetailedMessage()).isEqualTo("Both null");
    }

    /**
     * Test detailed message with error position in the middle of a line.
     */
    @Test
    public void testDetailedMessageColumnInMiddle() throws Exception {
        String code = "x = someFunction();";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = script.getBody().get(0).as(Swc4jAstExprStmt.class);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, exprStmt.getExpr(), "Unknown function");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Unknown function
                  Error at position 0, line 1, column 1
                  x = someFunction();
                  ^^^^^^^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with error at column 79 (error starts at beginning).
     */
    @Test
    public void testDetailedMessageErrorAtColumn79() throws Exception {
        // Create code where there are no word separators - all x's followed by "err;"
        String prefix = "x".repeat(78);
        String code = prefix + "err;";

        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Error at 79");
        // Error starts at position 0, shows full line with word-based context (no truncation since no word breaks)
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Error at 79
                  Error at position 0, line 1, column 1
                  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxerr;
                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with a line exactly 80 characters.
     */
    @Test
    public void testDetailedMessageExactly80Chars() throws Exception {
        // Create a line exactly 80 characters long: const x = "..." (11 + 67 + 2 = 80)
        String code = "const x = \"" + "a".repeat(67) + "\";";
        assertThat(code.length()).isEqualTo(80);

        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Line exactly 80 chars");
        assertThat(ex.getDetailedMessage()).isEqualTo(
                "Line exactly 80 chars\n" +
                        "  Error at position 0, line 1, column 1\n" +
                        "  " + code + "\n" +
                        "  " + "^".repeat(80));
    }

    /**
     * Test detailed message with very long error message.
     */
    @Test
    public void testDetailedMessageLongErrorMessage() throws Exception {
        String code = "const x = 1;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        String longMessage = "This is a very long error message that exceeds normal length: " + "details ".repeat(20);
        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, longMessage);
        assertThat(ex.getDetailedMessage()).isEqualTo(
                longMessage + "\n" +
                        "  Error at position 0, line 1, column 1\n" +
                        "  const x = 1;\n" +
                        "  ^^^^^^^^^^^^");
    }

    /**
     * Test detailed message with a line longer than 80 characters, error at start.
     */
    @Test
    public void testDetailedMessageLongLineErrorAtStart() throws Exception {
        // Create a line longer than 80 characters
        String code = "const x = \"" + "a".repeat(100) + "\";";
        assertThat(code.length()).isGreaterThan(80);

        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Long line error");
        // Error starts at position 0, so no prefix truncation; shows full line with word-based context
        assertThat(ex.getDetailedMessage()).isEqualTo(
                "Long line error\n" +
                        "  Error at position 0, line 1, column 1\n" +
                        "  " + code + "\n" +
                        "  " + "^".repeat(code.length()));
    }

    /**
     * Test detailed message with multiline code - error on first line.
     */
    @Test
    public void testDetailedMessageMultilineFirstLine() throws Exception {
        String code = "const x = 1;\nconst y = 2;\nconst z = 3;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst firstStmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, firstStmt, "Error on first line");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Error on first line
                  Error at position 0, line 1, column 1
                  const x = 1;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with multiline code - error on last line.
     */
    @Test
    public void testDetailedMessageMultilineLastLine() throws Exception {
        String code = "const x = 1;\nconst y = 2;\nconst z = 3;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst thirdStmt = script.getBody().get(2);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, thirdStmt, "Error on third line");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Error on third line
                  Error at position 26, line 3, column 1
                  const z = 3;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with multiline code - error on middle line.
     */
    @Test
    public void testDetailedMessageMultilineMiddleLine() throws Exception {
        String code = "const x = 1;\nconst y = 2;\nconst z = 3;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "Error on second line");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Error on second line
                  Error at position 13, line 2, column 1
                  const y = 2;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with multiple statements on same line.
     */
    @Test
    public void testDetailedMessageMultipleStatementsOneLine() throws Exception {
        String code = "const a = 1; const b = 2; const c = 3;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1); // const b = 2;

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "Second statement");
        // Error doesn't start at beginning, shows "... " + 3 words before + error + 5 words after
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Second statement
                  Error at position 13, line 1, column 14
                  ... a = 1; const b = 2; const c = 3;
                             ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message shows correct column for nested expression.
     */
    @Test
    public void testDetailedMessageNestedExpression() throws Exception {
        String code = "result = (a + b) * c;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = script.getBody().get(0).as(Swc4jAstExprStmt.class);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, exprStmt.getExpr(), "Nested expression");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Nested expression
                  Error at position 0, line 1, column 1
                  result = (a + b) * c;
                  ^^^^^^^^^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with code ending without newline.
     */
    @Test
    public void testDetailedMessageNoTrailingNewline() throws Exception {
        String code = "const x = 1;\nconst y = 2"; // No trailing newline
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "No trailing newline");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                No trailing newline
                  Error at position 13, line 2, column 1
                  const y = 2
                  ^^^^^^^^^^^""");
    }

    /**
     * Test detailed message preserves the original exception message.
     */
    @Test
    public void testDetailedMessagePreservesOriginalMessage() throws Exception {
        String code = "const x = 1;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        String originalMessage = "This is a detailed error message with special chars: <>&\"'";
        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, originalMessage);

        assertThat(ex.getMessage()).isEqualTo(originalMessage);
        assertThat(ex.getDetailedMessage()).isEqualTo(
                originalMessage + "\n" +
                        "  Error at position 0, line 1, column 1\n" +
                        "  const x = 1;\n" +
                        "  ^^^^^^^^^^^^");
    }

    /**
     * Test detailed message with single character code.
     */
    @Test
    public void testDetailedMessageSingleCharacter() throws Exception {
        String code = "x";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Single char");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Single char
                  Error at position 0, line 1, column 1
                  x
                  ^""");
    }

    /**
     * Test detailed message with simple single-line code.
     */
    @Test
    public void testDetailedMessageSingleLine() throws Exception {
        String code = "const x = 1;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst firstStmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, firstStmt, "Unexpected statement");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Unexpected statement
                  Error at position 0, line 1, column 1
                  const x = 1;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with Windows-style line endings (CRLF).
     */
    @Test
    public void testDetailedMessageWindowsLineEndings() throws Exception {
        String code = "const x = 1;\r\nconst y = 2;\r\nconst z = 3;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "Windows line endings");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Windows line endings
                  Error at position 14, line 2, column 1
                  const y = 2;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with empty line in multiline code.
     */
    @Test
    public void testDetailedMessageWithEmptyLine() throws Exception {
        String code = "const x = 1;\n\nconst y = 2;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1); // const y = 2;

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "After empty line");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                After empty line
                  Error at position 14, line 3, column 1
                  const y = 2;
                  ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with indented code.
     */
    @Test
    public void testDetailedMessageWithIndentation() throws Exception {
        String code = "function test() {\n    const x = 1;\n    return x;\n}";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst funcDecl = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, funcDecl, "Function error");
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Function error
                  Error at position 0, line 1, column 1
                  function test() {
                  ^^^^^^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with null AST.
     */
    @Test
    public void testDetailedMessageWithNullAst() {
        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException("const x = 1;", null, "Test error");
        assertThat(ex.getDetailedMessage()).isEqualTo("Test error");
    }

    /**
     * Test detailed message with null source code.
     */
    @Test
    public void testDetailedMessageWithNullSourceCode() throws Exception {
        String code = "const x = 1;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        ISwc4jAst ast = output.getProgram();

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(null, ast, "Test error");
        assertThat(ex.getDetailedMessage()).isEqualTo("Test error");
    }

    /**
     * Test detailed message with many statements - shows both prefix and suffix.
     */
    @Test
    public void testDetailedMessageWithPrefixAndSuffix() throws Exception {
        String code = "const a = 1; const b = 2; const c = 3; const d = 4; const e = 5; const f = 6; const g = 7;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst secondStmt = script.getBody().get(1); // const b = 2;

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, secondStmt, "Middle statement");
        // Error in middle: shows "... " prefix (3 words before) and " ..." suffix (5 words after: const c = 3; const)
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Middle statement
                  Error at position 13, line 1, column 14
                  ... a = 1; const b = 2; const c = 3; const ...
                             ^^^^^^^^^^^^""");
    }

    /**
     * Test detailed message with tab characters.
     */
    @Test
    public void testDetailedMessageWithTabs() throws Exception {
        String code = "\tconst x = 1;";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Tab test");
        // Tab is whitespace, so error at column 2 shows "... " prefix
        assertThat(ex.getDetailedMessage()).isEqualTo(
                "Tab test\n" +
                        "  Error at position 1, line 1, column 2\n" +
                        "  ... const x = 1;\n" +
                        "      ^^^^^^^^^^^^");
    }

    /**
     * Test detailed message with Unicode characters in code.
     */
    @Test
    public void testDetailedMessageWithUnicode() throws Exception {
        String code = "const greeting = \"Hello, 世界!\";";
        Swc4jParseOutput output = swc4j.parse(code, jsScriptParseOptions.setCaptureAst(true));
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        ISwc4jAst stmt = script.getBody().get(0);

        Swc4jByteCodeCompilerException ex = new Swc4jByteCodeCompilerException(code, stmt, "Unicode test");
        // Note: span length is 30 characters
        assertThat(ex.getDetailedMessage()).isEqualTo("""
                Unicode test
                  Error at position 0, line 1, column 1
                  const greeting = "Hello, 世界!";
                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^""");
    }
}
