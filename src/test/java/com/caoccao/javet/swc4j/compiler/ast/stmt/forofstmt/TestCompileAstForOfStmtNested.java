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
 * Test suite for nested for-of loops (Phase 8)
 * Tests multiple levels of nesting and labeled break/continue
 */
public class TestCompileAstForOfStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAccessingOuterValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr1 = [10, 20]
                      const arr2 = [1, 2]
                      let sum: int = 0
                      for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          sum += (v1 as int) + (v2 as int)
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(66);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInnerLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b"]
                      const arr2 = [1, 2, 3]
                      let result: string = ""
                      for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          if ((v2 as int) == 2) {
                            break
                          }
                          result += v1
                          result += v2
                        }
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        // Inner loop breaks at 2
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a1,b1,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInnerLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b"]
                      const arr2 = [1, 2, 3]
                      let result: string = ""
                      for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          if ((v2 as int) == 2) {
                            continue
                          }
                          result += v1
                          result += v2
                        }
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        // Inner loop continues (skips 2)
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a1a3,b1b3,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForInInsideForOf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b"]
                      const obj = { x: 1, y: 2 }
                      let result: string = ""
                      for (let value of arr) {
                        for (let key in obj) {
                          result += value + key
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("axaybxby");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForOfInsideForIn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2 }
                      const arr = ["x", "y"]
                      let result: string = ""
                      for (let key in obj) {
                        for (let value of arr) {
                          result += key + value
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("axaybxby");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b", "c"]
                      const arr2 = [1, 2, 3]
                      let result: string = ""
                      outer: for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          result += v1
                          result += v2
                          result += ","
                          if ((v2 as int) == 2) {
                            break outer
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        // Breaks outer loop when v2 == 2
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a1,a2,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b"]
                      const arr2 = [1, 2, 3]
                      let result: string = ""
                      outer: for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          if ((v2 as int) == 2) {
                            continue outer
                          }
                          result += v1
                          result += v2
                        }
                        result += "!"
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a1b1");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSequentialLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b"]
                      const arr2 = ["x", "y"]
                      let result: string = ""
                      for (let v of arr1) {
                        result += v
                      }
                      result += "-"
                      for (let v of arr2) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ab-xy");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2]
                      const str: string = "ab"
                      let result: string = ""
                      for (let num of arr) {
                        for (let char of str) {
                          result += num + char
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1a1b2a2b");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOuterArrayInnerString(JdkVersion jdkVersion) throws Exception {
        // Note: Nested for-of with casted string is complex. Using a simpler pattern.
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2]
                      const str: string = "xy"
                      let result: string = ""
                      for (let num of arr) {
                        for (let char of str) {
                          result += num
                          result += char
                        }
                        result += "-"
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1x1y-2x2y-");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr1 = [1, 2]
                      const arr2 = [1, 2]
                      const arr3 = [1, 2]
                      let count: int = 0
                      for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          for (let v3 of arr3) {
                            count++
                          }
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(8);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = ["a", "b"]
                      const arr2 = ["x", "y"]
                      let result: string = ""
                      for (let v1 of arr1) {
                        for (let v2 of arr2) {
                          result += v1
                          result += v2
                          result += ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ax,ay,bx,by,");
    }
}
