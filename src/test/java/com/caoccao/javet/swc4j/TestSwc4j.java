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

import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.tokens.Swc4jAstToken;
import com.caoccao.javet.swc4j.ast.tokens.Swc4jAstTokenTextValue;
import com.caoccao.javet.swc4j.ast.tokens.Swc4jAstTokenTextValueFlags;
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

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4j {
    protected Swc4j swc4j;

    public TestSwc4j() {
        swc4j = new Swc4j();
    }

    @SuppressWarnings("unchecked")
    protected <T> void assertTokenValue(T expectedValue, Swc4jAstToken token) {
        assertInstanceOf(Swc4jAstTokenTextValue.class, token);
        Swc4jAstTokenTextValue<T> tokenTextAndValue = (Swc4jAstTokenTextValue<T>) token;
        assertEquals(expectedValue, tokenTextAndValue.getValue());
    }

    protected Swc4jAstToken parseAndAssert(
            String code,
            Swc4jParseOptions options,
            Swc4jAstTokenType type,
            String text,
            int startPosition,
            int endPosition)
            throws Swc4jCoreException {
        return parseAndAssert(code, options, type, text, startPosition, endPosition, 0, 1);
    }

    protected Swc4jAstToken parseAndAssert(
            String code,
            Swc4jParseOptions options,
            Swc4jAstTokenType type,
            String text,
            int startPosition,
            int endPosition,
            int tokenIndex,
            int tokenSize)
            throws Swc4jCoreException {
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output, code + " should be parsed successfully");
        List<Swc4jAstToken> tokens = output.getTokens();
        assertNotNull(tokens, code + " tokens shouldn't be null");
        assertEquals(tokenSize, tokens.size(), code + " token size should be 1");
        Swc4jAstToken token = tokens.get(tokenIndex);
        assertEquals(type, token.getType(), code + " type should match");
        assertEquals(text, token.getText(), code + " text should match");
        assertEquals(startPosition, token.getStartPosition(), code + " start position should match");
        assertEquals(endPosition, token.getEndPosition(), code + " end position should match");
        assertEquals(code.substring(startPosition, endPosition), token.getText(), code + " text should match");
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
    public void testParseTypeScriptWithCaptureAst() throws Swc4jCoreException {
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setCaptureAst(true);
        String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output);
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        assertNotNull(output.getAstModule());
        Swc4jAstModule module = output.getAstModule();
        assertEquals(1, module.getStartPosition());
        assertEquals(79, module.getEndPosition());
    }

    @SuppressWarnings("unchecked")
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
            List<Swc4jAstToken> tokens = output.getTokens();
            assertNotNull(tokens);
            assertEquals(18, tokens.size());
            assertEquals(Swc4jAstTokenType.Function, tokens.get(0).getType());
            assertTrue(tokens.get(0).isLineBreakAhead());
            assertEquals(Swc4jAstTokenType.Return, tokens.get(12).getType());
            assertFalse(tokens.get(12).isLineBreakAhead());
            tokens.forEach(token -> {
                assertNotEquals(Swc4jAstTokenType.Unknown, token.getType());
                assertEquals(
                        code.substring(token.getStartPosition(), token.getEndPosition()),
                        token.getText());
            });
        }
        // Keyword
        parseAndAssert("await f()", options, Swc4jAstTokenType.Await, "await", 0, 5, 0, 4);
        parseAndAssert("while (a) { break; }", options, Swc4jAstTokenType.Break, "break", 12, 17, 5, 8);
        parseAndAssert("switch (a) { case 1: break; }", options, Swc4jAstTokenType.Case, "case", 13, 17, 5, 11);
        parseAndAssert("try {} catch {}", options, Swc4jAstTokenType.Catch, "catch", 7, 12, 3, 6);
        parseAndAssert("class A {}", options, Swc4jAstTokenType.Class, "class", 0, 5, 0, 4);
        parseAndAssert("const a;", options, Swc4jAstTokenType.Const, "const", 0, 5, 0, 3);
        parseAndAssert("for (;;) { continue; }", options, Swc4jAstTokenType.Continue, "continue", 11, 19, 6, 9);
        parseAndAssert("debugger", options, Swc4jAstTokenType.Debugger, "debugger", 0, 8);
        parseAndAssert("export default a;", options, Swc4jAstTokenType.Default, "default", 7, 14, 1, 4);
        parseAndAssert("delete a.x;", options, Swc4jAstTokenType.Delete, "delete", 0, 6, 0, 5);
        parseAndAssert("do {} while (a);", options, Swc4jAstTokenType.Do, "do", 0, 2, 0, 8);
        parseAndAssert("if (a) {} else {}", options, Swc4jAstTokenType.Else, "else", 10, 14, 6, 9);
        parseAndAssert("export default a;", options, Swc4jAstTokenType.Export, "export", 0, 6, 0, 4);
        parseAndAssert("class A extends B {}", options, Swc4jAstTokenType.Extends, "extends", 8, 15, 2, 6);
        parseAndAssert("try {} catch {} finally {}", options, Swc4jAstTokenType.Finally, "finally", 16, 23, 6, 9);
        parseAndAssert("for (;;) {}", options, Swc4jAstTokenType.For, "for", 0, 3, 0, 7);
        parseAndAssert("function a() {}", options, Swc4jAstTokenType.Function, "function", 0, 8, 0, 6);
        parseAndAssert("if (a) {}", options, Swc4jAstTokenType.If, "if", 0, 2, 0, 6);
        parseAndAssert("import a from 'b';", options, Swc4jAstTokenType.Import, "import", 0, 6, 0, 5);
        parseAndAssert("a in b", options, Swc4jAstTokenType.In, "in", 2, 4, 1, 3);
        parseAndAssert("a instanceof b", options, Swc4jAstTokenType.InstanceOf, "instanceof", 2, 12, 1, 3);
        parseAndAssert("let a;", options, Swc4jAstTokenType.Let, "let", 0, 3, 0, 3);
        parseAndAssert("new Date()", options, Swc4jAstTokenType.New, "new", 0, 3, 0, 4);
        parseAndAssert("function a() { return 1; }", options, Swc4jAstTokenType.Return, "return", 15, 21, 5, 9);
        parseAndAssert("function a() { super(); }", options, Swc4jAstTokenType.Super, "super", 15, 20, 5, 10);
        parseAndAssert("switch (a) { case 1: break; }", options, Swc4jAstTokenType.Switch, "switch", 0, 6, 0, 11);
        parseAndAssert("function a() { this.x; }", options, Swc4jAstTokenType.This, "this", 15, 19, 5, 10);
        parseAndAssert("throw e;", options, Swc4jAstTokenType.Throw, "throw", 0, 5, 0, 3);
        parseAndAssert("try {} catch {}", options, Swc4jAstTokenType.Try, "try", 0, 3, 0, 6);
        parseAndAssert("typeof a", options, Swc4jAstTokenType.TypeOf, "typeof", 0, 6, 0, 2);
        parseAndAssert("var a;", options, Swc4jAstTokenType.Var, "var", 0, 3, 0, 3);
        parseAndAssert("void a", options, Swc4jAstTokenType.Void, "void", 0, 4, 0, 2);
        parseAndAssert("while (a) {}", options, Swc4jAstTokenType.While, "while", 0, 5, 0, 6);
        parseAndAssert("with (a) {}", options, Swc4jAstTokenType.With, "with", 0, 4, 0, 6);
        parseAndAssert("function *a() { yield 1; }", options, Swc4jAstTokenType.Yield, "yield", 16, 21, 6, 10);
        parseAndAssert("null", options, Swc4jAstTokenType.Null, "null", 0, 4);
        parseAndAssert("true", options, Swc4jAstTokenType.True, "true", 0, 4);
        parseAndAssert("false", options, Swc4jAstTokenType.False, "false", 0, 5);
        parseAndAssert("as", options, Swc4jAstTokenType.IdentKnown, "as", 0, 2);
        parseAndAssert("測試", options, Swc4jAstTokenType.IdentOther, "測試", 0, 2);
        // Operator - Generic
        parseAndAssert("() => {}", options, Swc4jAstTokenType.Arrow, "=>", 3, 5, 2, 5);
        parseAndAssert("class A { #abc; }", options, Swc4jAstTokenType.Hash, "#", 10, 11, 3, 7);
        parseAndAssert("a.b", options, Swc4jAstTokenType.Dot, ".", 1, 2, 1, 3);
        parseAndAssert("[...a]", options, Swc4jAstTokenType.DotDotDot, "...", 1, 4, 1, 4);
        parseAndAssert("!true", options, Swc4jAstTokenType.Bang, "!", 0, 1, 0, 2);
        parseAndAssert("a()", options, Swc4jAstTokenType.LParen, "(", 1, 2, 1, 3);
        parseAndAssert("a()", options, Swc4jAstTokenType.RParen, ")", 2, 3, 2, 3);
        parseAndAssert("a[0]", options, Swc4jAstTokenType.LBracket, "[", 1, 2, 1, 4);
        parseAndAssert("a[0]", options, Swc4jAstTokenType.RBracket, "]", 3, 4, 3, 4);
        parseAndAssert("a={}", options, Swc4jAstTokenType.LBrace, "{", 2, 3, 2, 4);
        parseAndAssert("a={}", options, Swc4jAstTokenType.RBrace, "}", 3, 4, 3, 4);
        parseAndAssert(";", options, Swc4jAstTokenType.Semi, ";", 0, 1);
        parseAndAssert("let a, b;", options, Swc4jAstTokenType.Comma, ",", 5, 6, 2, 5);
        parseAndAssert("``", options, Swc4jAstTokenType.BackQuote, "`", 0, 1, 0, 2);
        parseAndAssert("a={b:c}", options, Swc4jAstTokenType.Colon, ":", 4, 5, 4, 7);
        parseAndAssert("`${a}`", options, Swc4jAstTokenType.DollarLBrace, "${", 1, 3, 1, 5);
        parseAndAssert("a?.b", options, Swc4jAstTokenType.QuestionMark, "?", 1, 2, 1, 4);
        parseAndAssert("a++", options, Swc4jAstTokenType.PlusPlus, "++", 1, 3, 1, 2);
        parseAndAssert("a--", options, Swc4jAstTokenType.MinusMinus, "--", 1, 3, 1, 2);
        parseAndAssert("~true", options, Swc4jAstTokenType.Tilde, "~", 0, 1, 0, 2);
        // Operator - Binary
        parseAndAssert("1 == 2", options, Swc4jAstTokenType.EqEq, "==", 2, 4, 1, 3);
        parseAndAssert("1 != 2", options, Swc4jAstTokenType.NotEq, "!=", 2, 4, 1, 3);
        parseAndAssert("1 === 2", options, Swc4jAstTokenType.EqEqEq, "===", 2, 5, 1, 3);
        parseAndAssert("1 !== 2", options, Swc4jAstTokenType.NotEqEq, "!==", 2, 5, 1, 3);
        parseAndAssert("1 < 2", options, Swc4jAstTokenType.Lt, "<", 2, 3, 1, 3);
        parseAndAssert("1 <= 2", options, Swc4jAstTokenType.LtEq, "<=", 2, 4, 1, 3);
        parseAndAssert("1 > 2", options, Swc4jAstTokenType.Gt, ">", 2, 3, 1, 3);
        parseAndAssert("1 >= 2", options, Swc4jAstTokenType.GtEq, ">=", 2, 4, 1, 3);
        parseAndAssert("1 << 2", options, Swc4jAstTokenType.LShift, "<<", 2, 4, 1, 3);
        parseAndAssert("1 >> 2", options, Swc4jAstTokenType.RShift, ">>", 2, 4, 1, 3);
        parseAndAssert("1 >>> 2", options, Swc4jAstTokenType.ZeroFillRShift, ">>>", 2, 5, 1, 3);
        parseAndAssert("1 + 2", options, Swc4jAstTokenType.Add, "+", 2, 3, 1, 3);
        parseAndAssert("1 - 2", options, Swc4jAstTokenType.Sub, "-", 2, 3, 1, 3);
        parseAndAssert("1 * 2", options, Swc4jAstTokenType.Mul, "*", 2, 3, 1, 3);
        parseAndAssert("1 / 2", options, Swc4jAstTokenType.Div, "/", 2, 3, 1, 3);
        parseAndAssert("1 % 2", options, Swc4jAstTokenType.Mod, "%", 2, 3, 1, 3);
        parseAndAssert("1 | 2", options, Swc4jAstTokenType.BitOr, "|", 2, 3, 1, 3);
        parseAndAssert("1 ^ 2", options, Swc4jAstTokenType.BitXor, "^", 2, 3, 1, 3);
        parseAndAssert("1 & 2", options, Swc4jAstTokenType.BitAnd, "&", 2, 3, 1, 3);
        parseAndAssert("1 ** 2", options, Swc4jAstTokenType.Exp, "**", 2, 4, 1, 3);
        parseAndAssert("1 || 2", options, Swc4jAstTokenType.LogicalOr, "||", 2, 4, 1, 3);
        parseAndAssert("1 && 2", options, Swc4jAstTokenType.LogicalAnd, "&&", 2, 4, 1, 3);
        parseAndAssert("1 ?? 2", options, Swc4jAstTokenType.NullishCoalescing, "??", 2, 4, 1, 3);
        // Operator - Assign
        parseAndAssert("1 = 2", options, Swc4jAstTokenType.Assign, "=", 2, 3, 1, 3);
        parseAndAssert("1 += 2", options, Swc4jAstTokenType.AddAssign, "+=", 2, 4, 1, 3);
        parseAndAssert("1 -= 2", options, Swc4jAstTokenType.SubAssign, "-=", 2, 4, 1, 3);
        parseAndAssert("1 *= 2", options, Swc4jAstTokenType.MulAssign, "*=", 2, 4, 1, 3);
        parseAndAssert("1 /= 2", options, Swc4jAstTokenType.DivAssign, "/=", 2, 4, 1, 3);
        parseAndAssert("1 %= 2", options, Swc4jAstTokenType.ModAssign, "%=", 2, 4, 1, 3);
        parseAndAssert("1 <<= 2", options, Swc4jAstTokenType.LShiftAssign, "<<=", 2, 5, 1, 3);
        parseAndAssert("1 >>= 2", options, Swc4jAstTokenType.RShiftAssign, ">>=", 2, 5, 1, 3);
        parseAndAssert("1 >>>= 2", options, Swc4jAstTokenType.ZeroFillRShiftAssign, ">>>=", 2, 6, 1, 3);
        parseAndAssert("1 |= 2", options, Swc4jAstTokenType.BitOrAssign, "|=", 2, 4, 1, 3);
        parseAndAssert("1 ^= 2", options, Swc4jAstTokenType.BitXorAssign, "^=", 2, 4, 1, 3);
        parseAndAssert("1 &= 2", options, Swc4jAstTokenType.BitAndAssign, "&=", 2, 4, 1, 3);
        parseAndAssert("1 **= 2", options, Swc4jAstTokenType.ExpAssign, "**=", 2, 5, 1, 3);
        parseAndAssert("1 &&= 2", options, Swc4jAstTokenType.AndAssign, "&&=", 2, 5, 1, 3);
        parseAndAssert("1 ||= 2", options, Swc4jAstTokenType.OrAssign, "||=", 2, 5, 1, 3);
        parseAndAssert("1 ??= 2", options, Swc4jAstTokenType.NullishAssign, "??=", 2, 5, 1, 3);
        // TextValue
        assertTokenValue("/usr/bin/env -S -i node", parseAndAssert("#!/usr/bin/env -S -i node", options, Swc4jAstTokenType.Shebang, "#!/usr/bin/env -S -i node", 0, 25, 0, 1));
        assertTokenValue("x", parseAndAssert("a = 'x';", options, Swc4jAstTokenType.Str, "'x'", 4, 7, 2, 4));
        assertTokenValue(1D, parseAndAssert("a = 1;", options, Swc4jAstTokenType.Num, "1", 4, 5, 2, 4));
        assertTokenValue(1.23D, parseAndAssert("a = -1.23;", options, Swc4jAstTokenType.Num, "1.23", 5, 9, 3, 5));
        assertTokenValue(BigInteger.valueOf(1), parseAndAssert("a = 1n;", options, Swc4jAstTokenType.BigInt, "1n", 4, 6, 2, 4));
        assertTokenValue(BigInteger.valueOf(1), parseAndAssert("a = -1n;", options, Swc4jAstTokenType.BigInt, "1n", 5, 7, 3, 5));
        assertTokenValue(new BigInteger("1234567890123456789012345678901234567890"), parseAndAssert("a = 1234567890123456789012345678901234567890n;", options, Swc4jAstTokenType.BigInt, "1234567890123456789012345678901234567890n", 4, 45, 2, 4));
        // TextValueFlags
        Swc4jAstTokenTextValueFlags<String> astTokenRegex = (Swc4jAstTokenTextValueFlags<String>) parseAndAssert("a = /x/ig;", options, Swc4jAstTokenType.Regex, "x/ig", 5, 9, 3, 5);
        assertEquals("x", astTokenRegex.getValue());
        assertEquals("ig", astTokenRegex.getFlags());
        assertTokenValue("a ", parseAndAssert("`a ${b} c`", options, Swc4jAstTokenType.Template, "a ", 1, 3, 1, 7));
        parseAndAssert("`a ${b} c`", options, Swc4jAstTokenType.IdentOther, "b", 5, 6, 3, 7);
        assertTokenValue(" c", parseAndAssert("`a ${b} c`", options, Swc4jAstTokenType.Template, " c", 7, 9, 5, 7));
        // Jsx
        options.setMediaType(Swc4jMediaType.Jsx);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jAstTokenType.JsxTagStart, "<", 10, 11, 3, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jAstTokenType.JsxTagEnd, ">", 13, 14, 5, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jAstTokenType.JsxTagName, "h1", 11, 13, 4, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jAstTokenType.JsxTagText, "b", 14, 15, 6, 12);
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
    public void testTranspileTypeScriptWithCaptureTokens() throws Swc4jCoreException {
        Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setCaptureTokens(true);
        String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
        Swc4jTranspileOutput output = swc4j.transpile(code, options);
        assertNotNull(output);
        assertTrue(output.isModule());
        assertFalse(output.isScript());
        List<Swc4jAstToken> tokens = output.getTokens();
        assertNotNull(tokens);
        assertEquals(18, tokens.size());
        assertEquals(Swc4jAstTokenType.Function, tokens.get(0).getType());
        assertTrue(tokens.get(0).isLineBreakAhead());
        assertEquals(Swc4jAstTokenType.Return, tokens.get(12).getType());
        assertFalse(tokens.get(12).isLineBreakAhead());
        tokens.forEach(token ->
                assertEquals(
                        code.substring(token.getStartPosition(), token.getEndPosition()),
                        token.getText()));
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
