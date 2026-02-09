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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Processes call expressions for ArrayList instance methods.
 */
public final class CallExpressionForArrayListProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForArrayListProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Generate code for the object (ArrayList)
            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);

            // Get the method name
            String methodName = null;
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                methodName = propIdent.getSym();
            }

            switch (methodName) {
                case ConstantJavaMethod.METHOD_CONCAT -> generateConcat(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_COPY_WITHIN -> generateCopyWithin(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FILL -> generateFill(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FILTER -> generateFilter(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FIND -> generateFind(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FIND_INDEX -> generateFindIndex(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FLAT -> generateFlat(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FLAT_MAP -> generateFlatMap(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_FOR_EACH -> generateForEach(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_INCLUDES -> generateIncludes(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_INDEX_OF -> generateIndexOf(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_JOIN -> generateJoin(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_LAST_INDEX_OF -> generateLastIndexOf(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_KEYS -> generateKeys(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_MAP -> generateMap(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_POP -> generatePop(code, classWriter);
                case ConstantJavaMethod.METHOD_PUSH -> generatePush(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REDUCE -> generateReduce(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REDUCE_RIGHT -> generateReduceRight(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REVERSE -> generateReverse(code, classWriter);
                case ConstantJavaMethod.METHOD_SOME -> generateSome(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_VALUES -> generateValues(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_ENTRIES -> generateEntries(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_EVERY -> generateEvery(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SHIFT -> generateShift(code, classWriter);
                case ConstantJavaMethod.METHOD_SLICE -> generateSlice(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SORT -> generateSort(code, classWriter);
                case ConstantJavaMethod.METHOD_SPLICE -> generateSplice(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_TO_LOCALE_STRING -> generateToLocaleString(code, classWriter);
                case ConstantJavaMethod.METHOD_TO_REVERSED -> generateToReversed(code, classWriter);
                case ConstantJavaMethod.METHOD_TO_SORTED -> generateToSorted(code, classWriter);
                case ConstantJavaMethod.METHOD_TO_SPLICED -> generateToSpliced(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_TO_STRING -> generateToString(code, classWriter);
                case ConstantJavaMethod.METHOD_UNSHIFT -> generateUnshift(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_WITH -> generateWith(code, classWriter, callExpr);
                default ->
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "Method '" + methodName + "()' not supported on ArrayList");
            }
        }
    }

    private void generateConcat(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.concat(arr2) -> ArrayListApiUtils.concat(arr, arr2)
        // JavaScript's concat() returns a new array
        var cp = classWriter.getConstantPool();
        if (!callExpr.getArgs().isEmpty()) {
            // Get the second array argument
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

            // Call ArrayListApiUtils.concat(ArrayList, ArrayList)
            int concatMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_CONCAT,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(concatMethod);
        } else {
            // No argument - just return a copy of the array
            int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
            int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.LJAVA_UTIL_COLLECTION__V);

            code.newInstance(arrayListClass)
                    .dup_x1()  // Duplicate new ArrayList ref, place it below the original array
                    .swap()    // Swap to get: new ArrayList, original array, new ArrayList
                    .invokespecial(arrayListInit);  // Call ArrayList(Collection)
        }
    }

    private void generateCopyWithin(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.copyWithin(target, start, end) -> ArrayListApiUtils.copyWithin(arr, target, start, [end])
        // Returns the array itself (mutates in place)
        int argCount = callExpr.getArgs().size();

        if (argCount < 2) {
            // Need at least target and start
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "copyWithin() requires at least two arguments (target, start)");
        }

        // Generate the target argument
        var targetArg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, targetArg.getExpr(), null);

        // Unbox target if needed
        String targetType = compiler.getTypeResolver().inferTypeFromExpr(targetArg.getExpr());
        var cp = classWriter.getConstantPool();
        CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, targetType);

        // Generate the start argument
        var startArg = callExpr.getArgs().get(1);
        compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

        // Unbox start if needed
        String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
        CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

        if (argCount == 2) {
            // copyWithin(target, start) - copy from start to end
            int copyWithinMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_COPY_WITHIN,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_I__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(copyWithinMethod);

        } else {
            // copyWithin(target, start, end) - three arguments
            var endArg = callExpr.getArgs().get(2);
            compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

            // Unbox end if needed
            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, endType);

            // Call ArrayListApiUtils.copyWithin(ArrayList, int, int, int)
            int copyWithinMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_COPY_WITHIN,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_I_I__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(copyWithinMethod);
        }
    }

    private void generateEntries(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (!callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "entries() does not accept arguments");
        }
        var cp = classWriter.getConstantPool();
        int entriesMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_ENTRIES,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(entriesMethod);
    }

    private void generateEvery(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "every() requires a callback");
        }
        String interfaceDescriptor = selectPredicateInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int everyMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_EVERY,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Z");
        code.invokestatic(everyMethod);
    }

    private void generateFill(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.fill(value, start, end) -> ArrayListApiUtils.fill(arr, value, [start], [end])
        // Returns the array itself (mutates in place)
        int argCount = callExpr.getArgs().size();

        if (argCount == 0) {
            // No value provided - throw error
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "fill() requires at least one argument (value)");
        }

        // Generate the value argument
        var valueArg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, valueArg.getExpr(), null);

        // Box primitive value if needed
        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, TypeConversionUtils.getWrapperType(valueType));
        }

        var cp = classWriter.getConstantPool();
        if (argCount == 1) {
            // fill(value) - fill entire array
            int fillMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FILL,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_LJAVA_LANG_OBJECT__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(fillMethod);

        } else if (argCount == 2) {
            // fill(value, start) - fill from start to end
            var startArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if needed
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            // Call ArrayListApiUtils.fill(ArrayList, Object, int)
            int fillMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FILL,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_LJAVA_LANG_OBJECT_I__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(fillMethod);

        } else {
            // fill(value, start, end) - three arguments
            var startArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox start if needed
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            var endArg = callExpr.getArgs().get(2);
            compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

            // Unbox end if needed
            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, endType);

            // Call ArrayListApiUtils.fill(ArrayList, Object, int, int)
            int fillMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FILL,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_LJAVA_LANG_OBJECT_I_I__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(fillMethod);
        }
    }

    private void generateFilter(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "filter() requires a callback");
        }
        String interfaceDescriptor = selectPredicateInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int filterMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FILTER,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/util/ArrayList;");
        code.invokestatic(filterMethod);
    }

    private void generateFind(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "find() requires a callback");
        }
        String interfaceDescriptor = selectPredicateInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int findMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FIND,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/lang/Object;");
        code.invokestatic(findMethod);
    }

    private void generateFindIndex(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "findIndex() requires a callback");
        }
        String interfaceDescriptor = selectPredicateInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int findIndexMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FIND_INDEX,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")I");
        code.invokestatic(findIndexMethod);
    }

    private void generateFlat(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        int argCount = callExpr.getArgs().size();
        var cp = classWriter.getConstantPool();
        if (argCount == 0) {
            int flatMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FLAT,
                    ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(flatMethod);
            return;
        }
        var depthArg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, depthArg.getExpr(), null);
        String depthType = compiler.getTypeResolver().inferTypeFromExpr(depthArg.getExpr());
        CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, depthType);
        if (!ConstantJavaType.LJAVA_LANG_INTEGER.equals(depthType) && depthType != null && TypeConversionUtils.isPrimitiveType(depthType) && !ConstantJavaType.ABBR_INTEGER.equals(depthType)) {
            TypeConversionUtils.convertPrimitiveType(code, TypeConversionUtils.getPrimitiveType(depthType), ConstantJavaType.ABBR_INTEGER);
        }
        int flatMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FLAT,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(flatMethod);
    }

    private void generateFlatMap(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "flatMap() requires a callback");
        }
        String interfaceDescriptor = resolveMapInterfaceDescriptor(callExpr, 0);
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int flatMapMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FLAT_MAP,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/util/ArrayList;");
        code.invokestatic(flatMapMethod);
    }

    private void generateForEach(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "forEach() requires a callback");
        }
        String interfaceDescriptor = selectConsumerInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int forEachMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_FOR_EACH,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")V");
        code.invokestatic(forEachMethod);
    }

    private void generateFunctionalArg(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstCallExpr callExpr,
            int index,
            String interfaceDescriptor) throws Swc4jByteCodeCompilerException {
        var arg = callExpr.getArgs().get(index);
        ReturnTypeInfo targetTypeInfo = ReturnTypeInfo.of(getSourceCode(), callExpr, interfaceDescriptor);
        compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), targetTypeInfo);
    }

    private void generateIncludes(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.includes(elem) -> arr.contains(elem)
        // Returns boolean: true if element exists, false otherwise
        var cp = classWriter.getConstantPool();
        if (!callExpr.getArgs().isEmpty()) {
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            // Box argument if primitive
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }

            int containsMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_CONTAINS, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
            code.invokevirtual(containsMethod); // Returns boolean
        } else {
            // No argument - pop ArrayList ref and return false
            code.pop();
            code.iconst(0); // false
        }
    }

    private void generateIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.indexOf(elem) -> arr.indexOf(elem)
        // Returns int: index or -1 if not found
        var cp = classWriter.getConstantPool();
        if (!callExpr.getArgs().isEmpty()) {
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            // Box argument if primitive
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }

            int indexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__I);
            code.invokevirtual(indexOfMethod); // Returns int index
        } else {
            // No argument - pop ArrayList ref and return -1
            code.pop();
            code.iconst(-1);
        }
    }

    private void generateJoin(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.join(sep) -> ArrayHelper.join(arr, sep)
        // JavaScript's join() returns a string
        // Default separator is "," if not provided
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No separator provided - use default ","
            code.ldc(cp.addString(","));
        } else {
            // Get separator argument
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);

            // If the separator is not a String, convert it
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && !ConstantJavaType.LJAVA_LANG_STRING.equals(argType)) {
                // Convert to string using String.valueOf()
                int valueOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_VALUE_OF, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__LJAVA_LANG_STRING);
                code.invokestatic(valueOfMethod);
            }
        }

        // Call ArrayHelper.join(ArrayList, String)
        int joinMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_JOIN,
                ConstantJavaDescriptor.LJAVA_UTIL_LIST_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
        code.invokestatic(joinMethod);
    }

    private void generateKeys(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (!callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "keys() does not accept arguments");
        }
        var cp = classWriter.getConstantPool();
        int keysMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_KEYS,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(keysMethod);
    }

    private void generateLastIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.lastIndexOf(elem) -> arr.lastIndexOf(elem)
        // Returns int: last index or -1 if not found
        var cp = classWriter.getConstantPool();
        if (!callExpr.getArgs().isEmpty()) {
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            // Box argument if primitive
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }

            int lastIndexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_LAST_INDEX_OF, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__I);
            code.invokevirtual(lastIndexOfMethod); // Returns int index
        } else {
            // No argument - pop ArrayList ref and return -1
            code.pop();
            code.iconst(-1);
        }
    }

    private void generateMap(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "map() requires a callback");
        }
        String interfaceDescriptor = resolveMapInterfaceDescriptor(callExpr, 0);
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int mapMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_MAP,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/util/ArrayList;");
        code.invokestatic(mapMethod);
    }

    private void generatePop(CodeBuilder code, ClassWriter classWriter) {
        // arr.pop() -> arr.remove(arr.size() - 1)
        // Returns the removed element
        var cp = classWriter.getConstantPool();
        code.dup(); // Duplicate ArrayList reference for size() call

        int sizeMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
        code.invokevirtual(sizeMethod); // Get size

        code.iconst(1);
        code.isub(); // size - 1

        int removeMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT);
        code.invokevirtual(removeMethod); // Returns removed element
    }

    private void generatePush(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.push(value) -> arr.add(value)
        if (!callExpr.getArgs().isEmpty()) {
            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            // Box if primitive
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }
            var cp = classWriter.getConstantPool();
            int addMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);
            code.invokevirtual(addMethod);
            code.pop(); // Pop the boolean return value
        }
    }

    private void generateReduce(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        int argCount = callExpr.getArgs().size();
        if (argCount == 0) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "reduce() requires a callback");
        }
        String initType = null;
        if (argCount > 1) {
            initType = compiler.getTypeResolver().inferTypeFromExpr(callExpr.getArgs().get(1).getExpr());
        }
        String primitiveType = resolveReductionPrimitiveType(callExpr, initType);
        String interfaceDescriptor = selectBinaryOperatorInterfaceDescriptor(primitiveType);
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        if (argCount == 1) {
            int reduceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_REDUCE,
                    "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/lang/Object;");
            code.invokestatic(reduceMethod);
            return;
        }
        var initArg = callExpr.getArgs().get(1);
        if (primitiveType != null) {
            ReturnTypeInfo initTypeInfo = ReturnTypeInfo.of(getSourceCode(), initArg.getExpr(), primitiveType);
            compiler.getExpressionProcessor().generate(code, classWriter, initArg.getExpr(), initTypeInfo);
        } else {
            compiler.getExpressionProcessor().generate(code, classWriter, initArg.getExpr(), null);
            if (initType != null && TypeConversionUtils.isPrimitiveType(initType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, initType, TypeConversionUtils.getWrapperType(initType));
            }
        }
        String initDescriptor = primitiveType != null ? primitiveType : ConstantJavaType.LJAVA_LANG_OBJECT;
        int reduceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_REDUCE,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + initDescriptor + ")Ljava/lang/Object;");
        code.invokestatic(reduceMethod);
    }

    private void generateReduceRight(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        int argCount = callExpr.getArgs().size();
        if (argCount == 0) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "reduceRight() requires a callback");
        }
        String initType = null;
        if (argCount > 1) {
            initType = compiler.getTypeResolver().inferTypeFromExpr(callExpr.getArgs().get(1).getExpr());
        }
        String primitiveType = resolveReductionPrimitiveType(callExpr, initType);
        String interfaceDescriptor = selectBinaryOperatorInterfaceDescriptor(primitiveType);
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        if (argCount == 1) {
            int reduceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_REDUCE_RIGHT,
                    "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Ljava/lang/Object;");
            code.invokestatic(reduceMethod);
            return;
        }
        var initArg = callExpr.getArgs().get(1);
        if (primitiveType != null) {
            ReturnTypeInfo initTypeInfo = ReturnTypeInfo.of(getSourceCode(), initArg.getExpr(), primitiveType);
            compiler.getExpressionProcessor().generate(code, classWriter, initArg.getExpr(), initTypeInfo);
        } else {
            compiler.getExpressionProcessor().generate(code, classWriter, initArg.getExpr(), null);
            if (initType != null && TypeConversionUtils.isPrimitiveType(initType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, initType, TypeConversionUtils.getWrapperType(initType));
            }
        }
        String initDescriptor = primitiveType != null ? primitiveType : ConstantJavaType.LJAVA_LANG_OBJECT;
        int reduceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_REDUCE_RIGHT,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + initDescriptor + ")Ljava/lang/Object;");
        code.invokestatic(reduceMethod);
    }

    private void generateReverse(CodeBuilder code, ClassWriter classWriter) {
        // arr.reverse() -> Collections.reverse(arr); returns void but we keep arr on stack
        // JavaScript's reverse() returns the array itself (for chaining)
        code.dup(); // Duplicate array reference for return

        var cp = classWriter.getConstantPool();
        int reverseMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_COLLECTIONS, ConstantJavaMethod.METHOD_REVERSE, ConstantJavaDescriptor.LJAVA_UTIL_LIST__V);
        code.invokestatic(reverseMethod); // Reverse in place

        // The duplicated array reference is now on top of stack, ready to return
    }

    private void generateShift(CodeBuilder code, ClassWriter classWriter) {
        // arr.shift() -> arr.remove(0)
        // Returns the removed element
        code.iconst(0); // Index 0

        var cp = classWriter.getConstantPool();
        int removeMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_REMOVE, ConstantJavaDescriptor.I__LJAVA_LANG_OBJECT);
        code.invokevirtual(removeMethod); // Returns removed element
    }

    private void generateSlice(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.slice(start, end) -> ArrayListApiUtils.slice(arr, start, end)
        // JavaScript's slice() returns a new array with extracted elements
        int argCount = callExpr.getArgs().size();

        var cp = classWriter.getConstantPool();
        if (argCount == 0) {
            // No arguments: arr.slice() - copy entire array
            code.iconst(0);  // start = 0

            code.dup_x1();  // Duplicate start, place below ArrayList
            code.pop();  // Remove top 0

            code.dup();  // Duplicate ArrayList for size() call

            int sizeMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
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
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Need to unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            code.dup_x1();  // Duplicate start
            code.pop();  // Remove top start

            code.dup();  // Duplicate ArrayList for size() call

            int sizeMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_SIZE, ConstantJavaDescriptor.__I);
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
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            var endArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

            // Unbox if Integer
            String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, endType);
        }

        // Call ArrayListApiUtils.slice(ArrayList, int, int)
        int sliceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_SLICE,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_I__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(sliceMethod);
    }

    private void generateSome(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "some() requires a callback");
        }
        String interfaceDescriptor = selectPredicateInterfaceDescriptor(resolvePrimitiveElementType(callExpr));
        generateFunctionalArg(code, classWriter, callExpr, 0, interfaceDescriptor);
        var cp = classWriter.getConstantPool();
        int someMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_SOME,
                "(Ljava/util/ArrayList;" + interfaceDescriptor + ")Z");
        code.invokestatic(someMethod);
    }

    private void generateSort(CodeBuilder code, ClassWriter classWriter) {
        // arr.sort() -> Collections.sort(arr); returns void but we keep arr on stack
        // JavaScript's sort() returns the array itself (for chaining)
        code.dup(); // Duplicate array reference for return

        var cp = classWriter.getConstantPool();
        int sortMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_COLLECTIONS, ConstantJavaMethod.METHOD_SORT, ConstantJavaDescriptor.LJAVA_UTIL_LIST__V);
        code.invokestatic(sortMethod); // Sort in place

        // The duplicated array reference is now on top of stack, ready to return
    }

    private void generateSplice(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.splice(start, deleteCount, ...items) -> ArrayListApiUtils.splice(arr, start, deleteCount, items)
        // JavaScript's splice() mutates the array and returns removed elements
        int argCount = callExpr.getArgs().size();

        // Keep ArrayList reference for later (splice mutates it)
        code.dup();

        var cp = classWriter.getConstantPool();
        if (argCount == 0) {
            // No arguments: splice() - remove nothing, return empty array
            code.iconst(0);  // start = 0
            code.iconst(0);  // deleteCount = 0
            code.aconst_null();  // items = null
        } else if (argCount == 1) {
            // One argument: splice(start) - remove from start to end
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            // deleteCount = array.length - start (remove all after start)
            code.ldc(cp.addInteger(Integer.MAX_VALUE));
            code.aconst_null();  // items = null
        } else {
            // Two or more arguments: splice(start, deleteCount, ...items)
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            // Generate deleteCount parameter
            var deleteCountArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, deleteCountArg.getExpr(), null);

            // Unbox if Integer
            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, deleteCountType);

            // Create ArrayList for items to insert (if any)
            if (argCount > 2) {
                // Create new ArrayList for items
                int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
                int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
                int addMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);

                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInit);

                // Add each item to the items ArrayList
                for (int i = 2; i < argCount; i++) {
                    code.dup();  // Duplicate itemsList for add() call

                    var itemArg = callExpr.getArgs().get(i);
                    compiler.getExpressionProcessor().generate(code, classWriter, itemArg.getExpr(), null);

                    // Box if primitive
                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, itemType, TypeConversionUtils.getWrapperType(itemType));
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
        int spliceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_SPLICE,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_I_LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(spliceMethod);

        // Splice returns the removed elements, but we also kept the original array on stack
        // JavaScript splice returns removed elements, so we need to pop the original array and keep removed
        code.swap();  // Swap to get: removedElements, ArrayList
        code.pop();   // Pop the original ArrayList
    }

    private void generateToLocaleString(CodeBuilder code, ClassWriter classWriter) {
        // arr.toLocaleString() -> ArrayListApiUtils.arrayToLocaleString(arr)
        // Returns locale-specific string representation (comma-separated values)
        var cp = classWriter.getConstantPool();
        int toLocaleStringMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_ARRAY_TO_LOCALE_STRING,
                ConstantJavaDescriptor.LJAVA_UTIL_LIST__LJAVA_LANG_STRING);
        code.invokestatic(toLocaleStringMethod);
    }

    private void generateToReversed(CodeBuilder code, ClassWriter classWriter) {
        // arr.toReversed() -> ArrayListApiUtils.toReversed(arr)
        // Returns new reversed array without modifying original (ES2023)
        var cp = classWriter.getConstantPool();
        int toReversedMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_TO_REVERSED,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(toReversedMethod);
    }

    private void generateToSorted(CodeBuilder code, ClassWriter classWriter) {
        // arr.toSorted() -> ArrayListApiUtils.toSorted(arr)
        // Returns new sorted array without modifying original (ES2023)
        var cp = classWriter.getConstantPool();
        int toSortedMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_TO_SORTED,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(toSortedMethod);
    }

    private void generateToSpliced(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.toSpliced(start, deleteCount, ...items) -> ArrayListApiUtils.toSpliced(arr, start, deleteCount, items)
        // Returns new array with elements removed/inserted (ES2023 non-mutating)
        int argCount = callExpr.getArgs().size();

        var cp = classWriter.getConstantPool();
        if (argCount == 0) {
            // No arguments: toSpliced() - returns copy with no changes
            code.iconst(0);  // start = 0
            code.iconst(0);  // deleteCount = 0
            code.aconst_null();  // items = null
        } else if (argCount == 1) {
            // One argument: toSpliced(start) - remove from start to end
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            // deleteCount = Integer.MAX_VALUE (remove all after start)
            code.ldc(cp.addInteger(Integer.MAX_VALUE));
            code.aconst_null();  // items = null
        } else {
            // Two or more arguments: toSpliced(start, deleteCount, ...items)
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            // Unbox if Integer
            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            // Generate deleteCount parameter
            var deleteCountArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, deleteCountArg.getExpr(), null);

            // Unbox if Integer
            String deleteCountType = compiler.getTypeResolver().inferTypeFromExpr(deleteCountArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, deleteCountType);

            // Create ArrayList for items to insert (if any)
            if (argCount > 2) {
                // Create new ArrayList for items
                int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
                int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
                int addMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.LJAVA_LANG_OBJECT__Z);

                code.newInstance(arrayListClass);
                code.dup();
                code.invokespecial(arrayListInit);

                // Add each item to the items ArrayList
                for (int i = 2; i < argCount; i++) {
                    code.dup();  // Duplicate itemsList for add() call

                    var itemArg = callExpr.getArgs().get(i);
                    compiler.getExpressionProcessor().generate(code, classWriter, itemArg.getExpr(), null);

                    // Box if primitive
                    String itemType = compiler.getTypeResolver().inferTypeFromExpr(itemArg.getExpr());
                    if (itemType != null && TypeConversionUtils.isPrimitiveType(itemType)) {
                        TypeConversionUtils.boxPrimitiveType(code, classWriter, itemType, TypeConversionUtils.getWrapperType(itemType));
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
        int toSplicedMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_TO_SPLICED,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_I_LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(toSplicedMethod);
    }

    private void generateToString(CodeBuilder code, ClassWriter classWriter) {
        // arr.toString() -> ArrayListApiUtils.arrayToString(arr)
        // Returns string representation (comma-separated values)
        var cp = classWriter.getConstantPool();
        int toStringMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_ARRAY_TO_STRING,
                ConstantJavaDescriptor.LJAVA_UTIL_LIST__LJAVA_LANG_STRING);
        code.invokestatic(toStringMethod);
    }

    private void generateUnshift(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.unshift(value) -> arr.add(0, value)
        if (!callExpr.getArgs().isEmpty()) {
            code.iconst(0); // Index 0

            var arg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
            // Box if primitive
            String argType = compiler.getTypeResolver().inferTypeFromExpr(arg.getExpr());
            if (argType != null && TypeConversionUtils.isPrimitiveType(argType)) {
                TypeConversionUtils.boxPrimitiveType(code, classWriter, argType, TypeConversionUtils.getWrapperType(argType));
            }

            var cp = classWriter.getConstantPool();
            int addMethod = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_ADD, ConstantJavaDescriptor.I_LJAVA_LANG_OBJECT__V);
            code.invokevirtual(addMethod);
        }
    }

    private void generateValues(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (!callExpr.getArgs().isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "values() does not accept arguments");
        }
        var cp = classWriter.getConstantPool();
        int valuesMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_VALUES,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(valuesMethod);
    }

    private void generateWith(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // arr.with(index, value) -> ArrayListApiUtils.with(arr, index, value)
        // Returns new array with one element changed (ES2023)
        if (callExpr.getArgs().size() < 2) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), callExpr, "with() requires two arguments (index, value)");
        }

        // Generate index argument
        var indexArg = callExpr.getArgs().get(0);
        compiler.getExpressionProcessor().generate(code, classWriter, indexArg.getExpr(), null);

        var cp = classWriter.getConstantPool();
        // Unbox index if needed
        String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
        CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, indexType);

        // Generate value argument
        var valueArg = callExpr.getArgs().get(1);
        compiler.getExpressionProcessor().generate(code, classWriter, valueArg.getExpr(), null);

        // Box value if primitive
        String valueType = compiler.getTypeResolver().inferTypeFromExpr(valueArg.getExpr());
        if (valueType != null && TypeConversionUtils.isPrimitiveType(valueType)) {
            TypeConversionUtils.boxPrimitiveType(code, classWriter, valueType, TypeConversionUtils.getWrapperType(valueType));
        }

        // Call ArrayListApiUtils.with(ArrayList, int, Object)
        int withMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_ARRAYLIST_API_UTILS, ConstantJavaMethod.METHOD_WITH,
                ConstantJavaDescriptor.LJAVA_UTIL_ARRAYLIST_I_LJAVA_LANG_OBJECT__LJAVA_UTIL_ARRAYLIST);
        code.invokestatic(withMethod);
    }

    private String inferArrowReturnType(Swc4jAstCallExpr callExpr, int index, String paramType) throws Swc4jByteCodeCompilerException {
        var arg = callExpr.getArgs().get(index);
        ISwc4jAstExpr expr = arg.getExpr().unParenExpr();
        if (!(expr instanceof Swc4jAstArrowExpr arrowExpr)) {
            return null;
        }
        if (!(arrowExpr.getBody() instanceof ISwc4jAstExpr bodyExpr)) {
            return null;
        }
        var context = compiler.getMemory().getCompilationContext();
        var scope = context.pushInferredTypesScope();
        try {
            if (paramType != null) {
                for (ISwc4jAstPat param : arrowExpr.getParams()) {
                    String paramName = compiler.getTypeResolver().extractParameterName(param);
                    if (paramName != null) {
                        scope.put(paramName, paramType);
                    }
                }
            }
            return compiler.getTypeResolver().inferTypeFromExpr(bodyExpr);
        } finally {
            context.popInferredTypesScope();
        }
    }

    /**
     * Checks if the type is a wide numeric primitive (int, long, or double).
     * This is intentionally narrower than {@code TypeConversionUtils.isWideNumericPrimitive()},
     * which also includes byte, short, char, and float.
     *
     * @param type the primitive type abbreviation
     * @return true if the type is I, J, or D
     */
    private boolean isWideNumericPrimitive(String type) {
        return ConstantJavaType.ABBR_INTEGER.equals(type) || ConstantJavaType.ABBR_LONG.equals(type) || ConstantJavaType.ABBR_DOUBLE.equals(type);
    }

    private String resolveArrayElementType(Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        if (!(callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr)) {
            return null;
        }
        ISwc4jAstExpr objExpr = memberExpr.getObj().unParenExpr();
        if (objExpr instanceof Swc4jAstArrayLit arrayLit) {
            return compiler.getTypeResolver().inferArrayElementType(arrayLit);
        }
        if (objExpr instanceof Swc4jAstIdent ident) {
            return compiler.getMemory().getCompilationContext().getArrayElementTypes().get(ident.getSym());
        }
        return null;
    }

    private String resolveMapInterfaceDescriptor(Swc4jAstCallExpr callExpr, int index) throws Swc4jByteCodeCompilerException {
        String primitiveType = resolvePrimitiveElementType(callExpr);
        if (primitiveType == null) {
            return ConstantJavaType.LJAVA_UTIL_FUNCTION_FUNCTION;
        }
        String returnType = inferArrowReturnType(callExpr, index, primitiveType);
        String returnPrimitive = returnType == null ? null : TypeConversionUtils.getPrimitiveType(returnType);
        if (primitiveType.equals(returnPrimitive)) {
            return selectUnaryOperatorInterfaceDescriptor(primitiveType);
        }
        return selectFunctionInterfaceDescriptor(primitiveType);
    }

    private String resolvePrimitiveElementType(Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        String elementType = resolveArrayElementType(callExpr);
        if (elementType == null) {
            return null;
        }
        String primitiveType = TypeConversionUtils.getPrimitiveType(elementType);
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_DOUBLE ->
                    primitiveType;
            default -> null;
        };
    }

    private String resolveReductionPrimitiveType(Swc4jAstCallExpr callExpr, String initType) throws Swc4jByteCodeCompilerException {
        String elementType = resolvePrimitiveElementType(callExpr);
        String initPrimitive = initType == null ? null : TypeConversionUtils.getPrimitiveType(initType);
        if (!isWideNumericPrimitive(initPrimitive)) {
            initPrimitive = null;
        }
        if (elementType != null && initPrimitive != null) {
            return TypeResolver.getWidenedType(elementType, initPrimitive);
        }
        if (initPrimitive != null) {
            return initPrimitive;
        }
        return elementType;
    }

    private String selectBinaryOperatorInterfaceDescriptor(String primitiveType) {
        if (primitiveType == null) {
            return ConstantJavaType.LJAVA_UTIL_FUNCTION_BI_FUNCTION;
        }
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_UTIL_FUNCTION_INT_BINARY_OPERATOR;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_UTIL_FUNCTION_LONG_BINARY_OPERATOR;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_UTIL_FUNCTION_DOUBLE_BINARY_OPERATOR;
            default -> ConstantJavaType.LJAVA_UTIL_FUNCTION_BI_FUNCTION;
        };
    }

    private String selectConsumerInterfaceDescriptor(String primitiveType) {
        if (primitiveType == null) {
            return ConstantJavaType.LJAVA_UTIL_FUNCTION_CONSUMER;
        }
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_UTIL_FUNCTION_INT_CONSUMER;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_UTIL_FUNCTION_LONG_CONSUMER;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_UTIL_FUNCTION_DOUBLE_CONSUMER;
            default -> ConstantJavaType.LJAVA_UTIL_FUNCTION_CONSUMER;
        };
    }

    private String selectFunctionInterfaceDescriptor(String primitiveType) {
        if (primitiveType == null) {
            return ConstantJavaType.LJAVA_UTIL_FUNCTION_FUNCTION;
        }
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_UTIL_FUNCTION_INT_FUNCTION;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_UTIL_FUNCTION_LONG_FUNCTION;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_UTIL_FUNCTION_DOUBLE_FUNCTION;
            default -> ConstantJavaType.LJAVA_UTIL_FUNCTION_FUNCTION;
        };
    }

    private String selectPredicateInterfaceDescriptor(String primitiveType) {
        if (primitiveType == null) {
            return ConstantJavaType.LJAVA_UTIL_FUNCTION_PREDICATE;
        }
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_UTIL_FUNCTION_INT_PREDICATE;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_UTIL_FUNCTION_LONG_PREDICATE;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_UTIL_FUNCTION_DOUBLE_PREDICATE;
            default -> ConstantJavaType.LJAVA_UTIL_FUNCTION_PREDICATE;
        };
    }

    private String selectUnaryOperatorInterfaceDescriptor(String primitiveType) {
        return switch (primitiveType) {
            case ConstantJavaType.ABBR_INTEGER -> ConstantJavaType.LJAVA_UTIL_FUNCTION_INT_UNARY_OPERATOR;
            case ConstantJavaType.ABBR_LONG -> ConstantJavaType.LJAVA_UTIL_FUNCTION_LONG_UNARY_OPERATOR;
            case ConstantJavaType.ABBR_DOUBLE -> ConstantJavaType.LJAVA_UTIL_FUNCTION_DOUBLE_UNARY_OPERATOR;
            default -> ConstantJavaType.LJAVA_UTIL_FUNCTION_FUNCTION;
        };
    }
}
