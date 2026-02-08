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


package com.caoccao.javet.swc4j.compiler.memory;

import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Manages functional interface SAM (Single Abstract Method) information in a scoped manner.
 * Each scope represents a file being compiled, preventing data leakage between files.
 * Uses reflection to dynamically resolve SAM method information and caches results.
 */
public final class ScopedFunctionalInterfaceRegistry {
    private final Stack<Map<String, SamMethodInfo>> scopeStack;

    /**
     * Instantiates a new Scoped functional interface registry.
     */
    public ScopedFunctionalInterfaceRegistry() {
        scopeStack = new Stack<>();
        // Push global scope
        scopeStack.push(new HashMap<>());
    }

    /**
     * Converts a Java Class to its JVM type descriptor.
     */
    private String classToDescriptor(Class<?> clazz) {
        if (clazz == void.class) return ConstantJavaType.ABBR_VOID;
        if (clazz == boolean.class) return ConstantJavaType.ABBR_BOOLEAN;
        if (clazz == byte.class) return ConstantJavaType.ABBR_BYTE;
        if (clazz == char.class) return ConstantJavaType.ABBR_CHARACTER;
        if (clazz == short.class) return ConstantJavaType.ABBR_SHORT;
        if (clazz == int.class) return ConstantJavaType.ABBR_INTEGER;
        if (clazz == long.class) return ConstantJavaType.ABBR_LONG;
        if (clazz == float.class) return ConstantJavaType.ABBR_FLOAT;
        if (clazz == double.class) return ConstantJavaType.ABBR_DOUBLE;
        if (clazz.isArray()) {
            return ConstantJavaType.ARRAY_PREFIX + classToDescriptor(clazz.getComponentType());
        }
        return "L" + clazz.getName().replace('.', '/') + ";";
    }

    /**
     * Clears all scopes except the global scope.
     */
    public void clear() {
        while (scopeStack.size() > 1) {
            scopeStack.pop();
        }
        scopeStack.peek().clear();
    }

    /**
     * Enters a new scope (typically for a new file).
     */
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    /**
     * Exits the current scope (typically after finishing a file).
     * The global scope is never removed.
     */
    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    /**
     * Finds the single abstract method (SAM) of a functional interface.
     * This considers inherited methods and excludes Object methods.
     */
    private Method findSamMethod(Class<?> interfaceClass) {
        Method samMethod = null;

        for (Method method : interfaceClass.getMethods()) {
            // Skip static methods
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // Skip default methods
            if (method.isDefault()) {
                continue;
            }

            // Skip methods from Object class (equals, hashCode, toString)
            if (isObjectMethod(method)) {
                continue;
            }

            // Must be abstract
            if (!Modifier.isAbstract(method.getModifiers())) {
                continue;
            }

            // Found an abstract method
            if (samMethod != null) {
                // More than one abstract method - not a functional interface
                return null;
            }
            samMethod = method;
        }

        return samMethod;
    }

    /**
     * Gets parameter types for a functional interface from its descriptor.
     *
     * @param interfaceDescriptor the interface type descriptor (e.g., "Ljava/util/function/IntUnaryOperator;")
     * @return list of parameter type descriptors, or null if not a functional interface
     */
    public List<String> getParamTypes(String interfaceDescriptor) {
        if (interfaceDescriptor == null || !interfaceDescriptor.startsWith("L") || !interfaceDescriptor.endsWith(";")) {
            return null;
        }
        String interfaceName = TypeConversionUtils.descriptorToInternalName(interfaceDescriptor);
        SamMethodInfo info = getSamMethodInfo(interfaceName);
        return info != null ? info.paramTypes() : null;
    }

    /**
     * Gets the return type for a functional interface from its descriptor.
     *
     * @param interfaceDescriptor the interface type descriptor (e.g., "Ljava/util/function/IntUnaryOperator;")
     * @return return type descriptor, or null if not a functional interface
     */
    public String getReturnType(String interfaceDescriptor) {
        if (interfaceDescriptor == null || !interfaceDescriptor.startsWith("L") || !interfaceDescriptor.endsWith(";")) {
            return null;
        }
        String interfaceName = TypeConversionUtils.descriptorToInternalName(interfaceDescriptor);
        SamMethodInfo info = getSamMethodInfo(interfaceName);
        return info != null ? info.returnType() : null;
    }

