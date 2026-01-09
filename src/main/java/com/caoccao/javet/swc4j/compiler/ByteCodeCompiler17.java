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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsQualifiedName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;

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
            // First pass: collect type aliases
            collectTypeAliases(module.getBody());
            // Second pass: generate bytecode
            processModuleItems(module.getBody(), options.getPackagePrefix(), byteCodeMap);
        } else if (program instanceof Swc4jAstScript script) {
            // First pass: collect type aliases
            collectTypeAliasesFromStmts(script.getBody());
            // Second pass: generate bytecode
            processStmts(script.getBody(), options.getPackagePrefix(), byteCodeMap);
        }

        return byteCodeMap;
    }

    private void collectTypeAliases(List<ISwc4jAstModuleItem> items) {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                    processTypeAlias(typeAliasDecl);
                }
            } else if (item instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                processTypeAlias(typeAliasDecl);
            }
        }
    }

    private void collectTypeAliasesFromStmts(List<ISwc4jAstStmt> stmts) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                processTypeAlias(typeAliasDecl);
            }
        }
    }

    private void processTypeAlias(Swc4jAstTsTypeAliasDecl typeAliasDecl) {
        String aliasName = typeAliasDecl.getId().getSym();
        ISwc4jAstTsType typeAnn = typeAliasDecl.getTypeAnn();

        if (typeAnn instanceof Swc4jAstTsTypeRef typeRef) {
            ISwc4jAstTsEntityName entityName = typeRef.getTypeName();

            String targetType = resolveEntityName(entityName);
            // Resolve the target type if it's also an alias
            String resolvedType = options.getTypeAliasMap().getOrDefault(targetType, targetType);
            options.getTypeAliasMap().put(aliasName, resolvedType);
        }
    }

    private String resolveEntityName(ISwc4jAstTsEntityName entityName) {
        if (entityName instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (entityName instanceof Swc4jAstTsQualifiedName qualifiedName) {
            String left = resolveEntityName(qualifiedName.getLeft());
            String right = qualifiedName.getRight().getSym();
            return left + "." + right;
        }
        return entityName.toString();
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
                CompilationContext context = new CompilationContext();

                // Analyze variable declarations and infer types
                analyzeVariableDeclarations(body, context);

                // Determine return type from method body
                ReturnTypeInfo returnTypeInfo = analyzeReturnType(body, context);
                String descriptor = generateMethodDescriptor(function, returnTypeInfo);
                byte[] code = generateMethodCode(cp, body, returnTypeInfo, context);

                int accessFlags = 0x0001; // ACC_PUBLIC
                if (method.isStatic()) {
                    accessFlags |= 0x0008; // ACC_STATIC
                }

                int maxStack = Math.max(returnTypeInfo.maxStack, 2); // Ensure enough for binary operations
                int maxLocals = context.localVariableTable.getMaxLocals();

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

    private void analyzeVariableDeclarations(Swc4jAstBlockStmt body, CompilationContext context) {
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
                    ISwc4jAstPat name = declarator.getName();
                    if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                        String varName = bindingIdent.getId().getSym();
                        String varType = extractType(bindingIdent, declarator.getInit(), context);
                        context.localVariableTable.allocateVariable(varName, varType);
                        context.inferredTypes.put(varName, varType);
                    }
                }
            }
        }
    }

    private String extractType(Swc4jAstBindingIdent bindingIdent, java.util.Optional<ISwc4jAstExpr> init, CompilationContext context) {
        // Check for explicit type annotation
        var typeAnn = bindingIdent.getTypeAnn();
        if (typeAnn.isPresent()) {
            ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
            if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
                ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
                if (entityName instanceof Swc4jAstIdent ident) {
                    String typeName = ident.getSym();
                    return mapTypeNameToDescriptor(typeName);
                }
            }
        }

        // Type inference from initializer
        if (init.isPresent()) {
            return inferTypeFromExpr(init.get(), context);
        }

        return "Ljava/lang/Object;"; // Default
    }

    private String mapTypeNameToDescriptor(String typeName) {
        // Resolve type alias first
        String resolvedType = options.getTypeAliasMap().getOrDefault(typeName, typeName);

        return switch (resolvedType) {
            case "int" -> "I";
            case "double" -> "D";
            case "java.lang.String", "String" -> "Ljava/lang/String;";
            case "void" -> "V";
            default -> "L" + resolvedType.replace('.', '/') + ";";
        };
    }

    private String inferTypeFromExpr(ISwc4jAstExpr expr, CompilationContext context) {
        if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                return "I";
            }
            return "D";
        } else if (expr instanceof Swc4jAstStr) {
            return "Ljava/lang/String;";
        } else if (expr instanceof Swc4jAstIdent ident) {
            return context.inferredTypes.getOrDefault(ident.getSym(), "Ljava/lang/Object;");
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            if (binExpr.getOp() == Swc4jAstBinaryOp.Add) {
                String leftType = inferTypeFromExpr(binExpr.getLeft(), context);
                String rightType = inferTypeFromExpr(binExpr.getRight(), context);
                // String concatenation
                if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                    return "Ljava/lang/String;";
                }
                // Numeric addition
                return leftType;
            }
        }
        return "Ljava/lang/Object;";
    }

    private ReturnTypeInfo analyzeReturnType(Swc4jAstBlockStmt body, CompilationContext context) {
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
                        if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                            return new ReturnTypeInfo(ReturnType.INT, 1);
                        } else {
                            return new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        }
                    } else if (arg instanceof Swc4jAstIdent ident) {
                        String type = context.inferredTypes.get(ident.getSym());
                        if ("I".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.INT, 1);
                        } else if ("D".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        } else if ("Ljava/lang/String;".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.STRING, 1);
                        }
                    }
                }
                return new ReturnTypeInfo(ReturnType.VOID, 0);
            }
        }
        return new ReturnTypeInfo(ReturnType.VOID, 0);
    }

    private byte[] generateMethodCode(ClassWriter.ConstantPool cp, Swc4jAstBlockStmt body, ReturnTypeInfo returnTypeInfo, CompilationContext context) {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                generateVarDecl(code, cp, varDecl, context);
            } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                returnStmt.getArg().ifPresent(arg -> {
                    generateExpr(code, cp, arg, context);
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

    private void generateVarDecl(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstVarDecl varDecl, CompilationContext context) {
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.localVariableTable.getVariable(varName);

                declarator.getInit().ifPresent(init -> {
                    generateExpr(code, cp, init, context);

                    // Store the value in the local variable
                    if ("I".equals(localVar.type())) {
                        code.istore(localVar.index());
                    } else if ("D".equals(localVar.type())) {
                        // code.dstore(localVar.index());
                    } else {
                        code.astore(localVar.index());
                    }
                });
            }
        }
    }

    private void generateExpr(CodeBuilder code, ClassWriter.ConstantPool cp, ISwc4jAstExpr expr, CompilationContext context) {
        if (expr instanceof Swc4jAstStr str) {
            int stringIndex = cp.addString(str.getValue());
            code.ldc(stringIndex);
        } else if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                code.iconst((int) value);
            } else {
                code.dconst(value);
            }
        } else if (expr instanceof Swc4jAstIdent ident) {
            String varName = ident.getSym();
            LocalVariable localVar = context.localVariableTable.getVariable(varName);
            if (localVar != null) {
                if ("I".equals(localVar.type())) {
                    code.iload(localVar.index());
                } else if ("D".equals(localVar.type())) {
                    // code.dload(localVar.index());
                } else {
                    code.aload(localVar.index());
                }
            }
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            generateBinExpr(code, cp, binExpr, context);
        }
    }

    private void generateBinExpr(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstBinExpr binExpr, CompilationContext context) {
        Swc4jAstBinaryOp op = binExpr.getOp();

        if (op == Swc4jAstBinaryOp.Add) {
            String leftType = inferTypeFromExpr(binExpr.getLeft(), context);
            String rightType = inferTypeFromExpr(binExpr.getRight(), context);

            // Check if this is string concatenation
            if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                generateStringConcat(code, cp, binExpr.getLeft(), binExpr.getRight(), leftType, rightType, context);
            } else {
                // Generate left operand
                generateExpr(code, cp, binExpr.getLeft(), context);

                // Generate right operand
                generateExpr(code, cp, binExpr.getRight(), context);

                // Determine type and generate appropriate add instruction
                if ("I".equals(leftType)) {
                    code.iadd();
                } else if ("D".equals(leftType)) {
                    code.dadd();
                }
            }
        }
    }

    private void generateStringConcat(CodeBuilder code, ClassWriter.ConstantPool cp,
                                     ISwc4jAstExpr left, ISwc4jAstExpr right,
                                     String leftType, String rightType,
                                     CompilationContext context) {
        // Use StringBuilder for string concatenation
        // new StringBuilder
        int stringBuilderClass = cp.addClass("java/lang/StringBuilder");
        int stringBuilderInit = cp.addMethodRef("java/lang/StringBuilder", "<init>", "()V");
        int appendString = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int appendInt = cp.addMethodRef("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
        int toString = cp.addMethodRef("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

        code.newInstance(stringBuilderClass)
            .dup()
            .invokespecial(stringBuilderInit);

        // Append left operand
        generateExpr(code, cp, left, context);
        if ("Ljava/lang/String;".equals(leftType)) {
            code.invokevirtual(appendString);
        } else if ("I".equals(leftType)) {
            code.invokevirtual(appendInt);
        }

        // Append right operand
        generateExpr(code, cp, right, context);
        if ("Ljava/lang/String;".equals(rightType)) {
            code.invokevirtual(appendString);
        } else if ("I".equals(rightType)) {
            code.invokevirtual(appendInt);
        }

        // Call toString()
        code.invokevirtual(toString);
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

    private static class LocalVariableTable {
        private final Map<String, LocalVariable> variables = new HashMap<>();
        private int nextIndex = 1; // 0 is reserved for 'this'

        int allocateVariable(String name, String type) {
            int index = nextIndex;
            variables.put(name, new LocalVariable(name, type, index));
            // Doubles and longs take 2 slots
            nextIndex += (type.equals("D") || type.equals("J")) ? 2 : 1;
            return index;
        }

        LocalVariable getVariable(String name) {
            return variables.get(name);
        }

        int getMaxLocals() {
            return nextIndex;
        }
    }

    private record LocalVariable(String name, String type, int index) {
    }

    private static class CompilationContext {
        private final LocalVariableTable localVariableTable = new LocalVariableTable();
        private final Map<String, String> inferredTypes = new HashMap<>();
    }
}
