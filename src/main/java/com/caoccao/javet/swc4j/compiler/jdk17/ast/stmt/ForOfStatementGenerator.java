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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstArrayPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPatProp;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstKeyValuePatProp;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForOfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

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
public final class ForOfStatementGenerator extends BaseAstProcessor<Swc4jAstForOfStmt> {
    public ForOfStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private IterationType determineIterationType(ISwc4jAstExpr astExpr) throws Swc4jByteCodeCompilerException {
        String typeDescriptor = compiler.getTypeResolver().inferTypeFromExpr(astExpr);
        if (typeDescriptor == null) {
            throw new Swc4jByteCodeCompilerException(astExpr,
                    "Cannot determine type of for-of expression. Please add explicit type annotation.");
        }

        // Check for String type (String doesn't implement List or Map)
        if ("Ljava/lang/String;".equals(typeDescriptor)) {
            return IterationType.STRING;
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
            if (typeInfo.isAssignableTo("Ljava/util/List;")) {
                return IterationType.LIST;
            }
            if (typeInfo.isAssignableTo("Ljava/util/Set;")) {
                return IterationType.SET;
            }
            if (typeInfo.isAssignableTo("Ljava/util/Map;")) {
                return IterationType.MAP;
            }
        }

        throw new Swc4jByteCodeCompilerException(astExpr,
                "For-of loops require List, Set, Map, or String type, but got: " + typeDescriptor);
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
     * @param cp             the constant pool
     * @param forOfStmt      the for-of statement AST node
     * @param labelName      the label name (null for unlabeled loops)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Determine iteration strategy based on compile-time type
        IterationType iterationType = determineIterationType(forOfStmt.getRight());

        // Generate the appropriate iteration code
        switch (iterationType) {
            case LIST, SET -> generateIteratorIteration(code, cp, forOfStmt, labelName, returnTypeInfo);
            case MAP -> generateMapIteration(code, cp, forOfStmt, labelName, returnTypeInfo);
            case STRING -> generateStringIteration(code, cp, forOfStmt, labelName, returnTypeInfo);
        }
    }

    /**
     * Generate bytecode for a for-of statement (unlabeled).
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param forOfStmt      the for-of statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForOfStmt forOfStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, cp, forOfStmt, null, returnTypeInfo);
    }

    /**
     * Generate bytecode for iterating over an iterable (ArrayList, Set) using iterator.
     * Values are obtained directly via iterator.next() - no conversion needed.
     * Supports object destructuring patterns like { name, age } for iterating over array of maps.
     */
    private void generateIteratorIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Check for object destructuring pattern { name, age }
        ISwc4jAstForHead left = forOfStmt.getLeft();
        boolean hasObjectDestructuring = false;
        Swc4jAstObjectPat objectPat = null;
        int tempElementSlot = -1;

