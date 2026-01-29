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

package com.caoccao.javet.swc4j.compiler.ast.stmt.switchstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for fall-through behavior in switch statements (Phase 3)
 * Tests cases without break that fall through to subsequent cases
 */
public class TestCompileAstSwitchStmtFallThrough extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchCaseGrouping(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                        case 2:
                        case 3:
                          result = 123
                          break
                        case 4:
                        case 5:
                          result = 45
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(123);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(123);
        assertThat((int) instanceRunner.<Object>invoke("test", 3)).isEqualTo(123);
        assertThat((int) instanceRunner.<Object>invoke("test", 4)).isEqualTo(45);
        assertThat((int) instanceRunner.<Object>invoke("test", 5)).isEqualTo(45);
        assertThat((int) instanceRunner.<Object>invoke("test", 6)).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDefaultFallsThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        default:
                          result += 10
                        case 1:
                          result += 1
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1); // Case 1 only
        assertThat((int) instanceRunner.<Object>invoke("test", 99)).isEqualTo(11); //Default (10) + Case 1 (1) = 11
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughAll(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (1) {
                        case 1:
                          result += 1
                        case 2:
                          result += 2
                        case 3:
                          result += 3
                        default:
                          result += 10
                      }
                      return result
                    }
                  }
                }""");
        // All cases execute: 1 + 2 + 3 + 10 = 16
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(16);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughComplexPattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 0:
                          result = 0
                          break
                        case 1:
                        case 2:
                          result = 12
                          break
                        case 3:
                          result = 3
                        case 4:
                          result += 4
                        default:
                          result += 100
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 0)).isEqualTo(0);     // Case 0, break
        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(12);    // Cases 1-2, break
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(12);    // Cases 1-2, break
        assertThat((int) instanceRunner.<Object>invoke("test", 3)).isEqualTo(107); //Case 3 (3) + Case 4 (4) + default (100) = 107
        assertThat((int) instanceRunner.<Object>invoke("test", 4)).isEqualTo(104); //Case 4 (4) + default (100) = 104
        assertThat((int) instanceRunner.<Object>invoke("test", 99)).isEqualTo(100);  // Default only
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughLastCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (3) {
                        case 1:
                          result = 1
                          break
                        case 2:
                          result = 2
                          break
                        case 3:
                          result = 3
                      }
                      return result
                    }
                  }
                }""");
        // Last case without break still works
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughMultipleLevels(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (1) {
                        case 1:
                          result += 1
                        case 2:
                          result += 2
                        case 3:
                          result += 3
                        case 4:
                          result += 4
                        case 5:
                          result += 5
                          break
                      }
                      return result
                    }
                  }
                }""");
        // All 5 cases execute: 1 + 2 + 3 + 4 + 5 = 15
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughSimple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (2) {
                        case 1:
                          result += 1
                        case 2:
                          result += 2
                        case 3:
                          result += 3
                      }
                      return result
                    }
                  }
                }""");
        // Case 2 executes: result += 2, then falls through to case 3: result += 3
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5); // 2 + 3 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughSkipsCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (2) {
                        case 1:
                          result = 1
                        case 2:
                        case 3:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        // Case 2 is empty, falls through to case 3
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughToDefault(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result += 1
                        default:
                          result += 10
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(11); //Case 1 (1) + default (10) = 11
        assertThat((int) instanceRunner.<Object>invoke("test", 99)).isEqualTo(10); // Default only
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      switch (x) {
                        case 1:
                          return 1
                        case 2:
                          return 2
                        default:
                          return -1
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // Return stops execution, no fall-through
        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", 99)).isEqualTo(-1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFallThroughWithStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let count: int = 0
                      switch (2) {
                        case 1:
                          result += 1
                          count += 1
                        case 2:
                          result += 2
                          count += 1
                        case 3:
                          result += 3
                          count += 1
                          break
                      }
                      return result * 10 + count
                    }
                  }
                }""");
        // Case 2 (result=2, count=1) + Case 3 (result=5, count=2) = result=5, count=2 -> 52
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(52);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchMixedFallThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result += 1
                          break
                        case 2:
                          result += 2
                        case 3:
                          result += 3
                          break
                        case 4:
                          result += 4
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1);  // Case 1, break
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(5); //Case 2 (2) + Case 3 (3) = 5
        assertThat((int) instanceRunner.<Object>invoke("test", 3)).isEqualTo(3);  // Case 3, break
        assertThat((int) instanceRunner.<Object>invoke("test", 4)).isEqualTo(4);  // Case 4, break
    }
}
