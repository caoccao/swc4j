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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forofstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for for-of loops with break and continue (Phase 5)
 * Tests control flow statements within for-of loops
 */
public class TestCompileAstForOfStmtBreakContinue extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakAndContinueInSameLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5]
                      let result: string = ""
                      for (let value of arr) {
                        if ((value as int) == 2) {
                          continue
                        }
                        if ((value as int) == 4) {
                          break
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("13");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c", "d"]
                      let result: string = ""
                      for (let value of arr) {
                        if (value == "c") {
                          break
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ab");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInNestedIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5]
                      let result: string = ""
                      for (let value of arr) {
                        if ((value as int) > 2) {
                          if ((value as int) == 4) {
                            break
                          }
                          result += "!"
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("12!3");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakMapIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { a: 1, b: 2, c: 3, d: 4 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        if (key == "c") {
                          break
                        }
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ab");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakOnFirstIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5]
                      let count: int = 0
                      for (let value of arr) {
                        count++
                        break
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakStringIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let result: string = ""
                      for (let char of "abcdefg") {
                        if (char == "d") {
                          break
                        }
                        result += char
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("abc");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c", "d"]
                      let result: string = ""
                      for (let value of arr) {
                        if (value == "b" || value == "c") {
                          continue
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ad");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInNestedIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c", "d"]
                      let result: string = ""
                      for (let value of arr) {
                        if (value == "b") {
                          if (true) {
                            continue
                          }
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("acd");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueMapIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { a: 1, b: 2, c: 3, d: 4 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        if (key == "b" || key == "c") {
                          continue
                        }
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ad");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueOnAllIterations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3]
                      let sum: int = 0
                      for (let value of arr) {
                        sum += (value as int)
                        continue
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueStringIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let result: string = ""
                      for (let char of "aeiou") {
                        if (char == "e" || char == "o") {
                          continue
                        }
                        result += char
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("aiu");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleBreakConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5, 6]
                      let result: string = ""
                      for (let value of arr) {
                        if ((value as int) == 3) {
                          break
                        }
                        if ((value as int) == 5) {
                          break
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("12");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleContinueConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5]
                      let result: string = ""
                      for (let value of arr) {
                        if ((value as int) == 2) {
                          continue
                        }
                        if ((value as int) == 4) {
                          continue
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("135");
    }
}
