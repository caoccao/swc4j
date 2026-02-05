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

package com.caoccao.javet.swc4j.exceptions;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;

public final class Swc4jByteCodeCompilerException extends Swc4jException {
    private final ISwc4jAst ast;
    private final String sourceCode;

    public Swc4jByteCodeCompilerException(String sourceCode, ISwc4jAst ast, String message) {
        super(message);
        this.sourceCode = sourceCode;
        this.ast = ast;
    }

    public Swc4jByteCodeCompilerException(String sourceCode, ISwc4jAst ast, String message, Throwable cause) {
        super(message, cause);
        this.sourceCode = sourceCode;
        this.ast = ast;
    }

    public Swc4jByteCodeCompilerException(String sourceCode, ISwc4jAst ast, Throwable cause) {
        super(cause);
        this.sourceCode = sourceCode;
        this.ast = ast;
    }


    /**
     * Convert a JVM type descriptor to a human-readable type name.
     * <p>
     * Examples:
     * - "I" → "int"
     * - "D" → "double"
     * - "Ljava/lang/String;" → "String"
     * - "Ljava/lang/Integer;" → "Integer"
     * - "[I" → "int[]"
     *
     * @param descriptor JVM type descriptor
     * @return Human-readable type name
     */
    private static String descriptorToTypeName(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) {
            return "unknown";
        }

