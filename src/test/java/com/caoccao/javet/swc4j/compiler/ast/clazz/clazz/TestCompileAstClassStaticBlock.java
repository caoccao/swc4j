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


/**
 * Tests for ES2022 static blocks (static { } syntax).
 */
public class TestCompileAstClassStaticBlock extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicStaticBlock(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static value: int = 0
                    static {
                      A.value = 42
                    }
                    static getValue(): int { return A.value }
                  }
                }""");
        assertThat((int) runner.createStaticRunner("com.A").invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStaticBlocks(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static value: int = 0
                    static {
                      A.value = 10
                    }
                    static {
                      A.value = A.value + 20
                    }
                    static {
                      A.value = A.value * 2
                    }
                    static getValue(): int { return A.value }
                  }
                }""");
        // 0 -> 10 -> 30 -> 60
        assertThat((int) runner.createStaticRunner("com.A").invoke("getValue")).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockComputation(JdkVersion jdkVersion) throws Exception {
        // Test complex computation in static block
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Fibonacci {
                    static fib10: int = 0
                    static {
                      let a: int = 0
                      let b: int = 1
                      let i: int = 0
                      while (i < 10) {
                        let temp: int = a + b
                        a = b
                        b = temp
                        i = i + 1
                      }
                      Fibonacci.fib10 = a
                    }
                    static getFib10(): int { return Fibonacci.fib10 }
                  }
                }""");
        // 10th Fibonacci number (0-indexed): 0,1,1,2,3,5,8,13,21,34,55
        // After 10 iterations: a = 55
        assertThat((int) runner.createStaticRunner("com.Fibonacci").invoke("getFib10")).isEqualTo(55);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockInitializingPrivateStaticField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static #secret: int = 0
                    static {
                      A.#secret = 999
                    }
                    static getSecret(): int { return A.#secret }
                  }
                }""");
        assertThat((int) runner.createStaticRunner("com.A").invoke("getSecret")).isEqualTo(999);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockInterleavedWithFields(JdkVersion jdkVersion) throws Exception {
        // Test that static blocks and static field initializers are processed in declaration order
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static a: int = 1
                    static {
                      A.a = A.a * 10
                    }
                    static b: int = A.a + 5
                    static {
                      A.b = A.b * 2
                    }
                    static getA(): int { return A.a }
                    static getB(): int { return A.b }
                  }
                }""");
        // a: 1 -> 10
        // b: 10 + 5 = 15 -> 30
        assertThat((int) runner.createStaticRunner("com.A").invoke("getA")).isEqualTo(10);
        assertThat((int) runner.createStaticRunner("com.A").invoke("getB")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockWithConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static value: int = 5
                    static result: int = 0
                    static {
                      if (A.value > 3) {
                        A.result = 100
                      } else {
                        A.result = 50
                      }
                    }
                    static getResult(): int { return A.result }
                  }
                }""");
        assertThat((int) runner.createStaticRunner("com.A").invoke("getResult")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockWithLocalVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static result: int = 0
                    static {
                      let x: int = 10
                      let y: int = 20
                      A.result = x + y
                    }
                    static getResult(): int { return A.result }
                  }
                }""");
        assertThat((int) runner.createStaticRunner("com.A").invoke("getResult")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockWithLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static sum: int = 0
                    static {
                      let i: int = 1
                      while (i <= 5) {
                        A.sum = A.sum + i
                        i = i + 1
                      }
                    }
                    static getSum(): int { return A.sum }
                  }
                }""");
        // 1 + 2 + 3 + 4 + 5 = 15
        assertThat((int) runner.createStaticRunner("com.A").invoke("getSum")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticBlockWithoutStaticFields(JdkVersion jdkVersion) throws Exception {
        // Test static block can run even without static field initializers
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static initialized: boolean = false
                    static {
                      A.initialized = true
                    }
                    static isInitialized(): boolean { return A.initialized }
                  }
                }""");
        assertThat((boolean) runner.createStaticRunner("com.A").invoke("isInitialized")).isTrue();
    }
}
