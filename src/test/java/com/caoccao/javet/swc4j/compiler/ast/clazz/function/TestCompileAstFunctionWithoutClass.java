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

package com.caoccao.javet.swc4j.compiler.ast.clazz.function;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestCompileAstFunctionWithoutClass extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStandaloneFunctions(JdkVersion jdkVersion) throws Exception {
        // Multiple standalone functions should all be in the same dummy class $
        var runner = getCompiler(jdkVersion).compile("""
                export function add(a: int, b: int): int {
                  return a + b
                }
                export function sub(a: int, b: int): int {
                  return a - b
                }
                export function mul(a: int, b: int): int {
                  return a * b
                }""");
        var staticRunner = runner.createStaticRunner("$");
        assertThat((int) staticRunner.invoke("add", 10, 5)).isEqualTo(15);
        assertThat((int) staticRunner.invoke("sub", 10, 5)).isEqualTo(5);
        assertThat((int) staticRunner.invoke("mul", 10, 5)).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionInDummyClass(JdkVersion jdkVersion) throws Exception {
        // Standalone function without a class is compiled into dummy class $
        var runner = getCompiler(jdkVersion).compile("""
                export function add(a: int, b: int): int {
                  return a + b
                }""");
        var staticRunner = runner.createStaticRunner("$");
        assertThat((int) staticRunner.invoke("add", 10, 20)).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionInNamespace(JdkVersion jdkVersion) throws Exception {
        // Standalone function in namespace is compiled into namespace.$
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function multiply(a: int, b: int): int {
                    return a * b
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.$");
        assertThat((int) staticRunner.invoke("multiply", 5, 10)).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenDollarClassExists(JdkVersion jdkVersion) throws Exception {
        // When class $ already exists, use $1 for standalone functions
        var runner = getCompiler(jdkVersion).compile("""
                export class $ {
                  getValue(): int { return 100 }
                }
                export function helper(): int {
                  return 42
                }""");
        // Class $ should exist with its own method
        var instanceRunner = runner.createInstanceRunner("$");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(100);
        // Function should be in $1
        var staticRunner = runner.createStaticRunner("$1");
        assertThat((int) staticRunner.invoke("helper")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenDollarClassExistsInNamespace(JdkVersion jdkVersion) throws Exception {
        // When class $ already exists in namespace, use $1 for standalone functions
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class $ {
                    getValue(): int { return 200 }
                  }
                  export function helper(): int {
                    return 84
                  }
                }""");
        // Class com.$ should exist
        var instanceRunner = runner.createInstanceRunner("com.$");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(200);
        // Function should be in com.$1
        var staticRunner = runner.createStaticRunner("com.$1");
        assertThat((int) staticRunner.invoke("helper")).isEqualTo(84);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenMultipleDollarClassesExist(JdkVersion jdkVersion) throws Exception {
        // When classes $ and $1 exist, use $2 for standalone functions
        var runner = getCompiler(jdkVersion).compile("""
                export class $ {
                  getValue(): int { return 1 }
                }
                export class $1 {
                  getValue(): int { return 2 }
                }
                export function helper(): int {
                  return 3
                }""");
        var instanceRunner = runner.createInstanceRunner("$");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(1);
        var instanceRunner1 = runner.createInstanceRunner("$1");
        assertThat((int) instanceRunner1.invoke("getValue")).isEqualTo(2);
        var staticRunner = runner.createStaticRunner("$2");
        assertThat((int) staticRunner.invoke("helper")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWithClass(JdkVersion jdkVersion) throws Exception {
        // Standalone function alongside a regular class
        var runner = getCompiler(jdkVersion).compile("""
                export class Calculator {
                  add(a: int, b: int): int {
                    return a + b
                  }
                }
                export function helper(x: int): int {
                  return x * 2
                }""");
        // Regular class
        var instanceRunner = runner.createInstanceRunner("Calculator");
        assertThat((int) instanceRunner.invoke("add", 10, 20)).isEqualTo(30);
        // Standalone function in $
        var staticRunner = runner.createStaticRunner("$");
        assertThat((int) staticRunner.invoke("helper", 10)).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWithDefaultParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export function add(a: int, b: int = 10, c: int = 20): int {
                  return a + b + c
                }""");
        var staticRunner = runner.createStaticRunner("$");
        assertThat(
                Map.of(
                        "all", staticRunner.invoke("add", 1, 2, 3),
                        "oneDefault", staticRunner.invoke("add", 1, 2),
                        "twoDefaults", staticRunner.invoke("add", 1))
        ).isEqualTo(
                Map.of("all", 6, "oneDefault", 23, "twoDefaults", 31)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWithInvalidDefaultOrderShouldFail(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                export function add(a: int = 1, b: int): int {
                  return a + b
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Default parameters must come after all required parameters");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneVarargsFunctionHasVarargsFlag(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export function sum(...values: int[]): int {
                  return values.length
                }""");
        Class<?> dummyClass = runner.getClass("$");
        assertThat(dummyClass.getDeclaredMethod("sum", int[].class).isVarArgs()).isTrue();
        assertThat((int) runner.createStaticRunner("$").invoke("sum", new int[]{1, 2, 3})).isEqualTo(3);
    }
}
