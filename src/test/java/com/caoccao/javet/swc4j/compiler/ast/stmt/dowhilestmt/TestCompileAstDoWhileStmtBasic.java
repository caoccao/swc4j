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

package com.caoccao.javet.swc4j.compiler.ast.stmt.dowhilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for basic do-while loops (Phase 1)
 * Tests simple do-while loops with condition and body
 * Key difference from while: body always executes at least once
 */
public class TestCompileAstDoWhileStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicCountingLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i < 10)
                      return i
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileComplexBody(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let product: int = 1
                      let i: int = 1
                      do {
                        sum += i
                        product = product * i
                        i++
                      } while (i <= 5)
                      return sum * 100 + product
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // sum = 1+2+3+4+5 = 15, product = 1*2*3*4*5 = 120
        assertEquals(1620, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileExecutesOnceEvenWhenFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 10
                      do {
                        sum += i
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Unlike while, this executes once: sum = 10
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileGreaterThan(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 10
                      do {
                        sum += i
                        i--
                      } while (i > 0)
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(55, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      let i: int = 0
                      do {
                        count++
                        i++
                      } while (i <= 5)
                      return count
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(6, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileNotEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i != 10)
                      return i
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileSingleIteration(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i < 1)
                      return i
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithReturn(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        if (i == 5) {
                          return i
                        }
                        i++
                      } while (i < 10)
                      return 99
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithSum(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        sum += i
                        i++
                      } while (i < 10)
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(45, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithVarDecl(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        const x: int = i * 2
                        sum += x
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // sum = 0 + 2 + 4 + 6 + 8 = 20
        assertEquals(20, classA.getMethod("test").invoke(instance));
    }
}
