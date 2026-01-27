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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CapturedVariable;
import com.caoccao.javet.swc4j.compiler.memory.ScopedFunctionalInterfaceRegistry.SamMethodInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generates bytecode for calling functional interface variables directly.
 * <p>
 * For example: {@code factorial(n - 1)} where factorial is an IntUnaryOperator
 * will be translated to {@code factorial.applyAsInt(n - 1)}
 */
public final class CallExpressionForFunctionalInterfaceGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {

    public CallExpressionForFunctionalInterfaceGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var callee = callExpr.getCallee();
        if (!(callee instanceof Swc4jAstIdent ident)) {
            throw new Swc4jByteCodeCompilerException(callExpr, "Expected identifier callee for functional interface call");
        }

        String varName = ident.getSym();
        var context = compiler.getMemory().getCompilationContext();

        // Get the type of the variable
        String varType = context.getInferredTypes().get(varName);
        if (varType == null) {
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                varType = localVar.type();
            }
        }

        if (varType == null || !varType.startsWith("L") || !varType.endsWith(";")) {
            throw new Swc4jByteCodeCompilerException(callExpr, "Cannot determine type of callee: " + varName);
        }

        // Get SAM method info for the functional interface using reflection
        String interfaceName = varType.substring(1, varType.length() - 1);
        var registry = compiler.getMemory().getScopedFunctionalInterfaceRegistry();
        SamMethodInfo samInfo = registry.getSamMethodInfo(interfaceName);
        if (samInfo == null) {
            throw new Swc4jByteCodeCompilerException(callExpr,
                    "Type " + interfaceName + " is not a functional interface");
        }

        // Verify argument count matches parameter count
        var args = callExpr.getArgs();
        if (args.size() != samInfo.paramTypes().size()) {
            throw new Swc4jByteCodeCompilerException(callExpr,
                    "Expected " + samInfo.paramTypes().size() + " arguments but got " + args.size());
        }

        // Load the functional interface variable
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        CapturedVariable capturedVar = context.getCapturedVariables().get(varName);

        if (localVar != null) {
            code.aload(localVar.index());
        } else if (capturedVar != null) {
            // Load from captured field
            code.aload(0); // this
            String currentClass = context.getCurrentClassInternalName();
            int fieldRef = cp.addFieldRef(currentClass, capturedVar.fieldName(), capturedVar.type());
            code.getfield(fieldRef);
        } else {
            throw new Swc4jByteCodeCompilerException(callExpr, "Variable not found: " + varName);
        }

        // Generate arguments
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not supported");
            }
            String expectedType = samInfo.paramTypes().get(i);
            ReturnTypeInfo argTypeInfo = ReturnTypeInfo.of(arg.getExpr(), expectedType);
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), argTypeInfo);
        }

        // Call the SAM method using invokeinterface
        int methodRef = cp.addInterfaceMethodRef(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
        int argCount = args.size() + 1; // +1 for 'this'
        code.invokeinterface(methodRef, argCount);
    }

    /**
     * Checks if the callee is an identifier that refers to a functional interface variable.
     *
     * @param callee the callee expression
     * @return true if this generator can handle the call
     */
    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        if (!(callee instanceof Swc4jAstIdent ident)) {
            return false;
        }

        String varName = ident.getSym();
        var context = compiler.getMemory().getCompilationContext();

        // Get the type of the variable
        String varType = context.getInferredTypes().get(varName);
        if (varType == null) {
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                varType = localVar.type();
            }
        }

        if (varType == null || !varType.startsWith("L") || !varType.endsWith(";")) {
            return false;
        }

        // Check if it's a functional interface using the scoped registry
        String interfaceName = varType.substring(1, varType.length() - 1);
        return compiler.getMemory().getScopedFunctionalInterfaceRegistry().isFunctionalInterface(interfaceName);
    }
}
