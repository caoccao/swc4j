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

package com.caoccao.javet.swc4j.asm;

import java.util.*;

/**
 * Generates StackMapTable frames by analyzing bytecode with data flow analysis.
 */
public class StackMapGenerator {
    private static final int DOUBLE = 3;
    private static final int FLOAT = 2;
    private static final int INTEGER = 1;
    private static final int LONG = 4;
    private static final int NULL = 5;
    private static final int OBJECT = 7;
    // Verification type constants
    private static final int TOP = 0;
    private static final int UNINITIALIZED_THIS = 6;
    private final byte[] bytecode;
    private final String className;
    private final boolean isStatic;
    private final int maxLocals;

    public StackMapGenerator(byte[] bytecode, int maxLocals, boolean isStatic, String className) {
        this.bytecode = bytecode;
        this.maxLocals = maxLocals;
        this.isStatic = isStatic;
        this.className = className;
    }

    private Map<Integer, Frame> computeFramesDataFlow() {
        Map<Integer, Frame> frames = new HashMap<>();
        Queue<WorkItem> workQueue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        // Start with initial frame at offset 0
        Frame initialFrame = createInitialFrame();
        workQueue.add(new WorkItem(0, initialFrame));

        while (!workQueue.isEmpty()) {
            WorkItem item = workQueue.poll();
            int pc = item.offset;

            if (pc >= bytecode.length) continue;
            if (visited.contains(pc)) continue;
            visited.add(pc);

            // Save frame at this offset
            frames.put(pc, item.frame.copy());

            // Execute instruction and get next offsets
            List<Integer> nextOffsets = getNextOffsets(pc);
            for (int nextPc : nextOffsets) {
                Frame nextFrame = simulateInstruction(item.frame, pc);
                workQueue.add(new WorkItem(nextPc, nextFrame));
            }
        }

        return frames;
    }

    private Frame createInitialFrame() {
        List<Integer> locals = new ArrayList<>();
        if (!isStatic) {
            locals.add(OBJECT); // 'this'
        }
        // Don't pre-populate locals - they get added when stored to
        return new Frame(locals, new ArrayList<>());
    }

    private void ensureLocalSlot(List<Integer> locals, int index, int type) {
        // Extend locals list to include the specified index
        while (locals.size() <= index) {
            locals.add(TOP); // Fill gaps with TOP
        }
        locals.set(index, type);

        // Long and Double are followed by TOP for the second slot (JVM spec §4.10.1.5)
        if (type == LONG || type == DOUBLE) {
            while (locals.size() <= index + 1) {
                locals.add(TOP);
            }
            // Ensure the next slot is TOP (don't overwrite if already set)
            if (locals.get(index + 1) != TOP) {
                locals.set(index + 1, TOP);
            }
        }
    }

    private Set<Integer> findBranchTargets() {
        Set<Integer> targets = new TreeSet<>();
        for (int i = 0; i < bytecode.length; i++) {
            int opcode = bytecode[i] & 0xFF;

            if (opcode >= 0x99 && opcode <= 0xA6) { // Conditional branches
                if (i + 2 < bytecode.length) {
                    short offset = (short) (((bytecode[i + 1] & 0xFF) << 8) | (bytecode[i + 2] & 0xFF));
                    targets.add(i + offset);
                }
            } else if (opcode == 0xA7) { // goto
                if (i + 2 < bytecode.length) {
                    short offset = (short) (((bytecode[i + 1] & 0xFF) << 8) | (bytecode[i + 2] & 0xFF));
                    targets.add(i + offset);
                }
            } else if (opcode == 0xC6 || opcode == 0xC7) { // ifnull, ifnonnull
                if (i + 2 < bytecode.length) {
                    short offset = (short) (((bytecode[i + 1] & 0xFF) << 8) | (bytecode[i + 2] & 0xFF));
                    targets.add(i + offset);
                }
            }
        }
        return targets;
    }

