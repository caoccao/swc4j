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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.*;

/**
 * Generates bytecode for arrow expressions by creating anonymous inner classes.
 * <p>
 * Arrow expressions like {@code (x: int) => x * 2} are compiled to anonymous inner classes
 * that implement a functional interface (e.g., java.util.function.IntUnaryOperator).
 */
public final class ArrowExpressionGenerator extends BaseAstProcessor<Swc4jAstArrowExpr> {
    private int lambdaCounter = 0;

    public ArrowExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void addReturnIfNeeded(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
        byte[] bytecode = code.toByteArray();
        if (bytecode.length == 0) {
            code.returnVoid();
            return;
        }

        // Check if the last instruction is already a return
        int lastByte = bytecode[bytecode.length - 1] & 0xFF;
        boolean hasReturn = lastByte == 0xAC || // ireturn
                lastByte == 0xAD || // lreturn
                lastByte == 0xAE || // freturn
                lastByte == 0xAF || // dreturn
                lastByte == 0xB0 || // areturn
                lastByte == 0xB1;   // return (void)

        if (!hasReturn) {
            if (returnTypeInfo.type() == ReturnType.VOID) {
                code.returnVoid();
            }
        }
    }

    /**
     * Allocate variables for nested patterns recursively.
     *
     * @param context     compilation context
     * @param pat         the pattern to allocate variables for
     * @param elementType the type descriptor for elements (e.g., "I" for int from List<int>)
     */
    private void allocateNestedPatternVariables(CompilationContext context, ISwc4jAstPat pat, String elementType) {
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
    private void allocateRestPatternVariable(CompilationContext context, Swc4jAstRestPat restPat, boolean isArrayRest) {
        ISwc4jAstPat arg = restPat.getArg();
        if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
            String varName = bindingIdent.getId().getSym();
            String varType = isArrayRest ? "Ljava/util/ArrayList;" : "Ljava/util/LinkedHashMap;";
            allocateVariableIfNeeded(context, varName, varType);
        }
    }

    /**
     * Allocate variable if it doesn't exist.
     */
    private void allocateVariableIfNeeded(CompilationContext context, String varName, String varType) {
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
     * Analyze arrow type with optional target type context for parameter type inference.
     *
     * @param arrowExpr      the arrow expression
     * @param targetTypeInfo optional target type info for inferring parameter types
     * @return ArrowTypeInfo containing the resolved types
     */
    private ArrowTypeInfo analyzeArrowType(Swc4jAstArrowExpr arrowExpr, ReturnTypeInfo targetTypeInfo)
            throws Swc4jByteCodeCompilerException {
        List<ISwc4jAstPat> params = arrowExpr.getParams();
        ISwc4jAstBlockStmtOrExpr body = arrowExpr.getBody();

        // Try to get parameter types from target functional interface
        List<String> targetParamTypes = null;
        if (targetTypeInfo != null && targetTypeInfo.descriptor() != null) {
            targetParamTypes = getTargetInterfaceParamTypes(targetTypeInfo.descriptor());
        }

        // Build parameter types
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            ISwc4jAstPat param = params.get(i);
            String paramType = compiler.getTypeResolver().extractParameterType(param);
            String paramName = extractParamName(param);

            // If parameter type is Object and we have target type info, try to infer from target
            if ("Ljava/lang/Object;".equals(paramType) && targetParamTypes != null && i < targetParamTypes.size()) {
                paramType = targetParamTypes.get(i);
            }

            paramTypes.add(paramType);
            paramNames.add(paramName != null ? paramName : "arg" + paramTypes.size());
        }

        // Push a new scope for parameter types to enable return type inference
        // This allows inferTypeFromExpr to correctly infer types like x * 2 when x is a long
        CompilationContext context = compiler.getMemory().getCompilationContext();
        Map<String, String> paramScope = context.pushInferredTypesScope();
        for (int i = 0; i < paramNames.size(); i++) {
            paramScope.put(paramNames.get(i), paramTypes.get(i));
        }

        // Determine return type
        ReturnTypeInfo returnInfo = analyzeReturnType(arrowExpr, body);

        // Pop the parameter types scope
        context.popInferredTypesScope();

        // Determine which functional interface to use
        String interfaceName;
        String methodName;
        String methodDescriptor;

        if (params.isEmpty() && returnInfo.type() == ReturnType.VOID) {
            // () => void -> Runnable
            interfaceName = "java/lang/Runnable";
            methodName = "run";
            methodDescriptor = "()V";
        } else if (params.isEmpty()) {
            // () => T -> Supplier<T>
            interfaceName = getSupplierInterface(returnInfo);
            methodName = getSupplierMethodName(returnInfo);
            // For generic Supplier<T>, the method returns Object due to type erasure
            methodDescriptor = "()" + getErasedSupplierReturnDescriptor(returnInfo);
        } else if (params.size() == 1 && returnInfo.type() == ReturnType.VOID) {
            // (T) => void -> Consumer<T>
            interfaceName = getConsumerInterface(paramTypes.get(0));
            methodName = getConsumerMethodName(paramTypes.get(0));
            methodDescriptor = "(" + paramTypes.get(0) + ")V";
        } else if (params.size() == 1) {
            // (T) => R -> Function<T, R> or primitive specialization
            interfaceName = getFunctionInterface(paramTypes.get(0), returnInfo);
            methodName = getFunctionMethodName(paramTypes.get(0), returnInfo);
            methodDescriptor = "(" + paramTypes.get(0) + ")" + getReturnDescriptor(returnInfo);
        } else if (params.size() == 2) {
            // Check for primitive binary operators first
            String binaryOperatorInfo = getBinaryOperatorInfo(paramTypes.get(0), paramTypes.get(1), returnInfo);
            if (binaryOperatorInfo != null) {
                String[] parts = binaryOperatorInfo.split("\\|");
                interfaceName = parts[0];
                methodName = parts[1];
                methodDescriptor = parts[2];
            } else {
                // (T, U) => R -> BiFunction<T, U, R>
                interfaceName = "java/util/function/BiFunction";
                methodName = "apply";
                StringBuilder desc = new StringBuilder("(");
                for (String pt : paramTypes) {
                    desc.append(boxedDescriptor(pt));
                }
                desc.append(")").append(boxedDescriptor(getReturnDescriptor(returnInfo)));
                methodDescriptor = desc.toString();
            }
        } else {
            // For more than 2 parameters, we'll generate a custom interface or use Object varargs
            throw new Swc4jByteCodeCompilerException(arrowExpr,
                    "Arrow functions with more than 2 parameters are not yet supported");
        }

        return new ArrowTypeInfo(interfaceName, methodName, methodDescriptor, paramTypes, paramNames, returnInfo);
    }

