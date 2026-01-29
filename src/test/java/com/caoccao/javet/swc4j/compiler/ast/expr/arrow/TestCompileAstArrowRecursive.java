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

import java.util.function.IntUnaryOperator;


/**
 * Tests for recursive arrow expressions.
 * Recursive arrows reference themselves by name (e.g., factorial, fibonacci).
 * Implementation uses self-referencing captured variables with non-final fields
 * that are updated after instantiation.
 */
public class TestCompileAstArrowRecursive extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveFactorial(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow: factorial using ternary expression
        // Uses direct call syntax: factorial(n - 1) instead of factorial.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const factorial: IntUnaryOperator = (n: int) =>
                        n <= 1 ? 1 : n * factorial(n - 1)
                      return factorial(5)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(120);  // 5! = 120
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveFactorialReturnsLambda(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow returned from method
        // Uses direct call syntax: factorial(n - 1) instead of factorial.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getFactorial(): IntUnaryOperator {
                      const factorial: IntUnaryOperator = (n: int) =>
                        n <= 1 ? 1 : n * factorial(n - 1)
                      return factorial
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("getFactorial");
        assertThat(result).isNotNull();
        IntUnaryOperator factorial = (IntUnaryOperator) result;
        assertThat(factorial.applyAsInt(0)).isEqualTo(1);
        assertThat(factorial.applyAsInt(1)).isEqualTo(1);
        assertThat(factorial.applyAsInt(2)).isEqualTo(2);
        assertThat(factorial.applyAsInt(3)).isEqualTo(6);
        assertThat(factorial.applyAsInt(4)).isEqualTo(24);
        assertThat(factorial.applyAsInt(5)).isEqualTo(120);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveFibonacci(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow: fibonacci using block body
        // Uses direct call syntax: fib(n - 1) instead of fib.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const fib: IntUnaryOperator = (n: int) => {
                        if (n <= 1) {
                          return n
                        }
                        const a: int = fib(n - 1)
                        const b: int = fib(n - 2)
                        return a + b
                      }
                      return fib(10)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(55);  // fib(10) = 55
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveFibonacciReturnsLambda(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow returned from method
        // Uses direct call syntax: fib(n - 1) instead of fib.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getFibonacci(): IntUnaryOperator {
                      const fib: IntUnaryOperator = (n: int) => {
                        if (n <= 1) {
                          return n
                        }
                        const a: int = fib(n - 1)
                        const b: int = fib(n - 2)
                        return a + b
                      }
                      return fib
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("getFibonacci");
        assertThat(result).isNotNull();
        IntUnaryOperator fib = (IntUnaryOperator) result;
        assertThat(fib.applyAsInt(0)).isEqualTo(0);
        assertThat(fib.applyAsInt(1)).isEqualTo(1);
        assertThat(fib.applyAsInt(2)).isEqualTo(1);
        assertThat(fib.applyAsInt(3)).isEqualTo(2);
        assertThat(fib.applyAsInt(4)).isEqualTo(3);
        assertThat(fib.applyAsInt(5)).isEqualTo(5);
        assertThat(fib.applyAsInt(6)).isEqualTo(8);
        assertThat(fib.applyAsInt(7)).isEqualTo(13);
        assertThat(fib.applyAsInt(8)).isEqualTo(21);
        assertThat(fib.applyAsInt(9)).isEqualTo(34);
        assertThat(fib.applyAsInt(10)).isEqualTo(55);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveSum(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow: sum 1 to n
        // Uses direct call syntax: sum(n - 1) instead of sum.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const sum: IntUnaryOperator = (n: int) => {
                        if (n <= 0) {
                          return 0
                        }
                        return n + sum(n - 1)
                      }
                      return sum(10)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(55);  // 1+2+3+...+10 = 55
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveWithCapture(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow with additional captured variable
        // Uses direct call syntax: recMul(n - 1) instead of recMul.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const multiplier: int = 2
                      const recMul: IntUnaryOperator = (n: int) => {
                        if (n <= 0) {
                          return 0
                        }
                        return multiplier + recMul(n - 1)
                      }
                      return recMul(5)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(10);  // 2+2+2+2+2 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveWithThisCapture(JdkVersion jdkVersion) throws Exception {
        // Recursive arrow with 'this' capture
        // Uses direct call syntax: recSum(n - 1) instead of recSum.applyAsInt(n - 1)
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    base: int = 1
                    test(): int {
                      const recSum: IntUnaryOperator = (n: int) => {
                        if (n <= 0) {
                          return this.base
                        }
                        return n + recSum(n - 1)
                      }
                      return recSum(5)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(16);  // 5+4+3+2+1+1(base) = 16
    }
}
