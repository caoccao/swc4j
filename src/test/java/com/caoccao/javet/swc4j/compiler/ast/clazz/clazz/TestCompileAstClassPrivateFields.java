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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for ES2022 private fields (#field syntax).
 */
public class TestCompileAstClassPrivateFields extends BaseTestCompileSuite {

    /**
     * Helper to invoke an action and then return the result of another call.
     */
    private static <T> T invokeAfter(ThrowingRunnable action, ThrowingSupplier<T> result) throws Exception {
        action.run();
        return result.get();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedStaticAndInstancePrivateFields(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #staticValue: int = 100
                    #instanceValue: int = 50
                    static getStaticValue(): int { return A.#staticValue }
                    getInstanceValue(): int { return this.#instanceValue }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify static field
        var staticField = classA.getDeclaredField("staticValue");
        assertThat(Modifier.isPrivate(staticField.getModifiers())).as("#staticValue should be private").isTrue();
        assertThat(Modifier.isStatic(staticField.getModifiers())).as("#staticValue should be static").isTrue();

        // Verify instance field
        var instanceField = classA.getDeclaredField("instanceValue");
        assertThat(Modifier.isPrivate(instanceField.getModifiers())).as("#instanceValue should be private").isTrue();
        assertThat(Modifier.isStatic(instanceField.getModifiers())).as("#instanceValue should not be static").isFalse();

        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        runner.createStaticRunner("com.A").invoke("getStaticValue"),
                        (int) instanceRunner.invoke("getInstanceValue")
                )
        ).isEqualTo(
                List.of(100, 50)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 10
                    setValue(v: int): void { this.#value = v }
                    getValue(): int { return this.#value }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        (int) instanceRunner.invoke("getValue"),
                        invokeAfter(() -> instanceRunner.invoke("setValue", 99),
                                () -> instanceRunner.invoke("getValue"))
                )
        ).isEqualTo(
                List.of(10, 99)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldBasicRead(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 42
                    getValue(): int { return this.#value }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is private
        var valueField = classA.getDeclaredField("value");
        assertThat(Modifier.isPrivate(valueField.getModifiers())).as("#value should be private").isTrue();

        // Test functionality
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldInCounter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    #count: int = 0
                    increment(): void { this.#count = this.#count + 1 }
                    decrement(): void { this.#count = this.#count - 1 }
                    getCount(): int { return this.#count }
                    reset(): void { this.#count = 0 }
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
    public void testPrivateFieldMixedWithPublic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    publicValue: int = 5
                    #privateValue: int = 10
                    getPublic(): int { return this.publicValue }
                    getPrivate(): int { return this.#privateValue }
                    getSum(): int { return this.publicValue + this.#privateValue }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify access modifiers
        var publicField = classA.getDeclaredField("publicValue");
        var privateField = classA.getDeclaredField("privateValue");
        assertThat(Modifier.isPublic(publicField.getModifiers())).as("publicValue should be public").isTrue();
        assertThat(Modifier.isPrivate(privateField.getModifiers())).as("#privateValue should be private").isTrue();

        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        instanceRunner.invoke("getPublic"),
                        instanceRunner.invoke("getPrivate"),
                        (int) instanceRunner.invoke("getSum")
                )
        ).isEqualTo(
                List.of(5, 10, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #x: int = 10
                    #y: int = 20
                    sum(): int { return this.#x + this.#y }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("sum")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #intValue: int = 42
                    #doubleValue: double = 3.14
                    #boolValue: boolean = true
                    #stringValue: String = "Hello"
                
                    getInt(): int { return this.#intValue }
                    getDouble(): double { return this.#doubleValue }
                    getBool(): boolean { return this.#boolValue }
                    getString(): String { return this.#stringValue }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify all fields are private
        for (String fieldName : List.of("intValue", "doubleValue", "boolValue", "stringValue")) {
            var field = classA.getDeclaredField(fieldName);
            assertThat(Modifier.isPrivate(field.getModifiers())).as("#" + fieldName + " should be private").isTrue();
        }

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
    public void testPrivateFieldWithExpressionInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 10 + 20 * 2
                    getValue(): int { return this.#value }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateFieldAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #counter: int = 0
                    static increment(): void { A.#counter = A.#counter + 1 }
                    static getCounter(): int { return A.#counter }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");

        assertThat(
                List.of(
                        (int) staticRunner.invoke("getCounter"),
                        invokeAfter(() -> staticRunner.invoke("increment"), () -> staticRunner.invoke("getCounter")),
                        invokeAfter(() -> staticRunner.invoke("increment"), () -> staticRunner.invoke("getCounter"))
                )
        ).isEqualTo(
                List.of(0, 1, 2)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateFieldBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #count: int = 100
                    static getCount(): int { return A.#count }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is private and static
        var countField = classA.getDeclaredField("count");
        assertThat(Modifier.isPrivate(countField.getModifiers())).as("#count should be private").isTrue();
        assertThat(Modifier.isStatic(countField.getModifiers())).as("#count should be static").isTrue();

        assertThat((int) runner.createStaticRunner("com.A").invoke("getCount")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateFieldCounter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    static #count: int = 0
                    static increment(): void { Counter.#count = Counter.#count + 1 }
                    static decrement(): void { Counter.#count = Counter.#count - 1 }
                    static getCount(): int { return Counter.#count }
                    static reset(): void { Counter.#count = 0 }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.Counter");

        assertThat(
                List.of(
                        (int) staticRunner.invoke("getCount"),
                        invokeAfter(() -> staticRunner.invoke("increment"), () -> staticRunner.invoke("getCount")),
                        invokeAfter(() -> staticRunner.invoke("increment"), () -> staticRunner.invoke("getCount")),
                        invokeAfter(() -> staticRunner.invoke("decrement"), () -> staticRunner.invoke("getCount")),
                        invokeAfter(() -> staticRunner.invoke("reset"), () -> staticRunner.invoke("getCount"))
                )
        ).isEqualTo(
                List.of(0, 1, 2, 1, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateFieldTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #intValue: int = 42
                    static #doubleValue: double = 3.14
                    static #boolValue: boolean = true
                    static #stringValue: String = "Hello"
                
                    static getInt(): int { return A.#intValue }
                    static getDouble(): double { return A.#doubleValue }
                    static getBool(): boolean { return A.#boolValue }
                    static getString(): String { return A.#stringValue }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify all fields are private and static
        for (String fieldName : List.of("intValue", "doubleValue", "boolValue", "stringValue")) {
            var field = classA.getDeclaredField(fieldName);
            assertThat(Modifier.isPrivate(field.getModifiers())).as("#" + fieldName + " should be private").isTrue();
            assertThat(Modifier.isStatic(field.getModifiers())).as("#" + fieldName + " should be static").isTrue();
        }

        var staticRunner = runner.createStaticRunner("com.A");
        assertThat(
                Map.of(
                        "int", staticRunner.invoke("getInt"),
                        "double", staticRunner.invoke("getDouble"),
                        "bool", (boolean) staticRunner.invoke("getBool"),
                        "string", (String) staticRunner.invoke("getString")
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

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
