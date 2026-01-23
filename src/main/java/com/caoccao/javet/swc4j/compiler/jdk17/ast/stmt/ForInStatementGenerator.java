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
import com.caoccao.javet.swc4j.compiler.memory.LoopLabelInfo;
import com.caoccao.javet.swc4j.compiler.memory.PatchInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for for-in loops.
 * <p>
 * For-in loops iterate over the keys of an object (LinkedHashMap) or indices of an array (ArrayList).
 * <p>
 * Bytecode pattern for objects (LinkedHashMap):
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
 * Bytecode pattern for arrays (ArrayList):
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
     * Generate bytecode for a for-in statement (potentially labeled).
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
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Enter scope for for-in loop variables
        context.getLocalVariableTable().enterScope();

        // Infer type of right expression
        String rightType = compiler.getTypeResolver().inferTypeFromExpr(forInStmt.getRight());

        if ("Ljava/util/LinkedHashMap;".equals(rightType)) {
            generateObjectIteration(code, cp, forInStmt, labelName, returnTypeInfo);
        } else if ("Ljava/util/ArrayList;".equals(rightType)) {
            generateArrayIteration(code, cp, forInStmt, labelName, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(
                "For-in loops require LinkedHashMap or ArrayList, got: " + rightType);
        }

        // Exit scope
        context.getLocalVariableTable().exitScope();
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

        // 10. Store in loop variable
        int keySlot = allocateLoopVariable(forInStmt.getLeft());
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
     * Generate bytecode for iterating over an array (ArrayList).
     */
    private void generateArrayIteration(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstForInStmt forInStmt,
            String labelName,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

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

        // 8. Convert counter to String: String.valueOf(int) -> String
        code.iload(counterSlot);
        int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(I)Ljava/lang/String;");
        code.invokestatic(valueOfRef);

        // 9. Store in loop variable
        int keySlot = allocateLoopVariable(forInStmt.getLeft());
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
     * Allocate local variable slot for the loop variable.
     * Handles both variable declarations and existing variables.
     */
    private int allocateLoopVariable(ISwc4jAstForHead left) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();

        if (left instanceof Swc4jAstVarDecl varDecl) {
            // Variable declaration: let key or const key
            if (varDecl.getDecls().isEmpty()) {
                throw new Swc4jByteCodeCompilerException("For-in variable declaration is empty");
            }
            Swc4jAstVarDeclarator decl = varDecl.getDecls().get(0);
            ISwc4jAstPat name = decl.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                // Allocate variable in current scope (keys/indices are always strings)
                int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/String;");
                // Also register in inferredTypes so TypeResolver can find it
                context.getInferredTypes().put(varName, "Ljava/lang/String;");
                return slot;
            } else {
                throw new Swc4jByteCodeCompilerException("For-in variable must be a simple identifier");
            }
        } else if (left instanceof Swc4jAstBindingIdent bindingIdent) {
            // Existing variable - look it up
            String varName = bindingIdent.getId().getSym();
            var variable = context.getLocalVariableTable().getVariable(varName);
            if (variable == null) {
                throw new Swc4jByteCodeCompilerException("Variable not found: " + varName);
            }
            return variable.index();
        } else {
            throw new Swc4jByteCodeCompilerException("Unsupported for-in left type: " + left.getClass().getName());
        }
    }
}
