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
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.*;

/**
 * Generates bytecode for arrow expressions by creating anonymous inner classes.
 * <p>
 * Arrow expressions like {@code (x: int) => x * 2} are compiled to anonymous inner classes
 * that implement a functional interface (e.g., java.util.function.IntUnaryOperator).
 */
public final class ArrowExpressionProcessor extends BaseAstProcessor<Swc4jAstArrowExpr> {
    private int lambdaCounter = 0;

    /**
     * Constructs a new ArrowExpressionProcessor.
     *
     * @param compiler the bytecode compiler instance
     */
    public ArrowExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
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
            String varType = isArrayRest ? ConstantJavaType.LJAVA_UTIL_ARRAYLIST : ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
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
        CompilationContext context = compiler.getMemory().getCompilationContext();

        // Register type parameters from the arrow expression (e.g., <T>(x: T): T => x)
        // This enables type erasure when extracting parameter types
        boolean hasTypeParams = arrowExpr.getTypeParams().isPresent();
        if (hasTypeParams) {
            var typeParamDecl = arrowExpr.getTypeParams().get();
            TypeParameterScope typeScope = TypeParameterScope.fromDecl(typeParamDecl);
            context.pushTypeParameterScope(typeScope);
        }

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
            String paramName = AstUtils.extractParamName(param);

