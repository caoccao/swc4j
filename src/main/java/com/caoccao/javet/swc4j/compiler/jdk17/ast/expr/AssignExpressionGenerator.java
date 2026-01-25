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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

public final class AssignExpressionGenerator extends BaseAstProcessor<Swc4jAstAssignExpr> {
    public AssignExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Extract property name from ISwc4jAstPropName.
     */
    private String extractPropertyName(ISwc4jAstPropName propName) throws Swc4jByteCodeCompilerException {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        } else {
            throw new Swc4jByteCodeCompilerException(propName,
                    "Unsupported property name type: " + propName.getClass().getName());
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstAssignExpr assignExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        // Handle assignments like arr[1] = value or arr.length = 0
        var left = assignExpr.getLeft();
        if (left instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

            if (objType != null && objType.startsWith("[")) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value - array element assignment
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [array]
                    compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [array, index]

                    // Convert index to int if needed
                    String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if (indexType != null && !"I".equals(indexType)) {
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
                    }

                    // Generate the value to store
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null); // Stack: [array, index, value]

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
                        throw new Swc4jByteCodeCompilerException(assignExpr, "Cannot set length on Java array - array size is fixed");
                    }
                }
            } else if ("Ljava/util/ArrayList;".equals(objType) || "Ljava/util/List;".equals(objType)) {
                // Check if it's arr[index] = value
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value -> arr.set(index, value)
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                    compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [ArrayList/List, index]

                    // Convert index to int if it's a String (for-in returns string indices in JS semantics)
                    String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if ("Ljava/lang/String;".equals(indexType)) {
                        // String index -> Integer.parseInt(index)
                        int parseIntMethod = cp.addMethodRef("java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                        code.invokestatic(parseIntMethod); // Stack: [ArrayList/List, int]
                    }

                    compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null); // Stack: [ArrayList/List, index, value]

                    // Box value if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    if (TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        // wrapperType is already in the form "Ljava/lang/Integer;" so use it directly
                        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
                        int valueOfRef = cp.addMethodRef(className, "valueOf", "(" + valueType + ")" + wrapperType);
                        code.invokestatic(valueOfRef); // Stack: [ArrayList/List, index, boxedValue]
                    }

                    // Call List.set(int, Object) via interface method
                    int setMethod = cp.addInterfaceMethodRef("java/util/List", "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
                    code.invokeinterface(setMethod, 3); // Stack: [oldValue] - the return value of set() is the previous value
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
                            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [List]
                            int clearMethod = cp.addInterfaceMethodRef("java/util/List", "clear", "()V");
                            code.invokeinterface(clearMethod, 1); // Stack: []
                            // Assignment expression should return the assigned value (0 in this case)
                            code.iconst(0); // Stack: [0]
                            return;
                        }

                        // General case for constant new length (like arr.length = 2)
                        // Use List.subList(newLength, size()).clear() to remove excess elements
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number) {
                            int newLength = (int) number.getValue();

                            // Call arr.subList(newLength, arr.size()).clear()
                            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [List]
                            code.dup(); // Stack: [List, List] - keep one for potential use
                            code.iconst(newLength); // Stack: [List, List, newLength]

                            // Get arr.size() - need to load List again
                            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [List, List, newLength, List]
                            int sizeMethod = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
                            code.invokeinterface(sizeMethod, 1); // Stack: [List, List, newLength, size]

                            // Call subList(newLength, size) on the second List
                            int subListMethod = cp.addInterfaceMethodRef("java/util/List", "subList", "(II)Ljava/util/List;");
                            code.invokeinterface(subListMethod, 3); // Stack: [List, List]

                            // Call clear() on the returned subList
                            int clearMethod2 = cp.addInterfaceMethodRef("java/util/List", "clear", "()V");
                            code.invokeinterface(clearMethod2, 1); // Stack: [List]

                            // Assignment expression returns the assigned value (newLength), not the List
                            code.pop(); // Pop the List we kept, Stack: []
                            code.iconst(newLength); // Stack: [newLength]
                            return;
                        }

                        // For non-constant expressions, we need more complex handling
                        throw new Swc4jByteCodeCompilerException(assignExpr, "Setting array length to non-constant values not yet supported");
                    }
                }
            } else if ("Ljava/util/LinkedHashMap;".equals(objType)) {
                // LinkedHashMap operations (object literal property assignment)
                // Check if it's obj[key] = value (computed property)
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // obj[key] = value -> map.put(key, value)
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [LinkedHashMap, key]

                    // Box primitive keys if needed
                    String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, keyType, wrapperType);
                    }

                    compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null); // Stack: [LinkedHashMap, key, value]

                    // Box primitive values if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
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
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    int keyIndex = cp.addString(propName);
                    code.ldc(keyIndex); // Stack: [LinkedHashMap, "prop"]

                    compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null); // Stack: [LinkedHashMap, "prop", value]

                    // Box primitive values if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
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
                throw new Swc4jByteCodeCompilerException(assignExpr, "Undefined variable: " + varName);
            }

            String varType = var.type();
            Swc4jAstAssignOp op = assignExpr.getOp();

            if (op == Swc4jAstAssignOp.Assign) {
                // Simple assignment: x = value
                compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
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
                compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());

                // Special handling for String += (need to convert value to String)
                if ("Ljava/lang/String;".equals(varType) && op == Swc4jAstAssignOp.AddAssign) {
                    // Convert value to String using String.valueOf() if it's not already a String
                    if (!"Ljava/lang/String;".equals(valueType)) {
                        String valueOfDescriptor = switch (valueType) {
                            case "I" -> "(I)Ljava/lang/String;";
                            case "J" -> "(J)Ljava/lang/String;";
                            case "F" -> "(F)Ljava/lang/String;";
                            case "D" -> "(D)Ljava/lang/String;";
                            case "Z" -> "(Z)Ljava/lang/String;";
                            case "C" -> "(C)Ljava/lang/String;";
                            default -> "(Ljava/lang/Object;)Ljava/lang/String;";
                        };
                        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", valueOfDescriptor);
                        code.invokestatic(valueOfRef);
                    }
                } else if (valueType != null && !valueType.equals(varType)) {
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
                            case "Ljava/lang/String;" -> {
                                // String concatenation: result = result.concat(value)
                                // Stack is: [result, value] where value is now a String
                                int concatMethod = cp.addMethodRef("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
                                code.invokevirtual(concatMethod); // Stack: [concatenated]
                            }
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
                            throw new Swc4jByteCodeCompilerException(assignExpr, "Compound assignment not yet supported: " + op.getName());
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
        } else if (left instanceof Swc4jAstArrayPat arrayPat) {
            // Array destructuring assignment: [a, ...rest] = newArray;
            generateArrayPatternAssign(code, cp, context, assignExpr, arrayPat);
            return;
        } else if (left instanceof Swc4jAstObjectPat objectPat) {
            // Object destructuring assignment: { x, ...rest } = newObject;
            generateObjectPatternAssign(code, cp, context, assignExpr, objectPat);
            return;
        }
        throw new Swc4jByteCodeCompilerException(assignExpr, "Assignment expression not yet supported: " + left);
    }

    /**
     * Generate bytecode for array pattern assignment with destructuring.
     * Example: [a, b, ...rest] = newArray;
     */
    private void generateArrayPatternAssign(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstAssignExpr assignExpr,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {

        // Generate the right-hand side expression and store in a temp variable
        compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null);
        int listClass = cp.addClass("java/util/List");
        code.checkcast(listClass);
        int tempListSlot = getOrAllocateTempSlot(context, "$tempList", "Ljava/util/List;");
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef("java/util/List", "get", "(I)Ljava/lang/Object;");
        int listSizeRef = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
        int listAddRef = cp.addInterfaceMethodRef("java/util/List", "add", "(Ljava/lang/Object;)Z");
        int listClearRef = cp.addInterfaceMethodRef("java/util/List", "clear", "()V");

        // First pass: count elements before rest to get restStartIndex
        int restStartIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                restStartIndex++;
                continue;
            }
            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent) {
                restStartIndex++;
            } else if (elem instanceof Swc4jAstRestPat) {
                // Rest pattern found, stop counting
                break;
            }
        }

        // Second pass: extract values and assign to existing variables
        int currentIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                currentIndex++;
                continue;
            }

            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                if (localVar == null) {
                    throw new Swc4jByteCodeCompilerException(bindingIdent,
                            "Undefined variable in array destructuring assignment: " + varName);
                }

                // Load list and call get(index)
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                code.astore(localVar.index());
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                    if (localVar == null) {
                        throw new Swc4jByteCodeCompilerException(bindingIdent,
                                "Undefined variable in array destructuring rest assignment: " + varName);
                    }

                    // Clear the existing ArrayList and repopulate it
                    // rest.clear()
                    code.aload(localVar.index());
                    code.invokeinterface(listClearRef, 1);

                    // Get source list size
                    code.aload(tempListSlot);
                    code.invokeinterface(listSizeRef, 1);
                    int sizeSlot = getOrAllocateTempSlot(context, "$restSize", "I");
                    code.istore(sizeSlot);

                    // Initialize loop counter at restStartIndex
                    code.iconst(restStartIndex);
                    int iSlot = getOrAllocateTempSlot(context, "$restI", "I");
                    code.istore(iSlot);

                    // Loop to copy remaining elements
                    int loopStart = code.getCurrentOffset();
                    code.iload(iSlot);
                    code.iload(sizeSlot);
                    code.if_icmpge(0); // Placeholder
                    int loopExitPatch = code.getCurrentOffset() - 2;

                    // rest.add(source.get(i))
                    code.aload(localVar.index());
                    code.aload(tempListSlot);
                    code.iload(iSlot);
                    code.invokeinterface(listGetRef, 2);
                    code.invokeinterface(listAddRef, 2);
                    code.pop();

                    // i++
                    code.iinc(iSlot, 1);

                    // goto loop start
                    code.gotoLabel(0);
                    int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
                    int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
                    int backwardGotoOffset = loopStart - backwardGotoOpcodePos;
                    code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

                    // Patch loop exit
                    int loopEnd = code.getCurrentOffset();
                    int exitOffset = loopEnd - (loopExitPatch - 1);
                    code.patchShort(loopExitPatch, (short) exitOffset);
                } else {
                    throw new Swc4jByteCodeCompilerException(restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            }
        }

        // Leave the source array on the stack as the expression result
        code.aload(tempListSlot);
    }

    /**
     * Generate bytecode for object pattern assignment with destructuring.
     * Example: { a, b, ...rest } = newObject;
     */
    private void generateObjectPatternAssign(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstAssignExpr assignExpr,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {

        // Generate the right-hand side expression and store in a temp variable
        compiler.getExpressionGenerator().generate(code, cp, assignExpr.getRight(), null);
        int mapClass = cp.addClass("java/util/Map");
        code.checkcast(mapClass);
        int tempMapSlot = getOrAllocateTempSlot(context, "$tempMap", "Ljava/util/Map;");
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef("java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        int mapRemoveRef = cp.addInterfaceMethodRef("java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
        int mapClearRef = cp.addInterfaceMethodRef("java/util/Map", "clear", "()V");
        int mapPutAllRef = cp.addInterfaceMethodRef("java/util/Map", "putAll", "(Ljava/util/Map;)V");

        List<String> extractedKeys = new ArrayList<>();

        // First pass: collect extracted keys
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
                extractedKeys.add(keyName);
            }
            // Rest pattern doesn't add to extracted keys
        }

        // Second pass: extract values and assign to existing variables
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                if (localVar == null) {
                    throw new Swc4jByteCodeCompilerException(assignProp,
                            "Undefined variable in object destructuring assignment: " + varName);
                }

                // Load map and call get(key)
                code.aload(tempMapSlot);
                int stringIndex = cp.addString(varName);
                code.ldc(stringIndex);
                code.invokeinterface(mapGetRef, 2);

                // Handle default value if present
                if (assignProp.getValue().isPresent()) {
                    code.astore(localVar.index());
                    code.aload(localVar.index());
                    code.ifnonnull(0);
                    int skipDefaultPos = code.getCurrentOffset() - 2;

                    compiler.getExpressionGenerator().generate(code, cp, assignProp.getValue().get(), null);
                    code.astore(localVar.index());

                    int afterDefaultLabel = code.getCurrentOffset();
                    int skipOffset = afterDefaultLabel - (skipDefaultPos - 1);
                    code.patchShort(skipDefaultPos, (short) skipOffset);
                } else {
                    code.astore(localVar.index());
                }

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                    if (localVar == null) {
                        throw new Swc4jByteCodeCompilerException(bindingIdent,
                                "Undefined variable in object destructuring assignment: " + varName);
                    }

                    // Load map and call get(key)
                    code.aload(tempMapSlot);
                    int stringIndex = cp.addString(keyName);
                    code.ldc(stringIndex);
                    code.invokeinterface(mapGetRef, 2);
                    code.astore(localVar.index());
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                    if (localVar == null) {
                        throw new Swc4jByteCodeCompilerException(bindingIdent,
                                "Undefined variable in object destructuring rest assignment: " + varName);
                    }

                    // Clear existing map and copy source content
                    // rest.clear()
                    code.aload(localVar.index());
                    code.invokeinterface(mapClearRef, 1);

                    // rest.putAll(tempMap)
                    code.aload(localVar.index());
                    code.aload(tempMapSlot);
                    code.invokeinterface(mapPutAllRef, 2);

                    // Remove all extracted keys from the rest map
                    for (String key : extractedKeys) {
                        code.aload(localVar.index());
                        int keyStringIndex = cp.addString(key);
                        code.ldc(keyStringIndex);
                        code.invokeinterface(mapRemoveRef, 2);
                        code.pop();
                    }
                } else {
                    throw new Swc4jByteCodeCompilerException(restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            }
        }

        // Leave the source map on the stack as the expression result
        code.aload(tempMapSlot);
    }

    /**
     * Get or allocate a temp variable slot.
     */
    private int getOrAllocateTempSlot(CompilationContext context, String name, String type) {
        LocalVariable existing = context.getLocalVariableTable().getVariable(name);
        if (existing != null) {
            return existing.index();
        }
        return context.getLocalVariableTable().allocateVariable(name, type);
    }
}
