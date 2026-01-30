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
 * Test suite for primitive type switch statements
 * Tests switch on byte, short, char with automatic promotion to int
 */
public class TestCompileAstSwitchStmtPrimitive extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: byte): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 10
                          break
                        case 2:
                          result = 20
                          break
                        case 3:
                          result = 30
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (byte) 1)).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("test", (byte) 2)).isEqualTo(20);
        assertThat((int) instanceRunner.invoke("test", (byte) 3)).isEqualTo(30);
        assertThat((int) instanceRunner.invoke("test", (byte) 4)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteFallThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: byte): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result += 1
                        case 2:
                          result += 10
                          break
                        case 3:
                          result += 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (byte) 1)).isEqualTo(11); // 1 + 10
        assertThat((int) instanceRunner.invoke("test", (byte) 2)).isEqualTo(10); // 10
        assertThat((int) instanceRunner.invoke("test", (byte) 3)).isEqualTo(100); // 100
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: byte): int {
                      let result: int = 0
                      switch (x) {
                        case -10:
                          result = 1
                          break
                        case 0:
                          result = 2
                          break
                        case 10:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (byte) -10)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", (byte) 0)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", (byte) 10)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", (byte) 5)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteSparse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: byte): int {
                      switch (x) {
                        case 1: return 1
                        case 10: return 2
                        case 20: return 3
                        case 50: return 4
                        default: return 0
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (byte) 1)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", (byte) 10)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", (byte) 20)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", (byte) 50)).isEqualTo(4);
        assertThat((int) instanceRunner.invoke("test", (byte) 5)).isEqualTo(0); // Default
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCharBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: char): int {
                      let result: int = 0
                      switch (x) {
                        case 'a':
                          result = 1
                          break
                        case 'b':
                          result = 2
                          break
                        case 'c':
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 'a')).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", 'b')).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", 'c')).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", 'd')).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCharDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: char): int {
                      switch (x) {
                        case '0': return 0
                        case '1': return 1
                        case '2': return 2
                        case '3': return 3
                        case '4': return 4
                        case '5': return 5
                        case '6': return 6
                        case '7': return 7
                        case '8': return 8
                        case '9': return 9
                        default: return -1
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", '0')).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("test", '5')).isEqualTo(5);
        assertThat((int) instanceRunner.invoke("test", '9')).isEqualTo(9);
        assertThat((int) instanceRunner.invoke("test", 'a')).isEqualTo(-1); // Default
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCharUpperLower(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: char): int {
                      let result: int = 0
                      switch (x) {
                        case 'A':
                        case 'a':
                          result = 1
                          break
                        case 'B':
                        case 'b':
                          result = 2
                          break
                        case 'C':
                        case 'c':
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 'A')).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", 'a')).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", 'B')).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", 'b')).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", 'C')).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", 'c')).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", 'D')).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchShortBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: short): int {
                      let result: int = 0
                      switch (x) {
                        case 100:
                          result = 1
                          break
                        case 200:
                          result = 2
                          break
                        case 300:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (short) 100)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", (short) 200)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", (short) 300)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", (short) 400)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchShortDense(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: short): int {
                      switch (x) {
                        case 0: return 0
                        case 1: return 1
                        case 2: return 2
                        case 3: return 3
                        case 4: return 4
                        default: return -1
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (short) 0)).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("test", (short) 1)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", (short) 2)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", (short) 3)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", (short) 4)).isEqualTo(4);
        assertThat((int) instanceRunner.invoke("test", (short) 5)).isEqualTo(-1); // Default
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchShortWithDefault(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: short): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 10
                          break
                        case 2:
                          result = 20
                          break
                        default:
                          result = 99
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", (short) 1)).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("test", (short) 2)).isEqualTo(20);
        assertThat((int) instanceRunner.invoke("test", (short) 3)).isEqualTo(99); // Default
        assertThat((int) instanceRunner.invoke("test", (short) 1000)).isEqualTo(99); // Default
    }
}
