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
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.UsingResourceInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Processor for {@code using} declaration statements.
 * Maps TypeScript's {@code using} to Java's try-with-resources pattern
 * (try-finally calling {@code AutoCloseable.close()}).
 *
 * <p>A {@code using} declaration wraps all remaining statements in the enclosing block
 * in nested try-finally blocks. Multiple declarators create nested try-finally blocks
 * with resources closed in reverse declaration order.</p>
 *
 * <p>Bytecode pattern for a single resource:</p>
 * <pre>
 *   // init resource
 *   expr; astore &lt;resource&gt;
 *   try_start:
 *     // remaining statements
 *   try_end:
 *     // normal path: null-safe close
 *     aload &lt;resource&gt;; ifnull skip; aload &lt;resource&gt;; invokeinterface close; skip:
 *     goto after_handler
 *   handler:
 *     astore &lt;exc&gt;
 *     aload &lt;resource&gt;; ifnull skip2; aload &lt;resource&gt;; invokeinterface close; skip2:
 *     aload &lt;exc&gt;; athrow
 *   after_handler:
 * </pre>
 */
public final class UsingDeclProcessor extends BaseAstProcessor<Swc4jAstUsingDecl> {

    /**
     * Instantiates a new Using declaration processor.
     *
     * @param compiler the compiler
     */
    public UsingDeclProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Direct generation is not supported for using declarations.
     * Use {@link #generateWithRemainingStatements} instead, which provides the
     * remaining statements context needed for wrapping.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param usingDecl      the using declaration
     * @param returnTypeInfo return type information
     * @throws Swc4jByteCodeCompilerException always, as direct generation is not supported
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUsingDecl usingDecl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        throw new Swc4jByteCodeCompilerException(getSourceCode(), usingDecl,
                "Using declaration must be processed with remaining statements context. "
                        + "Use generateWithRemainingStatements() instead.");
    }

    /**
     * Generate null-safe close bytecode for a resource.
     * Pattern: {@code aload <resource>; ifnull skip; aload <resource>; invokeinterface close; skip:}
     *
     * @param code         the code builder
     * @param classWriter  the class writer
     * @param resourceSlot the local variable slot of the resource
     */
    public void generateCloseCall(CodeBuilder code, ClassWriter classWriter, int resourceSlot) {
        var cp = classWriter.getConstantPool();
        // aload <resource>
        code.aload(resourceSlot);
        // ifnull skip (placeholder offset)
        int ifnullPos = code.getCurrentOffset();
        code.ifnull(0);
        int ifnullOffsetPos = code.getCurrentOffset() - 2;
        // aload <resource>
        code.aload(resourceSlot);
        // invokeinterface AutoCloseable.close:()V
        int closeRef = cp.addInterfaceMethodRef("java/lang/AutoCloseable", "close", "()V");
        code.invokeinterface(closeRef, 1);
        // skip:
        int skipPc = code.getCurrentOffset();
        // Patch ifnull offset
        code.patchShort(ifnullOffsetPos, skipPc - ifnullPos);
    }

