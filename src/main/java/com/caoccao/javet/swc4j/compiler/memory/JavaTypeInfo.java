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

import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores metadata about an imported Java type (class, interface, or enum).
 * <p>
 * This class handles method overload resolution using a scoring-based system:
 * - All candidate methods are evaluated and scored (0.0 to 1.0)
 * - Exact type matches score 1.0
 * - Widening conversions score 0.95-0.99 (closer conversions score higher)
 * - Boxing/unboxing scores 0.7
 * - Varargs matches score slightly lower (0.95× actual score)
 * - The method with the highest average score wins
 * <p>
 * This approach properly handles:
 * - Method overloading with different parameter types
 * - Varargs methods (variable number of arguments)
 * - Primitive type widening following Java rules
 * - Boxing and unboxing conversions
 * <p>
 * For enums, this class stores the enum member ordinals in the enumValues map.
 */
public final class JavaTypeInfo {
    private final String alias;
    private final Map<String, Integer> enumValues;
    private final Map<String, FieldInfo> fields;
    private final String internalName;
    private final Map<String, List<MethodInfo>> methods;
    private final String packageName;
    private final List<JavaTypeInfo> parentTypeInfos;
    private final JavaType type;

    /**
     * Constructs a JavaTypeInfo for a class type.
     *
     * @param alias        the import alias
     * @param packageName  the package name
     * @param internalName the internal JVM name
     */
    public JavaTypeInfo(String alias, String packageName, String internalName) {
        this(alias, packageName, internalName, JavaType.CLASS);
    }

    /**
     * Constructs a JavaTypeInfo with a specific type.
     *
     * @param alias        the import alias
     * @param packageName  the package name
     * @param internalName the internal JVM name
     * @param type         the Java type (class, interface, or enum)
     */
    public JavaTypeInfo(String alias, String packageName, String internalName, JavaType type) {
        this.alias = alias;
        this.packageName = packageName;
        this.internalName = internalName;
        this.type = type;
        this.methods = new HashMap<>();
        this.fields = new HashMap<>();
        this.parentTypeInfos = new ArrayList<>();
        this.enumValues = new HashMap<>();
    }

    /**
     * Adds a field to this type.
     *
     * @param fieldName the field name
     * @param fieldInfo the field info
     */
    public void addField(String fieldName, FieldInfo fieldInfo) {
        fields.put(fieldName, fieldInfo);
    }

    /**
     * Adds a method to this type.
     *
     * @param methodName the method name
     * @param methodInfo the method info
     */
    public void addMethod(String methodName, MethodInfo methodInfo) {
        methods.computeIfAbsent(methodName, k -> new ArrayList<>()).add(methodInfo);
    }

    /**
     * Adds a parent type info (from extends or implements).
     *
     * @param parentTypeInfo the parent type info
     */
    public void addParentTypeInfo(JavaTypeInfo parentTypeInfo) {
        parentTypeInfos.add(parentTypeInfo);
    }

    /**
     * Gets the import alias for this type.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the enum member ordinal value.
     *
     * @param memberName the enum member name
     * @return the ordinal value, or null if not found
     */
    public Integer getEnumMemberOrdinal(String memberName) {
        return enumValues.get(memberName);
    }

