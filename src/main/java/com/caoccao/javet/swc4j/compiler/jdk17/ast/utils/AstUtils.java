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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsQualifiedName;

/**
 * Utility class for AST-related operations.
 */
public final class AstUtils {

    private AstUtils() {
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
}
