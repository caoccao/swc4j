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
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
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
public final class IfStatementGenerator {
    private IfStatementGenerator() {
    }

    /**
     * Generate bytecode for an if statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param ifStmt         the if statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Evaluate the test condition
        ExpressionGenerator.generate(code, cp, ifStmt.getTest(), null, context, options);

        if (ifStmt.getAlt().isEmpty()) {
            // Simple if without else
            generateSimpleIf(code, cp, ifStmt, returnTypeInfo, context, options);
        } else {
            // If-else
            generateIfElse(code, cp, ifStmt, returnTypeInfo, context, options);
        }
    }

    /**
     * Generate bytecode for simple if statement (no else clause).
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param ifStmt         the if statement AST node
     * @param returnTypeInfo return type information
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateSimpleIf(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Jump to end if condition is false (0)
        code.ifeq(0); // Placeholder, will patch offset later
        int ifeqOffsetPos = code.getCurrentOffset() - 2;
        int ifeqOpcodePos = code.getCurrentOffset() - 3;

        // Generate consequent (then branch)
        StatementGenerator.generate(code, cp, ifStmt.getCons(), returnTypeInfo, context, options);

        // End of if statement
        int endLabel = code.getCurrentOffset();

        // Calculate and patch the ifeq offset
        int ifeqOffset = endLabel - ifeqOpcodePos;
        code.patchShort(ifeqOffsetPos, ifeqOffset);
    }

    /**
     * Generate bytecode for if-else statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param ifStmt         the if statement AST node
     * @param returnTypeInfo return type information
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateIfElse(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstIfStmt ifStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Jump to else branch if condition is false (0)
        code.ifeq(0); // Placeholder
        int ifeqOffsetPos = code.getCurrentOffset() - 2;
        int ifeqOpcodePos = code.getCurrentOffset() - 3;

        // Generate consequent (then branch)
        StatementGenerator.generate(code, cp, ifStmt.getCons(), returnTypeInfo, context, options);

        // Check if consequent ends with return - if so, no need for goto
        boolean consEndsWithReturn = endsWithReturn(ifStmt.getCons());

        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;

        if (!consEndsWithReturn) {
            // Jump over the alternate branch (only if consequent doesn't return)
            code.gotoLabel(0); // Placeholder for goto
            gotoOffsetPos = code.getCurrentOffset() - 2;
            gotoOpcodePos = code.getCurrentOffset() - 3;
        }

        // Generate alternate (else branch)
        int elseLabel = code.getCurrentOffset();
        StatementGenerator.generate(code, cp, ifStmt.getAlt().get(), returnTypeInfo, context, options);

        // End of if-else statement
        int endLabel = code.getCurrentOffset();

        // Calculate and patch the ifeq offset
        int ifeqOffset = elseLabel - ifeqOpcodePos;
        code.patchShort(ifeqOffsetPos, ifeqOffset);

        // Calculate and patch the goto offset (only if it was generated)
        if (!consEndsWithReturn) {
            int gotoOffset = endLabel - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }
    }

    /**
     * Check if a statement ends with a return statement.
     *
     * @param stmt the statement to check
     * @return true if the statement ends with a return
     */
    private static boolean endsWithReturn(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstReturnStmt) {
            return true;
        }
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            var stmts = blockStmt.getStmts();
            if (!stmts.isEmpty()) {
                return endsWithReturn(stmts.get(stmts.size() - 1));
            }
        }
        return false;
    }
}
