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
                        int joinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "join",
                                "(Ljava/util/List;Ljava/lang/String;)Ljava/lang/String;");
                        code.invokestatic(joinMethod);

                        return;
                    }
                    case "concat" -> {
                        // arr.concat(arr2) -> ArrayApiUtils.concat(arr, arr2)
                        // JavaScript's concat() returns a new array

                        if (!callExpr.getArgs().isEmpty()) {
                            // Get the second array argument
                            var arg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, arg.getExpr(), null, context, options);

                            // Call ArrayApiUtils.concat(ArrayList, ArrayList)
                            int concatMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "concat",
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
                    case "slice" -> {
                        // arr.slice(start, end) -> ArrayApiUtils.slice(arr, start, end)
                        // JavaScript's slice() returns a new array with extracted elements

                        int argCount = callExpr.getArgs().size();

                        if (argCount == 0) {
                            // No arguments: arr.slice() - copy entire array
                            // Stack: ArrayList
                            code.iconst(0);  // start = 0
                            // Stack: ArrayList, 0

                            code.dup_x1();  // Duplicate start, place below ArrayList
                            // Stack: 0, ArrayList, 0

                            code.pop();  // Remove top 0
                            // Stack: 0, ArrayList

                            code.dup();  // Duplicate ArrayList for size() call
                            // Stack: 0, ArrayList, ArrayList

                            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                            code.invokevirtual(sizeMethod);  // Get size
                            // Stack: 0, ArrayList, size

                            // Now we need: ArrayList, start, end
                            // Current stack: 0, ArrayList, size
                            // Reorder to: ArrayList, 0, size

                            code.dup_x2();  // Duplicate size, place it before ArrayList
                            // Stack: size, 0, ArrayList, size

                            code.pop();  // Remove top size
                            // Stack: size, 0, ArrayList

                            code.dup_x2();  // Duplicate ArrayList
                            // Stack: ArrayList, size, 0, ArrayList

                            code.pop();  // Remove top ArrayList
                            // Stack: ArrayList, size, 0

                            code.swap();  // Swap to get: ArrayList, 0, size
                            // Stack: ArrayList, 0, size

                        } else if (argCount == 1) {
                            // One argument: arr.slice(start) - from start to end
                            // Stack: ArrayList

                            var startArg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, startArg.getExpr(), null, context, options);
                            // Stack: ArrayList, start

                            // Need to unbox if Integer
                            String startType = TypeResolver.inferTypeFromExpr(startArg.getExpr(), context, options);
                            if (startType != null && "Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start (int)

                            code.dup_x1();  // Duplicate start
                            // Stack: start, ArrayList, start

                            code.pop();  // Remove top start
                            // Stack: start, ArrayList

                            code.dup();  // Duplicate ArrayList for size() call
                            // Stack: start, ArrayList, ArrayList

                            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
                            code.invokevirtual(sizeMethod);  // Get size
                            // Stack: start, ArrayList, size

                            // Reorder to: ArrayList, start, size
                            code.dup_x2();  // Duplicate size
                            // Stack: size, start, ArrayList, size

                            code.pop();  // Remove top size
                            // Stack: size, start, ArrayList

                            code.dup_x2();  // Duplicate ArrayList
                            // Stack: ArrayList, size, start, ArrayList

                            code.pop();  // Remove top ArrayList
                            // Stack: ArrayList, size, start

                            code.swap();  // Swap to get: ArrayList, start, size
                            // Stack: ArrayList, start, size

                        } else {
                            // Two arguments: arr.slice(start, end)
                            // Stack: ArrayList

                            var startArg = callExpr.getArgs().get(0);
                            ExpressionGenerator.generate(code, cp, startArg.getExpr(), null, context, options);
                            // Stack: ArrayList, start

                            // Unbox if Integer
                            String startType = TypeResolver.inferTypeFromExpr(startArg.getExpr(), context, options);
                            if (startType != null && "Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start

                            var endArg = callExpr.getArgs().get(1);
                            ExpressionGenerator.generate(code, cp, endArg.getExpr(), null, context, options);
                            // Stack: ArrayList, start, end

                            // Unbox if Integer
                            String endType = TypeResolver.inferTypeFromExpr(endArg.getExpr(), context, options);
                            if (endType != null && "Ljava/lang/Integer;".equals(endType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start, end
                        }

                        // Call ArrayApiUtils.slice(ArrayList, int, int)
                        int sliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "slice",
                                "(Ljava/util/ArrayList;II)Ljava/util/ArrayList;");
                        code.invokestatic(sliceMethod);

                        return;
                    }
                }
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Call expression not yet supported");
    }
}
