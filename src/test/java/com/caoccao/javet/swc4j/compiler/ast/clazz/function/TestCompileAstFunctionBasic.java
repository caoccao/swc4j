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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstFunctionBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionCallingAnotherMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    helper(): int {
                      return 10
                    }
                    test(): int {
                      return this.helper() + 5
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionReturningVoidExplicit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): void {
                      return
                    }
                  }
                }""");
        assertThat((Object) runner.createInstanceRunner("com.A").invoke("test")).isNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionWithNoParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 42
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFunctionWithOnlyReturnStatement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("getValue")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMutuallyRecursiveFunctions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    isEven(n: int): boolean {
                      if (n == 0) return true
                      return this.isOdd(n - 1)
                    }
                    isOdd(n: int): boolean {
                      if (n == 0) return false
                      return this.isEven(n - 1)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("isEven", 4)).isTrue();
        assertThat((boolean) instanceRunner.invoke("isEven", 3)).isFalse();
        assertThat((boolean) instanceRunner.invoke("isOdd", 5)).isTrue();
        assertThat((boolean) instanceRunner.invoke("isOdd", 6)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecursiveFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    factorial(n: int): int {
                      if (n <= 1) return 1
                      return n * this.factorial(n - 1)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("factorial", 5)).isEqualTo(120);
        assertThat((int) instanceRunner.invoke("factorial", 0)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("factorial", 1)).isEqualTo(1);
    }
}
