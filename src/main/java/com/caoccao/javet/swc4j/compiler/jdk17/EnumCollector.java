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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects enum declarations and registers them in the scoped enum registry.
 */
public final class EnumCollector {
    private final ByteCodeCompiler compiler;

    public EnumCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

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

        // Register in the scoped Java class registry as an enum type
        compiler.getMemory().getScopedJavaTypeRegistry().registerEnum(qualifiedName, memberOrdinals);

        // Register the simple name as an alias in the scoped type registry
        // This allows the enum to be referenced by its simple name within its declaration scope
        compiler.getMemory().getScopedTypeAliasRegistry().registerAlias(enumName, qualifiedName);
    }
}
