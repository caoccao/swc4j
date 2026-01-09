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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;

import java.util.Optional;

public final class TypeResolver {
    private TypeResolver() {
    }

    public static String mapTypeNameToDescriptor(String typeName, ByteCodeCompilerOptions options) {
        // Resolve type alias first
        String resolvedType = options.typeAliasMap().getOrDefault(typeName, typeName);

        return switch (resolvedType) {
            case "int" -> "I";
            case "float" -> "F";
            case "double" -> "D";
            case "java.lang.String", "String" -> "Ljava/lang/String;";
            case "void" -> "V";
            default -> "L" + resolvedType.replace('.', '/') + ";";
        };
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
            if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
                ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
                if (entityName instanceof Swc4jAstIdent ident) {
                    String typeName = ident.getSym();
                    return mapTypeNameToDescriptor(typeName, options);
                }
            }
        }

        // Type inference from initializer
        if (init.isPresent()) {
            return inferTypeFromExpr(init.get(), context, options);
        }

        return "Ljava/lang/Object;"; // Default
    }

    public static String inferTypeFromExpr(
            ISwc4jAstExpr expr,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        if (expr instanceof Swc4jAstNumber number) {
            double value = number.getValue();
            if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                return "I";
            }
            return "D";
        } else if (expr instanceof Swc4jAstStr) {
            return "Ljava/lang/String;";
        } else if (expr instanceof Swc4jAstIdent ident) {
            return context.getInferredTypes().getOrDefault(ident.getSym(), "Ljava/lang/Object;");
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            if (binExpr.getOp() == Swc4jAstBinaryOp.Add) {
                String leftType = inferTypeFromExpr(binExpr.getLeft(), context, options);
                String rightType = inferTypeFromExpr(binExpr.getRight(), context, options);
                // String concatenation
                if ("Ljava/lang/String;".equals(leftType) || "Ljava/lang/String;".equals(rightType)) {
                    return "Ljava/lang/String;";
                }
                // Numeric addition
                return leftType;
            }
        }
        return "Ljava/lang/Object;";
    }

    public static ReturnTypeInfo analyzeReturnType(
            Swc4jAstFunction function,
            Swc4jAstBlockStmt body,
            CompilationContext context,
            ByteCodeCompilerOptions options) {
        // Check for explicit return type annotation first
        var returnTypeOpt = function.getReturnType();
        if (returnTypeOpt.isPresent()) {
            var returnTypeAnn = returnTypeOpt.get();
            var tsType = returnTypeAnn.getTypeAnn();
            if (tsType instanceof Swc4jAstTsTypeRef typeRef) {
                ISwc4jAstTsEntityName entityName = typeRef.getTypeName();
                if (entityName instanceof com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent ident) {
                    String typeName = ident.getSym();
                    String descriptor = mapTypeNameToDescriptor(typeName, options);
                    return switch (descriptor) {
                        case "I" -> new ReturnTypeInfo(ReturnType.INT, 1);
                        case "F" -> new ReturnTypeInfo(ReturnType.FLOAT, 1);
                        case "D" -> new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        case "Ljava/lang/String;" -> new ReturnTypeInfo(ReturnType.STRING, 1);
                        case "V" -> new ReturnTypeInfo(ReturnType.VOID, 0);
                        default -> new ReturnTypeInfo(ReturnType.STRING, 1); // Default to object reference
                    };
                }
            }
        }

        // Fall back to type inference from return statement
        for (ISwc4jAstStmt stmt : body.getStmts()) {
            if (stmt instanceof Swc4jAstReturnStmt returnStmt) {
                var argOpt = returnStmt.getArg();
                if (argOpt.isPresent()) {
                    ISwc4jAstExpr arg = argOpt.get();
                    if (arg instanceof Swc4jAstStr) {
                        return new ReturnTypeInfo(ReturnType.STRING, 1);
                    } else if (arg instanceof Swc4jAstNumber number) {
                        double value = number.getValue();
                        if (value == Math.floor(value) && !Double.isInfinite(value) && !Double.isNaN(value)) {
                            return new ReturnTypeInfo(ReturnType.INT, 1);
                        } else {
                            return new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        }
                    } else if (arg instanceof Swc4jAstIdent ident) {
                        String type = context.getInferredTypes().get(ident.getSym());
                        if ("I".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.INT, 1);
                        } else if ("F".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.FLOAT, 1);
                        } else if ("D".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.DOUBLE, 2);
                        } else if ("Ljava/lang/String;".equals(type)) {
                            return new ReturnTypeInfo(ReturnType.STRING, 1);
                        }
                    }
                }
                return new ReturnTypeInfo(ReturnType.VOID, 0);
            }
        }
        return new ReturnTypeInfo(ReturnType.VOID, 0);
    }
}
