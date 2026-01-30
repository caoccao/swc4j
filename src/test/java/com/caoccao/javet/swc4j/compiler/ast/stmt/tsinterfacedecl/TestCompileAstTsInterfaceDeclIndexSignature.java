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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringDictionary {
                    [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.StringDictionary");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): String
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(Modifier.isAbstract(get.getModifiers())).isTrue();
        assertThat(get.getReturnType()).isEqualTo(String.class);

        // Should have set(String key, String value): void
        Method set = interfaceClass.getMethod("set", String.class, String.class);
        assertThat(set).isNotNull();
        assertThat(Modifier.isAbstract(set.getModifiers())).isTrue();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureBooleanValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with boolean value type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface BooleanDict {
                    [key: String]: boolean
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.BooleanDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): boolean
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(boolean.class);

        // Should have set(String key, boolean value): void
        Method set = interfaceClass.getMethod("set", String.class, boolean.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureDoubleValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with double value type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DoubleDict {
                    [key: String]: double
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.DoubleDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): double
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(double.class);

        // Should have set(String key, double value): void
        Method set = interfaceClass.getMethod("set", String.class, double.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureIntKey(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with int key and String value (like array-like access)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface NumberKeyDict {
                    [index: int]: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.NumberKeyDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(int index): String
        Method get = interfaceClass.getMethod("get", int.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(String.class);

        // Should have set(int index, String value): void
        Method set = interfaceClass.getMethod("set", int.class, String.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureIntKeyBooleanValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with int key and boolean value (sparse boolean array)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface SparseBooleanArray {
                    [index: int]: boolean
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.SparseBooleanArray");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(int index): boolean
        Method get = interfaceClass.getMethod("get", int.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(boolean.class);

        // Should have set(int index, boolean value): void
        Method set = interfaceClass.getMethod("set", int.class, boolean.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureLongValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with long value type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface LongDict {
                    [key: String]: long
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.LongDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): long
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(long.class);

        // Should have set(String key, long value): void
        Method set = interfaceClass.getMethod("set", String.class, long.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignaturePrimitiveValue(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature with String key and primitive int value
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface IntValueDict {
                    [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.IntValueDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): int
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(int.class);

        // Should have set(String key, int value): void
        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureReadonly(JdkVersion jdkVersion) throws Exception {
        // Test: Readonly index signature should only have getter, no setter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ReadonlyDict {
                    readonly [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.ReadonlyDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): String
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(String.class);

        // Should NOT have set method
        assertThatThrownBy(() ->
                interfaceClass.getMethod("set", String.class, String.class)).isInstanceOf(NoSuchMethodException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureReadonlyWithIntValue(JdkVersion jdkVersion) throws Exception {
        // Test: Readonly index signature with primitive value type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ReadonlyIntDict {
                    readonly [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.ReadonlyIntDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have get(String key): int
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(int.class);

        // Should NOT have set method
        assertThatThrownBy(() ->
                interfaceClass.getMethod("set", String.class, int.class)).isInstanceOf(NoSuchMethodException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithMethods(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with method signatures
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface DictWithMethods {
                    [key: String]: int
                    clear(): void
                    size(): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.DictWithMethods");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Index signature methods
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(int.class);

        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);

        // Additional methods
        Method clear = interfaceClass.getMethod("clear");
        assertThat(clear).isNotNull();
        assertThat(clear.getReturnType()).isEqualTo(void.class);

        Method size = interfaceClass.getMethod("size");
        assertThat(size).isNotNull();
        assertThat(size.getReturnType()).isEqualTo(int.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithProperties(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with regular properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface MixedDict {
                    name: String
                    [key: String]: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.MixedDict");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have getName() and setName()
        Method getName = interfaceClass.getMethod("getName");
        assertThat(getName).isNotNull();
        assertThat(getName.getReturnType()).isEqualTo(String.class);

        Method setName = interfaceClass.getMethod("setName", String.class);
        assertThat(setName).isNotNull();
        assertThat(setName.getReturnType()).isEqualTo(void.class);

        // Should also have get(String key) and set(String key, String value)
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(String.class);

        Method set = interfaceClass.getMethod("set", String.class, String.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIndexSignatureWithReadonlyProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Index signature combined with readonly property
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface MixedWithReadonly {
                    readonly id: int
                    [key: String]: int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.MixedWithReadonly");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Readonly property should only have getter
        Method getId = interfaceClass.getMethod("getId");
        assertThat(getId).isNotNull();
        assertThat(getId.getReturnType()).isEqualTo(int.class);

        assertThatThrownBy(() ->
                interfaceClass.getMethod("setId", int.class)).isInstanceOf(NoSuchMethodException.class);

        // Index signature should have both get and set
        Method get = interfaceClass.getMethod("get", String.class);
        assertThat(get).isNotNull();
        assertThat(get.getReturnType()).isEqualTo(int.class);

        Method set = interfaceClass.getMethod("set", String.class, int.class);
        assertThat(set).isNotNull();
        assertThat(set.getReturnType()).isEqualTo(void.class);
    }
}
