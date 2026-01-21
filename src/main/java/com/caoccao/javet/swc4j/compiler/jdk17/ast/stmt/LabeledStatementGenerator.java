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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstDoWhileStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstLabeledStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstWhileStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
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
public final class LabeledStatementGenerator {
    private LabeledStatementGenerator() {
    }

    /**
     * Generate bytecode for a labeled statement.
     *
     * @param compiler       the compiler
     * @param code           the code builder
     * @param cp             the constant pool
     * @param labeledStmt    the labeled statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstLabeledStmt labeledStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Extract label name
        String labelName = labeledStmt.getLabel().getSym();

        // Dispatch based on body type
        var body = labeledStmt.getBody();

        if (body instanceof Swc4jAstForStmt forStmt) {
            // Generate labeled for loop
            ForStatementGenerator.generate(compiler, code, cp, forStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstWhileStmt whileStmt) {
            // Generate labeled while loop
            WhileStatementGenerator.generate(compiler, code, cp, whileStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstDoWhileStmt doWhileStmt) {
            // Generate labeled do-while loop
            DoWhileStatementGenerator.generate(compiler, code, cp, doWhileStmt, labelName, returnTypeInfo);
        } else {
            // For other statement types, just generate the body
            // (labels on non-loop statements are allowed but don't affect code generation)
            StatementGenerator.generate(compiler, code, cp, body, returnTypeInfo);
        }
    }
}
