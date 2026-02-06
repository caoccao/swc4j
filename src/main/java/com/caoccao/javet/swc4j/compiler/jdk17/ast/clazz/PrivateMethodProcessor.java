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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateMethod;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * The type Private method processor.
 */
public final class PrivateMethodProcessor extends BaseAstProcessor<Swc4jAstPrivateMethod> {
    /**
     * Instantiates a new Private method processor.
     *
     * @param compiler the compiler
     */
    public PrivateMethodProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, Swc4jAstPrivateMethod privateMethod, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Get method name from PrivateName (without the # prefix)
        String methodName = privateMethod.getKey().getName();
        Swc4jAstFunction function = privateMethod.getFunction();

        // Handle abstract private methods (rare but possible in interfaces)
        if (privateMethod.isAbstract()) {
            generateAbstractPrivateMethod(classWriter, privateMethod, methodName, function);
            return;
        }

        // Handle methods with bodies
        var bodyOpt = function.getBody();
        if (bodyOpt.isPresent()) {
            // Push type parameters scope for generic methods (type erasure)
            TypeParameterScope methodTypeParamScope = function.getTypeParams()
                    .map(TypeParameterScope::fromDecl)
                    .orElse(null);
            if (methodTypeParamScope != null) {
                compiler.getMemory().getCompilationContext().pushTypeParameterScope(methodTypeParamScope);
            }

            try {
                Swc4jAstBlockStmt body = bodyOpt.get();

                // Reset compilation context for this method
                compiler.getMemory().resetCompilationContext(privateMethod.isStatic());
                CompilationContext context = compiler.getMemory().getCompilationContext();

                // Analyze function parameters and allocate local variable slots
                compiler.getVariableAnalyzer().analyzeParameters(function);

                // Analyze variable declarations and infer types
                compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

                // Analyze mutable captures to determine which variables need holder objects
                compiler.getMutableCaptureAnalyzer().analyze(body);

                // Determine return type from method body or explicit annotation
                returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, body);
                String descriptor = generateDescriptor(function, returnTypeInfo);
                code = generateCode(classWriter, body, returnTypeInfo);

                // Private methods are always ACC_PRIVATE (0x0002)
                int accessFlags = 0x0002; // ACC_PRIVATE
                if (privateMethod.isStatic()) {
                    accessFlags |= 0x0008; // ACC_STATIC
                }
                // Check if method has varargs (RestPat in last parameter)
                if (!function.getParams().isEmpty()) {
                    Swc4jAstParam lastParam = function.getParams().get(function.getParams().size() - 1);
                    if (lastParam.getPat() instanceof Swc4jAstRestPat) {
                        accessFlags |= 0x0080; // ACC_VARARGS
                    }
                }

                int maxStack = Math.max(returnTypeInfo.maxStack(), returnTypeInfo.type().getMinStack());
                maxStack = Math.max(maxStack, 10);
                int maxLocals = context.getLocalVariableTable().getMaxLocals();

                // Add debug information if enabled
                if (compiler.getOptions().debug()) {
                    List<ClassWriter.LineNumberEntry> lineNumbers = code.getLineNumbers();
                    List<ClassWriter.LocalVariableEntry> localVariableTable = new java.util.ArrayList<>();

                    int codeLength = code.getCurrentOffset();
                    for (LocalVariable var : context.getLocalVariableTable().getAllVariables()) {
                        localVariableTable.add(new ClassWriter.LocalVariableEntry(
                                0, codeLength, var.name(), var.type(), var.index()
                        ));
                    }

                    boolean isStatic = (accessFlags & 0x0008) != 0;
                    var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, classWriter.getClassName(), descriptor, classWriter.getConstantPool());
                    var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();
                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals,
                            lineNumbers, localVariableTable, stackMapTable, exceptionTable);
                } else {
                    boolean isStatic = (accessFlags & 0x0008) != 0;
                    var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, classWriter.getClassName(), descriptor, classWriter.getConstantPool());
                    var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();
                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals,
                            null, null, stackMapTable, exceptionTable);
                }

                // Generate overloaded methods for default parameters
                generateDefaultParameterOverloadsForPrivateMethod(classWriter, privateMethod, methodName, function, returnTypeInfo, accessFlags);
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), privateMethod, "Failed to generate private method: " + methodName, e);
            } finally {
                if (methodTypeParamScope != null) {
                    compiler.getMemory().getCompilationContext().popTypeParameterScope();
                }
            }
        }
    }

    private void generateAbstractPrivateMethod(
            ClassWriter classWriter,
            Swc4jAstPrivateMethod method,
            String methodName,
            Swc4jAstFunction function) throws Swc4jByteCodeCompilerException {
        TypeParameterScope methodTypeParamScope = function.getTypeParams()
                .map(TypeParameterScope::fromDecl)
                .orElse(null);
        if (methodTypeParamScope != null) {
            compiler.getMemory().getCompilationContext().pushTypeParameterScope(methodTypeParamScope);
        }

        try {
            compiler.getMemory().resetCompilationContext(method.isStatic());
            compiler.getVariableAnalyzer().analyzeParameters(function);
            ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, null);
            String descriptor = generateDescriptor(function, returnTypeInfo);

            // ACC_PRIVATE + ACC_ABSTRACT
            int accessFlags = 0x0002 | 0x0400; // ACC_PRIVATE | ACC_ABSTRACT
            if (method.isStatic()) {
                accessFlags |= 0x0008; // ACC_STATIC
            }

            classWriter.addMethod(accessFlags, methodName, descriptor, null, 0, 0);
        } finally {
            if (methodTypeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
            }
        }
    }

    private CodeBuilder generateCode(
            ClassWriter classWriter,
            Swc4jAstBlockStmt body,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        compiler.getStatementProcessor().generate(code, classWriter, body.getStmts(), returnTypeInfo);

        // Add implicit return for void methods if not already present
        if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.VOID) {
            // Check if the last bytecode is not already a terminal instruction
            byte[] bytecode = code.toByteArray();
            if (bytecode.length == 0 || !CodeGeneratorUtils.isTerminalBytecode(bytecode[bytecode.length - 1] & 0xFF)) {
                code.returnVoid();
            }
        }

        return code;
    }

    /**
     * Generates overloaded methods for private methods with default parameters.
     */
    private void generateDefaultParameterOverloadsForPrivateMethod(
            ClassWriter classWriter,
            Swc4jAstPrivateMethod method,
            String methodName,
            Swc4jAstFunction function,
            ReturnTypeInfo returnTypeInfo,
            int baseAccessFlags) throws Swc4jByteCodeCompilerException {
        List<Swc4jAstParam> params = function.getParams();

        int firstDefaultIndex = -1;
        for (int i = 0; i < params.size(); i++) {
            if (compiler.getTypeResolver().hasDefaultValue(params.get(i).getPat())) {
                firstDefaultIndex = i;
                break;
            }
        }

        if (firstDefaultIndex == -1) {
            return;
        }

        String fullDescriptor = generateDescriptor(function, returnTypeInfo);
        String internalClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

        for (int paramCount = firstDefaultIndex; paramCount < params.size(); paramCount++) {
            generatePrivateOverloadMethod(classWriter, method, methodName, function, returnTypeInfo,
                    baseAccessFlags, paramCount, fullDescriptor, internalClassName);
        }
    }

    /**
     * Generate descriptor string.
     *
     * @param function       the function
     * @param returnTypeInfo the return type info
     * @return the string
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String generateDescriptor(
            Swc4jAstFunction function,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Build parameter descriptors
        StringBuilder paramDescriptors = new StringBuilder();
        for (Swc4jAstParam param : function.getParams()) {
            String paramType = compiler.getTypeResolver().extractParameterType(param.getPat());
            paramDescriptors.append(paramType);
        }

        String returnDescriptor = switch (returnTypeInfo.type()) {
            case VOID -> "V";
            case INT -> "I";
            case BOOLEAN -> "Z";
            case BYTE -> "B";
            case CHAR -> "C";
            case SHORT -> "S";
            case LONG -> "J";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
            case OBJECT -> returnTypeInfo.descriptor() != null ? returnTypeInfo.descriptor() : "Ljava/lang/Object;";
        };

        return "(" + paramDescriptors + ")" + returnDescriptor;
    }

    /**
     * Generates a single overload method for a private method.
     */
    private void generatePrivateOverloadMethod(
            ClassWriter classWriter,
            Swc4jAstPrivateMethod method,
            String methodName,
            Swc4jAstFunction function,
            ReturnTypeInfo returnTypeInfo,
            int baseAccessFlags,
            int paramCount,
            String fullDescriptor,
            String internalClassName) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        List<Swc4jAstParam> params = function.getParams();
        boolean isStatic = method.isStatic();

        compiler.getMemory().resetCompilationContext(isStatic);

        StringBuilder overloadParamDescriptors = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            overloadParamDescriptors.append(paramType);
        }
        String returnDescriptor = CodeGeneratorUtils.getReturnDescriptor(returnTypeInfo);
        String overloadDescriptor = "(" + overloadParamDescriptors + ")" + returnDescriptor;

        CodeBuilder code = new CodeBuilder();

        int slot = 0;
        if (!isStatic) {
            code.aload(0);
            slot = 1;
        }

        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            CodeGeneratorUtils.loadParameter(code, slot, paramType);
            slot += CodeGeneratorUtils.getSlotSize(paramType);
        }

        for (int i = paramCount; i < params.size(); i++) {
            ISwc4jAstExpr defaultValue = compiler.getTypeResolver().extractDefaultValue(params.get(i).getPat());
            if (defaultValue != null) {
                String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
                ReturnTypeInfo expectedType = CodeGeneratorUtils.createReturnTypeInfoFromDescriptor(paramType);
                compiler.getExpressionProcessor().generate(code, classWriter, defaultValue, expectedType);
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), params.get(i),
                        "Expected default value for parameter at index " + i);
            }
        }

        int methodRef = cp.addMethodRef(internalClassName, methodName, fullDescriptor);
        if (isStatic) {
            code.invokestatic(methodRef);
        } else {
            // Private methods use invokespecial
            code.invokespecial(methodRef);
        }

        CodeGeneratorUtils.generateReturn(code, returnTypeInfo);

        int maxLocals = isStatic ? 0 : 1;
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            maxLocals += CodeGeneratorUtils.getSlotSize(paramType);
        }

        classWriter.addMethod(baseAccessFlags, methodName, overloadDescriptor, code.toByteArray(),
                10, maxLocals);
    }
}
