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
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.ts.TsAsExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ExpressionGenerator extends BaseAstProcessor {
    public ExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        if (expr instanceof Swc4jAstTsAsExpr tsAsExpr) {
            TsAsExpressionGenerator.generate(compiler, code, cp, tsAsExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstStr str) {
            compiler.getStringLiteralGenerator().generate(code, cp, str, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNumber number) {
            compiler.getNumberLiteralGenerator().generate(code, cp, number, returnTypeInfo);
        } else if (expr instanceof Swc4jAstBool bool) {
            compiler.getBoolLiteralGenerator().generate(code, cp, bool, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNull nullLit) {
            compiler.getNullLiteralGenerator().generate(code, cp, nullLit, returnTypeInfo);
        } else if (expr instanceof Swc4jAstArrayLit arrayLit) {
            compiler.getArrayLiteralGenerator().generate(code, cp, arrayLit, returnTypeInfo, this::generate);
        } else if (expr instanceof Swc4jAstObjectLit objectLit) {
            compiler.getObjectLiteralGenerator().generate(code, cp, objectLit, returnTypeInfo, this::generate);
        } else if (expr instanceof Swc4jAstIdent ident) {
            compiler.getIdentifierGenerator().generate(code, cp, ident, returnTypeInfo);
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            compiler.getMemberExpressionGenerator().generate(code, cp, memberExpr);
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            compiler.getCallExpressionGenerator().generate(code, cp, callExpr);
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            compiler.getAssignExpressionGenerator().generate(code, cp, assignExpr);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            compiler.getBinaryExpressionGenerator().generate(code, cp, binExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            compiler.getUnaryExpressionGenerator().generate(code, cp, unaryExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            compiler.getUpdateExpressionGenerator().generate(code, cp, updateExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstCondExpr condExpr) {
            compiler.getConditionalExpressionGenerator().generate(code, cp, condExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            compiler.getParenExpressionGenerator().generate(code, cp, parenExpr, returnTypeInfo, this::generate);
        } else if (expr instanceof Swc4jAstSeqExpr seqExpr) {
            compiler.getSeqExpressionGenerator().generate(code, cp, seqExpr, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException("Unsupported expression type: " + expr.getClass().getSimpleName());
        }
    }
}
