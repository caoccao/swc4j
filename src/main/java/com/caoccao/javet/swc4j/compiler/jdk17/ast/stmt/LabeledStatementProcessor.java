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

import com.caoccao.javet.swc4j.ast.stmt.*;
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

        // Dispatch based on body type
        var body = labeledStmt.getBody();

        if (body instanceof Swc4jAstForStmt forStmt) {
            // Generate labeled for loop
            compiler.getForStatementProcessor().generate(code, classWriter, forStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstForInStmt forInStmt) {
            // Generate labeled for-in loop
            compiler.getForInStatementProcessor().generate(code, classWriter, forInStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstForOfStmt forOfStmt) {
            // Generate labeled for-of loop
            compiler.getForOfStatementProcessor().generate(code, classWriter, forOfStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstWhileStmt whileStmt) {
            // Generate labeled while loop
            compiler.getWhileStatementProcessor().generate(code, classWriter, whileStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstDoWhileStmt doWhileStmt) {
            // Generate labeled do-while loop
            compiler.getDoWhileStatementProcessor().generate(code, classWriter, doWhileStmt, labelName, returnTypeInfo);
        } else if (body instanceof Swc4jAstSwitchStmt switchStmt) {
            // Generate labeled switch statement
            compiler.getSwitchStatementProcessor().generate(code, classWriter, switchStmt, labelName, returnTypeInfo);
        } else {
            // For other statement types, just generate the body
            // (labels on non-loop statements are allowed but don't affect code generation)
            compiler.getStatementProcessor().generate(code, classWriter, body, returnTypeInfo);
        }
    }
}
