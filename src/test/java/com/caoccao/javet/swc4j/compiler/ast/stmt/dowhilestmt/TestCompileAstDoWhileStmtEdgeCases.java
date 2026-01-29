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
 * Test suite for do-while loop edge cases (Phase 7)
 * Tests unusual patterns and boundary conditions
 */
public class TestCompileAstDoWhileStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileComplexBreakPattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        i++
                        if (i > 10) {
                          break
                        }
                        if (i % 3 == 0) {
                          continue
                        }
                        sum = sum + i
                      } while (i < 100)
                      return sum
                    }
                  }
                }""");
        // i=1,2,4,5,7,8,10 (skip 3,6,9, break at 11)
        // sum = 1+2+4+5+7+8+10 = 37
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(37);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileConditionSideEffect(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let count: int = 0
                      do {
                        count++
                      } while (i++ < 3)
                      return count * 10 + i
                    }
                  }
                }""");
        // i starts at 0, each iteration: count++, then test i++ < 3
        // Iteration 1: count=1, test: 0<3 (true), i=1
        // Iteration 2: count=2, test: 1<3 (true), i=2
        // Iteration 3: count=3, test: 2<3 (true), i=3
        // Iteration 4: count=4, test: 3<3 (false), i=4
        // count=4, i=4
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(44);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileEmptyBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 5
                      do {
                      } while (false)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileReturnInBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                        if (i == 1) {
                          return i * 10
                        }
                      } while (false)
                      return 99
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileUnconditionalBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i = 42
                        break
                      } while (true)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileVariableScope(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        const x: int = i * 2
                        const y: int = x + 1
                        sum = sum + y
                        i++
                      } while (i < 3)
                      return sum
                    }
                  }
                }""");
        // i=0: y=1, i=1: y=3, i=2: y=5
        // sum = 1 + 3 + 5 = 9
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(9);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithBlockBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        {
                          sum = sum + 1
                        }
                        {
                          sum = sum + 2
                        }
                        i++
                      } while (i < 3)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(9);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithIfElse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        if (i % 2 == 0) {
                          sum = sum + 1
                        } else {
                          sum = sum + 2
                        }
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        // i=0,2,4 add 1 each = 3
        // i=1,3 add 2 each = 4
        // total = 7
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(7);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithMultipleReturns(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                        if (i == 3) {
                          return 100
                        }
                        if (i == 7) {
                          return 200
                        }
                      } while (i < 10)
                      return 300
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileZeroIterationsImpossible(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let executed: int = 0
                      do {
                        executed = 1
                      } while (false)
                      return executed
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }
}
