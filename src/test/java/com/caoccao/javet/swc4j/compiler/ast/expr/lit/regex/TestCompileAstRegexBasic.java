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
 * Tests for basic regex patterns.
 * Phase 1: Basic Patterns (15 tests)
 */
public class TestCompileAstRegexBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexAnchors(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /^start/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("^start");
        assertThat(pattern.flags()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexCharacterClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[a-z0-9]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("[a-z0-9]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\d+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\d+");
        assertThat(pattern.flags()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDot(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /./
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo(".");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexEscapes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\n\\t\\r/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\n\\t\\r");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexGroups(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(abc)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(abc)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexHexEscape(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\x41/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\x41");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegatedClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[^a-z]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("[^a-z]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNonCapturingGroup(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?:abc)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?:abc)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexQuantifiers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testStar(): Pattern { return /a*/ }
                    testPlus(): Pattern { return /a+/ }
                    testQuestion(): Pattern { return /a?/ }
                    testExact(): Pattern { return /a{3}/ }
                    testRange(): Pattern { return /a{2,5}/ }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(((Pattern) instanceRunner.<Object>invoke("testStar")).pattern()).isEqualTo("a*");
        assertThat(((Pattern) instanceRunner.<Object>invoke("testPlus")).pattern()).isEqualTo("a+");
        assertThat(((Pattern) instanceRunner.<Object>invoke("testQuestion")).pattern()).isEqualTo("a?");
        assertThat(((Pattern) instanceRunner.<Object>invoke("testExact")).pattern()).isEqualTo("a{3}");
        assertThat(((Pattern) instanceRunner.<Object>invoke("testRange")).pattern()).isEqualTo("a{2,5}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexSimple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /abc/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("abc");
        assertThat(pattern.flags()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodeEscape(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\u0041/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\u0041");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexWhitespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\s+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\s+");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexWords(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\w+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\w+");
    }
}
