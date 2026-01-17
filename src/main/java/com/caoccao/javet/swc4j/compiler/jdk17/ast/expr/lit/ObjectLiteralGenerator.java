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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit;

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstKeyValueProp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSpreadElement;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringConcatUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for object literal bytecode.
 * Generates LinkedHashMap<String, Object> by default (Phase 1-4: no type annotation).
 * <p>
 * Phase 1: Basic key-value pairs with identifier, string literal, and numeric keys
 * Phase 2: Computed property names {[expr]: value}
 * Phase 3: Property shorthand {x, y} equivalent to {x: x, y: y}
 * Phase 4: Spread operator {...other} for shallow merging
 * <p>
 * JavaScript: {a: 1, b: "hello", c: true, [key]: value, x, y, ...other}
 * Java: LinkedHashMap<String, Object> with key-value pairs
 */
public final class ObjectLiteralGenerator {
    private ObjectLiteralGenerator() {
    }

    /**
     * Generate bytecode for object literal.
     * Phase 1-4: Basic implementation with computed property names, shorthand, and spread - no type annotation, default to LinkedHashMap<String, Object>
     * Phase 2: Type validation for Record<K, V> types
     *
     * @param code           code builder for bytecode generation
     * @param cp             constant pool
     * @param objectLit      object literal AST node
     * @param returnTypeInfo return type information (used for Record type validation in Phase 2)
     * @param context        compilation context
     * @param options        compiler options
     * @param callback       callback for generating nested expressions
     * @throws Swc4jByteCodeCompilerException if generation fails or type validation fails
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstObjectLit objectLit,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

        // Phase 2: Extract generic type info for Record type validation
        GenericTypeInfo genericTypeInfo = returnTypeInfo != null ? returnTypeInfo.genericTypeInfo() : null;

        // Create new LinkedHashMap instance
        int hashMapClass = cp.addClass("java/util/LinkedHashMap");
        int hashMapInit = cp.addMethodRef("java/util/LinkedHashMap", "<init>", "()V");
        int hashMapPut = cp.addMethodRef("java/util/LinkedHashMap", "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

        // Stack: []
        code.newInstance(hashMapClass);  // Stack: [map]
        code.dup();                      // Stack: [map, map]
        code.invokespecial(hashMapInit); // Stack: [map]

        // Add each property to the map
        for (var prop : objectLit.getProps()) {
            if (prop instanceof Swc4jAstKeyValueProp kvProp) {
                // Phase 2: Validate key and value types against Record<K, V> if present
                if (genericTypeInfo != null) {
                    validateKeyValueProperty(kvProp, genericTypeInfo, context, options);
                }

                // Duplicate map reference for put() call
                code.dup(); // Stack: [map, map]

                // Generate key
                String expectedKeyType = genericTypeInfo != null ? genericTypeInfo.getKeyType() : null;
                generateKey(code, cp, kvProp.getKey(), expectedKeyType, context, options, callback);
                // Stack: [map, map, key]

                // Generate value
                ISwc4jAstExpr valueExpr = kvProp.getValue();
                String valueType = TypeResolver.inferTypeFromExpr(valueExpr, context, options);
                if (valueType == null) {
                    valueType = "Ljava/lang/Object;";
                }

                callback.generateExpr(code, cp, valueExpr, null, context, options);
                // Stack: [map, map, key, value]

                // Box primitive values to Object
                if (TypeConversionUtils.isPrimitiveType(valueType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                    TypeConversionUtils.boxPrimitiveType(code, cp, valueType, wrapperType);
                }
                // Stack: [map, map, key, boxedValue]

                // Call map.put(key, value)
                code.invokevirtual(hashMapPut); // Stack: [map, oldValue]
                code.pop(); // Discard old value - Stack: [map]
            } else if (prop instanceof Swc4jAstIdent ident) {
                // Property shorthand: {x, y} → {x: x, y: y}
                // Phase 3: Handle shorthand property syntax (Swc4jAstIdent directly in props list)

                // Phase 2: Validate shorthand property types against Record<K, V> if present
                if (genericTypeInfo != null) {
                    validateShorthandProperty(ident, genericTypeInfo, context, options);
                }

                code.dup(); // Stack: [map, map]

                // Generate key from identifier name
                String keyStr = ident.getSym();
                int keyIndex = cp.addString(keyStr);
                code.ldc(keyIndex);
                // Stack: [map, map, key]

                // Generate value - the identifier refers to a variable with that name
                String valueType = TypeResolver.inferTypeFromExpr(ident, context, options);
                if (valueType == null) {
                    valueType = "Ljava/lang/Object;";
                }

                callback.generateExpr(code, cp, ident, null, context, options);
                // Stack: [map, map, key, value]

                // Box primitive values to Object
                if (TypeConversionUtils.isPrimitiveType(valueType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                    TypeConversionUtils.boxPrimitiveType(code, cp, valueType, wrapperType);
                }
                // Stack: [map, map, key, boxedValue]

                // Call map.put(key, value)
                code.invokevirtual(hashMapPut); // Stack: [map, oldValue]
                code.pop(); // Discard old value - Stack: [map]
            } else if (prop instanceof Swc4jAstSpreadElement spread) {
                // Spread operator: {...other} for shallow merging
                // Phase 4: Handle spread syntax
                code.dup(); // Stack: [map, map]

                // Generate the spread expression (should evaluate to a Map)
                ISwc4jAstExpr spreadExpr = spread.getExpr();
                callback.generateExpr(code, cp, spreadExpr, null, context, options);
                // Stack: [map, map, spreadMap]

                // Call map.putAll(spreadMap) to merge all properties
                int putAllRef = cp.addMethodRef("java/util/LinkedHashMap", "putAll",
                        "(Ljava/util/Map;)V");
                code.invokevirtual(putAllRef); // Stack: [map]
                // Note: putAll returns void, so no need to pop
            }
            // TODO: Phase 5+ - Handle other property types (Method, Getter/Setter, etc.)
        }
        // Stack: [map]
        // LinkedHashMap instance is left on stack for return/assignment
    }

    /**
     * Generate bytecode for property key.
     * Phase 1-2: Support IdentName, Str, Number, and Computed keys (all converted to String by default)
     * Phase 2.1: Support primitive wrapper keys when Record<T, V> is declared with non-String key type
     *
     * @param code            code builder
     * @param cp              constant pool
     * @param key             property key AST node
     * @param expectedKeyType expected key type descriptor from Record type (may be null)
     * @param context         compilation context
     * @param options         compiler options
     * @param callback        callback for generating computed keys
     * @throws Swc4jByteCodeCompilerException if key generation fails
     */
    private static void generateKey(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstPropName key,
            String expectedKeyType,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

        // Phase 2.1: Check if we should generate primitive wrapper keys (not String)
        boolean nonStringKeys = expectedKeyType != null && isPrimitiveKeyType(expectedKeyType);

        if (key instanceof Swc4jAstIdentName identName) {
            // Identifier key: {name: value} → "name"
            String keyStr = identName.getSym();
            int keyIndex = cp.addString(keyStr);
            code.ldc(keyIndex);
        } else if (key instanceof Swc4jAstStr str) {
            // String literal key: {"string-key": value} → "string-key"
            String keyStr = str.getValue();
            int keyIndex = cp.addString(keyStr);
            code.ldc(keyIndex);
        } else if (key instanceof Swc4jAstNumber num) {
            // Numeric key handling depends on Record type
            if (nonStringKeys) {
                // Phase 2.1: Record<T, V> where T is a wrapper type - generate boxed key
                generateNumericKey(code, cp, num, expectedKeyType);
            } else {
                // Phase 1: Default behavior - convert to String
                String keyStr = String.valueOf((int) num.getValue());
                int keyIndex = cp.addString(keyStr);
                code.ldc(keyIndex);
            }
        } else if (key instanceof Swc4jAstComputedPropName computed) {
            // Computed property name: {[expr]: value}
            // Phase 2.1: Generate expression and convert to appropriate type (String or Number)
            ISwc4jAstExpr expr = computed.getExpr();
            String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
            if (exprType == null) {
                exprType = "Ljava/lang/Object;";
            }

            // Generate the expression
            callback.generateExpr(code, cp, expr, null, context, options);
            // Stack: [expr_result]

            // Phase 2.1: Convert to appropriate key type based on Record type
            if (nonStringKeys) {
                // Record<T, V> where T is a primitive wrapper - ensure key is boxed
                if (TypeConversionUtils.isPrimitiveType(exprType)) {
                    // Box primitive types
                    String wrapperType = TypeConversionUtils.getWrapperType(exprType);
                    TypeConversionUtils.boxPrimitiveType(code, cp, exprType, wrapperType);
                    // Stack: [boxed value]
                } else if (isPrimitiveKeyType(exprType)) {
                    // Already boxed primitive wrapper type, no conversion needed
                    // Stack: [boxed value]
                } else {
                    // Non-wrapper type - should have been caught by validation
                    // Stack: [Object]
                }
            } else {
                // Default behavior - convert to String using String.valueOf()
                if ("Ljava/lang/String;".equals(exprType)) {
                    // Already a String, no conversion needed
                } else {
                    // Box primitives first, then call String.valueOf(Object)
                    if (TypeConversionUtils.isPrimitiveType(exprType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(exprType);
                        TypeConversionUtils.boxPrimitiveType(code, cp, exprType, wrapperType);
                    }
                    // Stack: [Object]

                    // Call String.valueOf(Object) to convert to String
                    int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf",
                            "(Ljava/lang/Object;)Ljava/lang/String;");
                    code.invokestatic(valueOfRef);
                    // Stack: [String]
                }
            }
        } else {
            throw new Swc4jByteCodeCompilerException(
                    "Unsupported property key type: " + key.getClass().getSimpleName() +
                            ". Supported types: Identifier, String literal, Number, and Computed property names.");
        }
    }

