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
 * Test suite for nested for loops (Phase 5)
 * Tests for loops nested within other for loops
 */
public class TestCompileAstForStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInInnerLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 5; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (j === 3) {
                            break
                          }
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(45);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInOuterLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 10; i++) {
                        if (i === 3) {
                          break
                        }
                        for (let j: int = 0; j < 3; j++) {
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(18);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInInnerLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 3; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (j % 2 === 0) {
                            continue
                          }
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(18);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInOuterLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 5; i++) {
                        if (i % 2 === 0) {
                          continue
                        }
                        for (let j: int = 0; j < 2; j++) {
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyInnerLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      for (let i: int = 0; i < 5; i++) {
                        count++
                        for (let j: int = 0; j < 3; j++) {
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFiveLevelDeepNesting(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      for (let a: int = 0; a < 2; a++) {
                        for (let b: int = 0; b < 2; b++) {
                          for (let c: int = 0; c < 2; c++) {
                            for (let d: int = 0; d < 2; d++) {
                              for (let e: int = 0; e < 2; e++) {
                                count++
                              }
                            }
                          }
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(32);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFourLevelNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      for (let i: int = 0; i < 2; i++) {
                        for (let j: int = 0; j < 2; j++) {
                          for (let k: int = 0; k < 2; k++) {
                            for (let l: int = 0; l < 2; l++) {
                              count++
                            }
                          }
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(16);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInnerModifiesOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      for (let i: int = 0; i < 10; i++) {
                        for (let j: int = 0; j < 3; j++) {
                          i++
                          count++
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(9);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedBreakAndContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 5; i++) {
                        if (i === 4) {
                          break
                        }
                        for (let j: int = 0; j < 5; j++) {
                          if (j % 2 === 0) {
                            continue
                          }
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(28);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithComplexConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 10; i++) {
                        for (let j: int = 0; j < 10; j++) {
                          if (i > 5 && j > 5) {
                            break
                          }
                          if (i % 2 === 0 && j % 2 === 0) {
                            continue
                          }
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(63);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithDifferentIterations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 1; i <= 3; i++) {
                        for (let j: int = 1; j <= i; j++) {
                          sum += i * j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(25);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      for (let i: int = 0; i < 5; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (i === 2 && j === 3) {
                            return i * 10 + j
                          }
                        }
                      }
                      return 0
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(23);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSharedVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 4; i++) {
                        for (let j: int = 0; j < i; j++) {
                          sum += j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 2; i++) {
                        for (let j: int = 0; j < 2; j++) {
                          for (let k: int = 0; k < 2; k++) {
                            sum += i + j + k
                          }
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoLevelNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 3; i++) {
                        for (let j: int = 0; j < 3; j++) {
                          sum += i * j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(9);
    }
}
