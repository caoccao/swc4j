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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var consumer = (DoubleConsumer) classA.getMethod("getConsumer").invoke(instance);
        assertNotNull(consumer);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var consumer = (IntConsumer) classA.getMethod("getConsumer").invoke(instance);
        assertNotNull(consumer);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var consumer = (LongConsumer) classA.getMethod("getConsumer").invoke(instance);
        assertNotNull(consumer);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("createComputer", int.class, int.class, int.class, int.class)
                .invoke(instance, 1, 2, 3, 4);

        assertEquals(
                List.of(10, 15, 20, 110),
                List.of(fn.applyAsInt(0), fn.applyAsInt(5), fn.applyAsInt(10), fn.applyAsInt(100)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(42, ((IntSupplier) fn).getAsInt());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (DoubleUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(10.0, fn.applyAsDouble(5.0), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(10, ((IntUnaryOperator) fn).applyAsInt(5));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (LongUnaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(200L, fn.applyAsLong(100L));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (DoubleBinaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(8.5, fn.applyAsDouble(3.5, 5.0), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var adder = (IntBinaryOperator) classA.getMethod("getAdder").invoke(instance);
        var multiplier = (IntBinaryOperator) classA.getMethod("getMultiplier").invoke(instance);

        assertEquals(
                List.of(8, 15),
                List.of(adder.applyAsInt(3, 5), multiplier.applyAsInt(3, 5)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (LongBinaryOperator) classA.getMethod("get").invoke(instance);
        assertEquals(30L, fn.applyAsLong(10L, 20L));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn2 = (IntUnaryOperator) classA.getMethod("createMultiplier", int.class).invoke(instance, 2);
        var fn3 = (IntUnaryOperator) classA.getMethod("createMultiplier", int.class).invoke(instance, 3);

        // fn2.applyAsInt(5) = 5*2 = 10
        // fn3.applyAsInt(5) = 5*3 = 15
        // fn2.applyAsInt(15) = 15*2 = 30
        // fn3.applyAsInt(10) = 10*3 = 30
        assertEquals(
                List.of(10, 15, 30, 30),
                List.of(fn2.applyAsInt(5), fn3.applyAsInt(5), fn2.applyAsInt(15), fn3.applyAsInt(10)));
    }
}
