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

package com.caoccao.javet.swc4j.compiler.jdk17.ast;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsEnumDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsInterfaceDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;

public final class AstProcessor {
    private final ByteCodeCompiler compiler;

    public AstProcessor(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    private String getCurrentPackage() {
        return compiler.getMemory().getScopedPackage().getCurrentPackage();
    }

    private String getSourceCode() {
        return compiler.getMemory().getScopedSourceCode().getSourceCode();
    }

    public String getModuleName(ISwc4jAstTsModuleName moduleName) {
        if (moduleName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return moduleName.toString();
    }

    public void processEnumDecl(Swc4jAstTsEnumDecl enumDecl) throws Swc4jByteCodeCompilerException {
        String currentPackage = getCurrentPackage();
        String enumName = enumDecl.getId().getSym();
        String fullClassName = currentPackage.isEmpty() ? enumName : currentPackage + "." + enumName;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = compiler.getEnumGenerator().generateBytecode(internalClassName, enumDecl);
            if (bytecode != null) {  // null means ambient declaration
                compiler.getMemory().getByteCodeMap().put(fullClassName, bytecode);
            }
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), enumDecl, "Failed to generate bytecode for enum: " + fullClassName, e);
        }
    }

    public void processInterfaceDecl(Swc4jAstTsInterfaceDecl interfaceDecl) throws Swc4jByteCodeCompilerException {
        String currentPackage = getCurrentPackage();
        String interfaceName = interfaceDecl.getId().getSym();
        String fullClassName = currentPackage.isEmpty() ? interfaceName : currentPackage + "." + interfaceName;
        String internalClassName = fullClassName.replace('.', '/');

        byte[] bytecode = compiler.getTsInterfaceGenerator().generateBytecode(internalClassName, interfaceDecl);
        if (bytecode != null) {  // null means ambient declaration
            compiler.getMemory().getByteCodeMap().put(fullClassName, bytecode);
        }
    }

    public void processModuleItems(List<ISwc4jAstModuleItem> items) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                processTsModuleDecl(moduleDecl);
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstClassDecl classDecl) {
                    compiler.getClassDeclGenerator().generate(null, null, classDecl, null);
                } else if (decl instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
                    processTsModuleDecl(tsModuleDecl);
                } else if (decl instanceof Swc4jAstTsEnumDecl enumDecl) {
                    processEnumDecl(enumDecl);
                } else if (decl instanceof Swc4jAstTsInterfaceDecl interfaceDecl) {
                    processInterfaceDecl(interfaceDecl);
                }
            } else if (item instanceof ISwc4jAstStmt stmt) {
                processStmt(stmt);
            }
        }
    }

    public void processStmt(ISwc4jAstStmt stmt) throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstTsModuleDecl moduleDecl) {
            processTsModuleDecl(moduleDecl);
        } else if (stmt instanceof Swc4jAstClassDecl classDecl) {
            compiler.getClassDeclGenerator().generate(null, null, classDecl, null);
        } else if (stmt instanceof Swc4jAstTsEnumDecl enumDecl) {
            processEnumDecl(enumDecl);
        } else if (stmt instanceof Swc4jAstTsInterfaceDecl interfaceDecl) {
            processInterfaceDecl(interfaceDecl);
        }
    }

    public void processStmts(List<ISwc4jAstStmt> stmts) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : stmts) {
            processStmt(stmt);
        }
    }

    public void processTsModuleDecl(Swc4jAstTsModuleDecl moduleDecl) throws Swc4jByteCodeCompilerException {
        ISwc4jAstTsModuleName moduleName = moduleDecl.getId();
        String namespaceName = getModuleName(moduleName);

        // Enter the new package scope
        compiler.getMemory().getScopedPackage().enterScope(namespaceName);

        try {
            var bodyOpt = moduleDecl.getBody();
            if (bodyOpt.isPresent() && bodyOpt.get() instanceof Swc4jAstTsModuleBlock moduleBlock) {
                processModuleItems(moduleBlock.getBody());
            }
        } finally {
            // Exit the package scope
            compiler.getMemory().getScopedPackage().exitScope();
        }
    }
}
