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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CapturedVariable;
import com.caoccao.javet.swc4j.compiler.memory.ScopedFunctionalInterfaceRegistry.SamMethodInfo;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Generates bytecode for calling functional interface variables directly.
 * <p>
 * For example: {@code factorial(n - 1)} where factorial is an IntUnaryOperator
 * will be translated to {@code factorial.applyAsInt(n - 1)}
 */
public final class CallExpressionForFunctionalInterfaceProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {

    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForFunctionalInterfaceProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        var callee = callExpr.getCallee();
        if (!(callee instanceof Swc4jAstIdent ident)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Expected identifier callee for functional interface call");
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
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Cannot determine type of callee: " + varName);
        }

        // Get SAM method info for the functional interface using reflection
        String interfaceName = TypeConversionUtils.descriptorToInternalName(varType);
        var registry = compiler.getMemory().getScopedFunctionalInterfaceRegistry();
        SamMethodInfo samInfo = registry.getSamMethodInfo(interfaceName);
        if (samInfo == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                    "Type " + interfaceName + " is not a functional interface");
        }

        // Verify argument count matches parameter count (allow rest/optional padding)
        var args = callExpr.getArgs();
        int paramCount = samInfo.paramTypes().size();
        boolean hasArrayRest = paramCount > 0 && samInfo.paramTypes().get(paramCount - 1).startsWith(ConstantJavaType.ARRAY_PREFIX);
        String syntheticRestType = null;
        if (!hasArrayRest && paramCount == 1 && args.size() > 1
                && ConstantJavaType.LJAVA_LANG_OBJECT.equals(samInfo.paramTypes().get(0))) {
            hasArrayRest = true;
            syntheticRestType = "[Ljava/lang/Object;";
        }
        if (args.size() > paramCount && !hasArrayRest) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                    "Expected " + paramCount + " arguments but got " + args.size());
        }
        if (hasArrayRest && args.size() < paramCount - 1) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                    "Expected at least " + (paramCount - 1) + " arguments but got " + args.size());
        }
        if (!hasArrayRest && args.size() < paramCount) {
            for (int i = args.size(); i < paramCount; i++) {
                String expectedType = samInfo.paramTypes().get(i);
                if (TypeConversionUtils.isPrimitiveType(expectedType)) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
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
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Variable not found: " + varName);
        }

        // Generate arguments (with rest packing / optional padding)
        for (int i = 0; i < paramCount; i++) {
            String expectedType = samInfo.paramTypes().get(i);
            if (hasArrayRest && i == paramCount - 1) {
                String restType = syntheticRestType != null ? syntheticRestType : expectedType;
                generateRestArray(code, classWriter, args, i, restType);
                continue;
            }
            if (i < args.size()) {
                var arg = args.get(i);
                if (arg.getSpread().isPresent()) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported");
                }
                ReturnTypeInfo argTypeInfo = ReturnTypeInfo.of(getSourceCode(), arg.getExpr(), expectedType);
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), argTypeInfo);
            } else {
                pushMissingArg(code, classWriter, expectedType);
            }
        }

        // Call the SAM method using invokeinterface
        int methodRef = cp.addInterfaceMethodRef(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
        // Calculate slot count: 1 for 'this' + slots for all parameters
        // long and double take 2 slots, others take 1 slot
        int slotCount = 1; // 'this'
        for (String paramType : samInfo.paramTypes()) {
            slotCount += (ConstantJavaType.ABBR_LONG.equals(paramType) || ConstantJavaType.ABBR_DOUBLE.equals(paramType)) ? 2 : 1;
        }
        code.invokeinterface(methodRef, slotCount);
    }

    private void generateRestArray(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstExprOrSpread> args,
            int startIndex,
            String arrayType) throws Swc4jByteCodeCompilerException {
        String componentType = TypeConversionUtils.getArrayElementType(arrayType);
        int restCount = Math.max(0, args.size() - startIndex);

        var cp = classWriter.getConstantPool();
        code.iconst(restCount);
        if (TypeConversionUtils.isPrimitiveType(componentType)) {
            code.newarray(TypeConversionUtils.getNewarrayTypeCode(componentType));
        } else {
            int classRef = cp.addClass(TypeConversionUtils.toInternalName(componentType));
            code.anewarray(classRef);
        }

        for (int i = 0; i < restCount; i++) {
            var arg = args.get(startIndex + i);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported");
            }
            code.dup();
            code.iconst(i);
            ReturnTypeInfo argTypeInfo = ReturnTypeInfo.of(getSourceCode(), arg.getExpr(), componentType);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), argTypeInfo);
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(componentType)) {
                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
                if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
                }
            }
            switch (componentType) {
                case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore();
                case ConstantJavaType.ABBR_CHARACTER -> code.castore();
                case ConstantJavaType.ABBR_SHORT -> code.sastore();
                case ConstantJavaType.ABBR_INTEGER -> code.iastore();
                case ConstantJavaType.ABBR_LONG -> code.lastore();
                case ConstantJavaType.ABBR_FLOAT -> code.fastore();
                case ConstantJavaType.ABBR_DOUBLE -> code.dastore();
                default -> code.aastore();
            }
        }
    }

    private void pushMissingArg(CodeBuilder code, ClassWriter classWriter, String expectedType) {
        var cp = classWriter.getConstantPool();
        if (expectedType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            String componentType = TypeConversionUtils.getArrayElementType(expectedType);
            code.iconst(0);
            if (TypeConversionUtils.isPrimitiveType(componentType)) {
                code.newarray(TypeConversionUtils.getNewarrayTypeCode(componentType));
            } else {
                int classRef = cp.addClass(TypeConversionUtils.toInternalName(componentType));
                code.anewarray(classRef);
            }
        } else if (TypeConversionUtils.isPrimitiveType(expectedType)) {
            switch (expectedType) {
                case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE,
                     ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_SHORT,
                     ConstantJavaType.ABBR_INTEGER -> code.iconst(0);
                case ConstantJavaType.ABBR_LONG -> code.lconst(0L);
                case ConstantJavaType.ABBR_FLOAT -> code.fconst(0.0f);
                case ConstantJavaType.ABBR_DOUBLE -> code.dconst(0.0d);
                default -> code.iconst(0);
            }
        } else {
            code.aconst_null();
        }
    }

}
