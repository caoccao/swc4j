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

public class TestCompileBinExprBitOr extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12
                      const b: int = 10
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(14, classA.getMethod("test").invoke(instance)); // 12 | 10 = 14
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrFlagCombination(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag1: int = 1
                      const flag2: int = 2
                      const flag3: int = 4
                      const flags = flag1 | flag2 | flag3
                      return flags
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(7, classA.getMethod("test").invoke(instance)); // Combine flags: 1 | 2 | 4 = 7
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrInExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 16
                      const b: int = 8
                      const c: int = 4
                      const d = (a | b) + (b | c)
                      return d
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(36, classA.getMethod("test").invoke(instance)); // (16 | 8) + (8 | 4) = 24 + 12 = 36
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrLongCombination(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 0xFF000000
                      const b: long = 0x00FF0000
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0xFFFF0000L, classA.getMethod("test").invoke(instance)); // Combine bits
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrSameValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 789
                      const b: int = 789
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(789, classA.getMethod("test").invoke(instance)); // 789 | 789 = 789 (idempotent)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrSetLowBits(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0xFF00
                      const b: int = 0x00FF
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0xFFFF, classA.getMethod("test").invoke(instance)); // Combine high and low bytes
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrWithAllOnes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 456
                      const b: int = -1
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-1, classA.getMethod("test").invoke(instance)); // 456 | -1 = -1 (all bits set)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitOrWithZero(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 123
                      const b: int = 0
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(123, classA.getMethod("test").invoke(instance)); // 123 | 0 = 123
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothNegativeBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -10
                      const b: int = -5
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-1, classA.getMethod("test").invoke(instance)); // -10 | -5 = -1
    }

    // Negative number tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 8
                      const b: byte = 7
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance)); // 8 | 7 = 15
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitOrInt(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 16
                      const b: int = 15
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(31, classA.getMethod("test").invoke(instance)); // 16 | 15 = 31
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectBitOrIntObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 10
                      const b: Integer = 20
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(30, classA.getMethod("test").invoke(instance)); // 10 | 20 = 30
    }

    // Edge case tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = 2
                      const c: int = 4
                      const d = a | b | c
                      return d
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(7, classA.getMethod("test").invoke(instance)); // 1 | 2 | 4 = 7
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntBitOrLong(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 200
                      const b: long = 150
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(222L, classA.getMethod("test").invoke(instance)); // 200 | 150 = 222 (widened to long)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 16
                      const b: Integer = 12
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(28, classA.getMethod("test").invoke(instance)); // 16 | 12 = 28
    }

    // Chained operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 255
                      const b: long = 127
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(255L, classA.getMethod("test").invoke(instance)); // 255 | 127 = 255
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 1000
                      const b: Long = 500
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1020L, classA.getMethod("test").invoke(instance)); // 1000 | 500 = 1020
    }

    // Bit setting tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -20
                      const b: int = 15
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-17, classA.getMethod("test").invoke(instance)); // -20 | 15 = -17
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -800
                      const b: long = 255
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-769L, classA.getMethod("test").invoke(instance)); // -800 | 255 = -769
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortBitOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 100
                      const b: short = 50
                      const c = a | b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(118, classA.getMethod("test").invoke(instance)); // 100 | 50 = 118
    }
}
