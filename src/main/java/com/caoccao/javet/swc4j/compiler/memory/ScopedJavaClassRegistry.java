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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Manages Java class registrations in a scoped manner.
 * Each scope represents a file being compiled, preventing import leakage between files.
 * Also tracks TypeScript class method signatures for type inference.
 */
public final class ScopedJavaClassRegistry {
    private final Stack<Map<String, JavaClassInfo>> scopeStack;
    // Map from "qualifiedClassName.methodName(paramDescriptors)" to return type descriptor for TS classes
    // Example: "com.example.MyClass.test()I" -> "I"
    private final Map<String, String> tsClassMethodSignatures;

    public ScopedJavaClassRegistry() {
        scopeStack = new Stack<>();
        // Push global scope
        scopeStack.push(new HashMap<>());
        tsClassMethodSignatures = new HashMap<>();
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        scopeStack.peek().clear();
        tsClassMethodSignatures.clear();
    }

    /**
     * Enters a new scope (typically for a new file).
     */
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    /**
     * Exits the current scope (typically after finishing a file).
     * The global scope is never removed.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
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
     * Registers a Java class in the current scope.
     *
     * @param alias         the alias/name used in imports
     * @param javaClassInfo the Java class metadata
     */
    public void registerClass(String alias, JavaClassInfo javaClassInfo) {
        scopeStack.peek().put(alias, javaClassInfo);
    }

    /**
     * Registers a TypeScript class method signature.
     *
     * @param qualifiedClassName fully qualified class name (e.g., "com.example.MyClass")
     * @param methodName         method name
     * @param descriptor         full method descriptor (e.g., "()I", "(Ljava/lang/String;)V")
     */
    public void registerTSClassMethod(String qualifiedClassName, String methodName, String descriptor) {
        // Extract parameter part from descriptor (up to and including ')')
        int returnTypeStart = descriptor.lastIndexOf(')') + 1;
        String paramDescriptor = descriptor.substring(0, returnTypeStart);
        String returnType = descriptor.substring(returnTypeStart);
        // Key uses only parameter types, not return type
        String key = qualifiedClassName + "." + methodName + paramDescriptor;
        tsClassMethodSignatures.put(key, returnType);
    }

    /**
     * Resolves a Java class by alias, searching from innermost to outermost scope.
     *
     * @param className the class alias to resolve
     * @return the JavaClassInfo or null if not found
     */
    public JavaClassInfo resolve(String className) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, JavaClassInfo> scope = scopeStack.get(i);
            JavaClassInfo info = scope.get(className);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    /**
     * Resolves a TypeScript class method return type.
     *
     * @param qualifiedClassName fully qualified class name
     * @param methodName         method name
     * @param paramDescriptor    parameter descriptor only (e.g., "()", "(Ljava/lang/String;)")
     * @return return type descriptor, or null if not found
     */
    public String resolveTSClassMethodReturnType(String qualifiedClassName, String methodName, String paramDescriptor) {
        String key = qualifiedClassName + "." + methodName + paramDescriptor;
        return tsClassMethodSignatures.get(key);
    }
}
