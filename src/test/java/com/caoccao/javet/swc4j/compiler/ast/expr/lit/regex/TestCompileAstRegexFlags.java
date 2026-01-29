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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Tests for regex flags.
 * Phase 2: Flags (10 tests)
 */
public class TestCompileAstRegexFlags extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexAllCompatibleFlags(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /pattern/imsu
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
        // i=2, m=8, s=32, u=256+64=320 → 2|8|32|320 = 362
        assertThat(pattern.flags()).isEqualTo(362);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexCaseInsensitive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /abc/i
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("abc");
        assertThat(pattern.flags()).isEqualTo(Pattern.CASE_INSENSITIVE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDotAll(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /./s
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo(".");
        assertThat(pattern.flags()).isEqualTo(Pattern.DOTALL);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexFlagOrder(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /pattern/msi
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        // Order should not matter: msi = ims
        int expected = Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE;
        assertThat(pattern.flags()).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexGlobalFlag(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /pattern/g
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
        // Global flag is ignored (not a Pattern flag)
        assertThat(pattern.flags()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexIndicesFlagError(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test(): Pattern {
                          return /pattern/d
                        }
                      }
                    }""");
        });
        assertThat(exception).isInstanceOf(Swc4jByteCodeCompilerException.class);
        // Check the root cause message
        assertThat(exception.getCause()).isNotNull();
        assertThat(exception.getCause().getMessage().contains("Indices flag 'd' is not supported")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexMultiline(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /^start/m
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("^start");
        assertThat(pattern.flags()).isEqualTo(Pattern.MULTILINE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexMultipleFlags(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /pattern/gim
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
        // g is ignored, i=2, m=8 → 10
        int expected = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;
        assertThat(pattern.flags()).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexStickyFlagError(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test(): Pattern {
                          return /pattern/y
                        }
                      }
                    }""");
        });
        assertThat(exception).isInstanceOf(Swc4jByteCodeCompilerException.class);
        // Check the root cause message
        assertThat(exception.getCause()).isNotNull();
        assertThat(exception.getCause().getMessage().contains("Sticky flag 'y' is not supported")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodeMode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\w+/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\w+");
        // u flag = UNICODE_CHARACTER_CLASS (256) | UNICODE_CASE (64) = 320
        int expected = Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE;
        assertThat(pattern.flags()).isEqualTo(expected);
    }
}
