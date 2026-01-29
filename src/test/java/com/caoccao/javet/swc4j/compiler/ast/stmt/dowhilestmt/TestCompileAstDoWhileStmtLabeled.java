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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for labeled do-while loops (Phase 6)
 * Tests labeled break and continue in do-while loops
 */
public class TestCompileAstDoWhileStmtLabeled extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledDoWhileBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: do {
                        let j: int = 0
                        do {
                          sum++
                          if (sum >= 5) {
                            break outer
                          }
                          j++
                        } while (j < 3)
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledDoWhileContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: do {
                        i++
                        let j: int = 0
                        do {
                          j++
                          if (j == 2) {
                            continue outer
                          }
                          sum++
                        } while (j < 5)
                        sum = sum + 100
                      } while (i < 3)
                      return sum
                    }
                  }
                }""");
        // Each outer iteration: j=1 increments sum, j=2 continues outer (skipping rest)
        // 3 iterations, each adds 1 = 3
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledDoWhileWithContinueOuter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: do {
                        i++
                        if (i == 2) {
                          continue outer
                        }
                        let j: int = 0
                        do {
                          sum = sum + i
                          j++
                        } while (j < 2)
                      } while (i < 4)
                      return sum
                    }
                  }
                }""");
        // i=1: sum = 1+1 = 2
        // i=2: continue (skip inner)
        // i=3: sum = 2+3+3 = 8
        // i=4: sum = 8+4+4 = 16
        assertEquals(16, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledDoWhileWithMultipleBreaks(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let i: int = 0
                      outer: do {
                        let j: int = 0
                        do {
                          result++
                          if (i == 1 && j == 1) {
                            break outer
                          }
                          if (j == 2) {
                            break
                          }
                          j++
                        } while (j < 5)
                        i++
                      } while (i < 3)
                      return result
                    }
                  }
                }""");
        // i=0: j=0,1,2 (break), result=3, i=1
        // i=1: j=0,1 (break outer), result=5
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedLabeledDoWhile(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      outer: do {
                        let j: int = 0
                        inner: do {
                          sum++
                          if (sum == 3) {
                            break inner
                          }
                          if (sum == 7) {
                            break outer
                          }
                          j++
                        } while (j < 3)
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        // First outer: sum=1,2,3 (break inner), i=1
        // Second outer: sum=4,5,6 (j=0,1,2), i=2
        // Third outer: sum=7 (break outer)
        assertEquals(7, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
