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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;
import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates bytecode for class method calls:
 * - Java class static method calls (e.g., Math.floor())
 * - TypeScript/JavaScript class instance method calls
 */
public final class CallExpressionForClassProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForClassProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts between types when needed.
     */
    private void convertType(CodeBuilder code, ClassWriter classWriter, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return;
        }

        var cp = classWriter.getConstantPool();
        // Handle primitive conversions
        if (TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.convertPrimitiveType(code, fromType, toType);
        }
        // Handle boxing/unboxing
        else if (TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(fromType);
            TypeConversionUtils.boxPrimitiveType(code, classWriter, fromType, wrapperType);
        } else if (!TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.unboxWrapperType(code, classWriter, fromType);
        }
        // Handle reference type casting (e.g., AbstractStringBuilder -> StringBuilder)
        else if (fromType.startsWith("L") && toType.startsWith("L") && !fromType.equals(toType)) {
            // Extract internal class names
            String toInternalName = toType.substring(1, toType.length() - 1);
            // Add checkcast instruction to downcast to the target type
            int classIndex = cp.addClass(toInternalName);
            code.checkcast(classIndex);
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Class method call must be a member expression");
        }

        // Get method name
        String methodName = getMethodName(memberExpr.getProp());
        if (methodName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "Could not determine method name");
        }

        // Determine if it's a Java class static method call or TS class instance method call
        JavaTypeInfo javaTypeInfo = null;
        boolean isPrivateMethodCall = isPrivateMethod(memberExpr.getProp());

        if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent) {
            String className = objIdent.getSym();
            javaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
        }

        // Private method calls on TypeScript classes should be handled as TS class calls, not Java static calls
        // Java classes don't have #method syntax, so if the prop is PrivateName, it must be a TS class
        boolean isJavaStaticCall = javaTypeInfo != null && !isPrivateMethodCall;

        // Infer argument types
        var args = callExpr.getArgs();
        List<String> argTypes = new ArrayList<>();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not yet supported in class method calls");
            }
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = "Ljava/lang/Object;";
            }
            argTypes.add(argType);
        }

        String internalClassName;
        String methodDescriptor;
        String returnType;

        if (isJavaStaticCall) {
            // Java class static method call
            MethodInfo methodInfo = javaTypeInfo.getMethod(methodName, argTypes);
            if (methodInfo == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                        "Method not found: " + javaTypeInfo.getAlias() + "." + methodName +
                                " with argument types " + argTypes);
            }

            if (!methodInfo.isStatic()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                        "Method is not static: " + javaTypeInfo.getAlias() + "." + methodName);
            }

            generateArgumentsWithVarargs(code, classWriter, args, argTypes, methodInfo);

            internalClassName = javaTypeInfo.getInternalName();
            methodDescriptor = methodInfo.descriptor();
            returnType = methodInfo.returnType();

            // Generate invokestatic
            int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
            code.invokestatic(methodRef);
        } else {
            // TypeScript class instance/static method call
            String objType = null;
            boolean isStaticCall = false;
            JavaTypeInfo instanceJavaTypeInfo = null;

            // Handle static private method calls like A.#helper()
            // Check if obj is a class identifier (before inferring type which returns Object)
            if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent && isPrivateMethodCall) {
                String className = objIdent.getSym();
                JavaTypeInfo tsTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
                if (tsTypeInfo != null) {
                    // This is a static private method call on a TS class
                    objType = "L" + tsTypeInfo.getInternalName() + ";";
                    isStaticCall = true;
                }
            }

            // For non-static calls, infer the object type
            if (objType == null) {
                objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
            }

            if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                String internalName = objType.substring(1, objType.length() - 1);
                instanceJavaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolveByInternalName(internalName);
            }

            // Only handle as Java class if it has methods (populated via reflection for imported Java classes)
            // TypeScript classes are registered but have empty methods map, so they fall through to TS handling below
            if (instanceJavaTypeInfo != null && !instanceJavaTypeInfo.getMethods().isEmpty()) {
                MethodInfo methodInfo = instanceJavaTypeInfo.getMethod(methodName, argTypes);
                if (methodInfo == null) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                            "Method not found: " + instanceJavaTypeInfo.getAlias() + "." + methodName +
                                    " with argument types " + argTypes);
                }
                if (methodInfo.isStatic()) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                            "Method is static: " + instanceJavaTypeInfo.getAlias() + "." + methodName);
                }

                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
                generateArgumentsWithVarargs(code, classWriter, args, argTypes, methodInfo);

                internalClassName = instanceJavaTypeInfo.getInternalName();
                methodDescriptor = methodInfo.descriptor();
                returnType = methodInfo.returnType();

                String qualifiedClassName = internalClassName.replace('/', '.');
                boolean isInterface = isInterface(qualifiedClassName);
                if (isInterface) {
                    int methodRef = cp.addInterfaceMethodRef(internalClassName, methodName, methodDescriptor);
                    int argCount = args.size() + 1;
                    code.invokeinterface(methodRef, argCount);
                } else {
                    int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
                    code.invokevirtual(methodRef);
                }

                // For method chaining: if the return type is a parent class of the imported class,
                // cast it to the imported class type to ensure subsequent chained calls work correctly
                String expectedType = "L" + internalClassName + ";";
                if (!returnType.equals(expectedType) && returnType.startsWith("L") && !returnType.equals("V")) {
                    // Check if the return type is assignable to the class type
                    // If so, cast to the class type for proper method chaining
                    try {
                        Class<?> returnClass = Class.forName(returnType.substring(1, returnType.length() - 1).replace('/', '.'));
                        Class<?> expectedClass = Class.forName(qualifiedClassName);
                        if (returnClass.isAssignableFrom(expectedClass)) {
                            // Return type is a parent of the expected type, cast down
                            int classIndex = cp.addClass(internalClassName);
                            code.checkcast(classIndex);
                            returnType = expectedType; // Update returnType for subsequent conversions
                        }
                    } catch (ClassNotFoundException e) {
                        // If class not found, don't add cast
                    }
                }

                if (returnTypeInfo != null && returnTypeInfo.descriptor() != null) {
                    convertType(code, classWriter, returnType, returnTypeInfo.descriptor());
                }
                return;
            }

            if (objType == null || !objType.startsWith("L") || !objType.endsWith(";")) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "Invalid object type for TS class method call: " + objType);
            }

            // Generate the object reference for instance calls
            if (!isStaticCall) {
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
            }

            // Generate arguments
            StringBuilder paramDescriptors = new StringBuilder();
            for (int i = 0; i < args.size(); i++) {
                var arg = args.get(i);
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                paramDescriptors.append(argTypes.get(i));
            }

            // Get internal and qualified class names
            internalClassName = objType.substring(1, objType.length() - 1);
            String qualifiedClassName = internalClassName.replace('/', '.');

            // Look up method return type
            String paramDescriptor = "(" + paramDescriptors + ")";
            returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                    .resolveClassMethodReturnType(qualifiedClassName, methodName, paramDescriptor);
            if (returnType == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                        "Cannot infer return type for method call " + qualifiedClassName + "." + methodName +
                                ". Please add explicit return type annotation to the method.");
            }

            methodDescriptor = paramDescriptor + returnType;

            // Check if this is a private method call (ES2022 #method)
            boolean isPrivate = isPrivateMethod(memberExpr.getProp());

            // Determine if the target is an interface (use invokeinterface) or class (use invokevirtual/invokespecial)
            boolean isInterface = isInterface(qualifiedClassName);

            if (isPrivate && isStaticCall) {
                // Static private method call - use invokestatic
                int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
                code.invokestatic(methodRef);
            } else if (isPrivate) {
                // Instance private method call - use invokespecial (like constructor calls)
                int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
                code.invokespecial(methodRef);
            } else if (isInterface) {
                // Interface method call - use invokeinterface
                int methodRef = cp.addInterfaceMethodRef(internalClassName, methodName, methodDescriptor);
                // invokeinterface needs the argument count (including 'this')
                int argCount = args.size() + 1;
                code.invokeinterface(methodRef, argCount);
            } else {
                // Class method call - use invokevirtual
                int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
                code.invokevirtual(methodRef);
            }
        }

        // Type conversion for return value (Java static calls only)
        if (isJavaStaticCall && returnTypeInfo != null && returnTypeInfo.descriptor() != null) {
            convertType(code, classWriter, returnType, returnTypeInfo.descriptor());
        }
    }

    private void generateArgumentsWithVarargs(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstExprOrSpread> args,
            List<String> argTypes,
            MethodInfo methodInfo) throws Swc4jByteCodeCompilerException {
        List<String> expectedTypes = ScoreUtils.parseParameterDescriptors(methodInfo.descriptor());
        if (!methodInfo.isVarArgs() || expectedTypes.isEmpty()) {
            for (int i = 0; i < args.size(); i++) {
                Swc4jAstExprOrSpread arg = args.get(i);
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                if (i < expectedTypes.size()) {
                    convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                }
            }
            return;
        }

        int fixedCount = expectedTypes.size() - 1;
        String varargArrayType = expectedTypes.get(expectedTypes.size() - 1);
        String componentType = varargArrayType.startsWith("[") ? varargArrayType.substring(1) : varargArrayType;

        boolean directArrayPass = args.size() == expectedTypes.size()
                && argTypes.get(argTypes.size() - 1).equals(varargArrayType);

        if (directArrayPass) {
            for (int i = 0; i < args.size(); i++) {
                Swc4jAstExprOrSpread arg = args.get(i);
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
            }
            return;
        }

        for (int i = 0; i < fixedCount; i++) {
            Swc4jAstExprOrSpread arg = args.get(i);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
        }

        generateVarargsArray(code, classWriter, args, argTypes, fixedCount, varargArrayType, componentType);
    }

    private void generateVarargsArray(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstExprOrSpread> args,
            List<String> argTypes,
            int startIndex,
            String arrayType,
            String componentType) throws Swc4jByteCodeCompilerException {
        int varargCount = args.size() - startIndex;
        code.iconst(varargCount);

        var cp = classWriter.getConstantPool();
        if (TypeConversionUtils.isPrimitiveType(componentType)) {
            int typeCode = switch (componentType) {
                case "Z" -> 4;
                case "C" -> 5;
                case "F" -> 6;
                case "D" -> 7;
                case "B" -> 8;
                case "S" -> 9;
                case "I" -> 10;
                case "J" -> 11;
                default ->
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), null, "Unsupported vararg primitive type: " + componentType);
            };
            code.newarray(typeCode);
        } else {
            String internalName = componentType.substring(1, componentType.length() - 1);
            int classIndex = cp.addClass(internalName);
            code.anewarray(classIndex);
        }

        for (int i = 0; i < varargCount; i++) {
            code.dup();
            code.iconst(i);
            Swc4jAstExprOrSpread arg = args.get(startIndex + i);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported in varargs calls");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = argTypes.get(startIndex + i);
            convertType(code, classWriter, argType, componentType);

            switch (componentType) {
                case "Z", "B" -> code.bastore();
                case "C" -> code.castore();
                case "S" -> code.sastore();
                case "I" -> code.iastore();
                case "J" -> code.lastore();
                case "F" -> code.fastore();
                case "D" -> code.dastore();
                default -> code.aastore();
            }
        }
    }

    private String getMethodName(ISwc4jAstMemberProp prop) {
        if (prop instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (prop instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (prop instanceof Swc4jAstPrivateName privateName) {
            // ES2022 private method - return name without # prefix
            return privateName.getName();
        }
        return null;
    }

    /**
     * Checks if a given class name is an interface.
     * Uses Java reflection to determine if the type is an interface.
     *
     * @param qualifiedClassName fully qualified class name (e.g., "java.util.function.IntUnaryOperator")
     * @return true if the class is an interface, false otherwise
     */
    private boolean isInterface(String qualifiedClassName) {
        try {
            Class<?> clazz = Class.forName(qualifiedClassName);
            return clazz.isInterface();
        } catch (ClassNotFoundException e) {
            // If class not found, assume it's not an interface (could be a TS class)
            return false;
        }
    }

    /**
     * Checks if the member property is a private name (ES2022 #method).
     */
    private boolean isPrivateMethod(ISwc4jAstMemberProp prop) {
        return prop instanceof Swc4jAstPrivateName;
    }

    /**
     * Checks if the given object type is a TypeScript/JavaScript class.
     *
     * @param objType the JVM type descriptor to check
     * @return true if the type is a TypeScript/JavaScript class
     */
    public boolean isTypeSupported(String objType) {
        // A TS class type is an object type (starts with L and ends with ;)
        // that is not a known Java built-in type
        return objType != null && objType.startsWith("L") && objType.endsWith(";");
    }

}
