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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstOptChainExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstOptChainBase;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstOptCall;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.JavaType;
import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generator for optional chaining expressions.
 * <p>
 * Handles optional member access (a?.b) and optional calls (a?.()).
 * Implements short-circuit evaluation where if any optional link is null,
 * the entire chain evaluates to null.
 * <p>
 * Example: {@code a?.b?.c} compiles to:
 * <pre>
 * 1. Evaluate 'a'
 * 2. If null, return null (short-circuit)
 * 3. Access 'b'
 * 4. If null, return null (short-circuit)
 * 5. Access 'c'
 * </pre>
 */
public final class OptionalChainExpressionProcessor extends BaseAstProcessor<Swc4jAstOptChainExpr> {
    /**
     * Instantiates a new Optional chain expression processor.
     *
     * @param compiler the compiler
     */
    public OptionalChainExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private void addNullJump(CodeBuilder code, List<Integer> nullJumpPositions) {
        code.ifnull(0);
        nullJumpPositions.add(code.getCurrentOffset() - 3);
    }

    private void convertType(CodeBuilder code, ClassWriter classWriter, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return;
        }
        if (TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.convertPrimitiveType(code, fromType, toType);
        } else if (TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(fromType);
            TypeConversionUtils.boxPrimitiveType(code, classWriter, fromType, wrapperType);
        } else if (!TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            TypeConversionUtils.unboxWrapperType(code, classWriter, fromType);
        }
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Collect all optional chain points to handle short-circuit properly
        List<Integer> nullJumpPositions = new ArrayList<>();
        int resultSlot = compiler.getMemory().getCompilationContext()
                .getLocalVariableTable()
                .allocateVariable("$optChainResult" + compiler.getMemory().getCompilationContext().getNextTempId(),
                        ConstantJavaType.LJAVA_LANG_OBJECT);

        // Generate the chain with null checks
        generateChain(code, classWriter, optChainExpr, nullJumpPositions);

        // Store non-null result
        code.astore(resultSlot);

        // Jump past the null handler
        code.gotoLabel(0);
        int gotoEndPos = code.getCurrentOffset() - 2;
        int gotoOpcodePos = code.getCurrentOffset() - 3;

        // NULL_LABEL: store null result (null already on stack)
        int nullLabel = code.getCurrentOffset();
        code.astore(resultSlot);

        // END_LABEL
        int endLabel = code.getCurrentOffset();
        code.aload(resultSlot);

        // Patch all null jumps to point to nullLabel
        for (int opcodePos : nullJumpPositions) {
            int offsetPos = opcodePos + 1;
            int offset = nullLabel - opcodePos;
            code.patchShort(offsetPos, offset);
        }

