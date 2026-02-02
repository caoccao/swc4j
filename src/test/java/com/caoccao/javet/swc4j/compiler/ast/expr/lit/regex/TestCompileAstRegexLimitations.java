/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.regex;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for regex limitations: variable-length lookbehind and unicode properties.
 * These features have Java version dependencies or limited support.
 */
public class TestCompileAstRegexLimitations extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexComplexUnicodePattern(JdkVersion jdkVersion) throws Exception {
        // Complex pattern with multiple unicode properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /^\\p{Lu}\\p{Ll}+$/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("^\\p{Lu}\\p{Ll}+$");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegatedUnicodeProperty(JdkVersion jdkVersion) throws Exception {
        // \P{L} - Not a letter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\P{L}/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\P{L}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegativeVariableLengthLookbehind(JdkVersion jdkVersion) throws Exception {
        // Negative variable-length lookbehind: (?<!a+)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<!a+)b/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<!a+)b");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodePropertyDigit(JdkVersion jdkVersion) throws Exception {
        // \p{Nd} - Decimal digit number
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\p{Nd}/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\p{Nd}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodePropertyLetter(JdkVersion jdkVersion) throws Exception {
        // \p{L} or \p{Letter} - should work in both JS and Java
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\p{L}/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\p{L}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodeScriptGreek(JdkVersion jdkVersion) throws Exception {
        // \p{IsGreek} - Greek script (Java syntax)
        // JavaScript uses \p{Script=Greek}, Java uses \p{IsGreek} or \p{Greek}
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\p{IsGreek}/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\p{IsGreek}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariableLengthLookbehindAlternation(JdkVersion jdkVersion) throws Exception {
        // Variable-length lookbehind with alternation: (?<=foo|foobar)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=foo|foobar)test/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=foo|foobar)test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariableLengthLookbehindDigits(JdkVersion jdkVersion) throws Exception {
        // Variable-length lookbehind with \d+: (?<=prefix_\d+)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=prefix_\\d+)word/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=prefix_\\d+)word");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariableLengthLookbehindOptional(JdkVersion jdkVersion) throws Exception {
        // Variable-length lookbehind with optional: (?<=a?)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=a?)b/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=a?)b");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariableLengthLookbehindQuantifier(JdkVersion jdkVersion) throws Exception {
        // Variable-length lookbehind: (?<=a+) requires Java 11+
        // On Java 8-10, this would throw PatternSyntaxException
        // On Java 11+, this should work fine
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=a+)b/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=a+)b");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariableLookbehindWithUnicode(JdkVersion jdkVersion) throws Exception {
        // Combine variable-length lookbehind with unicode flag
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=\\p{L}+)\\d/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=\\p{L}+)\\d");
        // Verify unicode flags are set
        int expected = Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE;
        assertThat(pattern.flags()).isEqualTo(expected);
    }
}
