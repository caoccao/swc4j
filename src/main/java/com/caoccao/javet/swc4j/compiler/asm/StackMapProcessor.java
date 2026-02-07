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


package com.caoccao.javet.swc4j.compiler.asm;

import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;

import java.util.*;

/**
 * Generates StackMapTable frames by analyzing bytecode with data flow analysis.
 */
public class StackMapProcessor {
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
    private final ClassWriter.ConstantPool constantPool;
    private final String descriptor;
    private final List<ClassWriter.ExceptionTableEntry> exceptionTable;
    private final boolean isStatic;
    private final int maxLocals;

    /**
     * Instantiates a new Stack map processor.
     *
     * @param bytecode       the bytecode
     * @param maxLocals      the max locals
     * @param isStatic       the is static
     * @param className      the class name
     * @param descriptor     the descriptor
     * @param constantPool   the constant pool
     * @param exceptionTable the exception table
     */
    public StackMapProcessor(byte[] bytecode, int maxLocals, boolean isStatic, String className, String descriptor, ClassWriter.ConstantPool constantPool, List<ClassWriter.ExceptionTableEntry> exceptionTable) {
        this.bytecode = bytecode;
        this.maxLocals = maxLocals;
        this.isStatic = isStatic;
        this.className = className;
        this.descriptor = descriptor;
        this.constantPool = constantPool;
        this.exceptionTable = exceptionTable != null ? exceptionTable : List.of();
    }

    private Map<Integer, Frame> computeFramesDataFlow() {
        Map<Integer, Frame> frames = new HashMap<>();
        Queue<WorkItem> workQueue = new LinkedList<>();
        Set<Integer> visitedHandlers = new HashSet<>();

        // Start with initial frame at offset 0
        Frame initialFrame = createInitialFrame();
        workQueue.add(new WorkItem(0, initialFrame));

        while (!workQueue.isEmpty()) {
            WorkItem item = workQueue.poll();
            int pc = item.offset;

            if (pc >= bytecode.length) continue;

            // Check if we already have a frame at this offset
            Frame existingFrame = frames.get(pc);
            if (existingFrame != null) {
                // Merge frames - if already visited, merge and check if changed
                Frame mergedFrame = mergeFrames(existingFrame, item.frame);
                if (framesEqual(mergedFrame, existingFrame)) {
                    continue; // No change, don't reprocess
                }
                frames.put(pc, mergedFrame);
            } else {
                // First visit - save the frame
                frames.put(pc, item.frame.copy());
            }

            // Get the current frame at this offset for simulation
            Frame currentFrame = frames.get(pc);

            // Check if this pc is within any try block, and propagate to handler if so
            for (ClassWriter.ExceptionTableEntry entry : exceptionTable) {
                if (pc >= entry.startPc() && pc < entry.endPc()) {
                    // This instruction is in a try block - propagate to handler
                    int handlerPc = entry.handlerPc();
                    if (!visitedHandlers.contains(handlerPc) || !frames.containsKey(handlerPc)) {
                        visitedHandlers.add(handlerPc);
                        Frame handlerFrame = currentFrame.copy();
                        handlerFrame.stack.clear();
                        // Determine exception type from catch_type
                        String exceptionClass;
                        if (entry.catchType() != 0) {
                            exceptionClass = constantPool.getClassName(entry.catchType());
                            if (exceptionClass == null) {
                                exceptionClass = ConstantJavaType.JAVA_LANG_THROWABLE;
                            }
                        } else {
                            exceptionClass = ConstantJavaType.JAVA_LANG_THROWABLE; // catch-all
                        }
                        handlerFrame.stack.add(VerificationType.object(exceptionClass));
                        workQueue.add(new WorkItem(handlerPc, handlerFrame));
                    }
                }
            }

            // Execute instruction and get next offsets
            List<Integer> nextOffsets = getNextOffsets(pc);
            for (int nextPc : nextOffsets) {
                Frame nextFrame = simulateInstruction(currentFrame, pc);
                workQueue.add(new WorkItem(nextPc, nextFrame));
            }
        }

        return frames;
    }

    /**
     * Convert verification types to the format needed by ClassWriter.StackMapEntry.
     * Returns type tags and corresponding class names for OBJECT types.
     */
    private FrameData convertVerificationTypes(List<VerificationType> types) {
        List<Integer> resultTypes = new ArrayList<>();
        List<String> resultClassNames = new ArrayList<>();

        for (int i = 0; i < types.size(); i++) {
            VerificationType type = types.get(i);

            // Skip the next TOP if current type is LONG or DOUBLE (implicit in JVM spec)
            if ((type.tag == LONG || type.tag == DOUBLE) && i + 1 < types.size() && types.get(i + 1).tag == TOP) {
                resultTypes.add(type.tag);
                // LONG and DOUBLE don't have class names
                i++; // Skip the next TOP
            } else {
                resultTypes.add(type.tag);
                // Only add class names for OBJECT types (ClassWriter expects sparse list)
                if (type.tag == OBJECT) {
                    // Every OBJECT type must have a className
                    String className = (type.className != null) ? type.className : ConstantJavaType.JAVA_LANG_OBJECT;
                    resultClassNames.add(className);
                }
            }
        }

        // Remove trailing TOPs (variables out of scope)
        // Note: resultClassNames only contains entries for OBJECT types, so we don't remove from it
        while (!resultTypes.isEmpty() && resultTypes.get(resultTypes.size() - 1) == TOP) {
            resultTypes.remove(resultTypes.size() - 1);
        }

        return new FrameData(resultTypes, resultClassNames);
    }