        // Primitive types
        return switch (descriptor) {
            case "Z" -> "boolean";
            case "B" -> "byte";
            case "C" -> "char";
            case "S" -> "short";
            case "I" -> "int";
            case "J" -> "long";
            case "F" -> "float";
            case "D" -> "double";
            case "V" -> "void";
            default -> {
                // Object types: "Ljava/lang/String;" → "String"
                if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
                    String className = descriptor.substring(1, descriptor.length() - 1);
                    // Get simple name (after last slash)
                    int lastSlash = className.lastIndexOf('/');
                    if (lastSlash >= 0 && lastSlash < className.length() - 1) {
                        yield className.substring(lastSlash + 1);
                    }
                    yield className.replace('/', '.');
                }
                // Array types: "[I" → "int[]"
                if (descriptor.startsWith("[")) {
                    String componentDescriptor = descriptor.substring(1);
                    String componentType = descriptorToTypeName(componentDescriptor);
                    yield componentType + "[]";
                }
                yield descriptor;
            }
        };
    }

    /**
     * Create a type mismatch exception for object literal properties.
     * <p>
     * Generates clear, actionable error messages like:
     * - "Property 'name' has type String, but Record requires double"
     * - "Key 'count' has type String, but Record requires Integer"
     * - "Nested property 'outer.inner' has type String, but Record requires double"
     *
     * @param sourceCode   the source code being compiled
     * @param ast          the AST node that caused the exception
     * @param propertyName Property name (or nested path like "outer.inner")
     * @param expectedType JVM type descriptor of expected type (e.g., "D", "Ljava/lang/Integer;")
     * @param actualType   JVM type descriptor of actual type (e.g., "Ljava/lang/String;")
     * @param isKey        true if this is a key type mismatch, false for value type mismatch
     * @return Swc4jByteCodeCompilerException with formatted error message
     */
    public static Swc4jByteCodeCompilerException typeMismatch(
            String sourceCode,
            ISwc4jAst ast,
            String propertyName,
            String expectedType,
            String actualType,
            boolean isKey) {
        String propertyKind = isKey ? "Key" : "Property";
        String expectedTypeName = descriptorToTypeName(expectedType);
        String actualTypeName = descriptorToTypeName(actualType);

        // Determine if this is a nested property
        boolean isNested = propertyName != null && propertyName.contains(".");

        String message;
        if (isNested) {
            message = String.format(
                    "Nested property '%s' has type %s, but Record requires %s",
                    propertyName,
                    actualTypeName,
                    expectedTypeName
            );
        } else {
            message = String.format(
                    "%s '%s' has type %s, but Record requires %s",
                    propertyKind,
                    propertyName != null ? propertyName : "<unknown>",
                    actualTypeName,
                    expectedTypeName
            );
        }

        return new Swc4jByteCodeCompilerException(sourceCode, ast, message);
    }

    /**
     * Create a type mismatch exception with TypeScript Record type context.
     * <p>
     * Generates messages that reference the TypeScript type annotation:
     * - "Property 'age' has type String, but {@code Record<string, number>} requires double"
     * - "Key 'id' has type String, but {@code Record<number, string>} requires Integer"
     *
     * @param sourceCode          the source code being compiled
     * @param ast                 the AST node that caused the exception
     * @param propertyName        Property name
     * @param expectedType        JVM type descriptor of expected type
     * @param actualType          JVM type descriptor of actual type
     * @param isKey               true for key mismatch, false for value mismatch
     * @param recordKeyTypeName   TypeScript type name for key (e.g., "string", "number")
     * @param recordValueTypeName TypeScript type name for value (e.g., "number", "string")
     * @return Swc4jByteCodeCompilerException with formatted error message including Record type
     */
    public static Swc4jByteCodeCompilerException typeMismatchWithRecordType(
            String sourceCode,
            ISwc4jAst ast,
            String propertyName,
            String expectedType,
            String actualType,
            boolean isKey,
            String recordKeyTypeName,
            String recordValueTypeName) {
        String propertyKind = isKey ? "Key" : "Property";
        String expectedTypeName = descriptorToTypeName(expectedType);
        String actualTypeName = descriptorToTypeName(actualType);

        // Determine if this is a nested property
        boolean isNested = propertyName != null && propertyName.contains(".");

        String recordTypeStr = String.format("Record<%s, %s>", recordKeyTypeName, recordValueTypeName);

        String message;
        if (isNested) {
            message = String.format(
                    "Nested property '%s' has type %s, but %s requires %s",
                    propertyName,
                    actualTypeName,
                    recordTypeStr,
                    expectedTypeName
            );
        } else {
            message = String.format(
                    "%s '%s' has type %s, but %s requires %s",
                    propertyKind,
                    propertyName != null ? propertyName : "<unknown>",
                    actualTypeName,
                    recordTypeStr,
                    expectedTypeName
            );
        }

        return new Swc4jByteCodeCompilerException(sourceCode, ast, message);
    }

    /**
     * Gets the AST node that caused the exception.
     *
     * @return the AST node, or null if not available
     */
    public ISwc4jAst getAst() {
        return ast;
    }

    /**
     * Gets a detailed error message with source code context.
     * <p>
     * The format is:
     * <pre>
     * Error message here
     *   Error at position 18, line 1, column 19
     *   ...x: number = "hello"; return x
     *                  ^^^^^^^
     * </pre>
     * <p>
     * The number of caret (^) characters corresponds to the span length (end - start).
     * If the error doesn't start at the beginning of the line, at most 3 words before
     * the error are shown with "..." prefix. At most 5 words after the error end are shown.
     * If the context is truncated, "..." is added at the end.
     *
     * @return formatted error message with source context, or just the message if context unavailable
     */
    public String getDetailedMessage() {
        String message = getMessage();
        if (sourceCode == null || ast == null) {
            return message;
        }

        var span = ast.getSpan();
        if (span == null || span.getStart() < 0) {
            return message;
        }

        // Find the line containing the error
        int start = span.getStart();
        int end = span.getEnd();
        int spanLength = Math.max(1, end - start);
        int lineStart = findLineStart(sourceCode, start);
        int lineEnd = findLineEnd(sourceCode, start);

        if (lineStart < 0 || lineEnd < 0) {
            return message;
        }

        String sourceLine = sourceCode.substring(lineStart, lineEnd);
        int column = start - lineStart;
        int errorEndColumn = Math.min(column + spanLength, sourceLine.length());

        // Calculate line number (1-based)
        int lineNumber = 1;
        for (int i = 0; i < lineStart; i++) {
            if (sourceCode.charAt(i) == '\n') {
                lineNumber++;
            }
        }

        // Build the formatted message
        StringBuilder sb = new StringBuilder();
        sb.append(message).append("\n");

        // Add position, line and column info
        sb.append("  Error at position ").append(start)
                .append(", line ").append(lineNumber)
                .append(", column ").append(column + 1).append("\n");

        // Calculate context boundaries based on word counts
        int contextStart = column > 0 ? findContextStart(sourceLine, column, 3) : 0;
        int contextEnd = findContextEnd(sourceLine, errorEndColumn, 5);

        // Build the context string with ellipses (with spaces)
        boolean hasPrefix = contextStart > 0;
        boolean hasSuffix = contextEnd < sourceLine.length();

        String contextStr;
        if (hasPrefix && hasSuffix) {
            contextStr = "... " + sourceLine.substring(contextStart, contextEnd) + " ...";
        } else if (hasPrefix) {
            contextStr = "... " + sourceLine.substring(contextStart, contextEnd);
        } else if (hasSuffix) {
            contextStr = sourceLine.substring(contextStart, contextEnd) + " ...";
        } else {
            contextStr = sourceLine.substring(contextStart, contextEnd);
        }

        // Calculate caret position in the context string (prefix "... " is 4 chars)
        int caretStart = column - contextStart + (hasPrefix ? 4 : 0);
        int visibleCarets = Math.min(spanLength, contextEnd - column);

        sb.append("  ").append(contextStr).append("\n");
        sb.append("  ");
        for (int i = 0; i < caretStart; i++) {
            sb.append(' ');
        }
        for (int i = 0; i < visibleCarets; i++) {
            sb.append('^');
        }

        return sb.toString();
    }

    /**
     * Finds the start position for context display, going back at most the specified number of words.
     *
     * @param line      the source line
     * @param errorCol  the error column (0-indexed)
     * @param maxWords  maximum number of words to include before the error
     * @return the start position for context display
     */
    private int findContextStart(String line, int errorCol, int maxWords) {
        if (errorCol <= 0) {
            return 0;
        }

        int pos = errorCol - 1;
        int wordCount = 0;
        boolean inWord = false;

        // Skip any whitespace immediately before error
        while (pos >= 0 && Character.isWhitespace(line.charAt(pos))) {
            pos--;
        }

        // Count words going backwards
        while (pos >= 0 && wordCount < maxWords) {
            char c = line.charAt(pos);
            if (Character.isWhitespace(c)) {
                if (inWord) {
                    wordCount++;
                    inWord = false;
                }
            } else {
                inWord = true;
            }
            pos--;
        }

        // Move forward to start of word (skip whitespace)
        pos++;
        while (pos < errorCol && Character.isWhitespace(line.charAt(pos))) {
            pos++;
        }

        return pos;
    }

    /**
     * Finds the end position for context display, going forward at most the specified number of words.
     *
     * @param line         the source line
     * @param errorEndCol  the error end column (0-indexed, exclusive)
     * @param maxWords     maximum number of words to include after the error
     * @return the end position for context display
     */
    private int findContextEnd(String line, int errorEndCol, int maxWords) {
        if (errorEndCol >= line.length()) {
            return line.length();
        }

        int pos = errorEndCol;
        int wordCount = 0;
        boolean inWord = false;

        // Count words going forward
        while (pos < line.length() && wordCount < maxWords) {
            char c = line.charAt(pos);
            if (Character.isWhitespace(c)) {
                if (inWord) {
                    wordCount++;
                    inWord = false;
                }
            } else {
                inWord = true;
            }
            pos++;
        }

        // If we ended in a word, that counts as one more word
        if (inWord) {
            wordCount++;
        }

        // If we've reached maxWords, we're done
        // If not at end of line and we ended on whitespace, trim it
        if (pos < line.length()) {
            while (pos > errorEndCol && Character.isWhitespace(line.charAt(pos - 1))) {
                pos--;
            }
        }

        return pos;
    }

    /**
     * Gets the source code that was being compiled when the exception occurred.
     *
     * @return the source code, or null if not available
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Finds the end position of the line containing the given position.
     *
     * @param source   the source code
     * @param position the position within the source
     * @return the index of the newline character or end of string
     */
    private int findLineEnd(String source, int position) {
        if (position >= source.length()) {
            return source.length();
        }
        int end = source.indexOf('\n', position);
        if (end < 0) {
            end = source.length();
        }
        // Handle \r\n line endings
        if (end > 0 && source.charAt(end - 1) == '\r') {
            end--;
        }
        return end;
    }

    /**
     * Finds the start position of the line containing the given position.
     *
     * @param source   the source code
     * @param position the position within the source
     * @return the index after the preceding newline, or 0 if at the first line
     */
    private int findLineStart(String source, int position) {
        if (position <= 0) {
            return 0;
        }
        int start = source.lastIndexOf('\n', position - 1);
        return start < 0 ? 0 : start + 1;
    }
}
