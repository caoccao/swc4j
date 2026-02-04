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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;

/**
 * Generates a dummy class containing standalone functions as static methods.
 */
public final class StandaloneFunctionGenerator extends BaseAstProcessor {
    public StandaloneFunctionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void addReturnIfNeeded(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
        byte[] bytecode = code.toByteArray();
        if (bytecode.length == 0) {
            code.returnVoid();
            return;
        }

        // Check if the last instruction is already a return
        int lastByte = bytecode[bytecode.length - 1] & 0xFF;
        boolean hasReturn = lastByte == 0xAC || // ireturn
                lastByte == 0xAD || // lreturn
                lastByte == 0xAE || // freturn
                lastByte == 0xAF || // dreturn
                lastByte == 0xB0 || // areturn
                lastByte == 0xB1;   // return (void)

        if (!hasReturn) {
            if (returnTypeInfo.type() == ReturnType.VOID) {
                code.returnVoid();
            }
            // For non-void methods, we assume the code generator has already added the return
        }
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter.ConstantPool cp, ISwc4jAst ast, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        throw new Swc4jByteCodeCompilerException(getSourceCode(), ast, "StandaloneFunctionGenerator does not support generic generate method.");
    }

    /**
     * Generates bytecode for a dummy class containing all standalone functions as static methods.
     *
     * @param internalClassName the internal class name (e.g., "com/$" or "$")
     * @param functions         the list of function declarations
     * @return the bytecode for the class
     */
    public byte[] generateBytecode(String internalClassName, List<Swc4jAstFnDecl> functions) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(internalClassName, "java/lang/Object");
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Generate default constructor
        ClassDeclGenerator.generateDefaultConstructor(classWriter, cp, "java/lang/Object");

        // Generate each function as a static method
        for (Swc4jAstFnDecl fnDecl : functions) {
            generateStaticMethod(classWriter, cp, fnDecl, internalClassName);
        }

        return classWriter.toByteArray();
    }

    private void generateStaticMethod(ClassWriter classWriter, ClassWriter.ConstantPool cp, Swc4jAstFnDecl fnDecl, String internalClassName) throws Swc4jByteCodeCompilerException {
        String methodName = fnDecl.getIdent().getSym();
        Swc4jAstFunction function = fnDecl.getFunction();

        var bodyOpt = function.getBody();
        if (bodyOpt.isEmpty()) {
            // Skip declare functions (ambient declarations)
            return;
        }

        // Push type parameters scope for generic functions (type erasure)
        TypeParameterScope methodTypeParamScope = function.getTypeParams()
                .map(TypeParameterScope::fromDecl)
                .orElse(null);
        if (methodTypeParamScope != null) {
            compiler.getMemory().getCompilationContext().pushTypeParameterScope(methodTypeParamScope);
        }

        try {
            Swc4jAstBlockStmt body = bodyOpt.get();

            // Reset compilation context for this method (static = true)
            compiler.getMemory().resetCompilationContext(true);
            CompilationContext context = compiler.getMemory().getCompilationContext();

            // Analyze return type
            ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, body);

            // Build parameter descriptors and allocate parameter slots
            StringBuilder paramDescriptors = new StringBuilder();
            for (Swc4jAstParam param : function.getParams()) {
                String paramType = compiler.getTypeResolver().extractParameterType(param.getPat());
                paramDescriptors.append(paramType);

                // Allocate slot for parameter
                String paramName = compiler.getTypeResolver().extractParameterName(param.getPat());
                if (paramName != null) {
                    context.getLocalVariableTable().allocateVariable(paramName, paramType);
                    context.getInferredTypes().put(paramName, paramType);
                }
            }

            String returnDescriptor = getReturnDescriptor(returnTypeInfo);
            String descriptor = "(" + paramDescriptors + ")" + returnDescriptor;

            // Analyze variable declarations in the body
            compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

            // Generate method body
            CodeBuilder code = new CodeBuilder();
            for (ISwc4jAstStmt stmt : body.getStmts()) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }

            // Add return if needed
            addReturnIfNeeded(code, returnTypeInfo);

            int maxLocals = context.getLocalVariableTable().getMaxLocals();

            // ACC_PUBLIC | ACC_STATIC = 0x0009
            int accessFlags = 0x0009;
            boolean isStatic = true;

            // Generate stack map table for methods with branches (required for Java 7+)
            var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, internalClassName, descriptor, cp);
            var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();

            classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), 10, maxLocals,
                    null, null, stackMapTable, exceptionTable);
        } finally {
            if (methodTypeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
            }
        }
    }

    private String getReturnDescriptor(ReturnTypeInfo returnTypeInfo) {
        if (returnTypeInfo.descriptor() != null) {
            return returnTypeInfo.descriptor();
        }
        String primitiveDescriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
        return primitiveDescriptor != null ? primitiveDescriptor : "V";
    }
}