    /**
     * Generate bytecode for numeric literal key based on expected primitive wrapper type.
     * Phase 2.1: Converts numeric literals to the appropriate wrapper type.
     * For explicit wrapper types (Long, Short, Byte, Float), uses that type.
     * For generic number type (D) or Integer, infers from the actual value (int → Integer, large int → Long, decimal → Double).
     * Supports: Integer, Long, Float, Double, Short, Byte
     *
     * @param code            code builder
     * @param cp              constant pool
     * @param num             numeric key AST node
     * @param expectedKeyType expected key type descriptor (determines which wrapper to use)
     * @throws Swc4jByteCodeCompilerException if generation fails
     */
    private static void generateNumericKey(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstNumber num,
            String expectedKeyType) throws Swc4jByteCodeCompilerException {
        double value = num.getValue();

        // For explicit Long type annotation, always use Long
        if ("Ljava/lang/Long;".equals(expectedKeyType) || "J".equals(expectedKeyType)) {
            long longValue = (long) value;
            code.ldc2_w(cp.addLong(longValue));
            int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            code.invokestatic(valueOfRef);
        }
        // For explicit Short type annotation, always use Short
        else if ("Ljava/lang/Short;".equals(expectedKeyType) || "S".equals(expectedKeyType)) {
            short shortValue = (short) value;
            code.iconst(shortValue);
            int valueOfRef = cp.addMethodRef("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            code.invokestatic(valueOfRef);
        }
        // For explicit Byte type annotation, always use Byte
        else if ("Ljava/lang/Byte;".equals(expectedKeyType) || "B".equals(expectedKeyType)) {
            byte byteValue = (byte) value;
            code.iconst(byteValue);
            int valueOfRef = cp.addMethodRef("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            code.invokestatic(valueOfRef);
        }
        // For explicit Float type annotation, always use Float
        else if ("Ljava/lang/Float;".equals(expectedKeyType) || "F".equals(expectedKeyType)) {
            float floatValue = (float) value;
            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                code.fconst(floatValue);
            } else {
                code.ldc(cp.addFloat(floatValue));
            }
            int valueOfRef = cp.addMethodRef("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            code.invokestatic(valueOfRef);
        }
        // For explicit Double type annotation, always use Double
        else if ("Ljava/lang/Double;".equals(expectedKeyType)) {
            if (value == 0.0 || value == 1.0) {
                code.dconst(value);
            } else {
                code.ldc2_w(cp.addDouble(value));
            }
            int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            code.invokestatic(valueOfRef);
        }
        // For generic number type (primitive D) or Integer, infer from actual value
        // This handles Record<number, V> where number is primitive double (D)
        else {
            // Infer wrapper type from actual numeric value
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                // Integer value
                long longValue = (long) value;
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    // Fits in int → Integer
                    int intValue = (int) longValue;
                    if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                        code.iconst(intValue);
                    } else {
                        code.ldc(cp.addInteger(intValue));
                    }
                    int valueOfRef = cp.addMethodRef("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                    code.invokestatic(valueOfRef);
                } else {
                    // Doesn't fit in int → Long
                    code.ldc2_w(cp.addLong(longValue));
                    int valueOfRef = cp.addMethodRef("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                    code.invokestatic(valueOfRef);
                }
            } else {
                // Floating-point value → Double
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    code.ldc2_w(cp.addDouble(value));
                }
                int valueOfRef = cp.addMethodRef("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                code.invokestatic(valueOfRef);
            }
        }
        // Stack: [boxed key]
    }

    /**
     * Extract property name from a property key for error messages.
     *
     * @param key property key AST node
     * @return property name as string
     */
    private static String getPropertyName(ISwc4jAstPropName key) {
        if (key instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        } else if (key instanceof Swc4jAstStr str) {
            return str.getValue();
        } else if (key instanceof Swc4jAstNumber num) {
            return String.valueOf((int) num.getValue());
        } else if (key instanceof Swc4jAstComputedPropName) {
            return "<computed>";
        }
        return "<unknown>";
    }

    /**
     * Check if a type descriptor represents a primitive wrapper key type.
     * Phase 2.1: Support Record<T, V> where T can be any primitive wrapper (Integer, Long, Boolean, etc.)
     *
     * @param typeDescriptor JVM type descriptor
     * @return true if type is a primitive wrapper or primitive (not String)
     */
    private static boolean isPrimitiveKeyType(String typeDescriptor) {
        if (typeDescriptor == null) {
            return false;
        }
        // Support all primitive wrappers and primitives except String
        // Wrappers: Integer, Long, Float, Double, Boolean, Short, Byte, Character
        // Primitives: int, long, float, double, boolean, short, byte, char
        return "Ljava/lang/Integer;".equals(typeDescriptor) ||
                "Ljava/lang/Long;".equals(typeDescriptor) ||
                "Ljava/lang/Float;".equals(typeDescriptor) ||
                "Ljava/lang/Double;".equals(typeDescriptor) ||
                "Ljava/lang/Boolean;".equals(typeDescriptor) ||
                "Ljava/lang/Short;".equals(typeDescriptor) ||
                "Ljava/lang/Byte;".equals(typeDescriptor) ||
                "Ljava/lang/Character;".equals(typeDescriptor) ||
                "I".equals(typeDescriptor) ||
                "J".equals(typeDescriptor) ||
                "F".equals(typeDescriptor) ||
                "D".equals(typeDescriptor) ||
                "Z".equals(typeDescriptor) ||
                "S".equals(typeDescriptor) ||
                "B".equals(typeDescriptor) ||
                "C".equals(typeDescriptor);
    }

    /**
     * Validate a key-value property against Record<K, V> type constraints.
     * Phase 2: Type validation for object literal properties
     *
     * @param kvProp          key-value property to validate
     * @param genericTypeInfo Record type information (key and value type constraints)
     * @param context         compilation context
     * @param options         compiler options
     * @throws Swc4jByteCodeCompilerException if key or value type doesn't match Record constraints
     */
    private static void validateKeyValueProperty(
            Swc4jAstKeyValueProp kvProp,
            GenericTypeInfo genericTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        ISwc4jAstPropName key = kvProp.getKey();
        ISwc4jAstExpr valueExpr = kvProp.getValue();

        // Validate key type
        String actualKeyType = TypeResolver.inferKeyType(key, context, options);
        String expectedKeyType = genericTypeInfo.getKeyType();

        // Special handling for primitive wrapper keys: if expected type is a primitive wrapper,
        // allow any numeric literal since we'll convert during generation
        boolean expectedIsPrimitiveWrapper = isPrimitiveKeyType(expectedKeyType);
        boolean actualIsNumeric = isPrimitiveKeyType(actualKeyType);

        if (!TypeResolver.isAssignable(actualKeyType, expectedKeyType)) {
            // Allow numeric literal → primitive wrapper conversion (e.g., Integer → Long)
            if (expectedIsPrimitiveWrapper && actualIsNumeric) {
                // Compatible - will be converted during generation
            } else {
                String keyName = getPropertyName(key);
                throw Swc4jByteCodeCompilerException.typeMismatch(
                        keyName,
                        expectedKeyType,
                        actualKeyType,
                        true  // isKey = true
                );
            }
        }

        // Validate value type
        String actualValueType = TypeResolver.inferTypeFromExpr(valueExpr, context, options);
        if (actualValueType == null) {
            actualValueType = "Ljava/lang/Object;";
        }

        String expectedValueType = genericTypeInfo.getValueType();

        // Handle nested Record types
        if (genericTypeInfo.isNested() && valueExpr instanceof Swc4jAstObjectLit) {
            // Nested object literal - validation will be handled recursively
            // when the nested object literal is generated
            return;
        }

        if (!TypeResolver.isAssignable(actualValueType, expectedValueType)) {
            String keyName = getPropertyName(key);
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    keyName,
                    expectedValueType,
                    actualValueType,
                    false  // isKey = false (this is a value)
            );
        }
    }

