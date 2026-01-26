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

package com.caoccao.javet.swc4j.compiler.ast.stmt.tsinterfacedecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 7: Index Signature Tests.
 * Tests index signatures in TypeScript interfaces.
 * Index signatures like {@code [key: string]: number} are translated to
 * {@code get(String key)} and {@code set(String key, double value)} methods.
 */
public class TestCompileAstTsInterfaceDeclIndexSignature extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureBasic(JdkVersion jdkVersion) throws Exception {
        // Test: Basic index signature with string key and string value
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringDictionary {
                    [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.StringDictionary"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): String
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertTrue(Modifier.isAbstract(get.getModifiers()));
        assertEquals(String.class, get.getReturnType());

        // Should have set(String key, String value): void
        Method set = interfaceClass.getMethod("set", String.class, String.class);
        assertNotNull(set);
        assertTrue(Modifier.isAbstract(set.getModifiers()));
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureBooleanValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with boolean value type
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface BooleanDict {
                    [key: String]: boolean
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.BooleanDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): boolean
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(boolean.class, get.getReturnType());

        // Should have set(String key, boolean value): void
        Method set = interfaceClass.getMethod("set", String.class, boolean.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureDoubleValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with double value type
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DoubleDict {
                    [key: String]: double
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.DoubleDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): double
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(double.class, get.getReturnType());

        // Should have set(String key, double value): void
        Method set = interfaceClass.getMethod("set", String.class, double.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureIntKey(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with int key and String value (like array-like access)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface NumberKeyDict {
                    [index: int]: String
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.NumberKeyDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(int index): String
        Method get = interfaceClass.getMethod("get", int.class);
        assertNotNull(get);
        assertEquals(String.class, get.getReturnType());

        // Should have set(int index, String value): void
        Method set = interfaceClass.getMethod("set", int.class, String.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureIntKeyBooleanValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with int key and boolean value (sparse boolean array)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface SparseBooleanArray {
                    [index: int]: boolean
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.SparseBooleanArray"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(int index): boolean
        Method get = interfaceClass.getMethod("get", int.class);
        assertNotNull(get);
        assertEquals(boolean.class, get.getReturnType());

        // Should have set(int index, boolean value): void
        Method set = interfaceClass.getMethod("set", int.class, boolean.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureLongValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with long value type
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface LongDict {
                    [key: String]: long
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.LongDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): long
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(long.class, get.getReturnType());

        // Should have set(String key, long value): void
        Method set = interfaceClass.getMethod("set", String.class, long.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignaturePrimitiveValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with String key and primitive int value
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntValueDict {
                    [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.IntValueDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): int
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(int.class, get.getReturnType());

        // Should have set(String key, int value): void
        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureReadonly(JdkVersion jdkVersion) throws Exception {
        // Test: Readonly index signature should only have getter, no setter
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ReadonlyDict {
                    readonly [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.ReadonlyDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): String
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(String.class, get.getReturnType());

        // Should NOT have set method
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("set", String.class, String.class));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureReadonlyWithIntValue(JdkVersion jdkVersion) throws Exception {
        // Test: Readonly index signature with primitive value type
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ReadonlyIntDict {
                    readonly [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.ReadonlyIntDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have get(String key): int
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(int.class, get.getReturnType());

        // Should NOT have set method
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("set", String.class, int.class));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithMethods(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with method signatures
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DictWithMethods {
                    [key: String]: int
                    clear(): void
                    size(): int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.DictWithMethods"));

        assertTrue(interfaceClass.isInterface());

        // Index signature methods
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(int.class, get.getReturnType());

        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());

        // Additional methods
        Method clear = interfaceClass.getMethod("clear");
        assertNotNull(clear);
        assertEquals(void.class, clear.getReturnType());

        Method size = interfaceClass.getMethod("size");
        assertNotNull(size);
        assertEquals(int.class, size.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithProperties(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with regular properties
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface MixedDict {
                    name: String
                    [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.MixedDict"));

        assertTrue(interfaceClass.isInterface());

        // Should have getName() and setName()
        Method getName = interfaceClass.getMethod("getName");
        assertNotNull(getName);
        assertEquals(String.class, getName.getReturnType());

        Method setName = interfaceClass.getMethod("setName", String.class);
        assertNotNull(setName);
        assertEquals(void.class, setName.getReturnType());

        // Should also have get(String key) and set(String key, String value)
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(String.class, get.getReturnType());

        Method set = interfaceClass.getMethod("set", String.class, String.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithReadonlyProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with readonly property
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface MixedWithReadonly {
                    readonly id: int
                    [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.MixedWithReadonly"));

        assertTrue(interfaceClass.isInterface());

        // Readonly property should only have getter
        Method getId = interfaceClass.getMethod("getId");
        assertNotNull(getId);
        assertEquals(int.class, getId.getReturnType());

        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setId", int.class));

        // Index signature should have both get and set
        Method get = interfaceClass.getMethod("get", String.class);
        assertNotNull(get);
        assertEquals(int.class, get.getReturnType());

        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertNotNull(set);
        assertEquals(void.class, set.getReturnType());
    }
}
