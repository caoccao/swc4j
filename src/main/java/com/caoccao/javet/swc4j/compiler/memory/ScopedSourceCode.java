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
 * Stores the source code being compiled in a scoped manner.
 * <p>
 * Each scope represents a file being compiled, preventing source code leakage between files.
 * This allows error messages to include source code snippets for better debugging experience.
 */
public final class ScopedSourceCode {
    private final Stack<String> sourceCodeStack;

    /**
     * Instantiates a new Scoped source code.
     */
    public ScopedSourceCode() {
        sourceCodeStack = new Stack<>();
        // Push global scope
        sourceCodeStack.push(null);
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (sourceCodeStack.size() > 1) {
            sourceCodeStack.pop();
        }
        sourceCodeStack.set(0, null);
    }

    /**
     * Enters a new scope (typically for a new file).
     */
    public void enterScope() {
        sourceCodeStack.push(null);
    }

    /**
     * Exits the current scope (typically after finishing a file).
     * The global scope is never removed.
     */
    public void exitScope() {
        if (sourceCodeStack.size() > 1) {
            sourceCodeStack.pop();
        }
    }

    /**
     * Get the stored source code for the current scope.
     *
     * @return the source code, or null if not set
     */
    public String getSourceCode() {
        return sourceCodeStack.peek();
    }

    /**
     * Set the source code being compiled in the current scope.
     *
     * @param code the source code
     */
    public void setSourceCode(String code) {
        sourceCodeStack.set(sourceCodeStack.size() - 1, code);
    }
}
