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


package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForOfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator for for-of loops.
 * <p>
 * For-of loops iterate over the VALUES of an iterable (array, string, set, map).
 * This is different from for-in which iterates over KEYS/INDICES.
 * <p>
 * Bytecode pattern for arrays (List):
 * <pre>
 *   aload arr                              // Load array
 *   invokeinterface List.iterator()        // Get iterator
 *   astore iterator                        // Store iterator
 *   TEST_LABEL:                            // Loop entry point
 *   aload iterator
 *   invokeinterface Iterator.hasNext()
 *   ifeq END_LABEL                         // Exit if no more elements
 *   aload iterator
 *   invokeinterface Iterator.next()
 *   astore value                           // Store value variable (actual element!)
 *   [body statements]
 *   goto TEST_LABEL                        // Jump back to test
 *   END_LABEL:                             // Break target
 * </pre>
 * <p>
 * Bytecode pattern for strings (String):
 * <pre>
 *   aload str                              // Load string
 *   invokevirtual String.length()          // Get length
 *   istore length                          // Store length
 *   iconst_0                               // i = 0
 *   istore counter                         // Store counter
 *   TEST_LABEL:                            // Loop entry point
 *   iload counter
 *   iload length
 *   if_icmpge END_LABEL                    // Exit if i >= length
 *   aload str
 *   iload counter
 *   invokevirtual String.charAt(I)C        // Get char at index
 *   invokestatic String.valueOf(C)         // Convert char to String
 *   astore value                           // Store character as String
 *   [body statements]
 *   iinc counter, 1                        // i++
 *   goto TEST_LABEL                        // Jump back to test
 *   END_LABEL:                             // Break target
 * </pre>
 * <p>
 * Bytecode pattern for maps (LinkedHashMap) with destructuring [key, value]:
 * <pre>
 *   aload map                              // Load map
 *   invokeinterface Map.entrySet()         // Get entry set
 *   invokeinterface Set.iterator()         // Get iterator
 *   astore iterator                        // Store iterator
 *   TEST_LABEL:                            // Loop entry point
 *   aload iterator
 *   invokeinterface Iterator.hasNext()
 *   ifeq END_LABEL                         // Exit if no more entries
 *   aload iterator
 *   invokeinterface Iterator.next()
 *   checkcast Map$Entry                    // Cast to Entry
 *   astore entry                           // Store entry
 *   aload entry
 *   invokeinterface Map$Entry.getKey()
 *   astore key                             // Store key variable
 *   aload entry
 *   invokeinterface Map$Entry.getValue()
 *   astore value                           // Store value variable
 *   [body statements]
 *   goto TEST_LABEL                        // Jump back to test
 *   END_LABEL:                             // Break target
 * </pre>
 */
