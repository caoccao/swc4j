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
 * Phase 3: Optional Properties Tests.
 * Tests optional property handling in interface declarations.
 */
public class TestCompileAstTsInterfaceDeclOptional extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceAllOptionalProperties(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with all optional properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Options {
                    timeout?: int
                    retries?: int
                    verbose?: boolean
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Options");

        assertThat(interfaceClass.isInterface()).isTrue();

        // All optional properties should have getters
        assertThat(interfaceClass.getMethod("getTimeout")).isNotNull();
        assertThat(interfaceClass.getMethod("getRetries")).isNotNull();
        assertThat(interfaceClass.getMethod("isVerbose")).isNotNull();

        // All optional properties should have setters
        assertThat(interfaceClass.getMethod("setTimeout", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setRetries", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setVerbose", boolean.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMixedOptionalAndRequired(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with mix of optional and required properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface User {
                    id: int
                    name: String
                    email?: String
                    phone?: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.User");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Required properties
        assertThat(interfaceClass.getMethod("getId")).isNotNull();
        assertThat(interfaceClass.getMethod("setId", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("getName")).isNotNull();
        assertThat(interfaceClass.getMethod("setName", String.class)).isNotNull();

        // Optional properties (generate same methods as required)
        assertThat(interfaceClass.getMethod("getEmail")).isNotNull();
        assertThat(interfaceClass.getMethod("setEmail", String.class)).isNotNull();
        assertThat(interfaceClass.getMethod("getPhone")).isNotNull();
        assertThat(interfaceClass.getMethod("setPhone", String.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceOptionalPropertyWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Optional property with implementing class that can return null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Contact {
                    name: String
                    email?: String
                  }
                  export class ContactImpl implements Contact {
                    name: String = ""
                    email: String = null
                    getName(): String { return this.name }
                    setName(name: String): void { this.name = name }
                    getEmail(): String { return this.email }
                    setEmail(email: String): void { this.email = email }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Contact");
        Class<?> implClass = runner.getClass("com.ContactImpl");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Required property has default value
        assertThat(implClass.getMethod("getName").<Object>invoke(instance)).isEqualTo("");

        // Optional property can be null
        assertThat(implClass.getMethod("getEmail").<Object>invoke(instance)).isNull();

        // Set values
        implClass.getMethod("setName", String.class).invoke(instance, "John");
        implClass.getMethod("setEmail", String.class).invoke(instance, "john@example.com");

        // Verify values
        assertThat(implClass.getMethod("getName").<Object>invoke(instance)).isEqualTo("John");
        assertThat(implClass.getMethod("getEmail").<Object>invoke(instance)).isEqualTo("john@example.com");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceOptionalReadonlyProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Optional readonly property (getter only)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Entity {
                    readonly id?: int
                    name: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Entity");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Optional readonly property should have getter but no setter
        Method getId = interfaceClass.getMethod("getId");
        assertThat(getId).isNotNull();
        assertThat(Modifier.isAbstract(getId.getModifiers())).isTrue();

        assertThatThrownBy(() ->
                interfaceClass.getMethod("setId", int.class)).isInstanceOf(NoSuchMethodException.class);;

        // Non-readonly property should have both
        assertThat(interfaceClass.getMethod("getName")).isNotNull();
        assertThat(interfaceClass.getMethod("setName", String.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceSingleOptionalProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with single optional property
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Named {
                    name?: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Named");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Optional property should have getter
        Method getName = interfaceClass.getMethod("getName");
        assertThat(getName).isNotNull();
        assertThat(Modifier.isAbstract(getName.getModifiers())).isTrue();
        assertThat(getName.getReturnType()).isEqualTo(String.class);

        // Optional property should have setter
        Method setName = interfaceClass.getMethod("setName", String.class);
        assertThat(setName).isNotNull();
        assertThat(Modifier.isAbstract(setName.getModifiers())).isTrue();
        assertThat(setName.getReturnType()).isEqualTo(void.class);
    }
}
