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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUpdateOp;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.ClassHierarchyUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Update expression processor.
 */
public final class UpdateExpressionProcessor extends BaseAstProcessor<Swc4jAstUpdateExpr> {
    /**
     * Instantiates a new Update expression processor.
     *
     * @param compiler the compiler
     */
    public UpdateExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void addPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER -> code.iadd();
            case ConstantJavaType.ABBR_LONG -> code.ladd();
            case ConstantJavaType.ABBR_FLOAT -> code.fadd();
            case ConstantJavaType.ABBR_DOUBLE -> code.dadd();
        }
    }

    private void duplicatePrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_DOUBLE ->
                    code.dup2(); // long and double take 2 stack slots
            default -> code.dup();         // all others take 1 slot
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Check what kind of operand we have
        if (updateExpr.getArg() instanceof Swc4jAstIdent ident) {
            // Phase 1: Local variables
            handleLocalVariable(code, classWriter, updateExpr, ident, returnTypeInfo);
        } else if (updateExpr.getArg() instanceof Swc4jAstSuperPropExpr superPropExpr) {
            handleSuperPropertyUpdate(code, classWriter, updateExpr, superPropExpr);
        } else if (updateExpr.getArg() instanceof Swc4jAstMemberExpr memberExpr) {
            // Phase 2: Member access (obj.prop++ or arr[i]++)
            handleMemberAccess(code, classWriter, updateExpr, memberExpr, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                    "Update expressions only support identifiers and member expressions, got: " + updateExpr.getArg().getClass().getSimpleName());
        }
    }

    /**
     * Generate appropriate array load instruction based on element type.
     */
    private void generateArrayLoad(CodeBuilder code, String elementType) {
        switch (elementType) {
            case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload(); // boolean and byte
            case ConstantJavaType.ABBR_CHARACTER -> code.caload(); // char
            case ConstantJavaType.ABBR_SHORT -> code.saload(); // short
            case ConstantJavaType.ABBR_INTEGER -> code.iaload(); // int
            case ConstantJavaType.ABBR_LONG -> code.laload(); // long
            case ConstantJavaType.ABBR_FLOAT -> code.faload(); // float
            case ConstantJavaType.ABBR_DOUBLE -> code.daload(); // double
            default -> code.aaload(); // reference types (Object, Integer, etc.)
        }
    }

    /**
     * Generate appropriate array store instruction based on element type.
     */
    private void generateArrayStore(CodeBuilder code, String elementType) {
        switch (elementType) {
            case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore(); // boolean and byte
            case ConstantJavaType.ABBR_CHARACTER -> code.castore(); // char
            case ConstantJavaType.ABBR_SHORT -> code.sastore(); // short
            case ConstantJavaType.ABBR_INTEGER -> code.iastore(); // int
            case ConstantJavaType.ABBR_LONG -> code.lastore(); // long
            case ConstantJavaType.ABBR_FLOAT -> code.fastore(); // float
            case ConstantJavaType.ABBR_DOUBLE -> code.dastore(); // double
            default -> code.aastore(); // reference types (Object, Integer, etc.)
        }
    }

    private void generateBox(CodeBuilder code, ClassWriter classWriter, String primitiveType, String wrapperType) {
        var cp = classWriter.getConstantPool();
        String className = TypeConversionUtils.descriptorToInternalName(wrapperType);
        String descriptor = "(" + primitiveType + ")" + wrapperType;
        int methodRef = cp.addMethodRef(className, ConstantJavaMethod.METHOD_VALUE_OF, descriptor);
        code.invokestatic(methodRef);
    }

    /**
     * General code generation for non-int numeric types (long, float, double, wrappers).
     * Always leaves a value on the stack (either old value for postfix or new value for prefix).
     */
    private void generateGeneralUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
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
            generateUnbox(code, classWriter, varType, primitiveType);
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
            generateBox(code, classWriter, primitiveType, varType);
            // For prefix: duplicate boxed value BEFORE storing
            if (isPrefix) {
                code.dup(); // Duplicate the wrapper reference
            }
            code.astore(varIndex);
            // For postfix: box the old primitive value left on stack
            if (!isPrefix) {
                generateBox(code, classWriter, primitiveType, varType);
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
    private void generateIntUpdate(
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

    private void generateUnbox(CodeBuilder code, ClassWriter classWriter, String wrapperType, String primitiveType) {
        var cp = classWriter.getConstantPool();
        String methodName = switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaMethod.METHOD_INT_VALUE;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaMethod.METHOD_LONG_VALUE;
            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaMethod.METHOD_FLOAT_VALUE;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaMethod.METHOD_DOUBLE_VALUE;
            case ConstantJavaType.ABBR_BYTE -> ConstantJavaMethod.METHOD_BYTE_VALUE;
            case ConstantJavaType.ABBR_SHORT -> ConstantJavaMethod.METHOD_SHORT_VALUE;
            default -> throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
        };

        String descriptor = "()" + primitiveType;
        String className = TypeConversionUtils.descriptorToInternalName(wrapperType);
        int methodRef = cp.addMethodRef(className, methodName, descriptor);
        code.invokevirtual(methodRef);
    }

    /**
     * Handle update on ArrayList element: arr[index]++.
     */
    private void handleArrayListUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr,
            Swc4jAstComputedPropName computedProp,
            boolean isIncrement,
            boolean isPrefix) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Step 1: Get current value from ArrayList
        compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // [ArrayList]
        compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // [ArrayList, index]

        // Convert index to int if needed
        String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
        if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
        }

        // Call ArrayList.get(int)
        int getMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT);
        code.invokevirtual(getMethod); // [Object]

        // Assume Integer type for now
        int integerClass = cp.addClass(ConstantJavaType.JAVA_LANG_INTEGER);
        code.checkcast(integerClass); // [Integer]

        // Unbox to primitive
        int intValueMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.__I);
        code.invokevirtual(intValueMethod); // [int] - old value

        // Step 2: Increment/decrement to get new value
        code.iconst(1); // [int, 1]
        if (isIncrement) {
            code.iadd();
        } else {
            code.isub();
        } // [new_int]

        // Step 3: Box the new value
        int valueOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.I__LJAVA_LANG_INTEGER);
        code.invokestatic(valueOfMethod); // [new_Integer]

        // Step 4: For postfix, we need to return the old value, so get it from ArrayList.set()'s return
        // For prefix, we duplicate the new value to return it
        if (isPrefix) {
            code.dup(); // [new_Integer, new_Integer] - one to store, one to return
        }

        // Step 5: Store back to ArrayList
        // Stack is now: [new_Integer] (postfix) or [new_Integer, new_Integer] (prefix)

        // Generate ArrayList reference
        compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
        // Stack: [new_Integer, ArrayList] or [new_Integer, new_Integer, ArrayList]

        // Load index
        compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);

        // Convert index to int if needed
        if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
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
        int setMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_SET, ConstantJavaDescriptor.I_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        code.invokevirtual(setMethod); // [return_value, old_value_from_set] or [old_value_from_set]

        // For prefix, we already have the return value on stack, pop the set result
        // For postfix, the set result IS the old value we want to return
        if (isPrefix) {
            code.pop(); // [return_value=new_Integer]
        }
        // For postfix: [old_Integer] from set's return value
    }

    private void handleInstanceFieldUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstMemberExpr memberExpr) throws Swc4jByteCodeCompilerException {
        String fieldName;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            fieldName = propIdent.getSym();
        } else if (memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
            fieldName = privateName.getName();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Unsupported instance field update target: " + memberExpr.getProp().getClass().getSimpleName());
        }

        CompilationContext context = compiler.getMemory().getCompilationContext();
        String currentClassName = context.getCurrentClassInternalName();
        if (currentClassName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "No current class context for instance field update");
        }

        String ownerInternalName = currentClassName;
        FieldInfo fieldInfo = null;

        var cp = classWriter.getConstantPool();
        var capturedThis = context.getCapturedVariable("this");
        if (capturedThis != null) {
            String outerClassName = TypeConversionUtils.descriptorToInternalName(capturedThis.type());
            code.aload(0);
            int capturedThisRef = cp.addFieldRef(currentClassName, capturedThis.fieldName(), capturedThis.type());
            code.getfield(capturedThisRef);

            String outerQualifiedName = outerClassName.replace('/', '.');
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(outerQualifiedName);
            if (typeInfo == null) {
                int lastSlash = outerClassName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? outerClassName.substring(lastSlash + 1) : outerClassName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }
            if (typeInfo != null) {
                FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                if (lookupResult != null) {
                    fieldInfo = lookupResult.fieldInfo;
                    ownerInternalName = lookupResult.ownerInternalName;
                }
            }
        } else {
            code.aload(0);
            String qualifiedName = currentClassName.replace('/', '.');
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                int lastSlash = currentClassName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }
            if (typeInfo != null) {
                FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                if (lookupResult != null) {
                    fieldInfo = lookupResult.fieldInfo;
                    ownerInternalName = lookupResult.ownerInternalName;
                }
            }
        }

        if (fieldInfo == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "Field not found: " + fieldName);
        }

        updateInstanceField(code, classWriter, updateExpr, ownerInternalName, fieldName, fieldInfo.descriptor());
    }

    /**
     * Handle update on LinkedHashMap property: obj.prop++ or obj[key]++.
     */
    private void handleLinkedHashMapUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr,
            boolean isIncrement,
            boolean isPrefix) throws Swc4jByteCodeCompilerException {

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
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Unsupported property type for update expression: " + memberExpr.getProp().getClass().getSimpleName());
        }

        // Step 1: Get current value from map
        compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // [map or Object]

        // Cast to LinkedHashMap if the type is Object (for nested properties)
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        var cp = classWriter.getConstantPool();
        if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(objType)) {
            int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
            code.checkcast(linkedHashMapClass); // [LinkedHashMap]
        }

        if (isComputedKey) {
            Swc4jAstComputedPropName computedProp = (Swc4jAstComputedPropName) memberExpr.getProp();
            compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // [map, key]
            // Box key if primitive
            String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
            if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, keyType, TypeConversionUtils.getWrapperType(keyType));
            }
        } else {
            // Named property: load string constant
            int keyIndex = cp.addString(propertyName);
            code.ldc(keyIndex); // [map, "prop"]
        }

        // Call map.get(key)
        int getMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        code.invokevirtual(getMethod); // [Object]

        // Assume Integer type for now (we'll need to infer this properly later)
        // TODO: Add proper type inference for map values
        int integerClass = cp.addClass(ConstantJavaType.JAVA_LANG_INTEGER);
        code.checkcast(integerClass); // [Integer]

        // Unbox to primitive
        int intValueMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.__I);
        code.invokevirtual(intValueMethod); // [int] - old value

        // Step 2: Increment/decrement to get new value
        code.iconst(1); // [int, 1]
        if (isIncrement) {
            code.iadd();
        } else {
            code.isub();
        } // [new_int]

        // Step 3: Box the new value
        int valueOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.I__LJAVA_LANG_INTEGER);
        code.invokestatic(valueOfMethod); // [new_Integer]

        // Step 4: For postfix, we need to return the old value, so get it from map.put()'s return
        // For prefix, we duplicate the new value to return it
        if (isPrefix) {
            code.dup(); // [new_Integer, new_Integer] - one to store, one to return
        }

        // Step 5: Store back to map
        // Stack is now: [new_Integer] (postfix) or [new_Integer, new_Integer] (prefix)

        // Generate map reference
        compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
        // Stack: [new_Integer, map or Object] or [new_Integer, new_Integer, map or Object]

        // Cast to LinkedHashMap if the type is Object (for nested properties)
        if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(objType)) {
            int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
            code.checkcast(linkedHashMapClass); // [LinkedHashMap]
        }
        // Stack: [new_Integer, map] or [new_Integer, new_Integer, map]

        // Load key
        if (isComputedKey) {
            Swc4jAstComputedPropName computedProp = (Swc4jAstComputedPropName) memberExpr.getProp();
            compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);
            // Box key if primitive
            String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
            if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, keyType, TypeConversionUtils.getWrapperType(keyType));
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
        int putMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, "put", ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
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
    private void handleLocalVariable(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstIdent ident,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        String varName = ident.getSym();
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

        if (localVar == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), ident, "Variable '" + varName + "' not found in local scope");
        }

        String varType = localVar.type();
        int varIndex = localVar.index();
        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        // Validate type - only numeric types can be incremented/decremented
        switch (varType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_FLOAT,
                 ConstantJavaType.ABBR_DOUBLE,
                 ConstantJavaType.LJAVA_LANG_INTEGER, ConstantJavaType.LJAVA_LANG_LONG,
                 ConstantJavaType.LJAVA_LANG_FLOAT,
                 ConstantJavaType.LJAVA_LANG_DOUBLE, ConstantJavaType.LJAVA_LANG_BYTE,
                 ConstantJavaType.LJAVA_LANG_SHORT -> {
                // Valid numeric type
            }
            case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.LJAVA_LANG_BOOLEAN ->
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                            "Cannot apply " + updateExpr.getOp().getName() + " operator to boolean type");
            case ConstantJavaType.LJAVA_LANG_STRING ->
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                            "Cannot apply " + updateExpr.getOp().getName() + " operator to string type");
            default -> throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to type: " + varType);
        }

        // Optimization: Use iinc instruction for int local variables
        if (varType.equals(ConstantJavaType.ABBR_INTEGER)) {
            generateIntUpdate(code, varIndex, isIncrement, isPrefix);
        } else {
            // General case for other numeric types
            generateGeneralUpdate(code, classWriter, varType, varIndex, isIncrement, isPrefix);
        }
    }

    /**
     * Handle update expression on member access (Phase 2).
     * Supports obj.prop++ and obj[key]++.
     */
    private void handleMemberAccess(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstMemberExpr memberExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        if (memberExpr.getObj() instanceof Swc4jAstThisExpr
                && (memberExpr.getProp() instanceof Swc4jAstIdentName || memberExpr.getProp() instanceof Swc4jAstPrivateName)) {
            handleInstanceFieldUpdate(code, classWriter, updateExpr, memberExpr);
            return;
        }

        if (memberExpr.getObj() instanceof Swc4jAstIdent
                && (memberExpr.getProp() instanceof Swc4jAstIdentName || memberExpr.getProp() instanceof Swc4jAstPrivateName)) {
            if (handleStaticFieldUpdate(code, classWriter, updateExpr, memberExpr)) {
                return;
            }
        }

        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        // Handle LinkedHashMap (object literals): obj.prop++ or obj[key]++
        if (ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP.equals(objType)) {
            handleLinkedHashMapUpdate(code, classWriter, memberExpr, isIncrement, isPrefix);
            return;
        }

        // Handle ArrayList: arr[index]++
        if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType)) {
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                handleArrayListUpdate(code, classWriter, memberExpr, computedProp, isIncrement, isPrefix);
                return;
            }
        }

        // Handle native Java arrays: arr[index]++ for int[], Object[], etc.
        if (objType != null && objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                handleNativeArrayUpdate(code, classWriter, memberExpr, computedProp, objType, isIncrement, isPrefix);
                return;
            }
        }

        // Handle Object type (nested properties like obj.inner.count++ where obj.inner returns Object)
        // Assume it's a LinkedHashMap since that's what object literals compile to
        if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(objType)) {
            handleLinkedHashMapUpdate(code, classWriter, memberExpr, isIncrement, isPrefix);
            return;
        }

        throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                "Update expressions on member access not yet supported for type: " + objType);
    }

    /**
     * Handle update on native Java array element: arr[index]++ for int[], long[], etc.
     * Currently supports only primitive arrays (int[], long[], double[], etc.).
     */
    private void handleNativeArrayUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr,
            Swc4jAstComputedPropName computedProp,
            String arrayType,
            boolean isIncrement,
            boolean isPrefix) throws Swc4jByteCodeCompilerException {

        // Determine element type from array type descriptor
        // e.g., ConstantJavaType.ARRAY_I -> ConstantJavaType.ABBR_INTEGER (int), ConstantJavaType.ARRAY_J -> ConstantJavaType.ABBR_LONG (long)
        String elementType = TypeConversionUtils.getArrayElementType(arrayType);

        // Only support primitive element types for now
        if (!TypeConversionUtils.isPrimitiveType(elementType)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Update expressions on arrays currently only support primitive element types, got: " + elementType);
        }

        if (!isPrefix && (ConstantJavaType.ABBR_LONG.equals(elementType) || ConstantJavaType.ABBR_DOUBLE.equals(elementType))) {
            CompilationContext context = compiler.getMemory().getCompilationContext();
            int arraySlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                    "$tempUpdateArray" + context.getNextTempId(), arrayType);
            int indexSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                    "$tempUpdateIndex" + context.getNextTempId(), ConstantJavaType.ABBR_INTEGER);
            int valueSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                    "$tempUpdateValue" + context.getNextTempId(), elementType);
            int newValueSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                    "$tempUpdateNewValue" + context.getNextTempId(), elementType);

            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
            code.astore(arraySlot);

            compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);
            String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
            if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
                TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
            }
            code.istore(indexSlot);

            code.aload(arraySlot);
            code.iload(indexSlot);
            generateArrayLoad(code, elementType);
            storePrimitive(code, elementType, valueSlot);

            loadPrimitive(code, elementType, valueSlot);
            loadOne(code, elementType);
            if (isIncrement) {
                addPrimitive(code, elementType);
            } else {
                subtractPrimitive(code, elementType);
            }
            storePrimitive(code, elementType, newValueSlot);

            code.aload(arraySlot);
            code.iload(indexSlot);
            loadPrimitive(code, elementType, newValueSlot);
            generateArrayStore(code, elementType);

            loadPrimitive(code, elementType, valueSlot);
            return;
        }

        // Use dup2 early for both prefix and postfix to avoid complex stack manipulation

        // Step 1: Load array and index
        compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // [array]
        compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // [array, index]

        // Convert index to int if needed
        String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
        if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
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
            if (ConstantJavaType.ABBR_LONG.equals(elementType) || ConstantJavaType.ABBR_DOUBLE.equals(elementType)) {
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
            if (ConstantJavaType.ABBR_LONG.equals(elementType) || ConstantJavaType.ABBR_DOUBLE.equals(elementType)) {
                // Category 2 - use temp local to avoid complex stack manipulation
                CompilationContext context = compiler.getMemory().getCompilationContext();
                int tempSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                        "$tempUpdate" + context.getNextTempId(), elementType);
                storePrimitive(code, elementType, tempSlot); // [array, index]

                loadPrimitive(code, elementType, tempSlot); // [array, index, old]
                loadOne(code, elementType); // [array, index, old, 1]
                if (isIncrement) {
                    addPrimitive(code, elementType); // [array, index, new]
                } else {
                    subtractPrimitive(code, elementType); // [array, index, new]
                }
                generateArrayStore(code, elementType); // []
                loadPrimitive(code, elementType, tempSlot); // [old]
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

    private boolean handleStaticFieldUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstMemberExpr memberExpr) throws Swc4jByteCodeCompilerException {
        if (!(memberExpr.getObj() instanceof Swc4jAstIdent classIdent)) {
            return false;
        }
        String className = classIdent.getSym();
        String fieldName;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            fieldName = propIdent.getSym();
        } else if (memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
            fieldName = privateName.getName();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Unsupported static field update target: " + memberExpr.getProp().getClass().getSimpleName());
        }

        JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
        if (typeInfo == null) {
            return false;
        }
        FieldInfo fieldInfo = typeInfo.getField(fieldName);
        if (fieldInfo == null || !fieldInfo.isStatic()) {
            return false;
        }

        updateStaticField(code, classWriter, updateExpr, typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
        return true;
    }

    private void handleSuperPropertyUpdate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            Swc4jAstSuperPropExpr superPropExpr) throws Swc4jByteCodeCompilerException {
        String fieldName = AstUtils.extractSuperPropertyName(getSourceCode(), superPropExpr);
        CompilationContext context = compiler.getMemory().getCompilationContext();
        String currentClassName = context.getCurrentClassInternalName();
        if (currentClassName == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "super property update outside of class context");
        }

        String superClassInternalName = ClassHierarchyUtils.resolveSuperClassInternalName(compiler, currentClassName);
        if (superClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Cannot resolve superclass for " + currentClassName);
        }

        JavaTypeInfo superTypeInfo = resolveTypeInfoByInternalName(superClassInternalName);
        if (superTypeInfo == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Cannot resolve superclass type info for " + superClassInternalName);
        }

        FieldLookupResult lookupResult = lookupFieldInHierarchy(superTypeInfo, fieldName);
        if (lookupResult == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Field not found in super hierarchy: " + fieldName);
        }

        code.aload(0);
        updateInstanceField(
                code,
                classWriter,
                updateExpr,
                lookupResult.ownerInternalName,
                fieldName,
                lookupResult.fieldInfo.descriptor());
    }

    private void loadOne(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER -> code.iconst(1);
            case ConstantJavaType.ABBR_LONG -> code.lconst(1L);
            case ConstantJavaType.ABBR_FLOAT -> code.fconst(1.0f);
            case ConstantJavaType.ABBR_DOUBLE -> code.dconst(1.0);
        }
    }

    private void loadPrimitive(CodeBuilder code, String primitiveType, int varIndex) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER -> code.iload(varIndex);
            case ConstantJavaType.ABBR_LONG -> code.lload(varIndex);
            case ConstantJavaType.ABBR_FLOAT -> code.fload(varIndex);
            case ConstantJavaType.ABBR_DOUBLE -> code.dload(varIndex);
        }
    }

    private FieldLookupResult lookupFieldInHierarchy(JavaTypeInfo typeInfo, String fieldName) {
        FieldInfo fieldInfo = typeInfo.getField(fieldName);
        if (fieldInfo != null) {
            return new FieldLookupResult(fieldInfo, typeInfo.getInternalName());
        }
        for (JavaTypeInfo parentInfo : typeInfo.getParentTypeInfos()) {
            FieldLookupResult result = lookupFieldInHierarchy(parentInfo, fieldName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private JavaTypeInfo resolveTypeInfoByInternalName(String internalName) {
        String qualifiedName = internalName.replace('/', '.');
        JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
        if (typeInfo == null) {
            int lastSlash = internalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
            typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        }
        return typeInfo;
    }

    private void storePrimitive(CodeBuilder code, String primitiveType, int varIndex) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER -> code.istore(varIndex);
            case ConstantJavaType.ABBR_LONG -> code.lstore(varIndex);
            case ConstantJavaType.ABBR_FLOAT -> code.fstore(varIndex);
            case ConstantJavaType.ABBR_DOUBLE -> code.dstore(varIndex);
        }
    }

    private void subtractPrimitive(CodeBuilder code, String primitiveType) {
        switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_SHORT,
                 ConstantJavaType.ABBR_CHARACTER -> code.isub();
            case ConstantJavaType.ABBR_LONG -> code.lsub();
            case ConstantJavaType.ABBR_FLOAT -> code.fsub();
            case ConstantJavaType.ABBR_DOUBLE -> code.dsub();
        }
    }

    private String unwrapType(String wrapperType) {
        return switch (wrapperType) {
            case ConstantJavaType.LJAVA_LANG_INTEGER -> ConstantJavaType.ABBR_INTEGER;
            case ConstantJavaType.LJAVA_LANG_LONG -> ConstantJavaType.ABBR_LONG;
            case ConstantJavaType.LJAVA_LANG_FLOAT -> ConstantJavaType.ABBR_FLOAT;
            case ConstantJavaType.LJAVA_LANG_DOUBLE -> ConstantJavaType.ABBR_DOUBLE;
            case ConstantJavaType.LJAVA_LANG_BYTE -> ConstantJavaType.ABBR_BYTE;
            case ConstantJavaType.LJAVA_LANG_SHORT -> ConstantJavaType.ABBR_SHORT;
            case ConstantJavaType.LJAVA_LANG_CHARACTER -> ConstantJavaType.ABBR_CHARACTER;
            default -> throw new IllegalArgumentException("Unknown wrapper type: " + wrapperType);
        };
    }

    private void updateInstanceField(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            String ownerInternalName,
            String fieldName,
            String fieldType) throws Swc4jByteCodeCompilerException {
        String primitiveType = TypeConversionUtils.getPrimitiveType(fieldType);
        boolean isPrimitive = fieldType.equals(primitiveType);
        boolean isWrapper = TypeConversionUtils.isPrimitiveType(primitiveType) && !isPrimitive;

        if (!TypeConversionUtils.isPrimitiveType(primitiveType) || ConstantJavaType.ABBR_BOOLEAN.equals(primitiveType)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to type: " + fieldType);
        }

        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        var cp = classWriter.getConstantPool();
        int fieldRef = cp.addFieldRef(ownerInternalName, fieldName, fieldType);
        code.dup();
        code.getfield(fieldRef); // [obj, old]

        CompilationContext context = compiler.getMemory().getCompilationContext();
        int tempSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                "$tempUpdate" + context.getNextTempId(), fieldType);

        if (!isPrefix) {
            if (isWrapper) {
                code.dup();
                code.astore(tempSlot);
                TypeConversionUtils.unboxWrapperType(code, classWriter, fieldType);
            } else {
                storePrimitive(code, primitiveType, tempSlot);
            }
        } else if (isWrapper) {
            TypeConversionUtils.unboxWrapperType(code, classWriter, fieldType);
        }

        if (!isPrefix) {
            if (isWrapper) {
                // already unboxed
            } else {
                loadPrimitive(code, primitiveType, tempSlot);
            }
        }

        loadOne(code, primitiveType);
        if (isIncrement) {
            addPrimitive(code, primitiveType);
        } else {
            subtractPrimitive(code, primitiveType);
        }

        if (isWrapper) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, fieldType);
            if (isPrefix) {
                code.dup_x1();
            }
            code.putfield(fieldRef);
            if (!isPrefix) {
                code.aload(tempSlot);
            }
        } else {
            if (isPrefix) {
                if (ConstantJavaType.ABBR_LONG.equals(primitiveType) || ConstantJavaType.ABBR_DOUBLE.equals(primitiveType)) {
                    code.dup2_x1();
                } else {
                    code.dup_x1();
                }
            }
            code.putfield(fieldRef);
            if (!isPrefix) {
                loadPrimitive(code, primitiveType, tempSlot);
            }
        }
    }

    private void updateStaticField(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstUpdateExpr updateExpr,
            String ownerInternalName,
            String fieldName,
            String fieldType) throws Swc4jByteCodeCompilerException {
        String primitiveType = TypeConversionUtils.getPrimitiveType(fieldType);
        boolean isPrimitive = fieldType.equals(primitiveType);
        boolean isWrapper = TypeConversionUtils.isPrimitiveType(primitiveType) && !isPrimitive;

        if (!TypeConversionUtils.isPrimitiveType(primitiveType) || ConstantJavaType.ABBR_BOOLEAN.equals(primitiveType)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), updateExpr,
                    "Cannot apply " + updateExpr.getOp().getName() + " operator to type: " + fieldType);
        }

        boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;
        boolean isPrefix = updateExpr.isPrefix();

        var cp = classWriter.getConstantPool();
        int fieldRef = cp.addFieldRef(ownerInternalName, fieldName, fieldType);
        code.getstatic(fieldRef); // [old]

        CompilationContext context = compiler.getMemory().getCompilationContext();
        int tempSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context,
                "$tempUpdate" + context.getNextTempId(), fieldType);

        if (!isPrefix) {
            if (isWrapper) {
                code.dup();
                code.astore(tempSlot);
                TypeConversionUtils.unboxWrapperType(code, classWriter, fieldType);
            } else {
                storePrimitive(code, primitiveType, tempSlot);
            }
        } else if (isWrapper) {
            TypeConversionUtils.unboxWrapperType(code, classWriter, fieldType);
        }

        if (!isPrefix) {
            if (isWrapper) {
                // already unboxed
            } else {
                loadPrimitive(code, primitiveType, tempSlot);
            }
        }

        loadOne(code, primitiveType);
        if (isIncrement) {
            addPrimitive(code, primitiveType);
        } else {
            subtractPrimitive(code, primitiveType);
        }

        if (isWrapper) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveType, fieldType);
            if (isPrefix) {
                code.dup();
            }
            code.putstatic(fieldRef);
            if (!isPrefix) {
                code.aload(tempSlot);
            }
        } else {
            if (isPrefix) {
                duplicatePrimitive(code, primitiveType);
            }
            code.putstatic(fieldRef);
            if (!isPrefix) {
                loadPrimitive(code, primitiveType, tempSlot);
            }
        }
    }

    private record FieldLookupResult(FieldInfo fieldInfo, String ownerInternalName) {
    }
}
