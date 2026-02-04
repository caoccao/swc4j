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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionForArrayStaticProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForArrayStaticProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            return;
        }
        String methodName = null;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            methodName = propIdent.getSym();
        }
        if (methodName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Array static method name not supported");
        }
        switch (methodName) {
            case "isArray" -> generateIsArray(code, classWriter, callExpr);
            case "from" -> generateFrom(code, classWriter, callExpr);
            case "of" -> generateOf(code, classWriter, callExpr);
            default ->
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Array." + methodName + "() not supported");
        }
    }

    private void generateFrom(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Array.from() requires an argument");
        }
        if (callExpr.getArgs().size() > 1) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Array.from() with mapping function not supported");
        }
        var arg = callExpr.getArgs().get(0);
        if (arg.getSpread().isPresent()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported");
        }
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
        String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
        if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
        }
        var cp = classWriter.getConstantPool();
        int fromMethod = cp.addMethodRef(
                "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayStaticApiUtils",
                "from",
                "(Ljava/lang/Object;)Ljava/util/ArrayList;");
        code.invokestatic(fromMethod);
    }

    private void generateIsArray(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            code.iconst(0);
            return;
        }
        var arg = callExpr.getArgs().get(0);
        if (arg.getSpread().isPresent()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported");
        }
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
        String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
        if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
        }
        var cp = classWriter.getConstantPool();
        int isArrayMethod = cp.addMethodRef(
                "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayStaticApiUtils",
                "isArray",
                "(Ljava/lang/Object;)Z");
        code.invokestatic(isArrayMethod);
    }

    private void generateOf(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int arrayListClass = cp.addClass("java/util/ArrayList");
        int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
        int arrayListAdd = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

        code.newInstance(arrayListClass);
        code.dup();
        code.invokespecial(arrayListInit);

        for (var arg : callExpr.getArgs()) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported");
            }
            code.dup();
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }
            code.invokevirtual(arrayListAdd);
            code.pop();
        }
    }

    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        if (!(callee instanceof Swc4jAstMemberExpr memberExpr)) {
            return false;
        }
        if (memberExpr.getObj() instanceof Swc4jAstIdent ident) {
            return "Array".equals(ident.getSym());
        }
        return false;
    }
}
