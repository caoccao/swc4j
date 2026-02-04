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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ExpressionGenerator extends BaseAstProcessor<ISwc4jAstExpr> {
    public ExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (expr instanceof Swc4jAstTsAsExpr tsAsExpr) {
            compiler.getTsAsExpressionGenerator().generate(code, classWriter, tsAsExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstStr str) {
            compiler.getStringLiteralGenerator().generate(code, classWriter, str, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNumber number) {
            compiler.getNumberLiteralGenerator().generate(code, classWriter, number, returnTypeInfo);
        } else if (expr instanceof Swc4jAstBigInt bigInt) {
            compiler.getBigIntLiteralGenerator().generate(code, classWriter, bigInt, returnTypeInfo);
        } else if (expr instanceof Swc4jAstBool bool) {
            compiler.getBoolLiteralGenerator().generate(code, classWriter, bool, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNull nullLit) {
            compiler.getNullLiteralGenerator().generate(code, classWriter, nullLit, returnTypeInfo);
        } else if (expr instanceof Swc4jAstRegex regex) {
            compiler.getRegexLiteralGenerator().generate(code, classWriter, regex, returnTypeInfo);
        } else if (expr instanceof Swc4jAstArrayLit arrayLit) {
            compiler.getArrayLiteralGenerator().generate(code, classWriter, arrayLit, returnTypeInfo);
        } else if (expr instanceof Swc4jAstObjectLit objectLit) {
            compiler.getObjectLiteralGenerator().generate(code, classWriter, objectLit, returnTypeInfo);
        } else if (expr instanceof Swc4jAstIdent ident) {
            compiler.getIdentifierGenerator().generate(code, classWriter, ident, returnTypeInfo);
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            compiler.getMemberExpressionGenerator().generate(code, classWriter, memberExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            compiler.getCallExpressionGenerator().generate(code, classWriter, callExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstNewExpr newExpr) {
            compiler.getNewExpressionGenerator().generate(code, classWriter, newExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            compiler.getAssignExpressionGenerator().generate(code, classWriter, assignExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            compiler.getBinaryExpressionGenerator().generate(code, classWriter, binExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            compiler.getUnaryExpressionGenerator().generate(code, classWriter, unaryExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            compiler.getUpdateExpressionGenerator().generate(code, classWriter, updateExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstOptChainExpr optChainExpr) {
            compiler.getOptionalChainExpressionGenerator().generate(code, classWriter, optChainExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstCondExpr condExpr) {
            compiler.getConditionalExpressionGenerator().generate(code, classWriter, condExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            compiler.getParenExpressionGenerator().generate(code, classWriter, parenExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstSeqExpr seqExpr) {
            compiler.getSeqExpressionGenerator().generate(code, classWriter, seqExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstThisExpr thisExpr) {
            compiler.getThisExpressionGenerator().generate(code, classWriter, thisExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstArrowExpr arrowExpr) {
            compiler.getArrowExpressionGenerator().generate(code, classWriter, arrowExpr, returnTypeInfo);
        } else if (expr instanceof Swc4jAstTpl tpl) {
            compiler.getTemplateLiteralGenerator().generate(code, classWriter, tpl, returnTypeInfo);
        } else if (expr instanceof Swc4jAstTaggedTpl taggedTpl) {
            compiler.getTaggedTemplateLiteralGenerator().generate(code, classWriter, taggedTpl, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), expr, "Unsupported expression type: " + expr.getClass().getSimpleName());
        }
    }
}
