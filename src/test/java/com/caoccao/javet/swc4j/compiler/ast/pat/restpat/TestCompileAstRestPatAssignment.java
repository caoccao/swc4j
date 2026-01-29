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
 * Test suite for rest patterns in assignment expressions (Phase 7)
 * Tests [a, ...rest] = newArray; and { x, ...rest } = newObject;
 */
public class TestCompileAstRestPatAssignment extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignment(JdkVersion jdkVersion) throws Exception {
        // Basic array rest assignment
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = []
                      const arr = [1, 2, 3, 4, 5];
                      [a, ...rest] = arr
                      let result: string = ""
                      result += a
                      result += ":"
                      for (const item of rest) {
                        result += item
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2345");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignmentEmptyRest(JdkVersion jdkVersion) throws Exception {
        // Array rest assignment with empty rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let a: Object = null
                      let b: Object = null
                      let rest = []
                      const arr = ["X", "Y"];
                      [a, b, ...rest] = arr
                      let count: int = 0
                      for (const item of rest) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignmentInLoop(JdkVersion jdkVersion) throws Exception {
        // Array rest assignment inside a loop
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let first: Object = null
                      let rest = []
                
                      const arrays = [[1, 2, 3], [4, 5], [6]];
                      let result: string = ""
                      for (const arr of arrays) {
                        [first, ...rest] = arr
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2,4:1,6:0,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignmentMultipleElements(JdkVersion jdkVersion) throws Exception {
        // Array rest assignment with multiple elements before rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let b: Object = null
                      let c: Object = null
                      let rest = []
                      const arr = ["A", "B", "C", "D", "E"];
                      [a, b, c, ...rest] = arr
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ABC:DE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignmentSum(JdkVersion jdkVersion) throws Exception {
        // Sum rest values after array assignment
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let first: Object = null
                      let rest = []
                      const arr = [10, 1, 2, 3, 4];
                      [first, ...rest] = arr
                      let sum: int = 0
                      for (const item of rest) {
                        sum += (item as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestAssignmentWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Array rest assignment with mixed types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let first: Object = null
                      let rest = []
                      const arr = ["header", 42, true, "end"];
                      [first, ...rest] = arr
                      let result: string = ""
                      for (const item of rest) {
                        result += item
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("42,true,end,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayRestSequentialAssignments(JdkVersion jdkVersion) throws Exception {
        // Multiple sequential array rest assignments
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = []
                
                      const arr1 = [1, 2, 3];
                      [a, ...rest] = arr1
                
                      let result1: string = ""
                      result1 += a
                      result1 += ":"
                      let count1: int = 0
                      for (const item of rest) {
                        count1++
                      }
                      result1 += count1
                
                      const arr2 = [4, 5, 6, 7, 8];
                      [a, ...rest] = arr2
                
                      let result2: string = ""
                      result2 += a
                      result2 += ":"
                      let count2: int = 0
                      for (const item of rest) {
                        count2++
                      }
                      result2 += count2
                
                      return result1 + "," + result2
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2,4:4");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignment(JdkVersion jdkVersion) throws Exception {
        // Basic object rest assignment
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = {}
                      const obj = { a: "A", b: "B", c: "C" };
                      ({ a, ...rest } = obj)
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("A:BC");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentEmptyRest(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment with empty rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: Object = null
                      let y: Object = null
                      let rest = {}
                      const obj = { x: 1, y: 2 };
                      ({ x, y, ...rest } = obj)
                      let count: int = 0
                      for (const [k, v] of rest) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentInLoop(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment inside a loop
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = {}
                
                      const objects = [
                        { a: 1, b: 2, c: 3 },
                        { a: 4, d: 5 },
                        { a: 6 }
                      ];
                      let result: string = ""
                      for (const obj of objects) {
                        ({ a, ...rest } = obj)
                        result += a
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1:2,4:1,6:0,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentMultipleProps(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment with multiple properties before rest
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let b: Object = null
                      let c: Object = null
                      let rest = {}
                      const obj = { a: "A", b: "B", c: "C", d: "D", e: "E" };
                      ({ a, b, c, ...rest } = obj)
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("ABC:DE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentPreservesOrder(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment preserves insertion order
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = {}
                      const obj = { z: 3, a: 1, m: 2 };
                      ({ a, ...rest } = obj)
                      let result: string = ""
                      for (const [k, v] of rest) {
                        result += k
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("zm");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentSum(JdkVersion jdkVersion) throws Exception {
        // Sum rest values after object assignment
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let id: Object = null
                      let rest = {}
                      const obj = { id: 999, x: 10, y: 20, z: 30 };
                      ({ id, ...rest } = obj)
                      let sum: int = 0
                      for (const [k, v] of rest) {
                        sum += (v as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentWithDefault(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment with default value
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = {}
                      const obj = { b: "B", c: "C" };
                      ({ a = "default", ...rest } = obj)
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("default:BC");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment with mixed types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let name: Object = null
                      let rest = {}
                      const obj = { name: "test", count: 42, active: true };
                      ({ name, ...rest } = obj)
                      let result: string = ""
                      result += name
                      result += ":"
                      for (const [k, v] of rest) {
                        result += v
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("test:42,true,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestAssignmentWithRename(JdkVersion jdkVersion) throws Exception {
        // Object rest assignment with property rename
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let x: Object = null
                      let rest = {}
                      const obj = { a: "A", b: "B", c: "C" };
                      ({ a: x, ...rest } = obj)
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
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("A:BC");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectRestSequentialAssignments(JdkVersion jdkVersion) throws Exception {
        // Multiple sequential object rest assignments
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let a: Object = null
                      let rest = {}
                
                      const obj1 = { a: "X", b: 1, c: 2 };
                      ({ a, ...rest } = obj1)
                
                      let result1: string = ""
                      result1 += a
                      result1 += ":"
                      let count1: int = 0
                      for (const [k, v] of rest) {
                        count1++
                      }
                      result1 += count1
                
                      const obj2 = { a: "Y", d: 3, e: 4, f: 5, g: 6 };
                      ({ a, ...rest } = obj2)
                
                      let result2: string = ""
                      result2 += a
                      result2 += ":"
                      let count2: int = 0
                      for (const [k, v] of rest) {
                        count2++
                      }
                      result2 += count2
                
                      return result1 + "," + result2
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("X:2,Y:4");
    }
}
