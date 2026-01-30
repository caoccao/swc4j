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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 6: Generic Interfaces Tests.
 * Tests generic type parameter handling in interface declarations.
 */
public class TestCompileAstTsInterfaceDeclGenerics extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultipleTypeParameters(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with multiple type parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Pair<K, V> {
                    key: K
                    value: V
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Pair");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have two type parameters
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(2);
        assertThat(typeParams[0].getName()).isEqualTo("K");
        assertThat(typeParams[1].getName()).isEqualTo("V");

        // Property getters should exist (returning Object due to type erasure)
        assertThat(interfaceClass.getMethod("getKey")).isNotNull();
        assertThat(interfaceClass.getMethod("getValue")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceSingleTypeParameter(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with single type parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Container<T> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Container");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have one type parameter named "T"
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(1);
        assertThat(typeParams[0].getName()).isEqualTo("T");

        // Methods should exist (returning Object due to type erasure)
        Method getContent = interfaceClass.getMethod("getContent");
        assertThat(getContent).isNotNull();
        assertThat(Modifier.isAbstract(getContent.getModifiers())).isTrue();
        // Return type is Object due to erasure
        assertThat(getContent.getReturnType()).isEqualTo(Object.class);

        Method setContent = interfaceClass.getMethod("setContent", Object.class);
        assertThat(setContent).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterInMethod(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter used in method signature
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Processor<T> {
                    process(input: T): T
                    transform(input: T, fn: String): T
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Processor");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(1);
        assertThat(typeParams[0].getName()).isEqualTo("T");

        // Method with generic parameter and return type (erased to Object)
        Method process = interfaceClass.getMethod("process", Object.class);
        assertThat(process).isNotNull();
        assertThat(process.getReturnType()).isEqualTo(Object.class);

        // Method with mixed parameter types
        Method transform = interfaceClass.getMethod("transform", Object.class, String.class);
        assertThat(transform).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterInProperty(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter used in property
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Box<T> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Box");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(1);
        assertThat(typeParams[0].getName()).isEqualTo("T");

        // Property should have getter and setter (erased to Object)
        Method getContent = interfaceClass.getMethod("getContent");
        assertThat(getContent).isNotNull();
        assertThat(getContent.getReturnType()).isEqualTo(Object.class);

        Method setContent = interfaceClass.getMethod("setContent", Object.class);
        assertThat(setContent).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceTypeParameterWithBound(JdkVersion jdkVersion) throws Exception {
        // Test: Type parameter with extends constraint
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface NumericContainer<T extends Number> {
                    content: T
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.NumericContainer");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have type parameter T with bound
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(1);
        assertThat(typeParams[0].getName()).isEqualTo("T");

        // The bound should be Number
        var bounds = typeParams[0].getBounds();
        assertThat(bounds.length).isEqualTo(1);
        assertThat(bounds[0]).isEqualTo(Number.class);

        // Property getter should exist (returning Object due to type erasure)
        assertThat(interfaceClass.getMethod("getContent")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMixedGenericAndConcreteTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with mix of generic and concrete types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Repository<T> {
                    id: int
                    data: T
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Repository");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Should have type parameter T
        TypeVariable<?>[] typeParams = interfaceClass.getTypeParameters();
        assertThat(typeParams.length).isEqualTo(1);
        assertThat(typeParams[0].getName()).isEqualTo("T");

        // Concrete type methods (from property)
        Method getId = interfaceClass.getMethod("getId");
        assertThat(getId).isNotNull();
        assertThat(getId.getReturnType()).isEqualTo(int.class);

        Method setId = interfaceClass.getMethod("setId", int.class);
        assertThat(setId).isNotNull();

        // Generic type methods (erased to Object, from property)
        Method getData = interfaceClass.getMethod("getData");
        assertThat(getData).isNotNull();
        assertThat(getData.getReturnType()).isEqualTo(Object.class);

        Method setData = interfaceClass.getMethod("setData", Object.class);
        assertThat(setData).isNotNull();
    }
}
