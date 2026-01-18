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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr;

import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUpdateOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUpdateExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class UpdateExpressionGenerator {
    private UpdateExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUpdateExpr updateExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Phase 1: Only support local variables
        if (!(updateExpr.getArg() instanceof Swc4jAstIdent ident)) {
            throw new Swc4jByteCodeCompilerException(
                    "Update expressions currently only support local variables (member access and array access not yet implemented)");
        }

        String varName = ident.getSym();
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

        if (localVar == null) {
            throw new Swc4jByteCodeCompilerException("Variable '" + varName + "' not found in local scope");
        }

        String varType = localVar.type();
        int varIndex = localVar.index();
        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        // Validate type - only numeric types can be incremented/decremented
        switch (varType) {
            case "I", "B", "S", "C", "J", "F", "D",
                    "Ljava/lang/Integer;", "Ljava/lang/Long;", "Ljava/lang/Float;",
                    "Ljava/lang/Double;", "Ljava/lang/Byte;", "Ljava/lang/Short;" -> {
                // Valid numeric type
            }
            case "Z", "Ljava/lang/Boolean;" -> throw new Swc4jByteCodeCompilerException(
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to boolean type");
            case "Ljava/lang/String;" -> throw new Swc4jByteCodeCompilerException(
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to string type");
            default -> throw new Swc4jByteCodeCompilerException(
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to type: " + varType);
        }

        // Optimization: Use iinc instruction for int local variables
        if (varType.equals("I")) {
            generateIntUpdate(code, varIndex, isIncrement, isPrefix);
        } else {
            // General case for other numeric types
            generateGeneralUpdate(code, cp, varType, varIndex, isIncrement, isPrefix);
        }
    }

    /**
     * Optimized code generation for int variables using iinc instruction.
     * Always leaves a value on the stack (either old value for postfix or new value for prefix).
     */
    private static void generateIntUpdate(
            CodeBuilder code,
            int varIndex,
            boolean isIncrement,
            boolean isPrefix) {

        int delta = isIncrement ? 1 : -1;

        if (isPrefix) {
            // Prefix (++i): increment first, then load
            code.iinc(varIndex, delta);
            code.iload(varIndex);
        } else {
            // Postfix (i++): load old value, then increment
            code.iload(varIndex);
            code.iinc(varIndex, delta);
        }
    }

    /**
     * General code generation for non-int numeric types (long, float, double, wrappers).
     * Always leaves a value on the stack (either old value for postfix or new value for prefix).
     */
    private static void generateGeneralUpdate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String varType,
            int varIndex,
            boolean isIncrement,
            boolean isPrefix) {

        // Determine if this is a wrapper type
        boolean isWrapper = varType.startsWith("L");
        String primitiveType = isWrapper ? unwrapType(varType) : varType;

        if (isWrapper) {
            // Load wrapper, unbox to primitive
            code.aload(varIndex);
            generateUnbox(code, cp, varType, primitiveType);
        } else {
            // Load primitive directly
            loadPrimitive(code, primitiveType, varIndex);
        }

        // For postfix: duplicate old value BEFORE modification
        if (!isPrefix) {
            duplicatePrimitive(code, primitiveType);
        }

        // Load constant 1 and add/subtract
        loadOne(code, primitiveType);
        if (isIncrement) {
            addPrimitive(code, primitiveType);
        } else {
            subtractPrimitive(code, primitiveType);
        }

        if (isWrapper) {
            // Box the new value
            generateBox(code, cp, primitiveType, varType);
            // For prefix: duplicate boxed value BEFORE storing
            if (isPrefix) {
                code.dup(); // Duplicate the wrapper reference
            }
            code.astore(varIndex);
            // For postfix: box the old primitive value left on stack
            if (!isPrefix) {
                generateBox(code, cp, primitiveType, varType);
            }
        } else {
            // For prefix: duplicate new primitive value AFTER modification
            if (isPrefix) {
                duplicatePrimitive(code, primitiveType);
            }
            // Store primitive directly
            storePrimitive(code, primitiveType, varIndex);
        }
    }

    private static String unwrapType(String wrapperType) {
        return switch (wrapperType) {
            case "Ljava/lang/Integer;" -> "I";
            case "Ljava/lang/Long;" -> "J";
            case "Ljava/lang/Float;" -> "F";
            case "Ljava/lang/Double;" -> "D";
            case "Ljava/lang/Byte;" -> "B";
            case "Ljava/lang/Short;" -> "S";
            default -> throw new IllegalArgumentException("Unknown wrapper type: " + wrapperType);
        };
    }

    private static void generateUnbox(CodeBuilder code, ClassWriter.ConstantPool cp, String wrapperType, String primitiveType) {
        String methodName = switch (primitiveType) {
            case "I" -> "intValue";
            case "J" -> "longValue";
            case "F" -> "floatValue";
            case "D" -> "doubleValue";
            case "B" -> "byteValue";
            case "S" -> "shortValue";
            default -> throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
        };

        String descriptor = "()" + primitiveType;
        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
        int methodRef = cp.addMethodRef(className, methodName, descriptor);
        code.invokevirtual(methodRef);
    }

    private static void generateBox(CodeBuilder code, ClassWriter.ConstantPool cp, String primitiveType, String wrapperType) {
        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
        String descriptor = "(" + primitiveType + ")" + wrapperType;
        int methodRef = cp.addMethodRef(className, "valueOf", descriptor);
        code.invokestatic(methodRef);
    }

    private static void loadPrimitive(CodeBuilder code, String primitiveType, int varIndex) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.iload(varIndex);
            case "J" -> code.lload(varIndex);
            case "F" -> code.fload(varIndex);
            case "D" -> code.dload(varIndex);
        }
    }

    private static void storePrimitive(CodeBuilder code, String primitiveType, int varIndex) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.istore(varIndex);
            case "J" -> code.lstore(varIndex);
            case "F" -> code.fstore(varIndex);
            case "D" -> code.dstore(varIndex);
        }
    }

    private static void loadOne(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.iconst(1);
            case "J" -> code.lconst(1L);
            case "F" -> code.fconst(1.0f);
            case "D" -> code.dconst(1.0);
        }
    }

    private static void addPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.iadd();
            case "J" -> code.ladd();
            case "F" -> code.fadd();
            case "D" -> code.dadd();
        }
    }

    private static void subtractPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.isub();
            case "J" -> code.lsub();
            case "F" -> code.fsub();
            case "D" -> code.dsub();
        }
    }

    private static void duplicatePrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "J", "D" -> code.dup2(); // long and double take 2 stack slots
            default -> code.dup();         // all others take 1 slot
        }
    }
}
