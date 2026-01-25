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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ClassGenerator extends BaseAstProcessor {
    public ClassGenerator(ByteCodeCompiler compiler) {
        super(compiler);
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

    @Override
    public void generate(CodeBuilder code, ClassWriter.ConstantPool cp, ISwc4jAst ast, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        throw new Swc4jByteCodeCompilerException(ast, "ClassGenerator does not support generic generate method.");
    }

    public byte[] generateBytecode(
            String internalClassName,
            Swc4jAstClass clazz) throws IOException, Swc4jByteCodeCompilerException {
        String qualifiedName = internalClassName.replace('/', '.');
        ClassWriter classWriter = new ClassWriter(internalClassName);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Push the current class onto the stack for 'this' resolution (supports nested classes)
        compiler.getMemory().getCompilationContext().pushClass(internalClassName);

        try {
            // Collect fields from the class body and from the registry
            List<FieldInfo> instanceFields = new ArrayList<>();

            // Get class info from registry to access collected field metadata
            // Try qualified name first, then fall back to simple name
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                int lastDot = qualifiedName.lastIndexOf('.');
                String simpleName = lastDot >= 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            // Generate field declarations
            for (ISwc4jAstClassMember member : clazz.getBody()) {
                if (member instanceof Swc4jAstClassProp prop) {
                    String fieldName = prop.getKey().toString();
                    FieldInfo fieldInfo = typeInfo != null ? typeInfo.getField(fieldName) : null;

                    if (fieldInfo != null) {
                        int accessFlags = 0x0001; // ACC_PUBLIC
                        if (fieldInfo.isStatic()) {
                            accessFlags |= 0x0008; // ACC_STATIC
                        }
                        classWriter.addField(accessFlags, fieldInfo.name(), fieldInfo.descriptor());

                        // Collect instance fields for constructor initialization
                        if (!fieldInfo.isStatic() && fieldInfo.initializer().isPresent()) {
                            instanceFields.add(fieldInfo);
                        }
                    }
                }
            }

            // Generate constructor with field initialization if there are fields to initialize
            if (!instanceFields.isEmpty()) {
                generateConstructorWithFieldInit(classWriter, cp, internalClassName, instanceFields);
            } else {
                generateDefaultConstructor(classWriter, cp);
            }

            // Generate methods
            for (ISwc4jAstClassMember member : clazz.getBody()) {
                if (member instanceof Swc4jAstClassMethod method) {
                    compiler.getMethodGenerator().generate(classWriter, cp, method);
                }
            }

            return classWriter.toByteArray();
        } finally {
            // Pop the class from the stack when done
            compiler.getMemory().getCompilationContext().popClass();
        }
    }

    public void generateConstructorWithFieldInit(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String internalClassName,
            List<FieldInfo> fieldsToInit) throws Swc4jByteCodeCompilerException {
        // Generate: public <init>() { super(); this.field1 = value1; ... }
        int superCtorRef = cp.addMethodRef("java/lang/Object", "<init>", "()V");

        // Reset compilation context for constructor code generation
        compiler.getMemory().resetCompilationContext(false); // not static

        CodeBuilder code = new CodeBuilder();
        code.aload(0)                    // load this
                .invokespecial(superCtorRef); // call super()

        // Initialize fields with their initializers
        for (FieldInfo field : fieldsToInit) {
            if (field.initializer().isPresent()) {
                code.aload(0); // load this for putfield
                // Generate code for the initializer expression
                compiler.getExpressionGenerator().generate(code, cp, field.initializer().get(), null);
                // Store to field
                int fieldRef = cp.addFieldRef(internalClassName, field.name(), field.descriptor());
                code.putfield(fieldRef);
            }
        }

        code.returnVoid(); // return

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                "<init>",
                "()V",
                code.toByteArray(),
                10, // max stack (increased for field initialization)
                1   // max locals (this)
        );
    }
}