    /**
     * Count the number of stack slots consumed by method arguments.
     * Long and double take 2 slots, everything else takes 1.
     */
    private int countMethodArgSlots(String descriptor) {
        int count = 0;
        int i = 1; // Skip opening '('
        while (i < descriptor.length() && descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            if (c == ConstantJavaType.CHAR_LONG || c == ConstantJavaType.CHAR_DOUBLE) {
                count += 2; // Long and double take 2 slots
                i++;
            } else if (c == ConstantJavaType.CHAR_REFERENCE) {
                count++;
                // Skip to ';'
                while (i < descriptor.length() && descriptor.charAt(i) != ';') {
                    i++;
                }
                i++; // Skip ';'
            } else if (c == ConstantJavaType.CHAR_ARRAY) {
                count++;
                i++;
                // Skip array element type
                while (i < descriptor.length() && descriptor.charAt(i) == ConstantJavaType.CHAR_ARRAY) {
                    i++;
                }
                if (i < descriptor.length() && descriptor.charAt(i) == ConstantJavaType.CHAR_REFERENCE) {
                    while (i < descriptor.length() && descriptor.charAt(i) != ';') {
                        i++;
                    }
                    i++; // Skip ';'
                } else {
                    i++; // Skip primitive type
                }
            } else {
                count++; // Other primitives take 1 slot
                i++;
            }
        }
        return count;
    }

    private Frame createInitialFrame() {
        List<VerificationType> locals = new ArrayList<>();
        if (!isStatic) {
            locals.add(VerificationType.object(className)); // 'this'
        }

        // Parse method descriptor to add parameters to initial frame
        if (descriptor != null && descriptor.startsWith("(")) {
            int endParams = descriptor.indexOf(')');
            if (endParams > 0) {
                String params = descriptor.substring(1, endParams);
                int i = 0;
                while (i < params.length()) {
                    char c = params.charAt(i);
                    switch (c) {
                        case ConstantJavaType.CHAR_BYTE, ConstantJavaType.CHAR_CHARACTER, ConstantJavaType.CHAR_SHORT,
                             ConstantJavaType.CHAR_INTEGER, ConstantJavaType.CHAR_BOOLEAN -> {
                            locals.add(VerificationType.integer());
                            i++;
                        }
                        case ConstantJavaType.CHAR_LONG -> {
                            locals.add(VerificationType.long_());
                            locals.add(VerificationType.top()); // Long takes 2 slots
                            i++;
                        }
                        case ConstantJavaType.CHAR_FLOAT -> {
                            locals.add(VerificationType.float_());
                            i++;
                        }
                        case ConstantJavaType.CHAR_DOUBLE -> {
                            locals.add(VerificationType.double_());
                            locals.add(VerificationType.top()); // Double takes 2 slots
                            i++;
                        }
                        case ConstantJavaType.CHAR_REFERENCE -> {
                            // Object type - extract class name
                            int end = params.indexOf(';', i);
                            String className = params.substring(i + 1, end); // Skip 'L', stop before ';'
                            locals.add(VerificationType.object(className));
                            i = end + 1;
                        }
                        case ConstantJavaType.CHAR_ARRAY -> {
                            // Array type - extract full array descriptor
                            int start = i;
                            i++;
                            while (i < params.length() && params.charAt(i) == ConstantJavaType.CHAR_ARRAY) {
                                i++;
                            }
                            if (i < params.length()) {
                                if (params.charAt(i) == ConstantJavaType.CHAR_REFERENCE) {
                                    i = params.indexOf(';', i) + 1;
                                } else {
                                    i++;
                                }
                            }
                            String arrayDesc = params.substring(start, i);
                            locals.add(VerificationType.object(arrayDesc));
                        }
                        default -> i++; // Skip unknown
                    }
                }
            }
        }

        return new Frame(locals, new ArrayList<>());
    }

    private void ensureLocalSlot(List<VerificationType> locals, int index, VerificationType type) {
        // Extend locals list to include the specified index
        while (locals.size() <= index) {
            locals.add(VerificationType.top()); // Fill gaps with TOP
        }
        locals.set(index, type);

        // Long and Double are followed by TOP for the second slot (JVM spec §4.10.1.5)
        if (type.tag == LONG || type.tag == DOUBLE) {
            while (locals.size() <= index + 1) {
                locals.add(VerificationType.top());
            }
            // Ensure the next slot is TOP (don't overwrite if already set)
            if (locals.get(index + 1).tag != TOP) {
                locals.set(index + 1, VerificationType.top());
            }
        }
    }