    public List<ClassWriter.StackMapEntry> generate() {
        // Find branch targets
        Set<Integer> branchTargets = findBranchTargets();
        if (branchTargets.isEmpty()) {
            return null;
        }

        // Compute frames using data flow analysis
        Map<Integer, Frame> frames = computeFramesDataFlow();

        // Generate stack map entries only for branch targets
        List<ClassWriter.StackMapEntry> entries = new ArrayList<>();
        List<Integer> sortedTargets = new ArrayList<>(branchTargets);
        Collections.sort(sortedTargets);

        int previousOffset = -1;
        for (int offset : sortedTargets) {
            Frame frame = frames.get(offset);
            if (frame == null) continue;

            int offsetDelta = previousOffset == -1 ? offset : offset - previousOffset - 1;

            // Remove explicit TOP entries that follow LONG/DOUBLE (they're implicit in the JVM spec)
            List<Integer> compactLocals = removeExplicitTops(frame.locals);
            List<Integer> compactStack = removeExplicitTops(frame.stack);

            entries.add(new ClassWriter.StackMapEntry(offsetDelta, 255, compactLocals, compactStack));
            previousOffset = offset;
        }

        return entries.isEmpty() ? null : entries;
    }

    private int getInstructionSize(int pc, int opcode) {
        switch (opcode) {
            case 0x10:
            case 0x15:
            case 0x16:
            case 0x17:
            case 0x18:
            case 0x19: // bipush, *load
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3A: // *store
                return 2;
            case 0x11: // sipush
            case 0x99:
            case 0x9A:
            case 0x9B:
            case 0x9C:
            case 0x9D:
            case 0x9E: // if<cond>
            case 0x9F:
            case 0xA0:
            case 0xA1:
            case 0xA2:
            case 0xA3:
            case 0xA4: // if_icmp<cond>
            case 0xA5:
            case 0xA6: // if_acmp<cond>
            case 0xA7: // goto
            case 0xB6:
            case 0xB7:
            case 0xB8: // invoke*
            case 0xBD: // anewarray
            case 0xC6:
            case 0xC7: // ifnull, ifnonnull
                return 3;
            default:
                return 1;
        }
    }

