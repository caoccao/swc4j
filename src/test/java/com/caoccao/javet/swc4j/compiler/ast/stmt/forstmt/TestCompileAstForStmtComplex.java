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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for for loops with complex init/update expressions (Phase 4)
 * Tests complex initialization and update patterns
 */
public class TestCompileAstForStmtComplex extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexArithmeticUpdate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 1; i < 50; i = i * 2 + 1) {
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(57);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexInitExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      let sum: int = 0
                      for (let i: int = x * 2; i < 20; i++) {
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(145);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexTestCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      for (let i: int = 0, j: int = 10; i < 20 && j > 0; i++, j--) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexUpdateExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 1; i < 100; i = i * 2) {
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(127);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCountingBackwardWithMultipleVars(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 10, j: int = 0; i > j; i--, j++) {
                        sum += i - j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDependentUpdates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 1, j: int = 0; i < 10; i++, j = i * 2) {
                        sum += j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(88);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInitWithNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = -5, j: int = 5; i < 0; i++, j--) {
                        sum += i + j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      for (let i: int = 0, d: double = 0.0; i < 5; i++, d += 0.5) {
                        result += i + (d as int)
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(14);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleUpdates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0, j: int = 20; i < 5; i++, j -= 2) {
                        sum += j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(80);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleVariablesInit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0, j: int = 10; i < j; i++, j--) {
                        sum += i + j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleVariablesWithDifferentUpdates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0, j: int = 100; i < 10; i += 1, j -= 10) {
                        sum += i + j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(595);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeVariablesInit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      for (let i: int = 0, j: int = 10, k: int = 100; i < 5; i++, j++, k--) {
                        result += i + j + k
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(560);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUpdateWithMultipleOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0, j: int = 0; i < 5; i += 2, j += 3) {
                        sum += i + j
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);
    }
}
