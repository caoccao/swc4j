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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Base class for AST node processors that generate JVM bytecode.
 *
 * @param <AST> the type of AST node this processor handles
 */
public abstract class BaseAstProcessor<AST extends ISwc4jAst> {
    /**
     * The bytecode compiler instance.
     */
    protected final ByteCodeCompiler compiler;

    /**
     * Constructs a new BaseAstProcessor.
     *
     * @param compiler the bytecode compiler instance
     */
    public BaseAstProcessor(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Generates bytecode for the given AST node.
     *
     * @param code           the code builder for generating bytecode
     * @param classWriter    the class writer for constant pool access
     * @param ast            the AST node to process
     * @param returnTypeInfo type hint for the expected return type (nullable)
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public abstract void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            AST ast,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException;

    /**
     * Generates bytecode for a list of AST nodes.
     *
     * @param code           the code builder for generating bytecode
     * @param classWriter    the class writer for constant pool access
     * @param asts           the list of AST nodes to process
     * @param returnTypeInfo type hint for the expected return type (nullable)
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            List<AST> asts,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        for (AST ast : asts) {
            generate(code, classWriter, ast, returnTypeInfo);
        }
    }

    /**
     * Gets the source code being compiled.
     *
     * @return the source code
     */
    protected String getSourceCode() {
        return compiler.getMemory().getScopedSourceCode().getSourceCode();
    }
}
