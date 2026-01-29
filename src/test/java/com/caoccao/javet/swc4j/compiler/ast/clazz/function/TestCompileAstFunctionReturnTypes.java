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
import static org.assertj.core.api.Assertions.within;


public class TestCompileAstFunctionReturnTypes extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEarlyReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      if (x < 0) return 0
                      return x * 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("test", -5)).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("test", 5)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleReturnStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sign(x: int): int {
                      if (x < 0) return -1
                      if (x == 0) return 0
                      return 1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("sign", -10)).isEqualTo(-1);
        assertThat((int) instanceRunner.invoke("sign", 0)).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("sign", 10)).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    isTrue(): boolean {
                      return true
                    }
                    isFalse(): boolean {
                      return false
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("isTrue")).isTrue();
        assertThat((boolean) instanceRunner.invoke("isFalse")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 3.14159
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(3.14159, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    findFirst(arr: int[], target: int): int {
                      for (let i: int = 0; i < arr.length; i++) {
                        if (arr[i] == target) return i
                      }
                      return -1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("findFirst", new int[]{1, 2, 3, 4, 5}, 3)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("findFirst", new int[]{1, 2, 3, 4, 5}, 10)).isEqualTo(-1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInt(JdkVersion jdkVersion) throws Exception {
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
    public void testReturnLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 9999999999
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(9999999999L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world"
                    }
                  }
                }""");
        assertThat((String) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo("hello world");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnVoid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): void {
                      const x: int = 1
                    }
                  }
                }""");
        assertThat((Object) runner.createInstanceRunner("com.A").invoke("test")).isNull();
    }
}
