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

package com.caoccao.javet.swc4j.compiler.ast.stmt.switchstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for string switches (Phase 4)
 * Tests string-based switch statements using if-else chain approach
 */
public class TestCompileAstSwitchStmtString extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "foo":
                          result = 1
                          break
                        case "bar":
                          result = 2
                          break
                        case "baz":
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "foo"));
        assertEquals(2, (int) instanceRunner.invoke("test", "bar"));
        assertEquals(3, (int) instanceRunner.invoke("test", "baz"));
        assertEquals(0, (int) instanceRunner.invoke("test", "other"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringCaseSensitive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "Foo":
                          result = 1
                          break
                        case "foo":
                          result = 2
                          break
                        case "FOO":
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "Foo"));
        assertEquals(2, (int) instanceRunner.invoke("test", "foo"));
        assertEquals(3, (int) instanceRunner.invoke("test", "FOO"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringEmptyCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "":
                          result = 0
                          break
                        case "foo":
                          result = 1
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(0, (int) instanceRunner.invoke("test", ""));
        assertEquals(1, (int) instanceRunner.invoke("test", "foo"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringFallThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "a":
                          result += 1
                        case "b":
                          result += 10
                          break
                        case "c":
                          result += 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(11, (int) instanceRunner.invoke("test", "a")); // 1 + 10 = 11
        assertEquals(10, (int) instanceRunner.invoke("test", "b")); // 10
        assertEquals(100, (int) instanceRunner.invoke("test", "c")); // 100
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringHashCollision(JdkVersion jdkVersion) throws Exception {
        // "Aa" and "BB" have the same hash code in Java
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "Aa":
                          result = 1
                          break
                        case "BB":
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "Aa"));
        assertEquals(2, (int) instanceRunner.invoke("test", "BB"));
        assertEquals(0, (int) instanceRunner.invoke("test", "other"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringLongStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "a very long string literal that exceeds normal length":
                          result = 1
                          break
                        case "another long string":
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "a very long string literal that exceeds normal length"));
        assertEquals(2, (int) instanceRunner.invoke("test", "another long string"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringMultipleMatches(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "red":
                        case "green":
                        case "blue":
                          result = 1
                          break
                        case "yellow":
                        case "orange":
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "red"));
        assertEquals(1, (int) instanceRunner.invoke("test", "green"));
        assertEquals(1, (int) instanceRunner.invoke("test", "blue"));
        assertEquals(2, (int) instanceRunner.invoke("test", "yellow"));
        assertEquals(2, (int) instanceRunner.invoke("test", "orange"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringSpecialChars(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "hello\\nworld":
                          result = 1
                          break
                        case "tab\\there":
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "hello\nworld"));
        assertEquals(2, (int) instanceRunner.invoke("test", "tab\there"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "你好":
                          result = 1
                          break
                        case "مرحبا":
                          result = 2
                          break
                        case "🚀":
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "你好"));
        assertEquals(2, (int) instanceRunner.invoke("test", "مرحبا"));
        assertEquals(3, (int) instanceRunner.invoke("test", "🚀"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchStringWithDefault(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(str: string): int {
                      let result: int = 0
                      switch (str) {
                        case "apple":
                          result = 1
                          break
                        case "banana":
                          result = 2
                          break
                        default:
                          result = -1
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertEquals(1, (int) instanceRunner.invoke("test", "apple"));
        assertEquals(2, (int) instanceRunner.invoke("test", "banana"));
        assertEquals(-1, (int) instanceRunner.invoke("test", "xyz"));
    }
}
