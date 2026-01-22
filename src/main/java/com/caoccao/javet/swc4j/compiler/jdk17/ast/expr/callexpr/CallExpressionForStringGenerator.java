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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class CallExpressionForStringGenerator extends BaseAstProcessor<Swc4jAstCallExpr> {
    public CallExpressionForStringGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstCallExpr callExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (callExpr.getCallee() instanceof Swc4jAstMemberExpr memberExpr) {
            // Generate code for the object (String)
            compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

            // Get the method name
            String methodName = null;
            if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdent) {
                methodName = propIdent.getSym();
            }

            switch (methodName) {
                case "charAt" -> generateCharAt(code, cp, callExpr);
                case "charCodeAt" -> generateCharCodeAt(code, cp, callExpr);
                case "codePointAt" -> generateCodePointAt(code, cp, callExpr);
                case "concat" -> generateConcat(code, cp, callExpr);
                case "endsWith" -> generateEndsWith(code, cp, callExpr);
                case "includes" -> generateIncludes(code, cp, callExpr);
                case "indexOf" -> generateIndexOf(code, cp, callExpr);
                case "lastIndexOf" -> generateLastIndexOf(code, cp, callExpr);
                case "match" -> generateMatch(code, cp, callExpr);
                case "matchAll" -> generateMatchAll(code, cp, callExpr);
                case "padEnd" -> generatePadEnd(code, cp, callExpr);
                case "padStart" -> generatePadStart(code, cp, callExpr);
                case "repeat" -> generateRepeat(code, cp, callExpr);
                case "replace" -> generateReplace(code, cp, callExpr);
                case "replaceAll" -> generateReplaceAll(code, cp, callExpr);
                case "search" -> generateSearch(code, cp, callExpr);
                case "slice" -> generateSlice(code, cp, callExpr);
                case "split" -> generateSplit(code, cp, callExpr);
                case "startsWith" -> generateStartsWith(code, cp, callExpr);
                case "substr" -> generateSubstr(code, cp, callExpr);
                case "substring" -> generateSubstring(code, cp, callExpr);
                case "test" -> generateTest(code, cp, callExpr);
                case "toLowerCase" -> generateToLowerCase(code, cp);
                case "toUpperCase" -> generateToUpperCase(code, cp);
                case "trim" -> generateTrim(code, cp);
                case "trimEnd", "trimRight" -> generateTrimEnd(code, cp);
                case "trimStart", "trimLeft" -> generateTrimStart(code, cp);
                default ->
                        throw new Swc4jByteCodeCompilerException("Method '" + methodName + "()' not supported on String");
            }
        }
    }

    private void generateCharAt(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
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
    }

    private void generateCharCodeAt(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.charCodeAt
        if (callExpr.getArgs().isEmpty()) {
            // No index - return -1 (NaN)
            code.pop();
            code.iconst(-1);
        } else {
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
        }
    }

    private void generateCodePointAt(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use Java's codePointAt for proper surrogate pair handling
        if (callExpr.getArgs().isEmpty()) {
            // No index - return -1 (similar to charCodeAt behavior)
            code.pop();
            code.iconst(-1);
        } else {
            var indexArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, indexArg.getExpr(), null);

            String indexType = compiler.getTypeResolver().inferTypeFromExpr(indexArg.getExpr());
            if ("Ljava/lang/Integer;".equals(indexType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            // Call String.codePointAt(int) -> int
            int codePointAtMethod = cp.addMethodRef("java/lang/String", "codePointAt", "(I)I");
            code.invokevirtual(codePointAtMethod);
        }
    }

    private void generateConcat(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.concat(str1, str2, ...) - chain multiple concat calls
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string (already on stack)
        } else {
            // For each argument, call concat
            for (var arg : callExpr.getArgs()) {
                compiler.getExpressionGenerator().generate(code, cp, arg.getExpr(), null);
                int concatMethod = cp.addMethodRef("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
                code.invokevirtual(concatMethod);
                // Stack now has result, which becomes receiver for next concat
            }
        }
    }

    private void generateEndsWith(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.endsWith(searchString)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

            int endsWithMethod = cp.addMethodRef("java/lang/String", "endsWith", "(Ljava/lang/String;)Z");
            code.invokevirtual(endsWithMethod);
        }
    }

    private void generateIncludes(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.includes(searchString)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

            int containsMethod = cp.addMethodRef("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
            code.invokevirtual(containsMethod);
        }
    }

    private void generateIndexOf(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.indexOf(searchString) or str.indexOf(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return -1
            code.pop(); // Pop string reference
            code.iconst(-1);
        } else {
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
        }
    }

    private void generateLastIndexOf(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.lastIndexOf(searchString) or str.lastIndexOf(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return -1
            code.pop();
            code.iconst(-1);
        } else {
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
        }
    }

    private void generateMatch(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.match(pattern) - Returns ArrayList<String> or null
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return null
            code.pop();
            code.aconst_null();
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, patternArg.getExpr(), null);

            // Call StringApiUtils.match(String, String) -> ArrayList<String>
            int matchMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "match", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
            code.invokestatic(matchMethod);
        }
    }

    private void generateMatchAll(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.matchAll(pattern) - Returns ArrayList<ArrayList<String>>
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
            compiler.getExpressionGenerator().generate(code, cp, patternArg.getExpr(), null);

            // Call StringApiUtils.matchAll(String, String) -> ArrayList<ArrayList<String>>
            int matchAllMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "matchAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
            code.invokestatic(matchAllMethod);
        }
    }

    private void generatePadEnd(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.padEnd
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string
        } else {
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
        }
    }

    private void generatePadStart(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.padStart
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return original string
        } else {
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
        }
    }

    private void generateRepeat(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.repeat(count)
        if (callExpr.getArgs().isEmpty()) {
            // No count - return empty string
            code.pop();
            int emptyStrIndex = cp.addString("");
            code.ldc(emptyStrIndex);
        } else {
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
        }
    }

    private void generateReplace(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.replace to match JavaScript behavior (first occurrence only, literal)
        if (callExpr.getArgs().size() < 2) {
            // Not enough arguments - return original string
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

            var replacementArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, replacementArg.getExpr(), null);

            // Call StringApiUtils.replace(String, String, String)
            int replaceMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "replace", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
            code.invokestatic(replaceMethod);
        }
    }

    private void generateReplaceAll(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.replaceAll(search, replacement)
        // Use Java's replace(CharSequence, CharSequence) which replaces ALL
        if (callExpr.getArgs().size() < 2) {
            // Not enough arguments - return original string
        } else {
            var searchArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, searchArg.getExpr(), null);

            var replacementArg = callExpr.getArgs().get(1);
            compiler.getExpressionGenerator().generate(code, cp, replacementArg.getExpr(), null);

            int replaceMethod = cp.addMethodRef("java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
            code.invokevirtual(replaceMethod);
        }
    }

    private void generateSearch(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.search(pattern) - Returns int (index or -1)
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return -1
            code.pop();
            code.iconst(-1);
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, patternArg.getExpr(), null);

            // Call StringApiUtils.search(String, String) -> int
            int searchMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "search", "(Ljava/lang/String;Ljava/lang/String;)I");
            code.invokestatic(searchMethod);
        }
    }

    private void generateSlice(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.slice to handle negative indices
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
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
        }
    }

    private void generateSplit(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Returns ArrayList<String> (JavaScript returns Array)
        if (callExpr.getArgs().isEmpty()) {
            // No separator - call StringApiUtils.split with null separator
            code.aconst_null();

            // Call StringApiUtils.split(String, String) -> ArrayList<String>
            int splitMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "split", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;");
            code.invokestatic(splitMethod);
        } else {
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
        }
    }

    private void generateStartsWith(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.startsWith(searchString) or str.startsWith(searchString, position)
        if (callExpr.getArgs().isEmpty()) {
            code.pop();
            code.iconst(0); // false
        } else {
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
        }
    }

    private void generateSubstr(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.substr to handle start+length semantics
        // Note: substr is deprecated in JavaScript but still widely used
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
            var startArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, startArg.getExpr(), null);

            String startType = compiler.getTypeResolver().inferTypeFromExpr(startArg.getExpr());
            if ("Ljava/lang/Integer;".equals(startType)) {
                int intValueMethod = cp.addMethodRef("java/lang/Integer", "intValue", "()I");
                code.invokevirtual(intValueMethod);
            }

            if (callExpr.getArgs().size() > 1) {
                // Two arguments: substr(start, length)
                var lengthArg = callExpr.getArgs().get(1);
                compiler.getExpressionGenerator().generate(code, cp, lengthArg.getExpr(), null);

                String lengthType = compiler.getTypeResolver().inferTypeFromExpr(lengthArg.getExpr());
                if ("Ljava/lang/Integer;".equals(lengthType)) {
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

    private void generateSubstring(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // Use StringApiUtils.substring to handle edge cases
        if (callExpr.getArgs().isEmpty()) {
            // No arguments - return whole string
        } else {
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
        }
    }

    private void generateTest(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstCallExpr callExpr) throws Swc4jByteCodeCompilerException {
        // str.test(pattern) - Returns boolean
        if (callExpr.getArgs().isEmpty()) {
            // No pattern - return false
            code.pop();
            code.iconst(0); // false
        } else {
            var patternArg = callExpr.getArgs().get(0);
            compiler.getExpressionGenerator().generate(code, cp, patternArg.getExpr(), null);

            // Call StringApiUtils.test(String, String) -> boolean
            int testMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "test", "(Ljava/lang/String;Ljava/lang/String;)Z");
            code.invokestatic(testMethod);
        }
    }

    private void generateToLowerCase(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // str.toLowerCase()
        int toLowerMethod = cp.addMethodRef("java/lang/String", "toLowerCase", "()Ljava/lang/String;");
        code.invokevirtual(toLowerMethod);
    }

    private void generateToUpperCase(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // str.toUpperCase()
        int toUpperMethod = cp.addMethodRef("java/lang/String", "toUpperCase", "()Ljava/lang/String;");
        code.invokevirtual(toUpperMethod);
    }

    private void generateTrim(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // str.trim()
        int trimMethod = cp.addMethodRef("java/lang/String", "trim", "()Ljava/lang/String;");
        code.invokevirtual(trimMethod);
    }

    private void generateTrimEnd(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // str.trimEnd() or str.trimRight() (alias)
        // Use StringApiUtils.trimEnd which uses stripTrailing() (JDK 11+)
        int trimEndMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "trimEnd", "(Ljava/lang/String;)Ljava/lang/String;");
        code.invokestatic(trimEndMethod);
    }

    private void generateTrimStart(CodeBuilder code, ClassWriter.ConstantPool cp) {
        // str.trimStart() or str.trimLeft() (alias)
        // Use StringApiUtils.trimStart which uses stripLeading() (JDK 11+)
        int trimStartMethod = cp.addMethodRef("com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils", "trimStart", "(Ljava/lang/String;)Ljava/lang/String;");
        code.invokestatic(trimStartMethod);
    }

    public boolean isTypeSupported(String type) {
        return "Ljava/lang/String;".equals(type);
    }
}
