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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariableTable;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;

import java.util.*;

/**
 * Compilation context for a single method.
 * Holds local variable table, label stacks, and type information.
 */
public class CompilationContext {
    private final Map<String, String> arrayElementTypes;
    private final Stack<LoopLabelInfo> breakLabels;
    private final Map<String, CapturedVariable> capturedVariables;
    private final Stack<String> classStack;
    private final Stack<LoopLabelInfo> continueLabels;
    private final Map<String, GenericTypeInfo> genericTypeInfoMap;
    private final Stack<Map<String, String>> inferredTypesScopes;
    private final Set<Swc4jAstBlockStmt> inlineExecutingFinallyBlocks;
    private final LocalVariableTable localVariableTable;
    private final Stack<Swc4jAstBlockStmt> pendingFinallyBlocks;
    private final Stack<TypeParameterScope> typeParameterScopes;
    private final IdentityHashMap<Swc4jAstBlockStmt, UsingResourceInfo> usingResourceMap;
    private int tempIdCounter;

    /**
     * Constructs a new CompilationContext with empty state.
     */
    public CompilationContext() {
        breakLabels = new Stack<>();
        capturedVariables = new HashMap<>();
        arrayElementTypes = new HashMap<>();
        classStack = new Stack<>();
        continueLabels = new Stack<>();
        inlineExecutingFinallyBlocks = new HashSet<>();
        pendingFinallyBlocks = new Stack<>();
        genericTypeInfoMap = new HashMap<>();
        inferredTypesScopes = new Stack<>();
        inferredTypesScopes.push(new HashMap<>()); // Base scope
        localVariableTable = new LocalVariableTable();
        typeParameterScopes = new Stack<>();
        usingResourceMap = new IdentityHashMap<>();
        tempIdCounter = 0;
    }

    /**
     * Get the inferred array element types by variable name.
     *
     * @return map of variable name to array element type descriptor
     */
    public Map<String, String> getArrayElementTypes() {
        return arrayElementTypes;
    }

    /**
     * Get a captured variable by name.
     *
     * @param name the variable name
     * @return the captured variable info, or null if not captured
     */
    public CapturedVariable getCapturedVariable(String name) {
        return capturedVariables.get(name);
    }

    /**
     * Get the captured variables map.
     *
     * @return the captured variables map
     */
    public Map<String, CapturedVariable> getCapturedVariables() {
        return capturedVariables;
    }

    /**
     * Gets the current break label (innermost loop).
     *
     * @return the current break label, or null if not in a loop
     */
    public LoopLabelInfo getCurrentBreakLabel() {
        if (breakLabels.isEmpty()) {
            return null;
        }
        return breakLabels.peek();
    }

    /**
     * Get the internal name of the current class being compiled.
     * Used for resolving 'this' expressions.
     *
     * @return the internal class name (e.g., "com/example/MyClass"), or null if not in a class
     */
    public String getCurrentClassInternalName() {
        return classStack.isEmpty() ? null : classStack.peek();
    }

    /**
     * Gets the current continue label (innermost loop).
     *
     * @return the current continue label, or null if not in a loop
     */
    public LoopLabelInfo getCurrentContinueLabel() {
        if (continueLabels.isEmpty()) {
            return null;
        }
        return continueLabels.peek();
    }

    /**
     * Gets the generic type information map.
     *
     * @return the generic type information map
     */
    public Map<String, GenericTypeInfo> getGenericTypeInfoMap() {
        return genericTypeInfoMap;
    }

    /**
     * Get an inferred type by name, searching from innermost to outermost scope.
     *
     * @param name         the variable name
     * @param defaultValue the default value if not found
     * @return the inferred type, or defaultValue if not found in any scope
     */
    public String getInferredType(String name, String defaultValue) {
        for (int i = inferredTypesScopes.size() - 1; i >= 0; i--) {
            String type = inferredTypesScopes.get(i).get(name);
            if (type != null) {
                return type;
            }
        }
        return defaultValue;
    }

    /**
     * Get the current (innermost) inferred types scope.
     * Use this for putting new types into the current scope.
     *
     * @return the current inferred types map
     */
    public Map<String, String> getInferredTypes() {
        return inferredTypesScopes.peek();
    }

    /**
     * Find a labeled break target by searching the stack from innermost to outermost.
     *
     * @param labelName the label name to find
     * @return the label info, or null if not found
     */
    public LoopLabelInfo getLabeledBreakLabel(String labelName) {
        for (int i = breakLabels.size() - 1; i >= 0; i--) {
            LoopLabelInfo labelInfo = breakLabels.get(i);
            if (labelName.equals(labelInfo.getLabelName())) {
                return labelInfo;
            }
        }
        return null;
    }

