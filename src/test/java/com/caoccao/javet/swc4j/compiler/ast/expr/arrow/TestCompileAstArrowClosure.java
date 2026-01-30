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

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for arrow expression closures (variable capture).
 * Tests Phase 2 from the implementation plan.
 */
public class TestCompileAstArrowClosure extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureBothParamAndLocal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    compute(base: int): IntSupplier {
                      const multiplier: int = 10
                      return () => base * multiplier
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("compute", 5);
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(50);  // 5 * 10 = 50
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureLocalVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const x: int = 42
                      const fn: IntSupplier = () => x
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
    public void testCaptureMethodParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(offset: int): IntUnaryOperator {
                      return (x: int) => x + offset
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("createAdder", 100);
        assertThat(result).isNotNull();
        assertThat(((IntUnaryOperator) result).applyAsInt(5)).isEqualTo(105);   // 5 + 100 = 105
        assertThat(((IntUnaryOperator) result).applyAsInt(10)).isEqualTo(110);  // 10 + 100 = 110
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureMultipleLocalVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): IntSupplier {
                      const x: int = 10
                      const y: int = 20
                      const z: int = 30
                      const fn: IntSupplier = () => x + y + z
                      return fn
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(60);  // 10 + 20 + 30 = 60
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureThisAndLocal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = runner.createInstanceRunner("com.A").invoke("compute", 5);
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(20);  // 5 * 2 + 10 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCaptureThisFieldAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    value: int = 42
                    getValueFn(): IntSupplier {
                      return () => this.value
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("getValueFn");
        assertThat(result).isNotNull();
        assertThat(((IntSupplier) result).getAsInt()).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleArrowsSameScope(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getAddOne(): IntUnaryOperator {
                      return (n: int) => n + 1
                    }
                    getDouble(): IntUnaryOperator {
                      return (n: int) => n * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var addOne = instanceRunner.invoke("getAddOne");
        var dbl = instanceRunner.invoke("getDouble");
        assertThat(addOne).isNotNull();
        assertThat(dbl).isNotNull();
        assertThat(((IntUnaryOperator) addOne).applyAsInt(5)).isEqualTo(6);   // 5 + 1 = 6
        assertThat(((IntUnaryOperator) dbl).applyAsInt(5)).isEqualTo(10);     // 5 * 2 = 10
    }
}
