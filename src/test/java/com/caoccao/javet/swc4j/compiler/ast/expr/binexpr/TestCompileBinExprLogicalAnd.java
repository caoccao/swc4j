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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileBinExprLogicalAnd extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = false
                      const c = a && b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // false && false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = true
                      const c = a && b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonAndComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = (x < y) && (x > 0)
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // (5 < 10) && (5 > 0) = true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonAndComparisonFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = (x > y) && (x > 0)
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // (5 > 10) && (5 > 0) = false && true = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEqualityAndInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const c = (x == 5) && (x != 10)
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // (5 == 5) && (5 != 10) = true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFalseAndTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = true
                      const c = a && b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // false && true = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralFalseShortCircuit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = false && true
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // false && true = false (short-circuit)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralTrueAndFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = true && false
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // true && false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralTrueAndTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = true && true
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = true
                      const c: boolean = true
                      const d = a && b && c
                      return d
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // true && true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleAndWithFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      const d = a && b && c
                      return d
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // true && false && true = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedComparisons(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const z: int = 15
                      const c = ((x < y) && (y < z)) && (x < z)
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // All comparisons true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrueAndFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c = a && b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // true && false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWithNegation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c = a && !b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // true && !false = true && true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroAndNonZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 0
                      const y: int = 5
                      const c = (x == 0) && (y != 0)
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // (0 == 0) && (5 != 0) = true && true = true
    }
}
