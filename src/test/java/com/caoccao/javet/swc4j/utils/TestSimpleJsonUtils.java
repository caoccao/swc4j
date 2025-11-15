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

import org.junit.jupiter.api.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimpleJsonUtils {
    @Test
    public void testArrayWithBoolean() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of(
                SimpleJsonUtils.JsonBooleanNode.of(true),
                SimpleJsonUtils.JsonBooleanNode.of(false));
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse("[true,false]"));
    }

    @Test
    public void testBoolean() {
        Stream.of(true, false).forEach(b -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonBooleanNode.of(b);
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse(Boolean.toString(b)));
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  " + b + "  "));
        });
    }

    @Test
    public void testEmptyArray() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of();
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse("[]"));
    }

    @Test
    public void testEmptyArrayWithWhiteSpaces() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of();
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse(" \t [ \n\r ] "));
    }

    @Test
    public void testEmptyObject() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse("{}"));
    }

    @Test
    public void testEmptyObjectWithWhiteSpaces() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse(" \t { \n\r } "));
    }

    @Test
    public void testNull() {
        // Test null value
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNullNode.of();
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse("null"));
        assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  null  "));

        // Test null in object
        SimpleJsonUtils.JsonObjectNode expectedObjectNode = SimpleJsonUtils.JsonObjectNode.of();
        expectedObjectNode.getNodeMap().put("value", SimpleJsonUtils.JsonNullNode.of());
        assertEquals(expectedObjectNode, SimpleJsonUtils.parse("{\"value\": null}"));

        // Test null in array
        SimpleJsonUtils.JsonArrayNode expectedArrayNode = SimpleJsonUtils.JsonArrayNode.of(
                SimpleJsonUtils.JsonNumberNode.of(1),
                SimpleJsonUtils.JsonNullNode.of(),
                SimpleJsonUtils.JsonTextNode.of("test"));
        assertEquals(expectedArrayNode, SimpleJsonUtils.parse("[1, null, \"test\"]"));

        // Test mixed values
        SimpleJsonUtils.JsonObjectNode expectedMixedNode = SimpleJsonUtils.JsonObjectNode.of();
        expectedMixedNode.getNodeMap().put("a", SimpleJsonUtils.JsonNullNode.of());
        SimpleJsonUtils.JsonArrayNode nestedArray = SimpleJsonUtils.JsonArrayNode.of(
                SimpleJsonUtils.JsonNullNode.of(),
                SimpleJsonUtils.JsonBooleanNode.of(true),
                SimpleJsonUtils.JsonNullNode.of());
        expectedMixedNode.getNodeMap().put("b", nestedArray);
        SimpleJsonUtils.JsonObjectNode nestedObject = SimpleJsonUtils.JsonObjectNode.of();
        nestedObject.getNodeMap().put("d", SimpleJsonUtils.JsonNullNode.of());
        expectedMixedNode.getNodeMap().put("c", nestedObject);
        assertEquals(expectedMixedNode, SimpleJsonUtils.parse("{\"a\": null, \"b\": [null, true, null], \"c\": {\"d\": null}}"));
    }

    @Test
    public void testNumber() {
        IntStream.of(123, -123, Integer.MAX_VALUE, Integer.MIN_VALUE, 0).forEach(i -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(i);
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse(Integer.toString(i)));
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  " + i + "  "));
        });
        LongStream.of(Long.MAX_VALUE / 2, Long.MIN_VALUE / 2, Long.MAX_VALUE, Long.MIN_VALUE).forEach(l -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(l);
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse(Long.toString(l)));
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  " + l + "  "));
        });
        DoubleStream.of(Double.MAX_VALUE / 2, Double.MIN_VALUE / 2, Double.MAX_VALUE, Double.MIN_VALUE).forEach(d -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(d);
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse(Double.toString(d)));
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  " + d + "  "));
        });
    }

    @Test
    public void testSourceMap() {
        SimpleJsonUtils.JsonObjectNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        expectedJsonNode.getNodeMap().put("version", SimpleJsonUtils.JsonNumberNode.of("3"));
        expectedJsonNode.getNodeMap().put("sources", SimpleJsonUtils.JsonArrayNode.of(SimpleJsonUtils.JsonTextNode.of("file:///main.js")));
        expectedJsonNode.getNodeMap().put("sourcesContent", SimpleJsonUtils.JsonArrayNode.of(SimpleJsonUtils.JsonTextNode.of("function add(a:number, b:number)\n{ return a+b; }")));
        expectedJsonNode.getNodeMap().put("names", SimpleJsonUtils.JsonArrayNode.of());
        expectedJsonNode.getNodeMap().put("mappings", SimpleJsonUtils.JsonTextNode.of("AAAA,SAAS,IAAI,GAAE,MAAM,EAAE,GAAE,MAAM;EAC7B,OAAO,IAAE;AAAG"));
        SimpleJsonUtils.JsonNode jsonNode = SimpleJsonUtils.parse("""
                {
                  "version" : 3,
                  "sources" : [ "file:///main.js" ],
                  "sourcesContent" : [ "function add(a:number, b:number)
                { return a+b; }" ],
                  "names" : [ ],
                  "mappings" : "AAAA,SAAS,IAAI,GAAE,MAAM,EAAE,GAAE,MAAM;EAC7B,OAAO,IAAE;AAAG"
                }""");
        assertEquals(expectedJsonNode, jsonNode);
    }

    @Test
    public void testText() {
        Stream.of("abc", "abc def", "\b\f\\\r\n\t\"").forEach(s -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonTextNode.of(s);
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("\"" + SimpleJsonUtils.escape(s) + "\""));
            assertEquals(expectedJsonNode, SimpleJsonUtils.parse("  \"" + SimpleJsonUtils.escape(s) + "\"  "));
        });
    }
}
