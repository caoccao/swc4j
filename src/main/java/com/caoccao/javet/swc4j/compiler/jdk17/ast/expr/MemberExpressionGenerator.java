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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class MemberExpressionGenerator {
    private MemberExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle member access on arrays (e.g., arr.length or arr[index])
        String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

        if (objType != null && objType.startsWith("[")) {
            // Java array operations
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] - array element access
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [array, index]

                // Convert index to int if needed
                String indexType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                if (indexType != null && !"I".equals(indexType)) {
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), "I");
                }

                // Use appropriate array load instruction based on element type
                String elemType = objType.substring(1); // Remove leading "["
                switch (elemType) {
                    case "Z", "B" -> code.baload(); // boolean and byte
                    case "C" -> code.caload(); // char
                    case "S" -> code.saload(); // short
                    case "I" -> code.iaload(); // int
                    case "J" -> code.laload(); // long
                    case "F" -> code.faload(); // float
                    case "D" -> code.daload(); // double
                    default -> code.aaload(); // reference types
                }
                return;
            }

            // Named property access
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length - use arraylength instruction
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [array]
                    code.arraylength(); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/ArrayList;".equals(objType)) {
            // ArrayList operations
            // Check if it's a computed property (arr[index]) or named property (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] -> arr.get(index)
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [ArrayList, index]

                // Call ArrayList.get(int)
                int getMethod = cp.addMethodRef("java/util/ArrayList", "get", "(I)Ljava/lang/Object;");
                code.invokevirtual(getMethod); // Stack: [Object]
                return;
            }

            // Named property access (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length -> arr.size()
                    ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [ArrayList]
                    int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                    code.invokevirtual(sizeMethod); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/LinkedHashMap;".equals(objType) || "Ljava/lang/Object;".equals(objType)) {
            // LinkedHashMap operations (object literal member access)
            // Also handle Object type (for nested properties where map values are typed as Object)
            // Check if it's a computed property (obj[key]) or named property (obj.prop)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // obj[key] -> map.get(key)
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [LinkedHashMap or Object]

                // Cast to LinkedHashMap if type is Object
                if ("Ljava/lang/Object;".equals(objType)) {
                    int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
                    code.checkcast(linkedHashMapClass); // Stack: [LinkedHashMap]
                }

                ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options); // Stack: [LinkedHashMap, key]

                // Box primitive keys if needed
                String keyType = TypeResolver.inferTypeFromExpr(computedProp.getExpr(), context, options);
                if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                    TypeConversionUtils.boxPrimitiveType(code, cp, keyType, wrapperType);
                }

                // Call LinkedHashMap.get(Object)
                int getMethod = cp.addMethodRef("java/util/LinkedHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                code.invokevirtual(getMethod); // Stack: [Object]
                return;
            }

            // Named property access (obj.prop)
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                // obj.prop -> map.get("prop")
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options); // Stack: [LinkedHashMap or Object]

                // Cast to LinkedHashMap if type is Object
                if ("Ljava/lang/Object;".equals(objType)) {
                    int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
                    code.checkcast(linkedHashMapClass); // Stack: [LinkedHashMap]
                }

                int keyIndex = cp.addString(propName);
                code.ldc(keyIndex); // Stack: [LinkedHashMap, "prop"]

                // Call LinkedHashMap.get(Object)
                int getMethod = cp.addMethodRef("java/util/LinkedHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                code.invokevirtual(getMethod); // Stack: [Object]
                return;
            }
        }
        // For unsupported member expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Member expression not yet supported: " + memberExpr.getProp());
    }
}
