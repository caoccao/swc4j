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
 * Test suite for rest patterns in variable declarations (Phase 4)
 * Tests const/let [first, ...rest] = arr and const/let { x, ...rest } = obj
 */
public class TestCompileAstRestPatVarDecl extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAccessAfterDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest variable accessible after declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [10, 20, 30, 40]
                      const [first, ...tail] = arr
                      let sum: int = 0
                      for (const item of tail) {
                        sum += (item as int)
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // 20 + 30 + 40 = 90
        assertEquals(90, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAsOnly(JdkVersion jdkVersion) throws Exception {
        // const [...all] = arr - copies entire array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3]
                      const [...all] = arr
                      let count: int = 0
                      for (const item of all) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestEmpty(JdkVersion jdkVersion) throws Exception {
        // Rest is empty when all elements extracted
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2]
                      const [a, b, ...rest] = arr
                      let count: int = 0
                      for (const item of rest) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestMultipleElements(JdkVersion jdkVersion) throws Exception {
        // const [a, b, c, ...rest] = arr
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["A", "B", "C", "D", "E"]
                      const [a, b, c, ...rest] = arr
                      let result: string = ""
                      result += a
                      result += b
                      result += c
                      result += ":"
                      for (const item of rest) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("ABC:DE", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstArrayRest(JdkVersion jdkVersion) throws Exception {
        // const [first, ...rest] = arr
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5]
                      const [first, ...rest] = arr
                      let result: string = ""
                      result += first
                      result += ":"
                      for (const item of rest) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("1:2345", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstObjectRest(JdkVersion jdkVersion) throws Exception {
        // const { a, ...rest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: "A", b: "B", c: "C" }
                      const { a, ...rest } = obj
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("A:BC", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLetArrayRest(JdkVersion jdkVersion) throws Exception {
        // let [first, ...rest] = arr
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c"]
                      let [first, ...rest] = arr
                      let result: string = ""
                      result += first
                      result += ":"
                      for (const item of rest) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("a:bc", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLetObjectRest(JdkVersion jdkVersion) throws Exception {
        // let { x, ...rest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { x: "X", y: "Y", z: "Z" }
                      let { x, ...rest } = obj
                      let result: string = ""
                      result += x
                      result += ":"
                      for (const [k, v] of rest) {
                        result += k
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("X:yz", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleDestructuringDeclarations(JdkVersion jdkVersion) throws Exception {
        // Multiple destructuring with rest in same function
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3]
                      const obj = { a: "A", b: "B" }
                      const [first, ...arrRest] = arr
                      const { a, ...objRest } = obj
                      let result: string = ""
                      result += first
                      result += ":"
                      for (const item of arrRest) {
                        result += item
                      }
                      result += ":"
                      result += a
                      result += ":"
                      for (const [k, v] of objRest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("1:23:A:B", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAccessAfterDeclaration(JdkVersion jdkVersion) throws Exception {
        // Rest variable accessible after declaration
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { id: 1, x: 10, y: 20, z: 30 }
                      const { id, ...data } = obj
                      let sum: int = 0
                      for (const [k, v] of data) {
                        sum += (v as int)
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // 10 + 20 + 30 = 60
        assertEquals(60, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAsOnly(JdkVersion jdkVersion) throws Exception {
        // const { ...all } = obj - copies entire object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 1, b: 2, c: 3 }
                      const { ...all } = obj
                      let count: int = 0
                      for (const [k, v] of all) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestEmpty(JdkVersion jdkVersion) throws Exception {
        // Rest is empty when all properties extracted
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { x: 1, y: 2 }
                      const { x, y, ...rest } = obj
                      let count: int = 0
                      for (const [k, v] of rest) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestMultipleProps(JdkVersion jdkVersion) throws Exception {
        // const { a, b, c, ...rest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: "A", b: "B", c: "C", d: "D", e: "E" }
                      const { a, b, c, ...rest } = obj
                      let result: string = ""
                      result += a
                      result += b
                      result += c
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("ABC:DE", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestPreservesOrder(JdkVersion jdkVersion) throws Exception {
        // Rest preserves insertion order
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { z: 3, a: 1, m: 2 }
                      const { a, ...rest } = obj
                      let result: string = ""
                      for (const [k, v] of rest) {
                        result += k
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Order: z, m (a was extracted)
        assertEquals("zm", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestWithDefault(JdkVersion jdkVersion) throws Exception {
        // const { a = "default", ...rest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { b: "B", c: "C" }
                      const { a = "default", ...rest } = obj
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("default:BC", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestWithRename(JdkVersion jdkVersion) throws Exception {
        // const { a: x, ...rest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: "A", b: "B", c: "C" }
                      const { a: x, ...rest } = obj
                      let result: string = ""
                      result += x
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("A:BC", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestInConditional(JdkVersion jdkVersion) throws Exception {
        // Rest used in conditional context
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3]
                      const [first, ...rest] = arr
                      let hasRest: boolean = false
                      for (const item of rest) {
                        hasRest = true
                        break
                      }
                      return hasRest ? "yes" : "no"
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("yes", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Rest with mixed value types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { name: "test", count: 42, active: true }
                      const { name, ...metadata } = obj
                      let result: string = ""
                      result += name
                      result += ":"
                      for (const [k, v] of metadata) {
                        result += v
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals("test:42,true,", classA.getMethod("test").invoke(instance));
    }
}
