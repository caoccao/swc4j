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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstSwitchCase;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Variable analyzer.
 */
public final class VariableAnalyzer {
    private final ByteCodeCompiler compiler;

    /**
     * Instantiates a new Variable analyzer.
     *
     * @param compiler the compiler
     */
    public VariableAnalyzer(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Analyze function parameters and allocate local variable slots for them.
     * Parameters start at index 1 (index 0 is reserved for 'this' in instance methods).
     *
     * @param function the function
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public void analyzeParameters(Swc4jAstFunction function) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        for (Swc4jAstParam param : function.getParams()) {
            ISwc4jAstPat pat = param.getPat();

            if (pat instanceof Swc4jAstRestPat restPat) {
                // Handle varargs parameter
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String paramName = bindingIdent.getId().getSym();
                    String paramType = compiler.getTypeResolver().extractParameterType(restPat);
                    context.getLocalVariableTable().allocateVariable(paramName, paramType);
                    context.getInferredTypes().put(paramName, paramType);
                }
            } else if (pat instanceof Swc4jAstAssignPat assignPat) {
                // Handle default parameter - extract name and type from left side
                String paramName = compiler.getTypeResolver().extractParameterName(assignPat);
                String paramType = compiler.getTypeResolver().extractParameterType(assignPat);
                if (paramName != null) {
                    context.getLocalVariableTable().allocateVariable(paramName, paramType);
                    context.getInferredTypes().put(paramName, paramType);
                }
            } else if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
                // Handle regular parameter
                String paramName = bindingIdent.getId().getSym();
                String paramType = compiler.getTypeResolver().extractParameterType(pat);
                context.getLocalVariableTable().allocateVariable(paramName, paramType);
                context.getInferredTypes().put(paramName, paramType);
            }
        }
    }

    private void analyzeStatement(ISwc4jAstStmt stmt) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        if (stmt instanceof Swc4jAstVarDecl varDecl) {
            // Analyze variable declaration
            analyzeVarDecl(varDecl);
        } else if (stmt instanceof Swc4jAstForStmt forStmt) {
            // For loops create a new scope for their loop variable
            context.getLocalVariableTable().enterScope();

            // Analyze for loop init section - use shadowing-aware allocation
            if (forStmt.getInit().isPresent()) {
                ISwc4jAstVarDeclOrExpr init = forStmt.getInit().get();
                if (init instanceof Swc4jAstVarDecl varDecl) {
                    analyzeVarDecl(varDecl);
                }
            }
            // Recursively analyze for loop body
            analyzeStatement(forStmt.getBody());

            // Exit the for loop scope - restores shadowed variables
            context.getLocalVariableTable().exitScope();
        } else if (stmt instanceof Swc4jAstForInStmt forInStmt) {
            // For-in loops create a new scope for their loop variable
            context.getLocalVariableTable().enterScope();

            // For-in loop variables are allocated during code generation (not analysis)
            // because their type depends on the runtime collection type
            // Just skip variable declaration analysis here
            ISwc4jAstForHead left = forInStmt.getLeft();
            if (left instanceof Swc4jAstVarDecl) {
                // Skip allocation - will be done during generation
            }
            // If left is a BindingIdent, it's an existing variable - no allocation needed

            // Recursively analyze for-in loop body
            analyzeStatement(forInStmt.getBody());

            // Exit the for-in loop scope - restores shadowed variables
            context.getLocalVariableTable().exitScope();
        } else if (stmt instanceof Swc4jAstForOfStmt forOfStmt) {
            // For-of loops create a new scope for their loop variable
            context.getLocalVariableTable().enterScope();

            // For-of loop variables are allocated during code generation (not analysis)
            // because their type depends on the runtime collection type
            // Just skip variable declaration analysis here
            ISwc4jAstForHead left = forOfStmt.getLeft();
            if (left instanceof Swc4jAstVarDecl) {
                // Skip allocation - will be done during generation
            }
            // If left is a BindingIdent, it's an existing variable - no allocation needed

            // Recursively analyze for-of loop body
            analyzeStatement(forOfStmt.getBody());

            // Exit the for-of loop scope - restores shadowed variables
            context.getLocalVariableTable().exitScope();
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            // Recursively analyze if statement branches
            analyzeStatement(ifStmt.getCons());
            if (ifStmt.getAlt().isPresent()) {
                analyzeStatement(ifStmt.getAlt().get());
            }
        } else if (stmt instanceof Swc4jAstWhileStmt whileStmt) {
            // Recursively analyze while loop body
            analyzeStatement(whileStmt.getBody());
        } else if (stmt instanceof Swc4jAstDoWhileStmt doWhileStmt) {
            // Recursively analyze do-while loop body
            analyzeStatement(doWhileStmt.getBody());
        } else if (stmt instanceof Swc4jAstLabeledStmt labeledStmt) {
            // Recursively analyze labeled statement body
            analyzeStatement(labeledStmt.getBody());
        } else if (stmt instanceof Swc4jAstSwitchStmt switchStmt) {
            // Recursively analyze switch case bodies
            for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
                for (ISwc4jAstStmt caseStmt : switchCase.getCons()) {
                    analyzeStatement(caseStmt);
                }
            }
        } else if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            // Recursively analyze block statements
            context.getLocalVariableTable().enterScope();
            try {
                for (ISwc4jAstStmt childStmt : blockStmt.getStmts()) {
                    analyzeStatement(childStmt);
                }
            } finally {
                context.getLocalVariableTable().exitScope();
            }
        } else if (stmt instanceof Swc4jAstTryStmt tryStmt) {
            // Analyze try block
            for (ISwc4jAstStmt childStmt : tryStmt.getBlock().getStmts()) {
                analyzeStatement(childStmt);
            }
            // Analyze catch clause
            if (tryStmt.getHandler().isPresent()) {
                var catchClause = tryStmt.getHandler().get();
                // Allocate the catch exception variable
                if (catchClause.getParam().isPresent()) {
                    ISwc4jAstPat param = catchClause.getParam().get();
                    if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                        String varName = bindingIdent.getId().getSym();
                        String varType = ConstantJavaType.LJAVA_LANG_THROWABLE;
                        if (bindingIdent.getTypeAnn().isPresent()) {
                            var typeAnn = bindingIdent.getTypeAnn().get();
                            varType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                        }
                        context.getLocalVariableTable().allocateVariable(varName, varType);
                        context.getInferredTypes().put(varName, varType);
                    }
                }
                // Analyze catch body
                for (ISwc4jAstStmt childStmt : catchClause.getBody().getStmts()) {
                    analyzeStatement(childStmt);
                }
            }
            // Analyze finally block
            if (tryStmt.getFinalizer().isPresent()) {
                for (ISwc4jAstStmt childStmt : tryStmt.getFinalizer().get().getStmts()) {
                    analyzeStatement(childStmt);
                }
            }
        } else if (stmt instanceof Swc4jAstUsingDecl usingDecl) {
            // Allocate local variables for using declarations (resources)
            for (Swc4jAstVarDeclarator declarator : usingDecl.getDecls()) {
                ISwc4jAstPat name = declarator.getName();
                if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    String varType = compiler.getTypeResolver().extractType(
                            bindingIdent, declarator.getInit());
                    // Using declarations are immutable (like const)
                    context.getLocalVariableTable().allocateVariable(varName, varType, false);
                    context.getInferredTypes().put(varName, varType);
                }
            }
        }
    }

    private void analyzeVarDecl(Swc4jAstVarDecl varDecl) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        // let and var are mutable, const is not
        boolean isMutable = varDecl.getKind() != Swc4jAstVarDeclKind.Const;

        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                String varType = compiler.getTypeResolver().extractType(bindingIdent, declarator.getInit());
                context.getLocalVariableTable().allocateVariable(varName, varType, isMutable);
                context.getInferredTypes().put(varName, varType);

                // Phase 2: Extract GenericTypeInfo for Record types
                GenericTypeInfo genericTypeInfo = compiler.getTypeResolver().extractGenericTypeInfo(bindingIdent);
                if (genericTypeInfo != null) {
                    context.getGenericTypeInfoMap().put(varName, genericTypeInfo);
                }

                if (declarator.getInit().isPresent() && ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(varType)) {
                    var initExpr = declarator.getInit().get().unParenExpr();
                    if (initExpr instanceof Swc4jAstArrayLit arrayLit) {
                        String elementType = compiler.getTypeResolver().inferArrayElementType(arrayLit);
                        context.getArrayElementTypes().put(varName, elementType);
                    }
                }
            }
        }
    }

    /**
     * Analyze variable declarations.
     *
     * @param body the body
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public void analyzeVariableDeclarations(Swc4jAstBlockStmt body) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            analyzeStatement(stmt);
        }
    }

}
