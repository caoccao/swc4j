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

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.GenericTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for object literal bytecode.
 * Generates {@code LinkedHashMap<String, Object>} by default (Phase 1-4: no type annotation).
 * <p>
 * Phase 1: Basic key-value pairs with identifier, string literal, and numeric keys
 * Phase 2: Computed property names {[expr]: value}
 * Phase 3: Property shorthand {x, y} equivalent to {x: x, y: y}
 * Phase 4: Spread operator {...other} for shallow merging
 * <p>
 * JavaScript: {a: 1, b: "hello", c: true, [key]: value, x, y, ...other}
 * Java: {@code LinkedHashMap<String, Object>} with key-value pairs
 */
public final class ObjectLiteralProcessor extends BaseAstProcessor<Swc4jAstObjectLit> {
    /**
     * Instantiates a new Object literal processor.
     *
     * @param compiler the compiler
     */
    public ObjectLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Generate bytecode for object literal.
     * Phase 1-4: Basic implementation with computed property names, shorthand, and spread - no type annotation, default to {@code LinkedHashMap<String, Object>}
     * Phase 2: Type validation for {@code Record<K, V>} types
     *
     * @param code           code builder for bytecode generation
     * @param classWriter    the class writer
     * @param objectLit      object literal AST node
     * @param returnTypeInfo return type information (used for Record type validation in Phase 2)
     * @throws Swc4jByteCodeCompilerException if generation fails or type validation fails
     */
    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstObjectLit objectLit,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Phase 2: Extract generic type info for Record type validation
        GenericTypeInfo genericTypeInfo = returnTypeInfo != null ? returnTypeInfo.genericTypeInfo() : null;

