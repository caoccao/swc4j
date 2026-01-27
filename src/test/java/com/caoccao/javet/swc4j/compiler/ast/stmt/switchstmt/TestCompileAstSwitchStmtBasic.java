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
 * Test suite for basic integer switch statements (Phase 1)
 * Tests simple switch with constant integer cases and break statements
 */
public class TestCompileAstSwitchStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBasicThreeCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 10
                          break
                        case 2:
                          result = 20
                          break
                        case 3:
                          result = 30
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(10, testMethod.invoke(instance, 1));
        assertEquals(20, testMethod.invoke(instance, 2));
        assertEquals(30, testMethod.invoke(instance, 3));
        assertEquals(0, testMethod.invoke(instance, 4)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDenseCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 0:
                          result = 0
                          break
                        case 1:
                          result = 1
                          break
                        case 2:
                          result = 2
                          break
                        case 3:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(0, testMethod.invoke(instance, 0));
        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(2, testMethod.invoke(instance, 2));
        assertEquals(3, testMethod.invoke(instance, 3));
        assertEquals(0, testMethod.invoke(instance, 4)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFirstCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (1) {
                        case 1:
                          result = 100
                          break
                        case 2:
                          result = 200
                          break
                        case 3:
                          result = 300
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test");

        assertEquals(100, testMethod.invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLastCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (3) {
                        case 1:
                          result = 100
                          break
                        case 2:
                          result = 200
                          break
                        case 3:
                          result = 300
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test");

        assertEquals(300, testMethod.invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNegativeCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case -5:
                          result = 5
                          break
                        case -3:
                          result = 3
                          break
                        case 0:
                          result = 0
                          break
                        case 3:
                          result = -3
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(5, testMethod.invoke(instance, -5));
        assertEquals(3, testMethod.invoke(instance, -3));
        assertEquals(0, testMethod.invoke(instance, 0));
        assertEquals(-3, testMethod.invoke(instance, 3));
        assertEquals(0, testMethod.invoke(instance, 99)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = -1
                      switch (99) {
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test");

        assertEquals(-1, testMethod.invoke(instance)); // No match, result unchanged
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchSingleCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = -1
                      switch (x) {
                        case 5:
                          result = 50
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(50, testMethod.invoke(instance, 5));
        assertEquals(-1, testMethod.invoke(instance, 1)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchSparseCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        case 10:
                          result = 10
                          break
                        case 100:
                          result = 100
                          break
                        case 1000:
                          result = 1000
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(1, testMethod.invoke(instance, 1));
        assertEquals(10, testMethod.invoke(instance, 10));
        assertEquals(100, testMethod.invoke(instance, 100));
        assertEquals(1000, testMethod.invoke(instance, 1000));
        assertEquals(0, testMethod.invoke(instance, 50)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchWithExpressionDiscriminant(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x * 2) {
                        case 5:
                          result = 5
                          break
                        case 10:
                          result = 10
                          break
                        case 15:
                          result = 15
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(10, testMethod.invoke(instance, 5)); // 5 * 2 = 10
        assertEquals(0, testMethod.invoke(instance, 3)); // 3 * 2 = 6, no match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchWithVariableInCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          const y: int = 10
                          result = y
                          break
                        case 2:
                          const z: int = 20
                          result = z
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", int.class);

        assertEquals(10, testMethod.invoke(instance, 1));
        assertEquals(20, testMethod.invoke(instance, 2));
        assertEquals(0, testMethod.invoke(instance, 3));
    }
}
