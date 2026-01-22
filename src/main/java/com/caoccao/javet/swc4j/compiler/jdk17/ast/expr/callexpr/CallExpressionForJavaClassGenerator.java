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
 * Generates bytecode for Java class static method calls (e.g., Math.floor()).
 */
public final class CallExpressionForJavaClassGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForJavaClassGenerator(ByteCodeCompiler compiler) {
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
            throw new Swc4jByteCodeCompilerException("Java class method call must be a member expression");
        }

        if (!(memberExpr.getObj() instanceof Swc4jAstIdent objIdent)) {
            throw new Swc4jByteCodeCompilerException("Java class method call object must be an identifier");
        }

        if (!(memberExpr.getProp() instanceof Swc4jAstIdentName propIdent)) {
            throw new Swc4jByteCodeCompilerException("Java class method call property must be an identifier");
        }

        String className = objIdent.getSym();
        String methodName = propIdent.getSym();

        JavaClassInfo classInfo = compiler.getMemory().getScopedJavaClassRegistry().resolve(className);
        if (classInfo == null) {
            throw new Swc4jByteCodeCompilerException("Java class not found: " + className);
        }

        // Infer argument types
        var args = callExpr.getArgs();
        List<String> argTypes = new ArrayList<>();
        for (var arg : args) {
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = "Ljava/lang/Object;"; // Default to Object if type cannot be inferred
            }
            argTypes.add(argType);
        }

        // Get method with exact type matching or widening
        MethodInfo methodInfo = classInfo.getMethod(methodName, argTypes);
        if (methodInfo == null) {
            throw new Swc4jByteCodeCompilerException("Method not found: " + className + "." + methodName + " with argument types " + argTypes);
        }

        if (!methodInfo.isStatic()) {
            throw new Swc4jByteCodeCompilerException("Method is not static: " + className + "." + methodName);
        }

        // Parse expected parameter types from method descriptor
        String descriptor = methodInfo.descriptor();
        String paramTypes = descriptor.substring(1, descriptor.indexOf(')'));
        var expectedTypes = parseParameterTypes(paramTypes);

        // Generate arguments with type conversion
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);

            // Type conversion if needed (we already have argTypes from above)
            if (i < argTypes.size() && i < expectedTypes.size()) {
                convertType(code, cp, argTypes.get(i), expectedTypes.get(i));
            }
        }

        // Call the static method
        int methodRef = cp.addMethodRef(classInfo.getInternalName(), methodName, methodInfo.descriptor());
        code.invokestatic(methodRef);

        // Type conversion if needed for return value
        if (returnTypeInfo != null && returnTypeInfo.descriptor() != null) {
            String returnType = methodInfo.returnType();
            convertType(code, cp, returnType, returnTypeInfo.descriptor());
        }
    }

    /**
     * Checks if the call expression is a Java class method call.
     */
    public boolean isJavaClassMethodCall(Swc4jAstCallExpr callExpr) {
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            return false;
        }

        if (!(memberExpr.getObj() instanceof Swc4jAstIdent objIdent)) {
            return false;
        }

        String className = objIdent.getSym();
        return compiler.getMemory().getScopedJavaClassRegistry().resolve(className) != null;
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
