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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstCatchClause;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPatProp;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstKeyValuePatProp;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;

/**
 * Generator for try-catch-finally statements.
 * <p>
 * Supports three forms:
 * <ul>
 *   <li>try-catch: {@code try { } catch (e) { }}</li>
 *   <li>try-finally: {@code try { } finally { }}</li>
 *   <li>try-catch-finally: {@code try { } catch (e) { } finally { }}</li>
 * </ul>
 * <p>
 * Bytecode patterns follow JVM exception handling:
 * <ul>
 *   <li>Try-catch uses exception table entries to direct control to catch handlers</li>
 *   <li>Finally blocks are duplicated in bytecode for each exit path</li>
 *   <li>Stack map frames are generated at catch handler entry points</li>
 * </ul>
 */
public final class TryStatementProcessor extends BaseAstProcessor<Swc4jAstTryStmt> {
    /**
     * Instantiates a new Try statement processor.
     *
     * @param compiler the compiler
     */
    public TryStatementProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private boolean blockEndsWithTerminal(Swc4jAstBlockStmt block) {
        var stmts = block.getStmts();
        if (stmts.isEmpty()) {
            return false;
        }
        return isTerminalStatement(stmts.get(stmts.size() - 1));
    }

    private boolean containsTerminalStatement(List<ISwc4jAstStmt> stmts) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (isTerminalStatement(stmt)) {
                return true;
            }
        }
        return false;
    }

    private String extractPropertyName(ISwc4jAstPropName propName) throws Swc4jByteCodeCompilerException {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), propName,
                    "Unsupported property name type in catch destructuring: " + propName.getClass().getName());
        }
    }

    /**
     * Generate bytecode for a try statement.
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param tryStmt        the try statement AST node
     * @param returnTypeInfo return type information for the enclosing method
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        boolean hasCatch = tryStmt.getHandler().isPresent();
        boolean hasFinally = tryStmt.getFinalizer().isPresent();

        if (!hasCatch && !hasFinally) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), tryStmt,
                    "Try statement must have at least a catch or finally block");
        }

        // Check if all blocks are empty - in that case, skip the entire try-catch-finally
        // as it's a no-op but would generate invalid bytecode
        boolean tryEmpty = tryStmt.getBlock().getStmts().isEmpty();
        boolean catchEmpty = !hasCatch || tryStmt.getHandler().get().getBody().getStmts().isEmpty();
        boolean finallyEmpty = !hasFinally || tryStmt.getFinalizer().get().getStmts().isEmpty();

        if (tryEmpty && catchEmpty && finallyEmpty) {
            // All blocks are empty, nothing to generate
            return;
        }

        if (hasCatch && hasFinally) {
            generateTryCatchFinally(code, classWriter, tryStmt, returnTypeInfo);
        } else if (hasCatch) {
            generateTryCatch(code, classWriter, tryStmt, returnTypeInfo);
        } else {
            generateTryFinally(code, classWriter, tryStmt, returnTypeInfo);
        }
    }

    private void generateCatchDestructuring(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstObjectPat objectPat,
            int exceptionSlot,
            String exceptionType) throws Swc4jByteCodeCompilerException {
        var context = compiler.getMemory().getCompilationContext();

        // Determine the exception class for method refs (remove L and ; from descriptor)
        String exceptionClassName = exceptionType.substring(1, exceptionType.length() - 1);

        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                // Shorthand property: { message } or { message = "default" }
                String varName = assignProp.getKey().getId().getSym();
                String propertyName = varName;

                // Get the variable type for this property
                String varType = getPropertyType(propertyName);

                // Allocate variable if not already allocated
                LocalVariable variable = context.getLocalVariableTable().getVariable(varName);
                if (variable == null) {
                    context.getLocalVariableTable().allocateVariable(varName, varType);
                    variable = context.getLocalVariableTable().getVariable(varName);
                }

                // Register inferred type so the type resolver can use it
                context.getInferredTypes().put(varName, varType);

                // Generate code to extract the property
                generatePropertyExtraction(code, classWriter, exceptionSlot, propertyName, variable);

                // Handle default value if present
                if (assignProp.getValue().isPresent()) {
                    handleDefaultValue(code, classWriter, assignProp, variable);
                }

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                // Renamed property: { message: msg }
                String propertyName = extractPropertyName(keyValueProp.getKey());
                ISwc4jAstPat valuePat = keyValueProp.getValue();

                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();

                    // Get the variable type for this property
                    String varType = getPropertyType(propertyName);

                    // Allocate variable if not already allocated
                    LocalVariable variable = context.getLocalVariableTable().getVariable(varName);
                    if (variable == null) {
                        context.getLocalVariableTable().allocateVariable(varName, varType);
                        variable = context.getLocalVariableTable().getVariable(varName);
                    }

                    // Register inferred type so the type resolver can use it
                    context.getInferredTypes().put(varName, varType);

                    // Generate code to extract the property
                    generatePropertyExtraction(code, classWriter, exceptionSlot, propertyName, variable);
                } else {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), keyValueProp,
                            "Unsupported value pattern type in catch destructuring: " + valuePat.getClass().getName());
                }
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), prop,
                        "Unsupported property type in catch destructuring: " + prop.getClass().getName());
            }
        }
    }

    private void generateFinallyBlock(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBlockStmt finallyBlock,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        compiler.getStatementProcessor().generate(code, classWriter, finallyBlock.getStmts(), returnTypeInfo);
    }

    private void generatePropertyExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            int exceptionSlot,
            String propertyName,
            LocalVariable variable) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        switch (propertyName) {
            case "message" -> {
                // exception.getMessage() -> String
                code.aload(exceptionSlot);
                int getMessageRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_THROWABLE, "getMessage", ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_STRING);
                code.invokevirtual(getMessageRef);
                code.astore(variable.index());
            }
            case "stack" -> {
                // Arrays.toString(exception.getStackTrace()) -> String
                code.aload(exceptionSlot);
                int getStackTraceRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_THROWABLE, "getStackTrace",
                        ConstantJavaDescriptor.DESCRIPTOR___ARRAY_LJAVA_LANG_STACKTRACEELEMENT);
                code.invokevirtual(getStackTraceRef);
                int arraysToStringRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYS, ConstantJavaMethod.METHOD_TO_STRING,
                        "([Ljava/lang/Object;)Ljava/lang/String;");
                code.invokestatic(arraysToStringRef);
                code.astore(variable.index());
            }
            case "cause" -> {
                // exception.getCause() -> Throwable
                code.aload(exceptionSlot);
                int getCauseRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_THROWABLE, "getCause", ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_THROWABLE);
                code.invokevirtual(getCauseRef);
                code.astore(variable.index());
            }
            case "name" -> {
                // For JsError types, use getName(); for others, use class simple name
                // Use runtime instanceof check since catch type may be Throwable
                int jsErrorClass = cp.addClass("com/caoccao/javet/swc4j/exceptions/JsError");

                // Check if exception instanceof JsError
                code.aload(exceptionSlot);
                code.instanceof_(jsErrorClass);
                code.ifeq(0); // Jump to else if not JsError
                int elseJumpPos = code.getCurrentOffset() - 2;
                int elseJumpOpcodePos = code.getCurrentOffset() - 3;

                // True branch: cast to JsError and call getName()
                code.aload(exceptionSlot);
                code.checkcast(jsErrorClass);
                int getNameRef = cp.addMethodRef("com/caoccao/javet/swc4j/exceptions/JsError", "getName",
                        ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_STRING);
                code.invokevirtual(getNameRef);
                code.astore(variable.index());
                code.gotoLabel(0); // Jump to end
                int endJumpPos = code.getCurrentOffset() - 2;
                int endJumpOpcodePos = code.getCurrentOffset() - 3;

                // Else branch: use getClass().getSimpleName()
                int elseLabel = code.getCurrentOffset();
                code.patchShort(elseJumpPos, elseLabel - elseJumpOpcodePos);
                code.aload(exceptionSlot);
                int getClassRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_OBJECT, "getClass", ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_CLASS);
                code.invokevirtual(getClassRef);
                int getSimpleNameRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_CLASS, "getSimpleName", ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_STRING);
                code.invokevirtual(getSimpleNameRef);
                code.astore(variable.index());

                // End label - patch the goto from true branch
                int endLabel = code.getCurrentOffset();
                code.patchShort(endJumpPos, endLabel - endJumpOpcodePos);
            }
            default -> throw new Swc4jByteCodeCompilerException(getSourceCode(), null,
                    "Unsupported property in catch destructuring: " + propertyName +
                            ". Supported properties: message, stack, cause, name");
        }
    }

    private void generateTryCatch(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        compiler.getStatementProcessor().generate(code, classWriter, tryBlock.getStmts(), returnTypeInfo);
        boolean tryEndsWithTerminal = containsTerminalStatement(tryBlock.getStmts());

        // Record try block end (before goto)
        int tryEndPc = code.getCurrentOffset();

        // Normal exit: skip catch block (only if try doesn't end with terminal statement)
        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;
        if (!tryEndsWithTerminal) {
            code.gotoLabel(0); // Placeholder
            gotoOffsetPos = code.getCurrentOffset() - 2;
            gotoOpcodePos = code.getCurrentOffset() - 3;
        }

        // Generate catch block
        int catchStartPc = code.getCurrentOffset();

        Swc4jAstCatchClause catchClause = tryStmt.getHandler().get();

        // Store exception in local variable if catch has a parameter
        String catchType = ConstantJavaType.LJAVA_LANG_THROWABLE;  // Default to Throwable
        var context = compiler.getMemory().getCompilationContext();
        if (catchClause.getParam().isPresent()) {
            ISwc4jAstPat param = catchClause.getParam().get();
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                String varType = ConstantJavaType.LJAVA_LANG_THROWABLE;

                // Check if variable type annotation exists
                if (bindingIdent.getTypeAnn().isPresent()) {
                    var typeAnn = bindingIdent.getTypeAnn().get();
                    varType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                    catchType = varType;  // Use annotated type for exception table
                }

                // Get the exception variable (already pre-allocated by VariableAnalyzer)
                LocalVariable exceptionVar = context.getLocalVariableTable().getVariable(varName);
                if (exceptionVar == null) {
                    // Fallback: allocate if not pre-allocated
                    context.getLocalVariableTable().allocateVariable(varName, varType);
                    exceptionVar = context.getLocalVariableTable().getVariable(varName);
                }

                // Store exception
                code.astore(exceptionVar.index());
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Object destructuring: catch ({message, stack})
                // Check for type annotation to determine catch type
                if (objectPat.getTypeAnn().isPresent()) {
                    var typeAnn = objectPat.getTypeAnn().get();
                    catchType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                }

                // Store exception in temporary variable for destructuring
                String tempName = "$catchException$" + System.identityHashCode(catchClause);
                context.getLocalVariableTable().allocateVariable(tempName, catchType);
                LocalVariable tempVar = context.getLocalVariableTable().getVariable(tempName);
                code.astore(tempVar.index());

                // Generate destructuring code
                generateCatchDestructuring(code, classWriter, objectPat, tempVar.index(), catchType);
            } else {
                // Other pattern types - pop exception
                code.pop();
            }
        } else {
            // Catch without parameter (ES2019+) - discard exception
            code.pop();
        }

        // Generate catch body
        Swc4jAstBlockStmt catchBody = catchClause.getBody();
        compiler.getStatementProcessor().generate(code, classWriter, catchBody.getStmts(), returnTypeInfo);

        // Mark end of catch-try
        int afterCatchPc = code.getCurrentOffset();

        // Patch goto offset (only if goto was generated)
        if (gotoOffsetPos != -1) {
            int gotoOffset = afterCatchPc - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }

        // Add exception table entry - use the type from annotation if provided
        // Only add if there's actual code to protect
        if (tryStartPc < tryEndPc) {
            String catchClassName = catchType.substring(1, catchType.length() - 1);  // Remove L and ;
            int catchClassIndex = cp.addClass(catchClassName);
            code.addExceptionHandler(tryStartPc, tryEndPc, catchStartPc, catchClassIndex);
        }
    }

    private void generateTryCatchFinally(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Get the finally block and check if it ends with terminal statement
        Swc4jAstBlockStmt finalizerBlock = tryStmt.getFinalizer().get();
        boolean finallyEndsWithTerminal = blockEndsWithTerminal(finalizerBlock);

        // Push finally block onto context so return statements can find it
        var context = compiler.getMemory().getCompilationContext();
        context.pushFinallyBlock(finalizerBlock);

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        compiler.getStatementProcessor().generate(code, classWriter, tryBlock.getStmts(), returnTypeInfo);
        boolean tryEndsWithTerminal = containsTerminalStatement(tryBlock.getStmts());

        // Record try block end
        int tryEndPc = code.getCurrentOffset();

        // Normal path: execute finally and continue (only if try doesn't end with terminal statement)
        int goto1OffsetPos = -1;
        int goto1OpcodePos = -1;
        if (!tryEndsWithTerminal) {
            generateFinallyBlock(code, classWriter, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                goto1OffsetPos = code.getCurrentOffset() - 2;
                goto1OpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Generate catch block
        int catchStartPc = code.getCurrentOffset();

        Swc4jAstCatchClause catchClause = tryStmt.getHandler().get();

        // Store exception in local variable if catch has a parameter
        String catchType = ConstantJavaType.LJAVA_LANG_THROWABLE;  // Default to Throwable
        if (catchClause.getParam().isPresent()) {
            ISwc4jAstPat param = catchClause.getParam().get();
            if (param instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                String varType = ConstantJavaType.LJAVA_LANG_THROWABLE;

                // Check if variable type annotation exists
                if (bindingIdent.getTypeAnn().isPresent()) {
                    var typeAnn = bindingIdent.getTypeAnn().get();
                    varType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                    catchType = varType;  // Use annotated type for exception table
                }

                // Get the exception variable (already pre-allocated by VariableAnalyzer)
                LocalVariable exceptionVar = context.getLocalVariableTable().getVariable(varName);
                if (exceptionVar == null) {
                    // Fallback: allocate if not pre-allocated
                    context.getLocalVariableTable().allocateVariable(varName, varType);
                    exceptionVar = context.getLocalVariableTable().getVariable(varName);
                }

                // Store exception
                code.astore(exceptionVar.index());
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Object destructuring: catch ({message, stack})
                // Check for type annotation to determine catch type
                if (objectPat.getTypeAnn().isPresent()) {
                    var typeAnn = objectPat.getTypeAnn().get();
                    catchType = compiler.getTypeResolver().mapTsTypeToDescriptor(typeAnn.getTypeAnn());
                }

                // Store exception in temporary variable for destructuring
                String tempName = "$catchException$" + System.identityHashCode(catchClause);
                context.getLocalVariableTable().allocateVariable(tempName, catchType);
                LocalVariable tempVar = context.getLocalVariableTable().getVariable(tempName);
                code.astore(tempVar.index());

                // Generate destructuring code
                generateCatchDestructuring(code, classWriter, objectPat, tempVar.index(), catchType);
            } else {
                // Other pattern types - pop exception
                code.pop();
            }
        } else {
            // Catch without parameter (ES2019+) - discard exception
            code.pop();
        }

        // Generate catch body
        Swc4jAstBlockStmt catchBody = catchClause.getBody();
        compiler.getStatementProcessor().generate(code, classWriter, catchBody.getStmts(), returnTypeInfo);
        boolean catchEndsWithTerminal = containsTerminalStatement(catchBody.getStmts());

        // Pop finally block from context (the try and catch block code has been generated)
        context.popFinallyBlock();

        // Record catch block end
        int catchEndPc = code.getCurrentOffset();

        // After catch: execute finally and continue (only if catch doesn't end with terminal statement)
        int goto2OffsetPos = -1;
        int goto2OpcodePos = -1;
        if (!catchEndsWithTerminal) {
            generateFinallyBlock(code, classWriter, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                goto2OffsetPos = code.getCurrentOffset() - 2;
                goto2OpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Finally handler: catches exceptions from both try and catch
        int finallyHandlerPc = code.getCurrentOffset();

        // Allocate temp slot for exception (only if we might rethrow)
        LocalVariable tempException = null;
        if (!finallyEndsWithTerminal) {
            String tempName = "$finallyException$" + System.identityHashCode(tryStmt);
            context.getLocalVariableTable().allocateVariable(tempName, ConstantJavaType.LJAVA_LANG_THROWABLE);
            tempException = context.getLocalVariableTable().getVariable(tempName);

            // Store exception
            code.astore(tempException.index());
        } else {
            // Finally will throw, so we don't need the caught exception
            // But we still need to pop it from the stack
            code.pop();
        }

        // Execute finally
        generateFinallyBlock(code, classWriter, finalizerBlock, returnTypeInfo);

        // Re-throw exception (only if finally doesn't end with terminal)
        if (!finallyEndsWithTerminal) {
            code.aload(tempException.index());
            code.athrow();
        }

        // Mark end
        int afterAllPc = code.getCurrentOffset();

        // Patch goto offsets (only if they were generated)
        if (goto1OffsetPos != -1) {
            int goto1Offset = afterAllPc - goto1OpcodePos;
            code.patchShort(goto1OffsetPos, goto1Offset);
        }

        if (goto2OffsetPos != -1) {
            int goto2Offset = afterAllPc - goto2OpcodePos;
            code.patchShort(goto2OffsetPos, goto2Offset);
        }

        // Add exception table entries (order matters - first match wins)
        // Only add entries if there's actually code to protect
        if (tryStartPc < tryEndPc) {
            // Catch specified type in the try block
            String catchClassName = catchType.substring(1, catchType.length() - 1);  // Remove L and ;
            int catchClassIndex = cp.addClass(catchClassName);
            code.addExceptionHandler(tryStartPc, tryEndPc, catchStartPc, catchClassIndex);
        }
        if (tryStartPc < catchEndPc) {
            // Finally handler catches any exception (type 0)
            code.addExceptionHandler(tryStartPc, catchEndPc, finallyHandlerPc, 0);
        }
    }

    private void generateTryFinally(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstTryStmt tryStmt,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {

        // Get the finally block
        Swc4jAstBlockStmt finalizerBlock = tryStmt.getFinalizer().get();
        boolean finallyEndsWithTerminal = blockEndsWithTerminal(finalizerBlock);

        // Push finally block onto context so return statements can find it
        var context = compiler.getMemory().getCompilationContext();
        context.pushFinallyBlock(finalizerBlock);

        // Record try block start
        int tryStartPc = code.getCurrentOffset();

        // Generate try block
        Swc4jAstBlockStmt tryBlock = tryStmt.getBlock();
        compiler.getStatementProcessor().generate(code, classWriter, tryBlock.getStmts(), returnTypeInfo);
        boolean tryEndsWithTerminal = containsTerminalStatement(tryBlock.getStmts());

        // Pop finally block from context (the try block code has been generated)
        context.popFinallyBlock();

        // Record try block end (before normal path finally)
        int tryEndPc = code.getCurrentOffset();

        // Normal path: execute finally and continue (only if try doesn't end with terminal statement)
        // Note: Return statements inside try blocks handle their own finally execution
        int gotoOffsetPos = -1;
        int gotoOpcodePos = -1;
        if (!tryEndsWithTerminal) {
            generateFinallyBlock(code, classWriter, finalizerBlock, returnTypeInfo);

            // Only need goto if finally doesn't end with terminal
            if (!finallyEndsWithTerminal) {
                code.gotoLabel(0); // Placeholder
                gotoOffsetPos = code.getCurrentOffset() - 2;
                gotoOpcodePos = code.getCurrentOffset() - 3;
            }
        }

        // Exception path: catch any exception, execute finally, re-throw
        int finallyHandlerPc = code.getCurrentOffset();

        // Allocate temp slot for exception (only if we might rethrow)
        LocalVariable tempException = null;
        if (!finallyEndsWithTerminal) {
            String tempName = "$finallyException$" + System.identityHashCode(tryStmt);
            context.getLocalVariableTable().allocateVariable(tempName, ConstantJavaType.LJAVA_LANG_THROWABLE);
            tempException = context.getLocalVariableTable().getVariable(tempName);

            // Store exception
            code.astore(tempException.index());
        } else {
            // Finally will throw, so we don't need the caught exception
            // But we still need to pop it from the stack
            code.pop();
        }

        // Execute finally
        generateFinallyBlock(code, classWriter, finalizerBlock, returnTypeInfo);

        // Re-throw exception (only if finally doesn't end with terminal)
        if (!finallyEndsWithTerminal) {
            code.aload(tempException.index());
            code.athrow();
        }

        // Mark end
        int afterFinallyPc = code.getCurrentOffset();

        // Patch goto offset (only if generated)
        if (gotoOffsetPos != -1) {
            int gotoOffset = afterFinallyPc - gotoOpcodePos;
            code.patchShort(gotoOffsetPos, gotoOffset);
        }

        // Add exception table entry (catch_type=0 means catch all exceptions)
        // Only add if there's actual code to protect
        if (tryStartPc < tryEndPc) {
            code.addExceptionHandler(tryStartPc, tryEndPc, finallyHandlerPc, 0);
        }
    }

    private String getPropertyType(String propertyName) {
        return switch (propertyName) {
            case "message", "stack", "name" -> ConstantJavaType.LJAVA_LANG_STRING;
            case "cause" -> ConstantJavaType.LJAVA_LANG_THROWABLE;
            default -> ConstantJavaType.LJAVA_LANG_OBJECT;
        };
    }

    private void handleDefaultValue(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstAssignPatProp assignProp,
            LocalVariable variable) throws Swc4jByteCodeCompilerException {
        // Load the current value
        code.aload(variable.index());
        // If not null, skip the default value assignment
        code.ifnonnull(0); // Placeholder
        int skipDefaultPos = code.getCurrentOffset() - 2;

        // Value is null, generate default value and store
        compiler.getExpressionProcessor().generate(code, classWriter, assignProp.getValue().get(), null);
        code.astore(variable.index());

        // Patch the skip jump
        int afterDefaultLabel = code.getCurrentOffset();
        int skipOffset = afterDefaultLabel - (skipDefaultPos - 1);
        code.patchShort(skipDefaultPos, (short) skipOffset);
    }

    private boolean isTerminalStatement(ISwc4jAstStmt stmt) {
        if (stmt instanceof Swc4jAstBlockStmt blockStmt) {
            return containsTerminalStatement(blockStmt.getStmts());
        }
        return stmt instanceof Swc4jAstBreakStmt ||
                stmt instanceof Swc4jAstContinueStmt ||
                stmt instanceof Swc4jAstReturnStmt ||
                stmt instanceof Swc4jAstThrowStmt;
    }
}
