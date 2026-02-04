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

import java.util.Stack;

/**
 * Stores the current package name in a scoped manner.
 * <p>
 * Each scope represents a namespace/module being compiled.
 * This allows nested namespace declarations to be properly resolved.
 */
public final class ScopedPackage {
    private final Stack<String> packageStack;

    public ScopedPackage() {
        packageStack = new Stack<>();
        // Push empty package as global scope
        packageStack.push("");
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (packageStack.size() > 1) {
            packageStack.pop();
        }
        packageStack.set(0, "");
    }

    /**
     * Enters a new package scope by appending to the current package.
     *
     * @param namespaceName the namespace name to append
     */
    public void enterScope(String namespaceName) {
        String current = packageStack.peek();
        String newPackage = current.isEmpty() ? namespaceName : current + "." + namespaceName;
        packageStack.push(newPackage);
    }

    /**
     * Exits the current package scope.
     * The global scope is never removed.
     */
    public void exitScope() {
        if (packageStack.size() > 1) {
            packageStack.pop();
        }
    }

    /**
     * Get the current package name.
     *
     * @return the current package name, or empty string if at global scope
     */
    public String getCurrentPackage() {
        return packageStack.peek();
    }
}
