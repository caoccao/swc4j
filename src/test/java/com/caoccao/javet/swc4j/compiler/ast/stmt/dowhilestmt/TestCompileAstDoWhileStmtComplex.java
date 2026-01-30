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

package com.caoccao.javet.swc4j.compiler.ast.stmt.dowhilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for do-while loops with complex conditions (Phase 4)
 * Tests logical operators (&&, ||, !) and complex expressions
 */
public class TestCompileAstDoWhileStmtComplex extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileAndCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let j: int = 0
                      do {
                        i++
                        j++
                      } while (i < 5 && j < 3)
                      return i * 10 + j
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(33);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileComplexExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let j: int = 10
                      do {
                        i++
                        j--
                      } while (i < 5 && j > 5 || i < 3)
                      return i * 10 + j
                    }
                  }
                }""");
        // (i < 5 && j > 5) || i < 3
        // Stops when i=5, j=5
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(55);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileEqualityCheck(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let target: int = 7
                      do {
                        i++
                      } while (i != target)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(7);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileGreaterThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 10
                      do {
                        i--
                      } while (i >= 5)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileMultipleComparisons(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 0
                      let b: int = 10
                      let c: int = 5
                      do {
                        a++
                        b--
                      } while (a < c && b > c)
                      return a * 100 + b
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(505);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileNotCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 10
                      do {
                        i--
                      } while (!(i <= 5))
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileOrCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let j: int = 0
                      do {
                        i++
                        j = j + 2
                      } while (i < 3 || j < 10)
                      return i * 10 + j
                    }
                  }
                }""");
        // Continues until both conditions false: i>=3 AND j>=10
        // Stops when i=5, j=10
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(60);
    }
}