            // If parameter type is Object and we have target type info, try to infer from target
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(paramType) && targetParamTypes != null && i < targetParamTypes.size()) {
                paramType = targetParamTypes.get(i);
            }

            paramTypes.add(paramType);
            paramNames.add(paramName != null ? paramName : "arg" + paramTypes.size());
        }

        // Push a new scope for parameter types to enable return type inference
        // This allows inferTypeFromExpr to correctly infer types like x * 2 when x is a long
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

        // First check if target type info specifies a functional interface directly
        // This is needed for generic arrows like <T>(x: T): T => x assigned to UnaryOperator<Object>
        if (targetTypeInfo != null && targetTypeInfo.descriptor() != null) {
            String targetDesc = targetTypeInfo.descriptor();
            // Extract interface name from descriptor (e.g., "Ljava/util/function/UnaryOperator;" -> "java/util/function/UnaryOperator")
            if (targetDesc.startsWith("L") && targetDesc.endsWith(";")) {
                String targetInterface = TypeConversionUtils.descriptorToInternalName(targetDesc);
                FunctionalInterfaceInfo info = getFunctionalInterfaceInfo(targetInterface, paramTypes, returnInfo);
                if (info != null) {
                    interfaceName = info.interfaceName;
                    methodName = info.methodName;
                    methodDescriptor = info.methodDescriptor;

                    // Pop type parameter scope before returning
                    if (hasTypeParams) {
                        context.popTypeParameterScope();
                    }

                    return new ArrowTypeInfo(interfaceName, methodName, methodDescriptor, paramTypes, paramNames, returnInfo);
                }
            }
        }

        if (params.isEmpty() && returnInfo.type() == ReturnType.VOID) {
            // () => void -> Runnable
            interfaceName = ConstantJavaType.JAVA_LANG_RUNNABLE;
            methodName = "run";
            methodDescriptor = ConstantJavaDescriptor.__V;
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
            if (isErasedFunctionInterface(interfaceName)) {
                methodDescriptor = getErasedFunctionDescriptor(interfaceName);
            } else {
                methodDescriptor = "(" + paramTypes.get(0) + ")" + TypeConversionUtils.getReturnDescriptor(returnInfo);
            }
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
                interfaceName = ConstantJavaType.JAVA_UTIL_FUNCTION_BI_FUNCTION;
                methodName = ConstantJavaMethod.METHOD_APPLY;
                StringBuilder desc = new StringBuilder("(");
                for (String pt : paramTypes) {
                    desc.append(boxedDescriptor(pt));
                }
                desc.append(")").append(boxedDescriptor(TypeConversionUtils.getReturnDescriptor(returnInfo)));
                methodDescriptor = desc.toString();
            }
        } else {
            // For more than 2 parameters, we'll generate a custom interface or use Object varargs
            // Pop type parameter scope before throwing
            if (hasTypeParams) {
                context.popTypeParameterScope();
            }
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr,
                    "Arrow functions with more than 2 parameters are not yet supported");
        }

        // Pop type parameter scope before returning
        if (hasTypeParams) {
            context.popTypeParameterScope();
        }

        return new ArrowTypeInfo(interfaceName, methodName, methodDescriptor, paramTypes, paramNames, returnInfo);
    }

    private ReturnTypeInfo analyzeBlockReturnType(Swc4jAstBlockStmt blockStmt)
            throws Swc4jByteCodeCompilerException {
        // Build a map of variable names to their declared types
        Map<String, String> varTypes = new HashMap<>();
        AstUtils.collectVariableTypes(compiler, blockStmt.getStmts(), varTypes);

        // Find return statement and infer type
        for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
            ReturnTypeInfo result = findReturnType(stmt, varTypes);
            if (result != null) {
                return result;
            }
        }

        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    /**
     * Analyze captured variables for an arrow expression.
     *
     * @param arrowExpr         the arrow expression
     * @param selfReferenceName the name of the variable being assigned (for recursive arrows), or null
     * @return list of captured variables with self-reference information
     */
    private List<CapturedVariable> analyzeCapturedVariables(
            Swc4jAstArrowExpr arrowExpr,
            String selfReferenceName,
            boolean captureThis) {
        List<CapturedVariable> captured = new ArrayList<>();

        // Get the parameter names (these are NOT captured)
        Set<String> paramNames = new HashSet<>();
        for (ISwc4jAstPat param : arrowExpr.getParams()) {
            String paramName = AstUtils.extractParamName(param);
            if (paramName != null) {
                paramNames.add(paramName);
            }
        }

        // Analyze the body for referenced variables
        Set<String> referencedVars = AstUtils.collectReferencedIdentifiers(arrowExpr.getBody());

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
                // Check if this is a self-reference (recursive arrow)
                boolean isSelfRef = varName.equals(selfReferenceName);
                // Check if this variable needs a holder (mutable capture)
                if (localVar.needsHolder()) {
                    // Capture the holder array instead of the value
                    captured.add(new CapturedVariable(
                            varName,
                            localVar.getHolderType(),  // e.g., ConstantJavaType.ARRAY_I for int[]
                            localVar.holderIndex(),    // slot of the holder array
                            isSelfRef,
                            true,                       // isHolder = true
                            localVar.type()            // original type e.g., ConstantJavaType.ABBR_INTEGER
                    ));
                } else {
                    captured.add(new CapturedVariable(varName, localVar.type(), localVar.index(), isSelfRef));
                }
            }
        }

        // Check if 'this' is captured
        String currentClass = context.getCurrentClassInternalName();
        if (captureThis && currentClass != null && referencesThis(arrowExpr.getBody())) {
            captured.add(0, new CapturedVariable("this", "L" + currentClass + ";", 0, false));
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
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_LANG_INTEGER;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_LANG_LONG;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_LANG_DOUBLE;
            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaType.LJAVA_LANG_FLOAT;
            case ConstantJavaType.ABBR_BOOLEAN -> ConstantJavaType.LJAVA_LANG_BOOLEAN;
            case ConstantJavaType.ABBR_BYTE -> ConstantJavaType.LJAVA_LANG_BYTE;
            case ConstantJavaType.ABBR_CHARACTER -> ConstantJavaType.LJAVA_LANG_CHARACTER;
            case ConstantJavaType.ABBR_SHORT -> ConstantJavaType.LJAVA_LANG_SHORT;
            case ConstantJavaType.ABBR_VOID -> ConstantJavaType.LJAVA_LANG_VOID;
            default -> descriptor;
        };
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
        return ConstantJavaType.LJAVA_LANG_OBJECT;
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
        return ConstantJavaType.LJAVA_LANG_OBJECT;
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
                    type = ConstantJavaType.LJAVA_LANG_OBJECT;
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
            ClassWriter classWriter,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        generateInternal(code, classWriter, arrowExpr, returnTypeInfo, null, true);
    }

    /**
     * Generate bytecode for an arrow expression with optional self-reference support.
     *
     * @param code              the code builder
     * @param classWriter       the class writer
     * @param arrowExpr         the arrow expression
     * @param returnTypeInfo    return type info for type inference
     * @param selfReferenceName the variable name for recursive arrows (or null)
     * @return information about self-references that need post-processing (or null)
     * @throws Swc4jByteCodeCompilerException if code generation fails
     */
    public SelfReferenceInfo generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo,
            String selfReferenceName) throws Swc4jByteCodeCompilerException {
        return generateInternal(code, classWriter, arrowExpr, returnTypeInfo, selfReferenceName, true);
    }

    /**
     * Generate bytecode for array pattern extraction from a value already on the stack (parameter).
     *
     * @param code        the code builder
     * @param classWriter the class writer
     * @param context     the compilation context
     * @param arrayPat    the array pattern to extract
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public void generateArrayPatternExtraction(
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
                generateUnboxingIfNeeded(code, classWriter, elementType);
                CodeGeneratorUtils.storeVariable(code, localVar.index(), elementType);
                currentIndex++;

            } else if (elem instanceof Swc4jAstArrayPat nestedArrayPat) {
                // Nested array pattern: [a, [b, ...inner], ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested array pattern
                generateArrayPatternExtraction(code, classWriter, context, nestedArrayPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstObjectPat nestedObjectPat) {
                // Nested object pattern: [a, {b, ...inner}, ...outer]
                code.aload(tempListSlot);
                code.iconst(currentIndex);
                code.invokeinterface(listGetRef, 2);
                // Recursively extract nested object pattern
                generateObjectPatternExtraction(code, classWriter, context, nestedObjectPat);
                currentIndex++;

            } else if (elem instanceof Swc4jAstRestPat restPat) {
                generateRestPatternExtraction(code, classWriter, context, restPat, tempListSlot, restStartIndex, true, listGetRef, listSizeRef, listAddRef);
            }
        }
    }

    private void generateEmptyArray(CodeBuilder code, ClassWriter classWriter, String arrayType) {
        var cp = classWriter.getConstantPool();
        String componentType = TypeConversionUtils.getArrayElementType(arrayType);
        code.iconst(0);
        if (TypeConversionUtils.isPrimitiveType(componentType)) {
            code.newarray(TypeConversionUtils.getNewarrayTypeCode(componentType));
        } else {
            int classRef = cp.addClass(TypeConversionUtils.toInternalName(componentType));
            code.anewarray(classRef);
        }
    }

    /**
     * Generate for function expr.
     *
     * @param code           the code
     * @param classWriter    the class writer
     * @param arrowExpr      the arrow expr
     * @param returnTypeInfo the return type info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public void generateForFunctionExpr(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        generateInternal(code, classWriter, arrowExpr, returnTypeInfo, null, false);
    }

    private void generateInstantiation(
            CodeBuilder code,
            ClassWriter classWriter,
            String lambdaClassName,
            List<CapturedVariable> capturedVariables) {
        var cp = classWriter.getConstantPool();
        // new LambdaClass
        int classRef = cp.addClass(lambdaClassName);
        code.newInstance(classRef);
        code.dup();

        // Load captured variables onto stack (skip self-references for now)
        for (CapturedVariable captured : capturedVariables) {
            if (!captured.isSelfReference()) {
                CodeGeneratorUtils.loadParameter(code, captured.outerSlot(), captured.type());
            } else {
                // For self-references, load null initially
                code.aconst_null();
            }
        }

        // Build constructor descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (CapturedVariable captured : capturedVariables) {
            descriptor.append(captured.type());
        }
        descriptor.append(")V");

        // invokespecial <init>
        int constructorRef = cp.addMethodRef(lambdaClassName, ConstantJavaMethod.METHOD_INIT, descriptor.toString());
        code.invokespecial(constructorRef);

        // For self-referencing captures, we need to update the field AFTER the lambda is stored
        // This is handled by the caller (VarDeclProcessor) which will:
        // 1. Store the lambda in the local variable
        // 2. Load the lambda back
        // 3. Dup it
        // 4. Store into the captured field
    }

    private SelfReferenceInfo generateInternal(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo,
            String selfReferenceName,
            boolean captureThis) throws Swc4jByteCodeCompilerException {
        // Check for unsupported features
        if (arrowExpr.isAsync()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr, "Async arrow functions are not supported");
        }
        if (arrowExpr.isGenerator()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr, "Generator arrow functions are not supported");
        }

        try {
            // Generate a unique class name for this lambda
            String lambdaClassName = generateLambdaClassName();

            // Analyze captured variables, marking self-references
            List<CapturedVariable> capturedVariables = analyzeCapturedVariables(arrowExpr, selfReferenceName, captureThis);

            // Determine the functional interface and method signature
            // Pass returnTypeInfo to enable parameter type inference from target type context
            ArrowTypeInfo typeInfo = analyzeArrowType(arrowExpr, returnTypeInfo);

            // Generate the anonymous inner class bytecode
            byte[] lambdaBytecode = generateLambdaClass(lambdaClassName, arrowExpr, typeInfo, capturedVariables);

            // Store the bytecode in the compiler memory
            compiler.getMemory().getByteCodeMap().put(lambdaClassName.replace('/', '.'), lambdaBytecode);

            // Generate code to instantiate the lambda
            generateInstantiation(code, classWriter, lambdaClassName, capturedVariables);

            // Check if there are self-references that need post-processing
            for (CapturedVariable captured : capturedVariables) {
                if (captured.isSelfReference()) {
                    return new SelfReferenceInfo(lambdaClassName, captured.name(), captured.type());
                }
            }
            return null;
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr, "Failed to generate lambda class", e);
        }
    }

    private byte[] generateLambdaClass(
            String lambdaClassName,
            Swc4jAstArrowExpr arrowExpr,
            ArrowTypeInfo typeInfo,
            List<CapturedVariable> capturedVariables) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(lambdaClassName, ConstantJavaType.JAVA_LANG_OBJECT);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Add the functional interface
        classWriter.addInterface(typeInfo.interfaceName());

        // Add fields for captured variables
        for (CapturedVariable captured : capturedVariables) {
            // Self-referencing fields cannot be final (they are set after construction)
            // and must be accessible from the outer class (package-private or public)
            // 0x0000 = package-private, 0x0010 = ACC_FINAL, 0x0012 = ACC_PRIVATE | ACC_FINAL
            int fieldAccess = captured.isSelfReference() ? 0x0000 : 0x0012;
            classWriter.addField(fieldAccess,
                    "captured$" + captured.name(), captured.type());
        }

        // Generate constructor
        generateLambdaConstructor(classWriter, lambdaClassName, capturedVariables);

        // Generate the functional interface method
        generateLambdaMethod(classWriter, lambdaClassName, arrowExpr, typeInfo, capturedVariables);

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
            String lambdaClassName,
            List<CapturedVariable> capturedVariables) {
        var cp = classWriter.getConstantPool();
        CodeBuilder code = new CodeBuilder();

        // Call super()
        code.aload(0);
        int superInit = cp.addMethodRef(ConstantJavaType.JAVA_LANG_OBJECT, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
        code.invokespecial(superInit);

        // Initialize captured variable fields
        int slot = 1;
        for (CapturedVariable captured : capturedVariables) {
            code.aload(0); // this
            CodeGeneratorUtils.loadParameter(code, slot, captured.type());
            int fieldRef = cp.addFieldRef(lambdaClassName, "captured$" + captured.name(), captured.type());
            code.putfield(fieldRef);
            slot += CodeGeneratorUtils.getSlotSize(captured.type());
        }

        code.returnVoid();

        // Build constructor descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (CapturedVariable captured : capturedVariables) {
            descriptor.append(captured.type());
        }
        descriptor.append(")V");

        int maxLocals = slot;
        classWriter.addMethod(0x0001, ConstantJavaMethod.METHOD_INIT, descriptor.toString(), code.toByteArray(), 10, maxLocals);
    }

    private void generateLambdaMethod(
            ClassWriter classWriter,
            String lambdaClassName,
            Swc4jAstArrowExpr arrowExpr,
            ArrowTypeInfo typeInfo,
            List<CapturedVariable> capturedVariables) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
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
            // Store in inferred types - use original type for type checking, not holder type
            lambdaContext.getInferredTypes().put(captured.name(), captured.originalType());
            // Register as captured variable for field access resolution
            lambdaContext.getCapturedVariables().put(
                    captured.name(),
                    new com.caoccao.javet.swc4j.compiler.memory.CapturedVariable(
                            captured.name(),
                            "captured$" + captured.name(),
                            captured.type(),
                            captured.isHolder(),
                            captured.originalType()
                    )
            );
        }

        // Generate method body
        CodeBuilder code = new CodeBuilder();

        // Cast erased Object parameters to their declared types
        List<String> descriptorParams = ScoreUtils.parseParameterDescriptors(typeInfo.methodDescriptor());
        for (int i = 0; i < descriptorParams.size() && i < typeInfo.paramTypes().size(); i++) {
            String descriptorParam = descriptorParams.get(i);
            String paramType = typeInfo.paramTypes().get(i);
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(descriptorParam) && !ConstantJavaType.LJAVA_LANG_OBJECT.equals(paramType)) {
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                int classRef = cp.addClass(paramType.startsWith(ConstantJavaType.ARRAY_PREFIX) ? paramType : TypeConversionUtils.descriptorToInternalName(paramType));
                code.checkcast(classRef);
                code.astore(paramVar.index());
            }
        }

        // Apply default parameter values for assign patterns (null -> default)
        List<ISwc4jAstPat> params = arrowExpr.getParams();
        for (int i = 0; i < params.size(); i++) {
            ISwc4jAstPat param = params.get(i);
            if (param instanceof Swc4jAstAssignPat assignPat) {
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                String paramType = typeInfo.paramTypes().get(i);
                if (!TypeConversionUtils.isPrimitiveType(paramType)) {
                    code.aload(paramVar.index());
                    code.ifnonnull(0);
                    int ifnonnullOffsetPos = code.getCurrentOffset() - 2;
                    int ifnonnullOpcodePos = code.getCurrentOffset() - 3;

                    ReturnTypeInfo defaultTypeInfo = ReturnTypeInfo.of(getSourceCode(), assignPat, paramType);
                    compiler.getExpressionProcessor().generate(code, classWriter, assignPat.getRight(), defaultTypeInfo);
                    CodeGeneratorUtils.storeVariable(code, paramVar.index(), paramType);

                    int endLabel = code.getCurrentOffset();
                    int ifnonnullOffset = endLabel - ifnonnullOpcodePos;
                    code.patchShort(ifnonnullOffsetPos, ifnonnullOffset);
                }
            } else if (param instanceof Swc4jAstRestPat) {
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                String paramType = typeInfo.paramTypes().get(i);
                if (paramType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
                    code.aload(paramVar.index());
                    code.ifnonnull(0);
                    int ifnonnullOffsetPos = code.getCurrentOffset() - 2;
                    int ifnonnullOpcodePos = code.getCurrentOffset() - 3;

                    generateEmptyArray(code, classWriter, paramType);
                    code.astore(paramVar.index());

                    int endLabel = code.getCurrentOffset();
                    int ifnonnullOffset = endLabel - ifnonnullOpcodePos;
                    code.patchShort(ifnonnullOffsetPos, ifnonnullOffset);
                }
            }
        }

        // Generate destructuring extraction code for parameters
        for (int i = 0; i < params.size(); i++) {
            ISwc4jAstPat param = params.get(i);
            if (param instanceof Swc4jAstArrayPat arrayPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate array destructuring extraction
                generateArrayPatternExtraction(code, classWriter, lambdaContext, arrayPat);
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate object destructuring extraction
                generateObjectPatternExtraction(code, classWriter, lambdaContext, objectPat);
            }
        }

        ISwc4jAstBlockStmtOrExpr body = arrowExpr.getBody();
        if (body instanceof Swc4jAstBlockStmt blockStmt) {
            // Block body - generate statements
            compiler.getVariableAnalyzer().analyzeVariableDeclarations(blockStmt);
            for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
                compiler.getStatementProcessor().generate(code, classWriter, stmt, typeInfo.returnTypeInfo());
            }
            // Add return if needed
            CodeGeneratorUtils.addReturnIfNeeded(code, typeInfo.returnTypeInfo());
        } else if (body instanceof ISwc4jAstExpr expr) {
            // Expression body - implicit return
            compiler.getExpressionProcessor().generate(code, classWriter, expr, typeInfo.returnTypeInfo());
            CodeGeneratorUtils.generateReturn(code, typeInfo.returnTypeInfo());
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
     *
     * @param code        the code builder
     * @param classWriter the class writer
     * @param context     the compilation context
     * @param objectPat   the object pattern to extract
     * @throws Swc4jByteCodeCompilerException if bytecode generation fails
     */
    public void generateObjectPatternExtraction(
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
        String valueType = extractElementType(objectPat);

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
                    generateArrayPatternExtraction(code, classWriter, context, nestedArrayPat);
                } else if (valuePat instanceof Swc4jAstObjectPat nestedObjectPat) {
                    // Nested object pattern: { nested: { y, ...rest } }
                    generateObjectPatternExtraction(code, classWriter, context, nestedObjectPat);
                }

            } else if (prop instanceof Swc4jAstRestPat restPat) {
                generateObjectRestExtraction(code, classWriter, context, restPat, tempMapSlot, extractedKeys, mapRemoveRef);
            }
        }
    }

    /**
     * Generate bytecode for object rest pattern extraction {...rest}.
     */
    private void generateObjectRestExtraction(
            CodeBuilder code,
            ClassWriter classWriter,
            CompilationContext context,
            Swc4jAstRestPat restPat,
            int tempMapSlot,
            List<String> extractedKeys,
            int mapRemoveRef) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat, "Rest pattern argument must be a binding identifier");
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
    private void generateRestPatternExtraction(
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

        ISwc4jAstPat arg = restPat.getArg();
        if (!(arg instanceof Swc4jAstBindingIdent bindingIdent)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), restPat, "Rest pattern argument must be a binding identifier");
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
    private void generateUnboxingIfNeeded(CodeBuilder code, ClassWriter classWriter, String targetType) {
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

    /**
     * Gets binary operator interface info if both params and return are same primitive type.
     * Returns "interfaceName|methodName|methodDescriptor" or null if not a binary operator.
     */
    private String getBinaryOperatorInfo(String param1Type, String param2Type, ReturnTypeInfo returnInfo) {
        // IntBinaryOperator: (int, int) => int
        if (param1Type.equals(ConstantJavaType.ABBR_INTEGER) && param2Type.equals(ConstantJavaType.ABBR_INTEGER) && returnInfo.type() == ReturnType.INT) {
            return ConstantJavaType.JAVA_UTIL_FUNCTION_INT_BINARY_OPERATOR + "|" + ConstantJavaMethod.METHOD_APPLY_AS_INT + "|(II)I";
        }
        // LongBinaryOperator: (long, long) => long
        if (param1Type.equals(ConstantJavaType.ABBR_LONG) && param2Type.equals(ConstantJavaType.ABBR_LONG) && returnInfo.type() == ReturnType.LONG) {
            return ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_BINARY_OPERATOR + "|" + ConstantJavaMethod.METHOD_APPLY_AS_LONG + "|(JJ)J";
        }
        // DoubleBinaryOperator: (double, double) => double
        if (param1Type.equals(ConstantJavaType.ABBR_DOUBLE) && param2Type.equals(ConstantJavaType.ABBR_DOUBLE) && returnInfo.type() == ReturnType.DOUBLE) {
            return ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_BINARY_OPERATOR + "|" + ConstantJavaMethod.METHOD_APPLY_AS_DOUBLE + "|(DD)D";
        }
        return null;
    }

    private String getConsumerInterface(String paramType) {
        return switch (paramType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.JAVA_UTIL_FUNCTION_INT_CONSUMER;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_CONSUMER;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_CONSUMER;
            default -> ConstantJavaType.JAVA_UTIL_FUNCTION_CONSUMER;
        };
    }

    private String getConsumerMethodName(String paramType) {
        return ConstantJavaMethod.METHOD_ACCEPT;
    }

    private String getErasedFunctionDescriptor(String interfaceName) {
        if (interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_INT_FUNCTION)) {
            return ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT;
        }
        if (interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_FUNCTION)) {
            return ConstantJavaDescriptor.J__LJAVA_LANG_OBJECT;
        }
        if (interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_FUNCTION)) {
            return ConstantJavaDescriptor.D__LJAVA_LANG_OBJECT;
        }
        return ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT;
    }

    private String getErasedSupplierReturnDescriptor(ReturnTypeInfo returnInfo) {
        // For primitive supplier interfaces, return the primitive descriptor
        // For generic Supplier<T>, return Object due to type erasure
        return switch (returnInfo.type()) {
            case INT -> ConstantJavaType.ABBR_INTEGER;
            case LONG -> ConstantJavaType.ABBR_LONG;
            case DOUBLE -> ConstantJavaType.ABBR_DOUBLE;
            case BOOLEAN -> ConstantJavaType.ABBR_BOOLEAN;
            default -> ConstantJavaType.LJAVA_LANG_OBJECT;
        };
    }

    private String getFunctionInterface(String paramType, ReturnTypeInfo returnInfo) {
        // Check for primitive specializations
        return switch (paramType) {
            case ConstantJavaType.ABBR_INTEGER -> {
                if (returnInfo.type() == ReturnType.INT) {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_INT_UNARY_OPERATOR;
                } else if (returnInfo.type() == ReturnType.BOOLEAN) {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_INT_PREDICATE;
                } else {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_INT_FUNCTION;
                }
            }
            case ConstantJavaType.ABBR_LONG -> {
                if (returnInfo.type() == ReturnType.LONG) {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_UNARY_OPERATOR;
                } else {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_FUNCTION;
                }
            }
            case ConstantJavaType.ABBR_DOUBLE -> {
                if (returnInfo.type() == ReturnType.DOUBLE) {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_UNARY_OPERATOR;
                } else {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_FUNCTION;
                }
            }
            default -> {
                if (returnInfo.type() == ReturnType.BOOLEAN) {
                    yield ConstantJavaType.JAVA_UTIL_FUNCTION_PREDICATE;
                }
                yield ConstantJavaType.JAVA_UTIL_FUNCTION_FUNCTION;
            }
        };
    }

    private String getFunctionMethodName(String paramType, ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case INT -> {
                if (paramType.equals(ConstantJavaType.ABBR_INTEGER)) {
                    yield ConstantJavaMethod.METHOD_APPLY_AS_INT;
                }
                yield ConstantJavaMethod.METHOD_APPLY;
            }
            case LONG -> {
                if (paramType.equals(ConstantJavaType.ABBR_LONG)) {
                    yield ConstantJavaMethod.METHOD_APPLY_AS_LONG;
                }
                yield ConstantJavaMethod.METHOD_APPLY;
            }
            case DOUBLE -> {
                if (paramType.equals(ConstantJavaType.ABBR_DOUBLE)) {
                    yield ConstantJavaMethod.METHOD_APPLY_AS_DOUBLE;
                }
                yield ConstantJavaMethod.METHOD_APPLY;
            }
            case BOOLEAN -> ConstantJavaMethod.METHOD_TEST;
            default -> ConstantJavaMethod.METHOD_APPLY;
        };
    }

    /**
     * Get functional interface info for a known interface type.
     * This supports type erasure for generic functional interfaces.
     *
     * @param interfaceName the internal name of the interface (e.g., "java/util/function/UnaryOperator")
     * @param paramTypes    the arrow's parameter types
     * @param returnInfo    the arrow's return type info
     * @return FunctionalInterfaceInfo or null if not a known interface
     */
    private FunctionalInterfaceInfo getFunctionalInterfaceInfo(String interfaceName, List<String> paramTypes, ReturnTypeInfo returnInfo) {
        // Custom functional interface (generated from function type syntax)
        if (interfaceName.contains("/$Fn$")) {
            // Look up the method info from the registry
            var samInfo = compiler.getMemory().getScopedFunctionalInterfaceRegistry()
                    .getSamMethodInfo(interfaceName);
            if (samInfo != null) {
                return new FunctionalInterfaceInfo(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
            }
        }

        // Known functional interfaces
        var result = switch (interfaceName) {
            // UnaryOperator<T> - single param, same return type
            case ConstantJavaType.JAVA_UTIL_FUNCTION_UNARY_OPERATOR ->
                // UnaryOperator.apply(Object): Object
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_APPLY, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
            // Function<T, R> - single param, different return type
            case ConstantJavaType.JAVA_UTIL_FUNCTION_FUNCTION ->
                // Function.apply(Object): Object
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_APPLY, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
            // Supplier<T> - no params, returns T
            case ConstantJavaType.JAVA_UTIL_FUNCTION_SUPPLIER ->
                // Supplier.get(): Object
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.__LJAVA_LANG_OBJECT);
            // Consumer<T> - single param, void return
            case ConstantJavaType.JAVA_UTIL_FUNCTION_CONSUMER ->
                // Consumer.accept(Object): void
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_ACCEPT, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__V);
            // Predicate<T> - single param, boolean return
            case ConstantJavaType.JAVA_UTIL_FUNCTION_PREDICATE ->
                // Predicate.test(Object): boolean
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_TEST, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
            // BiFunction<T, U, R> - two params, returns R
            case ConstantJavaType.JAVA_UTIL_FUNCTION_BI_FUNCTION ->
                // BiFunction.apply(Object, Object): Object
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_APPLY, ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
            // BinaryOperator<T> - two params same type, returns same type
            case ConstantJavaType.JAVA_UTIL_FUNCTION_BINARY_OPERATOR ->
                // BinaryOperator.apply(Object, Object): Object
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_APPLY, ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
            // BiConsumer<T, U> - two params, void return
            case ConstantJavaType.JAVA_UTIL_FUNCTION_BI_CONSUMER ->
                // BiConsumer.accept(Object, Object): void
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_ACCEPT, ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__V);
            // BiPredicate<T, U> - two params, boolean return
            case ConstantJavaType.JAVA_UTIL_FUNCTION_BI_PREDICATE ->
                // BiPredicate.test(Object, Object): boolean
                    new FunctionalInterfaceInfo(interfaceName, ConstantJavaMethod.METHOD_TEST, ConstantJavaDescriptor.LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__Z);
            default -> null;
        };

        if (result != null) {
            return result;
        }

        // Check if it's a custom functional interface registered in the registry
        var samInfo = compiler.getMemory().getScopedFunctionalInterfaceRegistry()
                .getSamMethodInfo(interfaceName);
        if (samInfo != null) {
            return new FunctionalInterfaceInfo(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
        }

        return null;
    }

    private String getSupplierInterface(ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case INT -> ConstantJavaType.JAVA_UTIL_FUNCTION_INT_SUPPLIER;
            case LONG -> ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_SUPPLIER;
            case DOUBLE -> ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_SUPPLIER;
            case BOOLEAN -> ConstantJavaType.JAVA_UTIL_FUNCTION_BOOLEAN_SUPPLIER;
            default -> ConstantJavaType.JAVA_UTIL_FUNCTION_SUPPLIER;
        };
    }

    private String getSupplierMethodName(ReturnTypeInfo returnInfo) {
        return switch (returnInfo.type()) {
            case INT -> ConstantJavaMethod.METHOD_GET_AS_INT;
            case LONG -> ConstantJavaMethod.METHOD_GET_AS_LONG;
            case DOUBLE -> ConstantJavaMethod.METHOD_GET_AS_DOUBLE;
            case BOOLEAN -> ConstantJavaMethod.METHOD_GET_AS_BOOLEAN;
            default -> ConstantJavaMethod.METHOD_GET;
        };
    }

    /**
     * Get parameter types from a target functional interface descriptor.
     * This enables parameter type inference from context.
     * Uses reflection to dynamically resolve the SAM method parameters.
     *
     * @param interfaceDescriptor the interface type descriptor (e.g., "Ljava/util/function/IntUnaryOperator;")
     * @return list of parameter type descriptors, or null if unknown interface
     */
    private List<String> getTargetInterfaceParamTypes(String interfaceDescriptor) {
        return compiler.getMemory().getScopedFunctionalInterfaceRegistry().getParamTypes(interfaceDescriptor);
    }

    private boolean isErasedFunctionInterface(String interfaceName) {
        return interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_FUNCTION)
                || interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_UNARY_OPERATOR)
                || interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_INT_FUNCTION)
                || interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_LONG_FUNCTION)
                || interfaceName.equals(ConstantJavaType.JAVA_UTIL_FUNCTION_DOUBLE_FUNCTION);
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
     * Represents a captured variable with information about whether it's a self-reference.
     *
     * @param name            variable name
     * @param type            JVM type descriptor (holder array type if isHolder is true)
     * @param outerSlot       slot index in outer scope (holder slot if isHolder is true)
     * @param isSelfReference true if this is a self-referencing capture (recursive arrow)
     * @param isHolder        true if this capture is via a holder array (for mutable captures)
     * @param originalType    the original variable type (before holder wrapping), same as type if not a holder
     */
    private record CapturedVariable(String name, String type, int outerSlot, boolean isSelfReference, boolean isHolder,
                                    String originalType) {
        /**
         * Instantiates a new Captured variable.
         *
         * @param name      the name
         * @param type      the type
         * @param outerSlot the outer slot
         */
        CapturedVariable(String name, String type, int outerSlot) {
            this(name, type, outerSlot, false, false, type);
        }

        /**
         * Instantiates a new Captured variable.
         *
         * @param name            the name
         * @param type            the type
         * @param outerSlot       the outer slot
         * @param isSelfReference the is self reference
         */
        CapturedVariable(String name, String type, int outerSlot, boolean isSelfReference) {
            this(name, type, outerSlot, isSelfReference, false, type);
        }
    }

    /**
     * Helper record for functional interface info lookup.
     */
    private record FunctionalInterfaceInfo(String interfaceName, String methodName, String methodDescriptor) {
    }

    /**
     * Information about a self-referencing capture that needs post-processing.
     *
     * @param lambdaClassName the internal name of the lambda class
     * @param fieldName       the name of the self-reference field
     * @param fieldType       the JVM type descriptor of the field
     */
    public record SelfReferenceInfo(String lambdaClassName, String fieldName, String fieldType) {
    }
}
