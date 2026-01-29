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
 * Test suite for infinite while loops (Phase 2)
 * Tests while(true) patterns with various exit conditions
 */
public class TestCompileAstWhileStmtInfinite extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueBreakInMiddle(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (true) {
                        sum += i
                        i++
                        if (i > 5) {
                          break
                        }
                        sum = sum + 1
                      }
                      return sum
                    }
                  }
                }""");
        // Iteration: i=0 sum=0+0=0, i=1, sum=0+1=1
        // Iteration: i=1 sum=1+1=2, i=2, sum=2+1=3
        // Iteration: i=2 sum=3+2=5, i=3, sum=5+1=6
        // Iteration: i=3 sum=6+3=9, i=4, sum=9+1=10
        // Iteration: i=4 sum=10+4=14, i=5, sum=14+1=15
        // Iteration: i=5 sum=15+5=20, i=6, break (no +1)
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueConditionalBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      while (true) {
                        if (i >= 10) {
                          break
                        }
                        sum += i
                        i++
                      }
                      return sum
                    }
                  }
                }""");
        assertEquals(45, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueEarlyReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (true) {
                        i++
                        if (i == 1) {
                          return 42
                        }
                      }
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueMultipleConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      while (true) {
                        x++
                        if (x == 25) {
                          x = x * 2
                        }
                        if (x > 100) {
                          break
                        }
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(101, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueWithBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      while (true) {
                        x++
                        if (x > 100) {
                          break
                        }
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(101, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueWithCounter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      while (true) {
                        count++
                        if (count == 20) {
                          break
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTrueWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      while (true) {
                        x++
                        if (x >= 50) {
                          return x
                        }
                      }
                    }
                  }
                }""");
        assertEquals(50, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
