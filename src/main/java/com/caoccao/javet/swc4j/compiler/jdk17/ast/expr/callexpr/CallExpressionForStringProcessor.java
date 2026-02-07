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
                case "charAt" -> generateCharAt(code, classWriter, callExpr);
                case "charCodeAt" -> generateCharCodeAt(code, classWriter, callExpr);
                case "codePointAt" -> generateCodePointAt(code, classWriter, callExpr);
                case "concat" -> generateConcat(code, classWriter, callExpr);
                case "endsWith" -> generateEndsWith(code, classWriter, callExpr);
                case "includes" -> generateIncludes(code, classWriter, callExpr);
                case "indexOf" -> generateIndexOf(code, classWriter, callExpr);
                case "lastIndexOf" -> generateLastIndexOf(code, classWriter, callExpr);
                case "match" -> generateMatch(code, classWriter, callExpr);
                case "matchAll" -> generateMatchAll(code, classWriter, callExpr);
                case "padEnd" -> generatePadEnd(code, classWriter, callExpr);
                case "padStart" -> generatePadStart(code, classWriter, callExpr);
                case "repeat" -> generateRepeat(code, classWriter, callExpr);
                case "replace" -> generateReplace(code, classWriter, callExpr);
                case "replaceAll" -> generateReplaceAll(code, classWriter, callExpr);
                case "search" -> generateSearch(code, classWriter, callExpr);
                case "slice" -> generateSlice(code, classWriter, callExpr);
                case "split" -> generateSplit(code, classWriter, callExpr);
                case "startsWith" -> generateStartsWith(code, classWriter, callExpr);
                case "substr" -> generateSubstr(code, classWriter, callExpr);
                case "substring" -> generateSubstring(code, classWriter, callExpr);
                case "test" -> generateTest(code, classWriter, callExpr);
                case "toLowerCase" -> generateToLowerCase(code, classWriter);
                case "toUpperCase" -> generateToUpperCase(code, classWriter);
                case "trim" -> generateTrim(code, classWriter);
                case "trimEnd", "trimRight" -> generateTrimEnd(code, classWriter);
                case "trimStart", "trimLeft" -> generateTrimStart(code, classWriter);
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(indexType)) {
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(indexType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call StringApiUtils.charCodeAt(String, int) -> int
            int charCodeAtMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "charCodeAt", "(Ljava/lang/String;I)I");
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(indexType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call String.codePointAt(int) -> int
            int codePointAtMethod = cp.addMethodRef("java/lang/String", "codePointAt", "(I)I");
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
                int concatMethod = cp.addMethodRef("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
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
            int endsWithMethod = cp.addMethodRef("java/lang/String", "endsWith", "(Ljava/lang/String;)Z");
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
            int containsMethod = cp.addMethodRef("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
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
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(fromIndexType)) {
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
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(fromIndexType)) {
                    int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                    code.invokevirtual(intValueMethod);
                }

                int lastIndexOfMethod = cp.addMethodRef("java/lang/String", "lastIndexOf", "(Ljava/lang/String;I)I");
                code.invokevirtual(lastIndexOfMethod);
            } else {
                int lastIndexOfMethod = cp.addMethodRef("java/lang/String", "lastIndexOf", "(Ljava/lang/String;)I");
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
            int matchMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "match", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
            code.invokestatic(matchMethod);
        }
    }

    private void generateMatchAll(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.matchAll(pattern) - Returns ArrayList<ArrayList<String>>
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return empty ArrayList
            code.pop();
            int arrayListClass = cp.addClass("java/util/ArrayList");
            int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
            code.newInstance(arrayListClass);
            code.dup();
            code.invokespecial(arrayListInit);
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, patternArg.getExpr(), null);

            // Call StringApiUtils.matchAll(String, String) -> ArrayList<ArrayList<String>>
            int matchAllMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "matchAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(lengthType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Custom pad string
                var padStringArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, padStringArg.getExpr(), null);

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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(lengthType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Custom pad string
                var padStringArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, padStringArg.getExpr(), null);

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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(countType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call String.repeat(int) - Available in JDK 11+
            int repeatMethod = cp.addMethodRef("java/lang/String", "repeat", "(I)Ljava/lang/String;");
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
            int replaceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "replace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
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
            int replaceMethod = cp.addMethodRef("java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
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
            int searchMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "search", "(Ljava/lang/String;Ljava/lang/String;)I");
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: slice(start, end)
                var endArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

                String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(endType)) {
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
        }
    }

    private void generateSplit(CodeBuilder code, ClassWriter classWriter, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Returns ArrayList<String> (JavaScript returns Array)
        var cp = classWriter.getConstantPool();
        if (callExpr.getArgs().isEmpty()) {
            // No separator - call StringApiUtils.split with null separator
            code.aconst_null();

            // Call StringApiUtils.split(String, String) -> ArrayList<String>
            int splitMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "split", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
            code.invokestatic(splitMethod);
        } else {
            var separatorArg = callExpr.getArgs().get(0);
            compiler.getExpressionProcessor().generate(code, classWriter, separatorArg.getExpr(), null);

            if (callExpr.getArgs().size() > 1) {
                // With limit
                var limitArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, limitArg.getExpr(), null);

                String limitType = compiler.getTypeResolver().inferTypeFromExpr(limitArg.getExpr());
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(limitType)) {
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
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(posType)) {
                    int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                    code.invokevirtual(intValueMethod);
                }

                int startsWithMethod = cp.addMethodRef("java/lang/String", "startsWith", "(Ljava/lang/String;I)Z");
                code.invokevirtual(startsWithMethod);
            } else {
                int startsWithMethod = cp.addMethodRef("java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: substr(start, length)
                var lengthArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, lengthArg.getExpr(), null);

                String lengthType = compiler.getTypeResolver().inferTypeFromExpr(lengthArg.getExpr());
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(lengthType)) {
                    int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                    code.invokevirtual(intValueMethod);
                }

                // Call StringApiUtils.substr(String, int, int)
                int substrMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "substr", "(Ljava/lang/String;II)Ljava/lang/String;");
                code.invokestatic(substrMethod);
            } else {
                // One argument only: substr(start) - extract to end of string
                code.ldc(cp.addInteger(Integer.MAX_VALUE));

                int substrMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "substr", "(Ljava/lang/String;II)Ljava/lang/String;");
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
            if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: substring(start, end)
                var endArg = callExpr.getArgs().get(1);
                compiler.getExpressionProcessor().generate(code, classWriter, endArg.getExpr(), null);

                String endType = compiler.getTypeResolver().inferTypeFromExpr(endArg.getExpr());
                if (TypeConversionUtils.LJAVA_LANG_INTEGER.equals(endType)) {
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
            int testMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "test", "(Ljava/lang/String;Ljava/lang/String;)Z");
            code.invokestatic(testMethod);
        }
    }

    private void generateToLowerCase(CodeBuilder code, ClassWriter classWriter) {
        // str.toLowerCase()
        var cp = classWriter.getConstantPool();
        int toLowerMethod = cp.addMethodRef("java/lang/String", "toLowerCase", "()Ljava/lang/String;");
        code.invokevirtual(toLowerMethod);
    }

    private void generateToUpperCase(CodeBuilder code, ClassWriter classWriter) {
        // str.toUpperCase()
        var cp = classWriter.getConstantPool();
        int toUpperMethod = cp.addMethodRef("java/lang/String", "toUpperCase", "()Ljava/lang/String;");
        code.invokevirtual(toUpperMethod);
    }

    private void generateTrim(CodeBuilder code, ClassWriter classWriter) {
        // str.trim()
        var cp = classWriter.getConstantPool();
        int trimMethod = cp.addMethodRef("java/lang/String", "trim", "()Ljava/lang/String;");
        code.invokevirtual(trimMethod);
    }

    private void generateTrimEnd(CodeBuilder code, ClassWriter classWriter) {
        // str.trimEnd() or str.trimRight() (alias)
        // Use StringApiUtils.trimEnd which uses stripTrailing() (JDK 11+)
        var cp = classWriter.getConstantPool();
        int trimEndMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "trimEnd", "(Ljava/lang/String;)Ljava/lang/String;");
        code.invokestatic(trimEndMethod);
    }

    private void generateTrimStart(CodeBuilder code, ClassWriter classWriter) {
        // str.trimStart() or str.trimLeft() (alias)
        // Use StringApiUtils.trimStart which uses stripLeading() (JDK 11+)
        var cp = classWriter.getConstantPool();
        int trimStartMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "trimStart", "(Ljava/lang/String;)Ljava/lang/String;");
        code.invokestatic(trimStartMethod);
    }

    /**
     * Checks if the given type is supported by this processor.
     *
     * @param type the JVM type descriptor to check
     * @return true if the type is String
     */
    public boolean isTypeSupported(String type) {
        return TypeConversionUtils.LJAVA_LANG_STRING.equals(type);
    }
}