    /**
     * Find a labeled continue target by searching the stack from innermost to outermost.
     *
     * @param labelName the label name to find
     * @return the label info, or null if not found
     */
    public LoopLabelInfo getLabeledContinueLabel(String labelName) {
        for (int i = continueLabels.size() - 1; i >= 0; i--) {
            LoopLabelInfo labelInfo = continueLabels.get(i);
            if (labelName.equals(labelInfo.getLabelName())) {
                return labelInfo;
            }
        }
        return null;
    }

    /**
     * Gets the local variable table for tracking variables and their slots.
     *
     * @return the local variable table
     */
    public LocalVariableTable getLocalVariableTable() {
        return localVariableTable;
    }

    /**
     * Get the next unique temp variable ID.
     * Used for generating unique temp variable names in nested patterns.
     *
     * @return the next unique ID
     */
    public int getNextTempId() {
        return tempIdCounter++;
    }

    /**
     * Get all pending finally blocks from innermost to outermost.
     * These blocks need to be executed before a return or throw.
     *
     * @return list of finally blocks in execution order (innermost first)
     */
    public List<Swc4jAstBlockStmt> getPendingFinallyBlocks() {
        return new ArrayList<>(pendingFinallyBlocks);
    }

    /**
     * Get the pending finally blocks that are not currently being executed inline.
     * This allows nested finally blocks inside inline finally execution to still run.
     *
     * @return list of finally blocks excluding those being executed inline
     */
    public List<Swc4jAstBlockStmt> getPendingFinallyBlocksExcludingInline() {
        List<Swc4jAstBlockStmt> result = new ArrayList<>();
        for (Swc4jAstBlockStmt block : pendingFinallyBlocks) {
            if (!inlineExecutingFinallyBlocks.contains(block)) {
                result.add(block);
            }
        }
        return result;
    }

    /**
     * Get using resource info for a sentinel finally block.
     *
     * @param sentinel the sentinel block to look up
     * @return the using resource info, or null if not a using resource sentinel
     */
    public UsingResourceInfo getUsingResourceInfo(Swc4jAstBlockStmt sentinel) {
        return usingResourceMap.get(sentinel);
    }

    /**
     * Check if there are any pending finally blocks.
     *
     * @return true if there are pending finally blocks
     */
    public boolean hasPendingFinallyBlocks() {
        return !pendingFinallyBlocks.isEmpty();
    }

    /**
     * Check if a specific finally block is currently being executed inline.
     *
     * @param block the finally block to check
     * @return true if this specific block is being executed inline
     */
    public boolean isFinallyBlockInlineExecuting(Swc4jAstBlockStmt block) {
        return inlineExecutingFinallyBlocks.contains(block);
    }

