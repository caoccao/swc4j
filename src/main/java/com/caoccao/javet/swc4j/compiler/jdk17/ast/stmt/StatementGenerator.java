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
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
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
     * @param code           the code builder
     * @param cp             the constant pool
     * @param stmt           the statement to generate code for
     * @param returnTypeInfo return type information for the enclosing method
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstStmt stmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        if (options.debug() && stmt.getSpan() != null) {
            code.setLineNumber(stmt.getSpan().getLine());
        }

        if (stmt instanceof Swc4jAstVarDecl varDecl) {
            VarDeclGenerator.generate(code, cp, varDecl, context, options);
        } else if (stmt instanceof Swc4jAstExprStmt exprStmt) {
            generateExprStmt(code, cp, exprStmt, context, options);
        } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            generateReturnStmt(code, cp, returnStmt, returnTypeInfo, context, options);
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            IfStatementGenerator.generate(code, cp, ifStmt, returnTypeInfo, context, options);
        } else if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            generateBlockStmt(code, cp, blockStmt, returnTypeInfo, context, options);
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
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateBlockStmt(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt blockStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Generate code for each statement in the block
        for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
            generate(code, cp, stmt, returnTypeInfo, context, options);
        }
    }

    /**
     * Generate bytecode for an expression statement.
     *
     * @param code      the code builder
     * @param cp        the constant pool
     * @param exprStmt  the expression statement
     * @param context   compilation context
     * @param options   compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateExprStmt(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstExprStmt exprStmt,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        ISwc4jAstExpr expr = exprStmt.getExpr();
        ExpressionGenerator.generate(code, cp, expr, null, context, options);

        // Assignment and update expressions leave values on the stack that need to be popped
        // Call expressions handle their own return values (already popped if needed)
        if (expr instanceof Swc4jAstAssignExpr || expr instanceof Swc4jAstUpdateExpr) {
            // Assignment and update expressions leave the value on the stack
            String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
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

    /**
     * Generate bytecode for a return statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param returnStmt     the return statement
     * @param returnTypeInfo return type information
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private static void generateReturnStmt(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstReturnStmt returnStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        if (returnStmt.getArg().isPresent()) {
            // Generate the return value expression
            ExpressionGenerator.generate(code, cp, returnStmt.getArg().get(),
                    returnTypeInfo, context, options);

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
}