        // Create new LinkedHashMap instance
        int hashMapClass = cp.addClass(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP);
        int hashMapInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR___V);
        int hashMapPut = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_PUT,
                ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT);

        // Stack: []
        code.newInstance(hashMapClass);  // Stack: [map]
        code.dup();                      // Stack: [map, map]
        code.invokespecial(hashMapInit); // Stack: [map]

        // Add each property to the map
        for (var prop : objectLit.getProps()) {
            if (prop instanceof Swc4jAstKeyValueProp kvProp) {
                // Phase 2: Validate key and value types against Record<K, V> if present
                if (genericTypeInfo != null) {
                    validateKeyValueProperty(kvProp, genericTypeInfo);
                }

                // Duplicate map reference for put() call
                code.dup(); // Stack: [map, map]

                // Generate key
                String expectedKeyType = genericTypeInfo != null ? genericTypeInfo.getKeyType() : null;
                generateKey(code, classWriter, kvProp.getKey(), expectedKeyType);
                // Stack: [map, map, key]

                // Generate value
                ISwc4jAstExpr valueExpr = kvProp.getValue();
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueExpr);
                if (valueType == null) {
                    valueType = ConstantJavaType.LJAVA_LANG_OBJECT;
                }

                // Phase 2.3: Pass nested GenericTypeInfo for nested object literals
                ReturnTypeInfo valueReturnType = null;
                if (genericTypeInfo != null && genericTypeInfo.isNested() && valueExpr instanceof Swc4jAstObjectLit) {
                    // Create ReturnTypeInfo with nested GenericTypeInfo for recursive validation
                    valueReturnType = ReturnTypeInfo.of(getSourceCode(), valueExpr, ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP, genericTypeInfo.getNestedTypeInfo());
                }

                compiler.getExpressionProcessor().generate(code, classWriter, valueExpr, valueReturnType);
                // Stack: [map, map, key, value]

                // Box primitive values to Object
                if (TypeConversionUtils.isPrimitiveType(valueType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, wrapperType);
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
                    validateShorthandProperty(ident, genericTypeInfo);
                }

                code.dup(); // Stack: [map, map]

                // Generate key from identifier name
                String keyStr = ident.getSym();
                int keyIndex = cp.addString(keyStr);
                code.ldc(keyIndex);
                // Stack: [map, map, key]

                // Generate value - the identifier refers to a variable with that name
                String valueType = compiler.getTypeResolver().inferTypeFromExpr(ident);
                if (valueType == null) {
                    valueType = ConstantJavaType.LJAVA_LANG_OBJECT;
                }

                compiler.getExpressionProcessor().generate(code, classWriter, ident, null);
                // Stack: [map, map, key, value]

                // Box primitive values to Object
                if (TypeConversionUtils.isPrimitiveType(valueType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(valueType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, wrapperType);
                }
                // Stack: [map, map, key, boxedValue]

                // Call map.put(key, value)
                code.invokevirtual(hashMapPut); // Stack: [map, oldValue]
                code.pop(); // Discard old value - Stack: [map]
            } else if (prop instanceof Swc4jAstSpreadElement spread) {
                // Spread operator: {...other} for shallow merging
                // Phase 4: Handle spread syntax with type validation

                // Phase 4: Validate spread source type against Record<K, V> if present
                if (genericTypeInfo != null) {
                    validateSpreadElement(spread, genericTypeInfo);
                }

                code.dup(); // Stack: [map, map]

                // Generate the spread expression (should evaluate to a Map)
                ISwc4jAstExpr spreadExpr = spread.getExpr();
                compiler.getExpressionProcessor().generate(code, classWriter, spreadExpr, null);
                // Stack: [map, map, spreadMap]

                // Call map.putAll(spreadMap) to merge all properties
                int putAllRef = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_LINKEDHASHMAP, ConstantJavaMethod.METHOD_PUT_ALL,
                        ConstantJavaDescriptor.DESCRIPTOR_LJAVA_UTIL_MAP__V);
                code.invokevirtual(putAllRef); // Stack: [map]
                // Note: putAll returns void, so no need to pop
            } else if (prop instanceof Swc4jAstMethodProp methodProp) {
                // Method properties are not supported - requires function compilation
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        methodProp,
                        "Method properties are not supported in object literals. " +
                                "Object literals compile to LinkedHashMap which cannot store executable methods. " +
                                "Consider using a class with methods instead.");
            } else if (prop instanceof Swc4jAstGetterProp getterProp) {
                // Getter properties are not supported - incompatible with Map semantics
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        getterProp,
                        "Getter properties are not supported in object literals. " +
                                "LinkedHashMap cannot implement property descriptors. " +
                                "Consider storing the computed value directly or using a class with getter methods.");
            } else if (prop instanceof Swc4jAstSetterProp setterProp) {
                // Setter properties are not supported - incompatible with Map semantics
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        setterProp,
                        "Setter properties are not supported in object literals. " +
                                "LinkedHashMap cannot implement property descriptors. " +
                                "Consider using a class with setter methods instead.");
            }
        }
        // Stack: [map]
        // LinkedHashMap instance is left on stack for return/assignment
    }

    private void generateKey(
            CodeBuilder code,
            ClassWriter classWriter,
            ISwc4jAstPropName key,
            String expectedKeyType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();

        // Phase 2.1: Check if we should generate primitive wrapper keys (not String)
        boolean nonStringKeys = isPrimitiveKeyType(expectedKeyType);

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
                generateNumericKey(code, classWriter, num, expectedKeyType);
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

            // Check if the computed key is Symbol-related - reject with clear error
            if (isSymbolExpression(expr)) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        computed,
                        "Symbol keys are not supported in object literals. " +
                                "Java Map keys must be Objects with proper equals()/hashCode() implementations. " +
                                "JavaScript Symbols have no equivalent in Java. " +
                                "Consider using string keys instead.");
            }

            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            if (exprType == null) {
                exprType = ConstantJavaType.LJAVA_LANG_OBJECT;
            }

            // Generate the expression
            compiler.getExpressionProcessor().generate(code, classWriter, expr, null);
            // Stack: [expr_result]

            // Phase 2.1: Convert to appropriate key type based on Record type
            if (nonStringKeys) {
                // Record<T, V> where T is a primitive wrapper - ensure key is boxed
                if (TypeConversionUtils.isPrimitiveType(exprType)) {
                    // Box primitive types
                    String wrapperType = TypeConversionUtils.getWrapperType(exprType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, exprType, wrapperType);
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
                if (ConstantJavaType.LJAVA_LANG_STRING.equals(exprType)) {
                    // Already a String, no conversion needed
                } else {
                    // Box primitives first, then call String.valueOf(Object)
                    if (TypeConversionUtils.isPrimitiveType(exprType)) {
                        String wrapperType = TypeConversionUtils.getWrapperType(exprType);
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, exprType, wrapperType);
                    }
                    // Stack: [Object]

                    // Call String.valueOf(Object) to convert to String
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF,
                            ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
                    code.invokestatic(valueOfRef);
                    // Stack: [String]
                }
            }
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(),
                    key,
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
    private void generateNumericKey(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstNumber num,
            String expectedKeyType) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        double value = num.getValue();

        // For explicit Long type annotation, always use Long
        if (ConstantJavaType.LJAVA_LANG_LONG.equals(expectedKeyType) || ConstantJavaType.ABBR_LONG.equals(expectedKeyType)) {
            long longValue = (long) value;
            code.ldc2_w(cp.addLong(longValue));
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_J__LJAVA_LANG_LONG);
            code.invokestatic(valueOfRef);
        }
        // For explicit Short type annotation, always use Short
        else if (ConstantJavaType.LJAVA_LANG_SHORT.equals(expectedKeyType) || ConstantJavaType.ABBR_SHORT.equals(expectedKeyType)) {
            short shortValue = (short) value;
            code.iconst(shortValue);
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_SHORT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_S__LJAVA_LANG_SHORT);
            code.invokestatic(valueOfRef);
        }
        // For explicit Byte type annotation, always use Byte
        else if (ConstantJavaType.LJAVA_LANG_BYTE.equals(expectedKeyType) || ConstantJavaType.ABBR_BYTE.equals(expectedKeyType)) {
            byte byteValue = (byte) value;
            code.iconst(byteValue);
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_BYTE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_B__LJAVA_LANG_BYTE);
            code.invokestatic(valueOfRef);
        }
        // For explicit Float type annotation, always use Float
        else if (ConstantJavaType.LJAVA_LANG_FLOAT.equals(expectedKeyType) || ConstantJavaType.ABBR_FLOAT.equals(expectedKeyType)) {
            float floatValue = (float) value;
            if (floatValue == 0.0f || floatValue == 1.0f || floatValue == 2.0f) {
                code.fconst(floatValue);
            } else {
                code.ldc(cp.addFloat(floatValue));
            }
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_FLOAT, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_F__LJAVA_LANG_FLOAT);
            code.invokestatic(valueOfRef);
        }
        // For explicit Double type annotation, always use Double
        else if (ConstantJavaType.LJAVA_LANG_DOUBLE.equals(expectedKeyType)) {
            if (value == 0.0 || value == 1.0) {
                code.dconst(value);
            } else {
                code.ldc2_w(cp.addDouble(value));
            }
            int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_D__LJAVA_LANG_DOUBLE);
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
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_INTEGER, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_INTEGER);
                    code.invokestatic(valueOfRef);
                } else {
                    // Doesn't fit in int → Long
                    code.ldc2_w(cp.addLong(longValue));
                    int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_LONG, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_J__LJAVA_LANG_LONG);
                    code.invokestatic(valueOfRef);
                }
            } else {
                // Floating-point value → Double
                if (value == 0.0 || value == 1.0) {
                    code.dconst(value);
                } else {
                    code.ldc2_w(cp.addDouble(value));
                }
                int valueOfRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_DOUBLE, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.DESCRIPTOR_D__LJAVA_LANG_DOUBLE);
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
    private String getPropertyName(ISwc4jAstPropName key) {
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
    private boolean isPrimitiveKeyType(String typeDescriptor) {
        if (typeDescriptor == null) {
            return false;
        }
        // Support all primitive wrappers and primitives except String
        // Wrappers: Integer, Long, Float, Double, Boolean, Short, Byte, Character
        // Primitives: int, long, float, double, boolean, short, byte, char
        return ConstantJavaType.LJAVA_LANG_INTEGER.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_LONG.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_FLOAT.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_DOUBLE.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_BOOLEAN.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_SHORT.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_BYTE.equals(typeDescriptor) ||
                ConstantJavaType.LJAVA_LANG_CHARACTER.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_INTEGER.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_LONG.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_FLOAT.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_DOUBLE.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_BOOLEAN.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_SHORT.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_BYTE.equals(typeDescriptor) ||
                ConstantJavaType.ABBR_CHARACTER.equals(typeDescriptor);
    }

    /**
     * Check if an expression is Symbol-related.
     * Detects patterns like:
     * - Symbol (identifier)
     * - Symbol.iterator (member expression)
     * - Symbol() (call expression)
     * - Symbol.for() (call on member expression)
     *
     * @param expr the expression to check
     * @return true if the expression is Symbol-related
     */
    private boolean isSymbolExpression(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstIdent ident) {
            // Direct Symbol reference: {[Symbol]: value}
            return "Symbol".equals(ident.getSym());
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            // Symbol.iterator, Symbol.for, etc.
            ISwc4jAstExpr obj = memberExpr.getObj();
            if (obj instanceof Swc4jAstIdent objIdent) {
                return "Symbol".equals(objIdent.getSym());
            }
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            // Symbol() or Symbol.for()
            var callee = callExpr.getCallee();
            if (callee instanceof ISwc4jAstExpr calleeExpr) {
                return isSymbolExpression(calleeExpr);
            }
        }
        return false;
    }

    private void validateArrayElements(
            Swc4jAstArrayLit arrayLit,
            String expectedElementType,
            Swc4jAstKeyValueProp kvProp) throws Swc4jByteCodeCompilerException {
        // Validate each element in the array literal
        for (var elemOpt : arrayLit.getElems()) {
            if (elemOpt.isPresent()) {
                var elem = elemOpt.get();
                ISwc4jAstExpr elemExpr = elem.getExpr();

                // Skip spread elements for now (they would need recursive validation)
                if (elem.getSpread().isPresent()) {
                    continue;
                }

                // Infer the actual element type
                String actualElementType = compiler.getTypeResolver().inferTypeFromExpr(elemExpr);
                if (actualElementType == null) {
                    actualElementType = ConstantJavaType.LJAVA_LANG_OBJECT;
                }

                // Check if the actual element type is assignable to the expected element type
                if (!TypeResolver.isAssignable(actualElementType, expectedElementType)) {
                    String keyName = getPropertyName(kvProp.getKey());
                    throw Swc4jByteCodeCompilerException.typeMismatch(
                            getSourceCode(),
                            elemExpr,
                            keyName + " (array element)",
                            expectedElementType,
                            actualElementType,
                            false  // isKey = false (this is an element value)
                    );
                }
            }
        }
    }

    private void validateKeyValueProperty(
            Swc4jAstKeyValueProp kvProp,
            GenericTypeInfo genericTypeInfo) throws Swc4jByteCodeCompilerException {
        ISwc4jAstPropName key = kvProp.getKey();
        ISwc4jAstExpr valueExpr = kvProp.getValue();

        // Check for Symbol keys first - reject before type validation
        if (key instanceof Swc4jAstComputedPropName computed) {
            ISwc4jAstExpr expr = computed.getExpr();
            if (isSymbolExpression(expr)) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        computed,
                        "Symbol keys are not supported in object literals. " +
                                "Java Map keys must be Objects with proper equals()/hashCode() implementations. " +
                                "JavaScript Symbols have no equivalent in Java. " +
                                "Consider using string keys instead.");
            }
        }

        // Validate key type
        String actualKeyType = compiler.getTypeResolver().inferKeyType(key);
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
                        getSourceCode(),
                        key,
                        keyName,
                        expectedKeyType,
                        actualKeyType,
                        true  // isKey = true
                );
            }
        }

        // Validate value type
        String actualValueType = compiler.getTypeResolver().inferTypeFromExpr(valueExpr);
        if (actualValueType == null) {
            actualValueType = ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        String expectedValueType = genericTypeInfo.getValueType();

        // Phase 2.3: Handle nested Record types - validate nested object literals recursively
        if (genericTypeInfo.isNested() && valueExpr instanceof Swc4jAstObjectLit nestedObjLit) {
            // Nested object literal - recursively validate all properties
            GenericTypeInfo nestedTypeInfo = genericTypeInfo.getNestedTypeInfo();
            for (var nestedProp : nestedObjLit.getProps()) {
                if (nestedProp instanceof Swc4jAstKeyValueProp nestedKvProp) {
                    validateKeyValueProperty(nestedKvProp, nestedTypeInfo);
                } else if (nestedProp instanceof Swc4jAstIdent nestedIdent) {
                    validateShorthandProperty(nestedIdent, nestedTypeInfo);
                }
                // Spread elements in nested objects would need special handling
                // For now, we skip validation for spread (will use runtime types)
            }
            // Nested validation complete - actual type is LinkedHashMap which matches expected
            return;
        }

        // Handle Array value types - validate array literal elements
        if (genericTypeInfo.isArrayValue() && valueExpr instanceof Swc4jAstArrayLit arrayLit) {
            String expectedElementType = genericTypeInfo.getArrayElementType();
            validateArrayElements(arrayLit, expectedElementType, kvProp);
            // Array validation complete - actual type is ArrayList which matches expected
            return;
        }

        if (!TypeResolver.isAssignable(actualValueType, expectedValueType)) {
            String keyName = getPropertyName(key);
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    getSourceCode(),
                    valueExpr,
                    keyName,
                    expectedValueType,
                    actualValueType,
                    false  // isKey = false (this is a value)
            );
        }
    }

    private void validateShorthandProperty(
            Swc4jAstIdent ident,
            GenericTypeInfo genericTypeInfo) throws Swc4jByteCodeCompilerException {
        String propertyName = ident.getSym();

        // Shorthand keys are always strings
        String actualKeyType = ConstantJavaType.LJAVA_LANG_STRING;
        String expectedKeyType = genericTypeInfo.getKeyType();

        if (!TypeResolver.isAssignable(actualKeyType, expectedKeyType)) {
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    getSourceCode(),
                    ident,
                    propertyName,
                    expectedKeyType,
                    actualKeyType,
                    true  // isKey = true
            );
        }

        // Validate value type (the identifier's inferred type)
        String actualValueType = compiler.getTypeResolver().inferTypeFromExpr(ident);
        if (actualValueType == null) {
            actualValueType = ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        String expectedValueType = genericTypeInfo.getValueType();

        if (!TypeResolver.isAssignable(actualValueType, expectedValueType)) {
            throw Swc4jByteCodeCompilerException.typeMismatch(
                    getSourceCode(),
                    ident,
                    propertyName,
                    expectedValueType,
                    actualValueType,
                    false  // isKey = false (this is a value)
            );
        }
    }

    private void validateSpreadElement(
            Swc4jAstSpreadElement spread,
            GenericTypeInfo genericTypeInfo) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();


        ISwc4jAstExpr spreadExpr = spread.getExpr();

        // Infer the type of the spread expression
        String spreadType = compiler.getTypeResolver().inferTypeFromExpr(spreadExpr);
        if (spreadType == null) {
            spreadType = ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        // The spread source must be a LinkedHashMap (or compatible Map type)
        // For now, we check if it's assignable to LinkedHashMap
        if (!ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP.equals(spreadType) &&
                !ConstantJavaType.LJAVA_UTIL_MAP.equals(spreadType) &&
                !ConstantJavaType.LJAVA_LANG_OBJECT.equals(spreadType)) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(),
                    spread,
                    "Spread source must be a Map type, got: " + spreadType);
        }

        // Check if the spread expression has generic type info (e.g., it's a Record type)
        GenericTypeInfo spreadGenericInfo = null;
        if (spreadExpr instanceof Swc4jAstIdent ident) {
            // Look up the variable's generic type info from context
            String varName = ident.getSym();
            spreadGenericInfo = context.getGenericTypeInfoMap().get(varName);
        }

        // If we have generic type info for both target and source, validate compatibility
        if (spreadGenericInfo != null) {
            String expectedKeyType = genericTypeInfo.getKeyType();
            String actualKeyType = spreadGenericInfo.getKeyType();

            // Validate key types match
            if (!TypeResolver.isAssignable(actualKeyType, expectedKeyType)) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        spread,
                        "Spread source has incompatible key type: expected " +
                                expectedKeyType + ", got " + actualKeyType);
            }

            String expectedValueType = genericTypeInfo.getValueType();
            String actualValueType = spreadGenericInfo.getValueType();

            // Phase 4.1: Handle nested Record types - validate nested compatibility
            if (genericTypeInfo.isNested() && spreadGenericInfo.isNested()) {
                // Both are nested - recursively validate nested types
                GenericTypeInfo expectedNestedInfo = genericTypeInfo.getNestedTypeInfo();
                GenericTypeInfo actualNestedInfo = spreadGenericInfo.getNestedTypeInfo();

                String expectedNestedKeyType = expectedNestedInfo.getKeyType();
                String actualNestedKeyType = actualNestedInfo.getKeyType();

                if (!TypeResolver.isAssignable(actualNestedKeyType, expectedNestedKeyType)) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(),
                            spread,
                            "Spread source has incompatible nested key type: expected " +
                                    expectedNestedKeyType + ", got " + actualNestedKeyType);
                }

                String expectedNestedValueType = expectedNestedInfo.getValueType();
                String actualNestedValueType = actualNestedInfo.getValueType();

                if (!TypeResolver.isAssignable(actualNestedValueType, expectedNestedValueType)) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(),
                            spread,
                            "Spread source has incompatible value type: expected " +
                                    expectedNestedValueType + ", got " + actualNestedValueType);
                }
            } else if (genericTypeInfo.isNested() != spreadGenericInfo.isNested()) {
                // One is nested, the other is not - incompatible
                throw new Swc4jByteCodeCompilerException(getSourceCode(),
                        spread,
                        "Spread source has incompatible nesting: expected nested=" +
                                genericTypeInfo.isNested() + ", got nested=" + spreadGenericInfo.isNested());
            } else {
                // Neither is nested - validate value types match
                if (!TypeResolver.isAssignable(actualValueType, expectedValueType)) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(),
                            spread,
                            "Spread source has incompatible value type: expected " +
                                    expectedValueType + ", got " + actualValueType);
                }
            }
        }
        // If spread source doesn't have generic type info, we can't validate at compile time
        // Runtime will handle any type mismatches
    }
}
