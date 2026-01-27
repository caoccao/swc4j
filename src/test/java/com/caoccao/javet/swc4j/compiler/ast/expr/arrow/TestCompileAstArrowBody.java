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
 * Tests for arrow expression body types.
 * Tests various body patterns from the implementation plan edge cases.
 */
public class TestCompileAstArrowBody extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithBooleanExpression(JdkVersion jdkVersion) throws Exception {
        // Test arrow with boolean expression body
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var positiveFn = (IntPredicate) classA.getMethod("getPositive").invoke(instance);
        var evenFn = (IntPredicate) classA.getMethod("getEven").invoke(instance);

        assertTrue(positiveFn.test(5));
        assertFalse(positiveFn.test(-5));
        assertFalse(positiveFn.test(0));

        assertTrue(evenFn.test(4));
        assertFalse(evenFn.test(5));
        assertTrue(evenFn.test(0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithDoubleReturn(JdkVersion jdkVersion) throws Exception {
        // Test arrow with double return type
        var map = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return (x: double) => x * 2.5
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(12.5, ((DoubleUnaryOperator) fn).applyAsDouble(5.0), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyConditionalReturns(JdkVersion jdkVersion) throws Exception {
        // Edge case 26: Block body - conditional returns
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getClamp(): IntUnaryOperator {
                      return (x: int) => {
                        if (x < 0) return 0
                        if (x > 100) return 100
                        return x
                      }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getClamp").invoke(instance);

        assertEquals(
                List.of(0, 0, 50, 100, 100),
                List.of(fn.applyAsInt(-10), fn.applyAsInt(0), fn.applyAsInt(50),
                        fn.applyAsInt(100), fn.applyAsInt(150)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyEmpty(JdkVersion jdkVersion) throws Exception {
        // Edge case 22: Block body empty (void return)
        var map = getCompiler(jdkVersion).compile("""
                import { Runnable } from 'java.lang'
                namespace com {
                  export class A {
                    get(): Runnable {
                      return () => {}
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        // Just verify it doesn't throw
        ((Runnable) fn).run();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyForLoop(JdkVersion jdkVersion) throws Exception {
        // Edge case 27 (extended): Block body with for loop
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getSum(): IntUnaryOperator {
                      return (n: int) => {
                        let sum: int = 0
                        for (let i: int = 1; i <= n; i = i + 1) {
                          sum = sum + i
                        }
                        return sum
                      }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getSum").invoke(instance);

        assertEquals(
                List.of(0, 1, 3, 6, 10, 55),
                List.of(fn.applyAsInt(0), fn.applyAsInt(1), fn.applyAsInt(2),
                        fn.applyAsInt(3), fn.applyAsInt(4), fn.applyAsInt(10)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyMultipleStatements(JdkVersion jdkVersion) throws Exception {
        // Edge case 24: Block body - multiple statements
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getComplex(): IntUnaryOperator {
                      return (x: int) => {
                        const doubled: int = x * 2
                        const incremented: int = doubled + 1
                        const result: int = incremented * 3
                        return result
                      }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getComplex").invoke(instance);

        // x=5: doubled=10, incremented=11, result=33
        assertEquals(
                List.of(3, 33, 63),
                List.of(fn.applyAsInt(0), fn.applyAsInt(5), fn.applyAsInt(10)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyNoReturnVoid(JdkVersion jdkVersion) throws Exception {
        // Edge case 25: Block body - no return (void)
        var map = getCompiler(jdkVersion).compile("""
                import { IntConsumer } from 'java.util.function'
                namespace com {
                  export class A {
                    getConsumer(): IntConsumer {
                      return (x: int) => {
                        let temp: int = x * 2
                        temp = temp + 1
                      }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntConsumer) classA.getMethod("getConsumer").invoke(instance);
        // Just verify it doesn't throw - no observable side effect in this simple version
        fn.accept(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodySingleReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 23: Block body with single return
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => { return x * 2 }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(10, ((IntUnaryOperator) fn).applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyWithLoop(JdkVersion jdkVersion) throws Exception {
        // Edge case 27: Block body with loop
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getFactorial(): IntUnaryOperator {
                      return (n: int) => {
                        let result: int = 1
                        let i: int = 1
                        while (i <= n) {
                          result = result * i
                          i = i + 1
                        }
                        return result
                      }
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("getFactorial").invoke(instance);
        assertNotNull(fn);
        assertEquals(1, ((IntUnaryOperator) fn).applyAsInt(0));
        assertEquals(1, ((IntUnaryOperator) fn).applyAsInt(1));
        assertEquals(6, ((IntUnaryOperator) fn).applyAsInt(3));
        assertEquals(120, ((IntUnaryOperator) fn).applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyBinaryOperation(JdkVersion jdkVersion) throws Exception {
        // Edge case 19: Expression body - binary operation
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getCompute(): IntUnaryOperator {
                      return (x: int) => x + x * 2
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (IntUnaryOperator) classA.getMethod("getCompute").invoke(instance);

        assertEquals(
                List.of(3, 15, 30),
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyMethodCall(JdkVersion jdkVersion) throws Exception {
        // Edge case 20: Expression body - method call on captured String
        var map = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getValue(): Supplier {
                      const s: String = "hello"
                      return () => s
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = (Supplier<?>) classA.getMethod("getValue").invoke(instance);

        assertEquals("hello", fn.get());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyPrimitiveReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 15: Expression body with primitive return
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(10, ((IntUnaryOperator) fn).applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyTernary(JdkVersion jdkVersion) throws Exception {
        // Edge case 18: Expression body - ternary
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAbs(): IntUnaryOperator {
                      return (x: int) => x > 0 ? x : -x
                    }
                    getMax(): IntUnaryOperator {
                      return (x: int) => x > 100 ? 100 : x
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var absFn = (IntUnaryOperator) classA.getMethod("getAbs").invoke(instance);
        var maxFn = (IntUnaryOperator) classA.getMethod("getMax").invoke(instance);

        assertEquals(
                List.of(5, 5, 0),
                List.of(absFn.applyAsInt(5), absFn.applyAsInt(-5), absFn.applyAsInt(0)));
        assertEquals(
                List.of(50, 100, 100),
                List.of(maxFn.applyAsInt(50), maxFn.applyAsInt(100), maxFn.applyAsInt(150)));
    }
}
