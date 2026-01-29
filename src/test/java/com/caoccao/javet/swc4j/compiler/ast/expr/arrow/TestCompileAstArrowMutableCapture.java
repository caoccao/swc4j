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
import java.util.function.IntSupplier;


/**
 * Tests for mutable variable capture in arrow expressions.
 * Mutable captures use holder objects (arrays) to allow modification after capture.
 * Edge case 34 from the implementation plan.
 */
public class TestCompileAstArrowMutableCapture extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableBooleanCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): boolean {
                      let x: boolean = false
                      const fn: BooleanSupplier = () => x
                      x = true
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((boolean) result).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableCaptureInLoop(JdkVersion jdkVersion) throws Exception {
        // Edge case 35: Capture Loop Variable
        // Tests that mutable loop variable is captured correctly through holder
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let total: int = 0
                      const fn: IntSupplier = () => total
                      total = 10
                      const a = fn()
                      total = 20
                      const b = fn()
                      total = 30
                      const c = fn()
                      return a + b + c
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(60);  // 10 + 20 + 30 = 60
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableCaptureMixedWithImmutable(JdkVersion jdkVersion) throws Exception {
        // Mix of mutable and immutable captures
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      const immutable: int = 100
                      let mutable: int = 1
                      const fn: IntSupplier = () => immutable + mutable
                      mutable = 10
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(110);  // 100 + 10 = 110
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableCaptureMultipleModifications(JdkVersion jdkVersion) throws Exception {
        // Multiple modifications before reading
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 1
                      const fn: IntSupplier = () => x
                      x = 2
                      x = 3
                      x = 4
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(4);  // Should return final value
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableCaptureMultipleVariables(JdkVersion jdkVersion) throws Exception {
        // Multiple mutable captures
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 1
                      let b: int = 2
                      const fn: IntSupplier = () => a + b
                      a = 10
                      b = 20
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(30);  // 10 + 20 = 30
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableCaptureReturnedLambda(JdkVersion jdkVersion) throws Exception {
        // Mutable capture in returned lambda
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createCounter(): IntSupplier {
                      let count: int = 0
                      return () => {
                        count = count + 1
                        return count
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var counter = (IntSupplier) instanceRunner.invoke("createCounter");
        assertThat(
                List.of(
                        counter.getAsInt(), counter.getAsInt(), counter.getAsInt()
                )
        ).isEqualTo(
                List.of(1, 2, 3)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableDoubleCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): double {
                      let x: double = 1.5
                      const fn: DoubleSupplier = () => x
                      x = 2.5
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((double) result).isEqualTo(2.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableIntCapture(JdkVersion jdkVersion) throws Exception {
        // Mutable capture: variable modified after lambda creation
        // Edge case 34: Capture Variable Modified After Arrow Definition
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 10
                      const fn: IntSupplier = () => x
                      x = 20
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(20);  // Should return 20, not 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableLongCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): long {
                      let x: long = 100
                      const fn: LongSupplier = () => x
                      x = 200
                      return fn()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((long) result).isEqualTo(200L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutableObjectCapture(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): String {
                      let x: String = "hello"
                      const fn: Supplier<String> = () => x
                      x = "world"
                      return fn() as String
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("world");
    }
}
