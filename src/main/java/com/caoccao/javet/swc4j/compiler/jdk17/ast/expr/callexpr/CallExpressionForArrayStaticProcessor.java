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
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Processes call expressions for Array static methods.
 */
public final class CallExpressionForArrayStaticProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
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
            case ConstantJavaMethod.METHOD_IS_ARRAY -> generateIsArray(code, classWriter, callExpr);
            case ConstantJavaMethod.METHOD_FROM -> generateFrom(code, classWriter, callExpr);
            case ConstantJavaMethod.METHOD_OF -> generateOf(code, classWriter, callExpr);
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
                ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAY_STATIC_API_UTILS,
                ConstantJavaMethod.METHOD_FROM,
                ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_UTIL_ARRAYLIST);
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
                ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAY_STATIC_API_UTILS,
                ConstantJavaMethod.METHOD_IS_ARRAY,
                ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
        code.invokestatic(isArrayMethod);
    }

    private void generateOf(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
        int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
        int arrayListAdd = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);

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

    /**
     * Checks if the given callee is supported by this processor.
     *
     * @param callee the callee to check
     * @return true if the callee is an Array static method call
     */
    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        if (!(callee instanceof Swc4jAstMemberExpr memberExpr)) {
            return false;
        }
        if (memberExpr.getObj() instanceof Swc4jAstIdent ident) {
            return ConstantJavaType.TYPE_ALIAS_ARRAY.equals(ident.getSym());
        }
        return false;
    }
}
