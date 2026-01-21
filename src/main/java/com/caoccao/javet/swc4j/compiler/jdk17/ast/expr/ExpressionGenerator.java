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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.ts.TsAsExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ExpressionGenerator {
    private ExpressionGenerator() {
    }

    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        if (expr instanceof Swc4jAstTsAsExpr tsAsExpr) {
            TsAsExpressionGenerator.generate(compiler, code, cp, tsAsExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstStr str) {
            StringLiteralGenerator.generate(compiler, code, cp, str, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNumber number) {
            NumberLiteralGenerator.generate(compiler, code, cp, number, returnTypeInfo);
        } else if (expr instanceof Swc4jAstBool bool) {
            BoolLiteralGenerator.generate(compiler, code, cp, bool, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNull nullLit) {
            NullLiteralGenerator.generate(compiler, code, cp, nullLit, returnTypeInfo);
        } else if (expr instanceof Swc4jAstArrayLit arrayLit) {
            ArrayLiteralGenerator.generate(compiler, code, cp, arrayLit, returnTypeInfo, ExpressionGenerator::generate);
        } else if (expr instanceof Swc4jAstObjectLit objectLit) {
            ObjectLiteralGenerator.generate(compiler, code, cp, objectLit, returnTypeInfo, ExpressionGenerator::generate);
        } else if (expr instanceof Swc4jAstIdent ident) {
            IdentifierGenerator.generate(compiler, code, cp, ident, returnTypeInfo);
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            MemberExpressionGenerator.generate(compiler, code, cp, memberExpr);
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            CallExpressionGenerator.generate(compiler, code, cp, callExpr);
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            AssignExpressionGenerator.generate(compiler, code, cp, assignExpr);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            BinaryExpressionGenerator.generate(compiler, code, cp, binExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            UnaryExpressionGenerator.generate(compiler, code, cp, unaryExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            UpdateExpressionGenerator.generate(compiler, code, cp, updateExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstCondExpr condExpr) {
            ConditionalExpressionGenerator.generate(compiler, code, cp, condExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            ParenExpressionGenerator.generate(compiler, code, cp, parenExpr, returnTypeInfo, ExpressionGenerator::generate);
        } else if (expr instanceof Swc4jAstSeqExpr seqExpr) {
            SeqExpressionGenerator.generate(compiler, code, cp, seqExpr, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException("Unsupported expression type: " + expr.getClass().getSimpleName());
        }
    }
}
