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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
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
            case EqEq, EqEqEq -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Determine the comparison type (widen to common type for primitives)
                String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                String leftPrimitive = TypeConversionHelper.getPrimitiveType(leftType);
                String rightPrimitive = TypeConversionHelper.getPrimitiveType(rightType);

                // Check if both are primitive types (NOT wrappers - those use Objects.equals)
                boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                        rightType.equals(rightPrimitive) &&
                        (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                leftPrimitive.equals("C") || leftPrimitive.equals("Z"));

                if (isPrimitiveComparison) {
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                    TypeConversionHelper.convertPrimitiveType(code, leftPrimitive, comparisonType);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                    TypeConversionHelper.convertPrimitiveType(code, rightPrimitive, comparisonType);

                    // Use direct bytecode comparison instructions
                    // Pattern: if not equal, jump to iconst_0, else fall through to iconst_1
                    switch (comparisonType) {
                        case "I", "Z" -> {
                            // int/boolean comparison: use if_icmpne (boolean values are represented as int on stack)
                            // if a != b, jump to push 0, else push 1
                            code.if_icmpne(7); // if not equal, jump to iconst_0
                            code.iconst(1);    // equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // not equal: push 0
                        }
                        case "J" -> {
                            // long comparison: use lcmp then ifne
                            code.lcmp();       // compare longs, result is 0 if equal
                            code.ifne(7);      // if non-zero (not equal), jump to iconst_0
                            code.iconst(1);    // equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // not equal: push 0
                        }
                        case "F" -> {
                            // float comparison: use fcmpl then ifne
                            code.fcmpl();      // compare floats, result is 0 if equal
                            code.ifne(7);      // if non-zero (not equal), jump to iconst_0
                            code.iconst(1);    // equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // not equal: push 0
                        }
                        case "D" -> {
                            // double comparison: use dcmpl then ifne
                            code.dcmpl();      // compare doubles, result is 0 if equal
                            code.ifne(7);      // if non-zero (not equal), jump to iconst_0
                            code.iconst(1);    // equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // not equal: push 0
                        }
                    }
                } else {
                    // Object comparison: use Objects.equals() for null-safe comparison
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);

                    // Objects.equals returns boolean (Z) which is represented as 0 or 1
                    int equalsRef = cp.addMethodRef("java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                    code.invokestatic(equalsRef);
                }
            }
            case NotEq, NotEqEq -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Determine the comparison type (widen to common type for primitives)
                String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                String leftPrimitive = TypeConversionHelper.getPrimitiveType(leftType);
                String rightPrimitive = TypeConversionHelper.getPrimitiveType(rightType);

                // Check if both are primitive types (NOT wrappers - those use Objects.equals)
                boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                        rightType.equals(rightPrimitive) &&
                        (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                leftPrimitive.equals("C") || leftPrimitive.equals("Z"));

                if (isPrimitiveComparison) {
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                    TypeConversionHelper.convertPrimitiveType(code, leftPrimitive, comparisonType);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                    TypeConversionHelper.convertPrimitiveType(code, rightPrimitive, comparisonType);

                    // Use direct bytecode comparison instructions (inverted logic from EqEq)
                    // Pattern: if equal, jump to iconst_0, else fall through to iconst_1
                    switch (comparisonType) {
                        case "I", "Z" -> {
                            // int/boolean comparison: use if_icmpeq (boolean values are represented as int on stack)
                            // if a == b, jump to push 0, else push 1
                            code.if_icmpeq(7); // if equal, jump to iconst_0
                            code.iconst(1);    // not equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // equal: push 0
                        }
                        case "J" -> {
                            // long comparison: use lcmp then ifeq (opposite of ifne)
                            code.lcmp();       // compare longs, result is 0 if equal
                            code.ifeq(7);      // if zero (equal), jump to iconst_0
                            code.iconst(1);    // not equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // equal: push 0
                        }
                        case "F" -> {
                            // float comparison: use fcmpl then ifeq (opposite of ifne)
                            code.fcmpl();      // compare floats, result is 0 if equal
                            code.ifeq(7);      // if zero (equal), jump to iconst_0
                            code.iconst(1);    // not equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // equal: push 0
                        }
                        case "D" -> {
                            // double comparison: use dcmpl then ifeq (opposite of ifne)
                            code.dcmpl();      // compare doubles, result is 0 if equal
                            code.ifeq(7);      // if zero (equal), jump to iconst_0
                            code.iconst(1);    // not equal: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // equal: push 0
                        }
                    }
                } else {
                    // Object comparison: use Objects.equals() then invert the result
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);

                    // Objects.equals returns boolean (Z) which is represented as 0 or 1
                    int equalsRef = cp.addMethodRef("java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                    code.invokestatic(equalsRef);

                    // Invert the result: 0 -> 1, 1 -> 0
                    // We can use: iconst_1, ixor (XOR with 1 flips the bit)
                    code.iconst(1);
                    code.ixor();
                }
            }
            case Lt, LtEq -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Determine the comparison type (widen to common type for primitives)
                String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                String leftPrimitive = TypeConversionHelper.getPrimitiveType(leftType);
                String rightPrimitive = TypeConversionHelper.getPrimitiveType(rightType);

                // Check if both are primitive types (NOT wrappers)
                boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                        rightType.equals(rightPrimitive) &&
                        (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                leftPrimitive.equals("C"));

                if (isPrimitiveComparison) {
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                    TypeConversionHelper.convertPrimitiveType(code, leftPrimitive, comparisonType);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                    TypeConversionHelper.convertPrimitiveType(code, rightPrimitive, comparisonType);

                    // Use direct bytecode comparison instructions
                    // Pattern: if condition is FALSE, jump to iconst_0, else fall through to iconst_1
                    boolean isLtEq = binExpr.getOp() == Swc4jAstBinaryOp.LtEq;
                    switch (comparisonType) {
                        case "I", "Z" -> {
                            // int/boolean comparison: use if_icmpge/if_icmpgt (boolean values are represented as int on stack)
                            if (isLtEq) {
                                code.if_icmpgt(7); // if a > b (NOT <=), jump to iconst_0
                            } else {
                                code.if_icmpge(7); // if a >= b (NOT <), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "J" -> {
                            // long comparison: use lcmp then ifge or ifgt
                            code.lcmp();       // compare longs, result is -1, 0, or 1
                            if (isLtEq) {
                                code.ifgt(7);  // if result > 0 (NOT <=), jump to iconst_0
                            } else {
                                code.ifge(7);  // if result >= 0 (NOT <), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "F" -> {
                            // float comparison: use fcmpl then ifge or ifgt
                            code.fcmpl();      // compare floats, result is -1, 0, or 1 (NaN -> 1)
                            if (isLtEq) {
                                code.ifgt(7);  // if result > 0 (NOT <=), jump to iconst_0
                            } else {
                                code.ifge(7);  // if result >= 0 (NOT <), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "D" -> {
                            // double comparison: use dcmpl then ifge or ifgt
                            code.dcmpl();      // compare doubles, result is -1, 0, or 1 (NaN -> 1)
                            if (isLtEq) {
                                code.ifgt(7);  // if result > 0 (NOT <=), jump to iconst_0
                            } else {
                                code.ifge(7);  // if result >= 0 (NOT <), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                    }
                } else {
                    // Object comparison: not supported for < or <=
                    throw new Swc4jByteCodeCompilerException(
                            "Less than comparison not supported for non-primitive types. " +
                                    "Use comparable types or implement Comparable interface.");
                }
            }
            case Gt, GtEq -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Determine the comparison type (widen to common type for primitives)
                String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                String leftPrimitive = TypeConversionHelper.getPrimitiveType(leftType);
                String rightPrimitive = TypeConversionHelper.getPrimitiveType(rightType);

                // Check if both are primitive types (NOT wrappers)
                boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                        rightType.equals(rightPrimitive) &&
                        (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                leftPrimitive.equals("C"));

                if (isPrimitiveComparison) {
                    // Generate left operand
                    ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, leftType);
                    TypeConversionHelper.convertPrimitiveType(code, leftPrimitive, comparisonType);

                    // Generate right operand
                    ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                    TypeConversionHelper.unboxWrapperType(code, cp, rightType);
                    TypeConversionHelper.convertPrimitiveType(code, rightPrimitive, comparisonType);

                    // Use direct bytecode comparison instructions
                    // Pattern: if condition is FALSE, jump to iconst_0, else fall through to iconst_1
                    boolean isGtEq = binExpr.getOp() == Swc4jAstBinaryOp.GtEq;
                    switch (comparisonType) {
                        case "I", "Z" -> {
                            // int/boolean comparison: use if_icmple/if_icmplt (boolean values are represented as int on stack)
                            if (isGtEq) {
                                code.if_icmplt(7); // if a < b (NOT >=), jump to iconst_0
                            } else {
                                code.if_icmple(7); // if a <= b (NOT >), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "J" -> {
                            // long comparison: use lcmp then iflt or ifle
                            code.lcmp();       // compare longs, result is -1, 0, or 1
                            if (isGtEq) {
                                code.iflt(7);  // if result < 0 (NOT >=), jump to iconst_0
                            } else {
                                code.ifle(7);  // if result <= 0 (NOT >), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "F" -> {
                            // float comparison: use fcmpl then iflt or ifle
                            code.fcmpl();      // compare floats, result is -1, 0, or 1 (NaN -> 1)
                            if (isGtEq) {
                                code.iflt(7);  // if result < 0 (NOT >=), jump to iconst_0
                            } else {
                                code.ifle(7);  // if result <= 0 (NOT >), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                        case "D" -> {
                            // double comparison: use dcmpl then iflt or ifle
                            code.dcmpl();      // compare doubles, result is -1, 0, or 1 (NaN -> 1)
                            if (isGtEq) {
                                code.iflt(7);  // if result < 0 (NOT >=), jump to iconst_0
                            } else {
                                code.ifle(7);  // if result <= 0 (NOT >), jump to iconst_0
                            }
                            code.iconst(1);    // condition true: push 1
                            code.gotoLabel(4); // jump over iconst_0
                            code.iconst(0);    // condition false: push 0
                        }
                    }
                } else {
                    // Object comparison: not supported for > or >=
                    throw new Swc4jByteCodeCompilerException(
                            "Greater than comparison not supported for non-primitive types. " +
                                    "Use comparable types or implement Comparable interface.");
                }
            }
            case LogicalAnd -> {
                // LogicalAnd (&&) with short-circuit evaluation
                // If left is false, skip evaluating right and return false
                // If left is true, return the right operand value

                // Result type is always boolean
                resultType = "Z";

                // Get types of operands
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                // Unbox if it's a Boolean object
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);

                // Short-circuit: if left is false (0), skip right evaluation
                // Pattern:
                //   [left expression]
                //   ifeq FALSE_LABEL    // if left == 0, jump to FALSE_LABEL
                //   [right expression]
                //   goto END_LABEL
                //   FALSE_LABEL:
                //   iconst_0
                //   END_LABEL:

                code.ifeq(0); // Placeholder, will patch offset later
                // After ifeq(0), the stream has: [opcode][offset_byte1][offset_byte2]
                // getCurrentOffset() points to the byte after the instruction
                // ifeqOpcode position = getCurrentOffset() - 3
                // ifeqOffset position = getCurrentOffset() - 2
                int ifeqOffsetPos = code.getCurrentOffset() - 2;
                int ifeqOpcodePos = code.getCurrentOffset() - 3;

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                // Unbox if it's a Boolean object
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);

                code.gotoLabel(0); // Placeholder for goto
                int gotoOffsetPos = code.getCurrentOffset() - 2;
                int gotoOpcodePos = code.getCurrentOffset() - 3;

                int falseLabel = code.getCurrentOffset();
                code.iconst(0); // Push false

                int endLabel = code.getCurrentOffset();

                // Calculate and patch the ifeq offset
                // JVM offset is relative to the opcode position
                int ifeqOffset = falseLabel - ifeqOpcodePos;
                code.patchShort(ifeqOffsetPos, ifeqOffset);

                // Calculate and patch the goto offset
                // JVM offset is relative to the opcode position
                int gotoOffset = endLabel - gotoOpcodePos;
                code.patchShort(gotoOffsetPos, gotoOffset);
            }
            case LogicalOr -> {
                // LogicalOr (||) with short-circuit evaluation
                // If left is true, skip evaluating right and return true
                // If left is false, evaluate and return the right operand value

                // Result type is always boolean
                resultType = "Z";

                // Get types of operands
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);

                // Generate left operand
                ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);
                // Unbox if it's a Boolean object
                TypeConversionHelper.unboxWrapperType(code, cp, leftType);

                // Short-circuit: if left is true (non-zero), skip right evaluation
                // Pattern:
                //   [left expression]
                //   ifne TRUE_LABEL    // if left != 0, jump to TRUE_LABEL
                //   [right expression]
                //   goto END_LABEL
                //   TRUE_LABEL:
                //   iconst_1
                //   END_LABEL:

                code.ifne(0); // Placeholder, will patch offset later
                // After ifne(0), the stream has: [opcode][offset_byte1][offset_byte2]
                // getCurrentOffset() points to the byte after the instruction
                // ifneOpcode position = getCurrentOffset() - 3
                // ifneOffset position = getCurrentOffset() - 2
                int ifneOffsetPos = code.getCurrentOffset() - 2;
                int ifneOpcodePos = code.getCurrentOffset() - 3;

                // Generate right operand
                ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);
                // Unbox if it's a Boolean object
                TypeConversionHelper.unboxWrapperType(code, cp, rightType);

                code.gotoLabel(0); // Placeholder for goto
                int gotoOrOffsetPos = code.getCurrentOffset() - 2;
                int gotoOrOpcodePos = code.getCurrentOffset() - 3;

                int trueLabel = code.getCurrentOffset();
                code.iconst(1); // Push true

                int endOrLabel = code.getCurrentOffset();

                // Calculate and patch the ifne offset
                // JVM offset is relative to the opcode position
                int ifneOffset = trueLabel - ifneOpcodePos;
                code.patchShort(ifneOffsetPos, ifneOffset);

                // Calculate and patch the goto offset
                // JVM offset is relative to the opcode position
                int gotoOrOffset = endOrLabel - gotoOrOpcodePos;
                code.patchShort(gotoOrOffsetPos, gotoOrOffset);
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
