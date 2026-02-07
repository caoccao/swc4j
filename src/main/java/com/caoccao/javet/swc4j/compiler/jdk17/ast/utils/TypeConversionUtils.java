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

import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;

import java.util.Set;

/**
 * The type Type conversion utils.
 */
public final class TypeConversionUtils {
    private static final Set<String> INTEGER_PRIMITIVES = Set.of(ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_CHARACTER);
    private static final Set<String> NUMERIC_PRIMITIVES = Set.of(ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_FLOAT, ConstantJavaType.ABBR_DOUBLE, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_CHARACTER);
    private static final Set<String> PRIMITIVE_TYPES = Set.of(ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_FLOAT, ConstantJavaType.ABBR_DOUBLE);

    private TypeConversionUtils() {
    }

    /**
     * Box primitive type.
     *
     * @param code          the code
     * @param classWriter   the class writer
     * @param primitiveType the primitive type
     * @param targetType    the target type
     */
    public static void boxPrimitiveType(CodeBuilder code, ClassWriter classWriter, String primitiveType, String targetType) {
        var cp = classWriter.getConstantPool();
        // Only box if targetType is a wrapper
        if (!targetType.startsWith(ConstantJavaType.LJAVA_LANG_)) {
            return; // Target is primitive, no boxing needed
        }

        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> {
                if (ConstantJavaType.LJAVA_LANG_INTEGER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_INTEGER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_BOOLEAN -> {
                if (ConstantJavaType.LJAVA_LANG_BOOLEAN.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BOOLEAN, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_Z__LJAVA_LANG_BOOLEAN);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_BYTE -> {
                if (ConstantJavaType.LJAVA_LANG_BYTE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_B__LJAVA_LANG_BYTE);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_CHARACTER -> {
                if (ConstantJavaType.LJAVA_LANG_CHARACTER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_CHARACTER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_C__LJAVA_LANG_CHARACTER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_SHORT -> {
                if (ConstantJavaType.LJAVA_LANG_SHORT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_S__LJAVA_LANG_SHORT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_LONG -> {
                if (ConstantJavaType.LJAVA_LANG_LONG.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_J__LJAVA_LANG_LONG);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_FLOAT -> {
                if (ConstantJavaType.LJAVA_LANG_FLOAT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_F__LJAVA_LANG_FLOAT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                if (ConstantJavaType.LJAVA_LANG_DOUBLE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_D__LJAVA_LANG_DOUBLE);
                    code.invokestatic(valueOfRef);
                }
            }
        }
    }

    /**
     * Convert primitive type.
     *
     * @param code     the code
     * @param fromType the from type
     * @param toType   the to type
     */
    public static void convertPrimitiveType(CodeBuilder code, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return; // No conversion needed
        }

        // byte, short, char are stored as int on the stack, so they start as "I" after unboxing
        String stackFromType = switch (fromType) {
            case ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_CHARACTER ->
                    ConstantJavaType.ABBR_INTEGER;
            default -> fromType;
        };

        // Convert from stack type to target type
        switch (stackFromType) {
            case ConstantJavaType.ABBR_INTEGER -> {
                switch (toType) {
                    case ConstantJavaType.ABBR_BYTE -> code.i2b();
                    case ConstantJavaType.ABBR_CHARACTER -> code.i2c();
                    case ConstantJavaType.ABBR_SHORT -> code.i2s();
                    case ConstantJavaType.ABBR_LONG -> code.i2l();
                    case ConstantJavaType.ABBR_FLOAT -> code.i2f();
                    case ConstantJavaType.ABBR_DOUBLE -> code.i2d();
                }
            }
            case ConstantJavaType.ABBR_LONG -> {
                switch (toType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.l2i();
                    case ConstantJavaType.ABBR_FLOAT -> code.l2f();
                    case ConstantJavaType.ABBR_DOUBLE -> code.l2d();
                }
            }
            case ConstantJavaType.ABBR_FLOAT -> {
                switch (toType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.f2i();
                    case ConstantJavaType.ABBR_LONG -> code.f2l();
                    case ConstantJavaType.ABBR_DOUBLE -> code.f2d();
                }
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                switch (toType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.d2i();
                    case ConstantJavaType.ABBR_LONG -> code.d2l();
                    case ConstantJavaType.ABBR_FLOAT -> code.d2f();
                }
            }
        }
    }

    /**
     * Gets the JVM newarray type code for a primitive type descriptor.
     * These are the standard JVM type codes used by the newarray bytecode instruction.
     *
     * @param primitiveType the primitive type descriptor
     * @return the newarray type code (4-11), or 10 (int) as default
     */
    public static int getNewarrayTypeCode(String primitiveType) {
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_BOOLEAN -> 4;   // T_BOOLEAN
            case ConstantJavaType.ABBR_CHARACTER -> 5; // T_CHAR
            case ConstantJavaType.ABBR_FLOAT -> 6;     // T_FLOAT
            case ConstantJavaType.ABBR_DOUBLE -> 7;    // T_DOUBLE
            case ConstantJavaType.ABBR_BYTE -> 8;      // T_BYTE
            case ConstantJavaType.ABBR_SHORT -> 9;     // T_SHORT
            case ConstantJavaType.ABBR_INTEGER -> 10;  // T_INT
            case ConstantJavaType.ABBR_LONG -> 11;     // T_LONG
            default -> 10;                              // Default to int
        };
    }

    /**
     * Gets primitive type.
     *
     * @param type the type
     * @return the primitive type
     */
    public static String getPrimitiveType(String type) {
        return switch (type) {
            case ConstantJavaType.LJAVA_LANG_BYTE -> ConstantJavaType.ABBR_BYTE;
            case ConstantJavaType.LJAVA_LANG_SHORT -> ConstantJavaType.ABBR_SHORT;
            case ConstantJavaType.LJAVA_LANG_INTEGER -> ConstantJavaType.ABBR_INTEGER;
            case ConstantJavaType.LJAVA_LANG_LONG -> ConstantJavaType.ABBR_LONG;
            case ConstantJavaType.LJAVA_LANG_FLOAT -> ConstantJavaType.ABBR_FLOAT;
            case ConstantJavaType.LJAVA_LANG_DOUBLE -> ConstantJavaType.ABBR_DOUBLE;
            case ConstantJavaType.LJAVA_LANG_CHARACTER -> ConstantJavaType.ABBR_CHARACTER;
            default -> type;
        };
    }

    /**
     * Converts a ReturnTypeInfo to its descriptor string.
     *
     * @param returnTypeInfo the return type information
     * @return the descriptor string (e.g., "V", "I", "Ljava/lang/String;")
     */
    public static String getReturnDescriptor(ReturnTypeInfo returnTypeInfo) {
        // For OBJECT type with specific descriptor, use it; otherwise use enum default
        return returnTypeInfo.descriptor() != null ? returnTypeInfo.descriptor() : returnTypeInfo.type().getDescriptor();
    }

    /**
     * Gets the result of the {@code typeof} operator for a statically-known type.
     * Returns {@code null} if the type requires runtime checking (e.g., generic Object).
     *
     * @param type the JVM type descriptor
     * @return the typeof result string, or null if runtime check is needed
     */
    public static String getTypeOfResult(String type) {
        if (type == null) return null;
        if (ConstantJavaType.ABBR_VOID.equals(type)) return ConstantJavaType.TYPEOF_UNDEFINED;
        if (ConstantJavaType.LJAVA_LANG_STRING.equals(type)) return ConstantJavaType.TYPEOF_STRING;
        if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(type)) return ConstantJavaType.TYPEOF_NUMBER;
        if (type.startsWith(ConstantJavaType.ARRAY_PREFIX)) return ConstantJavaType.TYPEOF_OBJECT;
        String primitiveType = getPrimitiveType(type);
        if (ConstantJavaType.ABBR_BOOLEAN.equals(primitiveType)) return ConstantJavaType.TYPEOF_BOOLEAN;
        if (isNumericPrimitive(primitiveType)) return ConstantJavaType.TYPEOF_NUMBER;
        return null;
    }

    /**
     * Gets wrapper type.
     *
     * @param primitiveType the primitive type
     * @return the wrapper type
     */
    public static String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_BYTE -> ConstantJavaType.LJAVA_LANG_BYTE;
            case ConstantJavaType.ABBR_SHORT -> ConstantJavaType.LJAVA_LANG_SHORT;
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_LANG_INTEGER;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_LANG_LONG;
            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaType.LJAVA_LANG_FLOAT;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_LANG_DOUBLE;
            case ConstantJavaType.ABBR_CHARACTER -> ConstantJavaType.LJAVA_LANG_CHARACTER;
            case ConstantJavaType.ABBR_BOOLEAN -> ConstantJavaType.LJAVA_LANG_BOOLEAN;
            default -> primitiveType; // Already a wrapper or reference type
        };
    }

    /**
     * Checks if the given primitive type descriptor is an integer-category type (I, J, B, S, C).
     *
     * @param primitiveType the primitive type descriptor
     * @return true if integer-category
     */
    public static boolean isIntegerPrimitive(String primitiveType) {
        return INTEGER_PRIMITIVES.contains(primitiveType);
    }

    /**
     * Checks if the given primitive type descriptor is a numeric type (I, J, F, D, B, S, C).
     *
     * @param primitiveType the primitive type descriptor
     * @return true if numeric
     */
    public static boolean isNumericPrimitive(String primitiveType) {
        return NUMERIC_PRIMITIVES.contains(primitiveType);
    }

    /**
     * Is primitive type boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isPrimitiveType(String type) {
        return PRIMITIVE_TYPES.contains(type);
    }

    /**
     * Pops a value from the stack based on its type descriptor.
     * No-op for void, {@code pop2} for long/double, {@code pop} for everything else.
     *
     * @param code the code builder
     * @param type the JVM type descriptor
     */
    public static void popByType(CodeBuilder code, String type) {
        if (ConstantJavaType.ABBR_VOID.equals(type)) return;
        if (ConstantJavaType.ABBR_LONG.equals(type) || ConstantJavaType.ABBR_DOUBLE.equals(type)) {
            code.pop2();
        } else {
            code.pop();
        }
    }

    /**
     * Unbox wrapper type.
     *
     * @param code        the code
     * @param classWriter the class writer
     * @param type        the type
     */
    public static void unboxWrapperType(CodeBuilder code, ClassWriter classWriter, String type) {
        var cp = classWriter.getConstantPool();
        switch (type) {
            case ConstantJavaType.LJAVA_LANG_BOOLEAN -> {
                int booleanValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BOOLEAN, ConstantJavaMethod.METHOD_BOOLEAN_VALUE, ConstantJavaDescriptor.DESCRIPTOR___Z);
                code.invokevirtual(booleanValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_INTEGER -> {
                int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                code.invokevirtual(intValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_CHARACTER -> {
                int charValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_CHARACTER, ConstantJavaMethod.METHOD_CHAR_VALUE, ConstantJavaDescriptor.DESCRIPTOR___C);
                code.invokevirtual(charValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_BYTE -> {
                int byteValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_BYTE_VALUE, ConstantJavaDescriptor.DESCRIPTOR___B);
                code.invokevirtual(byteValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_LONG -> {
                int longValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_LONG_VALUE, ConstantJavaDescriptor.DESCRIPTOR___J);
                code.invokevirtual(longValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_SHORT -> {
                int shortValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_SHORT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___S);
                code.invokevirtual(shortValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_FLOAT -> {
                int floatValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_FLOAT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___F);
                code.invokevirtual(floatValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_DOUBLE -> {
                int doubleValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_DOUBLE_VALUE, ConstantJavaDescriptor.DESCRIPTOR___D);
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
