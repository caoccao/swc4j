/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.tokens;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class TestSwc4jToken extends BaseTestSuite {
    protected <T> void assertTokenValue(T expectedValue, Swc4jToken token) {
        assertInstanceOf(Swc4jTokenTextValue.class, token);
        Swc4jTokenTextValue<T> tokenTextAndValue = (Swc4jTokenTextValue<T>) token;
        assertEquals(expectedValue, tokenTextAndValue.getValue());
    }

    protected Swc4jToken parseAndAssert(
            String code,
            Swc4jParseOptions options,
            Swc4jTokenType type,
            String text,
            int start,
            int end)
            throws Swc4jCoreException {
        return parseAndAssert(code, options, type, text, start, end, 0, 1);
    }

    protected Swc4jToken parseAndAssert(
            String code,
            Swc4jParseOptions options,
            Swc4jTokenType type,
            String text,
            int start,
            int end,
            int tokenIndex,
            int tokenSize)
            throws Swc4jCoreException {
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output, code + " should be parsed successfully");
        List<Swc4jToken> tokens = output.getTokens();
        assertNotNull(tokens, code + " tokens shouldn't be null");
        assertEquals(tokenSize, tokens.size(), code + " token size should be " + tokenSize);
        Swc4jToken token = tokens.get(tokenIndex);
        assertEquals(type, token.getType(), code + " type should match");
        assertEquals(text, token.getText(), code + " text should match");
        assertEquals(start, token.getSpan().getStart(), code + " start position should match");
        assertEquals(end, token.getSpan().getEnd(), code + " end position should match");
        assertEquals(code.substring(start, end), token.getText(), code + " text should match");
        return token;
    }

    @Test
    public void testByteToIndexMap() throws Swc4jCoreException {
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setCaptureTokens(true);
        String code = "function add加法(a變量:number, b變量:number) { return a變量+b變量; }";
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Script, output.getParseMode());
        List<Swc4jToken> tokens = output.getTokens();
        assertNotNull(tokens);
        assertEquals(18, tokens.size());
        assertEquals(Swc4jTokenType.Function, tokens.get(0).getType());
        assertTrue(tokens.get(0).isLineBreakAhead());
        assertEquals(Swc4jTokenType.Return, tokens.get(12).getType());
        assertFalse(tokens.get(12).isLineBreakAhead());
        tokens.forEach(token -> {
            assertNotEquals(Swc4jTokenType.Unknown, token.getType());
            assertEquals(
                    code.substring(token.getSpan().getStart(), token.getSpan().getEnd()),
                    token.getText());
        });
    }

    @Test
    public void testTokens() throws Swc4jCoreException {
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setCaptureTokens(true);
        // Keyword
        parseAndAssert("await f()", options, Swc4jTokenType.Await, "await", 0, 5, 0, 4);
        parseAndAssert("while (a) { break; }", options, Swc4jTokenType.Break, "break", 12, 17, 5, 8);
        parseAndAssert("switch (a) { case 1: break; }", options, Swc4jTokenType.Case, "case", 13, 17, 5, 11);
        parseAndAssert("try {} catch {}", options, Swc4jTokenType.Catch, "catch", 7, 12, 3, 6);
        parseAndAssert("class A {}", options, Swc4jTokenType.Class, "class", 0, 5, 0, 4);
        parseAndAssert("const a;", options, Swc4jTokenType.Const, "const", 0, 5, 0, 3);
        parseAndAssert("for (;;) { continue; }", options, Swc4jTokenType.Continue, "continue", 11, 19, 6, 9);
        parseAndAssert("debugger", options, Swc4jTokenType.Debugger, "debugger", 0, 8);
        parseAndAssert("export default a;", options, Swc4jTokenType.Default, "default", 7, 14, 1, 4);
        parseAndAssert("delete a.x;", options, Swc4jTokenType.Delete, "delete", 0, 6, 0, 5);
        parseAndAssert("do {} while (a);", options, Swc4jTokenType.Do, "do", 0, 2, 0, 8);
        parseAndAssert("if (a) {} else {}", options, Swc4jTokenType.Else, "else", 10, 14, 6, 9);
        parseAndAssert("export default a;", options, Swc4jTokenType.Export, "export", 0, 6, 0, 4);
        parseAndAssert("class A extends B {}", options, Swc4jTokenType.Extends, "extends", 8, 15, 2, 6);
        parseAndAssert("try {} catch {} finally {}", options, Swc4jTokenType.Finally, "finally", 16, 23, 6, 9);
        parseAndAssert("for (;;) {}", options, Swc4jTokenType.For, "for", 0, 3, 0, 7);
        parseAndAssert("function a() {}", options, Swc4jTokenType.Function, "function", 0, 8, 0, 6);
        parseAndAssert("if (a) {}", options, Swc4jTokenType.If, "if", 0, 2, 0, 6);
        parseAndAssert("import a from 'b';", options, Swc4jTokenType.Import, "import", 0, 6, 0, 5);
        parseAndAssert("a in b", options, Swc4jTokenType.In, "in", 2, 4, 1, 3);
        parseAndAssert("a instanceof b", options, Swc4jTokenType.InstanceOf, "instanceof", 2, 12, 1, 3);
        parseAndAssert("let a;", options, Swc4jTokenType.Let, "let", 0, 3, 0, 3);
        parseAndAssert("new Date()", options, Swc4jTokenType.New, "new", 0, 3, 0, 4);
        parseAndAssert("function a() { return 1; }", options, Swc4jTokenType.Return, "return", 15, 21, 5, 9);
        parseAndAssert("function a() { super(); }", options, Swc4jTokenType.Super, "super", 15, 20, 5, 10);
        parseAndAssert("switch (a) { case 1: break; }", options, Swc4jTokenType.Switch, "switch", 0, 6, 0, 11);
        parseAndAssert("function a() { this.x; }", options, Swc4jTokenType.This, "this", 15, 19, 5, 10);
        parseAndAssert("throw e;", options, Swc4jTokenType.Throw, "throw", 0, 5, 0, 3);
        parseAndAssert("try {} catch {}", options, Swc4jTokenType.Try, "try", 0, 3, 0, 6);
        parseAndAssert("typeof a", options, Swc4jTokenType.TypeOf, "typeof", 0, 6, 0, 2);
        parseAndAssert("var a;", options, Swc4jTokenType.Var, "var", 0, 3, 0, 3);
        parseAndAssert("void a", options, Swc4jTokenType.Void, "void", 0, 4, 0, 2);
        parseAndAssert("while (a) {}", options, Swc4jTokenType.While, "while", 0, 5, 0, 6);
        parseAndAssert("with (a) {}", options, Swc4jTokenType.With, "with", 0, 4, 0, 6);
        parseAndAssert("function *a() { yield 1; }", options, Swc4jTokenType.Yield, "yield", 16, 21, 6, 10);
        parseAndAssert("null", options, Swc4jTokenType.Null, "null", 0, 4);
        parseAndAssert("true", options, Swc4jTokenType.True, "true", 0, 4);
        parseAndAssert("false", options, Swc4jTokenType.False, "false", 0, 5);
        parseAndAssert("as", options, Swc4jTokenType.IdentKnown, "as", 0, 2);
        parseAndAssert("測試", options, Swc4jTokenType.IdentOther, "測試", 0, 2);
        // Operator - Generic
        parseAndAssert("() => {}", options, Swc4jTokenType.Arrow, "=>", 3, 5, 2, 5);
        parseAndAssert("class A { #abc; }", options, Swc4jTokenType.Hash, "#", 10, 11, 3, 7);
        parseAndAssert("a.b", options, Swc4jTokenType.Dot, ".", 1, 2, 1, 3);
        parseAndAssert("[...a]", options, Swc4jTokenType.DotDotDot, "...", 1, 4, 1, 4);
        parseAndAssert("!true", options, Swc4jTokenType.Bang, "!", 0, 1, 0, 2);
        parseAndAssert("a()", options, Swc4jTokenType.LParen, "(", 1, 2, 1, 3);
        parseAndAssert("a()", options, Swc4jTokenType.RParen, ")", 2, 3, 2, 3);
        parseAndAssert("a[0]", options, Swc4jTokenType.LBracket, "[", 1, 2, 1, 4);
        parseAndAssert("a[0]", options, Swc4jTokenType.RBracket, "]", 3, 4, 3, 4);
        parseAndAssert("a={}", options, Swc4jTokenType.LBrace, "{", 2, 3, 2, 4);
        parseAndAssert("a={}", options, Swc4jTokenType.RBrace, "}", 3, 4, 3, 4);
        parseAndAssert(";", options, Swc4jTokenType.Semi, ";", 0, 1);
        parseAndAssert("let a, b;", options, Swc4jTokenType.Comma, ",", 5, 6, 2, 5);
        parseAndAssert("``", options, Swc4jTokenType.BackQuote, "`", 0, 1, 0, 2);
        parseAndAssert("a={b:c}", options, Swc4jTokenType.Colon, ":", 4, 5, 4, 7);
        parseAndAssert("`${a}`", options, Swc4jTokenType.DollarLBrace, "${", 1, 3, 1, 5);
        parseAndAssert("a?.b", options, Swc4jTokenType.QuestionMark, "?", 1, 2, 1, 4);
        parseAndAssert("a++", options, Swc4jTokenType.PlusPlus, "++", 1, 3, 1, 2);
        parseAndAssert("a--", options, Swc4jTokenType.MinusMinus, "--", 1, 3, 1, 2);
        parseAndAssert("~true", options, Swc4jTokenType.Tilde, "~", 0, 1, 0, 2);
        // Operator - Binary
        parseAndAssert("1 == 2", options, Swc4jTokenType.EqEq, "==", 2, 4, 1, 3);
        parseAndAssert("1 != 2", options, Swc4jTokenType.NotEq, "!=", 2, 4, 1, 3);
        parseAndAssert("1 === 2", options, Swc4jTokenType.EqEqEq, "===", 2, 5, 1, 3);
        parseAndAssert("1 !== 2", options, Swc4jTokenType.NotEqEq, "!==", 2, 5, 1, 3);
        parseAndAssert("1 < 2", options, Swc4jTokenType.Lt, "<", 2, 3, 1, 3);
        parseAndAssert("1 <= 2", options, Swc4jTokenType.LtEq, "<=", 2, 4, 1, 3);
        parseAndAssert("1 > 2", options, Swc4jTokenType.Gt, ">", 2, 3, 1, 3);
        parseAndAssert("1 >= 2", options, Swc4jTokenType.GtEq, ">=", 2, 4, 1, 3);
        parseAndAssert("1 << 2", options, Swc4jTokenType.LShift, "<<", 2, 4, 1, 3);
        parseAndAssert("1 >> 2", options, Swc4jTokenType.RShift, ">>", 2, 4, 1, 3);
        parseAndAssert("1 >>> 2", options, Swc4jTokenType.ZeroFillRShift, ">>>", 2, 5, 1, 3);
        parseAndAssert("1 + 2", options, Swc4jTokenType.Add, "+", 2, 3, 1, 3);
        parseAndAssert("1 - 2", options, Swc4jTokenType.Sub, "-", 2, 3, 1, 3);
        parseAndAssert("1 * 2", options, Swc4jTokenType.Mul, "*", 2, 3, 1, 3);
        parseAndAssert("1 / 2", options, Swc4jTokenType.Div, "/", 2, 3, 1, 3);
        parseAndAssert("1 % 2", options, Swc4jTokenType.Mod, "%", 2, 3, 1, 3);
        parseAndAssert("1 | 2", options, Swc4jTokenType.BitOr, "|", 2, 3, 1, 3);
        parseAndAssert("1 ^ 2", options, Swc4jTokenType.BitXor, "^", 2, 3, 1, 3);
        parseAndAssert("1 & 2", options, Swc4jTokenType.BitAnd, "&", 2, 3, 1, 3);
        parseAndAssert("1 ** 2", options, Swc4jTokenType.Exp, "**", 2, 4, 1, 3);
        parseAndAssert("1 || 2", options, Swc4jTokenType.LogicalOr, "||", 2, 4, 1, 3);
        parseAndAssert("1 && 2", options, Swc4jTokenType.LogicalAnd, "&&", 2, 4, 1, 3);
        parseAndAssert("1 ?? 2", options, Swc4jTokenType.NullishCoalescing, "??", 2, 4, 1, 3);
        // Operator - Assign
        parseAndAssert("a = 2", options, Swc4jTokenType.Assign, "=", 2, 3, 1, 3);
        parseAndAssert("a += 2", options, Swc4jTokenType.AddAssign, "+=", 2, 4, 1, 3);
        parseAndAssert("a -= 2", options, Swc4jTokenType.SubAssign, "-=", 2, 4, 1, 3);
        parseAndAssert("a *= 2", options, Swc4jTokenType.MulAssign, "*=", 2, 4, 1, 3);
        parseAndAssert("a /= 2", options, Swc4jTokenType.DivAssign, "/=", 2, 4, 1, 3);
        parseAndAssert("a %= 2", options, Swc4jTokenType.ModAssign, "%=", 2, 4, 1, 3);
        parseAndAssert("a <<= 2", options, Swc4jTokenType.LShiftAssign, "<<=", 2, 5, 1, 3);
        parseAndAssert("a >>= 2", options, Swc4jTokenType.RShiftAssign, ">>=", 2, 5, 1, 3);
        parseAndAssert("a >>>= 2", options, Swc4jTokenType.ZeroFillRShiftAssign, ">>>=", 2, 6, 1, 3);
        parseAndAssert("a |= 2", options, Swc4jTokenType.BitOrAssign, "|=", 2, 4, 1, 3);
        parseAndAssert("a ^= 2", options, Swc4jTokenType.BitXorAssign, "^=", 2, 4, 1, 3);
        parseAndAssert("a &= 2", options, Swc4jTokenType.BitAndAssign, "&=", 2, 4, 1, 3);
        parseAndAssert("a **= 2", options, Swc4jTokenType.ExpAssign, "**=", 2, 5, 1, 3);
        parseAndAssert("a &&= 2", options, Swc4jTokenType.AndAssign, "&&=", 2, 5, 1, 3);
        parseAndAssert("a ||= 2", options, Swc4jTokenType.OrAssign, "||=", 2, 5, 1, 3);
        parseAndAssert("a ??= 2", options, Swc4jTokenType.NullishAssign, "??=", 2, 5, 1, 3);
        // TextValue
        assertTokenValue("/usr/bin/env -S -i node", parseAndAssert("#!/usr/bin/env -S -i node", options, Swc4jTokenType.Shebang, "#!/usr/bin/env -S -i node", 0, 25, 0, 1));
        assertTokenValue("x", parseAndAssert("a = 'x';", options, Swc4jTokenType.Str, "'x'", 4, 7, 2, 4));
        assertTokenValue(1D, parseAndAssert("a = 1;", options, Swc4jTokenType.Num, "1", 4, 5, 2, 4));
        assertTokenValue(1.23D, parseAndAssert("a = -1.23;", options, Swc4jTokenType.Num, "1.23", 5, 9, 3, 5));
        assertTokenValue(BigInteger.valueOf(1), parseAndAssert("a = 1n;", options, Swc4jTokenType.BigInt, "1n", 4, 6, 2, 4));
        assertTokenValue(BigInteger.valueOf(1), parseAndAssert("a = -1n;", options, Swc4jTokenType.BigInt, "1n", 5, 7, 3, 5));
        assertTokenValue(new BigInteger("1234567890123456789012345678901234567890"), parseAndAssert("a = 1234567890123456789012345678901234567890n;", options, Swc4jTokenType.BigInt, "1234567890123456789012345678901234567890n", 4, 45, 2, 4));
        // TextValueFlags
        Swc4jTokenTextValueFlags<String> astTokenRegex = (Swc4jTokenTextValueFlags<String>) parseAndAssert("a = /x/ig;", options, Swc4jTokenType.Regex, "/x/ig", 4, 9, 2, 4);
        assertEquals("x", astTokenRegex.getValue());
        assertEquals("ig", astTokenRegex.getFlags());
        assertTokenValue("a ", parseAndAssert("`a ${b} c`", options, Swc4jTokenType.Template, "a ", 1, 3, 1, 7));
        parseAndAssert("`a ${b} c`", options, Swc4jTokenType.IdentOther, "b", 5, 6, 3, 7);
        assertTokenValue(" c", parseAndAssert("`a ${b} c`", options, Swc4jTokenType.Template, " c", 7, 9, 5, 7));
        // Jsx
        options.setMediaType(Swc4jMediaType.Jsx);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jTokenType.JsxTagStart, "<", 10, 11, 3, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jTokenType.JsxTagEnd, ">", 13, 14, 5, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jTokenType.JsxTagName, "h1", 11, 13, 4, 12);
        parseAndAssert("const a = <h1>b</h1>;", options, Swc4jTokenType.JsxTagText, "b", 14, 15, 6, 12);
    }
}
