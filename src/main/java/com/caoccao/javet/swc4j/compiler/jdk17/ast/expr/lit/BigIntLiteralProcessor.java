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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
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

import java.math.BigInteger;

/**
 * Generator for BigInt literals.
 * Compiles JavaScript/TypeScript BigInt literals (e.g., 123n) to Java BigInteger objects.
 * Supports conversions to primitive types via BigInteger's intValue(), longValue(), etc.
 */
public final class BigIntLiteralProcessor extends BaseAstProcessor<Swc4jAstBigInt> {
    /**
     * Constructs a new BigInt literal processor.
     *
     * @param compiler the bytecode compiler
     */
    public BigIntLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts BigInteger on stack to primitive type.
     * Calls appropriate conversion method (intValue, longValue, etc.).
     */
    private void convertToPrimitive(CodeBuilder code, ClassWriter classWriter, ReturnTypeInfo returnTypeInfo) {
        var cp = classWriter.getConstantPool();
        String descriptor = returnTypeInfo.type().getPrimitiveDescriptor();

        switch (descriptor) {
            case ConstantJavaType.ABBR_INTEGER: // int
                int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.__I);
                code.invokevirtual(intValueRef);
                break;
            case ConstantJavaType.ABBR_LONG: // long
                int longValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_LONG_VALUE, ConstantJavaDescriptor.__J);
                code.invokevirtual(longValueRef);
                break;
            case ConstantJavaType.ABBR_DOUBLE: // double
                int doubleValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_DOUBLE_VALUE, ConstantJavaDescriptor.__D);
                code.invokevirtual(doubleValueRef);
                break;
            case ConstantJavaType.ABBR_FLOAT: // float
                int floatValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_FLOAT_VALUE, ConstantJavaDescriptor.__F);
                code.invokevirtual(floatValueRef);
                break;
            case ConstantJavaType.ABBR_BYTE: // byte
                int byteValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_BYTE_VALUE, ConstantJavaDescriptor.__B);
                code.invokevirtual(byteValueRef);
                break;
            case ConstantJavaType.ABBR_SHORT: // short
                int shortValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SHORT_VALUE, ConstantJavaDescriptor.__S);
                code.invokevirtual(shortValueRef);
                break;
            case ConstantJavaType.ABBR_BOOLEAN: // boolean
                // BigInteger.signum() returns -1, 0, or 1; non-zero is true
                // Use BigInteger.equals(ZERO) to check if zero
                int zeroFieldRef = cp.addFieldRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, "ZERO", ConstantJavaType.LJAVA_MATH_BIGINTEGER);
                code.getstatic(zeroFieldRef); // Push BigInteger.ZERO
                // Stack: [BigInteger, BigInteger.ZERO]
                int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
                code.invokevirtual(equalsRef); // Returns 1 if equal, 0 if not
                // Stack: [boolean] where 1=equal (false for our purposes), 0=not equal (true for our purposes)
                // We need to invert: 1→0, 0→1
                code.iconst(1);
                code.ixor(); // XOR with 1 to flip: 0→1, 1→0
                break;
            default:
                // For other descriptors, leave BigInteger on stack
                break;
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBigInt bigInt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get signed value
        BigInteger value = getSignedValue(bigInt);

        // Check if we need to convert to primitive type
        if (returnTypeInfo != null && returnTypeInfo.type() != ReturnType.OBJECT) {
            // Generate BigInteger first, then convert to primitive
            generateBigInteger(code, classWriter, value);
            convertToPrimitive(code, classWriter, returnTypeInfo);
        } else {
            // Return as BigInteger object (default)
            generateBigInteger(code, classWriter, value);
        }
    }

    /**
     * Generates bytecode to create a BigInteger object on the stack.
     * Uses optimization for common values (ZERO, ONE, TEN) when possible.
     */
    private void generateBigInteger(CodeBuilder code, ClassWriter classWriter, BigInteger value) {
        var cp = classWriter.getConstantPool();
        // Optimize for common values using static constants
        if (BigInteger.ZERO.equals(value)) {
            int zeroFieldRef = cp.addFieldRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, "ZERO", ConstantJavaType.LJAVA_MATH_BIGINTEGER);
            code.getstatic(zeroFieldRef);
        } else if (BigInteger.ONE.equals(value)) {
            int oneFieldRef = cp.addFieldRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, "ONE", ConstantJavaType.LJAVA_MATH_BIGINTEGER);
            code.getstatic(oneFieldRef);
        } else if (BigInteger.TEN.equals(value)) {
            int tenFieldRef = cp.addFieldRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, "TEN", ConstantJavaType.LJAVA_MATH_BIGINTEGER);
            code.getstatic(tenFieldRef);
        } else {
            // General case: new BigInteger(String)
            int bigIntegerClass = cp.addClass(ConstantJavaType.JAVA_MATH_BIGINTEGER);
            code.newInstance(bigIntegerClass);
            code.dup();

            // Push string value
            int stringIndex = cp.addString(value.toString());
            code.ldc(stringIndex);

            // Call constructor: BigInteger(String)
            int constructorRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INIT, "(Ljava/lang/String;)V");
            code.invokespecial(constructorRef);
        }
    }

    /**
     * Get the signed BigInteger value from a Swc4jAstBigInt node.
     * Applies the sign field to the value.
     */
    private BigInteger getSignedValue(Swc4jAstBigInt bigInt) {
        BigInteger value = bigInt.getValue();
        if (bigInt.getSign() == Swc4jAstBigIntSign.Minus) {
            return value.negate();
        }
        return value;
    }
}
