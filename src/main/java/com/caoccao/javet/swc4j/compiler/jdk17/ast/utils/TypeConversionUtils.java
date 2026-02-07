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

import java.util.Set;

/**
 * The type Type conversion utils.
 */
public final class TypeConversionUtils {
    /**
     * The constant ABBR_BOOLEAN.
     */
    public static final String ABBR_BOOLEAN = "Z";
    /**
     * The constant ABBR_BYTE.
     */
    public static final String ABBR_BYTE = "B";
    /**
     * The constant ABBR_CHARACTER.
     */
    public static final String ABBR_CHARACTER = "C";
    /**
     * The constant ABBR_DOUBLE.
     */
    public static final String ABBR_DOUBLE = "D";
    /**
     * The constant ABBR_FLOAT.
     */
    public static final String ABBR_FLOAT = "F";
    /**
     * The constant ABBR_INTEGER.
     */
    public static final String ABBR_INTEGER = "I";
    /**
     * The constant ABBR_LONG.
     */
    public static final String ABBR_LONG = "J";
    /**
     * The constant ABBR_SHORT.
     */
    public static final String ABBR_SHORT = "S";
    /**
     * The constant ABBR_VOID.
     */
    public static final String ABBR_VOID = "V";
    /**
     * The constant ARRAY_PREFIX.
     */
    public static final String ARRAY_PREFIX = "[";
    /**
     * The constant DESCRIPTER_B__LJAVA_LANG_BYTE.
     */
    public static final String DESCRIPTER_B__LJAVA_LANG_BYTE = "(B)Ljava/lang/Byte;";
    /**
     * The constant DESCRIPTER_C__LJAVA_LANG_CHARACTER.
     */
    public static final String DESCRIPTER_C__LJAVA_LANG_CHARACTER = "(C)Ljava/lang/Character;";
    /**
     * The constant DESCRIPTER_D__LJAVA_LANG_DOUBLE.
     */
    public static final String DESCRIPTER_D__LJAVA_LANG_DOUBLE = "(D)Ljava/lang/Double;";
    /**
     * The constant DESCRIPTER_F__LJAVA_LANG_FLOAT.
     */
    public static final String DESCRIPTER_F__LJAVA_LANG_FLOAT = "(F)Ljava/lang/Float;";
    /**
     * The constant DESCRIPTER_I__LJAVA_LANG_INTEGER.
     */
    public static final String DESCRIPTER_I__LJAVA_LANG_INTEGER = "(I)Ljava/lang/Integer;";
    /**
     * The constant DESCRIPTER_J__LJAVA_LANG_LONG.
     */
    public static final String DESCRIPTER_J__LJAVA_LANG_LONG = "(J)Ljava/lang/Long;";
    /**
     * The constant DESCRIPTER_S__LJAVA_LANG_SHORT.
     */
    public static final String DESCRIPTER_S__LJAVA_LANG_SHORT = "(S)Ljava/lang/Short;";
    /**
     * The constant DESCRIPTER_Z__LJAVA_LANG_BOOLEAN.
     */
    public static final String DESCRIPTER_Z__LJAVA_LANG_BOOLEAN = "(Z)Ljava/lang/Boolean;";
    /**
     * The constant DESCRIPTER___B.
     */
    public static final String DESCRIPTER___B = "()B";
    /**
     * The constant DESCRIPTER___C.
     */
    public static final String DESCRIPTER___C = "()C";
    /**
     * The constant DESCRIPTER___D.
     */
    public static final String DESCRIPTER___D = "()D";
    /**
     * The constant DESCRIPTER___F.
     */
    public static final String DESCRIPTER___F = "()F";
    /**
     * The constant DESCRIPTER___I.
     */
    public static final String DESCRIPTER___I = "()I";
    /**
     * The constant DESCRIPTER___J.
     */
    public static final String DESCRIPTER___J = "()J";
    /**
     * The constant DESCRIPTER___S.
     */
    public static final String DESCRIPTER___S = "()S";
    /**
     * The constant DESCRIPTER___Z.
     */
    public static final String DESCRIPTER___Z = "()Z";
    /**
     * The constant JAVA_LANG_BOOLEAN.
     */
    public static final String JAVA_LANG_BOOLEAN = "java/lang/Boolean";
    /**
     * The constant JAVA_LANG_BYTE.
     */
    public static final String JAVA_LANG_BYTE = "java/lang/Byte";
    /**
     * The constant JAVA_LANG_CHARACTER.
     */
    public static final String JAVA_LANG_CHARACTER = "java/lang/Character";
    /**
     * The constant JAVA_LANG_DOUBLE.
     */
    public static final String JAVA_LANG_DOUBLE = "java/lang/Double";
    /**
     * The constant JAVA_LANG_FLOAT.
     */
    public static final String JAVA_LANG_FLOAT = "java/lang/Float";
    /**
     * The constant JAVA_LANG_INTEGER.
     */
    public static final String JAVA_LANG_INTEGER = "java/lang/Integer";
    /**
     * The constant JAVA_LANG_LONG.
     */
    public static final String JAVA_LANG_LONG = "java/lang/Long";
    /**
     * The constant JAVA_LANG_SHORT.
     */
    public static final String JAVA_LANG_SHORT = "java/lang/Short";
    /**
     * The constant LJAVA_LANG_.
     */
    public static final String LJAVA_LANG_ = "Ljava/lang/";
    /**
     * The constant LJAVA_LANG_BOOLEAN.
     */
    public static final String LJAVA_LANG_BOOLEAN = "Ljava/lang/Boolean;";
    /**
     * The constant LJAVA_LANG_BYTE.
     */
    public static final String LJAVA_LANG_BYTE = "Ljava/lang/Byte;";
    /**
     * The constant LJAVA_LANG_CHARACTER.
     */
    public static final String LJAVA_LANG_CHARACTER = "Ljava/lang/Character;";
    /**
     * The constant LJAVA_LANG_DOUBLE.
     */
    public static final String LJAVA_LANG_DOUBLE = "Ljava/lang/Double;";
    /**
     * The constant LJAVA_LANG_FLOAT.
     */
    public static final String LJAVA_LANG_FLOAT = "Ljava/lang/Float;";
    /**
     * The constant LJAVA_LANG_INTEGER.
     */
    public static final String LJAVA_LANG_INTEGER = "Ljava/lang/Integer;";
    /**
     * The constant LJAVA_LANG_LONG.
     */
    public static final String LJAVA_LANG_LONG = "Ljava/lang/Long;";
    /**
     * The constant LJAVA_LANG_OBJECT.
     */
    public static final String LJAVA_LANG_OBJECT = "Ljava/lang/Object;";
    /**
     * The constant LJAVA_LANG_SHORT.
     */
    public static final String LJAVA_LANG_SHORT = "Ljava/lang/Short;";
    /**
     * The constant LJAVA_LANG_STRING.
     */
    public static final String LJAVA_LANG_STRING = "Ljava/lang/String;";
    /**
     * The constant LJAVA_MATH_BIGINTEGER.
     */
    public static final String LJAVA_MATH_BIGINTEGER = "Ljava/math/BigInteger;";
    /**
     * The constant TYPEOF_BOOLEAN.
     */
    public static final String TYPEOF_BOOLEAN = "boolean";
    /**
     * The constant TYPEOF_NUMBER.
     */
    public static final String TYPEOF_NUMBER = "number";
    /**
     * The constant TYPEOF_OBJECT.
     */
    public static final String TYPEOF_OBJECT = "object";
    /**
     * The constant TYPEOF_STRING.
     */
    public static final String TYPEOF_STRING = "string";
    /**
     * The constant TYPEOF_UNDEFINED.
     */
    public static final String TYPEOF_UNDEFINED = "undefined";
    private static final String BOOLEAN_VALUE = "booleanValue";
    private static final String BYTE_VALUE = "byteValue";
    private static final String CHAR_VALUE = "charValue";
    private static final String DOUBLE_VALUE = "doubleValue";
    private static final String FLOAT_VALUE = "floatValue";
    private static final Set<String> INTEGER_PRIMITIVES = Set.of(ABBR_INTEGER, ABBR_LONG, ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER);
    private static final String INT_VALUE = "intValue";
    private static final String LONG_VALUE = "longValue";
    private static final Set<String> NUMERIC_PRIMITIVES = Set.of(ABBR_INTEGER, ABBR_LONG, ABBR_FLOAT, ABBR_DOUBLE, ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER);
    private static final Set<String> PRIMITIVE_TYPES = Set.of(ABBR_INTEGER, ABBR_BOOLEAN, ABBR_BYTE, ABBR_CHARACTER, ABBR_SHORT, ABBR_LONG, ABBR_FLOAT, ABBR_DOUBLE);
    private static final String SHORT_VALUE = "shortValue";
    private static final String VALUE_OF = "valueOf";

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
        if (!targetType.startsWith(LJAVA_LANG_)) {
            return; // Target is primitive, no boxing needed
        }

