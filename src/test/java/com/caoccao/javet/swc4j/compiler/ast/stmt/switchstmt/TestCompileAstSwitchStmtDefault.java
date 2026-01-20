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
 * Test suite for default clause in switch statements (Phase 2)
 * Tests default clause in various positions and behaviors
 */
public class TestCompileAstSwitchStmtDefault extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultAtBeginning(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        default:
                          result = -1
                          break
                        case 1:
                          result = 1
                          break
                        case 2:
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(2, testMethod.invoke(instance, 2));
        assertEquals(-1, testMethod.invoke(instance, 99)); // Default case
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultAtEnd(JdkVersion jdkVersion) throws Exception {
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
                          break
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

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(2, testMethod.invoke(instance, 2));
        assertEquals(-1, testMethod.invoke(instance, 99)); // Default case
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultInMiddle(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        default:
                          result = -1
                          break
                        case 2:
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(2, testMethod.invoke(instance, 2));
        assertEquals(-1, testMethod.invoke(instance, 99)); // Default case
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultNoBreak(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        default:
                          result = -1
                        case 2:
                          result += 10
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(10, testMethod.invoke(instance, 2));
        assertEquals(9, testMethod.invoke(instance, 99)); // Default sets -1, falls to case 2 which adds 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultOnly(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        default:
                          result = 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(100, testMethod.invoke(instance, 1));
        assertEquals(100, testMethod.invoke(instance, 99));
        assertEquals(100, testMethod.invoke(instance, -5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        default:
                          const tmp: int = x * 2
                          result = tmp + 10
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(30, testMethod.invoke(instance, 10)); // 10 * 2 + 10 = 30
        assertEquals(18, testMethod.invoke(instance, 4));  // 4 * 2 + 10 = 18
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultWithReturn(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      switch (x) {
                        case 1:
                          return 1
                        default:
                          return -1
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(-1, testMethod.invoke(instance, 99));
        assertEquals(-1, testMethod.invoke(instance, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEmptyDefault(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = -1
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        default:
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(-1, testMethod.invoke(instance, 99)); // Empty default does nothing
    }
}
