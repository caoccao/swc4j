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

import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariableTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Compilation context for a single method.
 * Holds local variable table, label stacks, and type information.
 */
public class CompilationContext {
    private final Stack<LoopLabelInfo> breakLabels;
    private final Stack<LoopLabelInfo> continueLabels;
    private final Map<String, GenericTypeInfo> genericTypeInfoMap;
    private final Map<String, String> inferredTypes;
    private final LocalVariableTable localVariableTable;
    private int tempIdCounter;

    public CompilationContext() {
        breakLabels = new Stack<>();
        continueLabels = new Stack<>();
        genericTypeInfoMap = new HashMap<>();
        inferredTypes = new HashMap<>();
        localVariableTable = new LocalVariableTable();
        tempIdCounter = 0;
    }

    public LoopLabelInfo getCurrentBreakLabel() {
        if (breakLabels.isEmpty()) {
            return null;
        }
        return breakLabels.peek();
    }

    public LoopLabelInfo getCurrentContinueLabel() {
        if (continueLabels.isEmpty()) {
            return null;
        }
        return continueLabels.peek();
    }

    public Map<String, GenericTypeInfo> getGenericTypeInfoMap() {
        return genericTypeInfoMap;
    }

    public Map<String, String> getInferredTypes() {
        return inferredTypes;
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

    public void popBreakLabel() {
        if (!breakLabels.isEmpty()) {
            breakLabels.pop();
        }
    }

    public void popContinueLabel() {
        if (!continueLabels.isEmpty()) {
            continueLabels.pop();
        }
    }

    public void pushBreakLabel(LoopLabelInfo labelInfo) {
        breakLabels.push(labelInfo);
    }

    public void pushContinueLabel(LoopLabelInfo labelInfo) {
        continueLabels.push(labelInfo);
    }

    public void reset() {
        breakLabels.clear();
        continueLabels.clear();
        genericTypeInfoMap.clear();
        inferredTypes.clear();
        localVariableTable.reset();
        tempIdCounter = 0;
    }
}
