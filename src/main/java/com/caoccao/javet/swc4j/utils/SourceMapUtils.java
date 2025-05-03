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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SourceMapUtils {
    private static final String MAPPINGS = "mappings";
    private static final String NAMES = "names";
    private static final String SOURCES = "sources";
    private static final String SOURCES_CONTENT = "sourcesContent";
    private static final String VERSION = "version";
    private final String mappings;
    private final List<SourceNode> nodes;
    private final List<String> sourceContents;
    private final List<String> sourceFilePaths;
    private int segmentOffset;

    public SourceMapUtils(List<String> sourceFilePaths, List<String> sourceContents, String mappings) {
        this.mappings = Objects.requireNonNull(mappings);
        nodes = new ArrayList<>();
        segmentOffset = 0;
        this.sourceContents = SimpleList.immutableCopyOf(sourceContents);
        this.sourceFilePaths = SimpleList.immutableCopyOf(sourceFilePaths);
    }

    public static SourceMapUtils from(String sourceMapString) {
        SimpleJsonUtils.JsonNode jsonNode = SimpleJsonUtils.parse(sourceMapString);
        List<String> sourceFilePaths = SimpleList.of();
        List<String> sourceContents = SimpleList.of();
        String mappings = StringUtils.EMPTY;
        if (!jsonNode.isObject()) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: " + sourceMapString);
        }
        SimpleJsonUtils.JsonObjectNode jsonObjectNode = jsonNode.asObject();
        if (!jsonObjectNode.getNodeMap().containsKey(VERSION)) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: version is not found");
        }
        SimpleJsonUtils.JsonNode versionJsonNode = jsonObjectNode.getNodeMap().get(VERSION);
        if (!versionJsonNode.isNumber() || !"3".equals(versionJsonNode.asNumber().getValue())) {
            throw new IllegalArgumentException(
                    "Invalid JSON string: version " + versionJsonNode.asNumber().getValue() + " is not found");
        }
        return new SourceMapUtils(sourceFilePaths, sourceContents, mappings);
    }

    public String getMappings() {
        return mappings;
    }

    /**
     * Gets node by 1-based line and 1-based column.
     *
     * @param line   the 1-based line
     * @param column the 1-based column
     * @return the mapping entry
     */
    public SourceNode getNode(int line, int column) {
        if (line > 0 && column > 0) {
            line--;
            column--;
            while (line >= nodes.size()) {
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
        return mappings.isEmpty() || segmentOffset > mappings.length();
    }

    private void parseNextSegment() {
        int startOffset = segmentOffset;
        int endOffset = mappings.indexOf(";", startOffset);
        if (endOffset == -1) {
            // We reach the end of the string.
            endOffset = mappings.length();
        }
        segmentOffset = endOffset + 1;
    }

    public static class SourceNode {
        public int generatedColumn;
        // Zero-based line and column in the generated code.
        public int generatedLine;
        public int originalColumn;
        // Zero-based line and column in the original source code file.
        public int originalLine;
        // Zero-based index into the source code array.
        public int sourceFileIndex;
    }
}
