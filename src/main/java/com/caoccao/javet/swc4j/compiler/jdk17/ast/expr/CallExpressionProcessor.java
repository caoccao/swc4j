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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstTsInstantiation;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.*;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Main processor for call expressions, delegates to specialized processors.
 */
public final class CallExpressionProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    private final CallExpressionForArrayProcessor arrayGenerator;
    private final CallExpressionForArrayListProcessor arrayListGenerator;
    private final CallExpressionForArrayStaticProcessor arrayStaticGenerator;
    private final CallExpressionForClassProcessor classProcessor;
    private final CallExpressionForFunctionalInterfaceProcessor functionalInterfaceGenerator;
    private final CallExpressionForIIFEProcessor iifeGenerator;
    private final CallExpressionForStringProcessor stringGenerator;
    private final CallExpressionForSuperConstructorProcessor superConstructorGenerator;
    private final CallExpressionForSuperMethodProcessor superMethodGenerator;
    private final CallExpressionForThisConstructorProcessor thisConstructorGenerator;

    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
        arrayGenerator = new CallExpressionForArrayProcessor(compiler);
        arrayListGenerator = new CallExpressionForArrayListProcessor(compiler);
        arrayStaticGenerator = new CallExpressionForArrayStaticProcessor(compiler);
        classProcessor = new CallExpressionForClassProcessor(compiler);
        functionalInterfaceGenerator = new CallExpressionForFunctionalInterfaceProcessor(compiler);
        iifeGenerator = new CallExpressionForIIFEProcessor(compiler);
        stringGenerator = new CallExpressionForStringProcessor(compiler);
        superConstructorGenerator = new CallExpressionForSuperConstructorProcessor(compiler);
        superMethodGenerator = new CallExpressionForSuperMethodProcessor(compiler);
        thisConstructorGenerator = new CallExpressionForThisConstructorProcessor(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var callee = callExpr.getCallee();
        ISwc4jAst calleeAst = callee;
        if (calleeAst instanceof Swc4jAstTsInstantiation instantiation) {
            if (instantiation.getExpr() instanceof ISwc4jAstCallee) {
                ISwc4jAstCallee innerCallee = instantiation.getExpr();
                callExpr.setCallee(innerCallee);
                callee = innerCallee;
            }
        }

        // Handle super() constructor calls
        if (superConstructorGenerator.isCalleeSupported(callee)) {
            superConstructorGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle this() constructor calls (constructor chaining)
        if (thisConstructorGenerator.isCalleeSupported(callee)) {
            thisConstructorGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle super.method() calls
        if (superMethodGenerator.isCalleeSupported(callee)) {
            superMethodGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle IIFE (Immediately Invoked Function Expression)
        if (iifeGenerator.isCalleeSupported(callee)) {
            iifeGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle direct calls to functional interface variables (e.g., factorial(n - 1))
        if (functionalInterfaceGenerator.isCalleeSupported(callee)) {
            functionalInterfaceGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        if (arrayStaticGenerator.isCalleeSupported(callee)) {
            arrayStaticGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle method calls on arrays, strings, Java classes, TypeScript classes, etc.
        if (callee instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

            // Check if this is an explicitly imported Java class
            // If so, use the class generator which has full reflection-based method support
            // This takes priority over special handlers like ArrayList/String
            boolean isImportedJavaClass = false;
            if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                String internalName = TypeConversionUtils.descriptorToInternalName(objType);
                var javaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolveByInternalName(internalName);
                // Check if it's a Java class (has methods populated via reflection)
                // TypeScript classes are also registered but have empty methods map
                if (javaTypeInfo != null && !javaTypeInfo.getMethods().isEmpty()) {
                    isImportedJavaClass = true;
                }
            }

            if (isImportedJavaClass) {
                classProcessor.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (arrayGenerator.isTypeSupported(objType)) {
                arrayGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (arrayListGenerator.isTypeSupported(objType)) {
                arrayListGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (stringGenerator.isTypeSupported(objType)) {
                stringGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (classProcessor.isTypeSupported(objType)) {
                classProcessor.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Call expression not yet supported");
    }
}
