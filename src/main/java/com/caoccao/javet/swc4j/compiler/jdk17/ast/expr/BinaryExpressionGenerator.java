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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringApiUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class BinaryExpressionGenerator extends BaseAstProcessor<Swc4jAstBinExpr> {
    public BinaryExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String resultType = null;
        switch (binExpr.getOp()) {
            case Add -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Check if this is string concatenation
                if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                    StringApiUtils.generateConcat(
                            compiler,
                            code,
                            cp,
                            binExpr.getLeft(), binExpr.getRight(),
                            leftType,
                            rightType);
                    resultType = "Ljava/lang/String;";
                } else if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger addition
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.add()
                    int addRef = cp.addMethodRef("java/math/BigInteger", "add", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(addRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

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
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger subtraction
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.subtract()
                    int subRef = cp.addMethodRef("java/math/BigInteger", "subtract", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(subRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate sub instruction based on result type
                    switch (resultType) {
                        case "I" -> code.isub();
                        case "J" -> code.lsub();
                        case "F" -> code.fsub();
                        case "D" -> code.dsub();
                    }
                }
            }
            case Mul -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger multiplication
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.multiply()
                    int mulRef = cp.addMethodRef("java/math/BigInteger", "multiply", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(mulRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate mul instruction based on result type
                    switch (resultType) {
                        case "I" -> code.imul();
                        case "J" -> code.lmul();
                        case "F" -> code.fmul();
                        case "D" -> code.dmul();
                    }
                }
            }
            case Div -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger division
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.divide()
                    int divRef = cp.addMethodRef("java/math/BigInteger", "divide", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(divRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate div instruction based on result type
                    switch (resultType) {
                        case "I" -> code.idiv();
                        case "J" -> code.ldiv();
                        case "F" -> code.fdiv();
                        case "D" -> code.ddiv();
                    }
                }
            }
            case Mod -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger modulo
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.remainder()
                    int remRef = cp.addMethodRef("java/math/BigInteger", "remainder", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(remRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate rem instruction based on result type
                    switch (resultType) {
                        case "I" -> code.irem();
                        case "J" -> code.lrem();
                        case "F" -> code.frem();
                        case "D" -> code.drem();
                    }
                }
            }
            case Exp -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType)) {
                    // BigInteger exponentiation
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand (base)
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand (exponent) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");
                    }

                    // Call BigInteger.pow(int)
                    int powRef = cp.addMethodRef("java/math/BigInteger", "pow", "(I)Ljava/math/BigInteger;");
                    code.invokevirtual(powRef);
                } else {
                    resultType = "D"; // Math.pow returns double

                    // Generate left operand (base) and convert to double
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), "D");

                    // Generate right operand (exponent) and convert to double
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "D");

                    // Call Math.pow(double, double)
                    int mathPowRef = cp.addMethodRef("java/lang/Math", "pow", "(DD)D");
                    code.invokestatic(mathPowRef);
                }
            }
            case LShift -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType)) {
                    // BigInteger left shift
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");
                    }

                    // Call BigInteger.shiftLeft(int)
                    int shiftLeftRef = cp.addMethodRef("java/math/BigInteger", "shiftLeft", "(I)Ljava/math/BigInteger;");
                    code.invokevirtual(shiftLeftRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");

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
            }
            case RShift -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType)) {
                    // BigInteger right shift
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");
                    }

                    // Call BigInteger.shiftRight(int)
                    int shiftRightRef = cp.addMethodRef("java/math/BigInteger", "shiftRight", "(I)Ljava/math/BigInteger;");
                    code.invokevirtual(shiftRightRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");

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
            }
            case ZeroFillRShift -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType)) {
                    // BigInteger unsigned right shift - not directly supported
                    // Use shiftRight for signed shift (limitation documented)
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");
                    }

                    // Call BigInteger.shiftRight(int) - note: this is signed shift
                    int shiftRightRef = cp.addMethodRef("java/math/BigInteger", "shiftRight", "(I)Ljava/math/BigInteger;");
                    code.invokevirtual(shiftRightRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), "I");

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
            }
            case BitAnd -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise AND
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.and()
                    int andRef = cp.addMethodRef("java/math/BigInteger", "and", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(andRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise AND instruction based on result type
                    switch (resultType) {
                        case "I" -> code.iand();
                        case "J" -> code.land();
                    }
                }
            }
            case BitOr -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise OR
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.or()
                    int orRef = cp.addMethodRef("java/math/BigInteger", "or", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(orRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise OR instruction based on result type
                    switch (resultType) {
                        case "I" -> code.ior();
                        case "J" -> code.lor();
                    }
                }
            }
            case BitXor -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise XOR
                    resultType = "Ljava/math/BigInteger;";

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.xor()
                    int xorRef = cp.addMethodRef("java/math/BigInteger", "xor", "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(xorRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise XOR instruction based on result type
                    switch (resultType) {
                        case "I" -> code.ixor();
                        case "J" -> code.lxor();
                    }
                }
            }
            case EqEq, EqEqEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger equality
                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.equals()
                    int equalsRef = cp.addMethodRef("java/math/BigInteger", "equals", "(Ljava/lang/Object;)Z");
                    code.invokevirtual(equalsRef);
                } else {
                    // Determine the comparison type (widen to common type for primitives)
                    String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                    String leftPrimitive = TypeConversionUtils.getPrimitiveType(leftType);
                    String rightPrimitive = TypeConversionUtils.getPrimitiveType(rightType);

                    // Check if both are primitive types (NOT wrappers - those use Objects.equals)
                    boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                            rightType.equals(rightPrimitive) &&
                            (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                    leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                    leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                    leftPrimitive.equals("C") || leftPrimitive.equals("Z"));

                    if (isPrimitiveComparison) {
                    // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                    ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(comparisonType);

                    // Generate left operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                    }

                    // Generate right operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                    }

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
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);

                        // Objects.equals returns boolean (Z) which is represented as 0 or 1
                        int equalsRef = cp.addMethodRef("java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                        code.invokestatic(equalsRef);
                    }
                }
            }
            case NotEq, NotEqEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger inequality
                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.equals() and invert result
                    int equalsRef = cp.addMethodRef("java/math/BigInteger", "equals", "(Ljava/lang/Object;)Z");
                    code.invokevirtual(equalsRef);
                    // Invert: 0 -> 1, 1 -> 0
                    code.iconst(1);
                    code.ixor();
                } else {
                    // Determine the comparison type (widen to common type for primitives)
                    String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                    String leftPrimitive = TypeConversionUtils.getPrimitiveType(leftType);
                    String rightPrimitive = TypeConversionUtils.getPrimitiveType(rightType);

                    // Check if both are primitive types (NOT wrappers - those use Objects.equals)
                    boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                            rightType.equals(rightPrimitive) &&
                            (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                    leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                    leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                    leftPrimitive.equals("C") || leftPrimitive.equals("Z"));

                    if (isPrimitiveComparison) {
                    // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                    ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(comparisonType);

                    // Generate left operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                    }

                    // Generate right operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                    }

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
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);

                    // Objects.equals returns boolean (Z) which is represented as 0 or 1
                    int equalsRef = cp.addMethodRef("java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                    code.invokestatic(equalsRef);

                        // Invert the result: 0 -> 1, 1 -> 0
                        // We can use: iconst_1, ixor (XOR with 1 flips the bit)
                        code.iconst(1);
                        code.ixor();
                    }
                }
            }
            case Lt, LtEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger less than / less than or equal
                    boolean isLtEq = binExpr.getOp() == Swc4jAstBinaryOp.LtEq;

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.compareTo() - returns -1, 0, or 1
                    int compareToRef = cp.addMethodRef("java/math/BigInteger", "compareTo", "(Ljava/math/BigInteger;)I");
                    code.invokevirtual(compareToRef);

                    // Check result: < 0 for Lt, <= 0 for LtEq
                    // Use hardcoded offsets (same pattern as primitive comparisons)
                    if (isLtEq) {
                        // result <= 0: if result > 0, jump to iconst_0
                        code.ifgt(7);
                    } else {
                        // result < 0: if result >= 0, jump to iconst_0
                        code.ifge(7);
                    }
                    code.iconst(1);        // condition true: push 1
                    code.gotoLabel(4);     // jump over iconst_0
                    code.iconst(0);        // condition false: push 0
                } else {
                    // Determine the comparison type (widen to common type for primitives)
                    String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                    String leftPrimitive = TypeConversionUtils.getPrimitiveType(leftType);
                    String rightPrimitive = TypeConversionUtils.getPrimitiveType(rightType);

                    // Check if both are primitive types (NOT wrappers)
                    boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                            rightType.equals(rightPrimitive) &&
                            (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                    leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                    leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                    leftPrimitive.equals("C"));

                    if (isPrimitiveComparison) {
                    // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                    ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(comparisonType);

                    // Generate left operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                    }

                    // Generate right operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                    }

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
            }
            case Gt, GtEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Result type for comparison is always boolean
                resultType = "Z";

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger greater than / greater than or equal
                    boolean isGtEq = binExpr.getOp() == Swc4jAstBinaryOp.GtEq;

                    // Generate left operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                        convertToBigInteger(code, cp, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                        convertToBigInteger(code, cp, rightType);
                    }

                    // Call BigInteger.compareTo() - returns -1, 0, or 1
                    int compareToRef = cp.addMethodRef("java/math/BigInteger", "compareTo", "(Ljava/math/BigInteger;)I");
                    code.invokevirtual(compareToRef);

                    // Check result: > 0 for Gt, >= 0 for GtEq
                    // Use hardcoded offsets (same pattern as primitive comparisons)
                    if (isGtEq) {
                        // result >= 0: if result < 0, jump to iconst_0
                        code.iflt(7);
                    } else {
                        // result > 0: if result <= 0, jump to iconst_0
                        code.ifle(7);
                    }
                    code.iconst(1);        // condition true: push 1
                    code.gotoLabel(4);     // jump over iconst_0
                    code.iconst(0);        // condition false: push 0
                } else {
                    // Determine the comparison type (widen to common type for primitives)
                    String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                    String leftPrimitive = TypeConversionUtils.getPrimitiveType(leftType);
                    String rightPrimitive = TypeConversionUtils.getPrimitiveType(rightType);

                    // Check if both are primitive types (NOT wrappers)
                    boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                            rightType.equals(rightPrimitive) &&
                            (leftPrimitive.equals("I") || leftPrimitive.equals("J") ||
                                    leftPrimitive.equals("F") || leftPrimitive.equals("D") ||
                                    leftPrimitive.equals("B") || leftPrimitive.equals("S") ||
                                    leftPrimitive.equals("C"));

                    if (isPrimitiveComparison) {
                    // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                    ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(comparisonType);

                    // Generate left operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, leftType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                    }

                    // Generate right operand with type hint
                    compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), compTypeInfo);
                    TypeConversionUtils.unboxWrapperType(code, cp, rightType);
                    // Skip conversion for number literals - they're already generated in the target type
                    if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                        TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                    }

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
            }
            case LogicalAnd -> {
                // LogicalAnd (&&) with short-circuit evaluation
                // If left is false, skip evaluating right and return false
                // If left is true, return the right operand value

                // Result type is always boolean
                resultType = "Z";

                // Get types of operands
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

                // Generate left operand
                compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, cp, leftType);

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
                compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, cp, rightType);

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
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

                // Generate left operand
                compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, cp, leftType);

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
                compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, cp, rightType);

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
                TypeConversionUtils.convertPrimitiveType(code, resultType, targetType);
            }
        }
    }

    /**
     * Check if an expression is a number literal that will be generated directly in the target type
     * when a type hint is provided. In this case, we should skip post-generation type conversion
     * because the literal was already generated in the target type.
     */
    private boolean isNumberLiteralWithTypeHint(ISwc4jAstExpr expr, String targetType) {
        // Number literals are generated directly in the target type when a primitive type hint is given
        return expr instanceof Swc4jAstNumber &&
                (targetType.equals("I") || targetType.equals("J") ||
                        targetType.equals("F") || targetType.equals("D"));
    }

    /**
     * Check if a type is BigInteger.
     */
    private boolean isBigInteger(String type) {
        return "Ljava/math/BigInteger;".equals(type);
    }

    /**
     * Convert a primitive value on the stack to BigInteger.
     * Stack before: [primitive value]
     * Stack after: [BigInteger]
     */
    private void convertToBigInteger(CodeBuilder code, ClassWriter.ConstantPool cp, String fromType) {
        String primitiveType = TypeConversionUtils.getPrimitiveType(fromType);

        // Convert to long first if needed
        if (!"J".equals(primitiveType)) {
            TypeConversionUtils.convertPrimitiveType(code, primitiveType, "J");
        }

        // Call BigInteger.valueOf(long)
        int valueOfRef = cp.addMethodRef("java/math/BigInteger", "valueOf", "(J)Ljava/math/BigInteger;");
        code.invokestatic(valueOfRef);
    }
}