    private ReturnTypeInfo analyzeBlockReturnType(Swc4jAstBlockStmt blockStmt)
            throws Swc4jByteCodeCompilerException {
        // Build a map of variable names to their declared types
        Map<String, String> varTypes = new HashMap<>();
        collectVariableTypes(blockStmt.getStmts(), varTypes);

        // Find return statement and infer type
        for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
            ReturnTypeInfo result = findReturnType(stmt, varTypes);
            if (result != null) {
                return result;
            }
        }

        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    private List<CapturedVariable> analyzeCapturedVariables(Swc4jAstArrowExpr arrowExpr) {
        List<CapturedVariable> captured = new ArrayList<>();

        // Get the parameter names (these are NOT captured)
        Set<String> paramNames = new HashSet<>();
        for (ISwc4jAstPat param : arrowExpr.getParams()) {
            String paramName = extractParamName(param);
            if (paramName != null) {
                paramNames.add(paramName);
            }
        }

        // Analyze the body for referenced variables
        Set<String> referencedVars = collectReferencedIdentifiers(arrowExpr.getBody());

        // Check which referenced variables are from the outer scope
        CompilationContext context = compiler.getMemory().getCompilationContext();
        for (String varName : referencedVars) {
            // Skip if it's a parameter of the arrow
            if (paramNames.contains(varName)) {
                continue;
            }

            // Check if it's a local variable in the outer scope
            var localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                captured.add(new CapturedVariable(varName, localVar.type(), localVar.index()));
            }
        }

        // Check if 'this' is captured
        String currentClass = context.getCurrentClassInternalName();
        if (currentClass != null && referencesThis(arrowExpr.getBody())) {
            captured.add(0, new CapturedVariable("this", "L" + currentClass + ";", 0));
        }

