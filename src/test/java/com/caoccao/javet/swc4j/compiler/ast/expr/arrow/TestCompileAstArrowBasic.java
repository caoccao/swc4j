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

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;


/**
 * Basic tests for arrow expressions.
 * Tests Phases 1-2 from the implementation plan.
 */
public class TestCompileAstArrowBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowBlockBodyWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntUnaryOperator {
                      const fn: IntUnaryOperator = (x: int) => {
                        const doubled: int = x * 2
                        const plusTen: int = doubled + 10
                        return plusTen
                      }
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((IntUnaryOperator) result).applyAsInt(5)).isEqualTo(20);  // 5 * 2 + 10 = 20
        assertThat(((IntUnaryOperator) result).applyAsInt(0)).isEqualTo(10);  // 0 * 2 + 10 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowBlockBodyWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const fn: IntSupplier = () => {
                        return 100
                      }
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowExpressionBodyWithIntParam(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntUnaryOperator {
                      const fn: IntUnaryOperator = (x: int) => x * 2
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((IntUnaryOperator) result).applyAsInt(5)).isEqualTo(10);
        assertThat(((IntUnaryOperator) result).applyAsInt(0)).isEqualTo(0);
        assertThat(((IntUnaryOperator) result).applyAsInt(-3)).isEqualTo(-6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowExpressionBodyWithIntReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const fn: IntSupplier = () => 42
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithStringReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Supplier {
                      const fn: Supplier = () => "hello"
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((Supplier<?>) result).get()).isEqualTo("hello");
    }
}
