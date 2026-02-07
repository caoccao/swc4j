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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.math.BigInteger;

/**
 * The type Unary expression processor.
 */
public final class UnaryExpressionProcessor extends BaseAstProcessor<Swc4jAstUnaryExpr> {
    /**
     * Instantiates a new Unary expression processor.
     *
     * @param compiler the compiler
     */
    public UnaryExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUnaryExpr unaryExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        Swc4jAstUnaryOp op = unaryExpr.getOp();

        switch (op) {
            case Bang -> {
                // Handle logical NOT operator (!)
                ISwc4jAstExpr arg = unaryExpr.getArg();
                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);

                // Bang operator requires boolean operand
                if ("Z".equals(argType) || "Ljava/lang/Boolean;".equals(argType)) {
                    // Generate the operand
                    compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                    // Unbox if wrapper type
                    TypeConversionUtils.unboxWrapperType(code, classWriter, argType);

                    // Invert the boolean value using ifeq
                    // If value == 0 (false), jump to TRUE_LABEL and push 1 (true)
                    // Otherwise push 0 (false)
                    code.ifeq(0); // Placeholder offset
                    int ifeqOffsetPos = code.getCurrentOffset() - 2;
                    int ifeqOpcodePos = code.getCurrentOffset() - 3;

                    code.iconst(0); // Value was true (1), push false (0)

                    code.gotoLabel(0); // Placeholder offset
                    int gotoOffsetPos = code.getCurrentOffset() - 2;
                    int gotoOpcodePos = code.getCurrentOffset() - 3;

                    int trueLabel = code.getCurrentOffset();
                    code.iconst(1); // Value was false (0), push true (1)

                    int endLabel = code.getCurrentOffset();

                    // Patch offsets
                    int ifeqOffset = trueLabel - ifeqOpcodePos;
                    code.patchShort(ifeqOffsetPos, ifeqOffset);

                    int gotoOffset = endLabel - gotoOpcodePos;
                    code.patchShort(gotoOffsetPos, gotoOffset);
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                            "Logical NOT (!) requires boolean operand, got: " + argType);
                }
            }
            case Delete -> {
                // Handle delete operator (e.g., delete arr[1])
                ISwc4jAstExpr arg = unaryExpr.getArg();
                if (arg instanceof Swc4jAstMemberExpr memberExpr) {
                    String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

                    if (objType != null && objType.startsWith("[")) {
                        // Java array - delete not supported
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr, "Delete operator not supported on Java arrays - arrays have fixed size");
                    } else if ("Ljava/util/ArrayList;".equals(objType)) {
                        if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                            // delete arr[index] -> arr.remove(index)
                            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [ArrayList]
                            compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [ArrayList, index]

                            // Call ArrayList.remove(int)
                            int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                            code.invokevirtual(removeMethod); // Stack: [removedObject]
                            // Delete expression returns true in JavaScript, but we'll just leave the removed object
                            // Actually, delete should return boolean true
                            code.pop(); // Pop the removed object
                            code.iconst(1); // Push true (1)
                            return;
                        }
                    } else if ("Ljava/util/Map;".equals(objType)
                            || "Ljava/util/HashMap;".equals(objType)
                            || "Ljava/util/LinkedHashMap;".equals(objType)) {
                        if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [Map]
                            compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [Map, key]

                            String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                            if (keyType != null) {
                                String primitiveType = TypeConversionUtils.getPrimitiveType(keyType);
                                if (TypeConversionUtils.isPrimitiveType(primitiveType)) {
                                    String wrapperType = TypeConversionUtils.getWrapperType(primitiveType);
                                    TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, wrapperType);
                                }
                            }

                            int removeMethod = cp.addInterfaceMethodRef("java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
                            code.invokeinterface(removeMethod, 2); // Stack: [removedObject]
                            code.pop(); // Pop the removed object
                            code.iconst(1); // Push true (1)
                            return;
                        }
                    }
                }
                throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr, "Delete operator not yet supported for: " + arg);
            }
            case Minus -> {
                // Handle numeric negation
                ISwc4jAstExpr arg = unaryExpr.getArg();

                if (arg instanceof Swc4jAstNumber number) {
                    // Directly generate the negated value
                    double value = number.getValue();

                    // Check if we're dealing with a long type
                    if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
                        long longValue = value == 9223372036854775808.0 ? Long.MIN_VALUE : -(long) value;
                        if (longValue == 0L || longValue == 1L) {
                            code.lconst(longValue);
                        } else {
                            int longIndex = cp.addLong(longValue);
                            code.ldc2_w(longIndex);
                        }
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Long wrapper
                        long longValue = value == 9223372036854775808.0 ? Long.MIN_VALUE : -(long) value;
                        if (longValue == 0L || longValue == 1L) {
                            code.lconst(longValue);
                        } else {
                            int longIndex = cp.addLong(longValue);
                            code.ldc2_w(longIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with an Integer wrapper
                        int intValue = value == 2147483648.0 ? Integer.MIN_VALUE : -(int) value;
                        if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                            code.iconst(intValue);
                        } else {
                            int intIndex = cp.addInteger(intValue);
                            code.ldc(intIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Byte;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Byte wrapper
                        byte byteValue = (byte) -(int) value;
                        code.iconst(byteValue);
                        int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Short wrapper
                        short shortValue = (short) -(int) value;
                        code.iconst(shortValue);
                        int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Float wrapper
                        float floatValue = -(float) value;
                        if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                            code.fconst(floatValue);
                        } else {
                            int floatIndex = cp.addFloat(floatValue);
                            code.ldc(floatIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                        code.invokestatic(valueOfRef);
                    } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                            && "Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                        // Check if we're dealing with a Double wrapper
                        double doubleValue = -value;
                        if (doubleValue == 0.0 || doubleValue == 1.0) {
                            code.dconst(doubleValue);
                        } else {
                            int doubleIndex = cp.addDouble(doubleValue);
                            code.ldc2_w(doubleIndex);
                        }
                        int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        code.invokestatic(valueOfRef);
                    } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                        // Integer value
                        int intValue = value == 2147483648.0 ? Integer.MIN_VALUE : -(int) value;
                        // Check if value fits in the range supported by iconst/bipush/sipush
                        if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                            code.iconst(intValue);
                        } else {
                            // Use ldc for values outside sipush range
                            int intIndex = cp.addInteger(intValue);
                            code.ldc(intIndex);
                        }
                    } else {
                        // Floating point value - need to determine if it's float or double
                        // Check context to infer the type
                        String targetType = "D"; // Default to double
                        if (returnTypeInfo != null) {
                            if (returnTypeInfo.type() == ReturnType.FLOAT) {
                                targetType = "F";
                            } else if (returnTypeInfo.type() == ReturnType.DOUBLE) {
                                targetType = "D";
                            } else if (returnTypeInfo.descriptor() != null) {
                                if (returnTypeInfo.descriptor().equals("F")) {
                                    targetType = "F";
                                } else if (returnTypeInfo.descriptor().equals("D")) {
                                    targetType = "D";
                                }
                            }
                        }

                        if ("F".equals(targetType)) {
                            // Float type
                            float floatValue = -(float) value;
                            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                                code.fconst(floatValue);
                            } else {
                                int floatIndex = cp.addFloat(floatValue);
                                code.ldc(floatIndex);
                            }
                        } else {
                            // Double type (default)
                            double doubleValue = -value;
                            if (doubleValue == 0.0 || doubleValue == 1.0) {
                                code.dconst(doubleValue);
                            } else {
                                int doubleIndex = cp.addDouble(doubleValue);
                                code.ldc2_w(doubleIndex);
                            }
                        }
                    }
                } else if (arg instanceof Swc4jAstBigInt bigInt) {
                    // Handle BigInt negation
                    // Get the current value (with its sign applied)
                    BigInteger value = bigInt.getValue();
                    if (bigInt.getSign() == Swc4jAstBigIntSign.Minus) {
                        value = value.negate();
                    }
                    // Apply the unary minus operator
                    value = value.negate();

                    // Create a new BigInt with NoSign and the negated value
                    Swc4jAstBigInt negatedBigInt = Swc4jAstBigInt.create();
                    negatedBigInt.setValue(value);
                    negatedBigInt.setSign(Swc4jAstBigIntSign.NoSign);

                    compiler.getBigIntLiteralProcessor().generate(code, classWriter, negatedBigInt, returnTypeInfo);
                } else {
                    // For complex expressions, generate the expression first then negate
                    compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                    String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);
                    // Handle null type - should not happen for negation, default to int
                    if (argType == null) argType = "I";

                    String primitiveType = TypeConversionUtils.getPrimitiveType(argType);
                    if ("Z".equals(primitiveType)) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                                "Unary minus (-) not supported on boolean types");
                    }

                    // Handle BigInteger negation
                    if ("Ljava/math/BigInteger;".equals(argType)) {
                        // Call BigInteger.negate() method
                        int negateRef = cp.addMethodRef("java/math/BigInteger", "negate", "()Ljava/math/BigInteger;");
                        code.invokevirtual(negateRef);
                    } else {
                        // Check if argType is a wrapper before unboxing
                        boolean isWrapper = !argType.equals(primitiveType);

                        // Unbox wrapper types before negation
                        TypeConversionUtils.unboxWrapperType(code, classWriter, argType);

                        // Get the primitive type for determining which negation instruction to use
                        switch (primitiveType) {
                            case "D" -> code.dneg();
                            case "F" -> code.fneg();
                            case "J" -> code.lneg();
                            default -> code.ineg();
                        }

                        // Box back to wrapper type if original was wrapper
                        if (isWrapper) {
                            TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, argType);
                        }
                    }
                }
            }
            case Plus -> {
                // Handle unary plus (numeric coercion) - essentially a no-op for numeric types
                ISwc4jAstExpr arg = unaryExpr.getArg();

                // Special case for BigInt: +123n is just 123n (no-op)
                if (arg instanceof Swc4jAstBigInt bigInt) {
                    compiler.getBigIntLiteralProcessor().generate(code, classWriter, bigInt, returnTypeInfo);
                    return;
                }

                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);

                // Handle null type
                if (argType == null) argType = "I";

                // Get primitive type
                String primitiveType = TypeConversionUtils.getPrimitiveType(argType);

                // Check if type is numeric
                if (!TypeConversionUtils.isNumericPrimitive(primitiveType)) {
                    // Reject boolean
                    if (primitiveType.equals("Z")) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                                "Unary plus (+) not supported on boolean types");
                    }
                    // Reject string
                    if ("Ljava/lang/String;".equals(argType)) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                                "Unary plus (+) string-to-number conversion not supported. " +
                                        "Use explicit parsing: Integer.parseInt() or Double.parseDouble()");
                    }
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                            "Unary plus (+) not supported for type: " + argType);
                }

                // For numeric types, just generate the expression
                compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                // Check if argType is a wrapper before unboxing
                boolean isWrapper = !argType.equals(primitiveType);

                // Unbox wrapper types to get primitive
                if (isWrapper) {
                    TypeConversionUtils.unboxWrapperType(code, classWriter, argType);

                    // Box back to wrapper type if original was wrapper
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, argType);
                }
                // For primitive types, nothing more to do (no-op)
            }
            case Tilde -> {
                ISwc4jAstExpr arg = unaryExpr.getArg();

                if (arg instanceof Swc4jAstBigInt bigInt) {
                    BigInteger value = bigInt.getValue();
                    if (bigInt.getSign() == Swc4jAstBigIntSign.Minus) {
                        value = value.negate();
                    }
                    value = value.not();

                    Swc4jAstBigInt notBigInt = Swc4jAstBigInt.create();
                    notBigInt.setValue(value);
                    notBigInt.setSign(Swc4jAstBigIntSign.NoSign);
                    compiler.getBigIntLiteralProcessor().generate(code, classWriter, notBigInt, returnTypeInfo);
                    return;
                }

                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);
                if (argType == null) {
                    argType = "I";
                }

                if ("Ljava/math/BigInteger;".equals(argType)) {
                    compiler.getExpressionProcessor().generate(code, classWriter, arg, null);
                    int notRef = cp.addMethodRef("java/math/BigInteger", "not", "()Ljava/math/BigInteger;");
                    code.invokevirtual(notRef);
                    return;
                }
                String primitiveType = TypeConversionUtils.getPrimitiveType(argType);

                if (!TypeConversionUtils.isIntegerPrimitive(primitiveType)) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                            "Bitwise NOT (~) requires integer type, got: " + argType);
                }

                compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                boolean isWrapper = !argType.equals(primitiveType);
                TypeConversionUtils.unboxWrapperType(code, classWriter, argType);

                if ("J".equals(primitiveType)) {
                    int longIndex = cp.addLong(-1L);
                    code.ldc2_w(longIndex);
                    code.lxor();
                } else {
                    code.iconst(-1);
                    code.ixor();
                    TypeConversionUtils.convertPrimitiveType(code, "I", primitiveType);
                }

                if (isWrapper) {
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, argType);
                }
            }
            case TypeOf -> {
                ISwc4jAstExpr arg = unaryExpr.getArg();
                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);
                if (argType == null) {
                    argType = "Ljava/lang/Object;";
                }

                // Try static resolution
                String typeOfResult = TypeConversionUtils.getTypeOfResult(argType);

                // Check functional interface registry
                if (typeOfResult == null && argType.startsWith("L") && argType.endsWith(";")) {
                    String internalName = argType.substring(1, argType.length() - 1);
                    if (compiler.getMemory().getScopedFunctionalInterfaceRegistry().isFunctionalInterface(internalName)) {
                        typeOfResult = "function";
                    }
                }

                if (typeOfResult != null) {
                    // Statically resolved — skip evaluation if side-effect-free
                    boolean sideEffectFree = arg instanceof ISwc4jAstLit || arg instanceof Swc4jAstIdent;
                    if (!sideEffectFree) {
                        compiler.getExpressionProcessor().generate(code, classWriter, arg, null);
                        TypeConversionUtils.popByType(code, argType);
                    }
                    int resultIndex = cp.addString(typeOfResult);
                    code.ldc(resultIndex);
                } else {
                    // Runtime path — must be Object or unknown reference type
                    compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                    // instanceof chain: null→"object", String→"string", Number→"number",
                    // Boolean→"boolean", Character→"number", default→"object"
                    code.dup();
                    code.ifnull(0);
                    int ifNullOffsetPos = code.getCurrentOffset() - 2;
                    int ifNullOpcodePos = code.getCurrentOffset() - 3;

                    code.dup();
                    int stringClass = cp.addClass("java/lang/String");
                    code.instanceof_(stringClass);
                    code.ifne(0);
                    int ifStringOffsetPos = code.getCurrentOffset() - 2;
                    int ifStringOpcodePos = code.getCurrentOffset() - 3;

                    code.dup();
                    int numberClass = cp.addClass("java/lang/Number");
                    code.instanceof_(numberClass);
                    code.ifne(0);
                    int ifNumberOffsetPos = code.getCurrentOffset() - 2;
                    int ifNumberOpcodePos = code.getCurrentOffset() - 3;

                    code.dup();
                    int booleanClass = cp.addClass("java/lang/Boolean");
                    code.instanceof_(booleanClass);
                    code.ifne(0);
                    int ifBooleanOffsetPos = code.getCurrentOffset() - 2;
                    int ifBooleanOpcodePos = code.getCurrentOffset() - 3;

                    code.dup();
                    int characterClass = cp.addClass("java/lang/Character");
                    code.instanceof_(characterClass);
                    code.ifne(0);
                    int ifCharacterOffsetPos = code.getCurrentOffset() - 2;
                    int ifCharacterOpcodePos = code.getCurrentOffset() - 3;

                    // default → "object"
                    code.pop();
                    int objectIndex = cp.addString("object");
                    code.ldc(objectIndex);
                    code.gotoLabel(0);
                    int gotoEndOffsetPos = code.getCurrentOffset() - 2;
                    int gotoEndOpcodePos = code.getCurrentOffset() - 3;

                    // null → "object"
                    int nullLabel = code.getCurrentOffset();
                    code.pop();
                    code.ldc(objectIndex);
                    code.gotoLabel(0);
                    int gotoEndFromNullOffsetPos = code.getCurrentOffset() - 2;
                    int gotoEndFromNullOpcodePos = code.getCurrentOffset() - 3;

                    // String → "string"
                    int stringLabel = code.getCurrentOffset();
                    code.pop();
                    int stringIndex = cp.addString("string");
                    code.ldc(stringIndex);
                    code.gotoLabel(0);
                    int gotoEndFromStringOffsetPos = code.getCurrentOffset() - 2;
                    int gotoEndFromStringOpcodePos = code.getCurrentOffset() - 3;

                    // Number or Character → "number"
                    int numberLabel = code.getCurrentOffset();
                    code.pop();
                    int numberIndex = cp.addString("number");
                    code.ldc(numberIndex);
                    code.gotoLabel(0);
                    int gotoEndFromNumberOffsetPos = code.getCurrentOffset() - 2;
                    int gotoEndFromNumberOpcodePos = code.getCurrentOffset() - 3;

                    // Boolean → "boolean"
                    int booleanLabel = code.getCurrentOffset();
                    code.pop();
                    int booleanIndex = cp.addString("boolean");
                    code.ldc(booleanIndex);

                    int endLabel = code.getCurrentOffset();

                    // Patch branch offsets
                    code.patchShort(ifNullOffsetPos, nullLabel - ifNullOpcodePos);
                    code.patchShort(ifStringOffsetPos, stringLabel - ifStringOpcodePos);
                    code.patchShort(ifNumberOffsetPos, numberLabel - ifNumberOpcodePos);
                    code.patchShort(ifBooleanOffsetPos, booleanLabel - ifBooleanOpcodePos);
                    code.patchShort(ifCharacterOffsetPos, numberLabel - ifCharacterOpcodePos);

                    code.patchShort(gotoEndOffsetPos, endLabel - gotoEndOpcodePos);
                    code.patchShort(gotoEndFromNullOffsetPos, endLabel - gotoEndFromNullOpcodePos);
                    code.patchShort(gotoEndFromStringOffsetPos, endLabel - gotoEndFromStringOpcodePos);
                    code.patchShort(gotoEndFromNumberOffsetPos, endLabel - gotoEndFromNumberOpcodePos);
                }
            }
            case Void -> {
                ISwc4jAstExpr arg = unaryExpr.getArg();
                compiler.getExpressionProcessor().generate(code, classWriter, arg, null);

                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg);
                if (argType == null) {
                    argType = "Ljava/lang/Object;";
                }

                TypeConversionUtils.popByType(code, argType);

                if (returnTypeInfo != null && returnTypeInfo.type() != ReturnType.VOID) {
                    code.aconst_null();
                }
            }
        }
    }
}
