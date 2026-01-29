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
import static org.assertj.core.api.Assertions.assertThat;


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

        assertThat((int) instanceRunner.<Object>invoke("test", "foo")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "bar")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "baz")).isEqualTo(3);
        assertThat((int) instanceRunner.<Object>invoke("test", "other")).isEqualTo(0);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "Foo")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "foo")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "FOO")).isEqualTo(3);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "")).isEqualTo(0);
        assertThat((int) instanceRunner.<Object>invoke("test", "foo")).isEqualTo(1);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "a")).isEqualTo(11); // 1 + 10 = 11
        assertThat((int) instanceRunner.<Object>invoke("test", "b")).isEqualTo(10); // 10
        assertThat((int) instanceRunner.<Object>invoke("test", "c")).isEqualTo(100); // 100
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

        assertThat((int) instanceRunner.<Object>invoke("test", "Aa")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "BB")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "other")).isEqualTo(0);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "a very long string literal that exceeds normal length")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "another long string")).isEqualTo(2);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "red")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "green")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "blue")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "yellow")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "orange")).isEqualTo(2);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "hello\nworld")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "tab\there")).isEqualTo(2);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "你好")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "مرحبا")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "🚀")).isEqualTo(3);
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

        assertThat((int) instanceRunner.<Object>invoke("test", "apple")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", "banana")).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", "xyz")).isEqualTo(-1);
    }
}
