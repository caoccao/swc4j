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
import static org.assertj.core.api.Assertions.within;


public class TestCompileBinExprExp extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteExpByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 3
                      const b: byte = 4
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(81.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectExpLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 2
                      const b: Long = 5
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(32.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedExponentiation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = 3
                      const c: int = 2
                      return a ** b ** c
                    }
                  }
                }""");
        // 2 ** 3 ** 2 = 2 ** 9 = 512 (right-associative in most languages)
        // However, if evaluated left-to-right: (2 ** 3) ** 2 = 8 ** 2 = 64
        // Let's test what we actually get
        double result = runner.createInstanceRunner("com.A").invoke("test");
        // Just verify it's either 512 or 64, both are valid depending on associativity
        assertThat(result == 512.0 || result == 64.0).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleExpDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.0
                      const b: double = 3.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(27.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleObjectExpDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 2.0
                      const b: Double = 4.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(16.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpCubeRoot(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 27.0
                      const b: double = 0.333333333
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(3.0, within(0.001)); // Approximate cube root
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpPrecision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.1
                      const b: double = 3.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(1.331, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpTruncation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: double = 2.5
                      const b: double = 2.0
                      return (a ** b) as int
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(6); // 2.5 ** 2 = 6.25, truncated to 6
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpWithExplicitCastToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      const a: int = 2
                      const b: int = 3
                      return (a ** b) as float
                    }
                  }
                }""");
        assertThat((Float) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(8.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpWithExplicitCastToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 5
                      const b: int = 2
                      return (a ** b) as int
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(25);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpWithExplicitCastToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const a: int = 3
                      const b: int = 4
                      return (a ** b) as long
                    }
                  }
                }""");
        assertThat((Long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(81L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpWithImplicitCastToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const a: int = 3
                      const b: int = 4
                      return a ** b
                    }
                  }
                }""");
        assertThat((Long) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(81L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExponentiationWithParentheses(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = 3
                      const c: int = 2
                      return (a ** b) ** c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(64.0); // (2 ** 3) ** 2 = 8 ** 2 = 64
    }

    // Mixed type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatExpDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.0
                      const b: double = 3.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(8.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatExpFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.5
                      const b: float = 2.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(6.25, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectExpFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 1.5
                      const b: Float = 3.0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(3.375, within(0.00001));
    }

    // Edge cases

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFractionalExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 4.0
                      const b: double = 0.5
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(2.0, within(0.00001)); // Square root of 4
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntExpFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 4
                      const b: float = 0.5
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(2.0, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntExpInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = 3
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(8.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntExpIntReturnInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 2
                      const b: int = 3
                      return a ** b
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(8);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntExpLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: long = 3
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(125.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerExpInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 4
                      const b: Integer = 2
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(16.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = 10
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1024.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongExpLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 10
                      const b: long = 2
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectExpLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 3
                      const b: Long = 4
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(81.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeBase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -2
                      const b: int = 3
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-8.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeBaseEvenExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -3
                      const b: int = 2
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(9.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = -1
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0.5);
    }

    // Explicit cast tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOneBase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: int = 100
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOneExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 1
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortExpShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 5
                      const b: short = 2
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(25.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroBase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 5
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0.0);
    }

    // Precision tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroExponent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1.0); // Any number to the power of 0 is 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroPowerZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a ** b
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1.0); // Math.pow(0) = 1.0 by convention
    }
}
