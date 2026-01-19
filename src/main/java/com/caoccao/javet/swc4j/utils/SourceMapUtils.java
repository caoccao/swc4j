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

import java.util.*;
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
                .map(node -> node.isText() ? node.asText().getValue() : null)
                .toList();
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
                .map(node -> node.isText() ? node.asText().getValue() : null)
                .toList();
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
                .map(node -> node.isText() ? node.asText().getValue() : null)
                .toList();
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
        SourceNode lastNode = SourceNode.of();
        if (!nodesInLines.isEmpty()) {
            List<SourceNode> lastNodesInLine = nodesInLines.get(nodesInLines.size() - 1);
            lastNode = SourceNode.of(lastNodesInLine.get(lastNodesInLine.size() - 1));
            lastNode.generatedPosition.column = 0;
        }
        lastNode.generatedPosition.line = nodesInLines.size();
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
            // Handle different segment field counts:
            // 1 field: generated column
            // 4 fields: generated column, source index, original line, original column
            // 5 fields: generated column, source index, original line, original column, name index
            if (fields.size() == 1) {
                // Only update generated column
                lastNode.generatedPosition.column += fields.get(0);
                // Don't add to nodes - this is just a position marker without source mapping
            } else if (fields.size() == 4 || fields.size() == 5) {
                lastNode.generatedPosition.column += fields.get(0);
                lastNode.sourceFileIndex += fields.get(1);
                lastNode.originalPosition.line += fields.get(2);
                lastNode.originalPosition.column += fields.get(3);
                if (fields.size() == 5) {
                    lastNode.nameIndex += fields.get(4);
                }
                // Validate indices are within bounds
                if (lastNode.sourceFileIndex < 0 || lastNode.sourceFileIndex >= sourceFilePaths.size()) {
                    throw new IllegalArgumentException(
                            "Invalid source map: sourceFileIndex " + lastNode.sourceFileIndex +
                            " is out of bounds (sources size: " + sourceFilePaths.size() + ")");
                }
                // nameIndex can be -1 (no name), or 0 to names.size()-1
                if (lastNode.nameIndex < -1 || (lastNode.nameIndex >= 0 && lastNode.nameIndex >= names.size())) {
                    throw new IllegalArgumentException(
                            "Invalid source map: nameIndex " + lastNode.nameIndex +
                            " is out of bounds (names size: " + names.size() + ")");
                }
                nodes.add(SourceNode.of(lastNode));
            }
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
     * Gets node by 0-based position.
     *
     * @param position the 0-based position
     * @return the node or null if not found
     */
    public SourceNode getNode(SourcePosition position) {
        return getNode(position.line, position.column);
    }

    /**
     * Gets node by 0-based line and 0-based column.
     *
     * @param line   the 0-based line
     * @param column the 0-based column
     * @return the node or null if not found
     */
    public SourceNode getNode(int line, int column) {
        SourceNode node = null;
        if (line >= 0 && column >= 0) {
            while (line >= nodesInLines.size()) {
                if (isParsed()) {
                    break;
                }
                parseNextSegment();
            }
            if (line < nodesInLines.size()) {
                List<SourceNode> nodes = nodesInLines.get(line);
                if (!nodes.isEmpty()) {
                    SourceNode lastNode = nodes.get(0);
                    final int length = nodes.size();
                    for (int i = 1; i < length; ++i) {
                        SourceNode currentNode = nodes.get(i);
                        if (column < currentNode.generatedPosition.column) {
                            break;
                        }
                        lastNode = currentNode;
                    }
                    if (column >= lastNode.generatedPosition.column) {
                        node = SourceNode.of(lastNode);
                        node.generatedPosition.column = column;
                        node.originalPosition.column += column - lastNode.generatedPosition.column;
                    }
                }
            }
        }
        return node;
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
        public SourcePosition generatedPosition;
        public SourcePosition originalPosition;
        // Zero-based index into the source code array.
        public int sourceFileIndex;
        // Zero-based index into the names array, -1 if not set.
        public int nameIndex;

        private SourceNode(
                SourcePosition originalPosition,
                SourcePosition generatedPosition,
                int sourceFileIndex,
                int nameIndex) {
            this.generatedPosition = SourcePosition.of(generatedPosition);
            this.originalPosition = SourcePosition.of(originalPosition);
            this.sourceFileIndex = sourceFileIndex;
            this.nameIndex = nameIndex;
        }

        public static SourceNode of() {
            return of(SourcePosition.of(), SourcePosition.of(), 0, -1);
        }

        public static SourceNode of(SourceNode node) {
            return of(node.originalPosition, node.generatedPosition, node.sourceFileIndex, node.nameIndex);
        }

        public static SourceNode of(SourcePosition originalPosition, SourcePosition generatedPosition) {
            return new SourceNode(originalPosition, generatedPosition, 0, -1);
        }

        public static SourceNode of(
                SourcePosition originalPosition,
                SourcePosition generatedPosition,
                int sourceFileIndex) {
            return new SourceNode(originalPosition, generatedPosition, sourceFileIndex, -1);
        }

        public static SourceNode of(
                SourcePosition originalPosition,
                SourcePosition generatedPosition,
                int sourceFileIndex,
                int nameIndex) {
            return new SourceNode(originalPosition, generatedPosition, sourceFileIndex, nameIndex);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SourceNode that = (SourceNode) o;
            return sourceFileIndex == that.sourceFileIndex
                    && nameIndex == that.nameIndex
                    && Objects.equals(generatedPosition, that.generatedPosition)
                    && Objects.equals(originalPosition, that.originalPosition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(generatedPosition, originalPosition, sourceFileIndex, nameIndex);
        }

        @Override
        public String toString() {
            return "SourceNode{" +
                    "generatedPosition=" + generatedPosition +
                    ", originalPosition=" + originalPosition +
                    ", sourceFileIndex=" + sourceFileIndex +
                    ", nameIndex=" + nameIndex +
                    '}';
        }
    }

    public static class SourcePosition {
        // Zero-based
        public int column;
        // Zero-based
        public int line;

        private SourcePosition(int line, int column) {
            this.column = column;
            this.line = line;
        }

        public static SourcePosition of() {
            return of(0, 0);
        }

        public static SourcePosition of(int line, int column) {
            return new SourcePosition(line, column);
        }

        public static SourcePosition of(SourcePosition position) {
            return new SourcePosition(position.line, position.column);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SourcePosition that = (SourcePosition) o;
            return column == that.column && line == that.line;
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, line);
        }

        @Override
        public String toString() {
            return "SourcePosition{" +
                    "column=" + column +
                    ", line=" + line +
                    '}';
        }
    }
}
