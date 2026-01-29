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

package com.caoccao.javet.swc4j.compiler.ast.clazz.clazz;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstClassFields extends BaseTestCompileSuite {

    /**
     * Helper to invoke an action and then return the result of another call.
     */
    private static <T> T invokeAfter(ThrowingRunnable action, ThrowingSupplier<T> result) throws Exception {
        action.run();
        return result.get();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10
                    setValue(v: int): void {
                      this.value = v
                    }
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertThat(
                List.of(
                        classA.getMethod("getValue").invoke(instance),
                        invokeAfter(() -> classA.getMethod("setValue", int.class).invoke(instance, 99),
                                () -> classA.getMethod("getValue").invoke(instance))
                )
        ).isEqualTo(
                List.of(10, 99)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    intValue: int = 42
                    doubleValue: double = 3.14
                    boolValue: boolean = true
                    stringValue: String = "Hello"
                
                    getInt(): int { return this.intValue }
                    getDouble(): double { return this.doubleValue }
                    getBool(): boolean { return this.boolValue }
                    getString(): String { return this.stringValue }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                Map.of(
                        "int", instanceRunner.invoke("getInt"),
                        "double", instanceRunner.invoke("getDouble"),
                        "bool", (boolean) instanceRunner.invoke("getBool"),
                        "string", (String) instanceRunner.invoke("getString")
                )
        ).isEqualTo(
                Map.of(
                        "int", 42,
                        "double", 3.14,
                        "bool", true,
                        "string", "Hello"
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldUsedInMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    count: int = 0
                    increment(): void { this.count = this.count + 1 }
                    decrement(): void { this.count = this.count - 1 }
                    getCount(): int { return this.count }
                    reset(): void { this.count = 0 }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.Counter");

        assertThat(
                List.of(
                        (int) instanceRunner.invoke("getCount"),
                        invokeAfter(() -> instanceRunner.invoke("increment"), () -> instanceRunner.invoke("getCount")),
                        invokeAfter(() -> instanceRunner.invoke("increment"), () -> instanceRunner.invoke("getCount")),
                        invokeAfter(() -> instanceRunner.invoke("decrement"), () -> instanceRunner.invoke("getCount")),
                        invokeAfter(() -> instanceRunner.invoke("reset"), () -> instanceRunner.invoke("getCount"))
                )
        ).isEqualTo(
                List.of(0, 1, 2, 1, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldWithExpressionInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10 + 20 * 2
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldWithInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 42
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFieldWithTypeAnnotationOnly(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        // Default value for int is 0
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleFields(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    x: int = 10
                    y: int = 20
                    sum(): int {
                      return this.x + this.y
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("sum")).isEqualTo(30);
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
