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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForInStmt;
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

/**
 * Generator for for-in loops.
 * <p>
 * For-in loops iterate over the keys of an object (Map), indices of an array (List),
 * or indices of a string (String). Type checking is performed at compile-time using
 * the type hierarchy registered in ScopedJavaTypeRegistry.
 * <p>
 * Bytecode pattern for objects (Map):
 * <pre>
 *   aload obj                              // Load object
 *   invokeinterface Map.keySet()           // Get key set
 *   invokeinterface Set.iterator()         // Get iterator
 *   astore iterator                        // Store iterator
 *   TEST_LABEL:                            // Loop entry point
 *   aload iterator
 *   invokeinterface Iterator.hasNext()
 *   ifeq END_LABEL                         // Exit if no more elements
 *   aload iterator
 *   invokeinterface Iterator.next()
 *   invokestatic String.valueOf()          // Convert key to string
 *   astore key                             // Store key variable
 *   [body statements]
 *   goto TEST_LABEL                        // Jump back to test
 *   END_LABEL:                             // Break target
 * </pre>
 * <p>
 * Bytecode pattern for arrays (List):
 * <pre>
 *   aload arr                              // Load array
 *   invokeinterface List.size()            // Get size
 *   istore size                            // Store size
 *   iconst_0                               // i = 0
 *   istore counter                         // Store counter
 *   TEST_LABEL:                            // Loop entry point
 *   iload counter
 *   iload size
 *   if_icmpge END_LABEL                    // Exit if i >= size
 *   iload counter
 *   invokestatic String.valueOf(I)         // Convert index to string
 *   astore key                             // Store key variable
 *   [body statements]
 *   iinc counter, 1                        // i++
 *   goto TEST_LABEL                        // Jump back to test
 *   END_LABEL:                             // Break target
 * </pre>
 */
public final class ForInStatementGenerator extends BaseAstProcessor<Swc4jAstForInStmt> {
    public ForInStatementGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private IterationType determineIterationType(ISwc4jAstExpr astExpr) throws Swc4jByteCodeCompilerException {
        String typeDescriptor = compiler.getTypeResolver().inferTypeFromExpr(astExpr);
        if (typeDescriptor == null) {
            throw new Swc4jByteCodeCompilerException(astExpr,
                    "Cannot determine type of for-in expression. Please add explicit type annotation.");
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
                // isAssignableTo() will use Class.forName-based checking for these
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
            if (typeInfo.isAssignableTo("Ljava/util/Map;")) {
                return IterationType.MAP;
            }
        }

        throw new Swc4jByteCodeCompilerException(astExpr,
                "For-in loops require List, Map, or String type, but got: " + typeDescriptor);
    }

    /**
     * Generate bytecode for a for-in statement (potentially labeled).
     * <p>
     * Uses compile-time type checking to determine iteration strategy:
     * - List: iterate over indices (0, 1, 2, ...)
     * - Map: iterate over keys
     * - String: iterate over character indices (0, 1, 2, ...)
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param forInStmt      the for-in statement AST node
     * @param labelName      the label name (null for unlabeled loops)
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Determine iteration strategy based on compile-time type
        IterationType iterationType = determineIterationType(forInStmt.getRight());

        // Generate the appropriate iteration code
        switch (iterationType) {
            case LIST -> generateArrayIteration(code, cp, forInStmt, labelName, returnTypeInfo);
            case MAP -> generateObjectIteration(code, cp, forInStmt, labelName, returnTypeInfo);
            case STRING -> generateStringIteration(code, cp, forInStmt, labelName, returnTypeInfo);
        }
    }

    /**
     * Generate bytecode for a for-in statement (unlabeled).
     *
     * @param code           the code builder
     * @param cp             the constant pool
     * @param forInStmt      the for-in statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generate(code, cp, forInStmt, null, returnTypeInfo);
    }

    /**
     * Generate bytecode for iterating over an array (ArrayList).
     * Indices are converted to String to match JavaScript for-in semantics.
     */
    private void generateArrayIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Initialize loop variable with String type (for-in keys are always strings in JS)
        int keySlot = initializeLoopVariable(code, forInStmt.getLeft());

