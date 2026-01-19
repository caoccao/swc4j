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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileBinExprBitXor extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12
                      const b: int = 10
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(6, classA.getMethod("test").invoke(instance)); // 12 ^ 10 = 6
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorDoubleApplication(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const mask: int = 0xFF
                      const c = (a ^ mask) ^ mask
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance)); // (a ^ b) ^ b = a (reversible)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorInExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 16
                      const b: int = 8
                      const c: int = 4
                      const d = (a ^ b) + (b ^ c)
                      return d
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(36, classA.getMethod("test").invoke(instance)); // (16 ^ 8) + (8 ^ 4) = 24 + 12 = 36
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorLongPattern(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 0xAAAAAAAA
                      const b: long = 0x55555555
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0xFFFFFFFFL, classA.getMethod("test").invoke(instance)); // Alternating bits XOR = all ones
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorSameValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 789
                      const b: int = 789
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance)); // 789 ^ 789 = 0 (cancel out)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorSwapPattern(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 17
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(59, classA.getMethod("test").invoke(instance)); // 42 ^ 17 = 59
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorToggleBits(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0xF0F0
                      const mask: int = 0xFFFF
                      const c = a ^ mask
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0x0F0F, classA.getMethod("test").invoke(instance)); // Toggle all bits
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorWithAllOnes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 456
                      const b: int = -1
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-457, classA.getMethod("test").invoke(instance)); // 456 ^ -1 = -457 (bitwise NOT)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitXorWithZero(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 123
                      const b: int = 0
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(123, classA.getMethod("test").invoke(instance)); // 123 ^ 0 = 123
    }

    // Negative number tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothNegativeBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -10
                      const b: int = -5
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(13, classA.getMethod("test").invoke(instance)); // -10 ^ -5 = 13
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 15
                      const b: byte = 7
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(8, classA.getMethod("test").invoke(instance)); // 15 ^ 7 = 8
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitXorInt(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 25
                      const b: int = 15
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(22, classA.getMethod("test").invoke(instance)); // 25 ^ 15 = 22
    }

    // Edge case tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectBitXorIntObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 30
                      const b: Integer = 20
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance)); // 30 ^ 20 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 255
                      const b: int = 127
                      const c: int = 63
                      const d = a ^ b ^ c
                      return d
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(191, classA.getMethod("test").invoke(instance)); // 255 ^ 127 ^ 63 = 191
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntBitXorLong(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 200
                      const b: long = 150
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(94L, classA.getMethod("test").invoke(instance)); // 200 ^ 150 = 94 (widened to long)
    }

    // Chained operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 28
                      const b: Integer = 12
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(16, classA.getMethod("test").invoke(instance)); // 28 ^ 12 = 16
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 255
                      const b: long = 127
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(128L, classA.getMethod("test").invoke(instance)); // 255 ^ 127 = 128
    }

    // XOR properties tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 1000
                      const b: Long = 500
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(540L, classA.getMethod("test").invoke(instance)); // 1000 ^ 500 = 540
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -20
                      const b: int = 15
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-29, classA.getMethod("test").invoke(instance)); // -20 ^ 15 = -29
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -800
                      const b: long = 255
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-993L, classA.getMethod("test").invoke(instance)); // -800 ^ 255 = -993
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortBitXor(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 100
                      const b: short = 50
                      const c = a ^ b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(86, classA.getMethod("test").invoke(instance)); // 100 ^ 50 = 86
    }
}
