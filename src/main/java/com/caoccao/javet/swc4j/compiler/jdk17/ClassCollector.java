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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Collects class declarations and registers them in the scoped type alias registry.
 * This allows classes to reference each other within the same compilation unit.
 */
public final class ClassCollector {
    private final ByteCodeCompiler compiler;

    public ClassCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    public void collectFromModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage) {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                String moduleName = getModuleName(moduleDecl);
                String newPackage = currentPackage.isEmpty() ? moduleName : currentPackage + "." + moduleName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstClassDecl classDecl) {
                    processClassDecl(classDecl, currentPackage);
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

    public void collectFromStmts(List<ISwc4jAstStmt> stmts, String currentPackage) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstClassDecl classDecl) {
                processClassDecl(classDecl, currentPackage);
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

    /**
     * Resolves a parent class info from an expression (typically an identifier).
     * Looks up in the ScopedJavaClassRegistry first, then creates a placeholder if not found.
     *
     * @param expr the expression representing the parent class/interface
     * @return the JavaTypeInfo for the parent, or null if cannot be resolved
     */
    private JavaTypeInfo resolveParentTypeInfo(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstIdent ident) {
            String parentName = ident.getSym();

            // First, try to resolve from the registry (might be an already-processed class or imported Java class)
            JavaTypeInfo existingInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(parentName);
            if (existingInfo != null) {
                return existingInfo;
            }

            // Try to resolve from type alias registry to get the qualified name
            String qualifiedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(parentName);
            if (qualifiedName == null) {
                qualifiedName = parentName; // Use as-is if no alias found
            }

            // Create a placeholder JavaTypeInfo for the parent
            // This will be properly linked when the parent class is processed
            String internalName = qualifiedName.replace('.', '/');
            int lastDot = qualifiedName.lastIndexOf('.');
            String packageName = lastDot > 0 ? qualifiedName.substring(0, lastDot) : "";

            return new JavaTypeInfo(parentName, packageName, internalName);
        }
        return null;
    }

    private void processClassDecl(Swc4jAstClassDecl classDecl, String currentPackage) {
        String className = classDecl.getIdent().getSym();
        String qualifiedName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        String internalName = qualifiedName.replace('.', '/');

        // Register the simple name as an alias in the scoped type alias registry
        // This allows the class to be referenced by its simple name within its declaration scope
        compiler.getMemory().getScopedTypeAliasRegistry().registerAlias(className, qualifiedName);

        // Create JavaTypeInfo for this class
        JavaTypeInfo typeInfo = new JavaTypeInfo(className, currentPackage, internalName);

        // Process extends (superClass)
        Swc4jAstClass clazz = classDecl.getClazz();
        clazz.getSuperClass().ifPresent(superClassExpr -> {
            JavaTypeInfo parentInfo = resolveParentTypeInfo(superClassExpr);
            if (parentInfo != null) {
                typeInfo.addParentTypeInfo(parentInfo);
            }
        });

        // Process implements
        for (Swc4jAstTsExprWithTypeArgs implementsExpr : clazz.getImplements()) {
            JavaTypeInfo parentInfo = resolveParentTypeInfo(implementsExpr.getExpr());
            if (parentInfo != null) {
                typeInfo.addParentTypeInfo(parentInfo);
            }
        }

        // Register the class info
        compiler.getMemory().getScopedJavaTypeRegistry().registerClass(className, typeInfo);

        // Analyze methods and register their return types
        for (ISwc4jAstClassMember member : clazz.getBody()) {
            if (member instanceof Swc4jAstClassMethod method) {
                try {
                    String methodName = method.getKey().toString();
                    // Analyze the return type
                    var function = method.getFunction();
                    var bodyOpt = function.getBody();
                    if (bodyOpt.isPresent()) {
                        Swc4jAstBlockStmt blockStmt = bodyOpt.get();
                        ReturnTypeInfo returnTypeInfo = compiler.getTypeResolver().analyzeReturnType(function, blockStmt);

                        // Build full method descriptor with parameters
                        StringBuilder paramDescriptors = new StringBuilder();
                        for (var param : function.getParams()) {
                            String paramType = compiler.getTypeResolver().extractParameterType(param.getPat());
                            paramDescriptors.append(paramType);
                        }

                        // Get return descriptor from ReturnTypeInfo
                        String returnDescriptor;
                        if (returnTypeInfo.descriptor() != null) {
                            // Object types have descriptor set
                            returnDescriptor = returnTypeInfo.descriptor();
                        } else {
                            // Primitive types need to use getPrimitiveTypeDescriptor()
                            returnDescriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
                            if (returnDescriptor == null) {
                                // VOID type or unknown
                                returnDescriptor = "V";
                            }
                        }

                        String fullDescriptor = "(" + paramDescriptors + ")" + returnDescriptor;

                        // Register method signature in ScopedJavaClassRegistry
                        compiler.getMemory().getScopedJavaTypeRegistry()
                                .registerClassMethod(qualifiedName, methodName, fullDescriptor);
                    }
                } catch (Swc4jByteCodeCompilerException e) {
                    // Ignore methods that can't be analyzed
                }
            }
        }
    }
}
