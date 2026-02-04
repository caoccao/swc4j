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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeParameterScope;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.ScopedTemplateCacheRegistry;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item to be initialized in the static initializer (&lt;clinit&gt;).
 * Can be either a static field initializer or a static block.
 */
sealed interface StaticInitItem permits StaticInitItem.FieldInit, StaticInitItem.BlockInit {
    record BlockInit(Swc4jAstStaticBlock staticBlock) implements StaticInitItem {
    }

    record FieldInit(FieldInfo fieldInfo) implements StaticInitItem {
    }
}

public final class ClassDeclGenerator extends BaseAstProcessor<Swc4jAstClassDecl> {
    public ClassDeclGenerator(ByteCodeCompiler compiler) {
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
    public void generate(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstClassDecl classDecl, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String currentPackage = compiler.getMemory().getScopedPackage().getCurrentPackage();
        String className = classDecl.getIdent().getSym();
        String fullClassName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = generateBytecode(internalClassName, classDecl.getClazz());
            compiler.getMemory().getByteCodeMap().put(fullClassName, bytecode);
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), classDecl, "Failed to generate bytecode for class: " + fullClassName, e);
        }
    }

    private byte[] generateBytecode(
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

        // Enter template cache scope for this class
        compiler.getMemory().getScopedTemplateCacheRegistry().enterScope();

        // Push type parameters scope for generics support (type erasure)
        TypeParameterScope typeParamScope = clazz.getTypeParams()
                .map(TypeParameterScope::fromDecl)
                .orElse(null);
        if (typeParamScope != null) {
            compiler.getMemory().getCompilationContext().pushTypeParameterScope(typeParamScope);
        }

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

            // Collect static initialization items (fields and static blocks) in declaration order
            List<StaticInitItem> staticInitItems = new ArrayList<>();

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
                                staticInitItems.add(new StaticInitItem.FieldInit(fieldInfo));
                            }
                        }
                        classWriter.addField(accessFlags, fieldInfo.name(), fieldInfo.descriptor());

                        // Collect instance fields for constructor initialization
                        if (!fieldInfo.isStatic() && fieldInfo.initializer().isPresent()) {
                            instanceFields.add(fieldInfo);
                        }
                    }
                } else if (member instanceof Swc4jAstPrivateProp privateProp) {
                    // ES2022 private fields (#field)
                    String fieldName = privateProp.getKey().getName(); // Without the # prefix
                    FieldInfo fieldInfo = typeInfo != null ? typeInfo.getField(fieldName) : null;

                    if (fieldInfo != null) {
                        // Private fields are always ACC_PRIVATE
                        int accessFlags = 0x0002; // ACC_PRIVATE
                        if (fieldInfo.isStatic()) {
                            accessFlags |= 0x0008; // ACC_STATIC
                            if (fieldInfo.initializer().isPresent()) {
                                staticInitItems.add(new StaticInitItem.FieldInit(fieldInfo));
                            }
                        }
                        classWriter.addField(accessFlags, fieldInfo.name(), fieldInfo.descriptor());

                        // Collect instance fields for constructor initialization
                        if (!fieldInfo.isStatic() && fieldInfo.initializer().isPresent()) {
                            instanceFields.add(fieldInfo);
                        }
                    }
                } else if (member instanceof Swc4jAstStaticBlock staticBlock) {
                    // ES2022 static block - add to static initialization sequence
                    staticInitItems.add(new StaticInitItem.BlockInit(staticBlock));
                }
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

            // Generate methods (this may create template cache entries)
            for (ISwc4jAstClassMember member : clazz.getBody()) {
                if (member instanceof Swc4jAstClassMethod method) {
                    compiler.getClassMethodGenerator().generate(classWriter, cp, method);
                } else if (member instanceof Swc4jAstPrivateMethod privateMethod) {
                    // ES2022 private methods (#method)
                    compiler.getClassMethodGenerator().generate(classWriter, cp, privateMethod);
                }
            }

            // After method generation, get template caches from current scope and add static fields
            List<ScopedTemplateCacheRegistry.TemplateCacheEntry> templateCaches =
                    compiler.getMemory().getScopedTemplateCacheRegistry().getCurrentCaches();
            for (ScopedTemplateCacheRegistry.TemplateCacheEntry cache : templateCaches) {
                // Add private static final String[] field for cooked strings
                classWriter.addField(
                        0x001A, // ACC_PRIVATE | ACC_STATIC | ACC_FINAL
                        cache.fieldName(),
                        "[Ljava/lang/String;"
                );
                // Add private static final TemplateStringsArray field for raw string access
                classWriter.addField(
                        0x001A, // ACC_PRIVATE | ACC_STATIC | ACC_FINAL
                        cache.fieldName() + "$raw",
                        "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;"
                );
            }

            // Generate <clinit> for static field initialization, static blocks, and template caches
            if (!staticInitItems.isEmpty() || !templateCaches.isEmpty()) {
                generateClinitMethod(classWriter, cp, internalClassName, staticInitItems, templateCaches);
            }

            return classWriter.toByteArray();
        } finally {
            // Exit template cache scope (automatically cleans up)
            compiler.getMemory().getScopedTemplateCacheRegistry().exitScope();
            // Pop the type parameter scope when done
            if (typeParamScope != null) {
                compiler.getMemory().getCompilationContext().popTypeParameterScope();
            }
            // Pop the class from the stack when done
            compiler.getMemory().getCompilationContext().popClass();
        }
    }

    /**
     * Generates the &lt;clinit&gt; method for static field initialization, static blocks, and template caches.
     * Items are processed in declaration order to ensure correct initialization sequence.
     * Template caches are initialized at the end of &lt;clinit&gt;.
     */
    private void generateClinitMethod(
            ClassWriter classWriter,
            ClassWriter.ConstantPool cp,
            String internalClassName,
            List<StaticInitItem> staticInitItems,
            List<ScopedTemplateCacheRegistry.TemplateCacheEntry> templateCaches) throws Swc4jByteCodeCompilerException {
        // Reset compilation context for static initialization
        compiler.getMemory().resetCompilationContext(true); // is static

        CodeBuilder code = new CodeBuilder();

        // Process static initialization items in declaration order
        for (StaticInitItem item : staticInitItems) {
            if (item instanceof StaticInitItem.FieldInit fieldInit) {
                FieldInfo field = fieldInit.fieldInfo();
                if (field.initializer().isPresent()) {
                    // Generate code for the initializer expression
                    compiler.getExpressionGenerator().generate(code, cp, field.initializer().get(), null);
                    // Store to static field
                    int fieldRef = cp.addFieldRef(internalClassName, field.name(), field.descriptor());
                    code.putstatic(fieldRef);
                }
            } else if (item instanceof StaticInitItem.BlockInit blockInit) {
                // ES2022 static block - generate code for each statement in the block
                Swc4jAstStaticBlock staticBlock = blockInit.staticBlock();
                Swc4jAstBlockStmt body = staticBlock.getBody();

                // Analyze variable declarations in the static block
                compiler.getVariableAnalyzer().analyzeVariableDeclarations(body);

                // Generate code for each statement in the static block
                for (ISwc4jAstStmt stmt : body.getStmts()) {
                    compiler.getStatementGenerator().generate(code, cp, stmt, null);
                }
            }
        }

        // Initialize template cache fields
        int stringClassRef = cp.addClass("java/lang/String");
        String templateStringsArrayClass = "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray";
        int templateStringsArrayClassRef = cp.addClass(templateStringsArrayClass);
        int templateStringsArrayCtorRef = cp.addMethodRef(
                templateStringsArrayClass,
                "<init>",
                "([Ljava/lang/String;[Ljava/lang/String;)V"
        );

        for (ScopedTemplateCacheRegistry.TemplateCacheEntry cache : templateCaches) {
            List<String> cooked = cache.cooked();
            List<String> raw = cache.raw();
            int size = cooked.size();

            // Create cooked String[] array: new String[size]
            code.iconst(size);
            code.anewarray(stringClassRef);

            // Populate cooked array: strings[i] = cooked[i]
            for (int i = 0; i < size; i++) {
                code.dup();
                code.iconst(i);
                int stringRef = cp.addString(cooked.get(i));
                code.ldc(stringRef);
                code.aastore();
            }

            // Store cooked array to static field: $tpl$N = array
            int cookedFieldRef = cp.addFieldRef(internalClassName, cache.fieldName(), "[Ljava/lang/String;");
            code.putstatic(cookedFieldRef);

            // Create TemplateStringsArray with both cooked and raw arrays
            // new TemplateStringsArray(cooked, raw)
            code.newInstance(templateStringsArrayClassRef);
            code.dup();

            // Load cooked array from field
            code.getstatic(cookedFieldRef);

            // Create raw String[] array: new String[size]
            code.iconst(size);
            code.anewarray(stringClassRef);

            // Populate raw array: rawStrings[i] = raw[i]
            for (int i = 0; i < size; i++) {
                code.dup();
                code.iconst(i);
                int stringRef = cp.addString(raw.get(i));
                code.ldc(stringRef);
                code.aastore();
            }

            // Invoke constructor: TemplateStringsArray(cooked, raw)
            code.invokespecial(templateStringsArrayCtorRef);

            // Store to static field: $tpl$N$raw = templateStringsArray
            int rawFieldRef = cp.addFieldRef(
                    internalClassName,
                    cache.fieldName() + "$raw",
                    "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;"
            );
            code.putstatic(rawFieldRef);
        }

        code.returnVoid(); // return

        int maxLocals = compiler.getMemory().getCompilationContext().getLocalVariableTable().getMaxLocals();

        // Generate stack map table for branches (loops, conditionals) in static blocks
        var stackMapTable = code.generateStackMapTable(
                Math.max(maxLocals, 1),
                true, // isStatic
                internalClassName,
                "()V",
                cp
        );
        var exceptionTable = code.getExceptionTable().isEmpty() ? null : code.getExceptionTable();

        classWriter.addMethod(
                0x0008, // ACC_STATIC
                "<clinit>",
                "()V",
                code.toByteArray(),
                10, // max stack
                Math.max(maxLocals, 1), // max locals (at least 1 for static blocks with local vars)
                null, // line numbers
                null, // local variable table
                stackMapTable,
                exceptionTable
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

        // Extract fully qualified name from identifier or member expression
        String qualifiedName = AstUtils.extractQualifiedName(superClassExpr);
        if (qualifiedName == null) {
            return "java/lang/Object";
        }

        // Get simple name (last part of qualified name)
        int lastDot = qualifiedName.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;

        // For simple names, try to resolve from type alias registry
        if (lastDot < 0) {
            String resolvedName = compiler.getMemory().getScopedTypeAliasRegistry().resolve(simpleName);
            if (resolvedName != null) {
                return resolvedName.replace('.', '/');
            }

            // Try to resolve from Java type registry
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            if (typeInfo != null) {
                return typeInfo.getInternalName();
            }
        }

        // For fully qualified names or unresolved simple names, convert to internal name
        return qualifiedName.replace('.', '/');
    }

}
