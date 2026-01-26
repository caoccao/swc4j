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
 * Phase 9: Explicit Getter/Setter Signatures Tests.
 * Tests explicit get and set accessor declarations in interfaces.
 */
public class TestCompileAstTsInterfaceDeclAccessors extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitBooleanGetter(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit boolean getter (should use 'is' prefix)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Toggle {
                    get enabled(): boolean
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Toggle"));

        assertTrue(interfaceClass.isInterface());

        // Boolean getter should use 'is' prefix
        Method isEnabled = interfaceClass.getMethod("isEnabled");
        assertNotNull(isEnabled);
        assertTrue(Modifier.isAbstract(isEnabled.getModifiers()));
        assertEquals(boolean.class, isEnabled.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with explicit getter signature
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Valued {
                    get value(): int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Valued"));

        assertTrue(interfaceClass.isInterface());

        // Explicit getter should generate abstract getter method
        Method getValue = interfaceClass.getMethod("getValue");
        assertNotNull(getValue);
        assertTrue(Modifier.isAbstract(getValue.getModifiers()));
        assertEquals(int.class, getValue.getReturnType());

        // No setter should be generated
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setValue", int.class));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetterAndSetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both explicit getter and setter
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Counter {
                    get count(): int
                    set count(v: int)
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Counter"));

        assertTrue(interfaceClass.isInterface());

        // Getter should exist
        Method getCount = interfaceClass.getMethod("getCount");
        assertNotNull(getCount);
        assertTrue(Modifier.isAbstract(getCount.getModifiers()));
        assertEquals(int.class, getCount.getReturnType());

        // Setter should exist
        Method setCount = interfaceClass.getMethod("setCount", int.class);
        assertNotNull(setCount);
        assertTrue(Modifier.isAbstract(setCount.getModifiers()));
        assertEquals(void.class, setCount.getReturnType());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitGetterAndSetterWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit getter and setter with implementing class (using int to avoid double init issue)
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Counter");
        Class<?> implClass = classes.get("com.SimpleCounter");

        assertTrue(interfaceClass.isInterface());

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial value
        assertEquals(0, implClass.getMethod("getValue").invoke(instance));

        // Set value
        implClass.getMethod("setValue", int.class).invoke(instance, 42);

        // Get value
        assertEquals(42, implClass.getMethod("getValue").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExplicitSetter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with explicit setter signature only
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Writer {
                    set output(data: String)
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Writer"));

        assertTrue(interfaceClass.isInterface());

        // Explicit setter should generate abstract setter method
        Method setOutput = interfaceClass.getMethod("setOutput", String.class);
        assertNotNull(setOutput);
        assertTrue(Modifier.isAbstract(setOutput.getModifiers()));
        assertEquals(void.class, setOutput.getReturnType());

        // No getter should be generated
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("getOutput"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMixedAccessorsAndProperties(JdkVersion jdkVersion) throws Exception {
        // Test: Mix of explicit accessors and property signatures
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Config {
                    name: String
                    get version(): int
                    set debug(enabled: boolean)
                    readonly id: int
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Config"));

        assertTrue(interfaceClass.isInterface());

        // Property 'name' should have getter and setter
        assertNotNull(interfaceClass.getMethod("getName"));
        assertNotNull(interfaceClass.getMethod("setName", String.class));

        // Explicit getter 'version' should only have getter
        assertNotNull(interfaceClass.getMethod("getVersion"));
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setVersion", int.class));

        // Explicit setter 'debug' should only have setter
        assertNotNull(interfaceClass.getMethod("setDebug", boolean.class));
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("isDebug"));

        // Readonly 'id' should only have getter
        assertNotNull(interfaceClass.getMethod("getId"));
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setId", int.class));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultipleAccessors(JdkVersion jdkVersion) throws Exception {
        // Test: Multiple explicit accessors
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Point {
                    get x(): int
                    get y(): int
                    set x(v: int)
                    set y(v: int)
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Point"));

        assertTrue(interfaceClass.isInterface());

        // All getters and setters should exist
        assertNotNull(interfaceClass.getMethod("getX"));
        assertNotNull(interfaceClass.getMethod("getY"));
        assertNotNull(interfaceClass.getMethod("setX", int.class));
        assertNotNull(interfaceClass.getMethod("setY", int.class));
    }
}
