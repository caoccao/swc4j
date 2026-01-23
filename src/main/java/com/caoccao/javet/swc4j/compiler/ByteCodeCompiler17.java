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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ByteCodeCompiler17 extends ByteCodeCompiler {
    ByteCodeCompiler17(ByteCodeCompilerOptions options) {
        super(options);
    }

    @Override
    void compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException {
        // Enter a new scope for this file
        memory.enterScope();
        try {
            if (program instanceof Swc4jAstModule module) {
                // First pass: process imports
                importDeclProcessor.processImports(module.getBody());
                // Second pass: collect type aliases and type declarations
                typeAliasCollector.collectFromModuleItems(module.getBody());
                classCollector.collectFromModuleItems(module.getBody(), options.packagePrefix());
                enumCollector.collectFromModuleItems(module.getBody(), options.packagePrefix());
                // Third pass: generate bytecode
                astProcessor.processModuleItems(module.getBody(), options.packagePrefix());
            } else if (program instanceof Swc4jAstScript script) {
                // First pass: process imports (scripts typically don't have imports, but support it anyway)
                importDeclProcessor.processImports(script.getBody());
                // Second pass: collect type aliases and type declarations
                typeAliasCollector.collectFromStmts(script.getBody());
                classCollector.collectFromStmts(script.getBody(), options.packagePrefix());
                enumCollector.collectFromStmts(script.getBody(), options.packagePrefix());
                // Third pass: generate bytecode
                astProcessor.processStmts(script.getBody(), options.packagePrefix());
            }
        } finally {
            // Always exit the scope, even if an exception occurs
            memory.exitScope();
        }
    }
}
