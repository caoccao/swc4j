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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.ts.TsAsExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ExpressionGenerator {
    private ExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        if (expr instanceof Swc4jAstTsAsExpr tsAsExpr) {
            TsAsExpressionGenerator.generate(code, cp, tsAsExpr, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstStr str) {
            StringLiteralGenerator.generate(code, cp, str, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstNumber number) {
            NumberLiteralGenerator.generate(code, cp, number, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstBool bool) {
            BoolLiteralGenerator.generate(code, cp, bool, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstNull nullLit) {
            NullLiteralGenerator.generate(code, cp, nullLit, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstArrayLit arrayLit) {
            ArrayLiteralGenerator.generate(code, cp, arrayLit, returnTypeInfo, context, options, ExpressionGenerator::generate);
        } else if (expr instanceof Swc4jAstObjectLit objectLit) {
            ObjectLiteralGenerator.generate(code, cp, objectLit, returnTypeInfo, context, options, ExpressionGenerator::generate);
        } else if (expr instanceof Swc4jAstIdent ident) {
            IdentifierGenerator.generate(code, cp, ident, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            MemberExpressionGenerator.generate(code, cp, memberExpr, context, options);
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            CallExpressionGenerator.generate(code, cp, callExpr, context, options);
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            AssignExpressionGenerator.generate(code, cp, assignExpr, context, options);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            BinaryExpressionGenerator.generate(code, cp, binExpr, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            UnaryExpressionGenerator.generate(code, cp, unaryExpr, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            UpdateExpressionGenerator.generate(code, cp, updateExpr, returnTypeInfo, context, options);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            ParenExpressionGenerator.generate(code, cp, parenExpr, returnTypeInfo, context, options, ExpressionGenerator::generate);
        }
    }
}
