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

import com.caoccao.javet.swc4j.asm.ClassWriter;
import com.caoccao.javet.swc4j.asm.CodeBuilder;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionGenerator {
    private CallExpressionGenerator() {
    }

    public static void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            CompilationContext context,
            ByteCodeCompilerOptions options) throws Swc4jByteCodeCompilerException {
        // Handle method calls on arrays (e.g., arr.push(value))
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

            if (objType != null && objType.startsWith("[")) {
                // Java array - method calls not supported
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }
                throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on Java arrays - arrays have fixed size");
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // Generate code for the object (ArrayList)
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options);

                // Get the method name
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }

                switch (methodName) {
                    case "push" -> {
                        // arr.push(value) -> arr.add(value)
                        if (!callExpr.getArgs().isEmpty()) {
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);
                            // Box if primitive
                            String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                                TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                            }
                            int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
                            code.invokevirtual(addMethod);
                            code.pop(); // Pop the boolean return value
                        }
                        return;
                    }
                    case "pop" -> {
                        // arr.pop() -> arr.remove(arr.size() - 1)
                        // Returns the removed element
                        code.dup(); // Duplicate ArrayList reference for size() call

                        int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                        code.invokevirtual(sizeMethod); // Get size

                        code.iconst(1);
                        code.isub(); // size - 1

                        int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                        code.invokevirtual(removeMethod); // Returns removed element

                        return;
                    }
                    case "shift" -> {
                        // arr.shift() -> arr.remove(0)
                        // Returns the removed element
                        code.iconst(0); // Index 0

                        int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                        code.invokevirtual(removeMethod); // Returns removed element

                        return;
                    }
                    case "unshift" -> {
                        // arr.unshift(value) -> arr.add(0, value)
                        if (!callExpr.getArgs().isEmpty()) {
                            code.iconst(0); // Index 0

                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);
                            // Box if primitive
                            String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                                TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                            }

                            int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(ILjava/lang/Object;)V");
                            code.invokevirtual(addMethod);
                        }
                        return;
                    }
                    case "indexOf" -> {
                        // arr.indexOf(elem) -> arr.indexOf(elem)
                        // Returns int: index or -1 if not found
                        if (!callExpr.getArgs().isEmpty()) {
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);
                            // Box argument if primitive
                            String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                                TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                            }

                            int indexOfMethod = cp.addMethodRef("java/util/ArrayList", "indexOf", "(Ljava/lang/Object;)I");
                            code.invokevirtual(indexOfMethod); // Returns int index
                        } else {
                            // No argument - pop ArrayList ref and return -1
                            code.pop();
                            code.iconst(-1);
                        }
                        return;
                    }
                    case "includes" -> {
                        // arr.includes(elem) -> arr.contains(elem)
                        // Returns boolean: true if element exists, false otherwise
                        if (!callExpr.getArgs().isEmpty()) {
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);
                            // Box argument if primitive
                            String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                                TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                            }

                            int containsMethod = cp.addMethodRef("java/util/ArrayList", "contains", "(Ljava/lang/Object;)Z");
                            code.invokevirtual(containsMethod); // Returns boolean
                        } else {
                            // No argument - pop ArrayList ref and return false
                            code.pop();
                            code.iconst(0); // false
                        }
                        return;
                    }
                    case "reverse" -> {
                        // arr.reverse() -> Collections.reverse(arr); returns void but we keep arr on stack
                        // JavaScript's reverse() returns the array itself (for chaining)
                        code.dup(); // Duplicate array reference for return

                        int reverseMethod = cp.addMethodRef("java/util/Collections", "reverse", "(Ljava/util/List;)V");
                        code.invokestatic(reverseMethod); // Reverse in place

                        // The duplicated array reference is now on top of stack, ready to return
                        return;
                    }
                    case "sort" -> {
                        // arr.sort() -> Collections.sort(arr); returns void but we keep arr on stack
                        // JavaScript's sort() returns the array itself (for chaining)
                        code.dup(); // Duplicate array reference for return

                        int sortMethod = cp.addMethodRef("java/util/Collections", "sort", "(Ljava/util/List;)V");
                        code.invokestatic(sortMethod); // Sort in place

                        // The duplicated array reference is now on top of stack, ready to return
                        return;
                    }
                    case "join" -> {
                        // arr.join(sep) -> ArrayHelper.join(arr, sep)
                        // JavaScript's join() returns a string
                        // Default separator is "," if not provided

                        if (callExpr.getArgs().isEmpty()) {
                            // No separator provided - use default ","
                            code.ldc(cp.addString(","));
                        } else {
                            // Get separator argument
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);

                            // If the separator is not a String, convert it
                            String argType = TypeResolver.inferTypeFromExpr(arg.getExpr(), context, options);
                            if (argType != null && !"Ljava/lang/String;".equals(argType)) {
                                // Convert to string using String.valueOf()
                                int valueOfMethod = cp.addMethodRef("java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
                                code.invokestatic(valueOfMethod);
                            }
                        }

                        // Call ArrayHelper.join(ArrayList, String)
                        int joinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayJoinUtils", "join",
                                "(Ljava/util/List;Ljava/lang/String;)Ljava/lang/String;");
                        code.invokestatic(joinMethod);

                        return;
                    }
                    case "concat" -> {
                        // arr.concat(arr2) -> ArrayJoinUtils.concat(arr, arr2)
                        // JavaScript's concat() returns a new array

                        if (!callExpr.getArgs().isEmpty()) {
                            // Get the second array argument
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);

                            // Call ArrayJoinUtils.concat(ArrayList, ArrayList)
                            int concatMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayJoinUtils", "concat",
                                    "(Ljava/util/ArrayList;Ljava/util/ArrayList;)Ljava/util/ArrayList;");
                            code.invokestatic(concatMethod);
                        } else {
                            // No argument - just return a copy of the array
                            int arrayListClass = cp.addClass("java/util/ArrayList");
                            int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");

                            code.newInstance(arrayListClass)
                                    .dup_x1()  // Duplicate new ArrayList ref, place it below the original array
                                    .swap()    // Swap to get: new ArrayList, original array, new ArrayList
                                    .invokespecial(arrayListInit);  // Call ArrayList(Collection)
                        }

                        return;
                    }
                }
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Call expression not yet supported");
    }
}
