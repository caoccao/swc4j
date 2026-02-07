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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for while loops.
 * <p>
 * Bytecode pattern matching javac output:
 * <pre>
 *   TEST_LABEL:            // Loop entry point
 *   [load left operand]
 *   [load right operand]
 *   if_icmpge END_LABEL    // Direct comparison jump (for i &lt; 10: if i &gt;= 10, exit)
 *   [body statements]
 *   goto TEST_LABEL        // Jump back to test (backward jump)
 *   END_LABEL:             // Break target
 * </pre>
 */
public final class WhileStatementProcessor extends BaseAstProcessor<Swc4jAstWhileStmt> {
    /**
     * Instantiates a new While statement processor.
     *
     * @param compiler the compiler
     */
    public WhileStatementProcessor(ByteCodeCompiler compiler) {
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
     * Generate bytecode for a while statement (unlabeled).
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param whileStmt      the while statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstWhileStmt whileStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, classWriter, whileStmt, null, returnTypeInfo);
    }

    /**
     * Generate bytecode for a while statement (potentially labeled).
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param whileStmt      the while statement AST node
     * @param labelName      the label name (null for unlabeled loops)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstWhileStmt whileStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // 1. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 2. Generate test condition (unless it's while(true))
        ISwc4jAstExpr testExpr = whileStmt.getTest();
        boolean isInfiniteLoop = isConstantTrue(testExpr);

        int condJumpOffsetPos = -1;
        int condJumpOpcodePos = -1;
        int exitGotoOffsetPos = -1;
        int exitGotoOpcodePos = -1;

        if (!isInfiniteLoop) {
            // Generate conditional test and jump to body if TRUE
            if (testExpr instanceof Swc4jAstBinExpr binExpr) {
                boolean generated = generateDirectConditionalJumpToBody(code, classWriter, binExpr);
                if (generated) {
                    condJumpOffsetPos = code.getCurrentOffset() - 2;
                    condJumpOpcodePos = code.getCurrentOffset() - 3;
                } else {
                    compiler.getExpressionProcessor().generate(code, classWriter, testExpr, null);
                    code.ifne(0); // Placeholder - jump to body if TRUE
                    condJumpOffsetPos = code.getCurrentOffset() - 2;
                    condJumpOpcodePos = code.getCurrentOffset() - 3;
                }
            } else {
                compiler.getExpressionProcessor().generate(code, classWriter, testExpr, null);
                code.ifne(0); // Placeholder - jump to body if TRUE
                condJumpOffsetPos = code.getCurrentOffset() - 2;
                condJumpOpcodePos = code.getCurrentOffset() - 3;
            }

            // Emit wide jump to end for the false path
            code.goto_w(0); // Placeholder
            exitGotoOffsetPos = code.getCurrentOffset() - 4;
            exitGotoOpcodePos = code.getCurrentOffset() - 5;

            int bodyLabel = code.getCurrentOffset();
            int condJumpOffset = bodyLabel - condJumpOpcodePos;
            code.patchShort(condJumpOffsetPos, condJumpOffset);
        }
        // For while(true), no conditional test needed - just fall through to body

        // 3. Create label info for break and continue
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        // Push labels onto stack before generating body
        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 4. Generate body and check if it can fall through
        compiler.getStatementProcessor().generate(code, classWriter, whileStmt.getBody(), returnTypeInfo);
        boolean bodyCanFallThrough = canFallThrough(whileStmt.getBody());

        // 5. Mark continue label (continue jumps to test label for while loops)
        continueLabel.setTargetOffset(testLabel);

        // 6. Generate backward goto only if body can fall through
        // If body always terminates (e.g., unconditional break/return/continue),
        // the backward jump is unreachable
        // Note: Unlike for loops, continue jumps directly to test (no update section),
        // so we don't need a backward jump when body ends with continue
        if (bodyCanFallThrough) {
            int backwardGotoOffset = testLabel - code.getCurrentOffset();
            code.goto_w(backwardGotoOffset);
        }

        // 7. Mark end label (break target)
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 8. Patch all break statements to jump to end label
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 9. Patch all continue statements to jump to test label
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 10. Patch exit goto (only if we generated one)
        if (!isInfiniteLoop) {
            int exitGotoOffset = endLabel - exitGotoOpcodePos;
            code.patchInt(exitGotoOffsetPos, exitGotoOffset);
        }

        // 11. Pop labels from stack
        context.popBreakLabel();
        context.popContinueLabel();
    }

    /**
     * Generate a direct conditional jump for comparison expressions (like javac does).
     * Instead of generating a boolean and using ifeq, we use if_icmpge etc. directly.
     *
     * @return true if direct jump was generated, false if caller should use fallback
     */
    private boolean generateDirectConditionalJumpToBody(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBinExpr binExpr) throws Swc4jByteCodeCompilerException {

        Swc4jAstBinaryOp op = binExpr.getOp();

        // Only handle comparison operators
        if (!isComparisonOp(op)) {
            return false;
        }

        // Get operand types
        String leftType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getLeft());
        String rightType = compiler.getTypeResolver().inferTypeFromExpr(binExpr.getRight());

        // Only handle int comparisons for now (most common case)
        if (!ConstantJavaType.ABBR_INTEGER.equals(leftType) || !ConstantJavaType.ABBR_INTEGER.equals(rightType)) {
            return false;
        }

        // Generate left operand
        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getLeft(), null);

        // Generate right operand
        compiler.getExpressionProcessor().generate(code, classWriter, binExpr.getRight(), null);

        // Generate comparison - jump to body if condition is TRUE
        // For "i < 10", we want to jump to body if "i < 10", so use if_icmplt
        switch (op) {
            case Lt -> code.if_icmplt(0);      // i < 10  -> enter body if i < 10
            case LtEq -> code.if_icmple(0);    // i <= 10 -> enter body if i <= 10
            case Gt -> code.if_icmpgt(0);      // i > 10  -> enter body if i > 10
            case GtEq -> code.if_icmpge(0);    // i >= 10 -> enter body if i >= 10
            case EqEq, EqEqEq -> code.if_icmpeq(0);  // i == 10 -> enter body if i == 10
            case NotEq, NotEqEq -> code.if_icmpne(0); // i != 10 -> enter body if i != 10
            default -> {
                return false;
            }
        }

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
