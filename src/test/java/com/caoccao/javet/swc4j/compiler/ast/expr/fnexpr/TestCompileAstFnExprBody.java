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

package com.caoccao.javet.swc4j.compiler.ast.expr.fnexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for function expression body variations.
 * Covers: block body with multiple statements, conditional return, multiple in same method/class,
 * ternary return, return type widening.
 */
public class TestCompileAstFnExprBody extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprBlockBodyMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return function(x: int): int {
                        const doubled: int = x * 2
                        const plusTen: int = doubled + 10
                        return plusTen
                      }
                    }
                  }
                }""");
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(
                List.of(fn.applyAsInt(0), fn.applyAsInt(5), fn.applyAsInt(10))
        ).isEqualTo(
                List.of(10, 20, 30)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprConditionalReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    clamp(): IntUnaryOperator {
                      return function(x: int): int {
                        if (x < 0) return 0
                        if (x > 100) return 100
                        return x
                      }
                    }
                  }
                }""");
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("clamp");
        assertThat(
                List.of(fn.applyAsInt(-10), fn.applyAsInt(0), fn.applyAsInt(50), fn.applyAsInt(100), fn.applyAsInt(150))
        ).isEqualTo(
                List.of(0, 0, 50, 100, 100)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprMultipleInSameClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getIncrementer(): IntUnaryOperator {
                      return function(x: int): int { return x + 1 }
                    }
                    getDoubler(): IntUnaryOperator {
                      return function(x: int): int { return x * 2 }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var inc = (IntUnaryOperator) instanceRunner.invoke("getIncrementer");
        var dbl = (IntUnaryOperator) instanceRunner.invoke("getDoubler");
        assertThat(
                List.of(inc.applyAsInt(5), dbl.applyAsInt(5))
        ).isEqualTo(
                List.of(6, 10)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprMultipleInSameMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const add1: IntUnaryOperator = function(x: int): int { return x + 1 }
                      const dbl: IntUnaryOperator = function(x: int): int { return x * 2 }
                      return [add1(5), dbl(5)]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(6, 10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprReturnTypeWidening(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongUnaryOperator {
                      return function(x: long): long { return x + 1 }
                    }
                  }
                }""");
        var fn = (LongUnaryOperator) runner.createInstanceRunner("com.A").invoke("get");
        assertThat(
                List.of(fn.applyAsLong(1L), fn.applyAsLong(100L), fn.applyAsLong(1000L))
        ).isEqualTo(
                List.of(2L, 101L, 1001L)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprTernaryReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    abs(): IntUnaryOperator {
                      return function(x: int): int { return x >= 0 ? x : -x }
                    }
                  }
                }""");
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("abs");
        assertThat(
                List.of(fn.applyAsInt(5), fn.applyAsInt(-5), fn.applyAsInt(0))
        ).isEqualTo(
                List.of(5, 5, 0)
        );
    }
}
