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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstNewExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Generates bytecode for constructor calls (new expressions).
 */
public final class NewExpressionGenerator extends BaseAstProcessor<Swc4jAstNewExpr> {

    public NewExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstNewExpr newExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr callee = newExpr.getCallee();

        // Only support simple class name constructors for now
        if (!(callee instanceof Swc4jAstIdent ident)) {
            throw new Swc4jByteCodeCompilerException(newExpr, "Only simple class names supported in new expressions");
        }

        String className = ident.getSym();

        // Resolve the class name using type alias registry
        String resolvedType = compiler.getMemory().getScopedTypeAliasRegistry().resolve(className);
        if (resolvedType == null) {
            // If not found in type alias registry, assume it's in the current package
            resolvedType = compiler.getOptions().packagePrefix().isEmpty()
                    ? className
                    : compiler.getOptions().packagePrefix() + "." + className;
        }

        // Convert qualified name to internal name: com.example.Foo -> com/example/Foo
        String internalClassName = resolvedType.replace('.', '/');

        // Generate: new <class>
        int classRef = cp.addClass(internalClassName);
        code.newInstance(classRef);

        // Duplicate the reference for the constructor call
        code.dup();

        // Generate arguments and build constructor descriptor
        StringBuilder paramDescriptors = new StringBuilder();
        if (newExpr.getArgs().isPresent()) {
            List<Swc4jAstExprOrSpread> args = newExpr.getArgs().get();
            for (Swc4jAstExprOrSpread arg : args) {
                if (arg.getSpread().isPresent()) {
                    throw new Swc4jByteCodeCompilerException(arg, "Spread arguments not supported in constructor calls");
                }
                // Generate argument expression
                compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                // Infer argument type for descriptor
                String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
                if (argType == null) {
                    argType = "Ljava/lang/Object;";
                }
                paramDescriptors.append(argType);
            }
        }

        // Generate: invokespecial <class>.<init>(args)V
        String constructorDescriptor = "(" + paramDescriptors + ")V";
        int constructorRef = cp.addMethodRef(internalClassName, "<init>", constructorDescriptor);
        code.invokespecial(constructorRef);

        // After this, the new object reference is on the stack
    }
}