    private Set<Integer> findBranchTargets() {
        Set<Integer> targets = new TreeSet<>();
        int i = 0;
        while (i < bytecode.length) {
            int opcode = bytecode[i] & 0xFF;
            int instructionSize = getInstructionSize(i, opcode);
            int nextInstructionOffset = i + instructionSize;

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
                // Add instruction after goto (if exists) - it needs a frame since it follows unconditional branch
                if (nextInstructionOffset < bytecode.length) {
                    targets.add(nextInstructionOffset);
                }
            } else if (opcode == 0xC8) { // goto_w
                if (i + 4 < bytecode.length) {
                    int offset = readInt(i + 1);
                    targets.add(i + offset);
                }
                if (nextInstructionOffset < bytecode.length) {
                    targets.add(nextInstructionOffset);
                }
            } else if (opcode == 0xC6 || opcode == 0xC7) { // ifnull, ifnonnull
                if (i + 2 < bytecode.length) {
                    short offset = (short) (((bytecode[i + 1] & 0xFF) << 8) | (bytecode[i + 2] & 0xFF));
                    targets.add(i + offset);
                }
            } else if (opcode == 0xAA) { // tableswitch
                int padding = (4 - ((i + 1) % 4)) % 4;
                int dataStart = i + 1 + padding;
                if (dataStart + 12 <= bytecode.length) {
                    int defaultOffset = readInt(dataStart);
                    int low = readInt(dataStart + 4);
                    int high = readInt(dataStart + 8);

                    targets.add(i + defaultOffset);

                    for (int j = 0; j <= high - low; j++) {
                        if (dataStart + 12 + (j * 4) + 4 <= bytecode.length) {
                            int caseOffset = readInt(dataStart + 12 + (j * 4));
                            targets.add(i + caseOffset);
                        }
                    }
                    // tableswitch has no fall-through (all paths jump)
                }
            } else if (opcode == 0xAB) { // lookupswitch
                int padding = (4 - ((i + 1) % 4)) % 4;
                int dataStart = i + 1 + padding;
                if (dataStart + 8 <= bytecode.length) {
                    int defaultOffset = readInt(dataStart);
                    int npairs = readInt(dataStart + 4);

                    targets.add(i + defaultOffset);

                    for (int j = 0; j < npairs; j++) {
                        if (dataStart + 8 + (j * 8) + 8 <= bytecode.length) {
                            int caseOffset = readInt(dataStart + 8 + (j * 8) + 4);
                            targets.add(i + caseOffset);
                        }
                    }
                    // lookupswitch has no fall-through (all paths jump)
                }
            } else if (isUnconditionalExit(opcode)) {
                // Return instructions and athrow - add instruction after them (if exists)
                if (nextInstructionOffset < bytecode.length) {
                    targets.add(nextInstructionOffset);
                }
            }
            // Properly skip to the next instruction
            i = nextInstructionOffset;
        }

        // Add exception handler targets
        for (ClassWriter.ExceptionTableEntry entry : exceptionTable) {
            targets.add(entry.handlerPc());
        }

