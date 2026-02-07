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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * The type Member expression processor.
 */
public final class MemberExpressionProcessor extends BaseAstProcessor<Swc4jAstMemberExpr> {
    /**
     * Instantiates a new Member expression processor.
     *
     * @param compiler the compiler
     */
    public MemberExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private Swc4jAstClassExpr extractClassExpr(ISwc4jAstExpr callee) {
        if (callee instanceof Swc4jAstClassExpr classExpr) {
            return classExpr;
        }
        if (callee instanceof Swc4jAstParenExpr parenExpr) {
            return extractClassExpr(parenExpr.getExpr());
        }
        return null;
    }

    private String extractSuperPropertyName(
            Swc4jAstSuperPropExpr superPropExpr) throws Swc4jByteCodeCompilerException {
        if (superPropExpr.getProp() instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        if (superPropExpr.getProp() instanceof Swc4jAstComputedPropName computedProp
                && computedProp.getExpr() instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        throw new Swc4jByteCodeCompilerException(
                getSourceCode(),
                superPropExpr,
                "Computed super property expressions not yet supported");
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstMemberExpr memberExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Handle this.field access for instance fields
        if (memberExpr.getObj() instanceof Swc4jAstThisExpr && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            String fieldName = propIdent.getSym();
            String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

            if (currentClassName != null) {
                // Check if we have a captured 'this' (we're inside a lambda)
                var capturedThis = compiler.getMemory().getCompilationContext().getCapturedVariable("this");
                if (capturedThis != null) {
                    // We're in a lambda - need to access field via captured$this
                    // Extract the outer class name from the captured type (e.g., "Lcom/A;" -> "com/A")
                    String outerClassName = capturedThis.type().substring(1, capturedThis.type().length() - 1);

                    // Load captured$this
                    code.aload(0); // load lambda's this
                    int capturedThisRef = cp.addFieldRef(currentClassName, capturedThis.fieldName(), capturedThis.type());
                    code.getfield(capturedThisRef); // Stack: [outer this]

                    // Look up the field in the outer class
                    String outerQualifiedName = outerClassName.replace('/', '.');
                    JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(outerQualifiedName);
                    if (typeInfo == null) {
                        int lastSlash = outerClassName.lastIndexOf('/');
                        String simpleName = lastSlash >= 0 ? outerClassName.substring(lastSlash + 1) : outerClassName;
                        typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                    }

                    if (typeInfo != null) {
                        FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                        if (lookupResult != null) {
                            int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName, fieldName, lookupResult.fieldInfo.descriptor());
                            code.getfield(fieldRef);
                            return;
                        }
                    }
                }

                // Regular this.field access (not in lambda)
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

        // Handle this.#field access for ES2022 private fields
        if (memberExpr.getObj() instanceof Swc4jAstThisExpr && memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
            String fieldName = privateName.getName(); // Name without # prefix
            String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

            if (currentClassName != null) {
                String qualifiedName = currentClassName.replace('/', '.');
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
                if (typeInfo == null) {
                    int lastSlash = currentClassName.lastIndexOf('/');
                    String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                    typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                }

                if (typeInfo != null) {
                    FieldInfo fieldInfo = typeInfo.getField(fieldName);
                    if (fieldInfo != null) {
                        code.aload(0); // load this
                        int fieldRef = cp.addFieldRef(currentClassName, fieldName, fieldInfo.descriptor());
                        code.getfield(fieldRef);
                        return;
                    }
                }
            }
        }

        // Handle ClassName.staticField access
        if (memberExpr.getObj() instanceof Swc4jAstIdent classIdent && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            String className = classIdent.getSym();
            String fieldName = propIdent.getSym();

            // Try to resolve the class
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
            if (typeInfo != null) {
                FieldInfo fieldInfo = typeInfo.getField(fieldName);
                if (fieldInfo != null && fieldInfo.isStatic()) {
                    // Generate getstatic instruction
                    int fieldRef = cp.addFieldRef(typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
                    code.getstatic(fieldRef);
                    return;
                }
            }
        }

        // Handle ClassName.#staticField access for ES2022 static private fields
        if (memberExpr.getObj() instanceof Swc4jAstIdent classIdent && memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
            String className = classIdent.getSym();
            String fieldName = privateName.getName(); // Name without # prefix

            // Try to resolve the class
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
            if (typeInfo != null) {
                FieldInfo fieldInfo = typeInfo.getField(fieldName);
                if (fieldInfo != null && fieldInfo.isStatic()) {
                    // Generate getstatic instruction
                    int fieldRef = cp.addFieldRef(typeInfo.getInternalName(), fieldName, fieldInfo.descriptor());
                    code.getstatic(fieldRef);
                    return;
                }
            }
        }

        // Handle member access on arrays (e.g., arr.length or arr[index])
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        if (TypeConversionUtils.LJAVA_LANG_OBJECT.equals(objType)) {
            String classExprType = inferTypeFromNewClassExpr(memberExpr.getObj());
            if (classExprType != null) {
                objType = classExprType;
            }
        }

        if (objType != null && objType.startsWith(TypeConversionUtils.ARRAY_PREFIX)) {
            // Java array operations
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] - array element access
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [array]
                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [array, index]

                // Convert index to int if needed
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (indexType != null && !TypeConversionUtils.ABBR_INTEGER.equals(indexType)) {
                    TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(indexType), TypeConversionUtils.ABBR_INTEGER);
                }

                // Use appropriate array load instruction based on element type
                String elemType = objType.substring(1); // Remove leading TypeConversionUtils.ARRAY_PREFIX
                switch (elemType) {
                    case TypeConversionUtils.ABBR_BOOLEAN, TypeConversionUtils.ABBR_BYTE ->
                            code.baload(); // boolean and byte
                    case TypeConversionUtils.ABBR_CHARACTER -> code.caload(); // char
                    case TypeConversionUtils.ABBR_SHORT -> code.saload(); // short
                    case TypeConversionUtils.ABBR_INTEGER -> code.iaload(); // int
                    case TypeConversionUtils.ABBR_LONG -> code.laload(); // long
                    case TypeConversionUtils.ABBR_FLOAT -> code.faload(); // float
                    case TypeConversionUtils.ABBR_DOUBLE -> code.daload(); // double
                    default -> {
                        // reference types
                        code.aaload();
                        // Add checkcast for non-Object element types to ensure type safety
                        if (elemType.startsWith("L") && elemType.endsWith(";") &&
                                !elemType.equals(TypeConversionUtils.LJAVA_LANG_OBJECT)) {
                            String elementInternalName = elemType.substring(1, elemType.length() - 1);
                            int classIndex = cp.addClass(elementInternalName);
                            code.checkcast(classIndex);
                        }
                    }
                }
                return;
            }

            // Named property access
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // arr.length - use arraylength instruction
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [array]
                    code.arraylength(); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/ArrayList;".equals(objType) || "Ljava/util/List;".equals(objType)) {
            // ArrayList/List operations
            // Check if it's a computed property (arr[index]) or named property (arr.length)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // arr[index] -> arr.get(index)
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [ArrayList/List, index]

                // Convert index to int if it's a String (for-in returns string indices in JS semantics)
                String indexType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (TypeConversionUtils.LJAVA_LANG_STRING.equals(indexType)) {
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
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [ArrayList/List]
                    int sizeMethod = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
                    code.invokeinterface(sizeMethod, 1); // Stack: [int]
                    return;
                }
            }
        } else if (TypeConversionUtils.LJAVA_LANG_STRING.equals(objType)) {
            // String operations
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String propName = propIdent.getSym();
                if ("length".equals(propName)) {
                    // str.length -> str.length()
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [String]
                    int lengthMethod = cp.addMethodRef("java/lang/String", "length", "()I");
                    code.invokevirtual(lengthMethod); // Stack: [int]
                    return;
                }
            }
        } else if ("Ljava/util/LinkedHashMap;".equals(objType) || TypeConversionUtils.LJAVA_LANG_OBJECT.equals(objType)) {
            // LinkedHashMap operations (object literal member access)
            // Also handle Object type (for nested properties where map values are typed as Object)
            // Check if it's a computed property (obj[key]) or named property (obj.prop)
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // obj[key] -> map.get(key)
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [LinkedHashMap or Object]

                // Cast to LinkedHashMap if type is Object
                if (TypeConversionUtils.LJAVA_LANG_OBJECT.equals(objType)) {
                    int linkedHashMapClass = cp.addClass("java/util/LinkedHashMap");
                    code.checkcast(linkedHashMapClass); // Stack: [LinkedHashMap]
                }

                compiler.getExpressionProcessor().generate(code, classWriter, computedProp.getExpr(), null); // Stack: [LinkedHashMap, key]

                // Box primitive keys if needed
                String keyType = compiler.getTypeResolver().inferTypeFromExpr(computedProp.getExpr());
                if (keyType != null && TypeConversionUtils.isPrimitiveType(keyType)) {
                    String wrapperType = TypeConversionUtils.getWrapperType(keyType);
                    TypeConversionUtils.boxPrimitiveType(code, classWriter, keyType, wrapperType);
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
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null); // Stack: [LinkedHashMap or Object]

                // Cast to LinkedHashMap if type is Object
                if (TypeConversionUtils.LJAVA_LANG_OBJECT.equals(objType)) {
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

        // Handle instance field access for class expression instances
        if (objType != null && objType.startsWith("L") && objType.endsWith(";")
                && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            String fieldName = propIdent.getSym();
            String internalName = objType.substring(1, objType.length() - 1);
            String qualifiedName = internalName.replace('/', '.');

            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            if (typeInfo != null) {
                FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                if (lookupResult != null) {
                    FieldInfo fieldInfo = lookupResult.fieldInfo;
                    compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);
                    int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName, fieldName, fieldInfo.descriptor());
                    code.getfield(fieldRef);
                    return;
                }
            }
        }

