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

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;

import java.util.*;

/**
 * Manages standalone function declarations in a scoped manner.
 * Each scope represents a file being compiled, preventing function leakage between files.
 */
public final class ScopedStandaloneFunctionRegistry {
    private final Stack<ScopeData> scopeStack;

    /**
     * Instantiates a new Scoped standalone function registry.
     */
    public ScopedStandaloneFunctionRegistry() {
        scopeStack = new Stack<>();
        // Push global scope
        scopeStack.push(new ScopeData());
    }

    /**
     * Adds a function to the current scope.
     *
     * @param packageName the package name (empty string for default package)
     * @param fnDecl      the function declaration
     */
    public void addFunction(String packageName, Swc4jAstFnDecl fnDecl) {
        scopeStack.peek().functionsByPackage
                .computeIfAbsent(packageName, k -> new ArrayList<>())
                .add(fnDecl);
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        scopeStack.peek().clear();
    }

    /**
     * Enters a new scope (typically for a new file).
     */
    public void enterScope() {
        scopeStack.push(new ScopeData());
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
     * Gets the dummy class name for a package in the current scope.
     *
     * @param packageName the package name
     * @return the dummy class name, or null if not set
     */
    public String getDummyClassName(String packageName) {
        return scopeStack.peek().dummyClassNames.get(packageName);
    }

    /**
     * Gets all functions for a package in the current scope.
     *
     * @param packageName the package name
     * @return the list of function declarations, or empty list if none
     */
    public List<Swc4jAstFnDecl> getFunctions(String packageName) {
        return scopeStack.peek().functionsByPackage.getOrDefault(packageName, Collections.emptyList());
    }

    /**
     * Gets all packages that have standalone functions in the current scope.
     *
     * @return the set of package names
     */
    public Set<String> getPackagesWithFunctions() {
        return scopeStack.peek().functionsByPackage.keySet();
    }

    /**
     * Checks if there are any standalone functions in the current scope.
     *
     * @return true if there are functions
     */
    public boolean hasFunctions() {
        return !scopeStack.peek().functionsByPackage.isEmpty();
    }

    /**
     * Sets the dummy class name for a package in the current scope.
     *
     * @param packageName    the package name
     * @param dummyClassName the dummy class name (e.g., "$", "$1")
     */
    public void setDummyClassName(String packageName, String dummyClassName) {
        scopeStack.peek().dummyClassNames.put(packageName, dummyClassName);
    }

    /**
     * Internal class to hold scope-specific data.
     */
    private static final class ScopeData {
        /**
         * The Dummy class names.
         */
        final Map<String, String> dummyClassNames = new HashMap<>();
        /**
         * The Functions by package.
         */
        final Map<String, List<Swc4jAstFnDecl>> functionsByPackage = new HashMap<>();

        /**
         * Clear.
         */
        void clear() {
            dummyClassNames.clear();
            functionsByPackage.clear();
        }
    }
}
