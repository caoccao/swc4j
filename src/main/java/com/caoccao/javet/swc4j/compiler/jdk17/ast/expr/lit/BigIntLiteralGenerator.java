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
public final class BigIntLiteralGenerator extends BaseAstProcessor<Swc4jAstBigInt> {
    public BigIntLiteralGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts BigInteger on stack to primitive type.
     * Calls appropriate conversion method (intValue, longValue, etc.).
     */
    private void convertToPrimitive(CodeBuilder code, ClassWriter classWriter, ReturnTypeInfo returnTypeInfo) {
        var cp = classWriter.getConstantPool();
        String descriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
        if (descriptor == null) return;

        switch (descriptor) {
            case "I": // int
                int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                code.invokevirtual(intValueRef);
                break;
            case "J": // long
                int longValueRef = cp.addMethodRef("java/math/BigInteger", "longValue", "()J");
                code.invokevirtual(longValueRef);
                break;
            case "D": // double
                int doubleValueRef = cp.addMethodRef("java/math/BigInteger", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
                break;
            case "F": // float
                int floatValueRef = cp.addMethodRef("java/math/BigInteger", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
                break;
            case "B": // byte
                int byteValueRef = cp.addMethodRef("java/math/BigInteger", "byteValue", "()B");
                code.invokevirtual(byteValueRef);
                break;
            case "S": // short
                int shortValueRef = cp.addMethodRef("java/math/BigInteger", "shortValue", "()S");
                code.invokevirtual(shortValueRef);
                break;
            case "Z": // boolean
                // BigInteger.signum() returns -1, 0, or 1; non-zero is true
                // Use BigInteger.equals(ZERO) to check if zero
                int zeroFieldRef = cp.addFieldRef("java/math/BigInteger", "ZERO", "Ljava/math/BigInteger;");
                code.getstatic(zeroFieldRef); // Push BigInteger.ZERO
                // Stack: [BigInteger, BigInteger.ZERO]
                int equalsRef = cp.addMethodRef("java/math/BigInteger", "equals", "(Ljava/lang/Object;)Z");
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
            int zeroFieldRef = cp.addFieldRef("java/math/BigInteger", "ZERO", "Ljava/math/BigInteger;");
            code.getstatic(zeroFieldRef);
        } else if (BigInteger.ONE.equals(value)) {
            int oneFieldRef = cp.addFieldRef("java/math/BigInteger", "ONE", "Ljava/math/BigInteger;");
            code.getstatic(oneFieldRef);
        } else if (BigInteger.TEN.equals(value)) {
            int tenFieldRef = cp.addFieldRef("java/math/BigInteger", "TEN", "Ljava/math/BigInteger;");
            code.getstatic(tenFieldRef);
        } else {
            // General case: new BigInteger(String)
            int bigIntegerClass = cp.addClass("java/math/BigInteger");
            code.newInstance(bigIntegerClass);
            code.dup();

            // Push string value
            int stringIndex = cp.addString(value.toString());
            code.ldc(stringIndex);

            // Call constructor: BigInteger(String)
            int constructorRef = cp.addMethodRef("java/math/BigInteger", "<init>", "(Ljava/lang/String;)V");
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
