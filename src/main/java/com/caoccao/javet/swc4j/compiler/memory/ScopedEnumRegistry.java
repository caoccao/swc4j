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
 * Manages enum registrations in a scoped manner.
 * Each scope represents a file being compiled, preventing enum leakage between files.
 * Stores enum members and their ordinals for switch statement compilation.
 */
public final class ScopedEnumRegistry {
    // Stack of scopes, each containing enum name -> (member name -> ordinal)
    private final Stack<Map<String, Map<String, Integer>>> scopeStack;

    public ScopedEnumRegistry() {
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
     * Gets all enum names from all scopes (used for fallback resolution).
     *
     * @return set of all registered enum names
     */
    public Set<String> getAllEnumNames() {
        Set<String> allNames = new HashSet<>();
        for (Map<String, Map<String, Integer>> scope : scopeStack) {
            allNames.addAll(scope.keySet());
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
            Map<String, Map<String, Integer>> scope = scopeStack.get(i);
            Map<String, Integer> members = scope.get(enumName);
            if (members != null) {
                Integer ordinal = members.get(memberName);
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
     * Registers an enum with its members in the current scope.
     *
     * @param enumName       the qualified enum name
     * @param memberOrdinals map of member name to ordinal value
     */
    public void registerEnum(String enumName, Map<String, Integer> memberOrdinals) {
        scopeStack.peek().put(enumName, memberOrdinals);
    }
}
