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

import java.lang.reflect.Method;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom TypeScript interfaces combined with arrow expressions.
 * Tests that custom interfaces compile correctly and can work alongside arrow functions.
 */
public class TestCompileAstArrowCustomInterface extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowsWithJavaFunctionalInterfaceAlongsideCustomInterface(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface compiled alongside arrows using java.util.function
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export interface MyTransformer {
                    transform(x: int): int
                  }
                  export class Calculator {
                    getDoubler(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");

        // Custom interface is compiled
        Class<?> transformerInterface = runner.getClass("com.MyTransformer");
        assertTrue(transformerInterface.isInterface());
        assertNotNull(transformerInterface.getMethod("transform", int.class));

        // Arrow works with Java functional interface
        Class<?> calculatorClass = runner.getClass("com.Calculator");
        var calculator = calculatorClass.getConstructor().newInstance();
        var doubler = calculatorClass.getMethod("getDoubler").invoke(calculator);
        assertNotNull(doubler);
        assertEquals(10, ((IntUnaryOperator) doubler).applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceCompiles(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface compiles correctly with getter/setter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.IntTransformer");

        assertTrue(interfaceClass.isInterface());
        Method transformMethod = interfaceClass.getMethod("transform", int.class);
        assertNotNull(transformMethod);
        assertEquals(int.class, transformMethod.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithBooleanReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with boolean return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntPredicate {
                    test(x: int): boolean
                  }
                  export class PositiveChecker implements IntPredicate {
                    test(x: int): boolean {
                      return x > 0
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.IntPredicate");
        Class<?> checkerClass = runner.getClass("com.PositiveChecker");

        assertTrue(interfaceClass.isInterface());

        var checker = checkerClass.getConstructor().newInstance();
        Method testMethod = interfaceClass.getMethod("test", int.class);
        assertTrue((boolean) testMethod.invoke(checker, 5));
        assertFalse((boolean) testMethod.invoke(checker, -5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithClassImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface implemented by a class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                  export class Doubler implements IntTransformer {
                    transform(x: int): int {
                      return x * 2
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.IntTransformer");
        Class<?> doublerClass = runner.getClass("com.Doubler");

        assertTrue(interfaceClass.isInterface());
        assertTrue(interfaceClass.isAssignableFrom(doublerClass));

        var doubler = doublerClass.getConstructor().newInstance();
        Method transformMethod = interfaceClass.getMethod("transform", int.class);
        assertEquals(10, transformMethod.invoke(doubler, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithDoubleParam(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with double parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DoubleTransformer {
                    transform(x: double): double
                  }
                  export class DoubleSquarer implements DoubleTransformer {
                    transform(x: double): double {
                      return x * x
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.DoubleTransformer");
        Class<?> squarerClass = runner.getClass("com.DoubleSquarer");

        assertTrue(interfaceClass.isInterface());

        var squarer = squarerClass.getConstructor().newInstance();
        Method transformMethod = interfaceClass.getMethod("transform", double.class);
        assertEquals(25.0, (double) transformMethod.invoke(squarer, 5.0), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithLongParam(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with long parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface LongTransformer {
                    transform(x: long): long
                  }
                  export class LongDoubler implements LongTransformer {
                    transform(x: long): long {
                      return x * 2
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.LongTransformer");
        Class<?> doublerClass = runner.getClass("com.LongDoubler");

        assertTrue(interfaceClass.isInterface());

        var doubler = doublerClass.getConstructor().newInstance();
        Method transformMethod = interfaceClass.getMethod("transform", long.class);
        assertEquals(20000000000L, transformMethod.invoke(doubler, 10000000000L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithMultipleParams(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with multiple parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface BinaryOp {
                    compute(a: int, b: int): int
                  }
                  export class Adder implements BinaryOp {
                    compute(a: int, b: int): int {
                      return a + b
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.BinaryOp");
        Class<?> adderClass = runner.getClass("com.Adder");

        assertTrue(interfaceClass.isInterface());

        var adder = adderClass.getConstructor().newInstance();
        Method computeMethod = interfaceClass.getMethod("compute", int.class, int.class);
        assertEquals(8, computeMethod.invoke(adder, 3, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithStringParam(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with String parameter and return
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringTransformer {
                    transform(s: String): String
                  }
                  export class UpperCaser implements StringTransformer {
                    transform(s: String): String {
                      return s.toUpperCase()
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.StringTransformer");
        Class<?> upperCaserClass = runner.getClass("com.UpperCaser");

        assertTrue(interfaceClass.isInterface());

        var upperCaser = upperCaserClass.getConstructor().newInstance();
        Method transformMethod = interfaceClass.getMethod("transform", String.class);
        assertEquals("HELLO", transformMethod.invoke(upperCaser, "hello"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithVoidReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Custom interface with void return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntAction {
                    perform(x: int): void
                  }
                  export class ValueSetter implements IntAction {
                    lastValue: int = 0
                    perform(x: int): void {
                      this.lastValue = x
                    }
                    getLastValue(): int {
                      return this.lastValue
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.IntAction");
        Class<?> setterClass = runner.getClass("com.ValueSetter");

        assertTrue(interfaceClass.isInterface());

        var setter = setterClass.getConstructor().newInstance();

        // Initially 0
        assertEquals(0, setterClass.getMethod("getLastValue").invoke(setter));

        // Call perform
        Method performMethod = interfaceClass.getMethod("perform", int.class);
        performMethod.invoke(setter, 42);

        // Value updated
        assertEquals(42, setterClass.getMethod("getLastValue").invoke(setter));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfacesMultiple(JdkVersion jdkVersion) throws Exception {
        // Test: Multiple custom interfaces compile correctly
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntSupplier {
                    get(): int
                  }
                  export interface IntConsumer {
                    accept(x: int): void
                  }
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                }""");

        Class<?> supplierInterface = runner.getClass("com.IntSupplier");
        Class<?> consumerInterface = runner.getClass("com.IntConsumer");
        Class<?> transformerInterface = runner.getClass("com.IntTransformer");

        assertTrue(supplierInterface.isInterface());
        assertTrue(consumerInterface.isInterface());
        assertTrue(transformerInterface.isInterface());

        assertNotNull(supplierInterface.getMethod("get"));
        assertNotNull(consumerInterface.getMethod("accept", int.class));
        assertNotNull(transformerInterface.getMethod("transform", int.class));
    }
}
