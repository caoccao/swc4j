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

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Number literal processor.
 */
public final class NumberLiteralProcessor extends BaseAstProcessor<Swc4jAstNumber> {
    /**
     * Instantiates a new Number literal processor.
     *
     * @param compiler the compiler
     */
    public NumberLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstNumber number,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        double value = number.getValue();

        // Check if we need to convert to float or double based on return type
        if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.FLOAT) {
            float floatValue = (float) value;
            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                code.fconst(floatValue);
            } else {
                int floatIndex = cp.addFloat(floatValue);
                code.ldc(floatIndex);
            }
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.DOUBLE) {
            // Double value
            if (value == 0.0 || value == 1.0) {
                code.dconst(value);
            } else {
                int doubleIndex = cp.addDouble(value);
                code.ldc2_w(doubleIndex);
            }
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
            // Long value
            long longValue = (long) value;
            if (longValue == 0L || longValue == 1L) {
                code.lconst(longValue);
            } else {
                int longIndex = cp.addLong(longValue);
                code.ldc2_w(longIndex);
            }
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_INTEGER.equals(returnTypeInfo.descriptor())) {
            // Box integer to Integer
            int intValue = (int) value;
            code.iconst(intValue);
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.I__LJAVA_LANG_INTEGER);
            code.invokestatic(valueOfRef);
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_BYTE.equals(returnTypeInfo.descriptor())) {
            // Box byte to Byte
            byte byteValue = (byte) value;
            code.iconst(byteValue);
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.B__LJAVA_LANG_BYTE);
            code.invokestatic(valueOfRef);
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_SHORT.equals(returnTypeInfo.descriptor())) {
            // Box short to Short
            short shortValue = (short) value;
            code.iconst(shortValue);
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.S__LJAVA_LANG_SHORT);
            code.invokestatic(valueOfRef);
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_LONG.equals(returnTypeInfo.descriptor())) {
            // Box long to Long
            long longValue = (long) value;
            if (longValue == 0L || longValue == 1L) {
                code.lconst(longValue);
            } else {
                int longIndex = cp.addLong(longValue);
                code.ldc2_w(longIndex);
            }
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.J__LJAVA_LANG_LONG);
            code.invokestatic(valueOfRef);
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_FLOAT.equals(returnTypeInfo.descriptor())) {
            // Box float to Float
            float floatValue = (float) value;
            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                code.fconst(floatValue);
            } else {
                int floatIndex = cp.addFloat(floatValue);
                code.ldc(floatIndex);
            }
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.F__LJAVA_LANG_FLOAT);
            code.invokestatic(valueOfRef);
        } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && ConstantJavaType.LJAVA_LANG_DOUBLE.equals(returnTypeInfo.descriptor())) {
            // Box double to Double
            if (value == 0.0 || value == 1.0) {
                code.dconst(value);
            } else {
                int doubleIndex = cp.addDouble(value);
                code.ldc2_w(doubleIndex);
            }
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.D__LJAVA_LANG_DOUBLE);
            code.invokestatic(valueOfRef);
        } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
            // Integer value - check if it fits in iconst/bipush/sipush range
            int intValue = (int) value;
            if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                code.iconst(intValue);
            } else {
                // Value requires ldc
                int intIndex = cp.addInteger(intValue);
                code.ldc(intIndex);
            }
        } else {
            // For double values
            if (value == 0.0 || value == 1.0) {
                code.dconst(value);
            } else {
                int doubleIndex = cp.addDouble(value);
                code.ldc2_w(doubleIndex);
            }
        }
    }
}
