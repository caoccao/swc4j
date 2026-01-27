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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUpdateExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Analyzes which mutable variables are captured by arrow expressions and need holder objects.
 * A variable needs a holder if it is:
 * 1. Declared with 'let' or 'var' (mutable)
 * 2. Captured by an arrow expression
 * 3. Modified either inside the arrow or after the arrow definition
 */
public final class MutableCaptureAnalyzer {
    private final ByteCodeCompiler compiler;

    public MutableCaptureAnalyzer(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Analyze a method body to find variables that need holder objects.
     * Call this before generating bytecode for the method.
     *
     * @param body the method body
     */
    public void analyze(Swc4jAstBlockStmt body) {
        // Find all mutable variables declared in the method
        Set<String> mutableVars = new HashSet<>();
        collectMutableVariables(body, mutableVars);

        if (mutableVars.isEmpty()) {
            return;
        }

        // Find all arrow expressions and their captured variables
        Set<String> capturedMutableVars = new HashSet<>();
        analyzeArrowCaptures(body, mutableVars, capturedMutableVars);

        if (capturedMutableVars.isEmpty()) {
            return;
        }

        // Find all variables that are modified
        Set<String> modifiedVars = new HashSet<>();
        collectModifiedVariables(body, modifiedVars);

        // Allocate holders for variables that are both captured and modified
        CompilationContext context = compiler.getMemory().getCompilationContext();
        for (String varName : capturedMutableVars) {
            if (modifiedVars.contains(varName)) {
                context.getLocalVariableTable().allocateHolderForVariable(varName);
            }
        }
    }

    /**
     * Analyze arrow expressions to find which mutable variables they capture.
     */
    private void analyzeArrowCaptures(ISwc4jAst node, Set<String> mutableVars, Set<String> capturedMutableVars) {
        if (node instanceof Swc4jAstArrowExpr arrowExpr) {
            // Get parameter names (not captured)
            Set<String> paramNames = new HashSet<>();
            for (ISwc4jAstPat param : arrowExpr.getParams()) {
                String paramName = extractParamName(param);
                if (paramName != null) {
                    paramNames.add(paramName);
                }
            }

            // Find referenced identifiers in arrow body
            Set<String> referencedVars = new HashSet<>();
            collectReferencedIdentifiers(arrowExpr.getBody(), referencedVars);

            // Check which referenced vars are mutable and not parameters
            for (String varName : referencedVars) {
                if (!paramNames.contains(varName) && mutableVars.contains(varName)) {
                    capturedMutableVars.add(varName);
                }
            }
        }

        // Recurse into children
        for (ISwc4jAst child : node.getChildNodes()) {
            analyzeArrowCaptures(child, mutableVars, capturedMutableVars);
        }
    }

    /**
     * Collect all variables that are modified (assigned to) in the node tree.
     */
    private void collectModifiedVariables(ISwc4jAst node, Set<String> modifiedVars) {
        if (node instanceof Swc4jAstAssignExpr assignExpr) {
            // Check left side of assignment
            if (assignExpr.getLeft() instanceof Swc4jAstIdent ident) {
                modifiedVars.add(ident.getSym());
            } else if (assignExpr.getLeft() instanceof Swc4jAstBindingIdent bindingIdent) {
                modifiedVars.add(bindingIdent.getId().getSym());
            }
        } else if (node instanceof Swc4jAstUpdateExpr updateExpr) {
            // ++x or x++
            if (updateExpr.getArg() instanceof Swc4jAstIdent ident) {
                modifiedVars.add(ident.getSym());
            }
        }

        // Recurse into children
        for (ISwc4jAst child : node.getChildNodes()) {
            collectModifiedVariables(child, modifiedVars);
        }
    }

    /**
     * Collect all mutable variable names declared in the node tree.
     */
    private void collectMutableVariables(ISwc4jAst node, Set<String> mutableVars) {
        if (node instanceof Swc4jAstVarDecl varDecl) {
            if (varDecl.getKind() != Swc4jAstVarDeclKind.Const) {
                for (Swc4jAstVarDeclarator decl : varDecl.getDecls()) {
                    if (decl.getName() instanceof Swc4jAstBindingIdent bindingIdent) {
                        mutableVars.add(bindingIdent.getId().getSym());
                    }
                }
            }
        }

        // Recurse into children
        for (ISwc4jAst child : node.getChildNodes()) {
            collectMutableVariables(child, mutableVars);
        }
    }

    /**
     * Collect all identifier references in the node tree.
     */
    private void collectReferencedIdentifiers(ISwc4jAst node, Set<String> identifiers) {
        if (node instanceof Swc4jAstIdent ident) {
            identifiers.add(ident.getSym());
        }

        // Recurse into children
        for (ISwc4jAst child : node.getChildNodes()) {
            collectReferencedIdentifiers(child, identifiers);
        }
    }

    private String extractParamName(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        }
        return null;
    }
}
