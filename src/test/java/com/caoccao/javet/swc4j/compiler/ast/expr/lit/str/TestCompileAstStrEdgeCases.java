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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.str;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for string edge cases and boundary conditions.
 * Phase 6: Edge Cases (15 tests)
 */
public class TestCompileAstStrEdgeCases extends BaseTestCompileSuite {

    // NOTE: .length property access tests are out of scope for string literal implementation
    // These belong in member expression/property access tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): char {
                      return '\\uFFFF'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo('\uFFFF');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringAllDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "0123456789"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("0123456789");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringAllSpaces(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "          "
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("          ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringLineTerminators(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "line1\\nline2\\rline3\\r\\nline4"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("line1\nline2\rline3\r\nline4");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringMaxUnicodeChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "\\uFFFF"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("\uFFFF");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringMinUnicodeChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "\\u0000"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("\u0000");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringOnlyWhitespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "     "
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("     ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringRepeatedCharacters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "aaaaaaaaaa"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("aaaaaaaaaa");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringSpecialSymbols(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "!@#$%^&*()_+-=[]{}|;:,.<>?/"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("!@#$%^&*()_+-=[]{}|;:,.<>?/");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeAndEmoji(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "Text\\u0041\\u4E2D🚀End"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("TextA中🚀End");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringVeryLong(JdkVersion jdkVersion) throws Exception {
        // Create a string with 1000 'a' characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String expected = sb.toString();

        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "%s"
                    }
                  }
                }""".formatted(expected));
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringWithAllEscapeTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "a\\nb\\tc\\rd\\\\e\\'f\\"g\\bh\\fi"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a\nb\tc\rd\\e'f\"g\bh\fi");
    }

    // NOTE: testStringEmojiLength removed - .length property access is out of scope
}
