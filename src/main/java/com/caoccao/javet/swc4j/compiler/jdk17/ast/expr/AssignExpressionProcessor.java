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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAssignOp;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor for assignment expressions in bytecode compilation.
 */
public final class AssignExpressionProcessor extends BaseAstProcessor<Swc4jAstAssignExpr> {
    /**
     * Constructs a new assignment expression processor.
     *
     * @param compiler the bytecode compiler
     */
    public AssignExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void coerceAssignmentValue(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstAssignExpr assignExpr,
            String valueType,
            String targetType) throws Swc4jByteCodeCompilerException {
        if (valueType == null || targetType == null || valueType.equals(targetType)) {
            return;
        }

        if (TypeConversionUtils.isPrimitiveType(targetType)) {
            String primitiveValueType = TypeConversionUtils.getPrimitiveType(valueType);
            if (!TypeConversionUtils.isPrimitiveType(primitiveValueType)) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr,
                        "Cannot assign non-primitive type " + valueType + " to primitive " + targetType);
            }
            TypeConversionUtils.unboxWrapperType(code, classWriter, valueType);
            if (!primitiveValueType.equals(targetType)) {
                TypeConversionUtils.convertPrimitiveType(code, primitiveValueType, targetType);
            }
            return;
        }

        String primitiveValueType = TypeConversionUtils.getPrimitiveType(valueType);
        boolean valueIsPrimitive = TypeConversionUtils.isPrimitiveType(primitiveValueType) &&
                TypeConversionUtils.isPrimitiveType(valueType);

        if (ConstantJavaType.LJAVA_LANG_STRING.equals(targetType)) {
            if (valueIsPrimitive) {
                generateStringValueOf(code, classWriter, primitiveValueType);
            } else if (!ConstantJavaType.LJAVA_LANG_STRING.equals(valueType)) {
                generateStringValueOf(code, classWriter, ConstantJavaType.LJAVA_LANG_OBJECT);
            }
            return;
        }

        if (valueIsPrimitive) {
            String wrapperType = TypeConversionUtils.getWrapperType(primitiveValueType);
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(targetType) || targetType.equals(wrapperType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, primitiveValueType, wrapperType);
                return;
            }
            throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr,
                    "Cannot assign primitive type " + primitiveValueType + " to " + targetType);
        }

        if (isWrapperType(targetType) && isWrapperType(valueType)) {
            String fromPrimitive = TypeConversionUtils.getPrimitiveType(valueType);
            String toPrimitive = TypeConversionUtils.getPrimitiveType(targetType);
            TypeConversionUtils.unboxWrapperType(code, classWriter, valueType);
            if (!fromPrimitive.equals(toPrimitive)) {
                TypeConversionUtils.convertPrimitiveType(code, fromPrimitive, toPrimitive);
            }
            TypeConversionUtils.boxPrimitiveType(code, classWriter, toPrimitive, targetType);
        }
    }

    /**
     * Extract property name from ISwc4jAstPropName.
     *
     * @param propName the property name AST node
     * @return the extracted property name string
     * @throws Swc4jByteCodeCompilerException if the property name type is unsupported
     */
    private String extractPropertyName(ISwc4jAstPropName propName) throws Swc4jByteCodeCompilerException {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), propName,
                    "Unsupported property name type: " + propName.getClass().getName());
        }
    }

    private String extractSuperPropertyName(
            Swc4jAstSuperPropExpr superPropExpr) throws Swc4jByteCodeCompilerException {
        if (superPropExpr.getProp() instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        if (superPropExpr.getProp() instanceof Swc4jAstComputedPropName computedProp
                && computedProp.getExpr() instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        throw new Swc4jByteCodeCompilerException(
                getSourceCode(),
                superPropExpr,
                "Computed super property expressions not yet supported");
    }

    /**
     * Generates bytecode for an assignment expression.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param assignExpr     the assignment expression AST node
     * @param returnTypeInfo the expected return type information
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstAssignExpr assignExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();
        // Handle assignments like arr[1] = value or arr.length = 0
        var left = assignExpr.getLeft();
        if (left instanceof Swc4jAstSuperPropExpr superPropExpr) {
            handleSuperPropertyAssignment(code, classWriter, assignExpr, superPropExpr);
            return;
        }
        if (left instanceof Swc4jAstMemberExpr memberExpr) {
            // Handle this.field = value assignment
            if (memberExpr.getObj() instanceof Swc4jAstThisExpr && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String fieldName = propIdent.getSym();
                String currentClassName = context.getCurrentClassInternalName();

                if (currentClassName != null) {
                    // Look up the field in the class registry - try qualified name first, then simple name
                    String qualifiedName = currentClassName.replace('/', '.');
                    JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
                    if (typeInfo == null) {
                        // Try simple name
                        int lastSlash = currentClassName.lastIndexOf('/');
                        String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                        typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                    }

                    if (typeInfo != null) {
                        FieldInfo fieldInfo = typeInfo.getField(fieldName);
                        if (fieldInfo != null) {
                            // Generate: aload 0; <value>; dup_x1; putfield
                            code.aload(0); // load this
                            compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                            // Convert value type if needed
                            String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                            coerceAssignmentValue(code, classWriter, assignExpr, valueType, fieldInfo.descriptor());

                            // Duplicate value for assignment expression result (assignment returns the assigned value)
                            String fieldDesc = fieldInfo.descriptor();
                            if (ConstantJavaType.ABBR_DOUBLE.equals(fieldDesc) || ConstantJavaType.ABBR_LONG.equals(fieldDesc)) {
                                code.dup2_x1(); // For wide types (double, long)
                            } else {
                                code.dup_x1(); // For single-slot types
                            }

                            int fieldRef = cp.addFieldRef(currentClassName, fieldName, fieldInfo.descriptor());
                            code.putfield(fieldRef);
                            return;
                        }
                    }
                }
            }

            // Handle this.#field = value assignment (ES2022 private fields)
            if (memberExpr.getObj() instanceof Swc4jAstThisExpr && memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
                String fieldName = privateName.getName(); // Name without # prefix
                String currentClassName = context.getCurrentClassInternalName();

                if (currentClassName != null) {
                    String qualifiedName = currentClassName.replace('/', '.');
                    JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
                    if (typeInfo == null) {
                        int lastSlash = currentClassName.lastIndexOf('/');
                        String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                        typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                    }

                    if (typeInfo != null) {
                        FieldInfo fieldInfo = typeInfo.getField(fieldName);
                        if (fieldInfo != null) {
                            code.aload(0); // load this
                            compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                            // Convert value type if needed
                            String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                            coerceAssignmentValue(code, classWriter, assignExpr, valueType, fieldInfo.descriptor());

                            // Duplicate value for assignment expression result
                            String fieldDesc = fieldInfo.descriptor();
                            if (ConstantJavaType.ABBR_DOUBLE.equals(fieldDesc) || ConstantJavaType.ABBR_LONG.equals(fieldDesc)) {
                                code.dup2_x1();
                            } else {
                                code.dup_x1();
                            }

                            int fieldRef = cp.addFieldRef(currentClassName, fieldName, fieldInfo.descriptor());
                            code.putfield(fieldRef);
                            return;
                        }
                    }
                }
            }

            // Handle ClassName.staticField = value assignment
            if (memberExpr.getObj() instanceof Swc4jAstIdent classIdent && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String className = classIdent.getSym();
                String fieldName = propIdent.getSym();

                // Try to resolve the class
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
                if (typeInfo != null) {
                    FieldInfo fieldInfo = typeInfo.getField(fieldName);
                    if (fieldInfo != null && fieldInfo.isStatic()) {
                        // Generate: <value>; dup; putstatic
                        compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                        // Convert value type if needed
                        String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                        coerceAssignmentValue(code, classWriter, assignExpr, valueType, fieldInfo.descriptor());

                        // Duplicate value for assignment expression result
                        String fieldDesc = fieldInfo.descriptor();
                        if (ConstantJavaType.ABBR_DOUBLE.equals(fieldDesc) || ConstantJavaType.ABBR_LONG.equals(fieldDesc)) {
                            code.dup2(); // For wide types (double, long)
                        } else {
                            code.dup(); // For single-slot types
                        }

                        int fieldRef = cp.addFieldRef(typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
                        code.putstatic(fieldRef);
                        return;
                    }
                }
            }

            // Handle ClassName.#staticField = value assignment for ES2022 static private fields
            if (memberExpr.getObj() instanceof Swc4jAstIdent classIdent && memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
                String className = classIdent.getSym();
                String fieldName = privateName.getName(); // Name without # prefix

                // Try to resolve the class
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
                if (typeInfo != null) {
                    FieldInfo fieldInfo = typeInfo.getField(fieldName);
                    if (fieldInfo != null && fieldInfo.isStatic()) {
                        // Generate: <value>; dup; putstatic
                        compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                        // Convert value type if needed
                        String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                        coerceAssignmentValue(code, classWriter, assignExpr, valueType, fieldInfo.descriptor());

                        // Duplicate value for assignment expression result
                        String fieldDesc = fieldInfo.descriptor();
                        if (ConstantJavaType.ABBR_DOUBLE.equals(fieldDesc) || ConstantJavaType.ABBR_LONG.equals(fieldDesc)) {
                            code.dup2(); // For wide types (double, long)
                        } else {
                            code.dup(); // For single-slot types
                        }

                        int fieldRef = cp.addFieldRef(typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
                        code.putstatic(fieldRef);
                        return;
                    }
                }
            }

            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

            if (objType != null && objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value - array element assignment
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [array]
                    compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [array, index]

                    // Convert index to int if needed
                    String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
                        TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
                    }

                    // Generate the value to store
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null); // Stack: [array, index, value]

                    // Unbox if needed
                    TypeConversionUtils.unboxWrapperType(code, classWriter, valueType);

                    // Convert to target element type if needed
                    String elemType = objType.substring(1); // Remove leading ConstantJavaType.ARRAY_PREFIX
                    String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);
                    TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, elemType);

                    // Duplicate value and place it below array and index so it's left after store
                    // Stack: [array, index, value] -> [value, array, index, value]
                    if (ConstantJavaType.ABBR_DOUBLE.equals(elemType) || ConstantJavaType.ABBR_LONG.equals(elemType)) {
                        code.dup2_x2(); // For wide types (double, long)
                    } else {
                        code.dup_x2(); // For single-slot types
                    }

                    // Use appropriate array store instruction
                    // Stack: [value, array, index, value] -> [value] after store
                    switch (elemType) {
                        case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE ->
                                code.bastore(); // boolean and byte
                        case ConstantJavaType.ABBR_CHARACTER -> code.castore(); // char
                        case ConstantJavaType.ABBR_SHORT -> code.sastore(); // short
                        case ConstantJavaType.ABBR_INTEGER -> code.iastore(); // int
                        case ConstantJavaType.ABBR_LONG -> code.lastore(); // long
                        case ConstantJavaType.ABBR_FLOAT -> code.fastore(); // float
                        case ConstantJavaType.ABBR_DOUBLE -> code.dastore(); // double
                        default -> code.aastore(); // reference types
                    }
                    // The duplicated value is now on the stack as the assignment result
                    return;
                }

                // Check if it's arr.length = newLength - NOT SUPPORTED for Java arrays
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr, "Cannot set length on Java array - array size is fixed");
                    }
                }
            } else if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType) || ConstantJavaType.LJAVA_UTIL_LIST.equals(objType)) {
                // Check if it's arr[index] = value
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // arr[index] = value -> arr.set(index, value)
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                    compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [ArrayList/List, index]

                    // Convert index to int if it's a String (for-in returns string indices in JS semantics)
                    String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if (ConstantJavaType.LJAVA_LANG_STRING.equals(indexType)) {
                        // String index -> Integer.parseInt(index)
                        int parseIntMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_PARSE_INT, ConstantJavaDescriptor.LJAVA_LANG_STRING__I);
                        code.invokestatic(parseIntMethod); // Stack: [ArrayList/List, int]
                    }

                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null); // Stack: [ArrayList/List, index, value]

                    // Box value if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    if (TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        // wrapperType is already in the form ConstantJavaType.LJAVA_LANG_INTEGER so use it directly
                        String className = TypeConversionUtils.descriptorToInternalName(wrapperType);
                        int valueOfRef = cp.addMethodRef(className, ConstantJavaMethod.METHOD_VALUE_OF, "(" + valueType + ")" + wrapperType);
                        code.invokestatic(valueOfRef); // Stack: [ArrayList/List, index, boxedValue]
                    }

                    // Call List.set(int, Object) via interface method
                    int setMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SET, ConstantJavaDescriptor.I_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                    code.invokeinterface(setMethod, 3); // Stack: [oldValue] - the return value of set() is the previous value
                    // Leave the value on stack for expression statements to pop
                    return;
                }

                // Check if it's arr.length = newLength
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        // arr.length = newLength
                        // Special case: arr.length = 0 -> arr.clear()
                        if (assignExpr.getRight() instanceof Swc4jAstNumber number && number.getValue() == 0.0) {
                            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [List]
                            int clearMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, "clear", ConstantJavaDescriptor.__V);
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
                            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [List]
                            code.dup(); // Stack: [List, List] - keep one for potential use
                            code.iconst(newLength); // Stack: [List, List, newLength]

                            // Get arr.size() - need to load List again
                            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [List, List, newLength, List]
                            int sizeMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
                            code.invokeinterface(sizeMethod, 1); // Stack: [List, List, newLength, size]

                            // Call subList(newLength, size) on the second List
                            int subListMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, "subList", ConstantJavaDescriptor.I_I__LJAVA_UTIL_LIST);
                            code.invokeinterface(subListMethod, 3); // Stack: [List, List]

                            // Call clear() on the returned subList
                            int clearMethod2 = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, "clear", ConstantJavaDescriptor.__V);
                            code.invokeinterface(clearMethod2, 1); // Stack: [List]

                            // Assignment expression returns the assigned value (newLength), not the List
                            code.pop(); // Pop the List we kept, Stack: []
                            code.iconst(newLength); // Stack: [newLength]
                            return;
                        }

                        // For non-constant expressions, we need more complex handling
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr, "Setting array length to non-constant values not yet supported");
                    }
                }
            } else if (ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP.equals(objType)) {
                // LinkedHashMap operations (object literal property assignment)
                // Check if it's obj[key] = value (computed property)
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                    // obj[key] = value -> map.put(key, value)
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [LinkedHashMap, key]

                    // Box primitive keys if needed
                    String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                    if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, keyType, wrapperType);
                    }

                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null); // Stack: [LinkedHashMap, key, value]

                    // Box primitive values if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, wrapperType);
                    }

                    // Call LinkedHashMap.put(Object, Object)
                    int putMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, "put", ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                    code.invokevirtual(putMethod); // Stack: [oldValue] - the return value is the previous value (or null)
                    // Leave the value on stack for expression statements to pop
                    return;
                }

                // Check if it's obj.prop = value (named property)
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    // obj.prop = value -> map.put("prop", value)
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [LinkedHashMap]
                    int keyIndex = cp.addString(propName);
                    code.ldc(keyIndex); // Stack: [LinkedHashMap, "prop"]

                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null); // Stack: [LinkedHashMap, "prop", value]

                    // Box primitive values if needed
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, wrapperType);
                    }

                    // Call LinkedHashMap.put(Object, Object)
                    int putMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, "put", ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                    code.invokevirtual(putMethod); // Stack: [oldValue]
                    // Leave the value on stack for expression statements to pop
                    return;
                }
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Variable assignment: x = value or compound like x += value
            String varName = bindingIdent.getId().getSym();

            // First check if it's a captured variable (inside a lambda)
            var capturedVar = context.getCapturedVariable(varName);
            if (capturedVar != null) {
                generateCapturedVariableAssignment(code, classWriter, context, assignExpr, capturedVar);
                return;
            }

            LocalVariable var = context.getLocalVariableTable().getVariable(varName);

            if (var == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr, "Undefined variable: " + varName);
            }

            String varType = var.type();
            Swc4jAstAssignOp op = assignExpr.getOp();

            // Check if variable uses holder (for mutable captures)
            if (var.needsHolder()) {
                // Store into holder array: holder[0] = value
                code.aload(var.holderIndex());
                code.iconst(0);

                if (op == Swc4jAstAssignOp.Assign) {
                    // Simple assignment
                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);
                } else {
                    // Compound assignment - load current value first
                    code.aload(var.holderIndex());
                    code.iconst(0);
                    switch (varType) {
                        case ConstantJavaType.ABBR_INTEGER -> code.iaload();
                        case ConstantJavaType.ABBR_LONG -> code.laload();
                        case ConstantJavaType.ABBR_FLOAT -> code.faload();
                        case ConstantJavaType.ABBR_DOUBLE -> code.daload();
                        case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload();
                        case ConstantJavaType.ABBR_CHARACTER -> code.caload();
                        case ConstantJavaType.ABBR_SHORT -> code.saload();
                        default -> code.aaload();
                    }

                    compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                    String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                    coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);

                    // Perform operation
                    generateCompoundOperation(code, classWriter, op, varType);
                }

                // Duplicate for expression result, then store
                if (ConstantJavaType.ABBR_DOUBLE.equals(varType) || ConstantJavaType.ABBR_LONG.equals(varType)) {
                    code.dup2_x2();
                } else {
                    code.dup_x2();
                }

                // Store into holder array
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.iastore();
                    case ConstantJavaType.ABBR_LONG -> code.lastore();
                    case ConstantJavaType.ABBR_FLOAT -> code.fastore();
                    case ConstantJavaType.ABBR_DOUBLE -> code.dastore();
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore();
                    case ConstantJavaType.ABBR_CHARACTER -> code.castore();
                    case ConstantJavaType.ABBR_SHORT -> code.sastore();
                    default -> code.aastore();
                }
                return;
            }

            if (op == Swc4jAstAssignOp.Assign) {
                // Simple assignment: x = value
                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);
            } else {
                // Compound assignment: x += value, x -= value, etc.
                // Load current value of variable
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN,
                         ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_CHARACTER,
                         ConstantJavaType.ABBR_SHORT -> code.iload(var.index());
                    case ConstantJavaType.ABBR_LONG -> code.lload(var.index());
                    case ConstantJavaType.ABBR_FLOAT -> code.fload(var.index());
                    case ConstantJavaType.ABBR_DOUBLE -> code.dload(var.index());
                    default -> code.aload(var.index());
                }

                // Generate the right-hand side expression
                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

                // Convert to the variable's type if needed
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());

                // Special handling for String += (need to convert value to String)
                if (ConstantJavaType.LJAVA_LANG_STRING.equals(varType) && op == Swc4jAstAssignOp.AddAssign) {
                    // Convert value to String using String.valueOf() if it's not already a String
                    if (!ConstantJavaType.LJAVA_LANG_STRING.equals(valueType)) {
                        String valueOfDescriptor = switch (valueType) {
                            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaDescriptor.I__LJAVA_LANG_STRING;
                            case ConstantJavaType.ABBR_LONG -> ConstantJavaDescriptor.J__LJAVA_LANG_STRING;
                            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaDescriptor.F__LJAVA_LANG_STRING;
                            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaDescriptor.D__LJAVA_LANG_STRING;
                            case ConstantJavaType.ABBR_BOOLEAN -> ConstantJavaDescriptor.Z__LJAVA_LANG_STRING;
                            case ConstantJavaType.ABBR_CHARACTER -> ConstantJavaDescriptor.C__LJAVA_LANG_STRING;
                            default -> ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_STRING;
                        };
                        int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF, valueOfDescriptor);
                        code.invokestatic(valueOfRef);
                    }
                } else if (valueType != null && !valueType.equals(varType)) {
                    coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);
                }

                // Perform the operation based on the compound operator
                generateCompoundOperation(code, classWriter, op, varType);
            }

            // Duplicate the value on the stack before storing (assignment returns the value)
            if (ConstantJavaType.ABBR_DOUBLE.equals(varType) || ConstantJavaType.ABBR_LONG.equals(varType)) {
                code.dup2(); // For wide types (double, long)
            } else {
                code.dup(); // For single-slot types
            }

            // Store into local variable
            switch (varType) {
                case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE,
                     ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_SHORT -> code.istore(var.index());
                case ConstantJavaType.ABBR_LONG -> code.lstore(var.index());
                case ConstantJavaType.ABBR_FLOAT -> code.fstore(var.index());
                case ConstantJavaType.ABBR_DOUBLE -> code.dstore(var.index());
                default -> code.astore(var.index()); // Reference types
            }
            // The duplicated value is now on the stack as the assignment result
            return;
        } else if (left instanceof Swc4jAstArrayPat arrayPat) {
            // Array destructuring assignment: [a, ...rest] = newArray;
            generateArrayPatternAssign(code, classWriter, context, assignExpr, arrayPat);
            return;
        } else if (left instanceof Swc4jAstObjectPat objectPat) {
            // Object destructuring assignment: { x, ...rest } = newObject;
            generateObjectPatternAssign(code, classWriter, context, assignExpr, objectPat);
            return;
        }
        throw new Swc4jByteCodeCompilerException(getSourceCode(), assignExpr, "Assignment expression not yet supported: " + left);
    }

    /**
     * Generate bytecode for array pattern assignment with destructuring.
     * Example: [a, b, ...rest] = newArray;
     */
    private void generateArrayPatternAssign(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstAssignExpr assignExpr,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Generate the right-hand side expression and store in a temp variable
        compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
        int listClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LIST);
        code.checkcast(listClass);
        int tempListSlot = getOrAllocateTempSlot(context, "$tempList", ConstantJavaType.LJAVA_UTIL_LIST);
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT);
        int listSizeRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
        int listAddRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
        int listClearRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, "clear", ConstantJavaDescriptor.__V);

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
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent,
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
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent,
                                "Undefined variable in array destructuring rest assignment: " + varName);
                    }

                    // Clear the existing ArrayList and repopulate it
                    // rest.clear()
                    code.aload(localVar.index());
                    code.invokeinterface(listClearRef, 1);

                    // Get source list size
                    code.aload(tempListSlot);
                    code.invokeinterface(listSizeRef, 1);
                    int sizeSlot = getOrAllocateTempSlot(context, "$restSize", ConstantJavaType.ABBR_INTEGER);
                    code.istore(sizeSlot);

                    // Initialize loop counter at restStartIndex
                    code.iconst(restStartIndex);
                    int iSlot = getOrAllocateTempSlot(context, "$restI", ConstantJavaType.ABBR_INTEGER);
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
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            }
        }

        // Leave the source array on the stack as the expression result
        code.aload(tempListSlot);
    }

    /**
     * Generate bytecode for assignment to a captured variable (inside lambda).
     * Captured holder variables store values in holder[0].
     */
    private void generateCapturedVariableAssignment(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstAssignExpr assignExpr,
            com.caoccao.javet.swc4j.compiler.memory.CapturedVariable capturedVar) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        String currentClass = context.getCurrentClassInternalName();
        int fieldRef = cp.addFieldRef(currentClass, capturedVar.fieldName(), capturedVar.type());
        Swc4jAstAssignOp op = assignExpr.getOp();
        String varType = capturedVar.originalType();

        if (capturedVar.isHolder()) {
            // Holder-based capture: store into holder[0]
            code.aload(0);  // Load 'this' (lambda instance)
            code.getfield(fieldRef);  // Get the holder array
            code.iconst(0);  // Array index 0

            if (op == Swc4jAstAssignOp.Assign) {
                // Simple assignment
                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);
            } else {
                // Compound assignment - load current value first
                code.aload(0);
                code.getfield(fieldRef);
                code.iconst(0);
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.iaload();
                    case ConstantJavaType.ABBR_LONG -> code.laload();
                    case ConstantJavaType.ABBR_FLOAT -> code.faload();
                    case ConstantJavaType.ABBR_DOUBLE -> code.daload();
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload();
                    case ConstantJavaType.ABBR_CHARACTER -> code.caload();
                    case ConstantJavaType.ABBR_SHORT -> code.saload();
                    default -> code.aaload();
                }

                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);

                // Perform operation
                generateCompoundOperation(code, classWriter, op, varType);
            }

            // Duplicate for expression result, then store into holder array
            if (ConstantJavaType.ABBR_DOUBLE.equals(varType) || ConstantJavaType.ABBR_LONG.equals(varType)) {
                code.dup2_x2();
            } else {
                code.dup_x2();
            }

            // Store into holder array
            switch (varType) {
                case ConstantJavaType.ABBR_INTEGER -> code.iastore();
                case ConstantJavaType.ABBR_LONG -> code.lastore();
                case ConstantJavaType.ABBR_FLOAT -> code.fastore();
                case ConstantJavaType.ABBR_DOUBLE -> code.dastore();
                case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore();
                case ConstantJavaType.ABBR_CHARACTER -> code.castore();
                case ConstantJavaType.ABBR_SHORT -> code.sastore();
                default -> code.aastore();
            }
        } else {
            // Non-holder capture: just update the field directly
            // (This should be rare since non-holder captures are typically immutable)
            code.aload(0);  // Load 'this' (lambda instance)

            if (op == Swc4jAstAssignOp.Assign) {
                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                coerceAssignmentValue(code, classWriter, assignExpr, valueType, varType);
            } else {
                // Compound assignment
                code.dup();  // Keep 'this' for putfield
                code.getfield(fieldRef);  // Get current value

                compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
                if (valueType != null && !valueType.equals(varType)) {
                    TypeConversionUtils.unboxWrapperType(code, classWriter, valueType);
                    String valuePrimitive = TypeConversionUtils.getPrimitiveType(valueType);
                    if (valuePrimitive != null && !valuePrimitive.equals(varType)) {
                        TypeConversionUtils.convertPrimitiveType(code, valuePrimitive, varType);
                    }
                }

                generateCompoundOperation(code, classWriter, op, varType);
            }

            // Duplicate for expression result, then store
            if (ConstantJavaType.ABBR_DOUBLE.equals(varType) || ConstantJavaType.ABBR_LONG.equals(varType)) {
                code.dup2_x1();
            } else {
                code.dup_x1();
            }

            code.putfield(fieldRef);
        }
    }

    /**
     * Generate compound operation bytecode (e.g., iadd, isub, etc.)
     */
    private void generateCompoundOperation(CodeBuilder code, ClassWriter classWriter, Swc4jAstAssignOp op, String varType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        switch (op) {
            case AddAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.iadd();
                    case ConstantJavaType.ABBR_LONG -> code.ladd();
                    case ConstantJavaType.ABBR_FLOAT -> code.fadd();
                    case ConstantJavaType.ABBR_DOUBLE -> code.dadd();
                    case ConstantJavaType.LJAVA_LANG_STRING -> {
                        int concatMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_CONCAT, ConstantJavaDescriptor.LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                        code.invokevirtual(concatMethod);
                    }
                }
            }
            case SubAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.isub();
                    case ConstantJavaType.ABBR_LONG -> code.lsub();
                    case ConstantJavaType.ABBR_FLOAT -> code.fsub();
                    case ConstantJavaType.ABBR_DOUBLE -> code.dsub();
                }
            }
            case MulAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.imul();
                    case ConstantJavaType.ABBR_LONG -> code.lmul();
                    case ConstantJavaType.ABBR_FLOAT -> code.fmul();
                    case ConstantJavaType.ABBR_DOUBLE -> code.dmul();
                }
            }
            case DivAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.idiv();
                    case ConstantJavaType.ABBR_LONG -> code.ldiv();
                    case ConstantJavaType.ABBR_FLOAT -> code.fdiv();
                    case ConstantJavaType.ABBR_DOUBLE -> code.ddiv();
                }
            }
            case ModAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.irem();
                    case ConstantJavaType.ABBR_LONG -> code.lrem();
                    case ConstantJavaType.ABBR_FLOAT -> code.frem();
                    case ConstantJavaType.ABBR_DOUBLE -> code.drem();
                }
            }
            case BitAndAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.iand();
                    case ConstantJavaType.ABBR_LONG -> code.land();
                }
            }
            case BitOrAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.ior();
                    case ConstantJavaType.ABBR_LONG -> code.lor();
                }
            }
            case BitXorAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.ixor();
                    case ConstantJavaType.ABBR_LONG -> code.lxor();
                }
            }
            case LShiftAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.ishl();
                    case ConstantJavaType.ABBR_LONG -> code.lshl();
                }
            }
            case RShiftAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.ishr();
                    case ConstantJavaType.ABBR_LONG -> code.lshr();
                }
            }
            case ZeroFillRShiftAssign -> {
                switch (varType) {
                    case ConstantJavaType.ABBR_INTEGER -> code.iushr();
                    case ConstantJavaType.ABBR_LONG -> code.lushr();
                }
            }
            default -> {
                // Not a compound operation (simple assignment)
            }
        }
    }

    /**
     * Generate bytecode for object pattern assignment with destructuring.
     * Example: { a, b, ...rest } = newObject;
     */
    private void generateObjectPatternAssign(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstAssignExpr assignExpr,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Generate the right-hand side expression and store in a temp variable
        compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);
        int mapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP);
        code.checkcast(mapClass);
        int tempMapSlot = getOrAllocateTempSlot(context, "$tempMap", ConstantJavaType.LJAVA_UTIL_MAP);
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        int mapRemoveRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        int mapClearRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, "clear", ConstantJavaDescriptor.__V);
        int mapPutAllRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, "putAll", ConstantJavaDescriptor.LJAVA_UTIL_MAP__V);

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
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), assignProp,
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

                    compiler.getExpressionProcessor().generate(code, classWriter, assignProp.getValue().get(), null);
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
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent,
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
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent,
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
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            }
        }

        // Leave the source map on the stack as the expression result
        code.aload(tempMapSlot);
    }

    private void generateStringValueOf(CodeBuilder code, ClassWriter classWriter, String valueType) {
        var cp = classWriter.getConstantPool();
        String descriptor = switch (valueType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaDescriptor.I__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaDescriptor.J__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaDescriptor.F__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaDescriptor.D__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_BOOLEAN -> ConstantJavaDescriptor.Z__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_CHARACTER -> ConstantJavaDescriptor.C__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_BYTE -> ConstantJavaDescriptor.B__LJAVA_LANG_STRING;
            case ConstantJavaType.ABBR_SHORT -> ConstantJavaDescriptor.S__LJAVA_LANG_STRING;
            default -> ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_STRING;
        };
        int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF, descriptor);
        code.invokestatic(valueOfRef);
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

    private void handleSuperPropertyAssignment(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstAssignExpr assignExpr,
            Swc4jAstSuperPropExpr superPropExpr) throws Swc4jByteCodeCompilerException {
        String fieldName = extractSuperPropertyName(superPropExpr);
        CompilationContext context = compiler.getMemory().getCompilationContext();
        String currentClassName = context.getCurrentClassInternalName();
        if (currentClassName == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "super property assignment outside of class context");
        }

        String superClassInternalName = resolveSuperClassInternalName(currentClassName);
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

        FieldLookupResult fieldLookupResult = lookupFieldInHierarchy(superTypeInfo, fieldName);
        if (fieldLookupResult == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Field not found in super hierarchy: " + fieldName);
        }

        var cp = classWriter.getConstantPool();
        String fieldDescriptor = fieldLookupResult.fieldInfo.descriptor();
        code.aload(0);
        compiler.getExpressionProcessor().generate(code, classWriter, assignExpr.getRight(), null);

        String valueType = compiler.getTypeResolver().inferTypeFromExpr(assignExpr.getRight());
        coerceAssignmentValue(code, classWriter, assignExpr, valueType, fieldDescriptor);

        if (ConstantJavaType.ABBR_DOUBLE.equals(fieldDescriptor) || ConstantJavaType.ABBR_LONG.equals(fieldDescriptor)) {
            code.dup2_x1();
        } else {
            code.dup_x1();
        }

        int fieldRef = cp.addFieldRef(fieldLookupResult.ownerInternalName, fieldName, fieldDescriptor);
        code.putfield(fieldRef);
    }

    private boolean isWrapperType(String type) {
        if (type == null) {
            return false;
        }
        String primitive = TypeConversionUtils.getPrimitiveType(type);
        return !type.equals(primitive) && TypeConversionUtils.isPrimitiveType(primitive);
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

    private String resolveSuperClassInternalName(String currentClassInternalName) {
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0
                    ? currentClassInternalName.substring(lastSlash + 1)
                    : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        return superClassInternalName;
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

    private record FieldLookupResult(FieldInfo fieldInfo, String ownerInternalName) {
    }
}
