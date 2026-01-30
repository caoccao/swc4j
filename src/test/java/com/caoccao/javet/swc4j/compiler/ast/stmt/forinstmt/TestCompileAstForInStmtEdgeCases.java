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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forinstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for for-in edge cases and advanced scenarios (Phase 8)
 */
public class TestCompileAstForInStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithSpreadOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr1 = [1, 2]
                      const arr2 = [3, 4]
                      const merged = [...arr1, ...arr2]
                      let result: string = ""
                      for (let i in merged) {
                        result += i + ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("0,1,2,3,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakOnFirstIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 1, b: 2, c: 3 }
                      let count: int = 0
                      for (let key in obj) {
                        count++
                        break
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueAllIterations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 1, b: 2, c: 3 }
                      let count: int = 0
                      for (let key in obj) {
                        count++
                        continue
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyLoopBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 1, b: 2, c: 3 }
                      let count: int = 0
                      for (let key in obj) {}
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForInWithVariableDeclaredInBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 10, b: 20, c: 30 }
                      let sum: int = 0
                      for (let key in obj) {
                        let x: int = 5
                        sum += x
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndicesAreStrings(JdkVersion jdkVersion) throws Exception {
        // For-in indices are strings in JavaScript semantics ("0", "1", "2")
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const arr = [10, 20, 30]
                      for (let i in arr) {
                        // i should be string "0", "1", "2" (JavaScript semantics)
                        if (i == "0") {
                          return true
                        }
                      }
                      return false
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInsertionOrderPreserved(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { c: 3, a: 1, b: 2 }
                      let result: string = ""
                      for (let k in obj) {
                        result += k
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("cab");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj1 = { a: 1, b: 2 }
                      const obj2 = { x: 10, y: 20 }
                      let count: int = 0
                      outer: for (let k1 in obj1) {
                        for (let k2 in obj2) {
                          count++
                          if (k2 == "x") {
                            continue outer
                          }
                        }
                      }
                      return count
                    }
                  }
                }""");
        // First outer iteration: k2="x" -> continue outer (count=1)
        // Second outer iteration: k2="x" -> continue outer (count=2)
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                   11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
                      let count: int = 0
                      for (let index in arr) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleContinuesInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3, d: 4, e: 5 }
                      let result: string = ""
                      for (let key in obj) {
                        if (key == "a") continue
                        if (key == "c") continue
                        if (key == "e") continue
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("bd");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = { a: 1, b: 5, c: 10, d: 3 }
                      let count: int = 0
                      for (let key in obj) {
                        if (key == "b" || key == "c") {
                          count++
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
    public void testNestedObjectAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const outer = { a: { x: 1 }, b: { y: 2 } }
                      let result: string = ""
                      for (let k1 in outer) {
                        result += k1 + ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a,b,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectWithStringKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { "key1": 1, "key2": 2 }
                      let result: string = ""
                      for (let k in obj) {
                        result += k + ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("key1,key2,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInFirstIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3 }
                      for (let key in obj) {
                        return key
                      }
                      return "none"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleElementArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [42]
                      let result: string = ""
                      for (let index in arr) {
                        result += index
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("0");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSinglePropertyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { only: 42 }
                      let result: string = ""
                      for (let key in obj) {
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("only");
    }
}
