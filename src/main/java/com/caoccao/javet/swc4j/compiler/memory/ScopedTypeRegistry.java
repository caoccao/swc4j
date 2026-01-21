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
 * Scoped type registry for resolving type names with import awareness.
 * Maintains a stack of scopes where each scope maps alias names to fully qualified names.
 * <p>
 * When `import { A } from 'moduleX'` is encountered, A is registered as an alias in the current scope.
 * When the scope ends, all aliases in that scope are removed.
 * <p>
 * This matches TypeScript's scoping behavior for imported types.
 */
public final class ScopedTypeRegistry {
    // Stack of scopes: each scope maps alias name -> fully qualified name
    private final Stack<Map<String, String>> scopeStack;

    public ScopedTypeRegistry() {
        scopeStack = new Stack<>();
        // Global scope is always present
        scopeStack.push(new HashMap<>());
    }

    /**
     * Clear all scopes and reset to initial state.
     */
    public void clear() {
        scopeStack.clear();
        scopeStack.push(new HashMap<>());
    }

    /**
     * Enter a new scope.
     * All type aliases registered after this call will be scoped to this level.
     */
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    /**
     * Exit the current scope.
     * All type aliases registered in this scope will be removed.
     * Cannot exit the global scope.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    /**
     * Get the current scope depth.
     * Global scope is depth 1, nested scopes are 2, 3, etc.
     *
     * @return the scope depth
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Register a type alias in the current scope.
     * E.g., from `import { Color } from 'colors'`, register "Color" -> "com.module.Color"
     *
     * @param alias              the short name or alias (e.g., "Color")
     * @param fullyQualifiedName the fully qualified name (e.g., "com.module.Color")
     */
    public void registerAlias(String alias, String fullyQualifiedName) {
        scopeStack.peek().put(alias, fullyQualifiedName);
    }

    /**
     * Resolve a type name to its fully qualified form.
     * Searches from the innermost scope to the outermost scope.
     *
     * @param name the type name (alias or fully qualified)
     * @return the fully qualified name, or null if not found
     */
    public String resolve(String name) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopeStack.get(i);
            String resolved = scope.get(name);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }
}
