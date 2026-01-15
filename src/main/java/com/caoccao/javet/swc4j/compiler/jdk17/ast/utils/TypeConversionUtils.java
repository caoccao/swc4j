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

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;

public final class TypeConversionUtils {
    private TypeConversionUtils() {
    }

    public static void boxPrimitiveType(CodeBuilder code, ClassWriter.ConstantPool cp, String primitiveType, String targetType) {
        // Only box if targetType is a wrapper
        if (!targetType.startsWith("Ljava/lang/")) {
            return; // Target is primitive, no boxing needed
        }

        switch (primitiveType) {
            case "I" -> {
                if ("Ljava/lang/Integer;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "Z" -> {
                if ("Ljava/lang/Boolean;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "B" -> {
                if ("Ljava/lang/Byte;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "C" -> {
                if ("Ljava/lang/Character;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "S" -> {
                if ("Ljava/lang/Short;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "J" -> {
                if ("Ljava/lang/Long;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "F" -> {
                if ("Ljava/lang/Float;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "D" -> {
                if ("Ljava/lang/Double;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                    code.invokestatic(valueOfRef);
                }
            }
        }
    }

    public static void convertPrimitiveType(CodeBuilder code, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return; // No conversion needed
        }

        // byte, short, char are stored as int on the stack, so they start as "I" after unboxing
        String stackFromType = switch (fromType) {
            case "B", "S", "C" -> "I";
            default -> fromType;
        };

        // Convert from stack type to target type
        switch (stackFromType) {
            case "I" -> {
                switch (toType) {
                    case "J" -> code.i2l();
                    case "F" -> code.i2f();
                    case "D" -> code.i2d();
                }
            }
            case "J" -> {
                switch (toType) {
                    case "I" -> code.l2i();
                    case "F" -> code.l2f();
                    case "D" -> code.l2d();
                }
            }
            case "F" -> {
                switch (toType) {
                    case "I" -> code.f2i();
                    case "J" -> code.f2l();
                    case "D" -> code.f2d();
                }
            }
            case "D" -> {
                switch (toType) {
                    case "I" -> code.d2i();
                    case "J" -> code.d2l();
                    case "F" -> code.d2f();
                }
            }
        }
    }

    public static String getPrimitiveType(String type) {
        return switch (type) {
            case "Ljava/lang/Byte;" -> "B";
            case "Ljava/lang/Short;" -> "S";
            case "Ljava/lang/Integer;" -> "I";
            case "Ljava/lang/Long;" -> "J";
            case "Ljava/lang/Float;" -> "F";
            case "Ljava/lang/Double;" -> "D";
            case "Ljava/lang/Character;" -> "C";
            default -> type;
        };
    }

    public static String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "B" -> "Ljava/lang/Byte;";
            case "S" -> "Ljava/lang/Short;";
            case "I" -> "Ljava/lang/Integer;";
            case "J" -> "Ljava/lang/Long;";
            case "F" -> "Ljava/lang/Float;";
            case "D" -> "Ljava/lang/Double;";
            case "C" -> "Ljava/lang/Character;";
            case "Z" -> "Ljava/lang/Boolean;";
            default -> primitiveType; // Already a wrapper or reference type
        };
    }

    public static boolean isPrimitiveType(String type) {
        return "I".equals(type) || "Z".equals(type) || "B".equals(type) ||
                "C".equals(type) || "S".equals(type) || "J".equals(type) ||
                "F".equals(type) || "D".equals(type);
    }

    public static void unboxWrapperType(CodeBuilder code, ClassWriter.ConstantPool cp, String type) {
        switch (type) {
            case "Ljava/lang/Integer;" -> {
                int intValueRef = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueRef);
            }
            case "Ljava/lang/Character;" -> {
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                code.invokevirtual(charValueRef);
            }
            case "Ljava/lang/Byte;" -> {
                int byteValueRef = cp.addMethodRef("java/lang/Byte", "byteValue", "()B");
                code.invokevirtual(byteValueRef);
            }
            case "Ljava/lang/Long;" -> {
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
            }
            case "Ljava/lang/Short;" -> {
                int shortValueRef = cp.addMethodRef("java/lang/Short", "shortValue", "()S");
                code.invokevirtual(shortValueRef);
            }
            case "Ljava/lang/Float;" -> {
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
            }
            case "Ljava/lang/Double;" -> {
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
