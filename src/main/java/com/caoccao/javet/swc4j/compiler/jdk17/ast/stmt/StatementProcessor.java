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
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Main dispatcher for statement code generation.
 * Delegates to specialized generators based on statement type.
 */
public final class StatementProcessor extends BaseAstProcessor<ISwc4jAstStmt> {
    public StatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Check if a block ends with a terminal statement.
     */
    private boolean blockEndsWithTerminal(Swc4jAstBlockStmt block) {
        var stmts = block.getStmts();
        if (stmts.isEmpty()) {
            return false;
        }
        return isTerminalStatement(stmts.get(stmts.size() - 1));
    }

    /**
     * Generate bytecode for a statement.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param stmt           the statement to generate code for
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            ISwc4jAstStmt stmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (compiler.getOptions().debug() && stmt.getSpan() != null) {
            code.setLineNumber(stmt.getSpan().getLine());
        }

        if (stmt instanceof Swc4jAstVarDecl varDecl) {
            compiler.getVarDeclProcessor().generate(code, classWriter, varDecl, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstExprStmt exprStmt) {
            generateExprStmt(code, classWriter, exprStmt);
        } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            generateReturnStmt(code, classWriter, returnStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            compiler.getIfStatementProcessor().generate(code, classWriter, ifStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstForStmt forStmt) {
            compiler.getForStatementProcessor().generate(code, classWriter, forStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstForInStmt forInStmt) {
            compiler.getForInStatementProcessor().generate(code, classWriter, forInStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstForOfStmt forOfStmt) {
            compiler.getForOfStatementProcessor().generate(code, classWriter, forOfStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstWhileStmt whileStmt) {
            compiler.getWhileStatementProcessor().generate(code, classWriter, whileStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstDoWhileStmt doWhileStmt) {
            compiler.getDoWhileStatementProcessor().generate(code, classWriter, doWhileStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstBreakStmt breakStmt) {
            compiler.getBreakStatementProcessor().generate(code, classWriter, breakStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstContinueStmt continueStmt) {
            compiler.getContinueStatementProcessor().generate(code, classWriter, continueStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstLabeledStmt labeledStmt) {
            compiler.getLabeledStatementProcessor().generate(code, classWriter, labeledStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            generateBlockStmt(code, classWriter, blockStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstSwitchStmt switchStmt) {
            compiler.getSwitchStatementProcessor().generate(code, classWriter, switchStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstThrowStmt throwStmt) {
            compiler.getThrowStatementProcessor().generate(code, classWriter, throwStmt, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstTryStmt tryStmt) {
            compiler.getTryStatementProcessor().generate(code, classWriter, tryStmt, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), stmt,
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
    private void generateBlockStmt(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBlockStmt blockStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        context.getLocalVariableTable().enterScope();
        try {
            // Generate code for each statement in the block
            // Stop generating code after a terminal control flow statement (break, continue, return)
            for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
                generate(code, classWriter, stmt, returnTypeInfo);

                // Check if this was a terminal statement - subsequent statements are unreachable
                if (isTerminalStatement(stmt)) {
                    break;
                }
            }
        } finally {
            context.getLocalVariableTable().exitScope();
        }
    }

    private void generateExprStmt(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstExprStmt exprStmt) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr expr = exprStmt.getExpr();
        compiler.getExpressionProcessor().generate(code, classWriter, expr, null);

        // Unwrap paren expressions to check the inner expression type
        ISwc4jAstExpr unwrappedExpr = expr.unParenExpr();

        // Assignment and update expressions leave values on the stack that need to be popped
        // Call expressions handle their own return values (already popped if needed)
        if (unwrappedExpr instanceof Swc4jAstAssignExpr || unwrappedExpr instanceof Swc4jAstUpdateExpr) {
            // Assignment and update expressions leave the value on the stack
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(unwrappedExpr);
            if (exprType != null && !("V".equals(exprType))) {
                // Expression leaves a value, pop it
                // Use pop2 for wide types (double, long)
                if ("D".equals(exprType) || "J".equals(exprType)) {
                    code.pop2();
                } else {
                    code.pop();
                }
            } else if (exprType == null) {
                // If type inference fails, still pop since assignment leaves a value
                code.pop();
            }
        }
        // Note: CallExpr already pops its return value in generateCallExpr
    }

    /**
     * Generate the appropriate return instruction based on return type.
     */
    private void generateReturnInstruction(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
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
    }

