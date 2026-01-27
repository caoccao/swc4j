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
 * Manages compilation contexts in a scoped manner.
 * Each scope represents a nested compilation unit (e.g., lambda, inner class).
 * The global scope is the root compilation context.
 */
public final class ScopedCompilationContext {
    private final Stack<CompilationContext> scopeStack;

    public ScopedCompilationContext() {
        scopeStack = new Stack<>();
        // Push global scope
        scopeStack.push(new CompilationContext());
    }

    /**
     * Clears all scopes except the global scope and resets it.
     */
    public void clear() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        scopeStack.peek().reset();
    }

    /**
     * Gets the current compilation context.
     *
     * @return the current compilation context
     */
    public CompilationContext current() {
        return scopeStack.peek();
    }

    /**
     * Enters a new scope (typically for a nested compilation unit like a lambda).
     *
     * @param isStatic true if the new context is for a static method
     * @return the new context
     */
    public CompilationContext enterScope(boolean isStatic) {
        CompilationContext newContext = new CompilationContext();
        newContext.reset(isStatic);
        scopeStack.push(newContext);
        return newContext;
    }

    /**
     * Exits the current scope (typically after finishing a nested compilation unit).
     * The global scope is never removed.
     *
     * @return the popped context, or the global context if at root level
     */
    public CompilationContext exitScope() {
        if (scopeStack.size() > 1) {
            return scopeStack.pop();
        }
        return scopeStack.peek();
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
     * Resets the current compilation context.
     */
    public void resetCurrent() {
        scopeStack.peek().reset();
    }

    /**
     * Resets the current compilation context for a specific method type.
     *
     * @param isStatic true if this is for a static method (no 'this' parameter)
     */
    public void resetCurrent(boolean isStatic) {
        scopeStack.peek().reset(isStatic);
    }
}
