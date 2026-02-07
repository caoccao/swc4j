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


package com.caoccao.javet.swc4j.compiler.jdk17;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.AstUtils;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Type resolver.
 */
public final class TypeResolver {
    private final ByteCodeCompiler compiler;

    /**
     * Instantiates a new Type resolver.
     *
     * @param compiler the compiler
     */
    public TypeResolver(ByteCodeCompiler compiler) {
        this.compiler = compiler;
    }

    /**
     * Find the common type between two types for conditional expressions.
     * Implements JVM type widening rules.
     * <p>
     * Note: For reference types, we conservatively return Object to ensure
     * compatibility with StackMapTable verification, since the stack map
     * generator cannot track precise reference types.
     *
     * @param type1 first type descriptor
     * @param type2 second type descriptor
     * @return common type that can represent both types
     */
    public static String findCommonType(String type1, String type2) {
        // Handle null types (e.g., null literal)
        if (type1 == null && type2 == null) {
            return null;
        }
        if (type1 == null) {
            // null with any type -> Object (for stackmap compatibility)
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        if (type2 == null) {
            // any type with null -> Object (for stackmap compatibility)
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        // Same type - no conversion needed
        if (type1.equals(type2)) {
            return type1;
        }

        // Both primitives - use numeric widening
        if (TypeConversionUtils.isPrimitiveType(type1) && TypeConversionUtils.isPrimitiveType(type2)) {
            return getWidenedType(type1, type2);
        }

        // One primitive, one reference - result is Object
        // (either boxing the primitive or finding common supertype)
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    private static String getPrimitiveType(String type) {
        return switch (type) {
            case ConstantJavaType.LJAVA_LANG_BOOLEAN -> ConstantJavaType.ABBR_BOOLEAN;
            case ConstantJavaType.LJAVA_LANG_BYTE -> ConstantJavaType.ABBR_BYTE;
            case ConstantJavaType.LJAVA_LANG_SHORT -> ConstantJavaType.ABBR_SHORT;
            case ConstantJavaType.LJAVA_LANG_INTEGER -> ConstantJavaType.ABBR_INTEGER;
            case ConstantJavaType.LJAVA_LANG_LONG -> ConstantJavaType.ABBR_LONG;
            case ConstantJavaType.LJAVA_LANG_FLOAT -> ConstantJavaType.ABBR_FLOAT;
            case ConstantJavaType.LJAVA_LANG_DOUBLE -> ConstantJavaType.ABBR_DOUBLE;
            case ConstantJavaType.LJAVA_LANG_CHARACTER -> ConstantJavaType.ABBR_CHARACTER;
            default -> type;
        };
    }

    /**
     * Gets widened type.
     *
     * @param leftType  the left type
     * @param rightType the right type
     * @return the widened type
     */
    public static String getWidenedType(String leftType, String rightType) {
        // Get primitive types (unwrap if needed)
        String left = getPrimitiveType(leftType);
        String right = getPrimitiveType(rightType);

        // byte, short, char promote to int for operations
        if (ConstantJavaType.ABBR_BYTE.equals(left) || ConstantJavaType.ABBR_SHORT.equals(left) || ConstantJavaType.ABBR_CHARACTER.equals(left)) {
            left = ConstantJavaType.ABBR_INTEGER;
        }
        if (ConstantJavaType.ABBR_BYTE.equals(right) || ConstantJavaType.ABBR_SHORT.equals(right) || ConstantJavaType.ABBR_CHARACTER.equals(right)) {
            right = ConstantJavaType.ABBR_INTEGER;
        }

        // Type widening rules: double > float > long > int
        if (ConstantJavaType.ABBR_DOUBLE.equals(left) || ConstantJavaType.ABBR_DOUBLE.equals(right)) {
            return ConstantJavaType.ABBR_DOUBLE;
        }
        if (ConstantJavaType.ABBR_FLOAT.equals(left) || ConstantJavaType.ABBR_FLOAT.equals(right)) {
            return ConstantJavaType.ABBR_FLOAT;
        }
        if (ConstantJavaType.ABBR_LONG.equals(left) || ConstantJavaType.ABBR_LONG.equals(right)) {
            return ConstantJavaType.ABBR_LONG;
        }
        return ConstantJavaType.ABBR_INTEGER;
    }

    /**
     * Get the wrapper type for a primitive type.
     *
     * @param primitiveType primitive type descriptor (e.g., "I", "D")
     * @return wrapper type descriptor (e.g., "Ljava/lang/Integer;", "Ljava/lang/Double;")
     */
    private static String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_BOOLEAN -> ConstantJavaType.LJAVA_LANG_BOOLEAN;
            case ConstantJavaType.ABBR_BYTE -> ConstantJavaType.LJAVA_LANG_BYTE;
            case ConstantJavaType.ABBR_CHARACTER -> ConstantJavaType.LJAVA_LANG_CHARACTER;
            case ConstantJavaType.ABBR_SHORT -> ConstantJavaType.LJAVA_LANG_SHORT;
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_LANG_INTEGER;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_LANG_LONG;
            case ConstantJavaType.ABBR_FLOAT -> ConstantJavaType.LJAVA_LANG_FLOAT;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_LANG_DOUBLE;
            default -> null;
        };
    }

    /**
     * Check if a value of fromType can be assigned to a variable of toType.
     * <p>
     * Handles:
     * - Exact type matches
     * - Primitive-to-wrapper boxing (int → Integer, double → Double, etc.)
     * - Wrapper-to-primitive unboxing (Integer → int, Double → double, etc.)
     * - Widening primitive conversions (int → long, int → double, float → double, etc.)
     * - Object hierarchy (String → Object, Integer → Number → Object)
     * - Narrowing conversions are REJECTED (long → int, double → int)
     *
     * @param fromType JVM type descriptor of source value (e.g., "I", "Ljava/lang/String;")
     * @param toType   JVM type descriptor of target type (e.g., "Ljava/lang/Integer;", "Ljava/lang/Object;")
     * @return true if fromType is assignable to toType
     */
    public static boolean isAssignable(String fromType, String toType) {
        if (fromType == null || toType == null) {
            return false;
        }

        // Exact match
        if (fromType.equals(toType)) {
            return true;
        }

        // Handle primitive to wrapper boxing
        if (TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            String wrapperType = getWrapperType(fromType);
            if (wrapperType.equals(toType)) {
                return true; // Direct boxing: int → Integer
            }
            // After boxing, check object hierarchy (e.g., int → Integer → Number)
            return isObjectAssignable(wrapperType, toType);
        }

        // Handle wrapper to primitive unboxing
        if (!TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            String primitiveType = getPrimitiveType(fromType);
            // getPrimitiveType returns the primitive type for wrappers, or the type unchanged for non-wrappers
            // Check if it's actually a primitive after conversion
            if (TypeConversionUtils.isPrimitiveType(primitiveType)) {
                if (primitiveType.equals(toType)) {
                    return true; // Direct unboxing: Integer → int
                }
                // Wrapper to different primitive requires unboxing + widening
                return isPrimitiveWidening(primitiveType, toType);
            }
            // fromType is not a wrapper type, so cannot unbox to primitive
            return false;
        }

        // Handle widening primitive conversions
        if (TypeConversionUtils.isPrimitiveType(fromType) && TypeConversionUtils.isPrimitiveType(toType)) {
            return isPrimitiveWidening(fromType, toType);
        }

        // Handle object hierarchy (both are reference types)
        if (!TypeConversionUtils.isPrimitiveType(fromType) && !TypeConversionUtils.isPrimitiveType(toType)) {
            return isObjectAssignable(fromType, toType);
        }

        return false;
    }

    /**
     * Check if an object type can be assigned to another object type.
     * <p>
     * Handles:
     * - Object hierarchy (String → Object, Integer → Number → Object)
     * - Wrapper class hierarchy (Integer/Long/Short/Byte → Number → Object)
     * - Any object → Object
     *
     * @param fromType object type descriptor (e.g., "Ljava/lang/String;")
     * @param toType   object type descriptor (e.g., "Ljava/lang/Object;")
     * @return true if fromType is assignable to toType
     */
    private static boolean isObjectAssignable(String fromType, String toType) {
        // Exact match
        if (fromType.equals(toType)) {
            return true;
        }

        // Everything is assignable to Object
        if (toType.equals(ConstantJavaType.LJAVA_LANG_OBJECT)) {
            return true;
        }

        // Number wrapper hierarchy: Integer/Long/Short/Byte/Float/Double → Number
        if (toType.equals(ConstantJavaType.LJAVA_LANG_NUMBER)) {
            return fromType.equals(ConstantJavaType.LJAVA_LANG_INTEGER) ||
                    fromType.equals(ConstantJavaType.LJAVA_LANG_LONG) ||
                    fromType.equals(ConstantJavaType.LJAVA_LANG_SHORT) ||
                    fromType.equals(ConstantJavaType.LJAVA_LANG_BYTE) ||
                    fromType.equals(ConstantJavaType.LJAVA_LANG_FLOAT) ||
                    fromType.equals(ConstantJavaType.LJAVA_LANG_DOUBLE);
        }

        // List interface hierarchy: ArrayList/LinkedList → List
        if (toType.equals(ConstantJavaType.LJAVA_UTIL_LIST)) {
            return fromType.equals(ConstantJavaType.LJAVA_UTIL_ARRAYLIST) ||
                    fromType.equals(ConstantJavaType.LJAVA_UTIL_LINKEDLIST);
        }

        // Map interface hierarchy: LinkedHashMap/HashMap → Map
        if (toType.equals(ConstantJavaType.LJAVA_UTIL_MAP)) {
            return fromType.equals(ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP) ||
                    fromType.equals(ConstantJavaType.LJAVA_UTIL_HASHMAP);
        }

        // For other object types, we'd need full class hierarchy information
        // For now, we only support the common cases above
        return false;
    }

    /**
     * Check if a primitive type can be widened to another primitive type.
     * <p>
     * Widening conversions allowed:
     * - byte → short, int, long, float, double
     * - short → int, long, float, double
     * - char → int, long, float, double
     * - int → long, float, double
     * - long → float, double
     * - float → double
     * <p>
     * Narrowing conversions are NOT allowed (e.g., long → int, double → float)
     *
     * @param fromPrimitive primitive type descriptor (e.g., "I", "D")
     * @param toPrimitive   primitive type descriptor (e.g., "J", "D")
     * @return true if widening conversion is allowed
     */
    private static boolean isPrimitiveWidening(String fromPrimitive, String toPrimitive) {
        // Same primitive type
        if (fromPrimitive.equals(toPrimitive)) {
            return true;
        }

        // Widening conversions matrix
        return switch (fromPrimitive) {
            case ConstantJavaType.ABBR_BYTE -> // byte
                    toPrimitive.equals(ConstantJavaType.ABBR_SHORT) || toPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || toPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                            toPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            case ConstantJavaType.ABBR_SHORT -> // short
                    toPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || toPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                            toPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            case ConstantJavaType.ABBR_CHARACTER -> // char
                    toPrimitive.equals(ConstantJavaType.ABBR_INTEGER) || toPrimitive.equals(ConstantJavaType.ABBR_LONG) ||
                            toPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            case ConstantJavaType.ABBR_INTEGER -> // int
                    toPrimitive.equals(ConstantJavaType.ABBR_LONG) || toPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            case ConstantJavaType.ABBR_LONG -> // long
                    toPrimitive.equals(ConstantJavaType.ABBR_FLOAT) || toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            case ConstantJavaType.ABBR_FLOAT -> // float
                    toPrimitive.equals(ConstantJavaType.ABBR_DOUBLE);
            default -> false;
        };
    }

    /**
     * Analyze return type return type info.
     *
     * @param function the function
     * @param body     the body
     * @return the return type info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public ReturnTypeInfo analyzeReturnType(
            Swc4jAstFunction function,
            Swc4jAstBlockStmt body) throws Swc4jByteCodeCompilerException {
        // Check for explicit return type annotation first
        if (function != null) {
            var returnTypeOpt = function.getReturnType();
            if (returnTypeOpt.isPresent()) {
                return analyzeReturnTypeFromAnnotation(returnTypeOpt.get());
            }
        }

        // If no body (e.g., abstract method), default to void
        if (body == null) {
            return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
        }

        // Fall back to type inference from return statements (including nested ones)
        String returnType = inferReturnTypeFromBlock(body);
        if (returnType != null) {
            // If type is ConstantJavaType.ABBR_VOID (void), return void type info
            if (ConstantJavaType.ABBR_VOID.equals(returnType)) {
                return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
            }
            // Otherwise, create ReturnTypeInfo from the inferred type
            return createReturnTypeInfoFromDescriptor(returnType);
        }

        // No return statements found - default to void
        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    /**
     * Analyzes a type annotation and returns the corresponding ReturnTypeInfo.
     *
     * @param typeAnn the TypeScript type annotation
     * @return ReturnTypeInfo based on the type annotation
     * @throws Swc4jByteCodeCompilerException if type analysis fails
     */
    public ReturnTypeInfo analyzeReturnTypeFromAnnotation(Swc4jAstTsTypeAnn typeAnn)
            throws Swc4jByteCodeCompilerException {
        var tsType = typeAnn.getTypeAnn();
        String descriptor = mapTsTypeToDescriptor(tsType);
        return ReturnTypeInfo.of(getSourceCode(), tsType, descriptor);
    }

    /**
     * Creates a ReturnTypeInfo from a JVM type descriptor.
     *
     * @param descriptor the JVM type descriptor (e.g., "I", "Ljava/lang/String;")
     * @return ReturnTypeInfo based on the descriptor
     */
    public ReturnTypeInfo createReturnTypeInfoFromDescriptor(String descriptor) {
        if (descriptor == null) {
            return new ReturnTypeInfo(ReturnType.OBJECT, 0, ConstantJavaType.LJAVA_LANG_OBJECT, null);
        }
        return switch (descriptor) {
            case ConstantJavaType.ABBR_VOID -> new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
            case ConstantJavaType.ABBR_BOOLEAN -> new ReturnTypeInfo(ReturnType.BOOLEAN, 0, null, null);
            case ConstantJavaType.ABBR_BYTE -> new ReturnTypeInfo(ReturnType.BYTE, 0, null, null);
            case ConstantJavaType.ABBR_CHARACTER -> new ReturnTypeInfo(ReturnType.CHAR, 0, null, null);
            case ConstantJavaType.ABBR_SHORT -> new ReturnTypeInfo(ReturnType.SHORT, 0, null, null);
            case ConstantJavaType.ABBR_INTEGER -> new ReturnTypeInfo(ReturnType.INT, 0, null, null);
            case ConstantJavaType.ABBR_LONG -> new ReturnTypeInfo(ReturnType.LONG, 0, null, null);
            case ConstantJavaType.ABBR_FLOAT -> new ReturnTypeInfo(ReturnType.FLOAT, 0, null, null);
            case ConstantJavaType.ABBR_DOUBLE -> new ReturnTypeInfo(ReturnType.DOUBLE, 0, null, null);
            case ConstantJavaType.LJAVA_LANG_STRING -> new ReturnTypeInfo(ReturnType.STRING, 0, descriptor, null);
            default -> new ReturnTypeInfo(ReturnType.OBJECT, 0, descriptor, null);
        };
    }

    /**
     * Extracts an arrow expression from a potentially parenthesized expression (IIFE pattern).
     *
     * @param expr the expression to check
     * @return the arrow expression if found, null otherwise
     */
    private Swc4jAstArrowExpr extractArrowFromIIFE(ISwc4jAstExpr expr) {
        if (expr instanceof Swc4jAstArrowExpr arrow) {
            return arrow;
        }
        if (expr instanceof Swc4jAstParenExpr parenExpr) {
            return extractArrowFromIIFE(parenExpr.getExpr());
        }
        return null;
    }

    /**
     * Extract default value expression from a parameter pattern.
     *
     * @param pat the parameter pattern
     * @return the default value expression, or null if not a default parameter
     */
    public ISwc4jAstExpr extractDefaultValue(ISwc4jAstPat pat) {
        if (pat instanceof Swc4jAstAssignPat assignPat) {
            return assignPat.getRight();
        }
        return null;
    }

    /**
     * Extract GenericTypeInfo from a BindingIdent's type annotation if it's a Record type.
     * Phase 2: Support for {@code Record<K, V>} type validation
     *
     * @param bindingIdent binding identifier with potential type annotation
     * @return GenericTypeInfo if the type annotation is a Record type, null otherwise
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public GenericTypeInfo extractGenericTypeInfo(
            Swc4jAstBindingIdent bindingIdent) throws Swc4jByteCodeCompilerException {
        var typeAnn = bindingIdent.getTypeAnn();
        if (typeAnn.isEmpty()) {
            return null;
        }

        ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();

        // Check if it's a Record type by calling parseRecordType
        // parseRecordType returns null if it's not a Record type
        return parseRecordType(tsType);
    }

    /**
     * Extract member expr prop name string.
     *
     * @param memberExpr the member expr
     * @return the string
     */
    public String extractMemberExprPropName(Swc4jAstMemberExpr memberExpr) {
        if (memberExpr.getProp() instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        if (memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
            return privateName.getName();
        }
        if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
            if (computedProp.getExpr() instanceof Swc4jAstStr str) {
                return str.getValue();
            }
        }
        return null;
    }

    /**
     * Extracts the parameter name from a pattern.
     *
     * @param pat the pattern
     * @return the parameter name, or null if not found
     */
    public String extractParameterName(ISwc4jAstPat pat) {
        if (pat instanceof Swc4jAstRestPat restPat) {
            ISwc4jAstPat arg = restPat.getArg();
            if (arg instanceof Swc4jAstBindingIdent bindingIdent) {
                return bindingIdent.getId().getSym();
            }
        } else if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
            return bindingIdent.getId().getSym();
        } else if (pat instanceof Swc4jAstAssignPat assignPat) {
            // Default parameter - extract name from left side
            return extractParameterName(assignPat.getLeft());
        }
        return null;
    }

