/*
 * Copyright (c) 2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstSwitchCase;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstSwitchStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ExpressionGenerator;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.*;

/**
 * Generator for switch statements.
 * <p>
 * Handles both tableswitch (dense cases) and lookupswitch (sparse cases).
 */
public final class SwitchStatementGenerator {
    private static final double DENSITY_THRESHOLD = 0.5;
    private static final int MAX_TABLE_SWITCH_RANGE = 10000;

    private SwitchStatementGenerator() {
    }

    /**
     * Analyze switch cases and extract case information (integer switches).
     */
    private static List<CaseInfo> analyzeCases(Swc4jAstSwitchStmt switchStmt) throws Swc4jByteCodeCompilerException {
        List<CaseInfo> cases = new ArrayList<>();
        Set<Integer> seenValues = new HashSet<>();
        boolean hasDefault = false;

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            if (switchCase.getTest().isEmpty()) {
                // Default case
                if (hasDefault) {
                    throw new Swc4jByteCodeCompilerException("Duplicate default case in switch statement");
                }
                hasDefault = true;
                cases.add(new CaseInfo((Integer) null, switchCase.getCons(), true));
            } else {
                // Regular case
                ISwc4jAstExpr testExpr = switchCase.getTest().get();
                Integer caseValue = extractConstantIntValue(testExpr);
                if (caseValue == null) {
                    throw new Swc4jByteCodeCompilerException(
                            "Switch case value must be a constant integer: " + testExpr.getClass().getSimpleName());
                }

                if (seenValues.contains(caseValue)) {
                    throw new Swc4jByteCodeCompilerException("Duplicate case value: " + caseValue);
                }
                seenValues.add(caseValue);

                cases.add(new CaseInfo(caseValue, switchCase.getCons(), false));
            }
        }

