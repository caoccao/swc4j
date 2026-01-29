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
        var instanceRunner = runner.createInstanceRunner("com.A");

        var positiveFn = (IntPredicate) instanceRunner.invoke("getPositive");
        var evenFn = (IntPredicate) instanceRunner.invoke("getEven");

        assertThat(positiveFn.test(5)).isTrue();
        assertThat(positiveFn.test(-5)).isFalse();
        assertThat(positiveFn.test(0)).isFalse();

        assertThat(evenFn.test(4)).isTrue();
        assertThat(evenFn.test(5)).isFalse();
        assertThat(evenFn.test(0)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithDoubleReturn(JdkVersion jdkVersion) throws Exception {
        // Test arrow with double return type
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleUnaryOperator {
                      return (x: double) => x * 2.5
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        assertThat(((DoubleUnaryOperator) fn).applyAsDouble(5.0)).isCloseTo(12.5, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyConditionalReturns(JdkVersion jdkVersion) throws Exception {
        // Edge case 26: Block body - conditional returns
        var runner = getCompiler(jdkVersion).compile("""
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
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("getClamp");

        assertThat(
                List.of(
                        fn.applyAsInt(-10), fn.applyAsInt(0), fn.applyAsInt(50),
                        fn.applyAsInt(100), fn.applyAsInt(150)
                )
        ).isEqualTo(
                List.of(0, 0, 50, 100, 100)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyEmpty(JdkVersion jdkVersion) throws Exception {
        // Edge case 22: Block body empty (void return)
        var runner = getCompiler(jdkVersion).compile("""
                import { Runnable } from 'java.lang'
                namespace com {
                  export class A {
                    get(): Runnable {
                      return () => {}
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        // Just verify it doesn't throw
        ((Runnable) fn).run();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyForLoop(JdkVersion jdkVersion) throws Exception {
        // Edge case 27 (extended): Block body with for loop
        var runner = getCompiler(jdkVersion).compile("""
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
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("getSum");

        assertThat(
                List.of(
                        fn.applyAsInt(0), fn.applyAsInt(1), fn.applyAsInt(2),
                        fn.applyAsInt(3), fn.applyAsInt(4), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(0, 1, 3, 6, 10, 55)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyMultipleStatements(JdkVersion jdkVersion) throws Exception {
        // Edge case 24: Block body - multiple statements
        var runner = getCompiler(jdkVersion).compile("""
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
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("getComplex");

        // x=5: doubled=10, incremented=11, result=33
        assertThat(
                List.of(
                        fn.applyAsInt(0), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(3, 33, 63)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyNoReturnVoid(JdkVersion jdkVersion) throws Exception {
        // Edge case 25: Block body - no return (void)
        var runner = getCompiler(jdkVersion).compile("""
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
        var fn = (IntConsumer) runner.createInstanceRunner("com.A").invoke("getConsumer");
        // Just verify it doesn't throw - no observable side effect in this simple version
        fn.accept(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodySingleReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 23: Block body with single return
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => { return x * 2 }
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        assertThat(((IntUnaryOperator) fn).applyAsInt(5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBlockBodyWithLoop(JdkVersion jdkVersion) throws Exception {
        // Edge case 27: Block body with loop
        var runner = getCompiler(jdkVersion).compile("""
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
        var fn = runner.createInstanceRunner("com.A").invoke("getFactorial");
        assertThat(fn).isNotNull();
        assertThat(((IntUnaryOperator) fn).applyAsInt(0)).isEqualTo(1);
        assertThat(((IntUnaryOperator) fn).applyAsInt(1)).isEqualTo(1);
        assertThat(((IntUnaryOperator) fn).applyAsInt(3)).isEqualTo(6);
        assertThat(((IntUnaryOperator) fn).applyAsInt(5)).isEqualTo(120);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyBinaryOperation(JdkVersion jdkVersion) throws Exception {
        // Edge case 19: Expression body - binary operation
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getCompute(): IntUnaryOperator {
                      return (x: int) => x + x * 2
                    }
                  }
                }""");
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("getCompute");

        assertThat(
                List.of(
                        fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10)
                )
        ).isEqualTo(
                List.of(3, 15, 30)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyMethodCall(JdkVersion jdkVersion) throws Exception {
        // Edge case 20: Expression body - method call on captured String
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    getValue(): Supplier {
                      const s: String = "hello"
                      return () => s
                    }
                  }
                }""");
        var fn = (Supplier<?>) runner.createInstanceRunner("com.A").invoke("getValue");

        assertThat(fn.get()).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyPrimitiveReturn(JdkVersion jdkVersion) throws Exception {
        // Edge case 15: Expression body with primitive return
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        var fn = runner.createInstanceRunner("com.A").invoke("get");
        assertThat(fn).isNotNull();
        assertThat(((IntUnaryOperator) fn).applyAsInt(5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExpressionBodyTernary(JdkVersion jdkVersion) throws Exception {
        // Edge case 18: Expression body - ternary
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.A");

        var absFn = (IntUnaryOperator) instanceRunner.invoke("getAbs");
        var maxFn = (IntUnaryOperator) instanceRunner.invoke("getMax");

        assertThat(
                List.of(
                        absFn.applyAsInt(5), absFn.applyAsInt(-5), absFn.applyAsInt(0)
                )
        ).isEqualTo(
                List.of(5, 5, 0)
        );
        assertThat(
                List.of(
                        maxFn.applyAsInt(50), maxFn.applyAsInt(100), maxFn.applyAsInt(150)
                )
        ).isEqualTo(
                List.of(50, 100, 100)
        );
    }
}
