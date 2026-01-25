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

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
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

    public static void generateDefaultConstructor(ClassWriter classWriter, ClassWriter.ConstantPool cp, String superClassInternalName) {
        // Generate: public <init>() { super(); }
        int superCtorRef = cp.addMethodRef(superClassInternalName, "<init>", "()V");

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

        // Resolve superclass
        String superClassInternalName = resolveSuperClass(clazz);

        ClassWriter classWriter = new ClassWriter(internalClassName, superClassInternalName);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Resolve and add implemented interfaces
        resolveInterfaces(clazz, classWriter);

        // Set ACC_ABSTRACT flag if class is abstract
        if (clazz.isAbstract()) {
            classWriter.setAccessFlags(0x0421); // ACC_PUBLIC | ACC_SUPER | ACC_ABSTRACT
        }

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

            // Collect static fields for <clinit> initialization
            List<FieldInfo> staticFields = new ArrayList<>();

            // Generate field declarations
            for (ISwc4jAstClassMember member : clazz.getBody()) {
                if (member instanceof Swc4jAstClassProp prop) {
                    String fieldName = prop.getKey().toString();
                    FieldInfo fieldInfo = typeInfo != null ? typeInfo.getField(fieldName) : null;

                    if (fieldInfo != null) {
                        int accessFlags = getAccessFlags(prop.getAccessibility());
                        if (fieldInfo.isStatic()) {
                            accessFlags |= 0x0008; // ACC_STATIC
                            // Collect static fields for <clinit> initialization
                            if (fieldInfo.initializer().isPresent()) {
                                staticFields.add(fieldInfo);
                            }
                        }
                        classWriter.addField(accessFlags, fieldInfo.name(), fieldInfo.descriptor());

                        // Collect instance fields for constructor initialization
                        if (!fieldInfo.isStatic() && fieldInfo.initializer().isPresent()) {
                            instanceFields.add(fieldInfo);
                        }
                    }
                }
            }

            // Generate <clinit> for static field initialization if needed
            if (!staticFields.isEmpty()) {
                generateClinitMethod(classWriter, cp, internalClassName, staticFields);
            }

            // Collect all explicit constructors
            List<Swc4jAstConstructor> constructors = new ArrayList<>();
            for (ISwc4jAstClassMember member : clazz.getBody()) {
                if (member instanceof Swc4jAstConstructor ctor) {
                    constructors.add(ctor);
                }
            }

            // Generate constructors
            if (!constructors.isEmpty()) {
                for (Swc4jAstConstructor ctor : constructors) {
                    generateExplicitConstructor(classWriter, cp, internalClassName, superClassInternalName, ctor);
                }
            } else if (!instanceFields.isEmpty()) {
                generateConstructorWithFieldInit(classWriter, cp, internalClassName, superClassInternalName, instanceFields);
            } else {
                generateDefaultConstructor(classWriter, cp, superClassInternalName);
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

    /**
     * Generates the &lt;clinit&gt; method for static field initialization.
     */
    private void generateClinitMethod(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String internalClassName,
            List<FieldInfo> staticFields) throws Swc4jByteCodeCompilerException {
        // Reset compilation context for static initialization
        compiler.getMemory().resetCompilationContext(true); // is static

        CodeBuilder code = new CodeBuilder();

        // Initialize static fields with their initializers
        for (FieldInfo field : staticFields) {
            if (field.initializer().isPresent()) {
                // Generate code for the initializer expression
                compiler.getExpressionGenerator().generate(code, cp, field.initializer().get(), null);
                // Store to static field
                int fieldRef = cp.addFieldRef(internalClassName, field.name(), field.descriptor());
                code.putstatic(fieldRef);
            }
        }

        code.returnVoid(); // return

        classWriter.addMethod(
                0x0008, // ACC_STATIC
                "<clinit>",
                "()V",
                code.toByteArray(),
                10, // max stack
                0   // max locals
        );
    }

    public void generateConstructorWithFieldInit(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String internalClassName,
            String superClassInternalName,
            List<FieldInfo> fieldsToInit) throws Swc4jByteCodeCompilerException {
        // Generate: public <init>() { super(); this.field1 = value1; ... }
        int superCtorRef = cp.addMethodRef(superClassInternalName, "<init>", "()V");

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

    /**
     * Generates an explicit constructor from the AST.
     */
    public void generateExplicitConstructor(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String internalClassName,
            String superClassInternalName,
            Swc4jAstConstructor constructor) throws Swc4jByteCodeCompilerException {
        // Reset compilation context for constructor code generation (not static)
        compiler.getMemory().resetCompilationContext(false);

        // Build parameter descriptor and allocate parameter slots
        StringBuilder paramDescriptors = new StringBuilder();
        for (ISwc4jAstParamOrTsParamProp paramOrProp : constructor.getParams()) {
            if (paramOrProp instanceof Swc4jAstParam param) {
                String paramType = compiler.getTypeResolver().extractParameterType(param.getPat());
                paramDescriptors.append(paramType);
                // Allocate slot for parameter - use VariableAnalyzer-like logic
                String paramName = compiler.getTypeResolver().extractParameterName(param.getPat());
                if (paramName != null) {
                    compiler.getMemory().getCompilationContext()
                            .getLocalVariableTable().allocateVariable(paramName, paramType);
                    compiler.getMemory().getCompilationContext()
                            .getInferredTypes().put(paramName, paramType);
                }
            }
            // TODO: Handle TsParamProp (parameter properties) later
        }

        String descriptor = "(" + paramDescriptors + ")V";

        // Generate constructor body
        CodeBuilder code = new CodeBuilder();

        if (constructor.getBody().isPresent()) {
            Swc4jAstBlockStmt body = constructor.getBody().get();
            List<ISwc4jAstStmt> stmts = body.getStmts();

            // Check if first statement is a super() or this() call
            boolean firstIsSuperOrThisCall = false;
            if (!stmts.isEmpty()) {
                ISwc4jAstStmt firstStmt = stmts.get(0);
                if (firstStmt instanceof Swc4jAstExprStmt exprStmt) {
                    if (exprStmt.getExpr() instanceof Swc4jAstCallExpr callExpr) {
                        if (callExpr.getCallee() instanceof Swc4jAstSuper
                                || callExpr.getCallee() instanceof Swc4jAstThisExpr) {
                            firstIsSuperOrThisCall = true;
                        }
                    }
                }
            }

            // If first statement is not super() or this(), inject an implicit super() call
            if (!firstIsSuperOrThisCall) {
                int superCtorRef = cp.addMethodRef(superClassInternalName, "<init>", "()V");
                code.aload(0).invokespecial(superCtorRef);
            }

            // Analyze variable declarations in the body
            compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

            // Process statements in the constructor body
            for (ISwc4jAstStmt stmt : stmts) {
                compiler.getStatementGenerator().generate(code, cp, stmt, null);
            }
        } else {
            // No body - generate default super() call
            int superCtorRef = cp.addMethodRef(superClassInternalName, "<init>", "()V");
            code.aload(0).invokespecial(superCtorRef);
        }

        // Add return if not already present
        byte[] bytecode = code.toByteArray();
        if (bytecode.length == 0 || bytecode[bytecode.length - 1] != (byte) 0xB1) {
            code.returnVoid();
        }

        int maxLocals = compiler.getMemory().getCompilationContext().getLocalVariableTable().getMaxLocals();

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                "<init>",
                descriptor,
                code.toByteArray(),
                10, // max stack
                maxLocals
        );
    }

    /**
     * Converts TypeScript/ES accessibility to JVM access flags.
     *
     * @param accessibility the accessibility modifier (Public, Protected, Private)
     * @return JVM access flags (ACC_PUBLIC=0x0001, ACC_PROTECTED=0x0004, ACC_PRIVATE=0x0002)
     */
    private int getAccessFlags(java.util.Optional<Swc4jAstAccessibility> accessibility) {
        if (accessibility.isEmpty()) {
            return 0x0001; // Default to ACC_PUBLIC
        }
        return switch (accessibility.get()) {
            case Public -> 0x0001;    // ACC_PUBLIC
            case Protected -> 0x0004; // ACC_PROTECTED
            case Private -> 0x0002;   // ACC_PRIVATE
        };
    }

    /**
     * Resolves implemented interfaces from the class AST and adds them to the ClassWriter.
     *
     * @param clazz       the class AST
     * @param classWriter the class writer to add interfaces to
     */
    private void resolveInterfaces(Swc4jAstClass clazz, ClassWriter classWriter) {
        for (Swc4jAstTsExprWithTypeArgs exprWithTypeArgs : clazz.getImplements()) {
            ISwc4jAstExpr expr = exprWithTypeArgs.getExpr();
            if (expr instanceof Swc4jAstIdent ident) {
                String interfaceName = ident.getSym();

                // Try to resolve from type alias registry
                String resolvedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(interfaceName);
                if (resolvedName != null) {
                    classWriter.addInterface(resolvedName.replace('.', '/'));
                    continue;
                }

                // Try to resolve from Java type registry
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(interfaceName);
                if (typeInfo != null) {
                    classWriter.addInterface(typeInfo.getInternalName());
                    continue;
                }

                // Default to simple name (might be in same package)
                classWriter.addInterface(interfaceName);
            }
        }
    }

    /**
     * Resolves the superclass internal name from the class AST.
     *
     * @param clazz the class AST
     * @return the superclass internal name, or "java/lang/Object" if no superclass
     */
    private String resolveSuperClass(Swc4jAstClass clazz) {
        if (clazz.getSuperClass().isEmpty()) {
            return "java/lang/Object";
        }

        ISwc4jAstExpr superClassExpr = clazz.getSuperClass().get();
        if (superClassExpr instanceof Swc4jAstIdent ident) {
            String superClassName = ident.getSym();

            // Try to resolve from type alias registry
            String resolvedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(superClassName);
            if (resolvedName != null) {
                return resolvedName.replace('.', '/');
            }

            // Try to resolve from Java type registry
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(superClassName);
            if (typeInfo != null) {
                return typeInfo.getInternalName();
            }

            // Default to simple name (might be in same package)
            return superClassName;
        }

        return "java/lang/Object";
    }
}
