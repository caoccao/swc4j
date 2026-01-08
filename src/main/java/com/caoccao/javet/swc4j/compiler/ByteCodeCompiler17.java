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

package com.caoccao.javet.swc4j.compiler;

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ByteCodeCompiler17 extends ByteCodeCompiler {
    ByteCodeCompiler17(ByteCodeCompilerOptions options) {
        super(options);
    }

    @Override
    Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) {
        Map<String, byte[]> byteCodeMap = new HashMap<>();

        if (program instanceof Swc4jAstModule module) {
            processModuleItems(module.getBody(), options.packagePrefix(), byteCodeMap);
        } else if (program instanceof Swc4jAstScript script) {
            processStmts(script.getBody(), options.packagePrefix(), byteCodeMap);
        }

        return byteCodeMap;
    }

    private void processModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage, Map<String, byte[]> byteCodeMap) {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                processTsModuleDecl(moduleDecl, currentPackage, byteCodeMap);
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstClassDecl classDecl) {
                    processClassDecl(classDecl, currentPackage, byteCodeMap);
                } else if (decl instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
                    processTsModuleDecl(tsModuleDecl, currentPackage, byteCodeMap);
                }
            } else if (item instanceof ISwc4jAstStmt stmt) {
                processStmt(stmt, currentPackage, byteCodeMap);
            }
        }
    }

    private void processStmts(List<ISwc4jAstStmt> stmts, String currentPackage, Map<String, byte[]> byteCodeMap) {
        for (ISwc4jAstStmt stmt : stmts) {
            processStmt(stmt, currentPackage, byteCodeMap);
        }
    }

    private void processStmt(ISwc4jAstStmt stmt, String currentPackage, Map<String, byte[]> byteCodeMap) {
        if (stmt instanceof Swc4jAstTsModuleDecl moduleDecl) {
            processTsModuleDecl(moduleDecl, currentPackage, byteCodeMap);
        } else if (stmt instanceof Swc4jAstClassDecl classDecl) {
            processClassDecl(classDecl, currentPackage, byteCodeMap);
        }
    }

    private void processTsModuleDecl(Swc4jAstTsModuleDecl moduleDecl, String currentPackage, Map<String, byte[]> byteCodeMap) {
        ISwc4jAstTsModuleName moduleName = moduleDecl.getId();
        String namespaceName = getModuleName(moduleName);

        String newPackage = currentPackage.isEmpty() ? namespaceName : currentPackage + "." + namespaceName;

        moduleDecl.getBody().ifPresent(body -> {
            if (body instanceof Swc4jAstTsModuleBlock moduleBlock) {
                processModuleItems(moduleBlock.getBody(), newPackage, byteCodeMap);
            }
        });
    }

    private String getModuleName(ISwc4jAstTsModuleName moduleName) {
        if (moduleName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return moduleName.toString();
    }

    private void processClassDecl(Swc4jAstClassDecl classDecl, String currentPackage, Map<String, byte[]> byteCodeMap) {
        String className = classDecl.getIdent().getSym();
        String fullClassName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = generateClassBytecode(internalClassName, classDecl.getClazz());
            byteCodeMap.put(fullClassName, bytecode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate bytecode for class: " + fullClassName, e);
        }
    }

    private byte[] generateClassBytecode(String internalClassName, Swc4jAstClass clazz) throws IOException {
        ClassWriter classWriter = new ClassWriter(internalClassName);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Generate default constructor
        generateDefaultConstructor(classWriter, cp);

        // Generate methods
        for (ISwc4jAstClassMember member : clazz.getBody()) {
            if (member instanceof Swc4jAstClassMethod method) {
                generateMethod(classWriter, cp, method);
            }
        }

        return classWriter.toByteArray();
    }

    private void generateDefaultConstructor(ClassWriter classWriter, ClassWriter.ConstantPool cp) {
        // Generate: public <init>() { super(); }
        int superCtorRef = cp.addMethodRef("java/lang/Object", "<init>", "()V");

        CodeBuilder code = new CodeBuilder();
        code.aload(0)                    // load this
                .invokespecial(superCtorRef) // call super()
                .returnVoid();               // return

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                "<init>",
                "()V",
                code.toByteArray(),
                1, // max stack
                1  // max locals (this)
        );
    }

    private void generateMethod(ClassWriter classWriter, ClassWriter.ConstantPool cp, Swc4jAstClassMethod method) {
        ISwc4jAstPropName key = method.getKey();
        String methodName = getMethodName(key);
        Swc4jAstFunction function = method.getFunction();

        // Only handle methods with bodies
        function.getBody().ifPresent(body -> {
            try {
                // Determine return type from method body
                ReturnTypeInfo returnTypeInfo = analyzeReturnType(body);
                String descriptor = generateMethodDescriptor(function, returnTypeInfo);
                byte[] code = generateMethodCode(cp, body, returnTypeInfo);

                int accessFlags = 0x0001; // ACC_PUBLIC
                if (method.isStatic()) {
                    accessFlags |= 0x0008; // ACC_STATIC
                }

                int maxStack = returnTypeInfo.maxStack;
                int maxLocals = method.isStatic() ? 0 : 1;

                classWriter.addMethod(accessFlags, methodName, descriptor, code, maxStack, maxLocals);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate method: " + methodName, e);
            }
        });
    }

    private String getMethodName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
    }

    private ReturnTypeInfo analyzeReturnType(Swc4jAstBlockStmt body) {
        // Analyze the return statement to determine the return type
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                var argOpt = returnStmt.getArg();
                if (argOpt.isPresent()) {
                    ISwc4jAstExpr arg = argOpt.get();
                    if (arg instanceof Swc4jAstStr) {
                        return new ReturnTypeInfo(ReturnType.STRING, 1);
                    } else if (arg instanceof Swc4jAstNumber number) {
                        double value = number.getValue();
                        // Check if it's an integer value
                        if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                            return new ReturnTypeInfo(ReturnType.INT, 1);
                        } else {
                            return new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        }
                    }
                }
                return new ReturnTypeInfo(ReturnType.VOID, 0);
            }
        }
        return new ReturnTypeInfo(ReturnType.VOID, 0);
    }

    private byte[] generateMethodCode(ClassWriter.ConstantPool cp, Swc4jAstBlockStmt body, ReturnTypeInfo returnTypeInfo) {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                returnStmt.getArg().ifPresent(arg -> {
                    if (arg instanceof Swc4jAstStr str) {
                        // ldc with string constant
                        int stringIndex = cp.addString(str.getValue());
                        code.ldc(stringIndex);
                    } else if (arg instanceof Swc4jAstNumber number) {
                        double value = number.getValue();
                        if (returnTypeInfo.type == ReturnType.INT) {
                            code.iconst((int) value);
                        } else {
                            code.dconst(value);
                        }
                    }
                });

                // Generate appropriate return instruction
                switch (returnTypeInfo.type) {
                    case VOID -> code.returnVoid();
                    case INT -> code.ireturn();
                    case DOUBLE -> code.dreturn();
                    case STRING -> code.areturn();
                }
            }
        }

        return code.toByteArray();
    }

    private String generateMethodDescriptor(Swc4jAstFunction function, ReturnTypeInfo returnTypeInfo) {
        // For now, assume no parameters
        String returnDescriptor = switch (returnTypeInfo.type) {
            case VOID -> "V";
            case INT -> "I";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
        };
        return "()" + returnDescriptor;
    }

    private enum ReturnType {
        VOID, INT, DOUBLE, STRING
    }

    private record ReturnTypeInfo(ReturnType type, int maxStack) {
    }
}
