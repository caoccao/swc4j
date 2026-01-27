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
 * Test suite for while loop edge cases (Phase 7)
 * Tests advanced scenarios and edge cases
 */
public class TestCompileAstWhileStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 0) {
                      }
                      return 42
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatingPointCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      let d: double = 0.0
                      while (d < 1.0) {
                        count++
                        d = d + 0.1
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Due to floating point precision, this iterates 11 times (0.1*10 > 1.0 due to rounding)
        assertEquals(11, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleReturnsInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i % 3 == 0) {
                          return 30
                        }
                        if (i % 5 == 0) {
                          return 50
                        }
                      }
                      return 999
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // i=1: neither, i=2: neither, i=3: 3%3==0, return 30
        assertEquals(30, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSequentialLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sumA: int = 0
                      let sumB: int = 0
                      let i: int = 0
                      while (i < 5) {
                        sumA += i
                        i++
                      }
                      let j: int = 0
                      while (j < 5) {
                        sumB += j
                        j++
                      }
                      return sumA * 10 + sumB
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // sumA = 0+1+2+3+4 = 10, sumB = 0+1+2+3+4 = 10
        assertEquals(110, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let even: int = 0
                      let odd: int = 0
                      let i: int = 0
                      while (i < 10) {
                        if (i % 2 == 0) {
                          even++
                        } else {
                          odd++
                        }
                        i++
                      }
                      return even * 10 + odd
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // even = 5 (0,2,4,6,8), odd = 5 (1,3,5,7,9)
        assertEquals(55, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i == 7) {
                          return i * 10
                        }
                      }
                      return 999
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(70, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVeryLargeCount(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100000) {
                        i++
                      }
                      return i
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(100000, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileInIfStatement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): int {
                      let sum: int = 0
                      if (flag) {
                        let i: int = 0
                        while (i < 10) {
                          sum += i
                          i++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(45, classA.getMethod("test", boolean.class).invoke(instance, true));
        assertEquals(0, classA.getMethod("test", boolean.class).invoke(instance, false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTruePattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      while (true) {
                        count++
                        if (count >= 15) {
                          break
                        }
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance));
    }
}
