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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class VarDeclGenerator {
    private VarDeclGenerator() {
    }

    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstVarDecl varDecl) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                // First try to get the variable from the current scope chain
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                // If not found in current scope, try to add it from the pre-allocated variables
                if (localVar == null) {
                    String varType = TypeResolver.extractType(compiler, bindingIdent, declarator.getInit());
                    localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
                }

                if (declarator.getInit().isPresent()) {
                    var init = declarator.getInit().get();

                    // Phase 2: Get GenericTypeInfo from context if available (for Record types)
                    GenericTypeInfo genericTypeInfo = context.getGenericTypeInfoMap().get(varName);
                    ReturnTypeInfo varTypeInfo = ReturnTypeInfo.of(localVar.type(), genericTypeInfo);

                    ExpressionGenerator.generate(compiler, code, cp, init, varTypeInfo);

                    // Store the value in the local variable
                    switch (localVar.type()) {
                        case "I", "S", "C", "Z", "B" -> code.istore(localVar.index());
                        case "J" -> code.lstore(localVar.index());
                        case "F" -> code.fstore(localVar.index());
                        case "D" -> code.dstore(localVar.index());
                        default -> code.astore(localVar.index());
                    }
                }
            }
        }
    }
}