    /**
     * Validate a shorthand property against Record<K, V> type constraints.
     * Phase 2: Type validation for shorthand properties like {x, y}
     *
     * @param ident           identifier in shorthand property
     * @param genericTypeInfo Record type information
     * @param context         compilation context
     * @param options         compiler options
     * @throws Swc4jByteCodeCompilerException if property type doesn't match Record constraints
     */
    private static void validateShorthandProperty(
            Swc4jAstIdent ident,
            GenericTypeInfo genericTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {

        String propertyName = ident.getSym();

        // Shorthand keys are always strings
        String actualKeyType = "Ljava/lang/String;";
        String expectedKeyType = genericTypeInfo.getKeyType();

        if (!TypeResolver.isAssignable(actualKeyType, expectedKeyType)) {
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    propertyName,
                    expectedKeyType,
                    actualKeyType,
                    true  // isKey = true
            );
        }

        // Validate value type (the identifier's inferred type)
        String actualValueType = TypeResolver.inferTypeFromExpr(ident, context, options);
        if (actualValueType == null) {
            actualValueType = "Ljava/lang/Object;";
        }

        String expectedValueType = genericTypeInfo.getValueType();

        if (!TypeResolver.isAssignable(actualValueType, expectedValueType)) {
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    propertyName,
                    expectedValueType,
                    actualValueType,
                    false  // isKey = false (this is a value)
            );
        }
    }
}
