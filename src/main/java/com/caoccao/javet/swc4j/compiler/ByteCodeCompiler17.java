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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.List;

public final class ByteCodeCompiler17 extends ByteCodeCompiler {
    ByteCodeCompiler17(ByteCodeCompilerOptions options) {
        super(options);
    }

    @Override
    void compileProgram(String code, ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException {
        // Store source code for error reporting
        memory.getScopedSourceCode().setSourceCode(code);
        // Initialize package scope with package prefix if present
        String packagePrefix = options.packagePrefix();
        if (packagePrefix != null && !packagePrefix.isEmpty()) {
            memory.getScopedPackage().enterScope(packagePrefix);
        }
        // Enter a new scope for this file
        memory.enterScope();
        try {
            if (program instanceof Swc4jAstModule module) {
                // First pass: process imports
                importDeclProcessor.processImports(module.getBody());
                // Second pass: collect type aliases and type declarations
                typeAliasCollector.collectFromModuleItems(module.getBody());
                classCollector.collectFromModuleItems(module.getBody(), packagePrefix);
                enumCollector.collectFromModuleItems(module.getBody(), packagePrefix);
                tsInterfaceCollector.collectFromModuleItems(module.getBody(), packagePrefix);
                // Collect standalone functions
                standaloneFunctionCollector.collectFromModuleItems(module.getBody(), packagePrefix);
                // Determine dummy class names after all class names are known
                standaloneFunctionCollector.determineDummyClassNames();
                // Third pass: generate bytecode for classes/enums
                astProcessor.processModuleItems(module.getBody());
                // Generate bytecode for standalone functions
                generateStandaloneFunctions();
            } else if (program instanceof Swc4jAstScript script) {
                // First pass: process imports (scripts typically don't have imports, but support it anyway)
                importDeclProcessor.processImports(script.getBody());
                // Second pass: collect type aliases and type declarations
                typeAliasCollector.collectFromStmts(script.getBody());
                classCollector.collectFromStmts(script.getBody(), packagePrefix);
                enumCollector.collectFromStmts(script.getBody(), packagePrefix);
                tsInterfaceCollector.collectFromStmts(script.getBody(), packagePrefix);
                // Collect standalone functions
                standaloneFunctionCollector.collectFromStmts(script.getBody(), packagePrefix);
                // Determine dummy class names after all class names are known
                standaloneFunctionCollector.determineDummyClassNames();
                // Third pass: generate bytecode for classes/enums
                astProcessor.processStmts(script.getBody());
                // Generate bytecode for standalone functions
                generateStandaloneFunctions();
            }
        } finally {
            // Always exit the scope, even if an exception occurs
            memory.exitScope();
            // Exit package prefix scope if it was entered
            if (packagePrefix != null && !packagePrefix.isEmpty()) {
                memory.getScopedPackage().exitScope();
            }
        }
    }

    private void generateStandaloneFunctions() throws Swc4jByteCodeCompilerException {
        var registry = memory.getScopedStandaloneFunctionRegistry();
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
                byte[] bytecode = standaloneFunctionGenerator.generateBytecode(internalClassName, functions);
                memory.getByteCodeMap().put(fullClassName, bytecode);
            } catch (IOException e) {
                throw new Swc4jByteCodeCompilerException(
                        memory.getScopedSourceCode().getSourceCode(),
                        functions.get(0),
                        "Failed to generate bytecode for standalone functions in: " + fullClassName, e);
            }
        }
    }
}
