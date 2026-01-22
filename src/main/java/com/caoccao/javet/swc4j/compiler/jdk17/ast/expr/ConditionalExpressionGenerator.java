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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCondExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ConditionalExpressionGenerator extends BaseAstProcessor<Swc4jAstCondExpr> {
    public ConditionalExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Convert a value on the stack from sourceType to targetType.
     */
    private void convertToCommonType(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String sourceType,
            String targetType) throws Swc4jByteCodeCompilerException {

        if (sourceType == null || targetType == null) {
            return; // No conversion needed for null
        }

        if (sourceType.equals(targetType)) {
            return; // No conversion needed
        }

        // Unbox if source is a wrapper type
        TypeConversionUtils.unboxWrapperType(code, cp, sourceType);
        String primitiveSource = TypeConversionUtils.getPrimitiveType(sourceType);

        // Unbox if target is a wrapper type
        String primitiveTarget = TypeConversionUtils.getPrimitiveType(targetType);

        // Convert between primitive types
        if (primitiveSource != null && primitiveTarget != null) {
            TypeConversionUtils.convertPrimitiveType(code, primitiveSource, primitiveTarget);
        }

        // Box if target is a wrapper type
        if (!targetType.equals(primitiveTarget) && primitiveTarget != null) {
            TypeConversionUtils.boxPrimitiveType(code, cp, primitiveTarget, targetType);
        }
    }

    /**
     * Find the common type between two branch types.
     * Phase 1: Simple implementation - both types must be the same.
     * Future phases will add type coercion and widening.
     */
    private String findCommonType(String type1, String type2) throws Swc4jByteCodeCompilerException {
        // Handle null types
        if (type1 == null && type2 == null) {
            return null; // Both null
        }
        if (type1 == null) {
            return type2; // One is null, use non-null type
        }
        if (type2 == null) {
            return type1; // One is null, use non-null type
        }

        // Phase 1: Simple same-type check
        if (type1.equals(type2)) {
            return type1;
        }

        // Phase 2 (future): Numeric widening
        // If both are primitives, widen to the larger type
        if (TypeConversionUtils.isPrimitiveType(type1) && TypeConversionUtils.isPrimitiveType(type2)) {
            return widenPrimitiveTypes(type1, type2);
        }

        // Phase 2 (future): Boxing/unboxing
        // If one is primitive and other is wrapper, use wrapper type

        // Phase 2 (future): Reference type hierarchy
        // Find common supertype (may default to Object)

        // For now, if types don't match and aren't both primitives, use Object
        return "Ljava/lang/Object;";
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCondExpr condExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Infer types for type conversion
        String consType = compiler.getTypeResolver().inferTypeFromExpr(condExpr.getCons());
        String altType = compiler.getTypeResolver().inferTypeFromExpr(condExpr.getAlt());

        // Find common type between branches
        String commonType = findCommonType(consType, altType);

        // Evaluate the test condition
        compiler.getExpressionGenerator().generate(code, cp, condExpr.getTest(), null);

        // Pattern:
        //   [test expression]          // Stack: [boolean]
        //   ifeq ELSE_LABEL            // Jump to else if false (0)
        //   [consequent expression]    // True branch
        //   goto END_LABEL
        //   ELSE_LABEL:
        //   [alternate expression]     // False branch
        //   END_LABEL:

        // Jump to else branch if condition is false (0)
        code.ifeq(0); // Placeholder, will patch offset later
        int ifeqOffsetPos = code.getCurrentOffset() - 2;
        int ifeqOpcodePos = code.getCurrentOffset() - 3;

        // True branch (consequent)
        compiler.getExpressionGenerator().generate(code, cp, condExpr.getCons(), null);
        // Convert to common type if needed
        convertToCommonType(code, cp, consType, commonType);

        // Jump over the alternate branch
        code.gotoLabel(0); // Placeholder for goto
        int gotoOffsetPos = code.getCurrentOffset() - 2;
        int gotoOpcodePos = code.getCurrentOffset() - 3;

        // False branch (alternate)
        int elseLabel = code.getCurrentOffset();
        compiler.getExpressionGenerator().generate(code, cp, condExpr.getAlt(), null);
        // Convert to common type if needed
        convertToCommonType(code, cp, altType, commonType);

        // End of conditional expression
        int endLabel = code.getCurrentOffset();

        // Calculate and patch the ifeq offset
        // JVM offset is relative to the opcode position
        int ifeqOffset = elseLabel - ifeqOpcodePos;
        code.patchShort(ifeqOffsetPos, ifeqOffset);

        // Calculate and patch the goto offset
        int gotoOffset = endLabel - gotoOpcodePos;
        code.patchShort(gotoOffsetPos, gotoOffset);

        // Handle final boxing if needed for return type
        // Only box if the return type is OBJECT and the result is a primitive
        if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT &&
                commonType != null && TypeConversionUtils.isPrimitiveType(commonType)) {
            // Box primitive to wrapper type
            String wrapperType = TypeConversionUtils.getWrapperType(commonType);
            TypeConversionUtils.boxPrimitiveType(code, cp, commonType, wrapperType);
        }
    }

    /**
     * Get the rank of a primitive type for widening comparison.
     */
    private int getPrimitiveRank(String type) {
        return switch (type) {
            case "B" -> 1; // byte
            case "S" -> 2; // short
            case "C" -> 2; // char (same rank as short, can widen to int)
            case "I" -> 3; // int
            case "J" -> 4; // long
            case "F" -> 5; // float
            case "D" -> 6; // double
            default -> 0;
        };
    }

    /**
     * Widen two primitive types to their common type.
     * Widening hierarchy: byte → short → int → long → float → double
     * char → int
     */
    private String widenPrimitiveTypes(String type1, String type2) {
        // If same type, no widening needed
        if (type1.equals(type2)) {
            return type1;
        }

        // Boolean can't be widened
        if ("Z".equals(type1) || "Z".equals(type2)) {
            return "Z"; // This will likely cause issues, but matches current behavior
        }

        // Create widening order (higher number = wider type)
        int rank1 = getPrimitiveRank(type1);
        int rank2 = getPrimitiveRank(type2);

        // Return the wider type
        return rank1 > rank2 ? type1 : type2;
    }
}