    /**
     * Recursively generate nested try-finally blocks for each declarator.
     * Each declarator gets its own try-finally, with the next declarator (or remaining statements)
     * as the try body. Resources are closed in reverse declaration order.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param decls          the list of declarators
     * @param idx            the current declarator index
     * @param remainingStmts the remaining statements after the using declaration
     * @param returnTypeInfo return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private void generateDeclaratorChain(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstVarDeclarator> decls,
            int idx,
            List<ISwc4jAstStmt> remainingStmts,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        CompilationContext context = compiler.getMemory().getCompilationContext();

        Swc4jAstVarDeclarator declarator = decls.get(idx);
        ISwc4jAstPat name = declarator.getName();

        if (!(name instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), declarator,
                    "Unsupported using declaration pattern: " + name.getClass().getSimpleName());
        }

        String varName = bindingIdent.getId().getSym();
        String varType = compiler.getTypeResolver().extractType(bindingIdent, declarator.getInit());
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        if (localVar == null) {
            // Try to re-add from allVariables (pre-allocated by VariableAnalyzer in a different scope instance)
            localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
        }
        if (localVar == null) {
            // Allocate fresh if not pre-allocated
            context.getLocalVariableTable().allocateVariable(varName, varType, false);
            context.getInferredTypes().put(varName, varType);
            localVar = context.getLocalVariableTable().getVariable(varName);
        }

        // Generate initializer expression and store in local variable
        if (declarator.getInit().isPresent()) {
            var init = declarator.getInit().get();
            ReturnTypeInfo varTypeInfo = ReturnTypeInfo.of(getSourceCode(), declarator, localVar.type(), null);
            compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
            code.astore(localVar.index());
        } else {
            // No initializer — store null
            code.aconst_null();
            code.astore(localVar.index());
        }

        // Create sentinel block and register in context
        Swc4jAstBlockStmt sentinel = Swc4jAstBlockStmt.create();
        UsingResourceInfo resourceInfo = new UsingResourceInfo(localVar.index(), localVar.type(), varName);
        context.registerUsingResource(sentinel, resourceInfo);

        // Push sentinel as a finally block
        context.pushFinallyBlock(sentinel);

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try body: next declarator or remaining statements
        boolean tryEndsWithTerminal = false;
        if (idx + 1 < decls.size()) {
            // More declarators — recurse
            generateDeclaratorChain(code, classWriter, decls, idx + 1, remainingStmts, returnTypeInfo);
            // We can't easily detect terminal from recursive chain, assume non-terminal
            // The terminal check is handled by the individual statement generation
        } else {
            // Last declarator — generate remaining statements using list-aware generate
            // which handles nested using declarations properly
            compiler.getStatementProcessor().generate(code, classWriter, remainingStmts, returnTypeInfo);
            for (ISwc4jAstStmt s : remainingStmts) {
                if (isTerminalStatement(s)) {
                    tryEndsWithTerminal = true;
                    break;
                }
            }
        }

        // Pop the finally block from context
        context.popFinallyBlock();

        // Record try block end
        int tryEndPc = code.getCurrentOffset();

        // If there's actual code to protect, generate the try-finally pattern
        if (tryStartPc < tryEndPc) {
            // Normal path: close resource (only if try block doesn't end with terminal)
            int gotoOffsetPos = -1;
            int gotoOpcodePos = -1;
            if (!tryEndsWithTerminal) {
                generateCloseCall(code, classWriter, localVar.index());
                // goto after exception handler
                code.gotoLabel(0); // placeholder
                gotoOffsetPos = code.getCurrentOffset() - 2;
                gotoOpcodePos = code.getCurrentOffset() - 3;
            }

            // Exception handler: catch all exceptions, close resource, rethrow
            int handlerPc = code.getCurrentOffset();

            // Allocate temp variable for exception
            String tempName = "$usingException$" + System.identityHashCode(declarator);
            context.getLocalVariableTable().allocateVariable(tempName, "Ljava/lang/Throwable;");
            LocalVariable tempException = context.getLocalVariableTable().getVariable(tempName);

            // Store exception
            code.astore(tempException.index());

            // Close resource (null-safe)
            generateCloseCall(code, classWriter, localVar.index());

            // Rethrow exception
            code.aload(tempException.index());
            code.athrow();

            // After handler
            int afterHandlerPc = code.getCurrentOffset();

            // Patch goto offset
            if (gotoOffsetPos != -1) {
                int gotoOffset = afterHandlerPc - gotoOpcodePos;
                code.patchShort(gotoOffsetPos, gotoOffset);
            }

            // Add exception table entry (catch_type=0 for catch-all)
            code.addExceptionHandler(tryStartPc, tryEndPc, handlerPc, 0);
        } else {
            // Empty try body — just close the resource on the normal path
            if (!tryEndsWithTerminal) {
                generateCloseCall(code, classWriter, localVar.index());
            }
        }

        // Unregister the sentinel
        context.unregisterUsingResource(sentinel);
    }

    /**
     * Generate inline close bytecode for return/break/continue handlers.
     * Called when a control flow transfer is encountered inside a using scope.
     *
     * @param code         the code builder
     * @param classWriter  the class writer
     * @param resourceSlot the local variable slot of the resource
     */
    public void generateInlineClose(CodeBuilder code, ClassWriter classWriter, int resourceSlot) {
        generateCloseCall(code, classWriter, resourceSlot);
    }

    /**
     * Generate bytecode for a using declaration with remaining statements.
     * This is the main entry point for using declaration processing.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param usingDecl      the using declaration
     * @param remainingStmts the remaining statements after the using declaration
     * @param returnTypeInfo return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generateWithRemainingStatements(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUsingDecl usingDecl,
            List<ISwc4jAstStmt> remainingStmts,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Reject await using
        if (usingDecl.isAwait()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), usingDecl,
                    "await using is not supported");
        }

        List<Swc4jAstVarDeclarator> decls = usingDecl.getDecls();
        if (decls.isEmpty()) {
            return;
        }

        // Start recursive chain from the first declarator
        generateDeclaratorChain(code, classWriter, decls, 0, remainingStmts, returnTypeInfo);
    }

    /**
     * Check if a statement is a terminal control flow statement.
     *
     * @param stmt the statement to check
     * @return true if the statement is terminal
     */
    private boolean isTerminalStatement(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            for (ISwc4jAstStmt s : blockStmt.getStmts()) {
                if (isTerminalStatement(s)) {
                    return true;
                }
            }
            return false;
        }
        return stmt instanceof Swc4jAstBreakStmt
                || stmt instanceof Swc4jAstContinueStmt
                || stmt instanceof Swc4jAstReturnStmt
                || stmt instanceof Swc4jAstThrowStmt;
    }
}
