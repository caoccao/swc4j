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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForArrayGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForArrayListGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForJavaClassGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForStringGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    private final CallExpressionForArrayGenerator arrayGenerator;
    private final CallExpressionForArrayListGenerator arrayListGenerator;
    private final CallExpressionForJavaClassGenerator javaClassGenerator;
    private final CallExpressionForStringGenerator stringGenerator;

    public CallExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
        arrayGenerator = new CallExpressionForArrayGenerator(compiler);
        arrayListGenerator = new CallExpressionForArrayListGenerator(compiler);
        javaClassGenerator = new CallExpressionForJavaClassGenerator(compiler);
        stringGenerator = new CallExpressionForStringGenerator(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Handle method calls on arrays, strings, Java classes, etc.
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Check for Java class method calls first
            if (javaClassGenerator.isJavaClassMethodCall(callExpr)) {
                javaClassGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            }

            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
            if (arrayGenerator.isTypeSupported(objType)) {
                arrayGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (arrayListGenerator.isTypeSupported(objType)) {
                arrayListGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (stringGenerator.isTypeSupported(objType)) {
                stringGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Call expression not yet supported");
    }
}
