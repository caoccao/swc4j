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
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        switch (binExpr.getOp()) {
            case Add -> {
                String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = "Ljava/lang/Object;";
                if (rightType == null) rightType = "Ljava/lang/Object;";

                // Check if this is string concatenation
                if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                    StringConcatHelper.generateConcat(code, cp, binExpr.getLeft(), binExpr.getRight(), leftType, rightType, context, options, ExpressionGenerator::generate);
                } else {
                    // Determine the widened result type
                    String resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

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
                String resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

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
                String resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

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
        }
    }
}
