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

import java.util.*;

/**
 * Manages Java type registrations (classes, interfaces, enums) in a scoped manner.
 * Each scope represents a file being compiled, preventing import leakage between files.
 * Also tracks class method signatures for type inference.
 */
public final class ScopedJavaTypeRegistry {
    // Scoped map from "qualifiedClassName.methodName(paramDescriptors)" to return type descriptor
    // Example: "com.example.MyClass.test()" -> "I"
    private final Stack<Map<String, String>> classMethodToReturnTypeMap;
    private final Stack<Map<String, JavaTypeInfo>> scopeStack;

    public ScopedJavaTypeRegistry() {
        scopeStack = new Stack<>();
        classMethodToReturnTypeMap = new Stack<>();
        // Push global scope
        scopeStack.push(new HashMap<>());
        classMethodToReturnTypeMap.push(new HashMap<>());
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        scopeStack.peek().clear();
        while (classMethodToReturnTypeMap.size() > 1) {
            classMethodToReturnTypeMap.pop();
        }
        classMethodToReturnTypeMap.peek().clear();
    }

    /**
     * Enters a new scope (typically for a new file).
     */
    public void enterScope() {
        scopeStack.push(new HashMap<>());
        classMethodToReturnTypeMap.push(new HashMap<>());
    }

    /**
     * Exits the current scope (typically after finishing a file).
     * The global scope is never removed.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        if (classMethodToReturnTypeMap.size() > 1) {
            classMethodToReturnTypeMap.pop();
        }
    }

    /**
     * Gets all enum names from all scopes (used for fallback resolution).
     *
     * @return set of all registered enum names
     */
    public Set<String> getAllEnumNames() {
        Set<String> allNames = new HashSet<>();
        for (Map<String, JavaTypeInfo> scope : scopeStack) {
            for (Map.Entry<String, JavaTypeInfo> entry : scope.entrySet()) {
                if (entry.getValue().isEnum()) {
                    allNames.add(entry.getKey());
                }
            }
        }
        return allNames;
    }

    /**
     * Gets the ordinal value for an enum member.
     * Searches from innermost to outermost scope.
     *
     * @param enumName   enum name (qualified or simple)
     * @param memberName enum member name (e.g., "RED")
     * @return ordinal value, or null if not found
     */
    public Integer getEnumMemberOrdinal(String enumName, String memberName) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, JavaTypeInfo> scope = scopeStack.get(i);
            JavaTypeInfo typeInfo = scope.get(enumName);
            if (typeInfo != null && typeInfo.isEnum()) {
                Integer ordinal = typeInfo.getEnumMemberOrdinal(memberName);
                if (ordinal != null) {
                    return ordinal;
                }
            }
        }
        return null;
    }

    /**
     * Gets the current scope depth.
     *
     * @return the number of scopes (1 = global only)
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Registers a Java type in the current scope.
     *
     * @param alias        the alias/name used in imports
     * @param javaTypeInfo the Java type metadata
     */
    public void registerClass(String alias, JavaTypeInfo javaTypeInfo) {
        scopeStack.peek().put(alias, javaTypeInfo);
    }

    /**
     * Registers a class method signature in the current scope.
     *
     * @param qualifiedClassName fully qualified class name (e.g., "com.example.MyClass")
     * @param methodName         method name
     * @param descriptor         full method descriptor (e.g., "()I", "(Ljava/lang/String;)V")
     */
    public void registerClassMethod(String qualifiedClassName, String methodName, String descriptor) {
        // Extract parameter part from descriptor (up to and including ')')
        int returnTypeStart = descriptor.lastIndexOf(')') + 1;
        String paramDescriptor = descriptor.substring(0, returnTypeStart);
        String returnType = descriptor.substring(returnTypeStart);
        // Key uses only parameter types, not return type
        String key = qualifiedClassName + "." + methodName + paramDescriptor;
        classMethodToReturnTypeMap.peek().put(key, returnType);
    }

    /**
     * Registers an enum with its members in the current scope.
     *
     * @param enumName       the qualified enum name
     * @param memberOrdinals map of member name to ordinal value
     */
    public void registerEnum(String enumName, Map<String, Integer> memberOrdinals) {
        // Extract simple name and package
        int lastDot = enumName.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? enumName.substring(lastDot + 1) : enumName;
        String packageName = lastDot >= 0 ? enumName.substring(0, lastDot) : "";
        String internalName = enumName.replace('.', '/');

        JavaTypeInfo enumInfo = new JavaTypeInfo(simpleName, packageName, internalName, JavaType.ENUM);
        enumInfo.setEnumValues(memberOrdinals);
        scopeStack.peek().put(enumName, enumInfo);
    }

    /**
     * Resolves a Java type by alias, searching from innermost to outermost scope.
     *
     * @param className the class alias to resolve
     * @return the JavaTypeInfo or null if not found
     */
    public JavaTypeInfo resolve(String className) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, JavaTypeInfo> scope = scopeStack.get(i);
            JavaTypeInfo info = scope.get(className);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    /**
     * Resolves a class method return type, searching from innermost to outermost scope.
     *
     * @param qualifiedClassName fully qualified class name
     * @param methodName         method name
     * @param paramDescriptor    parameter descriptor only (e.g., "()", "(Ljava/lang/String;)")
     * @return return type descriptor, or null if not found
     */
    public String resolveClassMethodReturnType(String qualifiedClassName, String methodName, String paramDescriptor) {
        String key = qualifiedClassName + "." + methodName + paramDescriptor;
        // Search from innermost to outermost scope
        for (int i = classMethodToReturnTypeMap.size() - 1; i >= 0; i--) {
            Map<String, String> scope = classMethodToReturnTypeMap.get(i);
            String returnType = scope.get(key);
            if (returnType != null) {
                return returnType;
            }
        }
        return null;
    }
}
