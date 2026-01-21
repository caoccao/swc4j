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

package com.caoccao.javet.swc4j.compiler.jdk17;

import java.util.*;

public class LocalVariableTable {
    // All allocated variables for maxLocals calculation
    private final List<LocalVariable> allVariables;
    // List of scope maps - index 0 is outermost, last index is innermost (current) scope
    // Variables are looked up from innermost to outermost - no copying needed
    private final List<Map<String, LocalVariable>> scopes;
    private int nextIndex; // 0 is reserved for 'this'

    public LocalVariableTable() {
        allVariables = new ArrayList<>();
        scopes = new ArrayList<>();
        // Initialize with a base scope
        scopes.add(new HashMap<>());
        nextIndex = 1;
    }

    public LocalVariable addExistingVariableToCurrentScope(String name, String type) {
        // Find matching variable from allVariables (reverse order for shadowing)
        for (int i = allVariables.size() - 1; i >= 0; i--) {
            LocalVariable var = allVariables.get(i);
            if (var.name().equals(name) && var.type().equals(type)) {
                LocalVariable existing = getVariable(name);
                if (existing == null || existing.index() != var.index()) {
                    scopes.get(scopes.size() - 1).put(name, var);
                    return var;
                }
            }
        }
        return null;
    }

    public int allocateVariable(String name, String type) {
        int index = nextIndex;
        LocalVariable var = new LocalVariable(name, type, index);
        scopes.get(scopes.size() - 1).put(name, var);
        allVariables.add(var);
        // Doubles and longs take 2 slots
        nextIndex += (type.equals("D") || type.equals("J")) ? 2 : 1;
        return index;
    }

    public void enterScope() {
        scopes.add(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.remove(scopes.size() - 1);
        }
    }

    public Collection<LocalVariable> getAllVariables() {
        return allVariables;
    }

    public int getMaxLocals() {
        return nextIndex;
    }

    public LocalVariable getVariable(String name) {
        // Search from innermost scope to outermost
        for (int i = scopes.size() - 1; i >= 0; i--) {
            LocalVariable var = scopes.get(i).get(name);
            if (var != null) {
                return var;
            }
        }
        return null;
    }

    public void reset() {
        allVariables.clear();
        scopes.clear();
        scopes.add(new HashMap<>());
        nextIndex = 1;
    }
}
