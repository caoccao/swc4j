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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstTpl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Generator for template literals.
 * Compiles template literals (e.g., `Hello ${name}!`) to concatenated strings using StringBuilder.
 * <p>
 * Template Structure:
 * - quasis: The static string parts
 * - exprs: The interpolated expressions
 * - Pattern: quasi[0] + expr[0] + quasi[1] + expr[1] + ... + quasi[n]
 * - Note: quasis.length = exprs.length + 1
 */
public final class TemplateLiteralProcessor extends BaseAstProcessor<Swc4jAstTpl> {
    /**
     * Instantiates a new Template literal processor.
     *
     * @param compiler the compiler
     */
    public TemplateLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTpl tpl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        List<Swc4jAstTplElement> quasis = tpl.getQuasis();
        List<ISwc4jAstExpr> exprs = tpl.getExprs();

        // Special case: empty template ``
        if (quasis.isEmpty() || (quasis.size() == 1 && quasis.get(0).getCooked().map(String::isEmpty).orElse(true) && exprs.isEmpty())) {
            int emptyStringRef = cp.addString("");
            code.ldc(emptyStringRef);
            return;
        }

        // Special case: single quasi with no expressions (e.g., `Hello World!`)
        if (exprs.isEmpty() && quasis.size() == 1) {
            String cookedValue = quasis.get(0).getCooked().orElse(quasis.get(0).getRaw());
            int stringRef = cp.addString(cookedValue);
            code.ldc(stringRef);
            return;
        }

        // General case: use StringBuilder for concatenation
        // new StringBuilder()
        int sbClass = cp.addClass(ConstantJavaType.JAVA_LANG_STRINGBUILDER);
        code.newInstance(sbClass);
        code.dup();
        int sbInit = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR___V);
        code.invokespecial(sbInit);

        // Append all quasis and expressions
        for (int i = 0; i < quasis.size(); i++) {
            // Append quasi[i]
            Swc4jAstTplElement quasi = quasis.get(i);
            String quasiValue = quasi.getCooked().orElse(quasi.getRaw());

            if (!quasiValue.isEmpty()) {
                int quasiRef = cp.addString(quasiValue);
                code.ldc(quasiRef);
                int appendString = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_APPEND, "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendString);
            }

            // Append expr[i] if exists
            if (i < exprs.size()) {
                ISwc4jAstExpr expr = exprs.get(i);

                // Infer the type of the expression
                String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);

                // Generate the expression with its natural type
                compiler.getExpressionProcessor().generate(code, classWriter, expr, null);

                // If the expression is a primitive, box it to its wrapper type
                if (TypeConversionUtils.isPrimitiveType(exprType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(exprType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, exprType, wrapperType);
                }

                // Convert to String using String.valueOf(Object)
                int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
                code.invokestatic(valueOfRef);

                // Append the String
                int appendString = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_APPEND, "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
                code.invokevirtual(appendString);
            }
        }

        // Call toString() to get the final String
        int toStringRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_TO_STRING, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_STRING);
        code.invokevirtual(toStringRef);
    }
}
