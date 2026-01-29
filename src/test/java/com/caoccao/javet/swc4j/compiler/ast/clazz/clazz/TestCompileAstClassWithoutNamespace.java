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
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstClassWithoutNamespace extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class A {
                  test(): int {
                    return 42
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("A").invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceAndField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class Counter {
                  count: int = 0
                  increment(): void {
                    this.count = this.count + 1
                  }
                  getCount(): int {
                    return this.count
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("Counter");
        instanceRunner.invoke("increment");
        instanceRunner.invoke("increment");
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceCallingAnotherClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class Calculator {
                  add(a: int, b: int): int {
                    return a + b
                  }
                }
                export class User {
                  compute(): int {
                    const calc = new Calculator()
                    return calc.add(10, 20)
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("User").invoke("compute")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class Empty {
                }""");
        var instanceRunner = runner.createInstanceRunner("Empty");
        assertThat(instanceRunner.getInstance()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class Math {
                  add(a: int, b: int): int { return a + b }
                  sub(a: int, b: int): int { return a - b }
                  mul(a: int, b: int): int { return a * b }
                }""");
        assertThat((int) runner.createInstanceRunner("Math").invoke("add", 2, 3)).isEqualTo(5);
        assertThat((int) runner.createInstanceRunner("Math").invoke("sub", 10, 3)).isEqualTo(7);
        assertThat((int) runner.createInstanceRunner("Math").invoke("mul", 3, 4)).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithoutNamespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export class A {
                  value(): int { return 1 }
                }
                export class B {
                  value(): int { return 2 }
                }
                export class C {
                  value(): int { return 3 }
                }""");
        assertThat((int) runner.createInstanceRunner("A").invoke("value")).isEqualTo(1);
        assertThat((int) runner.createInstanceRunner("B").invoke("value")).isEqualTo(2);
        assertThat((int) runner.createInstanceRunner("C").invoke("value")).isEqualTo(3);
    }

}
