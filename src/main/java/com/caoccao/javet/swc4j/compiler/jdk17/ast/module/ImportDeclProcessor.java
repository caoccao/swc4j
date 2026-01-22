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
import com.caoccao.javet.swc4j.compiler.memory.JavaClassInfo;
import com.caoccao.javet.swc4j.compiler.memory.MethodInfo;

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

        // Check if this is a Java package import
        if (!source.startsWith("java.") && !source.startsWith("javax.")) {
            return; // Not a Java import, skip
        }

        String packageName = source;

        for (var specifier : importDecl.getSpecifiers()) {
            if (specifier instanceof Swc4jAstImportNamedSpecifier namedImport) {
                String importedName = namedImport.getLocal().getSym();
                String fullyQualifiedName = packageName + "." + importedName;

                try {
                    // Load the Java class using reflection
                    Class<?> javaClass = Class.forName(fullyQualifiedName);
                    String internalName = getInternalName(fullyQualifiedName);

                    JavaClassInfo classInfo = new JavaClassInfo(importedName, packageName, internalName);

                    // Scan all public methods and register them
                    for (Method method : javaClass.getMethods()) {
                        if (Modifier.isPublic(method.getModifiers())) {
                            String methodName = method.getName();
                            String descriptor = getMethodDescriptor(method);
                            String returnType = getTypeDescriptor(method.getReturnType());
                            boolean isStatic = Modifier.isStatic(method.getModifiers());
                            boolean isVarArgs = method.isVarArgs();

                            MethodInfo methodInfo = new MethodInfo(methodName, descriptor, returnType, isStatic, isVarArgs);
                            classInfo.addMethod(methodName, methodInfo);
                        }
                    }

                    // Register in the scoped registry
                    compiler.getMemory().getScopedJavaClassRegistry().registerClass(importedName, classInfo);

                } catch (ClassNotFoundException e) {
                    // Class not found - could be a user error or unsupported class
                    // For now, silently skip (could add logging later)
                }
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
