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
        assertThat(transformerInterface.isInterface()).isTrue();
        assertThat(transformerInterface.getMethod("transform", int.class)).isNotNull();

        // Arrow works with Java functional interface
        var calculatorRunner = runner.createInstanceRunner("com.Calculator");
        var doubler = calculatorRunner.invoke("getDoubler");
        assertThat(doubler).isNotNull();
        assertThat(((IntUnaryOperator) doubler).applyAsInt(5)).isEqualTo(10);
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

        assertThat(interfaceClass.isInterface()).isTrue();
        Method transformMethod = interfaceClass.getMethod("transform", int.class);
        assertThat(transformMethod).isNotNull();
        assertThat(transformMethod.getReturnType()).isEqualTo(int.class);
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var checkerRunner = runner.createInstanceRunner("com.PositiveChecker");
        Method testMethod = interfaceClass.getMethod("test", int.class);
        assertThat((boolean) checkerRunner.invoke("test", 5)).isTrue();
        assertThat((boolean) checkerRunner.invoke("test", -5)).isFalse();
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

        assertThat(interfaceClass.isInterface()).isTrue();
        assertThat(interfaceClass.isAssignableFrom(doublerClass)).isTrue();

        var doublerRunner = runner.createInstanceRunner("com.Doubler");
        Method transformMethod = interfaceClass.getMethod("transform", int.class);
        assertThat((int) doublerRunner.invoke("transform", 5)).isEqualTo(10);
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var squarerRunner = runner.createInstanceRunner("com.DoubleSquarer");
        Method transformMethod = interfaceClass.getMethod("transform", double.class);
        assertThat((double) squarerRunner.invoke("transform", 5.0)).isCloseTo(25.0, within(0.0001));
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var doublerRunner = runner.createInstanceRunner("com.LongDoubler");
        Method transformMethod = interfaceClass.getMethod("transform", long.class);
        assertThat((long) doublerRunner.invoke("transform", 10000000000L)).isEqualTo(20000000000L);
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var adderRunner = runner.createInstanceRunner("com.Adder");
        Method computeMethod = interfaceClass.getMethod("compute", int.class, int.class);
        assertThat((int) adderRunner.invoke("compute", 3, 5)).isEqualTo(8);
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var upperCaserRunner = runner.createInstanceRunner("com.UpperCaser");
        Method transformMethod = interfaceClass.getMethod("transform", String.class);
        assertThat((String) upperCaserRunner.invoke("transform", "hello")).isEqualTo("HELLO");
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

        assertThat(interfaceClass.isInterface()).isTrue();

        var setterRunner = runner.createInstanceRunner("com.ValueSetter");

        // Initially 0
        assertThat((int) setterRunner.invoke("getLastValue")).isEqualTo(0);

        // Call perform
        Method performMethod = interfaceClass.getMethod("perform", int.class);
        setterRunner.invoke("perform", 42);

        // Value updated
        assertThat((int) setterRunner.invoke("getLastValue")).isEqualTo(42);
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

        assertThat(supplierInterface.isInterface()).isTrue();
        assertThat(consumerInterface.isInterface()).isTrue();
        assertThat(transformerInterface.isInterface()).isTrue();

        assertThat(supplierInterface.getMethod("get")).isNotNull();
        assertThat(consumerInterface.getMethod("accept", int.class)).isNotNull();
        assertThat(transformerInterface.getMethod("transform", int.class)).isNotNull();
    }
}
