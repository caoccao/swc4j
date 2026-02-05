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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.ts;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstTsAsExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Ts as expression processor.
 */
public final class TsAsExpressionProcessor extends BaseAstProcessor<Swc4jAstTsAsExpr> {
    /**
     * Instantiates a new Ts as expression processor.
     *
     * @param compiler the compiler
     */
    public TsAsExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTsAsExpr asExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Handle explicit type cast (e.g., a as double)
        String targetType = compiler.getTypeResolver().inferTypeFromExpr(asExpr);
        String innerType = compiler.getTypeResolver().inferTypeFromExpr(asExpr.getExpr());
        // Handle null types - should not happen for cast expressions, but default to Object if it does
        if (targetType == null) targetType = "Ljava/lang/Object;";
        if (innerType == null) innerType = "Ljava/lang/Object;";

        // Generate code for the inner expression
        compiler.getExpressionProcessor().generate(code, classWriter, asExpr.getExpr(), null);

        // Handle Object to primitive conversion
        // When the source is Object and target is a primitive, we need to cast first
        if ("Ljava/lang/Object;".equals(innerType) && TypeConversionUtils.isPrimitiveType(targetType)) {
            // Cast Object to Number (works for Integer, Long, Double, etc.)
            // Then call the appropriate value method
            String wrapperType = TypeConversionUtils.getWrapperType(targetType);
            switch (targetType) {
                case "I" -> {
                    int integerClass = cp.addClass("java/lang/Integer");
                    code.checkcast(integerClass);
                    int intValueRef = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                    code.invokevirtual(intValueRef);
                }
                case "J" -> {
                    int longClass = cp.addClass("java/lang/Long");
                    code.checkcast(longClass);
                    int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                    code.invokevirtual(longValueRef);
                }
                case "D" -> {
                    int doubleClass = cp.addClass("java/lang/Double");
                    code.checkcast(doubleClass);
                    int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                    code.invokevirtual(doubleValueRef);
                }
                case "F" -> {
                    int floatClass = cp.addClass("java/lang/Float");
                    code.checkcast(floatClass);
                    int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                    code.invokevirtual(floatValueRef);
                }
                case "B" -> {
                    int byteClass = cp.addClass("java/lang/Byte");
                    code.checkcast(byteClass);
                    int byteValueRef = cp.addMethodRef("java/lang/Byte", "byteValue", "()B");
                    code.invokevirtual(byteValueRef);
                }
                case "S" -> {
                    int shortClass = cp.addClass("java/lang/Short");
                    code.checkcast(shortClass);
                    int shortValueRef = cp.addMethodRef("java/lang/Short", "shortValue", "()S");
                    code.invokevirtual(shortValueRef);
                }
                case "C" -> {
                    int characterClass = cp.addClass("java/lang/Character");
                    code.checkcast(characterClass);
                    int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                    code.invokevirtual(charValueRef);
                }
                case "Z" -> {
                    int booleanClass = cp.addClass("java/lang/Boolean");
                    code.checkcast(booleanClass);
                    int booleanValueRef = cp.addMethodRef("java/lang/Boolean", "booleanValue", "()Z");
                    code.invokevirtual(booleanValueRef);
                }
            }
            return;
        }

        // Handle Object to reference type cast (e.g., Object as string)
        if ("Ljava/lang/Object;".equals(innerType) && targetType.startsWith("L") && !targetType.equals(innerType)) {
            // Cast Object to target reference type
            String targetClass = targetType.substring(1, targetType.length() - 1);
            int classRef = cp.addClass(targetClass);
            code.checkcast(classRef);
            return;
        }

        // Unbox if the inner expression is a wrapper type
        TypeConversionUtils.unboxWrapperType(code, classWriter, innerType);

        // Get the primitive types for conversion
        String innerPrimitive = TypeConversionUtils.getPrimitiveType(innerType);
        String targetPrimitive = TypeConversionUtils.getPrimitiveType(targetType);

        // Convert from inner primitive type to target primitive type
        TypeConversionUtils.convertPrimitiveType(code, innerPrimitive, targetPrimitive);

        // Box if the target type is a wrapper
        TypeConversionUtils.boxPrimitiveType(code, classWriter, targetPrimitive, targetType);
    }
}
