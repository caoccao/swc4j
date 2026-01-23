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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for basic for-in loops (Phase 1)
 * Tests object iteration with LinkedHashMap
 */
public class TestCompileAstForInStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicArrayIteration(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [10, 20, 30]
                      let result: string = ""
                      for (let index in arr) {
                        result += index
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("012", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicObjectIteration(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3 }
                      let result: string = ""
                      for (let key in obj) {
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("abc", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicStringIteration(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      let result: string = ""
                      for (let index in 'abc') {
                        result += index
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("012", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3, d: 4 }
                      let result: string = ""
                      for (let key in obj) {
                        if (key == "c") {
                          break
                        }
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ab", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInLoop(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3, d: 4 }
                      let result: string = ""
                      for (let key in obj) {
                        if (key == "b" || key == "c") {
                          continue
                        }
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ad", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = []
                      let count: int = 0
                      for (let i in arr) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = {}
                      let count: int = 0
                      for (let key in obj) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreak(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj1 = { a: 1, b: 2, c: 3 }
                      const obj2 = { x: 10, y: 20, z: 30 }
                      let result: string = ""
                      outer: for (let k1 in obj1) {
                        for (let k2 in obj2) {
                          result += k1 + k2 + ","
                          if (k2 == "y") {
                            break outer
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ax,ay,", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedForInLoops(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj1 = { a: 1, b: 2 }
                      const obj2 = { x: 10, y: 20 }
                      let result: string = ""
                      for (let k1 in obj1) {
                        for (let k2 in obj2) {
                          result += k1 + k2 + ","
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("ax,ay,bx,by,", classA.getMethod("test").invoke(instance));
    }
}
