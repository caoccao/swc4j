/*
 * Copyright (c) 2025-2026. caoccao.com Sam Cao
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
import static org.assertj.core.api.Assertions.assertThat;


public class TestSimpleJsonUtils {
    @Test
    public void testArrayWithBoolean() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of(
                SimpleJsonUtils.JsonBooleanNode.of(true),
                SimpleJsonUtils.JsonBooleanNode.of(false));
        assertThat(SimpleJsonUtils.parse("[true,false]")).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testBoolean() {
        Stream.of(true, false).forEach(b -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonBooleanNode.of(b);
            assertThat(SimpleJsonUtils.parse(Boolean.toString(b))).isEqualTo(expectedJsonNode);
            assertThat(SimpleJsonUtils.parse("  " + b + "  ")).isEqualTo(expectedJsonNode);
        });
    }

    @Test
    public void testEmptyArray() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of();
        assertThat(SimpleJsonUtils.parse("[]")).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testEmptyArrayWithWhiteSpaces() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonArrayNode.of();
        assertThat(SimpleJsonUtils.parse(" \t [ \n\r ] ")).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testEmptyObject() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        assertThat(SimpleJsonUtils.parse("{}")).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testEmptyObjectWithWhiteSpaces() {
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        assertThat(SimpleJsonUtils.parse(" \t { \n\r } ")).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testNull() {
        // Test null value
        SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNullNode.of();
        assertThat(SimpleJsonUtils.parse("null")).isEqualTo(expectedJsonNode);
        assertThat(SimpleJsonUtils.parse("  null  ")).isEqualTo(expectedJsonNode);

        // Test null in object
        SimpleJsonUtils.JsonObjectNode expectedObjectNode = SimpleJsonUtils.JsonObjectNode.of();
        expectedObjectNode.getNodeMap().put("value", SimpleJsonUtils.JsonNullNode.of());
        assertThat(SimpleJsonUtils.parse("{\"value\": null}")).isEqualTo(expectedObjectNode);

        // Test null in array
        SimpleJsonUtils.JsonArrayNode expectedArrayNode = SimpleJsonUtils.JsonArrayNode.of(
                SimpleJsonUtils.JsonNumberNode.of(1),
                SimpleJsonUtils.JsonNullNode.of(),
                SimpleJsonUtils.JsonTextNode.of("test"));
        assertThat(SimpleJsonUtils.parse("[1, null, \"test\"]")).isEqualTo(expectedArrayNode);

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
        assertThat(SimpleJsonUtils.parse("{\"a\": null, \"b\": [null, true, null], \"c\": {\"d\": null}}")).isEqualTo(expectedMixedNode);
    }

    @Test
    public void testNumber() {
        IntStream.of(123, -123, Integer.MAX_VALUE, Integer.MIN_VALUE, 0).forEach(i -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(i);
            assertThat(SimpleJsonUtils.parse(Integer.toString(i))).isEqualTo(expectedJsonNode);
            assertThat(SimpleJsonUtils.parse("  " + i + "  ")).isEqualTo(expectedJsonNode);
        });
        LongStream.of(Long.MAX_VALUE / 2, Long.MIN_VALUE / 2, Long.MAX_VALUE, Long.MIN_VALUE).forEach(l -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(l);
            assertThat(SimpleJsonUtils.parse(Long.toString(l))).isEqualTo(expectedJsonNode);
            assertThat(SimpleJsonUtils.parse("  " + l + "  ")).isEqualTo(expectedJsonNode);
        });
        DoubleStream.of(Double.MAX_VALUE / 2, Double.MIN_VALUE / 2, Double.MAX_VALUE, Double.MIN_VALUE).forEach(d -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonNumberNode.of(d);
            assertThat(SimpleJsonUtils.parse(Double.toString(d))).isEqualTo(expectedJsonNode);
            assertThat(SimpleJsonUtils.parse("  " + d + "  ")).isEqualTo(expectedJsonNode);
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
        assertThat(jsonNode).isEqualTo(expectedJsonNode);
    }

    @Test
    public void testText() {
        Stream.of("abc", "abc def", "\b\f\\\r\n\t\"").forEach(s -> {
            SimpleJsonUtils.JsonNode expectedJsonNode = SimpleJsonUtils.JsonTextNode.of(s);
            assertThat(SimpleJsonUtils.parse("\"" + SimpleJsonUtils.escape(s) + "\"")).isEqualTo(expectedJsonNode);
            assertThat(SimpleJsonUtils.parse("  \"" + SimpleJsonUtils.escape(s) + "\"  ")).isEqualTo(expectedJsonNode);
        });
    }
}
