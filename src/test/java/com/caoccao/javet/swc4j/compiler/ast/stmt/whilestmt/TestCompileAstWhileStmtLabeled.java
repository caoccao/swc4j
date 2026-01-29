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

package com.caoccao.javet.swc4j.compiler.ast.stmt.whilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for labeled while loops (Phase 6)
 * Tests labeled break and continue statements
 */
public class TestCompileAstWhileStmtLabeled extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabelOnSingleLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      myloop: while (i < 10) {
                        i++
                        if (i == 5) {
                          break myloop
                        }
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        // i=1: sum=1
        // i=2: sum=3
        // i=3: sum=6
        // i=4: sum=10
        // i=5: break
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakInConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 10) {
                        let j: int = 0
                        while (j < 10) {
                          if (i == 2 && j == 3) {
                            break outer
                          }
                          sum++
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: j=0..9 => 10
        // i=1: j=0..9 => 10
        // i=2: j=0,1,2 => 3, then break outer
        // Total: 10 + 10 + 3 = 23
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(23);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 10) {
                        let j: int = 0
                        while (j < 10) {
                          sum++
                          if (i * j > 20) {
                            break outer
                          }
                          j++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: j=0,1,...,9 => sum=10 (0*j never >20)
        // i=1: j=0,1,...,9 => sum=20 (1*j never >20)
        // i=2: j=0,1,...,9 => sum=30 (2*j never >20)
        // i=3: j=0,1,2,3,4,5,6,7 => at j=7: 3*7=21>20, break outer
        // sum = 10 + 10 + 10 + 8 = 38
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(38);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueSkipsRest(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 5) {
                        i++
                        let j: int = 0
                        while (j < 5) {
                          j++
                          if (i % 2 == 0 && j == 2) {
                            continue outer
                          }
                          sum++
                        }
                        sum = sum + 100
                      }
                      return sum
                    }
                  }
                }""");
        // i=1 (odd): j=1,2,3,4,5 => sum=5, then sum+=100 => 105
        // i=2 (even): j=1, then j=2 continue outer (skip +100) => sum=106
        // i=3 (odd): j=1,2,3,4,5 => sum=111, then sum+=100 => 211
        // i=4 (even): j=1, then j=2 continue outer (skip +100) => sum=212
        // i=5 (odd): j=1,2,3,4,5 => sum=217, then sum+=100 => 317
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(317);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueToOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 5) {
                        i++
                        let j: int = 0
                        while (j < 10) {
                          j++
                          if (j == 3) {
                            continue outer
                          }
                          sum++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // Each outer iteration: j=1,2 increment sum, then j=3 continues outer
        // 5 outer iterations * 2 inner increments = 10
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledWhileWithFor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 5) {
                        for (let j: int = 0; j < 5; j++) {
                          if (i * j > 6) {
                            break outer
                          }
                          sum++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: j=0,1,2,3,4 => 5 (0*j never >6)
        // i=1: j=0,1,2,3,4 => 5 (1*j never >6)
        // i=2: j=0,1,2,3 => 4, at j=4: 2*4=8>6, break outer
        // Total: 5 + 5 + 4 = 14
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(14);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedLabeledAndUnlabeled(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: while (i < 3) {
                        let j: int = 0
                        while (j < 5) {
                          j++
                          if (j == 2) {
                            continue
                          }
                          if (j == 4 && i == 2) {
                            break outer
                          }
                          sum++
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: j=1 (sum=1), j=2 continue, j=3 (sum=2), j=4 (sum=3), j=5 (sum=4) => 4
        // i=1: j=1 (sum=5), j=2 continue, j=3 (sum=6), j=4 (sum=7), j=5 (sum=8) => 4
        // i=2: j=1 (sum=9), j=2 continue, j=3 (sum=10), j=4 break outer => 2
        // Total: 10
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleLabeledLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let i: int = 0
                      outer: while (i < 3) {
                        let j: int = 0
                        middle: while (j < 3) {
                          let k: int = 0
                          while (k < 3) {
                            result++
                            if (k == 1 && j == 1) {
                              break middle
                            }
                            if (k == 2 && j == 2 && i == 2) {
                              break outer
                            }
                            k++
                          }
                          j++
                        }
                        i++
                      }
                      return result
                    }
                  }
                }""");
        // i=0: j=0: k=0,1,2 (3) | j=1: k=0,1 break middle (2) => 5
        // i=1: j=0: k=0,1,2 (3) | j=1: k=0,1 break middle (2) => 5
        // i=2: j=0: k=0,1,2 (3) | j=1: k=0,1 break middle (2) => 5
        // Total: 15
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);
    }
}
