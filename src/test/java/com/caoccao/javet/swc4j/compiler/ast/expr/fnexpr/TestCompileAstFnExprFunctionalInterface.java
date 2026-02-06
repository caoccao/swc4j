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

package com.caoccao.javet.swc4j.compiler.ast.expr.fnexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.function.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for function expressions with various functional interfaces.
 * Covers: IntSupplier, BooleanSupplier, DoubleSupplier, LongSupplier, IntPredicate,
 * IntBinaryOperator, DoubleBinaryOperator, DoubleUnaryOperator, LongUnaryOperator,
 * LongBinaryOperator, IntConsumer, Runnable, Supplier.
 */
public class TestCompileAstFnExprFunctionalInterface extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprBooleanSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): BooleanSupplier {
                      return function(): boolean { return true }
                    }
                  }
                }""");
        var fn = (BooleanSupplier) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.getAsBoolean()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprDoubleBinaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleBinaryOperator {
                      return function(x: double, y: double): double { return x / y }
                    }
                  }
                }""");
        var fn = (DoubleBinaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsDouble(10.0, 4.0)).isCloseTo(2.5, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprDoubleSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleSupplier {
                      return function(): double { return 3.14 }
                    }
                  }
                }""");
        var fn = (DoubleSupplier) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.getAsDouble()).isCloseTo(3.14, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprDoubleUnaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return function(x: double): double { return x * 2.5 }
                    }
                  }
                }""");
        var fn = (DoubleUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsDouble(4.0)).isCloseTo(10.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprIntBinaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(): IntBinaryOperator {
                      return function(a: int, b: int): int { return a + b }
                    }
                    getMultiplier(): IntBinaryOperator {
                      return function(a: int, b: int): int { return a * b }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var adder = (IntBinaryOperator) instanceRunner.invoke("getAdder");
        var multiplier = (IntBinaryOperator) instanceRunner.invoke("getMultiplier");
        assertThat(
                List.of(adder.applyAsInt(3, 5), multiplier.applyAsInt(3, 5))
        ).isEqualTo(
                List.of(8, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprIntConsumer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntConsumer {
                      return function(x: int): void { }
                    }
                  }
                }""");
        var fn = (IntConsumer) runner.createInstanceRunner("com.A").invoke("get");
        fn.accept(42); // should not throw
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprIntPredicate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    isPositive(): IntPredicate {
                      return function(x: int): boolean { return x > 0 }
                    }
                  }
                }""");
        var fn = (IntPredicate) runner.createInstanceRunner("com.A").invoke("isPositive");
        assertThat(
                List.of(fn.test(5), fn.test(-5), fn.test(0))
        ).isEqualTo(
                List.of(true, false, false)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprIntSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntSupplier {
                      return function(): int { return 42 }
                    }
                  }
                }""");
        var fn = (IntSupplier) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.getAsInt()).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprLongBinaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongBinaryOperator {
                      return function(x: long, y: long): long { return x - y }
                    }
                  }
                }""");
        var fn = (LongBinaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.applyAsLong(10L, 3L)).isEqualTo(7L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprLongSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongSupplier {
                      const big: long = 123456789012345
                      return function(): long { return big }
                    }
                  }
                }""");
        var fn = (LongSupplier) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.getAsLong()).isEqualTo(123456789012345L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprLongUnaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return function(x: long): long { return x * 3 }
                    }
                  }
                }""");
        var fn = (LongUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(
                List.of(fn.applyAsLong(1L), fn.applyAsLong(5L), fn.applyAsLong(10L))
        ).isEqualTo(
                List.of(3L, 15L, 30L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprRunnable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Runnable } from 'java.lang'
                namespace com {
                  export class A {
                    get(): Runnable {
                      return function(): void { }
                    }
                  }
                }""");
        var fn = (Runnable) runner.createInstanceRunner("com.A").invoke("get");
        fn.run(); // should not throw
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprSupplierString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): Supplier {
                      return function(): String { return "hello" }
                    }
                  }
                }""");
        var fn = (Supplier<?>) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn.get()).isEqualTo("hello");
    }
}
