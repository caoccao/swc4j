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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for String search methods: charAt, charCodeAt, codePointAt, indexOf, lastIndexOf, includes, startsWith, endsWith
 */
public class TestCompileAstStrSearch extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharAtBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".charAt(1)
                    }
                  }
                }""");
        assertEquals("e", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharAtFirst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".charAt(0)
                    }
                  }
                }""");
        assertEquals("h", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharAtLast(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".charAt(4)
                    }
                  }
                }""");
        assertEquals("o", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharAtNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".charAt(-1)
                    }
                  }
                }""");
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharAtOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello".charAt(10)
                    }
                  }
                }""");
        assertEquals("", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharCodeAtBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "A".charCodeAt(0)
                    }
                  }
                }""");
        assertEquals(65, (int) runner.createInstanceRunner("com.A").invoke("test")); // ASCII 'A' = 65
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharCodeAtOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello".charCodeAt(10)
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test")); // -1 represents NaN
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCodePointAtBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "A".codePointAt(0)
                    }
                  }
                }""");
        assertEquals(65, (int) runner.createInstanceRunner("com.A").invoke("test")); // ASCII 'A' = 65
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCodePointAtEmoji(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "😀".codePointAt(0)
                    }
                  }
                }""");
        assertEquals(0x1F600, (int) runner.createInstanceRunner("com.A").invoke("test")); // Grinning face emoji
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCodePointAtMiddle(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello".codePointAt(2)
                    }
                  }
                }""");
        assertEquals(108, (int) runner.createInstanceRunner("com.A").invoke("test")); // 'l' = 108
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCodePointAtSurrogatePair(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "🚀world".codePointAt(0)
                    }
                  }
                }""");
        assertEquals(0x1F680, (int) runner.createInstanceRunner("com.A").invoke("test")); // Rocket emoji
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCodePointAtUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "你好".codePointAt(0)
                    }
                  }
                }""");
        assertEquals(0x4F60, (int) runner.createInstanceRunner("com.A").invoke("test")); // Chinese character '你'
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEndsWithFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "world".endsWith("hello")
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEndsWithTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".endsWith("world")
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIncludesFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello".includes("xyz")
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIncludesTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".includes("world")
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexOfBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello world".indexOf("world")
                    }
                  }
                }""");
        assertEquals(6, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexOfFirstOccurrence(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello hello".indexOf("hello")
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexOfNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello".indexOf("xyz")
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexOfWithPosition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello hello".indexOf("hello", 3)
                    }
                  }
                }""");
        assertEquals(6, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLastIndexOfBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello hello".lastIndexOf("hello")
                    }
                  }
                }""");
        assertEquals(6, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLastIndexOfNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello".lastIndexOf("xyz")
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStartsWithFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello".startsWith("world")
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStartsWithPosition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".startsWith("world", 6)
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStartsWithTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".startsWith("hello")
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }
}
