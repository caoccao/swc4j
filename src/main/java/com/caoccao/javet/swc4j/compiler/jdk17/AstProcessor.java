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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class AstProcessor {
    private AstProcessor() {
    }

    public static void processModuleItems(
            List<ISwc4jAstModuleItem> items,
            String currentPackage,
            Map<String, byte[]> byteCodeMap,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                processTsModuleDecl(moduleDecl, currentPackage, byteCodeMap, options);
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstClassDecl classDecl) {
                    processClassDecl(classDecl, currentPackage, byteCodeMap, options);
                } else if (decl instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
                    processTsModuleDecl(tsModuleDecl, currentPackage, byteCodeMap, options);
                }
            } else if (item instanceof ISwc4jAstStmt stmt) {
                processStmt(stmt, currentPackage, byteCodeMap, options);
            }
        }
    }

    public static void processStmts(
            List<ISwc4jAstStmt> stmts,
            String currentPackage,
            Map<String, byte[]> byteCodeMap,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : stmts) {
            processStmt(stmt, currentPackage, byteCodeMap, options);
        }
    }

    public static void processStmt(
            ISwc4jAstStmt stmt,
            String currentPackage,
            Map<String, byte[]> byteCodeMap,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstTsModuleDecl moduleDecl) {
            processTsModuleDecl(moduleDecl, currentPackage, byteCodeMap, options);
        } else if (stmt instanceof Swc4jAstClassDecl classDecl) {
            processClassDecl(classDecl, currentPackage, byteCodeMap, options);
        }
    }

    public static void processTsModuleDecl(
            Swc4jAstTsModuleDecl moduleDecl,
            String currentPackage,
            Map<String, byte[]> byteCodeMap,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        ISwc4jAstTsModuleName moduleName = moduleDecl.getId();
        String namespaceName = getModuleName(moduleName);

        String newPackage = currentPackage.isEmpty() ? namespaceName : currentPackage + "." + namespaceName;

        var bodyOpt = moduleDecl.getBody();
        if (bodyOpt.isPresent() && bodyOpt.get() instanceof Swc4jAstTsModuleBlock moduleBlock) {
            processModuleItems(moduleBlock.getBody(), newPackage, byteCodeMap, options);
        }
    }

    public static String getModuleName(ISwc4jAstTsModuleName moduleName) {
        if (moduleName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return moduleName.toString();
    }

    public static void processClassDecl(
            Swc4jAstClassDecl classDecl,
            String currentPackage,
            Map<String, byte[]> byteCodeMap,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        String className = classDecl.getIdent().getSym();
        String fullClassName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = CodeGenerator.generateClassBytecode(internalClassName, classDecl.getClazz(), options);
            byteCodeMap.put(fullClassName, bytecode);
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException("Failed to generate bytecode for class: " + fullClassName, e);
        }
    }
}
