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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("worl", runner.createInstanceRunner("com.A").invoke("test")); // slice(-5, -1)
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
        assertEquals("hello", runner.createInstanceRunner("com.A").invoke("test")); // All but last 6
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
        assertEquals("world", runner.createInstanceRunner("com.A").invoke("test")); // Last 5 characters
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
        assertEquals("world", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("hello", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(List.of("a", "b", "c"), runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(List.of("h", "e", "l", "l", "o"), runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(List.of("hello", "world", "test"), runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(List.of("a", "b"), runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("hello", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("llo", runner.createInstanceRunner("com.A").invoke("test")); // Clamps to end
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
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test")); // Negative length returns empty
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
        assertEquals("world", runner.createInstanceRunner("com.A").invoke("test")); // Counts from end
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
        assertEquals("hel", runner.createInstanceRunner("com.A").invoke("test")); // Negative beyond start -> 0
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
        assertEquals("world", runner.createInstanceRunner("com.A").invoke("test")); // Extract to end
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
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test")); // Start beyond length
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
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test")); // Zero length
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
        assertEquals("hel", runner.createInstanceRunner("com.A").invoke("test")); // Negative clamped to 0
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
        assertEquals("world", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("el", runner.createInstanceRunner("com.A").invoke("test")); // JavaScript swaps arguments
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
        assertEquals("hello", runner.createInstanceRunner("com.A").invoke("test"));
    }
}
