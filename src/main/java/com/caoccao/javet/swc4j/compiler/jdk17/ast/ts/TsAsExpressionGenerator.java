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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class TsAsExpressionGenerator {
    private TsAsExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTsAsExpr asExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle explicit type cast (e.g., a as double)
        String targetType = TypeResolver.inferTypeFromExpr(asExpr, context, options);
        String innerType = TypeResolver.inferTypeFromExpr(asExpr.getExpr(), context, options);
        // Handle null types - should not happen for cast expressions, but default to Object if it does
        if (targetType == null) targetType = "Ljava/lang/Object;";
        if (innerType == null) innerType = "Ljava/lang/Object;";

        // Generate code for the inner expression
        ExpressionGenerator.generate(code, cp, asExpr.getExpr(), null, context, options);

        // Unbox if the inner expression is a wrapper type
        TypeConversionUtils.unboxWrapperType(code, cp, innerType);

        // Get the primitive types for conversion
        String innerPrimitive = TypeConversionUtils.getPrimitiveType(innerType);
        String targetPrimitive = TypeConversionUtils.getPrimitiveType(targetType);

        // Convert from inner primitive type to target primitive type
        TypeConversionUtils.convertPrimitiveType(code, innerPrimitive, targetPrimitive);

        // Box if the target type is a wrapper
        TypeConversionUtils.boxPrimitiveType(code, cp, targetPrimitive, targetType);
    }
}
