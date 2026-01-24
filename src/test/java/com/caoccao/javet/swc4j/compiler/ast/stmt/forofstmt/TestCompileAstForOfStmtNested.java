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
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // (10+1) + (10+2) + (20+1) + (20+2) = 11 + 12 + 21 + 22 = 66
        assertEquals(66, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInnerLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Inner loop breaks at 2
        assertEquals("a1,b1,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInnerLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Inner loop continues (skips 2)
        assertEquals("a1a3,b1b3,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForInInsideForOf(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("axaybxby", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForOfInsideForIn(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("axaybxby", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakToOuter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Breaks outer loop when v2 == 2
        assertEquals("a1,a2,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueToOuter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Continues outer when v2 == 2, skipping "!"
        assertEquals("a1b1", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSequentialLoops(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ab-xy", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1a1b2a2b", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOuterArrayInnerString(JdkVersion jdkVersion) throws Exception {
        // Note: Nested for-of with casted string is complex. Using a simpler pattern.
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1x1y-2x2y-", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedArrays(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 2 * 2 * 2 = 8
        assertEquals(8, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNestedArrays(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ax,ay,bx,by,", classA.getMethod("test").invoke(instance));
    }
}
