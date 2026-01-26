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
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
