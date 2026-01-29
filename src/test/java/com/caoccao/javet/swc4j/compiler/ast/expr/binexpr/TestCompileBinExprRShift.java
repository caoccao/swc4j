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
import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileBinExprRShift extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 20
                      const b: int = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5); // 20 >> 2 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectRightShiftIntObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 80
                      const b: Integer = 4
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5); // 80 >> 4 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 12
                      const b: byte = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3); // 12 >> 2 = 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteRightShiftInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 56
                      const b: int = 3
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(7); // 56 >> 3 = 7
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 32
                      const b: int = 2
                      const c: int = 1
                      return a >> b >> c
                    }
                  }
                }""");
        // First: 32 >> 2 = 8, then: 8 >> 1 = 4
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 130
                      const b: int = 1
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(65); // 130 >> 1 = 65
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivideByPowerOf2(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1024
                      const b: int = 10
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1); // 1024 >> 10 = 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftBy0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12345
                      const b: int = 0
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(12345);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftBy31(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1024
                      const b: int = 31
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1); // Any negative >> 31 = -1
    }

    // Shift amount masking tests (JVM masks to 5 bits for int, 6 bits for long)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftBy32SameAs0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 32
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // 32 & 0x1F = 0, so shifting by 32 is same as shifting by 0
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftBy33SameAs1(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 33
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // 33 & 0x1F = 1, so shifting by 33 is same as shifting by 1
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftByByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 336
                      const b: byte = 3
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42); // 336 >> 3 = 42
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftByNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1024
                      const b: int = -1
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // -1 & 0x1F = 31, so shifting by -1 is same as shifting by 31
        // -1024 >> 31 = -1 (arithmetic shift preserves sign)
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1);
    }

    // Negative shift amount tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 56
                      const b: int = 3
                      return (a >> b) as int
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(7);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntRightShiftWithExplicitCastToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const a: int = 3200
                      const b: int = 5
                      return (a >> b) as long
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100L);
    }

    // Shift by 0 tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 32
                      const b: Integer = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(8); // 32 >> 2 = 8
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeIntRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 102400000
                      const b: int = 10
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100000); // 102400000 >> 10 = 100000
    }

    // Power of 2 division using right shift

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeLongRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1048576000000000
                      const b: int = 20
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1000000000L); // 1048576000000000 >> 20 = 1000000000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongDivideByPowerOf2(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1099511627776
                      const b: int = 40
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1L); // 1099511627776 >> 40 = 1
    }

    // Negative operand tests (arithmetic shift preserves sign)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 395040
                      const b: Integer = 5
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(12345L); // 395040 >> 5 = 12345
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 8000000
                      const b: int = 3
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1000000L); // 8000000 >> 3 = 1000000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftBy0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 999999
                      const b: int = 0
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(999999L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftBy63(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1024
                      const b: int = 63
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1L); // Any negative >> 63 = -1
    }

    // Explicit cast tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftBy64SameAs0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000
                      const b: int = 64
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // 64 & 0x3F = 0, so shifting by 64 is same as shifting by 0
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1000L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftBy65SameAs1(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1554
                      const b: int = 65
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // 65 & 0x3F = 1, so shifting by 65 is same as shifting by 1
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(777L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftByNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1024
                      const b: int = -1
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // -1 & 0x3F = 63, so shifting by -1 is same as shifting by 63
        // -1024 >> 63 = -1 (arithmetic shift preserves sign)
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1L);
    }

    // Chained shift operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongRightShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: long = 100
                      const b: int = 2
                      return (a >> b) as int
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(25);
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -20
                      const b: int = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-5); //-20 >> 2 = -5 (sign extended)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntRightShiftBy31(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1000
                      const b: int = 31
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // Shifting negative by 31 fills with 1s, resulting in -1
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1);
    }

    // Large value tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -800
                      const b: int = 3
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-100L); //-800 >> 3 = -100 (sign extended)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongRightShiftBy63(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1000
                      const b: int = 63
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // Shifting negative by 63 fills with 1s, resulting in -1
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1L);
    }

    // Maximum shift amount tests (for int: 31, for long: 63)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPositiveIntRightShiftBy31(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 31
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0); // Integer.MAX_VALUE >> 31 = 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPositiveLongRightShiftBy63(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1024
                      const b: int = 63
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0L); // Any positive small value >> 63 = 0
    }

    // Positive value shifted by maximum amount

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 40
                      const b: short = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10); // 40 >> 2 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortRightShiftByLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 60
                      const b: long = 2
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15); // 60 >> 2 = 15
    }

    // Test sign extension with specific values

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSignExtensionInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -1
                      const b: int = 1
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // -1 in binary is all 1s, shifting right still all 1s, so -1
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSignExtensionLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1
                      const b: int = 1
                      const c = a >> b
                      return c
                    }
                  }
                }""");
        // -1 in binary is all 1s, shifting right still all 1s, so -1
        assertThat((long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-1L);
    }
}
