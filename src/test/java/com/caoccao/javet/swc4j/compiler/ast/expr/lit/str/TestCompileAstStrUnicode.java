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
 * Tests for Unicode and international characters.
 * Phase 3: Unicode and Special Characters (12 tests)
 */
public class TestCompileAstStrUnicode extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharFromUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): char {
                      return '\\u0041'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('A');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterFromUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return '\\u4E2D'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('中');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEmoji(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "Hello 🌍 World 🚀"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("Hello 🌍 World 🚀");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEmojiOnly(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "😀😁😂🤣😃"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("😀😁😂🤣😃");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringMixedUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "Hello 世界 🌍"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("Hello 世界 🌍");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeArabic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "مرحبا"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("مرحبا");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "\\u0041"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("A");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeChinese(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "你好世界"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("你好世界");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeEscapeSequence(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "\\u4F60\\u597D"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("你好");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeJapanese(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "こんにちは"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("こんにちは");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeKorean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "안녕하세요"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("안녕하세요");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringUnicodeMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      return "\\u0048\\u0065\\u006C\\u006C\\u006F"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("Hello");
    }
}
