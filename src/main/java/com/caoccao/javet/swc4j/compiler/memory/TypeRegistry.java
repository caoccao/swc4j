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

package com.caoccao.javet.swc4j.compiler.memory;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEnumMemberId;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsEnumDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsEnumMember;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.*;

/**
 * Global registry for type declarations (enums, classes, interfaces).
 * Stores fully qualified type information and works with ScopedTypeRegistry for name resolution.
 * <p>
 * Types must be referenced by fully qualified names unless they come from explicit imports.
 * Uses a ScopedTypeRegistry to resolve imported/aliased type names based on current scope.
 */
public final class TypeRegistry {
    // Enum registry: qualified enum name -> member name -> ordinal
    private final Map<String, Map<String, Integer>> enumRegistry;
    private final ScopedTypeRegistry scopedTypeRegistry;

    public TypeRegistry(ScopedTypeRegistry scopedTypeRegistry) {
        this.enumRegistry = new HashMap<>();
        this.scopedTypeRegistry = scopedTypeRegistry;
    }

    /**
     * Clear the type registry (for testing).
     */
    public void clear() {
        enumRegistry.clear();
    }

    /**
     * Collect type declarations from module items.
     */
    public void collectFromModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage)
            throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                String moduleName = getModuleName(moduleDecl);
                String newPackage = currentPackage.isEmpty() ? moduleName : currentPackage + "." + moduleName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstTsEnumDecl enumDecl) {
                    processEnumDecl(enumDecl, currentPackage);
                } else if (decl instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
                    String moduleName = getModuleName(tsModuleDecl);
                    String newPackage = currentPackage.isEmpty() ? moduleName : currentPackage + "." + moduleName;

                    if (tsModuleDecl.getBody().isPresent() && tsModuleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                        collectFromModuleItems(block.getBody(), newPackage);
                    }
                }
            }
        }
    }

    /**
     * Collect type declarations from statements.
     */
    public void collectFromStmts(List<ISwc4jAstStmt> stmts, String currentPackage)
            throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstTsEnumDecl enumDecl) {
                processEnumDecl(enumDecl, currentPackage);
            } else if (stmt instanceof Swc4jAstTsModuleDecl moduleDecl) {
                String moduleName = getModuleName(moduleDecl);
                String newPackage = currentPackage.isEmpty() ? moduleName : currentPackage + "." + moduleName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            }
        }
    }

    /**
     * Get the ordinal value for an enum member.
     * Resolution order:
     * 1. Check scoped type registry for imported/aliased name
     * 2. Try exact match with fully qualified name
     * 3. Fall back to searching all enums for matching simple name
     *
     * @param enumName   enum name (qualified, unqualified, or imported alias)
     * @param memberName enum member name (e.g., "RED")
     * @return ordinal value, or null if not found
     */
    public Integer getEnumMemberOrdinal(String enumName, String memberName) {
        String resolvedName = resolveTypeName(enumName);
        if (resolvedName != null) {
            Map<String, Integer> members = enumRegistry.get(resolvedName);
            if (members != null) {
                return members.get(memberName);
            }
        }
        return null;
    }

    public Set<String> getEnumNameSet() {
        return enumRegistry.keySet();
    }

    private String getModuleName(Swc4jAstTsModuleDecl moduleDecl) {
        return moduleDecl.getId().toString();
    }

    private void processEnumDecl(Swc4jAstTsEnumDecl enumDecl, String currentPackage)
            throws Swc4jByteCodeCompilerException {
        if (enumDecl.isDeclare()) {
            return; // Skip ambient declarations
        }

        String enumName = enumDecl.getId().getSym();
        String qualifiedName = currentPackage.isEmpty() ? enumName : currentPackage + "." + enumName;

        List<Swc4jAstTsEnumMember> members = enumDecl.getMembers();
        Map<String, Integer> memberOrdinals = new LinkedHashMap<>();

        // Java enums always have sequential ordinals starting from 0, regardless of explicit values.
        // Explicit values in TypeScript enums are ignored for switch statement purposes.
        int ordinal = 0;
        for (Swc4jAstTsEnumMember member : members) {
            ISwc4jAstTsEnumMemberId memberId = member.getId();
            String memberName = memberId instanceof Swc4jAstIdent ident ? ident.getSym() : memberId.toString();

            // Store the sequential ordinal (Java enums always use 0, 1, 2, ...)
            memberOrdinals.put(memberName, ordinal);
            ordinal++;
        }

        enumRegistry.put(qualifiedName, memberOrdinals);

        // Register the simple name as an alias in the scoped type registry
        // This allows the type to be referenced by its simple name within its declaration scope
        scopedTypeRegistry.registerAlias(enumName, qualifiedName);
    }

    /**
     * Resolve a type name to its fully qualified form.
     * Uses scoped type registry for resolution (imports and local type declarations).
     * If not found in scopes, assumes the name is already fully qualified.
     *
     * @param typeName the type name (imported alias or fully qualified)
     * @return the fully qualified name, or the original name if not found in scopes
     */
    public String resolveTypeName(String typeName) {
        // Check scoped type registry (for imports and local declarations)
        String scopedResolution = scopedTypeRegistry.resolve(typeName);
        if (scopedResolution != null) {
            return scopedResolution;
        }

        // If not found in scopes, assume it's already fully qualified
        return typeName;
    }
}
