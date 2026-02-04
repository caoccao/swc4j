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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class IdentifierGenerator extends BaseAstProcessor<Swc4jAstIdent> {
    public IdentifierGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstIdent ident,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        CompilationContext context = compiler.getMemory().getCompilationContext();
        String varName = ident.getSym();

        // Check if this is a captured variable (from enclosing scope)
        var capturedVar = context.getCapturedVariable(varName);
        if (capturedVar != null) {
            // Access captured variable via field: this.captured$varName
            String currentClass = context.getCurrentClassInternalName();
            code.aload(0);  // Load 'this' (the lambda instance)
            int fieldRef = cp.addFieldRef(currentClass, capturedVar.fieldName(), capturedVar.type());
            code.getfield(fieldRef);

            // If it's a holder, need to extract the value from the array
            if (capturedVar.isHolder()) {
                code.iconst(0);
                // Use originalType to determine the correct array load instruction
                switch (capturedVar.originalType()) {
                    case "I" -> code.iaload();
                    case "J" -> code.laload();
                    case "F" -> code.faload();
                    case "D" -> code.daload();
                    case "Z", "B" -> code.baload();
                    case "C" -> code.caload();
                    case "S" -> code.saload();
                    default -> code.aaload();
                }
            }
            return;
        }

        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        if (localVar != null) {
            // Check if variable uses a holder (for mutable captures)
            if (localVar.needsHolder()) {
                // Load value from holder array: holder[0]
                code.aload(localVar.holderIndex());
                code.iconst(0);
                switch (localVar.type()) {
                    case "I" -> code.iaload();
                    case "J" -> code.laload();
                    case "F" -> code.faload();
                    case "D" -> code.daload();
                    case "Z", "B" -> code.baload();
                    case "C" -> code.caload();
                    case "S" -> code.saload();
                    default -> code.aaload();
                }
            } else {
                switch (localVar.type()) {
                    case "I", "S", "C", "Z", "B" -> code.iload(localVar.index());
                    case "J" -> code.lload(localVar.index());
                    case "F" -> code.fload(localVar.index());
                    case "D" -> code.dload(localVar.index());
                    default -> code.aload(localVar.index());
                }
            }

            // Handle boxing if needed
            if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT && returnTypeInfo.descriptor() != null) {
                // Check if we need to box a primitive to its wrapper
                switch (localVar.type()) {
                    case "I" -> {
                        if ("Ljava/lang/Integer;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "Z" -> {
                        if ("Ljava/lang/Boolean;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "B" -> {
                        if ("Ljava/lang/Byte;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "C" -> {
                        if ("Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "S" -> {
                        if ("Ljava/lang/Short;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "J" -> {
                        if ("Ljava/lang/Long;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "F" -> {
                        if ("Ljava/lang/Float;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case "D" -> {
                        if ("Ljava/lang/Double;".equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                            code.invokestatic(valueOfRef);
                        }
                    }
                }
            }

            // Handle unboxing/conversion if needed (object to primitive)
            if (returnTypeInfo != null && returnTypeInfo.type() != ReturnType.OBJECT && returnTypeInfo.type() != ReturnType.STRING) {
                // Need to convert from object type to primitive
                if ("Ljava/math/BigInteger;".equals(localVar.type())) {
                    // Convert BigInteger to primitive
                    String targetDescriptor = returnTypeInfo.getPrimitiveTypeDescriptor();
                    if (targetDescriptor == null) return;
                    switch (targetDescriptor) {
                        case "I": // int
                            int intValueRef = cp.addMethodRef("java/math/BigInteger", "intValue", "()I");
                            code.invokevirtual(intValueRef);
                            break;
                        case "J": // long
                            int longValueRef = cp.addMethodRef("java/math/BigInteger", "longValue", "()J");
                            code.invokevirtual(longValueRef);
                            break;
                        case "D": // double
                            int doubleValueRef = cp.addMethodRef("java/math/BigInteger", "doubleValue", "()D");
                            code.invokevirtual(doubleValueRef);
                            break;
                        case "F": // float
                            int floatValueRef = cp.addMethodRef("java/math/BigInteger", "floatValue", "()F");
                            code.invokevirtual(floatValueRef);
                            break;
                        case "B": // byte
                            int byteValueRef = cp.addMethodRef("java/math/BigInteger", "byteValue", "()B");
                            code.invokevirtual(byteValueRef);
                            break;
                        case "S": // short
                            int shortValueRef = cp.addMethodRef("java/math/BigInteger", "shortValue", "()S");
                            code.invokevirtual(shortValueRef);
                            break;
                        case "Z": // boolean
                            // BigInteger.equals(ZERO) - zero is false, non-zero is true
                            int zeroFieldRef = cp.addFieldRef("java/math/BigInteger", "ZERO", "Ljava/math/BigInteger;");
                            code.getstatic(zeroFieldRef);
                            int equalsRef = cp.addMethodRef("java/math/BigInteger", "equals", "(Ljava/lang/Object;)Z");
                            code.invokevirtual(equalsRef);
                            // Invert: equals returns 1 for zero (false), 0 for non-zero (true)
                            code.iconst(1);
                            code.ixor();
                            break;
                    }
                }
            }
        }
    }
}
