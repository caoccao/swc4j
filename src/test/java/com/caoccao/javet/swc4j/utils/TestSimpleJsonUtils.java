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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimpleJsonUtils {
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
    public void testSourceMap() {
        SimpleJsonUtils.JsonObjectNode expectedJsonNode = SimpleJsonUtils.JsonObjectNode.of();
        expectedJsonNode.getNodeMap().put("version", SimpleJsonUtils.JsonNumberNode.of("3"));
        expectedJsonNode.getNodeMap().put("sources", SimpleJsonUtils.JsonArrayNode.of(SimpleJsonUtils.JsonTextNode.of("file:///main.js")));
        expectedJsonNode.getNodeMap().put("sourcesContent", SimpleJsonUtils.JsonArrayNode.of(SimpleJsonUtils.JsonTextNode.of("function add(a:number, b:number)\n{ return a+b; }")));
        expectedJsonNode.getNodeMap().put("names", SimpleJsonUtils.JsonArrayNode.of());
        expectedJsonNode.getNodeMap().put("mappings", SimpleJsonUtils.JsonTextNode.of("AAAA,SAAS,IAAI,GAAE,MAAM,EAAE,GAAE,MAAM;EAC7B,OAAO,IAAE;AAAG"));
        SimpleJsonUtils.JsonNode jsonNode = SimpleJsonUtils.parse("{\n" +
                "  \"version\" : 3,\n" +
                "  \"sources\" : [ \"file:///main.js\" ],\n" +
                "  \"sourcesContent\" : [ \"function add(a:number, b:number)\n{ return a+b; }\" ],\n" +
                "  \"names\" : [ ],\n" +
                "  \"mappings\" : \"AAAA,SAAS,IAAI,GAAE,MAAM,EAAE,GAAE,MAAM;EAC7B,OAAO,IAAE;AAAG\"\n" +
                "}");
        assertEquals(expectedJsonNode, jsonNode);
    }
}