    private List<Integer> getNextOffsets(int pc) {
        List<Integer> offsets = new ArrayList<>();
        int opcode = bytecode[pc] & 0xFF;

        // Check for branches
        if (opcode >= 0x99 && opcode <= 0xA6) { // Conditional branches
            if (pc + 2 < bytecode.length) {
                short offset = (short) (((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF));
                offsets.add(pc + offset); // Branch target
                offsets.add(pc + 3); // Fall-through
            }
        } else if (opcode == 0xA7) { // goto
            if (pc + 2 < bytecode.length) {
                short offset = (short) (((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF));
                offsets.add(pc + offset); // Only branch target, no fall-through
            }
        } else if (opcode == 0xC6 || opcode == 0xC7) { // ifnull, ifnonnull
            if (pc + 2 < bytecode.length) {
                short offset = (short) (((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF));
                offsets.add(pc + offset); // Branch target
                offsets.add(pc + 3); // Fall-through
            }
        } else if (opcode >= 0xAC && opcode <= 0xB1) { // return instructions
            // No next offset
        } else {
            // Normal instruction - continue to next
            offsets.add(pc + getInstructionSize(pc, opcode));
        }

        return offsets;
    }

    private List<Integer> removeExplicitTops(List<Integer> types) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            int type = types.get(i);
            result.add(type);
            // Skip the next TOP if current type is LONG or DOUBLE
            if ((type == LONG || type == DOUBLE) && i + 1 < types.size() && types.get(i + 1) == TOP) {
                i++; // Skip the next TOP
            }
        }
        return result;
    }

    private Frame simulateInstruction(Frame frame, int pc) {
        Frame newFrame = frame.copy();
        int opcode = bytecode[pc] & 0xFF;
        List<Integer> stack = newFrame.stack;

        // Simulate stack effects
        switch (opcode) {
            // Push constants
            case 0x01:
                stack.add(NULL);
                break; // aconst_null
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08: // iconst_*
                stack.add(INTEGER);
                break;
            case 0x10:
            case 0x11: // bipush, sipush
                stack.add(INTEGER);
                break;

            // Loads
            case 0x15:
            case 0x1A:
            case 0x1B:
            case 0x1C:
            case 0x1D: // iload*
                stack.add(INTEGER);
                break;
            case 0x16:
            case 0x1E:
            case 0x1F:
            case 0x20:
            case 0x21: // lload*
                stack.add(LONG);
                break;
            case 0x17:
            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25: // fload*
                stack.add(FLOAT);
                break;
            case 0x18:
            case 0x26:
            case 0x27:
            case 0x28:
            case 0x29: // dload*
                stack.add(DOUBLE);
                break;
            case 0x19:
            case 0x2A:
            case 0x2B:
            case 0x2C:
            case 0x2D: // aload*
                stack.add(OBJECT);
                break;

            // Stores - pop from stack and update locals
            case 0x36: // istore
                if (!stack.isEmpty()) {
                    int index = bytecode[pc + 1] & 0xFF;
                    ensureLocalSlot(newFrame.locals, index, INTEGER);
                    stack.remove(stack.size() - 1);
                }
                break;
            case 0x3B:
            case 0x3C:
            case 0x3D:
            case 0x3E: // istore_0 to istore_3
                if (!stack.isEmpty()) {
                    int index = opcode - 0x3B;
                    ensureLocalSlot(newFrame.locals, index, INTEGER);
                    stack.remove(stack.size() - 1);
                }
                break;
            case 0x37:
            case 0x3F:
            case 0x40:
            case 0x41:
            case 0x42: // lstore*
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x37) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x3F);
                    ensureLocalSlot(newFrame.locals, index, LONG);
                    stack.remove(stack.size() - 1);
                }
                break;
            case 0x38:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46: // fstore*
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x38) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x43);
                    ensureLocalSlot(newFrame.locals, index, FLOAT);
                    stack.remove(stack.size() - 1);
                }
                break;
            case 0x39:
            case 0x47:
            case 0x48:
            case 0x49:
            case 0x4A: // dstore*
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x39) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x47);
                    ensureLocalSlot(newFrame.locals, index, DOUBLE);
                    stack.remove(stack.size() - 1);
                }
                break;
            case 0x3A:
            case 0x4B:
            case 0x4C:
            case 0x4D:
            case 0x4E: // astore*
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x3A) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x4B);
                    ensureLocalSlot(newFrame.locals, index, OBJECT);
                    stack.remove(stack.size() - 1);
                }
                break;

            // Arithmetic - pop 2, push 1
            case 0x60:
            case 0x64:
            case 0x68:
            case 0x6C:
            case 0x70:
            case 0x7E:
            case 0x80:
            case 0x82: // int ops
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(INTEGER);
                }
                break;

            // Comparisons - pop 2, push int
            case 0x94: // lcmp
            case 0x95:
            case 0x96: // fcmpl, fcmpg
            case 0x97:
            case 0x98: // dcmpl, dcmpg
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(INTEGER);
                }
                break;

            // Conditional branches - pop operands
            case 0x99:
            case 0x9A:
            case 0x9B:
            case 0x9C:
            case 0x9D:
            case 0x9E: // if<cond>
            case 0xC6:
            case 0xC7: // ifnull, ifnonnull
                if (!stack.isEmpty()) stack.remove(stack.size() - 1);
                break;
            case 0x9F:
            case 0xA0:
            case 0xA1:
            case 0xA2:
            case 0xA3:
            case 0xA4: // if_icmp<cond>
            case 0xA5:
            case 0xA6: // if_acmp<cond>
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                }
                break;

            // Method calls - simplified
            case 0xB6:
            case 0xB7:
            case 0xB8: // invoke*
                stack.add(INTEGER); // Simplified: assume returns int
                break;

            // Type conversions
            case 0x85: // i2l
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(LONG);
                }
                break;
            case 0x86: // i2f
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(FLOAT);
                }
                break;
            case 0x87: // i2d
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(DOUBLE);
                }
                break;
            case 0x88: // l2i
            case 0x8B: // f2i
            case 0x8E: // d2i
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(INTEGER);
                }
                break;
        }

        return newFrame;
    }

    private record Frame(List<Integer> locals, List<Integer> stack) {

        Frame copy() {
                return new Frame(new ArrayList<>(locals), new ArrayList<>(stack));
            }
        }

    private record WorkItem(int offset, Frame frame) {
    }
}
