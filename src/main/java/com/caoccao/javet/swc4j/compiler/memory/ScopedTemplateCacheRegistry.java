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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Manages template literal string array caching in a scoped manner.
 * <p>
 * Each scope represents a class being compiled. Template caches are created
 * during method compilation and retrieved when generating static fields and
 * {@code <clinit>} initialization.
 * <p>
 * Tagged templates like {@code tag`Hello ${name}!`} are compiled to method calls
 * with a String[] array of quasis. Without caching, a new array is created on
 * every invocation. This registry enables caching the String[] as a static final
 * field, so it's created once at class load time and reused.
 * <p>
 * The registry supports deduplication of identical quasis arrays within the same class.
 */
public final class ScopedTemplateCacheRegistry {
    private final Stack<ClassTemplateCaches> scopeStack;

    public ScopedTemplateCacheRegistry() {
        scopeStack = new Stack<>();
    }

    /**
     * Clears all scopes.
     */
    public void clear() {
        scopeStack.clear();
    }

    /**
     * Enters a new scope (typically for a new class).
     */
    public void enterScope() {
        scopeStack.push(new ClassTemplateCaches());
    }

    /**
     * Exits the current scope and returns its template caches.
     * Call this after class generation is complete.
     *
     * @return the template caches for the exited scope, or empty list if no scope
     */
    public List<TemplateCacheEntry> exitScope() {
        if (scopeStack.isEmpty()) {
            return List.of();
        }
        return scopeStack.pop().getCaches();
    }

    /**
     * Get all template caches for the current scope.
     *
     * @return list of template cache entries, or empty list if no scope
     */
    public List<TemplateCacheEntry> getCurrentCaches() {
        if (scopeStack.isEmpty()) {
            return List.of();
        }
        return scopeStack.peek().getCaches();
    }

    /**
     * Get or create a cached template field for the given quasis in the current scope.
     * <p>
     * If an identical quasis array already exists in this scope, returns the
     * existing field name (deduplication). Otherwise, creates a new cache entry
     * and returns the new field name.
     *
     * @param quasis the list of template quasis strings
     * @return the field name for the cached String[] (e.g., "$tpl$0")
     * @throws IllegalStateException if no scope is active
     */
    public String getOrCreateCache(List<String> quasis) {
        if (scopeStack.isEmpty()) {
            throw new IllegalStateException("No active template cache scope. Call enterScope() first.");
        }
        return scopeStack.peek().getOrCreateCache(quasis);
    }

    /**
     * Gets the current scope depth.
     *
     * @return the number of scopes (0 = no active scope)
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Check if there is an active scope.
     *
     * @return true if there is at least one active scope
     */
    public boolean hasActiveScope() {
        return !scopeStack.isEmpty();
    }

    /**
     * Template caches for a single class scope.
     */
    private static class ClassTemplateCaches {
        private final List<TemplateCacheEntry> caches;
        private int counter;

        ClassTemplateCaches() {
            this.caches = new ArrayList<>();
            this.counter = 0;
        }

        List<TemplateCacheEntry> getCaches() {
            return caches;
        }

        String getOrCreateCache(List<String> quasis) {
            // Check for existing cache with identical content (deduplication)
            for (TemplateCacheEntry entry : caches) {
                if (entry.quasis().equals(quasis)) {
                    return entry.fieldName();
                }
            }
            // Create new cache entry
            String fieldName = "$tpl$" + counter++;
            caches.add(new TemplateCacheEntry(fieldName, new ArrayList<>(quasis)));
            return fieldName;
        }
    }

    /**
     * Represents a single template cache entry.
     *
     * @param fieldName the static field name (e.g., "$tpl$0")
     * @param quasis    the list of template quasis strings
     */
    public record TemplateCacheEntry(String fieldName, List<String> quasis) {
    }
}
