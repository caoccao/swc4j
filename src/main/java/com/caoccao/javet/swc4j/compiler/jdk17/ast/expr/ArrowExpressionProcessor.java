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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstArrayPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.ArrowExprUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.PatternExtractionUtils;
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
        ReturnTypeInfo returnInfo = ArrowExprUtils.analyzeReturnType(compiler, arrowExpr, body);

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
        if (captureThis && currentClass != null && ArrowExprUtils.referencesThis(arrowExpr.getBody())) {
            captured.add(0, new CapturedVariable("this", "L" + currentClass + ";", 0, false));
        }

        return captured;
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

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstArrowExpr arrowExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        Boolean override = compiler.getMemory().getCompilationContext().popCaptureThisOverride();
        boolean captureThis = (override != null) ? override : true;
        generateInternal(code, classWriter, arrowExpr, returnTypeInfo, null, captureThis);
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
                PatternExtractionUtils.generateArrayPatternExtraction(compiler, code, classWriter, lambdaContext, arrayPat);
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = lambdaContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate object destructuring extraction
                PatternExtractionUtils.generateObjectPatternExtraction(compiler, code, classWriter, lambdaContext, objectPat);
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
