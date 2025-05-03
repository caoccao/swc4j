/*
 * Copyright (c) 2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SimpleJsonUtils {
    private static final Pattern PATTERN_NUMBER = Pattern.compile("^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

    private static JsonToken extractNextToken(String jsonString, JsonParserContext context) {
        if (context.getOffset() >= jsonString.length()) {
            return JsonToken.END_OF_FILE;
        }
        final char c = jsonString.charAt(context.getOffset());
        final JsonToken jsonToken;
        switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                String whiteSpaces = extractWhiteSpaces(jsonString, context.getOffset());
                context.addOffset(whiteSpaces.length());
                jsonToken = extractNextToken(jsonString, context);
                break;
            case '"':
                jsonToken = JsonToken.fromString(extractString(jsonString, context.getOffset()));
                context.addOffset(jsonToken.getLength() + 2);
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                jsonToken = JsonToken.fromNumber(extractNumber(jsonString, context.getOffset()));
                context.addOffset(jsonToken.getLength());
                break;
            case '{':
                jsonToken = JsonToken.OBJECT_START;
                context.addOffset(jsonToken.getLength());
                break;
            case '}':
                jsonToken = JsonToken.OBJECT_END;
                context.addOffset(jsonToken.getLength());
                break;
            case '[':
                jsonToken = JsonToken.ARRAY_START;
                context.addOffset(jsonToken.getLength());
                break;
            case ']':
                jsonToken = JsonToken.ARRAY_END;
                context.addOffset(jsonToken.getLength());
                break;
            case ',':
                jsonToken = JsonToken.COMMA;
                context.addOffset(jsonToken.getLength());
                break;
            case ':':
                jsonToken = JsonToken.COLON;
                context.addOffset(jsonToken.getLength());
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid JSON string: unexpected char " + c + " at " + context.getOffset());
        }
        return jsonToken;
    }

    private static String extractNumber(String jsonString, int offset) {
        Matcher matcher = PATTERN_NUMBER.matcher(jsonString.substring(offset));
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid JSON string: expect number at " + offset);
        }
        return matcher.group();
    }

    private static String extractString(String jsonString, int offset) {
        // Skip the first char.
        ++offset;
        StringBuilder sb = new StringBuilder();
        final int length = jsonString.length();
        while (offset < length) {
            char c = jsonString.charAt(offset);
            if ('\\' == c) {
                if (offset + 1 < length) {
                    char next = jsonString.charAt(++offset);
                    switch (next) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            if (offset + 4 < length) {
                                String hex = jsonString.substring(offset + 1, offset + 5);
                                try {
                                    int codePoint = Integer.parseInt(hex, 16);
                                    sb.append((char) codePoint);
                                    offset += 4;
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException(
                                            "Invalid JSON string: invalid unicode escape \\u" + hex + " at " + offset);
                                }
                            } else {
                                throw new IllegalArgumentException(
                                        "Invalid JSON string: incomplete unicode escape sequence at end of string at " + offset);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Invalid JSON string: invalid escape sequence \\" + next + " at " + offset);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Invalid JSON string: invalid escape sequence \\ at " + offset);
                }
            } else if ('"' == c) {
                ++offset;
                break;
            } else {
                ++offset;
                sb.append(c);
            }
        }
        if (offset > length) {
            throw new IllegalArgumentException("Invalid JSON string: expect \" at " + offset);
        }
        return sb.toString();
    }

    private static String extractWhiteSpaces(String jsonString, int offset) {
        final int length = jsonString.length();
        StringBuilder sb = new StringBuilder();
        boolean hasMore = true;
        while (hasMore && offset < length) {
            char c = jsonString.charAt(offset);
            ++offset;
            switch (c) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    sb.append(c);
                    break;
                default:
                    hasMore = false;
                    break;
            }
        }
        return sb.toString();
    }

    public static JsonNode parse(String jsonString) {
        return parse(Objects.requireNonNull(jsonString), new JsonParserContext());
    }

    private static JsonNode parse(String jsonString, JsonParserContext context) {
        JsonToken jsonToken = extractNextToken(jsonString, context);
        switch (jsonToken.getType()) {
            case ArrayStart:
                return parseArray(jsonString, context);
            case ObjectStart:
                return parseObject(jsonString, context);
            default:
                throw new IllegalArgumentException(
                        "Invalid JSON string: expect { or [ at " + context.getOffset());
        }
    }

    private static JsonArrayNode parseArray(String jsonString, JsonParserContext context) {
        JsonArrayNode jsonArrayNode = JsonArrayNode.of();
        final int length = jsonString.length();
        while (context.getOffset() < length) {
            JsonToken jsonToken = extractNextToken(jsonString, context);
            if (jsonToken.getType() == JsonTokenType.ArrayEnd) {
                break;
            }
            switch (jsonToken.getType()) {
                case ArrayStart:
                    jsonArrayNode.getNodes().add(parseArray(jsonString, context));
                    break;
                case Number:
                    jsonArrayNode.getNodes().add(JsonNumberNode.of(jsonToken.getRawString()));
                    break;
                case ObjectStart:
                    jsonArrayNode.getNodes().add(parseObject(jsonString, context));
                    break;
                case Text:
                    jsonArrayNode.getNodes().add(JsonTextNode.of(jsonToken.getRawString()));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
            jsonToken = extractNextToken(jsonString, context);
            if (jsonToken.getType() == JsonTokenType.ArrayEnd) {
                break;
            } else if (jsonToken.getType() != JsonTokenType.Comma) {
                throw new IllegalArgumentException(
                        "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
        }
        return jsonArrayNode;
    }

    private static JsonObjectNode parseObject(String jsonString, JsonParserContext context) {
        JsonObjectNode jsonObjectNode = JsonObjectNode.of();
        final int length = jsonString.length();
        while (context.getOffset() < length) {
            JsonToken jsonToken = extractNextToken(jsonString, context);
            if (jsonToken.getType() == JsonTokenType.ObjectEnd) {
                break;
            }
            if (jsonToken.getType() != SimpleJsonUtils.JsonTokenType.Text) {
                throw new IllegalArgumentException(
                        "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
            String propertyName = jsonToken.getRawString();
            jsonToken = extractNextToken(jsonString, context);
            if (jsonToken.getType() != JsonTokenType.Colon) {
                throw new IllegalArgumentException(
                        "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
            jsonToken = extractNextToken(jsonString, context);
            switch (jsonToken.getType()) {
                case ArrayStart:
                    jsonObjectNode.getNodeMap().put(propertyName, parseArray(jsonString, context));
                    break;
                case Number:
                    jsonObjectNode.getNodeMap().put(propertyName, JsonNumberNode.of(jsonToken.getRawString()));
                    break;
                case ObjectStart:
                    jsonObjectNode.getNodeMap().put(propertyName, parseObject(jsonString, context));
                    break;
                case Text:
                    jsonObjectNode.getNodeMap().put(propertyName, JsonTextNode.of(jsonToken.getRawString()));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
            jsonToken = extractNextToken(jsonString, context);
            if (jsonToken.getType() == JsonTokenType.ObjectEnd) {
                break;
            }
            if (jsonToken.getType() != JsonTokenType.Comma) {
                throw new IllegalArgumentException(
                        "Invalid JSON string: unexpected " + jsonToken.getRawString() + " at " + context.getOffset());
            }
        }
        return jsonObjectNode;
    }

    public enum JsonTokenType {
        ArrayEnd,
        ArrayStart,
        Comma,
        Colon,
        EndOfFile,
        Number,
        ObjectEnd,
        ObjectStart,
        Text,
    }

    public interface JsonNode {
        default JsonArrayNode asArray() {
            return isArray() ? (JsonArrayNode) this : null;
        }

        default JsonNumberNode asNumber() {
            return isNumber() ? (JsonNumberNode) this : null;
        }

        default JsonObjectNode asObject() {
            return isObject() ? (JsonObjectNode) this : null;
        }

        default JsonTextNode asText() {
            return isText() ? (JsonTextNode) this : null;
        }

        default boolean isArray() {
            return this instanceof JsonArrayNode;
        }

        default boolean isNumber() {
            return this instanceof JsonNumberNode;
        }

        default boolean isObject() {
            return this instanceof JsonObjectNode;
        }

        default boolean isText() {
            return this instanceof JsonTextNode;
        }
    }

    public static class JsonArrayNode implements JsonNode {
        private final List<JsonNode> nodes;

        private JsonArrayNode() {
            nodes = new ArrayList<>();
        }

        public static JsonArrayNode of() {
            return new JsonArrayNode();
        }

        public static JsonArrayNode of(JsonNode... nodes) {
            JsonArrayNode jsonArrayNode = of();
            Collections.addAll(jsonArrayNode.getNodes(), nodes);
            return jsonArrayNode;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            JsonArrayNode that = (JsonArrayNode) o;
            return Objects.equals(nodes, that.nodes);
        }

        public List<JsonNode> getNodes() {
            return nodes;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(nodes);
        }
    }

    public static class JsonNumberNode implements JsonNode {
        private Double doubleValue;
        private Integer intValue;
        private Long longValue;
        private String value;

        private JsonNumberNode(double doubleValue) {
            this.doubleValue = doubleValue;
            value = Double.toString(doubleValue);
        }

        private JsonNumberNode(int intValue) {
            this.intValue = intValue;
            value = Integer.toString(intValue);
        }

        private JsonNumberNode(long longValue) {
            this.longValue = longValue;
            value = Long.toString(longValue);
        }

        private JsonNumberNode(String value) {
            setValue(value);
        }

        public static JsonNumberNode of(double value) {
            return new JsonNumberNode(value);
        }

        public static JsonNumberNode of(int value) {
            return new JsonNumberNode(value);
        }

        public static JsonNumberNode of(long value) {
            return new JsonNumberNode(value);
        }

        public static JsonNumberNode of(String value) {
            return new JsonNumberNode(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            JsonNumberNode that = (JsonNumberNode) o;
            return Objects.equals(value, that.value);
        }

        public double getDouble() {
            if (isDouble()) {
                return doubleValue;
            }
            if (isInteger()) {
                return intValue.doubleValue();
            }
            if (isLong()) {
                return longValue.doubleValue();
            }
            throw new IllegalArgumentException("Invalid JSON number");
        }

        public int getInteger() {
            if (isDouble()) {
                return doubleValue.intValue();
            }
            if (isInteger()) {
                return intValue;
            }
            if (isLong()) {
                return longValue.intValue();
            }
            throw new IllegalArgumentException("Invalid JSON number");
        }

        public long getLong() {
            if (isDouble()) {
                return doubleValue.longValue();
            }
            if (isInteger()) {
                return intValue.longValue();
            }
            if (isLong()) {
                return longValue;
            }
            throw new IllegalArgumentException("Invalid JSON number");
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        public boolean isDouble() {
            return doubleValue != null;
        }

        public boolean isInteger() {
            return intValue != null;
        }

        public boolean isLong() {
            return longValue != null;
        }

        public void setValue(String value) {
            this.value = Objects.requireNonNull(value);
            if (value.contains(".") || value.contains("e") || value.contains("E")) {
                doubleValue = Double.parseDouble(value);
            } else {
                longValue = Long.parseLong(value);
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    intValue = longValue.intValue();
                    longValue = null;
                }
            }
        }
    }

    public static class JsonObjectNode implements JsonNode {
        private final Map<String, JsonNode> nodeMap;

        private JsonObjectNode() {
            nodeMap = new LinkedHashMap<>();
        }

        public static JsonObjectNode of() {
            return new JsonObjectNode();
        }

        public static JsonObjectNode of(Map<String, JsonNode> nodeMap) {
            JsonObjectNode jsonObjectNode = JsonObjectNode.of();
            jsonObjectNode.getNodeMap().putAll(nodeMap);
            return jsonObjectNode;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            JsonObjectNode that = (JsonObjectNode) o;
            return Objects.equals(nodeMap, that.nodeMap);
        }

        public Map<String, JsonNode> getNodeMap() {
            return nodeMap;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(nodeMap);
        }
    }

    private static class JsonParserContext {
        private int offset;

        public JsonParserContext() {
            offset = 0;
        }

        public void addOffset(int delta) {
            offset += delta;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static class JsonTextNode implements JsonNode {
        private String value;

        private JsonTextNode(String value) {
            setValue(value);
        }

        public static JsonTextNode of() {
            return new JsonTextNode(StringUtils.EMPTY);
        }

        public static JsonTextNode of(String value) {
            return new JsonTextNode(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            JsonTextNode that = (JsonTextNode) o;
            return Objects.equals(value, that.value);
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        public void setValue(String value) {
            this.value = Objects.requireNonNull(value);
        }
    }

    public static final class JsonToken {
        public static final JsonToken ARRAY_END = new JsonToken("]", JsonTokenType.ArrayEnd);
        public static final JsonToken ARRAY_START = new JsonToken("[", JsonTokenType.ArrayStart);
        public static final JsonToken COMMA = new JsonToken(",", JsonTokenType.Comma);
        public static final JsonToken COLON = new JsonToken(":", JsonTokenType.Colon);
        public static final JsonToken END_OF_FILE = new JsonToken("", JsonTokenType.EndOfFile);
        public static final JsonToken OBJECT_END = new JsonToken("}", JsonTokenType.ObjectEnd);
        public static final JsonToken OBJECT_START = new JsonToken("{", JsonTokenType.ObjectStart);
        private final String rawString;
        private final JsonTokenType type;

        private JsonToken(String rawString, JsonTokenType type) {
            this.rawString = rawString;
            this.type = type;
        }

        public static JsonToken fromNumber(String rawString) {
            return new JsonToken(Objects.requireNonNull(rawString), JsonTokenType.Number);
        }

        public static JsonToken fromString(String rawString) {
            return new JsonToken(Objects.requireNonNull(rawString), JsonTokenType.Text);
        }

        public int getLength() {
            return rawString.length();
        }

        public String getRawString() {
            return rawString;
        }

        public JsonTokenType getType() {
            return type;
        }
    }
}
