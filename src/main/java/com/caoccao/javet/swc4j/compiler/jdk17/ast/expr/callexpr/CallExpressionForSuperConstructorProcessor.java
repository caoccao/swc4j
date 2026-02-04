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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generates bytecode for super() constructor calls.
 * Uses invokespecial to call the parent class constructor.
 */
public final class CallExpressionForSuperConstructorProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForSuperConstructorProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get the current class and resolve its superclass
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "super() call outside of class context");
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
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not yet supported in super constructor calls");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
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
     * Checks if the callee is a super expression.
     *
     * @param callee the callee expression
     * @return true if this generator can handle the call
     */
    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        return callee instanceof Swc4jAstSuper;
    }
}
