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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public record ReturnTypeInfo(ReturnType type, int maxStack, String descriptor) {
    public static ReturnTypeInfo of(String type) throws Swc4jByteCodeCompilerException {
        if (type == null || type.isEmpty()) {
            throw new Swc4jByteCodeCompilerException("Missing type info.");
        }
        if (type.length() == 1) {
            return switch (type) {
                case "I" -> new ReturnTypeInfo(ReturnType.INT, 1, null);
                case "Z" -> new ReturnTypeInfo(ReturnType.BOOLEAN, 1, null);
                case "B" -> new ReturnTypeInfo(ReturnType.BYTE, 1, null);
                case "C" -> new ReturnTypeInfo(ReturnType.CHAR, 1, null);
                case "S" -> new ReturnTypeInfo(ReturnType.SHORT, 1, null);
                case "J" -> new ReturnTypeInfo(ReturnType.LONG, 2, null);
                case "F" -> new ReturnTypeInfo(ReturnType.FLOAT, 1, null);
                case "D" -> new ReturnTypeInfo(ReturnType.DOUBLE, 2, null);
                case "V" -> new ReturnTypeInfo(ReturnType.VOID, 0, null);
                default -> throw new Swc4jByteCodeCompilerException("Unsupported primitive type: " + type);
            };
        }
        if (type.equals("Ljava/lang/String;")) {
            return new ReturnTypeInfo(ReturnType.STRING, 1, type);
        }
        if ((type.startsWith("L") || type.startsWith("[")) && type.endsWith(";")) {
            return new ReturnTypeInfo(ReturnType.OBJECT, 1, type);
        }
        throw new Swc4jByteCodeCompilerException("Unsupported object type: " + type);
    }
}
