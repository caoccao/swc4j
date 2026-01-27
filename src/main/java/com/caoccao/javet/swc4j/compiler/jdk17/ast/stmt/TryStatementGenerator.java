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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstCatchClause;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for try-catch-finally statements.
 * <p>
 * Supports three forms:
 * <ul>
 *   <li>try-catch: {@code try { } catch (e) { }}</li>
 *   <li>try-finally: {@code try { } finally { }}</li>
 *   <li>try-catch-finally: {@code try { } catch (e) { } finally { }}</li>
 * </ul>
 * <p>
 * Bytecode patterns follow JVM exception handling:
 * <ul>
 *   <li>Try-catch uses exception table entries to direct control to catch handlers</li>
 *   <li>Finally blocks are duplicated in bytecode for each exit path</li>
 *   <li>Stack map frames are generated at catch handler entry points</li>
 * </ul>
 */
public final class TryStatementGenerator extends BaseAstProcessor<Swc4jAstTryStmt> {
    public TryStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Check if a block ends with a terminal statement (or is empty).
     */
    private boolean blockEndsWithTerminal(Swc4jAstBlockStmt block) {
        var stmts = block.getStmts();
        if (stmts.isEmpty()) {
            return false;
        }
        return isTerminalStatement(stmts.get(stmts.size() - 1));
    }

