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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Return type info.
 *
 * @param type            the return type
 * @param maxStack        the maximum stack size
 * @param descriptor      the type descriptor
 * @param genericTypeInfo the generic type information
 */
public record ReturnTypeInfo(ReturnType type, int maxStack, String descriptor, GenericTypeInfo genericTypeInfo) {
    /**
     * Of return type info.
     *
     * @param sourceCode the source code
     * @param ast        the ast
     * @param type       the type
     * @return the return type info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public static ReturnTypeInfo of(String sourceCode, ISwc4jAst ast, String type) throws Swc4jByteCodeCompilerException {
        return of(sourceCode, ast, type, null);
    }

    /**
     * Of return type info.
     *
     * @param sourceCode      the source code
     * @param ast             the ast
     * @param type            the type
     * @param genericTypeInfo the generic type info
     * @return the return type info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public static ReturnTypeInfo of(String sourceCode, ISwc4jAst ast, String type, GenericTypeInfo genericTypeInfo) throws Swc4jByteCodeCompilerException {
        if (type == null || type.isEmpty()) {
            throw new Swc4jByteCodeCompilerException(sourceCode, ast, "Missing type info.");
        }
        if (type.length() == 1) {
            return switch (type) {
                case ConstantJavaType.ABBR_INTEGER -> new ReturnTypeInfo(ReturnType.INT, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_BOOLEAN -> new ReturnTypeInfo(ReturnType.BOOLEAN, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_BYTE -> new ReturnTypeInfo(ReturnType.BYTE, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_CHARACTER -> new ReturnTypeInfo(ReturnType.CHAR, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_SHORT -> new ReturnTypeInfo(ReturnType.SHORT, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_LONG -> new ReturnTypeInfo(ReturnType.LONG, 2, null, genericTypeInfo);
                case ConstantJavaType.ABBR_FLOAT -> new ReturnTypeInfo(ReturnType.FLOAT, 1, null, genericTypeInfo);
                case ConstantJavaType.ABBR_DOUBLE -> new ReturnTypeInfo(ReturnType.DOUBLE, 2, null, genericTypeInfo);
                case ConstantJavaType.ABBR_VOID -> new ReturnTypeInfo(ReturnType.VOID, 0, null, genericTypeInfo);
                default ->
                        throw new Swc4jByteCodeCompilerException(sourceCode, ast, "Unsupported primitive type: " + type);
            };
        }
        if (type.equals(ConstantJavaType.LJAVA_LANG_STRING)) {
            return new ReturnTypeInfo(ReturnType.STRING, 1, type, genericTypeInfo);
        }
        // Handle array types (primitive arrays like [I or reference arrays like [Ljava/lang/String;)
        if (type.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return new ReturnTypeInfo(ReturnType.OBJECT, 1, type, genericTypeInfo);
        }
        // Handle regular reference types
        if (type.startsWith("L") && type.endsWith(";")) {
            return new ReturnTypeInfo(ReturnType.OBJECT, 1, type, genericTypeInfo);
        }
        throw new Swc4jByteCodeCompilerException(sourceCode, ast, "Unsupported object type: " + type);
    }
}
