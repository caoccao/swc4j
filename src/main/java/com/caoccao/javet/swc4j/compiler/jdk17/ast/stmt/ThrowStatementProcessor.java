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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstThrowStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for throw statements.
 * <p>
 * Generates bytecode for JavaScript/TypeScript throw statements.
 * <p>
 * Bytecode pattern:
 * <pre>
 * [evaluate exception expression]
 * athrow
 * </pre>
 */
public final class ThrowStatementProcessor extends BaseAstProcessor<Swc4jAstThrowStmt> {
    public ThrowStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for a throw statement.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param throwStmt      the throw statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstThrowStmt throwStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Generate code to evaluate the exception expression
        // This pushes the exception object onto the stack
        compiler.getExpressionProcessor().generate(code, classWriter, throwStmt.getArg(), null);

        // Throw the exception
        code.athrow();
    }
}
