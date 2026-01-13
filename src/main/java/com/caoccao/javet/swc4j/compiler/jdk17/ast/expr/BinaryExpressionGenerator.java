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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringConcatHelper;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionHelper;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class BinaryExpressionGenerator {
    private BinaryExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        String resultType = null;
        switch (binExpr.getOp()) {
            case Add -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Check if this is string concatenation
                if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                    StringConcatHelper.generateConcat(
                            code,
                            cp,
                            binExpr.getLeft(), binExpr.getRight(),
                            leftType,
                            rightType,
                            context,
                            options,
                            ExpressionGenerator::generate);
                    resultType = "Ljava/lang/String;";
                } else {
                    // Determine the widened result type
                    resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                    TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                    TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                    // Generate appropriate add instruction based on result type
                    switch (resultType) {
                        case "I" -> code.iadd();
                        case "J" -> code.ladd();
                        case "F" -> code.fadd();
                        case "D" -> code.dadd();
                    }
                }
            }
            case Sub -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate sub instruction based on result type
                switch (resultType) {
                    case "I" -> code.isub();
                    case "J" -> code.lsub();
                    case "F" -> code.fsub();
                    case "D" -> code.dsub();
                }
            }
            case Mul -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate mul instruction based on result type
                switch (resultType) {
                    case "I" -> code.imul();
                    case "J" -> code.lmul();
                    case "F" -> code.fmul();
                    case "D" -> code.dmul();
                }
            }
            case Div -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate div instruction based on result type
                switch (resultType) {
                    case "I" -> code.idiv();
                    case "J" -> code.ldiv();
                    case "F" -> code.fdiv();
                    case "D" -> code.ddiv();
                }
            }
            case Mod -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate rem instruction based on result type
                switch (resultType) {
                    case "I" -> code.irem();
                    case "J" -> code.lrem();
                    case "F" -> code.frem();
                    case "D" -> code.drem();
                }
            }
            case Exp -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";
                resultType = "D"; // Math.pow returns double

                // Generate left operand (base) and convert to double
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), "D");

                // Generate right operand (exponent) and convert to double
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), "D");

                // Call Math.pow(double, double)
                int mathPowRef = cp.addMethodRef("java/lang/Math", "pow", "(DD)D");
                code.invokestatic(mathPowRef);
            }
            case LShift -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the result type based on left operand
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand and convert to result type (int or long)
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand (shift amount) and convert to int
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), "I");

                // Generate appropriate shift instruction based on result type
                switch (resultType) {
                    case "I" -> code.ishl();
                    case "J" -> code.lshl();
                    default -> {
                        // For other types (byte, short, char, float, double), convert to int first
                        // This matches JavaScript ToInt32 semantics
                        code.ishl();
                    }
                }
            }
            case RShift -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the result type based on left operand
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand and convert to result type (int or long)
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand (shift amount) and convert to int
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), "I");

                // Generate appropriate shift instruction based on result type
                switch (resultType) {
                    case "I" -> code.ishr();
                    case "J" -> code.lshr();
                    default -> {
                        // For other types (byte, short, char, float, double), convert to int first
                        // This matches JavaScript ToInt32 semantics
                        code.ishr();
                    }
                }
            }
            case ZeroFillRShift -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the result type based on left operand
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand and convert to result type (int or long)
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand (shift amount) and convert to int
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), "I");

                // Generate appropriate unsigned shift instruction based on result type
                switch (resultType) {
                    case "I" -> code.iushr();
                    case "J" -> code.lushr();
                    default -> {
                        // For other types (byte, short, char, float, double), convert to int first
                        // This matches JavaScript ToInt32 semantics
                        code.iushr();
                    }
                }
            }
            case BitAnd -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate bitwise AND instruction based on result type
                switch (resultType) {
                    case "I" -> code.iand();
                    case "J" -> code.land();
                }
            }
            case BitOr -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate bitwise OR instruction based on result type
                switch (resultType) {
                    case "I" -> code.ior();
                    case "J" -> code.lor();
                }
            }
            case BitXor -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Determine the widened result type
                resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(leftType), resultType);

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                TypeConversionHelper.convertPrimitiveType(code, TypeConversionHelper.getPrimitiveType(rightType), resultType);

                // Generate appropriate bitwise XOR instruction based on result type
                switch (resultType) {
                    case "I" -> code.ixor();
                    case "J" -> code.lxor();
                }
            }
        }
        if (returnTypeInfo != null && resultType != null) {
            String targetType = returnTypeInfo.getPrimitiveTypeDescriptor();
            if (targetType != null && !targetType.equals(resultType)) {
                TypeConversionHelper.convertPrimitiveType(code, resultType, targetType);
            }
        }
    }
}
