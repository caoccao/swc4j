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
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.LocalVariable;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Processes identifier expressions (variable references).
 */
public final class IdentifierProcessor extends BaseAstProcessor<Swc4jAstIdent> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public IdentifierProcessor(ByteCodeCompiler compiler) {
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
                    case ConstantJavaType.ABBR_INTEGER -> code.iaload();
                    case ConstantJavaType.ABBR_LONG -> code.laload();
                    case ConstantJavaType.ABBR_FLOAT -> code.faload();
                    case ConstantJavaType.ABBR_DOUBLE -> code.daload();
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload();
                    case ConstantJavaType.ABBR_CHARACTER -> code.caload();
                    case ConstantJavaType.ABBR_SHORT -> code.saload();
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
                    case ConstantJavaType.ABBR_INTEGER -> code.iaload();
                    case ConstantJavaType.ABBR_LONG -> code.laload();
                    case ConstantJavaType.ABBR_FLOAT -> code.faload();
                    case ConstantJavaType.ABBR_DOUBLE -> code.daload();
                    case ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BYTE -> code.baload();
                    case ConstantJavaType.ABBR_CHARACTER -> code.caload();
                    case ConstantJavaType.ABBR_SHORT -> code.saload();
                    default -> code.aaload();
                }
            } else {
                switch (localVar.type()) {
                    case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_SHORT,
                         ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_BOOLEAN,
                         ConstantJavaType.ABBR_BYTE -> code.iload(localVar.index());
                    case ConstantJavaType.ABBR_LONG -> code.lload(localVar.index());
                    case ConstantJavaType.ABBR_FLOAT -> code.fload(localVar.index());
                    case ConstantJavaType.ABBR_DOUBLE -> code.dload(localVar.index());
                    default -> code.aload(localVar.index());
                }
            }

            // Handle boxing if needed
            if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT && returnTypeInfo.descriptor() != null) {
                // Check if we need to box a primitive to its wrapper
                switch (localVar.type()) {
                    case ConstantJavaType.ABBR_INTEGER -> {
                        if (ConstantJavaType.LJAVA_LANG_INTEGER.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.I__LJAVA_LANG_INTEGER);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_BOOLEAN -> {
                        if (ConstantJavaType.LJAVA_LANG_BOOLEAN.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BOOLEAN, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.Z__LJAVA_LANG_BOOLEAN);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_BYTE -> {
                        if (ConstantJavaType.LJAVA_LANG_BYTE.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.B__LJAVA_LANG_BYTE);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_CHARACTER -> {
                        if (ConstantJavaType.LJAVA_LANG_CHARACTER.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_CHARACTER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.C__LJAVA_LANG_CHARACTER);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_SHORT -> {
                        if (ConstantJavaType.LJAVA_LANG_SHORT.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.S__LJAVA_LANG_SHORT);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_LONG -> {
                        if (ConstantJavaType.LJAVA_LANG_LONG.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.J__LJAVA_LANG_LONG);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_FLOAT -> {
                        if (ConstantJavaType.LJAVA_LANG_FLOAT.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.F__LJAVA_LANG_FLOAT);
                            code.invokestatic(valueOfRef);
                        }
                    }
                    case ConstantJavaType.ABBR_DOUBLE -> {
                        if (ConstantJavaType.LJAVA_LANG_DOUBLE.equals(returnTypeInfo.descriptor())) {
                            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.D__LJAVA_LANG_DOUBLE);
                            code.invokestatic(valueOfRef);
                        }
                    }
                }
            }

            // Handle unboxing/conversion if needed (object to primitive)
            if (returnTypeInfo != null && returnTypeInfo.type() != ReturnType.OBJECT && returnTypeInfo.type() != ReturnType.STRING) {
                // Need to convert from object type to primitive
                if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(localVar.type())) {
                    // Convert BigInteger to primitive
                    String targetDescriptor = returnTypeInfo.type().getPrimitiveDescriptor();
                    switch (targetDescriptor) {
                        case ConstantJavaType.ABBR_INTEGER: // int
                            int intValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_INT_VALUE, ConstantJavaDescriptor.__I);
                            code.invokevirtual(intValueRef);
                            break;
                        case ConstantJavaType.ABBR_LONG: // long
                            int longValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_LONG_VALUE, ConstantJavaDescriptor.__J);
                            code.invokevirtual(longValueRef);
                            break;
                        case ConstantJavaType.ABBR_DOUBLE: // double
                            int doubleValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_DOUBLE_VALUE, ConstantJavaDescriptor.__D);
                            code.invokevirtual(doubleValueRef);
                            break;
                        case ConstantJavaType.ABBR_FLOAT: // float
                            int floatValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_FLOAT_VALUE, ConstantJavaDescriptor.__F);
                            code.invokevirtual(floatValueRef);
                            break;
                        case ConstantJavaType.ABBR_BYTE: // byte
                            int byteValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_BYTE_VALUE, ConstantJavaDescriptor.__B);
                            code.invokevirtual(byteValueRef);
                            break;
                        case ConstantJavaType.ABBR_SHORT: // short
                            int shortValueRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_SHORT_VALUE, ConstantJavaDescriptor.__S);
                            code.invokevirtual(shortValueRef);
                            break;
                        case ConstantJavaType.ABBR_BOOLEAN: // boolean
                            // BigInteger.equals(ZERO) - zero is false, non-zero is true
                            int zeroFieldRef = cp.addFieldRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, "ZERO", ConstantJavaType.LJAVA_MATH_BIGINTEGER);
                            code.getstatic(zeroFieldRef);
                            int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_MATH_BIGINTEGER, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
                            code.invokevirtual(equalsRef);
                            // Invert: equals returns 1 for zero (false), 0 for non-zero (true)
                            code.iconst(1);
                            code.ixor();
                            break;
                        default:
                            // VOID or other non-primitive types - no conversion
                            break;
                    }
                }
            }
        }
    }
}
