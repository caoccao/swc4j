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

public class TestCompileBinExprLShift extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIntLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: int = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test")); // 5 << 2 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 3
                      const b: byte = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(12, (int) runner.createInstanceRunner("com.A").invoke("test")); // 3 << 2 = 12
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteLeftShiftInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 7
                      const b: int = 3
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(56, (int) runner.createInstanceRunner("com.A").invoke("test")); // 7 << 3 = 56
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectLeftShiftIntObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 5
                      const b: Integer = 4
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(80, (int) runner.createInstanceRunner("com.A").invoke("test")); // 5 << 4 = 80
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = 2
                      const c: int = 3
                      return a << b << c
                    }
                  }
                }""");
        // First: 1 << 2 = 4, then: 4 << 3 = 32
        assertEquals(32, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 'A'
                      const b: int = 1
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(130, (int) runner.createInstanceRunner("com.A").invoke("test")); // 65 << 1 = 130
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftBy0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12345
                      const b: int = 0
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(12345, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftBy31(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = 31
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(Integer.MIN_VALUE, (int) runner.createInstanceRunner("com.A").invoke("test")); // 1 << 31 = -2147483648
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftBy32SameAs0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 32
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // 32 & 0x1F = 0, so shifting by 32 is same as shifting by 0
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Shift amount masking tests (JVM masks to 5 bits for int, 6 bits for long)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftBy33SameAs1(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 50
                      const b: int = 33
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // 33 & 0x1F = 1, so shifting by 33 is same as shifting by 1
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftByByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: byte = 3
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(336, (int) runner.createInstanceRunner("com.A").invoke("test")); // 42 << 3 = 336
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftByNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = -1
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // -1 & 0x1F = 31, so shifting by -1 is same as shifting by 31
        // 1 << 31 = -2147483648 (sets the sign bit)
        assertEquals(-2147483648, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1000000000
                      const b: int = 10
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // Overflow wraps around in JVM
        assertEquals(1797783552, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Negative shift amount tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 7
                      const b: int = 3
                      return (a << b) as int
                    }
                  }
                }""");
        assertEquals(56, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLeftShiftWithExplicitCastToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const a: int = 100
                      const b: int = 5
                      return (a << b) as long
                    }
                  }
                }""");
        assertEquals(3200L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Shift by 0 tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 8
                      const b: Integer = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(32, (int) runner.createInstanceRunner("com.A").invoke("test")); // 8 << 2 = 32
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeIntLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100000
                      const b: int = 10
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(102400000, (int) runner.createInstanceRunner("com.A").invoke("test")); // 100000 << 10 = 102400000
    }

    // Edge case: shifting 1 to create powers of 2

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeLongLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000000000
                      const b: int = 20
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(1048576000000000L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 1000000000 << 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000000
                      const b: int = 3
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(8000000L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 1000000 << 3 = 8000000
    }

    // Negative operand tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftBy0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 999999
                      const b: int = 0
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(999999L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftBy63(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1
                      const b: int = 63
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(Long.MIN_VALUE, (long) runner.createInstanceRunner("com.A").invoke("test")); // 1 << 63 = -9223372036854775808
    }

    // Explicit cast tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftBy64SameAs0(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000
                      const b: int = 64
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // 64 & 0x3F = 0, so shifting by 64 is same as shifting by 0
        assertEquals(1000L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftBy65SameAs1(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 777
                      const b: int = 65
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // 65 & 0x3F = 1, so shifting by 65 is same as shifting by 1
        assertEquals(1554L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftByNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1
                      const b: int = -1
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // -1 & 0x3F = 63, so shifting by -1 is same as shifting by 63
        assertEquals(Long.MIN_VALUE, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Chained shift operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000000000000
                      const b: int = 40
                      const c = a << b
                      return c
                    }
                  }
                }""");
        // Overflow wraps around in JVM
        assertEquals(-6552737457824071680L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongLeftShiftWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: long = 25
                      const b: int = 2
                      return (a << b) as int
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 12345
                      const b: Integer = 5
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(395040L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 12345 << 5 = 395040
    }

    // Large value tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongPowerOfTwo(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1
                      const b: int = 40
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(1099511627776L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 1 << 40 = 1099511627776
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -5
                      const b: int = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(-20, (int) runner.createInstanceRunner("com.A").invoke("test")); // -5 << 2 = -20
    }

    // Overflow tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -100
                      const b: int = 3
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(-800L, (long) runner.createInstanceRunner("com.A").invoke("test")); // -100 << 3 = -800
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPowerOfTwo(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = 10
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(1024, (int) runner.createInstanceRunner("com.A").invoke("test")); // 1 << 10 = 1024
    }

    // Maximum shift amount tests (for int: 31, for long: 63)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 10
                      const b: short = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(40, (int) runner.createInstanceRunner("com.A").invoke("test")); // 10 << 2 = 40
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortLeftShiftByLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 15
                      const b: long = 2
                      const c = a << b
                      return c
                    }
                  }
                }""");
        assertEquals(60, (int) runner.createInstanceRunner("com.A").invoke("test")); // 15 << 2 = 60
    }
}
