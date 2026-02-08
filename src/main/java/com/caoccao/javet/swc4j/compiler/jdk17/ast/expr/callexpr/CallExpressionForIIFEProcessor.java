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


package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.memory.CapturedVariable;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.*;

/**
 * Generates bytecode for IIFE (Immediately Invoked Function Expression) patterns.
 * <p>
 * Handles patterns like: {@code ((x: int): int => x * 2)(5)}
 * <p>
 * IIFE is compiled to a custom interface with naming convention $interfaceN where N is an integer,
 * implemented by an anonymous inner class, which is immediately instantiated and invoked.
 */
public final class CallExpressionForIIFEProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    private int interfaceCounter = 0;

    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForIIFEProcessor(ByteCodeCompiler compiler) {
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

    private List<IIFECapturedVariable> analyzeCapturedVariables(Swc4jAstArrowExpr arrowExpr, boolean captureThis) {
        List<IIFECapturedVariable> captured = new ArrayList<>();

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
                captured.add(new IIFECapturedVariable(varName, localVar.type(), localVar.index()));
            }
        }

        // Check if 'this' is captured
        String currentClass = context.getCurrentClassInternalName();
        if (captureThis && currentClass != null && referencesThis(arrowExpr.getBody())) {
            captured.add(0, new IIFECapturedVariable("this", "L" + currentClass + ";", 0));
        }

        return captured;
    }

    private IIFETypeInfo analyzeIIFEType(Swc4jAstArrowExpr arrowExpr)
            throws Swc4jByteCodeCompilerException {
        List<ISwc4jAstPat> params = arrowExpr.getParams();
        ISwc4jAstBlockStmtOrExpr body = arrowExpr.getBody();

        // Build parameter types
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (ISwc4jAstPat param : params) {
            String paramType = compiler.getTypeResolver().extractParameterType(param);
            String paramName = extractParamName(param);
            paramTypes.add(paramType);
            paramNames.add(paramName != null ? paramName : "arg" + paramTypes.size());
        }

        // Push a new scope for parameter types to enable return type inference
        CompilationContext context = compiler.getMemory().getCompilationContext();
        Map<String, String> paramScope = context.pushInferredTypesScope();
        for (int i = 0; i < paramNames.size(); i++) {
            paramScope.put(paramNames.get(i), paramTypes.get(i));
        }

        // Determine return type
        ReturnTypeInfo returnInfo = analyzeReturnType(arrowExpr, body);

        // Pop the parameter types scope
        context.popInferredTypesScope();

        // For IIFE, we generate a custom interface with method name "call"
        String interfaceName = generateInterfaceName();
        String methodName = "call";
        StringBuilder methodDescriptor = new StringBuilder("(");
        for (String paramType : paramTypes) {
            methodDescriptor.append(paramType);
        }
        methodDescriptor.append(")").append(TypeConversionUtils.getReturnDescriptor(returnInfo));

        return new IIFETypeInfo(interfaceName, methodName, methodDescriptor.toString(),
                paramTypes, paramNames, returnInfo);
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

    private Set<String> collectReferencedIdentifiers(ISwc4jAstBlockStmtOrExpr body) {
        Set<String> identifiers = new HashSet<>();
        collectIdentifiersRecursive(body, identifiers);
        return identifiers;
    }

    private void collectVariableTypes(List<ISwc4jAstStmt> stmts, Map<String, String> varTypes) throws Swc4jByteCodeCompilerException {
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

    private boolean containsThis(ISwc4jAst ast) {
        if (ast instanceof Swc4jAstThisExpr) {
            return true;
        }
        for (var child : ast.getChildNodes()) {
            if (child instanceof Swc4jAstThisExpr) {
                return true;
            }
            if (containsThis(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract arrow expression from a potentially parenthesized expression.
     */
    private Swc4jAstArrowExpr extractArrowExpr(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstArrowExpr arrow) {
            return arrow;
        }
        if (expr instanceof Swc4jAstParenExpr parenExpr) {
            return extractArrowExpr(parenExpr.getExpr());
        }
        return null;
    }

    private Swc4jAstFnExpr extractFunctionExpr(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstFnExpr fnExpr) {
            return fnExpr;
        }
        if (expr instanceof Swc4jAstParenExpr parenExpr) {
            return extractFunctionExpr(parenExpr.getExpr());
        }
        return null;
    }

    private String extractParamName(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        } else if (param instanceof Swc4jAstRestPat restPat) {
            return extractParamName(restPat.getArg());
        } else if (param instanceof Swc4jAstAssignPat assignPat) {
            return extractParamName(assignPat.getLeft());
        }
        return null;
    }

    private ReturnTypeInfo findReturnType(ISwc4jAstStmt stmt, Map<String, String> varTypes)
            throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            if (returnStmt.getArg().isPresent()) {
                ISwc4jAstExpr arg = returnStmt.getArg().get();
                if (arg instanceof Swc4jAstIdent ident) {
                    String type = varTypes.get(ident.getSym());
                    if (type != null) {
                        return compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(type);
                    }
                }
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
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Extract arrow expression from parenthesized expression
        var callee = callExpr.getCallee();
        if (!(callee instanceof ISwc4jAstExpr calleeExpr)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "IIFE callee must be an expression");
        }

        Swc4jAstArrowExpr arrowExpr = extractArrowExpr(calleeExpr);
        boolean isFunctionExpr = false;
        if (arrowExpr == null) {
            Swc4jAstFnExpr fnExpr = extractFunctionExpr(calleeExpr);
            if (fnExpr != null) {
                arrowExpr = toArrowExpr(fnExpr);
                isFunctionExpr = true;
            }
        }
        if (arrowExpr == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                    "IIFE callee must be an arrow or function expression");
        }

        // Check for unsupported features
        if (arrowExpr.isAsync()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr, "Async IIFE are not supported");
        }
        if (arrowExpr.isGenerator()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), arrowExpr, "Generator IIFE are not supported");
        }

        try {
            // Analyze captured variables
            List<IIFECapturedVariable> capturedVariables = analyzeCapturedVariables(arrowExpr, !isFunctionExpr);

            // Determine the interface and method signature
            IIFETypeInfo typeInfo = analyzeIIFEType(arrowExpr);

            // Generate the custom interface
            byte[] interfaceBytecode = generateInterface(typeInfo);
            compiler.getMemory().getByteCodeMap().put(typeInfo.interfaceName().replace('/', '.'), interfaceBytecode);

            // Generate the anonymous implementation class
            String implClassName = generateImplClassName();
            byte[] implBytecode = generateImplClass(implClassName, arrowExpr, typeInfo, capturedVariables);
            compiler.getMemory().getByteCodeMap().put(implClassName.replace('/', '.'), implBytecode);

            // Generate code to instantiate, invoke, and get result
            generateInvocation(code, classWriter, implClassName, capturedVariables, typeInfo, callExpr.getArgs());
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Failed to generate IIFE", e);
        }
    }

    private byte[] generateImplClass(
            String implClassName,
            Swc4jAstArrowExpr arrowExpr,
            IIFETypeInfo typeInfo,
            List<IIFECapturedVariable> capturedVariables) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(implClassName, ConstantJavaType.JAVA_LANG_OBJECT);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Add the interface
        classWriter.addInterface(typeInfo.interfaceName());

        // Add fields for captured variables
        for (IIFECapturedVariable captured : capturedVariables) {
            classWriter.addField(0x0012, // ACC_PRIVATE | ACC_FINAL
                    "captured$" + captured.name(), captured.type());
        }

        // Generate constructor
        generateImplConstructor(classWriter, implClassName, capturedVariables);

        // Generate the interface method implementation
        generateImplMethod(classWriter, implClassName, arrowExpr, typeInfo, capturedVariables);

        return classWriter.toByteArray();
    }

    private String generateImplClassName() {
        String currentClass = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClass != null) {
            return currentClass + "$IIFE$" + interfaceCounter;
        } else {
            return "$IIFE$" + interfaceCounter;
        }
    }

    private void generateImplConstructor(
            ClassWriter classWriter,
            String implClassName,
            List<IIFECapturedVariable> capturedVariables) {
        CodeBuilder code = new CodeBuilder();

        // Call super()
        code.aload(0);
        var cp = classWriter.getConstantPool();
        int superInit = cp.addMethodRef(ConstantJavaType.JAVA_LANG_OBJECT, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
        code.invokespecial(superInit);

        // Initialize captured variable fields
        int slot = 1;
        for (IIFECapturedVariable captured : capturedVariables) {
            code.aload(0); // this
            CodeGeneratorUtils.loadParameter(code, slot, captured.type());
            int fieldRef = cp.addFieldRef(implClassName, "captured$" + captured.name(), captured.type());
            code.putfield(fieldRef);
            slot += getSlotSize(captured.type());
        }

        code.returnVoid();

        // Build constructor descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (IIFECapturedVariable captured : capturedVariables) {
            descriptor.append(captured.type());
        }
        descriptor.append(")V");

        int maxLocals = slot;
        classWriter.addMethod(0x0001, ConstantJavaMethod.METHOD_INIT, descriptor.toString(), code.toByteArray(), 10, maxLocals);
    }

    private void generateImplMethod(
            ClassWriter classWriter,
            String implClassName,
            Swc4jAstArrowExpr arrowExpr,
            IIFETypeInfo typeInfo,
            List<IIFECapturedVariable> capturedVariables) throws Swc4jByteCodeCompilerException {
        // Push a new compilation context for the method (instance method, so slot 0 is 'this')
        CompilationContext methodContext = compiler.getMemory().pushCompilationContext(false);

        // Push the impl class as current class
        methodContext.pushClass(implClassName);

        // Allocate slots for parameters
        for (int i = 0; i < typeInfo.paramNames().size(); i++) {
            String paramName = typeInfo.paramNames().get(i);
            String paramType = typeInfo.paramTypes().get(i);
            methodContext.getLocalVariableTable().allocateVariable(paramName, paramType);
            methodContext.getInferredTypes().put(paramName, paramType);
        }

        // Map captured variables to field access
        for (IIFECapturedVariable captured : capturedVariables) {
            methodContext.getInferredTypes().put(captured.name(), captured.type());
            methodContext.getCapturedVariables().put(
                    captured.name(),
                    new CapturedVariable(
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
                LocalVariable paramVar = methodContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate array destructuring extraction
                compiler.getArrowExpressionProcessor().generateArrayPatternExtraction(code, classWriter, methodContext, arrayPat);
            } else if (param instanceof Swc4jAstObjectPat objectPat) {
                // Load the parameter value onto the stack
                String paramName = typeInfo.paramNames().get(i);
                LocalVariable paramVar = methodContext.getLocalVariableTable().getVariable(paramName);
                code.aload(paramVar.index());
                // Generate object destructuring extraction
                compiler.getArrowExpressionProcessor().generateObjectPatternExtraction(code, classWriter, methodContext, objectPat);
            }
        }

        ISwc4jAstBlockStmtOrExpr body = arrowExpr.getBody();
        if (body instanceof Swc4jAstBlockStmt blockStmt) {
            // Block body - generate statements
            compiler.getVariableAnalyzer().analyzeVariableDeclarations(blockStmt);
            for (ISwc4jAstStmt stmt : blockStmt.getStmts()) {
                compiler.getStatementProcessor().generate(code, classWriter, stmt, typeInfo.returnTypeInfo());
            }
            addReturnIfNeeded(code, typeInfo.returnTypeInfo());
        } else if (body instanceof ISwc4jAstExpr expr) {
            // Expression body - implicit return
            compiler.getExpressionProcessor().generate(code, classWriter, expr, typeInfo.returnTypeInfo());
            generateReturn(code, typeInfo.returnTypeInfo());
        }

        // Pop impl class context
        methodContext.popClass();

        int maxLocals = methodContext.getLocalVariableTable().getMaxLocals();

        // Generate stack map table
        boolean isStatic = false;
        var cp = classWriter.getConstantPool();
        var stackMapTable = code.generateStackMapTable(maxLocals, isStatic, implClassName,
                typeInfo.methodDescriptor(), cp);
        var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();

        classWriter.addMethod(0x0001, // ACC_PUBLIC
                typeInfo.methodName(), typeInfo.methodDescriptor(), code.toByteArray(),
                10, maxLocals, null, null, stackMapTable, exceptionTable);

        // Pop the method compilation context
        compiler.getMemory().popCompilationContext();
    }

    private byte[] generateInterface(IIFETypeInfo typeInfo) throws IOException {
        ClassWriter classWriter = new ClassWriter(typeInfo.interfaceName(), ConstantJavaType.JAVA_LANG_OBJECT);
        // Set interface flag: ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT
        classWriter.setAccessFlags(0x0601);

        // Add the abstract method
        classWriter.addAbstractMethod(typeInfo.methodName(), typeInfo.methodDescriptor());

        return classWriter.toByteArray();
    }

    private String generateInterfaceName() {
        String currentClass = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClass != null) {
            return currentClass + "/$interface" + (++interfaceCounter);
        } else {
            return "$interface" + (++interfaceCounter);
        }
    }

    private void generateInvocation(
            CodeBuilder code,
            ClassWriter classWriter,
            String implClassName,
            List<IIFECapturedVariable> capturedVariables,
            IIFETypeInfo typeInfo,
            List<Swc4jAstExprOrSpread> callArgs) throws Swc4jByteCodeCompilerException {
        // new ImplClass
        var cp = classWriter.getConstantPool();
        int classRef = cp.addClass(implClassName);
        code.newInstance(classRef);
        code.dup();

        // Load captured variables onto stack
        for (IIFECapturedVariable captured : capturedVariables) {
            CodeGeneratorUtils.loadParameter(code, captured.outerSlot(), captured.type());
        }

        // Build constructor descriptor
        StringBuilder constructorDesc = new StringBuilder("(");
        for (IIFECapturedVariable captured : capturedVariables) {
            constructorDesc.append(captured.type());
        }
        constructorDesc.append(")V");

        // invokespecial <init>
        int constructorRef = cp.addMethodRef(implClassName, ConstantJavaMethod.METHOD_INIT, constructorDesc.toString());
        code.invokespecial(constructorRef);

        // Now we have the instance on the stack, call the method with arguments
        // Push arguments onto stack
        for (int i = 0; i < callArgs.size(); i++) {
            var arg = callArgs.get(i);
            if (arg.getSpread().isPresent()) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg, "Spread operator in IIFE arguments not yet supported");
            }
            // Create return type info for the argument based on the parameter type
            String paramType = typeInfo.paramTypes().get(i);
            ReturnTypeInfo argReturnType = compiler.getTypeResolver().createReturnTypeInfoFromDescriptor(paramType);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), argReturnType);
        }

        // invokeinterface call
        int methodRef = cp.addInterfaceMethodRef(typeInfo.interfaceName(), typeInfo.methodName(),
                typeInfo.methodDescriptor());
        // Calculate stack slots: 1 for 'this' + slots for all parameters
        int stackSlots = 1; // 'this'
        for (String paramType : typeInfo.paramTypes()) {
            stackSlots += getSlotSize(paramType);
        }
        code.invokeinterface(methodRef, stackSlots);
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

    private int getSlotSize(String type) {
        return (ConstantJavaType.ABBR_LONG.equals(type) || ConstantJavaType.ABBR_DOUBLE.equals(type)) ? 2 : 1;
    }

    /**
     * Check if the callee is an IIFE pattern (parenthesized arrow expression).
     *
     * @param callee the callee to check
     * @return true if the callee is an IIFE
     */
    public boolean isCalleeSupported(ISwc4jAstCallee callee) {
        if (callee instanceof ISwc4jAstExpr expr) {
            return extractArrowExpr(expr) != null || extractFunctionExpr(expr) != null;
        }
        return false;
    }

    private boolean referencesThis(ISwc4jAstBlockStmtOrExpr body) {
        if (body instanceof Swc4jAstThisExpr) {
            return true;
        }
        for (var child : body.getChildNodes()) {
            if (child instanceof Swc4jAstThisExpr) {
                return true;
            }
            if (child instanceof ISwc4jAstBlockStmtOrExpr childBody) {
                if (referencesThis(childBody)) {
                    return true;
                }
            } else {
                if (containsThis(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Reset the interface counter. Call this when starting a new compilation.
     */
    public void reset() {
        interfaceCounter = 0;
    }

    private Swc4jAstArrowExpr toArrowExpr(Swc4jAstFnExpr fnExpr) {
        Swc4jAstFunction function = fnExpr.getFunction();
        if (function.getBody().isEmpty()) {
            return null;
        }
        List<ISwc4jAstPat> params = new ArrayList<>();
        for (Swc4jAstParam param : function.getParams()) {
            params.add(param.getPat());
        }
        return new Swc4jAstArrowExpr(
                function.getCtxt(),
                params,
                function.getBody().get(),
                function.isAsync(),
                function.isGenerator(),
                function.getTypeParams().orElse(null),
                function.getReturnType().orElse(null),
                function.getSpan()
        );
    }

    /**
     * Record for captured variable information.
     */
    private record IIFECapturedVariable(String name, String type, int outerSlot) {
    }

    /**
     * Record for IIFE type information.
     */
    private record IIFETypeInfo(
            String interfaceName,
            String methodName,
            String methodDescriptor,
            List<String> paramTypes,
            List<String> paramNames,
            ReturnTypeInfo returnTypeInfo) {
    }
}