        // 1. Generate right expression (array)
        compiler.getExpressionGenerator().generate(code, cp, forInStmt.getRight(), null);

        // 2. Get size: List.size() -> int
        int sizeRef = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
        code.invokeinterface(sizeRef, 1);

        // 3. Store size in temporary variable
        int sizeSlot = context.getLocalVariableTable().allocateVariable("$size", "I");
        code.istore(sizeSlot);

        // 4. Initialize counter: i = 0
        code.iconst(0);
        int counterSlot = context.getLocalVariableTable().allocateVariable("$i", "I");
        code.istore(counterSlot);

        // 5. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 6. Test counter < size
        code.iload(counterSlot);
        code.iload(sizeSlot);

        // 7. Jump to end if i >= size
        code.if_icmpge(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 8. Convert counter to String: String.valueOf(int)
        code.iload(counterSlot);
        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(I)Ljava/lang/String;");
        code.invokestatic(valueOfRef);

        // 9. Store in loop variable (as String)
        code.astore(keySlot);

        // 10. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 11. Generate body
        compiler.getStatementGenerator().generate(code, cp, forInStmt.getBody(), returnTypeInfo);

        // 12. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 13. Mark update label (continue target)
        int updateLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(updateLabel);

        // 14. Increment counter: i++
        code.iinc(counterSlot, 1);

        // 15. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 16. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 17. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 18. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 19. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode for iterating over an object (LinkedHashMap).
     */
    private void generateObjectIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Initialize existing variable to null if needed (in case loop doesn't execute)
        int keySlot = initializeLoopVariable(code, forInStmt.getLeft());

        // 1. Generate right expression (object)
        compiler.getExpressionGenerator().generate(code, cp, forInStmt.getRight(), null);

        // 2. Get keySet: Map.keySet() -> Set
        int keySetRef = cp.addInterfaceMethodRef("java/util/Map", "keySet", "()Ljava/util/Set;");
        code.invokeinterface(keySetRef, 1);

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

        // 8. Get next element: Iterator.next() -> Object
        code.aload(iteratorSlot);
        int nextRef = cp.addInterfaceMethodRef("java/util/Iterator", "next", "()Ljava/lang/Object;");
        code.invokeinterface(nextRef, 1);

        // 9. Convert to String: String.valueOf(Object) -> String
        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        code.invokestatic(valueOfRef);

        // 10. Store in loop variable (keySlot already initialized at method start)
        code.astore(keySlot);

        // 11. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);
        continueLabel.setTargetOffset(testLabel);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 12. Generate body
        compiler.getStatementGenerator().generate(code, cp, forInStmt.getBody(), returnTypeInfo);

        // 13. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 14. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 15. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 16. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 17. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 18. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = testLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Generate bytecode for iterating over a string (String).
     * Indices are converted to String to match JavaScript for-in semantics.
     */
    private void generateStringIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Initialize loop variable with String type (for-in keys are always strings in JS)
        int keySlot = initializeLoopVariable(code, forInStmt.getLeft());

        // 1. Generate right expression (string)
        compiler.getExpressionGenerator().generate(code, cp, forInStmt.getRight(), null);

        // 2. Get length: String.length() -> int
        int lengthRef = cp.addMethodRef("java/lang/String", "length", "()I");
        code.invokevirtual(lengthRef);

        // 3. Store length in temporary variable
        int lengthSlot = context.getLocalVariableTable().allocateVariable("$length", "I");
        code.istore(lengthSlot);

        // 4. Initialize counter: i = 0
        code.iconst(0);
        int counterSlot = context.getLocalVariableTable().allocateVariable("$i", "I");
        code.istore(counterSlot);

        // 5. Mark test label (loop entry point)
        int testLabel = code.getCurrentOffset();

        // 6. Test counter < length
        code.iload(counterSlot);
        code.iload(lengthSlot);

        // 7. Jump to end if i >= length
        code.if_icmpge(0); // Placeholder
        int exitJumpPos = code.getCurrentOffset() - 2;

        // 8. Convert counter to String: String.valueOf(int)
        code.iload(counterSlot);
        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(I)Ljava/lang/String;");
        code.invokestatic(valueOfRef);

        // 9. Store in loop variable (as String)
        code.astore(keySlot);

        // 10. Setup break/continue labels
        LoopLabelInfo breakLabel = new LoopLabelInfo(labelName);
        LoopLabelInfo continueLabel = new LoopLabelInfo(labelName);

        context.pushBreakLabel(breakLabel);
        context.pushContinueLabel(continueLabel);

        // 11. Generate body
        compiler.getStatementGenerator().generate(code, cp, forInStmt.getBody(), returnTypeInfo);

        // 12. Pop labels
        context.popContinueLabel();
        context.popBreakLabel();

        // 13. Mark update label (continue target)
        int updateLabel = code.getCurrentOffset();
        continueLabel.setTargetOffset(updateLabel);

        // 14. Increment counter: i++
        code.iinc(counterSlot, 1);

        // 15. Jump back to test
        code.gotoLabel(0); // Placeholder
        int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
        int backwardGotoOpcodePos = code.getCurrentOffset() - 3;
        int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
        code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

        // 16. Mark end label
        int endLabel = code.getCurrentOffset();
        breakLabel.setTargetOffset(endLabel);

        // 17. Patch exit jump
        int exitOffset = endLabel - (exitJumpPos - 1);
        code.patchShort(exitJumpPos, (short) exitOffset);

        // 18. Patch all break statements
        for (PatchInfo patchInfo : breakLabel.getPatchPositions()) {
            int offset = endLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }

        // 19. Patch all continue statements
        for (PatchInfo patchInfo : continueLabel.getPatchPositions()) {
            int offset = updateLabel - patchInfo.opcodePos();
            code.patchShort(patchInfo.offsetPos(), offset);
        }
    }

    /**
     * Initialize loop variable and return its slot index.
     * For-in keys are always strings in JavaScript semantics.
     */
    private int initializeLoopVariable(CodeBuilder code, ISwc4jAstForHead left) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        if (left instanceof Swc4jAstVarDecl varDecl) {
            // Variable declaration: let key or const key
            if (varDecl.getDecls().isEmpty()) {
                throw new Swc4jByteCodeCompilerException(varDecl, "For-in variable declaration is empty");
            }
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                // Allocate variable in current scope (object keys are strings)
                int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/String;");
                // Also register in inferredTypes so TypeResolver can find it
                context.getInferredTypes().put(varName, "Ljava/lang/String;");
                // Initialize to null in case loop doesn't execute
                code.aconst_null();
                code.astore(slot);
                return slot;
            } else {
                throw new Swc4jByteCodeCompilerException(name, "For-in variable must be a simple identifier");
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Existing variable - look it up (do NOT reinitialize - keep existing value)
            String varName = bindingIdent.getId().getSym();
            var variable = context.getLocalVariableTable().getVariable(varName);
            if (variable == null) {
                throw new Swc4jByteCodeCompilerException(bindingIdent, "Variable not found: " + varName);
            }
            // Update inferredTypes to String for object keys
            context.getInferredTypes().put(varName, "Ljava/lang/String;");
            return variable.index();
        } else {
            throw new Swc4jByteCodeCompilerException(left, "Unsupported for-in left type: " + left.getClass().getName());
        }
    }

    /**
     * Enum representing the type of iteration to perform.
     */
    private enum IterationType {
        LIST,
        MAP,
        STRING
    }
}
