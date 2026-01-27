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

public class TestCompileBinExprGtEq extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 49
                      const b: int = 50
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 49 >= 50 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 50
                      const b: int = 50
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 50 >= 50 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteShortGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 99
                      const b: short = 100
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 99 >= 100 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.13
                      const b: double = 3.14
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 3.13 >= 3.14 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.14
                      const b: double = 3.14
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 3.14 >= 3.14 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDoubleGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: double = 1.5
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 1.5 >= 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.4
                      const b: float = 2.5
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 2.4 >= 2.5 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: float = 1.5
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 1.5 >= 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 43
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 42 >= 43 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 42
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 42 >= 42 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 99
                      const b: long = 100
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 99 >= 100 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: long = 100
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 100 >= 100 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeDoubleGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.7976931348623157E308
                      const b: double = 1.7976931348623157E308
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // Equal values, so >= is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567889
                      const b: long = 1234567890
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 1234567889 >= 1234567890 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567890
                      const b: long = 1234567890
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 1234567890 >= 1234567890 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMaxIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 2147483647
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // MAX_VALUE >= MAX_VALUE is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMinIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -2147483647
                      const b: int = -2147483647
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // -2147483647 >= -2147483647 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeDoubleGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = -3.14159
                      const b: double = -3.14159
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // -3.14159 >= -3.14159 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeFloatGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = -2.5
                      const b: float = -2.5
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // -2.5 >= -2.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -42
                      const b: int = -42
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // -42 >= -42 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1234567890
                      const b: long = -1234567890
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // -1234567890 >= -1234567890 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortGreaterThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 31999
                      const b: short = 32000
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 31999 >= 32000 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32000
                      const b: short = 32000
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 32000 >= 32000 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroDoubleGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 0.0
                      const b: double = 0.0
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 0.0 >= 0.0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroGreaterThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 0 >= 0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = -1
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("test").invoke(instance)); // 0 >= -1 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 1
                      const c = a >= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance)); // 0 >= 1 is false
    }
}
