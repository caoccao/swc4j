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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.function.*;


/**
 * Tests for arrow expression type inference (Phase 5).
 * Tests return type inference from expressions, statements, and various operators.
 */
public class TestCompileAstArrowTypeInference extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceBlockBody(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred with block body
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return x => {
                        const doubled: int = x * 2
                        return doubled + 1
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(3, 11, 21)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceDoubleBinaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter types inferred from DoubleBinaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleBinaryOperator {
                      return (x, y) => x / y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (DoubleBinaryOperator) instanceRunner.invoke("get");
        assertThat(fn.applyAsDouble(10.0, 5.0)).isCloseTo(2.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceDoubleUnaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from DoubleUnaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return x => x * 2.5
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (DoubleUnaryOperator) instanceRunner.invoke("get");
        assertThat(fn.applyAsDouble(1.0)).isCloseTo(2.5, within(0.0001));
        assertThat(fn.applyAsDouble(5.0)).isCloseTo(12.5, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceIntBinaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter types inferred from IntBinaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { IntBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(): IntBinaryOperator {
                      return (x, y) => x + y
                    }
                    getMultiplier(): IntBinaryOperator {
                      return (a, b) => a * b
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
    public void testParamTypeInferenceIntConsumer(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from IntConsumer context - void return
        var runner = getCompiler(jdkVersion).compile("""
                import { IntConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntConsumer {
                      return (x: int) => { }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var consumer = (java.util.function.IntConsumer) instanceRunner.invoke("get");
        consumer.accept(42); // Should not throw
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceIntPredicate(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from IntPredicate context
        var runner = getCompiler(jdkVersion).compile("""
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    isPositive(): IntPredicate {
                      return x => x > 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntPredicate) instanceRunner.invoke("isPositive");
        assertThat(
                List.of(
                        fn.test(5), fn.test(-5), fn.test(0)
                )
        ).isEqualTo(
                List.of(true, false, false)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceIntUnaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from IntUnaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return x => x * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(2, 10, 20)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceLongBinaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter types inferred from LongBinaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { LongBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongBinaryOperator {
                      return (x, y) => x - y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (LongBinaryOperator) instanceRunner.invoke("get");
        assertThat(fn.applyAsLong(10L, 3L)).isEqualTo(7L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceLongUnaryOperator(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from LongUnaryOperator context
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return x => x * 3
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (LongUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsLong(1L), fn.applyAsLong(5L), fn.applyAsLong(10L)
                )
        ).isEqualTo(
                List.of(3L, 15L, 30L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceVariableAssignment(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred from variable type annotation
        // Return the function and call it from Java side
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      const fn: IntUnaryOperator = x => x * 2
                      return fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(fn.applyAsInt(5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParamTypeInferenceWithCapture(JdkVersion jdkVersion) throws Exception {
        // Parameter type inferred, with closure capture
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    multiplier: int = 3
                    get(): IntUnaryOperator {
                      return x => x * this.multiplier
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(3, 15, 30)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceBinaryOperations(JdkVersion jdkVersion) throws Exception {
        // Return type inference from various binary operations
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
                    getModulo(): IntBinaryOperator {
                      return (x: int, y: int) => x % y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var adder = (IntBinaryOperator) instanceRunner.invoke("getAdder");
        var multiplier = (IntBinaryOperator) instanceRunner.invoke("getMultiplier");
        var modulo = (IntBinaryOperator) instanceRunner.invoke("getModulo");

        assertThat(
                List.of(
                        adder.applyAsInt(3, 5), multiplier.applyAsInt(3, 5), modulo.applyAsInt(17, 5)
                )
        ).isEqualTo(
                List.of(8, 15, 2)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceBitwiseOperations(JdkVersion jdkVersion) throws Exception {
        // Return type inference from bitwise operations
        var runner = getCompiler(jdkVersion).compile("""
                import { IntBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAnd(): IntBinaryOperator {
                      return (x: int, y: int) => x & y
                    }
                    getOr(): IntBinaryOperator {
                      return (x: int, y: int) => x | y
                    }
                    getXor(): IntBinaryOperator {
                      return (x: int, y: int) => x ^ y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var andOp = (IntBinaryOperator) instanceRunner.invoke("getAnd");
        var orOp = (IntBinaryOperator) instanceRunner.invoke("getOr");
        var xorOp = (IntBinaryOperator) instanceRunner.invoke("getXor");

        // 0b1010 & 0b1100 = 0b1000 = 8
        // 0b1010 | 0b1100 = 0b1110 = 14
        // 0b1010 ^ 0b1100 = 0b0110 = 6
        assertThat(
                List.of(
                        andOp.applyAsInt(10, 12), orOp.applyAsInt(10, 12), xorOp.applyAsInt(10, 12)
                )
        ).isEqualTo(
                List.of(8, 14, 6)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceBooleanExpression(JdkVersion jdkVersion) throws Exception {
        // Return type inferred as boolean from comparison
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
    public void testReturnTypeInferenceComplexExpression(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from complex expression
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * x + x - 1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        // f(x) = x^2 + x - 1
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(2), fn.applyAsInt(5)
                )
        ).isEqualTo(
                List.of(1, 5, 29)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceConditionalReturns(JdkVersion jdkVersion) throws Exception {
        // Return type inference with conditional returns in block
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => {
                        if (x < 0) return 0
                        if (x > 100) return 100
                        return x
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(-10), fn.applyAsInt(0), fn.applyAsInt(50),
                        fn.applyAsInt(100), fn.applyAsInt(150)
                )
        ).isEqualTo(
                List.of(0, 0, 50, 100, 100)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceDouble(JdkVersion jdkVersion) throws Exception {
        // Return type inferred as double from x * 2.0 where x is double
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return (x: double) => x * 2.0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (DoubleUnaryOperator) instanceRunner.invoke("get");
        assertThat(fn.applyAsDouble(1.0)).isCloseTo(2.0, within(0.0001));
        assertThat(fn.applyAsDouble(5.0)).isCloseTo(10.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceDoubleBinaryOperations(JdkVersion jdkVersion) throws Exception {
        // Return type inference from double binary operations
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(): DoubleBinaryOperator {
                      return (x: double, y: double) => x + y
                    }
                    getDivider(): DoubleBinaryOperator {
                      return (x: double, y: double) => x / y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var adder = (DoubleBinaryOperator) instanceRunner.invoke("getAdder");
        var divider = (DoubleBinaryOperator) instanceRunner.invoke("getDivider");

        assertThat(adder.applyAsDouble(3.5, 5.0)).isCloseTo(8.5, within(0.0001));
        assertThat(divider.applyAsDouble(10.0, 5.0)).isCloseTo(2.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceFromCapturedVariable(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from captured variable type
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getValue(x: int): IntSupplier {
                      return () => x * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntSupplier) instanceRunner.invoke("getValue", 5);
        assertThat(fn.getAsInt()).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceFromConstVariable(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from const variable type
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return (x: long) => {
                        const result: long = x * 2
                        return result
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (LongUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)
                )
        ).isEqualTo(
                List.of(2L, 200L, 2000L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceFromExpression(JdkVersion jdkVersion) throws Exception {
        // Edge case 60: Inferred return type from expression
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(2, 10, 20)
        );
    }

    // --- Phase 5: Parameter Type Inference from Context Tests ---

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceFromLocalVariable(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from local variable type
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => {
                        let result: int = x * 2
                        return result
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(2, 10, 20)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceFromReturnStatement(JdkVersion jdkVersion) throws Exception {
        // Edge case 61: Inferred return type from return statement
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => { return x * 2 }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(2, 10, 20)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceLong(JdkVersion jdkVersion) throws Exception {
        // Return type inferred as long from x * 2 where x is long
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return (x: long) => x * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (LongUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)
                )
        ).isEqualTo(
                List.of(2L, 200L, 2000L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceLongBinaryOperations(JdkVersion jdkVersion) throws Exception {
        // Return type inference from long binary operations
        var runner = getCompiler(jdkVersion).compile("""
                import { LongBinaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(): LongBinaryOperator {
                      return (x: long, y: long) => x + y
                    }
                    getMultiplier(): LongBinaryOperator {
                      return (x: long, y: long) => x * y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var adder = (LongBinaryOperator) instanceRunner.invoke("getAdder");
        var multiplier = (LongBinaryOperator) instanceRunner.invoke("getMultiplier");

        assertThat(
                List.of(
                        adder.applyAsLong(10L, 20L), multiplier.applyAsLong(10L, 20L)
                )
        ).isEqualTo(
                List.of(30L, 200L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceMultipleStatements(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from return statement in block with multiple statements
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => {
                        let doubled: int = x * 2
                        let result: int = doubled + 1
                        return result
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        // f(x) = x * 2 + 1
        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(3, 11, 21)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceNestedTernary(JdkVersion jdkVersion) throws Exception {
        // Return type inference from nested ternary
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getSign(): IntUnaryOperator {
                      return (x: int) => x > 0 ? 1 : x < 0 ? -1 : 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("getSign");
        assertThat(
                List.of(
                        fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)
                )
        ).isEqualTo(
                List.of(1, -1, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceSuppliers(JdkVersion jdkVersion) throws Exception {
        // Return type inference for various supplier types
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                import { LongSupplier } from 'java.util.function'
                import { DoubleSupplier } from 'java.util.function'
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getIntSupplier(): IntSupplier {
                      return () => 42
                    }
                    getLongSupplier(): LongSupplier {
                      const value: long = 42
                      return () => value
                    }
                    getDoubleSupplier(): DoubleSupplier {
                      return () => 3.14
                    }
                    getBooleanSupplier(): BooleanSupplier {
                      return () => true
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var intSupplier = (IntSupplier) instanceRunner.invoke("getIntSupplier");
        var longSupplier = (LongSupplier) instanceRunner.invoke("getLongSupplier");
        var doubleSupplier = (DoubleSupplier) instanceRunner.invoke("getDoubleSupplier");
        var booleanSupplier = (BooleanSupplier) instanceRunner.invoke("getBooleanSupplier");

        assertThat(intSupplier.getAsInt()).isEqualTo(42);
        assertThat(longSupplier.getAsLong()).isEqualTo(42L);
        assertThat(doubleSupplier.getAsDouble()).isCloseTo(3.14, within(0.001));
        assertThat(booleanSupplier.getAsBoolean()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceTernary(JdkVersion jdkVersion) throws Exception {
        // Return type inferred from ternary expression
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x > 0 ? x : -x
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)
                )
        ).isEqualTo(
                List.of(5, 5, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceUnaryOperations(JdkVersion jdkVersion) throws Exception {
        // Return type inference from unary operations
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getNegator(): IntUnaryOperator {
                      return (x: int) => -x
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("getNegator");
        assertThat(
                List.of(
                        fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)
                )
        ).isEqualTo(
                List.of(-5, 5, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeInferenceWithCapture(JdkVersion jdkVersion) throws Exception {
        // Return type inferred when using captured variables
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createSupplier(a: int, b: int, c: int): IntSupplier {
                      return () => a + b + c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntSupplier) instanceRunner.invoke("createSupplier", 10, 20, 30);
        assertThat(fn.getAsInt()).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnTypeWidening(JdkVersion jdkVersion) throws Exception {
        // When mixing int and long in expression, should widen to long
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return (x: long) => x + 1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (LongUnaryOperator) instanceRunner.invoke("get");
        assertThat(
                List.of(
                        fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)
                )
        ).isEqualTo(
                List.of(2L, 101L, 1001L)
        );
    }
}
