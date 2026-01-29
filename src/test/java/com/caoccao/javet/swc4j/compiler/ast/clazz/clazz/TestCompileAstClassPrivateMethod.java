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

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for ES2022 private methods (#method syntax).
 */
public class TestCompileAstClassPrivateMethod extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicPrivateMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #helper(): int {
                      return 42
                    }
                    getValue(): int {
                      return this.#helper()
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify method is private
        var helperMethod = classA.getDeclaredMethod("helper");
        assertThat(Modifier.isPrivate(helperMethod.getModifiers())).as("#helper should be private").isTrue();

        // Test functionality
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedPrivateAndPublicMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #privateHelper(): int {
                      return 10
                    }
                    publicMethod(): int {
                      return 20
                    }
                    combined(): int {
                      return this.#privateHelper() + this.publicMethod()
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        var privateMethod = classA.getDeclaredMethod("privateHelper");
        var publicMethod = classA.getDeclaredMethod("publicMethod");

        assertThat(Modifier.isPrivate(privateMethod.getModifiers())).as("#privateHelper should be private").isTrue();
        assertThat(Modifier.isPublic(publicMethod.getModifiers())).as("publicMethod should be public").isTrue();

        assertThat((int) runner.createInstanceRunner("com.A").invoke("combined")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodAccessingPrivateField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 100
                    #getInternalValue(): int {
                      return this.#value
                    }
                    getValue(): int {
                      return this.#getInternalValue()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodCallingAnotherPrivateMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #innerHelper(): int {
                      return 5
                    }
                    #outerHelper(): int {
                      return this.#innerHelper() * 2
                    }
                    getValue(): int {
                      return this.#outerHelper() + 1
                    }
                  }
                }""");
        // 5 * 2 + 1 = 11
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(11);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodReturningString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #formatMessage(name: String): String {
                      return "Hello, " + name + "!"
                    }
                    greet(name: String): String {
                      return this.#formatMessage(name)
                    }
                  }
                }""");
        assertThat((String) runner.createInstanceRunner("com.A").invoke("greet", "World")).isEqualTo("Hello, World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodWithConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #isEven(n: int): boolean {
                      return n % 2 == 0
                    }
                    checkEven(n: int): boolean {
                      return this.#isEven(n)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("checkEven", 4)).isTrue();
        assertThat((boolean) instanceRunner.invoke("checkEven", 5)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodWithLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #factorial(n: int): int {
                      let result: int = 1
                      let i: int = 1
                      while (i <= n) {
                        result = result * i
                        i = i + 1
                      }
                      return result
                    }
                    computeFactorial(n: int): int {
                      return this.#factorial(n)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("computeFactorial", 0)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("computeFactorial", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("computeFactorial", 5)).isEqualTo(120);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodWithMultipleCalls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #double(x: int): int {
                      return x * 2
                    }
                    quadruple(x: int): int {
                      return this.#double(this.#double(x))
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("quadruple", 10)).isEqualTo(40);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodWithParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #add(a: int, b: int): int {
                      return a + b
                    }
                    compute(x: int, y: int): int {
                      return this.#add(x, y)
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("compute", 10, 20)).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethodWithVoidReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    #value: int = 0
                    #increment(): void {
                      this.#value = this.#value + 1
                    }
                    addOne(): void {
                      this.#increment()
                    }
                    getValue(): int {
                      return this.#value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(0);
        instanceRunner.invoke("addOne");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #helper(): int {
                      return 99
                    }
                    static getValue(): int {
                      return A.#helper()
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify method is private and static
        var helperMethod = classA.getDeclaredMethod("helper");
        assertThat(Modifier.isPrivate(helperMethod.getModifiers())).as("#helper should be private").isTrue();
        assertThat(Modifier.isStatic(helperMethod.getModifiers())).as("#helper should be static").isTrue();

        assertThat((int) runner.createStaticRunner("com.A").invoke("getValue")).isEqualTo(99);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateMethodAccessingStaticField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #counter: int = 0
                    static #getAndIncrement(): int {
                      let result: int = A.#counter
                      A.#counter = A.#counter + 1
                      return result
                    }
                    static next(): int {
                      return A.#getAndIncrement()
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        assertThat((int) staticRunner.invoke("next")).isEqualTo(0);
        assertThat((int) staticRunner.invoke("next")).isEqualTo(1);
        assertThat((int) staticRunner.invoke("next")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateMethodWithParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #multiply(a: int, b: int): int {
                      return a * b
                    }
                    static compute(x: int, y: int): int {
                      return A.#multiply(x, y)
                    }
                  }
                }""");
        assertThat((int) runner.createStaticRunner("com.A").invoke("compute", 10, 20)).isEqualTo(200);
    }
}
