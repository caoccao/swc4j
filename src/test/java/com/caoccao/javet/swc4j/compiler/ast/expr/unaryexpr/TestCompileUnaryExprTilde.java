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

package com.caoccao.javet.swc4j.compiler.ast.expr.unaryexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestCompileUnaryExprTilde extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeBooleanThrows(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const x: boolean = true
                          return ~x
                        }
                      }
                    }""");
        }).isInstanceOf(Exception.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: byte = 1
                      const y: int = ~x
                      return y
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeFloatThrows(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const x: float = 1.5
                          return ~x
                        }
                      }
                    }""");
        }).isInstanceOf(Exception.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 5
                      return ~x
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeIntegerWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Integer {
                      const x: Integer = 42
                      return ~x
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-43);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const x: long = 100
                      return ~x
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-101L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Long {
                      const x: Long = 5000
                      return ~x
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-5001L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTildeShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: short = 2
                      const y: int = ~x
                      return y
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-3);
    }
}
