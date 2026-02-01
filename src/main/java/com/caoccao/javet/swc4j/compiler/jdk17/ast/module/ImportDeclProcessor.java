/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.module;

import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportNamedSpecifier;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Processes import declarations and registers Java classes in the scoped registry.
 */
public final class ImportDeclProcessor {
    private final ByteCodeCompiler compiler;

    public ImportDeclProcessor(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Generates a constructor descriptor from a Java Constructor.
     * Example: (int, double) -> void becomes "(ID)V"
     */
    private String getConstructorDescriptor(Constructor<?> constructor) {
        StringBuilder descriptor = new StringBuilder("(");

        for (Class<?> paramType : constructor.getParameterTypes()) {
            descriptor.append(getTypeDescriptor(paramType));
        }

        descriptor.append(")V");
        return descriptor.toString();
    }

    /**
     * Converts a Java class name to JVM internal name format.
     * Example: "java.lang.Math" -> "java/lang/Math"
     */
    private String getInternalName(String className) {
        return className.replace('.', '/');
    }

    /**
     * Generates a method descriptor from a Java Method.
     * Example: (int, double) -> String becomes "(ID)Ljava/lang/String;"
     */
    private String getMethodDescriptor(Method method) {
        StringBuilder descriptor = new StringBuilder("(");

        // Add parameter types
        for (Class<?> paramType : method.getParameterTypes()) {
            descriptor.append(getTypeDescriptor(paramType));
        }

        descriptor.append(")");

        // Add return type
        descriptor.append(getTypeDescriptor(method.getReturnType()));

        return descriptor.toString();
    }

    /**
     * Converts a Java type to JVM descriptor format.
     * Examples:
     * - int -> I
     * - double -> D
     * - String -> Ljava/lang/String;
     * - void -> V
     */
    private String getTypeDescriptor(Class<?> type) {
        if (type == void.class) return "V";
        if (type == boolean.class) return "Z";
        if (type == byte.class) return "B";
        if (type == char.class) return "C";
        if (type == short.class) return "S";
        if (type == int.class) return "I";
        if (type == long.class) return "J";
        if (type == float.class) return "F";
        if (type == double.class) return "D";
        if (type.isArray()) {
            return "[" + getTypeDescriptor(type.getComponentType());
        }
        return "L" + getInternalName(type.getName()) + ";";
    }

    /**
     * Processes a single import declaration.
     * Supports: import { ClassName } from 'package.name'
     */
    private void processImportDecl(Swc4jAstImportDecl importDecl) {
        String source = importDecl.getSrc().getValue();

        String packageName = source;

        for (var specifier : importDecl.getSpecifiers()) {
            if (specifier instanceof Swc4jAstImportNamedSpecifier namedImport) {
                String importedName = namedImport.getLocal().getSym();
                String fullyQualifiedName = packageName + "." + importedName;

                Class<?> javaClass = null;
                try {
                    // Load the Java class using reflection
                    javaClass = Class.forName(fullyQualifiedName);
                } catch (ClassNotFoundException e) {
                    // Try as an inner class (replace last dot with $)
                    int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        String innerClassName = fullyQualifiedName.substring(0, lastDotIndex) + "$" + fullyQualifiedName.substring(lastDotIndex + 1);
                        try {
                            javaClass = Class.forName(innerClassName);
                            fullyQualifiedName = innerClassName;
                        } catch (ClassNotFoundException ignored) {
                            // Not an inner class either, will be handled below
                        }
                    }
                }

                if (javaClass != null) {
                    String internalName = getInternalName(fullyQualifiedName);

                    JavaTypeInfo typeInfo = new JavaTypeInfo(importedName, packageName, internalName);

                    // Scan all public methods and register them
                    for (Method method : javaClass.getMethods()) {
                        if (Modifier.isPublic(method.getModifiers())) {
                            String methodName = method.getName();
                            String descriptor = getMethodDescriptor(method);
                            String returnType = getTypeDescriptor(method.getReturnType());
                            boolean isStatic = Modifier.isStatic(method.getModifiers());
                            boolean isVarArgs = method.isVarArgs();

                            MethodInfo methodInfo = new MethodInfo(methodName, descriptor, returnType, isStatic, isVarArgs);
                            typeInfo.addMethod(methodName, methodInfo);
                        }
                    }

                    // Scan public constructors and register them as <init>
                    for (Constructor<?> constructor : javaClass.getConstructors()) {
                        if (Modifier.isPublic(constructor.getModifiers())) {
                            String descriptor = getConstructorDescriptor(constructor);
                            boolean isVarArgs = constructor.isVarArgs();
                            MethodInfo methodInfo = new MethodInfo("<init>", descriptor, "V", false, isVarArgs);
                            typeInfo.addMethod("<init>", methodInfo);
                        }
                    }

                    // Register in the scoped registry
                    compiler.getMemory().getScopedJavaTypeRegistry().registerClass(importedName, typeInfo);

                    // Also register as a type alias so TypeResolver can resolve the type name
                    compiler.getMemory().getScopedTypeAliasRegistry().registerAlias(importedName, fullyQualifiedName);
                }
                // If javaClass is still null, the import is not a Java class - silently skip
            }
        }
    }

    /**
     * Processes import declarations and registers Java classes.
     *
     * @param moduleItems the module items (statements) to scan for imports
     */
    public void processImports(List<?> moduleItems) {
        for (Object item : moduleItems) {
            if (item instanceof Swc4jAstImportDecl importDecl) {
                processImportDecl(importDecl);
            }
        }
    }
}
