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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstSwitchCase;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstSwitchStmt;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.*;

/**
 * Generator for switch statements.
 * <p>
 * Handles both tableswitch (dense cases) and lookupswitch (sparse cases).
 */
public final class SwitchStatementGenerator extends BaseAstProcessor<Swc4jAstSwitchStmt> {
    private static final double DENSITY_THRESHOLD = 0.5;
    private static final int MAX_TABLE_SWITCH_RANGE = 10000;

    public SwitchStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Analyze switch cases and extract case information (integer switches).
     */
    private List<CaseInfo> analyzeCases(Swc4jAstSwitchStmt switchStmt) throws Swc4jByteCodeCompilerException {
        List<CaseInfo> cases = new ArrayList<>();
        Set<Integer> seenValues = new HashSet<>();
        boolean hasDefault = false;

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            if (switchCase.getTest().isEmpty()) {
                // Default case
                if (hasDefault) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate default case in switch statement");
                }
                hasDefault = true;
                cases.add(new CaseInfo(null, switchCase.getCons(), true));
            } else {
                // Regular case
                ISwc4jAstExpr testExpr = switchCase.getTest().get();
                Integer caseValue = extractConstantIntValue(testExpr);
                if (caseValue == null) {
                    throw new Swc4jByteCodeCompilerException(testExpr,
                            "Switch case value must be a constant integer: " + testExpr.getClass().getSimpleName());
                }

                if (seenValues.contains(caseValue)) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate case value: " + caseValue);
                }
                seenValues.add(caseValue);

