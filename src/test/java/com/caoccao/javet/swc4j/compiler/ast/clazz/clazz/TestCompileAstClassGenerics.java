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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Box<T> {
                    value: T
                    constructor(value: T) { this.value = value }
                    getValue(): T { return this.value }
                    setValue(value: T): void { this.value = value }
                  }
                }""");
        Class<?> classBox = loadClass(map.get("com.Box"));
        assertNotNull(classBox);

        // Verify field type is Object (type erasure)
        var valueField = classBox.getDeclaredField("value");
        assertEquals(Object.class, valueField.getType());

        // Verify method signatures use Object
        var getValue = classBox.getMethod("getValue");
        assertEquals(Object.class, getValue.getReturnType());

        var setValue = classBox.getMethod("setValue", Object.class);
        assertNotNull(setValue);

        // Test instance with String
        var instance = classBox.getConstructor(Object.class).newInstance("Hello");
        assertEquals("Hello", getValue.invoke(instance));

        setValue.invoke(instance, "World");
        assertEquals("World", getValue.invoke(instance));

        // Test instance with Integer
        var instance2 = classBox.getConstructor(Object.class).newInstance(42);
        assertEquals(42, getValue.invoke(instance2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassMultipleTypeParams(JdkVersion jdkVersion) throws Exception {
        // Generic class with multiple type parameters
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Pair<K, V> {
                    key: K
                    val: V
                    constructor(key: K, val: V) { this.key = key; this.val = val }
                    getKey(): K { return this.key }
                    getVal(): V { return this.val }
                  }
                }""");
        Class<?> classPair = loadClass(map.get("com.Pair"));
        assertNotNull(classPair);

        // Verify both fields are Object (no constraints)
        var keyField = classPair.getDeclaredField("key");
        var valField = classPair.getDeclaredField("val");
        assertEquals(List.of(Object.class, Object.class), List.of(keyField.getType(), valField.getType()));

        // Test instance
        var instance = classPair.getConstructor(Object.class, Object.class).newInstance("name", 42);
        assertEquals(
                List.of("name", 42),
                List.of(
                        classPair.getMethod("getKey").invoke(instance),
                        classPair.getMethod("getVal").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassTwoTypeParamsWithConstraints(JdkVersion jdkVersion) throws Exception {
        // Generic class with two type params, one constrained
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class KeyValue<K extends String, V> {
                    key: K
                    val: V
                    constructor(key: K, val: V) { this.key = key; this.val = val }
                    getKey(): K { return this.key }
                    getVal(): V { return this.val }
                  }
                }""");
        Class<?> classKV = loadClass(map.get("com.KeyValue"));
        assertNotNull(classKV);

        // Verify field types - K erases to String, V erases to Object
        var keyField = classKV.getDeclaredField("key");
        var valField = classKV.getDeclaredField("val");
        assertEquals(List.of(String.class, Object.class), List.of(keyField.getType(), valField.getType()));

        // Test instance
        var instance = classKV.getConstructor(String.class, Object.class).newInstance("name", 42);
        assertEquals(
                List.of("name", 42),
                List.of(
                        classKV.getMethod("getKey").invoke(instance),
                        classKV.getMethod("getVal").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassWithConstraint(JdkVersion jdkVersion) throws Exception {
        // Generic class with constraint - T erases to String (the constraint)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class StrBox<T extends String> {
                    value: T
                    constructor(value: T) { this.value = value }
                    getValue(): T { return this.value }
                  }
                }""");
        Class<?> classStrBox = loadClass(map.get("com.StrBox"));
        assertNotNull(classStrBox);

        // Verify field type is String (constraint type)
        var valueField = classStrBox.getDeclaredField("value");
        assertEquals(String.class, valueField.getType());

        // Verify method signatures use String
        var getValue = classStrBox.getMethod("getValue");
        assertEquals(String.class, getValue.getReturnType());

        // Test instance
        var instance = classStrBox.getConstructor(String.class).newInstance("Hello");
        assertEquals("Hello", getValue.invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericClassWithMultipleMethods(JdkVersion jdkVersion) throws Exception {
        // Generic class with multiple methods using the type parameter
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Holder<T> {
                    value: T
                    constructor(value: T) { this.value = value }
                    get(): T { return this.value }
                    set(value: T): void { this.value = value }
                    transform(newValue: T): T { return newValue }
                  }
                }""");
        Class<?> classHolder = loadClass(map.get("com.Holder"));
        assertNotNull(classHolder);

        // Verify field type is Object (erased type)
        var valueField = classHolder.getDeclaredField("value");
        assertEquals(Object.class, valueField.getType());

        // Verify method signatures
        var get = classHolder.getMethod("get");
        assertEquals(Object.class, get.getReturnType());

        var set = classHolder.getMethod("set", Object.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());

        var transform = classHolder.getMethod("transform", Object.class);
        assertEquals(Object.class, transform.getReturnType());

        // Test instance
        var instance = classHolder.getConstructor(Object.class).newInstance("Hello");
        assertEquals("Hello", get.invoke(instance));
        set.invoke(instance, "World");
        assertEquals("World", get.invoke(instance));
        assertEquals("New", transform.invoke(instance, "New"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericInstanceMethodBasic(JdkVersion jdkVersion) throws Exception {
        // Generic instance method
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Container {
                    wrap<T>(value: T): T { return value }
                  }
                }""");
        Class<?> classContainer = loadClass(map.get("com.Container"));
        assertNotNull(classContainer);

        // Verify method signature uses Object
        var wrap = classContainer.getMethod("wrap", Object.class);
        assertEquals(Object.class, wrap.getReturnType());

        // Test method
        var instance = classContainer.getConstructor().newInstance();
        assertEquals(
                List.of("Hello", 42),
                List.of(
                        wrap.invoke(instance, "Hello"),
                        wrap.invoke(instance, 42)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericMethodBasic(JdkVersion jdkVersion) throws Exception {
        // Generic method in a non-generic class
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static identity<T>(value: T): T { return value }
                    static first<T>(a: T, b: T): T { return a }
                  }
                }""");
        Class<?> classUtils = loadClass(map.get("com.Utils"));
        assertNotNull(classUtils);

        // Verify method signatures use Object
        var identity = classUtils.getMethod("identity", Object.class);
        assertEquals(Object.class, identity.getReturnType());

        var first = classUtils.getMethod("first", Object.class, Object.class);
        assertEquals(Object.class, first.getReturnType());

        // Test methods
        assertEquals(
                List.of("Hello", 42, "A"),
                List.of(
                        identity.invoke(null, "Hello"),
                        identity.invoke(null, 42),
                        first.invoke(null, "A", "B")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericMethodWithConstraint(JdkVersion jdkVersion) throws Exception {
        // Generic method with constraint - T erases to String
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static echo<T extends String>(value: T): T { return value }
                  }
                }""");
        Class<?> classUtils = loadClass(map.get("com.Utils"));
        assertNotNull(classUtils);

        // Verify method signature uses String (constraint)
        var echo = classUtils.getMethod("echo", String.class);
        assertEquals(String.class, echo.getReturnType());

        // Test method
        assertEquals(
                List.of("Hello", "World"),
                List.of(
                        echo.invoke(null, "Hello"),
                        echo.invoke(null, "World")
                )
        );
    }
}
