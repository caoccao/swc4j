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

package com.caoccao.javet.swc4j.compiler.ast.stmt.trystmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for break/continue with finally blocks (Phase 6).
 * <p>
 * Tests cover:
 * - Break in try within loop (finally runs)
 * - Continue in try within loop (finally runs)
 * - Labeled break with finally
 * - Labeled continue with finally
 * - Multiple finally blocks with break/continue
 * - Finally with return that overrides break/continue
 */
public class TestCompileAstTryStmtControlFlow extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInForLoopWithFinally(JdkVersion jdkVersion) throws Exception {
        // Break in for loop with try-finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      for (let i: int = 0; i < 10; i = i + 1) {
                        try {
                          if (i == 5) break
                          result = result + 1
                        } finally {
                          result = result + 10
                        }
                      }
                      return result
                    }
                  }
                }""");
        // i=0..4: result += (1+10) = 55, i=5: result += 10 = 65 (break + finally)
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(65);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInTryCatchFinally(JdkVersion jdkVersion) throws Exception {
        // Break in try-catch-finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      while (true) {
                        try {
                          result = 10
                          break
                        } catch (e) {
                          result = 20
                        } finally {
                          result = result + 3
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(13);  // 10 + 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInTryWithFinally(JdkVersion jdkVersion) throws Exception {
        // Break in try - finally runs before break
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      while (true) {
                        try {
                          result = 10
                          break
                        } finally {
                          result = result + 5
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);  // 10 + 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInTryWithFinallyAndFlag(JdkVersion jdkVersion) throws Exception {
        // Verify finally runs with a flag
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private finallyRan: boolean = false
                    test(): int {
                      let i: int = 0
                      while (i < 10) {
                        try {
                          if (i == 3) break
                          i = i + 1
                        } finally {
                          this.finallyRan = true
                        }
                      }
                      return i
                    }
                    didFinallyRun(): boolean {
                      return this.finallyRan
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(3);
        assertThat((boolean) instanceRunner.<Boolean>invoke("didFinallyRun")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakWithFinallyModifyingLoopVar(JdkVersion jdkVersion) throws Exception {
        // Finally modifies loop variable but break already decided
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 10) {
                        try {
                          if (i >= 3) break
                        } finally {
                          i = i + 1
                        }
                      }
                      return i
                    }
                  }
                }""");
        // i=0: finally i=1, i=1: finally i=2, i=2: finally i=3, i=3: break, finally i=4
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakWithMultipleFinallyBlocks(JdkVersion jdkVersion) throws Exception {
        // Break with three nested finally blocks
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      while (true) {
                        try {
                          try {
                            try {
                              result = 1
                              break
                            } finally {
                              result = result + 10
                            }
                          } finally {
                            result = result + 100
                          }
                        } finally {
                          result = result + 1000
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1111);  // 1 + 10 + 100 + 1000
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInForLoopWithFinally(JdkVersion jdkVersion) throws Exception {
        // Continue in for loop with try-finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      for (let i: int = 0; i < 5; i = i + 1) {
                        try {
                          if (i == 2) continue
                          result = result + i
                        } finally {
                          result = result + 10
                        }
                      }
                      return result
                    }
                  }
                }""");
        // i=0: 0+10, i=1: 1+10, i=2: 10 (continue), i=3: 3+10, i=4: 4+10 = 10+11+10+13+14 = 58
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(58);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInTryWithFinally(JdkVersion jdkVersion) throws Exception {
        // Continue in try - finally runs before continue
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 5) {
                        try {
                          i = i + 1
                          if (i == 3) continue
                          sum = sum + i
                        } finally {
                          sum = sum + 10
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // i=1: sum=0+1+10=11, i=2: sum=11+2+10=23, i=3: sum=23+10=33 (continue),
        // i=4: sum=33+4+10=47, i=5: sum=47+5+10=62
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(62);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInTryWithFinallyAndFlag(JdkVersion jdkVersion) throws Exception {
        // Verify finally runs on continue
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private finallyCount: int = 0
                    test(): int {
                      let i: int = 0
                      while (i < 5) {
                        try {
                          i = i + 1
                          continue
                        } finally {
                          this.finallyCount = this.finallyCount + 1
                        }
                      }
                      return this.finallyCount
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueWithMultipleFinallyBlocks(JdkVersion jdkVersion) throws Exception {
        // Continue with three nested finally blocks
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let count: int = 0
                      while (count < 2) {
                        try {
                          try {
                            try {
                              count = count + 1
                              result = result + 1
                              continue
                            } finally {
                              result = result + 10
                            }
                          } finally {
                            result = result + 100
                          }
                        } finally {
                          result = result + 1000
                        }
                      }
                      return result
                    }
                  }
                }""");
        // Each iteration: 1 + 10 + 100 + 1000 = 1111, 2 iterations = 2222
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(2222);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFinallyReturnOverridesBreak(JdkVersion jdkVersion) throws Exception {
        // Finally with return overrides break
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      while (true) {
                        try {
                          break
                        } finally {
                          return 99
                        }
                      }
                      return 0
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(99);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFinallyReturnOverridesContinue(JdkVersion jdkVersion) throws Exception {
        // Finally with return overrides continue
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 10) {
                        try {
                          i = i + 1
                          continue
                        } finally {
                          return 88
                        }
                      }
                      return 0
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(88);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBreakWithFinally(JdkVersion jdkVersion) throws Exception {
        // Labeled break with try-finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      outer: while (true) {
                        let i: int = 0
                        while (i < 10) {
                          try {
                            i = i + 1
                            if (i == 3) break outer
                            result = result + 1
                          } finally {
                            result = result + 10
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        // i=1: result=0+1+10=11, i=2: result=11+1+10=22, i=3: result=22+10=32 (break outer + finally)
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(32);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledContinueWithFinally(JdkVersion jdkVersion) throws Exception {
        // Labeled continue with try-finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let outerCount: int = 0
                      outer: while (outerCount < 3) {
                        outerCount = outerCount + 1
                        let i: int = 0
                        while (i < 5) {
                          try {
                            i = i + 1
                            if (i == 2) continue outer
                            result = result + 1
                          } finally {
                            result = result + 10
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        // Outer iter 1: i=1: result=0+1+10=11, i=2: result=11+10=21 (continue outer)
        // Outer iter 2: i=1: result=21+1+10=32, i=2: result=32+10=42 (continue outer)
        // Outer iter 3: i=1: result=42+1+10=53, i=2: result=53+10=63 (continue outer)
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(63);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryFinallyWithBreak(JdkVersion jdkVersion) throws Exception {
        // Nested try-finally with break - both finally blocks run
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      while (true) {
                        try {
                          try {
                            result = 10
                            break
                          } finally {
                            result = result + 5
                          }
                        } finally {
                          result = result + 3
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(18);  // 10 + 5 + 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryFinallyWithContinue(JdkVersion jdkVersion) throws Exception {
        // Nested try-finally with continue - both finally blocks run
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let i: int = 0
                      while (i < 2) {
                        try {
                          try {
                            i = i + 1
                            result = result + 10
                            continue
                          } finally {
                            result = result + 5
                          }
                        } finally {
                          result = result + 3
                        }
                      }
                      return result
                    }
                  }
                }""");
        // Each iteration: 10 + 5 + 3 = 18, 2 iterations = 36
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(36);
    }
}
