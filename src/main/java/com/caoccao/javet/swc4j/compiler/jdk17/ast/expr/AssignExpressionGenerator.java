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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class AssignExpressionGenerator {
    private AssignExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstAssignExpr assignExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle assignments like arr[1] = value or arr.length = 0
        var left = assignExpr.getLeft();
        if (left instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

            if (objType != null && objType.startsWith("[")) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value - array element assignment
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                    ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [array, index]

                    // Convert index to int if needed
                    String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                    if (indexType != null && !"I".equals(indexType)) {
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
                    }

                    // Generate the value to store
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
                    ExpressionGenerator.generate(code, cp, assignExpr.getRight(), null, context, options); // Stack: [array, index, value]

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
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                    ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]
                    ExpressionGenerator.generate(code, cp, assignExpr.getRight(), null, context, options); // Stack: [ArrayList, index, value]

                    // Box value if needed
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
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
                            ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
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
                            ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                            code.dup(); // Stack: [ArrayList, ArrayList] - keep one for potential use
                            code.iconst(newLength); // Stack: [ArrayList, ArrayList, newLength]

                            // Get arr.size() - need to load ArrayList again
                            ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList, ArrayList, newLength, ArrayList]
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
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [LinkedHashMap]
                    ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [LinkedHashMap, key]

                    // Box primitive keys if needed
                    String keyType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                    if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, keyType, wrapperType);
                    }

                    ExpressionGenerator.generate(code, cp, assignExpr.getRight(), null, context, options); // Stack: [LinkedHashMap, key, value]

                    // Box primitive values if needed
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
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
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [LinkedHashMap]
                    int keyIndex = cp.addString(propName);
                    code.ldc(keyIndex); // Stack: [LinkedHashMap, "prop"]

                    ExpressionGenerator.generate(code, cp, assignExpr.getRight(), null, context, options); // Stack: [LinkedHashMap, "prop", value]

                    // Box primitive values if needed
                    String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
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
            // Simple variable assignment: x = value
            String varName = bindingIdent.getId().getSym();
            LocalVariable var = context.getLocalVariableTable().getVariable(varName);

            if (var == null) {
                throw new Swc4jByteCodeCompilerException("Undefined variable: " + varName);
            }

            // Generate the right-hand side expression
            ExpressionGenerator.generate(code, cp, assignExpr.getRight(), null, context, options);

            // Convert to the variable's type if needed
            String valueType = TypeResolver.inferTypeFromExpr(assignExpr.getRight(), context, options);
            String varType = var.type();

            if (valueType != null && !valueType.equals(varType)) {
                // Unbox if needed
                TypeConversionUtils.unboxWrapperType(code, cp, valueType);
                String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);

                // Convert between primitive types if needed
                if (valuePrimitive != null && !valuePrimitive.equals(varType)) {
                    TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, varType);
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
