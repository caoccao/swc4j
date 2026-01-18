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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringConcatUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.Optional;

public final class ArrayLiteralGenerator {
    private ArrayLiteralGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstArrayLit arrayLit,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {
        // Check if we should generate a Java array or ArrayList
        boolean isJavaArray = returnTypeInfo != null &&
                returnTypeInfo.descriptor() != null &&
                returnTypeInfo.descriptor().startsWith("[");

        if (isJavaArray) {
            // Generate Java array
            generateJavaArray(code, cp, arrayLit, returnTypeInfo.descriptor(), context, options, callback);
        } else {
            // Array literal - convert to ArrayList
            int arrayListClass = cp.addClass("java/util/ArrayList");
            int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
            int arrayListAdd = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
            int arrayListAddAll = cp.addMethodRef("java/util/ArrayList", "addAll", "(Ljava/util/Collection;)Z");

            // Create new ArrayList instance
            code.newInstance(arrayListClass);
            code.dup();
            code.invokespecial(arrayListInit);

            // Add each element to the list
            for (var elemOpt : arrayLit.getElems()) {
                if (elemOpt.isPresent()) {
                    var elem = elemOpt.get();

                    // Check if this is a spread element
                    if (elem.getSpread().isPresent()) {
                        // Spread element: ...array
                        code.dup(); // Duplicate ArrayList reference
                        ISwc4jAstExpr elemExpr = elem.getExpr();

                        // Generate code for the spread expression (should be an array/collection)
                        callback.generateExpr(code, cp, elemExpr, null, context, options);

                        // Call ArrayList.addAll(Collection) to add all elements
                        code.invokevirtual(arrayListAddAll);
                        code.pop(); // Pop the boolean return value from addAll()
                    } else {
                        // Regular element
                        code.dup(); // Duplicate ArrayList reference
                        // Generate code for the element expression - ensure it's boxed
                        ISwc4jAstExpr elemExpr = elem.getExpr();
                        String elemType = TypeResolver.inferTypeFromExpr(elemExpr, context, options);
                        if (elemType == null) elemType = "Ljava/lang/Object;";

                        callback.generateExpr(code, cp, elemExpr, null, context, options);

                        // Box primitives to objects
                        if ("I".equals(elemType) || "Z".equals(elemType) || "B".equals(elemType) ||
                                "C".equals(elemType) || "S".equals(elemType) || "J".equals(elemType) ||
                                "F".equals(elemType) || "D".equals(elemType)) {
                            TypeConversionUtils.boxPrimitiveType(code, cp, elemType, TypeConversionUtils.getWrapperType(elemType));
                        }

                        // Call ArrayList.add(Object)
                        code.invokevirtual(arrayListAdd);
                        code.pop(); // Pop the boolean return value from add()
                    }
                }
            }
            // ArrayList reference is now on top of stack
        }
    }

    private static void generateJavaArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstArrayLit arrayLit,
            String arrayDescriptor,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {
        // Extract element type from array descriptor (e.g., "[I" -> "I", "[Ljava/lang/String;" -> "Ljava/lang/String;")
        String elemType = arrayDescriptor.substring(1);

        // Count non-empty elements
        int size = (int) arrayLit.getElems().stream().filter(Optional::isPresent).count();

        // Create the array
        code.iconst(size);

        // Use newarray for primitive types, anewarray for reference types
        switch (elemType) {
            case "Z" -> code.newarray(4);  // T_BOOLEAN
            case "C" -> code.newarray(5);  // T_CHAR
            case "F" -> code.newarray(6);  // T_FLOAT
            case "D" -> code.newarray(7);  // T_DOUBLE
            case "B" -> code.newarray(8);  // T_BYTE
            case "S" -> code.newarray(9);  // T_SHORT
            case "I" -> code.newarray(10); // T_INT
            case "J" -> code.newarray(11); // T_LONG
            default -> {
                // Reference type array - use anewarray
                String className = elemType.substring(1, elemType.length() - 1); // Remove "L" and ";"
                int classIndex = cp.addClass(className);
                code.anewarray(classIndex);
            }
        }

        // Store elements in the array
        int index = 0;
        for (var elemOpt : arrayLit.getElems()) {
            if (elemOpt.isPresent()) {
                var elem = elemOpt.get();
                ISwc4jAstExpr elemExpr = elem.getExpr();

                code.dup();          // Duplicate array reference
                code.iconst(index);  // Push index

                // Generate the element value
                String exprType = TypeResolver.inferTypeFromExpr(elemExpr, context, options);
                if (exprType == null) exprType = "Ljava/lang/Object;";

                callback.generateExpr(code, cp, elemExpr, null, context, options);

                // Unbox if needed
                TypeConversionUtils.unboxWrapperType(code, cp, exprType);

                // Convert to target type if needed
                String exprPrimitive = TypeConversionUtils.getPrimitiveType(exprType);
                TypeConversionUtils.convertPrimitiveType(code, exprPrimitive, elemType);

                // Store in array using appropriate instruction
                switch (elemType) {
                    case "Z", "B" -> code.bastore(); // boolean and byte use bastore
                    case "C" -> code.castore(); // char uses castore
                    case "S" -> code.sastore(); // short uses sastore
                    case "I" -> code.iastore(); // int uses iastore
                    case "J" -> code.lastore(); // long uses lastore
                    case "F" -> code.fastore(); // float uses fastore
                    case "D" -> code.dastore(); // double uses dastore
                    default -> code.aastore(); // reference types use aastore
                }

                index++;
            }
        }
        // Array reference is now on top of stack
    }
}
