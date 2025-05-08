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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SourceMapUtils {
    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final int[] BASE64_DECODE_TABLE = new int[123];
    private static final String MAPPINGS = "mappings";
    private static final String NAMES = "names";
    private static final String SOURCES = "sources";
    private static final String SOURCES_CONTENT = "sourcesContent";
    private static final Set<String> SUPPORTED_VERSION_SET = SimpleSet.of("3");
    private static final String VERSION = "version";
    private static final int VLQ_SHIFT = 5;
    private static final int VLQ_CONTINUATION_BIT = 1 << VLQ_SHIFT;
    private static final int VLQ_VALUE_MASK = VLQ_CONTINUATION_BIT - 1;

    static {
        Arrays.fill(BASE64_DECODE_TABLE, -1);
        IntStream.range(0, BASE64_CHARS.length()).forEach(i -> BASE64_DECODE_TABLE[BASE64_CHARS.charAt(i)] = i);
    }

    private final String mappings;
    private final List<String> names;
    private final List<List<SourceNode>> nodesInLines;
    private final List<String> sourceContents;
    private final List<String> sourceFilePaths;
    private int segmentOffset;

    public SourceMapUtils(
            List<String> sourceFilePaths,
            List<String> sourceContents,
            List<String> names,
            String mappings) {
        this.mappings = Objects.requireNonNull(mappings);
        this.names = SimpleList.immutableCopyOf(Objects.requireNonNull(names));
        nodesInLines = new ArrayList<>();
        segmentOffset = 0;
        this.sourceContents = SimpleList.immutableCopyOf(Objects.requireNonNull(sourceContents));
        this.sourceFilePaths = SimpleList.immutableCopyOf(Objects.requireNonNull(sourceFilePaths));
    }

    public static SourceMapUtils of(String sourceMapString) {
        SimpleJsonUtils.JsonNode jsonNode = SimpleJsonUtils.parse(sourceMapString);
        if (!jsonNode.isObject()) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: " + sourceMapString);
        }
        SimpleJsonUtils.JsonObjectNode jsonObjectNode = jsonNode.asObject();
        SimpleJsonUtils.JsonNode versionJsonNode = jsonObjectNode.getNodeMap().get(VERSION);
        if (versionJsonNode == null) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: version is not found");
        }
        if (!versionJsonNode.isNumber()) {
            throw new IllegalArgumentException("Invalid JSON string: version is not a number");
        }
        if (!SUPPORTED_VERSION_SET.contains(versionJsonNode.asNumber().getValue())) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: version " + versionJsonNode.asNumber().getValue() + " is not found");
        }
        SimpleJsonUtils.JsonNode sourcesJsonNode = jsonObjectNode.getNodeMap().get(SOURCES);
        if (sourcesJsonNode == null) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: sources is not found");
        }
        if (!sourcesJsonNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON string: sources is not an array");
        }
        List<String> sources = sourcesJsonNode.asArray().getNodes().stream()
                .filter(SimpleJsonUtils.JsonNode::isText)
                .map(SimpleJsonUtils.JsonNode::asText)
                .map(SimpleJsonUtils.JsonTextNode::getValue)
                .collect(Collectors.toList());
        if (sources.isEmpty()) {
            throw new IllegalArgumentException("Invalid JSON string: sources is empty");
        }
        SimpleJsonUtils.JsonNode sourcesContentJsonNode = jsonObjectNode.getNodeMap().get(SOURCES_CONTENT);
        if (sourcesContentJsonNode == null) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: sourcesContent is not found");
        }
        if (!sourcesContentJsonNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON string: sourcesContent is not an array");
        }
        List<String> sourcesContent = sourcesContentJsonNode.asArray().getNodes().stream()
                .filter(SimpleJsonUtils.JsonNode::isText)
                .map(SimpleJsonUtils.JsonNode::asText)
                .map(SimpleJsonUtils.JsonTextNode::getValue)
                .collect(Collectors.toList());
        if (sourcesContent.isEmpty()) {
            throw new IllegalArgumentException("Invalid JSON string: sourcesContent is empty");
        }
        SimpleJsonUtils.JsonNode namesJsonNode = jsonObjectNode.getNodeMap().get(NAMES);
        if (namesJsonNode == null) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: names is not found");
        }
        if (!namesJsonNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON string: names is not an array");
        }
        List<String> names = namesJsonNode.asArray().getNodes().stream()
                .filter(SimpleJsonUtils.JsonNode::isText)
                .map(SimpleJsonUtils.JsonNode::asText)
                .map(SimpleJsonUtils.JsonTextNode::getValue)
                .collect(Collectors.toList());
        SimpleJsonUtils.JsonNode mappingsJsonNode = jsonObjectNode.getNodeMap().get(MAPPINGS);
        if (mappingsJsonNode == null) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: mappings is not found");
        }
        if (!mappingsJsonNode.isText()) {
            throw new IllegalArgumentException("Invalid JSON string: mappings is not a text");
        }
        String mappings = mappingsJsonNode.asText().getValue();
        return new SourceMapUtils(sources, sourcesContent, names, mappings);
    }

    private void decodeLine(String lineString) {
        SourceNode lastNode = new SourceNode();
        if (!nodesInLines.isEmpty()) {
            List<SourceNode> lastNodesInLine = nodesInLines.get(nodesInLines.size() - 1);
            lastNode.copyFrom(lastNodesInLine.get(lastNodesInLine.size() - 1));
            lastNode.generatedColumnNumber = 0;
        }
        lastNode.generatedLineNumber = nodesInLines.size();
        List<SourceNode> nodes = new ArrayList<>();
        List<Integer> fields = new ArrayList<>();
        for (String segment : lineString.split(",")) {
            if (segment.isEmpty()) {
                continue;
            }
            fields.clear();
            int value = 0;
            int shift = 0;
            boolean negative;
            final int length = segment.length();
            for (int i = 0; i < length; ++i) {
                int index = BASE64_DECODE_TABLE[segment.charAt(i)];
                if (index == -1) {
                    throw new IllegalArgumentException("Invalid Base64 character in VLQ sequence: " + segment.charAt(i));
                }
                boolean continuation = (index & VLQ_CONTINUATION_BIT) != 0;
                index &= VLQ_VALUE_MASK;
                value += index << shift;
                if (continuation) {
                    shift += VLQ_SHIFT;
                } else {
                    negative = (value & 1) == 1;
                    value >>= 1;
                    fields.add(negative ? -value : value);
                    // reset for next value
                    value = 0;
                    shift = 0;
                }
            }
            if (fields.size() < 4) {
                continue;
            }
            lastNode.generatedColumnNumber += fields.get(0);
            lastNode.sourceFileIndex += fields.get(1);
            lastNode.originalLineNumber += fields.get(2);
            lastNode.originalColumnNumber += fields.get(3);
            SourceNode sourceNode = new SourceNode(lastNode);
            nodes.add(sourceNode);
        }
        nodesInLines.add(nodes);
    }

    public String getMappings() {
        return mappings;
    }

    public List<String> getNames() {
        return names;
    }

    /**
     * Gets node by 1-based line and 1-based column.
     *
     * @param line   the 1-based line
     * @param column the 1-based column
     * @return the mapping entry or null if not found
     */
    public SourceNode getNode(int line, int column) {
        if (line > 0 && column > 0) {
            line--;
            column--;
            while (line >= nodesInLines.size()) {
                if (isParsed()) {
                    break;
                }
                parseNextSegment();
            }
        }
        return null;
    }

    public List<String> getSourceContents() {
        return sourceContents;
    }

    public List<String> getSourceFilePaths() {
        return sourceFilePaths;
    }

    private boolean isParsed() {
        return mappings.isEmpty() || segmentOffset >= mappings.length();
    }

    private void parseNextSegment() {
        final int length = mappings.length();
        if (segmentOffset >= length) {
            return;
        }
        int startOffset = segmentOffset;
        int endOffset = mappings.indexOf(";", startOffset);
        if (endOffset == -1) {
            // We reach the end of the string.
            endOffset = length;
        }
        decodeLine(mappings.substring(startOffset, endOffset));
        segmentOffset = endOffset + 1;
    }

    public static class SourceNode {
        public int generatedColumnNumber;
        // Zero-based line and column in the generated code.
        public int generatedLineNumber;
        public int originalColumnNumber;
        // Zero-based line and column in the original source code file.
        public int originalLineNumber;
        // Zero-based index into the source code array.
        public int sourceFileIndex;

        public SourceNode() {
            this(0, 0, 0, 0, 0);
        }

        public SourceNode(SourceNode lastNode) {
            copyFrom(lastNode);
        }

        public SourceNode(
                int generatedLineNumber,
                int generatedColumnNumber,
                int originalLineNumber,
                int originalColumnNumber,
                int sourceFileIndex) {
            this.generatedColumnNumber = generatedColumnNumber;
            this.generatedLineNumber = generatedLineNumber;
            this.originalColumnNumber = originalColumnNumber;
            this.originalLineNumber = originalLineNumber;
            this.sourceFileIndex = sourceFileIndex;
        }

        public SourceNode copyFrom(SourceNode lastNode) {
            generatedColumnNumber = lastNode.generatedColumnNumber;
            generatedLineNumber = lastNode.generatedLineNumber;
            originalColumnNumber = lastNode.originalColumnNumber;
            originalLineNumber = lastNode.originalLineNumber;
            sourceFileIndex = lastNode.sourceFileIndex;
            return this;
        }
    }
}
