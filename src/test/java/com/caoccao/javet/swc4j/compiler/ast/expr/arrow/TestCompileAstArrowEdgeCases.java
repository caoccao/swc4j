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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for arrow expression edge cases.
 * Tests various edge cases from the implementation plan.
 */
public class TestCompileAstArrowEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowAsMethodReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 46: As return value
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAdder(x: int): IntUnaryOperator {
                      return (y: int) => x + y
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getAdder", int.class).invoke(instance, 10);
        assertEquals(15, fn.applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowAssignedToLocalVariable(JdkVersion jdkVersion) throws Exception {
        // Arrow assigned to local variable and returned
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var fn1 = (IntSupplier) classA.getMethod("getSupplier").invoke(instance);
        var fn2 = (IntSupplier) classA.getMethod("getComputedSupplier").invoke(instance);

        assertEquals(
                List.of(42, 20),
                List.of(fn1.getAsInt(), fn2.getAsInt()));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureMultipleVariables(JdkVersion jdkVersion) throws Exception {
        // Edge case 30: Capture multiple local variables
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntSupplier) classA.getMethod("test").invoke(instance);
        assertEquals(60, fn.getAsInt());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturePrimitiveAndObject(JdkVersion jdkVersion) throws Exception {
        // Test arrow capturing both primitive and object
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (Supplier<?>) classA.getMethod("test").invoke(instance);
        assertEquals("Value: 42", fn.get());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureStaticField(JdkVersion jdkVersion) throws Exception {
        // Edge case 39: Capture static field
        var map = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    static multiplier: int = 10
                    static getMultiplied(x: int): IntSupplier {
                      return () => x * A.multiplier
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var fn = (IntSupplier) classA.getMethod("getMultiplied", int.class).invoke(null, 5);
        assertEquals(50, fn.getAsInt());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCaptureThisAndLocalVars(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Capture both this and local variables
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntSupplier) classA.getMethod("compute", int.class).invoke(instance, 5);
        assertEquals(20, fn.getAsInt()); // 5 * 2 + 10 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturingBoolean(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing boolean values
        var map = getCompiler(jdkVersion).compile("""
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createSupplier(flag: boolean): BooleanSupplier {
                      return () => flag
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var trueFn = (BooleanSupplier) classA.getMethod("createSupplier", boolean.class).invoke(instance, true);
        var falseFn = (BooleanSupplier) classA.getMethod("createSupplier", boolean.class).invoke(instance, false);

        assertTrue(trueFn.getAsBoolean());
        assertFalse(falseFn.getAsBoolean());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowCapturingString(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing String and returning it
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var fn1 = (Supplier<?>) classA.getMethod("getString", String.class).invoke(instance, "hello");
        var fn2 = (Supplier<?>) classA.getMethod("getConcatenated", String.class, String.class)
                .invoke(instance, "Hello, ", "World!");

        assertEquals(
                List.of("hello", "Hello, World!"),
                List.of(fn1.get(), fn2.get()));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInConditionalExpression(JdkVersion jdkVersion) throws Exception {
        // Edge case 50: Arrow in conditional expression
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getOperator(isAdd: boolean): IntUnaryOperator {
                      return isAdd ? (x: int) => x + 10 : (x: int) => x - 10
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var addFn = (IntUnaryOperator) classA.getMethod("getOperator", boolean.class).invoke(instance, true);
        var subFn = (IntUnaryOperator) classA.getMethod("getOperator", boolean.class).invoke(instance, false);

        assertEquals(
                List.of(15, 25, -5, 5),
                List.of(addFn.applyAsInt(5), addFn.applyAsInt(15),
                        subFn.applyAsInt(5), subFn.applyAsInt(15)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInConstructor(JdkVersion jdkVersion) throws Exception {
        // Edge case 56: Arrow inside constructor - arrow assigned to field
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor(int.class).newInstance(21);
        var fn = (IntSupplier) classA.getMethod("getSupplier").invoke(instance);

        assertEquals(42, fn.getAsInt());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInFieldInitializer(JdkVersion jdkVersion) throws Exception {
        // Edge case 49: As field initializer (simplified - stores as field)
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    processor: IntUnaryOperator = (x: int) => x * 2
                    getProcessor(): IntUnaryOperator {
                      return this.processor
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getProcessor").invoke(instance);
        assertEquals(10, fn.applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowInStaticMethod(JdkVersion jdkVersion) throws Exception {
        // Edge case 57: Arrow inside static method
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    static getDoubler(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var fn = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(null);

        assertEquals(
                List.of(2, 10, 20),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowReassignment(JdkVersion jdkVersion) throws Exception {
        // Edge case 42: Arrow assigned to let variable (reassignment)
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var doublerFn = (IntUnaryOperator) classA.getMethod("compute", boolean.class).invoke(instance, true);
        var triplerFn = (IntUnaryOperator) classA.getMethod("compute", boolean.class).invoke(instance, false);

        assertEquals(
                List.of(10, 15),
                List.of(doublerFn.applyAsInt(5), triplerFn.applyAsInt(5)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithComplexCondition(JdkVersion jdkVersion) throws Exception {
        // Arrow with complex boolean condition
        var map = getCompiler(jdkVersion).compile("""
                import { IntPredicate } from 'java.util.function'
                namespace com {
                  export class A {
                    getInRangeChecker(min: int, max: int): IntPredicate {
                      return (x: int) => x >= min && x <= max
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntPredicate) classA.getMethod("getInRangeChecker", int.class, int.class)
                .invoke(instance, 10, 20);

        assertEquals(
                List.of(false, true, true, true, false),
                List.of(fn.test(5), fn.test(10), fn.test(15), fn.test(20), fn.test(25)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithMultiParamCapture(JdkVersion jdkVersion) throws Exception {
        // Arrow capturing multiple parameters
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(a: int, b: int): IntUnaryOperator {
                      return (x: int) => x + a + b
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("createAdder", int.class, int.class).invoke(instance, 10, 5);

        assertEquals(
                List.of(15, 16, 25, 115),
                List.of(fn.applyAsInt(0), fn.applyAsInt(1), fn.applyAsInt(10), fn.applyAsInt(100)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithNestedTernary(JdkVersion jdkVersion) throws Exception {
        // Test arrow with nested ternary
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getSign(): IntUnaryOperator {
                      return (x: int) => x > 0 ? 1 : x < 0 ? -1 : 0
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getSign").invoke(instance);
        assertEquals(1, fn.applyAsInt(5));
        assertEquals(-1, fn.applyAsInt(-5));
        assertEquals(0, fn.applyAsInt(0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureFromConstructor(JdkVersion jdkVersion) throws Exception {
        // Edge case 38: Capture from constructor
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance1 = classA.getConstructor(int.class).newInstance(42);
        var instance2 = classA.getConstructor(int.class).newInstance(100);

        var fn1 = (IntSupplier) classA.getMethod("getSupplier").invoke(instance1);
        var fn2 = (IntSupplier) classA.getMethod("getSupplier").invoke(instance2);

        assertEquals(
                List.of(42, 100),
                List.of(fn1.getAsInt(), fn2.getAsInt()));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsDifferentTypes(JdkVersion jdkVersion) throws Exception {
        // Multiple arrows with different functional interfaces
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var doubler = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(instance);
        var constant = (IntSupplier) classA.getMethod("getConstant").invoke(instance);
        var positive = (IntPredicate) classA.getMethod("getPositive").invoke(instance);

        assertEquals(
                List.of(10, 42, true, false),
                List.of(doubler.applyAsInt(5), constant.getAsInt(),
                        positive.test(5), positive.test(-5)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsInSameMethod(JdkVersion jdkVersion) throws Exception {
        // Multiple arrows in same method with different captures, returned as list
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var adder5 = (IntUnaryOperator) classA.getMethod("getAdder", int.class).invoke(instance, 5);
        var doubler = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(instance);
        var supplier5 = (IntSupplier) classA.getMethod("getSupplier", int.class).invoke(instance, 5);

        // supplier5() = 50, doubler(50) = 100, adder5(100) = 105
        int result = adder5.applyAsInt(doubler.applyAsInt(supplier5.getAsInt()));
        assertEquals(105, result);
    }
}