        return targets;
    }

    /**
     * Check if two frames are equal.
     */
    private boolean framesEqual(Frame frame1, Frame frame2) {
        return frame1.locals.equals(frame2.locals) && frame1.stack.equals(frame2.stack);
    }

    /**
     * Generate list.
     *
     * @return the list
     */
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
        Frame lastFrame = null;
        for (int offset : sortedTargets) {
            Frame frame = frames.get(offset);

            // For unreachable code (e.g., after return/throw), use the last known frame
            // This is needed because the JVM verifier still processes unreachable code
            if (frame == null) {
                if (lastFrame != null) {
                    frame = lastFrame.copy();
                    // Clear stack for unreachable code
                    frame.stack.clear();
                } else {
                    continue; // No frame available, skip
                }
            }

            lastFrame = frame;

            int offsetDelta = previousOffset == -1 ? offset : offset - previousOffset - 1;

            // Convert verification types to integers (handle LONG/DOUBLE properly)
            FrameData localsData = convertVerificationTypes(frame.locals);
            FrameData stackData = convertVerificationTypes(frame.stack);

            // Pass class names for proper OBJECT type handling in StackMapTable
            entries.add(new ClassWriter.StackMapEntry(
                    offsetDelta,
                    255,
                    localsData.types,
                    stackData.types,
                    localsData.classNames,
                    stackData.classNames));
            previousOffset = offset;
        }

        return entries.isEmpty() ? null : entries;
    }

    private int getInstructionSize(int pc, int opcode) {
        return switch (opcode) {
            case 0x10, 0x12, 0x15, 0x16, 0x17, 0x18, 0x19, 0x36, 0x37, 0x38, 0x39, 0x3A,
                 0xBC -> 2;
            case 0x11, 0x13, 0x14, 0x84, 0x99, 0x9A, 0x9B, 0x9C, 0x9D, 0x9E, 0x9F, 0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5,
                 0xA6, 0xA7, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB8, 0xBB, 0xBD, 0xC0, 0xC1, 0xC6, 0xC7 -> 3;
            case 0xC8 -> 5;
            case 0xB9 -> 5;
            case 0xAA -> getTableSwitchSize(pc);
            case 0xAB -> getLookupSwitchSize(pc);
            default -> 1;
        };
    }

    private int getLookupSwitchSize(int pc) {
        // lookupswitch: opcode + padding + default + npairs + npairs * 8
        int padding = (4 - ((pc + 1) % 4)) % 4;
        int baseSize = 1 + padding + 4 + 4; // opcode + padding + default + npairs

        // Read npairs to determine table size
        int dataStart = pc + 1 + padding;
        if (dataStart + 8 > bytecode.length) return 1; // Invalid, return minimal size

        int npairs = readInt(dataStart + 4);
        int pairsSize = npairs * 8;

        return baseSize + pairsSize;
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
        } else if (opcode == 0xC8) { // goto_w
            if (pc + 4 < bytecode.length) {
                int offset = readInt(pc + 1);
                offsets.add(pc + offset);
            }
        } else if (opcode == 0xC6 || opcode == 0xC7) { // ifnull, ifnonnull
            if (pc + 2 < bytecode.length) {
                short offset = (short) (((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF));
                offsets.add(pc + offset); // Branch target
                offsets.add(pc + 3); // Fall-through
            }
        } else if (opcode == 0xAA) { // tableswitch
            int padding = (4 - ((pc + 1) % 4)) % 4;
            int dataStart = pc + 1 + padding;
            if (dataStart + 12 <= bytecode.length) {
                int defaultOffset = readInt(dataStart);
                int low = readInt(dataStart + 4);
                int high = readInt(dataStart + 8);

                // Add default target
                offsets.add(pc + defaultOffset);

                // Add all case targets
                for (int i = 0; i <= high - low; i++) {
                    if (dataStart + 12 + (i * 4) + 4 <= bytecode.length) {
                        int caseOffset = readInt(dataStart + 12 + (i * 4));
                        offsets.add(pc + caseOffset);
                    }
                }
            }
        } else if (opcode == 0xAB) { // lookupswitch
            int padding = (4 - ((pc + 1) % 4)) % 4;
            int dataStart = pc + 1 + padding;
            if (dataStart + 8 <= bytecode.length) {
                int defaultOffset = readInt(dataStart);
                int npairs = readInt(dataStart + 4);

                // Add default target
                offsets.add(pc + defaultOffset);

                // Add all case targets
                for (int i = 0; i < npairs; i++) {
                    if (dataStart + 8 + (i * 8) + 8 <= bytecode.length) {
                        int caseOffset = readInt(dataStart + 8 + (i * 8) + 4); // Skip match value, read offset
                        offsets.add(pc + caseOffset);
                    }
                }
            }
        } else if (opcode >= 0xAC && opcode <= 0xB1) { // return instructions
            // No next offset
        } else if (opcode == 0xBF) { // athrow
            // No next offset - exception is thrown
        } else {
            // Normal instruction - continue to next
            offsets.add(pc + getInstructionSize(pc, opcode));
        }

        return offsets;
    }

    /**
     * Extract the return type from a method descriptor.
     * Returns everything after ')'.
     */
    private String getReturnType(String descriptor) {
        int parenIndex = descriptor.indexOf(')');
        if (parenIndex >= 0 && parenIndex + 1 < descriptor.length()) {
            return descriptor.substring(parenIndex + 1);
        }
        return ConstantJavaType.ABBR_VOID; // Default to void
    }

    private int getTableSwitchSize(int pc) {
        // tableswitch: opcode + padding + default + low + high + (high - low + 1) * 4
        int padding = (4 - ((pc + 1) % 4)) % 4;
        int baseSize = 1 + padding + 4 + 4 + 4; // opcode + padding + default + low + high

        // Read low and high to determine table size
        int dataStart = pc + 1 + padding;
        if (dataStart + 12 > bytecode.length) return 1; // Invalid, return minimal size

        int low = readInt(dataStart + 4);
        int high = readInt(dataStart + 8);
        int tableSize = (high - low + 1) * 4;

        return baseSize + tableSize;
    }

    /**
     * Check if opcode is an unconditional exit (return or throw).
     */
    private boolean isUnconditionalExit(int opcode) {
        return opcode == 0xAC // ireturn
                || opcode == 0xAD // lreturn
                || opcode == 0xAE // freturn
                || opcode == 0xAF // dreturn
                || opcode == 0xB0 // areturn
                || opcode == 0xB1 // return (void)
                || opcode == 0xBF; // athrow
    }

    /**
     * Merge two frames at a control flow merge point.
     * Uses the common supertype for each slot.
     */
    private Frame mergeFrames(Frame frame1, Frame frame2) {
        List<VerificationType> mergedLocals = new ArrayList<>();
        List<VerificationType> mergedStack = new ArrayList<>();

        // Merge locals
        int maxLocals = Math.max(frame1.locals.size(), frame2.locals.size());
        for (int i = 0; i < maxLocals; i++) {
            VerificationType type1 = i < frame1.locals.size() ? frame1.locals.get(i) : VerificationType.top();
            VerificationType type2 = i < frame2.locals.size() ? frame2.locals.get(i) : VerificationType.top();
            mergedLocals.add(mergeTypes(type1, type2));
        }

        // Merge stack - must be same size for valid merge
        int stackSize = Math.max(frame1.stack.size(), frame2.stack.size());
        for (int i = 0; i < stackSize; i++) {
            VerificationType type1 = i < frame1.stack.size() ? frame1.stack.get(i) : VerificationType.top();
            VerificationType type2 = i < frame2.stack.size() ? frame2.stack.get(i) : VerificationType.top();
            mergedStack.add(mergeTypes(type1, type2));
        }

        return new Frame(mergedLocals, mergedStack);
    }

    /**
     * Merge two verification types into their common supertype.
     * For local variables: if a variable is undefined (TOP) on any path,
     * it should be TOP at the merge point (can't guarantee it's valid).
     */
    private VerificationType mergeTypes(VerificationType type1, VerificationType type2) {
        // If types are equal (including class names), return as-is
        if (type1.equals(type2)) {
            return type1;
        }

        // TOP means undefined/unusable - if either is TOP, result is TOP
        if (type1.tag == TOP || type2.tag == TOP) {
            return VerificationType.top();
        }

        // NULL merges with any OBJECT to that OBJECT type
        if (type1.tag == NULL && type2.tag == OBJECT) return type2;
        if (type1.tag == OBJECT && type2.tag == NULL) return type1;

        // Two OBJECT types - if same class, preserve it; otherwise use common supertype
        if (type1.tag == OBJECT && type2.tag == OBJECT) {
            if (Objects.equals(type1.className, type2.className)) {
                return type1; // Same class, preserve it
            }
            return VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT);
        }

        // Different reference types
        if (type1.tag == NULL || type2.tag == NULL) {
            return VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT);
        }

        // For incompatible primitives, return TOP (invalid state)
        return VerificationType.top();
    }

    /**
     * Push the appropriate verification type onto the stack based on field descriptor.
     * Field descriptors have the same format as return types, so we reuse the same logic.
     */
    private void pushFieldType(List<VerificationType> stack, String fieldDescriptor) {
        pushReturnType(stack, fieldDescriptor);
    }

    /**
     * Push the appropriate verification type onto the stack based on return type.
     */
    private void pushReturnType(List<VerificationType> stack, String returnType) {
        if (returnType == null || returnType.isEmpty() || returnType.equals(ConstantJavaType.ABBR_VOID)) {
            // Void return - don't push anything
            return;
        }
        char c = returnType.charAt(0);
        switch (c) {
            case ConstantJavaType.CHAR_BOOLEAN, ConstantJavaType.CHAR_BYTE, ConstantJavaType.CHAR_CHARACTER,
                 ConstantJavaType.CHAR_SHORT, ConstantJavaType.CHAR_INTEGER -> stack.add(VerificationType.integer());
            case ConstantJavaType.CHAR_LONG -> stack.add(VerificationType.long_());
            case ConstantJavaType.CHAR_FLOAT -> stack.add(VerificationType.float_());
            case ConstantJavaType.CHAR_DOUBLE -> stack.add(VerificationType.double_());
            case ConstantJavaType.CHAR_REFERENCE -> {
                // Object type - extract class name (remove L and ;)
                String className = returnType.substring(1, returnType.length() - 1);
                stack.add(VerificationType.object(className));
            }
            case ConstantJavaType.CHAR_ARRAY -> {
                // Array type
                stack.add(VerificationType.object(returnType));
            }
            default -> stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
        }
    }

    private int readInt(int offset) {
        if (offset + 4 > bytecode.length) return 0;
        return ((bytecode[offset] & 0xFF) << 24) |
                ((bytecode[offset + 1] & 0xFF) << 16) |
                ((bytecode[offset + 2] & 0xFF) << 8) |
                (bytecode[offset + 3] & 0xFF);
    }

    private Frame simulateInstruction(Frame frame, int pc) {
        Frame newFrame = frame.copy();
        int opcode = bytecode[pc] & 0xFF;
        List<VerificationType> stack = newFrame.stack;

        // Simulate stack effects
        switch (opcode) {
            case 0x01 -> stack.add(VerificationType.null_());
            case 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x10, 0x11 -> stack.add(VerificationType.integer());
            case 0x09, 0x0a -> stack.add(VerificationType.long_());
            case 0x0b, 0x0c, 0x0d -> stack.add(VerificationType.float_());
            case 0x0e, 0x0f -> stack.add(VerificationType.double_());
            case 0x12, 0x13 -> {
                // Get constant pool index from bytecode
                int cpIndex;
                if (opcode == 0x12) {
                    cpIndex = bytecode[pc + 1] & 0xFF;
                } else {
                    cpIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                }
                // Look up constant type from constant pool
                char constType = (constantPool != null) ? constantPool.getConstantType(cpIndex) : 'O';
                switch (constType) {
                    case 'I' -> stack.add(VerificationType.integer());
                    case 'F' -> stack.add(VerificationType.float_());
                    case 'S' -> stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_STRING));
                    case 'C' -> stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_CLASS));
                    default -> stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                }
            }
            case 0x14 -> // ldc2_w (long or double)
            {
                // Get constant pool index from bytecode
                int cpIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                // Look up constant type from constant pool
                char constType = (constantPool != null) ? constantPool.getConstantType(cpIndex) : ConstantJavaType.CHAR_DOUBLE;
                if (constType == ConstantJavaType.CHAR_LONG) {
                    stack.add(VerificationType.long_());
                } else {
                    stack.add(VerificationType.double_());
                }
            }
            // Loads
            case 0x15, 0x1A, 0x1B, 0x1C, 0x1D -> stack.add(VerificationType.integer()); // iload*
            case 0x16, 0x1E, 0x1F, 0x20, 0x21 -> stack.add(VerificationType.long_()); // lload*
            case 0x17, 0x22, 0x23, 0x24, 0x25 -> stack.add(VerificationType.float_()); // fload*
            case 0x18, 0x26, 0x27, 0x28, 0x29 -> stack.add(VerificationType.double_()); // dload*
            case 0x19, 0x2A, 0x2B, 0x2C, 0x2D -> {
                int aloadIndex = (opcode == 0x19) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x2A);
                if (aloadIndex < newFrame.locals.size()) {
                    stack.add(newFrame.locals.get(aloadIndex));
                } else {
                    stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                }
            }
            // Stores - pop from stack and update locals
            case 0x36 -> {
                if (!stack.isEmpty()) {
                    int index = bytecode[pc + 1] & 0xFF;
                    ensureLocalSlot(newFrame.locals, index, VerificationType.integer());
                    stack.remove(stack.size() - 1);
                }
            }
            case 0x3B, 0x3C, 0x3D, 0x3E -> {
                if (!stack.isEmpty()) {
                    int index = opcode - 0x3B;
                    ensureLocalSlot(newFrame.locals, index, VerificationType.integer());
                    stack.remove(stack.size() - 1);
                }
            }
            case 0x37, 0x3F, 0x40, 0x41, 0x42 -> {
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x37) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x3F);
                    ensureLocalSlot(newFrame.locals, index, VerificationType.long_());
                    stack.remove(stack.size() - 1);
                }
            }
            case 0x38, 0x43, 0x44, 0x45, 0x46 -> {
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x38) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x43);
                    ensureLocalSlot(newFrame.locals, index, VerificationType.float_());
                    stack.remove(stack.size() - 1);
                }
            }
            case 0x39, 0x47, 0x48, 0x49, 0x4A -> {
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x39) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x47);
                    ensureLocalSlot(newFrame.locals, index, VerificationType.double_());
                    stack.remove(stack.size() - 1);
                }
            }
            case 0x3A, 0x4B, 0x4C, 0x4D, 0x4E -> {
                if (!stack.isEmpty()) {
                    int index = (opcode == 0x3A) ? (bytecode[pc + 1] & 0xFF) : (opcode - 0x4B);
                    VerificationType valueType = stack.get(stack.size() - 1);
                    ensureLocalSlot(newFrame.locals, index, valueType);
                    stack.remove(stack.size() - 1);
                }
            }
            // Arithmetic - pop 2, push 1
            // Int operations: iadd(0x60), isub(0x64), imul(0x68), idiv(0x6C), irem(0x70), iand(0x7E), ior(0x80), ixor(0x82)
            case 0x60, 0x64, 0x68, 0x6C, 0x70, 0x7E, 0x80, 0x82 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.integer());
                }
            }
            // Long operations: ladd(0x61), lsub(0x65), lmul(0x69), ldiv(0x6D), lrem(0x71), land(0x7F), lor(0x81), lxor(0x83)
            case 0x61, 0x65, 0x69, 0x6D, 0x71, 0x7F, 0x81, 0x83 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.long_());
                }
            }
            // Float operations: fadd(0x62), fsub(0x66), fmul(0x6A), fdiv(0x6E), frem(0x72)
            case 0x62, 0x66, 0x6A, 0x6E, 0x72 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.float_());
                }
            }
            // Double operations: dadd(0x63), dsub(0x67), dmul(0x6B), ddiv(0x6F), drem(0x73)
            case 0x63, 0x67, 0x6B, 0x6F, 0x73 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.double_());
                }
            }
            // Comparisons - pop 2, push int, lcmp, fcmpl, fcmpg
            case 0x94, 0x95, 0x96, 0x97, 0x98 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.integer());
                }
            }
            // iinc - increment local variable (doesn't affect stack)
            case 0x84 -> {
                // iinc <index> <const> - just increments a local variable
                // Stack is unchanged, local stays INTEGER type
            }
            // Stack manipulation
            case 0x57 -> {
                if (!stack.isEmpty()) stack.remove(stack.size() - 1);
            }
            case 0x58 -> {
                // pop2 removes either one category-2 value or two category-1 values
                if (!stack.isEmpty()) {
                    VerificationType topType = stack.remove(stack.size() - 1);
                    // If it wasn't a category-2 type, pop another
                    if (topType.tag != LONG && topType.tag != DOUBLE && !stack.isEmpty()) {
                        stack.remove(stack.size() - 1);
                    }
                }
            }
            case 0x59 -> {
                if (!stack.isEmpty()) {
                    stack.add(stack.get(stack.size() - 1));
                }
            }
            case 0x5C -> {
                if (!stack.isEmpty()) {
                    VerificationType topType = stack.get(stack.size() - 1);
                    if (topType.tag == LONG || topType.tag == DOUBLE) {
                        // Category 2: duplicate one value
                        stack.add(topType);
                    } else if (stack.size() >= 2) {
                        // Category 1: duplicate two values
                        VerificationType second = stack.get(stack.size() - 2);
                        stack.add(second);
                        stack.add(topType);
                    }
                }
            }
            case 0x5F -> {
                if (stack.size() >= 2) {
                    VerificationType top = stack.remove(stack.size() - 1);
                    VerificationType second = stack.remove(stack.size() - 1);
                    stack.add(top);
                    stack.add(second);
                }
            }
            // Conditional branches - pop operands
            // if<cond>
            case 0x99, 0x9A, 0x9B, 0x9C, 0x9D, 0x9E, 0xC6, 0xC7 -> {
                if (!stack.isEmpty()) stack.remove(stack.size() - 1);
            }
            // if_icmp<cond>
            case 0x9F, 0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1);
                    stack.remove(stack.size() - 1);
                }
            }
            // Switch instructions - pop int
            // tableswitch
            case 0xAA, 0xAB -> {
                if (!stack.isEmpty()) stack.remove(stack.size() - 1);
            }
            // Method calls - use constant pool to determine argument count and return type
            // invokevirtual
            // invokespecial
            case 0xB6, 0xB7, 0xB8 -> // invokestatic
            {
                // Get method reference index from bytecode
                int methodIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                // Look up method descriptor from constant pool
                String methodDescriptor = (constantPool != null) ? constantPool.getMethodDescriptor(methodIndex) : null;
                if (methodDescriptor != null) {
                    // Parse descriptor to get argument count and return type
                    // Format: (args)return e.g., ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING__LJAVA_LANG_STRING
                    int argCount = countMethodArgSlots(methodDescriptor);
                    String returnType = getReturnType(methodDescriptor);
                    // Pop arguments (and receiver for non-static)
                    int totalPops = argCount;
                    if (opcode != 0xB8) { // Non-static methods also pop receiver
                        totalPops++;
                    }
                    for (int i = 0; i < totalPops && !stack.isEmpty(); i++) {
                        stack.remove(stack.size() - 1);
                    }
                    // Push return type
                    pushReturnType(stack, returnType);
                } else {
                    // Fallback: clear stack and use heuristics
                    stack.clear();
                    int nextPc = pc + 3;
                    int nextOpcode = (nextPc < bytecode.length) ? (bytecode[nextPc] & 0xFF) : 0;
                    if (nextOpcode == 0x57 || nextOpcode == 0x58) {
                        stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                    } else if (nextOpcode == 0x59) {
                        stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                    } else if (opcode == 0xB7) {
                        // void return for invokespecial
                    } else {
                        stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                    }
                }
            }
            case 0xB9 -> // invokeinterface
            // invokeinterface: pop receiver and args, push result
            {
                // Get method reference index from bytecode
                int methodIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                // Look up method descriptor from constant pool
                String methodDescriptor = (constantPool != null) ? constantPool.getMethodDescriptor(methodIndex) : null;
                if (methodDescriptor != null) {
                    // Parse descriptor to get argument count and return type
                    int argCount = countMethodArgSlots(methodDescriptor);
                    String returnType = getReturnType(methodDescriptor);
                    // Pop arguments + receiver (interface methods are never static)
                    int totalPops = argCount + 1;
                    for (int i = 0; i < totalPops && !stack.isEmpty(); i++) {
                        stack.remove(stack.size() - 1);
                    }
                    // Push return type
                    pushReturnType(stack, returnType);
                } else {
                    // Fallback: use count byte to determine pops
                    int count = (pc + 3 < bytecode.length) ? (bytecode[pc + 3] & 0xFF) : 1;
                    for (int i = 0; i < count && !stack.isEmpty(); i++) {
                        stack.remove(stack.size() - 1);
                    }
                    // Determine return type from next instruction
                    int nextPc = pc + 5;
                    int nextOpcode = (nextPc < bytecode.length) ? (bytecode[nextPc] & 0xFF) : 0;
                    if (nextOpcode == 0x99 || nextOpcode == 0x9A) {
                        stack.add(VerificationType.integer());
                    } else {
                        stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                    }
                }
            }
            // Field access
            case 0xB2 -> // getstatic - pop nothing, push field value
            {
                int fieldIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                String fieldDescriptor = (constantPool != null) ? constantPool.getFieldDescriptor(fieldIndex) : null;
                if (fieldDescriptor != null) {
                    pushFieldType(stack, fieldDescriptor);
                } else {
                    stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                }
            }
            case 0xB3 -> // putstatic - pop value, push nothing
            {
                int fieldIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                String fieldDescriptor = (constantPool != null) ? constantPool.getFieldDescriptor(fieldIndex) : null;
                if (fieldDescriptor != null) {
                    // Pop based on field type (double/long take 2 slots conceptually but are single stack items)
                    if (!stack.isEmpty()) {
                        stack.remove(stack.size() - 1);
                    }
                } else if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                }
            }
            case 0xB4 -> // getfield - pop objectref, push field value
            {
                int fieldIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                String fieldDescriptor = (constantPool != null) ? constantPool.getFieldDescriptor(fieldIndex) : null;
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // pop objectref
                }
                if (fieldDescriptor != null) {
                    pushFieldType(stack, fieldDescriptor);
                } else {
                    stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                }
            }
            case 0xB5 -> // putfield - pop objectref and value, push nothing
            {
                // Pop value first, then objectref
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // pop value
                }
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // pop objectref
                }
            }
            // Object creation
            case 0xBB -> { // new
                int classIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                String newClassName = (constantPool != null) ? constantPool.getClassName(classIndex) : null;
                stack.add(VerificationType.object(newClassName != null ? newClassName : ConstantJavaType.JAVA_LANG_OBJECT));
            }
            // Type conversions, i2l, f2l
            case 0x85, 0x8C, 0x8F -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.long_());
                }
            }
            // i2f, l2f
            case 0x86, 0x89, 0x90 -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.float_());
                }
            }
            // i2d, l2d
            case 0x87, 0x8A, 0x8D -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.double_());
                }
            }
            // l2i, f2i
            case 0x88, 0x8B, 0x8E -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.integer());
                }
            }
            // Array loads - pop arrayref and index, push element
            // iaload - pop int[], pop index, push int
            // baload - pop byte[]/boolean[], pop index, push int (byte/boolean widened to int)
            // caload - pop char[], pop index, push int (char widened to int)
            case 0x2E, 0x33, 0x34, 0x35 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.integer());
                }
            }
            case 0x2F -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.long_());
                }
            }
            case 0x30 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.float_());
                }
            }
            case 0x31 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.double_());
                }
            }
            case 0x32 -> {
                if (stack.size() >= 2) {
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.object(ConstantJavaType.JAVA_LANG_OBJECT));
                }
            }
            // Array stores - pop arrayref, index, and value
            // iastore - pop int[], pop index, pop int
            // bastore - pop byte[]/boolean[], pop index, pop int
            // castore - pop char[], pop index, pop int
            // sastore - pop short[], pop index, pop int
            // lastore - pop long[], pop index, pop long
            // fastore - pop float[], pop index, pop float
            // dastore - pop double[], pop index, pop double
            case 0x4F, 0x54, 0x55, 0x56, 0x50, 0x51, 0x52, 0x53 -> {
                if (stack.size() >= 3) {
                    stack.remove(stack.size() - 1); // value
                    stack.remove(stack.size() - 1); // index
                    stack.remove(stack.size() - 1); // arrayref
                }
            }
            // arraylength - pop arrayref, push int
            case 0xBE -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // arrayref
                    stack.add(VerificationType.integer());
                }
            }
            // newarray - pop count, push array reference
            case 0xBC -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // count
                    // atype byte determines the primitive type, but result is always an array
                    stack.add(VerificationType.object(ConstantJavaType.ARRAY_I)); // Generic - actual type depends on atype
                }
            }
            // anewarray - pop count, push array reference
            case 0xBD -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1); // count
                    stack.add(VerificationType.object(ConstantJavaType.ARRAY_LJAVA_LANG_OBJECT));
                }
            }
            // Type checking
            case 0xC0 -> {
                if (!stack.isEmpty()) {
                    // Get class index from bytecode
                    int classIndex = ((bytecode[pc + 1] & 0xFF) << 8) | (bytecode[pc + 2] & 0xFF);
                    String className = (constantPool != null) ? constantPool.getClassName(classIndex) : ConstantJavaType.JAVA_LANG_OBJECT;
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.object(className));
                }
            }
            case 0xC1 -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                    stack.add(VerificationType.integer());
                }
            }
            // athrow - pops exception objectref, terminates normal flow
            case 0xBF -> {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                }
            }
        }
        return newFrame;
    }

    private record Frame(List<VerificationType> locals, List<VerificationType> stack) {

        /**
         * Copy frame.
         *
         * @return the frame
         */
        Frame copy() {
            return new Frame(new ArrayList<>(locals), new ArrayList<>(stack));
        }
    }

    /**
     * Helper record to hold both type tags and class names.
     */
    private record FrameData(List<Integer> types, List<String> classNames) {
    }

    /**
     * Represents a verification type in the stackmap table.
     * Can be a primitive type (int, float, etc.) or an object type with class name.
     */
    private static class VerificationType {
        /**
         * The Class name.
         */
        final String className; // For OBJECT types, the internal class name (e.g., ConstantJavaType.JAVA_LANG_STRING)
        /**
         * The Tag.
         */
        final int tag; // TOP, INTEGER, FLOAT, LONG, DOUBLE, NULL, UNINITIALIZED_THIS, OBJECT, etc.

        /**
         * Instantiates a new Verification type.
         *
         * @param tag the tag
         */
        VerificationType(int tag) {
            this.tag = tag;
            this.className = null;
        }

        /**
         * Instantiates a new Verification type.
         *
         * @param tag       the tag
         * @param className the class name
         */
        VerificationType(int tag, String className) {
            this.tag = tag;
            this.className = className;
        }

        /**
         * Double verification type.
         *
         * @return the verification type
         */
        static VerificationType double_() {
            return new VerificationType(DOUBLE);
        }

        /**
         * Float verification type.
         *
         * @return the verification type
         */
        static VerificationType float_() {
            return new VerificationType(FLOAT);
        }

        /**
         * Integer verification type.
         *
         * @return the verification type
         */
        static VerificationType integer() {
            return new VerificationType(INTEGER);
        }

        /**
         * Long verification type.
         *
         * @return the verification type
         */
        static VerificationType long_() {
            return new VerificationType(LONG);
        }

        /**
         * Null verification type.
         *
         * @return the verification type
         */
        static VerificationType null_() {
            return new VerificationType(NULL);
        }

        /**
         * Object verification type.
         *
         * @param className the class name
         * @return the verification type
         */
        static VerificationType object(String className) {
            return new VerificationType(OBJECT, className);
        }

        /**
         * Top verification type.
         *
         * @return the verification type
         */
        static VerificationType top() {
            return new VerificationType(TOP);
        }

        /**
         * Uninitialized this verification type.
         *
         * @return the verification type
         */
        static VerificationType uninitializedThis() {
            return new VerificationType(UNINITIALIZED_THIS);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VerificationType that)) return false;
            return tag == that.tag && Objects.equals(className, that.className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, className);
        }

        @Override
        public String toString() {
            if (className != null) {
                return "Object(" + className + ")";
            }
            return switch (tag) {
                case TOP -> "Top";
                case INTEGER -> "Integer";
                case FLOAT -> "Float";
                case LONG -> "Long";
                case DOUBLE -> "Double";
                case NULL -> "Null";
                case UNINITIALIZED_THIS -> "UninitializedThis";
                default -> "Unknown(" + tag + ")";
            };
        }
    }

    private record WorkItem(int offset, Frame frame) {
    }
}
