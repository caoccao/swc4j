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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateMethod;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
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

public final class ClassMethodGenerator extends BaseAstProcessor<Swc4jAstClassMethod> {
    public ClassMethodGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private ReturnTypeInfo createReturnTypeInfoFromDescriptor(String descriptor) {
        return switch (descriptor) {
            case "I" -> new ReturnTypeInfo(ReturnType.INT, 1, null, null);
            case "Z" -> new ReturnTypeInfo(ReturnType.BOOLEAN, 1, null, null);
            case "B" -> new ReturnTypeInfo(ReturnType.BYTE, 1, null, null);
            case "C" -> new ReturnTypeInfo(ReturnType.CHAR, 1, null, null);
            case "S" -> new ReturnTypeInfo(ReturnType.SHORT, 1, null, null);
            case "J" -> new ReturnTypeInfo(ReturnType.LONG, 2, null, null);
            case "F" -> new ReturnTypeInfo(ReturnType.FLOAT, 1, null, null);
            case "D" -> new ReturnTypeInfo(ReturnType.DOUBLE, 2, null, null);
            case "Ljava/lang/String;" -> new ReturnTypeInfo(ReturnType.STRING, 1, null, null);
            default -> new ReturnTypeInfo(ReturnType.OBJECT, 1, descriptor, null);
        };
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, Swc4jAstClassMethod classMethod, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        throw new Swc4jByteCodeCompilerException(getSourceCode(), classMethod, "Use generate method with ClassWriter for class methods.");
    }

    public void generate(
            ClassWriter classWriter,
            Swc4jAstClassMethod method) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        ISwc4jAstPropName key = method.getKey();
        String methodName = CodeGeneratorUtils.getMethodName(key);
        Swc4jAstFunction function = method.getFunction();

        // Handle abstract methods (no body)
        if (method.isAbstract()) {
            generateAbstractMethod(classWriter, method, methodName, function);
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
                // Pass isStatic so that static methods use slot 0 for first parameter (no 'this')
                compiler.getMemory().resetCompilationContext(method.isStatic());
                CompilationContext context = compiler.getMemory().getCompilationContext();

                // Analyze function parameters and allocate local variable slots
                compiler.getVariableAnalyzer().analyzeParameters(function);

                // Analyze variable declarations and infer types
                compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

                // Analyze mutable captures to determine which variables need holder objects
                compiler.getMutableCaptureAnalyzer().analyze(body);

                // Determine return type from method body or explicit annotation
                ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, body);
                String descriptor = generateDescriptor(function, returnTypeInfo);
                CodeBuilder code = generateCode(classWriter, body, returnTypeInfo);

