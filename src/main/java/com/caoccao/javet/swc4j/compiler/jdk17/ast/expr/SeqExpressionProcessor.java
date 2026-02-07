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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSeqExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for sequence expressions (comma operator).
 * Evaluates all expressions in sequence, leaving only the last value on the stack.
 */
public final class SeqExpressionProcessor extends BaseAstProcessor<Swc4jAstSeqExpr> {
    /**
     * Instantiates a new Seq expression processor.
     *
     * @param compiler the compiler
     */
    public SeqExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for a sequence expression (comma operator).
     * Evaluates all expressions in sequence, leaving only the last value on the stack.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param seqExpr        the sequence expression AST node
     * @param returnTypeInfo return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstSeqExpr seqExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        var exprs = seqExpr.getExprs();
        for (int i = 0; i < exprs.size(); i++) {
            ISwc4jAstExpr expr = exprs.get(i);
            boolean isLast = (i == exprs.size() - 1);

            // Generate the expression
            compiler.getExpressionProcessor().generate(code, classWriter, expr, isLast ? returnTypeInfo : null);

            // Pop the result of non-last expressions (they're evaluated for side effects only)
            if (!isLast) {
                String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
                if (exprType != null) {
                    TypeConversionUtils.popByType(code, exprType);
                }
            }
        }
    }
}
