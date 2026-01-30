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

/**
 * Tests for arrow expression edge cases.
 * Tests various edge cases from the implementation plan.
 */
public class TestCompileAstArrowEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowAsMethodReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 46: As return value
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(x: int): IntUnaryOperator {
                      return (y: int) => x + y
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("getAdder", 10);
        assertThat(fn.applyAsInt(5)).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowAssignedToLocalVariable(JdkVersion jdkVersion) throws Exception {
        // Arrow assigned to local variable and returned
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getSupplier(): IntSupplier {
                      const supplier: IntSupplier = () => 42
                      return supplier
                    }
                    getComputedSupplier(): IntSupplier {
                      const base: int = 10
                      const supplier: IntSupplier = () => base * 2
                      return supplier
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var fn1 = (IntSupplier) instanceRunner.invoke("getSupplier");
        var fn2 = (IntSupplier) instanceRunner.invoke("getComputedSupplier");

        assertThat(
                List.of(
                        fn1.getAsInt(), fn2.getAsInt()
                )
        ).isEqualTo(
                List.of(42, 20)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureMultipleVariables(JdkVersion jdkVersion) throws Exception {
        // Edge case 30: Capture multiple local variables
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const x: int = 10
                      const y: int = 20
                      const z: int = 30
                      return () => x + y + z
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntSupplier) instanceRunner.invoke("test");
        assertThat(fn.getAsInt()).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturePrimitiveAndObject(JdkVersion jdkVersion) throws Exception {
        // Test arrow capturing both primitive and object
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Supplier {
                      const prefix: String = "Value: "
                      const value: int = 42
                      return () => prefix + value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (Supplier<?>) instanceRunner.invoke("test");
        assertThat(fn.get()).isEqualTo("Value: 42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureStaticField(JdkVersion jdkVersion) throws Exception {
        // Edge case 39: Capture static field
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    static multiplier: int = 10
                    static getMultiplied(x: int): IntSupplier {
                      return () => x * A.multiplier
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        var fn = (IntSupplier) staticRunner.invoke("getMultiplied", 5);
        assertThat(fn.getAsInt()).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureThisAndLocalVars(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Capture both this and local variables
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    multiplier: int = 2
                    compute(base: int): IntSupplier {
                      const offset: int = 10
                      return () => base * this.multiplier + offset
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntSupplier) instanceRunner.invoke("compute", 5);
        assertThat(fn.getAsInt()).isEqualTo(20); // 5 * 2 + 10 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturingBoolean(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing boolean values
        var runner = getCompiler(jdkVersion).compile("""
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createSupplier(flag: boolean): BooleanSupplier {
                      return () => flag
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var trueFn = (BooleanSupplier) instanceRunner.invoke("createSupplier", true);
        var falseFn = (BooleanSupplier) instanceRunner.invoke("createSupplier", false);

        assertThat(trueFn.getAsBoolean()).isTrue();
        assertThat(falseFn.getAsBoolean()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturingString(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing String and returning it
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getString(s: String): Supplier {
                      return () => s
                    }
                    getConcatenated(a: String, b: String): Supplier {
                      return () => a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var fn1 = (Supplier<?>) instanceRunner.invoke("getString", "hello");
        var fn2 = (Supplier<?>) instanceRunner.invoke("getConcatenated", "Hello, ", "World!");

        assertThat(
                List.of(
                        fn1.get(), fn2.get()
                )
        ).isEqualTo(
                List.of("hello", "Hello, World!")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInConditionalExpression(JdkVersion jdkVersion) throws Exception {
        // Edge case 50: Arrow in conditional expression
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getOperator(isAdd: boolean): IntUnaryOperator {
                      return isAdd ? (x: int) => x + 10 : (x: int) => x - 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var addFn = (IntUnaryOperator) instanceRunner.invoke("getOperator", true);
        var subFn = (IntUnaryOperator) instanceRunner.invoke("getOperator", false);

        assertThat(
                List.of(
                        addFn.applyAsInt(5), addFn.applyAsInt(15),
                        subFn.applyAsInt(5), subFn.applyAsInt(15)
                )
        ).isEqualTo(
                List.of(15, 25, -5, 5)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInConstructor(JdkVersion jdkVersion) throws Exception {
        // Edge case 56: Arrow inside constructor - arrow assigned to field
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    supplier: IntSupplier
                    constructor(base: int) {
                      const multiplier: int = 2
                      this.supplier = () => base * multiplier
                    }
                    getSupplier(): IntSupplier {
                      return this.supplier
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A", 21);
        var fn = (IntSupplier) instanceRunner.invoke("getSupplier");

        assertThat(fn.getAsInt()).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInFieldInitializer(JdkVersion jdkVersion) throws Exception {
        // Edge case 49: As field initializer (simplified - stores as field)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    processor: IntUnaryOperator = (x: int) => x * 2
                    getProcessor(): IntUnaryOperator {
                      return this.processor
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("getProcessor");
        assertThat(fn.applyAsInt(5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInStaticMethod(JdkVersion jdkVersion) throws Exception {
        // Edge case 57: Arrow inside static method
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    static getDoubler(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        var fn = (IntUnaryOperator) staticRunner.invoke("getDoubler");

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
    public void testArrowReassignment(JdkVersion jdkVersion) throws Exception {
        // Edge case 42: Arrow assigned to let variable (reassignment)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    compute(useDoubler: boolean): IntUnaryOperator {
                      let fn: IntUnaryOperator = (x: int) => x * 2
                      if (!useDoubler) {
                        fn = (x: int) => x * 3
                      }
                      return fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var doublerFn = (IntUnaryOperator) instanceRunner.invoke("compute", true);
        var triplerFn = (IntUnaryOperator) instanceRunner.invoke("compute", false);

        assertThat(
                List.of(
                        doublerFn.applyAsInt(5), triplerFn.applyAsInt(5)
                )
        ).isEqualTo(
                List.of(10, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithComplexCondition(JdkVersion jdkVersion) throws Exception {
        // Arrow with complex boolean condition
        var runner = getCompiler(jdkVersion).compile("""
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    getInRangeChecker(min: int, max: int): IntPredicate {
                      return (x: int) => x >= min && x <= max
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntPredicate) instanceRunner.invoke("getInRangeChecker", 10, 20);

        assertThat(
                List.of(
                        fn.test(5), fn.test(10), fn.test(15), fn.test(20), fn.test(25)
                )
        ).isEqualTo(
                List.of(false, true, true, true, false)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithExplicitTypedVariable(JdkVersion jdkVersion) throws Exception {
        // Edge case 43: Assigned to typed variable
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getDoubler(): IntUnaryOperator {
                      const fn: IntUnaryOperator = (x: int) => x * 2
                      return fn
                    }
                    getTripler(): IntUnaryOperator {
                      const fn: IntUnaryOperator = (x: int) => x * 3
                      return fn
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var doubler = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        var tripler = (IntUnaryOperator) instanceRunner.invoke("getTripler");

        assertThat(
                List.of(
                        doubler.applyAsInt(5), tripler.applyAsInt(5)
                )
        ).isEqualTo(
                List.of(10, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithMultiParamCapture(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing multiple parameters
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(a: int, b: int): IntUnaryOperator {
                      return (x: int) => x + a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var fn = (IntUnaryOperator) instanceRunner.invoke("createAdder", 10, 5);

        assertThat(
                List.of(
                        fn.applyAsInt(0), fn.applyAsInt(1), fn.applyAsInt(10), fn.applyAsInt(100)
                )
        ).isEqualTo(
                List.of(15, 16, 25, 115)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithNestedTernary(JdkVersion jdkVersion) throws Exception {
        // Test arrow with nested ternary
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
        assertThat(fn.applyAsInt(5)).isEqualTo(1);
        assertThat(fn.applyAsInt(-5)).isEqualTo(-1);
        assertThat(fn.applyAsInt(0)).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureFromConstructor(JdkVersion jdkVersion) throws Exception {
        // Edge case 38: Capture from constructor
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    fn: IntSupplier
                    constructor(x: int) {
                      this.fn = () => x
                    }
                    getSupplier(): IntSupplier {
                      return this.fn
                    }
                  }
                }""");
        var instanceRunner1 = runner.createInstanceRunner("com.A", 42);
        var instanceRunner2 = runner.createInstanceRunner("com.A", 100);

        var fn1 = (IntSupplier) instanceRunner1.invoke("getSupplier");
        var fn2 = (IntSupplier) instanceRunner2.invoke("getSupplier");

        assertThat(
                List.of(
                        fn1.getAsInt(), fn2.getAsInt()
                )
        ).isEqualTo(
                List.of(42, 100)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsDifferentTypes(JdkVersion jdkVersion) throws Exception {
        // Multiple arrows with different functional interfaces
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                import { IntSupplier } from 'java.util.function'
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    getDoubler(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                    getConstant(): IntSupplier {
                      return () => 42
                    }
                    getPositive(): IntPredicate {
                      return (x: int) => x > 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var doubler = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        var constant = (IntSupplier) instanceRunner.invoke("getConstant");
        var positive = (IntPredicate) instanceRunner.invoke("getPositive");

        assertThat(
                List.of(
                        doubler.applyAsInt(5), constant.getAsInt(),
                        positive.test(5), positive.test(-5)
                )
        ).isEqualTo(
                List.of(10, 42, true, false)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsInSameMethod(JdkVersion jdkVersion) throws Exception {
        // Multiple arrows in same method with different captures, returned as list
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator, IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(x: int): IntUnaryOperator {
                      return (y: int) => y + x
                    }
                    getDoubler(): IntUnaryOperator {
                      return (y: int) => y * 2
                    }
                    getSupplier(x: int): IntSupplier {
                      return () => x * 10
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        var adder5 = (IntUnaryOperator) instanceRunner.invoke("getAdder", 5);
        var doubler = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        var supplier5 = (IntSupplier) instanceRunner.invoke("getSupplier", 5);

        // supplier5() = 50, doubler(50) = 100, adder5(100) = 105
        int result = adder5.applyAsInt(doubler.applyAsInt(supplier5.getAsInt()));
        assertThat(result).isEqualTo(105);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPureFunctionNoCapture(JdkVersion jdkVersion) throws Exception {
        // Edge case 40: Pure function with no external dependencies
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
}
