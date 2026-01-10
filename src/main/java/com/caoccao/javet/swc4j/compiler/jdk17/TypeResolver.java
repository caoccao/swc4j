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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsArrayType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsKeywordType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

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
                return new ReturnTypeInfo(ReturnType.VOID, 0, null);
            }
        }
        return new ReturnTypeInfo(ReturnType.VOID, 0, null);
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

    private static String getPrimitiveType(String type) {
        return switch (type) {
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

    private static String getWidenedType(String leftType, String rightType) {
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

    public static String inferTypeFromExpr(
            ISwc4jAstExpr expr,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        if (expr instanceof Swc4jAstTsAsExpr asExpr) {
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
            }
            return "Ljava/lang/Object;";
        } else if (expr instanceof Swc4jAstStr) {
            return "Ljava/lang/String;";
        } else if (expr instanceof Swc4jAstAssignExpr assignExpr) {
            // Assignment expression returns the type of the value being assigned
            return inferTypeFromExpr(assignExpr.getRight(), context, options);
        } else if (expr instanceof Swc4jAstIdent ident) {
            return context.getInferredTypes().getOrDefault(ident.getSym(), "Ljava/lang/Object;");
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            if (binExpr.getOp() == Swc4jAstBinaryOp.Add) {
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
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            // For unary expressions, infer type from the argument
            return inferTypeFromExpr(unaryExpr.getArg(), context, options);
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            // For parenthesized expressions, infer type from the inner expression
            return inferTypeFromExpr(parenExpr.getExpr(), context, options);
        }
        return "Ljava/lang/Object;";
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
                    // Array<T> syntax - maps to ArrayList
                    return "Ljava/util/ArrayList;";
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
}
