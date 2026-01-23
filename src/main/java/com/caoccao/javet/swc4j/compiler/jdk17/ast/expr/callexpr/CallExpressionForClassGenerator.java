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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.JavaClassInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates bytecode for class method calls:
 * - Java class static method calls (e.g., Math.floor())
 * - TypeScript/JavaScript class instance method calls
 */
public final class CallExpressionForClassGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForClassGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts between types when needed.
     */
    private void convertType(CodeBuilder code, ClassWriter.ConstantPool cp, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return;
        }

        // Handle primitive conversions
        if (TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.convertPrimitiveType(code, fromType, toType);
        }
        // Handle boxing/unboxing
        else if (TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(fromType);
            TypeConversionUtils.boxPrimitiveType(code, cp, fromType, wrapperType);
        } else if (!TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.unboxWrapperType(code, cp, fromType);
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            throw new Swc4jByteCodeCompilerException("Class method call must be a member expression");
        }

        // Get method name
        String methodName = getMethodName(memberExpr.getProp());
        if (methodName == null) {
            throw new Swc4jByteCodeCompilerException("Could not determine method name");
        }

        // Determine if it's a Java class static method call or TS class instance method call
        JavaClassInfo javaClassInfo = null;
        if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent) {
            String className = objIdent.getSym();
            javaClassInfo = compiler.getMemory().getScopedJavaClassRegistry().resolve(className);
        }

        boolean isJavaStaticCall = javaClassInfo != null;

        // Infer argument types
        var args = callExpr.getArgs();
        List<String> argTypes = new ArrayList<>();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException("Spread arguments not yet supported in class method calls");
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
            MethodInfo methodInfo = javaClassInfo.getMethod(methodName, argTypes);
            if (methodInfo == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Method not found: " + javaClassInfo.getAlias() + "." + methodName +
                                " with argument types " + argTypes);
            }

            if (!methodInfo.isStatic()) {
                throw new Swc4jByteCodeCompilerException(
                        "Method is not static: " + javaClassInfo.getAlias() + "." + methodName);
            }

            // Parse expected parameter types for type conversion
            String descriptor = methodInfo.descriptor();
            String paramTypes = descriptor.substring(1, descriptor.indexOf(')'));
            var expectedTypes = parseParameterTypes(paramTypes);

            // Generate arguments with type conversion
            for (int i = 0; i < args.size(); i++) {
                var arg = args.get(i);
                compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                if (i < argTypes.size() && i < expectedTypes.size()) {
                    convertType(code, cp, argTypes.get(i), expectedTypes.get(i));
                }
            }

            internalClassName = javaClassInfo.getInternalName();
            methodDescriptor = methodInfo.descriptor();
            returnType = methodInfo.returnType();

            // Generate invokestatic
            int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
            code.invokestatic(methodRef);
        } else {
            // TypeScript class instance method call
            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
            if (objType == null || !objType.startsWith("L") || !objType.endsWith(";")) {
                throw new Swc4jByteCodeCompilerException("Invalid object type for TS class method call: " + objType);
            }

            // Generate the object reference first
            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

            // Generate arguments
            StringBuilder paramDescriptors = new StringBuilder();
            for (int i = 0; i < args.size(); i++) {
                var arg = args.get(i);
                compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                paramDescriptors.append(argTypes.get(i));
            }

            // Get internal and qualified class names
            internalClassName = objType.substring(1, objType.length() - 1);
            String qualifiedClassName = internalClassName.replace('/', '.');

            // Look up method return type
            String paramDescriptor = "(" + paramDescriptors + ")";
            returnType = compiler.getMemory().getScopedJavaClassRegistry()
                    .resolveTSClassMethodReturnType(qualifiedClassName, methodName, paramDescriptor);
            if (returnType == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Cannot infer return type for method call " + qualifiedClassName + "." + methodName +
                                ". Please add explicit return type annotation to the method.");
            }

            methodDescriptor = paramDescriptor + returnType;

            // Generate invokevirtual
            int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
            code.invokevirtual(methodRef);
        }

        // Type conversion for return value (Java static calls only)
        if (isJavaStaticCall && returnTypeInfo != null && returnTypeInfo.descriptor() != null) {
            convertType(code, cp, returnType, returnTypeInfo.descriptor());
        }
    }

    private String getMethodName(ISwc4jAstMemberProp prop) {
        if (prop instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (prop instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        return null;
    }

    /**
     * Checks if the given object type is a TypeScript/JavaScript class.
     */
    public boolean isTypeSupported(String objType) {
        // A TS class type is an object type (starts with L and ends with ;)
        // that is not a known Java built-in type
        return objType != null && objType.startsWith("L") && objType.endsWith(";");
    }

    /**
     * Parses all parameter types from a method descriptor parameter string.
     * Returns a list of type descriptors.
     */
    private List<String> parseParameterTypes(String paramTypes) {
        List<String> types = new ArrayList<>();
        int position = 0;

        while (position < paramTypes.length()) {
            char c = paramTypes.charAt(position);

            if (c == 'L') {
                // Object type - find the semicolon
                int semicolon = paramTypes.indexOf(';', position);
                types.add(paramTypes.substring(position, semicolon + 1));
                position = semicolon + 1;
            } else if (c == '[') {
                // Array type - consume array markers and the element type
                int start = position;
                while (position < paramTypes.length() && paramTypes.charAt(position) == '[') {
                    position++;
                }
                if (position < paramTypes.length()) {
                    if (paramTypes.charAt(position) == 'L') {
                        int semicolon = paramTypes.indexOf(';', position);
                        position = semicolon + 1;
                    } else {
                        position++;
                    }
                }
                types.add(paramTypes.substring(start, position));
            } else {
                // Primitive type (single character)
                types.add(String.valueOf(c));
                position++;
            }
        }

        return types;
    }
}
