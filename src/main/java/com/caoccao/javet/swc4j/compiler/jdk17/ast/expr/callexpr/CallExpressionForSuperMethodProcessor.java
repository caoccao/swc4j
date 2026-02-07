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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSuperPropExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSuperProp;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generates bytecode for super.method() calls.
 * Uses invokespecial to call the parent class method.
 */
public final class CallExpressionForSuperMethodProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForSuperMethodProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (!(callExpr.getCallee() instanceof Swc4jAstSuperPropExpr superPropExpr)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Expected super property expression");
        }

        // Get method name from the super property expression
        ISwc4jAstSuperProp prop = superPropExpr.getProp();
        String methodName;
        if (prop instanceof Swc4jAstIdentName identName) {
            methodName = identName.getSym();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), superPropExpr, "Computed super property expressions not yet supported");
        }

        // Get the current class and resolve its superclass
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "super.method() call outside of class context");
        }

        // Resolve the superclass
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            // Try simple name
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? currentClassInternalName.substring(lastSlash + 1) : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        if (superClassInternalName == null) {
            superClassInternalName = "java/lang/Object";
        }

        // Load 'this' reference
        code.aload(0);

        // Infer argument types and generate argument bytecode
        var args = callExpr.getArgs();
        StringBuilder paramDescriptors = new StringBuilder();
        for (var arg : args) {
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread arguments not yet supported in super method calls");
            }
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType == null) {
                argType = TypeConversionUtils.LJAVA_LANG_OBJECT;
            }
            paramDescriptors.append(argType);
        }

        // Look up method return type from the superclass
        String paramDescriptor = "(" + paramDescriptors + ")";
        String superQualifiedName = superClassInternalName.replace('/', '.');
        String returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveClassMethodReturnType(superQualifiedName, methodName, paramDescriptor);
        if (returnType == null) {
            // Try simple name
            int lastSlash = superClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? superClassInternalName.substring(lastSlash + 1) : superClassInternalName;
            returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                    .resolveClassMethodReturnType(simpleName, methodName, paramDescriptor);
        }
        if (returnType == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                    "Cannot infer return type for super method call " + superClassInternalName + "." + methodName);
        }

        String methodDescriptor = paramDescriptor + returnType;

        // Generate invokespecial to call the superclass method
        int methodRef = cp.addMethodRef(superClassInternalName, methodName, methodDescriptor);
        code.invokespecial(methodRef);
    }

    /**
     * Checks if the callee is a super property expression.
     *
     * @param callee the callee expression
     * @return true if this generator can handle the call
     */
    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        return callee instanceof Swc4jAstSuperPropExpr;
    }
}
