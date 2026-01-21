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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAssignOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class AssignExpressionGenerator {
    private AssignExpressionGenerator() {
    }

    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstAssignExpr assignExpr) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        // Handle assignments like arr[1] = value or arr.length = 0
        var left = assignExpr.getLeft();
        if (left instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = TypeResolver.inferTypeFromExpr(compiler, memberExpr.getObj());

            if (objType != null && objType.startsWith("[")) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value - array element assignment
                    ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [array]
                    ExpressionGenerator.generate(compiler, code, cp, computedProp.getExpr(), null); // Stack: [array, index]

                    // Convert index to int if needed
                    String indexType = TypeResolver.inferTypeFromExpr(compiler, computedProp.getExpr());
                    if (indexType != null && !"I".equals(indexType)) {
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
                    }

                    // Generate the value to store
                    String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                    ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null); // Stack: [array, index, value]

                    // Unbox if needed
                    TypeConversionUtils.unboxWrapperType(code, cp, valueType);

                    // Convert to target element type if needed
                    String elemType = objType.substring(1); // Remove leading "["
                    String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);
                    TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, elemType);

                    // Duplicate value and place it below array and index so it's left after store
                    // Stack: [array, index, value] -> [value, array, index, value]
                    if ("D".equals(elemType) || "J".equals(elemType)) {
                        code.dup2_x2(); // For wide types (double, long)
                    } else {
                        code.dup_x2(); // For single-slot types
                    }

                    // Use appropriate array store instruction
                    // Stack: [value, array, index, value] -> [value] after store
                    switch (elemType) {
                        case "Z", "B" -> code.bastore(); // boolean and byte
                        case "C" -> code.castore(); // char
                        case "S" -> code.sastore(); // short
                        case "I" -> code.iastore(); // int
                        case "J" -> code.lastore(); // long
                        case "F" -> code.fastore(); // float
                        case "D" -> code.dastore(); // double
                        default -> code.aastore(); // reference types
                    }
                    // The duplicated value is now on the stack as the assignment result
                    return;
                }

                // Check if it's arr.length = newLength - NOT SUPPORTED for Java arrays
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        throw new Swc4jByteCodeCompilerException("Cannot set length on Java array - array size is fixed");
                    }
                }
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // Check if it's arr[index] = value
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value -> arr.set(index, value)
                    ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [ArrayList]
                    ExpressionGenerator.generate(compiler, code, cp, computedProp.getExpr(), null); // Stack: [ArrayList, index]
                    ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null); // Stack: [ArrayList, index, value]

                    // Box value if needed
                    String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                    if (TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        // wrapperType is already in the form "Ljava/lang/Integer;" so use it directly
                        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
                        int valueOfRef = cp.addMethodRef(className, "valueOf", "(" + valueType + ")" + wrapperType);
                        code.invokestatic(valueOfRef); // Stack: [ArrayList, index, boxedValue]
                    }

                    // Call ArrayList.set(int, Object)
                    int setMethod = cp.addMethodRef("java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
                    code.invokevirtual(setMethod); // Stack: [oldValue] - the return value of set() is the previous value
                    // Leave the value on stack for expression statements to pop
                    return;
                }

                // Check if it's arr.length = newLength
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        // arr.length = newLength
                        // Special case: arr.length = 0 -> arr.clear()
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number && number.getValue() == 0.0) {
                            ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [ArrayList]
                            int clearMethod = cp.addMethodRef("java/util/ArrayList", "clear", "()V");
                            code.invokevirtual(clearMethod); // Stack: []
                            // Assignment expression should return the assigned value (0 in this case)
                            code.iconst(0); // Stack: [0]
                            return;
                        }

                        // General case for constant new length (like arr.length = 2)
                        // Use ArrayList.subList(newLength, size()).clear() to remove excess elements
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number) {
                            int newLength = (int) number.getValue();

                            // Call arr.subList(newLength, arr.size()).clear()
                            ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [ArrayList]
                            code.dup(); // Stack: [ArrayList, ArrayList] - keep one for potential use
                            code.iconst(newLength); // Stack: [ArrayList, ArrayList, newLength]

                            // Get arr.size() - need to load ArrayList again
                            ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [ArrayList, ArrayList, newLength, ArrayList]
                            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                            code.invokevirtual(sizeMethod); // Stack: [ArrayList, ArrayList, newLength, size]

                            // Call subList(newLength, size) on the second ArrayList
                            int subListMethod = cp.addMethodRef("java/util/ArrayList", "subList", "(II)Ljava/util/List;");
                            code.invokevirtual(subListMethod); // Stack: [ArrayList, List]

                            // Call clear() on the List
                            int clearMethod2 = cp.addInterfaceMethodRef("java/util/List", "clear", "()V");
                            code.invokeinterface(clearMethod2, 1); // Stack: [ArrayList]

                            // Assignment expression returns the assigned value (newLength), not the ArrayList
                            code.pop(); // Pop the ArrayList we kept, Stack: []
                            code.iconst(newLength); // Stack: [newLength]
                            return;
                        }

                        // For non-constant expressions, we need more complex handling
                        throw new Swc4jByteCodeCompilerException("Setting array length to non-constant values not yet supported");
                    }
                }
            } else if ("Ljava/util/LinkedHashMap;".equals(objType)) {
                // LinkedHashMap operations (object literal property assignment)
                // Check if it's obj[key] = value (computed property)
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // obj[key] = value -> map.put(key, value)
                    ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    ExpressionGenerator.generate(compiler, code, cp, computedProp.getExpr(), null); // Stack: [LinkedHashMap, key]

                    // Box primitive keys if needed
                    String keyType = TypeResolver.inferTypeFromExpr(compiler, computedProp.getExpr());
                    if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, keyType, wrapperType);
                    }

                    ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null); // Stack: [LinkedHashMap, key, value]

                    // Box primitive values if needed
                    String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                    if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, valueType, wrapperType);
                    }

                    // Call LinkedHashMap.put(Object, Object)
                    int putMethod = cp.addMethodRef("java/util/LinkedHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
                    code.invokevirtual(putMethod); // Stack: [oldValue] - the return value is the previous value (or null)
                    // Leave the value on stack for expression statements to pop
                    return;
                }

                // Check if it's obj.prop = value (named property)
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    // obj.prop = value -> map.put("prop", value)
                    ExpressionGenerator.generate(compiler, code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    int keyIndex = cp.addString(propName);
                    code.ldc(keyIndex); // Stack: [LinkedHashMap, "prop"]

                    ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null); // Stack: [LinkedHashMap, "prop", value]

                    // Box primitive values if needed
                    String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                    if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, valueType, wrapperType);
                    }

                    // Call LinkedHashMap.put(Object, Object)
                    int putMethod = cp.addMethodRef("java/util/LinkedHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
                    code.invokevirtual(putMethod); // Stack: [oldValue]
                    // Leave the value on stack for expression statements to pop
                    return;
                }
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Variable assignment: x = value or compound like x += value
            String varName = bindingIdent.getId().getSym();
            LocalVariable var = context.getLocalVariableTable().getVariable(varName);

            if (var == null) {
                throw new Swc4jByteCodeCompilerException("Undefined variable: " + varName);
            }

            String varType = var.type();
            Swc4jAstAssignOp op = assignExpr.getOp();

            if (op == Swc4jAstAssignOp.Assign) {
                // Simple assignment: x = value
                ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                if (valueType != null && !valueType.equals(varType)) {
                    TypeConversionUtils.unboxWrapperType(code, cp, valueType);
                    String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);
                    if (valuePrimitive != null && !valuePrimitive.equals(varType)) {
                        TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, varType);
                    }
                }
            } else {
                // Compound assignment: x += value, x -= value, etc.
                // Load current value of variable
                switch (varType) {
                    case "I", "Z", "B", "C", "S" -> code.iload(var.index());
                    case "J" -> code.lload(var.index());
                    case "F" -> code.fload(var.index());
                    case "D" -> code.dload(var.index());
                    default -> code.aload(var.index());
                }

                // Generate the right-hand side expression
                ExpressionGenerator.generate(compiler, code, cp, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = TypeResolver.inferTypeFromExpr(compiler, assignExpr.getRight());
                if (valueType != null && !valueType.equals(varType)) {
                    TypeConversionUtils.unboxWrapperType(code, cp, valueType);
                    String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);
                    if (valuePrimitive != null && !valuePrimitive.equals(varType)) {
                        TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, varType);
                    }
                }

                // Perform the operation based on the compound operator
                switch (op) {
                    case AddAssign -> {
                        switch (varType) {
                            case "I" -> code.iadd();
                            case "J" -> code.ladd();
                            case "F" -> code.fadd();
                            case "D" -> code.dadd();
                        }
                    }
                    case SubAssign -> {
                        switch (varType) {
                            case "I" -> code.isub();
                            case "J" -> code.lsub();
                            case "F" -> code.fsub();
                            case "D" -> code.dsub();
                        }
                    }
                    case MulAssign -> {
                        switch (varType) {
                            case "I" -> code.imul();
                            case "J" -> code.lmul();
                            case "F" -> code.fmul();
                            case "D" -> code.dmul();
                        }
                    }
                    case DivAssign -> {
                        switch (varType) {
                            case "I" -> code.idiv();
                            case "J" -> code.ldiv();
                            case "F" -> code.fdiv();
                            case "D" -> code.ddiv();
                        }
                    }
                    case ModAssign -> {
                        switch (varType) {
                            case "I" -> code.irem();
                            case "J" -> code.lrem();
                            case "F" -> code.frem();
                            case "D" -> code.drem();
                        }
                    }
                    case BitAndAssign -> {
                        switch (varType) {
                            case "I" -> code.iand();
                            case "J" -> code.land();
                        }
                    }
                    case BitOrAssign -> {
                        switch (varType) {
                            case "I" -> code.ior();
                            case "J" -> code.lor();
                        }
                    }
                    case BitXorAssign -> {
                        switch (varType) {
                            case "I" -> code.ixor();
                            case "J" -> code.lxor();
                        }
                    }
                    case LShiftAssign -> {
                        switch (varType) {
                            case "I" -> code.ishl();
                            case "J" -> code.lshl();
                        }
                    }
                    case RShiftAssign -> {
                        switch (varType) {
                            case "I" -> code.ishr();
                            case "J" -> code.lshr();
                        }
                    }
                    case ZeroFillRShiftAssign -> {
                        switch (varType) {
                            case "I" -> code.iushr();
                            case "J" -> code.lushr();
                        }
                    }
                    default ->
                            throw new Swc4jByteCodeCompilerException("Compound assignment not yet supported: " + op.getName());
                }
            }

            // Duplicate the value on the stack before storing (assignment returns the value)
            if ("D".equals(varType) || "J".equals(varType)) {
                code.dup2(); // For wide types (double, long)
            } else {
                code.dup(); // For single-slot types
            }

            // Store into local variable
            switch (varType) {
                case "I", "Z", "B", "C", "S" -> code.istore(var.index());
                case "J" -> code.lstore(var.index());
                case "F" -> code.fstore(var.index());
                case "D" -> code.dstore(var.index());
                default -> code.astore(var.index()); // Reference types
            }
            // The duplicated value is now on the stack as the assignment result
            return;
        }
        throw new Swc4jByteCodeCompilerException("Assignment expression not yet supported: " + left);
    }
}
