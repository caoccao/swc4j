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

public class TestCompileBinExprLogicalOr extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = false
                      const c = a || b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // false || false = false
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
                      const c = a || b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // true || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonOrComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = (x > y) || (x > 0)
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // (5 > 10) || (5 > 0) = false || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonOrComparisonFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = (x > y) || (x < 0)
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // (5 > 10) || (5 < 0) = false || false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEqualityOrInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const c = (x == 10) || (x != 0)
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // (5 == 10) || (5 != 0) = false || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFalseOrTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = true
                      const c = a || b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // false || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralFalseOrFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = false || false
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // false || false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralTrueOrFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = true || false
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // true || false = true (short-circuit)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLiteralTrueOrTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = true || true
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // true || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = false
                      const c: boolean = true
                      const d = a || b || c
                      return d
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // false || false || true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleOrAllFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = false
                      const c: boolean = false
                      const d = a || b || c
                      return d
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // false || false || false = false
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
                      const c = ((x > y) || (y > z)) || (x < z)
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // Last comparison is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrueOrFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c = a || b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // true || false = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTrueShortCircuit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c = a || b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // true || false = true (short-circuit, b not evaluated)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroOrNonZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 0
                      const y: int = 5
                      const c = (x != 0) || (y != 0)
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // (0 != 0) || (5 != 0) = false || true = true
    }
}
