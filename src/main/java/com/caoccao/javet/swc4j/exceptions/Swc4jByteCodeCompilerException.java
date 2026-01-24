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

    public Swc4jByteCodeCompilerException(ISwc4jAst ast, String message) {
        super(message);
        this.ast = ast;
    }

    public Swc4jByteCodeCompilerException(ISwc4jAst ast, String message, Throwable cause) {
        super(message, cause);
        this.ast = ast;
    }

    public Swc4jByteCodeCompilerException(ISwc4jAst ast, Throwable cause) {
        super(cause);
        this.ast = ast;
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
     * Create a type mismatch exception for object literal properties.
     * <p>
     * Generates clear, actionable error messages like:
     * - "Property 'name' has type String, but Record requires double"
     * - "Key 'count' has type String, but Record requires Integer"
     * - "Nested property 'outer.inner' has type String, but Record requires double"
     *
     * @param ast          the AST node that caused the exception
     * @param propertyName Property name (or nested path like "outer.inner")
     * @param expectedType JVM type descriptor of expected type (e.g., "D", "Ljava/lang/Integer;")
     * @param actualType   JVM type descriptor of actual type (e.g., "Ljava/lang/String;")
     * @param isKey        true if this is a key type mismatch, false for value type mismatch
     * @return Swc4jByteCodeCompilerException with formatted error message
     */
    public static Swc4jByteCodeCompilerException typeMismatch(
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

        return new Swc4jByteCodeCompilerException(ast, message);
    }

    /**
     * Create a type mismatch exception with TypeScript Record type context.
     * <p>
     * Generates messages that reference the TypeScript type annotation:
     * - "Property 'age' has type String, but {@code Record<string, number>} requires double"
     * - "Key 'id' has type String, but {@code Record<number, string>} requires Integer"
     *
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

        return new Swc4jByteCodeCompilerException(ast, message);
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
}
