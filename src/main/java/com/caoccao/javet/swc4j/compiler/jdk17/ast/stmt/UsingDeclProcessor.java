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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstUsingDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Processor for {@code using} declaration statements.
 * Maps TypeScript's {@code using} to Java's try-with-resources pattern
 * (try-finally calling {@code AutoCloseable.close()}).
 *
 * <p>A {@code using} declaration wraps all remaining statements in the enclosing block
 * in nested try-finally blocks. Multiple declarators create nested try-finally blocks
 * with resources closed in reverse declaration order.</p>
 *
 * <p>Bytecode pattern for a single resource (with suppressed exception support):</p>
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
 *     astore &lt;primaryExc&gt;
 *     // close with suppression: if close() throws, addSuppressed to primaryExc
 *     aload &lt;resource&gt;; ifnull rethrow
 *     try_close: aload &lt;resource&gt;; invokeinterface close; goto rethrow
 *     catch_close: astore &lt;suppressedExc&gt;; aload &lt;primaryExc&gt;; aload &lt;suppressedExc&gt;;
 *                  invokevirtual Throwable.addSuppressed
 *     rethrow: aload &lt;primaryExc&gt;; athrow
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
     * Use {@link com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.UsingResourceUtils#generateWithRemainingStatements}
     * instead, which provides the remaining statements context needed for wrapping.
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
                        + "Use UsingResourceUtils.generateWithRemainingStatements() instead.");
    }
}
