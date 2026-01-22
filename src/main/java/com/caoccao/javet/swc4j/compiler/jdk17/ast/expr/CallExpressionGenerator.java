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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Handle method calls on arrays (e.g., arr.push(value))
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());

            if (objType != null && objType.startsWith("[")) {
                // Java array - method calls not supported
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }
                throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on Java arrays - arrays have fixed size");
            } else if ("Ljava/util/ArrayList;".equals(objType)) {
                // Generate code for the object (ArrayList)
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            // Box if primitive
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            // Box if primitive
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            // Box argument if primitive
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
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
                    case "lastIndexOf" -> {
                        // arr.lastIndexOf(elem) -> arr.lastIndexOf(elem)
                        // Returns int: last index or -1 if not found
                        if (!callExpr.getArgs().isEmpty()) {
                            var arg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            // Box argument if primitive
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
                            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                                TypeConversionUtils.boxPrimitiveType(code, cp, argType, TypeConversionUtils.getWrapperType(argType));
                            }

                            int lastIndexOfMethod = cp.addMethodRef("java/util/ArrayList", "lastIndexOf", "(Ljava/lang/Object;)I");
                            code.invokevirtual(lastIndexOfMethod); // Returns int index
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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            // Box argument if primitive
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
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
                    case "toReversed" -> {
                        // arr.toReversed() -> ArrayApiUtils.toReversed(arr)
                        // Returns new reversed array without modifying original (ES2023)
                        // Stack: ArrayList

                        // Call ArrayApiUtils.toReversed(ArrayList)
                        int toReversedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toReversed",
                                "(Ljava/util/ArrayList;)Ljava/util/ArrayList;");
                        code.invokestatic(toReversedMethod);
                        // Stack: new ArrayList (reversed)

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
                    case "toSorted" -> {
                        // arr.toSorted() -> ArrayApiUtils.toSorted(arr)
                        // Returns new sorted array without modifying original (ES2023)
                        // Stack: ArrayList

                        // Call ArrayApiUtils.toSorted(ArrayList)
                        int toSortedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toSorted",
                                "(Ljava/util/ArrayList;)Ljava/util/ArrayList;");
                        code.invokestatic(toSortedMethod);
                        // Stack: new ArrayList (sorted)

                        return;
                    }
                    case "with" -> {
                        // arr.with(index, value) -> ArrayApiUtils.with(arr, index, value)
                        // Returns new array with one element changed (ES2023)
                        // Stack: ArrayList

                        if (callExpr.getArgs().size() < 2) {
                            throw new Swc4jByteCodeCompilerException("with() requires two arguments (index, value)");
                        }

                        // Generate index argument
                        var indexArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, indexArg.getExpr(), null);

                        // Unbox index if needed
                        String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(indexType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        // Generate value argument
                        var valueArg = callExpr.getArgs().get(1);
                        compiler.getExpressionGenerator().generate(code, cp, valueArg.getExpr(), null);

                        // Box value if primitive
                        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
                        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                            TypeConversionUtils.boxPrimitiveType(code, cp, valueType, TypeConversionUtils.getWrapperType(valueType));
                        }

                        // Stack: ArrayList, index (int), value (Object)
                        // Call ArrayApiUtils.with(ArrayList, int, Object)
                        int withMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "with",
                                "(Ljava/util/ArrayList;ILjava/lang/Object;)Ljava/util/ArrayList;");
                        code.invokestatic(withMethod);
                        // Stack: new ArrayList (with element changed)

                        return;
                    }
                    case "toSpliced" -> {
                        // arr.toSpliced(start, deleteCount, ...items) -> ArrayApiUtils.toSpliced(arr, start, deleteCount, items)
                        // Returns new array with elements removed/inserted (ES2023 non-mutating)
                        // Stack: ArrayList

                        int argCount = callExpr.getArgs().size();

                        if (argCount == 0) {
                            // No arguments: toSpliced() - returns copy with no changes
                            code.iconst(0);  // start = 0
                            code.iconst(0);  // deleteCount = 0
                            code.aconst_null();  // items = null
                        } else if (argCount == 1) {
                            // One argument: toSpliced(start) - remove from start to end
                            var startArg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                            // Unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start

                            // deleteCount = Integer.MAX_VALUE (remove all after start)
                            code.ldc(cp.addInteger(Integer.MAX_VALUE));
                            // Stack: ArrayList, start, deleteCount

                            code.aconst_null();  // items = null
                            // Stack: ArrayList, start, deleteCount, null
                        } else {
                            // Two or more arguments: toSpliced(start, deleteCount, ...items)
                            // Generate start parameter
                            var startArg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                            // Unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start

                            // Generate deleteCount parameter
                            var deleteCountArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, deleteCountArg.getExpr(), null);

                            // Unbox if Integer
                            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(deleteCountType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start, deleteCount

                            // Create ArrayList for items to insert (if any)
                            if (argCount > 2) {
                                // Create new ArrayList for items
                                int arrayListClass = cp.addClass("java/util/ArrayList");
                                int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                                int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

                                code.newInstance(arrayListClass);
                                code.dup();
                                code.invokespecial(arrayListInit);
                                // Stack: ArrayList, start, deleteCount, itemsList

                                // Add each item to the items ArrayList
                                for (int i = 2; i < argCount; i++) {
                                    code.dup();  // Duplicate itemsList for add() call
                                    // Stack: ArrayList, start, deleteCount, itemsList, itemsList

                                    var itemArg = callExpr.getArgs().get(i);
                                    compiler.getExpressionGenerator().generate(code, cp, itemArg.getExpr(), null);
                                    // Stack: ArrayList, start, deleteCount, itemsList, itemsList, item

                                    // Box if primitive
                                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                                        TypeConversionUtils.boxPrimitiveType(code, cp, itemType, TypeConversionUtils.getWrapperType(itemType));
                                    }

                                    code.invokevirtual(addMethod);
                                    code.pop();  // Pop the boolean return value
                                    // Stack: ArrayList, start, deleteCount, itemsList
                                }
                                // Stack: ArrayList, start, deleteCount, itemsList
                            } else {
                                // No items to insert
                                code.aconst_null();
                                // Stack: ArrayList, start, deleteCount, null
                            }
                        }

                        // Call ArrayApiUtils.toSpliced(ArrayList, int, int, ArrayList)
                        int toSplicedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "toSpliced",
                                "(Ljava/util/ArrayList;IILjava/util/ArrayList;)Ljava/util/ArrayList;");
                        code.invokestatic(toSplicedMethod);
                        // Stack: new ArrayList (with modifications applied)

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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);

                            // If the separator is not a String, convert it
                            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
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
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);

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
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);
                            // Stack: ArrayList, start

                            // Need to unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
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
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);
                            // Stack: ArrayList, start

                            // Unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, start

                            var endArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);
                            // Stack: ArrayList, start, end

                            // Unbox if Integer
                            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(endType)) {
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
                    case "splice" -> {
                        // arr.splice(start, deleteCount, ...items) -> ArrayApiUtils.splice(arr, start, deleteCount, items)
                        // JavaScript's splice() mutates the array and returns removed elements

                        int argCount = callExpr.getArgs().size();

                        // Stack: ArrayList
                        // Keep ArrayList reference for later (splice mutates it)
                        code.dup();
                        // Stack: ArrayList, ArrayList

                        if (argCount == 0) {
                            // No arguments: splice() - remove nothing, return empty array
                            code.iconst(0);  // start = 0
                            code.iconst(0);  // deleteCount = 0
                            code.aconst_null();  // items = null
                        } else if (argCount == 1) {
                            // One argument: splice(start) - remove from start to end
                            var startArg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                            // Unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, ArrayList, start

                            // deleteCount = array.length - start (remove all after start)
                            // We need to calculate this, but for simplicity, use Integer.MAX_VALUE
                            code.ldc(cp.addInteger(Integer.MAX_VALUE));
                            // Stack: ArrayList, ArrayList, start, deleteCount

                            code.aconst_null();  // items = null
                            // Stack: ArrayList, ArrayList, start, deleteCount, null
                        } else {
                            // Two or more arguments: splice(start, deleteCount, ...items)
                            // Generate start parameter
                            var startArg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                            // Unbox if Integer
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, ArrayList, start

                            // Generate deleteCount parameter
                            var deleteCountArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, deleteCountArg.getExpr(), null);

                            // Unbox if Integer
                            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(deleteCountType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, ArrayList, start, deleteCount

                            // Create ArrayList for items to insert (if any)
                            if (argCount > 2) {
                                // Create new ArrayList for items
                                int arrayListClass = cp.addClass("java/util/ArrayList");
                                int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                                int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

                                code.newInstance(arrayListClass);
                                code.dup();
                                code.invokespecial(arrayListInit);
                                // Stack: ArrayList, ArrayList, start, deleteCount, itemsList

                                // Add each item to the items ArrayList
                                for (int i = 2; i < argCount; i++) {
                                    code.dup();  // Duplicate itemsList for add() call
                                    // Stack: ArrayList, ArrayList, start, deleteCount, itemsList, itemsList

                                    var itemArg = callExpr.getArgs().get(i);
                                    compiler.getExpressionGenerator().generate(code, cp, itemArg.getExpr(), null);
                                    // Stack: ArrayList, ArrayList, start, deleteCount, itemsList, itemsList, item

                                    // Box if primitive
                                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                                        TypeConversionUtils.boxPrimitiveType(code, cp, itemType, TypeConversionUtils.getWrapperType(itemType));
                                    }

                                    code.invokevirtual(addMethod);
                                    code.pop();  // Pop the boolean return value
                                    // Stack: ArrayList, ArrayList, start, deleteCount, itemsList
                                }
                                // Stack: ArrayList, ArrayList, start, deleteCount, itemsList
                            } else {
                                // No items to insert
                                code.aconst_null();
                                // Stack: ArrayList, ArrayList, start, deleteCount, null
                            }
                        }

                        // Call ArrayApiUtils.splice(ArrayList, int, int, ArrayList)
                        int spliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "splice",
                                "(Ljava/util/ArrayList;IILjava/util/ArrayList;)Ljava/util/ArrayList;");
                        code.invokestatic(spliceMethod);
                        // Stack: ArrayList, removedElements

                        // Splice returns the removed elements, but we also kept the original array on stack
                        // JavaScript splice returns removed elements, so we need to pop the original array and keep removed
                        code.swap();  // Swap to get: removedElements, ArrayList
                        code.pop();   // Pop the original ArrayList
                        // Stack: removedElements

                        return;
                    }
                    case "fill" -> {
                        // arr.fill(value, start, end) -> ArrayApiUtils.fill(arr, value, [start], [end])
                        // Returns the array itself (mutates in place)
                        int argCount = callExpr.getArgs().size();

                        if (argCount == 0) {
                            // No value provided - throw error
                            throw new Swc4jByteCodeCompilerException("fill() requires at least one argument (value)");
                        }

                        // Stack starts with: ArrayList

                        // Generate the value argument
                        var valueArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, valueArg.getExpr(), null);
                        // Stack: ArrayList, value

                        // Box primitive value if needed
                        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
                        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
                            TypeConversionUtils.boxPrimitiveType(code, cp, valueType, TypeConversionUtils.getWrapperType(valueType));
                        }
                        // Stack: ArrayList, value (Object)

                        if (argCount == 1) {
                            // fill(value) - fill entire array
                            // Stack: ArrayList, value
                            // Call ArrayApiUtils.fill(ArrayList, Object)
                            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "fill",
                                    "(Ljava/util/ArrayList;Ljava/lang/Object;)Ljava/util/ArrayList;");
                            code.invokestatic(fillMethod);

                        } else if (argCount == 2) {
                            // fill(value, start) - fill from start to end
                            var startArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);
                            // Stack: ArrayList, value, start

                            // Unbox if needed
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, value, start

                            // Call ArrayApiUtils.fill(ArrayList, Object, int)
                            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "fill",
                                    "(Ljava/util/ArrayList;Ljava/lang/Object;I)Ljava/util/ArrayList;");
                            code.invokestatic(fillMethod);

                        } else {
                            // fill(value, start, end) - three arguments
                            var startArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                            // Unbox start if needed
                            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(startType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, value, start

                            var endArg = callExpr.getArgs().get(2);
                            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

                            // Unbox end if needed
                            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(endType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, value, start, end

                            // Call ArrayApiUtils.fill(ArrayList, Object, int, int)
                            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "fill",
                                    "(Ljava/util/ArrayList;Ljava/lang/Object;II)Ljava/util/ArrayList;");
                            code.invokestatic(fillMethod);
                        }
                        // Stack: ArrayList (returned)

                        return;
                    }
                    case "copyWithin" -> {
                        // arr.copyWithin(target, start, end) -> ArrayApiUtils.copyWithin(arr, target, start, [end])
                        // Returns the array itself (mutates in place)
                        int argCount = callExpr.getArgs().size();

                        if (argCount < 2) {
                            // Need at least target and start
                            throw new Swc4jByteCodeCompilerException("copyWithin() requires at least two arguments (target, start)");
                        }

                        // Stack starts with: ArrayList

                        // Generate the target argument
                        var targetArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, targetArg.getExpr(), null);
                        // Stack: ArrayList, target

                        // Unbox target if needed
                        String targetType = compiler.getTypeResolver().inferTypeFromExpr(targetArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(targetType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }
                        // Stack: ArrayList, target (int)

                        // Generate the start argument
                        var startArg = callExpr.getArgs().get(1);
                        compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);
                        // Stack: ArrayList, target, start

                        // Unbox start if needed
                        String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(startType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }
                        // Stack: ArrayList, target, start

                        if (argCount == 2) {
                            // copyWithin(target, start) - copy from start to end
                            // Stack: ArrayList, target, start

                            // Call ArrayApiUtils.copyWithin(ArrayList, int, int)
                            int copyWithinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "copyWithin",
                                    "(Ljava/util/ArrayList;II)Ljava/util/ArrayList;");
                            code.invokestatic(copyWithinMethod);

                        } else {
                            // copyWithin(target, start, end) - three arguments
                            var endArg = callExpr.getArgs().get(2);
                            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

                            // Unbox end if needed
                            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(endType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }
                            // Stack: ArrayList, target, start, end

                            // Call ArrayApiUtils.copyWithin(ArrayList, int, int, int)
                            int copyWithinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "copyWithin",
                                    "(Ljava/util/ArrayList;III)Ljava/util/ArrayList;");
                            code.invokestatic(copyWithinMethod);
                        }
                        // Stack: ArrayList (returned)

                        return;
                    }
                    case "toString" -> {
                        // arr.toString() -> ArrayApiUtils.arrayToString(arr)
                        // Returns string representation (comma-separated values)
                        // Stack: ArrayList

                        // Call ArrayApiUtils.arrayToString(List)
                        int toStringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "arrayToString",
                                "(Ljava/util/List;)Ljava/lang/String;");
                        code.invokestatic(toStringMethod);
                        // Stack: String

                        return;
                    }
                    case "toLocaleString" -> {
                        // arr.toLocaleString() -> ArrayApiUtils.arrayToLocaleString(arr)
                        // Returns locale-specific string representation (comma-separated values)
                        // Stack: ArrayList

                        // Call ArrayApiUtils.arrayToLocaleString(List)
                        int toLocaleStringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayApiUtils", "arrayToLocaleString",
                                "(Ljava/util/List;)Ljava/lang/String;");
                        code.invokestatic(toLocaleStringMethod);
                        // Stack: String

                        return;
                    }
                }
            } else if ("Ljava/lang/String;".equals(objType)) {
                // Generate code for the object (String)
                compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

                // Get the method name
                String methodName = null;
                if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                    methodName = propIdent.getSym();
                }

                switch (methodName) {
                    // Priority 1: Direct Java equivalents
                    case "indexOf" -> {
                        // str.indexOf(searchString) or str.indexOf(searchString, position)
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return -1
                            code.pop(); // Pop string reference
                            code.iconst(-1);
                            return;
                        }

                        // First argument: search string
                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        if (callExpr.getArgs().size() > 1) {
                            // Second argument: fromIndex
                            var fromIndexArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, fromIndexArg.getExpr(), null);

                            // Unbox if Integer
                            String fromIndexType = compiler.getTypeResolver().inferTypeFromExpr(fromIndexArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(fromIndexType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            // Call indexOf(String, int)
                            int indexOfMethod = cp.addMethodRef("java/lang/String", "indexOf", "(Ljava/lang/String;I)I");
                            code.invokevirtual(indexOfMethod);
                        } else {
                            // Call indexOf(String)
                            int indexOfMethod = cp.addMethodRef("java/lang/String", "indexOf", "(Ljava/lang/String;)I");
                            code.invokevirtual(indexOfMethod);
                        }
                        return;
                    }
                    case "lastIndexOf" -> {
                        // str.lastIndexOf(searchString) or str.lastIndexOf(searchString, position)
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return -1
                            code.pop();
                            code.iconst(-1);
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        if (callExpr.getArgs().size() > 1) {
                            var fromIndexArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, fromIndexArg.getExpr(), null);

                            String fromIndexType = compiler.getTypeResolver().inferTypeFromExpr(fromIndexArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(fromIndexType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            int lastIndexOfMethod = cp.addMethodRef("java/lang/String", "lastIndexOf", "(Ljava/lang/String;I)I");
                            code.invokevirtual(lastIndexOfMethod);
                        } else {
                            int lastIndexOfMethod = cp.addMethodRef("java/lang/String", "lastIndexOf", "(Ljava/lang/String;)I");
                            code.invokevirtual(lastIndexOfMethod);
                        }
                        return;
                    }
                    case "toLowerCase" -> {
                        // str.toLowerCase()
                        int toLowerMethod = cp.addMethodRef("java/lang/String", "toLowerCase", "()Ljava/lang/String;");
                        code.invokevirtual(toLowerMethod);
                        return;
                    }
                    case "toUpperCase" -> {
                        // str.toUpperCase()
                        int toUpperMethod = cp.addMethodRef("java/lang/String", "toUpperCase", "()Ljava/lang/String;");
                        code.invokevirtual(toUpperMethod);
                        return;
                    }
                    case "trim" -> {
                        // str.trim()
                        int trimMethod = cp.addMethodRef("java/lang/String", "trim", "()Ljava/lang/String;");
                        code.invokevirtual(trimMethod);
                        return;
                    }
                    case "concat" -> {
                        // str.concat(str1, str2, ...) - chain multiple concat calls
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return original string (already on stack)
                            return;
                        }

                        // For each argument, call concat
                        for (var arg : callExpr.getArgs()) {
                            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                            int concatMethod = cp.addMethodRef("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
                            code.invokevirtual(concatMethod);
                            // Stack now has result, which becomes receiver for next concat
                        }
                        return;
                    }
                    case "startsWith" -> {
                        // str.startsWith(searchString) or str.startsWith(searchString, position)
                        if (callExpr.getArgs().isEmpty()) {
                            code.pop();
                            code.iconst(0); // false
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        if (callExpr.getArgs().size() > 1) {
                            // With position
                            var posArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, posArg.getExpr(), null);

                            String posType = compiler.getTypeResolver().inferTypeFromExpr(posArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(posType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            int startsWithMethod = cp.addMethodRef("java/lang/String", "startsWith", "(Ljava/lang/String;I)Z");
                            code.invokevirtual(startsWithMethod);
                        } else {
                            int startsWithMethod = cp.addMethodRef("java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
                            code.invokevirtual(startsWithMethod);
                        }
                        return;
                    }
                    case "endsWith" -> {
                        // str.endsWith(searchString)
                        if (callExpr.getArgs().isEmpty()) {
                            code.pop();
                            code.iconst(0); // false
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        int endsWithMethod = cp.addMethodRef("java/lang/String", "endsWith", "(Ljava/lang/String;)Z");
                        code.invokevirtual(endsWithMethod);
                        return;
                    }
                    case "includes" -> {
                        // str.includes(searchString)
                        if (callExpr.getArgs().isEmpty()) {
                            code.pop();
                            code.iconst(0); // false
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        int containsMethod = cp.addMethodRef("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
                        code.invokevirtual(containsMethod);
                        return;
                    }
                    case "repeat" -> {
                        // str.repeat(count)
                        if (callExpr.getArgs().isEmpty()) {
                            // No count - return empty string
                            code.pop();
                            int emptyStrIndex = cp.addString("");
                            code.ldc(emptyStrIndex);
                            return;
                        }

                        var countArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, countArg.getExpr(), null);

                        // Unbox if Integer
                        String countType = compiler.getTypeResolver().inferTypeFromExpr(countArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(countType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        // Call String.repeat(int) - Available in JDK 11+
                        int repeatMethod = cp.addMethodRef("java/lang/String", "repeat", "(I)Ljava/lang/String;");
                        code.invokevirtual(repeatMethod);
                        return;
                    }
                    case "replaceAll" -> {
                        // str.replaceAll(search, replacement)
                        // Use Java's replace(CharSequence, CharSequence) which replaces ALL
                        if (callExpr.getArgs().size() < 2) {
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        var replacementArg = callExpr.getArgs().get(1);
                        compiler.getExpressionGenerator().generate(code, cp, replacementArg.getExpr(), null);

                        int replaceMethod = cp.addMethodRef("java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
                        code.invokevirtual(replaceMethod);
                        return;
                    }

                    // Priority 2: Methods needing edge case handling via StringApiUtils
                    case "charAt" -> {
                        // Use StringApiUtils.charAt to handle bounds checking
                        if (!callExpr.getArgs().isEmpty()) {
                            var indexArg = callExpr.getArgs().get(0);
                            compiler.getExpressionGenerator().generate(code, cp, indexArg.getExpr(), null);

                            // Unbox if Integer wrapper
                            String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(indexType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            // Call StringApiUtils.charAt(String, int) -> String
                            int charAtMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "charAt", "(Ljava/lang/String;I)Ljava/lang/String;");
                            code.invokestatic(charAtMethod);
                        } else {
                            // No argument - pop string and push empty string
                            code.pop();
                            int emptyStrIndex = cp.addString("");
                            code.ldc(emptyStrIndex);
                        }
                        return;
                    }
                    case "substring" -> {
                        // Use StringApiUtils.substring to handle edge cases
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return whole string
                            return;
                        }

                        var startArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                        String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(startType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        if (callExpr.getArgs().size() > 1) {
                            // Two arguments: substring(start, end)
                            var endArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

                            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(endType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            // Call StringApiUtils.substring(String, int, int)
                            int substringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "substring", "(Ljava/lang/String;II)Ljava/lang/String;");
                            code.invokestatic(substringMethod);
                        } else {
                            // One argument: substring(start)
                            int substringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "substring", "(Ljava/lang/String;I)Ljava/lang/String;");
                            code.invokestatic(substringMethod);
                        }
                        return;
                    }
                    case "slice" -> {
                        // Use StringApiUtils.slice to handle negative indices
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return whole string
                            return;
                        }

                        var startArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

                        String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(startType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        if (callExpr.getArgs().size() > 1) {
                            // Two arguments: slice(start, end)
                            var endArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

                            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(endType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            // Call StringApiUtils.slice(String, int, int)
                            int sliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "slice", "(Ljava/lang/String;II)Ljava/lang/String;");
                            code.invokestatic(sliceMethod);
                        } else {
                            // One argument: slice(start)
                            int sliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "slice", "(Ljava/lang/String;I)Ljava/lang/String;");
                            code.invokestatic(sliceMethod);
                        }
                        return;
                    }
                    case "split" -> {
                        // Returns ArrayList<String> (JavaScript returns Array)
                        if (callExpr.getArgs().isEmpty()) {
                            // No separator - call StringApiUtils.split with null separator
                            code.aconst_null();

                            // Call StringApiUtils.split(String, String) -> ArrayList<String>
                            int splitMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "split", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
                            code.invokestatic(splitMethod);
                            return;
                        }

                        var separatorArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, separatorArg.getExpr(), null);

                        if (callExpr.getArgs().size() > 1) {
                            // With limit
                            var limitArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, limitArg.getExpr(), null);

                            String limitType = compiler.getTypeResolver().inferTypeFromExpr(limitArg.getExpr());
                            if ("Ljava/lang/Integer;".equals(limitType)) {
                                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                                code.invokevirtual(intValueMethod);
                            }

                            // Call StringApiUtils.split(String, String, int) -> ArrayList<String>
                            int splitMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "split", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/util/ArrayList;");
                            code.invokestatic(splitMethod);
                        } else {
                            // Without limit
                            // Call StringApiUtils.split(String, String) -> ArrayList<String>
                            int splitMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "split", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
                            code.invokestatic(splitMethod);
                        }
                        return;
                    }
                    case "replace" -> {
                        // Use StringApiUtils.replace to match JavaScript behavior (first occurrence only, literal)
                        if (callExpr.getArgs().size() < 2) {
                            // Not enough arguments - return original string
                            return;
                        }

                        var searchArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

                        var replacementArg = callExpr.getArgs().get(1);
                        compiler.getExpressionGenerator().generate(code, cp, replacementArg.getExpr(), null);

                        // Call StringApiUtils.replace(String, String, String)
                        int replaceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "replace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
                        code.invokestatic(replaceMethod);
                        return;
                    }

                    // Priority 3: Complex methods requiring helper utilities
                    case "charCodeAt" -> {
                        // Use StringApiUtils.charCodeAt
                        if (callExpr.getArgs().isEmpty()) {
                            // No index - return -1 (NaN)
                            code.pop();
                            code.iconst(-1);
                            return;
                        }

                        var indexArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, indexArg.getExpr(), null);

                        String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(indexType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        // Call StringApiUtils.charCodeAt(String, int) -> int
                        int charCodeAtMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "charCodeAt", "(Ljava/lang/String;I)I");
                        code.invokestatic(charCodeAtMethod);
                        return;
                    }
                    case "padStart" -> {
                        // Use StringApiUtils.padStart
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return original string
                            return;
                        }

                        var targetLengthArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, targetLengthArg.getExpr(), null);

                        String lengthType = compiler.getTypeResolver().inferTypeFromExpr(targetLengthArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(lengthType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        if (callExpr.getArgs().size() > 1) {
                            // Custom pad string
                            var padStringArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, padStringArg.getExpr(), null);

                            // Call StringApiUtils.padStart(String, int, String)
                            int padStartMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "padStart", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;");
                            code.invokestatic(padStartMethod);
                        } else {
                            // Default pad string " "
                            int spaceIndex = cp.addString(" ");
                            code.ldc(spaceIndex);

                            int padStartMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "padStart", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;");
                            code.invokestatic(padStartMethod);
                        }
                        return;
                    }
                    case "padEnd" -> {
                        // Use StringApiUtils.padEnd
                        if (callExpr.getArgs().isEmpty()) {
                            // No arguments - return original string
                            return;
                        }

                        var targetLengthArg = callExpr.getArgs().get(0);
                        compiler.getExpressionGenerator().generate(code, cp, targetLengthArg.getExpr(), null);

                        String lengthType = compiler.getTypeResolver().inferTypeFromExpr(targetLengthArg.getExpr());
                        if ("Ljava/lang/Integer;".equals(lengthType)) {
                            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                            code.invokevirtual(intValueMethod);
                        }

                        if (callExpr.getArgs().size() > 1) {
                            // Custom pad string
                            var padStringArg = callExpr.getArgs().get(1);
                            compiler.getExpressionGenerator().generate(code, cp, padStringArg.getExpr(), null);

                            // Call StringApiUtils.padEnd(String, int, String)
                            int padEndMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "padEnd", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;");
                            code.invokestatic(padEndMethod);
                        } else {
                            // Default pad string " "
                            int spaceIndex = cp.addString(" ");
                            code.ldc(spaceIndex);

                            int padEndMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "padEnd", "(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;");
                            code.invokestatic(padEndMethod);
                        }
                        return;
                    }

                    default -> throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on String");
                }
            }
        }
        // For unsupported call expressions, throw an error for now
        throw new Swc4jByteCodeCompilerException("Call expression not yet supported");
    }
}
