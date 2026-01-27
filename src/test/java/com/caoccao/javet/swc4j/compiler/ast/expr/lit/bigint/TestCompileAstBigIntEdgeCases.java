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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.bigint;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for BigInt edge cases and boundary conditions.
 * Phase 7: Edge Cases (15 tests)
 */
public class TestCompileAstBigIntEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntByteOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return 300n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // 300 wraps to 44 in byte range
        assertEquals((byte) 300, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntComplexNegation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -(-123n)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Double negation: -(-123n) = 123n
        assertEquals(new BigInteger("123"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMultipleBigInts(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 111n
                      const b: BigInteger = 222n
                      const c: BigInteger = 333n
                      const d: BigInteger = 444n
                      const e: BigInteger = 555n
                      return e
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(new BigInteger("555"), classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntNegativeZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -0n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // -0n should equal 0n (BigInteger.ZERO)
        assertEquals(BigInteger.ZERO, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntOverflowInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 2147483648n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Integer.MAX_VALUE + 1 = 2147483648, wraps to Integer.MIN_VALUE
        assertEquals(Integer.MIN_VALUE, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntOverflowLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 9223372036854775808n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Long.MAX_VALUE + 1, wraps to Long.MIN_VALUE
        assertEquals(Long.MIN_VALUE, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPrecisionLossDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 12345678901234567890123456789n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Very large BigInt loses precision when converted to double
        double result = (double) classA.getMethod("test").invoke(instance);
        // Check that it's approximately correct (precision loss expected)
        assertEquals(1.2345678901234567e28, result, 1e20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPrecisionLossFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 123456789012345n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Large BigInt loses precision when converted to float
        float result = (float) classA.getMethod("test").invoke(instance);
        // Check that it's approximately correct (precision loss expected)
        assertEquals(1.23456789e14f, result, 1e8f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntShortOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      return 40000n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // 40000 wraps to -25536 in short range
        assertEquals((short) 40000, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntSignPreservation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testPositive(): BigInteger {
                      return +123n
                    }
                    testNegative(): BigInteger {
                      return -123n
                    }
                    testNoSign(): BigInteger {
                      return 123n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(new BigInteger("123"), classA.getMethod("testPositive").invoke(instance));
        assertEquals(new BigInteger("-123"), classA.getMethod("testNegative").invoke(instance));
        assertEquals(new BigInteger("123"), classA.getMethod("testNoSign").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntUnderflowInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return -2147483649n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Integer.MIN_VALUE - 1 = -2147483649, wraps to Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntUnderflowLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return -9223372036854775809n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // Long.MIN_VALUE - 1, wraps to Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntVeryLargeNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -12345678901234567890123456789012345678901234567890n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(new BigInteger("-12345678901234567890123456789012345678901234567890"),
                classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntVeryLargePositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 98765432109876543210987654321098765432109876543210n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(new BigInteger("98765432109876543210987654321098765432109876543210"),
                classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntZeroConversionBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testZero(): boolean {
                      return 0n
                    }
                    testNonZero(): boolean {
                      return 1n
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        // 0n → false
        assertEquals(false, classA.getMethod("testZero").invoke(instance));
        // 1n → true
        assertEquals(true, classA.getMethod("testNonZero").invoke(instance));
    }
}
