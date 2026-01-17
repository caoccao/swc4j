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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;

public final class VariableAnalyzer {
    private VariableAnalyzer() {
    }

    /**
     * Analyze function parameters and allocate local variable slots for them.
     * Parameters start at index 1 (index 0 is reserved for 'this' in instance methods).
     */
    public static void analyzeParameters(
            Swc4jAstFunction function,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        for (Swc4jAstParam param : function.getParams()) {
            ISwc4jAstPat pat = param.getPat();

            if (pat instanceof Swc4jAstRestPat restPat) {
                // Handle varargs parameter
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String paramName = bindingIdent.getId().getSym();
                    String paramType = TypeResolver.extractParameterType(restPat, options);
                    context.getLocalVariableTable().allocateVariable(paramName, paramType);
                    context.getInferredTypes().put(paramName, paramType);
                }
            } else if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
                // Handle regular parameter
                String paramName = bindingIdent.getId().getSym();
                String paramType = TypeResolver.extractParameterType(pat, options);
                context.getLocalVariableTable().allocateVariable(paramName, paramType);
                context.getInferredTypes().put(paramName, paramType);
            }
        }
    }

    public static void analyzeVariableDeclarations(
            Swc4jAstBlockStmt body,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
                    ISwc4jAstPat name = declarator.getName();
                    if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                        String varName = bindingIdent.getId().getSym();
                        String varType = TypeResolver.extractType(bindingIdent, declarator.getInit(), context, options);
                        context.getLocalVariableTable().allocateVariable(varName, varType);
                        context.getInferredTypes().put(varName, varType);

                        // Phase 2: Extract GenericTypeInfo for Record types
                        GenericTypeInfo genericTypeInfo = TypeResolver.extractGenericTypeInfo(bindingIdent, options);
                        if (genericTypeInfo != null) {
                            context.getGenericTypeInfoMap().put(varName, genericTypeInfo);
                        }
                    }
                }
            }
        }
    }
}
