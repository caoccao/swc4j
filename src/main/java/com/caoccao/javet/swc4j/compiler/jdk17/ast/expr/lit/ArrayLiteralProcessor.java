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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.Optional;

/**
 * Processor for array literal expressions.
 * <p>
 * Handles JavaScript array literals like [1, 2, 3] and compiles them to ArrayList instances.
 */
public final class ArrayLiteralProcessor extends BaseAstProcessor<Swc4jAstArrayLit> {
    /**
     * Constructs a new ArrayLiteralProcessor.
     *
     * @param compiler the bytecode compiler instance
     */
    public ArrayLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrayLit arrayLit,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Check if we should generate a Java array or ArrayList
        boolean isJavaArray = returnTypeInfo != null &&
                returnTypeInfo.descriptor() != null &&
                returnTypeInfo.descriptor().startsWith(ConstantJavaType.ARRAY_PREFIX);

        if (isJavaArray) {
            // Generate Java array
            generateJavaArray(code, classWriter, arrayLit, returnTypeInfo.descriptor());
        } else {
            // Array literal - convert to ArrayList
            int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
            int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
            int arrayListAdd = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
            int arrayListAddAll = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, "addAll", "(Ljava/util/Collection;)Z");

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
                        compiler.getExpressionProcessor().generate(code, classWriter, elemExpr, null);

                        // Call ArrayList.addAll(Collection) to add all elements
                        code.invokevirtual(arrayListAddAll);
                        code.pop(); // Pop the boolean return value from addAll()
                    } else {
                        // Regular element
                        code.dup(); // Duplicate ArrayList reference
                        // Generate code for the element expression - ensure it's boxed
                        ISwc4jAstExpr elemExpr = elem.getExpr();
                        String elemType = compiler.getTypeResolver().inferTypeFromExpr(elemExpr);
                        if (elemType == null) elemType = ConstantJavaType.LJAVA_LANG_OBJECT;

                        compiler.getExpressionProcessor().generate(code, classWriter, elemExpr, null);

                        // Box primitives to objects
                        if (ConstantJavaType.ABBR_INTEGER.equals(elemType) || ConstantJavaType.ABBR_BOOLEAN.equals(elemType) || ConstantJavaType.ABBR_BYTE.equals(elemType) ||
                                ConstantJavaType.ABBR_CHARACTER.equals(elemType) || ConstantJavaType.ABBR_SHORT.equals(elemType) || ConstantJavaType.ABBR_LONG.equals(elemType) ||
                                ConstantJavaType.ABBR_FLOAT.equals(elemType) || ConstantJavaType.ABBR_DOUBLE.equals(elemType)) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, elemType, TypeConversionUtils.getWrapperType(elemType));
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

    private void generateJavaArray(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrayLit arrayLit,
            String arrayDescriptor) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Extract element type from array descriptor (e.g., ConstantJavaType.ARRAY_I -> ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ARRAY_LJAVA_LANG_STRING -> ConstantJavaType.LJAVA_LANG_STRING)
        String elemType = arrayDescriptor.substring(1);

        // Count non-empty elements
        int size = (int) arrayLit.getElems().stream().filter(Optional::isPresent).count();

        // Create the array
        code.iconst(size);

        // Use newarray for primitive types, anewarray for reference types
        switch (elemType) {
            case ConstantJavaType.ABBR_BOOLEAN -> code.newarray(4);  // T_BOOLEAN
            case ConstantJavaType.ABBR_CHARACTER -> code.newarray(5);  // T_CHAR
            case ConstantJavaType.ABBR_FLOAT -> code.newarray(6);  // T_FLOAT
            case ConstantJavaType.ABBR_DOUBLE -> code.newarray(7);  // T_DOUBLE
            case ConstantJavaType.ABBR_BYTE -> code.newarray(8);  // T_BYTE
            case ConstantJavaType.ABBR_SHORT -> code.newarray(9);  // T_SHORT
            case ConstantJavaType.ABBR_INTEGER -> code.newarray(10); // T_INT
            case ConstantJavaType.ABBR_LONG -> code.newarray(11); // T_LONG
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
                String exprType = compiler.getTypeResolver().inferTypeFromExpr(elemExpr);
                if (exprType == null) exprType = ConstantJavaType.LJAVA_LANG_OBJECT;

                compiler.getExpressionProcessor().generate(code, classWriter, elemExpr, null);

                // Unbox if needed
                TypeConversionUtils.unboxWrapperType(code, classWriter, exprType);

                // Convert to target type if needed
                String exprPrimitive = TypeConversionUtils.getPrimitiveType(exprType);
                TypeConversionUtils.convertPrimitiveType(code, exprPrimitive, elemType);

                // Store in array using appropriate instruction
                switch (elemType) {
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE ->
                            code.bastore(); // boolean and byte use bastore
                    case ConstantJavaType.ABBR_CHARACTER -> code.castore(); // char uses castore
                    case ConstantJavaType.ABBR_SHORT -> code.sastore(); // short uses sastore
                    case ConstantJavaType.ABBR_INTEGER -> code.iastore(); // int uses iastore
                    case ConstantJavaType.ABBR_LONG -> code.lastore(); // long uses lastore
                    case ConstantJavaType.ABBR_FLOAT -> code.fastore(); // float uses fastore
                    case ConstantJavaType.ABBR_DOUBLE -> code.dastore(); // double uses dastore
                    default -> code.aastore(); // reference types use aastore
                }

                index++;
            }
        }
        // Array reference is now on top of stack
    }
}
