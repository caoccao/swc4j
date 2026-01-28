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

import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(Modifier.isPrivate(helperMethod.getModifiers()), "#helper should be private");

        // Test functionality
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("getValue"));
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

        assertTrue(Modifier.isPrivate(privateMethod.getModifiers()), "#privateHelper should be private");
        assertTrue(Modifier.isPublic(publicMethod.getModifiers()), "publicMethod should be public");

        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("combined"));
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
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("getValue"));
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
        assertEquals(11, (int) runner.createInstanceRunner("com.A").invoke("getValue"));
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
        assertEquals("Hello, World!", runner.createInstanceRunner("com.A").invoke("greet", "World"));
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
        assertEquals(true, instanceRunner.invoke("checkEven", 4));
        assertEquals(false, instanceRunner.invoke("checkEven", 5));
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
        assertEquals(1, (int) instanceRunner.invoke("computeFactorial", 0));
        assertEquals(1, (int) instanceRunner.invoke("computeFactorial", 1));
        assertEquals(120, (int) instanceRunner.invoke("computeFactorial", 5));
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
        assertEquals(40, (int) runner.createInstanceRunner("com.A").invoke("quadruple", 10));
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
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("compute", 10, 20));
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
        assertEquals(0, (int) instanceRunner.invoke("getValue"));
        instanceRunner.invoke("addOne");
        assertEquals(1, (int) instanceRunner.invoke("getValue"));
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
        assertTrue(Modifier.isPrivate(helperMethod.getModifiers()), "#helper should be private");
        assertTrue(Modifier.isStatic(helperMethod.getModifiers()), "#helper should be static");

        assertEquals(99, (int) runner.createStaticRunner("com.A").invoke("getValue"));
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
        assertEquals(0, (int) staticRunner.invoke("next"));
        assertEquals(1, (int) staticRunner.invoke("next"));
        assertEquals(2, (int) staticRunner.invoke("next"));
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
        assertEquals(200, (int) runner.createStaticRunner("com.A").invoke("compute", 10, 20));
    }
}
