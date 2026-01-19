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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CompilationContext {
    private final Stack<LoopLabelInfo> breakLabels = new Stack<>();
    private final Stack<LoopLabelInfo> continueLabels = new Stack<>();
    private final Map<String, GenericTypeInfo> genericTypeInfoMap = new HashMap<>();
    private final Map<String, String> inferredTypes = new HashMap<>();
    private final LocalVariableTable localVariableTable = new LocalVariableTable();

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

    public LocalVariableTable getLocalVariableTable() {
        return localVariableTable;
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

    /**
     * Information about a loop label (break or continue target).
     */
    public static class LoopLabelInfo {
        private final String labelName; // null for unlabeled loops
        private final java.util.List<PatchInfo> patchPositions = new java.util.ArrayList<>();
        private int targetOffset; // Bytecode offset to jump to (-1 if not yet determined)

        public LoopLabelInfo(String labelName) {
            this.labelName = labelName;
            this.targetOffset = -1;
        }

        public LoopLabelInfo(String labelName, int targetOffset) {
            this.labelName = labelName;
            this.targetOffset = targetOffset;
        }

        public void addPatchPosition(int offsetPos, int opcodePos) {
            patchPositions.add(new PatchInfo(offsetPos, opcodePos));
        }

        public String getLabelName() {
            return labelName;
        }

        public java.util.List<PatchInfo> getPatchPositions() {
            return patchPositions;
        }

        public int getTargetOffset() {
            return targetOffset;
        }

        public boolean isUnlabeled() {
            return labelName == null;
        }

        public void setTargetOffset(int targetOffset) {
            this.targetOffset = targetOffset;
        }

        /**
         * Information about a position that needs to be patched.
         *
         * @param offsetPos Position of the offset bytes
         * @param opcodePos Position of the opcode
         */
                public record PatchInfo(int offsetPos, int opcodePos) {
        }
    }
}
