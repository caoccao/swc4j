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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstRegex;
import com.caoccao.javet.swc4j.compiler.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.regex.Pattern;

/**
 * Generator for Regex literals.
 * Compiles JavaScript/TypeScript regex literals (e.g., /pattern/flags) to Java Pattern objects.
 * Supports compatible flags and performs pattern conversion for known incompatibilities.
 */
public final class RegexLiteralGenerator extends BaseAstProcessor<Swc4jAstRegex> {
    public RegexLiteralGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts JavaScript flags to Java Pattern flags.
     *
     * @param jsFlags JavaScript flag string (e.g., "gim")
     * @return Combined Java Pattern flags as int
     * @throws Swc4jByteCodeCompilerException if unsupported flags are present
     */
    private int convertFlags(String jsFlags) throws Swc4jByteCodeCompilerException {
        int javaFlags = 0;
        boolean hasUnicodeFlag = jsFlags.contains("u");

        for (char flag : jsFlags.toCharArray()) {
            switch (flag) {
                case 'i':
                    javaFlags |= Pattern.CASE_INSENSITIVE;
                    // With 'u', also need UNICODE_CASE for proper Unicode case folding
                    if (hasUnicodeFlag) {
                        javaFlags |= Pattern.UNICODE_CASE;
                    }
                    break;
                case 'm':
                    javaFlags |= Pattern.MULTILINE;
                    break;
                case 's':
                    javaFlags |= Pattern.DOTALL;
                    break;
                case 'u':
                    javaFlags |= Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE;
                    break;
                case 'g':
                    // Global flag - not a Pattern flag, handled by usage (Matcher.find() loop)
                    // Silently ignore for now
                    break;
                case 'y':
                    throw new Swc4jByteCodeCompilerException(
                            "Sticky flag 'y' is not supported in Java regex. " +
                                    "Java Pattern does not have an equivalent to JavaScript's sticky matching.");
                case 'd':
                    throw new Swc4jByteCodeCompilerException(
                            "Indices flag 'd' is not supported in Java regex. " +
                                    "Java Pattern does not generate match indices in the same way.");
                default:
                    throw new Swc4jByteCodeCompilerException("Unknown regex flag: " + flag);
            }
        }

        return javaFlags;
    }

    /**
     * Converts JavaScript regex pattern to Java Pattern-compatible pattern.
     * Handles known syntax differences between JavaScript and Java regex.
     *
     * @param jsPattern JavaScript regex pattern string
     * @return Converted pattern string
     */
    private String convertPattern(String jsPattern) {
        String converted = jsPattern;

        // Convert \v (vertical tab) to \x0B
        // JavaScript supports \v, Java does not
        converted = converted.replace("\\v", "\\x0B");

        // Note: Other conversions like octal escapes and Unicode code points
        // are more complex and require regex parsing. For now, we'll pass
        // the pattern through and let Java Pattern.compile() validate it.
        // Invalid patterns will throw PatternSyntaxException at runtime.

        return converted;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstRegex regex,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Get pattern and flags
        String pattern = regex.getExp();
        String flags = regex.getFlags();

        // Convert pattern for Java compatibility
        String convertedPattern = convertPattern(pattern);

        // Convert flags
        int javaFlags = convertFlags(flags);

        // Generate bytecode: Pattern.compile(String, int)
        // Stack: [] → [Pattern]

        // Push pattern string
        int patternStringRef = cp.addString(convertedPattern);
        code.ldc(patternStringRef);

        // Push flags int
        if (javaFlags == 0) {
            code.iconst(0);
        } else if (javaFlags >= -1 && javaFlags <= 5) {
            code.iconst(javaFlags);
        } else if (javaFlags >= Byte.MIN_VALUE && javaFlags <= Byte.MAX_VALUE) {
            code.bipush((byte) javaFlags);
        } else if (javaFlags >= Short.MIN_VALUE && javaFlags <= Short.MAX_VALUE) {
            code.sipush((short) javaFlags);
        } else {
            // Use ldc for larger values
            int flagsRef = cp.addInteger(javaFlags);
            code.ldc(flagsRef);
        }

        // Call Pattern.compile(String, int)
        int compileMethodRef = cp.addMethodRef(
                "java/util/regex/Pattern",
                "compile",
                "(Ljava/lang/String;I)Ljava/util/regex/Pattern;");
        code.invokestatic(compileMethodRef);

        // Stack: [Pattern]
        // Pattern object is now on the stack, ready to be used or returned
    }
}
