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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 2: Interface Extension Tests.
 * Tests interface extends functionality.
 */
public class TestCompileAstTsInterfaceDeclExtends extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceDiamondInheritance(JdkVersion jdkVersion) throws Exception {
        // Test: Diamond inheritance pattern (D extends B, C; B extends A; C extends A)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface A {
                    id: int
                  }
                  export interface B extends A {
                    name: String
                  }
                  export interface C extends A {
                    active: boolean
                  }
                  export interface D extends B, C {
                    value: double
                  }
                }""");
        Class<?> a = runner.getClass("com.A");
        Class<?> b = runner.getClass("com.B");
        Class<?> c = runner.getClass("com.C");
        Class<?> d = runner.getClass("com.D");

        // D should extend both B and C
        Set<Class<?>> interfaces = Arrays.stream(d.getInterfaces()).collect(Collectors.toSet());
        assertThat(interfaces.contains(b)).isTrue();
        assertThat(interfaces.contains(c)).isTrue();

        // D should have getValue method
        assertThat(d.getMethod("getValue")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExtendsChain(JdkVersion jdkVersion) throws Exception {
        // Test: Chain of interface inheritance (A extends B extends C)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Base {
                    id: int
                  }
                  export interface Middle extends Base {
                    name: String
                  }
                  export interface Derived extends Middle {
                    active: boolean
                  }
                }""");
        Class<?> base = runner.getClass("com.Base");
        Class<?> middle = runner.getClass("com.Middle");
        Class<?> derived = runner.getClass("com.Derived");

        assertThat(base.isInterface()).isTrue();
        assertThat(middle.isInterface()).isTrue();
        assertThat(derived.isInterface()).isTrue();

        // Middle extends Base
        assertThat(Arrays.asList(middle.getInterfaces()).contains(base)).isTrue();

        // Derived extends Middle (but not directly Base)
        assertThat(Arrays.asList(derived.getInterfaces()).contains(middle)).isTrue();

        // Derived should have isActive method
        assertThat(derived.getMethod("isActive")).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExtendsMultiple(JdkVersion jdkVersion) throws Exception {
        // Test: Interface extending multiple interfaces
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Named {
                    name: String
                  }
                  export interface Aged {
                    age: int
                  }
                  export interface Person extends Named, Aged {
                    id: int
                  }
                }""");
        Class<?> named = runner.getClass("com.Named");
        Class<?> aged = runner.getClass("com.Aged");
        Class<?> person = runner.getClass("com.Person");

        assertThat(named.isInterface()).isTrue();
        assertThat(aged.isInterface()).isTrue();
        assertThat(person.isInterface()).isTrue();

        // Person should extend both Named and Aged
        Set<Class<?>> interfaces = Arrays.stream(person.getInterfaces()).collect(Collectors.toSet());
        assertThat(interfaces.contains(named)).isTrue();
        assertThat(interfaces.contains(aged)).isTrue();

        // Person should have getId/setId
        assertThat(person.getMethod("getId")).isNotNull();
        assertThat(person.getMethod("setId", int.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExtendsSingle(JdkVersion jdkVersion) throws Exception {
        // Test: Interface extending a single interface
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Named {
                    name: String
                  }
                  export interface Person extends Named {
                    age: int
                  }
                }""");
        Class<?> named = runner.getClass("com.Named");
        Class<?> person = runner.getClass("com.Person");

        assertThat(named.isInterface()).isTrue();
        assertThat(person.isInterface()).isTrue();

        // Person should extend Named
        assertThat(Arrays.asList(person.getInterfaces()).contains(named)).isTrue();

        // Named should have getName/setName
        assertThat(named.getMethod("getName")).isNotNull();
        assertThat(named.getMethod("setName", String.class)).isNotNull();

        // Person should have getAge/setAge
        assertThat(person.getMethod("getAge")).isNotNull();
        assertThat(person.getMethod("setAge", int.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceExtendsWithMethods(JdkVersion jdkVersion) throws Exception {
        // Test: Extending interface with methods
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Comparable {
                    compareTo(other: Object): int
                  }
                  export interface SortableItem extends Comparable {
                    sortKey: String
                    priority: int
                  }
                }""");
        Class<?> comparable = runner.getClass("com.Comparable");
        Class<?> sortableItem = runner.getClass("com.SortableItem");

        assertThat(comparable.isInterface()).isTrue();
        assertThat(sortableItem.isInterface()).isTrue();

        // Comparable should have compareTo
        assertThat(comparable.getMethod("compareTo", Object.class)).isNotNull();

        // SortableItem should extend Comparable
        assertThat(Arrays.asList(sortableItem.getInterfaces()).contains(comparable)).isTrue();

        // SortableItem should have its own methods
        assertThat(sortableItem.getMethod("getSortKey")).isNotNull();
        assertThat(sortableItem.getMethod("getPriority")).isNotNull();
    }
}