        return captured;
    }

    private ReturnTypeInfo analyzeReturnType(Swc4jAstArrowExpr arrowExpr, ISwc4jAstBlockStmtOrExpr body)
            throws Swc4jByteCodeCompilerException {
        // Check explicit return type annotation
        if (arrowExpr.getReturnType().isPresent()) {
            return compiler.getTypeResolver().analyzeReturnTypeFromAnnotation(arrowExpr.getReturnType().get());
        }

        // Infer from body
        if (body instanceof ISwc4jAstExpr expr) {
            // Expression body - infer from expression type
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(exprType);
        } else if (body instanceof Swc4jAstBlockStmt blockStmt) {
            // Block body - analyze return statements with variable type context
            return analyzeBlockReturnType(blockStmt);
        }

        // Default to void
        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    private String boxedDescriptor(String descriptor) {
        return switch (descriptor) {
            case "I" -> "Ljava/lang/Integer;";
            case "J" -> "Ljava/lang/Long;";
            case "D" -> "Ljava/lang/Double;";
            case "F" -> "Ljava/lang/Float;";
            case "Z" -> "Ljava/lang/Boolean;";
            case "B" -> "Ljava/lang/Byte;";
            case "C" -> "Ljava/lang/Character;";
            case "S" -> "Ljava/lang/Short;";
            case "V" -> "Ljava/lang/Void;";
            default -> descriptor;
        };
    }

    private void collectIdentifiersRecursive(Object node, Set<String> identifiers) {
        if (node == null) {
            return;
        }

        if (node instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent ident) {
            identifiers.add(ident.getSym());
        } else if (node instanceof com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst ast) {
            for (var child : ast.getChildNodes()) {
                collectIdentifiersRecursive(child, identifiers);
            }
        }
    }

    private Set<String> collectReferencedIdentifiers(ISwc4jAstBlockStmtOrExpr body) {
        Set<String> identifiers = new HashSet<>();
        collectIdentifiersRecursive(body, identifiers);
        return identifiers;
    }

    private void collectVariableTypes(List<ISwc4jAstStmt> stmts, Map<String, String> varTypes) {
        for (ISwc4jAstStmt stmt : stmts) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                for (Swc4jAstVarDeclarator decl : varDecl.getDecls()) {
                    if (decl.getName() instanceof Swc4jAstBindingIdent bindingIdent) {
                        String varName = bindingIdent.getId().getSym();
                        if (bindingIdent.getTypeAnn().isPresent()) {
                            String varType = compiler.getTypeResolver().mapTsTypeToDescriptor(
                                    bindingIdent.getTypeAnn().get().getTypeAnn());
                            varTypes.put(varName, varType);
                        }
                    }
                }
            } else if (stmt instanceof Swc4jAstBlockStmt inner) {
                collectVariableTypes(inner.getStmts(), varTypes);
            } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
                if (ifStmt.getCons() instanceof Swc4jAstBlockStmt consBlock) {
                    collectVariableTypes(consBlock.getStmts(), varTypes);
                }
                if (ifStmt.getAlt().isPresent() && ifStmt.getAlt().get() instanceof Swc4jAstBlockStmt altBlock) {
                    collectVariableTypes(altBlock.getStmts(), varTypes);
                }
            }
        }
    }

    private boolean containsThis(com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst ast) {
        if (ast instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr) {
            return true;
        }
        for (var child : ast.getChildNodes()) {
            if (child instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr) {
                return true;
            }
            // child is already an ISwc4jAst
            if (containsThis(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract element type from a parameter's type annotation.
     * For List<int>, returns "I". For Map<String, int>, returns "I" (value type).
     *
     * @param param the parameter pattern
     * @return element type descriptor, or "Ljava/lang/Object;" if cannot determine
     */
    private String extractElementType(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstArrayPat arrayPat) {
            // Array pattern - check type annotation
            var typeAnn = arrayPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                return extractTypeFromAnnotation(typeAnn.get().getTypeAnn(), true);
            }
        } else if (param instanceof Swc4jAstObjectPat objectPat) {
            // Object pattern - check type annotation
            var typeAnn = objectPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                return extractTypeFromAnnotation(typeAnn.get().getTypeAnn(), false);
            }
        }
        return "Ljava/lang/Object;";
    }

    private String extractParamName(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        } else if (param instanceof Swc4jAstRestPat restPat) {
            // Rest parameter: ...args -> extract "args"
            return extractParamName(restPat.getArg());
        } else if (param instanceof Swc4jAstAssignPat assignPat) {
            // Default parameter: x = 10 -> extract "x"
            return extractParamName(assignPat.getLeft());
        } else if (param instanceof Swc4jAstArrayPat || param instanceof Swc4jAstObjectPat) {
            // Destructuring parameter - will use temporary parameter name
            return null;
        }
        return null;
    }

    /**
     * Extract property name from ISwc4jAstPropName.
     */
    private String extractPropertyName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return null;
    }

    /**
     * Extract element/value type from a type annotation.
     *
     * @param tsType  the TypeScript type
     * @param isArray true for List (returns element type), false for Map (returns value type)
     * @return type descriptor
     */
    private String extractTypeFromAnnotation(ISwc4jAstTsType tsType, boolean isArray) {
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
        return "Ljava/lang/Object;";
    }

    private ReturnTypeInfo findReturnType(ISwc4jAstStmt stmt, Map<String, String> varTypes)
            throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            if (returnStmt.getArg().isPresent()) {
                ISwc4jAstExpr arg = returnStmt.getArg().get();
                // If returning an identifier, check our var types map first
                if (arg instanceof Swc4jAstIdent ident) {
                    String type = varTypes.get(ident.getSym());
                    if (type != null) {
                        return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(type);
                    }
                }
                // Fall back to type inference
                String type = compiler.getTypeResolver().inferTypeFromExpr(arg);
                if (type == null) {
                    type = "Ljava/lang/Object;";
                }
                return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(type);
            }
            return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
        } else if (stmt instanceof Swc4jAstBlockStmt inner) {
            for (ISwc4jAstStmt child : inner.getStmts()) {
                ReturnTypeInfo result = findReturnType(child, varTypes);
                if (result != null) {
                    return result;
                }
            }
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            if (ifStmt.getCons() instanceof Swc4jAstBlockStmt consBlock) {
                for (ISwc4jAstStmt child : consBlock.getStmts()) {
                    ReturnTypeInfo result = findReturnType(child, varTypes);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Check for unsupported features
        if (arrowExpr.isAsync()) {
            throw new Swc4jByteCodeCompilerException(arrowExpr, "Async arrow functions are not supported");
        }
        if (arrowExpr.isGenerator()) {
            throw new Swc4jByteCodeCompilerException(arrowExpr, "Generator arrow functions are not supported");
        }

        try {
            // Generate a unique class name for this lambda
            String lambdaClassName = generateLambdaClassName();

            // Analyze captured variables
            List<CapturedVariable> capturedVariables = analyzeCapturedVariables(arrowExpr);

            // Determine the functional interface and method signature
            // Pass returnTypeInfo to enable parameter type inference from target type context
            ArrowTypeInfo typeInfo = analyzeArrowType(arrowExpr, returnTypeInfo);

            // Generate the anonymous inner class bytecode
            byte[] lambdaBytecode = generateLambdaClass(lambdaClassName, arrowExpr, typeInfo, capturedVariables);

            // Store the bytecode in the compiler memory
            compiler.getMemory().getByteCodeMap().put(lambdaClassName.replace('/', '.'), lambdaBytecode);

            // Generate code to instantiate the lambda
            generateInstantiation(code, cp, lambdaClassName, capturedVariables);
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(arrowExpr, "Failed to generate lambda class", e);
        }
    }

    /**
     * Generate bytecode for array pattern extraction from a value already on the stack (parameter).
     */
    public void generateArrayPatternExtraction(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstArrayPat arrayPat) throws Swc4jByteCodeCompilerException {

        int listClass = cp.addClass("java/util/List");
        code.checkcast(listClass);
        int tempListSlot = getOrAllocateTempSlot(context, "$tempList" + context.getNextTempId(), "Ljava/util/List;");
        code.astore(tempListSlot);

        int listGetRef = cp.addInterfaceMethodRef("java/util/List", "get", "(I)Ljava/lang/Object;");
        int listSizeRef = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
        int listAddRef = cp.addInterfaceMethodRef("java/util/List", "add", "(Ljava/lang/Object;)Z");

        // Extract element type from the array pattern's type annotation
        String elementType = extractElementType(arrayPat);

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
                generateUnboxingIfNeeded(code, cp, elementType);
                storeValueByType(code, localVar.index(), elementType);
                currentIndex++;

            } else if (elem instanceof Swc4jAstArrayPat nestedArrayPat) {
                // Nested array pattern: [a, [b, ...inner], ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested array pattern
                generateArrayPatternExtraction(code, cp, context, nestedArrayPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstObjectPat nestedObjectPat) {
                // Nested object pattern: [a, {b, ...inner}, ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested object pattern
                generateObjectPatternExtraction(code, cp, context, nestedObjectPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                generateRestPatternExtraction(code, cp, context, restPat, tempListSlot, restStartIndex, true, listGetRef, listSizeRef, listAddRef);
            }
        }
    }

    private void generateInstantiation(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            String lambdaClassName,
            List<CapturedVariable> capturedVariables) {
        // new LambdaClass
        int classRef = cp.addClass(lambdaClassName);
        code.newInstance(classRef);
        code.dup();

        // Load captured variables onto stack
        for (CapturedVariable captured : capturedVariables) {
            loadVariable(code, captured.outerSlot(), captured.type());
        }

        // Build constructor descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (CapturedVariable captured : capturedVariables) {
            descriptor.append(captured.type());
        }
        descriptor.append(")V");

        // invokespecial <init>
        int constructorRef = cp.addMethodRef(lambdaClassName, "<init>", descriptor.toString());
        code.invokespecial(constructorRef);
    }

    private byte[] generateLambdaClass(
            String lambdaClassName,
            Swc4jAstArrowExpr arrowExpr,
            ArrowTypeInfo typeInfo,
            List<CapturedVariable> capturedVariables) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(lambdaClassName, "java/lang/Object");
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Add the functional interface
        classWriter.addInterface(typeInfo.interfaceName());

        // Add fields for captured variables
        for (CapturedVariable captured : capturedVariables) {
            classWriter.addField(0x0012, // ACC_PRIVATE | ACC_FINAL
                    "captured$" + captured.name(), captured.type());
        }

        // Generate constructor
        generateLambdaConstructor(classWriter, cp, lambdaClassName, capturedVariables);

        // Generate the functional interface method
        generateLambdaMethod(classWriter, cp, lambdaClassName, arrowExpr, typeInfo, capturedVariables);

        return classWriter.toByteArray();
    }

    private String generateLambdaClassName() {
        String currentClass = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClass != null) {
            return currentClass + "$Lambda$" + (++lambdaCounter);
        } else {
            return "$Lambda$" + (++lambdaCounter);
        }
    }

    private void generateLambdaConstructor(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String lambdaClassName,
            List<CapturedVariable> capturedVariables) {
        CodeBuilder code = new CodeBuilder();

        // Call super()
        code.aload(0);
        int superInit = cp.addMethodRef("java/lang/Object", "<init>", "()V");
        code.invokespecial(superInit);

        // Initialize captured variable fields
        int slot = 1;
        for (CapturedVariable captured : capturedVariables) {
            code.aload(0); // this
            loadVariable(code, slot, captured.type());
            int fieldRef = cp.addFieldRef(lambdaClassName, "captured$" + captured.name(), captured.type());
            code.putfield(fieldRef);
            slot += getSlotSize(captured.type());
        }

        code.returnVoid();

        // Build constructor descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (CapturedVariable captured : capturedVariables) {
            descriptor.append(captured.type());
        }
        descriptor.append(")V");

        int maxLocals = slot;
        classWriter.addMethod(0x0001, "<init>", descriptor.toString(), code.toByteArray(), 10, maxLocals);
    }

    private void generateLambdaMethod(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String lambdaClassName,
            Swc4jAstArrowExpr arrowExpr,
            ArrowTypeInfo typeInfo,
            List<CapturedVariable> capturedVariables) throws Swc4jByteCodeCompilerException {
        // Push a new compilation context for the lambda method (instance method, so slot 0 is 'this')
        CompilationContext lambdaContext = compiler.getMemory().pushCompilationContext(false);

        // Push the lambda class as current class
        lambdaContext.pushClass(lambdaClassName);

        // Allocate slots for parameters
        for (int i = 0; i < typeInfo.paramNames().size(); i++) {
            String paramName = typeInfo.paramNames().get(i);
            String paramType = typeInfo.paramTypes().get(i);
            lambdaContext.getLocalVariableTable().allocateVariable(paramName, paramType);
            lambdaContext.getInferredTypes().put(paramName, paramType);
        }

        // Map captured variables to field access - register them in the compilation context
        for (CapturedVariable captured : capturedVariables) {
            // Store in inferred types so the identifier generator knows the type
            lambdaContext.getInferredTypes().put(captured.name(), captured.type());
            // Register as captured variable for field access resolution
            lambdaContext.getCapturedVariables().put(
                    captured.name(),
                    new com.caoccao.javet.swc4j.compiler.memory.CapturedVariable(
                            captured.name(),
                            "captured$" + captured.name(),
                            captured.type()
                    )
            );
        }

        // Generate method body
        CodeBuilder code = new CodeBuilder();

        // Generate destructuring extraction code for parameters
        List<ISwc4jAstPat> params = arrowExpr.getParams();
        for (int i = 0; i < params.size(); i++) {
            ISwc4jAstPat param = params.get(i);
            if (param instanceof Swc4jAstArrayPat arrayPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate array destructuring extraction
                generateArrayPatternExtraction(code, cp, lambdaContext, arrayPat);
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate object destructuring extraction
                generateObjectPatternExtraction(code, cp, lambdaContext, objectPat);
            }
        }

        ISwc4jAstBlockStmtOrExpr body = arrowExpr.getBody();
        if (body instanceof Swc4jAstBlockStmt blockStmt) {
            // Block body - generate statements
            compiler.getVariableAnalyzer().analyzeVariableDeclarations(blockStmt);
            for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
                compiler.getStatementGenerator().generate(code, cp, stmt, typeInfo.returnTypeInfo());
            }
            // Add return if needed
            addReturnIfNeeded(code, typeInfo.returnTypeInfo());
        } else if (body instanceof ISwc4jAstExpr expr) {
            // Expression body - implicit return
            compiler.getExpressionGenerator().generate(code, cp, expr, typeInfo.returnTypeInfo());
            generateReturn(code, typeInfo.returnTypeInfo());
        }

        // Pop lambda class context
        lambdaContext.popClass();

        int maxLocals = lambdaContext.getLocalVariableTable().getMaxLocals();

        // Generate stack map table
        boolean isStatic = false;
        var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, lambdaClassName,
                typeInfo.methodDescriptor(), cp);
        var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();

        classWriter.addMethod(0x0001, // ACC_PUBLIC
                typeInfo.methodName(), typeInfo.methodDescriptor(), code.toByteArray(),
                10, maxLocals, null, null, stackMapTable, exceptionTable);

        // Pop the lambda compilation context to restore the outer context
        compiler.getMemory().popCompilationContext();
    }

    /**
     * Generate bytecode for object pattern extraction from a value already on the stack (parameter).
     */
    public void generateObjectPatternExtraction(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstObjectPat objectPat) throws Swc4jByteCodeCompilerException {

        int mapClass = cp.addClass("java/util/Map");
        code.checkcast(mapClass);
        int tempMapSlot = getOrAllocateTempSlot(context, "$tempMap" + context.getNextTempId(), "Ljava/util/Map;");
        code.astore(tempMapSlot);

        int mapGetRef = cp.addInterfaceMethodRef("java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        int mapRemoveRef = cp.addInterfaceMethodRef("java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");

        // Extract value type from the object pattern's type annotation (Map<K, V> -> V)
        String valueType = extractElementType(objectPat);

        List<String> extractedKeys = new ArrayList<>();

        // First pass: allocate variables and collect extracted keys
        for (ISwc4jAstObjectPatProp prop : objectPat.getProps()) {
            if (prop instanceof Swc4jAstAssignPatProp assignProp) {
                String varName = assignProp.getKey().getId().getSym();
                extractedKeys.add(varName);
                allocateVariableIfNeeded(context, varName, valueType);
            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
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
                generateUnboxingIfNeeded(code, cp, valueType);
                storeValueByType(code, localVar.index(), valueType);

            } else if (prop instanceof Swc4jAstKeyValuePatProp keyValueProp) {
                String keyName = extractPropertyName(keyValueProp.getKey());
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
                    generateUnboxingIfNeeded(code, cp, valueType);
                    storeValueByType(code, localVar.index(), valueType);
                } else if (valuePat instanceof Swc4jAstArrayPat nestedArrayPat) {
                    // Nested array pattern: { arr: [a, ...rest] }
                    generateArrayPatternExtraction(code, cp, context, nestedArrayPat);
                } else if (valuePat instanceof Swc4jAstObjectPat nestedObjectPat) {
                    // Nested object pattern: { nested: { y, ...rest } }
                    generateObjectPatternExtraction(code, cp, context, nestedObjectPat);
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                generateObjectRestExtraction(code, cp, context, restPat, tempMapSlot, extractedKeys, mapRemoveRef);
            }
        }
    }

    /**
     * Generate bytecode for object rest pattern extraction {...rest}.
     */
    private void generateObjectRestExtraction(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int tempMapSlot,
            List<String> extractedKeys,
            int mapRemoveRef) throws Swc4jByteCodeCompilerException {

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(restPat, "Rest pattern argument must be a binding identifier");
        }

        String restVarName = bindingIdent.getId().getSym();
        LocalVariable restVar = context.getLocalVariableTable().getVariable(restVarName);

        // Create a new LinkedHashMap for the rest object
        int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
        code.newInstance(linkedHashMapClass);
        code.dup();
        int linkedHashMapInitRef = cp.addMethodRef("java/util/LinkedHashMap", "<init>", "()V");
        code.invokespecial(linkedHashMapInitRef);
        int restMapSlot = getOrAllocateTempSlot(context, "$restMap" + context.getNextTempId(), "Ljava/util/LinkedHashMap;");
        code.astore(restMapSlot);

        // Copy all entries from original map except extracted keys
        int mapEntrySetRef = cp.addInterfaceMethodRef("java/util/Map", "entrySet", "()Ljava/util/Set;");
        int setIteratorRef = cp.addInterfaceMethodRef("java/util/Set", "iterator", "()Ljava/util/Iterator;");
        int iteratorHasNextRef = cp.addInterfaceMethodRef("java/util/Iterator", "hasNext", "()Z");
        int iteratorNextRef = cp.addInterfaceMethodRef("java/util/Iterator", "next", "()Ljava/lang/Object;");
        int entryGetKeyRef = cp.addInterfaceMethodRef("java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
        int entryGetValueRef = cp.addInterfaceMethodRef("java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
        int mapPutRef = cp.addInterfaceMethodRef("java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        int objectEqualsRef = cp.addMethodRef("java/lang/Object", "equals", "(Ljava/lang/Object;)Z");

        // Get iterator from original map's entry set
        code.aload(tempMapSlot);
        code.invokeinterface(mapEntrySetRef, 1);
        code.invokeinterface(setIteratorRef, 1);
        int iteratorSlot = getOrAllocateTempSlot(context, "$iterator" + context.getNextTempId(), "Ljava/util/Iterator;");
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
        int entryClass = cp.addClass("java/util/Map$Entry");
        code.checkcast(entryClass);
        int entrySlot = getOrAllocateTempSlot(context, "$entry" + context.getNextTempId(), "Ljava/util/Map$Entry;");
        code.astore(entrySlot);

        // Get key from entry
        code.aload(entrySlot);
        code.invokeinterface(entryGetKeyRef, 1);
        int keySlot = getOrAllocateTempSlot(context, "$key" + context.getNextTempId(), "Ljava/lang/Object;");
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
    private void generateRestPatternExtraction(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int tempListSlot,
            int restStartIndex,
            boolean isArrayRest,
            int listGetRef,
            int listSizeRef,
            int listAddRef) throws Swc4jByteCodeCompilerException {

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(restPat, "Rest pattern argument must be a binding identifier");
        }

        String restVarName = bindingIdent.getId().getSym();
        LocalVariable restVar = context.getLocalVariableTable().getVariable(restVarName);

        // Create a new ArrayList for the rest elements
        int arrayListClass = cp.addClass("java/util/ArrayList");
        code.newInstance(arrayListClass);
        code.dup();
        int arrayListInitRef = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
        code.invokespecial(arrayListInitRef);
        code.astore(restVar.index());

        // Get list size
        code.aload(tempListSlot);
        code.invokeinterface(listSizeRef, 1);
        int sizeSlot = getOrAllocateTempSlot(context, "$size" + context.getNextTempId(), "I");
        code.istore(sizeSlot);

        // Loop from restStartIndex to size, adding elements to rest array
        int indexSlot = getOrAllocateTempSlot(context, "$index" + context.getNextTempId(), "I");
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

    private void generateReturn(CodeBuilder code, ReturnTypeInfo returnTypeInfo) {
        switch (returnTypeInfo.type()) {
            case VOID -> code.returnVoid();
            case INT, BOOLEAN, BYTE, CHAR, SHORT -> code.ireturn();
            case LONG -> code.lreturn();
            case FLOAT -> code.freturn();
            case DOUBLE -> code.dreturn();
            case STRING, OBJECT -> code.areturn();
        }
    }

    /**
     * Generate unboxing code if the target type is a primitive.
     * Converts boxed types (Integer, Long, etc.) to primitives (int, long, etc.).
     */
    private void generateUnboxingIfNeeded(CodeBuilder code, ClassWriter.ConstantPool cp, String targetType) {
        switch (targetType) {
            case "I" -> { // int
                int intValueRef = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                int integerClass = cp.addClass("java/lang/Integer");
                code.checkcast(integerClass);
                code.invokevirtual(intValueRef);
            }
            case "J" -> { // long
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                int longClass = cp.addClass("java/lang/Long");
                code.checkcast(longClass);
                code.invokevirtual(longValueRef);
            }
            case "D" -> { // double
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                int doubleClass = cp.addClass("java/lang/Double");
                code.checkcast(doubleClass);
                code.invokevirtual(doubleValueRef);
            }
            case "F" -> { // float
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                int floatClass = cp.addClass("java/lang/Float");
                code.checkcast(floatClass);
                code.invokevirtual(floatValueRef);
            }
            case "Z" -> { // boolean
                int booleanValueRef = cp.addMethodRef("java/lang/Boolean", "booleanValue", "()Z");
                int booleanClass = cp.addClass("java/lang/Boolean");
                code.checkcast(booleanClass);
                code.invokevirtual(booleanValueRef);
            }
            case "B" -> { // byte
                int byteValueRef = cp.addMethodRef("java/lang/Byte", "byteValue", "()B");
                int byteClass = cp.addClass("java/lang/Byte");
                code.checkcast(byteClass);
                code.invokevirtual(byteValueRef);
            }
            case "C" -> { // char
                int charValueRef = cp.addMethodRef("java/lang/Character", "charValue", "()C");
                int charClass = cp.addClass("java/lang/Character");
                code.checkcast(charClass);
                code.invokevirtual(charValueRef);
            }
            case "S" -> { // short
                int shortValueRef = cp.addMethodRef("java/lang/Short", "shortValue", "()S");
                int shortClass = cp.addClass("java/lang/Short");
                code.checkcast(shortClass);
                code.invokevirtual(shortValueRef);
            }
            default -> {
                // Reference type or Object - just cast
                if (targetType.startsWith("L") && targetType.endsWith(";")) {
                    String className = targetType.substring(1, targetType.length() - 1);
                    int classRef = cp.addClass(className);
                    code.checkcast(classRef);
                }
            }
        }
    }

    /**
     * Gets binary operator interface info if both params and return are same primitive type.
     * Returns "interfaceName|methodName|methodDescriptor" or null if not a binary operator.
     */
    private String getBinaryOperatorInfo(String param1Type, String param2Type, ReturnTypeInfo returnInfo) {
        // IntBinaryOperator: (int, int) => int
        if (param1Type.equals("I") && param2Type.equals("I") && returnInfo.type() == ReturnType.INT) {
            return "java/util/function/IntBinaryOperator|applyAsInt|(II)I";
        }
        // LongBinaryOperator: (long, long) => long
        if (param1Type.equals("J") && param2Type.equals("J") && returnInfo.type() == ReturnType.LONG) {
            return "java/util/function/LongBinaryOperator|applyAsLong|(JJ)J";
        }
        // DoubleBinaryOperator: (double, double) => double
        if (param1Type.equals("D") && param2Type.equals("D") && returnInfo.type() == ReturnType.DOUBLE) {
            return "java/util/function/DoubleBinaryOperator|applyAsDouble|(DD)D";
        }
        return null;
    }

    private String getConsumerInterface(String paramType) {
        return switch (paramType) {
            case "I" -> "java/util/function/IntConsumer";
            case "J" -> "java/util/function/LongConsumer";
            case "D" -> "java/util/function/DoubleConsumer";
            default -> "java/util/function/Consumer";
        };
    }

    private String getConsumerMethodName(String paramType) {
        return "accept";
    }

    private String getErasedSupplierReturnDescriptor(ReturnTypeInfo returnInfo) {
        // For primitive supplier interfaces, return the primitive descriptor
        // For generic Supplier<T>, return Object due to type erasure
        return switch (returnInfo.type()) {
            case INT -> "I";
            case LONG -> "J";
            case DOUBLE -> "D";
            case BOOLEAN -> "Z";
            default -> "Ljava/lang/Object;";
        };
    }

    private String getFunctionInterface(String paramType, ReturnTypeInfo returnInfo) {
        // Check for primitive specializations
        if (paramType.equals("I") && returnInfo.type() == ReturnType.INT) {
            return "java/util/function/IntUnaryOperator";
        }
        if (paramType.equals("I") && returnInfo.type() == ReturnType.BOOLEAN) {
            return "java/util/function/IntPredicate";
        }
        if (paramType.equals("J") && returnInfo.type() == ReturnType.LONG) {
            return "java/util/function/LongUnaryOperator";
        }
        if (paramType.equals("D") && returnInfo.type() == ReturnType.DOUBLE) {
            return "java/util/function/DoubleUnaryOperator";
        }
        if (paramType.equals("I")) {
            return "java/util/function/IntFunction";
        }
        if (paramType.equals("J")) {
            return "java/util/function/LongFunction";
        }
        if (paramType.equals("D")) {
            return "java/util/function/DoubleFunction";
        }
        if (returnInfo.type() == ReturnType.BOOLEAN) {
            return "java/util/function/Predicate";
        }
        return "java/util/function/Function";
    }

    private String getFunctionMethodName(String paramType, ReturnTypeInfo returnInfo) {
        if (paramType.equals("I") && returnInfo.type() == ReturnType.INT) {
            return "applyAsInt";
        }
        if (paramType.equals("J") && returnInfo.type() == ReturnType.LONG) {
            return "applyAsLong";
        }
        if (paramType.equals("D") && returnInfo.type() == ReturnType.DOUBLE) {
            return "applyAsDouble";
        }
        if (returnInfo.type() == ReturnType.BOOLEAN) {
            return "test";
        }
        return "apply";
    }

    private int getOrAllocateTempSlot(CompilationContext context, String tempName, String type) {
        LocalVariable tempVar = context.getLocalVariableTable().getVariable(tempName);
        if (tempVar == null) {
            context.getLocalVariableTable().allocateVariable(tempName, type);
            tempVar = context.getLocalVariableTable().getVariable(tempName);
        }
        return tempVar.index();
    }

    private String getReturnDescriptor(ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case VOID -> "V";
            case INT -> "I";
            case BOOLEAN -> "Z";
            case BYTE -> "B";
            case CHAR -> "C";
            case SHORT -> "S";
            case LONG -> "J";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
            case OBJECT -> returnInfo.descriptor() != null ? returnInfo.descriptor() : "Ljava/lang/Object;";
        };
    }

    private int getSlotSize(String type) {
        return ("J".equals(type) || "D".equals(type)) ? 2 : 1;
    }

    /**
     * Get parameter types from a target functional interface descriptor.
     * This enables parameter type inference from context.
     *
     * @param interfaceDescriptor the interface type descriptor (e.g., "Ljava/util/function/IntUnaryOperator;")
     * @return list of parameter type descriptors, or null if unknown interface
     */
    private List<String> getTargetInterfaceParamTypes(String interfaceDescriptor) {
        if (interfaceDescriptor == null || !interfaceDescriptor.startsWith("L") || !interfaceDescriptor.endsWith(";")) {
            return null;
        }

        String interfaceName = interfaceDescriptor.substring(1, interfaceDescriptor.length() - 1);

        // Map well-known functional interfaces to their parameter types
        return switch (interfaceName) {
            // Suppliers - no parameters
            case "java/util/function/Supplier",
                 "java/util/function/IntSupplier",
                 "java/util/function/LongSupplier",
                 "java/util/function/DoubleSupplier",
                 "java/util/function/BooleanSupplier" -> List.of();

            // Consumers - single parameter
            case "java/util/function/Consumer" -> List.of("Ljava/lang/Object;");
            case "java/util/function/IntConsumer" -> List.of("I");
            case "java/util/function/LongConsumer" -> List.of("J");
            case "java/util/function/DoubleConsumer" -> List.of("D");

            // Predicates - single parameter, return boolean
            case "java/util/function/Predicate" -> List.of("Ljava/lang/Object;");
            case "java/util/function/IntPredicate" -> List.of("I");
            case "java/util/function/LongPredicate" -> List.of("J");
            case "java/util/function/DoublePredicate" -> List.of("D");

            // Functions - single parameter, return type varies
            case "java/util/function/Function" -> List.of("Ljava/lang/Object;");
            case "java/util/function/IntFunction" -> List.of("I");
            case "java/util/function/LongFunction" -> List.of("J");
            case "java/util/function/DoubleFunction" -> List.of("D");
            case "java/util/function/ToIntFunction" -> List.of("Ljava/lang/Object;");
            case "java/util/function/ToLongFunction" -> List.of("Ljava/lang/Object;");
            case "java/util/function/ToDoubleFunction" -> List.of("Ljava/lang/Object;");

            // Unary operators - single parameter, same return type
            case "java/util/function/UnaryOperator" -> List.of("Ljava/lang/Object;");
            case "java/util/function/IntUnaryOperator" -> List.of("I");
            case "java/util/function/LongUnaryOperator" -> List.of("J");
            case "java/util/function/DoubleUnaryOperator" -> List.of("D");

            // Binary operators - two parameters
            case "java/util/function/BiFunction" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/BiConsumer" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/BiPredicate" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/BinaryOperator" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/IntBinaryOperator" -> List.of("I", "I");
            case "java/util/function/LongBinaryOperator" -> List.of("J", "J");
            case "java/util/function/DoubleBinaryOperator" -> List.of("D", "D");

            // ToInt/Long/Double binary functions
            case "java/util/function/ToIntBiFunction" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/ToLongBiFunction" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");
            case "java/util/function/ToDoubleBiFunction" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");

            // Other common functional interfaces
            case "java/lang/Runnable" -> List.of();
            case "java/util/concurrent/Callable" -> List.of();
            case "java/util/Comparator" -> List.of("Ljava/lang/Object;", "Ljava/lang/Object;");

            default -> null;
        };
    }

    private String getSupplierInterface(ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case INT -> "java/util/function/IntSupplier";
            case LONG -> "java/util/function/LongSupplier";
            case DOUBLE -> "java/util/function/DoubleSupplier";
            case BOOLEAN -> "java/util/function/BooleanSupplier";
            default -> "java/util/function/Supplier";
        };
    }

    private String getSupplierMethodName(ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case INT -> "getAsInt";
            case LONG -> "getAsLong";
            case DOUBLE -> "getAsDouble";
            case BOOLEAN -> "getAsBoolean";
            default -> "get";
        };
    }

    private void loadVariable(CodeBuilder code, int slot, String type) {
        switch (type) {
            case "I", "Z", "B", "C", "S" -> code.iload(slot);
            case "J" -> code.lload(slot);
            case "F" -> code.fload(slot);
            case "D" -> code.dload(slot);
            default -> code.aload(slot);
        }
    }

    private boolean referencesThis(ISwc4jAstBlockStmtOrExpr body) {
        if (body instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr) {
            return true;
        }
        // ISwc4jAstBlockStmtOrExpr extends ISwc4jAst, so we can directly access getChildNodes()
        for (var child : body.getChildNodes()) {
            if (child instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr) {
                return true;
            }
            if (child instanceof ISwc4jAstBlockStmtOrExpr childBody) {
                if (referencesThis(childBody)) {
                    return true;
                }
            } else {
                // child is already an ISwc4jAst
                if (containsThis(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Reset the lambda counter. Call this when starting a new compilation.
     */
    public void reset() {
        lambdaCounter = 0;
    }

    /**
     * Store value to local variable slot based on type.
     */
    private void storeValueByType(CodeBuilder code, int slot, String type) {
        switch (type) {
            case "I", "Z", "B", "C", "S" -> code.istore(slot);
            case "J" -> code.lstore(slot);
            case "F" -> code.fstore(slot);
            case "D" -> code.dstore(slot);
            default -> code.astore(slot);
        }
    }

    /**
     * Record for arrow function type information.
     */
    private record ArrowTypeInfo(
            String interfaceName,
            String methodName,
            String methodDescriptor,
            List<String> paramTypes,
            List<String> paramNames,
            ReturnTypeInfo returnTypeInfo) {
    }

    /**
     * Record for captured variable information.
     */
    private record CapturedVariable(String name, String type, int outerSlot) {
    }
}
