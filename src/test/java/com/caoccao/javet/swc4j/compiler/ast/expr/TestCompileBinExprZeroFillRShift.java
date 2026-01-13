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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileBinExprZeroFillRShift extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 20
                      const b: int = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance)); // 20 >>> 2 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectZeroFillRShiftIntObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 80
                      const b: Integer = 4
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance)); // 80 >>> 4 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 12
                      const b: byte = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance)); // 12 >>> 2 = 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 32
                      const b: int = 2
                      const c: int = 1
                      return a >>> b >>> c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // First: 32 >>> 2 = 8, then: 8 >>> 1 = 4
        assertEquals(4, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 130
                      const b: int = 1
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(65, classA.getMethod("test").invoke(instance)); // 130 >>> 1 = 65
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftBy0(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12345
                      const b: int = 0
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(12345, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftBy32SameAs0(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 32
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 32 & 0x1F = 0, so shifting by 32 is same as shifting by 0
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftBy33SameAs1(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 33
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 33 & 0x1F = 1, so shifting by 33 is same as shifting by 1
        assertEquals(50, classA.getMethod("test").invoke(instance));
    }

    // Shift amount masking tests (JVM masks to 5 bits for int, 6 bits for long)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftByByte(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 336
                      const b: byte = 3
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test").invoke(instance)); // 336 >>> 3 = 42
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 56
                      const b: int = 3
                      return (a >>> b) as int
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(7, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZeroFillRShiftWithExplicitCastToLong(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const a: int = 3200
                      const b: int = 5
                      return (a >>> b) as long
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 32
                      const b: Integer = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(8, classA.getMethod("test").invoke(instance)); // 32 >>> 2 = 8
    }

    // Negative operand tests - KEY DIFFERENCE: zero-fill (no sign extension)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeIntZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 102400000
                      const b: int = 10
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100000, classA.getMethod("test").invoke(instance)); // 102400000 >>> 10 = 100000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeLongZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1048576000000000
                      const b: int = 20
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1000000000L, classA.getMethod("test").invoke(instance)); // 1048576000000000 >>> 20 = 1000000000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 395040
                      const b: Integer = 5
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(12345L, classA.getMethod("test").invoke(instance)); // 395040 >>> 5 = 12345
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongZeroFillRShiftBy0(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 999999
                      const b: int = 0
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(999999L, classA.getMethod("test").invoke(instance));
    }

    // Positive operand tests - behaves same as arithmetic shift

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongZeroFillRShiftBy64SameAs0(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000
                      const b: int = 64
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 64 & 0x3F = 0, so shifting by 64 is same as shifting by 0
        assertEquals(1000L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongZeroFillRShiftBy65SameAs1(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1554
                      const b: int = 65
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // 65 & 0x3F = 1, so shifting by 65 is same as shifting by 1
        assertEquals(777L, classA.getMethod("test").invoke(instance));
    }

    // Shift by 0 tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongZeroFillRShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: long = 100
                      const b: int = 2
                      return (a >>> b) as int
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(25, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 8000000
                      const b: int = 3
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1000000L, classA.getMethod("test").invoke(instance)); // 8000000 >>> 3 = 1000000
    }

    // Test -1 >>> n (all bits set)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -20
                      const b: int = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // -20 >>> 2 treats -20 as unsigned, fills with 0s
        assertEquals(1073741819, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntZeroFillRShiftBy31(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1024
                      const b: int = 31
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Negative >>> 31 fills with 0s, resulting in 1
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    // Explicit cast tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -800
                      const b: int = 3
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // -800 >>> 3 treats -800 as unsigned, fills with 0s
        assertEquals(2305843009213693852L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongZeroFillRShiftBy63(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1024
                      const b: int = 63
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Negative >>> 63 fills with 0s, resulting in 1
        assertEquals(1L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeOneIntZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1
                      const b: int = 1
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // -1 is 0xFFFFFFFF, >>> 1 = 0x7FFFFFFF = 2147483647
        assertEquals(2147483647, classA.getMethod("test").invoke(instance));
    }

    // Chained shift operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeOneLongZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1
                      const b: int = 1
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // -1L is 0xFFFFFFFFFFFFFFFF, >>> 1 = 0x7FFFFFFFFFFFFFFF = 9223372036854775807
        assertEquals(9223372036854775807L, classA.getMethod("test").invoke(instance));
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPositiveIntZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 20
                      const b: int = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance)); // 20 >>> 2 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPositiveLongZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 8000000
                      const b: int = 3
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1000000L, classA.getMethod("test").invoke(instance)); // 8000000 >>> 3 = 1000000
    }

    // Large value tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortZeroFillRShift(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 40
                      const b: short = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance)); // 40 >>> 2 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortZeroFillRShiftByLong(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 60
                      const b: long = 2
                      const c = a >>> b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance)); // 60 >>> 2 = 15
    }

    // Comparison with arithmetic shift

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroFillVsArithmeticShiftNegative(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -100
                      const b: int = 2
                      const c = a >>> b  // zero-fill
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // For negative numbers, >>> gives large positive: 1073741799
        assertEquals(1073741799, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroFillVsArithmeticShiftPositive(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 2
                      const c = a >>> b  // zero-fill
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // For positive numbers, >> and >>> give same result: 25
        assertEquals(25, classA.getMethod("test").invoke(instance));
    }
}