public final class ForOfStatementProcessor extends BaseAstProcessor<Swc4jAstForOfStmt> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public ForOfStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private IterationType determineIterationType(ISwc4jAstExpr astExpr) throws Swc4jByteCodeCompilerException {
        String typeDescriptor = compiler.getTypeResolver().inferTypeFromExpr(astExpr);
        if (typeDescriptor == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), astExpr,
                    "Cannot determine type of for-of expression. Please add explicit type annotation.");
        }

        // Check for String type (String doesn't implement List or Map)
        if (ConstantJavaType.LJAVA_LANG_STRING.equals(typeDescriptor)) {
            return IterationType.STRING;
        }

        // Check for array types (both primitive and object arrays)
        if (typeDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return IterationType.ARRAY;
        }

        // For object types, use isAssignableTo() for unified checking
        if (typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";")) {
            String internalName = typeDescriptor.substring(1, typeDescriptor.length() - 1);
            String qualifiedName = internalName.replace('/', '.');

            // Try to resolve from the registry first
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                // Try simple name
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            if (typeInfo == null) {
                // Create a temporary JavaTypeInfo for JDK types not in registry
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                int lastDot = qualifiedName.lastIndexOf('.');
                String packageName = lastDot >= 0 ? qualifiedName.substring(0, lastDot) : "";
                typeInfo = new JavaTypeInfo(simpleName, packageName, internalName);
            }

            // Check assignability using the unified type hierarchy
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_LIST)) {
                return IterationType.LIST;
            }
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_SET)) {
                return IterationType.SET;
            }
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_MAP)) {
                return IterationType.MAP;
            }
        }

        throw new Swc4jByteCodeCompilerException(getSourceCode(), astExpr,
                "For-of loops require List, Set, Map, String, or Array type, but got: " + typeDescriptor);
    }

    /**
     * Extract property name from ISwc4jAstPropName.
     *
     * @param propName the property name AST node
     * @return the string representation of the property name
     * @throws Swc4jByteCodeCompilerException if the property name type is not supported
     */
    private String extractPropertyName(ISwc4jAstPropName propName) throws Swc4jByteCodeCompilerException {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), propName,
                    "Unsupported property name type in object destructuring: " + propName.getClass().getName());
        }
    }

    /**
     * Generate bytecode for a for-of statement (potentially labeled).
     * <p>
     * Uses compile-time type checking to determine iteration strategy:
     * - List: iterate over values via iterator
     * - Set: iterate over values via iterator
     * - Map: iterate over entries (with destructuring support)
     * - String: iterate over characters
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param forOfStmt      the for-of statement AST node
     * @param labelName      the label name (null for unlabeled loops)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Determine iteration strategy based on compile-time type
        IterationType iterationType = determineIterationType(forOfStmt.getRight());

        // Generate the appropriate iteration code
        switch (iterationType) {
            case LIST, SET -> generateIteratorIteration(code, classWriter, forOfStmt, labelName, returnTypeInfo);
            case MAP -> generateMapIteration(code, classWriter, forOfStmt, labelName, returnTypeInfo);
            case STRING -> generateStringIteration(code, classWriter, forOfStmt, labelName, returnTypeInfo);
            case ARRAY -> generateArrayIteration(code, classWriter, forOfStmt, labelName, returnTypeInfo);
        }
    }

    /**
     * Generate bytecode for a for-of statement (unlabeled).
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param forOfStmt      the for-of statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, classWriter, forOfStmt, null, returnTypeInfo);
    }

    /**
     * Generate bytecode to extract elements from a List for array destructuring.
     * For pattern [first, second, ...rest], generates:
     * first = list.get(0), second = list.get(1), rest = new ArrayList with remaining elements
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param context        the compilation context
     * @param arrayPat       the array pattern
     * @param listSlot       the local variable slot containing the list
     * @param restStartIndex the index at which rest pattern starts
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private void generateArrayDestructuringExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstArrayPat arrayPat,
            int listSlot,
            int restStartIndex) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int listGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_OBJECT);
        int listSizeRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.DESCRIPTOR___I);
        int listAddRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);

        int currentIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                // Hole in pattern: [a, , b] - skip but advance index
                currentIndex++;
                continue;
            }

            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                // Simple element: extract list.get(index)
                String varName = bindingIdent.getId().getSym();
                var variable = context.getLocalVariableTable().getVariable(varName);
                if (variable == null) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent, "Variable not found: " + varName);
                }

                // Load list and call get(index)
                code.aload(listSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);

                // Store the value
                code.astore(variable.index());
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                // Rest element: [...rest]
                // Creates a new ArrayList with remaining elements from restStartIndex onwards
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    var variable = context.getLocalVariableTable().getVariable(varName);
                    if (variable == null) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat, "Variable not found: " + varName);
                    }

                    // Create new ArrayList
                    int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
                    int arrayListInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR___V);
                    code.newInstance(arrayListClass);
                    code.dup();
                    code.invokespecial(arrayListInitRef);
                    code.astore(variable.index());

                    // Get source list size
                    code.aload(listSlot);
                    code.invokeinterface(listSizeRef, 1);
                    int sizeSlot = context.getLocalVariableTable().allocateVariable("$restSize", ConstantJavaType.ABBR_INTEGER);
                    code.istore(sizeSlot);

                    // Initialize loop counter at restStartIndex
                    code.iconst(restStartIndex);
                    int iSlot = context.getLocalVariableTable().allocateVariable("$restI", ConstantJavaType.ABBR_INTEGER);
                    code.istore(iSlot);

                    // Loop to copy remaining elements
                    int loopStart = code.getCurrentOffset();
                    code.iload(iSlot);
                    code.iload(sizeSlot);
                    code.if_icmpge(0); // Placeholder - jump to loop end if i >= size
                    int loopExitPatch = code.getCurrentOffset() - 2;

                    // rest.add(source.get(i))
                    code.aload(variable.index());
                    code.aload(listSlot);
                    code.iload(iSlot);
                    code.invokeinterface(listGetRef, 2);
                    code.invokeinterface(listAddRef, 2);
                    code.pop(); // Discard boolean return

                    // i++
                    code.iinc(iSlot, 1);

                    // goto loop start
                    code.gotoLabel(0); // Placeholder
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
    }

    /**
     * Generate bytecode for for-of loop over an array (both primitive and object arrays).
     * Bytecode pattern:
     * <pre>
     *   aload arr                              // Load array
     *   dup                                    // Duplicate for later use
     *   arraylength                           // Get array length
     *   istore length                          // Store length
     *   astore arr_temp                        // Store array
     *   iconst_0                               // i = 0
     *   istore counter                         // Store counter
     *   TEST_LABEL:                            // Loop entry point
     *   iload counter
     *   iload length
     *   if_icmpge END_LABEL                    // Exit if i >= length
     *   aload arr_temp
     *   iload counter
     *   [a|i|d|f|l|b|c|s]aload               // Load array element (type-specific)
     *   [checkcast if object array]            // Cast to element type if needed
     *   [a|i|d|f|l]store value                // Store element in loop variable
     *   [body statements]
     *   iinc counter, 1                        // i++
     *   goto TEST_LABEL                        // Jump back to test
     *   END_LABEL:                             // Break target
     * </pre>
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param forOfStmt      the for-of statement AST node
     * @param labelName      the label name (null if unlabeled)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private void generateArrayIteration(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Get array type descriptor
        String arrayTypeDescriptor = compiler.getTypeResolver().inferTypeFromExpr(forOfStmt.getRight());
        if (arrayTypeDescriptor == null || !arrayTypeDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), forOfStmt.getRight(),
                    "Cannot determine array type");
        }

        // Extract element type (skip the '[' prefix)
        String elementTypeDescriptor = arrayTypeDescriptor.substring(1);

        // Initialize loop variable with element type
        int elementSlot = initializeLoopVariable(code, forOfStmt.getLeft(), elementTypeDescriptor);

        // 1. Generate right expression (array)
        compiler.getExpressionProcessor().generate(code, classWriter, forOfStmt.getRight(), null);

        // 2. Duplicate array for later use
        code.dup();

        // 3. Get array length
        code.arraylength();

        // 4. Store length in temporary variable
        int lengthSlot = context.getLocalVariableTable().allocateVariable("$length", ConstantJavaType.ABBR_INTEGER);
        code.istore(lengthSlot);

        // 5. Store array in temporary variable
        int arraySlot = context.getLocalVariableTable().allocateVariable("$array", arrayTypeDescriptor);
        code.astore(arraySlot);

        // 6. Initialize counter: i = 0
        code.iconst(0);
        int counterSlot = context.getLocalVariableTable().allocateVariable("$i", ConstantJavaType.ABBR_INTEGER);
        code.istore(counterSlot);

        // 7. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 8. Test counter < length
        code.iload(counterSlot);
        code.iload(lengthSlot);

        // 9. Jump to end if i >= length
        code.if_icmpge(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 10. Load array[counter]
        code.aload(arraySlot);
        code.iload(counterSlot);

        // Use type-specific array load instruction
        switch (elementTypeDescriptor) {
            case ConstantJavaType.ABBR_INTEGER -> code.iaload();  // int[]
            case ConstantJavaType.ABBR_DOUBLE -> code.daload();  // double[]
            case ConstantJavaType.ABBR_FLOAT -> code.faload();  // float[]
            case ConstantJavaType.ABBR_LONG -> code.laload();  // long[]
            case ConstantJavaType.ABBR_BYTE -> code.baload();  // byte[]
            case ConstantJavaType.ABBR_CHARACTER -> code.caload();  // char[]
            case ConstantJavaType.ABBR_SHORT -> code.saload();  // short[]
            case ConstantJavaType.ABBR_BOOLEAN -> code.baload();  // boolean[] (uses baload)
            default -> {
                // Object arrays
                code.aaload();
                // Add checkcast for object arrays if needed
                if (elementTypeDescriptor.startsWith("L") && elementTypeDescriptor.endsWith(";")) {
                    String elementInternalName = elementTypeDescriptor.substring(1, elementTypeDescriptor.length() - 1);
                    int classIndex = cp.addClass(elementInternalName);
                    code.checkcast(classIndex);
                }
            }
        }

        // 11. Store in loop variable
        switch (elementTypeDescriptor) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_CHARACTER,
                 ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_BOOLEAN ->
                    code.istore(elementSlot);  // int, byte, char, short, boolean
            case ConstantJavaType.ABBR_DOUBLE -> code.dstore(elementSlot);  // double
            case ConstantJavaType.ABBR_FLOAT -> code.fstore(elementSlot);  // float
            case ConstantJavaType.ABBR_LONG -> code.lstore(elementSlot);  // long
            default -> code.astore(elementSlot);  // Object references
        }

        // 12. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 13. Generate body
        compiler.getStatementProcessor().generate(code, classWriter, forOfStmt.getBody(), returnTypeInfo);

        // 14. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 15. Mark update label (continue target)
        int updateLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(updateLabel);

        // 16. Increment counter: i++
        code.iinc(counterSlot, 1);

        // 17. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 18. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 19. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 20. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 21. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode for iterating over an iterable (ArrayList, Set) using iterator.
     * Values are obtained directly via iterator.next() - no conversion needed.
     * Supports object destructuring patterns like { name, age } for iterating over array of maps.
     * Supports array destructuring patterns like [first, second, ...rest] for iterating over arrays of arrays.
     */
    private void generateIteratorIteration(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Check for destructuring patterns
        ISwc4jAstForHead left = forOfStmt.getLeft();
        boolean hasObjectDestructuring = false;
        boolean hasArrayDestructuring = false;
        Swc4jAstObjectPat objectPat = null;
        Swc4jAstArrayPat arrayPat = null;
        int tempElementSlot = -1;
        List<String> extractedKeys = new ArrayList<>();
        int restStartIndex = 0;

        if (left instanceof Swc4jAstVarDecl varDecl && !varDecl.getDecls().isEmpty()) {
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstObjectPat op) {
                hasObjectDestructuring = true;
                objectPat = op;
                // Allocate variables for each property in the object pattern
                extractedKeys = initializeObjectPatternVariables(code, context, objectPat);
            } else if (name instanceof Swc4jAstArrayPat ap) {
                hasArrayDestructuring = true;
                arrayPat = ap;
                // Allocate variables for each element in the array pattern
                restStartIndex = initializeArrayPatternVariables(code, context, arrayPat);
            }
        }

        int valueSlot = -1;
        if (!hasObjectDestructuring && !hasArrayDestructuring) {
            // Initialize loop variable with Object type (for-of values are objects)
            valueSlot = initializeLoopVariable(code, forOfStmt.getLeft(), ConstantJavaType.LJAVA_LANG_OBJECT);
        }

        // 1. Generate right expression (iterable)
        compiler.getExpressionProcessor().generate(code, classWriter, forOfStmt.getRight(), null);

        // 2. Get iterator: Iterable.iterator() -> Iterator
        int iteratorRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_LANG_ITERABLE, ConstantJavaMethod.METHOD_ITERATOR, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_UTIL_ITERATOR);
        code.invokeinterface(iteratorRef, 1);

        // 3. Store iterator in temporary variable
        int iteratorSlot = context.getLocalVariableTable().allocateVariable("$iterator", ConstantJavaType.LJAVA_UTIL_ITERATOR);
        code.astore(iteratorSlot);

        // 4. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 5. Test hasNext: Iterator.hasNext() -> boolean
        code.aload(iteratorSlot);
        int hasNextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_HAS_NEXT, ConstantJavaDescriptor.DESCRIPTOR___Z);
        code.invokeinterface(hasNextRef, 1);

        // 6. Jump to end if no more elements
        code.ifeq(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 7. Get next value: Iterator.next() -> Object
        code.aload(iteratorSlot);
        int nextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_NEXT, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_OBJECT);
        code.invokeinterface(nextRef, 1);

        if (hasObjectDestructuring) {
            // 8a. Handle object destructuring - element is a Map
            // Cast to Map and store temporarily
            int mapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP);
            code.checkcast(mapClass);
            tempElementSlot = context.getLocalVariableTable().allocateVariable("$element", ConstantJavaType.LJAVA_UTIL_MAP);
            code.astore(tempElementSlot);

            // Extract each property from the map
            generateObjectDestructuringExtraction(code, classWriter, context, objectPat, tempElementSlot, extractedKeys);
        } else if (hasArrayDestructuring) {
            // 8b. Handle array destructuring - element is a List
            // Cast to List and store temporarily
            int listClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LIST);
            code.checkcast(listClass);
            tempElementSlot = context.getLocalVariableTable().allocateVariable("$element", ConstantJavaType.LJAVA_UTIL_LIST);
            code.astore(tempElementSlot);

            // Extract each element from the list
            generateArrayDestructuringExtraction(code, classWriter, context, arrayPat, tempElementSlot, restStartIndex);
        } else {
            // 8c. Store in loop variable (NO conversion - keep actual value)
            code.astore(valueSlot);
        }

        // 9. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);
        continueLabel.setTargetOffset(testLabel);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 10. Generate body
        compiler.getStatementProcessor().generate(code, classWriter, forOfStmt.getBody(), returnTypeInfo);

        // 11. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 12. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 13. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 14. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 15. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 16. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode for iterating over a Map (LinkedHashMap) with entry destructuring.
     * For-of over Map returns [key, value] pairs, requiring array destructuring.
     */
    private void generateMapIteration(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Check if we have array destructuring pattern [key, value]
        ISwc4jAstForHead left = forOfStmt.getLeft();
        int keySlot = -1;
        int valueSlot = -1;
        int entrySlot = -1;
        boolean hasDestructuring = false;

        if (left instanceof Swc4jAstVarDecl varDecl && !varDecl.getDecls().isEmpty()) {
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstArrayPat arrayPat) {
                // Array destructuring: [key, value] of map
                hasDestructuring = true;
                List<ISwc4jAstPat> elems = arrayPat.getElems().stream()
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .toList();

                // Allocate variables for key and value
                if (!elems.isEmpty() && elems.get(0) instanceof Swc4jAstBindingIdent keyIdent) {
                    String keyName = keyIdent.getId().getSym();
                    keySlot = context.getLocalVariableTable().allocateVariable(keyName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    context.getInferredTypes().put(keyName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    code.aconst_null();
                    code.astore(keySlot);
                }
                if (elems.size() > 1 && elems.get(1) instanceof Swc4jAstBindingIdent valueIdent) {
                    String valueName = valueIdent.getId().getSym();
                    valueSlot = context.getLocalVariableTable().allocateVariable(valueName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    context.getInferredTypes().put(valueName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    code.aconst_null();
                    code.astore(valueSlot);
                }
            }
        }

        // If no destructuring, just use a single variable for the entry
        if (!hasDestructuring) {
            entrySlot = initializeLoopVariable(code, left, ConstantJavaType.LJAVA_UTIL_MAP_ENTRY);
        }

        // 1. Generate right expression (map)
        compiler.getExpressionProcessor().generate(code, classWriter, forOfStmt.getRight(), null);

        // 2. Get entrySet: Map.entrySet() -> Set<Entry>
        int entrySetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_ENTRY_SET, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_UTIL_SET);
        code.invokeinterface(entrySetRef, 1);

        // 3. Get iterator: Set.iterator() -> Iterator
        int iteratorRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_SET, ConstantJavaMethod.METHOD_ITERATOR, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_UTIL_ITERATOR);
        code.invokeinterface(iteratorRef, 1);

        // 4. Store iterator in temporary variable
        int iteratorSlot = context.getLocalVariableTable().allocateVariable("$iterator", ConstantJavaType.LJAVA_UTIL_ITERATOR);
        code.astore(iteratorSlot);

        // 5. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 6. Test hasNext: Iterator.hasNext() -> boolean
        code.aload(iteratorSlot);
        int hasNextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_HAS_NEXT, ConstantJavaDescriptor.DESCRIPTOR___Z);
        code.invokeinterface(hasNextRef, 1);

        // 7. Jump to end if no more elements
        code.ifeq(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 8. Get next entry: Iterator.next() -> Object
        code.aload(iteratorSlot);
        int nextRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_ITERATOR, ConstantJavaMethod.METHOD_NEXT, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_OBJECT);
        code.invokeinterface(nextRef, 1);

        // 9. Cast to Map.Entry
        int entryClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP_ENTRY);
        code.checkcast(entryClass);

        if (hasDestructuring) {
            // 10a. Store entry temporarily
            int tempEntrySlot = context.getLocalVariableTable().allocateVariable("$entry", ConstantJavaType.LJAVA_UTIL_MAP_ENTRY);
            code.astore(tempEntrySlot);

            // 11a. Extract key: entry.getKey()
            if (keySlot >= 0) {
                code.aload(tempEntrySlot);
                int getKeyRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP_ENTRY, ConstantJavaMethod.METHOD_GET_KEY, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_OBJECT);
                code.invokeinterface(getKeyRef, 1);
                code.astore(keySlot);
            }

            // 12a. Extract value: entry.getValue()
            if (valueSlot >= 0) {
                code.aload(tempEntrySlot);
                int getValueRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP_ENTRY, ConstantJavaMethod.METHOD_GET_VALUE, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_OBJECT);
                code.invokeinterface(getValueRef, 1);
                code.astore(valueSlot);
            }
        } else {
            // 10b. Store entry directly
            code.astore(entrySlot);
        }

        // 13. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);
        continueLabel.setTargetOffset(testLabel);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 14. Generate body
        compiler.getStatementProcessor().generate(code, classWriter, forOfStmt.getBody(), returnTypeInfo);

        // 15. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 16. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 17. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 18. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 19. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 20. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode to extract properties from a Map for object destructuring.
     * For pattern { name, age }, generates: name = map.get("name"), age = map.get("age")
     * Supports shorthand properties ({ name }), renamed properties ({ name: n }),
     * default values ({ name = "default" }), and rest patterns ({ ...rest }).
     *
     * @param code          the code builder
     * @param cp            the constant pool
     * @param context       the compilation context
     * @param objectPat     the object pattern
     * @param mapSlot       the local variable slot containing the map
     * @param extractedKeys list of keys that have been explicitly extracted (for rest pattern)
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private void generateObjectDestructuringExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstObjectPat objectPat,
            int mapSlot,
            List<String> extractedKeys) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int mapGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);

        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                // Shorthand property: { name } or { name = defaultValue }
                String varName = assignProp.getKey().getId().getSym();
                var variable = context.getLocalVariableTable().getVariable(varName);
                if (variable == null) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), assignProp, "Variable not found: " + varName);
                }

                // Load map and call get(key)
                code.aload(mapSlot);
                int stringIndex = cp.addString(varName);
                code.ldc(stringIndex);
                code.invokeinterface(mapGetRef, 2);

                // Handle default value if present
                if (assignProp.getValue().isPresent()) {
                    // Stack: [value] (may be null)
                    // Store value first, then check null
                    code.astore(variable.index()); // Stack: []
                    code.aload(variable.index());  // Stack: [value]
                    code.ifnonnull(0); // Placeholder - jump if not null. Stack: []
                    int skipDefaultPos = code.getCurrentOffset() - 2;

                    // Value is null, generate default value and store
                    compiler.getExpressionProcessor().generate(code, classWriter, assignProp.getValue().get(), null);
                    code.astore(variable.index()); // Stack: []

                    // Patch the skip jump
                    int afterDefaultLabel = code.getCurrentOffset();
                    int skipOffset = afterDefaultLabel - (skipDefaultPos - 1);
                    code.patchShort(skipDefaultPos, (short) skipOffset);
                } else {
                    // Store the value
                    code.astore(variable.index());
                }

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                // Renamed property: { name: n }
                String keyName = extractPropertyName(keyValueProp.getKey());
                ISwc4jAstPat valuePat = keyValueProp.getValue();

                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    var variable = context.getLocalVariableTable().getVariable(varName);
                    if (variable == null) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), keyValueProp, "Variable not found: " + varName);
                    }

                    // Load map and call get(key)
                    code.aload(mapSlot);
                    int stringIndex = cp.addString(keyName);
                    code.ldc(stringIndex);
                    code.invokeinterface(mapGetRef, 2);

                    // Store the value
                    code.astore(variable.index());
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), keyValueProp,
                            "Unsupported value pattern type in object destructuring: " + valuePat.getClass().getName());
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                // Rest property: { ...rest }
                // Creates a new LinkedHashMap with remaining properties
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    var variable = context.getLocalVariableTable().getVariable(varName);
                    if (variable == null) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat, "Variable not found: " + varName);
                    }

                    // Create new LinkedHashMap as copy of source map
                    // new LinkedHashMap(sourceMap)
                    int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
                    code.newInstance(linkedHashMapClass);
                    code.dup();
                    code.aload(mapSlot);
                    int linkedHashMapInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_UTIL_MAP__V);
                    code.invokespecial(linkedHashMapInitRef);

                    // Store the copy
                    code.astore(variable.index());

                    // Remove all extracted keys from the rest map
                    int mapRemoveRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                    for (String key : extractedKeys) {
                        code.aload(variable.index());
                        int keyStringIndex = cp.addString(key);
                        code.ldc(keyStringIndex);
                        code.invokeinterface(mapRemoveRef, 2);
                        code.pop(); // Discard removed value
                    }
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), prop,
                        "Unsupported property type in object destructuring: " + prop.getClass().getName());
            }
        }
    }

    /**
     * Generate bytecode for iterating over a string (String).
     * Characters are returned as String values (not indices like for-in).
     */
    private void generateStringIteration(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Initialize loop variable with String type (for-of over string returns chars as strings)
        int charSlot = initializeLoopVariable(code, forOfStmt.getLeft(), ConstantJavaType.LJAVA_LANG_STRING);

        // 1. Generate right expression (string)
        compiler.getExpressionProcessor().generate(code, classWriter, forOfStmt.getRight(), null);

        // 2. Duplicate string for later charAt calls
        code.dup();

        // 3. Get length: String.length() -> int
        int lengthRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_LENGTH, ConstantJavaDescriptor.DESCRIPTOR___I);
        code.invokevirtual(lengthRef);

        // 4. Store length in temporary variable
        int lengthSlot = context.getLocalVariableTable().allocateVariable("$length", ConstantJavaType.ABBR_INTEGER);
        code.istore(lengthSlot);

        // 5. Store string in temporary variable
        int stringSlot = context.getLocalVariableTable().allocateVariable("$string", ConstantJavaType.LJAVA_LANG_STRING);
        code.astore(stringSlot);

        // 6. Initialize counter: i = 0
        code.iconst(0);
        int counterSlot = context.getLocalVariableTable().allocateVariable("$i", ConstantJavaType.ABBR_INTEGER);
        code.istore(counterSlot);

        // 7. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 8. Test counter < length
        code.iload(counterSlot);
        code.iload(lengthSlot);

        // 9. Jump to end if i >= length
        code.if_icmpge(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 10. Get character at index: str.charAt(i)
        code.aload(stringSlot);
        code.iload(counterSlot);
        int charAtRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, "charAt", ConstantJavaDescriptor.DESCRIPTOR_I__C);
        code.invokevirtual(charAtRef);

        // 11. Convert char to String: String.valueOf(char)
        int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_C__LJAVA_LANG_STRING);
        code.invokestatic(valueOfRef);

        // 12. Store in loop variable (as String)
        code.astore(charSlot);

        // 13. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 14. Generate body
        compiler.getStatementProcessor().generate(code, classWriter, forOfStmt.getBody(), returnTypeInfo);

        // 15. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 16. Mark update label (continue target)
        int updateLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(updateLabel);

        // 17. Increment counter: i++
        code.iinc(counterSlot, 1);

        // 18. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 19. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 20. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 21. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }

        // 22. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchInt(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Initialize variables for all elements in an array pattern.
     *
     * @param code     the code builder
     * @param context  the compilation context
     * @param arrayPat the array pattern
     * @return the rest start index (number of regular elements before rest)
     * @throws Swc4jByteCodeCompilerException if initialization fails
     */
    private int initializeArrayPatternVariables(
            CodeBuilder code,
            CompilationContext context,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {
        int restStartIndex = 0;

        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                // Hole in pattern: [a, , b] - skip but count for rest index
                restStartIndex++;
                continue;
            }

            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                // Simple element: [a]
                String varName = bindingIdent.getId().getSym();
                int slot = context.getLocalVariableTable().allocateVariable(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                context.getInferredTypes().put(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                code.aconst_null();
                code.astore(slot);
                restStartIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                // Rest element: [...rest]
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    // Rest creates a new ArrayList
                    int slot = context.getLocalVariableTable().allocateVariable(varName, ConstantJavaType.LJAVA_UTIL_ARRAYLIST);
                    context.getInferredTypes().put(varName, ConstantJavaType.LJAVA_UTIL_ARRAYLIST);
                    code.aconst_null();
                    code.astore(slot);
                    // Don't increment restStartIndex - rest doesn't count
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), elem,
                        "Unsupported element type in array destructuring: " + elem.getClass().getName());
            }
        }
        return restStartIndex;
    }

    /**
     * Initialize loop variable and return its slot index.
     * For-of values depend on the iterable type.
     *
     * @param code        the code builder
     * @param left        the loop variable declaration or pattern
     * @param defaultType the default type descriptor for the variable
     * @return the local variable slot index
     */
    private int initializeLoopVariable(CodeBuilder code, ISwc4jAstForHead left, String defaultType)
            throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        if (left instanceof Swc4jAstVarDecl varDecl) {
            // Variable declaration: let value or const value
            if (varDecl.getDecls().isEmpty()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), varDecl, "For-of variable declaration is empty");
            }
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                // Allocate variable in current scope
                int slot = context.getLocalVariableTable().allocateVariable(varName, defaultType);
                // Also register in inferredTypes so TypeResolver can find it
                context.getInferredTypes().put(varName, defaultType);
                // Initialize with appropriate zero value based on type
                switch (defaultType) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_BYTE,
                         ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_SHORT,
                         ConstantJavaType.ABBR_BOOLEAN -> {
                        code.iconst(0);
                        code.istore(slot);
                    }
                    case ConstantJavaType.ABBR_LONG -> {
                        code.lconst(0);
                        code.lstore(slot);
                    }
                    case ConstantJavaType.ABBR_FLOAT -> {
                        code.fconst(0);
                        code.fstore(slot);
                    }
                    case ConstantJavaType.ABBR_DOUBLE -> {
                        code.dconst(0);
                        code.dstore(slot);
                    }
                    default -> {
                        code.aconst_null();
                        code.astore(slot);
                    }
                }
                return slot;
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), name,
                        "For-of variable must be a simple identifier or array pattern");
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Existing variable - look it up (do NOT reinitialize - keep existing value)
            String varName = bindingIdent.getId().getSym();
            var variable = context.getLocalVariableTable().getVariable(varName);
            if (variable == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), bindingIdent, "Variable not found: " + varName);
            }
            // Update inferredTypes
            context.getInferredTypes().put(varName, defaultType);
            return variable.index();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), left,
                    "Unsupported for-of left type: " + left.getClass().getName());
        }
    }

    /**
     * Initialize variables for all properties in an object pattern.
     *
     * @param code      the code builder
     * @param context   the compilation context
     * @param objectPat the object pattern
     * @return list of extracted key names (for rest pattern processing)
     * @throws Swc4jByteCodeCompilerException if initialization fails
     */
    private List<String> initializeObjectPatternVariables(
            CodeBuilder code,
            CompilationContext context,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {
        List<String> extractedKeys = new ArrayList<>();

        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                // Shorthand property: { name }
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
                int slot = context.getLocalVariableTable().allocateVariable(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                context.getInferredTypes().put(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                code.aconst_null();
                code.astore(slot);

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                // Renamed property: { name: n }
                String keyName = extractPropertyName(keyValueProp.getKey());
                extractedKeys.add(keyName);

                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    int slot = context.getLocalVariableTable().allocateVariable(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    context.getInferredTypes().put(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
                    code.aconst_null();
                    code.astore(slot);
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), keyValueProp,
                            "Unsupported value pattern type in object destructuring: " + valuePat.getClass().getName());
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                // Rest property: { ...rest }
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    // Rest creates a new LinkedHashMap
                    int slot = context.getLocalVariableTable().allocateVariable(varName, ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP);
                    context.getInferredTypes().put(varName, ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP);
                    code.aconst_null();
                    code.astore(slot);
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                            "Rest pattern argument must be a binding identifier");
                }
            }
        }
        return extractedKeys;
    }

    /**
     * Enum representing the type of iteration to perform.
     */
    private enum IterationType {
        /**
         * List iteration type.
         */
        LIST,
        /**
         * Set iteration type.
         */
        SET,
        /**
         * Map iteration type.
         */
        MAP,
        /**
         * String iteration type.
         */
        STRING,
        /**
         * Array iteration type.
         */
        ARRAY
    }
}
