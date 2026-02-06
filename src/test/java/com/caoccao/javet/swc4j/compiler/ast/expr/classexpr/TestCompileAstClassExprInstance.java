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

package com.caoccao.javet.swc4j.compiler.ast.expr.classexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for class expression instance creation and usage.
 * Covers: instance use, instance values, multiple methods, field computation,
 * conditional methods, method using field, multiple instances, boolean/string/double fields.
 */
public class TestCompileAstClassExprInstance extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprBooleanField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const obj = new (class {
                        active: boolean = true
                        isActive(): boolean { return this.active }
                      })()
                      return obj.isActive()
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprDoubleField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const obj = new (class {
                        pi: double = 3.14159
                        getPi(): double { return this.pi }
                      })()
                      return obj.getPi()
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test"))
                .isCloseTo(3.14159, org.assertj.core.data.Offset.offset(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprFieldComputation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj = new (class {
                        x: int = 10
                        y: int = 20
                
                        sum(): int { return this.x + this.y }
                        diff(): int { return this.x - this.y }
                      })()
                      return [obj.sum(), obj.diff()]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(30, -10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprInstanceUse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj = new (class {
                        value: int = 3
                
                        getValue(): int {
                          return this.value
                        }
                      })()
                      return [obj.value, obj.getValue()]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(List.of(3, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprInstanceValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj = new (class {
                        value: int = 4
                
                        getValue(): int {
                          return this.value + 1
                        }
                      })()
                      return [obj.value, obj.getValue()]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(List.of(4, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprMethodUsingField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const obj = new (class {
                        factor: int = 5
                
                        scale(x: int): int {
                          return x * this.factor
                        }
                      })()
                      return obj.scale(7)
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(35);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprMethodWithConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj = new (class {
                        abs(x: int): int {
                          if (x >= 0) return x
                          return -x
                        }
                      })()
                      return [obj.abs(5), obj.abs(-5), obj.abs(0)]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(5, 5, 0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprMultipleInstances(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj1 = new (class {
                        value: int = 10
                        getValue(): int { return this.value }
                      })()
                      const obj2 = new (class {
                        value: int = 20
                        getValue(): int { return this.value }
                      })()
                      return [obj1.getValue(), obj2.getValue()]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(10, 20));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const obj = new (class {
                        add(a: int, b: int): int { return a + b }
                        sub(a: int, b: int): int { return a - b }
                        mul(a: int, b: int): int { return a * b }
                      })()
                      return [obj.add(10, 3), obj.sub(10, 3), obj.mul(10, 3)]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(13, 7, 30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprStringField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = new (class {
                        name: String = "World"
                        greet(): String { return "Hello, " + this.name }
                      })()
                      return obj.greet()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<String>invoke("test"))
                .isEqualTo("Hello, World");
    }
}
