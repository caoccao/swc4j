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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores metadata about an imported Java class.
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
 */
public final class JavaClassInfo {
    private final String alias;
    private final String internalName;
    private final Map<String, List<MethodInfo>> methods;
    private final String packageName;

    public JavaClassInfo(String alias, String packageName, String internalName) {
        this.alias = alias;
        this.packageName = packageName;
        this.internalName = internalName;
        this.methods = new HashMap<>();
    }

    public void addMethod(String methodName, MethodInfo methodInfo) {
        methods.computeIfAbsent(methodName, k -> new ArrayList<>()).add(methodInfo);
    }

    /**
     * Counts the number of parameters in a method descriptor.
     */
    private int countParameters(String descriptor) {
        String paramTypes = descriptor.substring(1, descriptor.indexOf(')'));
        int count = 0;
        int position = 0;

        while (position < paramTypes.length()) {
            char c = paramTypes.charAt(position);

            if (c == 'L') {
                // Object type - find the semicolon
                int semicolon = paramTypes.indexOf(';', position);
                position = semicolon + 1;
                count++;
            } else if (c == '[') {
                // Array type - consume array markers and the element type
                while (position < paramTypes.length() && paramTypes.charAt(position) == '[') {
                    position++;
                }
                if (position < paramTypes.length()) {
                    if (paramTypes.charAt(position) == 'L') {
                        int semicolon = paramTypes.indexOf(';', position);
                        position = semicolon + 1;
                    } else {
                        position++;
                    }
                }
                count++;
            } else {
                // Primitive type (single character)
                position++;
                count++;
            }
        }

        return count;
    }

    public String getAlias() {
        return alias;
    }

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
     * Prefer using getMethod(String, List<String>) for proper overload resolution.
     */
    public MethodInfo getMethod(String methodName) {
        List<MethodInfo> overloads = methods.get(methodName);
        return overloads != null && !overloads.isEmpty() ? overloads.get(0) : null;
    }

    /**
     * Gets the best matching method for the given method name and argument count.
     * This is less precise than getMethod(String, List<String>) but useful as a fallback.
     *
     * @deprecated Use getMethod(String, List<String>) for proper type-based resolution
     */
    @Deprecated
    public MethodInfo getMethod(String methodName, int argCount) {
        List<MethodInfo> overloads = methods.get(methodName);
        if (overloads == null) {
            return null;
        }

        // Find method with matching parameter count
        for (MethodInfo method : overloads) {
            String descriptor = method.descriptor();
            int paramCount = countParameters(descriptor);
            if (paramCount == argCount) {
                return method;
            }
        }

        // If no exact match, return the first one
        return overloads.isEmpty() ? null : overloads.get(0);
    }

    /**
     * Gets all method overloads for the given method name.
     */
    public List<MethodInfo> getMethodOverloads(String methodName) {
        return methods.getOrDefault(methodName, new ArrayList<>());
    }

