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
 * Test suite for for-of loops with object destructuring (Phase 11)
 * Tests extracting properties from objects in arrays using patterns like { name, age }
 */
public class TestCompileAstForOfStmtObjectDestructuring extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicObjectDestructuring(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const people = [{ name: "Alice", age: 30 }, { name: "Bob", age: 25 }]
                      let result: string = ""
                      for (const { name, age } of people) {
                        result += name
                        result += ":"
                        result += age
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("Alice:30,Bob:25,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstDeclaration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ id: 1 }, { id: 2 }]
                      let result: string = ""
                      for (const { id } of items) {
                        result += id
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("12", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ name: "Alice" }, { name: "Bob", role: "Admin" }]
                      let result: string = ""
                      for (const { name, role = "User" } of items) {
                        result += name
                        result += ":"
                        result += role
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("Alice:User,Bob:Admin,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = []
                      let count: int = 0
                      for (const { name } of items) {
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
    public void testLetDeclaration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ value: "a" }, { value: "b" }]
                      let result: string = ""
                      for (let { value } of items) {
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("ab", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ str: "hello", num: 42, flag: true }]
                      let result: string = ""
                      for (const { str, num, flag } of items) {
                        result += str
                        result += ":"
                        result += num
                        result += ":"
                        result += flag
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("hello:42:true", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleDefaultValues(JdkVersion jdkVersion) throws Exception {
        // Test multiple properties all using default values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ d: "D" }]
                      let result: string = ""
                      for (const { a = "A", b = "B", c = "C", d } of items) {
                        result += a
                        result += b
                        result += c
                        result += d
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("ABCD", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleIterations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ value: 10 }, { value: 20 }, { value: 30 }, { value: 40 }]
                      let sum: int = 0
                      for (const { value } of items) {
                        sum += (value as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedLoopsSimple(JdkVersion jdkVersion) throws Exception {
        // Test nested for-of with object destructuring in outer loop
        // The inner loop iterates over a simple array, outer uses object destructuring
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const people = [{ name: "Alice" }, { name: "Bob" }]
                      const letters = ["X", "Y"]
                      let result: string = ""
                      for (const { name } of people) {
                        for (const letter of letters) {
                          result += name
                          result += letter
                          result += ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("AliceX,AliceY,BobX,BobY,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPartialDefaultValue(JdkVersion jdkVersion) throws Exception {
        // Test that properties without values use defaults
        // Using string default since primitive default requires boxing
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ x: "a" }, { x: "b", y: "Y" }]
                      let result: string = ""
                      for (const { x, y = "?" } of items) {
                        result += x
                        result += ":"
                        result += y
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("a:?,b:Y,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRenamedProperty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ name: "Alice" }, { name: "Bob" }]
                      let result: string = ""
                      for (const { name: n } of items) {
                        result += n
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("Alice,Bob,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRenamedPropertyMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ firstName: "Alice", lastName: "Smith" }]
                      let result: string = ""
                      for (const { firstName: first, lastName: last } of items) {
                        result += last
                        result += ", "
                        result += first
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("Smith, Alice", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleElementArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ only: "value" }]
                      let result: string = ""
                      for (const { only } of items) {
                        result += only
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("value", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleProperty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ name: "Alice" }, { name: "Bob" }, { name: "Charlie" }]
                      let result: string = ""
                      for (const { name } of items) {
                        result += name
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("Alice,Bob,Charlie,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWithBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ name: "a" }, { name: "STOP" }, { name: "c" }]
                      let result: string = ""
                      for (const { name } of items) {
                        if (name === "STOP") {
                          break
                        }
                        result += name
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("a", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWithContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const items = [{ name: "a" }, { name: "SKIP" }, { name: "c" }]
                      let result: string = ""
                      for (const { name } of items) {
                        if (name === "SKIP") {
                          continue
                        }
                        result += name
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("ac", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWithNumericComputation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const items = [{ x: 1, y: 2 }, { x: 3, y: 4 }, { x: 5, y: 6 }]
                      let sum: int = 0
                      for (const { x, y } of items) {
                        sum += ((x as int) * (y as int))
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(44, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const items = [{ name: "first" }, { name: "second" }]
                      for (const { name } of items) {
                        return name
                      }
                      return "empty"
                    }
                  }
                }""");
        assertEquals("first", runner.createInstanceRunner("com.A").invoke("test"));
    }
}
