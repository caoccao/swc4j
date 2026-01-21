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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUpdateExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Main dispatcher for statement code generation.
 * Delegates to specialized generators based on statement type.
 */
public final class StatementGenerator {
    private StatementGenerator() {
    }

    /**
     * Generate bytecode for a statement.
     *
     * @param compiler       the compiler
     * @param code           the code builder
     * @param cp             the constant pool
     * @param stmt           the statement to generate code for
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstStmt stmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (compiler.getOptions().debug() && stmt.getSpan() != null) {
            code.setLineNumber(stmt.getSpan().getLine());
        }

        if (stmt instanceof Swc4jAstVarDecl varDecl) {
            VarDeclGenerator.generate(compiler, code, cp, varDecl);
        } else if (stmt instanceof Swc4jAstExprStmt exprStmt) {
            generateExprStmt(compiler, code, cp, exprStmt);
        } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            generateReturnStmt(compiler, code, cp, returnStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            IfStatementGenerator.generate(compiler, code, cp, ifStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstForStmt forStmt) {
            ForStatementGenerator.generate(compiler, code, cp, forStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstWhileStmt whileStmt) {
            WhileStatementGenerator.generate(compiler, code, cp, whileStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstDoWhileStmt doWhileStmt) {
            DoWhileStatementGenerator.generate(compiler, code, cp, doWhileStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstBreakStmt breakStmt) {
            BreakStatementGenerator.generate(compiler, code, cp, breakStmt);
        } else if (stmt instanceof Swc4jAstContinueStmt continueStmt) {
            ContinueStatementGenerator.generate(compiler, code, cp, continueStmt);
        } else if (stmt instanceof Swc4jAstLabeledStmt labeledStmt) {
            LabeledStatementGenerator.generate(compiler, code, cp, labeledStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            generateBlockStmt(compiler, code, cp, blockStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstSwitchStmt switchStmt) {
            SwitchStatementGenerator.generate(compiler, code, cp, switchStmt, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(
                    "Unsupported statement type: " + stmt.getClass().getSimpleName());
        }
    }

    /**
     * Generate bytecode for a block statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param blockStmt      the block statement
     * @param returnTypeInfo return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateBlockStmt(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt blockStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Generate code for each statement in the block
        // Stop generating code after a terminal control flow statement (break, continue, return)
        for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
            generate(compiler, code, cp, stmt, returnTypeInfo);

            // Check if this was a terminal statement - subsequent statements are unreachable
            if (isTerminalStatement(stmt)) {
                break;
            }
        }
    }

    private static void generateExprStmt(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstExprStmt exprStmt) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr expr = exprStmt.getExpr();
        ExpressionGenerator.generate(compiler, code, cp, expr, null);

        // Assignment and update expressions leave values on the stack that need to be popped
        // Call expressions handle their own return values (already popped if needed)
        if (expr instanceof Swc4jAstAssignExpr || expr instanceof Swc4jAstUpdateExpr) {
            // Assignment and update expressions leave the value on the stack
            String exprType = TypeResolver.inferTypeFromExpr(compiler, expr);
            if (exprType != null && !("V".equals(exprType))) {
                // Expression leaves a value, pop it
                // Use pop2 for wide types (double, long)
                if ("D".equals(exprType) || "J".equals(exprType)) {
                    code.pop2();
                } else {
                    code.pop();
                }
            }
        }
        // Note: CallExpr already pops its return value in generateCallExpr
    }

    private static void generateReturnStmt(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstReturnStmt returnStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        if (returnStmt.getArg().isPresent()) {
            // Generate the return value expression
            ExpressionGenerator.generate(compiler, code, cp, returnStmt.getArg().get(),
                    returnTypeInfo);

            // Generate the appropriate return instruction
            if (returnTypeInfo != null) {
                switch (returnTypeInfo.type()) {
                    case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.ireturn();
                    case LONG -> code.lreturn();
                    case FLOAT -> code.freturn();
                    case DOUBLE -> code.dreturn();
                    case OBJECT, STRING -> code.areturn();
                    default -> code.areturn();
                }
            } else {
                code.areturn();
            }
        } else {
            // Void return
            code.returnVoid();
        }
    }

    /**
     * Check if a statement is a terminal control flow statement (break, continue, return).
     * Statements after a terminal statement are unreachable and should not be generated.
     *
     * @param stmt the statement to check
     * @return true if the statement is terminal
     */
    private static boolean isTerminalStatement(ISwc4jAstStmt stmt) {
        return stmt instanceof Swc4jAstBreakStmt ||
                stmt instanceof Swc4jAstContinueStmt ||
                stmt instanceof Swc4jAstReturnStmt;
    }
}
