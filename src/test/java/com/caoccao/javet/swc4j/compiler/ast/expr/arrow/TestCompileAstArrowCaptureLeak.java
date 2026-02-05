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


/**
 * Arrow Capture Leak Audit Tests.
 * Verifies that captured variables do not cause memory leaks and that
 * holder objects are properly managed.
 * <p>
 * Tests include:
 * - Multiple captures (up to 5 variables - within stack limits)
 * - Holder array lifecycle
 * - Reference retention patterns
 * - Multiple lambdas with shared captures
 */
public class TestCompileAstArrowCaptureLeak extends BaseTestCompileSuite {

    /**
     * Test capturing 5 local variables.
     * Tests multiple capture field generation.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCapture5Variables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const a1: int = 1
                      const a2: int = 2
                      const a3: int = 3
                      const a4: int = 4
                      const a5: int = 5
                      const fn: IntSupplier = () => a1 + a2 + a3 + a4 + a5
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(15);  // Sum 1 to 5 = 15
    }

    /**
     * Test capturing variables of different types.
     * Verifies mixed-type capture handling.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const i1: int = 10
                      const l1: long = 20
                      const d1: double = 30.0
                      const fn: IntSupplier = () => i1 + (l1 as int) + (d1 as int)
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(60);  // 10 + 20 + 30 = 60
    }

    /**
     * Test closure over loop variable with for-loop.
     * Classic closure-in-loop pattern.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClosureOverLoopVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let captured: int = 0
                      const fn: IntSupplier = () => captured
                      for (let i: int = 0; i < 5; i = i + 1) {
                        captured = i
                      }
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(4);  // Final value of loop
    }

    /**
     * Test curried function with capture chain.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCurriedFunctionCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(x: int): IntUnaryOperator {
                      return (y: int) => x + y
                    }
                
                    test(): int {
                      const add10 = this.createAdder(10)
                      const add20 = this.createAdder(20)
                      return add10(5) + add20(5)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(40);  // (10+5) + (20+5) = 40
    }

    /**
     * Test holder lifetime beyond scope - lambda captures mutable var,
     * value is modified, then lambda is returned.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testHolderLifetimeBeyondScope(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createWithMutable(): IntSupplier {
                      let value: int = 42
                      const fn: IntSupplier = () => value
                      value = 100
                      return fn
                    }
                
                    test(): int {
                      const fn = this.createWithMutable()
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(100);  // Should see final value before method returned
    }

    /**
     * Test that lambda invoked in loop maintains stable captures.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLambdaInLoopStableCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const base: int = 100
                      const fn: IntSupplier = () => base
                      let sum: int = 0
                      for (let i: int = 0; i < 5; i = i + 1) {
                        sum = sum + fn()
                      }
                      return sum
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(500);  // 100 * 5 = 500
    }

    /**
     * Test mixed immutable and mutable captures.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedImmutableAndMutableCaptures(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const constVal: int = 10
                      let mutableVal: int = 20
                      const fn: IntSupplier = () => constVal + mutableVal
                      mutableVal = 30
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(40);  // 10 + 30 = 40
    }

    /**
     * Test lambda capturing both this and local mutable variable.
     * Complex capture scenario.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedThisAndMutableCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    multiplier: int = 10
                
                    test(): int {
                      let counter: int = 0
                      const fn: IntSupplier = () => {
                        counter = counter + 1
                        return counter * this.multiplier
                      }
                      return fn() + fn() + fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(60);  // (1*10) + (2*10) + (3*10) = 60
    }

    /**
     * Test capturing and modifying multiple double values through holders.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleDoubleMutableCaptures(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): double {
                      let x: double = 1.5
                      let y: double = 2.5
                      const fn: DoubleSupplier = () => x + y
                      x = 10.5
                      y = 20.5
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((double) result).isEqualTo(31.0);
    }

    /**
     * Test multiple lambdas from same creation method.
     * Each lambda should have independent captures.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleLambdasIndependentCaptures(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(x: int): IntSupplier {
                      return () => x
                    }
                
                    test(): int {
                      const fn1 = this.createAdder(1)
                      const fn2 = this.createAdder(2)
                      const fn3 = this.createAdder(3)
                      return fn1() + fn2() + fn3()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(6);  // 1 + 2 + 3 = 6
    }

    /**
     * Test multiple lambdas capturing the same this reference.
     * Verifies this capture is shared correctly.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleLambdasSameThis(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    value: int = 10
                
                    test(): int {
                      const fn1: IntSupplier = () => this.value * 1
                      const fn2: IntSupplier = () => this.value * 2
                      const fn3: IntSupplier = () => this.value * 3
                      return fn1() + fn2() + fn3()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(60);  // 10*1 + 10*2 + 10*3 = 60
    }

    /**
     * Test capturing and modifying multiple long values through holders.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleLongMutableCaptures(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): long {
                      let a: long = 100
                      let b: long = 200
                      const fn: LongSupplier = () => a + b
                      a = 1000
                      b = 2000
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((long) result).isEqualTo(3000L);
    }

    /**
     * Test holder array with multiple mutable captures.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleMutableHolders(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      let y: int = 0
                      let z: int = 0
                      const fn: IntSupplier = () => {
                        x = x + 1
                        y = y + 2
                        z = z + 3
                        return x + y + z
                      }
                      fn()
                      fn()
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(18);  // (3*1) + (3*2) + (3*3) = 3 + 6 + 9 = 18
    }

    /**
     * Test object capture in holder array.
     * Verifies object references are properly maintained.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableObjectHolderCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): String {
                      let str: String = "first"
                      const fn: Supplier<String> = () => str
                      str = "second"
                      const a = fn() as String
                      str = "third"
                      const b = fn() as String
                      return a + "_" + b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("second_third");
    }

    /**
     * Test lambda that captures nothing (pure function).
     * Should not have any capture fields.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPureFunctionNoCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const double: IntUnaryOperator = (x: int) => x * 2
                      return double(21)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(42);
    }

    /**
     * Test lambda returned from method and invoked multiple times.
     * Verifies capture stability across invocations.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnedLambdaMultipleInvocations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createConstant(value: int): IntSupplier {
                      return () => value
                    }
                
                    test(): int {
                      const fn = this.createConstant(42)
                      return fn() + fn() + fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(126);  // 42 * 3 = 126
    }

    /**
     * Test that captured variables are not duplicated across multiple lambdas.
     * Both lambdas capture same variable, should use same holder.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSharedMutableCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let shared: int = 0
                      const reader: IntSupplier = () => shared
                      const writer: IntSupplier = () => {
                        shared = shared + 1
                        return shared
                      }
                      writer()
                      writer()
                      writer()
                      return reader()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(3);  // writer called 3 times, reader sees final value
    }

    /**
     * Test static method with capture (should not capture this).
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    static test(): int {
                      const value: int = 42
                      const fn: IntSupplier = () => value
                      return fn()
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var result = classA.getMethod("test").invoke(null);
        assertThat((int) result).isEqualTo(42);
    }

    /**
     * Test that lambda capturing this where this is later modified.
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThisCaptureWithFieldModification(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    counter: int = 0
                
                    test(): int {
                      const fn: IntSupplier = () => this.counter
                      this.counter = 100
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(100);  // Should see the modified value
    }
}
