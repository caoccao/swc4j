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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr;

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionHelper;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class UnaryExpressionGenerator {
    private UnaryExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUnaryExpr unaryExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        Swc4jAstUnaryOp op = unaryExpr.getOp();

        switch (op) {
            case Bang -> {
                // Handle logical NOT operator (!)
                ISwc4jAstExpr arg = unaryExpr.getArg();
                String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

                // Bang operator requires boolean operand
                if ("Z".equals(argType) || "Ljava/lang/Boolean;".equals(argType)) {
                    // Generate the operand
                    ExpressionGenerator.generate(code, cp, arg, null, context, options);

                    // Unbox if wrapper type
                    TypeConversionHelper.unboxWrapperType(code, cp, argType);

                    // Invert the boolean value using ifeq
                    // If value == 0 (false), jump to TRUE_LABEL and push 1 (true)
                    // Otherwise push 0 (false)
                    code.ifeq(0); // Placeholder offset
                    int ifeqOffsetPos = code.getCurrentOffset() - 2;
                    int ifeqOpcodePos = code.getCurrentOffset() - 3;

                    code.iconst(0); // Value was true (1), push false (0)

                    code.gotoLabel(0); // Placeholder offset
                    int gotoOffsetPos = code.getCurrentOffset() - 2;
                    int gotoOpcodePos = code.getCurrentOffset() - 3;

                    int trueLabel = code.getCurrentOffset();
                    code.iconst(1); // Value was false (0), push true (1)

                    int endLabel = code.getCurrentOffset();

                    // Patch offsets
                    int ifeqOffset = trueLabel - ifeqOpcodePos;
                    code.patchShort(ifeqOffsetPos, ifeqOffset);

                    int gotoOffset = endLabel - gotoOpcodePos;
                    code.patchShort(gotoOffsetPos, gotoOffset);
                } else {
                    throw new Swc4jByteCodeCompilerException(
                            "Logical NOT (!) requires boolean operand, got: " + argType);
                }
            }
            case Delete -> {
                // Handle delete operator (e.g., delete arr[1])
                ISwc4jAstExpr arg = unaryExpr.getArg();
                if (arg instanceof Swc4jAstMemberExpr memberExpr) {
                    String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

                    if (objType != null && objType.startsWith("[")) {
                        // Java array - delete not supported
                        throw new Swc4jByteCodeCompilerException("Delete operator not supported on Java arrays - arrays have fixed size");
                    } else if ("Ljava/util/ArrayList;".equals(objType)) {
                        if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                            // delete arr[index] -> arr.remove(index)
                            ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                            ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]

                            // Call ArrayList.remove(int)
                            int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                            code.invokevirtual(removeMethod); // Stack: [removedObject]
                            // Delete expression returns true in JavaScript, but we'll just leave the removed object
                            // Actually, delete should return boolean true
                            code.pop(); // Pop the removed object
                            code.iconst(1); // Push true (1)
                            return;
                        }
                    }
                }
                throw new Swc4jByteCodeCompilerException("Delete operator not yet supported for: " + arg);
            }
            case Minus -> {
                // Handle numeric negation
                ISwc4jAstExpr arg = unaryExpr.getArg();

                if (arg instanceof Swc4jAstNumber number) {
                    // Directly generate the negated value
                    double value = number.getValue();

                    // Check if we're dealing with a long type
                    if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
                        long longValue = -(long) value;
                        if (longValue == 0L || longValue == 1L) {
                            code.lconst(longValue);
                        } else {
                            int longIndex = cp.addLong(longValue);
                            code.ldc2_w(longIndex);
                        }
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Long wrapper
                        long longValue = -(long) value;
                        if (longValue == 0L || longValue == 1L) {
                            code.lconst(longValue);
                        } else {
                            int longIndex = cp.addLong(longValue);
                            code.ldc2_w(longIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with an Integer wrapper
                        int intValue = -(int) value;
                        if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                            code.iconst(intValue);
                        } else {
                            int intIndex = cp.addInteger(intValue);
                            code.ldc(intIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Byte;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Byte wrapper
                        byte byteValue = (byte) -(int) value;
                        code.iconst(byteValue);
                        int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Short wrapper
                        short shortValue = (short) -(int) value;
                        code.iconst(shortValue);
                        int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Float wrapper
                        float floatValue = -(float) value;
                        if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                            code.fconst(floatValue);
                        } else {
                            int floatIndex = cp.addFloat(floatValue);
                            code.ldc(floatIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Double wrapper
                        double doubleValue = -value;
                        if (doubleValue == 0.0 || doubleValue == 1.0) {
                            code.dconst(doubleValue);
                        } else {
                            int doubleIndex = cp.addDouble(doubleValue);
                            code.ldc2_w(doubleIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        code.invokestatic(valueOfRef);
                    } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                        // Integer value
                        int intValue = -(int) value;
                        // Check if value fits in the range supported by iconst/bipush/sipush
                        if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                            code.iconst(intValue);
                        } else {
                            // Use ldc for values outside sipush range
                            int intIndex = cp.addInteger(intValue);
                            code.ldc(intIndex);
                        }
                    } else {
                        // Floating point value - need to determine if it's float or double
                        // Check context to infer the type
                        String targetType = "D"; // Default to double
                        if (returnTypeInfo != null) {
                            if (returnTypeInfo.type() == ReturnType.FLOAT) {
                                targetType = "F";
                            } else if (returnTypeInfo.type() == ReturnType.DOUBLE) {
                                targetType = "D";
                            } else if (returnTypeInfo.descriptor() != null) {
                                if (returnTypeInfo.descriptor().equals("F")) {
                                    targetType = "F";
                                } else if (returnTypeInfo.descriptor().equals("D")) {
                                    targetType = "D";
                                }
                            }
                        }

                        if ("F".equals(targetType)) {
                            // Float type
                            float floatValue = -(float) value;
                            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                                code.fconst(floatValue);
                            } else {
                                int floatIndex = cp.addFloat(floatValue);
                                code.ldc(floatIndex);
                            }
                        } else {
                            // Double type (default)
                            double doubleValue = -value;
                            if (doubleValue == 0.0 || doubleValue == 1.0) {
                                code.dconst(doubleValue);
                            } else {
                                int doubleIndex = cp.addDouble(doubleValue);
                                code.ldc2_w(doubleIndex);
                            }
                        }
                    }
                } else {
                    // For complex expressions, generate the expression first then negate
                    ExpressionGenerator.generate(code, cp, arg, null, context, options);

                    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);
                    // Handle null type - should not happen for negation, default to int
                    if (argType == null) argType = "I";

                    // Check if argType is a wrapper before unboxing
                    boolean isWrapper = !argType.equals(TypeConversionHelper.getPrimitiveType(argType));

                    // Unbox wrapper types before negation
                    TypeConversionHelper.unboxWrapperType(code, cp, argType);

                    // Get the primitive type for determining which negation instruction to use
                    String primitiveType = TypeConversionHelper.getPrimitiveType(argType);

                    switch (primitiveType) {
                        case "D" -> code.dneg();
                        case "F" -> code.fneg();
                        case "J" -> code.lneg();
                        default -> code.ineg();
                    }

                    // Box back to wrapper type if original was wrapper
                    if (isWrapper) {
                        TypeConversionHelper.boxPrimitiveType(code, cp, primitiveType, argType);
                    }
                }
            }
        }
    }
}
