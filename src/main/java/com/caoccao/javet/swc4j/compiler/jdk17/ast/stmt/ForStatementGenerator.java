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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for for loops.
 * <p>
 * Bytecode pattern matching javac output:
 * <pre>
 *   [init code]            // Execute initialization
 *   TEST_LABEL:            // Loop entry point
 *   [load left operand]
 *   [load right operand]
 *   if_icmpge END_LABEL    // Direct comparison jump (for i < 10: if i >= 10, exit)
 *   [body statements]
 *   [update: iinc]         // Execute update
 *   goto TEST_LABEL        // Jump back to test (backward jump)
 *   END_LABEL:             // Break target
 * </pre>
 */
public final class ForStatementGenerator {
    private ForStatementGenerator() {
    }

    /**
     * Check if a statement can fall through (complete normally).
     * A statement cannot fall through if it always ends with break, return, or throw.
     * Note: continue CAN fall through to the update section, so it's not considered terminal here.
     */
    private static boolean canFallThrough(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstBreakStmt) {
            return false; // break always exits
        }
        if (stmt instanceof Swc4jAstReturnStmt) {
            return false; // return always exits
        }
        if (stmt instanceof Swc4jAstContinueStmt) {
            return false; // continue jumps to update, but we handle this separately
        }
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            // Block can fall through if its last statement can fall through
            var stmts = blockStmt.getStmts();
            if (stmts.isEmpty()) {
                return true; // empty block falls through
            }
            // Check each statement - if any is terminal, the block doesn't fall through from that point
            for (ISwc4jAstStmt s : stmts) {
                if (!canFallThrough(s)) {
                    return false; // found terminal statement
                }
            }
            return true;
        }
        if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            // If statement falls through if:
            // - No else branch (then branch might not execute)
            // - OR both branches can fall through
            if (ifStmt.getAlt().isEmpty()) {
                return true; // no else, condition might be false
            }
            boolean thenFallsThrough = canFallThrough(ifStmt.getCons());
            boolean elseFallsThrough = canFallThrough(ifStmt.getAlt().get());
            return thenFallsThrough || elseFallsThrough;
        }
        // Other statements (expressions, var decls, etc.) can fall through
        return true;
    }

    /**
     * Generate bytecode for a for statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param forStmt        the for statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @param context        compilation context
     * @param options        compilation options
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForStmt forStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Enter scope for for-loop variables (matches the scope entered during analysis)
        context.getLocalVariableTable().enterScope();

        // 1. Generate init (if present)
        if (forStmt.getInit().isPresent()) {
            generateInit(code, cp, forStmt.getInit().get(), context, options);
        }

        // 2. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 3. Setup placeholder for conditional jump to end (if test exists)
        int condJumpOffsetPos = -1;
        int condJumpOpcodePos = -1;

        if (forStmt.getTest().isPresent()) {
            ISwc4jAstExpr testExpr = forStmt.getTest().get();

            // Try to generate direct conditional jump (like javac does)
            if (testExpr instanceof Swc4jAstBinExpr binExpr) {
                boolean generated = generateDirectConditionalJump(code, cp, binExpr, context, options);
                if (generated) {
                    condJumpOffsetPos = code.getCurrentOffset() - 2;
                    condJumpOpcodePos = code.getCurrentOffset() - 3;
                } else {
                    // Fallback: generate boolean expression and use ifeq
                    ExpressionGenerator.generate(code, cp, testExpr, null, context, options);
                    code.ifeq(0); // Placeholder
                    condJumpOffsetPos = code.getCurrentOffset() - 2;
                    condJumpOpcodePos = code.getCurrentOffset() - 3;
                }
            } else {
                // Non-binary expression: generate as boolean and use ifeq
                ExpressionGenerator.generate(code, cp, testExpr, null, context, options);
                code.ifeq(0); // Placeholder
                condJumpOffsetPos = code.getCurrentOffset() - 2;
                condJumpOpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // 4. Create label info for break and continue
        CompilationContext.LoopLabelInfo breakLabel = new CompilationContext.LoopLabelInfo(null);
        CompilationContext.LoopLabelInfo continueLabel = new CompilationContext.LoopLabelInfo(null);

        // Push labels onto stack before generating body
        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 5. Generate body and track if it can fall through
        boolean bodyCanFallThrough = generateBodyAndCheckFallThrough(
                code, cp, forStmt.getBody(), returnTypeInfo, context, options);

        // 6. Mark update label (continue jumps here)
        int updateLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(updateLabel);

        // 7. Generate update and backward goto only if body can fall through or has continue
        // If body always terminates without continue (e.g., unconditional break/return),
        // the update section is unreachable
        boolean hasContinue = !continueLabel.getPatchPositions().isEmpty();

        if (bodyCanFallThrough || hasContinue) {
            // Generate update (if present)
            if (forStmt.getUpdate().isPresent()) {
                generateUpdate(code, cp, forStmt.getUpdate().get(), context, options);
            }

            // Jump back to test (backward jump)
            code.gotoLabel(0); // Placeholder
            int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
            int backwardGotoOpcodePos = code.getCurrentOffset() - 3;

            // Calculate and patch the backward jump offset
            int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
            code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);
        }

        // 8. Mark end label (break target)
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 9. Patch all break statements to jump to end label
        for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 10. Patch all continue statements to jump to update label
        for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 11. Patch conditional jump if test exists
        if (forStmt.getTest().isPresent()) {
            int condJumpOffset = endLabel - condJumpOpcodePos;
            code.patchShort(condJumpOffsetPos, condJumpOffset);
        }

        // 12. Pop labels from stack
        context.popBreakLabel();
        context.popContinueLabel();

        // 13. Exit scope for for-loop variables
        context.getLocalVariableTable().exitScope();
    }

    /**
     * Generate body statements and return whether the body can fall through to update.
     * Body can fall through if it doesn't end with break, return, or throw (but continue is ok).
     *
     * @return true if body can fall through to update section
     */
    private static boolean generateBodyAndCheckFallThrough(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstStmt body,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Generate the body
        StatementGenerator.generate(code, cp, body, returnTypeInfo, context, options);

        // Check if the body can fall through
        return canFallThrough(body);
    }

    /**
     * Generate a direct conditional jump for comparison expressions (like javac does).
     * Instead of generating a boolean and using ifeq, we use if_icmpge etc. directly.
     *
     * @return true if direct jump was generated, false if caller should use fallback
     */
    private static boolean generateDirectConditionalJump(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        Swc4jAstBinaryOp op = binExpr.getOp();

        // Only handle comparison operators
        if (!isComparisonOp(op)) {
            return false;
        }

        // Get operand types
        String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
        String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);

        // Only handle int comparisons for now (most common case)
        if (!"I".equals(leftType) || !"I".equals(rightType)) {
            return false;
        }

        // Generate left operand
        ExpressionGenerator.generate(code, cp, binExpr.getLeft(), null, context, options);

        // Generate right operand
        ExpressionGenerator.generate(code, cp, binExpr.getRight(), null, context, options);

        // Generate inverted comparison (jump to end if condition is FALSE)
        // For "i < 10", we want to exit if "i >= 10", so use if_icmpge
        switch (op) {
            case Lt -> code.if_icmpge(0);      // i < 10  -> exit if i >= 10
            case LtEq -> code.if_icmpgt(0);    // i <= 10 -> exit if i > 10
            case Gt -> code.if_icmple(0);      // i > 10  -> exit if i <= 10
            case GtEq -> code.if_icmplt(0);    // i >= 10 -> exit if i < 10
            case EqEq, EqEqEq -> code.if_icmpne(0);  // i == 10 -> exit if i != 10
            case NotEq, NotEqEq -> code.if_icmpeq(0); // i != 10 -> exit if i == 10
            default -> {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate bytecode for the init section of a for loop.
     */
    private static void generateInit(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstVarDeclOrExpr init,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        if (init instanceof Swc4jAstVarDecl varDecl) {
            // Add loop variables to the current scope before generating code
            // This ensures that shadowed variables are correctly resolved
            for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
                ISwc4jAstPat name = declarator.getName();
                if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    String varType = TypeResolver.extractType(bindingIdent, declarator.getInit(), context, options);
                    context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
                }
            }
            VarDeclGenerator.generate(code, cp, varDecl, context, options);
        } else if (init instanceof ISwc4jAstExpr expr) {
            ExpressionGenerator.generate(code, cp, expr, null, context, options);

            // Pop the result of the init expression if it leaves a value on the stack
            String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
            if (exprType != null && !"V".equals(exprType)) {
                if ("D".equals(exprType) || "J".equals(exprType)) {
                    code.pop2();
                } else {
                    code.pop();
                }
            }
        }
    }

    /**
     * Generate bytecode for the update section of a for loop.
     */
    private static void generateUpdate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr updateExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        ExpressionGenerator.generate(code, cp, updateExpr, null, context, options);

        // Pop the result of the update expression if it leaves a value on the stack
        // This includes AssignExpr (i = 5), UpdateExpr (i++), and SeqExpr (i++, j--)
        String updateType = TypeResolver.inferTypeFromExpr(updateExpr, context, options);
        if (updateType != null && !"V".equals(updateType)) {
            if ("D".equals(updateType) || "J".equals(updateType)) {
                code.pop2();
            } else {
                code.pop();
            }
        }
    }

    /**
     * Check if the operator is a comparison operator.
     */
    private static boolean isComparisonOp(Swc4jAstBinaryOp op) {
        return op == Swc4jAstBinaryOp.Lt ||
                op == Swc4jAstBinaryOp.LtEq ||
                op == Swc4jAstBinaryOp.Gt ||
                op == Swc4jAstBinaryOp.GtEq ||
                op == Swc4jAstBinaryOp.EqEq ||
                op == Swc4jAstBinaryOp.EqEqEq ||
                op == Swc4jAstBinaryOp.NotEq ||
                op == Swc4jAstBinaryOp.NotEqEq;
    }
}
