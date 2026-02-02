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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstTaggedTpl;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstTpl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Generator for tagged template literals.
 * <p>
 * Compiles tagged templates (e.g., {@code tag`Hello ${name}!`}) to method calls
 * where the tag function receives a String[] of template strings followed by
 * the interpolated expression values as individual arguments.
 * <p>
 * {@code this.tag`Hello ${name}!`} compiles to {@code this.tag(new String[]{"Hello ", "!"}, name)}
 */
public final class TaggedTemplateLiteralGenerator extends BaseAstProcessor<Swc4jAstTaggedTpl> {
    public TaggedTemplateLiteralGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTaggedTpl taggedTpl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr tag = taggedTpl.getTag();
        Swc4jAstTpl tpl = taggedTpl.getTpl();
        List<Swc4jAstTplElement> quasis = tpl.getQuasis();
        List<ISwc4jAstExpr> exprs = tpl.getExprs();

        if (tag instanceof Swc4jAstMemberExpr memberExpr) {
            generateMemberExprTagCall(code, cp, taggedTpl, memberExpr, quasis, exprs, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(taggedTpl,
                    "Unsupported tag expression type: " + tag.getClass().getSimpleName());
        }
    }

    private void generateMemberExprTagCall(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTaggedTpl taggedTpl,
            Swc4jAstMemberExpr memberExpr,
            List<Swc4jAstTplElement> quasis,
            List<ISwc4jAstExpr> exprs,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Determine the method name from the member expression property
        String methodName;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdentName) {
            methodName = propIdentName.getSym();
        } else if (memberExpr.getProp() instanceof Swc4jAstIdent propIdent) {
            methodName = propIdent.getSym();
        } else {
            throw new Swc4jByteCodeCompilerException(taggedTpl,
                    "Unsupported tag property type: " + memberExpr.getProp().getClass().getSimpleName());
        }

        // Infer object type
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        if (objType == null || !objType.startsWith("L") || !objType.endsWith(";")) {
            throw new Swc4jByteCodeCompilerException(taggedTpl,
                    "Cannot infer object type for tagged template tag: " + objType);
        }

        // Generate object reference
        compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

        // Build method descriptor: first param is String[], rest are expression types
        StringBuilder paramDescriptors = new StringBuilder();

        // First argument: String[] for quasis
        paramDescriptors.append("[Ljava/lang/String;");
        generateStringArray(code, cp, quasis);

        // Remaining arguments: interpolated expressions
        for (ISwc4jAstExpr expr : exprs) {
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            compiler.getExpressionGenerator().generate(code, cp, expr, null);
            if (exprType == null) {
                exprType = "Ljava/lang/Object;";
            }
            paramDescriptors.append(exprType);
        }

        // Resolve the return type
        String internalClassName = objType.substring(1, objType.length() - 1);
        String qualifiedClassName = internalClassName.replace('/', '.');
        String paramDescriptor = "(" + paramDescriptors + ")";
        String methodReturnType = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveClassMethodReturnType(qualifiedClassName, methodName, paramDescriptor);
        if (methodReturnType == null) {
            throw new Swc4jByteCodeCompilerException(taggedTpl,
                    "Cannot infer return type for tagged template tag function " +
                            qualifiedClassName + "." + methodName);
        }

        String methodDescriptor = paramDescriptor + methodReturnType;

        // Invoke the method
        int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
        code.invokevirtual(methodRef);

        // Handle return type conversion if needed
        if (returnTypeInfo != null && returnTypeInfo.descriptor() != null
                && !methodReturnType.equals(returnTypeInfo.descriptor())) {
            if (TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && !TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                String wrapperType = TypeConversionUtils.getWrapperType(methodReturnType);
                TypeConversionUtils.boxPrimitiveType(code, cp, methodReturnType, wrapperType);
            } else if (!TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                TypeConversionUtils.unboxWrapperType(code, cp, "L" + methodReturnType + ";");
            }
        }
    }

    /**
     * Generate a String[] array on the stack containing the template quasis (cooked values).
     */
    private void generateStringArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            List<Swc4jAstTplElement> quasis) {
        int size = quasis.size();
        code.iconst(size);
        int stringClass = cp.addClass("java/lang/String");
        code.anewarray(stringClass);

        for (int i = 0; i < size; i++) {
            code.dup();
            code.iconst(i);
            Swc4jAstTplElement quasi = quasis.get(i);
            String cookedValue = quasi.getCooked().orElse(quasi.getRaw());
            int stringRef = cp.addString(cookedValue);
            code.ldc(stringRef);
            code.aastore();
        }
    }
}
