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

/**
 * Manages local variables and their scope in bytecode generation.
 */
public class LocalVariableTable {
    // All allocated variables for maxLocals calculation
    private final List<LocalVariable> allVariables;
    // List of scope maps - index 0 is outermost, last index is innermost (current) scope
    // Variables are looked up from innermost to outermost - no copying needed
    private final List<Map<String, LocalVariable>> scopes;
    private int nextIndex; // 0 is reserved for 'this'

    /**
     * Constructs a new LocalVariableTable.
     */
    public LocalVariableTable() {
        allVariables = new ArrayList<>();
        scopes = new ArrayList<>();
        // Initialize with a base scope
        scopes.add(new HashMap<>());
        nextIndex = 1;
    }

    /**
     * Adds an existing variable to the current scope.
     *
     * @param name the variable name
     * @param type the JVM type descriptor
     * @return the local variable, or null if not found
     */
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

    /**
     * Allocate a holder slot for a mutable captured variable and update the variable record.
     *
     * @param name the variable name
     * @return the holder slot index, or -1 if variable not found
     */
    public int allocateHolderForVariable(String name) {
        LocalVariable existing = getVariable(name);
        if (existing == null || !existing.mutable()) {
            return -1;
        }

        // Allocate holder array slot
        String holderType = existing.getHolderType();
        int holderIndex = nextIndex;
        nextIndex += 1; // arrays always take 1 slot

        // Create new LocalVariable with holder info and update all references
        LocalVariable updated = existing.withHolder(holderIndex);
        updateVariable(name, updated);
        return holderIndex;
    }

    /**
     * Allocates a variable with default mutability (immutable).
     *
     * @param name the variable name
     * @param type the JVM type descriptor
     * @return the allocated slot index
     */
    public int allocateVariable(String name, String type) {
        return allocateVariable(name, type, false);
    }

    /**
     * Allocate a variable with mutability tracking.
     *
     * @param name    the variable name
     * @param type    the JVM type descriptor
     * @param mutable true if declared with 'let' or 'var'
     * @return the allocated slot index
     */
    public int allocateVariable(String name, String type, boolean mutable) {
        int index = nextIndex;
        LocalVariable var = new LocalVariable(name, type, index, mutable);
        scopes.get(scopes.size() - 1).put(name, var);
        allVariables.add(var);
        // Doubles and longs take 2 slots
        nextIndex += (type.equals("D") || type.equals("J")) ? 2 : 1;
        return index;
    }

    /**
     * Enters a new scope.
     */
    public void enterScope() {
        scopes.add(new HashMap<>());
    }

    /**
     * Exits the current scope.
     */
    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.remove(scopes.size() - 1);
        }
    }

    /**
     * Gets all allocated variables.
     *
     * @return collection of all variables
     */
    public Collection<LocalVariable> getAllVariables() {
        return allVariables;
    }

    /**
     * Gets the maximum number of local variable slots needed.
     *
     * @return the max locals count
     */
    public int getMaxLocals() {
        return nextIndex;
    }

    /**
     * Gets a variable by name, searching from innermost to outermost scope.
     *
     * @param name the variable name
     * @return the local variable, or null if not found
     */
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

    /**
     * Gets a variable in the current scope only.
     *
     * @param name the variable name
     * @return the local variable in current scope, or null if not found
     */
    public LocalVariable getVariableInCurrentScope(String name) {
        return scopes.get(scopes.size() - 1).get(name);
    }

    /**
     * Resets the table for instance methods (slot 0 is 'this').
     */
    public void reset() {
        reset(false);
    }

    /**
     * Reset the local variable table.
     *
     * @param isStatic true if this is for a static method (slot 0 is first parameter),
     *                 false for instance methods (slot 0 is 'this')
     */
    public void reset(boolean isStatic) {
        allVariables.clear();
        scopes.clear();
        scopes.add(new HashMap<>());
        nextIndex = isStatic ? 0 : 1;
    }

    /**
     * Update a variable with new information (e.g., holder index).
     * Updates the variable in all scope maps where it appears.
     *
     * @param name    the variable name
     * @param updated the updated LocalVariable
     */
    public void updateVariable(String name, LocalVariable updated) {
        // Update in scopes
        for (Map<String, LocalVariable> scope : scopes) {
            if (scope.containsKey(name)) {
                scope.put(name, updated);
            }
        }
        // Update in allVariables list
        for (int i = 0; i < allVariables.size(); i++) {
            if (allVariables.get(i).name().equals(name) &&
                    allVariables.get(i).index() == updated.index()) {
                allVariables.set(i, updated);
            }
        }
    }
}
