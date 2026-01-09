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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
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

                int maxStack = Math.max(returnTypeInfo.maxStack(), 2); // Ensure enough for binary operations
                int maxLocals = context.getLocalVariableTable().getMaxLocals();

                classWriter.addMethod(accessFlags, methodName, descriptor, code, maxStack, maxLocals);
            } catch (Exception e) {
                throw new Swc4jByteCodeCompilerException("Failed to generate method: " + methodName, e);
            }
        }
    }

    private static String getMethodName(ISwc4jAstPropName propName) {
        if (propName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return propName.toString();
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
                    case INT -> code.ireturn();
                    case FLOAT -> code.freturn();
                    case DOUBLE -> code.dreturn();
                    case STRING -> code.areturn();
                }
            }
        }

        return code.toByteArray();
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
                    if ("F".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.FLOAT, 1);
                    } else if ("D".equals(localVar.type())) {
                        varTypeInfo = new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                    }

                    generateExpr(code, cp, init, varTypeInfo, context, options);

                    // Store the value in the local variable
                    if ("I".equals(localVar.type())) {
                        code.istore(localVar.index());
                    } else if ("F".equals(localVar.type())) {
                        code.fstore(localVar.index());
                    } else if ("D".equals(localVar.type())) {
                        // code.dstore(localVar.index());
                    } else {
                        code.astore(localVar.index());
                    }
                });
            }
        }
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
                if ("I".equals(localVar.type())) {
                    code.iload(localVar.index());
                } else if ("F".equals(localVar.type())) {
                    code.fload(localVar.index());
                } else if ("D".equals(localVar.type())) {
                    // code.dload(localVar.index());
                } else {
                    code.aload(localVar.index());
                }
            }
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            generateBinExpr(code, cp, binExpr, context, options);
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

                // Generate right operand
                generateExpr(code, cp, binExpr.getRight(), null, context, options);

                // Determine type and generate appropriate add instruction
                if ("I".equals(leftType)) {
                    code.iadd();
                } else if ("D".equals(leftType)) {
                    code.dadd();
                }
            }
        }
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
        if ("Ljava/lang/String;".equals(leftType)) {
            code.invokevirtual(appendString);
        } else if ("I".equals(leftType)) {
            code.invokevirtual(appendInt);
        }

        // Append right operand
        generateExpr(code, cp, right, null, context, options);
        if ("Ljava/lang/String;".equals(rightType)) {
            code.invokevirtual(appendString);
        } else if ("I".equals(rightType)) {
            code.invokevirtual(appendInt);
        }

        // Call toString()
        code.invokevirtual(toString);
    }

    public static String generateMethodDescriptor(Swc4jAstFunction function, ReturnTypeInfo returnTypeInfo) {
        // For now, assume no parameters
        String returnDescriptor = switch (returnTypeInfo.type()) {
            case VOID -> "V";
            case INT -> "I";
            case FLOAT -> "F";
            case DOUBLE -> "D";
            case STRING -> "Ljava/lang/String;";
        };
        return "()" + returnDescriptor;
    }
}
