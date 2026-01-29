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

package com.caoccao.javet.swc4j.compiler.ast.pat.restpat;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for rest patterns in array destructuring (Phase 2 + 5)
 * Tests [first, ...rest] pattern collecting remaining elements into an ArrayList
 */
public class TestCompileAstRestPatArrayDestructuring extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicRestPattern(JdkVersion jdkVersion) throws Exception {
        // Basic rest pattern: [first, ...rest]
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [[1, 2, 3, 4, 5]]
                      let result: string = ""
                      for (const [first, ...rest] of arrays) {
                        result += first
                        result += ":"
                        let count: int = 0
                        for (const item of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("1:4", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAfterMultipleElements(JdkVersion jdkVersion) throws Exception {
        // Rest after multiple elements: [a, b, c, ...rest]
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["A", "B", "C", "D", "E", "F"]]
                      let result: string = ""
                      for (const [a, b, c, ...rest] of arrays) {
                        result += a
                        result += b
                        result += c
                        result += ":"
                        for (const item of rest) {
                          result += item
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("ABC:DEF", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAsOnlyElement(JdkVersion jdkVersion) throws Exception {
        // Rest as only element: [...all] copies entire array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[1, 2, 3]]
                      let totalSize: int = 0
                      for (const [...all] of arrays) {
                        for (const item of all) {
                          totalSize++
                        }
                      }
                      return totalSize
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestConcatStrings(JdkVersion jdkVersion) throws Exception {
        // Concatenate rest values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["prefix", "a", "b", "c"]]
                      let result: string = ""
                      for (const [prefix, ...rest] of arrays) {
                        result += prefix
                        result += ":"
                        for (const item of rest) {
                          result += item
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("prefix:abc", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestConstDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with const declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[1, 2, 3]]
                      let sum: int = 0
                      for (const [id, ...data] of arrays) {
                        for (const item of data) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestEmptyArray(JdkVersion jdkVersion) throws Exception {
        // For-of over empty array with rest pattern
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = []
                      let count: int = 0
                      for (const [first, ...rest] of arrays) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestFromEmptyArray(JdkVersion jdkVersion) throws Exception {
        // Rest from array with no remaining elements
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [["only"]]
                      let restCount: int = 0
                      for (const [first, ...rest] of arrays) {
                        for (const item of rest) {
                          restCount++
                        }
                      }
                      return restCount
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestIteratingValues(JdkVersion jdkVersion) throws Exception {
        // Iterate over rest values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["X", "A", "B", "C"]]
                      let result: string = ""
                      for (const [extracted, ...rest] of arrays) {
                        for (const item of rest) {
                          result += item
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("ABC", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestLetDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with let declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[1, 2, 3]]
                      let sum: int = 0
                      for (let [id, ...data] of arrays) {
                        for (const item of data) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestMultipleIterations(JdkVersion jdkVersion) throws Exception {
        // Each iteration creates new rest array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [
                        [1, 2, 3, 4],
                        [5, 6],
                        [7]
                      ]
                      let result: string = ""
                      for (const [first, ...rest] of arrays) {
                        result += first
                        result += ":"
                        let count: int = 0
                        for (const item of rest) {
                          count++
                        }
                        result += count
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("1:3,5:1,7:0,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestSumValues(JdkVersion jdkVersion) throws Exception {
        // Sum rest values (numeric)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[10, 1, 2, 3, 4]]
                      let sum: int = 0
                      for (const [first, ...rest] of arrays) {
                        for (const item of rest) {
                          sum += (item as int)
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestTwoElements(JdkVersion jdkVersion) throws Exception {
        // Two elements before rest: [a, b, ...rest]
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["X", "Y", "1", "2", "3"]]
                      let result: string = ""
                      for (const [a, b, ...rest] of arrays) {
                        result += a
                        result += b
                        result += ":"
                        for (const item of rest) {
                          result += item
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("XY:123", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithBreak(JdkVersion jdkVersion) throws Exception {
        // Rest with break statement
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[1, 2], [3, 4], [5, 6]]
                      let count: int = 0
                      for (const [first, ...rest] of arrays) {
                        count++
                        if ((first as int) > 2) {
                          break
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithContinue(JdkVersion jdkVersion) throws Exception {
        // Rest with continue statement
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [[1, 2], [2, 3], [3, 4]]
                      let sum: int = 0
                      for (const [first, ...rest] of arrays) {
                        if ((first as int) === 2) {
                          continue
                        }
                        for (const item of rest) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithEmptyRemaining(JdkVersion jdkVersion) throws Exception {
        // Rest when all elements are extracted: rest = []
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arrays = [["a", "b"]]
                      let restSize: int = 0
                      for (const [a, b, ...rest] of arrays) {
                        for (const item of rest) {
                          restSize++
                        }
                      }
                      return restSize
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Rest with mixed value types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["header", 42, true, "end"]]
                      let result: string = ""
                      for (const [first, ...rest] of arrays) {
                        for (const item of rest) {
                          result += item
                          result += ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("42,true,end,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithReturn(JdkVersion jdkVersion) throws Exception {
        // Rest with return statement
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const arrays = [[1, "first", "second"], [2, "third"]]
                      for (const [id, ...data] of arrays) {
                        if ((id as int) === 1) {
                          for (const item of data) {
                            return item
                          }
                        }
                      }
                      return "not found"
                    }
                  }
                }""");
        assertEquals("first", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithSingleElementSource(JdkVersion jdkVersion) throws Exception {
        // Source array has only one element, rest is empty
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arrays = [["only"]]
                      let result: string = ""
                      for (const [first, ...rest] of arrays) {
                        result += first
                        result += ":"
                        let count: int = 0
                        for (const item of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("only:0", runner.createInstanceRunner("com.A").invoke("test"));
    }
}
