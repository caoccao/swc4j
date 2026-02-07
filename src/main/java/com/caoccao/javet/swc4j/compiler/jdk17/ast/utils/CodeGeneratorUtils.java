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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;

/**
 * Utility class for code generation helpers.
 */
public final class CodeGeneratorUtils {
    private CodeGeneratorUtils() {
    }

    /**
     * Creates a ReturnTypeInfo from a type descriptor string.
     *
     * @param descriptor the type descriptor (e.g., TypeConversionUtils.ABBR_INTEGER, TypeConversionUtils.ABBR_BOOLEAN, TypeConversionUtils.LJAVA_LANG_STRING)
     * @return the corresponding ReturnTypeInfo
     */
    public static ReturnTypeInfo createReturnTypeInfoFromDescriptor(String descriptor) {
        return switch (descriptor) {
            case TypeConversionUtils.ABBR_INTEGER -> new ReturnTypeInfo(ReturnType.INT, 1, null, null);
            case TypeConversionUtils.ABBR_BOOLEAN -> new ReturnTypeInfo(ReturnType.BOOLEAN, 1, null, null);
            case TypeConversionUtils.ABBR_BYTE -> new ReturnTypeInfo(ReturnType.BYTE, 1, null, null);
            case TypeConversionUtils.ABBR_CHARACTER -> new ReturnTypeInfo(ReturnType.CHAR, 1, null, null);
            case TypeConversionUtils.ABBR_SHORT -> new ReturnTypeInfo(ReturnType.SHORT, 1, null, null);
            case TypeConversionUtils.ABBR_LONG -> new ReturnTypeInfo(ReturnType.LONG, 2, null, null);
            case TypeConversionUtils.ABBR_FLOAT -> new ReturnTypeInfo(ReturnType.FLOAT, 1, null, null);
            case TypeConversionUtils.ABBR_DOUBLE -> new ReturnTypeInfo(ReturnType.DOUBLE, 2, null, null);
            case TypeConversionUtils.LJAVA_LANG_STRING -> new ReturnTypeInfo(ReturnType.STRING, 1, null, null);
            default -> new ReturnTypeInfo(ReturnType.OBJECT, 1, descriptor, null);
        };
    }

    /**
     * Generates the appropriate return bytecode based on the return type.
     *
     * @param code           the code builder
     * @param returnTypeInfo the return type information
     */
    public static void generateReturn(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
        switch (returnTypeInfo.type()) {
            case VOID -> code.returnVoid();
            case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.ireturn();
            case LONG -> code.lreturn();
            case FLOAT -> code.freturn();
            case DOUBLE -> code.dreturn();
            case STRING, OBJECT -> code.areturn();
        }
    }

    /**
     * Gets the method name from a property name AST node.
     *
     * @param propName the property name AST node
     * @return the method name as a string
     */
    public static String getMethodName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
    }

    /**
     * Converts a ReturnTypeInfo to its descriptor string.
     *
     * @param returnTypeInfo the return type information
     * @return the descriptor string (e.g., TypeConversionUtils.ABBR_VOID, TypeConversionUtils.ABBR_INTEGER, TypeConversionUtils.LJAVA_LANG_STRING)
     */
    public static String getReturnDescriptor(ReturnTypeInfo returnTypeInfo) {
        return switch (returnTypeInfo.type()) {
            case VOID -> TypeConversionUtils.ABBR_VOID;
            case INT -> TypeConversionUtils.ABBR_INTEGER;
            case BOOLEAN -> TypeConversionUtils.ABBR_BOOLEAN;
            case BYTE -> TypeConversionUtils.ABBR_BYTE;
            case CHAR -> TypeConversionUtils.ABBR_CHARACTER;
            case SHORT -> TypeConversionUtils.ABBR_SHORT;
            case LONG -> TypeConversionUtils.ABBR_LONG;
            case FLOAT -> TypeConversionUtils.ABBR_FLOAT;
            case DOUBLE -> TypeConversionUtils.ABBR_DOUBLE;
            case STRING -> TypeConversionUtils.LJAVA_LANG_STRING;
            case OBJECT ->
                    returnTypeInfo.descriptor() != null ? returnTypeInfo.descriptor() : TypeConversionUtils.LJAVA_LANG_OBJECT;
        };
    }

    /**
     * Returns the number of local variable slots required for a type.
     *
     * @param type the type descriptor
     * @return 2 for long/double, 1 for all other types
     */
    public static int getSlotSize(String type) {
        return (TypeConversionUtils.ABBR_LONG.equals(type) || TypeConversionUtils.ABBR_DOUBLE.equals(type)) ? 2 : 1;
    }

    /**
     * Check if a bytecode opcode is a terminal instruction that ends a method.
     * Terminal instructions include all return types and athrow.
     *
     * @param opcode the bytecode opcode
     * @return true if the opcode is a terminal instruction
     */
    public static boolean isTerminalBytecode(int opcode) {
        return switch (opcode) {
            case 0xAC, // ireturn
                 0xAD, // lreturn
                 0xAE, // freturn
                 0xAF, // dreturn
                 0xB0, // areturn
                 0xB1, // return (void)
                 0xBF  // athrow
                    -> true;
            default -> false;
        };
    }

    /**
     * Generates bytecode to load a parameter from a local variable slot.
     *
     * @param code      the code builder
     * @param slot      the local variable slot index
     * @param paramType the parameter type descriptor
     */
    public static void loadParameter(CodeBuilder code, int slot, String paramType) {
        switch (paramType) {
            case TypeConversionUtils.ABBR_INTEGER, TypeConversionUtils.ABBR_BOOLEAN, TypeConversionUtils.ABBR_BYTE,
                 TypeConversionUtils.ABBR_CHARACTER, TypeConversionUtils.ABBR_SHORT -> code.iload(slot);
            case TypeConversionUtils.ABBR_LONG -> code.lload(slot);
            case TypeConversionUtils.ABBR_FLOAT -> code.fload(slot);
            case TypeConversionUtils.ABBR_DOUBLE -> code.dload(slot);
            default -> code.aload(slot);
        }
    }
}