    /**
     * Gets the SAM method information for a functional interface.
     * Results are cached in the current scope.
     *
     * @param interfaceInternalName internal name of the interface (e.g., "java/util/function/IntUnaryOperator")
     * @return SAM method info, or null if not a functional interface
     */
    public SamMethodInfo getSamMethodInfo(String interfaceInternalName) {
        if (interfaceInternalName == null) {
            return null;
        }

        // Search from innermost to outermost scope for cached result
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, SamMethodInfo> scope = scopeStack.get(i);
            if (scope.containsKey(interfaceInternalName)) {
                return scope.get(interfaceInternalName); // May be null if we've determined it's not a functional interface
            }
        }

        // Not cached, resolve using reflection
        SamMethodInfo info = resolveSamMethodInfo(interfaceInternalName);

        // Cache in current scope (even if null, to avoid repeated reflection)
        scopeStack.peek().put(interfaceInternalName, info);

        return info;
    }

    /**
     * Gets the current scope depth.
     *
     * @return the number of scopes (1 = global only)
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }

    /**
     * Checks if the given internal name is a functional interface.
     *
     * @param interfaceInternalName internal name of the interface
     * @return true if it's a functional interface
     */
    public boolean isFunctionalInterface(String interfaceInternalName) {
        return getSamMethodInfo(interfaceInternalName) != null;
    }

    /**
     * Checks if a method is one of the public methods from Object class
     * (which don't count against functional interface contract).
     */
    private boolean isObjectMethod(Method method) {
        String name = method.getName();
        Class<?>[] params = method.getParameterTypes();

        // equals(Object)
        if (ConstantJavaMethod.METHOD_EQUALS.equals(name) && params.length == 1 && params[0] == Object.class) {
            return true;
        }
        // hashCode()
        if ("hashCode".equals(name) && params.length == 0) {
            return true;
        }
        // toString()
        return ConstantJavaMethod.METHOD_TO_STRING.equals(name) && params.length == 0;
    }

    /**
     * Manually register SAM method information for a generated functional interface.
     * This is used for custom interfaces generated from function type syntax.
     *
     * @param interfaceInternalName internal name (e.g., "com/A/$Fn$1")
     * @param methodName            SAM method name (e.g., "call")
     * @param paramTypes            list of parameter type descriptors
     * @param returnType            return type descriptor
     */
    public void register(String interfaceInternalName, String methodName,
                         List<String> paramTypes, String returnType) {
        StringBuilder descriptor = new StringBuilder("(");
        for (String paramType : paramTypes) {
            descriptor.append(paramType);
        }
        descriptor.append(")").append(returnType);

        SamMethodInfo info = new SamMethodInfo(methodName, paramTypes, returnType, descriptor.toString());
        scopeStack.peek().put(interfaceInternalName, info);
    }

    private SamMethodInfo resolveSamMethodInfo(String interfaceInternalName) {
        try {
            String className = interfaceInternalName.replace('/', '.');
            Class<?> clazz = Class.forName(className);

            if (!clazz.isInterface()) {
                return null;
            }

            // Find the single abstract method
            Method samMethod = findSamMethod(clazz);
            if (samMethod == null) {
                return null;
            }

            String methodName = samMethod.getName();
            List<String> paramTypes = new ArrayList<>();
            StringBuilder descriptor = new StringBuilder("(");

            for (Class<?> paramType : samMethod.getParameterTypes()) {
                String paramDesc = classToDescriptor(paramType);
                paramTypes.add(paramDesc);
                descriptor.append(paramDesc);
            }

            descriptor.append(")");
            String returnType = classToDescriptor(samMethod.getReturnType());
            descriptor.append(returnType);

            return new SamMethodInfo(methodName, paramTypes, returnType, descriptor.toString());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Record containing SAM method information for a functional interface.
     *
     * @param methodName       the SAM method name (e.g., "applyAsInt")
     * @param paramTypes       list of parameter type descriptors
     * @param returnType       return type descriptor
     * @param methodDescriptor full method descriptor
     */
    public record SamMethodInfo(String methodName, List<String> paramTypes, String returnType,
                                String methodDescriptor) {
    }
}
