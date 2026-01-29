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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstClassImplements extends BaseTestCompileSuite {

    private ByteCodeCompiler createCompilerWithInterfaces(JdkVersion jdkVersion) {
        Map<String, String> typeAliases = new HashMap<>();
        typeAliases.put("boolean", "boolean");
        typeAliases.put("int", "int");
        typeAliases.put("double", "double");
        typeAliases.put("void", "void");
        typeAliases.put("String", "java.lang.String");
        typeAliases.put("Object", "java.lang.Object");
        typeAliases.put("Runnable", "java.lang.Runnable");
        typeAliases.put("Serializable", "java.io.Serializable");
        typeAliases.put("Comparable", "java.lang.Comparable");
        typeAliases.put("Comparator", "java.util.Comparator");

        return ByteCodeCompiler.of(ByteCodeCompilerOptions.builder()
                .jdkVersion(jdkVersion)
                .typeAliasMap(typeAliases)
                .debug(true)
                .build());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExtendsAndImplements(JdkVersion jdkVersion) throws Exception {
        var compiler = createCompilerWithInterfaces(jdkVersion);
        var runner = compiler.compile("""
                namespace com {
                  export class Base {
                    getValue(): int { return 42 }
                  }
                  export class Derived extends Base implements Runnable {
                    run(): void { }
                  }
                }""");
        Class<?> classDerived = runner.getClass("com.Derived");

        // Verify inheritance and interface implementation
        assertThat(Runnable.class.isAssignableFrom(classDerived)).as("Derived should implement Runnable").isTrue();
        Class<?> classBase = runner.getClass("com.Base");
        assertThat(classBase.isAssignableFrom(classDerived)).as("Derived should extend Base").isTrue();

        // Test methods
        assertThat(
                List.of(
                        (int) runner.createInstanceRunner("com.Derived").invoke("getValue")
                )
        ).isEqualTo(
                List.of(42)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleInterfaceImplementation(JdkVersion jdkVersion) throws Exception {
        var compiler = createCompilerWithInterfaces(jdkVersion);
        var runner = compiler.compile("""
                namespace com {
                  export class Multi implements Runnable, Serializable {
                    run(): void { }
                  }
                }""");
        Class<?> classMulti = runner.getClass("com.Multi");

        // Verify both interfaces are implemented
        assertThat(Runnable.class.isAssignableFrom(classMulti)).as("Multi should implement Runnable").isTrue();
        assertThat(Serializable.class.isAssignableFrom(classMulti)).as("Multi should implement Serializable").isTrue();

        // Create instance and call run
        runner.createInstanceRunner("com.Multi").invoke("run");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleInterfaceImplementation(JdkVersion jdkVersion) throws Exception {
        var compiler = createCompilerWithInterfaces(jdkVersion);
        var runner = compiler.compile("""
                namespace com {
                  export class A implements Runnable {
                    run(): void { }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify A implements Runnable
        assertThat(Runnable.class.isAssignableFrom(classA)).as("A should implement Runnable").isTrue();

        // Create instance and call run()
        runner.createInstanceRunner("com.A").invoke("run");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleInterfaceWithMethod(JdkVersion jdkVersion) throws Exception {
        var compiler = createCompilerWithInterfaces(jdkVersion);
        var runner = compiler.compile("""
                namespace com {
                  export class Counter implements Runnable {
                    count: int = 0
                    run(): void {
                      this.count = this.count + 1
                    }
                    getCount(): int { return this.count }
                  }
                }""");
        Class<?> classCounter = runner.getClass("com.Counter");

        // Verify Counter implements Runnable
        assertThat(Runnable.class.isAssignableFrom(classCounter)).as("Counter should implement Runnable").isTrue();

        // Test run() increments count
        var instanceRunner = runner.createInstanceRunner("com.Counter");
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(0);

        // Call run() multiple times
        instanceRunner.invoke("run");
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(1);

        instanceRunner.invoke("run");
        instanceRunner.invoke("run");
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(3);
    }
}