    private void generateReturnStmt(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstReturnStmt returnStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        var context = compiler.getMemory().getCompilationContext();

        // Get pending finally blocks (excluding those already being executed inline)
        var pendingFinallyBlocks = context.getPendingFinallyBlocksExcludingInline();

        if (returnStmt.getArg().isPresent()) {
            // Generate the return value expression
            compiler.getExpressionProcessor().generate(code, classWriter, returnStmt.getArg().get(),
                    returnTypeInfo);

            // If there are pending finally blocks, save return value and execute them
            if (!pendingFinallyBlocks.isEmpty()) {
                // Determine the return type descriptor
                String returnType = getDescriptorForReturnType(returnTypeInfo);

                // Allocate temp variable to store return value
                String tempName = "$returnValue$" + context.getNextTempId();
                context.getLocalVariableTable().allocateVariable(tempName, returnType);
                LocalVariable tempVar = context.getLocalVariableTable().getVariable(tempName);

                // Store return value
                storeByType(code, returnTypeInfo, tempVar.index());

                // Execute pending finally blocks
                for (Swc4jAstBlockStmt finallyBlock : pendingFinallyBlocks) {
                    context.markFinallyBlockAsInlineExecuting(finallyBlock);
                    try {
                        for (ISwc4jAstStmt stmt : finallyBlock.getStmts()) {
                            generate(code, classWriter, stmt, returnTypeInfo);
                            // If finally has its own return/throw, that takes precedence
                            if (isTerminalStatement(stmt)) {
                                return; // Finally's return/throw supersedes the original return
                            }
                        }
                    } finally {
                        context.unmarkFinallyBlockAsInlineExecuting(finallyBlock);
                    }
                }

                // Load return value (only reached if no finally was terminal)
                loadByType(code, returnTypeInfo, tempVar.index());
            }

            // Generate the appropriate return instruction
            generateReturnInstruction(code, returnTypeInfo);
        } else {
            // Void return - still need to execute finally blocks
            for (Swc4jAstBlockStmt finallyBlock : pendingFinallyBlocks) {
                context.markFinallyBlockAsInlineExecuting(finallyBlock);
                try {
                    for (ISwc4jAstStmt stmt : finallyBlock.getStmts()) {
                        generate(code, classWriter, stmt, returnTypeInfo);
                        if (isTerminalStatement(stmt)) {
                            return; // Finally's return/throw supersedes the original return
                        }
                    }
                } finally {
                    context.unmarkFinallyBlockAsInlineExecuting(finallyBlock);
                }
            }
            code.returnVoid();
        }
    }

    /**
     * Get the JVM descriptor for a return type.
     */
    private String getDescriptorForReturnType(ReturnTypeInfo returnTypeInfo) {
        if (returnTypeInfo == null) {
            return "Ljava/lang/Object;";
        }
        // If descriptor is available, use it
        if (returnTypeInfo.descriptor() != null) {
            return returnTypeInfo.descriptor();
        }
        // Otherwise, derive from type
        return switch (returnTypeInfo.type()) {
            case INT -> "I";
            case LONG -> "J";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case BOOLEAN -> "Z";
            case BYTE -> "B";
            case CHAR -> "C";
            case SHORT -> "S";
            case VOID -> "V";
            case STRING -> "Ljava/lang/String;";
            default -> "Ljava/lang/Object;";
        };
    }

    /**
     * Check if a statement is a terminal control flow statement (break, continue, return).
     * Statements after a terminal statement are unreachable and should not be generated.
     *
     * @param stmt the statement to check
     * @return true if the statement is terminal
     */
    private boolean isTerminalStatement(ISwc4jAstStmt stmt) {
        return stmt instanceof Swc4jAstBreakStmt ||
                stmt instanceof Swc4jAstContinueStmt ||
                stmt instanceof Swc4jAstReturnStmt ||
                stmt instanceof Swc4jAstThrowStmt;
    }

    /**
     * Load a value based on its type.
     */
    private void loadByType(CodeBuilder code, ReturnTypeInfo returnTypeInfo, int index) {
        if (returnTypeInfo == null) {
            code.aload(index);
            return;
        }
        switch (returnTypeInfo.type()) {
            case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.iload(index);
            case LONG -> code.lload(index);
            case FLOAT -> code.fload(index);
            case DOUBLE -> code.dload(index);
            default -> code.aload(index);
        }
    }

    /**
     * Store a value based on its type.
     */
    private void storeByType(CodeBuilder code, ReturnTypeInfo returnTypeInfo, int index) {
        if (returnTypeInfo == null) {
            code.astore(index);
            return;
        }
        switch (returnTypeInfo.type()) {
            case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.istore(index);
            case LONG -> code.lstore(index);
            case FLOAT -> code.fstore(index);
            case DOUBLE -> code.dstore(index);
            default -> code.astore(index);
        }
    }
}