        // Patch goto to end
        int gotoOffset = endLabel - gotoOpcodePos;
        code.patchShort(gotoEndPos, gotoOffset);
    }

    /**
     * Generate a call when the callee (as a functional interface) is already on the stack.
     */
    private void generateCallFromStack(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            Swc4jAstOptCall optCall,
            Swc4jAstOptChainExpr nestedChain) throws Swc4jByteCodeCompilerException {
        // This handles cases like: fn?.() where fn is a functional interface
        // The functional interface object is already on stack

        String calleeType = compiler.getTypeResolver().inferTypeFromExpr(nestedChain);
        if (calleeType == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), optChainExpr,
                    "Cannot infer callee type for optional call");
        }
        generateFunctionalInterfaceCall(code, classWriter, optChainExpr, optCall, calleeType);
    }

    /**
     * Generate the chain recursively, adding null checks at optional points.
     */
    private void generateChain(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            List<Integer> nullJumpPositions) throws Swc4jByteCodeCompilerException {
        ISwc4jAstOptChainBase base = optChainExpr.getBase();

        if (base instanceof Swc4jAstMemberExpr memberExpr) {
            generateMemberAccess(code, classWriter, optChainExpr, memberExpr, nullJumpPositions);
        } else if (base instanceof Swc4jAstOptCall optCall) {
            generateOptCall(code, classWriter, optChainExpr, optCall, nullJumpPositions);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), optChainExpr,
                    "Unsupported optional chain base: " + base.getClass().getSimpleName());
        }
    }

    /**
     * Generate the field access part of a member expression.
     * Assumes the object reference is already on the stack.
     */
    private void generateFieldAccess(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Infer object type
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        if (objType == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Cannot infer object type for optional member access");
        }

        if (objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName computedProp) {
                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (indexType != null && !ConstantJavaType.ABBR_INTEGER.equals(indexType)) {
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), ConstantJavaType.ABBR_INTEGER);
                }
                String elemType = objType.substring(1);
                switch (elemType) {
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload();
                    case ConstantJavaType.ABBR_CHARACTER -> code.caload();
                    case ConstantJavaType.ABBR_SHORT -> code.saload();
                    case ConstantJavaType.ABBR_INTEGER -> code.iaload();
                    case ConstantJavaType.ABBR_LONG -> code.laload();
                    case ConstantJavaType.ABBR_FLOAT -> code.faload();
                    case ConstantJavaType.ABBR_DOUBLE -> code.daload();
                    default -> {
                        code.aaload();
                        if (elemType.startsWith("L") && elemType.endsWith(";")
                                && !ConstantJavaType.LJAVA_LANG_OBJECT.equals(elemType)) {
                            int classIndex = cp.addClass(elemType.substring(1, elemType.length() - 1));
                            code.checkcast(classIndex);
                        }
                    }
                }
                if (TypeConversionUtils.isPrimitiveType(elemType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(elemType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, elemType, wrapperType);
                }
                return;
            }
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName propIdent) {
                if (ConstantJavaMethod.METHOD_LENGTH.equals(propIdent.getSym())) {
                    code.arraylength();
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, ConstantJavaType.ABBR_INTEGER, ConstantJavaType.LJAVA_LANG_INTEGER);
                    return;
                }
            }
        } else if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType) || ConstantJavaType.LJAVA_UTIL_LIST.equals(objType)) {
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName computedProp) {
                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (ConstantJavaType.LJAVA_LANG_STRING.equals(indexType)) {
                    int parseIntMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_PARSE_INT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING__I);
                    code.invokestatic(parseIntMethod);
                }
                int getMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_OBJECT);
                code.invokeinterface(getMethod, 2);
                return;
            }
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName propIdent) {
                if (ConstantJavaMethod.METHOD_LENGTH.equals(propIdent.getSym())) {
                    int sizeMethod = cp.addInterfaceMethodRef(ConstantJavaType.JAVA_UTIL_LIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.DESCRIPTOR___I);
                    code.invokeinterface(sizeMethod, 1);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, ConstantJavaType.ABBR_INTEGER, ConstantJavaType.LJAVA_LANG_INTEGER);
                    return;
                }
            }
        } else if (ConstantJavaType.LJAVA_LANG_STRING.equals(objType)) {
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName propIdent) {
                if (ConstantJavaMethod.METHOD_LENGTH.equals(propIdent.getSym())) {
                    int lengthMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_LENGTH, ConstantJavaDescriptor.DESCRIPTOR___I);
                    code.invokevirtual(lengthMethod);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, ConstantJavaType.ABBR_INTEGER, ConstantJavaType.LJAVA_LANG_INTEGER);
                    return;
                }
            }
        } else if (ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP.equals(objType) || ConstantJavaType.LJAVA_LANG_OBJECT.equals(objType)) {
            int linkedHashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
            code.checkcast(linkedHashMapClass);
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName computedProp) {
                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null);
                String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, keyType, wrapperType);
                }
                int getMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                code.invokevirtual(getMethod);
                return;
            }
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName propIdent) {
                int keyIndex = cp.addString(propIdent.getSym());
                code.ldc(keyIndex);
                int getMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_GET, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);
                code.invokevirtual(getMethod);
                return;
            }
        } else if ("Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;".equals(objType)) {
            if (memberExpr.getProp() instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName propIdent) {
                String fieldName = propIdent.getSym();
                if ("raw".equals(fieldName)) {
                    int fieldRef = cp.addFieldRef(
                            "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray",
                            "raw",
                            ConstantJavaType.ARRAY_LJAVA_LANG_STRING
                    );
                    code.getfield(fieldRef);
                    return;
                } else if (ConstantJavaMethod.METHOD_LENGTH.equals(fieldName)) {
                    int fieldRef = cp.addFieldRef(
                            "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray",
                            ConstantJavaMethod.METHOD_LENGTH,
                            ConstantJavaType.ABBR_INTEGER
                    );
                    code.getfield(fieldRef);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, ConstantJavaType.ABBR_INTEGER, ConstantJavaType.LJAVA_LANG_INTEGER);
                    return;
                }
            }
        }

        // Get field name
        String fieldName = compiler.getTypeResolver().extractMemberExprPropName(memberExpr);
        if (fieldName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Cannot extract field name from optional member access");
        }

        if (objType.startsWith("L") && objType.endsWith(";")) {
            String className = objType.substring(1, objType.length() - 1);
            String qualifiedName = className.replace('/', '.');

            var typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                int lastSlash = className.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? className.substring(lastSlash + 1) : className;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            if (typeInfo != null) {
                var fieldInfo = typeInfo.getField(fieldName);
                if (fieldInfo != null) {
                    int fieldRef = cp.addFieldRef(typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
                    code.getfield(fieldRef);
                    if (TypeConversionUtils.isPrimitiveType(fieldInfo.descriptor())) {
                        String wrapperType = TypeConversionUtils.getWrapperType(fieldInfo.descriptor());
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, fieldInfo.descriptor(), wrapperType);
                    }
                    return;
                }
            }
        }

        throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                "Cannot resolve field '" + fieldName + "' on type " + objType);
    }

    private void generateFunctionalInterfaceCall(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            Swc4jAstOptCall optCall,
            String calleeType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (!calleeType.startsWith("L") || !calleeType.endsWith(";")) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), optChainExpr,
                    "Cannot infer callee type for optional call");
        }

        String interfaceName = calleeType.substring(1, calleeType.length() - 1);
        var samInfo = compiler.getMemory().getScopedFunctionalInterfaceRegistry().getSamMethodInfo(interfaceName);
        if (samInfo == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), optChainExpr,
                    "Cannot find SAM method for optional call on type: " + calleeType);
        }

        int interfaceRef = cp.addClass(interfaceName);
        code.checkcast(interfaceRef);

        List<Swc4jAstExprOrSpread> args = optCall.getArgs();
        for (Swc4jAstExprOrSpread arg : args) {
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
        }

        int methodRef = cp.addInterfaceMethodRef(interfaceName, samInfo.methodName(), samInfo.methodDescriptor());
        code.invokeinterface(methodRef, args.size() + 1);

        String returnType = samInfo.returnType();
        if (TypeConversionUtils.isPrimitiveType(returnType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(returnType);
            TypeConversionUtils.boxPrimitiveType(code, classWriter, returnType, wrapperType);
        }
    }

    /**
     * Generate optional member access (a?.b or a.b in an optional chain).
     */
    private void generateMemberAccess(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            Swc4jAstMemberExpr memberExpr,
            List<Integer> nullJumpPositions) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr obj = memberExpr.getObj();

        // Check if the object is also an optional chain (for nested chains like a?.b?.c)
        if (obj instanceof Swc4jAstOptChainExpr nestedChain) {
            // Recursively handle the nested chain
            generateChain(code, classWriter, nestedChain, nullJumpPositions);
        } else {
            // Generate the base object expression
            compiler.getExpressionProcessor().generate(code, classWriter, obj, null);
        }

        // If this is an optional access point, add null check
        if (optChainExpr.isOptional()) {
            // dup the object reference for null check
            code.dup();
            addNullJump(code, nullJumpPositions);
        }

        // Now generate the member access on the non-null object
        // We need to use the MemberExpressionProcessor but the object is already on stack
        // So we generate only the field access part
        generateFieldAccess(code, classWriter, memberExpr);
    }

    /**
     * Generate a method call when the object is already on the stack.
     */
    private void generateMethodCall(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr,
            Swc4jAstOptCall optCall) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get method name
        String methodName = compiler.getTypeResolver().extractMemberExprPropName(memberExpr);
        if (methodName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Cannot extract method name for optional call");
        }

        // Infer object type
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        if (objType == null || !objType.startsWith("L") || !objType.endsWith(";")) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Cannot infer object type for optional call");
        }

        String className = objType.substring(1, objType.length() - 1);
        String qualifiedName = className.replace('/', '.');

        int classRef = cp.addClass(className);
        code.checkcast(classRef);

        List<Swc4jAstExprOrSpread> args = optCall.getArgs();
        List<String> argTypes = new ArrayList<>();
        for (Swc4jAstExprOrSpread arg : args) {
            ISwc4jAstExpr argExpr = arg.getExpr();
            String argType = compiler.getTypeResolver().inferTypeFromExpr(argExpr);
            argTypes.add(argType != null ? argType : ConstantJavaType.LJAVA_LANG_OBJECT);
        }

        var typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
        if (typeInfo == null) {
            int lastSlash = className.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? className.substring(lastSlash + 1) : className;
            typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        }

        String methodDescriptor;
        String returnType;
        if (typeInfo != null) {
            var methodInfo = typeInfo.getMethod(methodName, argTypes);
            if (methodInfo == null) {
                List<String> boxedArgTypes = new ArrayList<>();
                for (String argType : argTypes) {
                    if (TypeConversionUtils.isPrimitiveType(argType)) {
                        boxedArgTypes.add(TypeConversionUtils.getWrapperType(argType));
                    } else {
                        boxedArgTypes.add(argType);
                    }
                }
                methodInfo = typeInfo.getMethod(methodName, boxedArgTypes);
            }
            if (methodInfo == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                        "Cannot resolve method '" + methodName + "' on type " + objType);
            }
            methodDescriptor = methodInfo.descriptor();
            returnType = methodInfo.returnType();

            List<String> expectedTypes = ScoreUtils.parseParameterDescriptors(methodDescriptor);
            int invokeArgCount = args.size() + 1;
            if (methodInfo.isVarArgs() && !expectedTypes.isEmpty()) {
                int fixedCount = expectedTypes.size() - 1;
                String varargArrayType = expectedTypes.get(expectedTypes.size() - 1);
                String componentType = varargArrayType.startsWith(ConstantJavaType.ARRAY_PREFIX) ? varargArrayType.substring(1) : varargArrayType;

                invokeArgCount = fixedCount + 2;

                for (int i = 0; i < fixedCount; i++) {
                    Swc4jAstExprOrSpread arg = args.get(i);
                    compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                    convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                }
                generateVarargsArray(code, classWriter, args, argTypes, fixedCount, varargArrayType, componentType);
            } else {
                for (int i = 0; i < args.size(); i++) {
                    Swc4jAstExprOrSpread arg = args.get(i);
                    compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                    if (i < expectedTypes.size()) {
                        convertType(code, classWriter, argTypes.get(i), expectedTypes.get(i));
                    }
                }
            }

            if (typeInfo.getType() == JavaType.INTERFACE) {
                int methodRef = cp.addInterfaceMethodRef(className, methodName, methodDescriptor);
                code.invokeinterface(methodRef, invokeArgCount);
            } else {
                int methodRef = cp.addMethodRef(className, methodName, methodDescriptor);
                code.invokevirtual(methodRef);
            }
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr,
                    "Cannot resolve method '" + methodName + "' on type " + objType);
        }

        if (TypeConversionUtils.isPrimitiveType(returnType)) {
            String wrapperType = TypeConversionUtils.getWrapperType(returnType);
            TypeConversionUtils.boxPrimitiveType(code, classWriter, returnType, wrapperType);
        }
    }

    /**
     * Generate optional call (a?.() or a?.b()).
     */
    private void generateOptCall(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstOptChainExpr optChainExpr,
            Swc4jAstOptCall optCall,
            List<Integer> nullJumpPositions) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr callee = optCall.getCallee();

        // Check if the callee is an optional chain (for nested chains)
        if (callee instanceof Swc4jAstOptChainExpr nestedChain) {
            if (nestedChain.getBase() instanceof Swc4jAstMemberExpr nestedMemberExpr) {
                ISwc4jAstExpr obj = nestedMemberExpr.getObj();
                if (obj instanceof Swc4jAstOptChainExpr nestedObjChain) {
                    generateChain(code, classWriter, nestedObjChain, nullJumpPositions);
                } else {
                    compiler.getExpressionProcessor().generate(code, classWriter, obj, null);
                }

                if (nestedChain.isOptional()) {
                    code.dup();
                    addNullJump(code, nullJumpPositions);
                }

                generateMethodCall(code, classWriter, nestedMemberExpr, optCall);
                return;
            }

            generateChain(code, classWriter, nestedChain, nullJumpPositions);

            if (optChainExpr.isOptional()) {
                code.dup();
                addNullJump(code, nullJumpPositions);
            }

            generateCallFromStack(code, classWriter, optChainExpr, optCall, nestedChain);
        } else if (callee instanceof Swc4jAstMemberExpr memberExpr) {
            // Callee is obj.method - need to evaluate obj and call method
            ISwc4jAstExpr obj = memberExpr.getObj();

            // Handle nested optional chains in the object
            if (obj instanceof Swc4jAstOptChainExpr nestedChain) {
                generateChain(code, classWriter, nestedChain, nullJumpPositions);
            } else {
                compiler.getExpressionProcessor().generate(code, classWriter, obj, null);
            }

            // Add null check if this is an optional point
            if (optChainExpr.isOptional()) {
                code.dup();
                addNullJump(code, nullJumpPositions);
            }

            // Generate the method call
            generateMethodCall(code, classWriter, memberExpr, optCall);
        } else {
            // Direct call (e.g., fn?.())
            compiler.getExpressionProcessor().generate(code, classWriter, callee, null);

            if (optChainExpr.isOptional()) {
                code.dup();
                addNullJump(code, nullJumpPositions);
            }

            // Generate call - this would be for functional interface invocation
            String calleeType = compiler.getTypeResolver().inferTypeFromExpr(callee);
            if (calleeType == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), optChainExpr,
                        "Cannot infer callee type for optional call");
            }
            generateFunctionalInterfaceCall(code, classWriter, optChainExpr, optCall, calleeType);
        }
    }

    private void generateVarargsArray(
            CodeBuilder code,
            ClassWriter classWriter,
            List<Swc4jAstExprOrSpread> args,
            List<String> argTypes,
            int startIndex,
            String arrayType,
            String componentType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        int varargCount = args.size() - startIndex;
        code.iconst(varargCount);

        if (TypeConversionUtils.isPrimitiveType(componentType)) {
            code.newarray(TypeConversionUtils.getNewarrayTypeCode(componentType));
        } else {
            int classIndex = cp.addClass(toInternalName(componentType));
            code.anewarray(classIndex);
        }

        for (int i = 0; i < varargCount; i++) {
            code.dup();
            code.iconst(i);
            Swc4jAstExprOrSpread arg = args.get(startIndex + i);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            String argType = argTypes.get(startIndex + i);
            convertType(code, classWriter, argType, componentType);

            switch (componentType) {
                case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.bastore();
                case ConstantJavaType.ABBR_CHARACTER -> code.castore();
                case ConstantJavaType.ABBR_SHORT -> code.sastore();
                case ConstantJavaType.ABBR_INTEGER -> code.iastore();
                case ConstantJavaType.ABBR_LONG -> code.lastore();
                case ConstantJavaType.ABBR_FLOAT -> code.fastore();
                case ConstantJavaType.ABBR_DOUBLE -> code.dastore();
                default -> code.aastore();
            }
        }
    }

    private String toInternalName(String typeDescriptor) {
        if (typeDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return typeDescriptor;
        }
        if (typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";")) {
            return typeDescriptor.substring(1, typeDescriptor.length() - 1);
        }
        return typeDescriptor;
    }
}
