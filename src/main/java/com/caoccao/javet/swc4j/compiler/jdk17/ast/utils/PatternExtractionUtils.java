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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating bytecode for array and object pattern extraction (destructuring).
 */
public final class PatternExtractionUtils {

    private PatternExtractionUtils() {
    }

    /**
     * Allocate variables for nested patterns recursively.
     *
     * @param context     compilation context
     * @param pat         the pattern to allocate variables for
     * @param elementType the type descriptor for elements
     */
    private static void allocateNestedPatternVariables(CompilationContext context, ISwc4jAstPat pat, String elementType) {
        if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
            allocateVariableIfNeeded(context, bindingIdent.getId().getSym(), elementType);
        } else if (pat instanceof Swc4jAstArrayPat arrayPat) {
            for (var optElem : arrayPat.getElems()) {
                if (optElem.isPresent()) {
                    ISwc4jAstPat elem = optElem.get();
                    if (elem instanceof Swc4jAstRestPat restPat) {
                        allocateRestPatternVariable(context, restPat, true);
                    } else {
                        allocateNestedPatternVariables(context, elem, elementType);
                    }
                }
            }
        } else if (pat instanceof Swc4jAstObjectPat objectPat) {
            for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
                if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                    allocateVariableIfNeeded(context, assignProp.getKey().getId().getSym(), elementType);
                } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                    allocateNestedPatternVariables(context, keyValueProp.getValue(), elementType);
                } else if (prop instanceof Swc4jAstRestPat restPat) {
                    allocateRestPatternVariable(context, restPat, false);
                }
            }
        }
    }

    /**
     * Allocate variable for rest pattern.
     */
    private static void allocateRestPatternVariable(CompilationContext context, Swc4jAstRestPat restPat, boolean isArrayRest) {
        ISwc4jAstPat arg = restPat.getArg();
        if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
            String varName = bindingIdent.getId().getSym();
            String varType = isArrayRest ? ConstantJavaType.LJAVA_UTIL_ARRAYLIST : ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
            allocateVariableIfNeeded(context, varName, varType);
        }
    }

    /**
     * Allocate variable if it doesn't exist.
     */
    private static void allocateVariableIfNeeded(CompilationContext context, String varName, String varType) {
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        if (localVar == null) {
            localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
        }
        if (localVar == null) {
            context.getLocalVariableTable().allocateVariable(varName, varType);
            context.getInferredTypes().put(varName, varType);
        }
    }

    /**
     * Extract element type from a parameter's type annotation.
     * For List&lt;int&gt;, returns "I". For Map&lt;String, int&gt;, returns "I" (value type).
     *
     * @param compiler the bytecode compiler
     * @param param    the parameter pattern
     * @return element type descriptor, or "Ljava/lang/Object;" if cannot determine
     */
    private static String extractElementType(ByteCodeCompiler compiler, ISwc4jAstPat param) {
        if (param instanceof Swc4jAstArrayPat arrayPat) {
            // Array pattern - check type annotation
            var typeAnn = arrayPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                return extractTypeFromAnnotation(compiler, typeAnn.get().getTypeAnn(), true);
            }
        } else if (param instanceof Swc4jAstObjectPat objectPat) {
            // Object pattern - check type annotation
            var typeAnn = objectPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                return extractTypeFromAnnotation(compiler, typeAnn.get().getTypeAnn(), false);
            }
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Extract element/value type from a type annotation.
     *
     * @param compiler the bytecode compiler
     * @param tsType   the TypeScript type
     * @param isArray  true for List (returns element type), false for Map (returns value type)
     * @return type descriptor
     */
    private static String extractTypeFromAnnotation(ByteCodeCompiler compiler, ISwc4jAstTsType tsType, boolean isArray) {
        if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
            var typeParams = typeRef.getTypeParams();
            if (typeParams.isPresent()) {
                var params = typeParams.get().getParams();
                if (!params.isEmpty()) {
                    // For List<T>, take first param. For Map<K,V>, take second param (value type)
                    int index = isArray ? 0 : (params.size() > 1 ? 1 : 0);
                    if (index < params.size()) {
                        try {
                            return compiler.getTypeResolver().mapTsTypeToDescriptor(params.get(index));
                        } catch (Exception e) {
                            // Fall through to default
                        }
                    }
                }
            }
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Generate bytecode for array pattern extraction from a value already on the stack (parameter).
     *
     * @param compiler    the bytecode compiler
     * @param code        the code builder
     * @param classWriter the class writer
     * @param context     the compilation context
     * @param arrayPat    the array pattern to extract
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public static void generateArrayPatternExtraction(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        int listClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LIST);
        code.checkcast(listClass);
        int tempListSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$tempList" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_LIST);
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT);
        int listSizeRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
        int listAddRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);

        // Extract element type from the array pattern's type annotation
        String elementType = extractElementType(compiler, arrayPat);

        // Allocate variables for all elements including rest pattern
        int restStartIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (!optElem.isPresent()) {
                restStartIndex++;
                continue;
            }
            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                allocateVariableIfNeeded(context, varName, elementType);
                restStartIndex++;
            } else if (elem instanceof Swc4jAstArrayPat || elem instanceof Swc4jAstObjectPat) {
                // Nested pattern - allocate variables recursively
                allocateNestedPatternVariables(context, elem, elementType);
                restStartIndex++;
            } else if (elem instanceof Swc4jAstRestPat restPat) {
                allocateRestPatternVariable(context, restPat, true);
                break;
            }
        }

        // Extract elements
        int currentIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (!optElem.isPresent()) {
                currentIndex++;
                continue;
            }
            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                // list.get(index)
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);

                // Add type conversion/unboxing if needed
                generateUnboxingIfNeeded(code, classWriter, elementType);
                CodeGeneratorUtils.storeVariable(code, localVar.index(), elementType);
                currentIndex++;

            } else if (elem instanceof Swc4jAstArrayPat nestedArrayPat) {
                // Nested array pattern: [a, [b, ...inner], ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested array pattern
                PatternExtractionUtils.generateArrayPatternExtraction(compiler, code, classWriter, context, nestedArrayPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstObjectPat nestedObjectPat) {
                // Nested object pattern: [a, {b, ...inner}, ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested object pattern
                PatternExtractionUtils.generateObjectPatternExtraction(compiler, code, classWriter, context, nestedObjectPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                generateRestPatternExtraction(compiler, code, classWriter, context, restPat, tempListSlot, restStartIndex, true, listGetRef, listSizeRef, listAddRef);
            }
        }
    }

    /**
     * Generate bytecode for object pattern extraction from a value already on the stack (parameter).
     *
     * @param compiler    the bytecode compiler
     * @param code        the code builder
     * @param classWriter the class writer
     * @param context     the compilation context
     * @param objectPat   the object pattern to extract
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public static void generateObjectPatternExtraction(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        int mapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP);
        code.checkcast(mapClass);
        int tempMapSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$tempMap" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_MAP);
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        int mapRemoveRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);

        // Extract value type from the object pattern's type annotation (Map<K, V> -> V)
        String valueType = extractElementType(compiler, objectPat);

        List<String> extractedKeys = new ArrayList<>();

        // First pass: allocate variables and collect extracted keys
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
                allocateVariableIfNeeded(context, varName, valueType);
            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = AstUtils.extractPropertyName(keyValueProp.getKey());
                extractedKeys.add(keyName);
                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    allocateVariableIfNeeded(context, bindingIdent.getId().getSym(), valueType);
                } else if (valuePat instanceof Swc4jAstArrayPat || valuePat instanceof Swc4jAstObjectPat) {
                    // Nested pattern - allocate variables recursively
                    allocateNestedPatternVariables(context, valuePat, valueType);
                }
            } else if (prop instanceof Swc4jAstRestPat restPat) {
                allocateRestPatternVariable(context, restPat, false);
            }
        }

        // Second pass: extract values
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                // Load map and call get(key)
                code.aload(tempMapSlot);
                int keyRef = cp.addString(varName);
                code.ldc(keyRef);
                code.invokeinterface(mapGetRef, 2);

                // Add type conversion/unboxing if needed
                generateUnboxingIfNeeded(code, classWriter, valueType);
                CodeGeneratorUtils.storeVariable(code, localVar.index(), valueType);

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = AstUtils.extractPropertyName(keyValueProp.getKey());
                ISwc4jAstPat valuePat = keyValueProp.getValue();

                // Load map and call get(key)
                code.aload(tempMapSlot);
                int keyRef = cp.addString(keyName);
                code.ldc(keyRef);
                code.invokeinterface(mapGetRef, 2);

                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                    // Add type conversion/unboxing if needed
                    generateUnboxingIfNeeded(code, classWriter, valueType);
                    CodeGeneratorUtils.storeVariable(code, localVar.index(), valueType);
                } else if (valuePat instanceof Swc4jAstArrayPat nestedArrayPat) {
                    // Nested array pattern: { arr: [a, ...rest] }
                    PatternExtractionUtils.generateArrayPatternExtraction(compiler, code, classWriter, context, nestedArrayPat);
                } else if (valuePat instanceof Swc4jAstObjectPat nestedObjectPat) {
                    // Nested object pattern: { nested: { y, ...rest } }
                    PatternExtractionUtils.generateObjectPatternExtraction(compiler, code, classWriter, context, nestedObjectPat);
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                generateObjectRestExtraction(compiler, code, classWriter, context, restPat, tempMapSlot, extractedKeys, mapRemoveRef);
            }
        }
    }

    /**
     * Generate bytecode for object rest pattern extraction {...rest}.
     */
    private static void generateObjectRestExtraction(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int tempMapSlot,
            List<String> extractedKeys,
            int mapRemoveRef) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        String sourceCode = compiler.getMemory().getScopedSourceCode().getSourceCode();

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(sourceCode, restPat, "Rest pattern argument must be a binding identifier");
        }

        String restVarName = bindingIdent.getId().getSym();
        LocalVariable restVar = context.getLocalVariableTable().getVariable(restVarName);

        // Create a new LinkedHashMap for the rest object
        int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
        code.newInstance(linkedHashMapClass);
        code.dup();
        int linkedHashMapInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
        code.invokespecial(linkedHashMapInitRef);
        int restMapSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$restMap" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP);
        code.astore(restMapSlot);

        // Copy all entries from original map except extracted keys
        int mapEntrySetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_ENTRY_SET, ConstantJavaDescriptor.__LJAVA_UTIL_SET);
        int setIteratorRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_SET, ConstantJavaMethod.METHOD_ITERATOR, ConstantJavaDescriptor.__LJAVA_UTIL_ITERATOR);
        int iteratorHasNextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_HAS_NEXT, ConstantJavaDescriptor.__Z);
        int iteratorNextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_NEXT, ConstantJavaDescriptor.__LJAVA_LANG_OBJECT);
        int entryGetKeyRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP_ENTRY, ConstantJavaMethod.METHOD_GET_KEY, ConstantJavaDescriptor.__LJAVA_LANG_OBJECT);
        int entryGetValueRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP_ENTRY, ConstantJavaMethod.METHOD_GET_VALUE, ConstantJavaDescriptor.__LJAVA_LANG_OBJECT);
        int mapPutRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, "put", ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        int objectEqualsRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_OBJECT, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);

        // Get iterator from original map's entry set
        code.aload(tempMapSlot);
        code.invokeinterface(mapEntrySetRef, 1);
        code.invokeinterface(setIteratorRef, 1);
        int iteratorSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$iterator" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_ITERATOR);
        code.astore(iteratorSlot);

        // Loop through entries
        int loopStart = code.getCurrentOffset();
        code.aload(iteratorSlot);
        code.invokeinterface(iteratorHasNextRef, 1);
        code.ifeq(0); // Placeholder
        int loopExitPos = code.getCurrentOffset() - 2;

        // Get next entry
        code.aload(iteratorSlot);
        code.invokeinterface(iteratorNextRef, 1);
        int entryClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP_ENTRY);
        code.checkcast(entryClass);
        int entrySlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$entry" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_MAP_ENTRY);
        code.astore(entrySlot);

        // Get key from entry
        code.aload(entrySlot);
        code.invokeinterface(entryGetKeyRef, 1);
        int keySlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$key" + context.getNextTempId(), ConstantJavaType.LJAVA_LANG_OBJECT);
        code.astore(keySlot);

        // Check if key is in extracted keys - if so, skip adding to rest map
        List<Integer> skipAddPositions = new ArrayList<>();
        for (String extractedKey : extractedKeys) {
            code.aload(keySlot);
            int extractedKeyRef = cp.addString(extractedKey);
            code.ldc(extractedKeyRef);
            code.invokevirtual(objectEqualsRef);
            code.ifne(0); // If equal, skip adding this entry
            skipAddPositions.add(code.getCurrentOffset() - 2);
        }

        // If we get here, key is not in extracted keys - add to rest map
        code.aload(restMapSlot);
        code.aload(keySlot);
        code.aload(entrySlot);
        code.invokeinterface(entryGetValueRef, 1);
        code.invokeinterface(mapPutRef, 3);
        code.pop(); // Discard return value from put

        // Patch all skip jumps to here (after the add)
        int skipTarget = code.getCurrentOffset();
        for (int skipPos : skipAddPositions) {
            int offset = skipTarget - (skipPos - 1);
            code.patchShort(skipPos, offset);
        }

        // Jump back to loop start
        int gotoPos = code.getCurrentOffset();
        code.goto_(0);
        int gotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardOffset = loopStart - gotoPos;
        code.patchShort(gotoOffsetPos, backwardOffset);

        // Patch loop exit
        int loopEnd = code.getCurrentOffset();
        int exitOffset = loopEnd - (loopExitPos - 1);
        code.patchShort(loopExitPos, exitOffset);

        // Store rest map in rest variable
        code.aload(restMapSlot);
        code.astore(restVar.index());
    }

    /**
     * Generate bytecode for rest pattern extraction [...rest].
     */
    private static void generateRestPatternExtraction(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int tempListSlot,
            int restStartIndex,
            boolean isArrayRest,
            int listGetRef,
            int listSizeRef,
            int listAddRef) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        String sourceCode = compiler.getMemory().getScopedSourceCode().getSourceCode();

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(sourceCode, restPat, "Rest pattern argument must be a binding identifier");
        }

        String restVarName = bindingIdent.getId().getSym();
        LocalVariable restVar = context.getLocalVariableTable().getVariable(restVarName);

        // Create a new ArrayList for the rest elements
        int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
        code.newInstance(arrayListClass);
        code.dup();
        int arrayListInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
        code.invokespecial(arrayListInitRef);
        code.astore(restVar.index());

        // Get list size
        code.aload(tempListSlot);
        code.invokeinterface(listSizeRef, 1);
        int sizeSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$size" + context.getNextTempId(), ConstantJavaType.ABBR_INTEGER);
        code.istore(sizeSlot);

        // Loop from restStartIndex to size, adding elements to rest array
        int indexSlot = CodeGeneratorUtils.getOrAllocateTempSlot(context, "$index" + context.getNextTempId(), ConstantJavaType.ABBR_INTEGER);
        code.iconst(restStartIndex);
        code.istore(indexSlot);

        int loopStart = code.getCurrentOffset();
        code.iload(indexSlot);
        code.iload(sizeSlot);
        code.if_icmpge(0); // Placeholder
        int loopExitPos = code.getCurrentOffset() - 2;

        // Add element to rest array
        code.aload(restVar.index());
        code.aload(tempListSlot);
        code.iload(indexSlot);
        code.invokeinterface(listGetRef, 2);
        code.invokeinterface(listAddRef, 2);
        code.pop(); // Discard boolean return value

        // Increment index
        code.iinc(indexSlot, 1);

        // Jump back to loop start
        int gotoPos = code.getCurrentOffset();
        code.goto_(0);
        int gotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardOffset = loopStart - gotoPos;
        code.patchShort(gotoOffsetPos, backwardOffset);

        // Patch loop exit
        int loopEnd = code.getCurrentOffset();
        int exitOffset = loopEnd - (loopExitPos - 1);
        code.patchShort(loopExitPos, exitOffset);
    }

    /**
     * Generate unboxing code if the target type is a primitive.
     * Converts boxed types (Integer, Long, etc.) to primitives (int, long, etc.).
     */
    private static void generateUnboxingIfNeeded(CodeBuilder code, ClassWriter classWriter, String targetType) {
        var cp = classWriter.getConstantPool();
        switch (targetType) {
            case ConstantJavaType.ABBR_INTEGER -> { // int
                int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.__I);
                int integerClass = cp.addClass(ConstantJavaType.JAVA_LANG_INTEGER);
                code.checkcast(integerClass);
                code.invokevirtual(intValueRef);
            }
            case ConstantJavaType.ABBR_LONG -> { // long
                int longValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_LONG_VALUE, ConstantJavaDescriptor.__J);
                int longClass = cp.addClass(ConstantJavaType.JAVA_LANG_LONG);
                code.checkcast(longClass);
                code.invokevirtual(longValueRef);
            }
            case ConstantJavaType.ABBR_DOUBLE -> { // double
                int doubleValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_DOUBLE_VALUE, ConstantJavaDescriptor.__D);
                int doubleClass = cp.addClass(ConstantJavaType.JAVA_LANG_DOUBLE);
                code.checkcast(doubleClass);
                code.invokevirtual(doubleValueRef);
            }
            case ConstantJavaType.ABBR_FLOAT -> { // float
                int floatValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_FLOAT_VALUE, ConstantJavaDescriptor.__F);
                int floatClass = cp.addClass(ConstantJavaType.JAVA_LANG_FLOAT);
                code.checkcast(floatClass);
                code.invokevirtual(floatValueRef);
            }
            case ConstantJavaType.ABBR_BOOLEAN -> { // boolean
                int booleanValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BOOLEAN, ConstantJavaMethod.METHOD_BOOLEAN_VALUE, ConstantJavaDescriptor.__Z);
                int booleanClass = cp.addClass(ConstantJavaType.JAVA_LANG_BOOLEAN);
                code.checkcast(booleanClass);
                code.invokevirtual(booleanValueRef);
            }
            case ConstantJavaType.ABBR_BYTE -> { // byte
                int byteValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_BYTE_VALUE, ConstantJavaDescriptor.__B);
                int byteClass = cp.addClass(ConstantJavaType.JAVA_LANG_BYTE);
                code.checkcast(byteClass);
                code.invokevirtual(byteValueRef);
            }
            case ConstantJavaType.ABBR_CHARACTER -> { // char
                int charValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_CHARACTER, ConstantJavaMethod.METHOD_CHAR_VALUE, ConstantJavaDescriptor.__C);
                int charClass = cp.addClass(ConstantJavaType.JAVA_LANG_CHARACTER);
                code.checkcast(charClass);
                code.invokevirtual(charValueRef);
            }
            case ConstantJavaType.ABBR_SHORT -> { // short
                int shortValueRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_SHORT_VALUE, ConstantJavaDescriptor.__S);
                int shortClass = cp.addClass(ConstantJavaType.JAVA_LANG_SHORT);
                code.checkcast(shortClass);
                code.invokevirtual(shortValueRef);
            }
            default -> {
                // Reference type or Object - just cast
                if (targetType.startsWith("L") && targetType.endsWith(";")) {
                    String className = TypeConversionUtils.descriptorToInternalName(targetType);
                    int classRef = cp.addClass(className);
                    code.checkcast(classRef);
                }
            }
        }
    }
}
