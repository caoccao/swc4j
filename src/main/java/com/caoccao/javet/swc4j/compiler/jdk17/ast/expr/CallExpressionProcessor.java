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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
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
        if (callee instanceof Swc4jAstSuper) {
            superConstructorGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle this() constructor calls
        if (callee instanceof Swc4jAstThisExpr) {
            thisConstructorGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle super.method() calls
        if (callee instanceof Swc4jAstSuperPropExpr) {
            superMethodGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle IIFE
        if (callee instanceof ISwc4jAstExpr calleeExpr
                && (AstUtils.extractArrowExpr(calleeExpr) != null || AstUtils.extractFunctionExpr(calleeExpr) != null)) {
            iifeGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle direct calls to functional interface variables
        if (isFunctionalInterfaceCall(callee)) {
            functionalInterfaceGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle Array.from(), Array.of(), etc.
        if (callee instanceof Swc4jAstMemberExpr arrayStaticMember
                && arrayStaticMember.getObj() instanceof Swc4jAstIdent arrayStaticId
                && ConstantJavaType.TYPE_ALIAS_ARRAY.equals(arrayStaticId.getSym())) {
            arrayStaticGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
            return;
        }

        // Handle method calls on arrays, strings, Java classes, TypeScript classes, etc.
        if (callee instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

            // Check if this is an explicitly imported Java class
            boolean isImportedJavaClass = false;
            if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                String internalName = TypeConversionUtils.descriptorToInternalName(objType);
                var javaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolveByInternalName(internalName);
                if (javaTypeInfo != null && !javaTypeInfo.getMethods().isEmpty()) {
                    isImportedJavaClass = true;
                }
            }

            if (isImportedJavaClass) {
                classProcessor.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (objType != null && objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
                arrayGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType)) {
                arrayListGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (ConstantJavaType.LJAVA_LANG_STRING.equals(objType)) {
                stringGenerator.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            } else if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                classProcessor.generate(code, classWriter, callExpr, returnTypeInfo);
                return;
            }
        }
        throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Call expression not yet supported");
    }

    private boolean isFunctionalInterfaceCall(ISwc4jAstCallee callee) {
        if (!(callee instanceof Swc4jAstIdent ident)) {
            return false;
        }
        String varName = ident.getSym();
        var context = compiler.getMemory().getCompilationContext();
        String varType = context.getInferredTypes().get(varName);
        if (varType == null) {
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                varType = localVar.type();
            }
        }
        if (varType == null || !varType.startsWith("L") || !varType.endsWith(";")) {
            return false;
        }
        String interfaceName = TypeConversionUtils.descriptorToInternalName(varType);
        return compiler.getMemory().getScopedFunctionalInterfaceRegistry().isFunctionalInterface(interfaceName);
    }
}
