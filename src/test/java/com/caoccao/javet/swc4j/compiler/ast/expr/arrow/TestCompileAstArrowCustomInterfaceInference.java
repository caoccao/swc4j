/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests for arrow expressions assigned to custom TypeScript interfaces.
 * Tests type inference and implementation generation for custom functional interfaces.
 */
public class TestCompileAstArrowCustomInterfaceInference extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceDirectReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow directly returned (not assigned to variable first)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                  export class A {
                    getDoubler(): IntTransformer {
                      return (x: int) => x * 2
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getDoubler");

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", int.class);
        assertThat((int) transformMethod.invoke(transformer, 5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceMultipleParams(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with multiple parameters assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface BinaryOp {
                    compute(a: int, b: int): int
                  }
                  export class A {
                    getAdder(): BinaryOp {
                      const adder: BinaryOp = (a: int, b: int) => a + b
                      return adder
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object op = instanceRunner.invoke("getAdder");

        Class<?> opClass = op.getClass();
        Method computeMethod = opClass.getMethod("compute", int.class, int.class);
        assertThat((int) computeMethod.invoke(op, 3, 5)).isEqualTo(8);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithBooleanReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with boolean return assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntPredicate {
                    test(x: int): boolean
                  }
                  export class A {
                    getPositiveChecker(): IntPredicate {
                      const checker: IntPredicate = (x: int) => x > 0
                      return checker
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object predicate = instanceRunner.invoke("getPositiveChecker");

        Class<?> predicateClass = predicate.getClass();
        Method testMethod = predicateClass.getMethod("test", int.class);
        assertThat((boolean) testMethod.invoke(predicate, 5)).isTrue();
        assertThat((boolean) testMethod.invoke(predicate, -5)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithCapture(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with closure capture assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                  export class A {
                    createMultiplier(factor: int): IntTransformer {
                      const multiplier: IntTransformer = (x: int) => x * factor
                      return multiplier
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("createMultiplier", 10);

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", int.class);
        assertThat((int) transformMethod.invoke(transformer, 5)).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithDoubleParam(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with double parameter assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DoubleTransformer {
                    transform(x: double): double
                  }
                  export class A {
                    getSquarer(): DoubleTransformer {
                      const squarer: DoubleTransformer = (x: double) => x * x
                      return squarer
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getSquarer");

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", double.class);
        assertThat((double) transformMethod.invoke(transformer, 5.0)).isCloseTo(25.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithInferredParamType(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with parameter type inferred from interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                  export class A {
                    getTripler(): IntTransformer {
                      const tripler: IntTransformer = (x) => x * 3
                      return tripler
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getTripler");

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", int.class);
        assertThat((int) transformMethod.invoke(transformer, 5)).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithLongParam(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with long parameter assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface LongTransformer {
                    transform(x: long): long
                  }
                  export class A {
                    getDoubler(): LongTransformer {
                      const doubler: LongTransformer = (x: long) => x * 2
                      return doubler
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getDoubler");

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", long.class);
        assertThat((long) transformMethod.invoke(transformer, 10000000000L)).isEqualTo(20000000000L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithStringParam(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with String parameter assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringTransformer {
                    transform(s: String): String
                  }
                  export class A {
                    getUpperCaser(): StringTransformer {
                      const upperCaser: StringTransformer = (s: String) => s.toUpperCase()
                      return upperCaser
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getUpperCaser");

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", String.class);
        assertThat((String) transformMethod.invoke(transformer, "hello")).isEqualTo("HELLO");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomInterfaceWithVoidReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow with void return assigned to custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntAction {
                    perform(x: int): void
                  }
                  export class A {
                    getAction(): IntAction {
                      // Arrow with void return - it just returns without doing anything
                      const action: IntAction = (x: int) => { return }
                      return action
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object action = instanceRunner.invoke("getAction");

        // Verify the action has the perform method and can be called
        Class<?> actionClass = action.getClass();
        Method performMethod = actionClass.getMethod("perform", int.class);
        assertThat(performMethod).isNotNull();
        assertThat(performMethod.getReturnType()).isEqualTo(void.class);
        // Just verify it doesn't throw when called
        performMethod.invoke(action, 42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSimpleCustomInterface(JdkVersion jdkVersion) throws Exception {
        // Test: Arrow assigned to simple custom interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntTransformer {
                    transform(x: int): int
                  }
                  export class A {
                    getDoubler(): IntTransformer {
                      const doubler: IntTransformer = (x: int) => x * 2
                      return doubler
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object transformer = instanceRunner.invoke("getDoubler");
        assertThat(transformer).isNotNull();

        Class<?> transformerClass = transformer.getClass();
        Method transformMethod = transformerClass.getMethod("transform", int.class);
        assertThat((int) transformMethod.invoke(transformer, 5)).isEqualTo(10);
    }
}
