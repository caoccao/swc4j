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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstLabeledStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for labeled statements.
 * <p>
 * Labeled statements allow break and continue to target specific loops.
 * <p>
 * Example:
 * <pre>
 * outer: for (let i = 0; i &lt; 10; i++) {
 *   for (let j = 0; j &lt; 10; j++) {
 *     if (i * j &gt; 50) break outer;
 *   }
 * }
 * </pre>
 */
public final class LabeledStatementProcessor extends BaseAstProcessor<Swc4jAstLabeledStmt> {
    /**
     * Instantiates a new Labeled statement processor.
     *
     * @param compiler the compiler
     */
    public LabeledStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for a labeled statement.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param labeledStmt    the labeled statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstLabeledStmt labeledStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Extract label name
        String labelName = labeledStmt.getLabel().getSym();
        var body = labeledStmt.getBody();

        // Set pending label on context for loop/switch processors to consume
        compiler.getMemory().getCompilationContext().pushPendingLabelName(labelName);

        // Generate the body - loop/switch processors will consume the label
        compiler.getStatementProcessor().generate(code, classWriter, body, returnTypeInfo);
    }
}
