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
 * Tests for nested arrow expressions.
 * Tests Phase 6 from the implementation plan.
 */
public class TestCompileAstArrowNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowChainedWithCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier, IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getDoubler(): IntUnaryOperator {
                      const factor: int = 2
                      return (x: int) => x * factor
                    }
                    getIncrementer(): IntUnaryOperator {
                      const offset: int = 1
                      return (x: int) => x + offset
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var doubler = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        var incrementer = (IntUnaryOperator) instanceRunner.invoke("getIncrementer");

        assertThat(doubler.applyAsInt(5)).isEqualTo(10);   // 5 * 2 = 10
        assertThat(incrementer.applyAsInt(5)).isEqualTo(6); // 5 + 1 = 6
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInStaticMethod(JdkVersion jdkVersion) throws Exception {
        // Edge case 57: Arrow inside static method
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    static createMultiplier(factor: int): IntUnaryOperator {
                      return (x: int) => x * factor
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        var multiplier2 = (IntUnaryOperator) staticRunner.invoke("createMultiplier", 2);
        var multiplier5 = (IntUnaryOperator) staticRunner.invoke("createMultiplier", 5);
        assertThat(
                List.of(
                        multiplier2.applyAsInt(3), multiplier2.applyAsInt(5), multiplier5.applyAsInt(3), multiplier5.applyAsInt(5)
                )
        ).isEqualTo(
                List.of(6, 10, 15, 25)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInsideArrow(JdkVersion jdkVersion) throws Exception {
        // Edge case 53: Arrow inside arrow - basic curried function
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(x: int): IntUnaryOperator {
                      return (y: int) => x + y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var adder5 = (IntUnaryOperator) instanceRunner.invoke("createAdder", 5);
        var adder10 = (IntUnaryOperator) instanceRunner.invoke("createAdder", 10);
        assertThat(
                List.of(
                        adder5.applyAsInt(3), adder5.applyAsInt(8), adder10.applyAsInt(2), adder10.applyAsInt(7)
                )
        ).isEqualTo(
                List.of(8, 13, 12, 17)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithDifferentCaptureTypes(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing different types of variables
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createFormatter(prefix: String, value: int): Supplier {
                      return () => prefix + value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var supplier = (Supplier<?>) instanceRunner.invoke("createFormatter", "Value: ", 42);
        assertThat(supplier.get()).isEqualTo("Value: 42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithDoubleCaptures(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing double values
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createProduct(a: double, b: double): DoubleSupplier {
                      return () => a * b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var supplier = (DoubleSupplier) instanceRunner.invoke("createProduct", 2.5, 4.0);
        assertThat(supplier.getAsDouble()).isCloseTo(10.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithLongCaptures(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing long values
        var runner = getCompiler(jdkVersion).compile("""
                import { LongSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createSum(a: long, b: long): LongSupplier {
                      return () => a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var supplier = (LongSupplier) instanceRunner.invoke("createSum", 1000000000L, 2000000000L);
        assertThat(supplier.getAsLong()).isEqualTo(3000000000L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithMixedTypeCaptures(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing int, long, and double
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createComplex(a: int, b: long, c: double): DoubleSupplier {
                      return () => a + b + c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var supplier = (DoubleSupplier) instanceRunner.invoke("createComplex", 10, 20L, 30.5);
        // 10 + 20 + 30.5 = 60.5
        assertThat(supplier.getAsDouble()).isCloseTo(60.5, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithMultiplePrimitiveCaptures(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing multiple primitive values
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createComputer(a: int, b: int, c: int, d: int): IntSupplier {
                      return () => a * b + c * d
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var supplier = (IntSupplier) instanceRunner.invoke("createComputer", 2, 3, 4, 5);
        // 2 * 3 + 4 * 5 = 6 + 20 = 26
        assertThat(supplier.getAsInt()).isEqualTo(26);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithOffsetCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(offset: int): IntUnaryOperator {
                      return (x: int) => x + offset
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = instanceRunner.invoke("createAdder", 5);
        assertThat(fn).isNotNull();
        assertThat(((IntUnaryOperator) fn).applyAsInt(10)).isEqualTo(15);  // 10 + 5 = 15
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithStringCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createGreeter(name: String): Supplier {
                      const greeting: String = "Hello, "
                      return () => greeting + name
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = instanceRunner.invoke("createGreeter", "World");
        assertThat(fn).isNotNull();
        assertThat(((java.util.function.Supplier<?>) fn).get()).isEqualTo("Hello, World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithThisCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    value: int = 100
                    createValueGetter(): IntSupplier {
                      return () => this.value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = instanceRunner.invoke("createValueGetter");
        assertThat(fn).isNotNull();
        assertThat(((IntSupplier) fn).getAsInt()).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeeplyNestedArrows(JdkVersion jdkVersion) throws Exception {
        // Edge case 58: Deeply nested arrows (capturing multiple values)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(a: int, b: int, c: int): IntUnaryOperator {
                      return (x: int) => x + a + b + c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("createAdder", 1, 2, 3);

        // x + 1 + 2 + 3 = x + 6
        assertThat(
                List.of(
                        fn.applyAsInt(0), fn.applyAsInt(10), fn.applyAsInt(100)
                )
        ).isEqualTo(
                List.of(6, 16, 106)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiLevelCapture(JdkVersion jdkVersion) throws Exception {
        // Edge case 37: Multi-level capture - capturing multiple params
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createCombiner(a: int, b: int): IntUnaryOperator {
                      return (x: int) => x + a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var combiner = (IntUnaryOperator) instanceRunner.invoke("createCombiner", 10, 20);
        // x + a + b = x + 10 + 20 = x + 30
        assertThat(
                List.of(
                        combiner.applyAsInt(0), combiner.applyAsInt(5), combiner.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(30, 35, 40)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsFromSameClass(JdkVersion jdkVersion) throws Exception {
        // Multiple arrows returned from the same class
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator, IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getDoubler(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                    getAdder(base: int): IntUnaryOperator {
                      return (x: int) => x + base
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var doubler = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        var adder = (IntUnaryOperator) instanceRunner.invoke("getAdder", 10);
        assertThat(
                List.of(
                        doubler.applyAsInt(5), doubler.applyAsInt(10), adder.applyAsInt(5), adder.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(10, 20, 15, 20)
        );
    }
}