        return cases;
    }


    /**
     * Build lookupswitch instruction bytes.
     */
    private static void buildLookupSwitch(List<Byte> bytes, int actualPosition, int defaultOffset, int[][] matchOffsets) {
        bytes.add((byte) 0xAB); // lookupswitch opcode

        // Calculate padding based on actual position in code
        int padding = (4 - ((actualPosition + 1) % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            bytes.add((byte) 0);
        }

        // Write default offset
        writeInt(bytes, defaultOffset - actualPosition);

        // Write npairs
        writeInt(bytes, matchOffsets.length);

        // Write match-offset pairs
        for (int[] pair : matchOffsets) {
            writeInt(bytes, pair[0]); // match value
            writeInt(bytes, pair[1] - actualPosition); // offset
        }
    }

    /**
     * Build tableswitch instruction bytes.
     */
    private static void buildTableSwitch(List<Byte> bytes, int actualPosition, int defaultOffset, int low, int high, int[] jumpOffsets) {
        bytes.add((byte) 0xAA); // tableswitch opcode

        // Calculate padding based on actual position in code
        int padding = (4 - ((actualPosition + 1) % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            bytes.add((byte) 0);
        }

        // Write default offset
        writeInt(bytes, defaultOffset - actualPosition);

        // Write low and high
        writeInt(bytes, low);
        writeInt(bytes, high);

        // Write jump offsets
        for (int offset : jumpOffsets) {
            writeInt(bytes, offset - actualPosition);
        }
    }

    /**
     * Calculate the size of the switch instruction in bytes.
     */
    private static int calculateSwitchInstructionSize(List<CaseInfo> cases, boolean useTableSwitch, int switchStart) {
        if (useTableSwitch) {
            List<Integer> values = new ArrayList<>();
            for (CaseInfo caseInfo : cases) {
                if (!caseInfo.isDefault) {
                    values.add(caseInfo.caseValue);
                }
            }

            if (values.isEmpty()) {
                // Only default - use lookupswitch with 0 pairs
                int paddingSize = (4 - ((switchStart + 1) % 4)) % 4;
                return 1 + paddingSize + 4 + 4; // opcode + pad + default + npairs
            }

            Collections.sort(values);
            int low = values.get(0);
            int high = values.get(values.size() - 1);
            int range = high - low + 1;

            int paddingSize = (4 - ((switchStart + 1) % 4)) % 4;
            return 1 + paddingSize + 4 + 4 + 4 + (range * 4); // opcode + pad + default + low + high + table
        } else {
            List<Integer> values = new ArrayList<>();
            for (CaseInfo caseInfo : cases) {
                if (!caseInfo.isDefault) {
                    values.add(caseInfo.caseValue);
                }
            }

            int paddingSize = (4 - ((switchStart + 1) % 4)) % 4;
            return 1 + paddingSize + 4 + 4 + (values.size() * 8); // opcode + pad + default + npairs + pairs
        }
    }

    /**
     * Extract constant integer value from expression.
     * Returns null if not a constant integer.
     */
    private static Integer extractConstantIntValue(ISwc4jAstExpr expr) {
        // Handle unary minus for negative numbers
        if (expr instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr unaryExpr) {
            if (unaryExpr.getOp() == com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp.Minus) {
                Integer value = extractConstantIntValue(unaryExpr.getArg());
                return value != null ? -value : null;
            }
            if (unaryExpr.getOp() == com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp.Plus) {
                return extractConstantIntValue(unaryExpr.getArg());
            }
            return null;
        }

        if (expr instanceof Swc4jAstNumber number) {
            String raw = number.getRaw().orElse("0");
            try {
                // Handle different number formats
                if (raw.startsWith("0x") || raw.startsWith("0X")) {
                    return Integer.parseInt(raw.substring(2), 16);
                } else if (raw.startsWith("0o") || raw.startsWith("0O")) {
                    return Integer.parseInt(raw.substring(2), 8);
                } else if (raw.startsWith("0b") || raw.startsWith("0B")) {
                    return Integer.parseInt(raw.substring(2), 2);
                } else {
                    return (int) number.getValue();
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extract constant string value from expression.
     * Returns null if not a constant string.
     */
    private static String extractConstantStringValue(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstStr strExpr) {
            return strExpr.getValue();
        }
        return null;
    }

    /**
     * Generate bytecode for a string switch statement.
     * Uses simple if-else chain that jumps directly to case bodies.
     * Handles fall-through and empty cases correctly.
     */
    private static void generateStringSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // 1. Analyze string cases
        List<StringCaseInfo> stringCases = analyzeStringCases(switchStmt);

        if (stringCases.isEmpty()) {
            // Empty switch - just evaluate discriminant for side effects
            ExpressionGenerator.generate(code, cp, switchStmt.getDiscriminant(), null, context, options);
            code.pop();
            return;
        }

        // 2. Store discriminant in local variable
        int strLocal = context.getLocalVariableTable().allocateVariable("$switch$str", "Ljava/lang/String;");
        ExpressionGenerator.generate(code, cp, switchStmt.getDiscriminant(), null, context, options);
        code.astore(strLocal);

        // 3. Set up break label
        CompilationContext.LoopLabelInfo breakLabel = new CompilationContext.LoopLabelInfo(null);
        context.pushBreakLabel(breakLabel);

        int equalsRef = cp.addMethodRef("java/lang/String", "equals", "(Ljava/lang/Object;)Z");
        StringCaseInfo defaultCase = null;
        List<StringCaseInfo> regularCases = new ArrayList<>();

        // Separate regular cases from default
        for (StringCaseInfo caseInfo : stringCases) {
            if (caseInfo.caseValue != null) {
                regularCases.add(caseInfo);
            } else {
                defaultCase = caseInfo;
            }
        }

        // 4. Phase 1: Generate comparisons with conditional jumps to case bodies
        List<Integer> ifnePositions = new ArrayList<>();

        for (StringCaseInfo caseInfo : regularCases) {
            // if (str.equals("caseValue")) goto caseBody
            code.aload(strLocal);
            code.ldc(cp.addString(caseInfo.caseValue));
            code.invokevirtual(equalsRef);

            int ifnePos = code.getCurrentOffset();
            code.ifne(0); // if true, jump to case body - will be patched
            ifnePositions.add(ifnePos);
        }

        // After all comparisons, jump to default (or end)
        int gotoDefaultPos = code.getCurrentOffset();
        code.gotoLabel(0); // Will be patched

        // 5. Phase 2: Generate case bodies sequentially (allows fall-through)
        for (int i = 0; i < regularCases.size(); i++) {
            StringCaseInfo caseInfo = regularCases.get(i);

            // Patch the ifne to jump here
            int caseBodyStart = code.getCurrentOffset();
            int ifnePos = ifnePositions.get(i);
            code.patchShort(ifnePos + 1, caseBodyStart - ifnePos);

            // Generate case body
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                StatementGenerator.generate(code, cp, stmt, returnTypeInfo, context, options);
            }
            // Fall-through is automatic - no goto unless break was generated
        }

        // 6. Generate default case
        int defaultStart = code.getCurrentOffset();
        code.patchShort(gotoDefaultPos + 1, defaultStart - gotoDefaultPos);

        if (defaultCase != null) {
            for (ISwc4jAstStmt stmt : defaultCase.statements) {
                StatementGenerator.generate(code, cp, stmt, returnTypeInfo, context, options);
            }
        }

        // 7. End of switch
        int endLabel = code.getCurrentOffset();

        // Patch break statements
        for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();
    }

    /**
     * Analyze string cases and build StringCaseInfo list.
     */
    private static List<StringCaseInfo> analyzeStringCases(Swc4jAstSwitchStmt switchStmt) {
        List<StringCaseInfo> result = new ArrayList<>();

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            Optional<ISwc4jAstExpr> testOpt = switchCase.getTest();
            String caseValue = null;

            if (testOpt.isPresent()) {
                caseValue = extractConstantStringValue(testOpt.get());
            }

            StringCaseInfo info = new StringCaseInfo(caseValue, switchCase.getCons());
            result.add(info);
        }

        return result;
    }

    /**
     * Check if table switch should be used for position-based switch.
     */
    private static boolean shouldUseTableSwitchForPositions(List<Integer> positions) {
        if (positions.isEmpty()) {
            return false;
        }

        int min = positions.get(0);
        int max = positions.get(positions.size() - 1);
        long tableSize = (long) max - min + 1;
        long lookupSize = positions.size();

        // Use tableswitch if range is reasonable and space-efficient
        return tableSize <= lookupSize * 2 && tableSize <= 256;
    }

    /**
     * Calculate size of lookupswitch instruction.
     */
    private static int calculateLookupSwitchSize(int numCases, int startOffset) {
        int padding = (4 - ((startOffset + 1) % 4)) % 4;
        return 1 + padding + 8 + (numCases * 8); // opcode + padding + default + count + (match-offset pairs)
    }

    /**
     * Calculate size of tableswitch instruction for position range.
     */
    private static int calculateTableSwitchSize(List<Integer> positions, int startOffset) {
        if (positions.isEmpty()) {
            return 0;
        }
        int min = positions.get(0);
        int max = positions.get(positions.size() - 1);
        int numEntries = max - min + 1;
        int padding = (4 - ((startOffset + 1) % 4)) % 4;
        return 1 + padding + 12 + (numEntries * 4); // opcode + padding + default + low + high + offsets
    }

    /**
     * Patch lookupswitch instruction using byte list approach.
     */
    private static void patchLookupSwitch(
            CodeBuilder code,
            int switchStart,
            int defaultOffset,
            List<Integer> keys,
            Map<Integer, Integer> keyToOffset) {

        List<Byte> switchBytes = new ArrayList<>();

        // Build match-offset pairs
        int[][] matchOffsets = new int[keys.size()][2];
        for (int i = 0; i < keys.size(); i++) {
            int key = keys.get(i);
            matchOffsets[i][0] = key;
            matchOffsets[i][1] = keyToOffset.get(key);
        }

        // Build the lookupswitch instruction
        buildLookupSwitch(switchBytes, switchStart, defaultOffset, matchOffsets);

        // Convert to byte array and patch
        byte[] bytes = new byte[switchBytes.size()];
        for (int i = 0; i < switchBytes.size(); i++) {
            bytes[i] = switchBytes.get(i);
        }
        code.patchBytes(switchStart, bytes);
    }

    /**
     * Patch tableswitch instruction for position-based switch using byte list approach.
     */
    private static void patchTableSwitch(
            CodeBuilder code,
            int switchStart,
            int defaultOffset,
            List<Integer> positions,
            Map<Integer, Integer> positionToOffset) {

        List<Byte> switchBytes = new ArrayList<>();

        int min = positions.get(0);
        int max = positions.get(positions.size() - 1);

        // Build jump offsets array
        int[] jumpOffsets = new int[max - min + 1];
        for (int i = 0; i < jumpOffsets.length; i++) {
            int position = min + i;
            jumpOffsets[i] = positionToOffset.getOrDefault(position, defaultOffset);
        }

        // Build the tableswitch instruction
        buildTableSwitch(switchBytes, switchStart, defaultOffset, min, max, jumpOffsets);

        // Convert to byte array and patch
        byte[] bytes = new byte[switchBytes.size()];
        for (int i = 0; i < switchBytes.size(); i++) {
            bytes[i] = switchBytes.get(i);
        }
        code.patchBytes(switchStart, bytes);
    }

    /**
     * Information about a string case.
     */
    private static class StringCaseInfo {
        String caseValue;        // null for default case
        List<ISwc4jAstStmt> statements;
        int gotoPosition;        // position of the conditional jump instruction
        int bodyOffset;          // offset of the case body

        StringCaseInfo(String caseValue, List<ISwc4jAstStmt> statements) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.gotoPosition = -1;
            this.bodyOffset = -1;
        }
    }

    /**
     * Generate bytecode for a switch statement.
     * Supports both integer and string switches.
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // 1. Determine discriminant type
        String discriminantType = TypeResolver.inferTypeFromExpr(switchStmt.getDiscriminant(), context, options);

        // 2. Route to appropriate generator
        if ("Ljava/lang/String;".equals(discriminantType)) {
            generateStringSwitch(code, cp, switchStmt, returnTypeInfo, context, options);
            return;
        }

        // 3. Integer switch (default)
        generateIntegerSwitch(code, cp, switchStmt, returnTypeInfo, context, options);
    }

    /**
     * Generate bytecode for an integer switch statement.
     */
    private static void generateIntegerSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // 1. Analyze cases
        List<CaseInfo> cases = analyzeCases(switchStmt);

        // 2. Evaluate discriminant (push value onto stack)
        ExpressionGenerator.generate(code, cp, switchStmt.getDiscriminant(), null, context, options);

        if (cases.isEmpty()) {
            // Empty switch - just pop the discriminant value
            code.pop();
            return;
        }

        // 3. Determine tableswitch vs lookupswitch
        boolean useTableSwitch = shouldUseTableSwitch(cases);

        // 4. Mark switch start and reserve space for switch instruction
        int switchStart = code.getCurrentOffset();
        int switchInstrSize = calculateSwitchInstructionSize(cases, useTableSwitch, switchStart);

        // Emit placeholder bytes for the switch instruction
        for (int i = 0; i < switchInstrSize; i++) {
            code.emitByte(0);
        }

        // 5. Push break label and generate case bodies directly to main builder
        CompilationContext.LoopLabelInfo breakLabel = new CompilationContext.LoopLabelInfo(null);
        context.pushBreakLabel(breakLabel);

        // Generate case bodies and record their offsets
        for (CaseInfo caseInfo : cases) {
            caseInfo.labelOffset = code.getCurrentOffset();
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                StatementGenerator.generate(code, cp, stmt, returnTypeInfo, context, options);
            }
        }

        // 6. Mark end label
        int endLabel = code.getCurrentOffset();

        // Patch break statements
        for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();

        // 7. Determine default offset
        int defaultCaseOffset = -1;
        for (CaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                defaultCaseOffset = caseInfo.labelOffset;
                break;
            }
        }
        int defaultOffset = defaultCaseOffset != -1 ? defaultCaseOffset : endLabel;

        // 8. Patch the switch instruction with correct offsets
        patchSwitchInstructionInPlace(code, switchStart, cases, useTableSwitch, defaultOffset);
    }

    /**
     * Patch the switch instruction with correct case offsets.
     */
    private static void patchSwitchInstructionInPlace(
            CodeBuilder code,
            int switchStart,
            List<CaseInfo> cases,
            boolean useTableSwitch,
            int defaultOffset) {

        // Build the switch instruction bytes
        List<Byte> switchBytes = new ArrayList<>();

        if (useTableSwitch) {
            Map<Integer, Integer> caseOffsets = new HashMap<>();
            for (CaseInfo caseInfo : cases) {
                if (!caseInfo.isDefault) {
                    caseOffsets.put(caseInfo.caseValue, caseInfo.labelOffset);
                }
            }

            if (caseOffsets.isEmpty()) {
                // Only default - use lookupswitch with 0 pairs
                buildLookupSwitch(switchBytes, switchStart, defaultOffset, new int[0][]);
            } else {
                List<Integer> values = new ArrayList<>(caseOffsets.keySet());
                Collections.sort(values);
                int low = values.get(0);
                int high = values.get(values.size() - 1);

                int[] jumpOffsets = new int[high - low + 1];
                for (int i = 0; i < jumpOffsets.length; i++) {
                    int caseValue = low + i;
                    jumpOffsets[i] = caseOffsets.getOrDefault(caseValue, defaultOffset);
                }

                buildTableSwitch(switchBytes, switchStart, defaultOffset, low, high, jumpOffsets);
            }
        } else {
            Map<Integer, Integer> caseOffsets = new HashMap<>();
            for (CaseInfo caseInfo : cases) {
                if (!caseInfo.isDefault) {
                    caseOffsets.put(caseInfo.caseValue, caseInfo.labelOffset);
                }
            }

            List<Integer> values = new ArrayList<>(caseOffsets.keySet());
            Collections.sort(values);

            int[][] matchOffsets = new int[values.size()][2];
            for (int i = 0; i < values.size(); i++) {
                int value = values.get(i);
                matchOffsets[i][0] = value;
                matchOffsets[i][1] = caseOffsets.get(value);
            }

            buildLookupSwitch(switchBytes, switchStart, defaultOffset, matchOffsets);
        }

        // Patch the bytes
        byte[] bytes = new byte[switchBytes.size()];
        for (int i = 0; i < switchBytes.size(); i++) {
            bytes[i] = switchBytes.get(i);
        }
        code.patchBytes(switchStart, bytes);
    }

    /**
     * Determine whether to use tableswitch or lookupswitch.
     */
    private static boolean shouldUseTableSwitch(List<CaseInfo> cases) {
        // Extract non-default case values
        List<Integer> values = new ArrayList<>();
        for (CaseInfo caseInfo : cases) {
            if (!caseInfo.isDefault) {
                values.add(caseInfo.caseValue);
            }
        }

        if (values.isEmpty()) {
            return false; // Only default case
        }

        if (values.size() == 1) {
            return true; // Single case, tableswitch is fine
        }

        int min = Collections.min(values);
        int max = Collections.max(values);
        long range = (long) max - (long) min + 1;

        if (range <= 0 || range > MAX_TABLE_SWITCH_RANGE) {
            return false; // Range too large or wrapped
        }

        double density = (double) values.size() / (double) range;
        return density >= DENSITY_THRESHOLD;
    }

    /**
     * Write a 32-bit integer to the byte list.
     */
    private static void writeInt(List<Byte> bytes, int value) {
        bytes.add((byte) ((value >> 24) & 0xFF));
        bytes.add((byte) ((value >> 16) & 0xFF));
        bytes.add((byte) ((value >> 8) & 0xFF));
        bytes.add((byte) (value & 0xFF));
    }

    /**
     * Case information for integer switch generation.
     */
    private static class CaseInfo {
        Integer caseValue;       // null for default case
        boolean isDefault;
        int labelOffset;         // bytecode offset for this case label (set during generation)
        List<ISwc4jAstStmt> statements;

        CaseInfo(Integer caseValue, List<ISwc4jAstStmt> statements, boolean isDefault) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.isDefault = isDefault;
            this.labelOffset = -1;
        }
    }

}