    /**
     * Extract type from parameter pattern (regular param or varargs).
     *
     * @param pat the pat
     * @return the string
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String extractParameterType(
            ISwc4jAstPat pat) throws Swc4jByteCodeCompilerException {
        if (pat instanceof Swc4jAstRestPat restPat) {
            // Varargs parameter - extract type from RestPat's type annotation
            var typeAnn = restPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
                // RestPat type annotation is already an array type (int[])
                // We need to map it to the corresponding JVM array descriptor
                return mapTsTypeToDescriptor(tsType);
            }
            // Default to Object[] for untyped varargs
            return ConstantJavaType.ARRAY_LJAVA_LANG_OBJECT;
        } else if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
            // Regular parameter - extract type from type annotation
            var typeAnn = bindingIdent.getTypeAnn();
            if (typeAnn.isPresent()) {
                ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
                return mapTsTypeToDescriptor(tsType);
            }
            // Default to Object for untyped parameters
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (pat instanceof Swc4jAstAssignPat assignPat) {
            // Default parameter - extract type from left side
            return extractParameterType(assignPat.getLeft());
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Extract super property name string.
     *
     * @param superPropExpr the super property expression
     * @return the property name string, or null if dynamic
     */
    public String extractSuperPropExprPropName(Swc4jAstSuperPropExpr superPropExpr) {
        if (superPropExpr.getProp() instanceof Swc4jAstIdentName identName) {
            return identName.getSym();
        }
        if (superPropExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
            if (computedProp.getExpr() instanceof Swc4jAstStr str) {
                return str.getValue();
            }
        }
        return null;
    }

