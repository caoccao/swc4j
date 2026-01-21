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
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class IdentifierGenerator {
    private IdentifierGenerator() {
    }

    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstIdent ident,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        String varName = ident.getSym();
        LocalVariable localVar = context.getLocalVariableTable().getVariable(varName);
        if (localVar != null) {
            switch (localVar.type()) {
                case "I", "S", "C", "Z", "B" -> code.iload(localVar.index());
                case "J" -> code.lload(localVar.index());
                case "F" -> code.fload(localVar.index());
                case "D" -> code.dload(localVar.index());
                default -> code.aload(localVar.index());
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
        }
    }
}