                int accessFlags = getAccessFlags(method.getAccessibility());
                if (method.isStatic()) {
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
                // Increase max stack to handle complex expressions like array literals with boxing
                maxStack = Math.max(maxStack, 10);
                int maxLocals = context.getLocalVariableTable().getMaxLocals();

                // Add debug information if enabled
                if (compiler.getOptions().debug()) {
                    List<ClassWriter.LineNumberEntry> lineNumbers = code.getLineNumbers();
                    List<ClassWriter.LocalVariableEntry> localVariableTable = new java.util.ArrayList<>();

                    // Build LocalVariableTable from compilation context
                    int codeLength = code.getCurrentOffset();
                    for (LocalVariable var : context.getLocalVariableTable().getAllVariables()) {
                        localVariableTable.add(new ClassWriter.LocalVariableEntry(
                                0, // startPc - variable scope starts at method beginning
                                codeLength, // length - variable scope covers entire method
                                var.name(),
                                var.type(),
                                var.index()
                        ));
                    }

                    // Generate stack map table for methods with branches (required for Java 7+)
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
                generateDefaultParameterOverloads(classWriter, method, methodName, function, returnTypeInfo, accessFlags);
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), method, "Failed to generate method: " + methodName, e);
            } finally {
                // Pop the method type parameter scope when done
                if (methodTypeParamScope != null) {
                    compiler.getMemory().getCompilationContext().popTypeParameterScope();
                }
            }
        }
    }

    /**
     * Generate bytecode for an ES2022 private method (#method).
     * Private methods are compiled with ACC_PRIVATE access and the method name
     * without the # prefix (stored as a regular private method in bytecode).
     */
    public void generate(
            ClassWriter classWriter,
            Swc4jAstPrivateMethod method) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get method name from PrivateName (without the # prefix)
        String methodName = method.getKey().getName();
        Swc4jAstFunction function = method.getFunction();

        // Handle abstract private methods (rare but possible in interfaces)
        if (method.isAbstract()) {
            generateAbstractPrivateMethod(classWriter, method, methodName, function);
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
                compiler.getMemory().resetCompilationContext(method.isStatic());
                CompilationContext context = compiler.getMemory().getCompilationContext();

                // Analyze function parameters and allocate local variable slots
                compiler.getVariableAnalyzer().analyzeParameters(function);

                // Analyze variable declarations and infer types
                compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

                // Analyze mutable captures to determine which variables need holder objects
                compiler.getMutableCaptureAnalyzer().analyze(body);

                // Determine return type from method body or explicit annotation
                ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, body);
                String descriptor = generateDescriptor(function, returnTypeInfo);
                CodeBuilder code = generateCode(classWriter, body, returnTypeInfo);

                // Private methods are always ACC_PRIVATE (0x0002)
                int accessFlags = 0x0002; // ACC_PRIVATE
                if (method.isStatic()) {
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
                generateDefaultParameterOverloadsForPrivateMethod(classWriter, method, methodName, function, returnTypeInfo, accessFlags);
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), method, "Failed to generate private method: " + methodName, e);
            } finally {
                if (methodTypeParamScope != null) {
                    compiler.getMemory().getCompilationContext().popTypeParameterScope();
                }
            }
        }
    }

    private void generateAbstractMethod(
            ClassWriter classWriter,
            Swc4jAstClassMethod method,
            String methodName,
            Swc4jAstFunction function) throws Swc4jByteCodeCompilerException {
        // Push type parameters scope for generic methods (type erasure)
        TypeParameterScope methodTypeParamScope = function.getTypeParams()
                .map(TypeParameterScope::fromDecl)
                .orElse(null);
        if (methodTypeParamScope != null) {
            compiler.getMemory().getCompilationContext().pushTypeParameterScope(methodTypeParamScope);
        }

        try {
            // Reset compilation context to analyze parameters and return type
            compiler.getMemory().resetCompilationContext(method.isStatic());

            // Analyze function parameters
            compiler.getVariableAnalyzer().analyzeParameters(function);

            // Determine return type from explicit annotation
            ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, null);
            String descriptor = generateDescriptor(function, returnTypeInfo);

            // ACC_ABSTRACT + access modifier (no ACC_STATIC for abstract methods, but they can be static in interfaces)
            int accessFlags = getAccessFlags(method.getAccessibility()) | 0x0400; // ACC_ABSTRACT
            if (method.isStatic()) {
                accessFlags |= 0x0008; // ACC_STATIC
            }

            // Abstract methods have no code (null)
            classWriter.addMethod(accessFlags, methodName, descriptor, null, 0, 0);
        } finally {
            // Pop the method type parameter scope when done
            if (methodTypeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
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

    public CodeBuilder generateCode(
            ClassWriter classWriter,
            Swc4jAstBlockStmt body,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        compiler.getStatementGenerator().generate(code, classWriter, body.getStmts(), returnTypeInfo);

        // Add implicit return for void methods if not already present
        if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.VOID) {
            // Check if the last bytecode is not already a terminal instruction
            byte[] bytecode = code.toByteArray();
            if (bytecode.length == 0 || !isTerminalBytecode(bytecode[bytecode.length - 1] & 0xFF)) {
                code.returnVoid();
            }
        }

        return code;
    }

    /**
     * Generates overloaded methods for parameters with default values.
     * For each default parameter, generates a method that calls the full method with the default value.
     */
    private void generateDefaultParameterOverloads(
            ClassWriter classWriter,
            Swc4jAstClassMethod method,
            String methodName,
            Swc4jAstFunction function,
            ReturnTypeInfo returnTypeInfo,
            int baseAccessFlags) throws Swc4jByteCodeCompilerException {
        List<Swc4jAstParam> params = function.getParams();

        // Find the index of the first default parameter
        int firstDefaultIndex = -1;
        for (int i = 0; i < params.size(); i++) {
            if (compiler.getTypeResolver().hasDefaultValue(params.get(i).getPat())) {
                firstDefaultIndex = i;
                break;
            }
        }

        // No default parameters, nothing to generate
        if (firstDefaultIndex == -1) {
            return;
        }

        // Get full method descriptor for calling
        String fullDescriptor = generateDescriptor(function, returnTypeInfo);
        String internalClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

        // Generate overloads for each valid parameter count
        // e.g., for add(a: int, b: int = 10, c: int = 20):
        // - add(int a) calls add(a, 10, 20)
        // - add(int a, int b) calls add(a, b, 20)
        for (int paramCount = firstDefaultIndex; paramCount < params.size(); paramCount++) {
            generateOverloadMethod(classWriter, method, methodName, function, returnTypeInfo,
                    baseAccessFlags, paramCount, fullDescriptor, internalClassName);
        }
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

    public String generateDescriptor(
            Swc4jAstFunction function,
            ReturnTypeInfo returnTypeInfo) {
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
     * Generates a single overload method that calls the full method with default values.
     */
    private void generateOverloadMethod(
            ClassWriter classWriter,
            Swc4jAstClassMethod method,
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

        // Reset compilation context for this overload
        compiler.getMemory().resetCompilationContext(isStatic);

        // Build the overload descriptor (with fewer parameters)
        StringBuilder overloadParamDescriptors = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            overloadParamDescriptors.append(paramType);
        }
        String returnDescriptor = getReturnDescriptor(returnTypeInfo);
        String overloadDescriptor = "(" + overloadParamDescriptors + ")" + returnDescriptor;

        // Generate bytecode for the overload
        CodeBuilder code = new CodeBuilder();

        // Load 'this' for instance methods
        int slot = 0;
        if (!isStatic) {
            code.aload(0);
            slot = 1;
        }

        // Load parameters that are provided
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            loadParameter(code, slot, paramType);
            slot += getSlotSize(paramType);
        }

        // Generate default values for remaining parameters
        for (int i = paramCount; i < params.size(); i++) {
            ISwc4jAstExpr defaultValue = compiler.getTypeResolver().extractDefaultValue(params.get(i).getPat());
            if (defaultValue != null) {
                // Create ReturnTypeInfo for the expected parameter type
                String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
                ReturnTypeInfo expectedType = createReturnTypeInfoFromDescriptor(paramType);
                compiler.getExpressionGenerator().generate(code, classWriter, defaultValue, expectedType);
            } else {
                // This shouldn't happen if firstDefaultIndex is correct
                throw new Swc4jByteCodeCompilerException(getSourceCode(), params.get(i),
                        "Expected default value for parameter at index " + i);
            }
        }

        // Call the full method
        int methodRef = cp.addMethodRef(internalClassName, methodName, fullDescriptor);
        if (isStatic) {
            code.invokestatic(methodRef);
        } else {
            code.invokevirtual(methodRef);
        }

        // Return the result
        generateReturn(code, returnTypeInfo);

        // Calculate max locals for the overload
        int maxLocals = isStatic ? 0 : 1;
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            maxLocals += getSlotSize(paramType);
        }

        classWriter.addMethod(baseAccessFlags, methodName, overloadDescriptor, code.toByteArray(),
                10, // max stack
                maxLocals);
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
        String returnDescriptor = getReturnDescriptor(returnTypeInfo);
        String overloadDescriptor = "(" + overloadParamDescriptors + ")" + returnDescriptor;

        CodeBuilder code = new CodeBuilder();

        int slot = 0;
        if (!isStatic) {
            code.aload(0);
            slot = 1;
        }

        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            loadParameter(code, slot, paramType);
            slot += getSlotSize(paramType);
        }

        for (int i = paramCount; i < params.size(); i++) {
            ISwc4jAstExpr defaultValue = compiler.getTypeResolver().extractDefaultValue(params.get(i).getPat());
            if (defaultValue != null) {
                String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
                ReturnTypeInfo expectedType = createReturnTypeInfoFromDescriptor(paramType);
                compiler.getExpressionGenerator().generate(code, classWriter, defaultValue, expectedType);
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

        generateReturn(code, returnTypeInfo);

        int maxLocals = isStatic ? 0 : 1;
        for (int i = 0; i < paramCount; i++) {
            String paramType = compiler.getTypeResolver().extractParameterType(params.get(i).getPat());
            maxLocals += getSlotSize(paramType);
        }

        classWriter.addMethod(baseAccessFlags, methodName, overloadDescriptor, code.toByteArray(),
                10, maxLocals);
    }

    private void generateReturn(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
        switch (returnTypeInfo.type()) {
            case VOID -> code.returnVoid();
            case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.ireturn();
            case LONG -> code.lreturn();
            case FLOAT -> code.freturn();
            case DOUBLE -> code.dreturn();
            case STRING, OBJECT -> code.areturn();
        }
    }

    /**
     * Converts TypeScript/ES accessibility to JVM access flags.
     *
     * @param accessibility the accessibility modifier (Public, Protected, Private)
     * @return JVM access flags (ACC_PUBLIC=0x0001, ACC_PROTECTED=0x0004, ACC_PRIVATE=0x0002)
     */
    private int getAccessFlags(java.util.Optional<Swc4jAstAccessibility> accessibility) {
        if (accessibility.isEmpty()) {
            return 0x0001; // Default to ACC_PUBLIC
        }
        return switch (accessibility.get()) {
            case Public -> 0x0001;    // ACC_PUBLIC
            case Protected -> 0x0004; // ACC_PROTECTED
            case Private -> 0x0002;   // ACC_PRIVATE
        };
    }

    private String getReturnDescriptor(ReturnTypeInfo returnTypeInfo) {
        return switch (returnTypeInfo.type()) {
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
    }

    private int getSlotSize(String type) {
        return ("J".equals(type) || "D".equals(type)) ? 2 : 1;
    }

    /**
     * Check if a bytecode opcode is a terminal instruction that ends a method.
     * Terminal instructions include all return types and athrow.
     *
     * @param opcode the bytecode opcode
     * @return true if the opcode is a terminal instruction
     */
    private boolean isTerminalBytecode(int opcode) {
        return switch (opcode) {
            case 0xAC, // ireturn
                 0xAD, // lreturn
                 0xAE, // freturn
                 0xAF, // dreturn
                 0xB0, // areturn
                 0xB1, // return (void)
                 0xBF  // athrow
                    -> true;
            default -> false;
        };
    }

    private void loadParameter(CodeBuilder code, int slot, String paramType) {
        switch (paramType) {
            case "I", "Z", "B", "C", "S" -> code.iload(slot);
            case "J" -> code.lload(slot);
            case "F" -> code.fload(slot);
            case "D" -> code.dload(slot);
            default -> code.aload(slot);
        }
    }
}
