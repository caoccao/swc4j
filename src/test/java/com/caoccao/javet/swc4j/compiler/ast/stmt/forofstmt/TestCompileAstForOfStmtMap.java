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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forofstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for for-of loops over maps with destructuring (Phase 4)
 * Tests [key, value] entry iteration with LinkedHashMap
 */
public class TestCompileAstForOfStmtMap extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyMap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const m = {}
                      let count: int = 0
                      for (let [key, value] of m) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapCollectKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { apple: 1, banana: 2, cherry: 3 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        result += key
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("apple,banana,cherry,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapCollectValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const m = { a: 100, b: 200, c: 300 }
                      let sum: int = 0
                      for (let [key, value] of m) {
                        sum += (value as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(600, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapConstDeclaration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { a: 1, b: 2 }
                      let result: string = ""
                      for (const [key, value] of m) {
                        result += key
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("a1b2", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapDestructuringKeyValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { a: 1, b: 2, c: 3 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        result += key + "=" + value + ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("a=1,b=2,c=3,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapMixedValueTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { num: 42, str: "hello", bool: true }
                      let result: string = ""
                      for (let [key, value] of m) {
                        result += value
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("42,hello,true,", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapOnlyKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { x: 10, y: 20 }
                      let result: string = ""
                      for (let [k] of m) {
                        result += k
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("xy", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapPreservesOrder(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { c: 3, a: 1, b: 2 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        result += key
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("cab", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapSingleEntry(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const m = { only: 42 }
                      let result: string = ""
                      for (let [key, value] of m) {
                        result += key + ":" + value
                      }
                      return result
                    }
                  }
                }""");
        assertEquals("only:42", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapValueSum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const m = { a: 10, b: 20, c: 30 }
                      let sum: int = 0
                      for (let [key, value] of m) {
                        sum += (value as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(60, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
