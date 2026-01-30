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
 * Phase 9: Explicit Getter/Setter Signatures Tests.
 * Tests explicit get and set accessor declarations in interfaces.
 */
public class TestCompileAstTsInterfaceDeclAccessors extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitBooleanGetter(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit boolean getter (should use 'is' prefix)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Toggle {
                    get enabled(): boolean
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Toggle");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Boolean getter should use 'is' prefix
        Method isEnabled = interfaceClass.getMethod("isEnabled");
        assertThat(isEnabled).isNotNull();
        assertThat(Modifier.isAbstract(isEnabled.getModifiers())).isTrue();
        assertThat(isEnabled.getReturnType()).isEqualTo(boolean.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with explicit getter signature
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Valued {
                    get value(): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Valued");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Explicit getter should generate abstract getter method
        Method getValue = interfaceClass.getMethod("getValue");
        assertThat(getValue).isNotNull();
        assertThat(Modifier.isAbstract(getValue.getModifiers())).isTrue();
        assertThat(getValue.getReturnType()).isEqualTo(int.class);

        // No setter should be generated
        assertThatThrownBy(() ->
                interfaceClass.getMethod("setValue", int.class)).isInstanceOf(NoSuchMethodException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetterAndSetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both explicit getter and setter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Counter {
                    get count(): int
                    set count(v: int)
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Counter");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Getter should exist
        Method getCount = interfaceClass.getMethod("getCount");
        assertThat(getCount).isNotNull();
        assertThat(Modifier.isAbstract(getCount.getModifiers())).isTrue();
        assertThat(getCount.getReturnType()).isEqualTo(int.class);

        // Setter should exist
        Method setCount = interfaceClass.getMethod("setCount", int.class);
        assertThat(setCount).isNotNull();
        assertThat(Modifier.isAbstract(setCount.getModifiers())).isTrue();
        assertThat(setCount.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetterAndSetterWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit getter and setter with implementing class (using int to avoid double init issue)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Counter {
                    get value(): int
                    set value(v: int)
                  }
                  export class SimpleCounter implements Counter {
                    value: int = 0
                    getValue(): int { return this.value }
                    setValue(v: int): void { this.value = v }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Counter");
        Class<?> implClass = runner.getClass("com.SimpleCounter");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial value
        assertThat(implClass.getMethod("getValue").invoke(instance)).isEqualTo(0);

        // Set value
        implClass.getMethod("setValue", int.class).invoke(instance, 42);

        // Get value
        assertThat(implClass.getMethod("getValue").invoke(instance)).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitSetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with explicit setter signature only
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Writer {
                    set output(data: String)
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Writer");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Explicit setter should generate abstract setter method
        Method setOutput = interfaceClass.getMethod("setOutput", String.class);
        assertThat(setOutput).isNotNull();
        assertThat(Modifier.isAbstract(setOutput.getModifiers())).isTrue();
        assertThat(setOutput.getReturnType()).isEqualTo(void.class);

        // No getter should be generated
        assertThatThrownBy(() ->
                interfaceClass.getMethod("getOutput")).isInstanceOf(NoSuchMethodException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMixedAccessorsAndProperties(JdkVersion jdkVersion) throws Exception {
        // Test: Mix of explicit accessors and property signatures
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Config {
                    name: String
                    get version(): int
                    set debug(enabled: boolean)
                    readonly id: int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Config");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Property 'name' should have getter and setter
        assertThat(interfaceClass.getMethod("getName")).isNotNull();
        assertThat(interfaceClass.getMethod("setName", String.class)).isNotNull();

        // Explicit getter 'version' should only have getter
        assertThat(interfaceClass.getMethod("getVersion")).isNotNull();
        assertThatThrownBy(() ->
                interfaceClass.getMethod("setVersion", int.class)).isInstanceOf(NoSuchMethodException.class);

        // Explicit setter 'debug' should only have setter
        assertThat(interfaceClass.getMethod("setDebug", boolean.class)).isNotNull();
        assertThatThrownBy(() ->
                interfaceClass.getMethod("isDebug")).isInstanceOf(NoSuchMethodException.class);

        // Readonly 'id' should only have getter
        assertThat(interfaceClass.getMethod("getId")).isNotNull();
        assertThatThrownBy(() ->
                interfaceClass.getMethod("setId", int.class)).isInstanceOf(NoSuchMethodException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultipleAccessors(JdkVersion jdkVersion) throws Exception {
        // Test: Multiple explicit accessors
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Point {
                    get x(): int
                    get y(): int
                    set x(v: int)
                    set y(v: int)
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Point");

        assertThat(interfaceClass.isInterface()).isTrue();

        // All getters and setters should exist
        assertThat(interfaceClass.getMethod("getX")).isNotNull();
        assertThat(interfaceClass.getMethod("getY")).isNotNull();
        assertThat(interfaceClass.getMethod("setX", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setY", int.class)).isNotNull();
    }
}
