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
 * Test suite for nested rest patterns in destructuring (Phase 6)
 * Tests patterns like:
 * - const [a, [b, ...inner], ...outer] = nested
 * - const { x, nested: { y, ...innerRest }, ...outerRest } = obj
 */
public class TestCompileAstRestPatNestedDestructuring extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexNestedMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Complex nested: object containing array with nested object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = {
                        items: [
                          { name: "item1", value: 10 },
                          { name: "item2", value: 20 }
                        ],
                        meta: "M"
                      }
                      const { items: [{ name: firstName, ...itemRest }, ...otherItems], ...rest } = data
                      let result: string = ""
                      result += firstName
                      result += ":"
                      for (const [k, v] of itemRest) {
                        result += k
                        result += v
                      }
                      result += ":"
                      let count: int = 0
                      for (const item of otherItems) {
                        count++
                      }
                      result += count
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("item1:value10:1:M");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeepNestedObjectRest(JdkVersion jdkVersion) throws Exception {
        // Deep nested object with rest at multiple levels
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = {
                        level1: {
                          level2: { a: 1, b: 2 },
                          extra: "X"
                        },
                        top: "T"
                      }
                      const { level1: { level2: { a, ...l2Rest }, ...l1Rest }, ...topRest } = obj
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const [k, v] of l2Rest) {
                        result += k
                        result += v
                      }
                      result += ":"
                      for (const [k, v] of l1Rest) {
                        result += k
                        result += v
                      }
                      result += ":"
                      for (const [k, v] of topRest) {
                        result += k
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:b2:extraX:topT");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyRestAtNestedLevel(JdkVersion jdkVersion) throws Exception {
        // Empty rest at nested level
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const data = [1, [2]]
                      const [a, [b, ...inner]] = data
                      let count: int = 0
                      for (const item of inner) {
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
    public void testMixedArrayObjectNestedRest(JdkVersion jdkVersion) throws Exception {
        // Array containing objects with rest: const [{ a, ...objRest }, ...arrRest] = data
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [{ a: 1, b: 2 }, { c: 3 }, { d: 4 }]
                      const [{ a, ...objRest }, ...arrRest] = data
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const [k, v] of objRest) {
                        result += k
                        result += v
                      }
                      result += ":"
                      let count: int = 0
                      for (const item of arrRest) {
                        count++
                      }
                      result += count
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:b2:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedNestedPatternWithStrings(JdkVersion jdkVersion) throws Exception {
        // Mixed nested with string values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = { arr: ["a", "b", "c", "d"], key: "K", val: "V" }
                      const { arr: [first, second, ...arrRest], ...objRest } = data
                      let result: string = ""
                      result += first
                      result += second
                      result += ":"
                      for (const item of arrRest) {
                        result += item
                      }
                      result += ":"
                      for (const [k, v] of objRest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ab:cd:KV");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleNestedPatternsWithRest(JdkVersion jdkVersion) throws Exception {
        // Multiple nested patterns each with rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [[1, 2, 3], [4, 5, 6]]
                      const [[a, ...rest1], [b, ...rest2]] = data
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const item of rest1) {
                        result += item
                      }
                      result += ":"
                      result += b
                      result += ":"
                      for (const item of rest2) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:23:4:56");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedArrayCopyAll(JdkVersion jdkVersion) throws Exception {
        // Nested pattern with [...all] to copy entire inner array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [[1, 2, 3], 4, 5]
                      const [[...innerAll], ...outer] = data
                      let result: string = ""
                      for (const item of innerAll) {
                        result += item
                      }
                      result += ":"
                      for (const item of outer) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("123:45");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjectCopyAll(JdkVersion jdkVersion) throws Exception {
        // Nested pattern with {...all} to copy entire inner object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { inner: { a: 1, b: 2 }, x: 3, y: 4 }
                      const { inner: { ...innerAll }, ...outer } = obj
                      let result: string = ""
                      for (const [k, v] of innerAll) {
                        result += k
                        result += v
                      }
                      result += ":"
                      for (const [k, v] of outer) {
                        result += k
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a1b2:x3y4");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjectWithInnerRestOnly(JdkVersion jdkVersion) throws Exception {
        // Rest only at inner level: const { nested: { a, ...innerRest } } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { nested: { a: 1, b: 2, c: 3 }, x: 99 }
                      const { nested: { a, ...innerRest } } = obj
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const [k, v] of innerRest) {
                        result += k
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:b2c3");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRestUsedInCalculation(JdkVersion jdkVersion) throws Exception {
        // Use nested rest values in calculation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const data = [10, [20, 30, 40], 50, 60]
                      const [a, [b, ...inner], ...outer] = data
                      let sum: int = (a as int) + (b as int)
                      for (const item of inner) {
                        sum += (item as int)
                      }
                      for (const item of outer) {
                        sum += (item as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(210);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRestWithSingleElement(JdkVersion jdkVersion) throws Exception {
        // Nested pattern where inner array has only one element
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [1, [2], 3]
                      const [a, [b, ...inner], ...outer] = data
                      let result: string = ""
                      result += a
                      result += ":"
                      result += b
                      result += ":"
                      let innerCount: int = 0
                      for (const item of inner) {
                        innerCount++
                      }
                      result += innerCount
                      result += ":"
                      for (const item of outer) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2:0:3");
    }

    // Note: testNestedArrayInForOfLoop and testNestedObjectInForOfLoop are not implemented yet
    // They require updates to ForOfStatementProcessor to support nested patterns in for-of loop variable declarations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectWithNestedArrayRest(JdkVersion jdkVersion) throws Exception {
        // Object containing array with rest: const { arr: [first, ...rest], ...objRest } = data
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = { arr: [1, 2, 3], x: "X", y: "Y" }
                      const { arr: [first, ...arrRest], ...objRest } = data
                      let result: string = ""
                      result += first
                      result += ":"
                      for (const item of arrRest) {
                        result += item
                      }
                      result += ":"
                      for (const [k, v] of objRest) {
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:23:XY");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAtInnerLevelOnly(JdkVersion jdkVersion) throws Exception {
        // Rest only at inner level: const [a, [b, ...inner]] = data
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [1, [2, 3, 4, 5]]
                      const [a, [b, ...inner]] = data
                      let result: string = ""
                      result += a
                      result += ":"
                      result += b
                      result += ":"
                      for (const item of inner) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2:345");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestAtOuterLevelOnly(JdkVersion jdkVersion) throws Exception {
        // Rest only at outer level: const [[a, b], ...outer] = data
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const data = [[1, 2], [3, 4], [5, 6]]
                      const [[a, b], ...outer] = data
                      let result: string = ""
                      result += a
                      result += ":"
                      result += b
                      result += ":"
                      let count: int = 0
                      for (const item of outer) {
                        count++
                      }
                      result += count
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2:2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedArrayRest(JdkVersion jdkVersion) throws Exception {
        // Three-level nesting: const [[[a, ...l3], ...l2], ...l1] = nested
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const nested = [[[1, 2, 3], [4, 5]], [[6]]]
                      const [[[a, ...l3], ...l2], ...l1] = nested
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const item of l3) {
                        result += item
                      }
                      result += ":"
                      let l2Count: int = 0
                      for (const item of l2) {
                        l2Count++
                      }
                      result += l2Count
                      result += ":"
                      let l1Count: int = 0
                      for (const item of l1) {
                        l1Count++
                      }
                      result += l1Count
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:23:1:1");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNestedArrayRest(JdkVersion jdkVersion) throws Exception {
        // const [a, [b, ...inner], ...outer] = nested
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const nested = [1, [2, 3, 4], 5, 6]
                      const [a, [b, ...inner], ...outer] = nested
                      let result: string = ""
                      result += a
                      result += ":"
                      result += b
                      result += ":"
                      for (const item of inner) {
                        result += item
                      }
                      result += ":"
                      for (const item of outer) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2:34:56");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNestedObjectRest(JdkVersion jdkVersion) throws Exception {
        // const { x, nested: { y, ...innerRest }, ...outerRest } = obj
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { x: 1, nested: { y: 2, z: 3, w: 4 }, a: 5, b: 6 }
                      const { x, nested: { y, ...innerRest }, ...outerRest } = obj
                      let result: string = ""
                      result += x
                      result += ":"
                      result += y
                      result += ":"
                      for (const [k, v] of innerRest) {
                        result += k
                        result += v
                      }
                      result += ":"
                      for (const [k, v] of outerRest) {
                        result += k
                        result += v
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2:z3w4:a5b6");
    }
}

