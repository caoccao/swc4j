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

package com.caoccao.javet.swc4j.compiler.ast.stmt.switchstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for break and continue in switch statements (Phase 6)
 * Tests break/continue interaction with switches and nested loops
 */
public class TestCompileAstSwitchStmtBreak extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchMultipleBreaks(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, condition1: boolean, condition2: boolean): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          if (condition1) {
                            break
                          }
                          result = 10
                          if (condition2) {
                            break
                          }
                          result = 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class, boolean.class, boolean.class);

        assertEquals(1, testMethod.invoke(instance, 1, true, false));   // First break
        assertEquals(10, testMethod.invoke(instance, 1, false, true));  // Second break
        assertEquals(100, testMethod.invoke(instance, 1, false, false)); // Third break
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBreakInIfElse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          if (y > 5) {
                            result = 100
                            break
                          } else {
                            result = 50
                          }
                          result += 10
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class, int.class);

        assertEquals(100, testMethod.invoke(instance, 1, 10)); // y>5, break in if
        assertEquals(60, testMethod.invoke(instance, 1, 3));   // y<=5, else then +10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchContinueInLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          for (let i: int = 0; i < 5; i++) {
                            if (i == 2) {
                              continue
                            }
                            result += i
                          }
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        // 0+1+3+4 = 8 (skips 2)
        assertEquals(8, testMethod.invoke(instance, 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBreakInNestedLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          for (let i: int = 0; i < 10; i++) {
                            if (i == 5) {
                              break
                            }
                            result += i
                          }
                          result += 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        // 0+1+2+3+4 = 10, then +100 = 110
        assertEquals(110, testMethod.invoke(instance, 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNoBreakExit(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        case 2:
                          result = 2
                      }
                      result += 10
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(11, testMethod.invoke(instance, 1)); // 1 + 10
        assertEquals(12, testMethod.invoke(instance, 2)); // 2 + 10 (no break but last case)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchReturnInsteadOfBreak(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      switch (x) {
                        case 1:
                          return 100
                        case 2:
                          return 200
                        default:
                          return -1
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(100, testMethod.invoke(instance, 1));
        assertEquals(200, testMethod.invoke(instance, 2));
        assertEquals(-1, testMethod.invoke(instance, 99));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBreakFromWhileInCase(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          let i: int = 0
                          while (i < 10) {
                            if (i == 5) {
                              break
                            }
                            result += i
                            i++
                          }
                          result += 50
                          break
                        case 2:
                          result = 200
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        // 0+1+2+3+4 = 10, then +50 = 60
        assertEquals(60, testMethod.invoke(instance, 1));
        assertEquals(200, testMethod.invoke(instance, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchContinueFromWhileInCase(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          let i: int = 0
                          while (i < 5) {
                            i++
                            if (i == 3) {
                              continue
                            }
                            result += i
                          }
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        // 1+2+4+5 = 12 (skips 3)
        assertEquals(12, testMethod.invoke(instance, 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchMultipleCasesWithReturns(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        case 2:
                          return 2
                        default:
                          result = -1
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));   // Break, return result
        assertEquals(2, testMethod.invoke(instance, 2));   // Return directly
        assertEquals(-1, testMethod.invoke(instance, 99)); // Break, return result
    }
}
