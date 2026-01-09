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
import com.caoccao.javet.swc4j.compiler.jdk17.AstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeAliasCollector;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.HashMap;
import java.util.Map;

public final class ByteCodeCompiler17 extends ByteCodeCompiler {
    ByteCodeCompiler17(ByteCodeCompilerOptions options) {
        super(options);
    }

    @Override
    Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException {
        Map<String, byte[]> byteCodeMap = new HashMap<>();

        if (program instanceof Swc4jAstModule module) {
            // First pass: collect type aliases
            TypeAliasCollector.collectFromModuleItems(module.getBody(), options);
            // Second pass: generate bytecode
            AstProcessor.processModuleItems(module.getBody(), options.packagePrefix(), byteCodeMap, options);
        } else if (program instanceof Swc4jAstScript script) {
            // First pass: collect type aliases
            TypeAliasCollector.collectFromStmts(script.getBody(), options);
            // Second pass: generate bytecode
            AstProcessor.processStmts(script.getBody(), options.packagePrefix(), byteCodeMap, options);
        }

        return byteCodeMap;
    }
}
