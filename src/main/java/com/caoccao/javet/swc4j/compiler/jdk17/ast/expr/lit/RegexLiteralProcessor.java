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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.regex.Pattern;

/**
 * Generator for Regex literals.
 * Compiles JavaScript/TypeScript regex literals (e.g., /pattern/flags) to Java Pattern objects.
 * Supports compatible flags and performs pattern conversion for known incompatibilities.
 */
public final class RegexLiteralProcessor extends BaseAstProcessor<Swc4jAstRegex> {
    /**
     * Instantiates a new Regex literal processor.
     *
     * @param compiler the compiler
     */
    public RegexLiteralProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Converts JavaScript flags to Java Pattern flags.
     *
     * @param regex   the regex AST node (for error reporting)
     * @param jsFlags JavaScript flag string (e.g., "gim")
     * @return Combined Java Pattern flags as int
     * @throws Swc4jByteCodeCompilerException if unsupported flags are present
     */
    private int convertFlags(Swc4jAstRegex regex, String jsFlags) throws Swc4jByteCodeCompilerException {
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
                    throw new Swc4jByteCodeCompilerException(getSourceCode(),
                            regex,
                            "Sticky flag 'y' is not supported in Java regex. " +
                                    "Java Pattern does not have an equivalent to JavaScript's sticky matching.");
                case 'd':
                    throw new Swc4jByteCodeCompilerException(getSourceCode(),
                            regex,
                            "Indices flag 'd' is not supported in Java regex. " +
                                    "Java Pattern does not generate match indices in the same way.");
                default:
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), regex, "Unknown regex flag: " + flag);
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

        // Note: Variable-length lookbehind assertions (e.g., (?<=a+), (?<!foo|bar))
        // require Java 11+. On Java 8-10, these will throw PatternSyntaxException.
        // The current compiler targets Java 17, so this is not an issue, but
        // if future support for Java 8-10 is added, we should detect and reject
        // variable-length lookbehind patterns. See hasVariableLengthLookbehind().

        return converted;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstRegex regex,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        // Get pattern and flags
        String pattern = regex.getExp();
        String flags = regex.getFlags();

        // Convert pattern for Java compatibility
        String convertedPattern = convertPattern(pattern);

        // Convert flags
        int javaFlags = convertFlags(regex, flags);

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
                ConstantJavaType.JAVA_UTIL_REGEX_PATTERN,
                ConstantJavaMethod.METHOD_COMPILE,
                ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_STRING_I__LJAVA_UTIL_REGEX_PATTERN);
        code.invokestatic(compileMethodRef);

        // Stack: [Pattern]
        // Pattern object is now on the stack, ready to be used or returned
    }

    /**
     * Checks if pattern contains variable-length lookbehind assertions.
     * Variable-length lookbehind requires Java 11+.
     * Patterns like (?<=a+), (?<=foo|foobar), (?<!a*) have variable length.
     * Fixed-length patterns like (?<=abc), (?<=a{3}) are supported on all Java versions.
     *
     * @param pattern the regex pattern to check
     * @return true if pattern contains variable-length lookbehind
     */
    private boolean hasVariableLengthLookbehind(String pattern) {
        // This is a heuristic check - not a full regex parser
        // Looks for common variable-length patterns in lookbehind:
        // (?<=...*), (?<=...+), (?<=...?), (?<=...|...)
        // This is not exhaustive but covers common cases

        // Check for lookbehind assertions
        if (!pattern.contains("(?<=") && !pattern.contains("(?<!")) {
            return false;
        }

        // Look for patterns that indicate variable length:
        // - Quantifiers: *, +, ?
        // - Alternation with different lengths: |
        // - Variable repetition: {n,m} where n != m

        // Simple heuristic: if lookbehind contains *, +, ?, or | it's likely variable-length
        // This may have false positives but errs on the side of caution

        int lookbehindStart = pattern.indexOf("(?<=");
        if (lookbehindStart == -1) {
            lookbehindStart = pattern.indexOf("(?<!");
        }

        if (lookbehindStart == -1) {
            return false;
        }

        // Find the matching closing paren
        int depth = 0;
        int pos = lookbehindStart + 4; // Skip "(?<=" or "(?<!"
        while (pos < pattern.length()) {
            char c = pattern.charAt(pos);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                if (depth == 0) {
                    // Found the end of the lookbehind
                    String lookbehindContent = pattern.substring(lookbehindStart + 4, pos);
                    // Check for variable-length indicators
                    if (lookbehindContent.contains("*") ||
                            lookbehindContent.contains("+") ||
                            lookbehindContent.contains("?") ||
                            lookbehindContent.contains("|")) {
                        return true;
                    }
                    // Check for {n,m} where n != m
                    if (lookbehindContent.matches(".*\\{\\d+,\\d+\\}.*")) {
                        return true;
                    }
                    break;
                } else {
                    depth--;
                }
            }
            pos++;
        }

        return false;
    }
}
