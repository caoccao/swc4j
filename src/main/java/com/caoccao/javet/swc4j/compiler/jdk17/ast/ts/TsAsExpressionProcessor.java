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
        if (targetType == null) targetType = TypeConversionUtils.LJAVA_LANG_OBJECT;
        if (innerType == null) innerType = TypeConversionUtils.LJAVA_LANG_OBJECT;

        // Generate code for the inner expression
        compiler.getExpressionProcessor().generate(code, classWriter, asExpr.getExpr(), null);

        // Handle Object to primitive conversion
        // When the source is Object and target is a primitive, we need to cast first
        if (TypeConversionUtils.LJAVA_LANG_OBJECT.equals(innerType) && TypeConversionUtils.isPrimitiveType(targetType)) {
            // Cast Object to Number (works for Integer, Long, Double, etc.)
            // Then call the appropriate value method
            String wrapperType = TypeConversionUtils.getWrapperType(targetType);
            switch (targetType) {
                case TypeConversionUtils.ABBR_INTEGER -> {
                    int integerClass = cp.addClass(TypeConversionUtils.JAVA_LANG_INTEGER);
                    code.checkcast(integerClass);
                    int intValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_INTEGER, TypeConversionUtils.METHOD_INT_VALUE, TypeConversionUtils.DESCRIPTER___I);
                    code.invokevirtual(intValueRef);
                }
                case TypeConversionUtils.ABBR_LONG -> {
                    int longClass = cp.addClass(TypeConversionUtils.JAVA_LANG_LONG);
                    code.checkcast(longClass);
                    int longValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_LONG, TypeConversionUtils.METHOD_LONG_VALUE, TypeConversionUtils.DESCRIPTER___J);
                    code.invokevirtual(longValueRef);
                }
                case TypeConversionUtils.ABBR_DOUBLE -> {
                    int doubleClass = cp.addClass(TypeConversionUtils.JAVA_LANG_DOUBLE);
                    code.checkcast(doubleClass);
                    int doubleValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_DOUBLE, TypeConversionUtils.METHOD_DOUBLE_VALUE, TypeConversionUtils.DESCRIPTER___D);
                    code.invokevirtual(doubleValueRef);
                }
                case TypeConversionUtils.ABBR_FLOAT -> {
                    int floatClass = cp.addClass(TypeConversionUtils.JAVA_LANG_FLOAT);
                    code.checkcast(floatClass);
                    int floatValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_FLOAT, TypeConversionUtils.METHOD_FLOAT_VALUE, TypeConversionUtils.DESCRIPTER___F);
                    code.invokevirtual(floatValueRef);
                }
                case TypeConversionUtils.ABBR_BYTE -> {
                    int byteClass = cp.addClass(TypeConversionUtils.JAVA_LANG_BYTE);
                    code.checkcast(byteClass);
                    int byteValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_BYTE, TypeConversionUtils.METHOD_BYTE_VALUE, TypeConversionUtils.DESCRIPTER___B);
                    code.invokevirtual(byteValueRef);
                }
                case TypeConversionUtils.ABBR_SHORT -> {
                    int shortClass = cp.addClass(TypeConversionUtils.JAVA_LANG_SHORT);
                    code.checkcast(shortClass);
                    int shortValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_SHORT, TypeConversionUtils.METHOD_SHORT_VALUE, TypeConversionUtils.DESCRIPTER___S);
                    code.invokevirtual(shortValueRef);
                }
                case TypeConversionUtils.ABBR_CHARACTER -> {
                    int characterClass = cp.addClass(TypeConversionUtils.JAVA_LANG_CHARACTER);
                    code.checkcast(characterClass);
                    int charValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_CHARACTER, TypeConversionUtils.METHOD_CHAR_VALUE, TypeConversionUtils.DESCRIPTER___C);
                    code.invokevirtual(charValueRef);
                }
                case TypeConversionUtils.ABBR_BOOLEAN -> {
                    int booleanClass = cp.addClass(TypeConversionUtils.JAVA_LANG_BOOLEAN);
                    code.checkcast(booleanClass);
                    int booleanValueRef = cp.addMethodRef(TypeConversionUtils.JAVA_LANG_BOOLEAN, TypeConversionUtils.METHOD_BOOLEAN_VALUE, TypeConversionUtils.DESCRIPTER___Z);
                    code.invokevirtual(booleanValueRef);
                }
            }
            return;
        }

        // Handle Object to reference type cast (e.g., Object as string)
        if (TypeConversionUtils.LJAVA_LANG_OBJECT.equals(innerType) && targetType.startsWith("L") && !targetType.equals(innerType)) {
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
