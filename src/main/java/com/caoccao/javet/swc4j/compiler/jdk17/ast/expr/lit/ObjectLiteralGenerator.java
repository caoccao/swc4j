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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.StringConcatUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generator for object literal bytecode.
 * Generates LinkedHashMap<String, Object> by default (Phase 1-3: no type annotation).
 * <p>
 * Phase 1: Basic key-value pairs with identifier, string literal, and numeric keys
 * Phase 2: Computed property names {[expr]: value}
 * Phase 3: Property shorthand {x, y} equivalent to {x: x, y: y}
 * <p>
 * JavaScript: {a: 1, b: "hello", c: true, [key]: value, x, y}
 * Java: LinkedHashMap<String, Object> with key-value pairs
 */
public final class ObjectLiteralGenerator {
    private ObjectLiteralGenerator() {
    }

    /**
     * Generate bytecode for object literal.
     * Phase 1-3: Basic implementation with computed property names and shorthand - no type annotation, default to LinkedHashMap<String, Object>
     *
     * @param code           code builder for bytecode generation
     * @param cp             constant pool
     * @param objectLit      object literal AST node
     * @param returnTypeInfo return type information (currently unused, for future type validation)
     * @param context        compilation context
     * @param options        compiler options
     * @param callback       callback for generating nested expressions
     * @throws Swc4jByteCodeCompilerException if generation fails
     */
    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstObjectLit objectLit,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

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
                // Duplicate map reference for put() call
                code.dup(); // Stack: [map, map]

                // Generate key
                generateKey(code, cp, kvProp.getKey(), context, options, callback);
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
            }
            // TODO: Phase 4+ - Handle other property types (Spread, Method, Getter/Setter, etc.)
        }
        // Stack: [map]
        // LinkedHashMap instance is left on stack for return/assignment
    }

    /**
     * Generate bytecode for property key.
     * Phase 1-2: Support IdentName, Str, Number, and Computed keys (all converted to String)
     *
     * @param code     code builder
     * @param cp       constant pool
     * @param key      property key AST node
     * @param context  compilation context
     * @param options  compiler options
     * @param callback callback for generating computed keys
     * @throws Swc4jByteCodeCompilerException if key generation fails
     */
    private static void generateKey(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            ISwc4jAstPropName key,
            CompilationContext context,
            ByteCodeCompilerOptions options,
            StringConcatUtils.ExpressionGeneratorCallback callback) throws Swc4jByteCodeCompilerException {

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
            // Numeric key: {42: value} → "42" (JavaScript coerces to string)
            String keyStr = String.valueOf((int) num.getValue());
            int keyIndex = cp.addString(keyStr);
            code.ldc(keyIndex);
        } else if (key instanceof Swc4jAstComputedPropName computed) {
            // Computed property name: {[expr]: value}
            // Phase 2: Generate expression and convert to String
            ISwc4jAstExpr expr = computed.getExpr();
            String exprType = TypeResolver.inferTypeFromExpr(expr, context, options);
            if (exprType == null) {
                exprType = "Ljava/lang/Object;";
            }

            // Generate the expression
            callback.generateExpr(code, cp, expr, null, context, options);
            // Stack: [expr_result]

            // Convert to String using String.valueOf()
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
        } else {
            throw new Swc4jByteCodeCompilerException(
                    "Unsupported property key type: " + key.getClass().getSimpleName() +
                            ". Supported types: Identifier, String literal, Number, and Computed property names.");
        }
    }
}
