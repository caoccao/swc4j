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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUpdateOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUpdateExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class UpdateExpressionGenerator {
    private UpdateExpressionGenerator() {
    }

    private static void addPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.iadd();
            case "J" -> code.ladd();
            case "F" -> code.fadd();
            case "D" -> code.dadd();
        }
    }

    private static void duplicatePrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "J", "D" -> code.dup2(); // long and double take 2 stack slots
            default -> code.dup();         // all others take 1 slot
        }
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUpdateExpr updateExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Check what kind of operand we have
        if (updateExpr.getArg() instanceof Swc4jAstIdent ident) {
            // Phase 1: Local variables
            handleLocalVariable(code, cp, updateExpr, ident, returnTypeInfo, context, options);
        } else if (updateExpr.getArg() instanceof Swc4jAstMemberExpr memberExpr) {
            // Phase 2: Member access (obj.prop++ or arr[i]++)
            handleMemberAccess(code, cp, updateExpr, memberExpr, returnTypeInfo, context, options);
        } else {
            throw new Swc4jByteCodeCompilerException(
                    "Update expressions only support identifiers and member expressions, got: " + updateExpr.getArg().getClass().getSimpleName());
        }
    }

    /**
     * Generate appropriate array load instruction based on element type.
     */
    private static void generateArrayLoad(CodeBuilder code, String elementType) {
        switch (elementType) {
            case "Z", "B" -> code.baload(); // boolean and byte
            case "C" -> code.caload(); // char
            case "S" -> code.saload(); // short
            case "I" -> code.iaload(); // int
            case "J" -> code.laload(); // long
            case "F" -> code.faload(); // float
            case "D" -> code.daload(); // double
            default -> code.aaload(); // reference types (Object, Integer, etc.)
        }
    }

    /**
     * Generate appropriate array store instruction based on element type.
     */
    private static void generateArrayStore(CodeBuilder code, String elementType) {
        switch (elementType) {
            case "Z", "B" -> code.bastore(); // boolean and byte
            case "C" -> code.castore(); // char
            case "S" -> code.sastore(); // short
            case "I" -> code.iastore(); // int
            case "J" -> code.lastore(); // long
            case "F" -> code.fastore(); // float
            case "D" -> code.dastore(); // double
            default -> code.aastore(); // reference types (Object, Integer, etc.)
        }
    }

    private static void generateBox(CodeBuilder code, ClassWriter.ConstantPool cp, String primitiveType, String wrapperType) {
        String className = wrapperType.substring(1, wrapperType.length() - 1); // Remove L and ;
        String descriptor = "(" + primitiveType + ")" + wrapperType;
        int methodRef = cp.addMethodRef(className, "valueOf", descriptor);
        code.invokestatic(methodRef);
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

    /**
     * Handle update on ArrayList element: arr[index]++.
     */
    private static void handleArrayListUpdate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            Swc4jAstComputedPropName computedProp,
            boolean isIncrement,
            boolean isPrefix,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Step 1: Get current value from ArrayList
        ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // [ArrayList]
        ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // [ArrayList, index]

        // Convert index to int if needed
        String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
        if (indexType != null && !"I".equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
        }

        // Call ArrayList.get(int)
        int getMethod = cp.addMethodRef("java/util/ArrayList", "get", "(I)Ljava/lang/Object;");
        code.invokevirtual(getMethod); // [Object]

        // Assume Integer type for now
        int integerClass = cp.addClass("java/lang/Integer");
        code.checkcast(integerClass); // [Integer]

        // Unbox to primitive
        int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
        code.invokevirtual(intValueMethod); // [int] - old value

        // Step 2: Increment/decrement to get new value
        code.iconst(1); // [int, 1]
        if (isIncrement) {
            code.iadd();
        } else {
            code.isub();
        } // [new_int]

        // Step 3: Box the new value
        int valueOfMethod = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        code.invokestatic(valueOfMethod); // [new_Integer]

        // Step 4: For postfix, we need to return the old value, so get it from ArrayList.set()'s return
        // For prefix, we duplicate the new value to return it
        if (isPrefix) {
            code.dup(); // [new_Integer, new_Integer] - one to store, one to return
        }

        // Step 5: Store back to ArrayList
        // Stack is now: [new_Integer] (postfix) or [new_Integer, new_Integer] (prefix)

        // Generate ArrayList reference
        ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options);
        // Stack: [new_Integer, ArrayList] or [new_Integer, new_Integer, ArrayList]

        // Load index
        ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options);

        // Convert index to int if needed
        if (indexType != null && !"I".equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
        }
        // Stack: [new_Integer, ArrayList, index] or [new_Integer, new_Integer, ArrayList, index]

        // For prefix: need to rearrange [new_Integer, new_Integer, ArrayList, index] to [new_Integer, ArrayList, index, new_Integer]
        // For postfix: need to rearrange [new_Integer, ArrayList, index] to [ArrayList, index, new_Integer]
        if (isPrefix) {
            // Stack: [return_value, value_to_store, ArrayList, index]
            code.dup_x2(); // [return_value, index, value_to_store, ArrayList, index]
            code.pop(); // [return_value, index, value_to_store, ArrayList]
            code.swap(); // [return_value, index, ArrayList, value_to_store]
            code.dup_x2(); // [return_value, value_to_store, index, ArrayList, value_to_store]
            code.pop(); // [return_value, value_to_store, index, ArrayList]
            code.dup_x2(); // [return_value, ArrayList, value_to_store, index, ArrayList]
            code.pop(); // [return_value, ArrayList, value_to_store, index]
            code.swap(); // [return_value, ArrayList, index, value_to_store]
        } else {
            // Stack: [value_to_store, ArrayList, index]
            // Rearrange to: [ArrayList, index, value_to_store]
            code.dup_x2(); // [index, value_to_store, ArrayList, index]
            code.pop(); // [index, value_to_store, ArrayList]
            code.dup_x2(); // [ArrayList, index, value_to_store, ArrayList]
            code.pop(); // [ArrayList, index, value_to_store]
        }

        // Call ArrayList.set(int, Object)
        int setMethod = cp.addMethodRef("java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
        code.invokevirtual(setMethod); // [return_value, old_value_from_set] or [old_value_from_set]

        // For prefix, we already have the return value on stack, pop the set result
        // For postfix, the set result IS the old value we want to return
        if (isPrefix) {
            code.pop(); // [return_value=new_Integer]
        }
        // For postfix: [old_Integer] from set's return value
    }

    /**
     * Handle update on LinkedHashMap property: obj.prop++ or obj[key]++.
     */
    private static void handleLinkedHashMapUpdate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            boolean isIncrement,
            boolean isPrefix,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Determine the property name/key
        String propertyName = null;
        boolean isComputedKey = false;

        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            // obj.prop
            propertyName = propIdent.getSym();
        } else if (memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
            // obj[key] - computed property
            isComputedKey = true;
        } else {
            throw new Swc4jByteCodeCompilerException(
                    "Unsupported property type for update expression: " + memberExpr.getProp().getClass().getSimpleName());
        }

        // Step 1: Get current value from map
        ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // [map or Object]

        // Cast to LinkedHashMap if the type is Object (for nested properties)
        String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);
        if ("Ljava/lang/Object;".equals(objType)) {
            int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
            code.checkcast(linkedHashMapClass); // [LinkedHashMap]
        }

        if (isComputedKey) {
            Swc4jAstComputedPropName computedProp = (Swc4jAstComputedPropName) memberExpr.getProp();
            ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // [map, key]
            // Box key if primitive
            String keyType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
            if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                TypeConversionUtils.boxPrimitiveType(code, cp, keyType, TypeConversionUtils.getWrapperType(keyType));
            }
        } else {
            // Named property: load string constant
            int keyIndex = cp.addString(propertyName);
            code.ldc(keyIndex); // [map, "prop"]
        }

        // Call map.get(key)
        int getMethod = cp.addMethodRef("java/util/LinkedHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        code.invokevirtual(getMethod); // [Object]

        // Assume Integer type for now (we'll need to infer this properly later)
        // TODO: Add proper type inference for map values
        int integerClass = cp.addClass("java/lang/Integer");
        code.checkcast(integerClass); // [Integer]

        // Unbox to primitive
        int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
        code.invokevirtual(intValueMethod); // [int] - old value

        // Step 2: Increment/decrement to get new value
        code.iconst(1); // [int, 1]
        if (isIncrement) {
            code.iadd();
        } else {
            code.isub();
        } // [new_int]

        // Step 3: Box the new value
        int valueOfMethod = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        code.invokestatic(valueOfMethod); // [new_Integer]

        // Step 4: For postfix, we need to return the old value, so get it from map.put()'s return
        // For prefix, we duplicate the new value to return it
        if (isPrefix) {
            code.dup(); // [new_Integer, new_Integer] - one to store, one to return
        }

        // Step 5: Store back to map
        // Stack is now: [new_Integer] (postfix) or [new_Integer, new_Integer] (prefix)

        // Generate map reference
        ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options);
        // Stack: [new_Integer, map or Object] or [new_Integer, new_Integer, map or Object]

        // Cast to LinkedHashMap if the type is Object (for nested properties)
        if ("Ljava/lang/Object;".equals(objType)) {
            int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
            code.checkcast(linkedHashMapClass); // [LinkedHashMap]
        }
        // Stack: [new_Integer, map] or [new_Integer, new_Integer, map]

        // Load key
        if (isComputedKey) {
            Swc4jAstComputedPropName computedProp = (Swc4jAstComputedPropName) memberExpr.getProp();
            ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options);
            // Box key if primitive
            String keyType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
            if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                TypeConversionUtils.boxPrimitiveType(code, cp, keyType, TypeConversionUtils.getWrapperType(keyType));
            }
        } else {
            int keyIndex = cp.addString(propertyName);
            code.ldc(keyIndex);
        }
        // Stack: [new_Integer, map, key] or [new_Integer, new_Integer, map, key]

        // For prefix: need to rearrange [new_Integer, new_Integer, map, key] to [new_Integer, map, key, new_Integer]
        // For postfix: need to rearrange [new_Integer, map, key] to [map, key, new_Integer]
        if (isPrefix) {
            // Stack: [return_value, value_to_store, map, key]
            // Use dup_x2 and swap to rearrange
            code.dup_x2(); // [return_value, key, value_to_store, map, key]
            code.pop(); // [return_value, key, value_to_store, map]
            code.swap(); // [return_value, key, map, value_to_store]
            code.dup_x2(); // [return_value, value_to_store, key, map, value_to_store]
            code.pop(); // [return_value, value_to_store, key, map]
            code.dup_x2(); // [return_value, map, value_to_store, key, map]
            code.pop(); // [return_value, map, value_to_store, key]
            code.swap(); // [return_value, map, key, value_to_store]
        } else {
            // Stack: [value_to_store, map, key]
            // Rearrange to: [map, key, value_to_store]
            code.dup_x2(); // [key, value_to_store, map, key]
            code.pop(); // [key, value_to_store, map]
            code.dup_x2(); // [map, key, value_to_store, map]
            code.pop(); // [map, key, value_to_store]
        }

        // Call map.put(key, value)
        int putMethod = cp.addMethodRef("java/util/LinkedHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        code.invokevirtual(putMethod); // [return_value, old_value_from_put] or [old_value_from_put]

        // For prefix, we already have the return value on stack, pop the put result
        // For postfix, the put result IS the old value we want to return
        if (isPrefix) {
            code.pop(); // [return_value=new_Integer]
        }
        // For postfix: [old_Integer] from put's return value
    }

    /**
     * Handle update expression on local variable (Phase 1).
     */
    private static void handleLocalVariable(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstIdent ident,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

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
     * Handle update expression on member access (Phase 2).
     * Supports obj.prop++ and obj[key]++.
     */
    private static void handleMemberAccess(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstMemberExpr memberExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);
        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        // Handle LinkedHashMap (object literals): obj.prop++ or obj[key]++
        if ("Ljava/util/LinkedHashMap;".equals(objType)) {
            handleLinkedHashMapUpdate(code, cp, memberExpr, isIncrement, isPrefix, context, options);
            return;
        }

        // Handle ArrayList: arr[index]++
        if ("Ljava/util/ArrayList;".equals(objType)) {
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                handleArrayListUpdate(code, cp, memberExpr, computedProp, isIncrement, isPrefix, context, options);
                return;
            }
        }

        // Handle native Java arrays: arr[index]++ for int[], Object[], etc.
        if (objType != null && objType.startsWith("[")) {
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                handleNativeArrayUpdate(code, cp, memberExpr, computedProp, objType, isIncrement, isPrefix, context, options);
                return;
            }
        }

        // Handle Object type (nested properties like obj.inner.count++ where obj.inner returns Object)
        // Assume it's a LinkedHashMap since that's what object literals compile to
        if ("Ljava/lang/Object;".equals(objType)) {
            handleLinkedHashMapUpdate(code, cp, memberExpr, isIncrement, isPrefix, context, options);
            return;
        }

        throw new Swc4jByteCodeCompilerException(
                "Update expressions on member access not yet supported for type: " + objType);
    }

    /**
     * Handle update on native Java array element: arr[index]++ for int[], long[], etc.
     * Currently supports only primitive arrays (int[], long[], double[], etc.).
     */
    private static void handleNativeArrayUpdate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            Swc4jAstComputedPropName computedProp,
            String arrayType,
            boolean isIncrement,
            boolean isPrefix,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        // Determine element type from array type descriptor
        // e.g., "[I" -> "I" (int), "[J" -> "J" (long)
        String elementType = arrayType.substring(1);

        // Only support primitive element types for now
        if (!TypeConversionUtils.isPrimitiveType(elementType)) {
            throw new Swc4jByteCodeCompilerException(
                    "Update expressions on arrays currently only support primitive element types, got: " + elementType);
        }

        // Use dup2 early for both prefix and postfix to avoid complex stack manipulation

        // Step 1: Load array and index
        ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // [array]
        ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // [array, index]

        // Convert index to int if needed
        String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
        if (indexType != null && !"I".equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
        }

        // Duplicate array and index for later store
        code.dup2(); // [array, index, array, index]

        // Step 2: Load current element value
        generateArrayLoad(code, elementType); // [array, index, old_value]

        // Step 3: Handle prefix vs postfix
        if (isPrefix) {
            // Prefix: [array, index, old_value]
            loadOne(code, elementType);
            if (isIncrement) {
                addPrimitive(code, elementType);
            } else {
                subtractPrimitive(code, elementType);
            } // [array, index, new_value]

            // Duplicate new value for return, then store
            if ("J".equals(elementType) || "D".equals(elementType)) {
                // Category 2: [array, index, new_value(2)]
                code.dup2_x2(); // [new_value(2), array, index, new_value(2)]
            } else {
                // Category 1: [array, index, new_value]
                code.dup_x2(); // [new_value, array, index, new_value]
            }
            generateArrayStore(code, elementType); // [new_value]
        } else {
            // Postfix: [array, index, old_value]
            // For postfix, we need to keep the old value but store the new value
            // Stack rearrangement: [array, index, old] -> increment -> [array, index, old, new]
            // Then rearrange to: [old, array, index, new] so iastore consumes last 3

            // First, move old value out of the way
            // We'll use a pattern: load, increment, then juggle stack
            if ("J".equals(elementType) || "D".equals(elementType)) {
                // Category 2 - defer for now
                throw new Swc4jByteCodeCompilerException(
                        "Postfix update on long/double arrays not yet supported");
            } else {
                // Category 1: [array, index, old]
                // Move old to bottom, keeping array+index in place for later
                code.dup_x2(); // [old, array, index, old]
                // Increment the top copy
                loadOne(code, elementType); // [old, array, index, old, 1]
                if (isIncrement) {
                    addPrimitive(code, elementType); // [old, array, index, new]
                } else {
                    subtractPrimitive(code, elementType); // [old, array, index, new]
                }
                // Now: [old, array, index, new] - perfect for array store!
            }
            generateArrayStore(code, elementType); // [old]
        }

        // Stack now has return value: new value for prefix, old value for postfix
    }

    private static void loadOne(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.iconst(1);
            case "J" -> code.lconst(1L);
            case "F" -> code.fconst(1.0f);
            case "D" -> code.dconst(1.0);
        }
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

    private static void subtractPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case "I", "B", "S", "C" -> code.isub();
            case "J" -> code.lsub();
            case "F" -> code.fsub();
            case "D" -> code.dsub();
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
}