                cases.add(new CaseInfo(caseValue, switchCase.getCons(), false));
            }
        }

        return cases;
    }

    private List<DoubleCaseInfo> analyzeDoubleCases(Swc4jAstSwitchStmt switchStmt) throws Swc4jByteCodeCompilerException {
        List<DoubleCaseInfo> cases = new ArrayList<>();
        Set<Long> seenValues = new HashSet<>();
        boolean hasDefault = false;

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            if (switchCase.getTest().isEmpty()) {
                if (hasDefault) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate default case in switch statement");
                }
                hasDefault = true;
                cases.add(new DoubleCaseInfo(null, switchCase.getCons(), true));
            } else {
                ISwc4jAstExpr testExpr = switchCase.getTest().get();
                Double caseValue = extractConstantDoubleValue(testExpr);
                if (caseValue == null) {
                    throw new Swc4jByteCodeCompilerException(testExpr,
                            "Switch case value must be a constant double: " + testExpr.getClass().getSimpleName());
                }
                double normalized = caseValue == 0.0d ? 0.0d : caseValue;
                if (Double.isNaN(normalized)) {
                    normalized = Double.NaN;
                }
                long key = Double.doubleToLongBits(normalized);
                if (seenValues.contains(key)) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate case value: " + caseValue);
                }
                seenValues.add(key);
                cases.add(new DoubleCaseInfo(caseValue, switchCase.getCons(), false));
            }
        }

        return cases;
    }

    private List<FloatCaseInfo> analyzeFloatCases(Swc4jAstSwitchStmt switchStmt) throws Swc4jByteCodeCompilerException {
        List<FloatCaseInfo> cases = new ArrayList<>();
        Set<Integer> seenValues = new HashSet<>();
        boolean hasDefault = false;

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            if (switchCase.getTest().isEmpty()) {
                if (hasDefault) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate default case in switch statement");
                }
                hasDefault = true;
                cases.add(new FloatCaseInfo(null, switchCase.getCons(), true));
            } else {
                ISwc4jAstExpr testExpr = switchCase.getTest().get();
                Float caseValue = extractConstantFloatValue(testExpr);
                if (caseValue == null) {
                    throw new Swc4jByteCodeCompilerException(testExpr,
                            "Switch case value must be a constant float: " + testExpr.getClass().getSimpleName());
                }
                float normalized = caseValue == 0.0f ? 0.0f : caseValue;
                if (Float.isNaN(normalized)) {
                    normalized = Float.NaN;
                }
                int key = Float.floatToIntBits(normalized);
                if (seenValues.contains(key)) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate case value: " + caseValue);
                }
                seenValues.add(key);
                cases.add(new FloatCaseInfo(caseValue, switchCase.getCons(), false));
            }
        }

        return cases;
    }

    private List<LongCaseInfo> analyzeLongCases(Swc4jAstSwitchStmt switchStmt) throws Swc4jByteCodeCompilerException {
        List<LongCaseInfo> cases = new ArrayList<>();
        Set<Long> seenValues = new HashSet<>();
        boolean hasDefault = false;

        for (Swc4jAstSwitchCase switchCase : switchStmt.getCases()) {
            if (switchCase.getTest().isEmpty()) {
                if (hasDefault) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate default case in switch statement");
                }
                hasDefault = true;
                cases.add(new LongCaseInfo(null, switchCase.getCons(), true));
            } else {
                ISwc4jAstExpr testExpr = switchCase.getTest().get();
                Long caseValue = extractConstantLongValue(testExpr);
                if (caseValue == null) {
                    throw new Swc4jByteCodeCompilerException(testExpr,
                            "Switch case value must be a constant long: " + testExpr.getClass().getSimpleName());
                }
                if (seenValues.contains(caseValue)) {
                    throw new Swc4jByteCodeCompilerException(switchCase, "Duplicate case value: " + caseValue);
                }
                seenValues.add(caseValue);
                cases.add(new LongCaseInfo(caseValue, switchCase.getCons(), false));
            }
        }

        return cases;
    }

    /**
     * Analyze string cases and build StringCaseInfo list.
     */
    private List<StringCaseInfo> analyzeStringCases(Swc4jAstSwitchStmt switchStmt) {
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
     * Build lookupswitch instruction bytes.
     */
    private void buildLookupSwitch(List<Byte> bytes, int actualPosition, int defaultOffset, int[][] matchOffsets) {
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
    private void buildTableSwitch(List<Byte> bytes, int actualPosition, int defaultOffset, int low, int high, int[] jumpOffsets) {
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
     * Calculate size of lookupswitch instruction.
     */
    private int calculateLookupSwitchSize(int numCases, int startOffset) {
        int padding = (4 - ((startOffset + 1) % 4)) % 4;
        return 1 + padding + 8 + (numCases * 8); // opcode + padding + default + count + (match-offset pairs)
    }

    /**
     * Calculate lookupswitch size for hash codes.
     */
    private int calculateLookupSwitchSizeForHashes(int numCases, int startOffset) {
        int padding = (4 - ((startOffset + 1) % 4)) % 4;
        return 1 + padding + 8 + (numCases * 8);
    }

    /**
     * Calculate the size of the switch instruction in bytes.
     */
    private int calculateSwitchInstructionSize(List<CaseInfo> cases, boolean useTableSwitch, int switchStart) {
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
     * Calculate size of tableswitch instruction for position range.
     */
    private int calculateTableSwitchSize(List<Integer> positions, int startOffset) {
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
     * Calculate tableswitch size for hash codes.
     */
    private int calculateTableSwitchSizeForHashes(List<Integer> hashCodes, int startOffset) {
        if (hashCodes.isEmpty()) {
            return 0;
        }
        int min = hashCodes.get(0);
        int max = hashCodes.get(hashCodes.size() - 1);
        int numEntries = max - min + 1;
        int padding = (4 - ((startOffset + 1) % 4)) % 4;
        return 1 + padding + 12 + (numEntries * 4);
    }

    private void emitDoubleConstant(CodeBuilder code, ClassWriter.ConstantPool cp, double value) {
        int constantIndex = cp.addDouble(value);
        code.ldc2_w(constantIndex);
    }

    private void emitFloatConstant(CodeBuilder code, ClassWriter.ConstantPool cp, float value) {
        int constantIndex = cp.addFloat(value);
        code.ldc(constantIndex);
    }

    private void emitLongConstant(CodeBuilder code, ClassWriter.ConstantPool cp, long value) {
        if (value == 0L || value == 1L) {
            code.lconst(value);
        } else {
            int constantIndex = cp.addLong(value);
            code.ldc2_w(constantIndex);
        }
    }

    private Double extractConstantDoubleValue(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Minus) {
                Double value = extractConstantDoubleValue(unaryExpr.getArg());
                return value != null ? -value : null;
            }
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Plus) {
                return extractConstantDoubleValue(unaryExpr.getArg());
            }
            return null;
        }

        if (expr instanceof Swc4jAstNumber number) {
            return number.getValue();
        }

        return null;
    }

    private Float extractConstantFloatValue(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Minus) {
                Float value = extractConstantFloatValue(unaryExpr.getArg());
                return value != null ? -value : null;
            }
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Plus) {
                return extractConstantFloatValue(unaryExpr.getArg());
            }
            return null;
        }

        if (expr instanceof Swc4jAstNumber number) {
            return (float) number.getValue();
        }

        return null;
    }

    /**
     * Extract constant integer value from expression.
     * Returns null if not a constant integer.
     */
    private Integer extractConstantIntValue(ISwc4jAstExpr expr) {
        // Handle unary minus for negative numbers
        if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Minus) {
                Integer value = extractConstantIntValue(unaryExpr.getArg());
                return value != null ? -value : null;
            }
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Plus) {
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

        if (expr instanceof Swc4jAstBool boolExpr) {
            return boolExpr.isValue() ? 1 : 0;
        }

        // Handle character literals (represented as Swc4jAstStr with single character)
        if (expr instanceof Swc4jAstStr strExpr) {
            String value = strExpr.getValue();
            if (value != null && value.length() == 1) {
                // Single character - convert to int (char value)
                return (int) value.charAt(0);
            }
        }

        // Handle enum member expressions (e.g., Color.RED)
        if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            return extractEnumMemberOrdinal(memberExpr);
        }

        return null;
    }

    private Long extractConstantLongValue(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Minus) {
                Long value = extractConstantLongValue(unaryExpr.getArg());
                return value != null ? -value : null;
            }
            if (unaryExpr.getOp() == Swc4jAstUnaryOp.Plus) {
                return extractConstantLongValue(unaryExpr.getArg());
            }
            return null;
        }

        if (expr instanceof Swc4jAstNumber number) {
            String raw = number.getRaw().orElse("0");
            try {
                if (raw.startsWith("0x") || raw.startsWith("0X")) {
                    return Long.parseLong(raw.substring(2), 16);
                } else if (raw.startsWith("0o") || raw.startsWith("0O")) {
                    return Long.parseLong(raw.substring(2), 8);
                } else if (raw.startsWith("0b") || raw.startsWith("0B")) {
                    return Long.parseLong(raw.substring(2), 2);
                }
            } catch (NumberFormatException ignored) {
            }
            return (long) number.getValue();
        }

        return null;
    }

    /**
     * Extract constant string value from expression.
     * Returns null if not a constant string.
     */
    private String extractConstantStringValue(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstStr strExpr) {
            return strExpr.getValue();
        }
        return null;
    }

    /**
     * Extract ordinal value from enum member expression.
     * E.g., Color.RED -> 0 (if RED is first member of Color enum)
     */
    private Integer extractEnumMemberOrdinal(Swc4jAstMemberExpr memberExpr) {
        // Get enum name from object
        ISwc4jAstExpr obj = memberExpr.getObj();
        if (!(obj instanceof Swc4jAstIdent astIdent)) {
            return null;
        }
        String enumName = astIdent.getSym();

        // Get member name from property - can be either Swc4jAstIdent or Swc4jAstIdentName
        ISwc4jAstMemberProp prop = memberExpr.getProp();
        String memberName = null;
        if (prop instanceof Swc4jAstIdent ident) {
            memberName = ident.getSym();
        } else if (prop instanceof Swc4jAstIdentName identName) {
            memberName = identName.getSym();
        }

        if (memberName == null) {
            return null;
        }

        // Resolve enum name to qualified name using type registry
        String qualifiedEnumName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(enumName);
        if (qualifiedEnumName == null) {
            qualifiedEnumName = enumName; // Use as-is if not found
        }

        // Look up in scoped Java class registry using qualified name
        Integer ordinal = compiler.getMemory().getScopedJavaTypeRegistry().getEnumMemberOrdinal(qualifiedEnumName, memberName);
        return ordinal;
    }

    /**
     * Generate bytecode for a switch statement.
     * Supports integer, string, enum, boxed types, and primitive promotion.
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, cp, switchStmt, null, returnTypeInfo);
    }

    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // 1. Determine discriminant type
        String discriminantType = compiler.getTypeResolver().inferTypeFromExpr(switchStmt.getDiscriminant());

        // 2. Route to appropriate generator based on type

        // String switches use 2-phase hash approach
        if ("Ljava/lang/String;".equals(discriminantType)) {
            generateStringSwitch(code, cp, switchStmt, labelName, returnTypeInfo);
            return;
        }

        // Boxed type switches: unbox then use integer switch
        if ("Ljava/lang/Integer;".equals(discriminantType)) {
            generateBoxedIntegerSwitch(code, cp, switchStmt, "java/lang/Integer", "intValue", "()I", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Byte;".equals(discriminantType)) {
            generateBoxedIntegerSwitch(code, cp, switchStmt, "java/lang/Byte", "byteValue", "()B", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Short;".equals(discriminantType)) {
            generateBoxedIntegerSwitch(code, cp, switchStmt, "java/lang/Short", "shortValue", "()S", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Character;".equals(discriminantType)) {
            generateBoxedIntegerSwitch(code, cp, switchStmt, "java/lang/Character", "charValue", "()C", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Boolean;".equals(discriminantType)) {
            generateBoxedIntegerSwitch(code, cp, switchStmt, "java/lang/Boolean", "booleanValue", "()Z", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Long;".equals(discriminantType)) {
            generateBoxedLongSwitch(code, cp, switchStmt, "java/lang/Long", "longValue", "()J", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Float;".equals(discriminantType)) {
            generateBoxedFloatSwitch(code, cp, switchStmt, "java/lang/Float", "floatValue", "()F", labelName, returnTypeInfo);
            return;
        }
        if ("Ljava/lang/Double;".equals(discriminantType)) {
            generateBoxedDoubleSwitch(code, cp, switchStmt, "java/lang/Double", "doubleValue", "()D", labelName, returnTypeInfo);
            return;
        }

        if ("J".equals(discriminantType)) {
            generateLongSwitch(code, cp, switchStmt, labelName, returnTypeInfo);
            return;
        }
        if ("F".equals(discriminantType)) {
            generateFloatSwitch(code, cp, switchStmt, labelName, returnTypeInfo);
            return;
        }
        if ("D".equals(discriminantType)) {
            generateDoubleSwitch(code, cp, switchStmt, labelName, returnTypeInfo);
            return;
        }

        // Enum switches: call ordinal() to get int, then use integer switch
        // Check if it's an object type (starts with L) but not a known type
        if (discriminantType != null && discriminantType.startsWith("L") && discriminantType.endsWith(";")) {
            // Likely an enum type - call ordinal()
            generateEnumSwitch(code, cp, switchStmt, discriminantType, labelName, returnTypeInfo);
            return;
        }

        // 3. Integer switch (int, byte, short, char, boolean - primitives are auto-promoted to int)
        generateIntegerSwitch(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateBoxedDoubleSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String className,
            String unboxMethod,
            String methodDescriptor,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        int unboxRef = cp.addMethodRef(className, unboxMethod, methodDescriptor);
        code.invokevirtual(unboxRef);
        generateDoubleSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateBoxedFloatSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String className,
            String unboxMethod,
            String methodDescriptor,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        int unboxRef = cp.addMethodRef(className, unboxMethod, methodDescriptor);
        code.invokevirtual(unboxRef);
        generateFloatSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    /**
     * Generate bytecode for a boxed type switch.
     * Unboxes the value using the appropriate *Value() method, then uses integer switch.
     */
    private void generateBoxedIntegerSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String className,
            String unboxMethod,
            String methodDescriptor,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Generate discriminant expression (boxed value on stack)
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);

        // Call appropriate unboxing method
        int unboxRef = cp.addMethodRef(className, unboxMethod, methodDescriptor);
        code.invokevirtual(unboxRef);

        // For byte, short, char, the value is already promoted to int on the stack by JVM
        // Now use integer switch with the unboxed value on stack
        generateIntegerSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateBoxedLongSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String className,
            String unboxMethod,
            String methodDescriptor,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        int unboxRef = cp.addMethodRef(className, unboxMethod, methodDescriptor);
        code.invokevirtual(unboxRef);
        generateLongSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateDoubleSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        generateDoubleSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateDoubleSwitchWithDiscriminantOnStack(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        List<DoubleCaseInfo> cases = analyzeDoubleCases(switchStmt);
        if (cases.isEmpty()) {
            code.pop2();
            return;
        }

        int tempIndex = context.getLocalVariableTable().allocateVariable(
                "$switch$double$" + context.getNextTempId(), "D");
        code.dstore(tempIndex);

        List<CaseJumpInfo<DoubleCaseInfo>> caseJumps = new ArrayList<>();
        for (DoubleCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                continue;
            }
            code.dload(tempIndex);
            emitDoubleConstant(code, cp, caseInfo.caseValue);
            code.dcmpl();

            code.ifne(0);
            int ifneOffsetPos = code.getCurrentOffset() - 2;
            int ifneOpcodePos = code.getCurrentOffset() - 3;

            code.goto_w(0);
            int gotoOffsetPos = code.getCurrentOffset() - 4;
            int gotoOpcodePos = code.getCurrentOffset() - 5;
            caseJumps.add(new CaseJumpInfo<>(caseInfo, gotoOffsetPos, gotoOpcodePos));

            int afterStub = code.getCurrentOffset();
            code.patchShort(ifneOffsetPos, afterStub - ifneOpcodePos);
        }

        code.goto_w(0);
        int defaultGotoOffsetPos = code.getCurrentOffset() - 4;
        int defaultGotoOpcodePos = code.getCurrentOffset() - 5;

        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        for (DoubleCaseInfo caseInfo : cases) {
            caseInfo.labelOffset = code.getCurrentOffset();
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
        }

        int endLabel = code.getCurrentOffset();

        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();

        int defaultOffset = endLabel;
        for (DoubleCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                defaultOffset = caseInfo.labelOffset;
                break;
            }
        }

        for (CaseJumpInfo<DoubleCaseInfo> jumpInfo : caseJumps) {
            int offset = jumpInfo.caseInfo.labelOffset - jumpInfo.gotoOpcodePos;
            code.patchInt(jumpInfo.gotoOffsetPos, offset);
        }

        code.patchInt(defaultGotoOffsetPos, defaultOffset - defaultGotoOpcodePos);
    }

    /**
     * Generate bytecode for an enum switch.
     * Calls ordinal() on the enum value, then uses integer switch.
     */
    private void generateEnumSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String enumType,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Generate discriminant expression
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);

        // Call ordinal() method to get int value
        // ordinal() is defined in java.lang.Enum and returns int
        int ordinalRef = cp.addMethodRef("java/lang/Enum", "ordinal", "()I");
        code.invokevirtual(ordinalRef);

        // Now use integer switch with the ordinal value on stack
        generateIntegerSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateFloatSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        generateFloatSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateFloatSwitchWithDiscriminantOnStack(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        List<FloatCaseInfo> cases = analyzeFloatCases(switchStmt);
        if (cases.isEmpty()) {
            code.pop();
            return;
        }

        int tempIndex = context.getLocalVariableTable().allocateVariable(
                "$switch$float$" + context.getNextTempId(), "F");
        code.fstore(tempIndex);

        List<CaseJumpInfo<FloatCaseInfo>> caseJumps = new ArrayList<>();
        for (FloatCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                continue;
            }
            code.fload(tempIndex);
            emitFloatConstant(code, cp, caseInfo.caseValue);
            code.fcmpl();

            code.ifne(0);
            int ifneOffsetPos = code.getCurrentOffset() - 2;
            int ifneOpcodePos = code.getCurrentOffset() - 3;

            code.goto_w(0);
            int gotoOffsetPos = code.getCurrentOffset() - 4;
            int gotoOpcodePos = code.getCurrentOffset() - 5;
            caseJumps.add(new CaseJumpInfo<>(caseInfo, gotoOffsetPos, gotoOpcodePos));

            int afterStub = code.getCurrentOffset();
            code.patchShort(ifneOffsetPos, afterStub - ifneOpcodePos);
        }

        code.goto_w(0);
        int defaultGotoOffsetPos = code.getCurrentOffset() - 4;
        int defaultGotoOpcodePos = code.getCurrentOffset() - 5;

        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        for (FloatCaseInfo caseInfo : cases) {
            caseInfo.labelOffset = code.getCurrentOffset();
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
        }

        int endLabel = code.getCurrentOffset();

        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();

        int defaultOffset = endLabel;
        for (FloatCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                defaultOffset = caseInfo.labelOffset;
                break;
            }
        }

        for (CaseJumpInfo<FloatCaseInfo> jumpInfo : caseJumps) {
            int offset = jumpInfo.caseInfo.labelOffset - jumpInfo.gotoOpcodePos;
            code.patchInt(jumpInfo.gotoOffsetPos, offset);
        }

        code.patchInt(defaultGotoOffsetPos, defaultOffset - defaultGotoOpcodePos);
    }

    /**
     * Generate bytecode for an integer switch statement.
     */
    private void generateIntegerSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // 1. Analyze cases
        List<CaseInfo> cases = analyzeCases(switchStmt);

        // 2. Evaluate discriminant (push value onto stack)
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);

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
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        // Generate case bodies and record their offsets
        for (CaseInfo caseInfo : cases) {
            caseInfo.labelOffset = code.getCurrentOffset();
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
        }

        // 6. Mark end label
        int endLabel = code.getCurrentOffset();

        // Patch break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
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
     * Helper method for integer switch when discriminant is already on stack.
     * Used by enum and boxed type switches.
     */
    private void generateIntegerSwitchWithDiscriminantOnStack(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Discriminant is already on stack (as int)
        // Proceed with rest of integer switch logic without re-evaluating discriminant

        // Analyze cases
        List<CaseInfo> cases = analyzeCases(switchStmt);

        if (cases.isEmpty()) {
            // Empty switch - discriminant already evaluated and on stack, just pop it
            code.pop();
            return;
        }

        // Set up break label
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        // Determine switch type
        boolean useTableSwitch = shouldUseTableSwitch(cases);

        // Collect unique case values (excluding default)
        List<Integer> caseValues = new ArrayList<>();
        CaseInfo defaultCase = null;

        for (CaseInfo caseInfo : cases) {
            if (caseInfo.caseValue == null) {
                defaultCase = caseInfo;
            } else {
                if (!caseValues.contains(caseInfo.caseValue)) {
                    caseValues.add(caseInfo.caseValue);
                }
            }
        }

        Collections.sort(caseValues);

        // Reserve space for switch instruction
        int switchStart = code.getCurrentOffset();
        int switchSize = useTableSwitch
                ? calculateTableSwitchSize(caseValues, switchStart)
                : calculateLookupSwitchSize(caseValues.size(), switchStart);

        for (int i = 0; i < switchSize; i++) {
            code.emitByte(0);
        }

        // Generate case bodies and record their offsets
        Map<Integer, Integer> valueToOffset = new LinkedHashMap<>();
        int defaultBodyOffset = -1;

        for (CaseInfo caseInfo : cases) {
            int caseOffset = code.getCurrentOffset();

            if (caseInfo.caseValue == null) {
                defaultBodyOffset = caseOffset;
            } else {
                // Only record first occurrence of each value (for empty cases that share bodies)
                if (!valueToOffset.containsKey(caseInfo.caseValue)) {
                    valueToOffset.put(caseInfo.caseValue, caseOffset);
                }
            }

            // Generate case body
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
            // Fall-through is automatic - no goto unless break was generated
        }

        // End of switch
        int endLabel = code.getCurrentOffset();

        // If no default case, default jumps to end
        if (defaultBodyOffset == -1) {
            defaultBodyOffset = endLabel;
        }

        // Patch the switch instruction
        if (useTableSwitch) {
            patchTableSwitch(code, switchStart, defaultBodyOffset, caseValues, valueToOffset);
        } else {
            patchLookupSwitch(code, switchStart, defaultBodyOffset, caseValues, valueToOffset);
        }

        // Patch break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();
    }

    private void generateLongSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        generateLongSwitchWithDiscriminantOnStack(code, cp, switchStmt, labelName, returnTypeInfo);
    }

    private void generateLongSwitchWithDiscriminantOnStack(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        List<LongCaseInfo> cases = analyzeLongCases(switchStmt);
        if (cases.isEmpty()) {
            code.pop2();
            return;
        }

        int tempIndex = context.getLocalVariableTable().allocateVariable(
                "$switch$long$" + context.getNextTempId(), "J");
        code.lstore(tempIndex);

        List<CaseJumpInfo<LongCaseInfo>> caseJumps = new ArrayList<>();
        for (LongCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                continue;
            }
            code.lload(tempIndex);
            emitLongConstant(code, cp, caseInfo.caseValue);
            code.lcmp();

            code.ifne(0);
            int ifneOffsetPos = code.getCurrentOffset() - 2;
            int ifneOpcodePos = code.getCurrentOffset() - 3;

            code.goto_w(0);
            int gotoOffsetPos = code.getCurrentOffset() - 4;
            int gotoOpcodePos = code.getCurrentOffset() - 5;
            caseJumps.add(new CaseJumpInfo<>(caseInfo, gotoOffsetPos, gotoOpcodePos));

            int afterStub = code.getCurrentOffset();
            code.patchShort(ifneOffsetPos, afterStub - ifneOpcodePos);
        }

        code.goto_w(0);
        int defaultGotoOffsetPos = code.getCurrentOffset() - 4;
        int defaultGotoOpcodePos = code.getCurrentOffset() - 5;

        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        for (LongCaseInfo caseInfo : cases) {
            caseInfo.labelOffset = code.getCurrentOffset();
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
        }

        int endLabel = code.getCurrentOffset();

        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();

        int defaultOffset = endLabel;
        for (LongCaseInfo caseInfo : cases) {
            if (caseInfo.isDefault) {
                defaultOffset = caseInfo.labelOffset;
                break;
            }
        }

        for (CaseJumpInfo<LongCaseInfo> jumpInfo : caseJumps) {
            int offset = jumpInfo.caseInfo.labelOffset - jumpInfo.gotoOpcodePos;
            code.patchInt(jumpInfo.gotoOffsetPos, offset);
        }

        code.patchInt(defaultGotoOffsetPos, defaultOffset - defaultGotoOpcodePos);
    }

    /**
     * Phase 1: Generate switch on hashCode() to compute string position.
     * For each hash code, generates if-else chain with equals() checks.
     */
    private void generatePhase1HashSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            int strLocal,
            int posLocal,
            Map<Integer, Set<String>> hashToStrings,
            Map<String, Integer> caseLabelToPosition) {

        int hashCodeRef = cp.addMethodRef("java/lang/String", "hashCode", "()I");
        int equalsRef = cp.addMethodRef("java/lang/String", "equals", "(Ljava/lang/Object;)Z");

        // Generate hashCode() call
        code.aload(strLocal);
        code.invokevirtual(hashCodeRef);

        // Determine whether to use tableswitch or lookupswitch for hash codes
        List<Integer> hashCodes = new ArrayList<>(hashToStrings.keySet());
        Collections.sort(hashCodes);

        boolean useTableSwitch = shouldUseTableSwitchForHashCodes(hashCodes);

        // Reserve space for the switch instruction
        int switchStart = code.getCurrentOffset();
        int switchInstrSize = useTableSwitch
                ? calculateTableSwitchSizeForHashes(hashCodes, switchStart)
                : calculateLookupSwitchSizeForHashes(hashCodes.size(), switchStart);

        for (int i = 0; i < switchInstrSize; i++) {
            code.emitByte(0);
        }

        // Generate hash case bodies and record their offsets
        Map<Integer, Integer> hashToOffset = new LinkedHashMap<>();
        List<Integer> breakPatchPositions = new ArrayList<>();

        for (int hashCode : hashCodes) {
            int caseOffset = code.getCurrentOffset();
            hashToOffset.put(hashCode, caseOffset);

            Set<String> stringsWithHash = hashToStrings.get(hashCode);

            // Generate if-else chain for strings with this hash
            for (String str : stringsWithHash) {
                // if (str.equals("label")) { pos = position; break; }
                code.aload(strLocal);
                code.ldc(cp.addString(str));
                code.invokevirtual(equalsRef);

                int ifeqPos = code.getCurrentOffset();
                code.ifeq(0); // if false, skip to next comparison - will be patched

                // Assign position
                code.iconst(caseLabelToPosition.get(str));
                code.istore(posLocal);

                // Break out of switch1
                int gotoBreakPos = code.getCurrentOffset();
                code.gotoLabel(0); // will be patched to end of switch1
                breakPatchPositions.add(gotoBreakPos);

                // Patch ifeq to jump here (to next comparison)
                int nextComparisonOffset = code.getCurrentOffset();
                code.patchShort(ifeqPos + 1, nextComparisonOffset - ifeqPos);
            }

            // End of this hash case - break (fall-through to next case or end)
            int gotoEndPos = code.getCurrentOffset();
            code.gotoLabel(0);
            breakPatchPositions.add(gotoEndPos);
        }

        // Default case (no match) - just falls through to phase 2
        int defaultOffset = code.getCurrentOffset();

        // Patch all breaks to here
        for (int patchPos : breakPatchPositions) {
            code.patchShort(patchPos + 1, defaultOffset - patchPos);
        }

        // Patch the switch instruction
        if (useTableSwitch) {
            patchTableSwitchForHashes(code, switchStart, defaultOffset, hashCodes, hashToOffset);
        } else {
            patchLookupSwitchForHashes(code, switchStart, defaultOffset, hashCodes, hashToOffset);
        }
    }

    /**
     * Phase 2: Generate switch on position with original case bodies.
     * This preserves the exact structure of the original switch for proper fall-through.
     */
    private void generatePhase2PositionSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            int posLocal,
            List<StringCaseInfo> stringCases,
            int defaultCasePosition,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Set up break label for the switch
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        context.pushBreakLabel(breakLabel);

        // Load position variable
        code.iload(posLocal);

        // Build position-based switch
        // Positions are 0, 1, 2, ... for each case in source order
        // Default case doesn't have a position in the switch - it maps to -1 or any unmatched value

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < stringCases.size(); i++) {
            if (stringCases.get(i).caseValue != null) {
                positions.add(i);
            }
        }

        // Reserve space for switch2 instruction
        int switch2Start = code.getCurrentOffset();
        boolean useTableSwitch = !positions.isEmpty() && shouldUseTableSwitchForPositions(positions);
        int switch2Size = useTableSwitch
                ? calculateTableSwitchSize(positions, switch2Start)
                : calculateLookupSwitchSize(positions.size(), switch2Start);

        for (int i = 0; i < switch2Size; i++) {
            code.emitByte(0);
        }

        // Generate case bodies and record their offsets
        Map<Integer, Integer> positionToOffset = new LinkedHashMap<>();
        int defaultBodyOffset = -1;

        for (int i = 0; i < stringCases.size(); i++) {
            StringCaseInfo caseInfo = stringCases.get(i);
            int caseOffset = code.getCurrentOffset();

            if (caseInfo.caseValue == null) {
                // Default case
                defaultBodyOffset = caseOffset;
            } else {
                positionToOffset.put(i, caseOffset);
            }

            // Generate case body
            for (ISwc4jAstStmt stmt : caseInfo.statements) {
                compiler.getStatementGenerator().generate(code, cp, stmt, returnTypeInfo);
            }
            // Fall-through is automatic
        }

        // End of switch
        int endLabel = code.getCurrentOffset();

        // If no default case, default jumps to end
        if (defaultBodyOffset == -1) {
            defaultBodyOffset = endLabel;
        }

        // Patch the switch2 instruction
        if (useTableSwitch && !positions.isEmpty()) {
            patchTableSwitch(code, switch2Start, defaultBodyOffset, positions, positionToOffset);
        } else {
            patchLookupSwitch(code, switch2Start, defaultBodyOffset,
                    new ArrayList<>(positionToOffset.keySet()), positionToOffset);
        }

        // Patch break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        context.popBreakLabel();
    }

    /**
     * Generate bytecode for a string switch statement.
     * Uses JDK-style 2-phase approach:
     * Phase 1: Switch on hashCode() to compute string position
     * Phase 2: Switch on computed position with original case bodies
     * This properly handles hash collisions and fall-through semantics.
     */
    private void generateStringSwitch(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstSwitchStmt switchStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // 1. Analyze string cases
        List<StringCaseInfo> stringCases = analyzeStringCases(switchStmt);

        if (stringCases.isEmpty()) {
            // Empty switch - just evaluate discriminant for side effects
            compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
            code.pop();
            return;
        }

        // 2. Build data structures for 2-phase compilation
        // Map from string case label to its position (0-based index of non-default cases)
        Map<String, Integer> caseLabelToPosition = new LinkedHashMap<>();
        // Map from hash code to set of strings with that hash
        Map<Integer, Set<String>> hashToStrings = new LinkedHashMap<>();
        // Track default case position
        int defaultCasePosition = -1;
        StringCaseInfo defaultCaseInfo = null;

        int position = 0;
        for (StringCaseInfo caseInfo : stringCases) {
            if (caseInfo.caseValue == null) {
                // Default case
                defaultCaseInfo = caseInfo;
                defaultCasePosition = position;
            } else {
                // Regular string case
                caseLabelToPosition.put(caseInfo.caseValue, position);
                int hashCode = caseInfo.caseValue.hashCode();
                hashToStrings.computeIfAbsent(hashCode, k -> new LinkedHashSet<>()).add(caseInfo.caseValue);
            }
            position++;
        }

        // 3. Store discriminant in local variable
        int strLocal = context.getLocalVariableTable().allocateVariable("$switch$str", "Ljava/lang/String;");
        compiler.getExpressionGenerator().generate(code, cp, switchStmt.getDiscriminant(), null);
        code.astore(strLocal);

        // 4. Create temp variable for position, initialized to -1
        int posLocal = context.getLocalVariableTable().allocateVariable("$switch$pos", "I");
        code.iconst(-1);
        code.istore(posLocal);

        // 5. Phase 1: Switch on hashCode() to determine position
        if (!hashToStrings.isEmpty()) {
            generatePhase1HashSwitch(code, cp, strLocal, posLocal, hashToStrings, caseLabelToPosition);
        }

        // 6. Phase 2: Switch on position with original case bodies
        generatePhase2PositionSwitch(code, cp, posLocal, stringCases, defaultCasePosition,
                labelName, returnTypeInfo);
    }

    /**
     * Patch lookupswitch instruction using byte list approach.
     */
    private void patchLookupSwitch(
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
     * Patch lookupswitch instruction for hash codes.
     */
    private void patchLookupSwitchForHashes(
            CodeBuilder code,
            int switchStart,
            int defaultOffset,
            List<Integer> hashCodes,
            Map<Integer, Integer> hashToOffset) {

        List<Byte> switchBytes = new ArrayList<>();

        int[][] matchOffsets = new int[hashCodes.size()][2];
        for (int i = 0; i < hashCodes.size(); i++) {
            int hashCode = hashCodes.get(i);
            matchOffsets[i][0] = hashCode;
            matchOffsets[i][1] = hashToOffset.get(hashCode);
        }

        buildLookupSwitch(switchBytes, switchStart, defaultOffset, matchOffsets);

        byte[] bytes = new byte[switchBytes.size()];
        for (int i = 0; i < switchBytes.size(); i++) {
            bytes[i] = switchBytes.get(i);
        }
        code.patchBytes(switchStart, bytes);
    }

    /**
     * Patch the switch instruction with correct case offsets.
     */
    private void patchSwitchInstructionInPlace(
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
     * Patch tableswitch instruction for position-based switch using byte list approach.
     */
    private void patchTableSwitch(
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
     * Patch tableswitch instruction for hash codes.
     */
    private void patchTableSwitchForHashes(
            CodeBuilder code,
            int switchStart,
            int defaultOffset,
            List<Integer> hashCodes,
            Map<Integer, Integer> hashToOffset) {

        List<Byte> switchBytes = new ArrayList<>();

        int min = hashCodes.get(0);
        int max = hashCodes.get(hashCodes.size() - 1);

        int[] jumpOffsets = new int[max - min + 1];
        for (int i = 0; i < jumpOffsets.length; i++) {
            int hashCode = min + i;
            jumpOffsets[i] = hashToOffset.getOrDefault(hashCode, defaultOffset);
        }

        buildTableSwitch(switchBytes, switchStart, defaultOffset, min, max, jumpOffsets);

        byte[] bytes = new byte[switchBytes.size()];
        for (int i = 0; i < switchBytes.size(); i++) {
            bytes[i] = switchBytes.get(i);
        }
        code.patchBytes(switchStart, bytes);
    }

    /**
     * Determine whether to use tableswitch or lookupswitch.
     */
    private boolean shouldUseTableSwitch(List<CaseInfo> cases) {
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
     * Determine whether to use tableswitch for hash codes.
     */
    private boolean shouldUseTableSwitchForHashCodes(List<Integer> hashCodes) {
        if (hashCodes.isEmpty() || hashCodes.size() == 1) {
            return hashCodes.size() == 1;
        }

        int min = hashCodes.get(0);
        int max = hashCodes.get(hashCodes.size() - 1);
        long range = (long) max - (long) min + 1;

        if (range <= 0 || range > MAX_TABLE_SWITCH_RANGE) {
            return false;
        }

        double density = (double) hashCodes.size() / (double) range;
        return density >= DENSITY_THRESHOLD;
    }

    /**
     * Check if table switch should be used for position-based switch.
     */
    private boolean shouldUseTableSwitchForPositions(List<Integer> positions) {
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
     * Write a 32-bit integer to the byte list.
     */
    private void writeInt(List<Byte> bytes, int value) {
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

    private record CaseJumpInfo<T>(T caseInfo, int gotoOffsetPos, int gotoOpcodePos) {
    }

    private static class DoubleCaseInfo {
        Double caseValue;
        boolean isDefault;
        int labelOffset;
        List<ISwc4jAstStmt> statements;

        DoubleCaseInfo(Double caseValue, List<ISwc4jAstStmt> statements, boolean isDefault) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.isDefault = isDefault;
            this.labelOffset = -1;
        }
    }

    private static class FloatCaseInfo {
        Float caseValue;
        boolean isDefault;
        int labelOffset;
        List<ISwc4jAstStmt> statements;

        FloatCaseInfo(Float caseValue, List<ISwc4jAstStmt> statements, boolean isDefault) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.isDefault = isDefault;
            this.labelOffset = -1;
        }
    }

    private static class LongCaseInfo {
        Long caseValue;
        boolean isDefault;
        int labelOffset;
        List<ISwc4jAstStmt> statements;

        LongCaseInfo(Long caseValue, List<ISwc4jAstStmt> statements, boolean isDefault) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.isDefault = isDefault;
            this.labelOffset = -1;
        }
    }

    /**
     * Information about a string case.
     */
    private static class StringCaseInfo {
        int bodyOffset;          // offset of the case body
        String caseValue;        // null for default case
        int gotoPosition;        // position of the conditional jump instruction
        List<ISwc4jAstStmt> statements;

        StringCaseInfo(String caseValue, List<ISwc4jAstStmt> statements) {
            this.caseValue = caseValue;
            this.statements = statements;
            this.gotoPosition = -1;
            this.bodyOffset = -1;
        }
    }

}
