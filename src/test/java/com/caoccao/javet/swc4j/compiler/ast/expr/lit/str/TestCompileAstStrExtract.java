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

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for String extraction methods: substring, slice, substr, split
 */
public class TestCompileAstStrExtract extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSliceBothNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".slice(-5, -1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("worl");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSliceNegativeEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".slice(0, -6)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello"); // All but last 6
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSliceNegativeStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".slice(-5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("world"); // Last 5 characters
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSliceOneArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".slice(6)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("world");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSliceTwoArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".slice(0, 5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSplitComma(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "a,b,c".split(",")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("a", "b", "c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSplitEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello".split("")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("h", "e", "l", "l", "o"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSplitSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello world test".split(" ")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("hello", "world", "test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSplitWithLimit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "a,b,c,d".split(",", 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("a", "b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".substr(0, 5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "".substr(0, 5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrExceedLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substr(2, 100)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("llo"); // Clamps to end
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrNegativeLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substr(2, -1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(""); // Negative length returns empty
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrNegativeStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".substr(-5, 5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("world"); // Counts from end
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrNegativeStartExceed(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substr(-10, 3)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hel"); // Negative beyond start -> 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrOneArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".substr(6)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("world"); // Extract to end
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrStartBeyond(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substr(10, 3)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(""); // Start beyond length
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstrZeroLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substr(2, 0)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(""); // Zero length
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstringNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substring(-2, 3)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hel"); // Negative clamped to 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstringOneArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".substring(6)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("world");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstringSwap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".substring(3, 1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("el"); // JavaScript swaps arguments
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubstringTwoArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world".substring(0, 5)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }
}
