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

package com.caoccao.javet.swc4j.compiler.ast.expr.arrow;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.function.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for arrow expression parameter handling.
 * Tests various parameter patterns from the implementation plan.
 */
public class TestCompileAstArrowParams extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleConsumer(JdkVersion jdkVersion) throws Exception {
        // Single double parameter with void return -> DoubleConsumer
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    getConsumer(): DoubleConsumer {
                      return (x: double) => {}
                    }
                  }
                }""");
        var consumer = (DoubleConsumer) runner.createInstanceRunner("com.A").invoke("getConsumer");
        assertThat(consumer).isNotNull();
        // Verify it doesn't throw
        consumer.accept(3.14);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntConsumer(JdkVersion jdkVersion) throws Exception {
        // Single int parameter with void return -> IntConsumer
        var runner = getCompiler(jdkVersion).compile("""
                import { IntConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    getConsumer(): IntConsumer {
                      return (x: int) => {}
                    }
                  }
                }""");
        var consumer = (IntConsumer) runner.createInstanceRunner("com.A").invoke("getConsumer");
        assertThat(consumer).isNotNull();
        // Verify it doesn't throw
        consumer.accept(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPredicate(JdkVersion jdkVersion) throws Exception {
        // Single int parameter with boolean return -> IntPredicate
        var runner = getCompiler(jdkVersion).compile("""
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    getPositive(): IntPredicate {
                      return (x: int) => x > 0
                    }
                    getEven(): IntPredicate {
                      return (x: int) => x % 2 == 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var positiveFn = (IntPredicate) instanceRunner.invoke("getPositive");
        var evenFn = (IntPredicate) instanceRunner.invoke("getEven");

        assertThat(
                List.of(
                        positiveFn.test(5), positiveFn.test(-5), positiveFn.test(0)
                )
        ).isEqualTo(
                List.of(true, false, false)
        );

        assertThat(
                List.of(
                        evenFn.test(4), evenFn.test(5), evenFn.test(0)
                )
        ).isEqualTo(
                List.of(true, false, true)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongConsumer(JdkVersion jdkVersion) throws Exception {
        // Single long parameter with void return -> LongConsumer
        var runner = getCompiler(jdkVersion).compile("""
                import { LongConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    getConsumer(): LongConsumer {
                      return (x: long) => {}
                    }
                  }
                }""");
        var consumer = (LongConsumer) runner.createInstanceRunner("com.A").invoke("getConsumer");
        assertThat(consumer).isNotNull();
        // Verify it doesn't throw
        consumer.accept(42L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testManyParametersViaClosure(JdkVersion jdkVersion) throws Exception {
        // Edge case 6: Many parameters via closure capture
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createComputer(a: int, b: int, c: int, d: int): IntUnaryOperator {
                      return (x: int) => x + a + b + c + d
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("createComputer", 1, 2, 3, 4);

        assertThat(
                List.of(
                        fn.applyAsInt(0), fn.applyAsInt(5), fn.applyAsInt(10), fn.applyAsInt(100)
                )
        ).isEqualTo(
                List.of(10, 15, 20, 110)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNoParameters(JdkVersion jdkVersion) throws Exception {
        // Edge case 1: No parameters
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntSupplier {
                      return () => 42
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        assertThat(((IntSupplier) fn).getAsInt()).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleDoubleParameter(JdkVersion jdkVersion) throws Exception {
        // Test with double parameter
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return (x: double) => x * 2.0
                    }
                  }
                }""");
        var fn = (DoubleUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsDouble(5.0)).isCloseTo(10.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleIntParameter(JdkVersion jdkVersion) throws Exception {
        // Edge case 4: Single parameter with parentheses
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        assertThat(((IntUnaryOperator) fn).applyAsInt(5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleLongParameter(JdkVersion jdkVersion) throws Exception {
        // Test with long parameter
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return (x: long) => x * 2
                    }
                  }
                }""");
        var fn = (LongUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsLong(100L)).isEqualTo(200L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoDoubleParameters(JdkVersion jdkVersion) throws Exception {
        // Two double parameters -> DoubleBinaryOperator
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleBinaryOperator {
                      return (x: double, y: double) => x + y
                    }
                  }
                }""");
        var fn = (DoubleBinaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsDouble(3.5, 5.0)).isCloseTo(8.5, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoIntParameters(JdkVersion jdkVersion) throws Exception {
        // Edge case 5: Two int parameters -> IntBinaryOperator
        var runner = getCompiler(jdkVersion).compile("""
                import { IntBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(): IntBinaryOperator {
                      return (x: int, y: int) => x + y
                    }
                    getMultiplier(): IntBinaryOperator {
                      return (x: int, y: int) => x * y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var adder = (IntBinaryOperator) instanceRunner.invoke("getAdder");
        var multiplier = (IntBinaryOperator) instanceRunner.invoke("getMultiplier");

        assertThat(
                List.of(
                        adder.applyAsInt(3, 5), multiplier.applyAsInt(3, 5)
                )
        ).isEqualTo(
                List.of(8, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLongParameters(JdkVersion jdkVersion) throws Exception {
        // Two long parameters -> LongBinaryOperator
        var runner = getCompiler(jdkVersion).compile("""
                import { LongBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongBinaryOperator {
                      return (x: long, y: long) => x + y
                    }
                  }
                }""");
        var fn = (LongBinaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsLong(10L, 20L)).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypedParametersMixed(JdkVersion jdkVersion) throws Exception {
        // Edge case 7: Typed parameters with mixed types (via capture)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createMultiplier(factor: int): IntUnaryOperator {
                      return (x: int) => x * factor
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn2 = (IntUnaryOperator) instanceRunner.invoke("createMultiplier", 2);
        var fn3 = (IntUnaryOperator) instanceRunner.invoke("createMultiplier", 3);

        // fn2.applyAsInt(5) = 5*2 = 10
        // fn3.applyAsInt(5) = 5*3 = 15
        // fn2.applyAsInt(15) = 15*2 = 30
        // fn3.applyAsInt(10) = 10*3 = 30
        assertThat(
                List.of(
                        fn2.applyAsInt(5), fn3.applyAsInt(5), fn2.applyAsInt(15), fn3.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(10, 15, 30, 30)
        );
    }
}
