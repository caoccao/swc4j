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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for if statements.
 * <p>
 * Bytecode pattern for if without else:
 * <pre>
 *   [test expression]      // Stack: [boolean]
 *   ifeq END_LABEL         // Jump to end if false (0)
 *   [consequent statements]
 *   END_LABEL:
 * </pre>
 * <p>
 * Bytecode pattern for if-else:
 * <pre>
 *   [test expression]      // Stack: [boolean]
 *   ifeq ELSE_LABEL        // Jump to else if false (0)
 *   [consequent statements]
 *   goto END_LABEL
 *   ELSE_LABEL:
 *   [alternate statements]
 *   END_LABEL:
 * </pre>
 */
public final class IfStatementProcessor extends BaseAstProcessor<Swc4jAstIfStmt> {
    public IfStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Check if a statement ends with an unconditional control transfer (return, break, continue).
     * These statements don't fall through to the next instruction.
     *
     * @param stmt the statement to check
     * @return true if the statement ends with an unconditional control transfer
     */
    private boolean endsWithUnconditionalJump(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstReturnStmt) {
            return true;
        }
        if (stmt instanceof Swc4jAstBreakStmt) {
            return true;
        }
        if (stmt instanceof Swc4jAstContinueStmt) {
            return true;
        }
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            var stmts = blockStmt.getStmts();
            if (!stmts.isEmpty()) {
                return endsWithUnconditionalJump(stmts.get(stmts.size() - 1));
            }
        }
        if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            // If-else ends with unconditional jump only if BOTH branches end with unconditional jump
            if (ifStmt.getAlt().isEmpty()) {
                return false; // No else branch means fall-through is possible
            }
            return endsWithUnconditionalJump(ifStmt.getCons()) &&
                    endsWithUnconditionalJump(ifStmt.getAlt().get());
        }
        return false;
    }

    /**
     * Generate bytecode for an if statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param ifStmt         the if statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Evaluate the test condition
        compiler.getExpressionProcessor().generate(code, classWriter, ifStmt.getTest(), null);

        if (ifStmt.getAlt().isEmpty()) {
            // Simple if without else
            generateSimpleIf(code, classWriter, ifStmt, returnTypeInfo);
        } else {
            // If-else
            generateIfElse(code, classWriter, ifStmt, returnTypeInfo);
        }
    }

    private void generateIfElse(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Jump to else branch if condition is false (0)
        code.ifeq(0); // Placeholder
        int ifeqOffsetPos = code.getCurrentOffset() - 2;
        int ifeqOpcodePos = code.getCurrentOffset() - 3;

        // Generate consequent (then branch)
        compiler.getStatementProcessor().generate(code, classWriter, ifStmt.getCons(), returnTypeInfo);

        // Check if consequent ends with unconditional jump - if so, no need for goto
        boolean consEndsWithJump = endsWithUnconditionalJump(ifStmt.getCons());

        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;

        if (!consEndsWithJump) {
            // Jump over the alternate branch (only if consequent doesn't have unconditional jump)
            code.gotoLabel(0); // Placeholder for goto
            gotoOffsetPos = code.getCurrentOffset() - 2;
            gotoOpcodePos = code.getCurrentOffset() - 3;
        }

        // Generate alternate (else branch)
        int elseLabel = code.getCurrentOffset();
        compiler.getStatementProcessor().generate(code, classWriter, ifStmt.getAlt().get(), returnTypeInfo);

        // End of if-else statement
        int endLabel = code.getCurrentOffset();

        // Calculate and patch the ifeq offset
        int ifeqOffset = elseLabel - ifeqOpcodePos;
        code.patchShort(ifeqOffsetPos, ifeqOffset);

        // Calculate and patch the goto offset (only if it was generated)
        if (!consEndsWithJump) {
            int gotoOffset = endLabel - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }
    }

    private void generateSimpleIf(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Jump to end if condition is false (0)
        code.ifeq(0); // Placeholder, will patch offset later
        int ifeqOffsetPos = code.getCurrentOffset() - 2;
        int ifeqOpcodePos = code.getCurrentOffset() - 3;

        // Generate consequent (then branch)
        compiler.getStatementProcessor().generate(code, classWriter, ifStmt.getCons(), returnTypeInfo);

        // End of if statement
        int endLabel = code.getCurrentOffset();

        // Calculate and patch the ifeq offset
        int ifeqOffset = endLabel - ifeqOpcodePos;
        code.patchShort(ifeqOffsetPos, ifeqOffset);
    }
}
