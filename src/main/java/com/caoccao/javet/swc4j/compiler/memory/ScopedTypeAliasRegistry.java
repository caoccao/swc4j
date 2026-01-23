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
 * Manages type aliases in a scoped manner.
 * Each scope represents a file being compiled, preventing type alias leakage between files.
 * The global scope contains type aliases from compiler options.
 */
public final class ScopedTypeAliasRegistry {
    private final Stack<Map<String, String>> scopeStack;

    public ScopedTypeAliasRegistry() {
        scopeStack = new Stack<>();
        // Push global scope
        scopeStack.push(new HashMap<>());
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
     * Gets the global scope map (for initializing with options type aliases).
     *
     * @return the global scope map
     */
    public Map<String, String> getGlobalScope() {
        return scopeStack.firstElement();
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
     * Registers a type alias in the current scope.
     *
     * @param alias the alias name
     * @param type  the actual type
     */
    public void put(String alias, String type) {
        scopeStack.peek().put(alias, type);
    }

    /**
     * Registers all type aliases in the current scope.
     *
     * @param aliases the type aliases to register
     */
    public void putAll(Map<String, String> aliases) {
        scopeStack.peek().putAll(aliases);
    }

    public void registerAlias(String alias, String fullyQualifiedName) {
        scopeStack.peek().put(alias, fullyQualifiedName);
    }

    /**
     * Resolves a type alias, searching from innermost to outermost scope.
     *
     * @param alias the alias to resolve
     * @return the resolved type, or null if not found
     */
    public String resolve(String alias) {
        // Search from innermost to outermost scope
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopeStack.get(i);
            String type = scope.get(alias);
            if (type != null) {
                return type;
            }
        }
        return null;
    }
}
