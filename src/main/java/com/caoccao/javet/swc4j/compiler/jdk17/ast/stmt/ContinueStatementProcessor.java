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
 * Generator for continue statements.
 * <p>
 * Continue statements skip to the next iteration of the innermost loop (or labeled loop)
 * by jumping to the update label (or test label if no update).
 * If there are pending finally blocks (from enclosing try-finally statements), they are
 * executed before the continue.
 * <p>
 * Bytecode pattern (without finally):
 * <pre>
 *   goto UPDATE_LABEL      // Jump to continue target
 * </pre>
 * <p>
 * Bytecode pattern (with finally):
 * <pre>
 *   [finally block code]   // Execute pending finally blocks
 *   goto UPDATE_LABEL      // Jump to continue target
 * </pre>
 */
public final class ContinueStatementProcessor extends BaseAstProcessor<Swc4jAstContinueStmt> {
    public ContinueStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for a continue statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param continueStmt   the continue statement AST node
     * @param returnTypeInfo return type information (unused for continue statements)
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstContinueStmt continueStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        LoopLabelInfo continueLabel;

        // Check if this is a labeled continue
        if (continueStmt.getLabel().isPresent()) {
            // Labeled continue - search for the labeled loop
            String labelName = continueStmt.getLabel().get().getSym();
            continueLabel = context.getLabeledContinueLabel(labelName);

            if (continueLabel == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), continueStmt,
                        "Label '" + labelName + "' not found for continue statement");
            }
        } else {
            // Unlabeled continue - use innermost loop
            continueLabel = context.getCurrentContinueLabel();

            if (continueLabel == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), continueStmt,
                        "Continue statement outside of loop");
            }
        }

        // Execute pending finally blocks before continue (excluding those already being executed inline)
        var pendingFinallyBlocks = context.getPendingFinallyBlocksExcludingInline();
        for (Swc4jAstBlockStmt finallyBlock : pendingFinallyBlocks) {
            context.markFinallyBlockAsInlineExecuting(finallyBlock);
            try {
                for (ISwc4jAstStmt stmt : finallyBlock.getStmts()) {
                    compiler.getStatementProcessor().generate(code, classWriter, stmt, returnTypeInfo);
                    // If finally has its own terminal statement, it takes precedence
                    if (isTerminalStatement(stmt)) {
                        return; // Finally's return/throw supersedes the continue
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
        continueLabel.addPatchPosition(gotoOffsetPos, gotoOpcodePos);
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
