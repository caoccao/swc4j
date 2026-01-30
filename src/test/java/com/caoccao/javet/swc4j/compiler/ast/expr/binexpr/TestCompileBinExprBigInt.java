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

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Comprehensive BigInt binary operations tests.
 * Tests all working BigInt operations: arithmetic, equality, bitwise.
 */
public class TestCompileBinExprBigInt extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAdditionInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 100n + 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("300"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMixedWithLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 100n
                      const b: long = 50
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("150"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 0xFFn & 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("15"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseAndInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 0xFFn & 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("15"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 0xF0n | 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("255"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseOrInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 0xF0n | 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("255"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseXor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 0xFFn ^ 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("240"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBitwiseXorInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 0xFFn ^ 0x0Fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("240"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivide(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 100n / 3n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("33"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 100n / 3n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("33"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEqualityReturnsBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 100n == 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEquals(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return 100n == 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEqualsFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return 100n == 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExponentiation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 2n ** 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1024"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExponentiationInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 2n ** 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1024"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExponentiationLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 10n ** 20
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("100000000000000000000"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testTrue() {
                      return 200n > 100n
                    }
                    testFalse() {
                      return 100n > 200n
                    }
                    testEqual() {
                      return 100n > 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testTrue")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testFalse")).isFalse();
        assertThat(instanceRunner.<Boolean>invoke("testEqual")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testGreater() {
                      return 200n >= 100n
                    }
                    testEqual() {
                      return 100n >= 100n
                    }
                    testLess() {
                      return 100n >= 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testGreater")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testEqual")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testLess")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testLarge() {
                      return 999999999999999999n < 1000000000000000000n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testLarge")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 1n << 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1024"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLeftShiftInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 1n << 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1024"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLeftShiftLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 1n << 100
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1267650600228229401496703205376"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLessThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testTrue() {
                      return 100n < 200n
                    }
                    testFalse() {
                      return 200n < 100n
                    }
                    testEqual() {
                      return 100n < 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testTrue")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testFalse")).isFalse();
        assertThat(instanceRunner.<Boolean>invoke("testEqual")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testLess() {
                      return 100n <= 200n
                    }
                    testEqual() {
                      return 100n <= 100n
                    }
                    testGreater() {
                      return 200n <= 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testLess")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testEqual")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testGreater")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedWithPrimitive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testBigIntLtInt() {
                      const a: int = 200
                      return 100n < a
                    }
                    testIntLtBigInt() {
                      const a: int = 100
                      return a < 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testBigIntLtInt")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testIntLtBigInt")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedWithPrimitiveInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 50
                      return 100n + a
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("150"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testModulo(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 100n % 7n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("2"));
    }

    // Equality Operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testModuloInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 100n % 7n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 10n + 20n
                      return a * 3n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("90"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 5n * 7n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("35"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiply(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 5n * 7n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("35"));
    }

    // Bitwise Operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplyLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 123456789n * 987654321n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("121932631112635269"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeBigInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -100n + 50n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("-50"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeNumbers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testNegativeLt() {
                      return -100n < -50n
                    }
                    testNegativeGt() {
                      return -50n > -100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("testNegativeLt")).isTrue();
        assertThat(instanceRunner.<Boolean>invoke("testNegativeGt")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNotEquals(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return 100n != 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 1024n >> 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("256"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRightShiftInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 1024n >> 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("256"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStrictEquals(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      return 100n === 100n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Boolean>invoke("test")).isTrue();
    }

    // Mixed type operations

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSubtractionInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 500n - 200n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("300"));
    }

    // Edge cases

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVariableInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const result = 10n * 20n
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("200"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroFillRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 1024n >>> 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        // Note: >>> uses signed shift for BigInteger (limitation)
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("256"));
    }
}
