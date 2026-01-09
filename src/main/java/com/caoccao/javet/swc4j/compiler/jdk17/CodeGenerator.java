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

package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;

public final class CodeGenerator {
    private CodeGenerator() {
    }

    private static void appendOperandToStringBuilder(
            CodeBuilder code,
            String operandType,
            int appendString,
            int appendInt) {
        switch (operandType) {
            case "Ljava/lang/String;" -> code.invokevirtual(appendString);
            case "I" -> code.invokevirtual(appendInt);
        }
    }

    public static void generateBinExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstBinExpr binExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        Swc4jAstBinaryOp op = binExpr.getOp();

        if (op == Swc4jAstBinaryOp.Add) {
            String leftType = TypeResolver.inferTypeFromExpr(binExpr.getLeft(), context, options);
            String rightType = TypeResolver.inferTypeFromExpr(binExpr.getRight(), context, options);

            // Check if this is string concatenation
            if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                generateStringConcat(code, cp, binExpr.getLeft(), binExpr.getRight(), leftType, rightType, context, options);
            } else {
                // Generate left operand
                generateExpr(code, cp, binExpr.getLeft(), null, context, options);
                unboxWrapperType(code, cp, leftType);

                // Generate right operand
                generateExpr(code, cp, binExpr.getRight(), null, context, options);
                unboxWrapperType(code, cp, rightType);

                // Determine type and generate appropriate add instruction
                switch (leftType) {
                    case "I", "S", "Ljava/lang/Integer;", "Ljava/lang/Short;" -> code.iadd();
                    case "J", "Ljava/lang/Long;" -> code.ladd();
                    case "F", "Ljava/lang/Float;" -> code.fadd();
                    case "D", "Ljava/lang/Double;" -> code.dadd();
                }
            }
        }
    }

    public static byte[] generateClassBytecode(
            String internalClassName,
            Swc4jAstClass clazz,
            ByteCodeCompilerOptions options) throws IOException, Swc4jByteCodeCompilerException {
        ClassWriter classWriter = new ClassWriter(internalClassName);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Generate default constructor
        generateDefaultConstructor(classWriter, cp);

        // Generate methods
        for (ISwc4jAstClassMember member : clazz.getBody()) {
            if (member instanceof Swc4jAstClassMethod method) {
                generateMethod(classWriter, cp, method, options);
            }
        }

        return classWriter.toByteArray();
    }

    public static void generateDefaultConstructor(ClassWriter classWriter, ClassWriter.ConstantPool cp) {
        // Generate: public <init>() { super(); }
        int superCtorRef = cp.addMethodRef("java/lang/Object", "<init>", "()V");

        CodeBuilder code = new CodeBuilder();
        code.aload(0)                    // load this
                .invokespecial(superCtorRef) // call super()
                .returnVoid();               // return

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                "<init>",
                "()V",
                code.toByteArray(),
                1, // max stack
                1  // max locals (this)
        );
    }

    public static void generateExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr expr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        if (expr instanceof Swc4jAstStr str) {
            int stringIndex = cp.addString(str.getValue());
            code.ldc(stringIndex);
        } else if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();

            // Check if we need to convert to float based on return type
            if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.FLOAT) {
                float floatValue = (float) value;
                if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                    code.fconst(floatValue);
                } else {
                    int floatIndex = cp.addFloat(floatValue);
                    code.ldc(floatIndex);
                }
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
                // Long value
                long longValue = (long) value;
                if (longValue == 0L || longValue == 1L) {
                    code.lconst(longValue);
                } else {
                    int longIndex = cp.addLong(longValue);
                    code.ldc2_w(longIndex);
                }
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                // Box integer to Integer
                int intValue = (int) value;
                code.iconst(intValue);
                int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                // Box short to Short
                short shortValue = (short) value;
                code.iconst(shortValue);
                int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                // Box long to Long
                long longValue = (long) value;
                if (longValue == 0L || longValue == 1L) {
                    code.lconst(longValue);
                } else {
                    int longIndex = cp.addLong(longValue);
                    code.ldc2_w(longIndex);
                }
                int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                // Box float to Float
                float floatValue = (float) value;
                if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                    code.fconst(floatValue);
                } else {
                    int floatIndex = cp.addFloat(floatValue);
                    code.ldc(floatIndex);
                }
                int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                code.invokestatic(valueOfRef);
            } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                    && "Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                // Box double to Double
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    int doubleIndex = cp.addDouble(value);
                    code.ldc2_w(doubleIndex);
                }
                int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                code.invokestatic(valueOfRef);
            } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                code.iconst((int) value);
            } else {
                // For double values
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    int doubleIndex = cp.addDouble(value);
                    code.ldc2_w(doubleIndex);
                }
            }
        } else if (expr instanceof Swc4jAstIdent ident) {
            String varName = ident.getSym();
            LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
            if (localVar != null) {
                switch (localVar.type()) {
                    case "I", "S" -> code.iload(localVar.index());
                    case "J" -> code.lload(localVar.index());
                    case "F" -> code.fload(localVar.index());
                    case "D" -> {
                        // code.dload(localVar.index());
                    }
                    default -> code.aload(localVar.index());
                }
            }
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            generateBinExpr(code, cp, binExpr, context, options);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            generateUnaryExpr(code, cp, unaryExpr, returnTypeInfo, context, options);
        }
    }

    public static void generateMethod(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            Swc4jAstClassMethod method,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        ISwc4jAstPropName key = method.getKey();
        String methodName = getMethodName(key);
        Swc4jAstFunction function = method.getFunction();

        // Only handle methods with bodies
        var bodyOpt = function.getBody();
        if (bodyOpt.isPresent()) {
            try {
                Swc4jAstBlockStmt body = bodyOpt.get();
                CompilationContext context = new CompilationContext();

                // Analyze variable declarations and infer types
                VariableAnalyzer.analyzeVariableDeclarations(body, context, options);

                // Determine return type from method body or explicit annotation
                ReturnTypeInfo returnTypeInfo = TypeResolver.analyzeReturnType(function, body, context, options);
                String descriptor = generateMethodDescriptor(function, returnTypeInfo);
                byte[] code = generateMethodCode(cp, body, returnTypeInfo, context, options);

                int accessFlags = 0x0001; // ACC_PUBLIC
                if (method.isStatic()) {
                    accessFlags |= 0x0008; // ACC_STATIC
                }

                int maxStack = Math.max(returnTypeInfo.maxStack(), returnTypeInfo.type().getMinStack());
                int maxLocals = context.getLocalVariableTable().getMaxLocals();

                classWriter.addMethod(accessFlags, methodName, descriptor, code, maxStack, maxLocals);
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException("Failed to generate method: " + methodName, e);
            }
        }
    }

    public static byte[] generateMethodCode(
            ClassWriter.ConstantPool cp,
            Swc4jAstBlockStmt body,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        CodeBuilder code = new CodeBuilder();

        // Process statements in the method body
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstVarDecl varDecl) {
                generateVarDecl(code, cp, varDecl, context, options);
            } else if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                returnStmt.getArg().ifPresent(arg -> {
                    generateExpr(code, cp, arg, returnTypeInfo, context, options);
                });

                // Generate appropriate return instruction
                switch (returnTypeInfo.type()) {
                    case VOID -> code.returnVoid();
                    case INT, SHORT -> code.ireturn();
                    case LONG -> code.lreturn();
                    case FLOAT -> code.freturn();
                    case DOUBLE -> code.dreturn();
                    case STRING, OBJECT -> code.areturn();
                }
            }
        }

        return code.toByteArray();
    }

    public static String generateMethodDescriptor(Swc4jAstFunction function, ReturnTypeInfo returnTypeInfo) {
        // For now, assume no parameters
        String returnDescriptor = switch (returnTypeInfo.type()) {
            case VOID -> "V";
            case INT -> "I";
            case SHORT -> "S";
            case LONG -> "J";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
            case OBJECT -> returnTypeInfo.descriptor() != null ? returnTypeInfo.descriptor() : "Ljava/lang/Object;";
        };
        return "()" + returnDescriptor;
    }

    public static void generateStringConcat(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            String leftType,
            String rightType,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        // Use StringBuilder for string concatenation
        // new StringBuilder
        int stringBuilderClass = cp.addClass("java/lang/StringBuilder");
        int stringBuilderInit = cp.addMethodRef("java/lang/StringBuilder", "<init>", "()V");
        int appendString = cp.addMethodRef("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int appendInt = cp.addMethodRef("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
        int toString = cp.addMethodRef("java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

        code.newInstance(stringBuilderClass)
                .dup()
                .invokespecial(stringBuilderInit);

        // Append left operand
        generateExpr(code, cp, left, null, context, options);
        appendOperandToStringBuilder(code, leftType, appendString, appendInt);

        // Append right operand
        generateExpr(code, cp, right, null, context, options);
        appendOperandToStringBuilder(code, rightType, appendString, appendInt);

        // Call toString()
        code.invokevirtual(toString);
    }

    public static void generateUnaryExpr(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstUnaryExpr unaryExpr,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        Swc4jAstUnaryOp op = unaryExpr.getOp();

        if (op == Swc4jAstUnaryOp.Minus) {
            // Handle numeric negation
            ISwc4jAstExpr arg = unaryExpr.getArg();

            if (arg instanceof Swc4jAstNumber number) {
                // Directly generate the negated value
                double value = number.getValue();

                // Check if we're dealing with a long type
                if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.LONG) {
                    long longValue = -(long) value;
                    if (longValue == 0L || longValue == 1L) {
                        code.lconst(longValue);
                    } else {
                        int longIndex = cp.addLong(longValue);
                        code.ldc2_w(longIndex);
                    }
                } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                        && "Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                    // Check if we're dealing with a Long wrapper
                    long longValue = -(long) value;
                    if (longValue == 0L || longValue == 1L) {
                        code.lconst(longValue);
                    } else {
                        int longIndex = cp.addLong(longValue);
                        code.ldc2_w(longIndex);
                    }
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                    code.invokestatic(valueOfRef);
                } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                        && "Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                    // Check if we're dealing with a Short wrapper
                    short shortValue = (short) -(int) value;
                    code.iconst(shortValue);
                    int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                    code.invokestatic(valueOfRef);
                } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                        && "Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                    // Check if we're dealing with a Float wrapper
                    float floatValue = -(float) value;
                    if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                        code.fconst(floatValue);
                    } else {
                        int floatIndex = cp.addFloat(floatValue);
                        code.ldc(floatIndex);
                    }
                    int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                    code.invokestatic(valueOfRef);
                } else if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                        && "Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                    // Check if we're dealing with a Double wrapper
                    double doubleValue = -value;
                    if (doubleValue == 0.0 || doubleValue == 1.0) {
                        code.dconst(doubleValue);
                    } else {
                        int doubleIndex = cp.addDouble(doubleValue);
                        code.ldc2_w(doubleIndex);
                    }
                    int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                    code.invokestatic(valueOfRef);
                } else if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                    // Integer value - use iconst for negated int
                    code.iconst(-(int) value);
                } else {
                    // Floating point value - use dconst
                    code.dconst(-value);
                }
            } else {
                // For complex expressions, generate the expression first then negate
                generateExpr(code, cp, arg, null, context, options);

                String argType = TypeResolver.inferTypeFromExpr(arg, context, options);
                switch (argType) {
                    case "D" -> code.dneg();
                    case "F" -> code.fneg();
                    case "J" -> code.lneg();
                    default -> code.ineg();
                }
            }
        }
    }

    public static void generateVarDecl(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstVarDecl varDecl,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        for (Swc4jAstVarDeclarator declarator : varDecl.getDecls()) {
            ISwc4jAstPat name = declarator.getName();
            if (name instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);

                declarator.getInit().ifPresent(init -> {
                    // Create a ReturnTypeInfo based on the variable type so we can convert the value appropriately
                    ReturnTypeInfo varTypeInfo = null;
                    if ("S".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.SHORT, 1, null);
                    } else if ("J".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.LONG, 2, null);
                    } else if ("F".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.FLOAT, 1, null);
                    } else if ("D".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.DOUBLE, 2, null);
                    } else if (localVar.type().startsWith("L") && localVar.type().endsWith(";")) {
                        // Object type
                        varTypeInfo = new ReturnTypeInfo(ReturnType.OBJECT, 1, localVar.type());
                    }

                    generateExpr(code, cp, init, varTypeInfo, context, options);

                    // Store the value in the local variable
                    switch (localVar.type()) {
                        case "I", "S" -> code.istore(localVar.index());
                        case "J" -> code.lstore(localVar.index());
                        case "F" -> code.fstore(localVar.index());
                        case "D" -> {
                            // code.dstore(localVar.index());
                        }
                        default -> code.astore(localVar.index());
                    }
                });
            }
        }
    }

    private static String getMethodName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
    }

    private static void unboxWrapperType(CodeBuilder code, ClassWriter.ConstantPool cp, String type) {
        switch (type) {
            case "Ljava/lang/Integer;" -> {
                int intValueRef = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueRef);
            }
            case "Ljava/lang/Long;" -> {
                int longValueRef = cp.addMethodRef("java/lang/Long", "longValue", "()J");
                code.invokevirtual(longValueRef);
            }
            case "Ljava/lang/Short;" -> {
                int shortValueRef = cp.addMethodRef("java/lang/Short", "shortValue", "()S");
                code.invokevirtual(shortValueRef);
            }
            case "Ljava/lang/Float;" -> {
                int floatValueRef = cp.addMethodRef("java/lang/Float", "floatValue", "()F");
                code.invokevirtual(floatValueRef);
            }
            case "Ljava/lang/Double;" -> {
                int doubleValueRef = cp.addMethodRef("java/lang/Double", "doubleValue", "()D");
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
