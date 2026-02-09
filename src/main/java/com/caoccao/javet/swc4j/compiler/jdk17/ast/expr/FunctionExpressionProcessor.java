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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates bytecode for function expressions.
 */
public final class FunctionExpressionProcessor extends BaseAstProcessor<Swc4jAstFnExpr> {
    /**
     * Constructs a new FunctionExpressionProcessor.
     *
     * @param compiler the bytecode compiler instance
     */
    public FunctionExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstFnExpr fnExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        Swc4jAstFunction function = fnExpr.getFunction();
        if (function.getBody().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), fnExpr,
                    "Function expressions require a body");
        }
        if (function.isAsync()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), fnExpr,
                    "Async function expressions are not supported");
        }
        if (function.isGenerator()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), fnExpr,
                    "Generator function expressions are not supported");
        }

        List<ISwc4jAstPat> params = new ArrayList<>();
        for (Swc4jAstParam param : function.getParams()) {
            params.add(param.getPat());
        }

        Swc4jAstArrowExpr arrowExpr = new Swc4jAstArrowExpr(
                function.getCtxt(),
                params,
                function.getBody().get(),
                function.isAsync(),
                function.isGenerator(),
                function.getTypeParams().orElse(null),
                function.getReturnType().orElse(null),
                function.getSpan()
        );

        compiler.getMemory().getCompilationContext().pushCaptureThisOverride(false);
        compiler.getArrowExpressionProcessor().generate(code, classWriter, arrowExpr, returnTypeInfo);
    }
}