        if (left instanceof Swc4jAstVarDecl varDecl && !varDecl.getDecls().isEmpty()) {
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstObjectPat op) {
                hasObjectDestructuring = true;
                objectPat = op;
                // Allocate variables for each property in the object pattern
                initializeObjectPatternVariables(code, context, objectPat);
            }
        }

        int valueSlot = -1;
        if (!hasObjectDestructuring) {
            // Initialize loop variable with Object type (for-of values are objects)
            valueSlot = initializeLoopVariable(code, forOfStmt.getLeft(), "Ljava/lang/Object;");
        }

        // 1. Generate right expression (iterable)
        compiler.getExpressionGenerator().generate(code, cp, forOfStmt.getRight(), null);

        // 2. Get iterator: Iterable.iterator() -> Iterator
        int iteratorRef = cp.addInterfaceMethodRef("java/lang/Iterable", "iterator", "()Ljava/util/Iterator;");
        code.invokeinterface(iteratorRef, 1);

        // 3. Store iterator in temporary variable
        int iteratorSlot = context.getLocalVariableTable().allocateVariable("$iterator", "Ljava/util/Iterator;");
        code.astore(iteratorSlot);

        // 4. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 5. Test hasNext: Iterator.hasNext() -> boolean
        code.aload(iteratorSlot);
        int hasNextRef = cp.addInterfaceMethodRef("java/util/Iterator", "hasNext", "()Z");
        code.invokeinterface(hasNextRef, 1);

        // 6. Jump to end if no more elements
        code.ifeq(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 7. Get next value: Iterator.next() -> Object
        code.aload(iteratorSlot);
        int nextRef = cp.addInterfaceMethodRef("java/util/Iterator", "next", "()Ljava/lang/Object;");
        code.invokeinterface(nextRef, 1);

        if (hasObjectDestructuring) {
            // 8a. Handle object destructuring - element is a Map
            // Cast to Map and store temporarily
            int mapClass = cp.addClass("java/util/Map");
            code.checkcast(mapClass);
            tempElementSlot = context.getLocalVariableTable().allocateVariable("$element", "Ljava/util/Map;");
            code.astore(tempElementSlot);

            // Extract each property from the map
            generateObjectDestructuringExtraction(code, cp, context, objectPat, tempElementSlot);
        } else {
            // 8b. Store in loop variable (NO conversion - keep actual value)
            code.astore(valueSlot);
        }

        // 9. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);
        continueLabel.setTargetOffset(testLabel);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 10. Generate body
        compiler.getStatementGenerator().generate(code, cp, forOfStmt.getBody(), returnTypeInfo);

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
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 16. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
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
            throw new Swc4jByteCodeCompilerException(propName,
                    "Unsupported property name type in object destructuring: " + propName.getClass().getName());
        }
    }

    /**
     * Generate bytecode to extract properties from a Map for object destructuring.
     * For pattern { name, age }, generates: name = map.get("name"), age = map.get("age")
     * Supports shorthand properties ({ name }), renamed properties ({ name: n }),
     * and default values ({ name = "default" }).
     *
     * @param code         the code builder
     * @param cp           the constant pool
     * @param context      the compilation context
     * @param objectPat    the object pattern
     * @param mapSlot      the local variable slot containing the map
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    private void generateObjectDestructuringExtraction(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstObjectPat objectPat,
            int mapSlot) throws Swc4jByteCodeCompilerException {
        int mapGetRef = cp.addInterfaceMethodRef("java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");

        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                // Shorthand property: { name } or { name = defaultValue }
                String varName = assignProp.getKey().getId().getSym();
                var variable = context.getLocalVariableTable().getVariable(varName);
                if (variable == null) {
                    throw new Swc4jByteCodeCompilerException(assignProp, "Variable not found: " + varName);
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
                    compiler.getExpressionGenerator().generate(code, cp, assignProp.getValue().get(), null);
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
                        throw new Swc4jByteCodeCompilerException(keyValueProp, "Variable not found: " + varName);
                    }

                    // Load map and call get(key)
                    code.aload(mapSlot);
                    int stringIndex = cp.addString(keyName);
                    code.ldc(stringIndex);
                    code.invokeinterface(mapGetRef, 2);

                    // Store the value
                    code.astore(variable.index());
                } else {
                    throw new Swc4jByteCodeCompilerException(keyValueProp,
                            "Unsupported value pattern type in object destructuring: " + valuePat.getClass().getName());
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                // Rest property: { ...rest } - not yet supported
                throw new Swc4jByteCodeCompilerException(restPat,
                        "Rest patterns in object destructuring are not yet supported");
            } else {
                throw new Swc4jByteCodeCompilerException(prop,
                        "Unsupported property type in object destructuring: " + prop.getClass().getName());
            }
        }
    }

    /**
     * Initialize variables for all properties in an object pattern.
     *
     * @param code      the code builder
     * @param context   the compilation context
     * @param objectPat the object pattern
     * @throws Swc4jByteCodeCompilerException if initialization fails
     */
    private void initializeObjectPatternVariables(
            CodeBuilder code,
            CompilationContext context,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                // Shorthand property: { name }
                String varName = assignProp.getKey().getId().getSym();
                int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/Object;");
                context.getInferredTypes().put(varName, "Ljava/lang/Object;");
                code.aconst_null();
                code.astore(slot);

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                // Renamed property: { name: n }
                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/Object;");
                    context.getInferredTypes().put(varName, "Ljava/lang/Object;");
                    code.aconst_null();
                    code.astore(slot);
                } else {
                    throw new Swc4jByteCodeCompilerException(keyValueProp,
                            "Unsupported value pattern type in object destructuring: " + valuePat.getClass().getName());
                }

            } else if (prop instanceof Swc4jAstRestPat) {
                // Rest property - not yet supported, skip initialization
            }
        }
    }

    /**
     * Generate bytecode for iterating over a Map (LinkedHashMap) with entry destructuring.
     * For-of over Map returns [key, value] pairs, requiring array destructuring.
     */
    private void generateMapIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
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
                    keySlot = context.getLocalVariableTable().allocateVariable(keyName, "Ljava/lang/Object;");
                    context.getInferredTypes().put(keyName, "Ljava/lang/Object;");
                    code.aconst_null();
                    code.astore(keySlot);
                }
                if (elems.size() > 1 && elems.get(1) instanceof Swc4jAstBindingIdent valueIdent) {
                    String valueName = valueIdent.getId().getSym();
                    valueSlot = context.getLocalVariableTable().allocateVariable(valueName, "Ljava/lang/Object;");
                    context.getInferredTypes().put(valueName, "Ljava/lang/Object;");
                    code.aconst_null();
                    code.astore(valueSlot);
                }
            }
        }

        // If no destructuring, just use a single variable for the entry
        if (!hasDestructuring) {
            entrySlot = initializeLoopVariable(code, left, "Ljava/util/Map$Entry;");
        }

        // 1. Generate right expression (map)
        compiler.getExpressionGenerator().generate(code, cp, forOfStmt.getRight(), null);

        // 2. Get entrySet: Map.entrySet() -> Set<Entry>
        int entrySetRef = cp.addInterfaceMethodRef("java/util/Map", "entrySet", "()Ljava/util/Set;");
        code.invokeinterface(entrySetRef, 1);

        // 3. Get iterator: Set.iterator() -> Iterator
        int iteratorRef = cp.addInterfaceMethodRef("java/util/Set", "iterator", "()Ljava/util/Iterator;");
        code.invokeinterface(iteratorRef, 1);

        // 4. Store iterator in temporary variable
        int iteratorSlot = context.getLocalVariableTable().allocateVariable("$iterator", "Ljava/util/Iterator;");
        code.astore(iteratorSlot);

        // 5. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 6. Test hasNext: Iterator.hasNext() -> boolean
        code.aload(iteratorSlot);
        int hasNextRef = cp.addInterfaceMethodRef("java/util/Iterator", "hasNext", "()Z");
        code.invokeinterface(hasNextRef, 1);

        // 7. Jump to end if no more elements
        code.ifeq(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 8. Get next entry: Iterator.next() -> Object
        code.aload(iteratorSlot);
        int nextRef = cp.addInterfaceMethodRef("java/util/Iterator", "next", "()Ljava/lang/Object;");
        code.invokeinterface(nextRef, 1);

        // 9. Cast to Map.Entry
        int entryClass = cp.addClass("java/util/Map$Entry");
        code.checkcast(entryClass);

        if (hasDestructuring) {
            // 10a. Store entry temporarily
            int tempEntrySlot = context.getLocalVariableTable().allocateVariable("$entry", "Ljava/util/Map$Entry;");
            code.astore(tempEntrySlot);

            // 11a. Extract key: entry.getKey()
            if (keySlot >= 0) {
                code.aload(tempEntrySlot);
                int getKeyRef = cp.addInterfaceMethodRef("java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
                code.invokeinterface(getKeyRef, 1);
                code.astore(keySlot);
            }

            // 12a. Extract value: entry.getValue()
            if (valueSlot >= 0) {
                code.aload(tempEntrySlot);
                int getValueRef = cp.addInterfaceMethodRef("java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
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
        compiler.getStatementGenerator().generate(code, cp, forOfStmt.getBody(), returnTypeInfo);

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
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 20. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode for iterating over a string (String).
     * Characters are returned as String values (not indices like for-in).
     */
    private void generateStringIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForOfStmt forOfStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Initialize loop variable with String type (for-of over string returns chars as strings)
        int charSlot = initializeLoopVariable(code, forOfStmt.getLeft(), "Ljava/lang/String;");

        // 1. Generate right expression (string)
        compiler.getExpressionGenerator().generate(code, cp, forOfStmt.getRight(), null);

        // 2. Duplicate string for later charAt calls
        code.dup();

        // 3. Get length: String.length() -> int
        int lengthRef = cp.addMethodRef("java/lang/String", "length", "()I");
        code.invokevirtual(lengthRef);

        // 4. Store length in temporary variable
        int lengthSlot = context.getLocalVariableTable().allocateVariable("$length", "I");
        code.istore(lengthSlot);

        // 5. Store string in temporary variable
        int stringSlot = context.getLocalVariableTable().allocateVariable("$string", "Ljava/lang/String;");
        code.astore(stringSlot);

        // 6. Initialize counter: i = 0
        code.iconst(0);
        int counterSlot = context.getLocalVariableTable().allocateVariable("$i", "I");
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
        int charAtRef = cp.addMethodRef("java/lang/String", "charAt", "(I)C");
        code.invokevirtual(charAtRef);

        // 11. Convert char to String: String.valueOf(char)
        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(C)Ljava/lang/String;");
        code.invokestatic(valueOfRef);

        // 12. Store in loop variable (as String)
        code.astore(charSlot);

        // 13. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 14. Generate body
        compiler.getStatementGenerator().generate(code, cp, forOfStmt.getBody(), returnTypeInfo);

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
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 22. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
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
                throw new Swc4jByteCodeCompilerException(varDecl, "For-of variable declaration is empty");
            }
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                // Allocate variable in current scope
                int slot = context.getLocalVariableTable().allocateVariable(varName, defaultType);
                // Also register in inferredTypes so TypeResolver can find it
                context.getInferredTypes().put(varName, defaultType);
                // Initialize to null in case loop doesn't execute
                code.aconst_null();
                code.astore(slot);
                return slot;
            } else {
                throw new Swc4jByteCodeCompilerException(name,
                        "For-of variable must be a simple identifier or array pattern");
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Existing variable - look it up (do NOT reinitialize - keep existing value)
            String varName = bindingIdent.getId().getSym();
            var variable = context.getLocalVariableTable().getVariable(varName);
            if (variable == null) {
                throw new Swc4jByteCodeCompilerException(bindingIdent, "Variable not found: " + varName);
            }
            // Update inferredTypes
            context.getInferredTypes().put(varName, defaultType);
            return variable.index();
        } else {
            throw new Swc4jByteCodeCompilerException(left,
                    "Unsupported for-of left type: " + left.getClass().getName());
        }
    }

    /**
     * Enum representing the type of iteration to perform.
     */
    private enum IterationType {
        LIST,
        SET,
        MAP,
        STRING
    }
}