    public Map<String, List<MethodInfo>> getMethods() {
        return methods;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the primitive type for a wrapper type.
     */
    private String getPrimitiveType(String wrapperType) {
        return switch (wrapperType) {
            case "Ljava/lang/Boolean;" -> "Z";
            case "Ljava/lang/Byte;" -> "B";
            case "Ljava/lang/Character;" -> "C";
            case "Ljava/lang/Short;" -> "S";
            case "Ljava/lang/Integer;" -> "I";
            case "Ljava/lang/Long;" -> "J";
            case "Ljava/lang/Float;" -> "F";
            case "Ljava/lang/Double;" -> "D";
            default -> null;
        };
    }

    /**
     * Gets the wrapper type for a primitive type.
     */
    private String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "Z" -> "Ljava/lang/Boolean;";
            case "B" -> "Ljava/lang/Byte;";
            case "C" -> "Ljava/lang/Character;";
            case "S" -> "Ljava/lang/Short;";
            case "I" -> "Ljava/lang/Integer;";
            case "J" -> "Ljava/lang/Long;";
            case "F" -> "Ljava/lang/Float;";
            case "D" -> "Ljava/lang/Double;";
            default -> null;
        };
    }

    /**
     * Checks if a type descriptor represents a primitive type.
     */
    private boolean isPrimitive(String type) {
        return type.length() == 1 && "ZBCSIJFD".contains(type);
    }

    /**
     * Parses parameter types from a method descriptor.
     * Example: "(IDLjava/lang/String;)V" -> ["I", "D", "Ljava/lang/String;"]
     */
    private List<String> parseParameterTypes(String descriptor) {
        String paramTypes = descriptor.substring(1, descriptor.indexOf(')'));
        List<String> types = new ArrayList<>();
        int position = 0;

        while (position < paramTypes.length()) {
            char c = paramTypes.charAt(position);

            if (c == 'L') {
                // Object type - find the semicolon
                int semicolon = paramTypes.indexOf(';', position);
                types.add(paramTypes.substring(position, semicolon + 1));
                position = semicolon + 1;
            } else if (c == '[') {
                // Array type - consume array markers and the element type
                int start = position;
                while (position < paramTypes.length() && paramTypes.charAt(position) == '[') {
                    position++;
                }
                if (position < paramTypes.length()) {
                    if (paramTypes.charAt(position) == 'L') {
                        int semicolon = paramTypes.indexOf(';', position);
                        position = semicolon + 1;
                    } else {
                        position++;
                    }
                }
                types.add(paramTypes.substring(start, position));
            } else {
                // Primitive type (single character)
                types.add(String.valueOf(c));
                position++;
            }
        }

        return types;
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
        List<String> paramTypes = parseParameterTypes(method.descriptor());

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
                totalScore += scoreParameterMatch(argTypes.get(i), paramTypes.get(i));
            }

            // Score varargs parameters
            String varargType = paramTypes.get(paramTypes.size() - 1);
            // Vararg type is an array, get the component type
            String componentType = varargType.startsWith("[") ? varargType.substring(1) : varargType;

            for (int i = regularParamCount; i < argTypes.size(); i++) {
                // Varargs match gets a slightly lower score (0.95 * actual score)
                totalScore += 0.95 * scoreParameterMatch(argTypes.get(i), componentType);
            }

            // Average score across all parameters
            return totalScore / argTypes.size();
        } else {
            // Regular method: must have exact argument count
            if (argTypes.size() != paramTypes.size()) {
                return 0.0;
            }

            double totalScore = 0.0;
            for (int i = 0; i < argTypes.size(); i++) {
                totalScore += scoreParameterMatch(argTypes.get(i), paramTypes.get(i));
            }

            // Average score across all parameters
            return totalScore / argTypes.size();
        }
    }

    /**
     * Scores how well an argument type matches a parameter type.
     * Returns a score from 0.0 to 1.0.
     * <p>
     * Scoring:
     * - 1.0: Exact match
     * - 0.95-0.99: Primitive widening (closer to source type = higher score)
     * - 0.7: Boxing/unboxing with exact match
     * - 0.6-0.69: Boxing + widening
     * - 0.5: Reference type compatible (Object, etc.)
     * - 0.0: Incompatible
     */
    private double scoreParameterMatch(String argType, String paramType) {
        // Exact match
        if (argType.equals(paramType)) {
            return 1.0;
        }

        // Both primitive types - check widening
        if (isPrimitive(argType) && isPrimitive(paramType)) {
            return scoreWideningConversion(argType, paramType);
        }

        // Boxing: primitive -> wrapper
        if (isPrimitive(argType) && !isPrimitive(paramType)) {
            String wrapperType = getWrapperType(argType);
            if (wrapperType.equals(paramType)) {
                return 0.7; // Boxing with exact match
            }
            // Check if we can widen after boxing (rare but possible)
            return 0.0; // Not supported for now
        }

        // Unboxing: wrapper -> primitive
        if (!isPrimitive(argType) && isPrimitive(paramType)) {
            String primitiveType = getPrimitiveType(argType);
            if (primitiveType != null) {
                if (primitiveType.equals(paramType)) {
                    return 0.7; // Unboxing with exact match
                }
                // Can we widen after unboxing?
                double wideningScore = scoreWideningConversion(primitiveType, paramType);
                if (wideningScore > 0.0) {
                    return 0.6 + wideningScore * 0.09; // 0.6-0.69 range
                }
            }
            return 0.0;
        }

        // Both reference types
        if (!isPrimitive(argType) && !isPrimitive(paramType)) {
            // Any reference type can be assigned to Object
            if (paramType.equals("Ljava/lang/Object;")) {
                return 0.5;
            }
            // For now, only exact match for other reference types
            // Could be extended to check class hierarchy
            return 0.0;
        }

        return 0.0;
    }

    /**
     * Scores primitive widening conversions.
     * Returns 0.0 if conversion is not allowed.
     * Returns 0.95-0.99 for valid conversions (closer conversions get higher scores).
     */
    private double scoreWideningConversion(String fromType, String toType) {
        return switch (fromType) {
            case "B" -> // byte
                    switch (toType) {
                        case "S" -> 0.99; // byte -> short (closest)
                        case "I" -> 0.98; // byte -> int
                        case "J" -> 0.97; // byte -> long
                        case "F" -> 0.96; // byte -> float
                        case "D" -> 0.95; // byte -> double (farthest)
                        default -> 0.0;
                    };
            case "S" -> // short
                    switch (toType) {
                        case "I" -> 0.99; // short -> int (closest)
                        case "J" -> 0.98; // short -> long
                        case "F" -> 0.97; // short -> float
                        case "D" -> 0.96; // short -> double (farthest)
                        default -> 0.0;
                    };
            case "C" -> // char
                    switch (toType) {
                        case "I" -> 0.99; // char -> int (closest)
                        case "J" -> 0.98; // char -> long
                        case "F" -> 0.97; // char -> float
                        case "D" -> 0.96; // char -> double (farthest)
                        default -> 0.0;
                    };
            case "I" -> // int
                    switch (toType) {
                        case "J" -> 0.99; // int -> long (closest)
                        case "F" -> 0.98; // int -> float
                        case "D" -> 0.97; // int -> double (farthest)
                        default -> 0.0;
                    };
            case "J" -> // long
                    switch (toType) {
                        case "F" -> 0.99; // long -> float (closest)
                        case "D" -> 0.98; // long -> double (farthest)
                        default -> 0.0;
                    };
            case "F" -> // float
                    toType.equals("D") ? 0.99 : 0.0; // float -> double
            default -> 0.0;
        };
    }
}
