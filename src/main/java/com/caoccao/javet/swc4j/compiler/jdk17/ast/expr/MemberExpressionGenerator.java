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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class MemberExpressionGenerator extends BaseAstProcessor<Swc4jAstMemberExpr> {
    public MemberExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstMemberExpr memberExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Handle this.field access for instance fields
        if (memberExpr.getObj() instanceof Swc4jAstThisExpr && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            String fieldName = propIdent.getSym();
            String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

            if (currentClassName != null) {
                // Look up the field in the class registry - try qualified name first, then simple name
                String qualifiedName = currentClassName.replace('/', '.');
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
                if (typeInfo == null) {
                    // Try simple name
                    int lastSlash = currentClassName.lastIndexOf('/');
                    String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                    typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                }

                if (typeInfo != null) {
                    // Look up field in current class or parent classes
                    FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                    if (lookupResult != null) {
                        // Generate getfield instruction using the owner class
                        code.aload(0); // load this
                        int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName, fieldName, lookupResult.fieldInfo.descriptor());
                        code.getfield(fieldRef);
                        return;
                    }
                }
            }
        }

        // Handle member access on arrays (e.g., arr.length or arr[index])
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

        if (objType != null && objType.startsWith("[")) {
            // Java array operations
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] - array element access
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [array]
                compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [array, index]

                // Convert index to int if needed
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
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
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [array]
                    code.arraylength(); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/ArrayList;".equals(objType) || "Ljava/util/List;".equals(objType)) {
            // ArrayList/List operations
            // Check if it's a computed property (arr[index]) or named property (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] -> arr.get(index)
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [ArrayList/List, index]

                // Convert index to int if it's a String (for-in returns string indices in JS semantics)
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if ("Ljava/lang/String;".equals(indexType)) {
                    // String index -> Integer.parseInt(index)
                    int parseIntMethod = cp.addMethodRef("java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
                    code.invokestatic(parseIntMethod); // Stack: [ArrayList/List, int]
                }

                // Call List.get(int) via interface method
                int getMethod = cp.addInterfaceMethodRef("java/util/List", "get", "(I)Ljava/lang/Object;");
                code.invokeinterface(getMethod, 2); // Stack: [Object]
                return;
            }

            // Named property access (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length -> arr.size()
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                    int sizeMethod = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
                    code.invokeinterface(sizeMethod, 1); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/lang/String;".equals(objType)) {
            // String operations
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // str.length -> str.length()
                    compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [String]
                    int lengthMethod = cp.addMethodRef("java/lang/String", "length", "()I");
                    code.invokevirtual(lengthMethod); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/LinkedHashMap;".equals(objType) || "Ljava/lang/Object;".equals(objType)) {
            // LinkedHashMap operations (object literal member access)
            // Also handle Object type (for nested properties where map values are typed as Object)
            // Check if it's a computed property (obj[key]) or named property (obj.prop)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // obj[key] -> map.get(key)
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap or Object]

                // Cast to LinkedHashMap if type is Object
                if ("Ljava/lang/Object;".equals(objType)) {
                    int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
                    code.checkcast(linkedHashMapClass); // Stack: [LinkedHashMap]
                }

                compiler.getExpressionGenerator().generate(code, cp, computedProp.getExpr(), null); // Stack: [LinkedHashMap, key]

                // Box primitive keys if needed
                String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
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
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null); // Stack: [LinkedHashMap or Object]

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
        throw new Swc4jByteCodeCompilerException(memberExpr, "Member expression not yet supported: " + memberExpr.getProp());
    }

    /**
     * Looks up a field in the class hierarchy, starting from the given class and traversing up to parent classes.
     *
     * @param typeInfo  the starting class type info
     * @param fieldName the field name to look up
     * @return the lookup result containing the field info and owner class, or null if not found
     */
    private FieldLookupResult lookupFieldInHierarchy(JavaTypeInfo typeInfo, String fieldName) {
        // First check in current class
        FieldInfo fieldInfo = typeInfo.getField(fieldName);
        if (fieldInfo != null) {
            return new FieldLookupResult(fieldInfo, typeInfo.getInternalName());
        }

        // Check in parent classes
        for (JavaTypeInfo parentInfo : typeInfo.getParentTypeInfos()) {
            FieldLookupResult result = lookupFieldInHierarchy(parentInfo, fieldName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Result of a field lookup in the class hierarchy.
     */
    private record FieldLookupResult(FieldInfo fieldInfo, String ownerInternalName) {
    }
}