        switch (primitiveType) {
            case ABBR_INTEGER -> {
                if (LJAVA_LANG_INTEGER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_INTEGER, VALUE_OF, DESCRIPTER_I__LJAVA_LANG_INTEGER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_BOOLEAN -> {
                if (LJAVA_LANG_BOOLEAN.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_BOOLEAN, VALUE_OF, DESCRIPTER_Z__LJAVA_LANG_BOOLEAN);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_BYTE -> {
                if (LJAVA_LANG_BYTE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_BYTE, VALUE_OF, DESCRIPTER_B__LJAVA_LANG_BYTE);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_CHARACTER -> {
                if (LJAVA_LANG_CHARACTER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_CHARACTER, VALUE_OF, DESCRIPTER_C__LJAVA_LANG_CHARACTER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_SHORT -> {
                if (LJAVA_LANG_SHORT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_SHORT, VALUE_OF, DESCRIPTER_S__LJAVA_LANG_SHORT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_LONG -> {
                if (LJAVA_LANG_LONG.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_LONG, VALUE_OF, DESCRIPTER_J__LJAVA_LANG_LONG);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_FLOAT -> {
                if (LJAVA_LANG_FLOAT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_FLOAT, VALUE_OF, DESCRIPTER_F__LJAVA_LANG_FLOAT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_DOUBLE -> {
                if (LJAVA_LANG_DOUBLE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_DOUBLE, VALUE_OF, DESCRIPTER_D__LJAVA_LANG_DOUBLE);
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
            case ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER -> ABBR_INTEGER;
            default -> fromType;
        };

        // Convert from stack type to target type
        switch (stackFromType) {
            case ABBR_INTEGER -> {
                switch (toType) {
                    case ABBR_BYTE -> code.i2b();
                    case ABBR_CHARACTER -> code.i2c();
                    case ABBR_SHORT -> code.i2s();
                    case ABBR_LONG -> code.i2l();
                    case ABBR_FLOAT -> code.i2f();
                    case ABBR_DOUBLE -> code.i2d();
                }
            }
            case ABBR_LONG -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.l2i();
                    case ABBR_FLOAT -> code.l2f();
                    case ABBR_DOUBLE -> code.l2d();
                }
            }
            case ABBR_FLOAT -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.f2i();
                    case ABBR_LONG -> code.f2l();
                    case ABBR_DOUBLE -> code.f2d();
                }
            }
            case ABBR_DOUBLE -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.d2i();
                    case ABBR_LONG -> code.d2l();
                    case ABBR_FLOAT -> code.d2f();
                }
            }
        }
    }

    /**
     * Gets primitive type.
     *
     * @param type the type
     * @return the primitive type
     */
    public static String getPrimitiveType(String type) {
        return switch (type) {
            case LJAVA_LANG_BYTE -> ABBR_BYTE;
            case LJAVA_LANG_SHORT -> ABBR_SHORT;
            case LJAVA_LANG_INTEGER -> ABBR_INTEGER;
            case LJAVA_LANG_LONG -> ABBR_LONG;
            case LJAVA_LANG_FLOAT -> ABBR_FLOAT;
            case LJAVA_LANG_DOUBLE -> ABBR_DOUBLE;
            case LJAVA_LANG_CHARACTER -> ABBR_CHARACTER;
            default -> type;
        };
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
        if (ABBR_VOID.equals(type)) return TYPEOF_UNDEFINED;
        if (LJAVA_LANG_STRING.equals(type)) return TYPEOF_STRING;
        if (LJAVA_MATH_BIGINTEGER.equals(type)) return TYPEOF_NUMBER;
        if (type.startsWith(ARRAY_PREFIX)) return TYPEOF_OBJECT;
        String primitiveType = getPrimitiveType(type);
        if (ABBR_BOOLEAN.equals(primitiveType)) return TYPEOF_BOOLEAN;
        if (isNumericPrimitive(primitiveType)) return TYPEOF_NUMBER;
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
            case ABBR_BYTE -> LJAVA_LANG_BYTE;
            case ABBR_SHORT -> LJAVA_LANG_SHORT;
            case ABBR_INTEGER -> LJAVA_LANG_INTEGER;
            case ABBR_LONG -> LJAVA_LANG_LONG;
            case ABBR_FLOAT -> LJAVA_LANG_FLOAT;
            case ABBR_DOUBLE -> LJAVA_LANG_DOUBLE;
            case ABBR_CHARACTER -> LJAVA_LANG_CHARACTER;
            case ABBR_BOOLEAN -> LJAVA_LANG_BOOLEAN;
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
        if (ABBR_VOID.equals(type)) return;
        if (ABBR_LONG.equals(type) || ABBR_DOUBLE.equals(type)) {
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
            case LJAVA_LANG_BOOLEAN -> {
                int booleanValueRef = cp.addMethodRef(JAVA_LANG_BOOLEAN, BOOLEAN_VALUE, DESCRIPTER___Z);
                code.invokevirtual(booleanValueRef);
            }
            case LJAVA_LANG_INTEGER -> {
                int intValueRef = cp.addMethodRef(JAVA_LANG_INTEGER, INT_VALUE, DESCRIPTER___I);
                code.invokevirtual(intValueRef);
            }
            case LJAVA_LANG_CHARACTER -> {
                int charValueRef = cp.addMethodRef(JAVA_LANG_CHARACTER, CHAR_VALUE, DESCRIPTER___C);
                code.invokevirtual(charValueRef);
            }
            case LJAVA_LANG_BYTE -> {
                int byteValueRef = cp.addMethodRef(JAVA_LANG_BYTE, BYTE_VALUE, DESCRIPTER___B);
                code.invokevirtual(byteValueRef);
            }
            case LJAVA_LANG_LONG -> {
                int longValueRef = cp.addMethodRef(JAVA_LANG_LONG, LONG_VALUE, DESCRIPTER___J);
                code.invokevirtual(longValueRef);
            }
            case LJAVA_LANG_SHORT -> {
                int shortValueRef = cp.addMethodRef(JAVA_LANG_SHORT, SHORT_VALUE, DESCRIPTER___S);
                code.invokevirtual(shortValueRef);
            }
            case LJAVA_LANG_FLOAT -> {
                int floatValueRef = cp.addMethodRef(JAVA_LANG_FLOAT, FLOAT_VALUE, DESCRIPTER___F);
                code.invokevirtual(floatValueRef);
            }
            case LJAVA_LANG_DOUBLE -> {
                int doubleValueRef = cp.addMethodRef(JAVA_LANG_DOUBLE, DOUBLE_VALUE, DESCRIPTER___D);
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
