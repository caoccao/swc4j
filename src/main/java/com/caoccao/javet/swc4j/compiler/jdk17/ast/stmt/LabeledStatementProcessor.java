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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
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
     * Checks whether the labeled statement is one that consumes the pending label itself, which is
     * the case for every loop and for a switch statement. Every other statement, a labeled block in
     * particular, is handled here instead.
     *
     * @param body the labeled statement body
     * @return true if the body consumes the pending label
     */
    private boolean consumesPendingLabel(ISwc4jAstStmt body) {
        return body instanceof Swc4jAstForStmt
                || body instanceof Swc4jAstForInStmt
                || body instanceof Swc4jAstForOfStmt
                || body instanceof Swc4jAstWhileStmt
                || body instanceof Swc4jAstDoWhileStmt
                || body instanceof Swc4jAstSwitchStmt;
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
        CompilationContext context = compiler.getMemory().getCompilationContext();

        if (consumesPendingLabel(body)) {
            // Set pending label on context for loop/switch processors to consume
            context.pushPendingLabelName(labelName);
            try {
                // Generate the body - loop/switch processors will consume the label
                compiler.getStatementProcessor().generate(code, classWriter, body, returnTypeInfo);
            } finally {
                // The processor normally consumes the label; drop it if it did not, so that the
                // label cannot attach itself to whatever loop comes next
                context.popPendingLabelName();
            }
            return;
        }

        // Any other labeled statement, a labeled block in particular, is only a break target.
        // Push a break-only label, generate the body, and patch the breaks to the statement end.
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);
        try {
            compiler.getStatementProcessor().generate(code, classWriter, body, returnTypeInfo);
        } finally {
            context.popBreakLabel();
        }
        CodeGeneratorUtils.patchLabelPositions(code, breakLabel, code.getCurrentOffset());
    }
}
