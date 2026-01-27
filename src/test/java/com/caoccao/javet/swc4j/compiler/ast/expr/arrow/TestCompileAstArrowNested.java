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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();

        var doubler = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(instance);
        var incrementer = (IntUnaryOperator) classA.getMethod("getIncrementer").invoke(instance);

        assertEquals(10, doubler.applyAsInt(5));   // 5 * 2 = 10
        assertEquals(6, incrementer.applyAsInt(5)); // 5 + 1 = 6
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
        Class<?> classA = runner.getClass("com.A");
        var multiplier2 = (IntUnaryOperator) classA.getMethod("createMultiplier", int.class).invoke(null, 2);
        var multiplier5 = (IntUnaryOperator) classA.getMethod("createMultiplier", int.class).invoke(null, 5);
        assertEquals(
                List.of(6, 10, 15, 25),
                List.of(multiplier2.applyAsInt(3), multiplier2.applyAsInt(5), multiplier5.applyAsInt(3), multiplier5.applyAsInt(5)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var adder5 = (IntUnaryOperator) classA.getMethod("createAdder", int.class).invoke(instance, 5);
        var adder10 = (IntUnaryOperator) classA.getMethod("createAdder", int.class).invoke(instance, 10);
        assertEquals(
                List.of(8, 13, 12, 17),
                List.of(adder5.applyAsInt(3), adder5.applyAsInt(8), adder10.applyAsInt(2), adder10.applyAsInt(7)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var supplier = (Supplier<?>) classA.getMethod("createFormatter", String.class, int.class)
                .invoke(instance, "Value: ", 42);
        assertEquals("Value: 42", supplier.get());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var supplier = (DoubleSupplier) classA.getMethod("createProduct", double.class, double.class)
                .invoke(instance, 2.5, 4.0);
        assertEquals(10.0, supplier.getAsDouble(), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var supplier = (LongSupplier) classA.getMethod("createSum", long.class, long.class)
                .invoke(instance, 1000000000L, 2000000000L);
        assertEquals(3000000000L, supplier.getAsLong());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var supplier = (DoubleSupplier) classA.getMethod("createComplex", int.class, long.class, double.class)
                .invoke(instance, 10, 20L, 30.5);
        // 10 + 20 + 30.5 = 60.5
        assertEquals(60.5, supplier.getAsDouble(), 0.0001);
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var supplier = (IntSupplier) classA.getMethod("createComputer", int.class, int.class, int.class, int.class)
                .invoke(instance, 2, 3, 4, 5);
        // 2 * 3 + 4 * 5 = 6 + 20 = 26
        assertEquals(26, supplier.getAsInt());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createAdder", int.class).invoke(instance, 5);
        assertNotNull(fn);
        assertEquals(15, ((IntUnaryOperator) fn).applyAsInt(10));  // 10 + 5 = 15
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createGreeter", String.class).invoke(instance, "World");
        assertNotNull(fn);
        assertEquals("Hello, World", ((java.util.function.Supplier<?>) fn).get());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createValueGetter").invoke(instance);
        assertNotNull(fn);
        assertEquals(100, ((IntSupplier) fn).getAsInt());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("createAdder", int.class, int.class, int.class)
                .invoke(instance, 1, 2, 3);

        // x + 1 + 2 + 3 = x + 6
        assertEquals(
                List.of(6, 16, 106),
                List.of(fn.applyAsInt(0), fn.applyAsInt(10), fn.applyAsInt(100)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var combiner = (IntUnaryOperator) classA.getMethod("createCombiner", int.class, int.class)
                .invoke(instance, 10, 20);
        // x + a + b = x + 10 + 20 = x + 30
        assertEquals(
                List.of(30, 35, 40),
                List.of(combiner.applyAsInt(0), combiner.applyAsInt(5), combiner.applyAsInt(10)));
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var doubler = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(instance);
        var adder = (IntUnaryOperator) classA.getMethod("getAdder", int.class).invoke(instance, 10);
        assertEquals(
                List.of(10, 20, 15, 20),
                List.of(doubler.applyAsInt(5), doubler.applyAsInt(10), adder.applyAsInt(5), adder.applyAsInt(10)));
    }
}
