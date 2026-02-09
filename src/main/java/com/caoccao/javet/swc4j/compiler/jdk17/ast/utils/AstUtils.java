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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSuperPropExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsQualifiedName;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for AST-related operations.
 */
public final class AstUtils {

    private AstUtils() {
    }

    /**
     * Recursively collects all identifier names from an AST node.
     *
     * @param node        the AST node to collect identifiers from
     * @param identifiers the set to collect identifier names into
     */
    public static void collectIdentifiers(Object node, Set<String> identifiers) {
        if (node == null) {
            return;
        }
        if (node instanceof Swc4jAstIdent ident) {
            identifiers.add(ident.getSym());
        } else if (node instanceof ISwc4jAst ast) {
            for (var child : ast.getChildNodes()) {
                collectIdentifiers(child, identifiers);
            }
        }
    }

    /**
     * Collects all referenced identifiers from an expression or block statement.
     *
     * @param body the block statement or expression to collect identifiers from
     * @return the set of referenced identifier names
     */
    public static Set<String> collectReferencedIdentifiers(ISwc4jAstBlockStmtOrExpr body) {
        Set<String> identifiers = new HashSet<>();
        collectIdentifiers(body, identifiers);
        return identifiers;
    }

    /**
     * Recursively collects variable type annotations from statement lists into a map.
     *
     * @param compiler the bytecode compiler (for type resolver access)
     * @param stmts    the list of statements to collect variable types from
     * @param varTypes the map to collect variable name to type descriptor mappings into
     * @throws Swc4jByteCodeCompilerException if type resolution fails
     */
    public static void collectVariableTypes(ByteCodeCompiler compiler, List<ISwc4jAstStmt> stmts, Map<String, String> varTypes) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                for (Swc4jAstVarDeclarator decl : varDecl.getDecls()) {
                    if (decl.getName() instanceof Swc4jAstBindingIdent bindingIdent) {
                        String varName = bindingIdent.getId().getSym();
                        if (bindingIdent.getTypeAnn().isPresent()) {
                            String varType = compiler.getTypeResolver().mapTsTypeToDescriptor(
                                    bindingIdent.getTypeAnn().get().getTypeAnn());
                            varTypes.put(varName, varType);
                        }
                    }
                }
            } else if (stmt instanceof Swc4jAstBlockStmt inner) {
                collectVariableTypes(compiler, inner.getStmts(), varTypes);
            } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
                if (ifStmt.getCons() instanceof Swc4jAstBlockStmt consBlock) {
                    collectVariableTypes(compiler, consBlock.getStmts(), varTypes);
                }
                if (ifStmt.getAlt().isPresent() && ifStmt.getAlt().get() instanceof Swc4jAstBlockStmt altBlock) {
                    collectVariableTypes(compiler, altBlock.getStmts(), varTypes);
                }
            }
        }
    }

    /**
     * Extracts a parameter name from a pattern AST node.
     *
     * @param param the pattern to extract the parameter name from
     * @return the parameter name, or null if cannot be extracted (e.g., destructuring patterns)
     */
    public static String extractParamName(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        } else if (param instanceof Swc4jAstRestPat restPat) {
            return extractParamName(restPat.getArg());
        } else if (param instanceof Swc4jAstAssignPat assignPat) {
            return extractParamName(assignPat.getLeft());
        } else if (param instanceof Swc4jAstArrayPat || param instanceof Swc4jAstObjectPat) {
            return null;
        }
        return null;
    }

    /**
     * Extracts a property name string from an AST property name node.
     *
     * @param propName the property name AST node
     * @return the string representation of the property name
     */
    public static String extractPropertyName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
    }

    /**
     * Extracts a fully qualified name from a TypeScript entity name.
     * Handles both simple identifiers and qualified names (e.g., com.example.MyClass).
     *
     * @param entityName the entity name to extract the qualified name from
     * @return the fully qualified name, or null if cannot be extracted
     */
    public static String extractQualifiedName(ISwc4jAstTsEntityName entityName) {
        if (entityName instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (entityName instanceof Swc4jAstTsQualifiedName qualifiedName) {
            String leftPart = extractQualifiedName(qualifiedName.getLeft());
            if (leftPart != null) {
                return leftPart + "." + qualifiedName.getRight().getSym();
            }
        }
        return null;
    }

    /**
     * Extracts a fully qualified name from an expression.
     * Handles both simple identifiers and member expressions (e.g., com.example.MyClass).
     *
     * @param expr the expression to extract the qualified name from
     * @return the fully qualified name, or null if cannot be extracted
     */
    public static String extractQualifiedName(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            String objPart = extractQualifiedName(memberExpr.getObj());
            if (objPart != null && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                return objPart + "." + propIdent.getSym();
            }
        }
        return null;
    }

    /**
     * Extracts a property name string from a super property expression.
     *
     * @param sourceCode    the source code string for error reporting
     * @param superPropExpr the super property expression AST node
     * @return the property name as a string
     * @throws Swc4jByteCodeCompilerException if the property expression is not supported
     */
    public static String extractSuperPropertyName(
            String sourceCode,
            Swc4jAstSuperPropExpr superPropExpr) throws Swc4jByteCodeCompilerException {
        if (superPropExpr.getProp() instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        if (superPropExpr.getProp() instanceof Swc4jAstComputedPropName computedProp
                && computedProp.getExpr() instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        throw new Swc4jByteCodeCompilerException(
                sourceCode,
                superPropExpr,
                "Computed super property expressions not yet supported");
    }
}
