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

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestSourceMapUtils extends BaseTestSuite {
    @Test
    public void testEmptySegments() {
        // Test that empty segments (between commas) are properly skipped
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["content"],
                  "names": [],
                  "mappings": "AAAA,,CACA"
                }
                """;
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);
        SourceMapUtils.SourceNode node = sourceMapUtils.getNode(0, 0);
        assertNotNull(node);
    }

    @Test
    public void testFiveFieldSegmentsWithNameIndex() {
        // Test segments with 5 fields (including name index)
        // Mappings format: AAAAC = [0,0,0,0,1] where the last field is name index delta
        // Starting from nameIndex=-1, delta=1 makes it 0 (pointing to "myName")
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["original"],
                  "names": ["myName"],
                  "mappings": "AAAAC"
                }
                """;
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);
        SourceMapUtils.SourceNode node = sourceMapUtils.getNode(0, 0);

        assertNotNull(node);
        assertEquals(0, node.sourceFileIndex);
        assertEquals(0, node.nameIndex);
        assertEquals(0, node.originalPosition.line);
        assertEquals(0, node.originalPosition.column);
    }

    @Test
    public void testNameIndexOutOfBounds() {
        // Test that invalid nameIndex throws exception
        // Mappings: "AAAAE" = [0, 0, 0, 0, 2] where field 4 is name index delta
        // Starting from nameIndex=-1, delta=2 makes it 1, which is out of bounds (only have name at index 0)
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["content"],
                  "names": ["name1"],
                  "mappings": "AAAAE"
                }
                """;

        assertThrows(IllegalArgumentException.class, () -> {
            SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);
            sourceMapUtils.getNode(0, 0); // This should trigger parsing and validation
        });
    }

    @Test
    public void testNullPreservationInArrays() {
        // Test that null values in sources, sourcesContent, and names are preserved
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["file1.js", null, "file3.js"],
                  "sourcesContent": ["content1", null, "content3"],
                  "names": ["name1", null, "name3"],
                  "mappings": "AAAA"
                }
                """;
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);

        // Verify arrays preserve nulls and maintain correct indices
        assertEquals(3, sourceMapUtils.getSourceFilePaths().size());
        assertEquals("file1.js", sourceMapUtils.getSourceFilePaths().get(0));
        assertNull(sourceMapUtils.getSourceFilePaths().get(1));
        assertEquals("file3.js", sourceMapUtils.getSourceFilePaths().get(2));

        assertEquals(3, sourceMapUtils.getSourceContents().size());
        assertEquals("content1", sourceMapUtils.getSourceContents().get(0));
        assertNull(sourceMapUtils.getSourceContents().get(1));
        assertEquals("content3", sourceMapUtils.getSourceContents().get(2));

        assertEquals(3, sourceMapUtils.getNames().size());
        assertEquals("name1", sourceMapUtils.getNames().get(0));
        assertNull(sourceMapUtils.getNames().get(1));
        assertEquals("name3", sourceMapUtils.getNames().get(2));
    }

    @Test
    public void testOneFieldSegments() {
        // Test that 1-field segments are handled (they update column but don't create mappings)
        // "A" = [0] (1 field), "C" = [1] (1 field)
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["content"],
                  "names": [],
                  "mappings": "A,C,AAAA"
                }
                """;
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);

        // The 1-field segments should not create source nodes
        // Only the 4-field segment "AAAA" should create a node
        SourceMapUtils.SourceNode node = sourceMapUtils.getNode(0, 2);
        assertNotNull(node);
        assertEquals(0, node.sourceFileIndex);
    }

    @Test
    public void testParsingMultipleLines() {
        // Test that the parser correctly handles requests for lines beyond initial parse
        // Multiple lines separated by semicolons
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["line1\\nline2\\nline3"],
                  "names": [],
                  "mappings": "AAAA;AACA;AACA"
                }
                """;
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);

        // Request line 2 - should parse up to that line
        SourceMapUtils.SourceNode node = sourceMapUtils.getNode(2, 0);
        assertNotNull(node);
        assertEquals(2, node.generatedPosition.line);
        assertEquals(2, node.originalPosition.line);
    }

    @Test
    public void testSourceFileIndexOutOfBounds() {
        // Test that invalid sourceFileIndex throws exception
        // Mappings: "ACAA" = [0, 1, 0, 0] where field 1 is source index delta
        // Starting from sourceFileIndex=0, delta=1 makes it 1, which is out of bounds (only have index 0)
        String sourceMapJson = """
                {
                  "version": 3,
                  "sources": ["test.js"],
                  "sourcesContent": ["content"],
                  "names": [],
                  "mappings": "ACAA"
                }
                """;

        assertThrows(IllegalArgumentException.class, () -> {
            SourceMapUtils sourceMapUtils = SourceMapUtils.of(sourceMapJson);
            sourceMapUtils.getNode(0, 0); // This should trigger parsing and validation
        });
    }

    @Test
    public void testTransform() throws Swc4jCoreException {
        String code = "function add(a:number, b:number)\n{ return a+b; }";
        String expectedCode = """
                function add(a: number, b: number) {
                  return a + b;
                }
                """;
        Swc4jTransformOutput output = swc4j.transform(code, tsModuleTransformOptions
                .setMinify(false)
                .setSourceMap(Swc4jSourceMapOption.Separate));
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(Swc4jMediaType.TypeScript, output.getMediaType());
        assertEquals(expectedCode, output.getCode());
        assertNotNull(output.getSourceMap());
        SourceMapUtils sourceMapUtils = SourceMapUtils.of(output.getSourceMap());
        assertNotNull(sourceMapUtils);
        assertEquals(
                SimpleList.of("file:///main.js"),
                sourceMapUtils.getSourceFilePaths());
        assertEquals(
                SimpleList.of("function add(a:number, b:number)\n{ return a+b; }"),
                sourceMapUtils.getSourceContents());
        assertEquals(SimpleList.of(), sourceMapUtils.getNames());
        assertEquals(
                "AAAA,SAAS,IAAI,GAAE,MAAM,EAAE,GAAE,MAAM;EAC7B,OAAO,IAAE;AAAG",
                sourceMapUtils.getMappings());
        SimpleMap.of(0, 0, -1, 0, 0, -1, -1, -1)
                .forEach((line, column) -> assertNull(sourceMapUtils.getNode(line, column)));
        Map<SourceMapUtils.SourceNode, SourceMapUtils.SourceNode> testCaseMap = new LinkedHashMap<>();
        testCaseMap.put(sourceMapUtils.getNode(SourceMapUtils.SourcePosition.of()), SourceMapUtils.SourceNode.of());
        testCaseMap.put(sourceMapUtils.getNode(SourceMapUtils.SourcePosition.of(0, 1)),
                SourceMapUtils.SourceNode.of(
                        SourceMapUtils.SourcePosition.of(0, 1),
                        SourceMapUtils.SourcePosition.of(0, 1)));
        testCaseMap.put(sourceMapUtils.getNode(0, 31),
                SourceMapUtils.SourceNode.of(
                        SourceMapUtils.SourcePosition.of(0, 29),
                        SourceMapUtils.SourcePosition.of(0, 31)));
        testCaseMap.put(sourceMapUtils.getNode(1, 13),
                SourceMapUtils.SourceNode.of(
                        SourceMapUtils.SourcePosition.of(1, 11),
                        SourceMapUtils.SourcePosition.of(1, 13)));
        testCaseMap.put(sourceMapUtils.getNode(1, 2),
                SourceMapUtils.SourceNode.of(
                        SourceMapUtils.SourcePosition.of(1, 2),
                        SourceMapUtils.SourcePosition.of(1, 2)));
        testCaseMap.forEach((node, expectedNode) ->
                assertEquals(expectedNode, node, "Expected " + expectedNode + " doesn't match " + node));
    }
}
