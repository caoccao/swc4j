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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for arrow expression type inference (Phase 5).
 * Tests return type inference from expressions, statements, and various operators.
 */
public class TestCompileAstArrowTypeInference extends BaseTestCompileSuite {

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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var adder = (IntBinaryOperator) classA.getMethod("getAdder").invoke(instance);
        var multiplier = (IntBinaryOperator) classA.getMethod("getMultiplier").invoke(instance);
        var modulo = (IntBinaryOperator) classA.getMethod("getModulo").invoke(instance);

        assertEquals(
                List.of(8, 15, 2),
                List.of(adder.applyAsInt(3, 5), multiplier.applyAsInt(3, 5), modulo.applyAsInt(17, 5)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var andOp = (IntBinaryOperator) classA.getMethod("getAnd").invoke(instance);
        var orOp = (IntBinaryOperator) classA.getMethod("getOr").invoke(instance);
        var xorOp = (IntBinaryOperator) classA.getMethod("getXor").invoke(instance);

        // 0b1010 & 0b1100 = 0b1000 = 8
        // 0b1010 | 0b1100 = 0b1110 = 14
        // 0b1010 ^ 0b1100 = 0b0110 = 6
        assertEquals(
                List.of(8, 14, 6),
                List.of(andOp.applyAsInt(10, 12), orOp.applyAsInt(10, 12), xorOp.applyAsInt(10, 12)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var positiveFn = (IntPredicate) classA.getMethod("getPositive").invoke(instance);
        var evenFn = (IntPredicate) classA.getMethod("getEven").invoke(instance);

        assertEquals(
                List.of(true, false, false),
                List.of(positiveFn.test(5), positiveFn.test(-5), positiveFn.test(0)));
        assertEquals(
                List.of(true, false, true),
                List.of(evenFn.test(4), evenFn.test(5), evenFn.test(0)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        // f(x) = x^2 + x - 1
        assertEquals(
                List.of(1, 5, 29),
                List.of(fn.applyAsInt(1), fn.applyAsInt(2), fn.applyAsInt(5)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(0, 0, 50, 100, 100),
                List.of(fn.applyAsInt(-10), fn.applyAsInt(0), fn.applyAsInt(50),
                        fn.applyAsInt(100), fn.applyAsInt(150)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (DoubleUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(2.0, fn.applyAsDouble(1.0), 0.0001);
        assertEquals(10.0, fn.applyAsDouble(5.0), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var adder = (DoubleBinaryOperator) classA.getMethod("getAdder").invoke(instance);
        var divider = (DoubleBinaryOperator) classA.getMethod("getDivider").invoke(instance);

        assertEquals(8.5, adder.applyAsDouble(3.5, 5.0), 0.0001);
        assertEquals(2.0, divider.applyAsDouble(10.0, 5.0), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntSupplier) classA.getMethod("getValue", int.class).invoke(instance, 5);
        assertEquals(10, fn.getAsInt());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (LongUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2L, 200L, 2000L),
                List.of(fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2, 10, 20),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
    }

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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2, 10, 20),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2, 10, 20),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (LongUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2L, 200L, 2000L),
                List.of(fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var adder = (LongBinaryOperator) classA.getMethod("getAdder").invoke(instance);
        var multiplier = (LongBinaryOperator) classA.getMethod("getMultiplier").invoke(instance);

        assertEquals(
                List.of(30L, 200L),
                List.of(adder.applyAsLong(10L, 20L), multiplier.applyAsLong(10L, 20L)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        // f(x) = x * 2 + 1
        assertEquals(
                List.of(3, 11, 21),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getSign").invoke(instance);
        assertEquals(
                List.of(1, -1, 0),
                List.of(fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var intSupplier = (IntSupplier) classA.getMethod("getIntSupplier").invoke(instance);
        var longSupplier = (LongSupplier) classA.getMethod("getLongSupplier").invoke(instance);
        var doubleSupplier = (DoubleSupplier) classA.getMethod("getDoubleSupplier").invoke(instance);
        var booleanSupplier = (BooleanSupplier) classA.getMethod("getBooleanSupplier").invoke(instance);

        assertEquals(42, intSupplier.getAsInt());
        assertEquals(42L, longSupplier.getAsLong());
        assertEquals(3.14, doubleSupplier.getAsDouble(), 0.001);
        assertTrue(booleanSupplier.getAsBoolean());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(5, 5, 0),
                List.of(fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getNegator").invoke(instance);
        assertEquals(
                List.of(-5, 5, 0),
                List.of(fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntSupplier) classA.getMethod("createSupplier", int.class, int.class, int.class)
                .invoke(instance, 10, 20, 30);
        assertEquals(60, fn.getAsInt());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (LongUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(
                List.of(2L, 101L, 1001L),
                List.of(fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L)));
    }
}
