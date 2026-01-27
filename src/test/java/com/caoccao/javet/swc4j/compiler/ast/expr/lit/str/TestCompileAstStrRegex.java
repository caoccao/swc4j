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
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for String regex methods: match, matchAll, search, test
 */
public class TestCompileAstStrRegex extends BaseTestCompileSuite {

    // matchAll() tests
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchAllBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "cat bat rat".matchAll("\\\\w+at")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(
                List.of(List.of("cat"), List.of("bat"), List.of("rat")),
                classA.getMethod("test").invoke(instance)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchAllEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "".matchAll("\\\\w+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchAllGroups(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "a1 b2 c3".matchAll("(\\\\w)(\\\\d)")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(
                List.of(
                        List.of("a1", "a", "1"),
                        List.of("b2", "b", "2"),
                        List.of("c3", "c", "3")
                ),
                classA.getMethod("test").invoke(instance)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchAllNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello".matchAll("\\\\d+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchAllOverlapping(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "aaa".matchAll("a+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Should match "aaa" once, not three separate "a"s
        assertEquals(List.of(List.of("aaa")), classA.getMethod("test").invoke(instance));
    }

    // match() tests
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello world".match("world")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("world"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchCaseInsensitive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "Hello World".match("(?i)hello")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("Hello"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "abc123def".match("\\\\d+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("123"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "".match("\\\\w+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertNull(classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchGroups(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello world".match("(\\\\w+) (\\\\w+)")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("hello world", "hello", "world"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMatchNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello world".match("xyz")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertNull(classA.getMethod("test").invoke(instance));
    }

    // search() tests
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello world".search("world")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchBeginning(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello world".search("^hello")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "abc123def".search("\\\\d")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "".search("\\\\w")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(-1, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello world".search("world$")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSearchNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return "hello world".search("xyz")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(-1, classA.getMethod("test").invoke(instance));
    }

    // test() tests
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".test("world")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestCaseInsensitive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "Hello".test("(?i)hello")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "abc123".test("\\\\d+")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "".test("\\\\w")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".test("xyz")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestPartialMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".test("^world")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTestWhitespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return "hello world".test("\\\\s")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance));
    }
}
