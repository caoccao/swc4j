/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.ScopedStandaloneFunctionRegistry;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;

/**
 * Collects standalone function declarations and determines the dummy class name for each package.
 * <p>
 * Standalone functions (not inside a class) are compiled into a dummy class named `$`.
 * If `$` already exists as a class name, `$1`, `$2`, etc. are tried until an available name is found.
 * <p>
 * This collector is stateless - all state is stored in the scoped registry in memory.
 */
public final class FunctionDeclarationProcessor extends BaseAstProcessor<Swc4jAstFnDecl> {

    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public FunctionDeclarationProcessor(ByteCodeCompiler compiler) {
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

    /**
     * Collects function declarations from module items and registers them in the function registry.
     *
     * @param items          the list of module items to process
     * @param currentPackage the current package context
     */
    public void collectFromModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage) {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                ISwc4jAstTsModuleName moduleName = moduleDecl.getId();
                String namespaceName = moduleName.toString();
                String newPackage = currentPackage.isEmpty() ? namespaceName : currentPackage + "." + namespaceName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstFnDecl fnDecl) {
                    getRegistry().addFunction(currentPackage, fnDecl);
                } else if (decl instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
                    ISwc4jAstTsModuleName moduleName = tsModuleDecl.getId();
                    String namespaceName = moduleName.toString();
                    String newPackage = currentPackage.isEmpty() ? namespaceName : currentPackage + "." + namespaceName;

                    if (tsModuleDecl.getBody().isPresent() && tsModuleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                        collectFromModuleItems(block.getBody(), newPackage);
                    }
                }
            } else if (item instanceof Swc4jAstFnDecl fnDecl) {
                getRegistry().addFunction(currentPackage, fnDecl);
            }
        }
    }

    /**
     * Collects function declarations from statements and registers them in the function registry.
     *
     * @param stmts          the list of statements to process
     * @param currentPackage the current package context
     */
    public void collectFromStmts(List<ISwc4jAstStmt> stmts, String currentPackage) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstFnDecl fnDecl) {
                getRegistry().addFunction(currentPackage, fnDecl);
            } else if (stmt instanceof Swc4jAstTsModuleDecl moduleDecl) {
                ISwc4jAstTsModuleName moduleName = moduleDecl.getId();
                String namespaceName = moduleName.toString();
                String newPackage = currentPackage.isEmpty() ? namespaceName : currentPackage + "." + namespaceName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            }
        }
    }

    /**
     * Determines the dummy class names for each package that has standalone functions.
     * Must be called after all class names have been collected.
     */
    public void determineDummyClassNames() {
        ScopedStandaloneFunctionRegistry registry = getRegistry();
        for (String packageName : registry.getPackagesWithFunctions()) {
            String dummyClassName = findAvailableDummyClassName(packageName);
            registry.setDummyClassName(packageName, dummyClassName);
        }
    }

    private String findAvailableDummyClassName(String packageName) {
        String baseName = "$";
        String fullName = packageName.isEmpty() ? baseName : packageName + "." + baseName;

        // Check if $ is available
        if (!isClassNameTaken(fullName)) {
            return baseName;
        }

        // Try $1, $2, $3, etc.
        int suffix = 1;
        while (true) {
            baseName = "$" + suffix;
            fullName = packageName.isEmpty() ? baseName : packageName + "." + baseName;
            if (!isClassNameTaken(fullName)) {
                return baseName;
            }
            suffix++;
        }
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, Swc4jAstFnDecl fnDecl, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var registry = getRegistry();
        if (!registry.hasFunctions()) {
            return;
        }

        for (String packageName : registry.getPackagesWithFunctions()) {
            String dummyClassName = registry.getDummyClassName(packageName);
            List<Swc4jAstFnDecl> functions = registry.getFunctions(packageName);

            if (functions.isEmpty() || dummyClassName == null) {
                continue;
            }

            String fullClassName = packageName.isEmpty() ? dummyClassName : packageName + "." + dummyClassName;
            String internalClassName = fullClassName.replace('.', '/');

            try {
                classWriter = new ClassWriter(internalClassName, "java/lang/Object");

                // Generate default constructor
                generateDefaultConstructor(classWriter, "java/lang/Object");

                // Generate each function as a static method
                for (Swc4jAstFnDecl childFnDecl : functions) {
                    generateStaticMethod(classWriter, childFnDecl, internalClassName);
                }
                compiler.getMemory().getByteCodeMap().put(fullClassName, classWriter.toByteArray());
            } catch (IOException e) {
                throw new Swc4jByteCodeCompilerException(
                        getSourceCode(),
                        functions.get(0),
                        "Failed to generate bytecode for standalone functions in: " + fullClassName, e);
            }
        }
    }

    private void generateDefaultConstructor(ClassWriter classWriter, String superClassInternalName) {
        var cp = classWriter.getConstantPool();
        // Generate: public <init>() { super(); }
        int superCtorRef = cp.addMethodRef(superClassInternalName, "<init>", "()V");

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

    private void generateStaticMethod(ClassWriter classWriter, Swc4jAstFnDecl fnDecl, String internalClassName) throws Swc4jByteCodeCompilerException {
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
                compiler.getStatementProcessor().generate(code, classWriter, stmt, returnTypeInfo);
            }

            // Add return if needed
            addReturnIfNeeded(code, returnTypeInfo);

            int maxLocals = context.getLocalVariableTable().getMaxLocals();

            // ACC_PUBLIC | ACC_STATIC = 0x0009
            int accessFlags = 0x0009;
            boolean isStatic = true;

            // Generate stack map table for methods with branches (required for Java 7+)
            var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, internalClassName, descriptor, classWriter.getConstantPool());
            var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();

            classWriter.addMethod(accessFlags, methodName, descriptor, code.toByteArray(), 10, maxLocals,
                    null, null, stackMapTable, exceptionTable);
        } finally {
            if (methodTypeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
            }
        }
    }

    private ScopedStandaloneFunctionRegistry getRegistry() {
        return compiler.getMemory().getScopedStandaloneFunctionRegistry();
    }

    private String getReturnDescriptor(ReturnTypeInfo returnTypeInfo) {
        if (returnTypeInfo.descriptor() != null) {
            return returnTypeInfo.descriptor();
        }
        String primitiveDescriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
        return primitiveDescriptor != null ? primitiveDescriptor : "V";
    }

    private boolean isClassNameTaken(String fullClassName) {
        // Check if this class name is already registered
        String simpleName = fullClassName.contains(".") ?
                fullClassName.substring(fullClassName.lastIndexOf('.') + 1) : fullClassName;
        return compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName) != null;
    }
}
