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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringApiUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
/**
 * Processor for binary expressions in bytecode compilation.
 */
public final class BinaryExpressionProcessor extends BaseAstProcessor<Swc4jAstBinExpr> {
    /**
     * Constructs a new binary expression processor.
     *
     * @param compiler the bytecode compiler
     */
    public BinaryExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Convert a primitive value on the stack to BigInteger.
     * Stack before: [primitive value]
     * Stack after: [BigInteger]
     */
    private void convertToBigInteger(CodeBuilder code, ClassWriter classWriter, String fromType) {
        var cp = classWriter.getConstantPool();
        String primitiveType = TypeConversionUtils.getPrimitiveType(fromType);

        // Convert to long first if needed
        if (!ConstantJavaType.ABBR_LONG.equals(primitiveType)) {
            TypeConversionUtils.convertPrimitiveType(code, primitiveType, ConstantJavaType.ABBR_LONG);
        }

        // Call BigInteger.valueOf(long)
        int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_J__LJAVA_MATH_BIGINTEGER);
        code.invokestatic(valueOfRef);
    }

    /**
     * Determines the container type for the 'in' operator.
     *
     * @param astExpr        the right operand expression
     * @param typeDescriptor the type descriptor of the right operand
     * @return the container type (LIST, MAP, or STRING)
     * @throws Swc4jByteCodeCompilerException if the type is not a valid container
     */
    private InContainerType determineInContainerType(ISwc4jAstExpr astExpr, String typeDescriptor)
            throws Swc4jByteCodeCompilerException {
        if (typeDescriptor == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), astExpr,
                    "Cannot determine type of 'in' right operand. Please add explicit type annotation.");
        }

        // Check for String type
        if (ConstantJavaType.LJAVA_LANG_STRING.equals(typeDescriptor)) {
            return InContainerType.STRING;
        }

        // For object types, use isAssignableTo() for unified checking
        if (typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";")) {
            String internalName = typeDescriptor.substring(1, typeDescriptor.length() - 1);
            String qualifiedName = internalName.replace('/', '.');

            // Try to resolve from the registry first
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                // Try simple name
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            if (typeInfo == null) {
                // Create a temporary JavaTypeInfo for JDK types not in registry
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                int lastDot = qualifiedName.lastIndexOf('.');
                String packageName = lastDot >= 0 ? qualifiedName.substring(0, lastDot) : "";
                typeInfo = new JavaTypeInfo(simpleName, packageName, internalName);
            }

            // Check assignability using the unified type hierarchy
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_LIST)) {
                return InContainerType.LIST;
            }
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_MAP)) {
                return InContainerType.MAP;
            }
        }

        throw new Swc4jByteCodeCompilerException(getSourceCode(), astExpr,
                "The 'in' operator requires List, Map, or String type, but got: " + typeDescriptor);
    }

    /**
     * Generates bytecode for a binary expression.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param binExpr        the binary expression AST node
     * @param returnTypeInfo the expected return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBinExpr binExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        String resultType = null;
        switch (binExpr.getOp()) {
            case Add -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                // Check if this is string concatenation
                // Treat Object + anything or anything + Object as string concatenation
                // (JavaScript semantics: + operator with non-numeric types converts to string)
                if (ConstantJavaType.LJAVA_LANG_STRING.equals(leftType) || ConstantJavaType.LJAVA_LANG_STRING.equals(rightType)
                        || ConstantJavaType.LJAVA_LANG_OBJECT.equals(leftType) || ConstantJavaType.LJAVA_LANG_OBJECT.equals(rightType)) {
                    StringApiUtils.generateConcat(
                            getSourceCode(),
                            compiler,
                            code,
                            classWriter,
                            binExpr.getLeft(), binExpr.getRight(),
                            leftType,
                            rightType);
                    resultType = ConstantJavaType.LJAVA_LANG_STRING;
                } else if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger addition
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.add()
                    int addRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_ADD, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(addRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate add instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.iadd();
                        case ConstantJavaType.ABBR_LONG -> code.ladd();
                        case ConstantJavaType.ABBR_FLOAT -> code.fadd();
                        case ConstantJavaType.ABBR_DOUBLE -> code.dadd();
                    }
                }
            }
            case Sub -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger subtraction
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.subtract()
                    int subRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SUBTRACT, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(subRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate sub instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.isub();
                        case ConstantJavaType.ABBR_LONG -> code.lsub();
                        case ConstantJavaType.ABBR_FLOAT -> code.fsub();
                        case ConstantJavaType.ABBR_DOUBLE -> code.dsub();
                    }
                }
            }
            case Mul -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger multiplication
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.multiply()
                    int mulRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_MULTIPLY, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(mulRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate mul instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.imul();
                        case ConstantJavaType.ABBR_LONG -> code.lmul();
                        case ConstantJavaType.ABBR_FLOAT -> code.fmul();
                        case ConstantJavaType.ABBR_DOUBLE -> code.dmul();
                    }
                }
            }
            case Div -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger division
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.divide()
                    int divRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_DIVIDE, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(divRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate div instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.idiv();
                        case ConstantJavaType.ABBR_LONG -> code.ldiv();
                        case ConstantJavaType.ABBR_FLOAT -> code.fdiv();
                        case ConstantJavaType.ABBR_DOUBLE -> code.ddiv();
                    }
                }
            }
            case Mod -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger modulo
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.remainder()
                    int remRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_REMAINDER, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(remRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate rem instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.irem();
                        case ConstantJavaType.ABBR_LONG -> code.lrem();
                        case ConstantJavaType.ABBR_FLOAT -> code.frem();
                        case ConstantJavaType.ABBR_DOUBLE -> code.drem();
                    }
                }
            }
            case Exp -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType)) {
                    // BigInteger exponentiation
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand (base)
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                    // Generate right operand (exponent) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);
                    }

                    // Call BigInteger.pow(int)
                    int powRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_POW, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER);
                    code.invokevirtual(powRef);
                } else {
                    resultType = ConstantJavaType.ABBR_DOUBLE; // Math.pow returns double

                    // Generate left operand (base) and convert to double
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), ConstantJavaType.ABBR_DOUBLE);

                    // Generate right operand (exponent) and convert to double
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_DOUBLE);

                    // Call Math.pow(double, double)
                    int mathPowRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_MATH, ConstantJavaMethod.METHOD_POW, ConstantJavaDescriptor.DESCRIPTOR_D_D__D);
                    code.invokestatic(mathPowRef);
                }
            }
            case LShift -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType)) {
                    // BigInteger left shift
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);
                    }

                    // Call BigInteger.shiftLeft(int)
                    int shiftLeftRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SHIFT_LEFT, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER);
                    code.invokevirtual(shiftLeftRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);

                    // Generate appropriate shift instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.ishl();
                        case ConstantJavaType.ABBR_LONG -> code.lshl();
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
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType)) {
                    // BigInteger right shift
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);
                    }

                    // Call BigInteger.shiftRight(int)
                    int shiftRightRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SHIFT_RIGHT, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER);
                    code.invokevirtual(shiftRightRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);

                    // Generate appropriate shift instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.ishr();
                        case ConstantJavaType.ABBR_LONG -> code.lshr();
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
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType)) {
                    // BigInteger unsigned right shift - not directly supported
                    // Use shiftRight for signed shift (limitation documented)
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (isBigInteger(rightType)) {
                        // Convert BigInteger to int using intValue()
                        int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokevirtual(intValueRef);
                    } else {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);
                    }

                    // Call BigInteger.shiftRight(int) - note: this is signed shift
                    int shiftRightRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SHIFT_RIGHT, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER);
                    code.invokevirtual(shiftRightRef);
                } else {
                    // Determine the result type based on left operand
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand and convert to result type (int or long)
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand (shift amount) and convert to int
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), ConstantJavaType.ABBR_INTEGER);

                    // Generate appropriate unsigned shift instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.iushr();
                        case ConstantJavaType.ABBR_LONG -> code.lushr();
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
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise AND
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.and()
                    int andRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_AND, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(andRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise AND instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.iand();
                        case ConstantJavaType.ABBR_LONG -> code.land();
                    }
                }
            }
            case BitOr -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise OR
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.or()
                    int orRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_OR, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(orRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise OR instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.ior();
                        case ConstantJavaType.ABBR_LONG -> code.lor();
                    }
                }
            }
            case BitXor -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger bitwise XOR
                    resultType = ConstantJavaType.LJAVA_MATH_BIGINTEGER;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.xor()
                    int xorRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_XOR, "(Ljava/math/BigInteger;)Ljava/math/BigInteger;");
                    code.invokevirtual(xorRef);
                } else {
                    // Determine the widened result type
                    resultType = compiler.getTypeResolver().inferTypeFromExpr(binExpr);

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(rightType), resultType);

                    // Generate appropriate bitwise XOR instruction based on result type
                    switch (resultType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.ixor();
                        case ConstantJavaType.ABBR_LONG -> code.lxor();
                    }
                }
            }
            case EqEq, EqEqEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                // Result type for comparison is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger equality
                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.equals()
                    int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);
                    code.invokevirtual(equalsRef);
                } else {
                    // Determine the comparison type (widen to common type for primitives)
                    String comparisonType = TypeResolver.getWidenedType(leftType, rightType);
                    String leftPrimitive = TypeConversionUtils.getPrimitiveType(leftType);
                    String rightPrimitive = TypeConversionUtils.getPrimitiveType(rightType);

                    // Check if both are primitive types (NOT wrappers - those use Objects.equals)
                    boolean isPrimitiveComparison = leftType.equals(leftPrimitive) &&
                            rightType.equals(rightPrimitive) &&
                            (leftPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || leftPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || leftPrimitive.equals(ConstantJavaType.ABBR_DOUBLE) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_BYTE) || leftPrimitive.equals(ConstantJavaType.ABBR_SHORT) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_CHARACTER) || leftPrimitive.equals(ConstantJavaType.ABBR_BOOLEAN));

                    if (isPrimitiveComparison) {
                        // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                        ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(getSourceCode(), binExpr, comparisonType);

                        // Generate left operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                        }

                        // Generate right operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                        }

                        // Use direct bytecode comparison instructions
                        // Pattern: if not equal, jump to iconst_0, else fall through to iconst_1
                        switch (comparisonType) {
                            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN -> {
                                // int/boolean comparison: use if_icmpne (boolean values are represented as int on stack)
                                // if a != b, jump to push 0, else push 1
                                code.if_icmpne(7); // if not equal, jump to iconst_0
                                code.iconst(1);    // equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // not equal: push 0
                            }
                            case ConstantJavaType.ABBR_LONG -> {
                                // long comparison: use lcmp then ifne
                                code.lcmp();       // compare longs, result is 0 if equal
                                code.ifne(7);      // if non-zero (not equal), jump to iconst_0
                                code.iconst(1);    // equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // not equal: push 0
                            }
                            case ConstantJavaType.ABBR_FLOAT -> {
                                // float comparison: use fcmpl then ifne
                                code.fcmpl();      // compare floats, result is 0 if equal
                                code.ifne(7);      // if non-zero (not equal), jump to iconst_0
                                code.iconst(1);    // equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // not equal: push 0
                            }
                            case ConstantJavaType.ABBR_DOUBLE -> {
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
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                        if (TypeConversionUtils.isPrimitiveType(leftType)) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, leftType, TypeConversionUtils.getWrapperType(leftType));
                        }

                        // Generate right operand
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                        if (TypeConversionUtils.isPrimitiveType(rightType)) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, rightType, TypeConversionUtils.getWrapperType(rightType));
                        }

                        // Objects.equals returns boolean (Z) which is represented as 0 or 1
                        int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_OBJECTS, ConstantJavaMethod.METHOD_EQUALS, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                        code.invokestatic(equalsRef);
                    }
                }
            }
            case NotEq, NotEqEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                // Result type for comparison is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger inequality
                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.equals() and invert result
                    int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);
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
                            (leftPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || leftPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || leftPrimitive.equals(ConstantJavaType.ABBR_DOUBLE) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_BYTE) || leftPrimitive.equals(ConstantJavaType.ABBR_SHORT) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_CHARACTER) || leftPrimitive.equals(ConstantJavaType.ABBR_BOOLEAN));

                    if (isPrimitiveComparison) {
                        // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                        ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(getSourceCode(), binExpr, comparisonType);

                        // Generate left operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                        }

                        // Generate right operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                        }

                        // Use direct bytecode comparison instructions (inverted logic from EqEq)
                        // Pattern: if equal, jump to iconst_0, else fall through to iconst_1
                        switch (comparisonType) {
                            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN -> {
                                // int/boolean comparison: use if_icmpeq (boolean values are represented as int on stack)
                                // if a == b, jump to push 0, else push 1
                                code.if_icmpeq(7); // if equal, jump to iconst_0
                                code.iconst(1);    // not equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // equal: push 0
                            }
                            case ConstantJavaType.ABBR_LONG -> {
                                // long comparison: use lcmp then ifeq (opposite of ifne)
                                code.lcmp();       // compare longs, result is 0 if equal
                                code.ifeq(7);      // if zero (equal), jump to iconst_0
                                code.iconst(1);    // not equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // equal: push 0
                            }
                            case ConstantJavaType.ABBR_FLOAT -> {
                                // float comparison: use fcmpl then ifeq (opposite of ifne)
                                code.fcmpl();      // compare floats, result is 0 if equal
                                code.ifeq(7);      // if zero (equal), jump to iconst_0
                                code.iconst(1);    // not equal: push 1
                                code.gotoLabel(4); // jump over iconst_0
                                code.iconst(0);    // equal: push 0
                            }
                            case ConstantJavaType.ABBR_DOUBLE -> {
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
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                        if (TypeConversionUtils.isPrimitiveType(leftType)) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, leftType, TypeConversionUtils.getWrapperType(leftType));
                        }

                        // Generate right operand
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                        if (TypeConversionUtils.isPrimitiveType(rightType)) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, rightType, TypeConversionUtils.getWrapperType(rightType));
                        }

                        // Objects.equals returns boolean (Z) which is represented as 0 or 1
                        int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_OBJECTS, ConstantJavaMethod.METHOD_EQUALS, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
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
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                // Result type for comparison is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger less than / less than or equal
                    boolean isLtEq = binExpr.getOp() == Swc4jAstBinaryOp.LtEq;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.compareTo() - returns -1, 0, or 1
                    int compareToRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_COMPARE_TO, "(Ljava/math/BigInteger;)I");
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
                            (leftPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || leftPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || leftPrimitive.equals(ConstantJavaType.ABBR_DOUBLE) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_BYTE) || leftPrimitive.equals(ConstantJavaType.ABBR_SHORT) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_CHARACTER));

                    if (isPrimitiveComparison) {
                        // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                        ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(getSourceCode(), binExpr, comparisonType);

                        // Generate left operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                        }

                        // Generate right operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                        }

                        // Use direct bytecode comparison instructions
                        // Pattern: if condition is FALSE, jump to iconst_0, else fall through to iconst_1
                        boolean isLtEq = binExpr.getOp() == Swc4jAstBinaryOp.LtEq;
                        switch (comparisonType) {
                            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN -> {
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
                            case ConstantJavaType.ABBR_LONG -> {
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
                            case ConstantJavaType.ABBR_FLOAT -> {
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
                            case ConstantJavaType.ABBR_DOUBLE -> {
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
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr,
                                "Less than comparison not supported for non-primitive types. " +
                                        "Use comparable types or implement Comparable interface.");
                    }
                }
            }
            case Gt, GtEq -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                // Handle null types - default to Object for null literals
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                // Result type for comparison is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Check for BigInteger comparison
                if (isBigInteger(leftType) || isBigInteger(rightType)) {
                    // BigInteger greater than / greater than or equal
                    boolean isGtEq = binExpr.getOp() == Swc4jAstBinaryOp.GtEq;

                    // Generate left operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (!isBigInteger(leftType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        convertToBigInteger(code, classWriter, leftType);
                    }

                    // Generate right operand
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (!isBigInteger(rightType)) {
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        convertToBigInteger(code, classWriter, rightType);
                    }

                    // Call BigInteger.compareTo() - returns -1, 0, or 1
                    int compareToRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_COMPARE_TO, "(Ljava/math/BigInteger;)I");
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
                            (leftPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || leftPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || leftPrimitive.equals(ConstantJavaType.ABBR_DOUBLE) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_BYTE) || leftPrimitive.equals(ConstantJavaType.ABBR_SHORT) ||
                                    leftPrimitive.equals(ConstantJavaType.ABBR_CHARACTER));

                    if (isPrimitiveComparison) {
                        // Create ReturnTypeInfo for the comparison type to generate operands directly in the right type
                        ReturnTypeInfo compTypeInfo = ReturnTypeInfo.of(getSourceCode(), binExpr, comparisonType);

                        // Generate left operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getLeft(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, leftPrimitive, comparisonType);
                        }

                        // Generate right operand with type hint
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), compTypeInfo);
                        TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);
                        // Skip conversion for number literals - they're already generated in the target type
                        if (!isNumberLiteralWithTypeHint(binExpr.getRight(), comparisonType)) {
                            TypeConversionUtils.convertPrimitiveType(code, rightPrimitive, comparisonType);
                        }

                        // Use direct bytecode comparison instructions
                        // Pattern: if condition is FALSE, jump to iconst_0, else fall through to iconst_1
                        boolean isGtEq = binExpr.getOp() == Swc4jAstBinaryOp.GtEq;
                        switch (comparisonType) {
                            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN -> {
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
                            case ConstantJavaType.ABBR_LONG -> {
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
                            case ConstantJavaType.ABBR_FLOAT -> {
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
                            case ConstantJavaType.ABBR_DOUBLE -> {
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
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr,
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
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Get types of operands
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

                // Generate left operand
                compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);

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
                compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);

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
            case InstanceOf -> {
                // InstanceOf checks if the left operand is an instance of the type specified by the right operand
                // The right operand must be an identifier (class name)

                // Result type is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Get the class name from the right operand
                ISwc4jAstExpr rightExpr = binExpr.getRight();
                String className;
                if (rightExpr instanceof Swc4jAstIdent ident) {
                    className = ident.getSym();
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr.getRight(),
                            "Right operand of instanceof must be a type identifier, got: " + rightExpr.getClass().getSimpleName());
                }

                // Resolve the class name to its JVM internal name
                String internalName = compiler.getMemory().resolveType(className, rightExpr).getInternalName();

                // Generate the left operand (object to check)
                compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                // Add the class to constant pool and emit instanceof instruction
                int classRef = cp.addClass(internalName);
                code.instanceof_(classRef);
            }
            case In -> {
                // In operator checks if a key exists in a container (Map, List, or String)
                // Result type is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Determine the container type from the right operand
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                InContainerType containerType = determineInContainerType(binExpr.getRight(), rightType);

                switch (containerType) {
                    case MAP -> {
                        // For Maps: call map.containsKey(key)
                        // Generate left operand (key) - convert to String for JS semantics
                        String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                        // Convert to String if not already a String
                        if (!ConstantJavaType.LJAVA_LANG_STRING.equals(leftType)) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF,
                                    ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
                            code.invokestatic(valueOfRef);
                        }

                        // Generate right operand (map)
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);

                        // Swap to get: map, key on stack
                        code.swap();

                        // Call Map.containsKey(Object) -> boolean
                        int containsKeyRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, "containsKey",
                                ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);
                        code.invokeinterface(containsKeyRef, 2);
                    }
                    case LIST -> {
                        // For Lists: check if index is valid (0 <= index < size)
                        // Left operand is converted to int index
                        // For float/double, must be a whole number (1.0 is valid, 1.1 is not)
                        // Non-whole floats are converted to -1, which fails the bounds check
                        String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                        // Convert left operand to int, with whole number check for float/double
                        generateConvertToIntForIndex(code, classWriter, leftType);

                        // Generate right operand (list)
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);

                        // Get list size: List.size() -> int
                        int sizeRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokeinterface(sizeRef, 1);

                        // Now stack has: [index, size]
                        // Check: index >= 0 && index < size
                        // Pattern: if index < 0 or index >= size -> false, else true
                        generateInBoundsCheck(code);
                    }
                    case STRING -> {
                        // For Strings: check if index is valid (0 <= index < length)
                        // Left operand is converted to int index
                        // For float/double, must be a whole number (1.0 is valid, 1.1 is not)
                        // Non-whole floats are converted to -1, which fails the bounds check
                        String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

                        // Convert left operand to int, with whole number check for float/double
                        generateConvertToIntForIndex(code, classWriter, leftType);

                        // Generate right operand (string)
                        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);

                        // Get string length: String.length() -> int
                        int lengthRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_LENGTH, ConstantJavaDescriptor.DESCRIPTOR___I);
                        code.invokevirtual(lengthRef);

                        // Now stack has: [index, length]
                        // Check: index >= 0 && index < length
                        generateInBoundsCheck(code);
                    }
                }
            }
            case NullishCoalescing -> {
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());
                if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;

                boolean leftPrimitive = TypeConversionUtils.isPrimitiveType(leftType);
                boolean rightPrimitive = TypeConversionUtils.isPrimitiveType(rightType);

                if (leftPrimitive && rightPrimitive) {
                    resultType = TypeResolver.getWidenedType(leftType, rightType);
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(leftType), resultType);
                } else {
                    resultType = ConstantJavaType.LJAVA_LANG_OBJECT;

                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                    if (leftPrimitive) {
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, leftType, TypeConversionUtils.getWrapperType(leftType));
                    }

                    code.dup();
                    code.ifnonnull(0);
                    int ifnonnullOffsetPos = code.getCurrentOffset() - 2;
                    int ifnonnullOpcodePos = code.getCurrentOffset() - 3;

                    code.pop();
                    compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                    if (rightPrimitive) {
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, rightType, TypeConversionUtils.getWrapperType(rightType));
                    }

                    int endLabel = code.getCurrentOffset();
                    int ifnonnullOffset = endLabel - ifnonnullOpcodePos;
                    code.patchShort(ifnonnullOffsetPos, ifnonnullOffset);
                }
            }
            case LogicalOr -> {
                // LogicalOr (||) with short-circuit evaluation
                // If left is true, skip evaluating right and return true
                // If left is false, evaluate and return the right operand value

                // Result type is always boolean
                resultType = ConstantJavaType.ABBR_BOOLEAN;

                // Get types of operands
                String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
                String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

                // Generate left operand
                compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, classWriter, leftType);

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
                compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);
                // Unbox if it's a Boolean object
                TypeConversionUtils.unboxWrapperType(code, classWriter, rightType);

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
            String targetType = returnTypeInfo.type().getPrimitiveDescriptor();
            if (!ConstantJavaType.ABBR_VOID.equals(targetType) && !targetType.equals(resultType)) {
                TypeConversionUtils.convertPrimitiveType(code, resultType, targetType);
            }
        }
    }

    /**
     * Generates bytecode to convert a value on the stack to int for use as an index.
     * For float/double types, this also generates a whole number check - if the value
     * has a fractional part, the index is set to -1 (which will fail bounds check).
     * <p>
     * Handles primitive types (int, long, float, double, byte, short, char),
     * boxed types (Integer, Long, etc.), and String (via Integer.parseInt).
     * <p>
     * For floating point (where whole number check is needed):
     * <pre>
     *   [float on stack]
     *   dup           ; [float, float]
     *   f2i           ; [float, int]
     *   dup_x1        ; [int, float, int]
     *   i2f           ; [int, float, float_from_int]
     *   fcmpl         ; [int, cmp_result]
     *   ifeq KEEP     ; if equal (whole number), keep the int
     *   pop           ; not whole: pop the int
     *   iconst_m1     ; push -1 (invalid index)
     *   KEEP:         ; stack: [int] (valid index or -1)
     * </pre>
     *
     * @param code     the code builder
     * @param cp       the constant pool
     * @param leftType the type descriptor of the value on the stack
     */
    private void generateConvertToIntForIndex(CodeBuilder code, ClassWriter classWriter, String leftType) {
        var cp = classWriter.getConstantPool();
        switch (leftType) {
            case ConstantJavaType.ABBR_INTEGER -> {
                // Already int, no conversion needed
            }
            case ConstantJavaType.ABBR_LONG -> {
                // long to int
                code.l2i();
            }
            case ConstantJavaType.ABBR_FLOAT -> {
                // float to int with whole number check
                generateFloatToIntWithWholeCheck(code, classWriter);
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                // double to int with whole number check
                generateDoubleToIntWithWholeCheck(code, classWriter);
            }
            case ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_CHARACTER -> {
                // byte, short, char are already int-compatible on stack
            }
            case ConstantJavaType.LJAVA_LANG_STRING -> {
                // Safely parse string to int, returning MIN_VALUE for invalid formats
                generateSafeStringToInt(code, classWriter);
            }
            case ConstantJavaType.LJAVA_LANG_INTEGER -> {
                // Unbox Integer to int
                int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___I);
                code.invokevirtual(intValueRef);
            }
            case ConstantJavaType.LJAVA_LANG_LONG -> {
                // Unbox Long to long, then convert to int
                int longValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_LONG_VALUE, ConstantJavaDescriptor.DESCRIPTOR___J);
                code.invokevirtual(longValueRef);
                code.l2i();
            }
            case ConstantJavaType.LJAVA_LANG_FLOAT -> {
                // Unbox Float to float, then check for whole number and convert to int
                int floatValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_FLOAT_VALUE, ConstantJavaDescriptor.DESCRIPTOR___F);
                code.invokevirtual(floatValueRef);
                generateFloatToIntWithWholeCheck(code, classWriter);
            }
            case ConstantJavaType.LJAVA_LANG_DOUBLE -> {
                // Unbox Double to double, then check for whole number and convert to int
                int doubleValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_DOUBLE_VALUE, ConstantJavaDescriptor.DESCRIPTOR___D);
                code.invokevirtual(doubleValueRef);
                generateDoubleToIntWithWholeCheck(code, classWriter);
            }
            default -> {
                // For other object types, convert to String first, then safely parse
                int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF,
                        ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
                code.invokestatic(valueOfRef);
                generateSafeStringToInt(code, classWriter);
            }
        }
    }

    /**
     * Generates bytecode to convert double to int with whole number check.
     * If not a whole number, sets index to Integer.MIN_VALUE (which will fail bounds check).
     * Uses branch-free logic to avoid stackmap frame issues.
     * <p>
     * Stack: [double] -> [int] (valid index if whole, Integer.MIN_VALUE if not whole)
     */
    private void generateDoubleToIntWithWholeCheck(CodeBuilder code, ClassWriter classWriter) {
        var cp = classWriter.getConstantPool();
        // Stack: [double] (2 slots)
        code.dup2();     // [double, double]
        code.d2i();      // [double, int]
        code.dup_x2();   // [int, double, int]
        code.i2d();      // [int, double, double_from_int]
        code.dcmpl();    // [int, cmp] (0 if equal, -1 or 1 otherwise)

        // Compute signBit = (cmp | -cmp) >>> 31 ; 0 if cmp==0, 1 otherwise
        code.dup();      // [int, cmp, cmp]
        code.ineg();     // [int, cmp, -cmp]
        code.ior();      // [int, cmp|-cmp]
        code.bipush(31); // [int, cmp|-cmp, 31]
        code.iushr();    // [int, signBit] (0 or 1)

        // Compute mask = -signBit ; 0 if valid, -1 if invalid
        code.ineg();     // [int, mask] (0 or -1)

        // result = (index & ~mask) | (MIN_VALUE & mask)
        code.dup_x1();   // [mask, int, mask]
        code.iconst(-1); // [mask, int, mask, -1]
        code.ixor();     // [mask, int, ~mask]
        code.iand();     // [mask, int & ~mask]
        code.swap();     // [int & ~mask, mask]
        code.ldc(cp.addInteger(Integer.MIN_VALUE)); // [int & ~mask, mask, MIN_VALUE]
        code.iand();     // [int & ~mask, MIN_VALUE & mask]
        code.ior();      // [result]
    }

    /**
     * Generates bytecode to convert float to int with whole number check.
     * If not a whole number, sets index to Integer.MIN_VALUE (which will fail bounds check).
     * Uses branch-free logic to avoid stackmap frame issues.
     * <p>
     * Stack: [float] -> [int] (valid index if whole, Integer.MIN_VALUE if not whole)
     * <p>
     * Algorithm:
     * <pre>
     *   int index = (int) floatValue;
     *   float reconstructed = (float) index;
     *   int cmp = Float.compare(floatValue, reconstructed);
     *   // cmp is 0 if equal, non-zero otherwise
     *   // We want: if cmp != 0, return Integer.MIN_VALUE
     *   // Using: Integer.MIN_VALUE | (((cmp | -cmp) >> 31) & (index ^ Integer.MIN_VALUE))
     *   // When cmp == 0: (0 | 0) >> 31 = 0, result = MIN_VALUE | (0 & x) = MIN_VALUE | 0... wait that's wrong
     *   // Let me use a different approach: just convert and let bounds check handle it
     * </pre>
     * <p>
     * Actually, for simplicity, we use f2i directly. Non-whole floats will truncate,
     * but we do a separate check: we convert back to float and compare.
     * If not equal, we know it wasn't a whole number.
     * We set index = index - (cmp != 0 ? index + 1 : 0) which gives -1 for non-whole.
     */
    private void generateFloatToIntWithWholeCheck(CodeBuilder code, ClassWriter classWriter) {
        var cp = classWriter.getConstantPool();
        // Stack: [float]
        // Strategy: convert to int, check if valid, use arithmetic to get -1 for invalid
        // f2i truncates, so 1.9 -> 1. We need to detect this.
        // Compare original float with (float)(int)float
        // If different, set index to a negative value

        // Simple approach: just convert and check bounds will reject negative
        // But 1.9 in [0,1,2] would give true (index 1 is valid), which is wrong.

        // Better approach: use Integer.MIN_VALUE for invalid
        // We can compute: isWhole = (float == (float)(int)float) ? 1 : 0
        // Then: result = isWhole == 1 ? index : Integer.MIN_VALUE

        // Using branches is problematic due to stackmap, so let's use arithmetic:
        // signBit = ((cmp | -cmp) >>> 31)  ; 0 if cmp==0, 1 if cmp!=0
        // result = (index & ~(-signBit)) | (MIN_VALUE & (-signBit))
        // When cmp==0: signBit=0, -signBit=0, ~0=-1, result = index & -1 | MIN_VALUE & 0 = index
        // When cmp!=0: signBit=1, -signBit=-1, ~(-1)=0, result = index & 0 | MIN_VALUE & -1 = MIN_VALUE

        code.dup();      // [float, float]
        code.f2i();      // [float, int]
        code.dup_x1();   // [int, float, int]
        code.i2f();      // [int, float, float_from_int]
        code.fcmpl();    // [int, cmp] (0 if equal, -1 or 1 otherwise)

        // Compute signBit = (cmp | -cmp) >>> 31 ; 0 if cmp==0, 1 otherwise
        code.dup();      // [int, cmp, cmp]
        code.ineg();     // [int, cmp, -cmp]
        code.ior();      // [int, cmp|-cmp]
        code.bipush(31); // [int, cmp|-cmp, 31]
        code.iushr();    // [int, signBit] (0 or 1)

        // Compute mask = -signBit ; 0 if valid, -1 if invalid
        code.ineg();     // [int, mask] (0 or -1)

        // result = (index & ~mask) | (MIN_VALUE & mask)
        code.dup_x1();   // [mask, int, mask]
        code.iconst(-1); // [mask, int, mask, -1]
        code.ixor();     // [mask, int, ~mask]
        code.iand();     // [mask, int & ~mask]
        code.swap();     // [int & ~mask, mask]
        code.ldc(cp.addInteger(Integer.MIN_VALUE)); // [int & ~mask, mask, MIN_VALUE]
        code.iand();     // [int & ~mask, MIN_VALUE & mask]
        code.ior();      // [result]
    }

    /**
     * Generates bytecode to check if an index is within bounds.
     * Expects stack: [index, size]
     * Leaves on stack: 1 (true) if 0 <= index < size, 0 (false) otherwise
     * <p>
     * Pattern:
     * <pre>
     *   [index, size on stack]
     *   swap               ; [size, index]
     *   dup                ; [size, index, index]
     *   iflt FALSE_LT      ; if index < 0, jump to false_lt
     *   swap               ; [index, size]
     *   if_icmpge FALSE_GE ; if index >= size, jump to false_ge
     *   iconst_1
     *   goto END
     *   FALSE_LT:          ; stack: [size, index]
     *   pop2               ; clear both values
     *   FALSE_GE:          ; stack: []
     *   iconst_0
     *   END:
     * </pre>
     *
     * @param code the code builder
     */
    private void generateInBoundsCheck(CodeBuilder code) {
        // Stack: [index, size]
        code.swap();     // [size, index]
        code.dup();      // [size, index, index]

        // Check: if index < 0, jump to false
        code.iflt(0);
        int ltJumpOffsetPos = code.getCurrentOffset() - 2;
        int ltJumpOpcodePos = code.getCurrentOffset() - 3;

        // Stack: [size, index]
        code.swap();     // [index, size]

        // Check: if index >= size, jump to false
        code.if_icmpge(0);
        int geJumpOffsetPos = code.getCurrentOffset() - 2;
        int geJumpOpcodePos = code.getCurrentOffset() - 3;

        // Stack: []
        // Both checks passed, push true
        code.iconst(1);
        code.gotoLabel(0);
        int gotoEndOffsetPos = code.getCurrentOffset() - 2;
        int gotoEndOpcodePos = code.getCurrentOffset() - 3;

        // FALSE label for index < 0 path
        // Stack at this point: [size, index] (2 int values)
        int falseLtLabel = code.getCurrentOffset();
        code.pop2();     // pop both size and index

        // FALSE label for index >= size path - stack is already empty
        int falseGeLabel = code.getCurrentOffset();
        code.iconst(0);

        // END label
        int endLabel = code.getCurrentOffset();

        // Patch jumps
        code.patchShort(ltJumpOffsetPos, falseLtLabel - ltJumpOpcodePos);
        code.patchShort(geJumpOffsetPos, falseGeLabel - geJumpOpcodePos);
        code.patchShort(gotoEndOffsetPos, endLabel - gotoEndOpcodePos);
    }

    /**
     * Generates bytecode to safely convert a String to int for array/string index.
     * If the string is not a valid integer format (e.g., "abc", "1.0") or overflows,
     * returns Integer.MIN_VALUE which will fail the bounds check.
     * <p>
     * Stack: [String] -> [int]
     * <p>
     * Uses try-catch to handle NumberFormatException from Integer.parseInt().
     *
     * @param code        the code builder
     * @param classWriter the class writer
     */
    private void generateSafeStringToInt(CodeBuilder code, ClassWriter classWriter) {
        var cp = classWriter.getConstantPool();
        // Stack: [String]

        // Try block: call Integer.parseInt
        int tryStart = code.getCurrentOffset();
        int parseIntRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_PARSE_INT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING__I);
        code.invokestatic(parseIntRef);                  // [int]
        int tryEnd = code.getCurrentOffset();

        // Jump to end (skip handler)
        code.gotoLabel(0); // placeholder
        int gotoOpcodePos = code.getCurrentOffset() - 3;
        int gotoOffsetPos = code.getCurrentOffset() - 2;

        // Exception handler: pop exception, push MIN_VALUE
        int handlerStart = code.getCurrentOffset();
        code.pop();                                      // [] - discard exception
        code.ldc(cp.addInteger(Integer.MIN_VALUE));      // [int]

        int endLabel = code.getCurrentOffset();

        // Patch goto to jump to end
        code.patchShort(gotoOffsetPos, endLabel - gotoOpcodePos);

        // Add exception table entry for NumberFormatException
        int catchTypeRef = cp.addClass(ConstantJavaType.JAVA_LANG_NUMBERFORMATEXCEPTION);
        code.addExceptionHandler(tryStart, tryEnd, handlerStart, catchTypeRef);

        // Stack: [int]
    }

    /**
     * Check if a type is BigInteger.
     */
    private boolean isBigInteger(String type) {
        return ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(type);
    }

    /**
     * Check if an expression is a number literal that will be generated directly in the target type
     * when a type hint is provided. In this case, we should skip post-generation type conversion
     * because the literal was already generated in the target type.
     */
    private boolean isNumberLiteralWithTypeHint(ISwc4jAstExpr expr, String targetType) {
        // Number literals are generated directly in the target type when a primitive type hint is given
        return expr instanceof Swc4jAstNumber &&
                (targetType.equals(ConstantJavaType.ABBR_INTEGER) || targetType.equals(ConstantJavaType.ABBR_LONG) ||
                        targetType.equals(ConstantJavaType.ABBR_FLOAT) || targetType.equals(ConstantJavaType.ABBR_DOUBLE));
    }

    /**
     * Enum representing the container type for the 'in' operator.
     */
    private enum InContainerType {
        /**
         * List in container type.
         */
        LIST,
        /**
         * Map in container type.
         */
        MAP,
        /**
         * String in container type.
         */
        STRING
    }
}
