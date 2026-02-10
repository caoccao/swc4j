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

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Default hand-rolled JSON provider implementation.
 * Serializes and parses JSON without external library dependencies.
 */
public final class DefaultJsonProvider implements JsonProvider {
    /**
     * Singleton instance.
     */
    public static final DefaultJsonProvider INSTANCE = new DefaultJsonProvider();

    private static final int MAX_DEPTH = 512;

    private DefaultJsonProvider() {
    }

    private static char expect(String json, int[] pos, char expected) {
        if (pos[0] >= json.length() || json.charAt(pos[0]) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at position " + pos[0]);
        }
        pos[0]++;
        return expected;
    }

    private static void expectLiteral(String json, int[] pos, String literal) {
        for (int i = 0; i < literal.length(); i++) {
            if (pos[0] >= json.length() || json.charAt(pos[0]) != literal.charAt(i)) {
                throw new RuntimeException("Unexpected token at position " + pos[0]);
            }
            pos[0]++;
        }
    }

    private static ArrayList<Object> parseArray(String json, int[] pos, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Maximum nesting depth exceeded");
        }
        expect(json, pos, '[');
        skipWhitespace(json, pos);
        ArrayList<Object> list = new ArrayList<>();
        if (pos[0] < json.length() && json.charAt(pos[0]) == ']') {
            pos[0]++;
            return list;
        }
        while (true) {
            Object value = parseValue(json, pos, depth + 1);
            list.add(value);
            skipWhitespace(json, pos);
            if (pos[0] >= json.length()) {
                throw new RuntimeException("Unexpected end of JSON input");
            }
            char c = json.charAt(pos[0]);
            if (c == ']') {
                pos[0]++;
                return list;
            }
            if (c != ',') {
                throw new RuntimeException("Expected ',' or ']' at position " + pos[0]);
            }
            pos[0]++;
            skipWhitespace(json, pos);
            if (pos[0] < json.length() && json.charAt(pos[0]) == ']') {
                throw new RuntimeException("Trailing comma in array at position " + pos[0]);
            }
        }
    }

    private static Number parseNumber(String json, int[] pos) {
        int start = pos[0];
        boolean hasDecimal = false;
        boolean hasExponent = false;

        if (pos[0] < json.length() && json.charAt(pos[0]) == '-') {
            pos[0]++;
        }
        if (pos[0] >= json.length()) {
            throw new RuntimeException("Unexpected end of JSON input");
        }
        if (json.charAt(pos[0]) == '0') {
            pos[0]++;
            if (pos[0] < json.length()) {
                char next = json.charAt(pos[0]);
                if (next >= '0' && next <= '9') {
                    throw new RuntimeException("Leading zeros not allowed at position " + start);
                }
            }
        } else if (json.charAt(pos[0]) >= '1' && json.charAt(pos[0]) <= '9') {
            pos[0]++;
            while (pos[0] < json.length() && json.charAt(pos[0]) >= '0' && json.charAt(pos[0]) <= '9') {
                pos[0]++;
            }
        } else {
            throw new RuntimeException("Invalid number at position " + start);
        }

        if (pos[0] < json.length() && json.charAt(pos[0]) == '.') {
            hasDecimal = true;
            pos[0]++;
            if (pos[0] >= json.length() || json.charAt(pos[0]) < '0' || json.charAt(pos[0]) > '9') {
                throw new RuntimeException("Invalid number: missing fractional digits at position " + pos[0]);
            }
            while (pos[0] < json.length() && json.charAt(pos[0]) >= '0' && json.charAt(pos[0]) <= '9') {
                pos[0]++;
            }
        }

        if (pos[0] < json.length() && (json.charAt(pos[0]) == 'e' || json.charAt(pos[0]) == 'E')) {
            hasExponent = true;
            pos[0]++;
            if (pos[0] < json.length() && (json.charAt(pos[0]) == '+' || json.charAt(pos[0]) == '-')) {
                pos[0]++;
            }
            if (pos[0] >= json.length() || json.charAt(pos[0]) < '0' || json.charAt(pos[0]) > '9') {
                throw new RuntimeException("Invalid number: missing exponent digits at position " + pos[0]);
            }
            while (pos[0] < json.length() && json.charAt(pos[0]) >= '0' && json.charAt(pos[0]) <= '9') {
                pos[0]++;
            }
        }

        String numStr = json.substring(start, pos[0]);

        if (hasDecimal || hasExponent) {
            return Double.parseDouble(numStr);
        }

        // Handle -0 special case: must return Double(-0.0)
        if ("-0".equals(numStr)) {
            return -0.0;
        }

        try {
            int intVal = Integer.parseInt(numStr);
            return intVal;
        } catch (NumberFormatException e) {
            // falls through
        }
        try {
            long longVal = Long.parseLong(numStr);
            return longVal;
        } catch (NumberFormatException e) {
            // falls through
        }
        return Double.parseDouble(numStr);
    }

    private static LinkedHashMap<String, Object> parseObject(String json, int[] pos, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Maximum nesting depth exceeded");
        }
        expect(json, pos, '{');
        skipWhitespace(json, pos);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (pos[0] < json.length() && json.charAt(pos[0]) == '}') {
            pos[0]++;
            return map;
        }
        while (true) {
            skipWhitespace(json, pos);
            if (pos[0] >= json.length()) {
                throw new RuntimeException("Unexpected end of JSON input");
            }
            if (json.charAt(pos[0]) != '"') {
                throw new RuntimeException("Expected '\"' for object key at position " + pos[0]);
            }
            String key = parseString(json, pos);
            skipWhitespace(json, pos);
            expect(json, pos, ':');
            Object value = parseValue(json, pos, depth + 1);
            map.put(key, value);
            skipWhitespace(json, pos);
            if (pos[0] >= json.length()) {
                throw new RuntimeException("Unexpected end of JSON input");
            }
            char c = json.charAt(pos[0]);
            if (c == '}') {
                pos[0]++;
                return map;
            }
            if (c != ',') {
                throw new RuntimeException("Expected ',' or '}' at position " + pos[0]);
            }
            pos[0]++;
            skipWhitespace(json, pos);
            if (pos[0] < json.length() && json.charAt(pos[0]) == '}') {
                throw new RuntimeException("Trailing comma in object at position " + pos[0]);
            }
        }
    }

    private static String parseString(String json, int[] pos) {
        expect(json, pos, '"');
        StringBuilder sb = new StringBuilder();
        while (pos[0] < json.length()) {
            char c = json.charAt(pos[0]);
            if (c == '"') {
                pos[0]++;
                return sb.toString();
            }
            if (c < 0x20) {
                throw new RuntimeException("Unescaped control character at position " + pos[0]);
            }
            if (c == '\\') {
                pos[0]++;
                if (pos[0] >= json.length()) {
                    throw new RuntimeException("Unexpected end of JSON input");
                }
                char escaped = json.charAt(pos[0]);
                switch (escaped) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        pos[0]++;
                        if (pos[0] + 4 > json.length()) {
                            throw new RuntimeException("Incomplete unicode escape at position " + pos[0]);
                        }
                        String hex = json.substring(pos[0], pos[0] + 4);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid unicode escape at position " + pos[0]);
                        }
                        pos[0] += 3; // +1 will happen below
                    }
                    default ->
                            throw new RuntimeException("Invalid escape sequence '\\" + escaped + "' at position " + (pos[0] - 1));
                }
            } else {
                sb.append(c);
            }
            pos[0]++;
        }
        throw new RuntimeException("Unterminated string at position " + pos[0]);
    }

    private static Object parseValue(String json, int[] pos, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Maximum nesting depth exceeded");
        }
        skipWhitespace(json, pos);
        if (pos[0] >= json.length()) {
            throw new RuntimeException("Unexpected end of JSON input");
        }
        char c = json.charAt(pos[0]);
        return switch (c) {
            case '{' -> parseObject(json, pos, depth);
            case '[' -> parseArray(json, pos, depth);
            case '"' -> parseString(json, pos);
            case 't' -> {
                expectLiteral(json, pos, "true");
                yield Boolean.TRUE;
            }
            case 'f' -> {
                expectLiteral(json, pos, "false");
                yield Boolean.FALSE;
            }
            case 'n' -> {
                expectLiteral(json, pos, "null");
                yield null;
            }
            default -> {
                if (c == '-' || (c >= '0' && c <= '9')) {
                    yield parseNumber(json, pos);
                }
                throw new RuntimeException("Unexpected character '" + c + "' at position " + pos[0]);
            }
        };
    }

    private static String resolveIndent(Object space) {
        if (space == null) {
            return null;
        }
        if (space instanceof Number number) {
            int count = Math.max(0, Math.min(10, number.intValue()));
            if (count == 0) {
                return null;
            }
            return " ".repeat(count);
        }
        if (space instanceof String str) {
            if (str.isEmpty()) {
                return null;
            }
            return str.length() > 10 ? str.substring(0, 10) : str;
        }
        return null;
    }

    private static Set<String> resolveReplacer(Object replacer) {
        if (replacer instanceof ArrayList<?> list) {
            Set<String> set = new HashSet<>();
            for (Object item : list) {
                if (item != null) {
                    set.add(String.valueOf(item));
                }
            }
            return set;
        }
        return null;
    }

    private static void skipWhitespace(String json, int[] pos) {
        while (pos[0] < json.length()) {
            char c = json.charAt(pos[0]);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                break;
            }
            pos[0]++;
        }
    }

    @SuppressWarnings("unchecked")
    private static void writeArray(
            StringBuilder sb, ArrayList<?> list, String indent, String currentIndent,
            Object resolvedReplacer, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Maximum nesting depth exceeded");
        }
        if (list.isEmpty()) {
            sb.append("[]");
            return;
        }
        visited.put(list, Boolean.TRUE);
        sb.append('[');
        String childIndent = indent != null ? currentIndent + indent : null;
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            if (childIndent != null) {
                sb.append('\n').append(childIndent);
            }
            Object element = list.get(i);
            if (resolvedReplacer instanceof BiFunction<?, ?, ?>) {
                element = ((BiFunction<String, Object, Object>) resolvedReplacer).apply(String.valueOf(i), element);
                if (element == JsonUtils.UNDEFINED) {
                    sb.append("null");
                    continue;
                }
            }
            writeValue(sb, element, indent, childIndent, resolvedReplacer, visited, depth + 1);
        }
        if (indent != null) {
            sb.append('\n').append(currentIndent);
        }
        sb.append(']');
        visited.remove(list);
    }

    @SuppressWarnings("unchecked")
    private static void writeObject(
            StringBuilder sb, LinkedHashMap<?, ?> map, String indent, String currentIndent,
            Object resolvedReplacer, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("Maximum nesting depth exceeded");
        }
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        visited.put(map, Boolean.TRUE);
        sb.append('{');
        String childIndent = indent != null ? currentIndent + indent : null;
        boolean first = true;
        if (resolvedReplacer instanceof Set<?> replacerSet) {
            for (Object keyObj : replacerSet) {
                String key = (String) keyObj;
                if (!map.containsKey(key)) {
                    continue;
                }
                Object value = map.get(key);
                if (!first) {
                    sb.append(',');
                }
                if (childIndent != null) {
                    sb.append('\n').append(childIndent);
                }
                writeString(sb, key);
                sb.append(':');
                if (indent != null) {
                    sb.append(' ');
                }
                writeValue(sb, value, indent, childIndent, resolvedReplacer, visited, depth + 1);
                first = false;
            }
        } else if (resolvedReplacer instanceof BiFunction<?, ?, ?> replacerFn) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                Object value = ((BiFunction<String, Object, Object>) replacerFn).apply(key, entry.getValue());
                if (value == JsonUtils.UNDEFINED) {
                    continue;
                }
                if (!first) {
                    sb.append(',');
                }
                if (childIndent != null) {
                    sb.append('\n').append(childIndent);
                }
                writeString(sb, key);
                sb.append(':');
                if (indent != null) {
                    sb.append(' ');
                }
                writeValue(sb, value, indent, childIndent, resolvedReplacer, visited, depth + 1);
                first = false;
            }
        } else {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                if (childIndent != null) {
                    sb.append('\n').append(childIndent);
                }
                writeString(sb, String.valueOf(entry.getKey()));
                sb.append(':');
                if (indent != null) {
                    sb.append(' ');
                }
                writeValue(sb, entry.getValue(), indent, childIndent, resolvedReplacer, visited, depth + 1);
                first = false;
            }
        }
        if (indent != null) {
            sb.append('\n').append(currentIndent);
        }
        sb.append('}');
        visited.remove(map);
    }

    private static void writeString(StringBuilder sb, String str) {
        sb.append('"');
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append("\\u");
                        sb.append(String.format("%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    private static void writeValue(
            StringBuilder sb, Object value, String indent, String currentIndent,
            Object resolvedReplacer, IdentityHashMap<Object, Boolean> visited, int depth) {
        if (value == null) {
            sb.append("null");
            return;
        }
        if (value instanceof String str) {
            writeString(sb, str);
            return;
        }
        if (value instanceof Boolean bool) {
            sb.append(bool ? "true" : "false");
            return;
        }
        if (value instanceof Integer intVal) {
            sb.append(intVal.intValue());
            return;
        }
        if (value instanceof Long longVal) {
            sb.append(longVal.longValue());
            return;
        }
        if (value instanceof Double doubleVal) {
            double d = doubleVal;
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                sb.append("null");
                return;
            }
            if (d == 0.0 && (Double.doubleToRawLongBits(d) & 0x8000000000000000L) != 0) {
                sb.append("0");
                return;
            }
            // Use a representation that avoids trailing zeros for whole numbers
            if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15) {
                long asLong = (long) d;
                sb.append(asLong);
            } else {
                sb.append(d);
            }
            return;
        }
        if (value instanceof Float floatVal) {
            float f = floatVal;
            if (Float.isNaN(f) || Float.isInfinite(f)) {
                sb.append("null");
                return;
            }
            if (f == 0.0f && (Float.floatToRawIntBits(f) & 0x80000000) != 0) {
                sb.append("0");
                return;
            }
            if (f == Math.floor(f) && !Float.isInfinite(f) && Math.abs(f) < 1e7) {
                int asInt = (int) f;
                sb.append(asInt);
            } else {
                sb.append(f);
            }
            return;
        }
        if (value instanceof Short shortVal) {
            sb.append(shortVal.shortValue());
            return;
        }
        if (value instanceof Byte byteVal) {
            sb.append(byteVal.byteValue());
            return;
        }
        if (value instanceof Character charVal) {
            writeString(sb, String.valueOf(charVal));
            return;
        }
        if (value instanceof BigInteger bigInt) {
            sb.append(bigInt);
            return;
        }
        if (value instanceof LinkedHashMap<?, ?> map) {
            if (visited.containsKey(map)) {
                throw new RuntimeException("Converting circular structure to JSON");
            }
            writeObject(sb, map, indent, currentIndent, resolvedReplacer, visited, depth);
            return;
        }
        if (value instanceof ArrayList<?> list) {
            if (visited.containsKey(list)) {
                throw new RuntimeException("Converting circular structure to JSON");
            }
            writeArray(sb, list, indent, currentIndent, resolvedReplacer, visited, depth);
            return;
        }
        sb.append("{}");
    }

    @Override
    public Object parse(String json) {
        if (json == null || json.isEmpty()) {
            throw new RuntimeException("Unexpected end of JSON input");
        }
        int[] pos = {0};
        Object result = parseValue(json, pos, 0);
        skipWhitespace(json, pos);
        if (pos[0] < json.length()) {
            throw new RuntimeException("Unexpected token at position " + pos[0]);
        }
        return result;
    }

    @Override
    public String stringify(Object value) {
        return stringify(value, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String stringify(Object value, Object replacer, Object space) {
        String indent = resolveIndent(space);
        Object resolvedReplacer;
        if (replacer instanceof BiFunction<?, ?, ?>) {
            // Function replacer: call with root key "" first
            BiFunction<String, Object, Object> fn = (BiFunction<String, Object, Object>) replacer;
            value = fn.apply("", value);
            if (value == JsonUtils.UNDEFINED) {
                return "undefined";
            }
            resolvedReplacer = replacer;
        } else {
            resolvedReplacer = resolveReplacer(replacer);
        }
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value, indent, "", resolvedReplacer, visited, 0);
        return sb.toString();
    }
}