    /**
     * Extract type string.
     *
     * @param bindingIdent the binding ident
     * @param init         the init
     * @return the string
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String extractType(
            Swc4jAstBindingIdent bindingIdent,
            Optional<ISwc4jAstExpr> init) throws Swc4jByteCodeCompilerException {
        // Check for explicit type annotation
        var typeAnn = bindingIdent.getTypeAnn();
        if (typeAnn.isPresent()) {
            ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
            return mapTsTypeToDescriptor(tsType);
        }

        if (init.isPresent() && init.get() instanceof Swc4jAstNewExpr newExpr) {
            ISwc4jAstExpr callee = newExpr.getCallee();
            Swc4jAstClassExpr classExpr = null;
            if (callee instanceof Swc4jAstClassExpr directClassExpr) {
                classExpr = directClassExpr;
            } else if (callee instanceof Swc4jAstParenExpr parenExpr && parenExpr.getExpr() instanceof Swc4jAstClassExpr parenClassExpr) {
                classExpr = parenClassExpr;
            }
            if (classExpr != null) {
                var info = compiler.getClassExpressionProcessor().prepareClassExpr(classExpr);
                return "L" + info.internalName() + ";";
            }
        }

        // Type inference from initializer
        if (init.isPresent()) {
            String type = inferTypeFromExpr(init.get());
            // If type is null (e.g., for null literal), default to Object
            return type != null ? type : ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        return ConstantJavaType.LJAVA_LANG_OBJECT; // Default
    }

    /**
     * Recursively finds return type in a statement.
     *
     * @param stmt the statement to analyze
     * @return the return type descriptor, or null if no return found
     */
    private String findReturnTypeInStmt(ISwc4jAstStmt stmt)
            throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
            if (returnStmt.getArg().isPresent()) {
                String type = inferTypeFromExpr(returnStmt.getArg().get());
                // If inferTypeFromExpr returns null (e.g., for null literals),
                // default to Object type since null is compatible with any reference type
                if (type == null) {
                    return ConstantJavaType.LJAVA_LANG_OBJECT;
                }
                return type;
            }
            return ConstantJavaType.ABBR_VOID; // void return
        } else if (stmt instanceof Swc4jAstBlockStmt innerBlock) {
            return inferReturnTypeFromBlock(innerBlock);
        } else if (stmt instanceof Swc4jAstIfStmt ifStmt) {
            // For if statements, we need to find the common type from both branches
            String consType = null;
            String altType = null;

            // Check consequent - handle both block and single statement
            ISwc4jAstStmt cons = ifStmt.getCons();
            if (cons instanceof Swc4jAstBlockStmt consBlock) {
                consType = inferReturnTypeFromBlock(consBlock);
            } else {
                consType = findReturnTypeInStmt(cons);
            }

            // Check alternate if present - handle both block and single statement
            if (ifStmt.getAlt().isPresent()) {
                ISwc4jAstStmt alt = ifStmt.getAlt().get();
                if (alt instanceof Swc4jAstBlockStmt altBlock) {
                    altType = inferReturnTypeFromBlock(altBlock);
                } else {
                    altType = findReturnTypeInStmt(alt);
                }
            }

            // If both branches have returns, find their common type
            if (consType != null && altType != null) {
                return findCommonType(consType, altType);
            }

            // If only one branch has a return, use that type
            if (consType != null) {
                return consType;
            }
            return altType;
        }
        return null;
    }

    /**
     * Generate a custom functional interface for a TsFnType.
     * The interface has a single abstract method named "call".
     *
     * @param fnType the function type AST node
     * @return the descriptor for the generated interface (e.g., "Lcom/A/$Fn$1;")
     */
    private String generateFunctionalInterface(Swc4jAstTsFnType fnType) throws Swc4jByteCodeCompilerException {
        // Generate interface name
        String currentClass = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        int counter = compiler.getMemory().getNextFnInterfaceCounter();
        String interfaceName;
        if (currentClass != null) {
            interfaceName = currentClass + "/$Fn$" + counter;
        } else {
            interfaceName = "$Fn$" + counter;
        }

        // Build parameter types
        List<String> paramTypes = new ArrayList<>();
        for (ISwc4jAstTsFnParam param : fnType.getParams()) {
            // ISwc4jAstTsFnParam implementations (BindingIdent, ArrayPat, etc.) also implement ISwc4jAstPat
            if (param instanceof ISwc4jAstPat pat) {
                paramTypes.add(extractParameterType(pat));
            } else {
                paramTypes.add(ConstantJavaType.LJAVA_LANG_OBJECT);
            }
        }

        // Get return type
        String returnType = mapTsTypeToDescriptor(fnType.getTypeAnn().getTypeAnn());

        // Build method descriptor
        StringBuilder descriptor = new StringBuilder("(");
        for (String paramType : paramTypes) {
            descriptor.append(paramType);
        }
        descriptor.append(")").append(returnType);

        // Generate interface bytecode
        try {
            ClassWriter classWriter = new ClassWriter(interfaceName, ConstantJavaType.JAVA_LANG_OBJECT);
            classWriter.setAccessFlags(0x0601);  // ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT
            classWriter.addAbstractMethod("call", descriptor.toString());
            byte[] bytecode = classWriter.toByteArray();
            compiler.getMemory().getByteCodeMap().put(interfaceName.replace('/', '.'), bytecode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate functional interface", e);
        }

        // Register the interface so ArrowExpressionProcessor can look up parameter types
        compiler.getMemory().getScopedFunctionalInterfaceRegistry().register(
                interfaceName,
                "call",
                paramTypes,
                returnType
        );

        return "L" + interfaceName + ";";
    }

    private String getSourceCode() {
        return compiler.getMemory().getScopedSourceCode().getSourceCode();
    }

    private Integer getTupleLiteralIndex(ISwc4jAstTsType indexType) {
        if (indexType instanceof Swc4jAstTsLitType litType && litType.getLit() instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value >= 0
                    && value == Math.floor(value)
                    && !Double.isInfinite(value)
                    && !Double.isNaN(value)
                    && value <= Integer.MAX_VALUE) {
                return (int) value;
            }
        }
        return null;
    }

    /**
     * Check if a parameter has a default value.
     *
     * @param pat the parameter pattern
     * @return true if the parameter has a default value
     */
    public boolean hasDefaultValue(ISwc4jAstPat pat) {
        return pat instanceof Swc4jAstAssignPat;
    }

    /**
     * Infer the element type of an array literal.
     *
     * @param arrayLit array literal
     * @return element type descriptor, or Object if unknown
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String inferArrayElementType(Swc4jAstArrayLit arrayLit) throws Swc4jByteCodeCompilerException {
        if (arrayLit == null) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        String elementType = null;
        for (var elemOpt : arrayLit.getElems()) {
            if (!elemOpt.isPresent()) {
                continue;
            }
            var elem = elemOpt.get();
            if (elem.getSpread().isPresent()) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            ISwc4jAstExpr elemExpr = elem.getExpr();
            String exprType = inferTypeFromExpr(elemExpr);
            if (exprType == null) {
                continue;
            }
            if (elementType == null) {
                elementType = exprType;
            } else {
                elementType = findCommonType(elementType, exprType);
            }
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(elementType)) {
                return elementType;
            }
        }
        return elementType != null ? elementType : ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Infer the JVM type descriptor for an object literal property key.
     * <p>
     * Handles different property name types:
     * - IdentName → String (identifier keys are always strings)
     * - Str → String (string literal keys)
     * - Number → Integer, Long, or Double based on the actual numeric value
     * - BigInt → Long or BigInteger based on size
     * - ComputedPropName → Inferred from the computed expression
     *
     * @param key Property name AST node
     * @return JVM type descriptor (e.g., "Ljava/lang/String;", "Ljava/lang/Integer;", "D")
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String inferKeyType(
            ISwc4jAstPropName key) throws Swc4jByteCodeCompilerException {
        if (key == null) {
            return ConstantJavaType.LJAVA_LANG_STRING; // Default to String
        }

        // IdentName keys are always strings
        if (key instanceof Swc4jAstIdentName) {
            return ConstantJavaType.LJAVA_LANG_STRING;
        }

        // String literal keys
        if (key instanceof Swc4jAstStr) {
            return ConstantJavaType.LJAVA_LANG_STRING;
        }

        // Numeric keys - infer specific numeric type
        if (key instanceof Swc4jAstNumber number) {
            double value = number.getValue();

            // Check if it's a whole number (no decimal part)
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                // It's an integer value
                long longValue = (long) value;

                // Check if it fits in an int
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    return ConstantJavaType.LJAVA_LANG_INTEGER;
                }

                // It needs a long
                return ConstantJavaType.LJAVA_LANG_LONG;
            }

            // It has a decimal part - use Double
            return ConstantJavaType.LJAVA_LANG_DOUBLE;
        }

        // BigInt keys - always Long or BigInteger
        if (key instanceof Swc4jAstBigInt bigInt) {
            // For now, default to Long
            // In the future, could parse the BigInt value and choose BigInteger if needed
            return ConstantJavaType.LJAVA_LANG_LONG;
        }

        // Computed property names - infer from expression
        if (key instanceof Swc4jAstComputedPropName computed) {
            ISwc4jAstExpr expr = computed.getExpr();
            String inferredType = inferTypeFromExpr(expr);
            return inferredType != null ? inferredType : ConstantJavaType.LJAVA_LANG_STRING;
        }

        // Default to String for any unknown key type
        return ConstantJavaType.LJAVA_LANG_STRING;
    }

    /**
     * Infers the return type from a block statement by analyzing return statements.
     *
     * @param blockStmt the block statement to analyze
     * @return the inferred return type descriptor, or null if no return found
     */
    private String inferReturnTypeFromBlock(Swc4jAstBlockStmt blockStmt)
            throws Swc4jByteCodeCompilerException {
        for (var stmt : blockStmt.getStmts()) {
            String returnType = findReturnTypeInStmt(stmt);
            if (returnType != null) {
                return returnType;
            }
        }
        return null;
    }

    /**
     * Infer type from expr string.
     *
     * @param expr the expr
     * @return the string
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String inferTypeFromExpr(
            ISwc4jAstExpr expr) throws Swc4jByteCodeCompilerException {
        CompilationContext context = compiler.getMemory().getCompilationContext();
        if (expr instanceof Swc4jAstCondExpr condExpr) {
            // Conditional expression: infer type from both branches and find common type
            String consType = inferTypeFromExpr(condExpr.getCons());
            String altType = inferTypeFromExpr(condExpr.getAlt());
            return findCommonType(consType, altType);
        } else if (expr instanceof Swc4jAstTsAsExpr asExpr) {
            // Explicit type cast - return the cast target type
            var tsType = asExpr.getTypeAnn();
            if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
                ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
                if (entityName instanceof Swc4jAstIdent ident) {
                    String typeName = ident.getSym();
                    return mapTypeNameToDescriptor(typeName);
                }
            }
            // If we can't determine the cast type, fall back to inferring from the expression
            return inferTypeFromExpr(asExpr.getExpr());
        } else if (expr instanceof Swc4jAstTsTypeAssertion typeAssertion) {
            return mapTsTypeToDescriptor(typeAssertion.getTypeAnn());
        } else if (expr instanceof Swc4jAstTsSatisfiesExpr satisfiesExpr) {
            return mapTsTypeToDescriptor(satisfiesExpr.getTypeAnn());
        } else if (expr instanceof Swc4jAstTsNonNullExpr nonNullExpr) {
            return inferTypeFromExpr(nonNullExpr.getExpr());
        } else if (expr instanceof Swc4jAstTsConstAssertion constAssertion) {
            return inferTypeFromExpr(constAssertion.getExpr());
        } else if (expr instanceof Swc4jAstTsInstantiation instantiation) {
            return inferTypeFromExpr(instantiation.getExpr());
        } else if (expr instanceof Swc4jAstNewExpr newExpr) {
            // Constructor call - infer type from the callee (class name)
            ISwc4jAstExpr callee = newExpr.getCallee();
            if (callee instanceof Swc4jAstClassExpr classExpr) {
                var info = compiler.getClassExpressionProcessor().prepareClassExpr(classExpr);
                return "L" + info.internalName() + ";";
            }
            if (callee instanceof Swc4jAstParenExpr parenExpr && parenExpr.getExpr() instanceof Swc4jAstClassExpr classExpr) {
                var info = compiler.getClassExpressionProcessor().prepareClassExpr(classExpr);
                return "L" + info.internalName() + ";";
            }
            if (callee instanceof Swc4jAstIdent ident) {
                String className = ident.getSym();
                // Resolve the class name using type alias registry
                String resolvedType = compiler.getMemory().getScopedTypeAliasRegistry().resolve(className);
                if (resolvedType != null) {
                    // Convert qualified name to descriptor: com.example.Foo -> Lcom/example/Foo;
                    return "L" + resolvedType.replace('.', '/') + ";";
                }
                // If not found in type alias registry, assume it's a class in the current package
                return mapTypeNameToDescriptor(className);
            }
            // If we can't determine the constructor type, return Object
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                return ConstantJavaType.ABBR_INTEGER;
            }
            return ConstantJavaType.ABBR_DOUBLE;
        } else if (expr instanceof Swc4jAstBool) {
            return ConstantJavaType.ABBR_BOOLEAN;
        } else if (expr instanceof Swc4jAstBigInt) {
            return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
        } else if (expr instanceof Swc4jAstNull) {
            // null has no specific type - it's compatible with any reference type
            // Return null to indicate that the type should be determined by context
            return null;
        } else if (expr instanceof Swc4jAstThisExpr) {
            // 'this' refers to the current class instance
            String currentClass = context.getCurrentClassInternalName();
            if (currentClass != null) {
                return "L" + currentClass + ";";
            }
            // Fallback to Object if no current class is set
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstSuperPropExpr superPropExpr) {
            String propName = extractSuperPropExprPropName(superPropExpr);
            if (propName == null) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            String currentClassName = context.getCurrentClassInternalName();
            if (currentClassName == null) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            String superClassInternalName = resolveSuperClassInternalName(currentClassName);
            if (superClassInternalName == null) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            JavaTypeInfo superTypeInfo = resolveTypeInfoByInternalName(superClassInternalName);
            if (superTypeInfo == null) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            FieldInfo fieldInfo = lookupFieldInHierarchy(superTypeInfo, propName);
            if (fieldInfo != null) {
                return fieldInfo.descriptor();
            }
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstArrayLit) {
            // Array literal - maps to ArrayList
            return ConstantJavaType.LJAVA_UTIL_ARRAYLIST;
        } else if (expr instanceof Swc4jAstObjectLit) {
            // Object literal - maps to LinkedHashMap
            return ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
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
                        FieldInfo fieldInfo = lookupFieldInHierarchy(typeInfo, fieldName);
                        if (fieldInfo != null) {
                            return fieldInfo.descriptor();
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
                            return fieldInfo.descriptor();
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
                        return fieldInfo.descriptor();
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
                        return fieldInfo.descriptor();
                    }
                }
            }

            // Member expression - handle array-like properties
            String objType = inferTypeFromExpr(memberExpr.getObj());

            if (objType != null && objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                    // arr[index] returns the element type
                    return objType.substring(1); // Remove leading "["
                }
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        return ConstantJavaType.ABBR_INTEGER; // arr.length returns int
                    }
                }
            } else if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType)) {
                // ArrayList operations
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        return ConstantJavaType.ABBR_INTEGER; // arr.length returns int
                    }
                }
            } else if (ConstantJavaType.LJAVA_LANG_STRING.equals(objType)) {
                // String operations
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        return ConstantJavaType.ABBR_INTEGER; // str.length returns int
                    }
                }
            } else if (ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP.equals(objType)) {
                // LinkedHashMap operations (object literal member access)
                // map.get() returns Object
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            } else if ("Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;".equals(objType)) {
                // TemplateStringsArray operations (for raw string access in tagged templates)
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("raw".equals(propName)) {
                        return ConstantJavaType.ARRAY_LJAVA_LANG_STRING; // raw field is String[]
                    } else if (ConstantJavaMethod.METHOD_LENGTH.equals(propName)) {
                        return ConstantJavaType.ABBR_INTEGER; // length field is int
                    }
                }
            }

            // General case: Handle field access on any custom class type (chained member access)
            // This handles cases like obj.field where obj is any expression returning a custom class type
            if (objType != null && objType.startsWith("L") && objType.endsWith(";") && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                String fieldName = propIdent.getSym();

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
                    FieldInfo fieldInfo = lookupFieldInHierarchy(typeInfo, fieldName);
                    if (fieldInfo != null) {
                        return fieldInfo.descriptor();
                    }
                }
            }

            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstStr) {
            return ConstantJavaType.LJAVA_LANG_STRING;
        } else if (expr instanceof Swc4jAstTpl) {
            // Template literal always returns String
            return ConstantJavaType.LJAVA_LANG_STRING;
        } else if (expr instanceof Swc4jAstTaggedTpl) {
            // Tagged template - return type depends on the tag function
            // For now, return null to let method resolution handle it
            return null;
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            // Assignment expression result is the type of the left operand (the variable being assigned to)
            // because the right operand is converted to the left operand's type before storing
            var left = assignExpr.getLeft();
            if (left instanceof Swc4jAstBindingIdent bindingIdent) {
                String varName = bindingIdent.getId().getSym();
                return context.getInferredType(varName, ConstantJavaType.LJAVA_LANG_OBJECT);
            }
            // For other left-hand side types (member expressions, etc.), fall back to right operand type
            return inferTypeFromExpr(assignExpr.getRight());
        } else if (expr instanceof Swc4jAstIdent ident) {
            return context.getInferredType(ident.getSym(), ConstantJavaType.LJAVA_LANG_OBJECT);
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            // Update expression (++/--) returns the type of the operand
            return inferTypeFromExpr(updateExpr.getArg());
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            switch (binExpr.getOp()) {
                case Add -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // String concatenation - if either operand is String, result is String
                    if (ConstantJavaType.LJAVA_LANG_STRING.equals(leftType) || ConstantJavaType.LJAVA_LANG_STRING.equals(rightType)) {
                        return ConstantJavaType.LJAVA_LANG_STRING;
                    }
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Numeric addition - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Sub -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Numeric subtraction - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Mul -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Numeric multiplication - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Div -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Numeric division - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Mod -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Numeric modulo - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Exp -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    // BigInteger exponentiation - if base is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Exponentiation always returns double (Math.pow returns double)
                    return ConstantJavaType.ABBR_DOUBLE;
                }
                case LShift -> {
                    // Left shift returns the type of the left operand (int or long)
                    // For JavaScript semantics, we convert to int (ToInt32), but JVM supports both
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger shift - if left is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if (ConstantJavaType.ABBR_LONG.equals(primitiveType)) {
                        return ConstantJavaType.ABBR_LONG;
                    }
                    return ConstantJavaType.ABBR_INTEGER;
                }
                case RShift -> {
                    // Right shift returns the type of the left operand (int or long)
                    // Same semantics as left shift
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger shift - if left is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if (ConstantJavaType.ABBR_LONG.equals(primitiveType)) {
                        return ConstantJavaType.ABBR_LONG;
                    }
                    return ConstantJavaType.ABBR_INTEGER;
                }
                case ZeroFillRShift -> {
                    // Zero-fill right shift returns the type of the left operand (int or long)
                    // Same semantics as other shift operations
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger shift - if left is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if (ConstantJavaType.ABBR_LONG.equals(primitiveType)) {
                        return ConstantJavaType.ABBR_LONG;
                    }
                    return ConstantJavaType.ABBR_INTEGER;
                }
                case BitAnd -> {
                    // Bitwise AND uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Use type widening rules (int & long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case BitOr -> {
                    // Bitwise OR uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Use type widening rules (int | long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case BitXor -> {
                    // Bitwise XOR uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    if (rightType == null) rightType = ConstantJavaType.LJAVA_LANG_OBJECT;
                    // BigInteger operations - if either operand is BigInteger, result is BigInteger
                    if (ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(leftType) || ConstantJavaType.LJAVA_MATH_BIGINTEGER.equals(rightType)) {
                        return ConstantJavaType.LJAVA_MATH_BIGINTEGER;
                    }
                    // Use type widening rules (int ^ long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case EqEq, EqEqEq, NotEq, NotEqEq, Lt, LtEq, Gt, GtEq, LogicalAnd, LogicalOr, InstanceOf, In -> {
                    // Equality, inequality, relational comparisons, logical operations, instanceof, and in return boolean
                    return ConstantJavaType.ABBR_BOOLEAN;
                }
                case NullishCoalescing -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft());
                    String rightType = inferTypeFromExpr(binExpr.getRight());
                    if (leftType == null) {
                        return rightType;
                    }
                    if (rightType == null) {
                        return leftType;
                    }
                    if (leftType.equals(rightType)) {
                        return leftType;
                    }
                    if (TypeConversionUtils.isPrimitiveType(leftType)
                            && TypeConversionUtils.isPrimitiveType(rightType)) {
                        return getWidenedType(leftType, rightType);
                    }
                    return ConstantJavaType.LJAVA_LANG_OBJECT;
                }
            }
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            switch (unaryExpr.getOp()) {
                case Bang, Delete -> {
                    return ConstantJavaType.ABBR_BOOLEAN;
                }
                case TypeOf -> {
                    return ConstantJavaType.LJAVA_LANG_STRING;
                }
                case Void -> {
                    return ConstantJavaType.LJAVA_LANG_OBJECT;
                }
                default -> {
                    // For unary expressions, infer type from the argument
                    return inferTypeFromExpr(unaryExpr.getArg());
                }
            }
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            // For parenthesized expressions, infer type from the inner expression
            return inferTypeFromExpr(parenExpr.getExpr());
        } else if (expr instanceof Swc4jAstOptChainExpr) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstFnExpr) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstClassExpr) {
            return ConstantJavaType.LJAVA_LANG_CLASS;
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            // For call expressions, try to infer the return type

            // super() and this() constructor calls always return void
            if (callExpr.getCallee() instanceof Swc4jAstSuper
                    || callExpr.getCallee() instanceof Swc4jAstThisExpr) {
                return ConstantJavaType.ABBR_VOID;
            }

            // Handle direct calls to functional interface variables (e.g., factorial(n - 1))
            if (callExpr.getCallee() instanceof Swc4jAstIdent ident) {
                String varName = ident.getSym();

                // Get the type of the variable (context is already available at method scope)
                String varType = context.getInferredTypes().get(varName);
                if (varType == null) {
                    var localVar = context.getLocalVariableTable().getVariable(varName);
                    if (localVar != null) {
                        varType = localVar.type();
                    }
                }

                if (varType != null && varType.startsWith("L") && varType.endsWith(";")) {
                    // Get return type from functional interface using reflection
                    String returnType = compiler.getMemory().getScopedFunctionalInterfaceRegistry().getReturnType(varType);
                    if (returnType != null) {
                        return returnType;
                    }
                }
            }

            // Handle super.method() calls
            if (callExpr.getCallee() instanceof Swc4jAstSuperPropExpr superPropExpr) {
                if (superPropExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String methodName = propIdent.getSym();

                    // Get the current class and resolve its superclass
                    String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
                    if (currentClassName != null) {
                        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                                .resolveSuperClass(currentClassName.replace('/', '.'));
                        if (superClassInternalName == null) {
                            // Try simple name
                            int lastSlash = currentClassName.lastIndexOf('/');
                            String simpleName = lastSlash >= 0 ? currentClassName.substring(lastSlash + 1) : currentClassName;
                            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
                        }

                        if (superClassInternalName != null) {
                            // Look up method return type from the superclass
                            String superQualifiedName = superClassInternalName.replace('/', '.');
                            String returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                                    .resolveClassMethodReturnType(superQualifiedName, methodName, "()");
                            if (returnType == null) {
                                // Try simple name
                                int lastSlash = superClassInternalName.lastIndexOf('/');
                                String simpleName = lastSlash >= 0 ? superClassInternalName.substring(lastSlash + 1) : superClassInternalName;
                                returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                                        .resolveClassMethodReturnType(simpleName, methodName, "()");
                            }
                            if (returnType != null) {
                                return returnType;
                            }
                        }
                    }
                }
            }

            if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
                if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent
                        && "Array".equals(objIdent.getSym())
                        && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String methodName = propIdent.getSym();
                    switch (methodName) {
                        case "isArray" -> {
                            return ConstantJavaType.ABBR_BOOLEAN;
                        }
                        case "from", "of" -> {
                            return ConstantJavaType.LJAVA_UTIL_ARRAYLIST;
                        }
                    }
                }
                String objType = inferTypeFromExpr(memberExpr.getObj());

                // Check if the object is a Java class identifier
                if (memberExpr.getObj() instanceof Swc4jAstIdent objIdent) {
                    String className = objIdent.getSym();
                    var javaClassInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(className);
                    if (javaClassInfo != null && memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        String methodName = propIdent.getSym();
                        var methodInfo = javaClassInfo.getMethod(methodName);
                        if (methodInfo != null) {
                            return methodInfo.returnType();
                        }
                    }
                }

                // ArrayList methods
                if (ConstantJavaType.LJAVA_UTIL_ARRAYLIST.equals(objType)) {
                    if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        String methodName = propIdent.getSym();
                        // Methods that return ArrayList
                        switch (methodName) {
                            case ConstantJavaMethod.METHOD_CONCAT, "reverse", "sort", "slice", "splice", "fill", "copyWithin", "toReversed",
                                 "toSorted", "with", "toSpliced", "map", "filter", "flat", "flatMap",
                                 "keys", "values", "entries" -> {
                                return ConstantJavaType.LJAVA_UTIL_ARRAYLIST;
                            }
                            case "push", "unshift", "forEach" -> {
                                return ConstantJavaType.ABBR_VOID;
                            }
                            case "find", "reduce", "reduceRight" -> {
                                return ConstantJavaType.LJAVA_LANG_OBJECT;
                            }
                            case "join", ConstantJavaMethod.METHOD_TO_STRING, "toLocaleString" -> {
                                return ConstantJavaType.LJAVA_LANG_STRING;
                            }
                            case ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaMethod.METHOD_LAST_INDEX_OF, "findIndex" -> {
                                return ConstantJavaType.ABBR_INTEGER;
                            }
                            case "includes", "some", "every" -> {
                                return ConstantJavaType.ABBR_BOOLEAN;
                            }
                            case "pop", "shift" -> {
                                return ConstantJavaType.LJAVA_LANG_OBJECT;
                            }
                        }
                    }
                }

                // Java array methods
                if (objType != null && objType.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
                    if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        String methodName = propIdent.getSym();
                        String elementType = objType.substring(1); // Remove leading ConstantJavaType.ARRAY_PREFIX
                        switch (methodName) {
                            case "reverse", "sort", "fill" -> {
                                // Methods that mutate and return the same array type
                                return objType;
                            }
                            case "toReversed", "toSorted" -> {
                                // Methods that return a new array of the same type
                                return objType;
                            }
                            case "join", ConstantJavaMethod.METHOD_TO_STRING -> {
                                return ConstantJavaType.LJAVA_LANG_STRING;
                            }
                            case ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaMethod.METHOD_LAST_INDEX_OF -> {
                                return ConstantJavaType.ABBR_INTEGER;
                            }
                            case "includes" -> {
                                return ConstantJavaType.ABBR_BOOLEAN;
                            }
                        }
                    }
                }

                // String methods
                if (ConstantJavaType.LJAVA_LANG_STRING.equals(objType)) {
                    if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        String methodName = propIdent.getSym();
                        return switch (methodName) {
                            // String return types
                            case "charAt", "substring", "slice", "substr", "toLowerCase", "toUpperCase", "trim",
                                 "trimStart", "trimLeft", "trimEnd", "trimRight", ConstantJavaMethod.METHOD_CONCAT, "repeat", "replace",
                                 "replaceAll", "padStart", "padEnd" -> ConstantJavaType.LJAVA_LANG_STRING;
                            // int return types
                            case ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaMethod.METHOD_LAST_INDEX_OF, "charCodeAt", "codePointAt", "search" ->
                                    ConstantJavaType.ABBR_INTEGER;
                            // boolean return types
                            case "startsWith", "endsWith", "includes", "test" -> ConstantJavaType.ABBR_BOOLEAN;
                            // ArrayList return type (split, match, matchAll)
                            case "split", "match", "matchAll" -> ConstantJavaType.LJAVA_UTIL_ARRAYLIST;
                            default -> ConstantJavaType.LJAVA_LANG_OBJECT;
                        };
                    }
                }

                // Check if it's an object method call (Java or TypeScript class)
                if (objType != null && objType.startsWith("L") && objType.endsWith(";")) {
                    String qualifiedClassName = objType.substring(1, objType.length() - 1).replace('/', '.');
                    String methodName = null;

                    // Handle public methods (IdentName) and private methods (PrivateName)
                    if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        methodName = propIdent.getSym();
                    } else if (memberExpr.getProp() instanceof Swc4jAstPrivateName privateName) {
                        // ES2022 private method (#method) - name without # prefix
                        methodName = privateName.getName();
                    }

                    if (methodName != null) {
                        // Build parameter descriptor from call arguments
                        StringBuilder paramDescriptors = new StringBuilder();
                        for (var arg : callExpr.getArgs()) {
                            if (arg.getSpread().isPresent()) {
                                throw new Swc4jByteCodeCompilerException(getSourceCode(), arg.getExpr(),
                                        "Spread arguments not supported in class method calls");
                            }
                            String argType = inferTypeFromExpr(arg.getExpr());
                            if (argType == null) {
                                argType = ConstantJavaType.LJAVA_LANG_OBJECT;
                            }
                            paramDescriptors.append(argType);
                        }
                        String paramDescriptor = "(" + paramDescriptors + ")";
                        // Use the registry which falls back to reflection for Java types
                        String returnType = compiler.getMemory().getScopedJavaTypeRegistry()
                                .resolveClassMethodReturnType(qualifiedClassName, methodName, paramDescriptor);
                        if (returnType != null) {
                            return returnType;
                        }
                        // For non-Java types, require explicit annotation
                        if (!objType.startsWith("Ljava/") && !objType.startsWith("Ljavax/")) {
                            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr,
                                    "Cannot infer return type for method call " + qualifiedClassName + "." + methodName +
                                            ". Please add explicit return type annotation to the method.");
                        }
                    }
                }
            }

            // Handle IIFE (Immediately Invoked Function Expression)
            // Pattern: ((x: int): int => x * 2)(5)
            var callee = callExpr.getCallee();
            if (callee instanceof ISwc4jAstExpr calleeExpr) {
                var arrowExpr = extractArrowFromIIFE(calleeExpr);
                if (arrowExpr != null) {
                    // Check for explicit return type annotation
                    if (arrowExpr.getReturnType().isPresent()) {
                        return mapTsTypeToDescriptor(arrowExpr.getReturnType().get().getTypeAnn());
                    }

                    // Infer from arrow body
                    var body = arrowExpr.getBody();
                    if (body instanceof ISwc4jAstExpr bodyExpr) {
                        // Expression body - infer from expression
                        return inferTypeFromExpr(bodyExpr);
                    } else if (body instanceof Swc4jAstBlockStmt blockStmt) {
                        // Block body - analyze return statements
                        String returnType = inferReturnTypeFromBlock(blockStmt);
                        if (returnType != null) {
                            return returnType;
                        }
                    }
                    // Default to Object if we can't infer
                    return ConstantJavaType.LJAVA_LANG_OBJECT;
                }
            }

            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (expr instanceof Swc4jAstSeqExpr seqExpr) {
            // Sequence expression returns the type of the last expression
            var exprs = seqExpr.getExprs();
            if (!exprs.isEmpty()) {
                return inferTypeFromExpr(exprs.get(exprs.size() - 1));
            }
            return ConstantJavaType.ABBR_VOID; // Empty sequence - void
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Looks up a field in the class hierarchy, starting from the given class and traversing up to parent classes.
     *
     * @param typeInfo  the starting class type info
     * @param fieldName the field name to look up
     * @return the field info, or null if not found
     */
    private FieldInfo lookupFieldInHierarchy(JavaTypeInfo typeInfo, String fieldName) {
        // First check in current class
        FieldInfo fieldInfo = typeInfo.getField(fieldName);
        if (fieldInfo != null) {
            return fieldInfo;
        }

        // Check in parent classes
        for (JavaTypeInfo parentInfo : typeInfo.getParentTypeInfos()) {
            fieldInfo = lookupFieldInHierarchy(parentInfo, fieldName);
            if (fieldInfo != null) {
                return fieldInfo;
            }
        }

        return null;
    }

    private String mapImportTypeToDescriptor(Swc4jAstTsImportType importType) throws Swc4jByteCodeCompilerException {
        if (importType.getQualifier().isEmpty()) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        String qualifierName = AstUtils.extractQualifiedName(importType.getQualifier().get());
        if (qualifierName == null || qualifierName.isEmpty()) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        String importPath = importType.getArg().getValue();
        if (importPath == null || importPath.isEmpty() || importPath.startsWith(".")) {
            return mapTypeNameToDescriptor(qualifierName);
        }
        String resolvedTypeName = importPath.replace('/', '.') + "." + qualifierName;
        return mapTypeNameToDescriptor(resolvedTypeName);
    }

    private String mapIndexedAccessTypeToDescriptor(
            Swc4jAstTsIndexedAccessType indexedAccessType) throws Swc4jByteCodeCompilerException {
        ISwc4jAstTsType objType = indexedAccessType.getObjType();
        ISwc4jAstTsType indexType = indexedAccessType.getIndexType();

        if (objType instanceof Swc4jAstTsArrayType arrayType) {
            return mapTsTypeToDescriptor(arrayType.getElemType());
        }

        if (objType instanceof Swc4jAstTsTypeRef typeRef
                && typeRef.getTypeName() instanceof Swc4jAstIdent ident
                && "Array".equals(ident.getSym())
                && typeRef.getTypeParams().isPresent()
                && typeRef.getTypeParams().get().getParams().size() == 1) {
            return mapTsTypeToDescriptor(typeRef.getTypeParams().get().getParams().get(0));
        }

        if (objType instanceof Swc4jAstTsTupleType tupleType) {
            Integer tupleIndex = getTupleLiteralIndex(indexType);
            if (tupleIndex != null && tupleIndex < tupleType.getElemTypes().size()) {
                return mapTsTypeToDescriptor(tupleType.getElemTypes().get(tupleIndex).getTy());
            }
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        String objDescriptor = mapTsTypeToDescriptor(objType);
        if (objDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return objDescriptor.substring(1);
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    private String mapIntersectionTypeToDescriptor(
            Swc4jAstTsIntersectionType intersectionType) throws Swc4jByteCodeCompilerException {
        if (intersectionType.getTypes().isEmpty()) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        String descriptor = mapTsTypeToDescriptor(intersectionType.getTypes().get(0));
        for (int i = 1; i < intersectionType.getTypes().size(); i++) {
            String nextDescriptor = mapTsTypeToDescriptor(intersectionType.getTypes().get(i));
            if (!descriptor.equals(nextDescriptor)) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
        }
        return descriptor;
    }

    private String mapRestTypeToDescriptor(
            Swc4jAstTsRestType restType) throws Swc4jByteCodeCompilerException {
        String componentDescriptor = mapTsTypeToDescriptor(restType.getTypeAnn());
        if (componentDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return componentDescriptor;
        }
        if (ConstantJavaType.ABBR_VOID.equals(componentDescriptor)) {
            return ConstantJavaType.ARRAY_LJAVA_LANG_OBJECT;
        }
        return ConstantJavaType.ARRAY_PREFIX + componentDescriptor;
    }

    private String mapTsLiteralTypeToDescriptor(
            ISwc4jAstTsLit lit) {
        if (lit instanceof Swc4jAstBigInt) {
            return ConstantJavaType.ABBR_LONG;
        }
        if (lit instanceof Swc4jAstBool) {
            return ConstantJavaType.ABBR_BOOLEAN;
        }
        if (lit instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value)
                    && !Double.isInfinite(value)
                    && !Double.isNaN(value)
                    && value >= Integer.MIN_VALUE
                    && value <= Integer.MAX_VALUE) {
                return ConstantJavaType.ABBR_INTEGER;
            }
            return ConstantJavaType.ABBR_DOUBLE;
        }
        if (lit instanceof Swc4jAstStr || lit instanceof Swc4jAstTsTplLitType) {
            return ConstantJavaType.LJAVA_LANG_STRING;
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Map ts type to descriptor string.
     *
     * @param tsType the ts type
     * @return the string
     * @throws Swc4jByteCodeCompilerException if a type reference cannot be resolved
     */
    public String mapTsTypeToDescriptor(
            ISwc4jAstTsType tsType) throws Swc4jByteCodeCompilerException {
        if (tsType == null) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        } else if (tsType instanceof Swc4jAstTsOptionalType optionalType) {
            return mapTsTypeToDescriptor(optionalType.getTypeAnn());
        } else if (tsType instanceof Swc4jAstTsArrayType arrayType) {
            // type[] syntax - maps to Java array
            String elemType = mapTsTypeToDescriptor(arrayType.getElemType());
            return ConstantJavaType.ARRAY_PREFIX + elemType;
        } else if (tsType instanceof Swc4jAstTsRestType restType) {
            return mapRestTypeToDescriptor(restType);
        } else if (tsType instanceof Swc4jAstTsUnionType unionType) {
            return mapUnionTypeToDescriptor(unionType);
        } else if (tsType instanceof Swc4jAstTsIntersectionType intersectionType) {
            return mapIntersectionTypeToDescriptor(intersectionType);
        } else if (tsType instanceof Swc4jAstTsTupleType) {
            // Tuples are represented as ordered Lists at runtime.
            return ConstantJavaType.LJAVA_UTIL_LIST;
        } else if (tsType instanceof Swc4jAstTsLitType litType) {
            return mapTsLiteralTypeToDescriptor(litType.getLit());
        } else if (tsType instanceof Swc4jAstTsMappedType) {
            // Mapped object types are compiled to LinkedHashMap.
            return ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
        } else if (tsType instanceof Swc4jAstTsIndexedAccessType indexedAccessType) {
            return mapIndexedAccessTypeToDescriptor(indexedAccessType);
        } else if (tsType instanceof Swc4jAstTsTypeOperator typeOperator) {
            return mapTypeOperatorToDescriptor(typeOperator);
        } else if (tsType instanceof Swc4jAstTsTypeQuery typeQuery) {
            return mapTypeQueryToDescriptor(typeQuery);
        } else if (tsType instanceof Swc4jAstTsImportType importType) {
            return mapImportTypeToDescriptor(importType);
        } else if (tsType instanceof Swc4jAstTsTypePredicate) {
            // Type predicates are function-return boolean contracts at runtime.
            return ConstantJavaType.ABBR_BOOLEAN;
        } else if (tsType instanceof Swc4jAstTsConditionalType conditionalType) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    conditionalType,
                    "Conditional types are not supported in runtime type mapping.");
        } else if (tsType instanceof Swc4jAstTsInferType inferType) {
            throw new Swc4jByteCodeCompilerException(
                    getSourceCode(),
                    inferType,
                    "Infer types are not supported in runtime type mapping.");
        } else if (tsType instanceof Swc4jAstTsKeywordType keywordType) {
            // Handle TypeScript keyword types (boolean, number, string, etc.)
            return switch (keywordType.getKind()) {
                case TsAnyKeyword, TsIntrinsicKeyword, TsNeverKeyword, TsNullKeyword,
                     TsObjectKeyword, TsSymbolKeyword, TsUndefinedKeyword, TsUnknownKeyword ->
                        ConstantJavaType.LJAVA_LANG_OBJECT;
                case TsBooleanKeyword -> ConstantJavaType.ABBR_BOOLEAN;
                case TsNumberKeyword -> ConstantJavaType.ABBR_DOUBLE;  // Default to double for number
                case TsStringKeyword -> ConstantJavaType.LJAVA_LANG_STRING;
                case TsBigIntKeyword -> ConstantJavaType.ABBR_LONG;  // Map to long
                case TsVoidKeyword -> ConstantJavaType.ABBR_VOID;
            };
        } else if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
            ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
            if (entityName instanceof Swc4jAstIdent ident) {
                String typeName = ident.getSym();
                // Check if this is Array<T> generic syntax
                if ("Array".equals(typeName)) {
                    // Array<T> syntax - maps to List interface (more flexible than ArrayList)
                    return ConstantJavaType.LJAVA_UTIL_LIST;
                }
                // Phase 2: Check if this is Record<K, V> generic syntax
                if ("Record".equals(typeName)) {
                    // Record<K, V> syntax - maps to LinkedHashMap
                    return ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP;
                }
                return mapTypeNameToDescriptor(typeName);
            }
        } else if (tsType instanceof Swc4jAstTsFnType fnType) {
            // Function type syntax: (a: int, b: int) => int
            // Generate a custom functional interface at compile time
            return generateFunctionalInterface(fnType);
        }
        // Default to Object for unknown types
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Map type name to descriptor string.
     *
     * @param typeName the type name
     * @return the string
     * @throws Swc4jByteCodeCompilerException if the type cannot be resolved
     */
    public String mapTypeNameToDescriptor(
            String typeName) throws Swc4jByteCodeCompilerException {
        // First check if this is a type parameter (for generics support)
        var context = compiler.getMemory().getCompilationContext();
        if (context.isTypeParameter(typeName)) {
            // Type parameter - resolve to its constraint type (type erasure)
            var constraintOpt = context.resolveTypeParameter(typeName);
            if (constraintOpt.isPresent()) {
                // Has constraint (e.g., T extends Number) - erase to constraint type
                return mapTsTypeToDescriptor(constraintOpt.get());
            } else {
                // No constraint - erase to Object
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
        }

        // Resolve type alias first
        String resolvedType = compiler.getMemory().getScopedTypeAliasRegistry().resolve(typeName);
        if (resolvedType == null) {
            resolvedType = typeName;
        }

        return switch (resolvedType) {
            case ConstantJavaType.TYPEOF_BOOLEAN -> ConstantJavaType.ABBR_BOOLEAN;
            case "byte" -> ConstantJavaType.ABBR_BYTE;
            case "char" -> ConstantJavaType.ABBR_CHARACTER;
            case "double" -> ConstantJavaType.ABBR_DOUBLE;
            case "float" -> ConstantJavaType.ABBR_FLOAT;
            case "int" -> ConstantJavaType.ABBR_INTEGER;
            case "long" -> ConstantJavaType.ABBR_LONG;
            case "short" -> ConstantJavaType.ABBR_SHORT;
            case "void" -> ConstantJavaType.ABBR_VOID;
            default -> {
                // Check if this is an enum type - resolve to fully qualified name
                String qualifiedName = resolveEnumTypeName(resolvedType);
                // If the type name was not resolved (no package prefix, not an enum, not qualified),
                // it would produce an invalid descriptor like "LObject;" — throw instead.
                // Exclude single uppercase letter names (likely generic type parameters like T, K, V)
                // that may not be registered in the current context during early analysis phases.
                if (qualifiedName.equals(resolvedType) && !resolvedType.contains(".")
                        && resolvedType.length() > 1) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), null,
                            "Unresolved type: '" + typeName + "'. "
                                    + "Import it or register it as a type alias.");
                }
                yield "L" + qualifiedName.replace('.', '/') + ";";
            }
        };
    }

    private String mapTypeOperatorToDescriptor(
            Swc4jAstTsTypeOperator typeOperator) throws Swc4jByteCodeCompilerException {
        return switch (typeOperator.getOp()) {
            case KeyOf -> ConstantJavaType.LJAVA_LANG_STRING;
            case ReadOnly, Unique -> mapTsTypeToDescriptor(typeOperator.getTypeAnn());
        };
    }

    private String mapTypeQueryToDescriptor(
            Swc4jAstTsTypeQuery typeQuery) throws Swc4jByteCodeCompilerException {
        ISwc4jAstTsTypeQueryExpr exprName = typeQuery.getExprName();
        if (exprName instanceof Swc4jAstTsImportType importType) {
            return mapImportTypeToDescriptor(importType);
        }
        if (exprName instanceof ISwc4jAstTsEntityName entityName) {
            String qualifiedName = AstUtils.extractQualifiedName(entityName);
            if (qualifiedName == null || qualifiedName.isEmpty()) {
                return ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            if (qualifiedName.contains(".")) {
                return mapTypeNameToDescriptor(qualifiedName);
            }
            CompilationContext context = compiler.getMemory().getCompilationContext();
            String inferredType = context.getInferredTypes().get(qualifiedName);
            if (inferredType != null) {
                return inferredType;
            }
            var localVariable = context.getLocalVariableTable().getVariable(qualifiedName);
            if (localVariable != null) {
                return localVariable.type();
            }
            String resolvedAlias = compiler.getMemory().getScopedTypeAliasRegistry().resolve(qualifiedName);
            if (resolvedAlias != null) {
                return mapTypeNameToDescriptor(resolvedAlias);
            }
        }
        return ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    private String mapUnionTypeToDescriptor(
            Swc4jAstTsUnionType unionType) throws Swc4jByteCodeCompilerException {
        if (unionType.getTypes().isEmpty()) {
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }
        String descriptor = null;
        for (ISwc4jAstTsType candidateType : unionType.getTypes()) {
            String candidateDescriptor = mapTsTypeToDescriptor(candidateType);
            if (ConstantJavaType.ABBR_VOID.equals(candidateDescriptor)) {
                candidateDescriptor = ConstantJavaType.LJAVA_LANG_OBJECT;
            }
            if (descriptor == null) {
                descriptor = candidateDescriptor;
            } else {
                descriptor = findCommonType(descriptor, candidateDescriptor);
            }
            if (ConstantJavaType.LJAVA_LANG_OBJECT.equals(descriptor)) {
                break;
            }
        }
        return descriptor != null ? descriptor : ConstantJavaType.LJAVA_LANG_OBJECT;
    }

    /**
     * Parses {@code Array<T>} type annotation and extracts the element type.
     * <p>
     * Supports:
     * <ul>
     * <li>{@code Array<number>} → "I" (int)</li>
     * <li>{@code Array<string>} → "Ljava/lang/String;"</li>
     * <li>{@code Array<Array<number>>} → nested array handling</li>
     * </ul>
     *
     * @param tsType TypeScript type annotation (expected to be TsTypeRef for Array)
     * @return element type descriptor, or null if not an Array type
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public String parseArrayType(ISwc4jAstTsType tsType) throws Swc4jByteCodeCompilerException {
        if (!(tsType instanceof Swc4jAstTsTypeRef typeRef)) {
            return null;
        }

        // Check if this is an "Array" type
        ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
        if (!(entityName instanceof Swc4jAstIdent ident)) {
            return null;
        }

        String typeName = ident.getSym();
        if (!"Array".equals(typeName)) {
            return null;
        }

        // Extract type parameter
        Optional<Swc4jAstTsTypeParamInstantiation> typeParamsOpt = typeRef.getTypeParams();
        if (typeParamsOpt.isEmpty()) {
            // Array without type parameter - default to Array<Object>
            return ConstantJavaType.LJAVA_LANG_OBJECT;
        }

        Swc4jAstTsTypeParamInstantiation typeParams = typeParamsOpt.get();
        List<ISwc4jAstTsType> params = typeParams.getParams();

        if (params.isEmpty() || params.size() > 1) {
            // Array type must have exactly 1 type parameter
            return null;
        }

        // Parse element type (first and only parameter)
        ISwc4jAstTsType elementTsType = params.get(0);
        return mapTsTypeToDescriptor(elementTsType);
    }

    /**
     * Parse {@code Record<K, V>} type annotation to extract generic type parameters.
     * <p>
     * Supports:
     * - {@code Record<string, number>} → GenericTypeInfo.of("Ljava/lang/String;", "I")
     * - {@code Record<number, string>} → GenericTypeInfo.of("Ljava/lang/Integer;", "Ljava/lang/String;")
     * - {@code Record<string, Record<string, number>>} → GenericTypeInfo.ofNested(...)
     *
     * @param tsType TypeScript type annotation (expected to be TsTypeRef with type params)
     * @return GenericTypeInfo containing key and value type descriptors, or null if not a Record type
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public GenericTypeInfo parseRecordType(
            ISwc4jAstTsType tsType) throws Swc4jByteCodeCompilerException {
        if (!(tsType instanceof Swc4jAstTsTypeRef typeRef)) {
            return null;
        }

        // Check if this is a "Record" type
        ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
        if (!(entityName instanceof Swc4jAstIdent ident)) {
            return null;
        }

        String typeName = ident.getSym();
        if (!"Record".equals(typeName)) {
            return null;
        }

        // Extract type parameters
        Optional<Swc4jAstTsTypeParamInstantiation> typeParamsOpt = typeRef.getTypeParams();
        if (typeParamsOpt.isEmpty()) {
            // Record without type parameters - default to Record<string, Object>
            return GenericTypeInfo.of(ConstantJavaType.LJAVA_LANG_STRING, ConstantJavaType.LJAVA_LANG_OBJECT);
        }

        Swc4jAstTsTypeParamInstantiation typeParams = typeParamsOpt.get();
        List<ISwc4jAstTsType> params = typeParams.getParams();

        if (params.size() != 2) {
            // Record type must have exactly 2 type parameters
            return null;
        }

        // Parse key type (first parameter)
        ISwc4jAstTsType keyTsType = params.get(0);
        String keyType = mapTsTypeToDescriptor(keyTsType);

        // Parse value type (second parameter)
        ISwc4jAstTsType valueTsType = params.get(1);

        // Check if value type is a nested Record
        GenericTypeInfo nestedInfo = parseRecordType(valueTsType);
        if (nestedInfo != null) {
            // Nested Record type: Record<K, Record<K2, V2>>
            return GenericTypeInfo.ofNested(keyType, nestedInfo);
        }

        // Check if value type is an Array
        String arrayElementType = parseArrayType(valueTsType);
        if (arrayElementType != null) {
            // Array value type: Record<K, Array<T>>
            return GenericTypeInfo.ofArray(keyType, arrayElementType);
        }

        // Simple Record type: Record<K, V>
        String valueType = mapTsTypeToDescriptor(valueTsType);
        return GenericTypeInfo.of(keyType, valueType);
    }

    /**
     * Resolve an enum type name to its fully qualified name.
     * If the type is an enum registered in EnumRegistry, returns the qualified name.
     * Otherwise, returns the type name as-is with package prefix if available.
     */
    private String resolveEnumTypeName(String typeName) {
        // If already qualified (contains '.'), return as-is
        if (typeName.contains(".")) {
            return typeName;
        }


        // Try to find the enum in the registry by checking if any member exists
        // We use a dummy member name since we just want to know if the enum exists
        for (String qualifiedName : compiler.getMemory().getScopedJavaTypeRegistry().getAllEnumNames()) {
            if (qualifiedName.endsWith("." + typeName)) {
                return qualifiedName;
            }
        }

        // Not an enum or not registered yet - add package prefix if available
        String packagePrefix = compiler.getOptions().packagePrefix();
        if (!packagePrefix.isEmpty()) {
            return packagePrefix + "." + typeName;
        }

        return typeName;
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
}
