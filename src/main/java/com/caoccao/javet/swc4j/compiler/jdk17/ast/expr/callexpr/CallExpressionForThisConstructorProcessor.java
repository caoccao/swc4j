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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generates bytecode for this() constructor calls (constructor chaining).
 * Uses invokespecial to call another constructor of the same class.
 */
public final class CallExpressionForThisConstructorProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForThisConstructorProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get the current class
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "this() call outside of class context");
        }

        // Load 'this' reference
        code.aload(0);

        // Infer argument types and generate argument bytecode
        var args = callExpr.getArgs();
        StringBuilder paramDescriptors = new StringBuilder();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not yet supported in this() constructor calls");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            paramDescriptors.append(argType);
        }

        String methodDescriptor = "(" + paramDescriptors + ")V";

        // Generate invokespecial to call another constructor of this class
        int ctorRef = cp.addMethodRef(currentClassInternalName, ConstantJavaMethod.METHOD_INIT, methodDescriptor);
        code.invokespecial(ctorRef);
    }

}
