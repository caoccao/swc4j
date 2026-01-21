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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstContinueStmt;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for continue statements.
 * <p>
 * Continue statements skip to the next iteration of the innermost loop (or labeled loop)
 * by jumping to the update label (or test label if no update).
 * <p>
 * Bytecode pattern:
 * <pre>
 *   goto UPDATE_LABEL      // Jump to continue target
 * </pre>
 */
public final class ContinueStatementGenerator extends BaseAstProcessor<Swc4jAstContinueStmt> {
    public ContinueStatementGenerator(ByteCodeCompiler compiler) {
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
            ClassWriter.ConstantPool cp,
            Swc4jAstContinueStmt continueStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        LoopLabelInfo continueLabel;

        // Check if this is a labeled continue
        if (continueStmt.getLabel().isPresent()) {
            // Labeled continue - search for the labeled loop
            String labelName = continueStmt.getLabel().get().getSym();
            continueLabel = context.getLabeledContinueLabel(labelName);

            if (continueLabel == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Label '" + labelName + "' not found for continue statement");
            }
        } else {
            // Unlabeled continue - use innermost loop
            continueLabel = context.getCurrentContinueLabel();

            if (continueLabel == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Continue statement outside of loop");
            }
        }

        // Generate goto with placeholder (will be patched later)
        code.gotoLabel(0);

        // Store position for patching when target is determined
        int gotoOffsetPos = code.getCurrentOffset() - 2;
        int gotoOpcodePos = code.getCurrentOffset() - 3;
        continueLabel.addPatchPosition(gotoOffsetPos, gotoOpcodePos);
    }
}
