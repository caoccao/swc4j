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
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1:4", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAfterMultipleElements(JdkVersion jdkVersion) throws Exception {
        // Rest after multiple elements: [a, b, c, ...rest]
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ABC:DEF", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAsOnlyElement(JdkVersion jdkVersion) throws Exception {
        // Rest as only element: [...all] copies entire array
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestConcatStrings(JdkVersion jdkVersion) throws Exception {
        // Concatenate rest values
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("prefix:abc", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestConstDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with const declaration
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestEmptyArray(JdkVersion jdkVersion) throws Exception {
        // For-of over empty array with rest pattern
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestFromEmptyArray(JdkVersion jdkVersion) throws Exception {
        // Rest from array with no remaining elements
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestIteratingValues(JdkVersion jdkVersion) throws Exception {
        // Iterate over rest values
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ABC", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestLetDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with let declaration
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestMultipleIterations(JdkVersion jdkVersion) throws Exception {
        // Each iteration creates new rest array
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1:3,5:1,7:0,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestSumValues(JdkVersion jdkVersion) throws Exception {
        // Sum rest values (numeric)
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 1 + 2 + 3 + 4 = 10
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestTwoElements(JdkVersion jdkVersion) throws Exception {
        // Two elements before rest: [a, b, ...rest]
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("XY:123", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithBreak(JdkVersion jdkVersion) throws Exception {
        // Rest with break statement
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithContinue(JdkVersion jdkVersion) throws Exception {
        // Rest with continue statement
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Two iterations (first=1 and first=3), each has 1 element in rest
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithEmptyRemaining(JdkVersion jdkVersion) throws Exception {
        // Rest when all elements are extracted: rest = []
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Rest with mixed value types
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("42,true,end,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithReturn(JdkVersion jdkVersion) throws Exception {
        // Rest with return statement
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("first", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithSingleElementSource(JdkVersion jdkVersion) throws Exception {
        // Source array has only one element, rest is empty
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("only:0", classA.getMethod("test").invoke(instance));
    }
}
