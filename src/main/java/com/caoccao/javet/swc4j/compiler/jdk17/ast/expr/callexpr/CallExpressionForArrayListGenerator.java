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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.callexpr;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionForArrayListGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForArrayListGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Generate code for the object (ArrayList)
            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

            // Get the method name
            String methodName = null;
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                methodName = propIdent.getSym();
            }

            switch (methodName) {
                case "concat" -> generateConcat(code, cp, callExpr);
                case "copyWithin" -> generateCopyWithin(code, cp, callExpr);
                case "fill" -> generateFill(code, cp, callExpr);
                case "includes" -> generateIncludes(code, cp, callExpr);
                case "indexOf" -> generateIndexOf(code, cp, callExpr);
                case "join" -> generateJoin(code, cp, callExpr);
                case "lastIndexOf" -> generateLastIndexOf(code, cp, callExpr);
                case "pop" -> generatePop(code, cp);
                case "push" -> generatePush(code, cp, callExpr);
                case "reverse" -> generateReverse(code, cp);
                case "shift" -> generateShift(code, cp);
                case "slice" -> generateSlice(code, cp, callExpr);
                case "sort" -> generateSort(code, cp);
                case "splice" -> generateSplice(code, cp, callExpr);
                case "toLocaleString" -> generateToLocaleString(code, cp);
                case "toReversed" -> generateToReversed(code, cp);
                case "toSorted" -> generateToSorted(code, cp);
                case "toSpliced" -> generateToSpliced(code, cp, callExpr);
                case "toString" -> generateToString(code, cp);
                case "unshift" -> generateUnshift(code, cp, callExpr);
                case "with" -> generateWith(code, cp, callExpr);
                default ->
                        throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on ArrayList");
            }
        }
    }

    private void generateConcat(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.concat(arr2) -> ArrayListApiUtils.concat(arr, arr2)
        // JavaScript's concat() returns a new array
        if (!callExpr.getArgs().isEmpty()) {
            // Get the second array argument
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);

            // Call ArrayListApiUtils.concat(ArrayList, ArrayList)
            int concatMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "concat",
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
    }

    private void generateCopyWithin(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.copyWithin(target, start, end) -> ArrayListApiUtils.copyWithin(arr, target, start, [end])
        // Returns the array itself (mutates in place)
        int argCount = callExpr.getArgs().size();

        if (argCount < 2) {
            // Need at least target and start
            throw new Swc4jByteCodeCompilerException("copyWithin() requires at least two arguments (target, start)");
        }

        // Generate the target argument
        var targetArg = callExpr.getArgs().get(0);
        compiler.getExpressionGenerator().generate(code, cp, targetArg.getExpr(), null);

        // Unbox target if needed
        String targetType = compiler.getTypeResolver().inferTypeFromExpr(targetArg.getExpr());
        if ("Ljava/lang/Integer;".equals(targetType)) {
            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
            code.invokevirtual(intValueMethod);
        }

        // Generate the start argument
        var startArg = callExpr.getArgs().get(1);
        compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

        // Unbox start if needed
        String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
        if ("Ljava/lang/Integer;".equals(startType)) {
            int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
            code.invokevirtual(intValueMethod);
        }

        if (argCount == 2) {
            // copyWithin(target, start) - copy from start to end
            int copyWithinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "copyWithin",
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

            // Call ArrayListApiUtils.copyWithin(ArrayList, int, int, int)
            int copyWithinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "copyWithin",
                    "(Ljava/util/ArrayList;III)Ljava/util/ArrayList;");
            code.invokestatic(copyWithinMethod);
        }
    }

    private void generateFill(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.fill(value, start, end) -> ArrayListApiUtils.fill(arr, value, [start], [end])
        // Returns the array itself (mutates in place)
        int argCount = callExpr.getArgs().size();

        if (argCount == 0) {
            // No value provided - throw error
            throw new Swc4jByteCodeCompilerException("fill() requires at least one argument (value)");
        }

        // Generate the value argument
        var valueArg = callExpr.getArgs().get(0);
        compiler.getExpressionGenerator().generate(code, cp, valueArg.getExpr(), null);

        // Box primitive value if needed
        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
            TypeConversionUtils.boxPrimitiveType(code, cp, valueType, TypeConversionUtils.getWrapperType(valueType));
        }

        if (argCount == 1) {
            // fill(value) - fill entire array
            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "fill",
                    "(Ljava/util/ArrayList;Ljava/lang/Object;)Ljava/util/ArrayList;");
            code.invokestatic(fillMethod);

        } else if (argCount == 2) {
            // fill(value, start) - fill from start to end
            var startArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            // Unbox if needed
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call ArrayListApiUtils.fill(ArrayList, Object, int)
            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "fill",
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

            var endArg = callExpr.getArgs().get(2);
            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

            // Unbox end if needed
            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
            if ("Ljava/lang/Integer;".equals(endType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call ArrayListApiUtils.fill(ArrayList, Object, int, int)
            int fillMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "fill",
                    "(Ljava/util/ArrayList;Ljava/lang/Object;II)Ljava/util/ArrayList;");
            code.invokestatic(fillMethod);
        }
    }

    private void generateIncludes(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generateIndexOf(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generateJoin(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
        int joinMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "join",
                "(Ljava/util/List;Ljava/lang/String;)Ljava/lang/String;");
        code.invokestatic(joinMethod);
    }

    private void generateLastIndexOf(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generatePop(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.pop() -> arr.remove(arr.size() - 1)
        // Returns the removed element
        code.dup(); // Duplicate ArrayList reference for size() call

        int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
        code.invokevirtual(sizeMethod); // Get size

        code.iconst(1);
        code.isub(); // size - 1

        int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
        code.invokevirtual(removeMethod); // Returns removed element
    }

    private void generatePush(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generateReverse(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.reverse() -> Collections.reverse(arr); returns void but we keep arr on stack
        // JavaScript's reverse() returns the array itself (for chaining)
        code.dup(); // Duplicate array reference for return

        int reverseMethod = cp.addMethodRef("java/util/Collections", "reverse", "(Ljava/util/List;)V");
        code.invokestatic(reverseMethod); // Reverse in place

        // The duplicated array reference is now on top of stack, ready to return
    }

    private void generateShift(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.shift() -> arr.remove(0)
        // Returns the removed element
        code.iconst(0); // Index 0

        int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
        code.invokevirtual(removeMethod); // Returns removed element
    }

    private void generateSlice(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.slice(start, end) -> ArrayListApiUtils.slice(arr, start, end)
        // JavaScript's slice() returns a new array with extracted elements
        int argCount = callExpr.getArgs().size();

        if (argCount == 0) {
            // No arguments: arr.slice() - copy entire array
            code.iconst(0);  // start = 0

            code.dup_x1();  // Duplicate start, place below ArrayList
            code.pop();  // Remove top 0

            code.dup();  // Duplicate ArrayList for size() call

            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
            code.invokevirtual(sizeMethod);  // Get size

            // Reorder to: ArrayList, 0, size
            code.dup_x2();  // Duplicate size
            code.pop();  // Remove top size

            code.dup_x2();  // Duplicate ArrayList
            code.pop();  // Remove top ArrayList

            code.swap();  // Swap to get: ArrayList, 0, size

        } else if (argCount == 1) {
            // One argument: arr.slice(start) - from start to end
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            // Need to unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            code.dup_x1();  // Duplicate start
            code.pop();  // Remove top start

            code.dup();  // Duplicate ArrayList for size() call

            int sizeMethod = cp.addMethodRef("java/util/ArrayList", "size", "()I");
            code.invokevirtual(sizeMethod);  // Get size

            // Reorder to: ArrayList, start, size
            code.dup_x2();  // Duplicate size
            code.pop();  // Remove top size

            code.dup_x2();  // Duplicate ArrayList
            code.pop();  // Remove top ArrayList

            code.swap();  // Swap to get: ArrayList, start, size

        } else {
            // Two arguments: arr.slice(start, end)
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            var endArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, endArg.getExpr(), null);

            // Unbox if Integer
            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
            if ("Ljava/lang/Integer;".equals(endType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }
        }

        // Call ArrayListApiUtils.slice(ArrayList, int, int)
        int sliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "slice",
                "(Ljava/util/ArrayList;II)Ljava/util/ArrayList;");
        code.invokestatic(sliceMethod);
    }

    private void generateSort(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.sort() -> Collections.sort(arr); returns void but we keep arr on stack
        // JavaScript's sort() returns the array itself (for chaining)
        code.dup(); // Duplicate array reference for return

        int sortMethod = cp.addMethodRef("java/util/Collections", "sort", "(Ljava/util/List;)V");
        code.invokestatic(sortMethod); // Sort in place

        // The duplicated array reference is now on top of stack, ready to return
    }

    private void generateSplice(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.splice(start, deleteCount, ...items) -> ArrayListApiUtils.splice(arr, start, deleteCount, items)
        // JavaScript's splice() mutates the array and returns removed elements
        int argCount = callExpr.getArgs().size();

        // Keep ArrayList reference for later (splice mutates it)
        code.dup();

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

            // deleteCount = array.length - start (remove all after start)
            code.ldc(cp.addInteger(Integer.MAX_VALUE));
            code.aconst_null();  // items = null
        } else {
            // Two or more arguments: splice(start, deleteCount, ...items)
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Generate deleteCount parameter
            var deleteCountArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, deleteCountArg.getExpr(), null);

            // Unbox if Integer
            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
            if ("Ljava/lang/Integer;".equals(deleteCountType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Create ArrayList for items to insert (if any)
            if (argCount > 2) {
                // Create new ArrayList for items
                int arrayListClass = cp.addClass("java/util/ArrayList");
                int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInit);

                // Add each item to the items ArrayList
                for (int i = 2; i < argCount; i++) {
                    code.dup();  // Duplicate itemsList for add() call

                    var itemArg = callExpr.getArgs().get(i);
                    compiler.getExpressionGenerator().generate(code, cp, itemArg.getExpr(), null);

                    // Box if primitive
                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                        TypeConversionUtils.boxPrimitiveType(code, cp, itemType, TypeConversionUtils.getWrapperType(itemType));
                    }

                    code.invokevirtual(addMethod);
                    code.pop();  // Pop the boolean return value
                }
            } else {
                // No items to insert
                code.aconst_null();
            }
        }

        // Call ArrayListApiUtils.splice(ArrayList, int, int, ArrayList)
        int spliceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "splice",
                "(Ljava/util/ArrayList;IILjava/util/ArrayList;)Ljava/util/ArrayList;");
        code.invokestatic(spliceMethod);

        // Splice returns the removed elements, but we also kept the original array on stack
        // JavaScript splice returns removed elements, so we need to pop the original array and keep removed
        code.swap();  // Swap to get: removedElements, ArrayList
        code.pop();   // Pop the original ArrayList
    }

    private void generateToLocaleString(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.toLocaleString() -> ArrayListApiUtils.arrayToLocaleString(arr)
        // Returns locale-specific string representation (comma-separated values)
        int toLocaleStringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "arrayToLocaleString",
                "(Ljava/util/List;)Ljava/lang/String;");
        code.invokestatic(toLocaleStringMethod);
    }

    private void generateToReversed(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.toReversed() -> ArrayListApiUtils.toReversed(arr)
        // Returns new reversed array without modifying original (ES2023)
        int toReversedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "toReversed",
                "(Ljava/util/ArrayList;)Ljava/util/ArrayList;");
        code.invokestatic(toReversedMethod);
    }

    private void generateToSorted(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.toSorted() -> ArrayListApiUtils.toSorted(arr)
        // Returns new sorted array without modifying original (ES2023)
        int toSortedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "toSorted",
                "(Ljava/util/ArrayList;)Ljava/util/ArrayList;");
        code.invokestatic(toSortedMethod);
    }

    private void generateToSpliced(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.toSpliced(start, deleteCount, ...items) -> ArrayListApiUtils.toSpliced(arr, start, deleteCount, items)
        // Returns new array with elements removed/inserted (ES2023 non-mutating)
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

            // deleteCount = Integer.MAX_VALUE (remove all after start)
            code.ldc(cp.addInteger(Integer.MAX_VALUE));
            code.aconst_null();  // items = null
        } else {
            // Two or more arguments: toSpliced(start, deleteCount, ...items)
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Generate deleteCount parameter
            var deleteCountArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, deleteCountArg.getExpr(), null);

            // Unbox if Integer
            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
            if ("Ljava/lang/Integer;".equals(deleteCountType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Create ArrayList for items to insert (if any)
            if (argCount > 2) {
                // Create new ArrayList for items
                int arrayListClass = cp.addClass("java/util/ArrayList");
                int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
                int addMethod = cp.addMethodRef("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");

                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInit);

                // Add each item to the items ArrayList
                for (int i = 2; i < argCount; i++) {
                    code.dup();  // Duplicate itemsList for add() call

                    var itemArg = callExpr.getArgs().get(i);
                    compiler.getExpressionGenerator().generate(code, cp, itemArg.getExpr(), null);

                    // Box if primitive
                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                        TypeConversionUtils.boxPrimitiveType(code, cp, itemType, TypeConversionUtils.getWrapperType(itemType));
                    }

                    code.invokevirtual(addMethod);
                    code.pop();  // Pop the boolean return value
                }
            } else {
                // No items to insert
                code.aconst_null();
            }
        }

        // Call ArrayListApiUtils.toSpliced(ArrayList, int, int, ArrayList)
        int toSplicedMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "toSpliced",
                "(Ljava/util/ArrayList;IILjava/util/ArrayList;)Ljava/util/ArrayList;");
        code.invokestatic(toSplicedMethod);
    }

    private void generateToString(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // arr.toString() -> ArrayListApiUtils.arrayToString(arr)
        // Returns string representation (comma-separated values)
        int toStringMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "arrayToString",
                "(Ljava/util/List;)Ljava/lang/String;");
        code.invokestatic(toStringMethod);
    }

    private void generateUnshift(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generateWith(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.with(index, value) -> ArrayListApiUtils.with(arr, index, value)
        // Returns new array with one element changed (ES2023)
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

        // Call ArrayListApiUtils.with(ArrayList, int, Object)
        int withMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/ArrayListApiUtils", "with",
                "(Ljava/util/ArrayList;ILjava/lang/Object;)Ljava/util/ArrayList;");
        code.invokestatic(withMethod);
    }

    public boolean isTypeSupported(String type) {
        return "Ljava/util/ArrayList;".equals(type);
    }
}
