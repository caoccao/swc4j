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

public class TestCompileBinExprBitAnd extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12
                      const b: int = 10
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(8, (int) runner.createInstanceRunner("com.A").invoke("test")); // 12 & 10 = 8
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndInExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 16
                      const b: int = 8
                      const c: int = 4
                      const d = (a & b) + (b & c)
                      return d
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test")); // (16 & 8) + (8 & 4) = 0 + 0 = 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndLongMask(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 4886718345
                      const mask: long = 4294967295
                      const c = a & mask
                      return c
                    }
                  }
                }""");
        assertEquals(591751049L, (long) runner.createInstanceRunner("com.A").invoke("test")); // Extract low 32 bits (4886718345 & 4294967295 = 591751049)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndSameValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 789
                      const b: int = 789
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(789, (int) runner.createInstanceRunner("com.A").invoke("test")); // 789 & 789 = 789 (idempotent)
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndWithAllOnes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 456
                      const b: int = -1
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(456, (int) runner.createInstanceRunner("com.A").invoke("test")); // 456 & -1 = 456 (all bits set)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndWithMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 1073741823
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(1073741823, (int) runner.createInstanceRunner("com.A").invoke("test")); // MAX_VALUE & smaller = smaller
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitAndWithZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 123
                      const b: int = 0
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test")); // 123 & 0 = 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitMaskHighBits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0xABCD
                      const mask: int = 0xFF00
                      const c = a & mask
                      return c
                    }
                  }
                }""");
        assertEquals(0xAB00, (int) runner.createInstanceRunner("com.A").invoke("test")); // Extract high byte
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitMaskLowByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0x12345678
                      const mask: int = 0xFF
                      const c = a & mask
                      return c
                    }
                  }
                }""");
        assertEquals(0x78, (int) runner.createInstanceRunner("com.A").invoke("test")); // Extract low byte
    }

    // Negative number tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitMaskOddBits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0xFFFF
                      const mask: int = 0xAAAA
                      const c = a & mask
                      return c
                    }
                  }
                }""");
        assertEquals(0xAAAA, (int) runner.createInstanceRunner("com.A").invoke("test")); // Extract odd bits (10101010...)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothNegativeBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -10
                      const b: int = -5
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(-14, (int) runner.createInstanceRunner("com.A").invoke("test")); // -10 & -5 = -14
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 15
                      const b: byte = 7
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(7, (int) runner.createInstanceRunner("com.A").invoke("test")); // 15 & 7 = 7
    }

    // Edge case tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteBitAndInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 25
                      const b: int = 15
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(9, (int) runner.createInstanceRunner("com.A").invoke("test")); // 25 & 15 = 9
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectBitAndIntObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 30
                      const b: Integer = 20
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test")); // 30 & 20 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 255
                      const b: int = 127
                      const c: int = 63
                      const d = a & b & c
                      return d
                    }
                  }
                }""");
        assertEquals(63, (int) runner.createInstanceRunner("com.A").invoke("test")); // 255 & 127 & 63 = 63
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntBitAndLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 200
                      const b: long = 150
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(128L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 200 & 150 = 128 (widened to long)
    }

    // Chained operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 28
                      const b: Integer = 12
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(12, (int) runner.createInstanceRunner("com.A").invoke("test")); // 28 & 12 = 12
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 255
                      const b: long = 127
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(127L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 255 & 127 = 127
    }

    // Bit masking tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 1000
                      const b: Long = 500
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(480L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 1000 & 500 = 480
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -20
                      const b: int = 15
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(12, (int) runner.createInstanceRunner("com.A").invoke("test")); // -20 & 15 = 12
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -800
                      const b: long = 255
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(224L, (long) runner.createInstanceRunner("com.A").invoke("test")); // -800 & 255 = 224
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortBitAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 100
                      const b: short = 50
                      const c = a & b
                      return c
                    }
                  }
                }""");
        assertEquals(32, (int) runner.createInstanceRunner("com.A").invoke("test")); // 100 & 50 = 32
    }
}
