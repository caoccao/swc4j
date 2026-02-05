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

/**
 * Information about a loop label (break or continue target).
 */
public class LoopLabelInfo {
    private final String labelName; // null for unlabeled loops
    private final List<PatchInfo> patchPositions = new ArrayList<>();
    private int targetOffset; // Bytecode offset to jump to (-1 if not yet determined)

    /**
     * Constructs a LoopLabelInfo with unknown target offset.
     *
     * @param labelName the label name (null for unlabeled loops)
     */
    public LoopLabelInfo(String labelName) {
        this.labelName = labelName;
        this.targetOffset = -1;
    }

    /**
     * Constructs a LoopLabelInfo with a known target offset.
     *
     * @param labelName    the label name (null for unlabeled loops)
     * @param targetOffset the bytecode offset to jump to
     */
    public LoopLabelInfo(String labelName, int targetOffset) {
        this.labelName = labelName;
        this.targetOffset = targetOffset;
    }

    /**
     * Adds a position that needs to be patched when the target is known.
     *
     * @param offsetPos the offset position
     * @param opcodePos the opcode position
     */
    public void addPatchPosition(int offsetPos, int opcodePos) {
        patchPositions.add(new PatchInfo(offsetPos, opcodePos));
    }

    /**
     * Gets the label name.
     *
     * @return the label name (null for unlabeled loops)
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * Gets the list of positions that need patching.
     *
     * @return the patch positions
     */
    public List<PatchInfo> getPatchPositions() {
        return patchPositions;
    }

    /**
     * Gets the target bytecode offset.
     *
     * @return the target offset (-1 if not yet determined)
     */
    public int getTargetOffset() {
        return targetOffset;
    }

    /**
     * Checks if this is an unlabeled loop.
     *
     * @return true if unlabeled
     */
    public boolean isUnlabeled() {
        return labelName == null;
    }

    /**
     * Sets the target bytecode offset.
     *
     * @param targetOffset the target offset
     */
    public void setTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
    }
}
