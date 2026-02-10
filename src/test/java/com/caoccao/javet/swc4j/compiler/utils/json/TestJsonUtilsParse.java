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

package com.caoccao.javet.swc4j.compiler.utils.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for JSON.parse edge cases (#21-46).
 */
public class TestJsonUtilsParse {

    @Test
    public void testEdgeCase21EmptyString() {
        // #21: Empty string → error
        assertThatThrownBy(() -> JsonUtils.parse(""))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase21NullInput() {
        // #21: Null input → error
        assertThatThrownBy(() -> JsonUtils.parse(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase22WhitespaceOnly() {
        // #22: Whitespace-only → error
        assertThatThrownBy(() -> JsonUtils.parse("   \t\n"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase23TrailingContent() {
        // #23: Trailing non-whitespace → error
        assertThatThrownBy(() -> JsonUtils.parse("123abc"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase23TrailingContentMultipleValues() {
        assertThatThrownBy(() -> JsonUtils.parse("123 456"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase24TrailingCommaArray() {
        assertThatThrownBy(() -> JsonUtils.parse("[1,]"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase24TrailingCommaObject() {
        // #24: Trailing commas → error
        assertThatThrownBy(() -> JsonUtils.parse("{\"a\": 1,}"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase25SingleQuotes() {
        // #25: Single quotes → error
        assertThatThrownBy(() -> JsonUtils.parse("{'key': 'value'}"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase26UnquotedKeys() {
        // #26: Unquoted keys → error
        assertThatThrownBy(() -> JsonUtils.parse("{key: \"value\"}"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase27EscapeSequences() {
        // #27: All valid escape sequences
        assertThat(JsonUtils.parse("\"hello \\\"world\\\"\"")).isEqualTo("hello \"world\"");
        assertThat(JsonUtils.parse("\"back\\\\slash\"")).isEqualTo("back\\slash");
        assertThat(JsonUtils.parse("\"slash\\/path\"")).isEqualTo("slash/path");
        assertThat(JsonUtils.parse("\"line\\nbreak\"")).isEqualTo("line\nbreak");
        assertThat(JsonUtils.parse("\"\\r\\t\\b\\f\"")).isEqualTo("\r\t\b\f");
    }

    @Test
    public void testEdgeCase28InvalidEscape() {
        // #28: Invalid escape sequences → error
        assertThatThrownBy(() -> JsonUtils.parse("\"\\x41\""))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> JsonUtils.parse("\"\\a\""))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase29IncompleteUnicode() {
        // #29: Incomplete unicode escape → error
        assertThatThrownBy(() -> JsonUtils.parse("\"\\u00\""))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> JsonUtils.parse("\"\\u\""))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase29UnicodeEscape() {
        // Valid unicode escape
        assertThat(JsonUtils.parse("\"\\u0041\"")).isEqualTo("A");
        assertThat(JsonUtils.parse("\"\\u00e9\"")).isEqualTo("\u00e9");
    }

    @Test
    public void testEdgeCase30SurrogatePairs() {
        // #30: Surrogate pairs — pass through as Java chars
        String json = "\"\\uD83D\\uDE00\"";
        String result = (String) JsonUtils.parse(json);
        assertThat(result).isEqualTo("\uD83D\uDE00");
    }

    @Test
    public void testEdgeCase31LoneSurrogate() {
        // #31: Lone surrogate — accept as-is
        String json = "\"\\uD800\"";
        String result = (String) JsonUtils.parse(json);
        assertThat(result).isEqualTo("\uD800");
    }

    @Test
    public void testEdgeCase32NumericOverflow() {
        // #32: Very large integers → Double
        Object result = JsonUtils.parse("99999999999999999999");
        assertThat(result).isInstanceOf(Double.class);
    }

    @Test
    public void testEdgeCase33LeadingZeroAllowed() {
        // Zero itself and 0.x are allowed
        assertThat(JsonUtils.parse("0")).isEqualTo(0);
        assertThat(JsonUtils.parse("0.5")).isEqualTo(0.5);
    }

    @Test
    public void testEdgeCase33LeadingZeros() {
        // #33: Leading zeros → error
        assertThatThrownBy(() -> JsonUtils.parse("0123"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase34NegativeZero() {
        // #34: -0 → Double with -0.0
        Object result = JsonUtils.parse("-0");
        assertThat(result).isInstanceOf(Double.class);
        assertThat((double) result).isEqualTo(-0.0);
        assertThat(Double.doubleToRawLongBits((double) result) & 0x8000000000000000L).isNotEqualTo(0L);
    }

    @Test
    public void testEdgeCase35ExponentNotation() {
        // #35: Exponent notation → Double
        assertThat(JsonUtils.parse("1e10")).isEqualTo(1e10);
        assertThat(JsonUtils.parse("1E10")).isEqualTo(1e10);
        assertThat(JsonUtils.parse("1e+10")).isEqualTo(1e10);
        assertThat(JsonUtils.parse("1e-10")).isEqualTo(1e-10);
        assertThat(JsonUtils.parse("1.5e3")).isEqualTo(1.5e3);
    }

    @Test
    public void testEdgeCase36NoIntegerPart() {
        // #36: .5 → error (missing integer part)
        assertThatThrownBy(() -> JsonUtils.parse(".5"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase37NoFractionalPart() {
        // #37: 1. → error
        assertThatThrownBy(() -> JsonUtils.parse("1."))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase38NestedDepthLimit() {
        // #38: Deeply nested → error
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            sb.append("[");
        }
        sb.append("1");
        for (int i = 0; i < 600; i++) {
            sb.append("]");
        }
        String deep = sb.toString();
        assertThatThrownBy(() -> JsonUtils.parse(deep))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("depth");
    }

    @Test
    public void testEdgeCase39DuplicateKeys() {
        // #39: Duplicate keys → last value wins
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) JsonUtils.parse("{\"a\": 1, \"a\": 2}");
        assertThat(result).isEqualTo(Map.of("a", 2));
    }

    @Test
    public void testEdgeCase40UnicodeInStrings() {
        // #40: Raw unicode characters
        assertThat(JsonUtils.parse("\"caf\u00e9\"")).isEqualTo("caf\u00e9");
        assertThat(JsonUtils.parse("\"\u4e16\u754c\"")).isEqualTo("\u4e16\u754c");
    }

    @Test
    public void testEdgeCase41ControlCharactersInStrings() {
        // #41: Unescaped control chars → error
        assertThatThrownBy(() -> JsonUtils.parse("\"hello\u0000world\""))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> JsonUtils.parse("\"tab\u0009here\""))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testEdgeCase43NullLiteral() {
        // #43: null literal
        assertThat(JsonUtils.parse("null")).isNull();
    }

    @Test
    public void testEdgeCase44BooleanLiterals() {
        // #44: Boolean literals
        assertThat(JsonUtils.parse("true")).isEqualTo(Boolean.TRUE);
        assertThat(JsonUtils.parse("false")).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void testEdgeCase45IntegerVsLongVsDouble() {
        // #45: Integer if fits, Long if fits, else Double
        assertThat(JsonUtils.parse("42")).isEqualTo(42);
        assertThat(JsonUtils.parse("42")).isInstanceOf(Integer.class);
        assertThat(JsonUtils.parse("2147483647")).isEqualTo(Integer.MAX_VALUE);
        assertThat(JsonUtils.parse("2147483648")).isEqualTo(2147483648L);
        assertThat(JsonUtils.parse("2147483648")).isInstanceOf(Long.class);
        assertThat(JsonUtils.parse("-2147483648")).isEqualTo(Integer.MIN_VALUE);
        assertThat(JsonUtils.parse("-2147483649")).isEqualTo(-2147483649L);
        assertThat(JsonUtils.parse("-2147483649")).isInstanceOf(Long.class);
        assertThat(JsonUtils.parse("9223372036854775807")).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testParseArray() {
        @SuppressWarnings("unchecked")
        ArrayList<Object> result = (ArrayList<Object>) JsonUtils.parse("[1, \"two\", true, null]");
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo("two");
        assertThat(result.get(2)).isEqualTo(true);
        assertThat(result.get(3)).isNull();
    }

    @Test
    public void testParseEmptyArray() {
        assertThat(JsonUtils.parse("[]")).isEqualTo(List.of());
    }

    @Test
    public void testParseEmptyObject() {
        assertThat(JsonUtils.parse("{}")).isEqualTo(Map.of());
    }

    @Test
    public void testParseNegativeNumber() {
        assertThat(JsonUtils.parse("-42")).isEqualTo(-42);
        assertThat(JsonUtils.parse("-3.14")).isEqualTo(-3.14);
    }

    @Test
    public void testParseNestedStructure() {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) JsonUtils.parse(
                "{\"a\": [1, 2], \"b\": {\"c\": true}}");
        assertThat(result.get("a")).isEqualTo(List.of(1, 2));
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> inner = (LinkedHashMap<String, Object>) result.get("b");
        assertThat(inner).isEqualTo(Map.of("c", true));
    }

    @Test
    public void testParseObject() {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) JsonUtils.parse(
                "{\"name\": \"Alice\", \"age\": 30}");
        assertThat(result).isEqualTo(Map.of("name", "Alice", "age", 30));
    }

    @Test
    public void testParseWhitespace() {
        // Leading/trailing whitespace is allowed
        assertThat(JsonUtils.parse("  42  ")).isEqualTo(42);
        assertThat(JsonUtils.parse("\n\t{}\n")).isEqualTo(Map.of());
    }
}
