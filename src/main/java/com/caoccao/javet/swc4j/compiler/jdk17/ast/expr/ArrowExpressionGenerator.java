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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
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

    private ArrowTypeInfo analyzeArrowType(Swc4jAstArrowExpr arrowExpr) throws Swc4jByteCodeCompilerException {
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

    private String extractParamName(ISwc4jAstPat param) {
        if (param instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        }
        return null;
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
            ArrowTypeInfo typeInfo = analyzeArrowType(arrowExpr);

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
