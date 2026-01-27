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

        assertTrue(interfaceClass.isInterface());

        // All optional properties should have getters
        assertNotNull(interfaceClass.getMethod("getTimeout"));
        assertNotNull(interfaceClass.getMethod("getRetries"));
        assertNotNull(interfaceClass.getMethod("isVerbose"));

        // All optional properties should have setters
        assertNotNull(interfaceClass.getMethod("setTimeout", int.class));
        assertNotNull(interfaceClass.getMethod("setRetries", int.class));
        assertNotNull(interfaceClass.getMethod("setVerbose", boolean.class));
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

        assertTrue(interfaceClass.isInterface());

        // Required properties
        assertNotNull(interfaceClass.getMethod("getId"));
        assertNotNull(interfaceClass.getMethod("setId", int.class));
        assertNotNull(interfaceClass.getMethod("getName"));
        assertNotNull(interfaceClass.getMethod("setName", String.class));

        // Optional properties (generate same methods as required)
        assertNotNull(interfaceClass.getMethod("getEmail"));
        assertNotNull(interfaceClass.getMethod("setEmail", String.class));
        assertNotNull(interfaceClass.getMethod("getPhone"));
        assertNotNull(interfaceClass.getMethod("setPhone", String.class));
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

        assertTrue(interfaceClass.isInterface());

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Required property has default value
        assertEquals("", implClass.getMethod("getName").invoke(instance));

        // Optional property can be null
        assertNull(implClass.getMethod("getEmail").invoke(instance));

        // Set values
        implClass.getMethod("setName", String.class).invoke(instance, "John");
        implClass.getMethod("setEmail", String.class).invoke(instance, "john@example.com");

        // Verify values
        assertEquals("John", implClass.getMethod("getName").invoke(instance));
        assertEquals("john@example.com", implClass.getMethod("getEmail").invoke(instance));
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

        assertTrue(interfaceClass.isInterface());

        // Optional readonly property should have getter but no setter
        Method getId = interfaceClass.getMethod("getId");
        assertNotNull(getId);
        assertTrue(Modifier.isAbstract(getId.getModifiers()));

        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setId", int.class));

        // Non-readonly property should have both
        assertNotNull(interfaceClass.getMethod("getName"));
        assertNotNull(interfaceClass.getMethod("setName", String.class));
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

        assertTrue(interfaceClass.isInterface());

        // Optional property should have getter
        Method getName = interfaceClass.getMethod("getName");
        assertNotNull(getName);
        assertTrue(Modifier.isAbstract(getName.getModifiers()));
        assertEquals(String.class, getName.getReturnType());

        // Optional property should have setter
        Method setName = interfaceClass.getMethod("setName", String.class);
        assertNotNull(setName);
        assertTrue(Modifier.isAbstract(setName.getModifiers()));
        assertEquals(void.class, setName.getReturnType());
    }
}
