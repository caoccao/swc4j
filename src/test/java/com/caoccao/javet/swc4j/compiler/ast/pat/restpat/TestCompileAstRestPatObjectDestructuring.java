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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for rest patterns in object destructuring (Phase 3 + 5)
 * Tests { a, ...rest } pattern collecting remaining properties into a Map
 */
public class TestCompileAstRestPatObjectDestructuring extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicRestPattern(JdkVersion jdkVersion) throws Exception {
        // Basic rest pattern: { a, ...rest }
        // Count remaining properties by iterating
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ a: "A", b: "B", c: "C" }]
                      let result: string = ""
                      for (const { a, ...rest } of items) {
                        result += a
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("A:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAsOnlyProperty(JdkVersion jdkVersion) throws Exception {
        // Rest as only property: { ...all } copies entire object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ x: 1, y: 2, z: 3 }]
                      let totalSize: int = 0
                      for (const { ...all } of items) {
                        for (const [k, v] of all) {
                          totalSize++
                        }
                      }
                      return totalSize
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestCollectingKeys(JdkVersion jdkVersion) throws Exception {
        // Collect keys from rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ extracted: "X", keyA: "A", keyB: "B" }]
                      let result: string = ""
                      for (const { extracted, ...rest } of items) {
                        for (const [key, value] of rest) {
                          result += key
                          result += ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("keyA,keyB,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestConstDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with const declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ id: 1, x: 10, y: 20 }]
                      let sum: int = 0
                      for (const { id, ...data } of items) {
                        for (const [k, v] of data) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestEmptyArray(JdkVersion jdkVersion) throws Exception {
        // For-of over empty array with rest pattern
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = []
                      let count: int = 0
                      for (const { id, ...rest } of items) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestFromEmptyObject(JdkVersion jdkVersion) throws Exception {
        // Rest from empty object: rest = {}
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{}]
                      let restSize: int = 0
                      for (const { ...rest } of items) {
                        for (const [k, v] of rest) {
                          restSize++
                        }
                      }
                      return restSize
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestIteratingValues(JdkVersion jdkVersion) throws Exception {
        // Iterate over rest values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ extracted: "X", a: "A", b: "B", c: "C" }]
                      let result: string = ""
                      for (const { extracted, ...rest } of items) {
                        for (const [key, value] of rest) {
                          result += value
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ABC");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestLetDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest with let declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ id: 1, x: 10, y: 20 }]
                      let sum: int = 0
                      for (let { id, ...data } of items) {
                        for (const [k, v] of data) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestMultipleIterations(JdkVersion jdkVersion) throws Exception {
        // Each iteration creates new rest map
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [
                        { id: 1, name: "Alice", age: 30 },
                        { id: 2, name: "Bob" },
                        { id: 3 }
                      ]
                      let result: string = ""
                      for (const { id, ...rest } of items) {
                        result += id
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2,2:1,3:0,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestPreservesOrder(JdkVersion jdkVersion) throws Exception {
        // Rest preserves insertion order (LinkedHashMap)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ c: 3, a: 1, b: 2, d: 4 }]
                      let result: string = ""
                      for (const { b, ...rest } of items) {
                        for (const [key, value] of rest) {
                          result += key
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("cad");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithBreak(JdkVersion jdkVersion) throws Exception {
        // Rest with break statement
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ a: 1, b: 2 }, { a: 3, b: 4 }, { a: 5, b: 6 }]
                      let count: int = 0
                      for (const { a, ...rest } of items) {
                        count++
                        if ((a as int) > 2) {
                          break
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithContinue(JdkVersion jdkVersion) throws Exception {
        // Rest with continue statement
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ a: 1, b: 2 }, { a: 2, b: 3 }, { a: 3, b: 4 }]
                      let sum: int = 0
                      for (const { a, ...rest } of items) {
                        if ((a as int) === 2) {
                          continue
                        }
                        for (const [k, v] of rest) {
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithDefaultValue(JdkVersion jdkVersion) throws Exception {
        // Rest with default value property: { a = "default", ...rest }
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ b: "B", c: "C" }]
                      let result: string = ""
                      for (const { a = "default", ...rest } of items) {
                        result += a
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("default:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithEmptyRemaining(JdkVersion jdkVersion) throws Exception {
        // Rest when all properties are extracted: rest = {}
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ a: 1, b: 2 }]
                      let restSize: int = 0
                      for (const { a, b, ...rest } of items) {
                        for (const [k, v] of rest) {
                          restSize++
                        }
                      }
                      return restSize
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMissingProperty(JdkVersion jdkVersion) throws Exception {
        // Extract property that doesn't exist (gets null), rest still works
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ b: "B", c: "C" }]
                      let result: string = ""
                      for (const { a, ...rest } of items) {
                        result += (a === null ? "null" : "value")
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Rest with mixed value types - collect values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ str: "hello", num: 42, flag: true }]
                      let result: string = ""
                      for (const { str, ...rest } of items) {
                        for (const [key, value] of rest) {
                          result += value
                          result += ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("42,true,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMultipleExtracted(JdkVersion jdkVersion) throws Exception {
        // Multiple extracted properties before rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ a: "A", b: "B", c: "C", d: "D", e: "E" }]
                      let result: string = ""
                      for (const { a, b, c, ...rest } of items) {
                        result += a
                        result += b
                        result += c
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ABC:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithRenamedProperty(JdkVersion jdkVersion) throws Exception {
        // Rest with renamed property: { a: x, ...rest }
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ a: "X", b: "B", c: "C" }]
                      let result: string = ""
                      for (const { a: x, ...rest } of items) {
                        result += x
                        result += ":"
                        let count: int = 0
                        for (const [k, v] of rest) {
                          count++
                        }
                        result += count
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("X:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithReturn(JdkVersion jdkVersion) throws Exception {
        // Rest with return statement - iterate to find value
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const items = [{ id: 1, data: "first" }, { id: 2, data: "second" }]
                      for (const { id, ...rest } of items) {
                        if ((id as int) === 1) {
                          for (const [key, value] of rest) {
                            if (key === "data") {
                              return value
                            }
                          }
                        }
                      }
                      return "not found"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("first");
    }
}
