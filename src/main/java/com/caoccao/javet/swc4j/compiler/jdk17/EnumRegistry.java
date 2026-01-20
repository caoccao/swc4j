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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for enum declarations and their member ordinals.
 * Used to resolve enum member expressions in switch statements.
 */
public final class EnumRegistry {
    // Global registry: qualified enum name -> member name -> ordinal
    private static final Map<String, Map<String, Integer>> enumRegistry = new HashMap<>();

    private EnumRegistry() {
    }

    /**
     * Clear the enum registry (for testing).
     */
    public static void clear() {
        enumRegistry.clear();
    }

    /**
     * Collect enum declarations from module items.
     */
    public static void collectFromModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage)
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
     * Collect enum declarations from statements.
     */
    public static void collectFromStmts(List<ISwc4jAstStmt> stmts, String currentPackage)
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
     *
     * @param enumName   qualified or unqualified enum name (e.g., "com.Color" or "Color")
     * @param memberName enum member name (e.g., "RED")
     * @return ordinal value, or null if not found
     */
    public static Integer getMemberOrdinal(String enumName, String memberName) {
        // Try exact match first
        Map<String, Integer> members = enumRegistry.get(enumName);
        if (members != null) {
            return members.get(memberName);
        }

        // If not found, try to find enum by unqualified name
        // E.g., if enumName is "Color", search for "*.Color"
        for (Map.Entry<String, Map<String, Integer>> entry : enumRegistry.entrySet()) {
            String qualifiedName = entry.getKey();
            // Check if qualified name ends with ".enumName" or equals enumName
            if (qualifiedName.equals(enumName) || qualifiedName.endsWith("." + enumName)) {
                members = entry.getValue();
                Integer ordinal = members.get(memberName);
                if (ordinal != null) {
                    return ordinal;
                }
            }
        }

        return null;
    }

    private static String getModuleName(Swc4jAstTsModuleDecl moduleDecl) {
        return moduleDecl.getId().toString();
    }

    private static void processEnumDecl(Swc4jAstTsEnumDecl enumDecl, String currentPackage)
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
    }
}
