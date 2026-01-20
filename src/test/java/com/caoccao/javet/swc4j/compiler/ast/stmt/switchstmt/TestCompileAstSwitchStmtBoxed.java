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
 * Test suite for boxed type switch statements
 * Tests switch on Integer, Byte, Short, Character with unboxing
 */
public class TestCompileAstSwitchStmtBoxed extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteBasic(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Byte): int {
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Byte.class);

        assertEquals(10, testMethod.invoke(instance, (byte) 1));
        assertEquals(20, testMethod.invoke(instance, (byte) 2));
        assertEquals(30, testMethod.invoke(instance, (byte) 3));
        assertEquals(0, testMethod.invoke(instance, (byte) 4)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchByteNegativeValues(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Byte): int {
                      let result: int = 0
                      switch (x) {
                        case -1:
                          result = 1
                          break
                        case 0:
                          result = 2
                          break
                        case 1:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Byte.class);

        assertEquals(1, testMethod.invoke(instance, (byte) -1));
        assertEquals(2, testMethod.invoke(instance, (byte) 0));
        assertEquals(3, testMethod.invoke(instance, (byte) 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCharacterBasic(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Character): int {
                      let result: int = 0
                      switch (x) {
                        case 'a':
                          result = 1
                          break
                        case 'b':
                          result = 2
                          break
                        case 'c':
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Character.class);

        assertEquals(1, testMethod.invoke(instance, 'a'));
        assertEquals(2, testMethod.invoke(instance, 'b'));
        assertEquals(3, testMethod.invoke(instance, 'c'));
        assertEquals(0, testMethod.invoke(instance, 'd')); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCharacterDigits(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Character): int {
                      let result: int = 0
                      switch (x) {
                        case '0':
                          result = 0
                          break
                        case '1':
                          result = 1
                          break
                        case '2':
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
        var testMethod = classA.getMethod("test", Character.class);

        assertEquals(0, testMethod.invoke(instance, '0'));
        assertEquals(1, testMethod.invoke(instance, '1'));
        assertEquals(2, testMethod.invoke(instance, '2'));
        assertEquals(-1, testMethod.invoke(instance, '3')); // Default
        assertEquals(-1, testMethod.invoke(instance, 'a')); // Default
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchIntegerBasic(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Integer): int {
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Integer.class);

        assertEquals(10, testMethod.invoke(instance, 1));
        assertEquals(20, testMethod.invoke(instance, 2));
        assertEquals(30, testMethod.invoke(instance, 3));
        assertEquals(0, testMethod.invoke(instance, 4)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchIntegerFallThrough(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Integer): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result += 1
                        case 2:
                          result += 10
                          break
                        case 3:
                          result += 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Integer.class);

        assertEquals(11, testMethod.invoke(instance, 1)); // 1 + 10
        assertEquals(10, testMethod.invoke(instance, 2)); // 10
        assertEquals(100, testMethod.invoke(instance, 3)); // 100
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchIntegerWithDefault(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Integer): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 10
                          break
                        case 2:
                          result = 20
                          break
                        default:
                          result = 99
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Integer.class);

        assertEquals(10, testMethod.invoke(instance, 1));
        assertEquals(20, testMethod.invoke(instance, 2));
        assertEquals(99, testMethod.invoke(instance, 3)); // Default
        assertEquals(99, testMethod.invoke(instance, 100)); // Default
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchShortBasic(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Short): int {
                      let result: int = 0
                      switch (x) {
                        case 100:
                          result = 1
                          break
                        case 200:
                          result = 2
                          break
                        case 300:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Short.class);

        assertEquals(1, testMethod.invoke(instance, (short) 100));
        assertEquals(2, testMethod.invoke(instance, (short) 200));
        assertEquals(3, testMethod.invoke(instance, (short) 300));
        assertEquals(0, testMethod.invoke(instance, (short) 400)); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchShortSparse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: Short): int {
                      switch (x) {
                        case 1:
                          return 1
                        case 100:
                          return 2
                        case 1000:
                          return 3
                        case 10000:
                          return 4
                        default:
                          return 0
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var testMethod = classA.getMethod("test", Short.class);

        assertEquals(1, testMethod.invoke(instance, (short) 1));
        assertEquals(2, testMethod.invoke(instance, (short) 100));
        assertEquals(3, testMethod.invoke(instance, (short) 1000));
        assertEquals(4, testMethod.invoke(instance, (short) 10000));
        assertEquals(0, testMethod.invoke(instance, (short) 50));
    }
}
