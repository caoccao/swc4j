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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSuperPropExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSuperProp;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForArrayGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForArrayListGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForClassGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForStringGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    private final CallExpressionForArrayGenerator arrayGenerator;
    private final CallExpressionForArrayListGenerator arrayListGenerator;
    private final CallExpressionForClassGenerator classGenerator;
    private final CallExpressionForStringGenerator stringGenerator;

    public CallExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
        arrayGenerator = new CallExpressionForArrayGenerator(compiler);
        arrayListGenerator = new CallExpressionForArrayListGenerator(compiler);
        classGenerator = new CallExpressionForClassGenerator(compiler);
        stringGenerator = new CallExpressionForStringGenerator(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Handle super() constructor calls
        if (callExpr.getCallee() instanceof Swc4jAstSuper) {
            generateSuperConstructorCall(code, cp, callExpr);
            return;
        }

        // Handle super.method() calls
        if (callExpr.getCallee() instanceof Swc4jAstSuperPropExpr superPropExpr) {
            generateSuperMethodCall(code, cp, callExpr, superPropExpr, returnTypeInfo);
            return;
        }

        // Handle method calls on arrays, strings, Java classes, TypeScript classes, etc.
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Check for Java class method calls first (static methods)

            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
            if (arrayGenerator.isTypeSupported(objType)) {
                arrayGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (arrayListGenerator.isTypeSupported(objType)) {
                arrayListGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (stringGenerator.isTypeSupported(objType)) {
                stringGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (classGenerator.isTypeSupported(objType)) {
                classGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException(callExpr, "Call expression not yet supported");
    }

    /**
     * Generates bytecode for super() constructor calls.
     * Uses invokespecial to call the parent class constructor.
     */
    private void generateSuperConstructorCall(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Get the current class and resolve its superclass
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(callExpr, "super() call outside of class context");
        }

        // Resolve the superclass
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            // Try simple name
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? currentClassInternalName.substring(lastSlash + 1) : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        if (superClassInternalName == null) {
            superClassInternalName = "java/lang/Object";
        }

        // Load 'this' reference
        code.aload(0);

        // Infer argument types and generate argument bytecode
        var args = callExpr.getArgs();
        StringBuilder paramDescriptors = new StringBuilder();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not yet supported in super constructor calls");
            }
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = "Ljava/lang/Object;";
            }
            paramDescriptors.append(argType);
        }

        String methodDescriptor = "(" + paramDescriptors + ")V";

        // Generate invokespecial to call the superclass constructor
        int ctorRef = cp.addMethodRef(superClassInternalName, "<init>", methodDescriptor);
        code.invokespecial(ctorRef);
    }

    /**
     * Generates bytecode for super.method() calls.
     * Uses invokespecial to call the parent class method.
     */
    private void generateSuperMethodCall(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            Swc4jAstSuperPropExpr superPropExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Get method name from the super property expression
        ISwc4jAstSuperProp prop = superPropExpr.getProp();
        String methodName;
        if (prop instanceof Swc4jAstIdentName identName) {
            methodName = identName.getSym();
        } else {
            throw new Swc4jByteCodeCompilerException(superPropExpr, "Computed super property expressions not yet supported");
        }

        // Get the current class and resolve its superclass
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(callExpr, "super.method() call outside of class context");
        }

        // Resolve the superclass
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            // Try simple name
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? currentClassInternalName.substring(lastSlash + 1) : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        if (superClassInternalName == null) {
            superClassInternalName = "java/lang/Object";
        }

        // Load 'this' reference
        code.aload(0);

        // Infer argument types and generate argument bytecode
        var args = callExpr.getArgs();
        StringBuilder paramDescriptors = new StringBuilder();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not yet supported in super method calls");
            }
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = "Ljava/lang/Object;";
            }
            paramDescriptors.append(argType);
        }

        // Look up method return type from the superclass
        String paramDescriptor = "(" + paramDescriptors + ")";
        String superQualifiedName = superClassInternalName.replace('/', '.');
        String returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveClassMethodReturnType(superQualifiedName, methodName, paramDescriptor);
        if (returnType == null) {
            // Try simple name
            int lastSlash = superClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? superClassInternalName.substring(lastSlash + 1) : superClassInternalName;
            returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                    .resolveClassMethodReturnType(simpleName, methodName, paramDescriptor);
        }
        if (returnType == null) {
            throw new Swc4jByteCodeCompilerException(callExpr,
                    "Cannot infer return type for super method call " + superClassInternalName + "." + methodName);
        }

        String methodDescriptor = paramDescriptor + returnType;

        // Generate invokespecial to call the superclass method
        int methodRef = cp.addMethodRef(superClassInternalName, methodName, methodDescriptor);
        code.invokespecial(methodRef);
    }
}
