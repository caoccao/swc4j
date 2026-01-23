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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsTypeAliasDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsQualifiedName;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;

import java.util.List;

public final class TypeAliasCollector {
    private final ByteCodeCompiler compiler;

    public TypeAliasCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    public void collectFromModuleItems(List<ISwc4jAstModuleItem> items) {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                    processTypeAlias(typeAliasDecl);
                }
            } else if (item instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                processTypeAlias(typeAliasDecl);
            }
        }
    }

    public void collectFromStmts(List<ISwc4jAstStmt> stmts) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstTsTypeAliasDecl typeAliasDecl) {
                processTypeAlias(typeAliasDecl);
            }
        }
    }

    private void processTypeAlias(Swc4jAstTsTypeAliasDecl typeAliasDecl) {
        String aliasName = typeAliasDecl.getId().getSym();
        ISwc4jAstTsType typeAnn = typeAliasDecl.getTypeAnn();

        if (typeAnn instanceof Swc4jAstTsTypeRef typeRef) {
            ISwc4jAstTsEntityName entityName = typeRef.getTypeName();

            String targetType = resolveEntityName(entityName);
            // Resolve the target type if it's also an alias
            String resolvedType = compiler.getMemory().getScopedTypeAliasRegistry().resolve(targetType);
            if (resolvedType == null) {
                resolvedType = targetType;
            }
            compiler.getMemory().getScopedTypeAliasRegistry().put(aliasName, resolvedType);
        }
    }

    private String resolveEntityName(ISwc4jAstTsEntityName entityName) {
        if (entityName instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        } else if (entityName instanceof Swc4jAstTsQualifiedName qualifiedName) {
            String left = resolveEntityName(qualifiedName.getLeft());
            String right = qualifiedName.getRight().getSym();
            return left + "." + right;
        }
        return entityName.toString();
    }
}
