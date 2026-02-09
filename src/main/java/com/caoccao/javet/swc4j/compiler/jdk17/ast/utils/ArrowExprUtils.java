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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for arrow expression analysis shared between
 * {@link com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ArrowExpressionProcessor}
 * and {@link com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr.CallExpressionForIIFEProcessor}.
 */
public final class ArrowExprUtils {

    private ArrowExprUtils() {
    }

    /**
     * Analyzes the return type of a block statement by collecting variable types
     * and searching for return statements.
     *
     * @param compiler  the bytecode compiler instance
     * @param blockStmt the block statement to analyze
     * @return the resolved {@link ReturnTypeInfo}
     * @throws Swc4jByteCodeCompilerException if type analysis fails
     */
    private static ReturnTypeInfo analyzeBlockReturnType(ByteCodeCompiler compiler, Swc4jAstBlockStmt blockStmt)
            throws Swc4jByteCodeCompilerException {
        // Build a map of variable names to their declared types
        Map<String, String> varTypes = new HashMap<>();
        AstUtils.collectVariableTypes(compiler, blockStmt.getStmts(), varTypes);

        // Find return statement and infer type
        for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
            ReturnTypeInfo result = findReturnType(compiler, stmt, varTypes);
            if (result != null) {
                return result;
            }
        }

        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    /**
     * Analyzes the return type of an arrow expression by first checking for an
     * explicit return type annotation, then inferring from the body.
     *
     * @param compiler  the bytecode compiler instance
     * @param arrowExpr the arrow expression
     * @param body      the body of the arrow expression
     * @return the resolved {@link ReturnTypeInfo}
     * @throws Swc4jByteCodeCompilerException if type analysis fails
     */
    public static ReturnTypeInfo analyzeReturnType(ByteCodeCompiler compiler, Swc4jAstArrowExpr arrowExpr, ISwc4jAstBlockStmtOrExpr body)
            throws Swc4jByteCodeCompilerException {
        // Check explicit return type annotation
        if (arrowExpr.getReturnType().isPresent()) {
            return compiler.getTypeResolver().analyzeReturnTypeFromAnnotation(arrowExpr.getReturnType().get());
        }

        // Infer from body
        if (body instanceof ISwc4jAstExpr expr) {
            // Expression body - infer from expression type
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(exprType);
        } else if (body instanceof Swc4jAstBlockStmt blockStmt) {
            // Block body - analyze return statements with variable type context
            return analyzeBlockReturnType(compiler, blockStmt);
        }

        // Default to void
        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    /**
     * Recursively checks whether the given AST node or any of its descendants
     * is a {@code this} expression.
     *
     * @param ast the AST node to inspect
     * @return {@code true} if a {@code this} expression is found
     */
    public static boolean containsThis(ISwc4jAst ast) {
        if (ast instanceof Swc4jAstThisExpr) {
            return true;
        }
        for (var child : ast.getChildNodes()) {
            if (child instanceof Swc4jAstThisExpr) {
                return true;
            }
            if (containsThis(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches a statement (and its nested blocks) for a return statement and
     * infers the return type from the returned expression.
     *
     * @param compiler the bytecode compiler instance
     * @param stmt     the statement to inspect
     * @param varTypes a map of variable names to their declared type descriptors
     * @return the resolved {@link ReturnTypeInfo}, or {@code null} if no return is found
     * @throws Swc4jByteCodeCompilerException if type inference fails
     */
    public static ReturnTypeInfo findReturnType(ByteCodeCompiler compiler, ISwc4jAstStmt stmt, Map<String, String> varTypes)
            throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            if (returnStmt.getArg().isPresent()) {
                ISwc4jAstExpr arg = returnStmt.getArg().get();
                // If returning an identifier, check our var types map first
                if (arg instanceof Swc4jAstIdent ident) {
                    String type = varTypes.get(ident.getSym());
                    if (type != null) {
                        return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(type);
                    }
                }
                // Fall back to type inference
                String type = compiler.getTypeResolver().inferTypeFromExpr(arg);
                if (type == null) {
                    type = ConstantJavaType.LJAVA_LANG_OBJECT;
                }
                return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(type);
            }
            return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
        } else if (stmt instanceof Swc4jAstBlockStmt inner) {
            for (ISwc4jAstStmt child : inner.getStmts()) {
                ReturnTypeInfo result = findReturnType(compiler, child, varTypes);
                if (result != null) {
                    return result;
                }
            }
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            if (ifStmt.getCons() instanceof Swc4jAstBlockStmt consBlock) {
                for (ISwc4jAstStmt child : consBlock.getStmts()) {
                    ReturnTypeInfo result = findReturnType(compiler, child, varTypes);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the body of an arrow/function expression references {@code this}.
     *
     * @param body the block statement or expression body
     * @return {@code true} if a {@code this} expression is referenced
     */
    public static boolean referencesThis(ISwc4jAstBlockStmtOrExpr body) {
        if (body instanceof Swc4jAstThisExpr) {
            return true;
        }
        for (var child : body.getChildNodes()) {
            if (child instanceof Swc4jAstThisExpr) {
                return true;
            }
            if (child instanceof ISwc4jAstBlockStmtOrExpr childBody) {
                if (referencesThis(childBody)) {
                    return true;
                }
            } else {
                if (containsThis(child)) {
                    return true;
                }
            }
        }
        return false;
    }
}
