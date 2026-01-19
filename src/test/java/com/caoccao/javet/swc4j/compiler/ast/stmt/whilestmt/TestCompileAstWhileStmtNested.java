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

package com.caoccao.javet.swc4j.compiler.ast.stmt.whilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for nested while loops (Phase 5)
 * Tests while loops nested within other while loops
 */
public class TestCompileAstWhileStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInInnerLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 3) {
                        let j: int = 0
                        while (j < 5) {
                          if (j == 2) {
                            break
                          }
                          sum++
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Each inner loop runs 2 times (j=0,1), outer runs 3 times => 3*2 = 6
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInInnerLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 3) {
                        let j: int = 0
                        while (j < 5) {
                          j++
                          if (j % 2 == 0) {
                            continue
                          }
                          sum++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Each inner loop: j=1,3,5 (odd values) => 3 iterations
        // Outer runs 3 times => 3*3 = 9
        assertEquals(9, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeeplyNestedWithSum(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 2) {
                        let j: int = 0
                        while (j < 2) {
                          let k: int = 0
                          while (k < 2) {
                            let m: int = 0
                            while (m < 2) {
                              sum++
                              m++
                            }
                            k++
                          }
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 2 * 2 * 2 * 2 = 16 iterations
        assertEquals(16, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testForInWhile(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 3) {
                        for (let j: int = 0; j < 2; j++) {
                          sum++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // While loop runs 3 times, for loop runs 2 times each => 3*2 = 6
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInnerModifiesOuterVariable(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        let j: int = 0
                        while (j < 3) {
                          i++
                          j++
                        }
                      }
                      return i
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i starts at 0, inner loop increments i by 3 each outer iteration
        // i=0: inner runs 3 times => i=3
        // i=3: inner runs 3 times => i=6
        // ... continues until i>=100
        // i increments by 3 each outer iteration: 0,3,6,...,99,102
        // At i=99, inner adds 3 => i=102 >= 100, stops
        assertEquals(102, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSharedVariables(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 3) {
                        let j: int = 0
                        while (j < i) {
                          sum++
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i=0: j<0 => 0 iterations
        // i=1: j<1 => 1 iteration
        // i=2: j<2 => 2 iterations
        // Total: 0 + 1 + 2 = 3
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedWhile(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 2) {
                        let j: int = 0
                        while (j < 2) {
                          let k: int = 0
                          while (k < 2) {
                            sum++
                            k++
                          }
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 2 * 2 * 2 = 8 iterations
        assertEquals(8, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNestedWhile(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 3) {
                        let j: int = 0
                        while (j < 3) {
                          sum++
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 3 * 3 = 9 iterations
        assertEquals(9, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileInFor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 3; i++) {
                        let j: int = 0
                        while (j < 2) {
                          sum++
                          j++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // For loop runs 3 times, while loop runs 2 times each => 3*2 = 6
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }
}
