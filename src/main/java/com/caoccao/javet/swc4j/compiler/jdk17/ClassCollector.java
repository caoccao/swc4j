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

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Collects class declarations and registers them in the scoped type alias registry.
 * This allows classes to reference each other within the same compilation unit.
 */
public final class ClassCollector {
    private final ByteCodeCompiler compiler;

    /**
     * Constructs a ClassCollector with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public ClassCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Collects class declarations from module items and registers them in the type alias registry.
     *
     * @param items          the list of module items to process
     * @param currentPackage the current package context
     */
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

    /**
     * Collects class declarations from statements and registers them in the type alias registry.
     *
     * @param stmts          the list of statements to process
     * @param currentPackage the current package context
     */
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

        // Push type parameter scope for generics support (type erasure)
        // This allows field/method type resolution to correctly erase type parameters to Object
        TypeParameterScope typeParamScope = clazz.getTypeParams()
                .map(TypeParameterScope::fromDecl)
                .orElse(null);
        if (typeParamScope != null) {
            compiler.getMemory().getCompilationContext().pushTypeParameterScope(typeParamScope);
        }

        // Push the class onto the compilation context so type inference
        // (e.g., inferTypeFromExpr for this.field) can resolve fields correctly
        compiler.getMemory().getCompilationContext().pushClass(internalName);
        try {
            processClassDeclInternal(classDecl, clazz, typeInfo, qualifiedName);
        } finally {
            compiler.getMemory().getCompilationContext().popClass();
            // Pop the type parameter scope when done
            if (typeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
            }
        }
    }

    private void processClassDeclInternal(
            Swc4jAstClassDecl classDecl,
            Swc4jAstClass clazz,
            JavaTypeInfo typeInfo,
            String qualifiedName) {
        String className = classDecl.getIdent().getSym();

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

        // Analyze fields and register their types
        for (ISwc4jAstClassMember member : clazz.getBody()) {
            if (member instanceof Swc4jAstClassProp prop) {
                processFieldProp(prop, typeInfo);
            } else if (member instanceof Swc4jAstPrivateProp privateProp) {
                processPrivateFieldProp(privateProp, typeInfo);
            }
        }

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
            } else if (member instanceof Swc4jAstPrivateMethod privateMethod) {
                // ES2022 private methods (#method)
                try {
                    String methodName = privateMethod.getKey().getName(); // Without # prefix
                    var function = privateMethod.getFunction();
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
                            returnDescriptor = returnTypeInfo.descriptor();
                        } else {
                            returnDescriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
                            if (returnDescriptor == null) {
                                returnDescriptor = "V";
                            }
                        }

                        String fullDescriptor = "(" + paramDescriptors + ")" + returnDescriptor;

                        // Register private method signature in ScopedJavaClassRegistry
                        compiler.getMemory().getScopedJavaTypeRegistry()
                                .registerClassMethod(qualifiedName, methodName, fullDescriptor);
                    }
                } catch (Swc4jByteCodeCompilerException e) {
                    // Ignore methods that can't be analyzed
                }
            }
        }
    }

    private void processFieldProp(Swc4jAstClassProp prop, JavaTypeInfo typeInfo) {
        String fieldName = prop.getKey().toString();
        boolean isStatic = prop.isStatic();

        // Determine field type from type annotation or initializer
        String fieldDescriptor = "Ljava/lang/Object;"; // default

        // Check type annotation first
        if (prop.getTypeAnn().isPresent()) {
            fieldDescriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(prop.getTypeAnn().get().getTypeAnn());
        } else if (prop.getValue().isPresent()) {
            // Infer type from initializer
            try {
                String inferredType = compiler.getTypeResolver().inferTypeFromExpr(prop.getValue().get());
                if (inferredType != null) {
                    fieldDescriptor = inferredType;
                }
            } catch (Swc4jByteCodeCompilerException e) {
                // Ignore - use default type
            }
        }

        // Create FieldInfo and register it
        FieldInfo fieldInfo = new FieldInfo(fieldName, fieldDescriptor, isStatic, prop.getValue());
        typeInfo.addField(fieldName, fieldInfo);
    }

    private void processPrivateFieldProp(Swc4jAstPrivateProp privateProp, JavaTypeInfo typeInfo) {
        // ES2022 private fields (#field) - field name without # prefix
        String fieldName = privateProp.getKey().getName();
        boolean isStatic = privateProp.isStatic();

        // Determine field type from type annotation or initializer
        String fieldDescriptor = "Ljava/lang/Object;"; // default

        // Check type annotation first
        if (privateProp.getTypeAnn().isPresent()) {
            fieldDescriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(privateProp.getTypeAnn().get().getTypeAnn());
        } else if (privateProp.getValue().isPresent()) {
            // Infer type from initializer
            try {
                String inferredType = compiler.getTypeResolver().inferTypeFromExpr(privateProp.getValue().get());
                if (inferredType != null) {
                    fieldDescriptor = inferredType;
                }
            } catch (Swc4jByteCodeCompilerException e) {
                // Ignore - use default type
            }
        }

        // Create FieldInfo and register it
        FieldInfo fieldInfo = new FieldInfo(fieldName, fieldDescriptor, isStatic, privateProp.getValue());
        typeInfo.addField(fieldName, fieldInfo);
    }

    /**
     * Registers a class expression as a synthetic class declaration.
     *
     * @param classDecl      the synthetic class declaration
     * @param currentPackage the current package context
     */
    public void registerClassExpr(Swc4jAstClassDecl classDecl, String currentPackage) {
        processClassDecl(classDecl, currentPackage);
    }

    /**
     * Resolves a parent class info from an expression (typically an identifier).
     * Looks up in the ScopedJavaClassRegistry first, then creates a placeholder if not found.
     *
     * @param expr the expression representing the parent class/interface
     * @return the JavaTypeInfo for the parent, or null if cannot be resolved
     */
    private JavaTypeInfo resolveParentTypeInfo(ISwc4jAstExpr expr) {
        // Extract fully qualified name from identifier or member expression
        String qualifiedName = AstUtils.extractQualifiedName(expr);
        if (qualifiedName == null) {
            return null;
        }

        // Get simple name (last part of qualified name)
        int lastDot = qualifiedName.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;

        // First, try to resolve from the registry (might be an already-processed class or imported Java class)
        JavaTypeInfo existingInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        if (existingInfo != null) {
            return existingInfo;
        }

        // For simple names, try to resolve from type alias registry to get the qualified name
        if (lastDot < 0) {
            String resolvedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(simpleName);
            if (resolvedName != null) {
                qualifiedName = resolvedName;
                lastDot = qualifiedName.lastIndexOf('.');
            }
        }

        // Create a placeholder JavaTypeInfo for the parent
        // This will be properly linked when the parent class is processed
        String internalName = qualifiedName.replace('.', '/');
        String packageName = lastDot > 0 ? qualifiedName.substring(0, lastDot) : "";

        return new JavaTypeInfo(simpleName, packageName, internalName);
    }
}
