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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.ArrowExpressionProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Var decl processor.
 */
public final class VarDeclProcessor extends BaseAstProcessor<Swc4jAstVarDecl> {
    /**
     * Instantiates a new Var decl processor.
     *
     * @param compiler the compiler
     */
    public VarDeclProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Allocate variables for nested patterns recursively.
     */
    private void allocateNestedPatternVariables(CompilationContext context, ISwc4jAstPat pat, boolean allowShadow) {
        if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
            allocateVariableIfNeeded(context, bindingIdent.getId().getSym(), ConstantJavaType.LJAVA_LANG_OBJECT, allowShadow);
        } else if (pat instanceof Swc4jAstArrayPat arrayPat) {
            for (var optElem : arrayPat.getElems()) {
                if (optElem.isPresent()) {
                    ISwc4jAstPat elem = optElem.get();
                    if (elem instanceof Swc4jAstRestPat restPat) {
                        allocateRestPatternVariable(context, restPat, true, allowShadow);
                    } else {
                        allocateNestedPatternVariables(context, elem, allowShadow);
                    }
                }
            }
        } else if (pat instanceof Swc4jAstObjectPat objectPat) {
            for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
                if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                    allocateVariableIfNeeded(context, assignProp.getKey().getId().getSym(), ConstantJavaType.LJAVA_LANG_OBJECT, allowShadow);
                } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                    allocateNestedPatternVariables(context, keyValueProp.getValue(), allowShadow);
                } else if (prop instanceof Swc4jAstRestPat restPat) {
                    allocateRestPatternVariable(context, restPat, false, allowShadow);
                }
            }
        }
    }

    /**
     * Allocate variable for rest pattern.
     */
    private void allocateRestPatternVariable(
            CompilationContext context,
            Swc4jAstRestPat restPat,
            boolean isArrayRest,
            boolean allowShadow) {
        ISwc4jAstPat arg = restPat.getArg();
        if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
            String varName = bindingIdent.getId().getSym();
            String varType = isArrayRest ? ConstantJavaType.LJAVA_UTIL_ARRAYLIST : ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
            allocateVariableIfNeeded(context, varName, varType, allowShadow);
        }
    }

    /**
     * Allocate variable if it doesn't exist.
     */
    private void allocateVariableIfNeeded(CompilationContext context, String varName, String varType, boolean allowShadow) {
        LocalVariable localVar = context.getLocalVariableTable().getVariableInCurrentScope(varName);
        if (localVar == null && !allowShadow) {
            localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
            }
        }
        if (localVar == null) {
            context.getLocalVariableTable().allocateVariable(varName, varType);
            context.getInferredTypes().put(varName, varType);
        }
    }

    /**
     * Checks if an arrow expression references a specific variable name.
     * Used to detect self-referencing (recursive) arrows.
     *
     * @param arrowExpr the arrow expression to check
     * @param varName   the variable name to look for
     * @return true if the arrow body references the variable
     */
    private boolean arrowReferencesSelf(Swc4jAstArrowExpr arrowExpr, String varName) {
        Set<String> identifiers = new HashSet<>();
        collectIdentifiersRecursive(arrowExpr.getBody(), identifiers);
        return identifiers.contains(varName);
    }

    /**
     * Recursively collects all identifier names from an AST node.
     */
    private void collectIdentifiersRecursive(Object node, Set<String> identifiers) {
        if (node == null) {
            return;
        }
        if (node instanceof Swc4jAstIdent ident) {
            identifiers.add(ident.getSym());
        } else if (node instanceof ISwc4jAst ast) {
            for (var child : ast.getChildNodes()) {
                collectIdentifiersRecursive(child, identifiers);
            }
        }
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
            throw new Swc4jByteCodeCompilerException(getSourceCode(), propName,
                    "Unsupported property name type: " + propName.getClass().getName());
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstVarDecl varDecl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();
        boolean allowShadow = varDecl.getKind() != Swc4jAstVarDeclKind.Var;
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                generateBindingIdentDecl(code, classWriter, context, declarator, bindingIdent, allowShadow);
            } else if (name instanceof Swc4jAstArrayPat arrayPat) {
                generateArrayPatternDecl(code, classWriter, context, declarator, arrayPat, allowShadow);
            } else if (name instanceof Swc4jAstObjectPat objectPat) {
                generateObjectPatternDecl(code, classWriter, context, declarator, objectPat, allowShadow);
            } else {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), name,
                        "Unsupported pattern type in variable declaration: " + name.getClass().getName());
            }
        }
    }

    /**
     * Generate bytecode for array pattern declaration with destructuring.
     * Example: const [first, second, ...rest] = arr;
     * Also supports nested patterns: const [a, [b, ...inner], ...outer] = nested;
     */
    private void generateArrayPatternDecl(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstArrayPat arrayPat,
            boolean allowShadow) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (declarator.getInit().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), declarator,
                    "Array destructuring requires an initializer");
        }

        // Generate the initializer expression and store in a temp variable
        compiler.getExpressionProcessor().generate(code, classWriter, declarator.getInit().get(), null);
        generateArrayPatternExtraction(code, classWriter, context, arrayPat, allowShadow);
    }

    /**
     * Generate bytecode for array pattern extraction from a value already on the stack.
     * This method handles both top-level and nested array patterns.
     */
    private void generateArrayPatternExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstArrayPat arrayPat,
            boolean allowShadow) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        int listClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LIST);
        code.checkcast(listClass);
        int tempListSlot = getOrAllocateTempSlot(context, "$tempList" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_LIST);
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_OBJECT);
        int listSizeRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.DESCRIPTOR___I);
        int listAddRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);

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
                allocateVariableIfNeeded(context, varName, ConstantJavaType.LJAVA_LANG_OBJECT, allowShadow);
                restStartIndex++;
            } else if (elem instanceof Swc4jAstArrayPat || elem instanceof Swc4jAstObjectPat) {
                // Nested pattern - allocate variables recursively
                allocateNestedPatternVariables(context, elem, allowShadow);
                restStartIndex++;
            } else if (elem instanceof Swc4jAstRestPat restPat) {
                allocateRestPatternVariable(context, restPat, true, allowShadow);
            }
        }

        // Second pass: extract values
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

                // Load list and call get(index)
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                code.astore(localVar.index());
                currentIndex++;

            } else if (elem instanceof Swc4jAstArrayPat nestedArrayPat) {
                // Nested array pattern: [a, [b, ...inner], ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested array pattern
                generateArrayPatternExtraction(code, classWriter, context, nestedArrayPat, allowShadow);
                currentIndex++;

            } else if (elem instanceof Swc4jAstObjectPat nestedObjectPat) {
                // Nested object pattern: [a, {b, ...inner}, ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested object pattern
                generateObjectPatternExtraction(code, classWriter, context, nestedObjectPat, allowShadow);
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                generateRestPatternExtraction(code, classWriter, context, restPat, tempListSlot, restStartIndex, true, listGetRef, listSizeRef, listAddRef);
            }
        }
    }

    /**
     * Generate bytecode for a simple binding identifier declaration.
     */
    private void generateBindingIdentDecl(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstBindingIdent bindingIdent,
            boolean allowShadow) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        String varName = bindingIdent.getId().getSym();
        LocalVariable localVar = allowShadow ?
                context.getLocalVariableTable().getVariableInCurrentScope(varName) :
                context.getLocalVariableTable().getVariable(varName);
        if (localVar == null) {
            String varType = compiler.getTypeResolver().extractType(bindingIdent, declarator.getInit());
            if (!allowShadow) {
                localVar = context.getLocalVariableTable().addExistingVariableToCurrentScope(varName, varType);
            }
            if (localVar == null) {
                context.getLocalVariableTable().allocateVariable(varName, varType);
                context.getInferredTypes().put(varName, varType);
                localVar = context.getLocalVariableTable().getVariable(varName);
            }
        }

        if (declarator.getInit().isPresent()) {
            var init = declarator.getInit().get();

            // Pre-register the variable type before generating the initializer.
            // This is essential for recursive arrows that need to reference themselves.
            // For example: const factorial: IntUnaryOperator = (n: int) => n <= 1 ? 1 : n * factorial.applyAsInt(n - 1)
            // Without this, the arrow body wouldn't know the type of 'factorial'.
            context.getInferredTypes().put(varName, localVar.type());

            // Check if this is a self-referencing (recursive) arrow expression.
            // If so, we need to pre-initialize the variable to null before creating the lambda.
            // This is because the lambda will capture this variable, and the JVM verifier
            // requires that captured variables be initialized before use.
            boolean isSelfReferencingArrow = init instanceof Swc4jAstArrowExpr arrowExpr
                    && arrowReferencesSelf(arrowExpr, varName);

            // Phase 2: Get GenericTypeInfo from context if available (for Record types)
            GenericTypeInfo genericTypeInfo = context.getGenericTypeInfoMap().get(varName);
            ReturnTypeInfo varTypeInfo = ReturnTypeInfo.of(getSourceCode(), declarator, localVar.type(), genericTypeInfo);

            // Check if this variable needs a holder (mutable variable captured by lambda)
            if (localVar.needsHolder()) {
                generateHolderInitialization(code, classWriter, localVar, init, varTypeInfo);
            } else if (isSelfReferencingArrow) {
                // Pre-initialize to null (for reference types) or default value (for primitives)
                // This ensures the variable slot is initialized when captured by the lambda
                switch (localVar.type()) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_SHORT,
                         ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_BOOLEAN,
                         ConstantJavaType.ABBR_BYTE -> {
                        code.iconst(0);
                        code.istore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_LONG -> {
                        code.lconst(0);
                        code.lstore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_FLOAT -> {
                        code.fconst(0);
                        code.fstore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_DOUBLE -> {
                        code.dconst(0);
                        code.dstore(localVar.index());
                    }
                    default -> {
                        code.aconst_null();
                        code.astore(localVar.index());
                    }
                }

                // Call arrow generator directly with self-reference name
                Swc4jAstArrowExpr arrowExpr = (Swc4jAstArrowExpr) init;
                ArrowExpressionProcessor.SelfReferenceInfo selfRefInfo =
                        compiler.getArrowExpressionProcessor().generate(code, classWriter, arrowExpr, varTypeInfo, varName);

                // Store the lambda in the local variable
                code.astore(localVar.index());

                // If there's a self-reference, update the captured field after storing
                // The captured field was initialized to null during construction, now we update it
                // with the actual lambda reference so recursive calls work
                if (selfRefInfo != null) {
                    // Load the lambda reference
                    code.aload(localVar.index());
                    // Dup for both objectref and value (putfield pops both)
                    code.dup();
                    // Store into captured field: putfield captured$<varName>
                    int fieldRef = cp.addFieldRef(selfRefInfo.lambdaClassName(),
                            "captured$" + selfRefInfo.fieldName(), selfRefInfo.fieldType());
                    code.putfield(fieldRef);
                }
            } else {
                // Normal case: generate expression and store
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);

                // Store the value in the local variable
                switch (localVar.type()) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_SHORT,
                         ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_BOOLEAN,
                         ConstantJavaType.ABBR_BYTE -> code.istore(localVar.index());
                    case ConstantJavaType.ABBR_LONG -> code.lstore(localVar.index());
                    case ConstantJavaType.ABBR_FLOAT -> code.fstore(localVar.index());
                    case ConstantJavaType.ABBR_DOUBLE -> code.dstore(localVar.index());
                    default -> code.astore(localVar.index());
                }
            }
        } else {
            // Check if this variable needs a holder even without initializer
            if (localVar.needsHolder()) {
                generateHolderDefaultInitialization(code, classWriter, localVar);
            } else {
                // Generate default initialization for variables without initializers
                // This is required by the JVM verifier to track variable initialization
                switch (localVar.type()) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_SHORT,
                         ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_BOOLEAN,
                         ConstantJavaType.ABBR_BYTE -> {
                        code.iconst(0);
                        code.istore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_LONG -> {
                        code.lconst(0);
                        code.lstore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_FLOAT -> {
                        code.fconst(0);
                        code.fstore(localVar.index());
                    }
                    case ConstantJavaType.ABBR_DOUBLE -> {
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
    }

    /**
     * Generate holder array with default value for a variable without initializer.
     * Creates: type[] holder = new type[1]; // element initialized to default
     */
    private void generateHolderDefaultInitialization(
            CodeBuilder code,
            ClassWriter classWriter,
            LocalVariable localVar) {
        var cp = classWriter.getConstantPool();
        String type = localVar.type();
        int holderSlot = localVar.holderIndex();

        switch (type) {
            case ConstantJavaType.ABBR_INTEGER -> {
                code.iconst(1);
                code.newarray(10); // T_INT
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_LONG -> {
                code.iconst(1);
                code.newarray(11); // T_LONG
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                code.iconst(1);
                code.newarray(7); // T_DOUBLE
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_FLOAT -> {
                code.iconst(1);
                code.newarray(6); // T_FLOAT
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_BOOLEAN -> {
                code.iconst(1);
                code.newarray(4); // T_BOOLEAN
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_BYTE -> {
                code.iconst(1);
                code.newarray(8); // T_BYTE
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_CHARACTER -> {
                code.iconst(1);
                code.newarray(5); // T_CHAR
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_SHORT -> {
                code.iconst(1);
                code.newarray(9); // T_SHORT
                code.astore(holderSlot);
            }
            default -> {
                // Reference type - create an array of the appropriate element type
                code.iconst(1);
                // Extract element type from descriptor (e.g., ConstantJavaType.LJAVA_LANG_STRING -> ConstantJavaType.JAVA_LANG_STRING)
                String elementType;
                if (type.startsWith("L") && type.endsWith(";")) {
                    elementType = type.substring(1, type.length() - 1);
                } else {
                    elementType = ConstantJavaType.JAVA_LANG_OBJECT;
                }
                int elementClass = cp.addClass(elementType);
                code.anewarray(elementClass);
                code.astore(holderSlot);
            }
        }
    }

    /**
     * Generate holder array initialization with a value.
     * Creates: type[] holder = new type[] { value };
     */
    private void generateHolderInitialization(
            CodeBuilder code,
            ClassWriter classWriter,
            LocalVariable localVar,
            com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr init,
            ReturnTypeInfo varTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        String type = localVar.type();
        int holderSlot = localVar.holderIndex();

        switch (type) {
            case ConstantJavaType.ABBR_INTEGER -> {
                code.iconst(1);
                code.newarray(10); // T_INT
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.iastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_LONG -> {
                code.iconst(1);
                code.newarray(11); // T_LONG
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.lastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                code.iconst(1);
                code.newarray(7); // T_DOUBLE
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.dastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_FLOAT -> {
                code.iconst(1);
                code.newarray(6); // T_FLOAT
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.fastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_BOOLEAN -> {
                code.iconst(1);
                code.newarray(4); // T_BOOLEAN
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.bastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_BYTE -> {
                code.iconst(1);
                code.newarray(8); // T_BYTE
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.bastore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_CHARACTER -> {
                code.iconst(1);
                code.newarray(5); // T_CHAR
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.castore();
                code.astore(holderSlot);
            }
            case ConstantJavaType.ABBR_SHORT -> {
                code.iconst(1);
                code.newarray(9); // T_SHORT
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.sastore();
                code.astore(holderSlot);
            }
            default -> {
                // Reference type - create an array of the appropriate element type
                code.iconst(1);
                // Extract element type from descriptor (e.g., ConstantJavaType.LJAVA_LANG_STRING -> ConstantJavaType.JAVA_LANG_STRING)
                String elementType;
                if (type.startsWith("L") && type.endsWith(";")) {
                    elementType = type.substring(1, type.length() - 1);
                } else {
                    elementType = ConstantJavaType.JAVA_LANG_OBJECT;
                }
                int elementClass = cp.addClass(elementType);
                code.anewarray(elementClass);
                code.dup();
                code.iconst(0);
                compiler.getExpressionProcessor().generate(code, classWriter, init, varTypeInfo);
                code.aastore();
                code.astore(holderSlot);
            }
        }
    }

    /**
     * Generate bytecode for object pattern declaration with destructuring.
     * Example: const { a, b, ...rest } = obj;
     * Also supports nested patterns: const { x, nested: { y, ...innerRest }, ...outerRest } = obj;
     */
    private void generateObjectPatternDecl(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstVarDeclarator declarator,
            Swc4jAstObjectPat objectPat,
            boolean allowShadow) throws Swc4jByteCodeCompilerException {
        if (declarator.getInit().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), declarator,
                    "Object destructuring requires an initializer");
        }

        // Generate the initializer expression and extract patterns
        compiler.getExpressionProcessor().generate(code, classWriter, declarator.getInit().get(), null);
        generateObjectPatternExtraction(code, classWriter, context, objectPat, allowShadow);
    }

    /**
     * Generate bytecode for object pattern extraction from a value already on the stack.
     * This method handles both top-level and nested object patterns.
     */
    private void generateObjectPatternExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstObjectPat objectPat,
            boolean allowShadow) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        int mapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_MAP);
        code.checkcast(mapClass);
        int tempMapSlot = getOrAllocateTempSlot(context, "$tempMap" + context.getNextTempId(), ConstantJavaType.LJAVA_UTIL_MAP);
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
        int mapRemoveRef = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_MAP, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);

        List<String> extractedKeys = new ArrayList<>();

        // First pass: allocate variables and collect extracted keys
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
                allocateVariableIfNeeded(context, varName, ConstantJavaType.LJAVA_LANG_OBJECT, allowShadow);
            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
                extractedKeys.add(keyName);
                ISwc4jAstPat valuePat = keyValueProp.getValue();
                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    allocateVariableIfNeeded(context, bindingIdent.getId().getSym(), ConstantJavaType.LJAVA_LANG_OBJECT, allowShadow);
                } else if (valuePat instanceof Swc4jAstArrayPat || valuePat instanceof Swc4jAstObjectPat) {
                    // Nested pattern - allocate variables recursively
                    allocateNestedPatternVariables(context, valuePat, allowShadow);
                }
            } else if (prop instanceof Swc4jAstRestPat restPat) {
                allocateRestPatternVariable(context, restPat, false, allowShadow);
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

                // Load map and call get(key)
                code.aload(tempMapSlot);
                int stringIndex = cp.addString(keyName);
                code.ldc(stringIndex);
                code.invokeinterface(mapGetRef, 2);

                if (valuePat instanceof Swc4jAstBindingIdent bindingIdent) {
                    String varName = bindingIdent.getId().getSym();
                    LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
                    code.astore(localVar.index());
                } else if (valuePat instanceof Swc4jAstArrayPat nestedArrayPat) {
                    // Nested array pattern: { arr: [a, ...rest] }
                    generateArrayPatternExtraction(code, classWriter, context, nestedArrayPat, allowShadow);
                } else if (valuePat instanceof Swc4jAstObjectPat nestedObjectPat) {
                    // Nested object pattern: { nested: { y, ...rest } }
                    generateObjectPatternExtraction(code, classWriter, context, nestedObjectPat, allowShadow);
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                generateObjectRestExtraction(code, classWriter, context, restPat, tempMapSlot, extractedKeys, mapRemoveRef);
            }
        }
    }

    /**
     * Generate bytecode for object rest pattern extraction.
     */
    private void generateObjectRestExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int sourceSlot,
            List<String> extractedKeys,
            int mapRemoveRef) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        ISwc4jAstPat arg = restPat.getArg();
        if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
            String varName = bindingIdent.getId().getSym();
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

            // Create new LinkedHashMap as copy of source map
            int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
            int linkedHashMapInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_UTIL_MAP__V);
            code.newInstance(linkedHashMapClass);
            code.dup();
            code.aload(sourceSlot);
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
            throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                    "Rest pattern argument must be a binding identifier");
        }
    }

    /**
     * Generate bytecode for rest pattern extraction.
     */
    private void generateRestPatternExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int sourceSlot,
            int restStartIndex,
            boolean isArrayRest,
            int listGetRef,
            int listSizeRef,
            int listAddRef) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        ISwc4jAstPat arg = restPat.getArg();
        if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
            String varName = bindingIdent.getId().getSym();
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

            if (isArrayRest) {
                // Create new ArrayList
                int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
                int arrayListInitRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR___V);
                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInitRef);
                code.astore(localVar.index());

                // Get source list size
                code.aload(sourceSlot);
                code.invokeinterface(listSizeRef, 1);
                int sizeSlot = getOrAllocateTempSlot(context, "$restSize" + context.getNextTempId(), ConstantJavaType.ABBR_INTEGER);
                code.istore(sizeSlot);

                // Initialize loop counter at restStartIndex
                code.iconst(restStartIndex);
                int iSlot = getOrAllocateTempSlot(context, "$restI" + context.getNextTempId(), ConstantJavaType.ABBR_INTEGER);
                code.istore(iSlot);

                // Loop to copy remaining elements
                int loopStart = code.getCurrentOffset();
                code.iload(iSlot);
                code.iload(sizeSlot);
                code.if_icmpge(0); // Placeholder
                int loopExitPatch = code.getCurrentOffset() - 2;

                // rest.add(source.get(i))
                code.aload(localVar.index());
                code.aload(sourceSlot);
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
            }
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat,
                    "Rest pattern argument must be a binding identifier");
        }
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
}
