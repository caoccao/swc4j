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
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.CodeGeneratorUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Processes call expressions for String instance methods.
 */
public final class CallExpressionForStringProcessor extends BaseAstProcessor<Swc4jAstCallExpr> {
    /**
     * Constructs a processor with the specified compiler.
     *
     * @param compiler the bytecode compiler
     */
    public CallExpressionForStringProcessor(ByteCodeCompiler compiler) {
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
            // Generate code for the object (String)
            compiler.getExpressionProcessor().generate(code, classWriter, memberExpr.getObj(), null);

            // Get the method name
            String methodName = null;
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                methodName = propIdent.getSym();
            }

            switch (methodName) {
                case ConstantJavaMethod.METHOD_CHAR_AT -> generateCharAt(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_CHAR_CODE_AT -> generateCharCodeAt(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_CODE_POINT_AT -> generateCodePointAt(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_CONCAT -> generateConcat(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_ENDS_WITH -> generateEndsWith(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_INCLUDES -> generateIncludes(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_INDEX_OF -> generateIndexOf(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_LAST_INDEX_OF -> generateLastIndexOf(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_MATCH -> generateMatch(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_MATCH_ALL -> generateMatchAll(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_PAD_END -> generatePadEnd(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_PAD_START -> generatePadStart(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REPEAT -> generateRepeat(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REPLACE -> generateReplace(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_REPLACE_ALL -> generateReplaceAll(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SEARCH -> generateSearch(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SLICE -> generateSlice(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SPLIT -> generateSplit(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_STARTS_WITH -> generateStartsWith(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SUBSTR -> generateSubstr(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_SUBSTRING -> generateSubstring(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_TEST -> generateTest(code, classWriter, callExpr);
                case ConstantJavaMethod.METHOD_TO_LOWER_CASE -> generateToLowerCase(code, classWriter);
                case ConstantJavaMethod.METHOD_TO_UPPER_CASE -> generateToUpperCase(code, classWriter);
                case ConstantJavaMethod.METHOD_TRIM -> generateTrim(code, classWriter);
                case ConstantJavaMethod.METHOD_TRIM_END, ConstantJavaMethod.METHOD_TRIM_RIGHT ->
                        generateTrimEnd(code, classWriter);
                case ConstantJavaMethod.METHOD_TRIM_START, ConstantJavaMethod.METHOD_TRIM_LEFT ->
                        generateTrimStart(code, classWriter);
                default ->
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), memberExpr, "Method '" + methodName + "()' not supported on String");
            }
        }
    }

    private void generateCharAt(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.charAt to handle bounds checking
        var cp = classWriter.getConstantPool();
        if (!callExpr.getArgs().isEmpty()) {
            var indexArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, indexArg.getExpr(), null);

            // Unbox if Integer wrapper
            String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, indexType);

            // Call StringApiUtils.charAt(String, int) -> String
            int charAtMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_CHAR_AT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__LJAVA_LANG_STRING);
            code.invokestatic(charAtMethod);
        } else {
            // No argument - pop string and push empty string
            code.pop();
            int emptyStrIndex = cp.addString("");
            code.ldc(emptyStrIndex);
        }
    }

    private void generateCharCodeAt(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.charCodeAt
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No index - return -1 (NaN)
            code.pop();
            code.iconst(-1);
        } else {
            var indexArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, indexArg.getExpr(), null);

            String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, indexType);

            // Call StringApiUtils.charCodeAt(String, int) -> int
            int charCodeAtMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_CHAR_CODE_AT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__I);
            code.invokestatic(charCodeAtMethod);
        }
    }

    private void generateCodePointAt(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use Java's codePointAt for proper surrogate pair handling
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No index - return -1 (similar to charCodeAt behavior)
            code.pop();
            code.iconst(-1);
        } else {
            var indexArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, indexArg.getExpr(), null);

            String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, indexType);

            // Call String.codePointAt(int) -> int
            int codePointAtMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_CODE_POINT_AT, ConstantJavaDescriptor.I__I);
            code.invokevirtual(codePointAtMethod);
        }
    }

    private void generateConcat(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.concat(str1, str2, ...) - chain multiple concat calls
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string (already on stack)
        } else {
            // For each argument, call concat
            var cp = classWriter.getConstantPool();
            for (var arg : callExpr.getArgs()) {
                compiler.getExpressionProcessor().generate(code, classWriter, arg.getExpr(), null);
                int concatMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_CONCAT, ConstantJavaDescriptor.LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                code.invokevirtual(concatMethod);
                // Stack now has result, which becomes receiver for next concat
            }
        }
    }

    private void generateEndsWith(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.endsWith(searchString)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            int endsWithMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_ENDS_WITH, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING__Z);
            code.invokevirtual(endsWithMethod);
        }
    }

    private void generateIncludes(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.includes(searchString)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            int containsMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_CONTAINS, ConstantJavaDescriptor.LJAVA_LANG_CHAR_SEQUENCE__Z);
            code.invokevirtual(containsMethod);
        }
    }

    private void generateIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.indexOf(searchString) or str.indexOf(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return -1
            code.pop(); // Pop string reference
            code.iconst(-1);
        } else {
            // First argument: search string
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            if (callExpr.getArgs().size() > 1) {
                // Second argument: fromIndex
                var fromIndexArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, fromIndexArg.getExpr(), null);

                // Unbox if Integer
                String fromIndexType = compiler.getTypeResolver().inferTypeFromExpr(fromIndexArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, fromIndexType);

                // Call indexOf(String, int)
                int indexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__I);
                code.invokevirtual(indexOfMethod);
            } else {
                // Call indexOf(String)
                int indexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_INDEX_OF, ConstantJavaDescriptor.LJAVA_LANG_STRING__I);
                code.invokevirtual(indexOfMethod);
            }
        }
    }

    private void generateLastIndexOf(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.lastIndexOf(searchString) or str.lastIndexOf(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return -1
            code.pop();
            code.iconst(-1);
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            if (callExpr.getArgs().size() > 1) {
                var fromIndexArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, fromIndexArg.getExpr(), null);

                String fromIndexType = compiler.getTypeResolver().inferTypeFromExpr(fromIndexArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, fromIndexType);

                int lastIndexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_LAST_INDEX_OF, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__I);
                code.invokevirtual(lastIndexOfMethod);
            } else {
                int lastIndexOfMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_LAST_INDEX_OF, ConstantJavaDescriptor.LJAVA_LANG_STRING__I);
                code.invokevirtual(lastIndexOfMethod);
            }
        }
    }

    private void generateMatch(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.match(pattern) - Returns ArrayList<String> or null
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return null
            code.pop();
            code.aconst_null();
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, patternArg.getExpr(), null);

            // Call StringApiUtils.match(String, String) -> ArrayList<String>
            var cp = classWriter.getConstantPool();
            int matchMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_MATCH, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(matchMethod);
        }
    }

    private void generateMatchAll(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.matchAll(pattern) - Returns ArrayList<ArrayList<String>>
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return empty ArrayList
            code.pop();
            int arrayListClass = cp.addClass(ConstantJavaType.JAVA_UTIL_ARRAYLIST);
            int arrayListInit = cp.addMethodRef(ConstantJavaType.JAVA_UTIL_ARRAYLIST, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.__V);
            code.newInstance(arrayListClass);
            code.dup();
            code.invokespecial(arrayListInit);
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, patternArg.getExpr(), null);

            // Call StringApiUtils.matchAll(String, String) -> ArrayList<ArrayList<String>>
            int matchAllMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_MATCH_ALL, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(matchAllMethod);
        }
    }

    private void generatePadEnd(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.padEnd
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string
        } else {
            var targetLengthArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, targetLengthArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            String lengthType = compiler.getTypeResolver().inferTypeFromExpr(targetLengthArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, lengthType);

            if (callExpr.getArgs().size() > 1) {
                // Custom pad string
                var padStringArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, padStringArg.getExpr(), null);

                // Call StringApiUtils.padEnd(String, int, String)
                int padEndMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_PAD_END, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                code.invokestatic(padEndMethod);
            } else {
                // Default pad string " "
                int spaceIndex = cp.addString(" ");
                code.ldc(spaceIndex);

                int padEndMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_PAD_END, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                code.invokestatic(padEndMethod);
            }
        }
    }

    private void generatePadStart(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.padStart
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string
        } else {
            var targetLengthArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, targetLengthArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            String lengthType = compiler.getTypeResolver().inferTypeFromExpr(targetLengthArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, lengthType);

            if (callExpr.getArgs().size() > 1) {
                // Custom pad string
                var padStringArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, padStringArg.getExpr(), null);

                // Call StringApiUtils.padStart(String, int, String)
                int padStartMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_PAD_START, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                code.invokestatic(padStartMethod);
            } else {
                // Default pad string " "
                int spaceIndex = cp.addString(" ");
                code.ldc(spaceIndex);

                int padStartMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_PAD_START, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
                code.invokestatic(padStartMethod);
            }
        }
    }

    private void generateRepeat(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.repeat(count)
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No count - return empty string
            code.pop();
            int emptyStrIndex = cp.addString("");
            code.ldc(emptyStrIndex);
        } else {
            var countArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, countArg.getExpr(), null);

            // Unbox if Integer
            String countType = compiler.getTypeResolver().inferTypeFromExpr(countArg.getExpr());
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, countType);

            // Call String.repeat(int) - Available in JDK 11+
            int repeatMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_REPEAT, ConstantJavaDescriptor.I__LJAVA_LANG_STRING);
            code.invokevirtual(repeatMethod);
        }
    }

    private void generateReplace(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.replace to match JavaScript behavior (first occurrence only, literal)
        if (callExpr.getArgs().size() < 2) {
            // Not enough arguments - return original string
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var replacementArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, replacementArg.getExpr(), null);

            // Call StringApiUtils.replace(String, String, String)
            var cp = classWriter.getConstantPool();
            int replaceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_REPLACE, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING_LJAVA_LANG_STRING__LJAVA_LANG_STRING);
            code.invokestatic(replaceMethod);
        }
    }

    private void generateReplaceAll(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.replaceAll(search, replacement)
        // Use Java's replace(CharSequence, CharSequence) which replaces ALL
        if (callExpr.getArgs().size() < 2) {
            // Not enough arguments - return original string
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var replacementArg = callExpr.getArgs().get(1);
            compiler.getExpressionProcessor().generate(code, classWriter, replacementArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            int replaceMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_REPLACE, ConstantJavaDescriptor.LJAVA_LANG_CHAR_SEQUENCE_LJAVA_LANG_CHAR_SEQUENCE__LJAVA_LANG_STRING);
            code.invokevirtual(replaceMethod);
        }
    }

    private void generateSearch(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.search(pattern) - Returns int (index or -1)
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return -1
            code.pop();
            code.iconst(-1);
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, patternArg.getExpr(), null);

            // Call StringApiUtils.search(String, String) -> int
            var cp = classWriter.getConstantPool();
            int searchMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SEARCH, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__I);
            code.invokestatic(searchMethod);
        }
    }

    private void generateSlice(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.slice to handle negative indices
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            var cp = classWriter.getConstantPool();
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: slice(start, end)
                var endArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

                String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, endType);

                // Call StringApiUtils.slice(String, int, int)
                int sliceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SLICE, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_II__LJAVA_LANG_STRING);
                code.invokestatic(sliceMethod);
            } else {
                // One argument: slice(start)
                int sliceMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SLICE, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__LJAVA_LANG_STRING);
                code.invokestatic(sliceMethod);
            }
        }
    }

    private void generateSplit(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Returns ArrayList<String> (JavaScript returns Array)
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No separator - call StringApiUtils.split with null separator
            code.aconst_null();

            // Call StringApiUtils.split(String, String) -> ArrayList<String>
            int splitMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SPLIT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__LJAVA_UTIL_ARRAYLIST);
            code.invokestatic(splitMethod);
        } else {
            var separatorArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, separatorArg.getExpr(), null);

            if (callExpr.getArgs().size() > 1) {
                // With limit
                var limitArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, limitArg.getExpr(), null);

                String limitType = compiler.getTypeResolver().inferTypeFromExpr(limitArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, limitType);

                // Call StringApiUtils.split(String, String, int) -> ArrayList<String>
                int splitMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SPLIT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING_I__LJAVA_UTIL_ARRAYLIST);
                code.invokestatic(splitMethod);
            } else {
                // Without limit
                // Call StringApiUtils.split(String, String) -> ArrayList<String>
                int splitMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SPLIT, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__LJAVA_UTIL_ARRAYLIST);
                code.invokestatic(splitMethod);
            }
        }
    }

    private void generateStartsWith(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.startsWith(searchString) or str.startsWith(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, searchArg.getExpr(), null);

            var cp = classWriter.getConstantPool();
            if (callExpr.getArgs().size() > 1) {
                // With position
                var posArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, posArg.getExpr(), null);

                String posType = compiler.getTypeResolver().inferTypeFromExpr(posArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, posType);

                int startsWithMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_STARTS_WITH, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__Z);
                code.invokevirtual(startsWithMethod);
            } else {
                int startsWithMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_STARTS_WITH, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING__Z);
                code.invokevirtual(startsWithMethod);
            }
        }
    }

    private void generateSubstr(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.substr to handle start+length semantics
        // Note: substr is deprecated in JavaScript but still widely used
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            var cp = classWriter.getConstantPool();
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: substr(start, length)
                var lengthArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, lengthArg.getExpr(), null);

                String lengthType = compiler.getTypeResolver().inferTypeFromExpr(lengthArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, lengthType);

                // Call StringApiUtils.substr(String, int, int)
                int substrMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SUBSTR, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_II__LJAVA_LANG_STRING);
                code.invokestatic(substrMethod);
            } else {
                // One argument only: substr(start) - extract to end of string
                code.ldc(cp.addInteger(Integer.MAX_VALUE));

                int substrMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SUBSTR, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_II__LJAVA_LANG_STRING);
                code.invokestatic(substrMethod);
            }
        }
    }

    private void generateSubstring(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.substring to handle edge cases
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, startArg.getExpr(), null);

            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            var cp = classWriter.getConstantPool();
            CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, startType);

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: substring(start, end)
                var endArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

                String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                CodeGeneratorUtils.unboxIntegerIfNeeded(code, classWriter, endType);

                // Call StringApiUtils.substring(String, int, int)
                int substringMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SUBSTRING, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_II__LJAVA_LANG_STRING);
                code.invokestatic(substringMethod);
            } else {
                // One argument: substring(start)
                int substringMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_SUBSTRING, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__LJAVA_LANG_STRING);
                code.invokestatic(substringMethod);
            }
        }
    }

    private void generateTest(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.test(pattern) - Returns boolean
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return false
            code.pop();
            code.iconst(0); // false
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, patternArg.getExpr(), null);

            // Call StringApiUtils.test(String, String) -> boolean
            var cp = classWriter.getConstantPool();
            int testMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_TEST, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_LJAVA_LANG_STRING__Z);
            code.invokestatic(testMethod);
        }
    }

    private void generateToLowerCase(CodeBuilder code, ClassWriter classWriter) {
        // str.toLowerCase()
        var cp = classWriter.getConstantPool();
        int toLowerMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_TO_LOWER_CASE, ConstantJavaDescriptor.__LJAVA_LANG_STRING);
        code.invokevirtual(toLowerMethod);
    }

    private void generateToUpperCase(CodeBuilder code, ClassWriter classWriter) {
        // str.toUpperCase()
        var cp = classWriter.getConstantPool();
        int toUpperMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_TO_UPPER_CASE, ConstantJavaDescriptor.__LJAVA_LANG_STRING);
        code.invokevirtual(toUpperMethod);
    }

    private void generateTrim(CodeBuilder code, ClassWriter classWriter) {
        // str.trim()
        var cp = classWriter.getConstantPool();
        int trimMethod = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_TRIM, ConstantJavaDescriptor.__LJAVA_LANG_STRING);
        code.invokevirtual(trimMethod);
    }

    private void generateTrimEnd(CodeBuilder code, ClassWriter classWriter) {
        // str.trimEnd() or str.trimRight() (alias)
        // Use StringApiUtils.trimEnd which uses stripTrailing() (JDK 11+)
        var cp = classWriter.getConstantPool();
        int trimEndMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_TRIM_END, ConstantJavaDescriptor.LJAVA_LANG_STRING__LJAVA_LANG_STRING);
        code.invokestatic(trimEndMethod);
    }

    private void generateTrimStart(CodeBuilder code, ClassWriter classWriter) {
        // str.trimStart() or str.trimLeft() (alias)
        // Use StringApiUtils.trimStart which uses stripLeading() (JDK 11+)
        var cp = classWriter.getConstantPool();
        int trimStartMethod = cp.addMethodRef(ConstantJavaType.COM_CAOCCAO_JAVET_SWC4J_COMPILER_JDK17_AST_UTILS_STRING_API_UTILS, ConstantJavaMethod.METHOD_TRIM_START, ConstantJavaDescriptor.LJAVA_LANG_STRING__LJAVA_LANG_STRING);
        code.invokestatic(trimStartMethod);
    }

}
