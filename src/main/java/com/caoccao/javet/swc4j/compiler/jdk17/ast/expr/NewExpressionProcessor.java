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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;
import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates bytecode for constructor calls (new expressions).
 */
public final class NewExpressionProcessor extends BaseAstProcessor<Swc4jAstNewExpr> {

    /**
     * Instantiates a new New expression processor.
     *
     * @param compiler the compiler
     */
    public NewExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void convertType(CodeBuilder code, ClassWriter classWriter, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return;
        }
        if (TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.convertPrimitiveType(code, fromType, toType);
        } else if (TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(fromType);
            TypeConversionUtils.boxPrimitiveType(code, classWriter, fromType, wrapperType);
        } else if (!TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.unboxWrapperType(code, classWriter, fromType);
        }
    }

    private Swc4jAstClassExpr extractClassExpr(ISwc4jAstExpr callee) {
        if (callee instanceof Swc4jAstClassExpr classExpr) {
            return classExpr;
        }
        if (callee instanceof Swc4jAstParenExpr parenExpr) {
            return extractClassExpr(parenExpr.getExpr());
        }
        return null;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstNewExpr newExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        ISwc4jAstExpr callee = newExpr.getCallee();

        // Only support simple class name constructors for now
        String internalClassName;
        String className;
        Swc4jAstClassExpr classExpr = extractClassExpr(callee);
        if (classExpr != null) {
            var info = compiler.getClassExpressionProcessor().ensureClassGenerated(classExpr, classWriter);
            className = info.className();
            internalClassName = info.internalName();
        } else if (callee instanceof Swc4jAstIdent ident) {
            className = ident.getSym();

            // Resolve the class name using type alias registry
            String resolvedType = compiler.getMemory().getScopedTypeAliasRegistry().resolve(className);
            if (resolvedType == null) {
                // If not found in type alias registry, assume it's in the current package
                resolvedType = compiler.getOptions().packagePrefix().isEmpty()
                        ? className
                        : compiler.getOptions().packagePrefix() + "." + className;
            }

            // Convert qualified name to internal name: com.example.Foo -> com/example/Foo
            internalClassName = resolvedType.replace('.', '/');
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), newExpr, "Only simple class names or class expressions supported in new expressions");
        }

        // Generate: new <class>
        int classRef = cp.addClass(internalClassName);
        code.newInstance(classRef);

        // Duplicate the reference for the constructor call
        code.dup();

        List<Swc4jAstExprOrSpread> args = newExpr.getArgs().orElse(List.of());
        List<String> argTypes = new ArrayList<>();
        for (Swc4jAstExprOrSpread arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not supported in constructor calls");
            }
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            argTypes.add(argType);
        }

        JavaTypeInfo javaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
        MethodInfo constructorInfo = javaTypeInfo != null ? javaTypeInfo.getMethod(ConstantJavaMethod.METHOD_INIT, argTypes) : null;

        if (constructorInfo != null) {
            List<String> expectedTypes = ScoreUtils.parseParameterDescriptors(constructorInfo.descriptor());
            if (constructorInfo.isVarArgs() && !expectedTypes.isEmpty()) {
                int fixedCount = expectedTypes.size() - 1;
                String varargArrayType = expectedTypes.get(expectedTypes.size() - 1);
                String componentType = varargArrayType.startsWith(ConstantJavaType.ARRAY_PREFIX) ? varargArrayType.substring(1) : varargArrayType;

                boolean directArrayPass = args.size() == expectedTypes.size()
                        && argTypes.get(argTypes.size() - 1).equals(varargArrayType);

                if (!directArrayPass) {
                    for (int i = 0; i < fixedCount; i++) {
                        Swc4jAstExprOrSpread arg = args.get(i);
                        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                        convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                    }
                    generateVarargsArray(code, classWriter, args, argTypes, fixedCount, varargArrayType, componentType);
                } else {
                    for (int i = 0; i < args.size(); i++) {
                        Swc4jAstExprOrSpread arg = args.get(i);
                        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                        convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                    }
                }
            } else {
                for (int i = 0; i < args.size(); i++) {
                    Swc4jAstExprOrSpread arg = args.get(i);
                    compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                    if (i < expectedTypes.size()) {
                        convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                    }
                }
            }

            int constructorRef = cp.addMethodRef(internalClassName, ConstantJavaMethod.METHOD_INIT, constructorInfo.descriptor());
            code.invokespecial(constructorRef);
        } else {
            StringBuilder paramDescriptors = new StringBuilder();
            for (int i = 0; i < args.size(); i++) {
                Swc4jAstExprOrSpread arg = args.get(i);
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                paramDescriptors.append(argTypes.get(i));
            }
            String constructorDescriptor = "(" + paramDescriptors + ")V";
            int constructorRef = cp.addMethodRef(internalClassName, ConstantJavaMethod.METHOD_INIT, constructorDescriptor);
            code.invokespecial(constructorRef);
        }

        // After this, the new object reference is on the stack
    }

    private void generateVarargsArray(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstExprOrSpread> args,
            List<String> argTypes,
            int startIndex,
            String arrayType,
            String componentType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int varargCount = args.size() - startIndex;
        code.iconst(varargCount);

        if (TypeConversionUtils.isPrimitiveType(componentType)) {
            code.newarray(TypeConversionUtils.getNewarrayTypeCode(componentType));
        } else {
            String internalName = componentType.substring(1, componentType.length() - 1);
            int classIndex = cp.addClass(internalName);
            code.anewarray(classIndex);
        }

        for (int i = 0; i < varargCount; i++) {
            code.dup();
            code.iconst(i);
            Swc4jAstExprOrSpread arg = args.get(startIndex + i);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = argTypes.get(startIndex + i);
            convertType(code, classWriter, argType, componentType);

            switch (componentType) {
                case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore();
                case ConstantJavaType.ABBR_CHARACTER -> code.castore();
                case ConstantJavaType.ABBR_SHORT -> code.sastore();
                case ConstantJavaType.ABBR_INTEGER -> code.iastore();
                case ConstantJavaType.ABBR_LONG -> code.lastore();
                case ConstantJavaType.ABBR_FLOAT -> code.fastore();
                case ConstantJavaType.ABBR_DOUBLE -> code.dastore();
                default -> code.aastore();
            }
        }
    }
}
