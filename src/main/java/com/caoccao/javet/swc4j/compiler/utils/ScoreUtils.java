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

package com.caoccao.javet.swc4j.compiler.utils;

import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for scoring type matches in overload resolution.
 * <p>
 * This class provides a scoring-based system for method/constructor overload resolution:
 * - All candidates are evaluated and scored (0.0 to 1.0)
 * - Exact type matches score 1.0
 * - Widening conversions score 0.95-0.99 (closer conversions score higher)
 * - Boxing/unboxing scores 0.7
 * - Varargs matches score slightly lower (0.95× actual score)
 * - The candidate with the highest average score wins
 * <p>
 * Two sets of methods are provided:
 * - Methods working with JVM type descriptors (String like "I", "Ljava/lang/String;")
 * - Methods working with runtime Class objects
 */
public final class ScoreUtils {
    /**
     * Map from primitive Class to wrapper Class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class
    );

    /**
     * Map from wrapper Class to primitive Class.
     */
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = Map.of(
            Boolean.class, boolean.class,
            Byte.class, byte.class,
            Character.class, char.class,
            Short.class, short.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class
    );

    private ScoreUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Finds the best matching constructor from a cached array of constructors.
     * Uses a scoring system to evaluate all constructors and returns the one with the highest score.
     *
     * @param constructors    the array of constructors to search
     * @param constructorArgs the constructor arguments
     * @return the best matching constructor, or null if no matching constructor found
     */
    public static Constructor<?> findBestConstructor(Constructor<?>[] constructors, Object[] constructorArgs) {
        Constructor<?> bestConstructor = null;
        double bestScore = -1.0;

        for (Constructor<?> constructor : constructors) {
            double score = scoreConstructor(constructor, constructorArgs);
            if (score > bestScore) {
                bestScore = score;
                bestConstructor = constructor;
            }
        }

        return bestScore > 0.0 ? bestConstructor : null;
    }

    /**
     * Finds the best matching method for the given class, method name, and arguments.
     * Uses a scoring system to evaluate all methods with the given name and returns the one with the highest score.
     *
     * @param methods the methods
     * @param args    the method arguments
     * @return the best matching method, or null if no matching method found
     */
    public static Method findBestMethod(Method[] methods, Object[] args) {
        Method bestMethod = null;
        double bestScore = -1.0;

        for (Method method : methods) {
            double score = scoreMethod(method, args);
            if (score > bestScore) {
                bestScore = score;
                bestMethod = method;
            }
        }

        return bestScore > 0.0 ? bestMethod : null;
    }