    /**
     * Generate bytecode for a try statement.
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param tryStmt        the try statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        boolean hasCatch = tryStmt.getHandler().isPresent();
        boolean hasFinally = tryStmt.getFinalizer().isPresent();

        if (!hasCatch && !hasFinally) {
            throw new Swc4jByteCodeCompilerException(tryStmt,
                    "Try statement must have at least a catch or finally block");
        }

        // Check if all blocks are empty - in that case, skip the entire try-catch-finally
        // as it's a no-op but would generate invalid bytecode
        boolean tryEmpty = tryStmt.getBlock().getStmts().isEmpty();
        boolean catchEmpty = !hasCatch || tryStmt.getHandler().get().getBody().getStmts().isEmpty();
        boolean finallyEmpty = !hasFinally || tryStmt.getFinalizer().get().getStmts().isEmpty();

        if (tryEmpty && catchEmpty && finallyEmpty) {
            // All blocks are empty, nothing to generate
            return;
        }

        if (hasCatch && hasFinally) {
            generateTryCatchFinally(code, cp, tryStmt, returnTypeInfo);
        } else if (hasCatch) {
            generateTryCatch(code, cp, tryStmt, returnTypeInfo);
        } else {
            generateTryFinally(code, cp, tryStmt, returnTypeInfo);
        }
    }

    /**
     * Generate finally block code.
     * This code will be duplicated for each exit path (normal and exception).
     */
    private void generateFinallyBlock(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt finallyBlock,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : finallyBlock.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
        }
    }

    /**
     * Generate bytecode for try-catch (no finally).
     * <p>
     * Bytecode pattern:
     * <pre>
     * try_start:
     *     [try block code]
     *     goto after_catch
     * try_end:
     * catch_start:
     *     astore exception_var
     *     [catch block code]
     * after_catch:
     *     [continuation]
     *
     * Exception table:
     *     start_pc=try_start, end_pc=try_end, handler_pc=catch_start, catch_type=Throwable
     * </pre>
     */
    private void generateTryCatch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        boolean tryEndsWithTerminal = false;
        for (ISwc4jAstStmt stmt : tryBlock.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            if (isTerminalStatement(stmt)) {
                tryEndsWithTerminal = true;
                break;
            }
        }

        // Record try block end (before goto)
        int tryEndPc = code.getCurrentOffset();

        // Normal exit: skip catch block (only if try doesn't end with terminal statement)
        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;
        if (!tryEndsWithTerminal) {
            code.gotoLabel(0); // Placeholder
            gotoOffsetPos = code.getCurrentOffset() - 2;
            gotoOpcodePos = code.getCurrentOffset() - 3;
        }

        // Generate catch block
        int catchStartPc = code.getCurrentOffset();

        Swc4jAstCatchClause catchClause = tryStmt.getHandler().get();

        // Store exception in local variable if catch has a parameter
        String catchType = "Ljava/lang/Throwable;";  // Default to Throwable
        if (catchClause.getParam().isPresent()) {
            ISwc4jAstPat param = catchClause.getParam().get();
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                String varType = "Ljava/lang/Throwable;";

                // Check if variable type annotation exists
                if (bindingIdent.getTypeAnn().isPresent()) {
                    var typeAnn = bindingIdent.getTypeAnn().get();
                    varType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                    catchType = varType;  // Use annotated type for exception table
                }

                // Get the exception variable (already pre-allocated by VariableAnalyzer)
                var context = compiler.getMemory().getCompilationContext();
                LocalVariable exceptionVar = context.getLocalVariableTable().getVariable(varName);
                if (exceptionVar == null) {
                    // Fallback: allocate if not pre-allocated
                    context.getLocalVariableTable().allocateVariable(varName, varType);
                    exceptionVar = context.getLocalVariableTable().getVariable(varName);
                }

                // Store exception
                code.astore(exceptionVar.index());
            } else {
                // Catch without parameter or complex destructuring - pop exception
                code.pop();
            }
        } else {
            // Catch without parameter (ES2019+) - discard exception
            code.pop();
        }

        // Generate catch body
        Swc4jAstBlockStmt catchBody = catchClause.getBody();
        for (ISwc4jAstStmt stmt : catchBody.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
        }

        // Mark end of catch-try
        int afterCatchPc = code.getCurrentOffset();

        // Patch goto offset (only if goto was generated)
        if (gotoOffsetPos != -1) {
            int gotoOffset = afterCatchPc - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }

        // Add exception table entry - use the type from annotation if provided
        // Only add if there's actual code to protect
        if (tryStartPc < tryEndPc) {
            String catchClassName = catchType.substring(1, catchType.length() - 1);  // Remove L and ;
            int catchClassIndex = cp.addClass(catchClassName);
            code.addExceptionHandler(tryStartPc, tryEndPc, catchStartPc, catchClassIndex);
        }
    }

    /**
     * Generate bytecode for try-catch-finally.
     * <p>
     * Bytecode pattern:
     * <pre>
     * try_start:
     *     [try block code]
     *     [finally block code - copy 1]
     *     goto after_all
     * try_end:
     * catch_start:
     *     astore exception_var
     *     [catch block code]
     *     [finally block code - copy 2]
     *     goto after_all
     * catch_end:
     * finally_handler:
     *     astore temp_exception
     *     [finally block code - copy 3]
     *     aload temp_exception
     *     athrow
     * after_all:
     *     [continuation]
     *
     * Exception table (order matters):
     *     1. start_pc=try_start, end_pc=try_end, handler_pc=catch_start, catch_type=Throwable
     *     2. start_pc=try_start, end_pc=catch_end, handler_pc=finally_handler, catch_type=0
     * </pre>
     */
    private void generateTryCatchFinally(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Get the finally block and check if it ends with terminal statement
        Swc4jAstBlockStmt finalizerBlock = tryStmt.getFinalizer().get();
        boolean finallyEndsWithTerminal = blockEndsWithTerminal(finalizerBlock);

        // Push finally block onto context so return statements can find it
        var context = compiler.getMemory().getCompilationContext();
        context.pushFinallyBlock(finalizerBlock);

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        boolean tryEndsWithTerminal = false;
        for (ISwc4jAstStmt stmt : tryBlock.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            if (isTerminalStatement(stmt)) {
                tryEndsWithTerminal = true;
                break;
            }
        }

        // Record try block end
        int tryEndPc = code.getCurrentOffset();

        // Normal path: execute finally and continue (only if try doesn't end with terminal statement)
        int goto1OffsetPos = -1;
        int goto1OpcodePos = -1;
        if (!tryEndsWithTerminal) {
            generateFinallyBlock(code, cp, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                goto1OffsetPos = code.getCurrentOffset() - 2;
                goto1OpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Generate catch block
        int catchStartPc = code.getCurrentOffset();

        Swc4jAstCatchClause catchClause = tryStmt.getHandler().get();

        // Store exception in local variable if catch has a parameter
        String catchType = "Ljava/lang/Throwable;";  // Default to Throwable
        if (catchClause.getParam().isPresent()) {
            ISwc4jAstPat param = catchClause.getParam().get();
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                String varType = "Ljava/lang/Throwable;";

                // Check if variable type annotation exists
                if (bindingIdent.getTypeAnn().isPresent()) {
                    var typeAnn = bindingIdent.getTypeAnn().get();
                    varType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                    catchType = varType;  // Use annotated type for exception table
                }

                // Get the exception variable (already pre-allocated by VariableAnalyzer)
                LocalVariable exceptionVar = context.getLocalVariableTable().getVariable(varName);
                if (exceptionVar == null) {
                    // Fallback: allocate if not pre-allocated
                    context.getLocalVariableTable().allocateVariable(varName, varType);
                    exceptionVar = context.getLocalVariableTable().getVariable(varName);
                }

                // Store exception
                code.astore(exceptionVar.index());
            } else {
                // Catch without parameter or complex destructuring - pop exception
                code.pop();
            }
        } else {
            // Catch without parameter (ES2019+) - discard exception
            code.pop();
        }

        // Generate catch body
        Swc4jAstBlockStmt catchBody = catchClause.getBody();
        boolean catchEndsWithTerminal = false;
        for (ISwc4jAstStmt stmt : catchBody.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            if (isTerminalStatement(stmt)) {
                catchEndsWithTerminal = true;
                break;
            }
        }

        // Pop finally block from context (the try and catch block code has been generated)
        context.popFinallyBlock();

        // Record catch block end
        int catchEndPc = code.getCurrentOffset();

        // After catch: execute finally and continue (only if catch doesn't end with terminal statement)
        int goto2OffsetPos = -1;
        int goto2OpcodePos = -1;
        if (!catchEndsWithTerminal) {
            generateFinallyBlock(code, cp, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                goto2OffsetPos = code.getCurrentOffset() - 2;
                goto2OpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Finally handler: catches exceptions from both try and catch
        int finallyHandlerPc = code.getCurrentOffset();

        // Allocate temp slot for exception (only if we might rethrow)
        LocalVariable tempException = null;
        if (!finallyEndsWithTerminal) {
            String tempName = "$finallyException$" + System.identityHashCode(tryStmt);
            context.getLocalVariableTable().allocateVariable(tempName, "Ljava/lang/Throwable;");
            tempException = context.getLocalVariableTable().getVariable(tempName);

            // Store exception
            code.astore(tempException.index());
        } else {
            // Finally will throw, so we don't need the caught exception
            // But we still need to pop it from the stack
            code.pop();
        }

        // Execute finally
        generateFinallyBlock(code, cp, finalizerBlock, returnTypeInfo);

        // Re-throw exception (only if finally doesn't end with terminal)
        if (!finallyEndsWithTerminal) {
            code.aload(tempException.index());
            code.athrow();
        }

        // Mark end
        int afterAllPc = code.getCurrentOffset();

        // Patch goto offsets (only if they were generated)
        if (goto1OffsetPos != -1) {
            int goto1Offset = afterAllPc - goto1OpcodePos;
            code.patchShort(goto1OffsetPos, goto1Offset);
        }

        if (goto2OffsetPos != -1) {
            int goto2Offset = afterAllPc - goto2OpcodePos;
            code.patchShort(goto2OffsetPos, goto2Offset);
        }

        // Add exception table entries (order matters - first match wins)
        // Only add entries if there's actually code to protect
        if (tryStartPc < tryEndPc) {
            // Catch specified type in the try block
            String catchClassName = catchType.substring(1, catchType.length() - 1);  // Remove L and ;
            int catchClassIndex = cp.addClass(catchClassName);
            code.addExceptionHandler(tryStartPc, tryEndPc, catchStartPc, catchClassIndex);
        }
        if (tryStartPc < catchEndPc) {
            // Finally handler catches any exception (type 0)
            code.addExceptionHandler(tryStartPc, catchEndPc, finallyHandlerPc, 0);
        }
    }

    /**
     * Generate bytecode for try-finally (no catch).
     * <p>
     * Bytecode pattern:
     * <pre>
     * try_start:
     *     [try block code]
     *     [finally block code - copy 1]  (if normal exit)
     *     goto after_finally
     * try_end:
     * finally_handler:
     *     astore temp_exception
     *     [finally block code - copy 2]
     *     aload temp_exception
     *     athrow
     * after_finally:
     *     [continuation]
     *
     * Exception table:
     *     start_pc=try_start, end_pc=try_end, handler_pc=finally_handler, catch_type=0 (any)
     * </pre>
     * <p>
     * Note: For return statements inside the try block, the StatementGenerator
     * handles executing the finally block before returning by checking pending
     * finally blocks in the compilation context.
     */
    private void generateTryFinally(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Get the finally block
        Swc4jAstBlockStmt finalizerBlock = tryStmt.getFinalizer().get();
        boolean finallyEndsWithTerminal = blockEndsWithTerminal(finalizerBlock);

        // Push finally block onto context so return statements can find it
        var context = compiler.getMemory().getCompilationContext();
        context.pushFinallyBlock(finalizerBlock);

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        boolean tryEndsWithTerminal = false;
        for (ISwc4jAstStmt stmt : tryBlock.getStmts()) {
            compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            if (isTerminalStatement(stmt)) {
                tryEndsWithTerminal = true;
                break;
            }
        }

        // Pop finally block from context (the try block code has been generated)
        context.popFinallyBlock();

        // Record try block end (before normal path finally)
        int tryEndPc = code.getCurrentOffset();

        // Normal path: execute finally and continue (only if try doesn't end with terminal statement)
        // Note: Return statements inside try blocks handle their own finally execution
        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;
        if (!tryEndsWithTerminal) {
            generateFinallyBlock(code, cp, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                gotoOffsetPos = code.getCurrentOffset() - 2;
                gotoOpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Exception path: catch any exception, execute finally, re-throw
        int finallyHandlerPc = code.getCurrentOffset();

        // Allocate temp slot for exception (only if we might rethrow)
        LocalVariable tempException = null;
        if (!finallyEndsWithTerminal) {
            String tempName = "$finallyException$" + System.identityHashCode(tryStmt);
            context.getLocalVariableTable().allocateVariable(tempName, "Ljava/lang/Throwable;");
            tempException = context.getLocalVariableTable().getVariable(tempName);

            // Store exception
            code.astore(tempException.index());
        } else {
            // Finally will throw, so we don't need the caught exception
            // But we still need to pop it from the stack
            code.pop();
        }

        // Execute finally
        generateFinallyBlock(code, cp, finalizerBlock, returnTypeInfo);

        // Re-throw exception (only if finally doesn't end with terminal)
        if (!finallyEndsWithTerminal) {
            code.aload(tempException.index());
            code.athrow();
        }

        // Mark end
        int afterFinallyPc = code.getCurrentOffset();

        // Patch goto offset (only if generated)
        if (gotoOffsetPos != -1) {
            int gotoOffset = afterFinallyPc - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }

        // Add exception table entry (catch_type=0 means catch all exceptions)
        // Only add if there's actual code to protect
        if (tryStartPc < tryEndPc) {
            code.addExceptionHandler(tryStartPc, tryEndPc, finallyHandlerPc, 0);
        }
    }

    /**
     * Check if a statement is a terminal control flow statement (break, continue, return, throw).
     * Statements after a terminal statement are unreachable.
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
}
