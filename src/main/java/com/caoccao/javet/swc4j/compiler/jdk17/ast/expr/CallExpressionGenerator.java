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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.*;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    private final CallExpressionForArrayGenerator arrayGenerator;
    private final CallExpressionForArrayListGenerator arrayListGenerator;
    private final CallExpressionForArrayStaticGenerator arrayStaticGenerator;
    private final CallExpressionForClassGenerator classGenerator;
    private final CallExpressionForFunctionalInterfaceGenerator functionalInterfaceGenerator;
    private final CallExpressionForIIFEGenerator iifeGenerator;
    private final CallExpressionForStringGenerator stringGenerator;
    private final CallExpressionForSuperConstructorGenerator superConstructorGenerator;
    private final CallExpressionForSuperMethodGenerator superMethodGenerator;
    private final CallExpressionForThisConstructorGenerator thisConstructorGenerator;

    public CallExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
        arrayGenerator = new CallExpressionForArrayGenerator(compiler);
        arrayListGenerator = new CallExpressionForArrayListGenerator(compiler);
        arrayStaticGenerator = new CallExpressionForArrayStaticGenerator(compiler);
        classGenerator = new CallExpressionForClassGenerator(compiler);
        functionalInterfaceGenerator = new CallExpressionForFunctionalInterfaceGenerator(compiler);
        iifeGenerator = new CallExpressionForIIFEGenerator(compiler);
        stringGenerator = new CallExpressionForStringGenerator(compiler);
        superConstructorGenerator = new CallExpressionForSuperConstructorGenerator(compiler);
        superMethodGenerator = new CallExpressionForSuperMethodGenerator(compiler);
        thisConstructorGenerator = new CallExpressionForThisConstructorGenerator(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var callee = callExpr.getCallee();

        // Handle super() constructor calls
        if (superConstructorGenerator.isCalleeSupported(callee)) {
            superConstructorGenerator.generate(code, cp, callExpr, returnTypeInfo);
            return;
        }

        // Handle this() constructor calls (constructor chaining)
        if (thisConstructorGenerator.isCalleeSupported(callee)) {
            thisConstructorGenerator.generate(code, cp, callExpr, returnTypeInfo);
            return;
        }

        // Handle super.method() calls
        if (superMethodGenerator.isCalleeSupported(callee)) {
            superMethodGenerator.generate(code, cp, callExpr, returnTypeInfo);
            return;
        }

        // Handle IIFE (Immediately Invoked Function Expression)
        if (iifeGenerator.isCalleeSupported(callee)) {
            iifeGenerator.generate(code, cp, callExpr, returnTypeInfo);
            return;
        }

        // Handle direct calls to functional interface variables (e.g., factorial(n - 1))
        if (functionalInterfaceGenerator.isCalleeSupported(callee)) {
            functionalInterfaceGenerator.generate(code, cp, callExpr, returnTypeInfo);
            return;
        }

        if (arrayStaticGenerator.isCalleeSupported(callee)) {
            arrayStaticGenerator.generate(code, cp, callExpr, returnTypeInfo);
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
                String internalName = objType.substring(1, objType.length() - 1);
                var javaTypeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolveByInternalName(internalName);
                // Check if it's a Java class (has methods populated via reflection)
                // TypeScript classes are also registered but have empty methods map
                if (javaTypeInfo != null && !javaTypeInfo.getMethods().isEmpty()) {
                    isImportedJavaClass = true;
                }
            }

            if (isImportedJavaClass) {
                classGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (arrayGenerator.isTypeSupported(objType)) {
                arrayGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (arrayListGenerator.isTypeSupported(objType)) {
                arrayListGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (stringGenerator.isTypeSupported(objType)) {
                stringGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            } else if (classGenerator.isTypeSupported(objType)) {
                classGenerator.generate(code, cp, callExpr, returnTypeInfo);
                return;
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Call expression not yet supported");
    }
}
