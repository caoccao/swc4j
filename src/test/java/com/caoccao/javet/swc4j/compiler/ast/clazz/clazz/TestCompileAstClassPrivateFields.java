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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void testPrivateFieldAssignment(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 10
                    setValue(v: int): void { this.#value = v }
                    getValue(): int { return this.#value }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(
                List.of(10, 99),
                List.of(
                        classA.getMethod("getValue").invoke(instance),
                        invokeAfter(() -> classA.getMethod("setValue", int.class).invoke(instance, 99),
                                () -> classA.getMethod("getValue").invoke(instance))
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldBasicRead(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 42
                    getValue(): int { return this.#value }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));

        // Verify field is private
        var valueField = classA.getDeclaredField("value");
        assertTrue(Modifier.isPrivate(valueField.getModifiers()), "#value should be private");

        // Test functionality
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("getValue").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldInCounter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    #count: int = 0
                    increment(): void { this.#count = this.#count + 1 }
                    decrement(): void { this.#count = this.#count - 1 }
                    getCount(): int { return this.#count }
                    reset(): void { this.#count = 0 }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.Counter"));
        var instance = classA.getConstructor().newInstance();
        var getCount = classA.getMethod("getCount");
        var increment = classA.getMethod("increment");
        var decrement = classA.getMethod("decrement");
        var reset = classA.getMethod("reset");

        assertEquals(
                List.of(0, 1, 2, 1, 0),
                List.of(
                        getCount.invoke(instance),
                        invokeAfter(() -> increment.invoke(instance), () -> getCount.invoke(instance)),
                        invokeAfter(() -> increment.invoke(instance), () -> getCount.invoke(instance)),
                        invokeAfter(() -> decrement.invoke(instance), () -> getCount.invoke(instance)),
                        invokeAfter(() -> reset.invoke(instance), () -> getCount.invoke(instance))
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldMixedWithPublic(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    publicValue: int = 5
                    #privateValue: int = 10
                    getPublic(): int { return this.publicValue }
                    getPrivate(): int { return this.#privateValue }
                    getSum(): int { return this.publicValue + this.#privateValue }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));

        // Verify access modifiers
        var publicField = classA.getDeclaredField("publicValue");
        var privateField = classA.getDeclaredField("privateValue");
        assertTrue(Modifier.isPublic(publicField.getModifiers()), "publicValue should be public");
        assertTrue(Modifier.isPrivate(privateField.getModifiers()), "#privateValue should be private");

        var instance = classA.getConstructor().newInstance();
        assertEquals(
                List.of(5, 10, 15),
                List.of(
                        classA.getMethod("getPublic").invoke(instance),
                        classA.getMethod("getPrivate").invoke(instance),
                        classA.getMethod("getSum").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldMultiple(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #x: int = 10
                    #y: int = 20
                    sum(): int { return this.#x + this.#y }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(30, classA.getMethod("sum").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));

        // Verify all fields are private
        for (String fieldName : List.of("intValue", "doubleValue", "boolValue", "stringValue")) {
            var field = classA.getDeclaredField(fieldName);
            assertTrue(Modifier.isPrivate(field.getModifiers()), "#" + fieldName + " should be private");
        }

        var instance = classA.getConstructor().newInstance();
        assertEquals(
                Map.of(
                        "int", 42,
                        "double", 3.14,
                        "bool", true,
                        "string", "Hello"
                ),
                Map.of(
                        "int", classA.getMethod("getInt").invoke(instance),
                        "double", classA.getMethod("getDouble").invoke(instance),
                        "bool", classA.getMethod("getBool").invoke(instance),
                        "string", classA.getMethod("getString").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateFieldWithExpressionInitializer(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 10 + 20 * 2
                    getValue(): int { return this.#value }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(50, classA.getMethod("getValue").invoke(instance));
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
