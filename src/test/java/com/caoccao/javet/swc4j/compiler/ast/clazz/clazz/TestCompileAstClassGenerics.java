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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for generics support (type erasure to JVM bytecode).
 * <p>
 * JVM generics use type erasure - generic type parameters are replaced with
 * Object (or their constraint type) at runtime.
 */
public class TestCompileAstClassGenerics extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassBasic(JdkVersion jdkVersion) throws Exception {
        // Basic generic class - T erases to Object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Box<T> {
                    value: T
                    constructor(value: T) { this.value = value }
                    getValue(): T { return this.value }
                    setValue(value: T): void { this.value = value }
                  }
                }""");
        Class<?> classBox = runner.getClass("com.Box");
        assertThat(classBox).isNotNull();

        // Verify field type is Object (type erasure)
        var valueField = classBox.getDeclaredField("value");
        assertThat(valueField.getType()).isEqualTo(Object.class);

        // Verify method signatures use Object
        var getValue = classBox.getMethod("getValue");
        assertThat(getValue.getReturnType()).isEqualTo(Object.class);

        var setValue = classBox.getMethod("setValue", Object.class);
        assertThat(setValue).isNotNull();

        // Test instance with String
        var instanceRunner1 = runner.createInstanceRunner("com.Box", "Hello");
        assertThat((String) instanceRunner1.invoke("getValue")).isEqualTo("Hello");

        instanceRunner1.invoke("setValue", "World");
        assertThat((String) instanceRunner1.invoke("getValue")).isEqualTo("World");

        // Test instance with Integer
        var instanceRunner2 = runner.createInstanceRunner("com.Box", 42);
        assertThat((int) instanceRunner2.invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassMultipleTypeParams(JdkVersion jdkVersion) throws Exception {
        // Generic class with multiple type parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Pair<K, V> {
                    key: K
                    val: V
                    constructor(key: K, val: V) { this.key = key; this.val = val }
                    getKey(): K { return this.key }
                    getVal(): V { return this.val }
                  }
                }""");
        Class<?> classPair = runner.getClass("com.Pair");
        assertThat(classPair).isNotNull();

        // Verify both fields are Object (no constraints)
        var keyField = classPair.getDeclaredField("key");
        var valField = classPair.getDeclaredField("val");
        assertThat(List.of(keyField.getType(), valField.getType())).isEqualTo(List.of(Object.class, Object.class));

        // Test instance
        var instanceRunner = runner.createInstanceRunner("com.Pair", "name", 42);
        assertThat(
                List.of(
                        instanceRunner.invoke("getKey"),
                        instanceRunner.invoke("getVal")
                )
        ).isEqualTo(
                List.of("name", 42)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassTwoTypeParamsWithConstraints(JdkVersion jdkVersion) throws Exception {
        // Generic class with two type params, one constrained
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class KeyValue<K extends String, V> {
                    key: K
                    val: V
                    constructor(key: K, val: V) { this.key = key; this.val = val }
                    getKey(): K { return this.key }
                    getVal(): V { return this.val }
                  }
                }""");
        Class<?> classKV = runner.getClass("com.KeyValue");
        assertThat(classKV).isNotNull();

        // Verify field types - K erases to String, V erases to Object
        var keyField = classKV.getDeclaredField("key");
        var valField = classKV.getDeclaredField("val");
        assertThat(List.of(keyField.getType(), valField.getType())).isEqualTo(List.of(String.class, Object.class));

        // Test instance
        var instanceRunner = runner.createInstanceRunner("com.KeyValue", "name", 42);
        assertThat(
                List.of(
                        instanceRunner.invoke("getKey"),
                        instanceRunner.invoke("getVal")
                )
        ).isEqualTo(
                List.of("name", 42)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassWithConstraint(JdkVersion jdkVersion) throws Exception {
        // Generic class with constraint - T erases to String (the constraint)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class StrBox<T extends String> {
                    value: T
                    constructor(value: T) { this.value = value }
                    getValue(): T { return this.value }
                  }
                }""");
        Class<?> classStrBox = runner.getClass("com.StrBox");
        assertThat(classStrBox).isNotNull();

        // Verify field type is String (constraint type)
        var valueField = classStrBox.getDeclaredField("value");
        assertThat(valueField.getType()).isEqualTo(String.class);

        // Verify method signatures use String
        var getValue = classStrBox.getMethod("getValue");
        assertThat(getValue.getReturnType()).isEqualTo(String.class);

        // Test instance
        var instanceRunner = runner.createInstanceRunner("com.StrBox", "Hello");
        assertThat((String) instanceRunner.invoke("getValue")).isEqualTo("Hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassWithMultipleMethods(JdkVersion jdkVersion) throws Exception {
        // Generic class with multiple methods using the type parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Holder<T> {
                    value: T
                    constructor(value: T) { this.value = value }
                    get(): T { return this.value }
                    set(value: T): void { this.value = value }
                    transform(newValue: T): T { return newValue }
                  }
                }""");
        Class<?> classHolder = runner.getClass("com.Holder");
        assertThat(classHolder).isNotNull();

        // Verify field type is Object (erased type)
        var valueField = classHolder.getDeclaredField("value");
        assertThat(valueField.getType()).isEqualTo(Object.class);

        // Verify method signatures
        var get = classHolder.getMethod("get");
        assertThat(get.getReturnType()).isEqualTo(Object.class);

        var set = classHolder.getMethod("set", Object.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);

        var transform = classHolder.getMethod("transform", Object.class);
        assertThat(transform.getReturnType()).isEqualTo(Object.class);

        // Test instance
        var instanceRunner = runner.createInstanceRunner("com.Holder", "Hello");
        assertThat((String) instanceRunner.invoke("get")).isEqualTo("Hello");
        instanceRunner.invoke("set", "World");
        assertThat((String) instanceRunner.invoke("get")).isEqualTo("World");
        assertThat((String) instanceRunner.invoke("transform", "New")).isEqualTo("New");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericInstanceMethodBasic(JdkVersion jdkVersion) throws Exception {
        // Generic instance method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Container {
                    wrap<T>(value: T): T { return value }
                  }
                }""");
        Class<?> classContainer = runner.getClass("com.Container");
        assertThat(classContainer).isNotNull();

        // Verify method signature uses Object
        var wrap = classContainer.getMethod("wrap", Object.class);
        assertThat(wrap.getReturnType()).isEqualTo(Object.class);

        // Test method
        var instanceRunner = runner.createInstanceRunner("com.Container");
        assertThat(
                List.of(
                        instanceRunner.invoke("wrap", "Hello"),
                        instanceRunner.invoke("wrap", 42)
                )
        ).isEqualTo(
                List.of("Hello", 42)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericMethodBasic(JdkVersion jdkVersion) throws Exception {
        // Generic method in a non-generic class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static identity<T>(value: T): T { return value }
                    static first<T>(a: T, b: T): T { return a }
                  }
                }""");
        Class<?> classUtils = runner.getClass("com.Utils");
        assertThat(classUtils).isNotNull();

        // Verify method signatures use Object
        var identity = classUtils.getMethod("identity", Object.class);
        assertThat(identity.getReturnType()).isEqualTo(Object.class);

        var first = classUtils.getMethod("first", Object.class, Object.class);
        assertThat(first.getReturnType()).isEqualTo(Object.class);

        // Test methods
        var staticRunner = runner.createStaticRunner("com.Utils");
        assertThat(
                List.of(
                        staticRunner.invoke("identity", "Hello"),
                        staticRunner.invoke("identity", 42),
                        staticRunner.invoke("first", "A", "B")
                )
        ).isEqualTo(
                List.of("Hello", 42, "A")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericMethodWithConstraint(JdkVersion jdkVersion) throws Exception {
        // Generic method with constraint - T erases to String
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static echo<T extends String>(value: T): T { return value }
                  }
                }""");
        Class<?> classUtils = runner.getClass("com.Utils");
        assertThat(classUtils).isNotNull();

        // Verify method signature uses String (constraint)
        var echo = classUtils.getMethod("echo", String.class);
        assertThat(echo.getReturnType()).isEqualTo(String.class);

        // Test method
        var staticRunner = runner.createStaticRunner("com.Utils");
        assertThat(
                List.of(
                        staticRunner.invoke("echo", "Hello"),
                        staticRunner.invoke("echo", "World")
                )
        ).isEqualTo(
                List.of("Hello", "World")
        );
    }
}
