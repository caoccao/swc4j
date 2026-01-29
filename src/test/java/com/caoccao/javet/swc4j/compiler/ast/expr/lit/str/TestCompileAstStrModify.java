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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.str;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for String modification methods: concat, repeat, replace, replaceAll, trim, trimStart, trimEnd, padStart, padEnd
 */
public class TestCompileAstStrModify extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConcatMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "a".concat("b", "c", "d")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("abcd");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConcatSingle(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".concat(" world")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello world");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPadEndBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "5".padEnd(3, "0")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("500");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPadEndLonger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hi".padEnd(5, "ab")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hiaba"); // Truncates padding
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPadStartBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "5".padStart(3, "0")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("005");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPadStartLonger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hi".padStart(5, "ab")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("abahi"); // Truncates padding
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRepeatBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "ab".repeat(3)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ababab");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRepeatZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".repeat(0)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReplaceAllBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello hello".replaceAll("hello", "hi")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hi hi"); // All occurrences
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReplaceBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".replace("world", "there")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello there");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReplaceFirstOccurrence(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello hello".replace("hello", "hi")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hi hello"); // Only first occurrence
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello  ".trim()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimEndBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello  ".trimEnd()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("  hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimEndEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "".trimEnd()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimEndMixed(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello world\\t\\n  ".trimEnd()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("  hello world");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimEndNoTrailing(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello".trimEnd()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("  hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimLeftAlias(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello  ".trimLeft()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello  ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimRightAlias(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello  ".trimRight()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("  hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimStartBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  hello  ".trimStart()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello  ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimStartEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "".trimStart()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimStartMixed(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "  \\t\\nhello world  ".trimStart()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello world  ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimStartNoLeading(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello  ".trimStart()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello  ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrimTabs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "\t\thello\t\t".trim()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }
}