    /**
     * Gets a specific field by name.
     *
     * @param fieldName the field name
     * @return the field info, or null if not found
     */
    public FieldInfo getField(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Gets all fields in this type.
     *
     * @return the map of field names to field infos
     */
    public Map<String, FieldInfo> getFields() {
        return fields;
    }

    /**
     * Gets the internal JVM name of this type.
     *
     * @return the internal name
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Gets the best matching method for the given method name and argument types.
     * Uses a scoring system to evaluate all candidates and returns the one with the highest score.
     *
     * @param methodName the method name
     * @param argTypes   the argument types (JVM descriptors like "I", "D", "Ljava/lang/String;")
     * @return the best matching method, or null if not found
     */
    public MethodInfo getMethod(String methodName, List<String> argTypes) {
        List<MethodInfo> overloads = methods.get(methodName);
        if (overloads == null || overloads.isEmpty()) {
            return null;
        }

        MethodInfo bestMethod = null;
        double bestScore = -1.0;

        // Evaluate all candidates and pick the one with the highest score
        for (MethodInfo method : overloads) {
            double score = scoreMethod(method, argTypes);
            if (score > bestScore) {
                bestScore = score;
                bestMethod = method;
            }
        }

        // Only return if the score is positive (some match was found)
        return bestScore > 0.0 ? bestMethod : null;
    }

    /**
     * Gets the first method with the given name.
     * This is a fallback when exact type information is not available.
     * Prefer using {@code getMethod(String, List<String>)} for proper overload resolution.
     *
     * @param methodName the method name
     * @return the first method, or null if not found
     */
    public MethodInfo getMethod(String methodName) {
        List<MethodInfo> overloads = methods.get(methodName);
        return overloads != null && !overloads.isEmpty() ? overloads.get(0) : null;
    }

    /**
     * Gets all method overloads for the given method name.
     *
     * @param methodName the method name
     * @return the list of method overloads
     */
    public List<MethodInfo> getMethodOverloads(String methodName) {
        return methods.getOrDefault(methodName, new ArrayList<>());
    }

    /**
     * Gets all methods in this type.
     *
     * @return the map of method names to method lists
     */
    public Map<String, List<MethodInfo>> getMethods() {
        return methods;
    }

    /**
     * Gets the package name of this type.
     *
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the list of parent type infos (from extends and implements).
     *
     * @return the list of parent type infos
     */
    public List<JavaTypeInfo> getParentTypeInfos() {
        return parentTypeInfos;
    }

    /**
     * Gets the Java type (Class, Interface, or Enum).
     *
     * @return the Java type
     */
    public JavaType getType() {
        return type;
    }

    /**
     * Checks if this type is assignable to the given type.
     * This checks if any of the parent types (including transitive parents) match the given type.
     *
     * @param typeDescriptor the type descriptor to check (e.g., "Ljava/util/List;")
     * @return true if this type is assignable to the given type
     */
    public boolean isAssignableTo(String typeDescriptor) {
        try {
            return isAssignableTo(Class.forName(TypeConversionUtils.descriptorToQualifiedName(typeDescriptor)));
        } catch (ClassNotFoundException e) {
            return isAssignableTo(typeDescriptor, true);
        }
    }

    /**
     * Checks if this type is assignable to the given class.
     *
     * @param clazz the target class
     * @return true if assignable
     */
    public boolean isAssignableTo(Class<?> clazz) {
        try {
            return clazz.isAssignableFrom(Class.forName(internalName.replace('/', '.')));
        } catch (ClassNotFoundException e) {
            // Check direct match
            if (internalName.equals(clazz.getName().replace(".", "/"))) {
                return true;
            }
            // Check parent types recursively
            for (JavaTypeInfo parent : parentTypeInfos) {
                if (parent.isAssignableTo(clazz)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks if this type is assignable to the given type descriptor.
     *
     * @param typeDescriptor     the type descriptor
     * @param ignoreClassForName whether to ignore ClassForName lookup
     * @return true if assignable
     */
    public boolean isAssignableTo(String typeDescriptor, boolean ignoreClassForName) {
        // Check direct match
        String thisDescriptor = "L" + internalName + ";";
        if (thisDescriptor.equals(typeDescriptor)) {
            return true;
        }
        // Check parent types recursively
        for (JavaTypeInfo parent : parentTypeInfos) {
            if (parent.isAssignableTo(typeDescriptor, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this type is an enum.
     *
     * @return true if this is an enum type
     */
    public boolean isEnum() {
        return type == JavaType.ENUM;
    }

    /**
     * Scores a method based on how well the argument types match the parameter types.
     * Returns a normalized score from 0.0 to 1.0.
     * Higher scores indicate better matches.
     *
     * @param method   the method to score
     * @param argTypes the argument types
     * @return normalized score from 0.0 to 1.0
     */
    private double scoreMethod(MethodInfo method, List<String> argTypes) {
        List<String> paramTypes = ScoreUtils.parseParameterDescriptors(method.descriptor());

        // Handle varargs methods
        if (method.isVarArgs() && !paramTypes.isEmpty()) {
            // For varargs, the last parameter is an array type
            // We need at least (paramTypes.size() - 1) arguments
            if (argTypes.size() < paramTypes.size() - 1) {
                return 0.0; // Not enough arguments
            }

            // Score regular parameters
            double totalScore = 0.0;
            int regularParamCount = paramTypes.size() - 1;

            for (int i = 0; i < regularParamCount; i++) {
                totalScore += ScoreUtils.scoreDescriptorMatch(argTypes.get(i), paramTypes.get(i));
            }

            // Score varargs parameters
            String varargType = paramTypes.get(paramTypes.size() - 1);
            // Vararg type is an array, get the component type
            String componentType = varargType.startsWith(ConstantJavaType.ARRAY_PREFIX) ? varargType.substring(1) : varargType;

            for (int i = regularParamCount; i < argTypes.size(); i++) {
                // Varargs match gets a slightly lower score (0.95 * actual score)
                totalScore += 0.95 * ScoreUtils.scoreDescriptorMatch(argTypes.get(i), componentType);
            }

            // Handle case with no arguments (all varargs)
            if (argTypes.isEmpty()) {
                return 0.95; // Slightly lower than exact match since it's varargs
            }

            // Average score across all parameters
            return totalScore / argTypes.size();
        } else {
            // Regular method: must have exact argument count
            if (argTypes.size() != paramTypes.size()) {
                return 0.0;
            }

            // Handle methods with no parameters (perfect match)
            if (argTypes.isEmpty()) {
                return 1.0;
            }

            double totalScore = 0.0;
            for (int i = 0; i < argTypes.size(); i++) {
                totalScore += ScoreUtils.scoreDescriptorMatch(argTypes.get(i), paramTypes.get(i));
            }

            // Average score across all parameters
            return totalScore / argTypes.size();
        }
    }

    /**
     * Sets enum values for this type.
     *
     * @param memberOrdinals map of member name to ordinal value
     */
    public void setEnumValues(Map<String, Integer> memberOrdinals) {
        enumValues.clear();
        enumValues.putAll(memberOrdinals);
    }
}
