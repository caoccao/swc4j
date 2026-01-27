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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("abcd", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello world", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("500", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hiaba", classA.getMethod("test").invoke(instance)); // Truncates padding
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("005", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("abahi", classA.getMethod("test").invoke(instance)); // Truncates padding
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("ababab", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hi hi", classA.getMethod("test").invoke(instance)); // All occurrences
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello there", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hi hello", classA.getMethod("test").invoke(instance)); // Only first occurrence
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("  hello", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("  hello world", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("  hello", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello  ", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("  hello", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello  ", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello world  ", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello  ", classA.getMethod("test").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello", classA.getMethod("test").invoke(instance));
    }
}