    /**
     * Gets the primitive type descriptor for a wrapper type descriptor.
     *
     * @param wrapperType the wrapper type descriptor (e.g., "Ljava/lang/Integer;")
     * @return the primitive type descriptor (e.g., "I"), or null if not a wrapper
     */
    public static String getPrimitiveDescriptor(String wrapperType) {
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
     * Gets the wrapper type descriptor for a primitive type descriptor.
     *
     * @param primitiveType the primitive type descriptor (e.g., "I")
     * @return the wrapper type descriptor (e.g., "Ljava/lang/Integer;"), or null if not a primitive
     */
    public static String getWrapperDescriptor(String primitiveType) {
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
     * Parses parameter types from a method descriptor.
     * Example: "(IDLjava/lang/String;)V" -> ["I", "D", "Ljava/lang/String;"]
     *
     * @param descriptor the method descriptor
     * @return list of parameter type descriptors
     */
    public static List<String> parseParameterDescriptors(String descriptor) {
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
     * Scores how well a runtime argument matches a parameter type.
     * Returns a score from 0.0 to 1.0.
     * <p>
     * Scoring:
     * - 1.0: Exact match
     * - 0.95-0.99: Primitive widening (closer to source type = higher score)
     * - 0.8: Reference type compatible (superclass/interface)
     * - 0.7: Boxing/unboxing with exact match
     * - 0.6-0.69: Boxing + widening
     * - 0.5: Compatible with Object or null for reference types
     * - 0.0: Incompatible
     *
     * @param arg       the runtime argument (can be null)
     * @param paramType the parameter type
     * @return score from 0.0 to 1.0
     */
    public static double scoreClassMatch(Object arg, Class<?> paramType) {
        if (arg == null) {
            return paramType.isPrimitive() ? 0.0 : 0.5;
        }

        Class<?> argType = arg.getClass();

        // Exact match
        if (argType.equals(paramType)) {
            return 1.0;
        }

        // Unboxing: wrapper -> primitive
        if (paramType.isPrimitive()) {
            Class<?> primitiveType = WRAPPER_TO_PRIMITIVE.get(argType);
            if (primitiveType != null) {
                if (primitiveType.equals(paramType)) {
                    return 0.7;
                }
                double wideningScore = scoreClassWidening(primitiveType, paramType);
                if (wideningScore > 0.0) {
                    return 0.6 + wideningScore * 0.09;
                }
            }
            return 0.0;
        }

        // Boxing: primitive -> wrapper (arg is already boxed at runtime)
        Class<?> primitiveType = WRAPPER_TO_PRIMITIVE.get(argType);
        if (primitiveType != null) {
            Class<?> wrapperForParam = PRIMITIVE_TO_WRAPPER.get(paramType);
            if (argType.equals(wrapperForParam)) {
                return 0.7;
            }
        }

        // Reference type compatibility
        if (paramType.isAssignableFrom(argType)) {
            if (paramType.equals(Object.class)) {
                return 0.5;
            }
            return 0.8;
        }

        return 0.0;
    }

    /**
     * Scores primitive widening conversions using Class objects.
     * Returns 0.0 if conversion is not allowed.
     * Returns 0.95-0.99 for valid conversions (closer conversions get higher scores).
     *
     * @param fromType source primitive type
     * @param toType   target primitive type
     * @return score from 0.0 to 0.99
     */
    public static double scoreClassWidening(Class<?> fromType, Class<?> toType) {
        if (fromType == byte.class) {
            if (toType == short.class) return 0.99;
            if (toType == int.class) return 0.98;
            if (toType == long.class) return 0.97;
            if (toType == float.class) return 0.96;
            if (toType == double.class) return 0.95;
        } else if (fromType == short.class) {
            if (toType == int.class) return 0.99;
            if (toType == long.class) return 0.98;
            if (toType == float.class) return 0.97;
            if (toType == double.class) return 0.96;
        } else if (fromType == char.class) {
            if (toType == int.class) return 0.99;
            if (toType == long.class) return 0.98;
            if (toType == float.class) return 0.97;
            if (toType == double.class) return 0.96;
        } else if (fromType == int.class) {
            if (toType == long.class) return 0.99;
            if (toType == float.class) return 0.98;
            if (toType == double.class) return 0.97;
        } else if (fromType == long.class) {
            if (toType == float.class) return 0.99;
            if (toType == double.class) return 0.98;
        } else if (fromType == float.class) {
            if (toType == double.class) return 0.99;
        }
        return 0.0;
    }

    /**
     * Scores a constructor based on how well the argument types match the parameter types.
     * Returns a normalized score from 0.0 to 1.0.
     * Higher scores indicate better matches.
     *
     * @param constructor     the constructor to score
     * @param constructorArgs the constructor arguments
     * @return normalized score from 0.0 to 1.0
     */
    private static double scoreConstructor(Constructor<?> constructor, Object[] constructorArgs) {
        Class<?>[] paramTypes = constructor.getParameterTypes();

        // Handle varargs constructors
        if (constructor.isVarArgs() && paramTypes.length > 0) {
            int regularParamCount = paramTypes.length - 1;
            Class<?> varargArrayType = paramTypes[paramTypes.length - 1];
            Class<?> varargComponentType = varargArrayType.getComponentType();

            // Check if args matches the varargs as a direct array pass
            if (constructorArgs.length == paramTypes.length) {
                Object lastArg = constructorArgs[constructorArgs.length - 1];
                if (lastArg != null && varargArrayType.isAssignableFrom(lastArg.getClass())) {
                    // Direct array pass - score as exact match
                    double totalScore = 0.0;
                    for (int i = 0; i < regularParamCount; i++) {
                        totalScore += scoreClassMatch(constructorArgs[i], paramTypes[i]);
                    }
                    // Exact array type match scores 1.0, compatible array scores 0.95
                    totalScore += lastArg.getClass().equals(varargArrayType) ? 1.0 : 0.95;
                    return totalScore / paramTypes.length;
                }
            }

            // Varargs as individual elements
            if (constructorArgs.length < regularParamCount) {
                return 0.0;
            }

            double totalScore = 0.0;

            for (int i = 0; i < regularParamCount; i++) {
                totalScore += scoreClassMatch(constructorArgs[i], paramTypes[i]);
            }

            for (int i = regularParamCount; i < constructorArgs.length; i++) {
                totalScore += 0.95 * scoreClassMatch(constructorArgs[i], varargComponentType);
            }

            return constructorArgs.length > 0 ? totalScore / constructorArgs.length : 1.0;
        } else {
            if (constructorArgs.length != paramTypes.length) {
                return 0.0;
            }

            if (paramTypes.length == 0) {
                return 1.0;
            }

            double totalScore = 0.0;
            for (int i = 0; i < constructorArgs.length; i++) {
                totalScore += scoreClassMatch(constructorArgs[i], paramTypes[i]);
            }

            return totalScore / constructorArgs.length;
        }
    }

    /**
     * Scores how well an argument type descriptor matches a parameter type descriptor.
     * Returns a score from 0.0 to 1.0.
     * <p>
     * Scoring:
     * - 1.0: Exact match
     * - 0.95-0.99: Primitive widening (closer to source type = higher score)
     * - 0.7: Boxing/unboxing with exact match
     * - 0.6-0.69: Boxing + widening
     * - 0.5: Reference type compatible (Object, etc.)
     * - 0.0: Incompatible
     *
     * @param argType   the argument type descriptor
     * @param paramType the parameter type descriptor
     * @return score from 0.0 to 1.0
     */
    public static double scoreDescriptorMatch(String argType, String paramType) {
        // Exact match
        if (argType.equals(paramType)) {
            return 1.0;
        }

        // Both primitive types - check widening
        if (TypeConversionUtils.isPrimitiveType(argType) && TypeConversionUtils.isPrimitiveType(paramType)) {
            return scoreDescriptorWidening(argType, paramType);
        }

        // Boxing: primitive -> wrapper
        if (TypeConversionUtils.isPrimitiveType(argType) && !TypeConversionUtils.isPrimitiveType(paramType)) {
            String wrapperType = getWrapperDescriptor(argType);
            if (wrapperType != null && wrapperType.equals(paramType)) {
                return 0.7; // Boxing with exact match
            }
            return 0.0;
        }

        // Unboxing: wrapper -> primitive
        if (!TypeConversionUtils.isPrimitiveType(argType) && TypeConversionUtils.isPrimitiveType(paramType)) {
            String primitiveType = getPrimitiveDescriptor(argType);
            if (primitiveType != null) {
                if (primitiveType.equals(paramType)) {
                    return 0.7; // Unboxing with exact match
                }
                // Can we widen after unboxing?
                double wideningScore = scoreDescriptorWidening(primitiveType, paramType);
                if (wideningScore > 0.0) {
                    return 0.6 + wideningScore * 0.09; // 0.6-0.69 range
                }
            }
            return 0.0;
        }

        // Both reference types
        if (!TypeConversionUtils.isPrimitiveType(argType) && !TypeConversionUtils.isPrimitiveType(paramType)) {
            // Any reference type can be assigned to Object
            if (paramType.equals("Ljava/lang/Object;")) {
                return 0.5;
            }
            return 0.0;
        }

        return 0.0;
    }

    /**
     * Scores primitive widening conversions using type descriptors.
     * Returns 0.0 if conversion is not allowed.
     * Returns 0.95-0.99 for valid conversions (closer conversions get higher scores).
     *
     * @param fromType source primitive type descriptor
     * @param toType   target primitive type descriptor
     * @return score from 0.0 to 0.99
     */
    public static double scoreDescriptorWidening(String fromType, String toType) {
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

    private static double scoreMethod(Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();

        if (method.isVarArgs() && paramTypes.length > 0) {
            int regularParamCount = paramTypes.length - 1;
            Class<?> varargArrayType = paramTypes[paramTypes.length - 1];
            Class<?> varargComponentType = varargArrayType.getComponentType();

            // Check if args matches the varargs as a direct array pass
            // e.g., test(double[]) called with invoke("test", new double[]{1.5, 2.5})
            if (args.length == paramTypes.length) {
                Object lastArg = args[args.length - 1];
                if (lastArg != null && varargArrayType.isAssignableFrom(lastArg.getClass())) {
                    // Direct array pass - score as exact match
                    double totalScore = 0.0;
                    for (int i = 0; i < regularParamCount; i++) {
                        totalScore += scoreClassMatch(args[i], paramTypes[i]);
                    }
                    // Exact array type match scores 1.0, compatible array scores 0.95
                    totalScore += lastArg.getClass().equals(varargArrayType) ? 1.0 : 0.95;
                    return totalScore / paramTypes.length;
                }
            }

            // Varargs as individual elements
            if (args.length < regularParamCount) {
                return 0.0;
            }

            double totalScore = 0.0;

            for (int i = 0; i < regularParamCount; i++) {
                totalScore += scoreClassMatch(args[i], paramTypes[i]);
            }

            for (int i = regularParamCount; i < args.length; i++) {
                totalScore += 0.95 * scoreClassMatch(args[i], varargComponentType);
            }

            return args.length > 0 ? totalScore / args.length : 1.0;
        } else {
            if (args.length != paramTypes.length) {
                return 0.0;
            }

            if (paramTypes.length == 0) {
                return 1.0;
            }

            double totalScore = 0.0;
            for (int i = 0; i < args.length; i++) {
                totalScore += scoreClassMatch(args[i], paramTypes[i]);
            }

            return totalScore / args.length;
        }
    }
}