    /**
     * Check if a type name is a type parameter in any active scope.
     *
     * @param typeName the type name to check
     * @return true if it's a type parameter
     */
    public boolean isTypeParameter(String typeName) {
        for (int i = typeParameterScopes.size() - 1; i >= 0; i--) {
            if (typeParameterScopes.get(i).isTypeParameter(typeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mark a finally block as being executed inline.
     * Call this before generating finally block code inline for a return/break/continue.
     *
     * @param block the finally block being executed inline
     */
    public void markFinallyBlockAsInlineExecuting(Swc4jAstBlockStmt block) {
        inlineExecutingFinallyBlocks.add(block);
    }

    /**
     * Pops the current break label from the stack.
     */
    public void popBreakLabel() {
        if (!breakLabels.isEmpty()) {
            breakLabels.pop();
        }
    }

    /**
     * Pop the current class from the class stack.
     * Call this when exiting a class scope.
     */
    public void popClass() {
        if (!classStack.isEmpty()) {
            classStack.pop();
        }
    }

    /**
     * Pops the current continue label from the stack.
     */
    public void popContinueLabel() {
        if (!continueLabels.isEmpty()) {
            continueLabels.pop();
        }
    }

    /**
     * Pop the current finally block from the pending stack.
     * Call this when exiting a try-finally block normally.
     */
    public void popFinallyBlock() {
        if (!pendingFinallyBlocks.isEmpty()) {
            pendingFinallyBlocks.pop();
        }
    }

    /**
     * Pop the current inferred types scope from the stack.
     * Call this when exiting a scope (e.g., arrow function body analysis).
     * Note: The base scope is never popped.
     */
    public void popInferredTypesScope() {
        if (inferredTypesScopes.size() > 1) {
            inferredTypesScopes.pop();
        }
    }

    /**
     * Pop the current type parameter scope from the stack.
     * Call this when exiting a generic class or method.
     */
    public void popTypeParameterScope() {
        if (!typeParameterScopes.isEmpty()) {
            typeParameterScopes.pop();
        }
    }

    /**
     * Pushes a break label onto the stack.
     *
     * @param labelInfo the loop label information
     */
    public void pushBreakLabel(LoopLabelInfo labelInfo) {
        breakLabels.push(labelInfo);
    }

    /**
     * Push a class onto the class stack.
     * Call this when entering a class scope.
     *
     * @param internalName the internal class name (e.g., "com/example/MyClass")
     */
    public void pushClass(String internalName) {
        classStack.push(internalName);
    }

    /**
     * Pushes a continue label onto the stack.
     *
     * @param labelInfo the loop label information
     */
    public void pushContinueLabel(LoopLabelInfo labelInfo) {
        continueLabels.push(labelInfo);
    }

    /**
     * Push a finally block onto the pending stack.
     * Call this when entering a try-finally block.
     *
     * @param finallyBlock the finally block to execute before returns
     */
    public void pushFinallyBlock(Swc4jAstBlockStmt finallyBlock) {
        pendingFinallyBlocks.push(finallyBlock);
    }

    /**
     * Push a new inferred types scope onto the stack.
     * Call this when entering a new scope that may have its own type bindings
     * (e.g., arrow function parameter types for return type inference).
     *
     * @return the new scope map, which can be populated with type bindings
     */
    public Map<String, String> pushInferredTypesScope() {
        Map<String, String> newScope = new HashMap<>();
        inferredTypesScopes.push(newScope);
        return newScope;
    }

    /**
     * Push a new type parameter scope onto the stack.
     * Call this when entering a generic class or method.
     *
     * @param scope the type parameter scope
     */
    public void pushTypeParameterScope(TypeParameterScope scope) {
        if (scope != null && !scope.isEmpty()) {
            typeParameterScopes.push(scope);
        }
    }

    /**
     * Register a using resource sentinel block with its resource info.
     *
     * @param sentinel the sentinel block pushed onto pendingFinallyBlocks
     * @param info     the using resource information
     */
    public void registerUsingResource(Swc4jAstBlockStmt sentinel, UsingResourceInfo info) {
        usingResourceMap.put(sentinel, info);
    }

    /**
     * Resets the compilation context to its initial state.
     */
    public void reset() {
        reset(false);
    }

    /**
     * Reset the compilation context for a new method.
     * Does NOT clear the class stack or type parameter scopes - class context is preserved across methods.
     *
     * @param isStatic true if this is for a static method (no 'this' parameter)
     */
    public void reset(boolean isStatic) {
        breakLabels.clear();
        capturedVariables.clear();
        arrayElementTypes.clear();
        continueLabels.clear();
        genericTypeInfoMap.clear();
        pendingFinallyBlocks.clear();
        // Clear all inferred types scopes and reset to a single base scope
        inferredTypesScopes.clear();
        inferredTypesScopes.push(new HashMap<>());
        localVariableTable.reset(isStatic);
        tempIdCounter = 0;
        inlineExecutingFinallyBlocks.clear();
        usingResourceMap.clear();
        // Note: classStack and typeParameterScopes are NOT cleared - class context persists across method resets
    }

    /**
     * Fully reset the compilation context including the class stack and type parameter scopes.
     * Call this when starting a completely new compilation.
     */
    public void resetAll() {
        reset(false);
        classStack.clear();
        typeParameterScopes.clear();
    }

    /**
     * Resolve a type parameter to its constraint type (for type erasure).
     * Searches from innermost to outermost scope.
     *
     * @param typeName the type parameter name
     * @return the constraint type, or null if it's a type parameter with no constraint (erases to Object), or empty if not a type parameter at all
     */
    public Optional<ISwc4jAstTsType> resolveTypeParameter(String typeName) {
        for (int i = typeParameterScopes.size() - 1; i >= 0; i--) {
            TypeParameterScope scope = typeParameterScopes.get(i);
            if (scope.isTypeParameter(typeName)) {
                return scope.getConstraint(typeName);
            }
        }
        // Not found - return a special marker (we use Optional<Optional> pattern via empty)
        return Optional.empty();
    }

    /**
     * Unmark a finally block as being executed inline.
     * Call this after generating finally block code inline.
     *
     * @param block the finally block that finished inline execution
     */
    public void unmarkFinallyBlockAsInlineExecuting(Swc4jAstBlockStmt block) {
        inlineExecutingFinallyBlocks.remove(block);
    }

    /**
     * Unregister a using resource sentinel block.
     *
     * @param sentinel the sentinel block to unregister
     */
    public void unregisterUsingResource(Swc4jAstBlockStmt sentinel) {
        usingResourceMap.remove(sentinel);
    }
}
