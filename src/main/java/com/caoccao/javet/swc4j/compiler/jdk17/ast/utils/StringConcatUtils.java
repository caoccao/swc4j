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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

public final class StringConcatUtils {
    private StringConcatUtils() {
    }

    public static void appendOperandToStringBuilder(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String operandType,
            int appendString,
            int appendInt,
            int appendChar) throws Swc4jByteCodeCompilerException {
        switch (operandType) {
            case "Ljava/lang/String;" -> code.invokevirtual(appendString);
            case "I", "B", "S" -> code.invokevirtual(appendInt); // int, byte, short all use append(int)
            case "C" -> code.invokevirtual(appendChar);
            case "J" -> {
                // long
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "F" -> {
                // float
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "D" -> {
                // double
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Z" -> {
                // boolean
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            case "Ljava/lang/Character;" -> {
                // Unbox Character to char
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                code.invokevirtual(charValueRef);
                code.invokevirtual(appendChar);
            }
            case "Ljava/lang/Byte;", "Ljava/lang/Short;", "Ljava/lang/Integer;" -> {
                // Unbox to int, then append
                String wrapperClass = operandType.substring(1, operandType.length() - 1); // Remove L and ;
                String methodName = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "byteValue";
                    case "Ljava/lang/Short;" -> "shortValue";
                    case "Ljava/lang/Integer;" -> "intValue";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                String returnType = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "B";
                    case "Ljava/lang/Short;" -> "S";
                    case "Ljava/lang/Integer;" -> "I";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                int unboxRef = cp.addMethodRef(wrapperClass, methodName, "()" + returnType);
                code.invokevirtual(unboxRef);
                code.invokevirtual(appendInt); // byte, short, int all use append(int)
            }
            case "Ljava/lang/Long;" -> {
                // Unbox Long to long
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "Ljava/lang/Float;" -> {
                // Unbox Float to float
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "Ljava/lang/Double;" -> {
                // Unbox Double to double
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Ljava/lang/Boolean;" -> {
                // Unbox Boolean to boolean
                int booleanValueRef = cp.addMethodRef("java/lang/Boolean", "booleanValue", "()Z");
                code.invokevirtual(booleanValueRef);
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            default -> {
                // For any other object type, use append(Object)
                int appendObject = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendObject);
            }
        }
    }

    public static void collectOperands(
            ByteCodeCompiler compiler,
            ISwc4jAstExpr expr,
            List<ISwc4jAstExpr> operands,
            List<String> operandTypes) {
        // If this expression is a binary Add that results in a String, collect its operands
        if (expr instanceof Swc4jAstBinExpr binExpr && binExpr.getOp() == Swc4jAstBinaryOp.Add) {
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            if ("Ljava/lang/String;".equals(exprType)) {
                // This is a string concatenation - collect operands recursively
                collectOperands(compiler, binExpr.getLeft(), operands, operandTypes);
                collectOperands(compiler, binExpr.getRight(), operands, operandTypes);
                return;
            }
        }
        // Not a string concatenation - add this expression as an operand
        operands.add(expr);
        String operandType = compiler.getTypeResolver().inferTypeFromExpr(expr);
        // If type is null (e.g., for null literal), default to Object
        operandTypes.add(operandType != null ? operandType : "Ljava/lang/Object;");
    }

    public static void generateConcat(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            String leftType,
            String rightType,
            ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {
        // Use StringBuilder for string concatenation
        // new StringBuilder
        int stringBuilderClass = cp.addClass("java/lang/StringBuilder");
        int stringBuilderInit = cp.addMethodRef("java/lang/StringBuilder", "<init>", "()V");
        int appendString = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int appendInt = cp.addMethodRef("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
        int appendChar = cp.addMethodRef("java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        int toString = cp.addMethodRef("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

        code.newInstance(stringBuilderClass)
                .dup()
                .invokespecial(stringBuilderInit);

        // Flatten the operands - if left is also a string concatenation, collect all operands
        List<ISwc4jAstExpr> operands = new ArrayList<>();
        List<String> operandTypes = new ArrayList<>();

        // Collect operands from left side
        collectOperands(compiler, left, operands, operandTypes);

        // Add right operand
        operands.add(right);
        operandTypes.add(rightType);

        // Append all operands
        for (int i = 0; i < operands.size(); i++) {
            callback.generateExpr(compiler, code, cp, operands.get(i), null);
            appendOperandToStringBuilder(code, cp, operandTypes.get(i), appendString, appendInt, appendChar);
        }

        // Call toString()
        code.invokevirtual(toString);
    }

    @FunctionalInterface
    public interface ExpressionGeneratorCallback {
        void generateExpr(
                ByteCodeCompiler compiler,
                CodeBuilder code,
                ClassWriter.ConstantPool cp,
                ISwc4jAstExpr expr,
                ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException;
    }
}
