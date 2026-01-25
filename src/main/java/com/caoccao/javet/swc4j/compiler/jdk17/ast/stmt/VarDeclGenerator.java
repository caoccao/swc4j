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
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

public final class VarDeclGenerator extends BaseAstProcessor<Swc4jAstVarDecl> {
    public VarDeclGenerator(ByteCodeCompiler compiler) {
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
            Swc4jAstVarDecl varDecl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                generateBindingIdentDecl(code, cp, context, declarator, bindingIdent);
            } else if (name instanceof Swc4jAstArrayPat arrayPat) {
                generateArrayPatternDecl(code, cp, context, declarator, arrayPat);
            } else if (name instanceof Swc4jAstObjectPat objectPat) {
                generateObjectPatternDecl(code, cp, context, declarator, objectPat);
            } else {
                throw new Swc4jByteCodeCompilerException(name,
                        "Unsupported pattern type in variable declaration: " + name.getClass().getName());
            }
        }
    }

    /**
     * Generate bytecode for array pattern declaration with destructuring.
     * Example: const [first, second, ...rest] = arr;
     */
    private void generateArrayPatternDecl(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {
        if (declarator.getInit().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(declarator,
                    "Array destructuring requires an initializer");
        }

        // Generate the initializer expression and store in a temp variable
        compiler.getExpressionGenerator().generate(code, cp, declarator.getInit().get(), null);
        int listClass = cp.addClass("java/util/List");
        code.checkcast(listClass);
        int tempListSlot = context.getLocalVariableTable().allocateVariable("$tempList", "Ljava/util/List;");
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef("java/util/List", "get", "(I)Ljava/lang/Object;");
        int listSizeRef = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
        int listAddRef = cp.addInterfaceMethodRef("java/util/List", "add", "(Ljava/lang/Object;)Z");

        int currentIndex = 0;
        int restStartIndex = 0;

        // First pass: count elements before rest and allocate variables
        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                restStartIndex++;
                continue;
            }
            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                if (localVar == null) {
                    localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, "Ljava/lang/Object;");
                }
                if (localVar == null) {
                    int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/Object;");
                    context.getInferredTypes().put(varName, "Ljava/lang/Object;");
                }
                restStartIndex++;
            } else if (elem instanceof Swc4jAstRestPat restPat) {
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                    if (localVar == null) {
                        localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, "Ljava/util/ArrayList;");
                    }
                    if (localVar == null) {
                        int slot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/util/ArrayList;");
                        context.getInferredTypes().put(varName, "Ljava/util/ArrayList;");
                    }
                }
            }
        }

        // Second pass: extract values
        currentIndex = 0;
        for (var optElem : arrayPat.getElems()) {
            if (optElem.isEmpty()) {
                currentIndex++;
                continue;
            }

            ISwc4jAstPat elem = optElem.get();
            if (elem instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

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

                    // Create new ArrayList
                    int arrayListClass = cp.addClass("java/util/ArrayList");
                    int arrayListInitRef = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                    code.newInstance(arrayListClass);
                    code.dup();
                    code.invokespecial(arrayListInitRef);
                    code.astore(localVar.index());

                    // Get source list size
                    code.aload(tempListSlot);
                    code.invokeinterface(listSizeRef, 1);
                    int sizeSlot = context.getLocalVariableTable().allocateVariable("$restSize", "I");
                    code.istore(sizeSlot);

                    // Initialize loop counter at restStartIndex
                    code.iconst(restStartIndex);
                    int iSlot = context.getLocalVariableTable().allocateVariable("$restI", "I");
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
    }

    /**
     * Generate bytecode for a simple binding identifier declaration.
     */
    private void generateBindingIdentDecl(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstBindingIdent bindingIdent) throws Swc4jByteCodeCompilerException {
        String varName = bindingIdent.getId().getSym();
        // First try to get the variable from the current scope chain
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        // If not found in current scope, try to add it from the pre-allocated variables
        if (localVar == null) {
            String varType = compiler.getTypeResolver().extractType(bindingIdent, declarator.getInit());
            localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
        }

        if (declarator.getInit().isPresent()) {
            var init = declarator.getInit().get();

            // Phase 2: Get GenericTypeInfo from context if available (for Record types)
            GenericTypeInfo genericTypeInfo = context.getGenericTypeInfoMap().get(varName);
            ReturnTypeInfo varTypeInfo = ReturnTypeInfo.of(declarator, localVar.type(), genericTypeInfo);

            compiler.getExpressionGenerator().generate(code, cp, init, varTypeInfo);

            // Store the value in the local variable
            switch (localVar.type()) {
                case "I", "S", "C", "Z", "B" -> code.istore(localVar.index());
                case "J" -> code.lstore(localVar.index());
                case "F" -> code.fstore(localVar.index());
                case "D" -> code.dstore(localVar.index());
                default -> code.astore(localVar.index());
            }
        } else {
            // Generate default initialization for variables without initializers
            // This is required by the JVM verifier to track variable initialization
            switch (localVar.type()) {
                case "I", "S", "C", "Z", "B" -> {
                    code.iconst(0);
                    code.istore(localVar.index());
                }
                case "J" -> {
                    code.lconst(0);
                    code.lstore(localVar.index());
                }
                case "F" -> {
                    code.fconst(0);
                    code.fstore(localVar.index());
                }
                case "D" -> {
                    code.dconst(0);
                    code.dstore(localVar.index());
                }
                default -> {
                    code.aconst_null();
                    code.astore(localVar.index());
                }
            }
        }
    }

    /**
     * Generate bytecode for object pattern declaration with destructuring.
     * Example: const { a, b, ...rest } = obj;
     */
    private void generateObjectPatternDecl(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {
        if (declarator.getInit().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(declarator,
                    "Object destructuring requires an initializer");
        }

        // Generate the initializer expression and store in a temp variable
        compiler.getExpressionGenerator().generate(code, cp, declarator.getInit().get(), null);
        int mapClass = cp.addClass("java/util/Map");
        code.checkcast(mapClass);
        int tempMapSlot = context.getLocalVariableTable().allocateVariable("$tempMap", "Ljava/util/Map;");
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef("java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        int mapRemoveRef = cp.addInterfaceMethodRef("java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");

        List<String> extractedKeys = new ArrayList<>();

        // First pass: allocate variables and collect extracted keys
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                if (localVar == null) {
                    localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, "Ljava/lang/Object;");
                }
                if (localVar == null) {
                    context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/Object;");
                    context.getInferredTypes().put(varName, "Ljava/lang/Object;");
                }
            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
                extractedKeys.add(keyName);
                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                    if (localVar == null) {
                        localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, "Ljava/lang/Object;");
                    }
                    if (localVar == null) {
                        context.getLocalVariableTable().allocateVariable(varName, "Ljava/lang/Object;");
                        context.getInferredTypes().put(varName, "Ljava/lang/Object;");
                    }
                }
            } else if (prop instanceof Swc4jAstRestPat restPat) {
                ISwc4jAstPat arg = restPat.getArg();
                if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                    if (localVar == null) {
                        localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, "Ljava/util/LinkedHashMap;");
                    }
                    if (localVar == null) {
                        context.getLocalVariableTable().allocateVariable(varName, "Ljava/util/LinkedHashMap;");
                        context.getInferredTypes().put(varName, "Ljava/util/LinkedHashMap;");
                    }
                }
            }
        }

        // Second pass: extract values
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

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

                    // Create new LinkedHashMap as copy of source map
                    int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
                    int linkedHashMapInitRef = cp.addMethodRef("java/util/LinkedHashMap", "<init>", "(Ljava/util/Map;)V");
                    code.newInstance(linkedHashMapClass);
                    code.dup();
                    code.aload(tempMapSlot);
                    code.invokespecial(linkedHashMapInitRef);
                    code.astore(localVar.index());

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
    }
}
