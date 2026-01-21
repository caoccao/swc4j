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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for do-while loops.
 * <p>
 * Bytecode pattern matching javac output:
 * <pre>
 *   BODY_LABEL:            // Loop entry point (body first)
 *   [body statements]
 *   TEST_LABEL:            // Continue target (before test)
 *   [load left operand]
 *   [load right operand]
 *   if_icmplt BODY_LABEL   // Jump back if condition TRUE (inverted from while)
 *   END_LABEL:             // Break target
 * </pre>
 * <p>
 * Key differences from while loops:
 * - Body executes FIRST, before any condition check
 * - Conditional jump uses ifne/if_icmplt etc. to jump BACK if TRUE (not ifeq to exit if false)
 * - Body always executes at least once
 * - Continue jumps to test label (before condition evaluation)
 */
public final class DoWhileStatementGenerator extends BaseAstProcessor<Swc4jAstDoWhileStmt> {
    public DoWhileStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Check if a statement can fall through (complete normally).
     * A statement cannot fall through if it always ends with break, return, or throw.
     * Note: continue CAN fall through to the test, so it's not considered terminal here.
     */
    private boolean canFallThrough(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstBreakStmt) {
            return false; // break always exits
        }
        if (stmt instanceof Swc4jAstReturnStmt) {
            return false; // return always exits
        }
        if (stmt instanceof Swc4jAstContinueStmt) {
            return false; // continue jumps to test, but we handle this separately
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
     * Generate bytecode for a do-while statement (unlabeled).
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param doWhileStmt    the do-while statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstDoWhileStmt doWhileStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, cp, doWhileStmt, null, returnTypeInfo);
    }

    /**
     * Generate bytecode for a do-while statement (potentially labeled).
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param doWhileStmt    the do-while statement AST node
     * @param labelName      the label name (null for unlabeled loops)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstDoWhileStmt doWhileStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // 1. Mark body label (loop entry point - body executes first)
        int bodyLabel = code.getCurrentOffset();

        // 2. Create label info for break and continue
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        // Push labels onto stack before generating body
        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 3. Generate body and check if it can fall through
        compiler.getStatementGenerator().generate(code, cp, doWhileStmt.getBody(), returnTypeInfo);
        boolean bodyCanFallThrough = canFallThrough(doWhileStmt.getBody());

        // 4. Mark test label (continue target - before test evaluation)
        int testLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(testLabel);

        // 5. Generate test condition (only if body can fall through or has continue)
        // If body always exits (unconditional break/return), test is unreachable
        boolean hasContinue = !continueLabel.getPatchPositions().isEmpty();

        if (bodyCanFallThrough || hasContinue) {
            ISwc4jAstExpr testExpr = doWhileStmt.getTest();
            boolean isInfiniteLoop = isConstantTrue(testExpr);

            if (!isInfiniteLoop) {
                // Generate conditional test - jump BACK to body if TRUE (inverted from while)
                if (testExpr instanceof Swc4jAstBinExpr binExpr) {
                    boolean generated = generateDirectConditionalJumpToBody(code, cp, binExpr, bodyLabel);
                    if (!generated) {
                        // Fallback: generate boolean expression and use ifne (jump if TRUE)
                        compiler.getExpressionGenerator().generate(code, cp, testExpr, null);
                        code.ifne(0); // Placeholder - jump back if TRUE
                        int backwardJumpOffsetPos = code.getCurrentOffset() - 2;
                        int backwardJumpOpcodePos = code.getCurrentOffset() - 3;
                        int backwardJumpOffset = bodyLabel - backwardJumpOpcodePos;
                        code.patchShort(backwardJumpOffsetPos, backwardJumpOffset);
                    }
                } else {
                    // Non-binary expression: generate as boolean and use ifne
                    compiler.getExpressionGenerator().generate(code, cp, testExpr, null);
                    code.ifne(0); // Placeholder - jump back if TRUE
                    int backwardJumpOffsetPos = code.getCurrentOffset() - 2;
                    int backwardJumpOpcodePos = code.getCurrentOffset() - 3;
                    int backwardJumpOffset = bodyLabel - backwardJumpOpcodePos;
                    code.patchShort(backwardJumpOffsetPos, backwardJumpOffset);
                }
            } else {
                // Infinite loop: unconditional jump back to body
                code.gotoLabel(0); // Placeholder
                int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
                int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
                int backwardGotoOffset = bodyLabel - backwardGotoOpcodePos;
                code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);
            }
        }
        // If body cannot fall through and has no continue, no test or backward jump needed

        // 6. Mark end label (break target)
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 7. Patch all break statements to jump to end label
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 8. Patch all continue statements to jump to test label
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 9. Pop labels from stack
        context.popBreakLabel();
        context.popContinueLabel();
    }

    /**
     * Generate a direct conditional jump back to body for comparison expressions (inverted from while).
     * For do-while, we jump BACK if condition is TRUE, not forward if FALSE.
     * Instead of generating a boolean and using ifne, we use if_icmplt etc. directly.
     *
     * @return true if direct jump was generated, false if caller should use fallback
     */
    private boolean generateDirectConditionalJumpToBody(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            int bodyLabel) throws Swc4jByteCodeCompilerException {

        Swc4jAstBinaryOp op = binExpr.getOp();

        // Only handle comparison operators
        if (!isComparisonOp(op)) {
            return false;
        }

        // Get operand types
        String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
        String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

        // Only handle int comparisons for now (most common case)
        if (!"I".equals(leftType) || !"I".equals(rightType)) {
            return false;
        }

        // Generate left operand
        compiler.getExpressionGenerator().generate(code, cp, binExpr.getLeft(), null);

        // Generate right operand
        compiler.getExpressionGenerator().generate(code, cp, binExpr.getRight(), null);

        // Generate comparison - jump BACK to body if condition is TRUE (inverted from while)
        // For do-while with "i < 10", we want to jump back if "i < 10" is TRUE, so use if_icmplt
        switch (op) {
            case Lt -> code.if_icmplt(0);      // i < 10  -> jump back if i < 10
            case LtEq -> code.if_icmple(0);    // i <= 10 -> jump back if i <= 10
            case Gt -> code.if_icmpgt(0);      // i > 10  -> jump back if i > 10
            case GtEq -> code.if_icmpge(0);    // i >= 10 -> jump back if i >= 10
            case EqEq, EqEqEq -> code.if_icmpeq(0);  // i == 10 -> jump back if i == 10
            case NotEq, NotEqEq -> code.if_icmpne(0); // i != 10 -> jump back if i != 10
            default -> {
                return false;
            }
        }

        // Patch the jump offset to point back to body
        int backwardJumpOffsetPos = code.getCurrentOffset() - 2;
        int backwardJumpOpcodePos = code.getCurrentOffset() - 3;
        int backwardJumpOffset = bodyLabel - backwardJumpOpcodePos;
        code.patchShort(backwardJumpOffsetPos, backwardJumpOffset);

        return true;
    }

    /**
     * Check if the operator is a comparison operator.
     */
    private boolean isComparisonOp(Swc4jAstBinaryOp op) {
        return op == Swc4jAstBinaryOp.Lt ||
                op == Swc4jAstBinaryOp.LtEq ||
                op == Swc4jAstBinaryOp.Gt ||
                op == Swc4jAstBinaryOp.GtEq ||
                op == Swc4jAstBinaryOp.EqEq ||
                op == Swc4jAstBinaryOp.EqEqEq ||
                op == Swc4jAstBinaryOp.NotEq ||
                op == Swc4jAstBinaryOp.NotEqEq;
    }

    /**
     * Check if the test expression is a constant true.
     * This includes boolean literal true and numeric constant 1.
     */
    private boolean isConstantTrue(ISwc4jAstExpr testExpr) {
        if (testExpr instanceof Swc4jAstBool bool) {
            return bool.isValue();
        }
        if (testExpr instanceof Swc4jAstNumber number) {
            // Any non-zero number is truthy
            return number.getRaw().equals("1");
        }
        return false;
    }
}
