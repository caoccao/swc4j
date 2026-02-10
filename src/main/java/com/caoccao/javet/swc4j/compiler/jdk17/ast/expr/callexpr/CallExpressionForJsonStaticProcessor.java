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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
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
 * Processes call expressions for JSON static methods (JSON.stringify, JSON.parse).
 */
public final class CallExpressionForJsonStaticProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForJsonStaticProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            return;
        }
        String methodName = null;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            methodName = propIdent.getSym();
        }
        if (methodName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "JSON method name not supported");
        }
        switch (methodName) {
            case ConstantJavaMethod.METHOD_STRINGIFY -> generateStringify(code, classWriter, callExpr);
            case ConstantJavaMethod.METHOD_PARSE -> generateParse(code, classWriter, callExpr);
            default ->
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "JSON." + methodName + "() not supported");
        }
    }

    private void generateParse(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "JSON.parse() requires at least one argument");
        }
        var arg = callExpr.getArgs().get(0);
        if (arg.getSpread().isPresent()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported for JSON.parse");
        }
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
        // Ensure the argument is a String on the stack
        String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
        if (argType != null && !ConstantJavaType.LJAVA_LANG_STRING.equals(argType)) {
            if (TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }
            // Convert to String via toString
            var cp = classWriter.getConstantPool();
            int toStringRef = cp.addMethodRef(
                    ConstantJavaType.JAVA_LANG_OBJECT,
                    ConstantJavaMethod.METHOD_TO_STRING,
                    ConstantJavaDescriptor.__LJAVA_LANG_STRING);
            code.invokevirtual(toStringRef);
        }
        var cp = classWriter.getConstantPool();
        int parseMethod = cp.addMethodRef(
                ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_UTILS_JSON_JSON_UTILS,
                ConstantJavaMethod.METHOD_PARSE,
                ConstantJavaDescriptor.LJAVA_LANG_STRING__LJAVA_LANG_OBJECT);
        code.invokestatic(parseMethod);
    }

    private void generateStringify(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        var args = callExpr.getArgs();

        if (args.isEmpty()) {
            // 0 args: return "undefined"
            int strIndex = cp.addString("undefined");
            code.ldc(strIndex);
            return;
        }

        if (args.size() == 1) {
            // 1 arg: stringify(Object)
            var arg = args.get(0);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported for JSON.stringify");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }
            int stringifyMethod = cp.addMethodRef(
                    ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_UTILS_JSON_JSON_UTILS,
                    ConstantJavaMethod.METHOD_STRINGIFY,
                    ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
            code.invokestatic(stringifyMethod);
            return;
        }

        // 2+ args: stringify(Object, Object, Object)
        // Evaluate value
        var valueArg = args.get(0);
        if (valueArg.getSpread().isPresent()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), valueArg, "Spread arguments not supported for JSON.stringify");
        }
        compiler.getExpressionProcessor().generate(code, classWriter, valueArg.getExpr(), null);
        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, TypeConversionUtils.getWrapperType(valueType));
        }

        // Evaluate replacer
        var replacerArg = args.get(1);
        if (replacerArg.getSpread().isPresent()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), replacerArg, "Spread arguments not supported for JSON.stringify");
        }
        ISwc4jAstExpr replacerExpr = replacerArg.getExpr();
        ReturnTypeInfo replacerTargetType = null;
        if (replacerExpr instanceof Swc4jAstArrowExpr || replacerExpr instanceof Swc4jAstFnExpr) {
            replacerTargetType = ReturnTypeInfo.of(getSourceCode(), replacerArg, ConstantJavaType.LJAVA_UTIL_FUNCTION_BI_FUNCTION);
        }
        compiler.getExpressionProcessor().generate(code, classWriter, replacerExpr, replacerTargetType);
        String replacerType = compiler.getTypeResolver().inferTypeFromExpr(replacerExpr);
        if (replacerType != null && TypeConversionUtils.isPrimitiveType(replacerType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, replacerType, TypeConversionUtils.getWrapperType(replacerType));
        }

        // Evaluate space (or push null if only 2 args)
        if (args.size() >= 3) {
            var spaceArg = args.get(2);
            if (spaceArg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), spaceArg, "Spread arguments not supported for JSON.stringify");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, spaceArg.getExpr(), null);
            String spaceType = compiler.getTypeResolver().inferTypeFromExpr(spaceArg.getExpr());
            if (spaceType != null && TypeConversionUtils.isPrimitiveType(spaceType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, spaceType, TypeConversionUtils.getWrapperType(spaceType));
            }
        } else {
            code.aconst_null();
        }

        int stringifyMethod = cp.addMethodRef(
                ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_UTILS_JSON_JSON_UTILS,
                ConstantJavaMethod.METHOD_STRINGIFY,
                ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
        code.invokestatic(stringifyMethod);
    }
}
