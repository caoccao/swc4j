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
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for break statements.
 * <p>
 * Break statements exit the innermost loop (or labeled loop) by jumping to the end label.
 * If there are pending finally blocks (from enclosing try-finally statements), they are
 * executed before the break.
 * <p>
 * Bytecode pattern (without finally):
 * <pre>
 *   goto END_LABEL         // Jump to break target
 * </pre>
 * <p>
 * Bytecode pattern (with finally):
 * <pre>
 *   [finally block code]   // Execute pending finally blocks
 *   goto END_LABEL         // Jump to break target
 * </pre>
 */
public final class BreakStatementGenerator extends BaseAstProcessor<Swc4jAstBreakStmt> {
    public BreakStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for a break statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param breakStmt      the break statement AST node
     * @param returnTypeInfo return type information (unused for break statements)
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBreakStmt breakStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        LoopLabelInfo breakLabel;

        // Check if this is a labeled break
        if (breakStmt.getLabel().isPresent()) {
            // Labeled break - search for the labeled loop
            String labelName = breakStmt.getLabel().get().getSym();
            breakLabel = context.getLabeledBreakLabel(labelName);

            if (breakLabel == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), breakStmt,
                        "Label '" + labelName + "' not found for break statement");
            }
        } else {
            // Unlabeled break - use innermost loop
            breakLabel = context.getCurrentBreakLabel();

            if (breakLabel == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), breakStmt,
                        "Break statement outside of loop or switch");
            }
        }

        // Execute pending finally blocks before break (excluding those already being executed inline)
        var pendingFinallyBlocks = context.getPendingFinallyBlocksExcludingInline();
        for (Swc4jAstBlockStmt finallyBlock : pendingFinallyBlocks) {
            context.markFinallyBlockAsInlineExecuting(finallyBlock);
            try {
                for (ISwc4jAstStmt stmt : finallyBlock.getStmts()) {
                    compiler.getStatementGenerator().generate(code, classWriter, stmt, returnTypeInfo);
                    // If finally has its own terminal statement, it takes precedence
                    if (isTerminalStatement(stmt)) {
                        return; // Finally's return/throw supersedes the break
                    }
                }
            } finally {
                context.unmarkFinallyBlockAsInlineExecuting(finallyBlock);
            }
        }

        // Generate goto_w with placeholder (will be patched later)
        code.goto_w(0);

        // Store position for patching when target is determined
        int gotoOffsetPos = code.getCurrentOffset() - 4;
        int gotoOpcodePos = code.getCurrentOffset() - 5;
        breakLabel.addPatchPosition(gotoOffsetPos, gotoOpcodePos);
    }

    /**
     * Check if a statement is a terminal control flow statement.
     */
    private boolean isTerminalStatement(ISwc4jAstStmt stmt) {
        return stmt instanceof Swc4jAstBreakStmt ||
                stmt instanceof Swc4jAstContinueStmt ||
                stmt instanceof Swc4jAstReturnStmt ||
                stmt instanceof Swc4jAstThrowStmt;
    }
}
