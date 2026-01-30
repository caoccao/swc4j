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
 * Tests for labeled for loops with labeled break and continue.
 * Phase 6: Labeled Break/Continue support.
 */
public class TestCompileAstForStmtLabeled extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabelOnSingleLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      loop: for (let i: int = 0; i < 10; i++) {
                        if (i === 5) break loop
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1 + 2 + 3 + 4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakInConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 10; i++) {
                        for (let j: int = 0; j < 10; j++) {
                          if (i > 3) {
                            if (j > 5) {
                              break outer
                            }
                          }
                          sum += 1
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // i=0,1,2,3: each adds 10 = 40
        // i=4: adds 6 (j=0 through j=5), then breaks
        // Total = 40 + 6 = 46
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(46);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakToMiddleLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 3; i++) {
                        middle: for (let j: int = 0; j < 3; j++) {
                          for (let k: int = 0; k < 3; k++) {
                            if (k === 2) break middle
                            sum += 1
                          }
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // Each i: j goes 0, k adds 2 (0,1 then breaks middle), then j loop ends
        // i=0,j=0: k=0,1 (2), break middle ends j loop -> next i
        // i=1,j=0: k=0,1 (2), break middle ends j loop -> next i
        // i=2,j=0: k=0,1 (2), break middle ends j loop -> done
        // Total = 6
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 10; i++) {
                        for (let j: int = 0; j < 10; j++) {
                          if (i * j > 20) {
                            break outer
                          }
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // Should break outer loop when i * j > 20
        // i=0: sum = 0+0 through 0+9 = 45
        // i=1: sum += 1+0 through 1+9 = 55
        // i=2: sum += 2+0 through 2+9 = 65
        // i=3: sum += 3+0 through 3+6 = 42 (breaks when 3*7=21>20)
        // Total = 45 + 55 + 65 + 42 = 207
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(207);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueInConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 5; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (i > 2) {
                            if (j > 2) {
                              continue outer
                            }
                          }
                          sum += 1
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // i=0,1,2: each adds 5 = 15
        // i=3,4: each adds 3 (j=0,1,2, then continues outer) = 6
        // Total = 15 + 6 = 21
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(21);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 5; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (j === 2) {
                            continue outer
                          }
                          sum += i + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // Each iteration of outer: j goes 0, 1, then continues outer
        // i=0: sum = 0+0, 0+1 = 1
        // i=1: sum += 1+0, 1+1 = 3
        // i=2: sum += 2+0, 2+1 = 5
        // i=3: sum += 3+0, 3+1 = 7
        // i=4: sum += 4+0, 4+1 = 9
        // Total = 1 + 3 + 5 + 7 + 9 = 25
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(25);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedLabeledAndUnlabeled(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 5; i++) {
                        for (let j: int = 0; j < 5; j++) {
                          if (j === 3) break
                          if (i + j > 6) break outer
                          sum += 1
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleLabeledLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 3; i++) {
                        inner: for (let j: int = 0; j < 3; j++) {
                          if (j === 2) break inner
                          sum += i * 10 + j
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: sum = 0, 1 = 1
        // i=1: sum += 10, 11 = 22
        // i=2: sum += 20, 21 = 63
        // Total = 63
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(63);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedWithLabels(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      outer: for (let i: int = 0; i < 3; i++) {
                        middle: for (let j: int = 0; j < 3; j++) {
                          for (let k: int = 0; k < 3; k++) {
                            if (k === 1) continue middle
                            if (i + j + k > 4) break outer
                            sum += 1
                          }
                        }
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(9);
    }
}
