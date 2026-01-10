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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class CodeGenerator {
    private CodeGenerator() {
    }

    private static void appendOperandToStringBuilder(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String operandType,
            int appendString,
            int appendInt,
            int appendChar) throws Swc4jByteCodeCompilerException {
        switch (operandType) {
            case "Ljava/lang/String;" -> code.invokevirtual(appendString);
            case "I", "B", "S" -> code.invokevirtual(appendInt); // int, byte, short all use append(int)
            case "C" -> code.invokevirtual(appendChar);
            case "J" -> {
                // long
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "F" -> {
                // float
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "D" -> {
                // double
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Z" -> {
                // boolean
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            case "Ljava/lang/Character;" -> {
                // Unbox Character to char
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                code.invokevirtual(charValueRef);
                code.invokevirtual(appendChar);
            }
            case "Ljava/lang/Byte;", "Ljava/lang/Short;", "Ljava/lang/Integer;" -> {
                // Unbox to int, then append
                String wrapperClass = operandType.substring(1, operandType.length() - 1); // Remove L and ;
                String methodName = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "byteValue";
                    case "Ljava/lang/Short;" -> "shortValue";
                    case "Ljava/lang/Integer;" -> "intValue";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                String returnType = switch (operandType) {
                    case "Ljava/lang/Byte;" -> "B";
                    case "Ljava/lang/Short;" -> "S";
                    case "Ljava/lang/Integer;" -> "I";
                    default -> throw new Swc4jByteCodeCompilerException("Unexpected type: " + operandType);
                };
                int unboxRef = cp.addMethodRef(wrapperClass, methodName, "()" + returnType);
                code.invokevirtual(unboxRef);
                code.invokevirtual(appendInt); // byte, short, int all use append(int)
            }
            case "Ljava/lang/Long;" -> {
                // Unbox Long to long
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
                int appendLong = cp.addMethodRef("java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendLong);
            }
            case "Ljava/lang/Float;" -> {
                // Unbox Float to float
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
                int appendFloat = cp.addMethodRef("java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendFloat);
            }
            case "Ljava/lang/Double;" -> {
                // Unbox Double to double
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
                int appendDouble = cp.addMethodRef("java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendDouble);
            }
            case "Ljava/lang/Boolean;" -> {
                // Unbox Boolean to boolean
                int booleanValueRef = cp.addMethodRef("java/lang/Boolean", "booleanValue", "()Z");
                code.invokevirtual(booleanValueRef);
                int appendBoolean = cp.addMethodRef("java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendBoolean);
            }
            default -> {
                // For any other object type, use append(Object)
                int appendObject = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendObject);
            }
        }
    }

    private static void boxPrimitiveType(CodeBuilder code, ClassWriter.ConstantPool cp, String primitiveType, String targetType) {
        // Only box if targetType is a wrapper
        if (!targetType.startsWith("Ljava/lang/")) {
            return; // Target is primitive, no boxing needed
        }

        switch (primitiveType) {
            case "I" -> {
                if ("Ljava/lang/Integer;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "Z" -> {
                if ("Ljava/lang/Boolean;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "B" -> {
                if ("Ljava/lang/Byte;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "C" -> {
                if ("Ljava/lang/Character;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "S" -> {
                if ("Ljava/lang/Short;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "J" -> {
                if ("Ljava/lang/Long;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "F" -> {
                if ("Ljava/lang/Float;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                    code.invokestatic(valueOfRef);
                }
            }
            case "D" -> {
                if ("Ljava/lang/Double;".equals(targetType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                    code.invokestatic(valueOfRef);
                }
            }
        }
    }

    private static void collectStringConcatOperands(
            ISwc4jAstExpr expr,
            java.util.List<ISwc4jAstExpr> operands,
            java.util.List<String> operandTypes,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        // If this expression is a binary Add that results in a String, collect its operands
        if (expr instanceof Swc4jAstBinExpr binExpr && binExpr.getOp() == Swc4jAstBinaryOp.Add) {
            String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
            if ("Ljava/lang/String;".equals(exprType)) {
                // This is a string concatenation - collect operands recursively
                collectStringConcatOperands(binExpr.getLeft(), operands, operandTypes, context, options);
                collectStringConcatOperands(binExpr.getRight(), operands, operandTypes, context, options);
                return;
            }
        }
        // Not a string concatenation - add this expression as an operand
        operands.add(expr);
        String operandType = TypeResolver.inferTypeFromExpr(expr, context, options);
        // If type is null (e.g., for null literal), default to Object
        operandTypes.add(operandType != null ? operandType : "Ljava/lang/Object;");
    }

    private static void convertPrimitiveType(CodeBuilder code, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return; // No conversion needed
        }

        // byte, short, char are stored as int on the stack, so they start as "I" after unboxing
        String stackFromType = switch (fromType) {
            case "B", "S", "C" -> "I";
            default -> fromType;
        };

        // Convert from stack type to target type
        switch (stackFromType) {
            case "I" -> {
                switch (toType) {
                    case "J" -> code.i2l();
                    case "F" -> code.i2f();
                    case "D" -> code.i2d();
                }
            }
            case "J" -> {
                switch (toType) {
                    case "I" -> code.l2i();
                    case "F" -> code.l2f();
                    case "D" -> code.l2d();
                }
            }
            case "F" -> {
                switch (toType) {
                    case "I" -> code.f2i();
                    case "J" -> code.f2l();
                    case "D" -> code.f2d();
                }
            }
            case "D" -> {
                switch (toType) {
                    case "I" -> code.d2i();
                    case "J" -> code.d2l();
                    case "F" -> code.d2f();
                }
            }
        }
    }

    public static void generateAssignExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstAssignExpr assignExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle assignments like arr[1] = value or arr.length = 0
        var left = assignExpr.getLeft();
        if (left instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

            if (objType != null && objType.startsWith("[")) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value - array element assignment
                    generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                    generateExpr(code, cp, computedProp.getExpr(), null, context, options); // Stack: [array, index]

                    // Convert index to int if needed
                    String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                    if (indexType != null && !"I".equals(indexType)) {
                        convertPrimitiveType(code, getPrimitiveType(indexType), "I");
                    }

                    // Generate the value to store
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
                    generateExpr(code, cp, assignExpr.getRight(), null, context, options); // Stack: [array, index, value]

                    // Unbox if needed
                    unboxWrapperType(code, cp, valueType);

                    // Convert to target element type if needed
                    String elemType = objType.substring(1); // Remove leading "["
                    String valuePrimitive = getPrimitiveType(valueType);
                    convertPrimitiveType(code, valuePrimitive, elemType);

                    // Duplicate value and place it below array and index so it's left after store
                    // Stack: [array, index, value] -> [value, array, index, value]
                    if ("D".equals(elemType) || "J".equals(elemType)) {
                        code.dup2_x2(); // For wide types (double, long)
                    } else {
                        code.dup_x2(); // For single-slot types
                    }

                    // Use appropriate array store instruction
                    // Stack: [value, array, index, value] -> [value] after store
                    switch (elemType) {
                        case "Z", "B" -> code.bastore(); // boolean and byte
                        case "C" -> code.castore(); // char
                        case "S" -> code.sastore(); // short
                        case "I" -> code.iastore(); // int
                        case "J" -> code.lastore(); // long
                        case "F" -> code.fastore(); // float
                        case "D" -> code.dastore(); // double
                        default -> code.aastore(); // reference types
                    }
                    // The duplicated value is now on the stack as the assignment result
                    return;
                }

                // Check if it's arr.length = newLength - NOT SUPPORTED for Java arrays
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        throw new Swc4jByteCodeCompilerException("Cannot set length on Java array - array size is fixed");
                    }
                }
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // Check if it's arr[index] = value
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value -> arr.set(index, value)
                    generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                    generateExpr(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]
                    generateExpr(code, cp, assignExpr.getRight(), null, context, options); // Stack: [ArrayList, index, value]

                    // Box value if needed
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
                    if (isPrimitiveType(valueType)) {
                        String wrapperType = getWrapperType(valueType);
                        // wrapperType is already in the form "Ljava/lang/Integer;" so use it directly
                        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
                        int valueOfRef = cp.addMethodRef(className, "valueOf", "(" + valueType + ")" + wrapperType);
                        code.invokestatic(valueOfRef); // Stack: [ArrayList, index, boxedValue]
                    }

                    // Call ArrayList.set(int, Object)
                    int setMethod = cp.addMethodRef("java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
                    code.invokevirtual(setMethod); // Stack: [oldValue] - the return value of set() is the previous value
                    // Leave the value on stack for expression statements to pop
                    return;
                }

                // Check if it's arr.length = newLength
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        // arr.length = newLength
                        // Special case: arr.length = 0 -> arr.clear()
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number && number.getValue() == 0.0) {
                            generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                            int clearMethod = cp.addMethodRef("java/util/ArrayList", "clear", "()V");
                            code.invokevirtual(clearMethod); // Stack: []
                            // Assignment expression should return the assigned value (0 in this case)
                            code.iconst(0); // Stack: [0]
                            return;
                        }

                        // General case for constant new length (like arr.length = 2)
                        // Use ArrayList.subList(newLength, size()).clear() to remove excess elements
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number) {
                            int newLength = (int) number.getValue();

                            // Call arr.subList(newLength, arr.size()).clear()
                            generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                            code.dup(); // Stack: [ArrayList, ArrayList] - keep one for potential use
                            code.iconst(newLength); // Stack: [ArrayList, ArrayList, newLength]

                            // Get arr.size() - need to load ArrayList again
                            generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList, ArrayList, newLength, ArrayList]
                            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                            code.invokevirtual(sizeMethod); // Stack: [ArrayList, ArrayList, newLength, size]

                            // Call subList(newLength, size) on the second ArrayList
                            int subListMethod = cp.addMethodRef("java/util/ArrayList", "subList", "(II)Ljava/util/List;");
                            code.invokevirtual(subListMethod); // Stack: [ArrayList, List]

                            // Call clear() on the List
                            int clearMethod2 = cp.addInterfaceMethodRef("java/util/List", "clear", "()V");
                            code.invokeinterface(clearMethod2, 1); // Stack: [ArrayList]

                            // Assignment expression returns the assigned value (newLength), not the ArrayList
                            code.pop(); // Pop the ArrayList we kept, Stack: []
                            code.iconst(newLength); // Stack: [newLength]
                            return;
                        }

                        // For non-constant expressions, we need more complex handling
                        throw new Swc4jByteCodeCompilerException("Setting array length to non-constant values not yet supported");
                    }
                }
            }
        }
        throw new Swc4jByteCodeCompilerException("Assignment expression not yet supported: " + left);
    }

    public static void generateBinExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        Swc4jAstBinaryOp op = binExpr.getOp();

        if (op == Swc4jAstBinaryOp.Add) {
            String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
            String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);
            // Handle null types - default to Object for null literals
            if (leftType == null) leftType = "Ljava/lang/Object;";
            if (rightType == null) rightType = "Ljava/lang/Object;";

            // Check if this is string concatenation
            if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                generateStringConcat(code, cp, binExpr.getLeft(), binExpr.getRight(), leftType, rightType, context, options);
            } else {
                // Determine the widened result type
                String resultType = TypeResolver.inferTypeFromExpr(binExpr, context, options);

                // Generate left operand
                generateExpr(code, cp, binExpr.getLeft(), null, context, options);
                unboxWrapperType(code, cp, leftType);
                convertPrimitiveType(code, getPrimitiveType(leftType), resultType);

                // Generate right operand
                generateExpr(code, cp, binExpr.getRight(), null, context, options);
                unboxWrapperType(code, cp, rightType);
                convertPrimitiveType(code, getPrimitiveType(rightType), resultType);

                // Generate appropriate add instruction based on result type
                switch (resultType) {
                    case "I" -> code.iadd();
                    case "J" -> code.ladd();
                    case "F" -> code.fadd();
                    case "D" -> code.dadd();
                }
            }
        }
    }

    public static void generateCallExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle method calls on arrays (e.g., arr.push(value))
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

            if (objType != null && objType.startsWith("[")) {
                // Java array - method calls not supported
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }
                throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on Java arrays - arrays have fixed size");
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // Generate code for the object (ArrayList)
                generateExpr(code, cp, memberExpr.getObj(), null, context, options);

                // Get the method name
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }

                if ("push".equals(methodName)) {
                    // arr.push(value) -> arr.add(value)
                    if (!callExpr.getArgs().isEmpty()) {
                        var arg = callExpr.getArgs().get(0);
                        generateExpr(code, cp, arg.getExpr(), null, context, options);
                        // Box if primitive
                        String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                        if (argType != null && isPrimitiveType(argType)) {
                            boxPrimitiveType(code, cp, argType, getWrapperType(argType));
                        }
                        int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
                        code.invokevirtual(addMethod);
                        code.pop(); // Pop the boolean return value
                    }
                    // push() returns void in our implementation
                    return;
                }
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Call expression not yet supported");
    }

    public static byte[] generateClassBytecode(
            String internalClassName,
            Swc4jAstClass clazz,
            ByteCodeCompilerOptions options) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(internalClassName);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Generate default constructor
        generateDefaultConstructor(classWriter, cp);

        // Generate methods
        for (ISwc4jAstClassMember member : clazz.getBody()) {
            if (member instanceof Swc4jAstClassMethod method) {
                generateMethod(classWriter, cp, method, options);
            }
        }

        return classWriter.toByteArray();
    }

    public static void generateDefaultConstructor(ClassWriter classWriter, ClassWriter.ConstantPool cp) {
        // Generate: public <init>() { super(); }
        int superCtorRef = cp.addMethodRef("java/lang/Object", "<init>", "()V");

        CodeBuilder code = new CodeBuilder();
        code.aload(0)                    // load this
                .invokespecial(superCtorRef) // call super()
                .returnVoid();               // return

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                "<init>",
                "()V",
                code.toByteArray(),
                1, // max stack
                1  // max locals (this)
        );
    }

    public static void generateExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        if (expr instanceof Swc4jAstTsAsExpr asExpr) {
            // Handle explicit type cast (e.g., a as double)
            String targetType = TypeResolver.inferTypeFromExpr(asExpr, context, options);
            String innerType = TypeResolver.inferTypeFromExpr(asExpr.getExpr(), context, options);
            // Handle null types - should not happen for cast expressions, but default to Object if it does
            if (targetType == null) targetType = "Ljava/lang/Object;";
            if (innerType == null) innerType = "Ljava/lang/Object;";

            // Generate code for the inner expression
            generateExpr(code, cp, asExpr.getExpr(), null, context, options);

            // Unbox if the inner expression is a wrapper type
            unboxWrapperType(code, cp, innerType);

            // Get the primitive types for conversion
            String innerPrimitive = getPrimitiveType(innerType);
            String targetPrimitive = getPrimitiveType(targetType);

            // Convert from inner primitive type to target primitive type
            convertPrimitiveType(code, innerPrimitive, targetPrimitive);

            // Box if the target type is a wrapper
            boxPrimitiveType(code, cp, targetPrimitive, targetType);
        } else if (expr instanceof Swc4jAstStr str) {
            String value = str.getValue();
            // Check if we need to convert to char based on return type
            if (returnTypeInfo != null && (returnTypeInfo.type() == ReturnType.CHAR
                    || (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())))) {
                // Convert string to char - use first character
                if (value.length() > 0) {
                    char charValue = value.charAt(0);
                    code.iconst(charValue);
                    // Box to Character if needed
                    if (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                        int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                        code.invokestatic(valueOfRef);
                    }
                } else {
                    // Empty string, use null character
                    code.iconst(0);
                    if (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                        int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                        code.invokestatic(valueOfRef);
                    }
                }
            } else {
                // Regular string
                int stringIndex = cp.addString(value);
                code.ldc(stringIndex);
            }
        } else if (expr instanceof Swc4jAstNumber number) {
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
                    && "Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                // Box integer to Integer
                int intValue = (int) value;
                code.iconst(intValue);
                int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Byte;".equals(returnTypeInfo.descriptor())) {
                // Box byte to Byte
                byte byteValue = (byte) value;
                code.iconst(byteValue);
                int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                // Box short to Short
                short shortValue = (short) value;
                code.iconst(shortValue);
                int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                // Box long to Long
                long longValue = (long) value;
                if (longValue == 0L || longValue == 1L) {
                    code.lconst(longValue);
                } else {
                    int longIndex = cp.addLong(longValue);
                    code.ldc2_w(longIndex);
                }
                int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                // Box float to Float
                float floatValue = (float) value;
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
                // Box double to Double
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    int doubleIndex = cp.addDouble(value);
                    code.ldc2_w(doubleIndex);
                }
                int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                code.invokestatic(valueOfRef);
            } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                code.iconst((int) value);
            } else {
                // For double values
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    int doubleIndex = cp.addDouble(value);
                    code.ldc2_w(doubleIndex);
                }
            }
        } else if (expr instanceof Swc4jAstBool bool) {
            boolean value = bool.isValue();
            // Check if we need to box to Boolean
            if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Boolean;".equals(returnTypeInfo.descriptor())) {
                // Box boolean to Boolean
                code.iconst(value ? 1 : 0);
                int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                code.invokestatic(valueOfRef);
            } else {
                // Primitive boolean
                code.iconst(value ? 1 : 0);
            }
        } else if (expr instanceof Swc4jAstNull) {
            // null literal - always push null reference onto the stack
            code.aconst_null();
        } else if (expr instanceof Swc4jAstArrayLit arrayLit) {
            // Check if we should generate a Java array or ArrayList
            boolean isJavaArray = returnTypeInfo != null &&
                    returnTypeInfo.descriptor() != null &&
                    returnTypeInfo.descriptor().startsWith("[");

            if (isJavaArray) {
                // Generate Java array
                generateJavaArray(code, cp, arrayLit, returnTypeInfo.descriptor(), context, options);
            } else {
                // Array literal - convert to ArrayList
                int arrayListClass = cp.addClass("java/util/ArrayList");
                int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                int arrayListAdd = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

                // Create new ArrayList instance
                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInit);

                // Add each element to the list
                for (var elemOpt : arrayLit.getElems()) {
                    if (elemOpt.isPresent()) {
                        var elem = elemOpt.get();
                        code.dup(); // Duplicate ArrayList reference
                        // Generate code for the element expression - ensure it's boxed
                        ISwc4jAstExpr elemExpr = elem.getExpr();
                        String elemType = TypeResolver.inferTypeFromExpr(elemExpr, context, options);
                        if (elemType == null) elemType = "Ljava/lang/Object;";

                        generateExpr(code, cp, elemExpr, null, context, options);

                        // Box primitives to objects
                        if ("I".equals(elemType) || "Z".equals(elemType) || "B".equals(elemType) ||
                                "C".equals(elemType) || "S".equals(elemType) || "J".equals(elemType) ||
                                "F".equals(elemType) || "D".equals(elemType)) {
                            boxPrimitiveType(code, cp, elemType, getWrapperType(elemType));
                        }

                        // Call ArrayList.add(Object)
                        code.invokevirtual(arrayListAdd);
                        code.pop(); // Pop the boolean return value from add()
                    }
                }
                // ArrayList reference is now on top of stack
            }
        } else if (expr instanceof Swc4jAstIdent ident) {
            String varName = ident.getSym();
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                switch (localVar.type()) {
                    case "I", "S", "C", "Z", "B" -> code.iload(localVar.index());
                    case "J" -> code.lload(localVar.index());
                    case "F" -> code.fload(localVar.index());
                    case "D" -> code.dload(localVar.index());
                    default -> code.aload(localVar.index());
                }

                // Handle boxing if needed
                if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT && returnTypeInfo.descriptor() != null) {
                    // Check if we need to box a primitive to its wrapper
                    switch (localVar.type()) {
                        case "I" -> {
                            if ("Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "Z" -> {
                            if ("Ljava/lang/Boolean;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "B" -> {
                            if ("Ljava/lang/Byte;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "C" -> {
                            if ("Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "S" -> {
                            if ("Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "J" -> {
                            if ("Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "F" -> {
                            if ("Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                        case "D" -> {
                            if ("Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                                int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                                code.invokestatic(valueOfRef);
                            }
                        }
                    }
                }
            }
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            // Handle member access (e.g., arr.length)
            generateMemberExpr(code, cp, memberExpr, context, options);

            // If the member expression returns a primitive and we need an Object, box it
            if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT) {
                String exprType = TypeResolver.inferTypeFromExpr(memberExpr, context, options);
                if ("I".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                    code.invokestatic(valueOfRef);
                } else if ("Z".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                    code.invokestatic(valueOfRef);
                } else if ("D".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                    code.invokestatic(valueOfRef);
                } else if ("J".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                    code.invokestatic(valueOfRef);
                } else if ("F".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                    code.invokestatic(valueOfRef);
                } else if ("B".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                    code.invokestatic(valueOfRef);
                } else if ("C".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                    code.invokestatic(valueOfRef);
                } else if ("S".equals(exprType)) {
                    int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                    code.invokestatic(valueOfRef);
                }
            }
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            // Handle method calls (e.g., arr.push(value))
            generateCallExpr(code, cp, callExpr, context, options);
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            // Handle assignment expressions (e.g., arr[1] = value, arr.length = 0)
            generateAssignExpr(code, cp, assignExpr, context, options);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            generateBinExpr(code, cp, binExpr, context, options);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            generateUnaryExpr(code, cp, unaryExpr, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            // For parenthesized expressions, generate code for the inner expression
            generateExpr(code, cp, parenExpr.getExpr(), returnTypeInfo, context, options);
        }
    }

    private static void generateJavaArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstArrayLit arrayLit,
            String arrayDescriptor,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
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

                generateExpr(code, cp, elemExpr, null, context, options);

                // Unbox if needed
                unboxWrapperType(code, cp, exprType);

                // Convert to target type if needed
                String exprPrimitive = getPrimitiveType(exprType);
                convertPrimitiveType(code, exprPrimitive, elemType);

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

    public static void generateMemberExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle member access on arrays (e.g., arr.length or arr[index])
        String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

        if (objType != null && objType.startsWith("[")) {
            // Java array operations
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] - array element access
                generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                generateExpr(code, cp, computedProp.getExpr(), null, context, options); // Stack: [array, index]

                // Convert index to int if needed
                String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                if (indexType != null && !"I".equals(indexType)) {
                    convertPrimitiveType(code, getPrimitiveType(indexType), "I");
                }

                // Use appropriate array load instruction based on element type
                String elemType = objType.substring(1); // Remove leading "["
                switch (elemType) {
                    case "Z", "B" -> code.baload(); // boolean and byte
                    case "C" -> code.caload(); // char
                    case "S" -> code.saload(); // short
                    case "I" -> code.iaload(); // int
                    case "J" -> code.laload(); // long
                    case "F" -> code.faload(); // float
                    case "D" -> code.daload(); // double
                    default -> code.aaload(); // reference types
                }
                return;
            }

            // Named property access
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length - use arraylength instruction
                    generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                    code.arraylength(); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/ArrayList;".equals(objType)) {
            // ArrayList operations
            // Check if it's a computed property (arr[index]) or named property (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] -> arr.get(index)
                generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                generateExpr(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]

                // Convert to int if needed
                String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                if (!"I".equals(indexType)) {
                    // TODO: Handle type conversion
                }

                // Call ArrayList.get(int)
                int getMethod = cp.addMethodRef("java/util/ArrayList", "get", "(I)Ljava/lang/Object;");
                code.invokevirtual(getMethod); // Stack: [Object]
                return;
            }

            // Named property access (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length -> arr.size()
                    generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                    int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                    code.invokevirtual(sizeMethod); // Stack: [int]
                    return;
                }
            }
        }
        // For unsupported member expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Member expression not yet supported: " + memberExpr.getProp());
    }

    public static void generateMethod(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            Swc4jAstClassMethod method,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        ISwc4jAstPropName key = method.getKey();
        String methodName = getMethodName(key);
        Swc4jAstFunction function = method.getFunction();

        // Only handle methods with bodies
        var bodyOpt = function.getBody();
        if (bodyOpt.isPresent()) {
            try {
                Swc4jAstBlockStmt body = bodyOpt.get();
                CompilationContext context = new CompilationContext();

                // Analyze function parameters and allocate local variable slots
                VariableAnalyzer.analyzeParameters(function, context, options);

                // Analyze variable declarations and infer types
                VariableAnalyzer.analyzeVariableDeclarations(body, context, options);

                // Determine return type from method body or explicit annotation
                ReturnTypeInfo returnTypeInfo = TypeResolver.analyzeReturnType(function, body, context, options);
                String descriptor = generateMethodDescriptor(function, returnTypeInfo, options);
                CodeBuilder code = generateMethodCode(cp, body, returnTypeInfo, context, options);

                int accessFlags = 0x0001; // ACC_PUBLIC
                if (method.isStatic()) {
                    accessFlags |= 0x0008; // ACC_STATIC
                }
                // Check if method has varargs (RestPat in last parameter)
                if (!function.getParams().isEmpty()) {
                    Swc4jAstParam lastParam = function.getParams().get(function.getParams().size() - 1);
                    if (lastParam.getPat() instanceof com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat) {
                        accessFlags |= 0x0080; // ACC_VARARGS
                    }
                }

                int maxStack = Math.max(returnTypeInfo.maxStack(), returnTypeInfo.type().getMinStack());
                // Increase max stack to handle complex expressions like array literals with boxing
                maxStack = Math.max(maxStack, 10);
                int maxLocals = context.getLocalVariableTable().getMaxLocals();

                // Add debug information if enabled
                if (options.debug()) {
                    List<ClassWriter.LineNumberEntry> lineNumbers = code.getLineNumbers();
                    List<ClassWriter.LocalVariableEntry> localVariableTable = new java.util.ArrayList<>();

                    // Build LocalVariableTable from compilation context
                    int codeLength = code.getCurrentOffset();
                    for (LocalVariable var : context.getLocalVariableTable().getAllVariables()) {
                        localVariableTable.add(new ClassWriter.LocalVariableEntry(
                                0, // startPc - variable scope starts at method beginning
                                codeLength, // length - variable scope covers entire method
                                var.name(),
                                var.type(),
                                var.index()
                        ));
                    }

                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals,
                            lineNumbers, localVariableTable);
                } else {
                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals);
                }
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException("Failed to generate method: " + methodName, e);
            }
        }
    }

    public static CodeBuilder generateMethodCode(
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt body,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            // Add line number information if debug is enabled
            if (options.debug() && stmt.getSpan() != null) {
                code.setLineNumber(stmt.getSpan().getLine());
            }

            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                generateVarDecl(code, cp, varDecl, context, options);
            } else if (stmt instanceof Swc4jAstExprStmt exprStmt) {
                // Expression statement - evaluate the expression and discard result if any
                ISwc4jAstExpr expr = exprStmt.getExpr();
                generateExpr(code, cp, expr, null, context, options);

                // Assignment expressions leave values on the stack that need to be popped
                // Call expressions handle their own return values (already popped if needed)
                if (expr instanceof Swc4jAstAssignExpr) {
                    // Assignment expressions leave the assigned value on the stack
                    String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
                    if (exprType != null && !("V".equals(exprType))) {
                        // Expression leaves a value, pop it
                        // Use pop2 for wide types (double, long)
                        if ("D".equals(exprType) || "J".equals(exprType)) {
                            code.pop2();
                        } else {
                            code.pop();
                        }
                    }
                }
                // Note: CallExpr already pops its return value in generateCallExpr
            } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                if (returnStmt.getArg().isPresent()) {
                    generateExpr(code, cp, returnStmt.getArg().get(), returnTypeInfo, context, options);
                }

                // Generate appropriate return instruction
                switch (returnTypeInfo.type()) {
                    case VOID -> code.returnVoid();
                    case INT, SHORT, CHAR, BOOLEAN, BYTE -> code.ireturn();
                    case LONG -> code.lreturn();
                    case FLOAT -> code.freturn();
                    case DOUBLE -> code.dreturn();
                    case STRING, OBJECT -> code.areturn();
                }
            }
        }

        return code;
    }

    public static String generateMethodDescriptor(Swc4jAstFunction function, ReturnTypeInfo returnTypeInfo, ByteCodeCompilerOptions options) {
        // Build parameter descriptors
        StringBuilder paramDescriptors = new StringBuilder();
        for (Swc4jAstParam param : function.getParams()) {
            String paramType = TypeResolver.extractParameterType(param.getPat(), options);
            paramDescriptors.append(paramType);
        }

        String returnDescriptor = switch (returnTypeInfo.type()) {
            case VOID -> "V";
            case INT -> "I";
            case BOOLEAN -> "Z";
            case BYTE -> "B";
            case CHAR -> "C";
            case SHORT -> "S";
            case LONG -> "J";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
            case OBJECT -> returnTypeInfo.descriptor() != null ? returnTypeInfo.descriptor() : "Ljava/lang/Object;";
        };
        return "(" + paramDescriptors + ")" + returnDescriptor;
    }

    public static void generateStringConcat(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            String leftType,
            String rightType,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Use StringBuilder for string concatenation
        // new StringBuilder
        int stringBuilderClass = cp.addClass("java/lang/StringBuilder");
        int stringBuilderInit = cp.addMethodRef("java/lang/StringBuilder", "<init>", "()V");
        int appendString = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int appendInt = cp.addMethodRef("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
        int appendChar = cp.addMethodRef("java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        int toString = cp.addMethodRef("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

        code.newInstance(stringBuilderClass)
                .dup()
                .invokespecial(stringBuilderInit);

        // Flatten the operands - if left is also a string concatenation, collect all operands
        java.util.List<ISwc4jAstExpr> operands = new java.util.ArrayList<>();
        java.util.List<String> operandTypes = new java.util.ArrayList<>();

        // Collect operands from left side
        collectStringConcatOperands(left, operands, operandTypes, context, options);

        // Add right operand
        operands.add(right);
        operandTypes.add(rightType);

        // Append all operands
        for (int i = 0; i < operands.size(); i++) {
            generateExpr(code, cp, operands.get(i), null, context, options);
            appendOperandToStringBuilder(code, cp, operandTypes.get(i), appendString, appendInt, appendChar);
        }

        // Call toString()
        code.invokevirtual(toString);
    }

    public static void generateUnaryExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUnaryExpr unaryExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        Swc4jAstUnaryOp op = unaryExpr.getOp();

        if (op == Swc4jAstUnaryOp.Delete) {
            // Handle delete operator (e.g., delete arr[1])
            ISwc4jAstExpr arg = unaryExpr.getArg();
            if (arg instanceof Swc4jAstMemberExpr memberExpr) {
                String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

                if (objType != null && objType.startsWith("[")) {
                    // Java array - delete not supported
                    throw new Swc4jByteCodeCompilerException("Delete operator not supported on Java arrays - arrays have fixed size");
                } else if ("Ljava/util/ArrayList;".equals(objType)) {
                    if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                        // delete arr[index] -> arr.remove(index)
                        generateExpr(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                        generateExpr(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]

                        // Call ArrayList.remove(int)
                        int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                        code.invokevirtual(removeMethod); // Stack: [removedObject]
                        // Delete expression returns true in JavaScript, but we'll just leave the removed object
                        // Actually, delete should return boolean true
                        code.pop(); // Pop the removed object
                        code.iconst(1); // Push true (1)
                        return;
                    }
                }
            }
            throw new Swc4jByteCodeCompilerException("Delete operator not yet supported for: " + arg);
        } else if (op == Swc4jAstUnaryOp.Minus) {
            // Handle numeric negation
            ISwc4jAstExpr arg = unaryExpr.getArg();

            if (arg instanceof Swc4jAstNumber number) {
                // Directly generate the negated value
                double value = number.getValue();

                // Check if we're dealing with a long type
                if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
                    long longValue = -(long) value;
                    if (longValue == 0L || longValue == 1L) {
                        code.lconst(longValue);
                    } else {
                        int longIndex = cp.addLong(longValue);
                        code.ldc2_w(longIndex);
                    }
                } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                        && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                    // Check if we're dealing with a Long wrapper
                    long longValue = -(long) value;
                    if (longValue == 0L || longValue == 1L) {
                        code.lconst(longValue);
                    } else {
                        int longIndex = cp.addLong(longValue);
                        code.ldc2_w(longIndex);
                    }
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
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
                    // Integer value - use iconst for negated int
                    code.iconst(-(int) value);
                } else {
                    // Floating point value - use dconst
                    code.dconst(-value);
                }
            } else {
                // For complex expressions, generate the expression first then negate
                generateExpr(code, cp, arg, null, context, options);

                String argType = TypeResolver.inferTypeFromExpr(arg, context, options);
                // Handle null type - should not happen for negation, default to int
                if (argType == null) argType = "I";
                switch (argType) {
                    case "D" -> code.dneg();
                    case "F" -> code.fneg();
                    case "J" -> code.lneg();
                    default -> code.ineg();
                }
            }
        }
    }

    public static void generateVarDecl(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstVarDecl varDecl,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                if (declarator.getInit().isPresent()) {
                    var init = declarator.getInit().get();
                    ReturnTypeInfo varTypeInfo = ReturnTypeInfo.of(localVar.type());

                    generateExpr(code, cp, init, varTypeInfo, context, options);

                    // Store the value in the local variable
                    switch (localVar.type()) {
                        case "I", "S", "C", "Z", "B" -> code.istore(localVar.index());
                        case "J" -> code.lstore(localVar.index());
                        case "F" -> code.fstore(localVar.index());
                        case "D" -> code.dstore(localVar.index());
                        default -> code.astore(localVar.index());
                    }
                }
            }
        }
    }

    private static String getMethodName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
    }

    private static String getPrimitiveType(String type) {
        return switch (type) {
            case "Ljava/lang/Byte;" -> "B";
            case "Ljava/lang/Short;" -> "S";
            case "Ljava/lang/Integer;" -> "I";
            case "Ljava/lang/Long;" -> "J";
            case "Ljava/lang/Float;" -> "F";
            case "Ljava/lang/Double;" -> "D";
            case "Ljava/lang/Character;" -> "C";
            default -> type;
        };
    }

    private static String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "B" -> "Ljava/lang/Byte;";
            case "S" -> "Ljava/lang/Short;";
            case "I" -> "Ljava/lang/Integer;";
            case "J" -> "Ljava/lang/Long;";
            case "F" -> "Ljava/lang/Float;";
            case "D" -> "Ljava/lang/Double;";
            case "C" -> "Ljava/lang/Character;";
            case "Z" -> "Ljava/lang/Boolean;";
            default -> primitiveType; // Already a wrapper or reference type
        };
    }

    private static boolean isPrimitiveType(String type) {
        return "I".equals(type) || "Z".equals(type) || "B".equals(type) ||
                "C".equals(type) || "S".equals(type) || "J".equals(type) ||
                "F".equals(type) || "D".equals(type);
    }

    private static void unboxWrapperType(CodeBuilder code, ClassWriter.ConstantPool cp, String type) {
        switch (type) {
            case "Ljava/lang/Integer;" -> {
                int intValueRef = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueRef);
            }
            case "Ljava/lang/Character;" -> {
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                code.invokevirtual(charValueRef);
            }
            case "Ljava/lang/Byte;" -> {
                int byteValueRef = cp.addMethodRef("java/lang/Byte", "byteValue", "()B");
                code.invokevirtual(byteValueRef);
            }
            case "Ljava/lang/Long;" -> {
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
            }
            case "Ljava/lang/Short;" -> {
                int shortValueRef = cp.addMethodRef("java/lang/Short", "shortValue", "()S");
                code.invokevirtual(shortValueRef);
            }
            case "Ljava/lang/Float;" -> {
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
            }
            case "Ljava/lang/Double;" -> {
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
