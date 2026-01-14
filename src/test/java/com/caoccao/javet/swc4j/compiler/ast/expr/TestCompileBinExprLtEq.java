/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.expr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompileBinExprLtEq extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 51
                      const b: int = 50
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 51 <= 50 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 50
                      const b: int = 50
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 50 <= 50 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteShortLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 101
                      const b: short = 100
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 101 <= 100 is false
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.15
                      const b: double = 3.14
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 3.15 <= 3.14 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.14
                      const b: double = 3.14
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 3.14 <= 3.14 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDoubleLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: double = 1.5
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 1.5 <= 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.5
                      const b: float = 2.4
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 2.5 <= 2.4 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: float = 1.5
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 1.5 <= 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 43
                      const b: int = 42
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 43 <= 42 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 42
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 42 <= 42 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 101
                      const b: long = 100
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 101 <= 100 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: long = 100
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 100 <= 100 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeDoubleLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.7976931348623157E308
                      const b: double = 1.7976931348623157E308
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // Equal values, so <= is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567891
                      const b: long = 1234567890
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 1234567891 <= 1234567890 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567890
                      const b: long = 1234567890
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 1234567890 <= 1234567890 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMaxIntValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 2147483647
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // MAX_VALUE <= MAX_VALUE is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMinIntValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -2147483647
                      const b: int = -2147483647
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -2147483647 <= -2147483647 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeDoubleLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = -3.14159
                      const b: double = -3.14159
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -3.14159 <= -3.14159 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeFloatLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = -2.5
                      const b: float = -2.5
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -2.5 <= -2.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -42
                      const b: int = -42
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -42 <= -42 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1234567890
                      const b: long = -1234567890
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -1234567890 <= -1234567890 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortLessThanOrEqualFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32001
                      const b: short = 32000
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertFalse((boolean) classA.getMethod("test").invoke(instance)); // 32001 <= 32000 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32000
                      const b: short = 32000
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 32000 <= 32000 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroDoubleLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 0.0
                      const b: double = 0.0
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 0.0 <= 0.0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroLessThanOrEqualTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 0 <= 0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsNegative(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1
                      const b: int = 0
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // -1 <= 0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsPositive(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 1
                      const c = a <= b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertTrue((boolean) classA.getMethod("test").invoke(instance)); // 0 <= 1 is true
    }
}
