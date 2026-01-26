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
import java.lang.reflect.TypeVariable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 6: Generic Interfaces Tests.
 * Tests generic type parameter handling in interface declarations.
 */
public class TestCompileAstTsInterfaceDeclGenerics extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultipleTypeParameters(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with multiple type parameters
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Pair<K, V> {
                    key: K
                    value: V
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Pair"));

        assertTrue(interfaceClass.isInterface());

        // Should have two type parameters
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(2, typeParams.length);
        assertEquals("K", typeParams[0].getName());
        assertEquals("V", typeParams[1].getName());

        // Property getters should exist (returning Object due to type erasure)
        assertNotNull(interfaceClass.getMethod("getKey"));
        assertNotNull(interfaceClass.getMethod("getValue"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceSingleTypeParameter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with single type parameter
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Container<T> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Container"));

        assertTrue(interfaceClass.isInterface());

        // Should have one type parameter named "T"
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(1, typeParams.length);
        assertEquals("T", typeParams[0].getName());

        // Methods should exist (returning Object due to type erasure)
        Method getContent = interfaceClass.getMethod("getContent");
        assertNotNull(getContent);
        assertTrue(Modifier.isAbstract(getContent.getModifiers()));
        // Return type is Object due to erasure
        assertEquals(Object.class, getContent.getReturnType());

        Method setContent = interfaceClass.getMethod("setContent", Object.class);
        assertNotNull(setContent);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterInMethod(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter used in method signature
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Processor<T> {
                    process(input: T): T
                    transform(input: T, fn: String): T
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Processor"));

        assertTrue(interfaceClass.isInterface());

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(1, typeParams.length);
        assertEquals("T", typeParams[0].getName());

        // Method with generic parameter and return type (erased to Object)
        Method process = interfaceClass.getMethod("process", Object.class);
        assertNotNull(process);
        assertEquals(Object.class, process.getReturnType());

        // Method with mixed parameter types
        Method transform = interfaceClass.getMethod("transform", Object.class, String.class);
        assertNotNull(transform);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterInProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter used in property
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Box<T> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Box"));

        assertTrue(interfaceClass.isInterface());

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(1, typeParams.length);
        assertEquals("T", typeParams[0].getName());

        // Property should have getter and setter (erased to Object)
        Method getContent = interfaceClass.getMethod("getContent");
        assertNotNull(getContent);
        assertEquals(Object.class, getContent.getReturnType());

        Method setContent = interfaceClass.getMethod("setContent", Object.class);
        assertNotNull(setContent);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterWithBound(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter with extends constraint
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface NumericContainer<T extends Number> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.NumericContainer"));

        assertTrue(interfaceClass.isInterface());

        // Should have type parameter T with bound
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(1, typeParams.length);
        assertEquals("T", typeParams[0].getName());

        // The bound should be Number
        var bounds = typeParams[0].getBounds();
        assertEquals(1, bounds.length);
        assertEquals(Number.class, bounds[0]);

        // Property getter should exist (returning Object due to type erasure)
        assertNotNull(interfaceClass.getMethod("getContent"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMixedGenericAndConcreteTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with mix of generic and concrete types
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Repository<T> {
                    id: int
                    data: T
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Repository"));

        assertTrue(interfaceClass.isInterface());

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertEquals(1, typeParams.length);
        assertEquals("T", typeParams[0].getName());

        // Concrete type methods (from property)
        Method getId = interfaceClass.getMethod("getId");
        assertNotNull(getId);
        assertEquals(int.class, getId.getReturnType());

        Method setId = interfaceClass.getMethod("setId", int.class);
        assertNotNull(setId);

        // Generic type methods (erased to Object, from property)
        Method getData = interfaceClass.getMethod("getData");
        assertNotNull(getData);
        assertEquals(Object.class, getData.getReturnType());

        Method setData = interfaceClass.getMethod("setData", Object.class);
        assertNotNull(setData);
    }
}
