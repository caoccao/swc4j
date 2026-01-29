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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for while loops with break and continue (Phase 3)
 * Tests break and continue statements within while loops
 */
public class TestCompileAstWhileStmtBreakContinue extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakAndContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 20) {
                        i++
                        if (i % 2 == 0) {
                          continue
                        }
                        if (i > 10) {
                          break
                        }
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(25, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakFirstIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        break
                      }
                      return i
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInNestedIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i > 5) {
                          if (i > 7) {
                            break
                          }
                        }
                      }
                      return i
                    }
                  }
                }""");
        assertEquals(8, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueAllIterations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      let i: int = 0
                      while (i < 5) {
                        i++
                        count++
                        continue
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testContinueInNestedIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 10) {
                        i++
                        if (i > 3) {
                          if (i < 8) {
                            continue
                          }
                        }
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(33, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleBreaks(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i == 3) {
                          break
                        }
                        if (i == 5) {
                          break
                        }
                      }
                      return i
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleContinues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 10) {
                        i++
                        if (i % 2 == 0) {
                          continue
                        }
                        if (i % 3 == 0) {
                          continue
                        }
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(13, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileWithBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 100) {
                        sum += i
                        if (i == 5) {
                          break
                        }
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileWithContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (i < 10) {
                        i++
                        if (i % 2 == 0) {
                          continue
                        }
                        sum += i
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(25, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
