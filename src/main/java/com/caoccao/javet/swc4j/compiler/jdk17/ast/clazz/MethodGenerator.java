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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt.StatementGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

public final class MethodGenerator extends BaseAstProcessor {
    public MethodGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    public void generate(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            Swc4jAstClassMethod method) throws Swc4jByteCodeCompilerException {
        ISwc4jAstPropName key = method.getKey();
        String methodName = CodeGeneratorUtils.getMethodName(key);
        Swc4jAstFunction function = method.getFunction();

        // Only handle methods with bodies
        var bodyOpt = function.getBody();
        if (bodyOpt.isPresent()) {
            try {
                Swc4jAstBlockStmt body = bodyOpt.get();

                // Reset compilation context for this method
                compiler.getMemory().resetCompilationContext();
                CompilationContext context = compiler.getMemory().getCompilationContext();

                // Analyze function parameters and allocate local variable slots
                compiler.getVariableAnalyzer().analyzeParameters(function);

                // Analyze variable declarations and infer types
                compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

                // Determine return type from method body or explicit annotation
                ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, body);
                String descriptor = generateDescriptor(function, returnTypeInfo);
                CodeBuilder code = generateCode(cp, body, returnTypeInfo);

                int accessFlags = 0x0001; // ACC_PUBLIC
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
                    var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, classWriter.getClassName(), descriptor);
                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals,
                            lineNumbers, localVariableTable, stackMapTable);
                } else {
                    boolean isStatic = (accessFlags & 0x0008) != 0;
                    var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, classWriter.getClassName(), descriptor);
                    classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), maxStack, maxLocals,
                            null, null, stackMapTable);
                }
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException("Failed to generate method: " + methodName, e);
            }
        }
    }

    public CodeBuilder generateCode(
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt body,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            StatementGenerator.generate(compiler, code, cp, stmt, returnTypeInfo);
        }

        return code;
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
}