        // Handle TemplateStringsArray field access (for raw string support in tagged templates)
        if ("Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;".equals(objType)) {
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String fieldName = propIdent.getSym();
                compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);

                if ("raw".equals(fieldName)) {
                    // Access raw field: String[]
                    int fieldRef = cp.addFieldRef(
                            "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray",
                            "raw",
                            "[Ljava/lang/String;"
                    );
                    code.getfield(fieldRef);
                    return;
                } else if ("length".equals(fieldName)) {
                    // Access length field: int
                    int fieldRef = cp.addFieldRef(
                            "com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray",
                            "length",
                            TypeConversionUtils.ABBR_INTEGER
                    );
                    code.getfield(fieldRef);
                    return;
                }
            }
        }

        // General case: Handle field access on any object type (chained member access)
        // This handles cases like obj.field where obj is any expression returning a custom class type
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
            String fieldName = propIdent.getSym();

            // Generate code to evaluate the object expression
            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);

            // Infer the type of the object expression
            if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                // Extract the class name from the descriptor
                String className = objType.substring(1, objType.length() - 1);
                String qualifiedName = className.replace('/', '.');

                // Try to resolve the class in the type registry
                JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
                if (typeInfo == null) {
                    // Try simple name
                    int lastSlash = className.lastIndexOf('/');
                    String simpleName = lastSlash >= 0 ? className.substring(lastSlash + 1) : className;
                    typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
                }

                if (typeInfo != null) {
                    // Look up field in class hierarchy
                    FieldLookupResult lookupResult = lookupFieldInHierarchy(typeInfo, fieldName);
                    if (lookupResult != null) {
                        // Generate getfield instruction
                        int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName, fieldName, lookupResult.fieldInfo.descriptor());
                        code.getfield(fieldRef);
                        return;
                    }
                }
            }
        }

        // For unsupported member expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "Member expression not yet supported: " + memberExpr.getProp());
    }

    /**
     * Generate bytecode for super property reads (e.g. super.value).
     *
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param superPropExpr  the super property expression
     * @param returnTypeInfo the return type info
     * @throws Swc4jByteCodeCompilerException if generation fails
     */
    public void generateSuperProperty(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstSuperPropExpr superPropExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String fieldName = extractSuperPropertyName(superPropExpr);
        String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassName == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "super property access outside of class context");
        }
        String superClassInternalName = resolveSuperClassInternalName(currentClassName);
        if (superClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Cannot resolve superclass for " + currentClassName);
        }
        JavaTypeInfo superTypeInfo = resolveTypeInfoByInternalName(superClassInternalName);
        if (superTypeInfo == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Cannot resolve superclass type info for " + superClassInternalName);
        }
        FieldLookupResult lookupResult = lookupFieldInHierarchy(superTypeInfo, fieldName);
        if (lookupResult == null) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    superPropExpr,
                    "Field not found in super hierarchy: " + fieldName);
        }
        var cp = classWriter.getConstantPool();
        code.aload(0);
        int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName, fieldName, lookupResult.fieldInfo.descriptor());
        code.getfield(fieldRef);
    }

    private String inferTypeFromNewClassExpr(ISwc4jAstExpr expr) throws Swc4jByteCodeCompilerException {
        if (expr instanceof Swc4jAstNewExpr newExpr) {
            Swc4jAstClassExpr classExpr = extractClassExpr(newExpr.getCallee());
            if (classExpr != null) {
                var info = compiler.getClassExpressionProcessor().prepareClassExpr(classExpr);
                return "L" + info.internalName() + ";";
            }
        }
        return null;
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

    private String resolveSuperClassInternalName(String currentClassInternalName) {
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0
                    ? currentClassInternalName.substring(lastSlash + 1)
                    : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        return superClassInternalName;
    }

    private JavaTypeInfo resolveTypeInfoByInternalName(String internalName) {
        String qualifiedName = internalName.replace('/', '.');
        JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
        if (typeInfo == null) {
            int lastSlash = internalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
            typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        }
        return typeInfo;
    }

    /**
     * Result of a field lookup in the class hierarchy.
     */
    private record FieldLookupResult(FieldInfo fieldInfo, String ownerInternalName) {
    }
}
