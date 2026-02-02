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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.CapturedVariable;
import com.caoccao.javet.swc4j.compiler.memory.ScopedFunctionalInterfaceRegistry.SamMethodInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

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

        // Verify argument count matches parameter count (allow rest/optional padding)
        var args = callExpr.getArgs();
        int paramCount = samInfo.paramTypes().size();
        boolean hasArrayRest = paramCount > 0 && samInfo.paramTypes().get(paramCount - 1).startsWith("[");
        String syntheticRestType = null;
        if (!hasArrayRest && paramCount == 1 && args.size() > 1
                && "Ljava/lang/Object;".equals(samInfo.paramTypes().get(0))) {
            hasArrayRest = true;
            syntheticRestType = "[Ljava/lang/Object;";
        }
        if (args.size() > paramCount && !hasArrayRest) {
            throw new Swc4jByteCodeCompilerException(callExpr,
                    "Expected " + paramCount + " arguments but got " + args.size());
        }
        if (hasArrayRest && args.size() < paramCount - 1) {
            throw new Swc4jByteCodeCompilerException(callExpr,
                    "Expected at least " + (paramCount - 1) + " arguments but got " + args.size());
        }
        if (!hasArrayRest && args.size() < paramCount) {
            for (int i = args.size(); i < paramCount; i++) {
                String expectedType = samInfo.paramTypes().get(i);
                if (TypeConversionUtils.isPrimitiveType(expectedType)) {
                    throw new Swc4jByteCodeCompilerException(callExpr,
                            "Missing primitive argument at index " + i + " for " + interfaceName);
                }
            }
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

        // Generate arguments (with rest packing / optional padding)
        for (int i = 0; i < paramCount; i++) {
            String expectedType = samInfo.paramTypes().get(i);
            if (hasArrayRest && i == paramCount - 1) {
                String restType = syntheticRestType != null ? syntheticRestType : expectedType;
                generateRestArray(code, cp, args, i, restType);
                continue;
            }
            if (i < args.size()) {
                var arg = args.get(i);
                if (arg.getSpread().isPresent()) {
                    throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not supported");
                }
                ReturnTypeInfo argTypeInfo = ReturnTypeInfo.of(arg.getExpr(), expectedType);
                compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), argTypeInfo);
            } else {
                pushMissingArg(code, cp, expectedType);
            }
        }

        // Call the SAM method using invokeinterface
        int methodRef = cp.addInterfaceMethodRef(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
        // Calculate slot count: 1 for 'this' + slots for all parameters
        // long and double take 2 slots, others take 1 slot
        int slotCount = 1; // 'this'
        for (String paramType : samInfo.paramTypes()) {
            slotCount += ("J".equals(paramType) || "D".equals(paramType)) ? 2 : 1;
        }
        code.invokeinterface(methodRef, slotCount);
    }

    private void generateRestArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            List<Swc4jAstExprOrSpread> args,
            int startIndex,
            String arrayType) throws Swc4jByteCodeCompilerException {
        String componentType = arrayType.substring(1);
        int restCount = Math.max(0, args.size() - startIndex);

        code.iconst(restCount);
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
                default -> throw new Swc4jByteCodeCompilerException(null,
                        "Unsupported rest primitive type: " + componentType);
            };
            code.newarray(typeCode);
        } else {
            int classRef = cp.addClass(toInternalName(componentType));
            code.anewarray(classRef);
        }

        for (int i = 0; i < restCount; i++) {
            var arg = args.get(startIndex + i);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not supported");
            }
            code.dup();
            code.iconst(i);
            ReturnTypeInfo argTypeInfo = ReturnTypeInfo.of(arg.getExpr(), componentType);
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), argTypeInfo);
            if ("Ljava/lang/Object;".equals(componentType)) {
                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
                if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                    TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                }
            }
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

    private void pushMissingArg(CodeBuilder code, ClassWriter.ConstantPool cp, String expectedType) {
        if (expectedType.startsWith("[")) {
            String componentType = expectedType.substring(1);
            code.iconst(0);
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
                    default -> 10;
                };
                code.newarray(typeCode);
            } else {
                int classRef = cp.addClass(toInternalName(componentType));
                code.anewarray(classRef);
            }
        } else if (TypeConversionUtils.isPrimitiveType(expectedType)) {
            switch (expectedType) {
                case "Z", "B", "C", "S", "I" -> code.iconst(0);
                case "J" -> code.lconst(0L);
                case "F" -> code.fconst(0.0f);
                case "D" -> code.dconst(0.0d);
                default -> code.iconst(0);
            }
        } else {
            code.aconst_null();
        }
    }

    private String toInternalName(String typeDescriptor) {
        if (typeDescriptor.startsWith("[")) {
            return typeDescriptor;
        }
        if (typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";")) {
            return typeDescriptor.substring(1, typeDescriptor.length() - 1);
        }
        return typeDescriptor;
    }
}
