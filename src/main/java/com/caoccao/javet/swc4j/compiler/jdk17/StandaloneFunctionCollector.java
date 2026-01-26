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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.ScopedStandaloneFunctionRegistry;

import java.util.List;

/**
 * Collects standalone function declarations and determines the dummy class name for each package.
 * <p>
 * Standalone functions (not inside a class) are compiled into a dummy class named `$`.
 * If `$` already exists as a class name, `$1`, `$2`, etc. are tried until an available name is found.
 * <p>
 * This collector is stateless - all state is stored in the scoped registry in memory.
 */
public final class StandaloneFunctionCollector {
    private final ByteCodeCompiler compiler;

    public StandaloneFunctionCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

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

    private ScopedStandaloneFunctionRegistry getRegistry() {
        return compiler.getMemory().getScopedStandaloneFunctionRegistry();
    }

    private boolean isClassNameTaken(String fullClassName) {
        // Check if this class name is already registered
        String simpleName = fullClassName.contains(".") ?
                fullClassName.substring(fullClassName.lastIndexOf('.') + 1) : fullClassName;
        return compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName) != null;
    }
}
