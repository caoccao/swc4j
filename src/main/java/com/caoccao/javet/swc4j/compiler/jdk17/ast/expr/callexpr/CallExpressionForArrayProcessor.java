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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for call expressions on Java arrays.
 * <p>
 * Supported operations on Java arrays:
 * - .length property access (handled by MemberExpressionProcessor)
 * - Index access arr[i] (handled by MemberExpressionProcessor)
 * - Index assignment arr[i] = value (handled by AssignExpressionProcessor)
 * - indexOf, lastIndexOf, includes, reverse, sort, toReversed, toSorted, join, fill, toString (handled here)
 * <p>
 * Note: Java arrays have fixed size and do not support dynamic methods like push(), pop(), splice(), etc.
 * For dynamic arrays with these methods, use ArrayList instead.
 */
public final class CallExpressionForArrayProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForArrayProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Get the method name
            String methodName = null;
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                methodName = propIdent.getSym();
            }

            // Get array type
            String arrayType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
            String elementType = arrayType.substring(1); // Remove leading "["
            String arrayTypeName = getArrayTypeName(arrayType);

            // Generate code for the array object
            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);

            switch (methodName) {
                case "fill" -> generateFill(code, classWriter, callExpr, elementType);
                case "includes" -> generateIncludes(code, classWriter, callExpr, elementType);
                case "indexOf" -> generateIndexOf(code, classWriter, callExpr, elementType);
                case "join" -> generateJoin(code, classWriter, callExpr, elementType);
                case "lastIndexOf" -> generateLastIndexOf(code, classWriter, callExpr, elementType);
                case "reverse" -> generateReverse(code, classWriter, elementType);
                case "sort" -> generateSort(code, classWriter, elementType);
                case "toReversed" -> generateToReversed(code, classWriter, elementType);
                case "toSorted" -> generateToSorted(code, classWriter, elementType);
                case "toString" -> generateToString(code, classWriter, elementType);
                default -> throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                        "Method '" + methodName + "()' is not supported on Java arrays (" + arrayTypeName + "). " +
                                "Java arrays only support: .length property and index access arr[i].");
            }
            return;
        }

        throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Invalid call expression on Java array");
    }

    private void generateFill(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr, String elementType) throws Swc4jByteCodeCompilerException {
        // arr.fill(value) -> ArrayApiUtils.fill(arr, value)
        if (callExpr.getArgs().isEmpty()) {
            code.pop(); // Pop array reference
            return;
        }

        var cp = classWriter.getConstantPool();
        // Cast reference type arrays to Object[] for the method call
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        var arg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

        // For reference types, cast to Object; for primitives, unbox and convert
        if (elementType.startsWith("L")) {
            // Reference type - already an Object, no conversion needed
        } else {
            // Primitive type - unbox if needed and convert to element type
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            TypeConversionUtils.unboxWrapperType(code, classWriter, argType);
            String argPrimitive = TypeConversionUtils.getPrimitiveType(argType);
            TypeConversionUtils.convertPrimitiveType(code, argPrimitive, elementType);
        }

        // Call ArrayApiUtils.fill
        String methodSignature = getArrayApiUtilsFillSignature(elementType);
        int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "fill", methodSignature);
        code.invokestatic(fillMethod);

        // Cast back to original type if reference type
        if (elementType.startsWith("L")) {
            String originalArrayDescriptor = "[" + elementType;
            int originalArrayClass = cp.addClass(originalArrayDescriptor);
            code.checkcast(originalArrayClass);
        }
    }

    private void generateIncludes(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr, String elementType) throws Swc4jByteCodeCompilerException {
        // arr.includes(value) -> ArrayApiUtils.includes(arr, value)
        if (callExpr.getArgs().isEmpty()) {
            code.pop(); // Pop array reference
            code.iconst(0); // Return false
            return;
        }

        var cp = classWriter.getConstantPool();
        // Cast reference type arrays to Object[] for the method call
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        var arg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

        // For reference types, cast to Object; for primitives, unbox and convert
        if (elementType.startsWith("L")) {
            // Reference type - already an Object, no conversion needed
        } else {
            // Primitive type - unbox if needed and convert to element type
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            TypeConversionUtils.unboxWrapperType(code, classWriter, argType);
            String argPrimitive = TypeConversionUtils.getPrimitiveType(argType);
            TypeConversionUtils.convertPrimitiveType(code, argPrimitive, elementType);
        }

        // Call ArrayApiUtils.includes
        String methodSignature = getArrayApiUtilsSearchSignature("includes", elementType, "Z");
        int includesMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "includes", methodSignature);
        code.invokestatic(includesMethod);
    }

    private void generateIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr, String elementType) throws Swc4jByteCodeCompilerException {
        // arr.indexOf(value) -> ArrayApiUtils.indexOf(arr, value)
        if (callExpr.getArgs().isEmpty()) {
            code.pop(); // Pop array reference
            code.iconst(-1); // Return -1
            return;
        }

        var cp = classWriter.getConstantPool();
        // Cast reference type arrays to Object[] for the method call
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        var arg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

        // For reference types, cast to Object; for primitives, unbox and convert
        if (elementType.startsWith("L")) {
            // Reference type - already an Object, no conversion needed
        } else {
            // Primitive type - unbox if needed and convert to element type
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            TypeConversionUtils.unboxWrapperType(code, classWriter, argType);
            String argPrimitive = TypeConversionUtils.getPrimitiveType(argType);
            TypeConversionUtils.convertPrimitiveType(code, argPrimitive, elementType);
        }

        // Call ArrayApiUtils.indexOf
        String methodSignature = getArrayApiUtilsSearchSignature("indexOf", elementType, "I");
        int indexOfMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "indexOf", methodSignature);
        code.invokestatic(indexOfMethod);
    }

    private void generateJoin(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr, String elementType) throws Swc4jByteCodeCompilerException {
        // arr.join(separator) -> ArrayApiUtils.join(arr, separator)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        if (callExpr.getArgs().isEmpty()) {
            // Default separator is ","
            int commaIndex = cp.addString(",");
            code.ldc(commaIndex);
        } else {
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
        }

        // Call ArrayApiUtils.join
        String methodSignature = getArrayApiUtilsJoinSignature(elementType);
        int joinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "join", methodSignature);
        code.invokestatic(joinMethod);
    }

    private void generateLastIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr, String elementType) throws Swc4jByteCodeCompilerException {
        // arr.lastIndexOf(value) -> ArrayApiUtils.lastIndexOf(arr, value)
        if (callExpr.getArgs().isEmpty()) {
            code.pop(); // Pop array reference
            code.iconst(-1); // Return -1
            return;
        }

        var cp = classWriter.getConstantPool();
        // Cast reference type arrays to Object[] for the method call
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        var arg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

        // For reference types, cast to Object; for primitives, unbox and convert
        if (elementType.startsWith("L")) {
            // Reference type - already an Object, no conversion needed
        } else {
            // Primitive type - unbox if needed and convert to element type
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            TypeConversionUtils.unboxWrapperType(code, classWriter, argType);
            String argPrimitive = TypeConversionUtils.getPrimitiveType(argType);
            TypeConversionUtils.convertPrimitiveType(code, argPrimitive, elementType);
        }

        // Call ArrayApiUtils.lastIndexOf
        String methodSignature = getArrayApiUtilsSearchSignature("lastIndexOf", elementType, "I");
        int lastIndexOfMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "lastIndexOf", methodSignature);
        code.invokestatic(lastIndexOfMethod);
    }

    private void generateReverse(CodeBuilder code, ClassWriter classWriter, String elementType) {
        // arr.reverse() -> ArrayApiUtils.reverse(arr)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        String methodSignature = getArrayApiUtilsSignature("reverse", elementType);
        int reverseMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "reverse", methodSignature);
        code.invokestatic(reverseMethod);

        // Cast back to original type if reference type
        if (elementType.startsWith("L")) {
            String originalArrayDescriptor = "[" + elementType;
            int originalArrayClass = cp.addClass(originalArrayDescriptor);
            code.checkcast(originalArrayClass);
        }
    }

    private void generateSort(CodeBuilder code, ClassWriter classWriter, String elementType) {
        // arr.sort() -> ArrayApiUtils.sort(arr)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        String methodSignature = getArrayApiUtilsSignature("sort", elementType);
        int sortMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "sort", methodSignature);
        code.invokestatic(sortMethod);

        // Cast back to original type if reference type
        if (elementType.startsWith("L")) {
            String originalArrayDescriptor = "[" + elementType;
            int originalArrayClass = cp.addClass(originalArrayDescriptor);
            code.checkcast(originalArrayClass);
        }
    }

    private void generateToReversed(CodeBuilder code, ClassWriter classWriter, String elementType) {
        // arr.toReversed() -> ArrayApiUtils.toReversed(arr)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        String methodSignature = getArrayApiUtilsSignature("toReversed", elementType);
        int toReversedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toReversed", methodSignature);
        code.invokestatic(toReversedMethod);

        // Cast back to original type if reference type
        if (elementType.startsWith("L")) {
            String originalArrayDescriptor = "[" + elementType;
            int originalArrayClass = cp.addClass(originalArrayDescriptor);
            code.checkcast(originalArrayClass);
        }
    }

    private void generateToSorted(CodeBuilder code, ClassWriter classWriter, String elementType) {
        // arr.toSorted() -> ArrayApiUtils.toSorted(arr)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        String methodSignature = getArrayApiUtilsSignature("toSorted", elementType);
        int toSortedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toSorted", methodSignature);
        code.invokestatic(toSortedMethod);

        // Cast back to original type if reference type
        if (elementType.startsWith("L")) {
            String originalArrayDescriptor = "[" + elementType;
            int originalArrayClass = cp.addClass(originalArrayDescriptor);
            code.checkcast(originalArrayClass);
        }
    }

    private void generateToString(CodeBuilder code, ClassWriter classWriter, String elementType) {
        // arr.toString() -> ArrayApiUtils.toString(arr)
        // Cast reference type arrays to Object[] for the method call
        var cp = classWriter.getConstantPool();
        if (elementType.startsWith("L")) {
            int objectArrayClass = cp.addClass("[Ljava/lang/Object;");
            code.checkcast(objectArrayClass);
        }

        String methodSignature = getArrayApiUtilsToStringSignature(elementType);
        int toStringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toString", methodSignature);
        code.invokestatic(toStringMethod);
    }

    /**
     * Get method signature for ArrayApiUtils fill methods.
     * For example: fill(int[], int) returns int[], fill(Object[], Object) returns Object[]
     */
    private String getArrayApiUtilsFillSignature(String elementType) {
        String arrayDescriptor;
        String valueType;
        if (elementType.startsWith("L")) {
            // Reference type - use Object[] signature
            arrayDescriptor = "[Ljava/lang/Object;";
            valueType = "Ljava/lang/Object;";
        } else {
            // Primitive type
            arrayDescriptor = "[" + elementType;
            valueType = elementType;
        }
        return "(" + arrayDescriptor + valueType + ")" + arrayDescriptor;
    }

    /**
     * Get method signature for ArrayApiUtils join/toString methods.
     * For example: join(int[], String) returns String, join(Object[], String) returns String
     */
    private String getArrayApiUtilsJoinSignature(String elementType) {
        String arrayDescriptor;
        if (elementType.startsWith("L")) {
            // Reference type - use Object[] signature
            arrayDescriptor = "[Ljava/lang/Object;";
        } else {
            // Primitive type
            arrayDescriptor = "[" + elementType;
        }
        return "(" + arrayDescriptor + "Ljava/lang/String;)Ljava/lang/String;";
    }

    /**
     * Get method signature for ArrayApiUtils search methods (indexOf, lastIndexOf, includes).
     * For example: indexOf(int[], int) returns int, includes(int[], int) returns boolean
     * For Object arrays: indexOf(Object[], Object) returns int
     */
    private String getArrayApiUtilsSearchSignature(String methodName, String elementType, String returnType) {
        // For reference types, convert to Object[] signature
        String arrayDescriptor;
        String paramType;

        if (elementType.startsWith("L")) {
            // Reference type - use Object[] signature
            arrayDescriptor = "[Ljava/lang/Object;";
            paramType = "Ljava/lang/Object;";
        } else {
            // Primitive type - use the primitive type
            arrayDescriptor = "[" + elementType;
            paramType = elementType;
        }
        return "(" + arrayDescriptor + paramType + ")" + returnType;
    }

    /**
     * Get method signature for ArrayApiUtils methods that take and return the same array type.
     * For example: reverse(int[]) returns int[], reverse(Object[]) returns Object[]
     */
    private String getArrayApiUtilsSignature(String methodName, String elementType) {
        String arrayDescriptor;
        if (elementType.startsWith("L")) {
            // Reference type - use Object[] signature
            arrayDescriptor = "[Ljava/lang/Object;";
        } else {
            // Primitive type
            arrayDescriptor = "[" + elementType;
        }
        return "(" + arrayDescriptor + ")" + arrayDescriptor;
    }

    /**
     * Get method signature for ArrayApiUtils toString methods.
     * For example: toString(int[]) returns String, toString(Object[]) returns String
     */
    private String getArrayApiUtilsToStringSignature(String elementType) {
        String arrayDescriptor;
        if (elementType.startsWith("L")) {
            // Reference type - use Object[] signature
            arrayDescriptor = "[Ljava/lang/Object;";
        } else {
            // Primitive type
            arrayDescriptor = "[" + elementType;
        }
        return "(" + arrayDescriptor + ")Ljava/lang/String;";
    }

    /**
     * Gets a human-readable array type name for error messages.
     *
     * @param arrayDescriptor the array type descriptor (e.g., "[I", "[[Ljava/lang/String;")
     * @return human-readable type name (e.g., "int[]", "String[][]")
     */
    private String getArrayTypeName(String arrayDescriptor) {
        if (arrayDescriptor == null || !arrayDescriptor.startsWith("[")) {
            return "unknown array type";
        }

        int dimensions = 0;
        int index = 0;
        while (index < arrayDescriptor.length() && arrayDescriptor.charAt(index) == '[') {
            dimensions++;
            index++;
        }

        String brackets = "[]".repeat(dimensions);

        if (index >= arrayDescriptor.length()) {
            return "array" + brackets;
        }

        char typeChar = arrayDescriptor.charAt(index);
        String elementType = switch (typeChar) {
            case 'Z' -> "boolean";
            case 'B' -> "byte";
            case 'C' -> "char";
            case 'S' -> "short";
            case 'I' -> "int";
            case 'J' -> "long";
            case 'F' -> "float";
            case 'D' -> "double";
            case 'L' -> {
                // Reference type - extract class name
                int semicolonIndex = arrayDescriptor.indexOf(';', index);
                if (semicolonIndex > index) {
                    String className = arrayDescriptor.substring(index + 1, semicolonIndex);
                    // Get simple class name (after last /)
                    int lastSlash = className.lastIndexOf('/');
                    yield lastSlash >= 0 ? className.substring(lastSlash + 1) : className;
                }
                yield "Object";
            }
            default -> "unknown";
        };

        return elementType + brackets;
    }

    /**
     * Checks if this generator supports the given type.
     *
     * @param type the type descriptor
     * @return true if the type is a Java array (starts with '[')
     */
    public boolean isTypeSupported(String type) {
        return type != null && type.startsWith("[");
    }
}
