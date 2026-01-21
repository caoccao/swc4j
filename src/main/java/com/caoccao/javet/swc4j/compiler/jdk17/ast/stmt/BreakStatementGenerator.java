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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBreakStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for break statements.
 * <p>
 * Break statements exit the innermost loop (or labeled loop) by jumping to the end label.
 * <p>
 * Bytecode pattern:
 * <pre>
 *   goto END_LABEL         // Jump to break target
 * </pre>
 */
public final class BreakStatementGenerator {
    private BreakStatementGenerator() {
    }

    /**
     * Generate bytecode for a break statement.
     *
     * @param compiler  the compiler
     * @param code      the code builder
     * @param cp        the constant pool
     * @param breakStmt the break statement AST node
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBreakStmt breakStmt) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        LoopLabelInfo breakLabel;

        // Check if this is a labeled break
        if (breakStmt.getLabel().isPresent()) {
            // Labeled break - search for the labeled loop
            String labelName = breakStmt.getLabel().get().getSym();
            breakLabel = context.getLabeledBreakLabel(labelName);

            if (breakLabel == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Label '" + labelName + "' not found for break statement");
            }
        } else {
            // Unlabeled break - use innermost loop
            breakLabel = context.getCurrentBreakLabel();

            if (breakLabel == null) {
                throw new Swc4jByteCodeCompilerException(
                        "Break statement outside of loop or switch");
            }
        }

        // Generate goto with placeholder (will be patched later)
        code.gotoLabel(0);

        // Store position for patching when target is determined
        int gotoOffsetPos = code.getCurrentOffset() - 2;
        int gotoOpcodePos = code.getCurrentOffset() - 3;
        breakLabel.addPatchPosition(gotoOffsetPos, gotoOpcodePos);
    }
}
