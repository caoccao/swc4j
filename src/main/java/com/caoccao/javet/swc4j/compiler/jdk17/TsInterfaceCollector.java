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
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsInterfaceDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.JavaType;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Collects interface declarations and registers them in the scoped type registries.
 * This allows interfaces to reference each other within the same compilation unit.
 */
public final class TsInterfaceCollector {
    private final ByteCodeCompiler compiler;

    /**
     * Instantiates a new Ts interface collector.
     *
     * @param compiler the compiler
     */
    public TsInterfaceCollector(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Collects interface declarations from module items.
     *
     * @param items          the module items to process
     * @param currentPackage the current package name
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public void collectFromModuleItems(List<ISwc4jAstModuleItem> items, String currentPackage) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstModuleItem item : items) {
            if (item instanceof Swc4jAstTsModuleDecl moduleDecl) {
                String moduleName = getModuleName(moduleDecl);
                String newPackage = currentPackage.isEmpty() ? moduleName : currentPackage + "." + moduleName;

                if (moduleDecl.getBody().isPresent() && moduleDecl.getBody().get() instanceof Swc4jAstTsModuleBlock block) {
                    collectFromModuleItems(block.getBody(), newPackage);
                }
            } else if (item instanceof Swc4jAstExportDecl exportDecl) {
                ISwc4jAstDecl decl = exportDecl.getDecl();
                if (decl instanceof Swc4jAstTsInterfaceDecl interfaceDecl) {
                    processInterfaceDecl(interfaceDecl, currentPackage);
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
     * Collects interface declarations from statements.
     *
     * @param stmts          the statements to process
     * @param currentPackage the current package name
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public void collectFromStmts(List<ISwc4jAstStmt> stmts, String currentPackage) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstTsInterfaceDecl interfaceDecl) {
                processInterfaceDecl(interfaceDecl, currentPackage);
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
     * Gets the getter method name following Java naming conventions.
     *
     * @param propName   the property name
     * @param descriptor the type descriptor
     * @return the getter method name
     */
    private String getGetterName(String propName, String descriptor) {
        if (TypeConversionUtils.ABBR_BOOLEAN.equals(descriptor)) {
            // Boolean: use 'is' prefix unless already prefixed
            if (propName.startsWith("is") || propName.startsWith("has") || propName.startsWith("can")) {
                return propName;
            }
            return "is" + capitalize(propName);
        }
        return "get" + capitalize(propName);
    }

    private String getModuleName(Swc4jAstTsModuleDecl moduleDecl) {
        return moduleDecl.getId().toString();
    }

    /**
     * Extracts property name from a key expression.
     *
     * @param key the key expression
     * @return the property name
     */
    private String getPropertyName(ISwc4jAstExpr key) {
        if (key instanceof Swc4jAstIdent ident) {
            return ident.getSym();
        }
        return key.toString();
    }

    /**
     * Processes an explicit getter signature and registers it in the JavaTypeInfo.
     *
     * @param getter   the getter signature
     * @param typeInfo the JavaTypeInfo to register the method in
     */
    private void processGetterSignature(Swc4jAstTsGetterSignature getter, JavaTypeInfo typeInfo) throws Swc4jByteCodeCompilerException {
        String propName = getPropertyName(getter.getKey());

        // Get type descriptor
        String descriptor = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
        if (getter.getTypeAnn().isPresent()) {
            descriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(getter.getTypeAnn().get().getTypeAnn());
        }

        // Register getter method
        String getterName = getGetterName(propName, descriptor);
        String getterDescriptor = "()" + descriptor;
        MethodInfo getterInfo = new MethodInfo(getterName, getterDescriptor, descriptor, false, false);
        typeInfo.addMethod(getterName, getterInfo);
    }

    /**
     * Processes an index signature and registers get/set methods in the JavaTypeInfo.
     * Index signatures like {@code [key: string]: number} are translated to:
     * - {@code Object get(String key)} - getter method
     * - {@code void set(String key, double value)} - setter method (if not readonly)
     *
     * @param indexSig the index signature
     * @param typeInfo the JavaTypeInfo to register the methods in
     */
    private void processIndexSignature(Swc4jAstTsIndexSignature indexSig, JavaTypeInfo typeInfo) throws Swc4jByteCodeCompilerException {
        // Get key type from params (first parameter)
        String keyDescriptor = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
        if (!indexSig.getParams().isEmpty()) {
            ISwc4jAstTsFnParam param = indexSig.getParams().get(0);
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                if (bindingIdent.getTypeAnn().isPresent()) {
                    keyDescriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(
                            bindingIdent.getTypeAnn().get().getTypeAnn());
                }
            }
        }

        // Get value type from typeAnn
        String valueDescriptor = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
        if (indexSig.getTypeAnn().isPresent()) {
            valueDescriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(
                    indexSig.getTypeAnn().get().getTypeAnn());
        }

        // Register getter method: get(KeyType key): ValueType
        String getterDescriptor = "(" + keyDescriptor + ")" + valueDescriptor;
        MethodInfo getterInfo = new MethodInfo("get", getterDescriptor, valueDescriptor, false, false);
        typeInfo.addMethod("get", getterInfo);

        // Register setter method (if not readonly): set(KeyType key, ValueType value): void
        if (!indexSig.isReadonly()) {
            String setterDescriptor = "(" + keyDescriptor + valueDescriptor + ")V";
            MethodInfo setterInfo = new MethodInfo("set", setterDescriptor, TypeConversionUtils.ABBR_VOID, false, false);
            typeInfo.addMethod("set", setterInfo);
        }
    }

    /**
     * Processes an interface declaration and registers it in both registries.
     *
     * @param interfaceDecl  the interface declaration to process
     * @param currentPackage the current package name
     */
    private void processInterfaceDecl(Swc4jAstTsInterfaceDecl interfaceDecl, String currentPackage) throws Swc4jByteCodeCompilerException {
        if (interfaceDecl.isDeclare()) {
            return; // Skip ambient declarations
        }

        String interfaceName = interfaceDecl.getId().getSym();
        String qualifiedName = currentPackage.isEmpty() ? interfaceName : currentPackage + "." + interfaceName;
        String internalName = qualifiedName.replace('.', '/');

        // Create JavaTypeInfo for this interface
        JavaTypeInfo typeInfo = new JavaTypeInfo(interfaceName, currentPackage, internalName, JavaType.INTERFACE);

        // Process extends clause
        for (Swc4jAstTsExprWithTypeArgs extendsExpr : interfaceDecl.getExtends()) {
            JavaTypeInfo parentInfo = resolveParentTypeInfo(extendsExpr.getExpr());
            if (parentInfo != null) {
                typeInfo.addParentTypeInfo(parentInfo);
            }
        }

        // Process body members and register methods
        for (ISwc4jAstTsTypeElement element : interfaceDecl.getBody().getBody()) {
            if (element instanceof Swc4jAstTsPropertySignature prop) {
                processPropertySignature(prop, typeInfo);
            } else if (element instanceof Swc4jAstTsMethodSignature method) {
                processMethodSignature(method, typeInfo);
            } else if (element instanceof Swc4jAstTsGetterSignature getter) {
                processGetterSignature(getter, typeInfo);
            } else if (element instanceof Swc4jAstTsSetterSignature setter) {
                processSetterSignature(setter, typeInfo);
            } else if (element instanceof Swc4jAstTsIndexSignature indexSig) {
                processIndexSignature(indexSig, typeInfo);
            }
        }

        // Register the interface in the scoped type registry
        compiler.getMemory().getScopedJavaTypeRegistry().registerInterface(qualifiedName, typeInfo);

        // Register the simple name as an alias in the scoped type alias registry
        compiler.getMemory().getScopedTypeAliasRegistry().registerAlias(interfaceName, qualifiedName);
    }

    /**
     * Processes a method signature and registers it in the JavaTypeInfo.
     *
     * @param method   the method signature
     * @param typeInfo the JavaTypeInfo to register the method in
     */
    private void processMethodSignature(Swc4jAstTsMethodSignature method, JavaTypeInfo typeInfo) throws Swc4jByteCodeCompilerException {
        String methodName = getPropertyName(method.getKey());

        // Build method descriptor
        StringBuilder paramDescriptors = new StringBuilder("(");
        for (ISwc4jAstTsFnParam param : method.getParams()) {
            String paramType = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                if (bindingIdent.getTypeAnn().isPresent()) {
                    paramType = compiler.getTypeResolver().mapTsTypeToDescriptor(
                            bindingIdent.getTypeAnn().get().getTypeAnn());
                }
            }
            paramDescriptors.append(paramType);
        }
        paramDescriptors.append(")");

        // Get return type
        String returnType = TypeConversionUtils.ABBR_VOID; // Default to void
        if (method.getTypeAnn().isPresent()) {
            returnType = compiler.getTypeResolver().mapTsTypeToDescriptor(
                    method.getTypeAnn().get().getTypeAnn());
        }

        String fullDescriptor = paramDescriptors + returnType;
        MethodInfo methodInfo = new MethodInfo(methodName, fullDescriptor, returnType, false, false);
        typeInfo.addMethod(methodName, methodInfo);
    }

    /**
     * Processes a property signature and registers getter/setter methods in the JavaTypeInfo.
     *
     * @param prop     the property signature
     * @param typeInfo the JavaTypeInfo to register methods in
     */
    private void processPropertySignature(Swc4jAstTsPropertySignature prop, JavaTypeInfo typeInfo) throws Swc4jByteCodeCompilerException {
        String propName = getPropertyName(prop.getKey());

        // Get type descriptor
        String descriptor = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
        if (prop.getTypeAnn().isPresent()) {
            descriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(prop.getTypeAnn().get().getTypeAnn());
        }

        // Register getter method
        String getterName = getGetterName(propName, descriptor);
        String getterDescriptor = "()" + descriptor;
        MethodInfo getterInfo = new MethodInfo(getterName, getterDescriptor, descriptor, false, false);
        typeInfo.addMethod(getterName, getterInfo);

        // Register setter method (if not readonly)
        if (!prop.isReadonly()) {
            String setterName = "set" + capitalize(propName);
            String setterDescriptor = "(" + descriptor + ")V";
            MethodInfo setterInfo = new MethodInfo(setterName, setterDescriptor, TypeConversionUtils.ABBR_VOID, false, false);
            typeInfo.addMethod(setterName, setterInfo);
        }
    }

    /**
     * Processes an explicit setter signature and registers it in the JavaTypeInfo.
     *
     * @param setter   the setter signature
     * @param typeInfo the JavaTypeInfo to register the method in
     */
    private void processSetterSignature(Swc4jAstTsSetterSignature setter, JavaTypeInfo typeInfo) throws Swc4jByteCodeCompilerException {
        String propName = getPropertyName(setter.getKey());

        // Get type descriptor from parameter
        String descriptor = TypeConversionUtils.LJAVA_LANG_OBJECT; // Default
        ISwc4jAstTsFnParam param = setter.getParam();
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            if (bindingIdent.getTypeAnn().isPresent()) {
                descriptor = compiler.getTypeResolver().mapTsTypeToDescriptor(
                        bindingIdent.getTypeAnn().get().getTypeAnn());
            }
        }

        // Register setter method
        String setterName = "set" + capitalize(propName);
        String setterDescriptor = "(" + descriptor + ")V";
        MethodInfo setterInfo = new MethodInfo(setterName, setterDescriptor, TypeConversionUtils.ABBR_VOID, false, false);
        typeInfo.addMethod(setterName, setterInfo);
    }

    /**
     * Resolves a parent interface info from an expression.
     *
     * @param expr the expression representing the parent interface
     * @return the JavaTypeInfo for the parent, or null if cannot be resolved
     */
    private JavaTypeInfo resolveParentTypeInfo(ISwc4jAstExpr expr) {
        String qualifiedName = AstUtils.extractQualifiedName(expr);
        if (qualifiedName == null) {
            return null;
        }

        int lastDot = qualifiedName.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;

        // First, try to resolve from the registry
        JavaTypeInfo existingInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        if (existingInfo != null) {
            return existingInfo;
        }

        // For simple names, try to resolve from type alias registry
        if (lastDot < 0) {
            String resolvedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(simpleName);
            if (resolvedName != null) {
                qualifiedName = resolvedName;
                lastDot = qualifiedName.lastIndexOf('.');
            }
        }

        // Create a placeholder JavaTypeInfo for the parent
        String internalName = qualifiedName.replace('.', '/');
        String packageName = lastDot > 0 ? qualifiedName.substring(0, lastDot) : "";

        return new JavaTypeInfo(simpleName, packageName, internalName, JavaType.INTERFACE);
    }
}
