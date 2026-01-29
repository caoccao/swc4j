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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(66, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("a1,b1,", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("a1a3,b1b3,", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("axaybxby", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("axaybxby", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("a1,a2,", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("a1b1", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("ab-xy", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("1a1b2a2b", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("1x1y-2x2y-", runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(8, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals("ax,ay,bx,by,", runner.createInstanceRunner("com.A").invoke("test"));
    }
}
