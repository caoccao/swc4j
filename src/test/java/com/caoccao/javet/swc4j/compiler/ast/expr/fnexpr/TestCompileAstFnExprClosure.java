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
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for function expression closure/capture behavior.
 * Covers: capture local variable, capture method param, capture multiple locals, capture and compute.
 */
public class TestCompileAstFnExprClosure extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprCaptureAndCompute(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createSupplier(a: int, b: int, c: int): IntSupplier {
                      return function(): int { return a + b + c }
                    }
                  }
                }""");
        var fn = (IntSupplier) runner.createInstanceRunner("com.A").invoke("createSupplier", 10, 20, 30);
        assertThat(fn.getAsInt()).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprCaptureLocalVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntUnaryOperator {
                      const offset: int = 100
                      const fn: IntUnaryOperator = function(x: int): int { return x + offset }
                      return fn
                    }
                  }
                }""");
        var fn = (IntUnaryOperator) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(
                List.of(fn.applyAsInt(1), fn.applyAsInt(5), fn.applyAsInt(10))
        ).isEqualTo(
                List.of(101, 105, 110)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprCaptureMethodParam(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createMultiplier(factor: int): IntUnaryOperator {
                      return function(x: int): int { return x * factor }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var mul3 = (IntUnaryOperator) instanceRunner.invoke("createMultiplier", 3);
        var mul7 = (IntUnaryOperator) instanceRunner.invoke("createMultiplier", 7);
        assertThat(
                List.of(mul3.applyAsInt(4), mul7.applyAsInt(4))
        ).isEqualTo(
                List.of(12, 28)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionExprCaptureMultipleLocals(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const a: int = 10
                      const b: int = 20
                      const c: int = 30
                      return function(): int { return a + b + c }
                    }
                  }
                }""");
        var fn = (IntSupplier) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(fn.getAsInt()).isEqualTo(60);
    }
}
