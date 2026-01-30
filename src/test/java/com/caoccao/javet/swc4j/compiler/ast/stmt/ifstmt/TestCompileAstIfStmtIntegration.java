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

package com.caoccao.javet.swc4j.compiler.ast.stmt.ifstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for if statement integration tests.
 * Tests integration of if statements with other language constructs,
 * including variable declarations, complex expressions, and multiple variables.
 */
public class TestCompileAstIfStmtIntegration extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfAfterVarDecl(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      const y: int = 20
                      let result: int = 0
                      if (x < y) {
                        result = x + y
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfBeforeReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 100
                      const flag: boolean = true
                      if (flag) {
                        result = 200
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(200);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithComparisonChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 5
                      const b: int = 10
                      const c: int = 15
                      let result: int = 0
                      if (a < b && b < c) {
                        result = 1
                      } else {
                        result = 2
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithComplexArithmetic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const base: int = 10
                      let result: int = 0
                      if (base * 2 > 15) {
                        result = (base + 5) * (base - 5)
                      } else {
                        result = base * base
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(75);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithMixedLogicalOperators(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      let result: int = 0
                      if ((a && b) || c) {
                        result = 1
                      } else {
                        result = 2
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithMultipleVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 1
                      let b: int = 2
                      let c: int = 3
                      let d: int = 4
                      if (a < b) {
                        a = a + c
                        b = b + d
                      } else {
                        c = c + a
                        d = d + b
                      }
                      return a + b + c + d
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(17);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithNegatedComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      let result: int = 0
                      if (!(x < 5)) {
                        result = 100
                      } else {
                        result = 200
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSequentialIfsWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let intResult: int = 0
                      const flag1: boolean = true
                      const flag2: boolean = false
                      if (flag1) {
                        intResult = 10
                      }
                      if (flag2) {
                        intResult = 20
                      }
                      if (!flag2) {
                        intResult = intResult + 5
                      }
                      return intResult
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(15);
    }
}
