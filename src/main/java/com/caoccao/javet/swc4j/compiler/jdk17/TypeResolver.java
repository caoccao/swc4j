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
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsArrayType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsKeywordType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.List;
import java.util.Optional;

public final class TypeResolver {
    private TypeResolver() {
    }

    public static ReturnTypeInfo analyzeReturnType(
            Swc4jAstFunction function,
            Swc4jAstBlockStmt body,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Check for explicit return type annotation first
        var returnTypeOpt = function.getReturnType();
        if (returnTypeOpt.isPresent()) {
            var returnTypeAnn = returnTypeOpt.get();
            var tsType = returnTypeAnn.getTypeAnn();
            String descriptor = mapTsTypeToDescriptor(tsType, options);
            return ReturnTypeInfo.of(descriptor);
        }

        // Fall back to type inference from return statement
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                var argOpt = returnStmt.getArg();
                if (argOpt.isPresent()) {
                    ISwc4jAstExpr arg = argOpt.get();
                    // Use inferTypeFromExpr for general type inference
                    String type = inferTypeFromExpr(arg, context, options);
                    // If type is null (e.g., for null literal), default to Object
                    if (type == null) {
                        type = "Ljava/lang/Object;";
                    }
                    return ReturnTypeInfo.of(type);
                }
                return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
            }
        }
        return new ReturnTypeInfo(ReturnType.VOID, 0, null, null);
    }

    /**
     * Extract GenericTypeInfo from a BindingIdent's type annotation if it's a Record type.
     * Phase 2: Support for {@code Record<K, V>} type validation
     *
     * @param bindingIdent binding identifier with potential type annotation
     * @param options      compiler options
     * @return GenericTypeInfo if the type annotation is a Record type, null otherwise
     */
    public static GenericTypeInfo extractGenericTypeInfo(
            Swc4jAstBindingIdent bindingIdent,
            ByteCodeCompilerOptions options) {
        var typeAnn = bindingIdent.getTypeAnn();
        if (typeAnn.isEmpty()) {
            return null;
        }

        ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();

        // Check if it's a Record type by calling parseRecordType
        // parseRecordType returns null if it's not a Record type
        return parseRecordType(tsType, options);
    }

    /**
     * Extract type from parameter pattern (regular param or varargs).
     */
    public static String extractParameterType(
            ISwc4jAstPat pat,
            ByteCodeCompilerOptions options) {
        if (pat instanceof Swc4jAstRestPat restPat) {
            // Varargs parameter - extract type from RestPat's type annotation
            var typeAnn = restPat.getTypeAnn();
            if (typeAnn.isPresent()) {
                ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
                // RestPat type annotation is already an array type (int[])
                // We need to map it to the corresponding JVM array descriptor
                return mapTsTypeToDescriptor(tsType, options);
            }
            // Default to Object[] for untyped varargs
            return "[Ljava/lang/Object;";
        } else if (pat instanceof Swc4jAstBindingIdent bindingIdent) {
            // Regular parameter - extract type from type annotation
            var typeAnn = bindingIdent.getTypeAnn();
            if (typeAnn.isPresent()) {
                ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
                return mapTsTypeToDescriptor(tsType, options);
            }
            // Default to Object for untyped parameters
            return "Ljava/lang/Object;";
        }
        return "Ljava/lang/Object;";
    }

    public static String extractType(
            Swc4jAstBindingIdent bindingIdent,
            Optional<ISwc4jAstExpr> init,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        // Check for explicit type annotation
        var typeAnn = bindingIdent.getTypeAnn();
        if (typeAnn.isPresent()) {
            ISwc4jAstTsType tsType = typeAnn.get().getTypeAnn();
            return mapTsTypeToDescriptor(tsType, options);
        }

        // Type inference from initializer
        if (init.isPresent()) {
            String type = inferTypeFromExpr(init.get(), context, options);
            // If type is null (e.g., for null literal), default to Object
            return type != null ? type : "Ljava/lang/Object;";
        }

        return "Ljava/lang/Object;"; // Default
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
            return "Ljava/lang/Object;";
        }
        if (type2 == null) {
            // any type with null -> Object (for stackmap compatibility)
            return "Ljava/lang/Object;";
        }

        // Same type - no conversion needed
        if (type1.equals(type2)) {
            // For primitives, keep the specific type
            if (TypeConversionUtils.isPrimitiveType(type1)) {
                return type1;
            }
            // For reference types, use Object for stackmap compatibility
            return "Ljava/lang/Object;";
        }

        // Both primitives - use numeric widening
        if (TypeConversionUtils.isPrimitiveType(type1) && TypeConversionUtils.isPrimitiveType(type2)) {
            return getWidenedType(type1, type2);
        }

        // One primitive, one reference - result is Object
        // (either boxing the primitive or finding common supertype)
        return "Ljava/lang/Object;";
    }

    private static String getPrimitiveType(String type) {
        return switch (type) {
            case "Ljava/lang/Boolean;" -> "Z";
            case "Ljava/lang/Byte;" -> "B";
            case "Ljava/lang/Short;" -> "S";
            case "Ljava/lang/Integer;" -> "I";
            case "Ljava/lang/Long;" -> "J";
            case "Ljava/lang/Float;" -> "F";
            case "Ljava/lang/Double;" -> "D";
            case "Ljava/lang/Character;" -> "C";
            default -> type;
        };
    }

    public static String getWidenedType(String leftType, String rightType) {
        // Get primitive types (unwrap if needed)
        String left = getPrimitiveType(leftType);
        String right = getPrimitiveType(rightType);

        // byte, short, char promote to int for operations
        if ("B".equals(left) || "S".equals(left) || "C".equals(left)) {
            left = "I";
        }
        if ("B".equals(right) || "S".equals(right) || "C".equals(right)) {
            right = "I";
        }

        // Type widening rules: double > float > long > int
        if ("D".equals(left) || "D".equals(right)) {
            return "D";
        }
        if ("F".equals(left) || "F".equals(right)) {
            return "F";
        }
        if ("J".equals(left) || "J".equals(right)) {
            return "J";
        }
        return "I";
    }

    /**
     * Get the wrapper type for a primitive type.
     *
     * @param primitiveType primitive type descriptor (e.g., "I", "D")
     * @return wrapper type descriptor (e.g., "Ljava/lang/Integer;", "Ljava/lang/Double;")
     */
    private static String getWrapperType(String primitiveType) {
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
     * Infer the JVM type descriptor for an object literal property key.
     * <p>
     * Handles different property name types:
     * - IdentName → String (identifier keys are always strings)
     * - Str → String (string literal keys)
     * - Number → Integer, Long, or Double based on the actual numeric value
     * - BigInt → Long or BigInteger based on size
     * - ComputedPropName → Inferred from the computed expression
     *
     * @param key     Property name AST node
     * @param context Compilation context
     * @param options Compiler options
     * @return JVM type descriptor (e.g., "Ljava/lang/String;", "Ljava/lang/Integer;", "D")
     */
    public static String inferKeyType(
            ISwc4jAstPropName key,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        if (key == null) {
            return "Ljava/lang/String;"; // Default to String
        }

        // IdentName keys are always strings
        if (key instanceof Swc4jAstIdentName) {
            return "Ljava/lang/String;";
        }

        // String literal keys
        if (key instanceof Swc4jAstStr) {
            return "Ljava/lang/String;";
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
                    return "Ljava/lang/Integer;";
                }

                // It needs a long
                return "Ljava/lang/Long;";
            }

            // It has a decimal part - use Double
            return "Ljava/lang/Double;";
        }

        // BigInt keys - always Long or BigInteger
        if (key instanceof Swc4jAstBigInt bigInt) {
            // For now, default to Long
            // In the future, could parse the BigInt value and choose BigInteger if needed
            return "Ljava/lang/Long;";
        }

        // Computed property names - infer from expression
        if (key instanceof Swc4jAstComputedPropName computed) {
            ISwc4jAstExpr expr = computed.getExpr();
            String inferredType = inferTypeFromExpr(expr, context, options);
            return inferredType != null ? inferredType : "Ljava/lang/String;";
        }

        // Default to String for any unknown key type
        return "Ljava/lang/String;";
    }

    public static String inferTypeFromExpr(
            ISwc4jAstExpr expr,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        if (expr instanceof Swc4jAstCondExpr condExpr) {
            // Conditional expression: infer type from both branches and find common type
            String consType = inferTypeFromExpr(condExpr.getCons(), context, options);
            String altType = inferTypeFromExpr(condExpr.getAlt(), context, options);
            return findCommonType(consType, altType);
        } else if (expr instanceof Swc4jAstTsAsExpr asExpr) {
            // Explicit type cast - return the cast target type
            var tsType = asExpr.getTypeAnn();
            if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
                ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
                if (entityName instanceof Swc4jAstIdent ident) {
                    String typeName = ident.getSym();
                    return mapTypeNameToDescriptor(typeName, options);
                }
            }
            // If we can't determine the cast type, fall back to inferring from the expression
            return inferTypeFromExpr(asExpr.getExpr(), context, options);
        } else if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                return "I";
            }
            return "D";
        } else if (expr instanceof Swc4jAstBool) {
            return "Z";
        } else if (expr instanceof Swc4jAstNull) {
            // null has no specific type - it's compatible with any reference type
            // Return null to indicate that the type should be determined by context
            return null;
        } else if (expr instanceof Swc4jAstArrayLit) {
            // Array literal - maps to ArrayList
            return "Ljava/util/ArrayList;";
        } else if (expr instanceof Swc4jAstObjectLit) {
            // Object literal - maps to LinkedHashMap
            return "Ljava/util/LinkedHashMap;";
        } else if (expr instanceof Swc4jAstMemberExpr memberExpr) {
            // Member expression - handle array-like properties
            String objType = inferTypeFromExpr(memberExpr.getObj(), context, options);

            if (objType != null && objType.startsWith("[")) {
                // Java array operations
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                    // arr[index] returns the element type
                    return objType.substring(1); // Remove leading "["
                }
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        return "I"; // arr.length returns int
                    }
                }
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // ArrayList operations
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    String propName = propIdent.getSym();
                    if ("length".equals(propName)) {
                        return "I"; // arr.length returns int
                    }
                }
            } else if ("Ljava/util/LinkedHashMap;".equals(objType)) {
                // LinkedHashMap operations (object literal member access)
                // map.get() returns Object
                return "Ljava/lang/Object;";
            }
            return "Ljava/lang/Object;";
        } else if (expr instanceof Swc4jAstStr) {
            return "Ljava/lang/String;";
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            // Assignment expression returns the type of the value being assigned
            return inferTypeFromExpr(assignExpr.getRight(), context, options);
        } else if (expr instanceof Swc4jAstIdent ident) {
            return context.getInferredTypes().getOrDefault(ident.getSym(), "Ljava/lang/Object;");
        } else if (expr instanceof Swc4jAstUpdateExpr updateExpr) {
            // Update expression (++/--) returns the type of the operand
            return inferTypeFromExpr(updateExpr.getArg(), context, options);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            switch (binExpr.getOp()) {
                case Add -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // String concatenation - if either operand is String, result is String
                    if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                        return "Ljava/lang/String;";
                    }
                    // Numeric addition - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Sub -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Numeric subtraction - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Mul -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Numeric multiplication - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Div -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Numeric division - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Mod -> {
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Numeric modulo - use type widening rules
                    return getWidenedType(leftType, rightType);
                }
                case Exp -> {
                    // Exponentiation always returns double (Math.pow returns double)
                    return "D";
                }
                case LShift -> {
                    // Left shift returns the type of the left operand (int or long)
                    // For JavaScript semantics, we convert to int (ToInt32), but JVM supports both
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if ("J".equals(primitiveType)) {
                        return "J";
                    }
                    return "I";
                }
                case RShift -> {
                    // Right shift returns the type of the left operand (int or long)
                    // Same semantics as left shift
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if ("J".equals(primitiveType)) {
                        return "J";
                    }
                    return "I";
                }
                case ZeroFillRShift -> {
                    // Zero-fill right shift returns the type of the left operand (int or long)
                    // Same semantics as other shift operations
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    // Get primitive type
                    String primitiveType = getPrimitiveType(leftType);
                    // If long, keep as long; otherwise, convert to int (JavaScript ToInt32)
                    if ("J".equals(primitiveType)) {
                        return "J";
                    }
                    return "I";
                }
                case BitAnd -> {
                    // Bitwise AND uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Use type widening rules (int & long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case BitOr -> {
                    // Bitwise OR uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Use type widening rules (int | long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case BitXor -> {
                    // Bitwise XOR uses type widening - result is wider of the two operand types
                    String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                    String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                    // Handle null types - default to Object for null literals
                    if (leftType == null) leftType = "Ljava/lang/Object;";
                    if (rightType == null) rightType = "Ljava/lang/Object;";
                    // Use type widening rules (int ^ long → long, etc.)
                    return getWidenedType(leftType, rightType);
                }
                case EqEq, EqEqEq, NotEq, NotEqEq, Lt, LtEq, Gt, GtEq, LogicalAnd, LogicalOr -> {
                    // Equality, inequality, relational comparisons, and logical operations return boolean
                    return "Z";
                }
            }
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            // For unary expressions, infer type from the argument
            return inferTypeFromExpr(unaryExpr.getArg(), context, options);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            // For parenthesized expressions, infer type from the inner expression
            return inferTypeFromExpr(parenExpr.getExpr(), context, options);
        } else if (expr instanceof Swc4jAstCallExpr callExpr) {
            // For call expressions, try to infer the return type
            if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
                String objType = inferTypeFromExpr(memberExpr.getObj(), context, options);

                // ArrayList methods
                if ("Ljava/util/ArrayList;".equals(objType)) {
                    if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                        String methodName = propIdent.getSym();
                        // Methods that return ArrayList
                        switch (methodName) {
                            case "concat", "reverse", "sort", "slice", "splice", "fill", "copyWithin", "toReversed",
                                 "toSorted", "with", "toSpliced" -> {
                                return "Ljava/util/ArrayList;";
                            }
                            case "join", "toString", "toLocaleString" -> {
                                return "Ljava/lang/String;";
                            }
                            case "indexOf", "lastIndexOf" -> {
                                return "I";
                            }
                            case "includes" -> {
                                return "Z";
                            }
                            case "pop", "shift" -> {
                                return "Ljava/lang/Object;";
                            }
                        }
                    }
                }
            }
            return "Ljava/lang/Object;";
        }
        return "Ljava/lang/Object;";
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
        if (toType.equals("Ljava/lang/Object;")) {
            return true;
        }

        // Number wrapper hierarchy: Integer/Long/Short/Byte/Float/Double → Number
        if (toType.equals("Ljava/lang/Number;")) {
            return fromType.equals("Ljava/lang/Integer;") ||
                    fromType.equals("Ljava/lang/Long;") ||
                    fromType.equals("Ljava/lang/Short;") ||
                    fromType.equals("Ljava/lang/Byte;") ||
                    fromType.equals("Ljava/lang/Float;") ||
                    fromType.equals("Ljava/lang/Double;");
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
            case "B" -> // byte
                    toPrimitive.equals("S") || toPrimitive.equals("I") || toPrimitive.equals("J") ||
                            toPrimitive.equals("F") || toPrimitive.equals("D");
            case "S" -> // short
                    toPrimitive.equals("I") || toPrimitive.equals("J") ||
                            toPrimitive.equals("F") || toPrimitive.equals("D");
            case "C" -> // char
                    toPrimitive.equals("I") || toPrimitive.equals("J") ||
                            toPrimitive.equals("F") || toPrimitive.equals("D");
            case "I" -> // int
                    toPrimitive.equals("J") || toPrimitive.equals("F") || toPrimitive.equals("D");
            case "J" -> // long
                    toPrimitive.equals("F") || toPrimitive.equals("D");
            case "F" -> // float
                    toPrimitive.equals("D");
            default -> false;
        };
    }

    public static String mapTsTypeToDescriptor(ISwc4jAstTsType tsType, ByteCodeCompilerOptions options) {
        if (tsType instanceof Swc4jAstTsArrayType arrayType) {
            // type[] syntax - maps to Java array
            String elemType = mapTsTypeToDescriptor(arrayType.getElemType(), options);
            return "[" + elemType;
        } else if (tsType instanceof Swc4jAstTsKeywordType keywordType) {
            // Handle TypeScript keyword types (boolean, number, string, etc.)
            return switch (keywordType.getKind()) {
                case TsBooleanKeyword -> "Z";
                case TsNumberKeyword -> "D";  // Default to double for number
                case TsStringKeyword -> "Ljava/lang/String;";
                case TsBigIntKeyword -> "J";  // Map to long
                case TsVoidKeyword -> "V";
                default -> "Ljava/lang/Object;";
            };
        } else if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
            ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
            if (entityName instanceof Swc4jAstIdent ident) {
                String typeName = ident.getSym();
                // Check if this is Array<T> generic syntax
                if ("Array".equals(typeName)) {
                    // Array<T> syntax - maps to List interface (more flexible than ArrayList)
                    return "Ljava/util/List;";
                }
                // Phase 2: Check if this is Record<K, V> generic syntax
                if ("Record".equals(typeName)) {
                    // Record<K, V> syntax - maps to LinkedHashMap
                    return "Ljava/util/LinkedHashMap;";
                }
                return mapTypeNameToDescriptor(typeName, options);
            }
        }
        // Default to Object for unknown types
        return "Ljava/lang/Object;";
    }

    public static String mapTypeNameToDescriptor(String typeName, ByteCodeCompilerOptions options) {
        // Resolve type alias first
        String resolvedType = options.typeAliasMap().getOrDefault(typeName, typeName);

        return switch (resolvedType) {
            case "int" -> "I";
            case "boolean" -> "Z";
            case "byte" -> "B";
            case "char" -> "C";
            case "short" -> "S";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            case "java.lang.String", "String" -> "Ljava/lang/String;";
            case "java.lang.Object", "Object" -> "Ljava/lang/Object;";
            case "void" -> "V";
            default -> "L" + resolvedType.replace('.', '/') + ";";
        };
    }

    /**
     * Parse {@code Record<K, V>} type annotation to extract generic type parameters.
     * <p>
     * Supports:
     * - {@code Record<string, number>} → GenericTypeInfo.of("Ljava/lang/String;", "I")
     * - {@code Record<number, string>} → GenericTypeInfo.of("Ljava/lang/Integer;", "Ljava/lang/String;")
     * - {@code Record<string, Record<string, number>>} → GenericTypeInfo.ofNested(...)
     *
     * @param tsType  TypeScript type annotation (expected to be TsTypeRef with type params)
     * @param options compiler options
     * @return GenericTypeInfo containing key and value type descriptors, or null if not a Record type
     */
    public static GenericTypeInfo parseRecordType(ISwc4jAstTsType tsType, ByteCodeCompilerOptions options) {
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
            return GenericTypeInfo.of("Ljava/lang/String;", "Ljava/lang/Object;");
        }

        Swc4jAstTsTypeParamInstantiation typeParams = typeParamsOpt.get();
        List<ISwc4jAstTsType> params = typeParams.getParams();

        if (params.size() != 2) {
            // Record type must have exactly 2 type parameters
            return null;
        }

        // Parse key type (first parameter)
        ISwc4jAstTsType keyTsType = params.get(0);
        String keyType = mapTsTypeToDescriptor(keyTsType, options);

        // Parse value type (second parameter)
        ISwc4jAstTsType valueTsType = params.get(1);

        // Check if value type is a nested Record
        GenericTypeInfo nestedInfo = parseRecordType(valueTsType, options);
        if (nestedInfo != null) {
            // Nested Record type: Record<K, Record<K2, V2>>
            return GenericTypeInfo.ofNested(keyType, nestedInfo);
        }

        // Simple Record type: Record<K, V>
        String valueType = mapTsTypeToDescriptor(valueTsType, options);
        return GenericTypeInfo.of(keyType, valueType);
    }
}
