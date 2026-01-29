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
 * Test suite for while loop edge cases (Phase 7)
 * Tests advanced scenarios and edge cases
 */
public class TestCompileAstWhileStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 0) {
                      }
                      return 42
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatingPointCondition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      let d: double = 0.0
                      while (d < 1.0) {
                        count++
                        d = d + 0.1
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(11, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleReturnsInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i % 3 == 0) {
                          return 30
                        }
                        if (i % 5 == 0) {
                          return 50
                        }
                      }
                      return 999
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSequentialLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sumA: int = 0
                      let sumB: int = 0
                      let i: int = 0
                      while (i < 5) {
                        sumA += i
                        i++
                      }
                      let j: int = 0
                      while (j < 5) {
                        sumB += j
                        j++
                      }
                      return sumA * 10 + sumB
                    }
                  }
                }""");
        assertEquals(110, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let even: int = 0
                      let odd: int = 0
                      let i: int = 0
                      while (i < 10) {
                        if (i % 2 == 0) {
                          even++
                        } else {
                          odd++
                        }
                        i++
                      }
                      return even * 10 + odd
                    }
                  }
                }""");
        assertEquals(55, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100) {
                        i++
                        if (i == 7) {
                          return i * 10
                        }
                      }
                      return 999
                    }
                  }
                }""");
        assertEquals(70, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVeryLargeCount(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (i < 100000) {
                        i++
                      }
                      return i
                    }
                  }
                }""");
        assertEquals(100000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileInIfStatement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): int {
                      let sum: int = 0
                      if (flag) {
                        let i: int = 0
                        while (i < 10) {
                          sum += i
                          i++
                        }
                      }
                      return sum
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(45, (int) instanceRunner.invoke("test", true));
        assertEquals(0, (int) instanceRunner.invoke("test", false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileTruePattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      while (true) {
                        count++
                        if (count >= 15) {
                          break
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
