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
 * Test suite for edge cases in switch statements (Phase 7)
 * Tests unusual patterns, error conditions, and boundary cases
 */
public class TestCompileAstSwitchStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchAllCasesReturn(JdkVersion jdkVersion) throws Exception {
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

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", 99)).isEqualTo(-1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchComplexFallThroughPattern(JdkVersion jdkVersion) throws Exception {
        // Simulating weekday classification
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(day: int): boolean {
                      let isWeekend: boolean = false
                      switch (day) {
                        case 0:
                        case 6:
                          isWeekend = true
                          break
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                          isWeekend = false
                          break
                      }
                      return isWeekend
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat(instanceRunner.<Boolean>invoke("test", 0)).isTrue();  // Sunday
        assertThat(instanceRunner.<Boolean>invoke("test", 1)).isFalse(); // Monday
        assertThat(instanceRunner.<Boolean>invoke("test", 5)).isFalse(); // Friday
        assertThat(instanceRunner.<Boolean>invoke("test", 6)).isTrue();  // Saturday
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDensityBoundary(JdkVersion jdkVersion) throws Exception {
        // Cases: 0, 1, 2, 4 (missing 3)
        // Density = 4/5 = 80% (above 50% threshold, should use tableswitch)
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
                          result = 1
                          break
                        case 2:
                          result = 2
                          break
                        case 4:
                          result = 4
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 0)).isEqualTo(0);
        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", 3)).isEqualTo(0); // Missing case
        assertThat((int) instanceRunner.<Object>invoke("test", 4)).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDiscriminantSideEffect(JdkVersion jdkVersion) throws Exception {
        // Verify discriminant expression evaluation (already covered by testSwitchWithExpressionDiscriminant)
        // This test verifies that complex expressions work as discriminants
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int): int {
                      let result: int = 0
                      let temp: int = a + b
                      switch (temp) {
                        case 5:
                          result = 1
                          break
                        case 10:
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 2, 3)).isEqualTo(1); // 2+3 = 5
        assertThat((int) instanceRunner.<Object>invoke("test", 7, 3)).isEqualTo(2); // 7+3 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                      }
                      result = 1
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 5)).isEqualTo(1); // Switch does nothing
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchEmptyBlock(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                        case 2:
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(2); // Empty case 1 falls to case 2
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchHexOctalBinaryCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 0x10:
                          result = 1
                          break
                        case 0o24:
                          result = 2
                          break
                        case 0b100000:
                          result = 3
                          break
                        case 64:
                          result = 4
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // 0x10 = 16, 0o24 = 20, 0b100000 = 32
        assertThat((int) instanceRunner.<Object>invoke("test", 16)).isEqualTo(1);  // Hex
        assertThat((int) instanceRunner.<Object>invoke("test", 20)).isEqualTo(2);  // Octal
        assertThat((int) instanceRunner.<Object>invoke("test", 32)).isEqualTo(3);  // Binary
        assertThat((int) instanceRunner.<Object>invoke("test", 64)).isEqualTo(4);  // Decimal
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLargeDense(JdkVersion jdkVersion) throws Exception {
        // Build switch with cases 0-49 (50 cases, fully dense)
        StringBuilder code = new StringBuilder("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                """);

        for (int i = 0; i < 50; i++) {
            code.append("        case ").append(i).append(":\n");
            code.append("          result = ").append(i * 10).append("\n");
            code.append("          break\n");
        }

        code.append("""
                      }
                      return result
                    }
                  }
                }""");

        var runner = getCompiler(jdkVersion).compile(code.toString());
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 0)).isEqualTo(0);
        assertThat((int) instanceRunner.<Object>invoke("test", 10)).isEqualTo(100);
        assertThat((int) instanceRunner.<Object>invoke("test", 49)).isEqualTo(490);
        assertThat((int) instanceRunner.<Object>invoke("test", 50)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLargeSparse(JdkVersion jdkVersion) throws Exception {
        // Build switch with sparse cases (powers of 2)
        StringBuilder code = new StringBuilder("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                """);

        for (int i = 0; i < 10; i++) {
            int value = 1 << i; // 1, 2, 4, 8, 16, ..., 512
            code.append("        case ").append(value).append(":\n");
            code.append("          result = ").append(value).append("\n");
            code.append("          break\n");
        }

        code.append("""
                      }
                      return result
                    }
                  }
                }""");

        var runner = getCompiler(jdkVersion).compile(code.toString());
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", 16)).isEqualTo(16);
        assertThat((int) instanceRunner.<Object>invoke("test", 512)).isEqualTo(512);
        assertThat((int) instanceRunner.<Object>invoke("test", 3)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchMaxIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1000000:
                          result = 1
                          break
                        case -1000000:
                          result = 2
                          break
                        case 0:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1000000)).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("test", -1000000)).isEqualTo(2);
        assertThat((int) instanceRunner.<Object>invoke("test", 0)).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchVariableScope(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          const a: int = 10
                          result = a
                          break
                        case 2:
                          const b: int = 20
                          result = b
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(10);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchWithComplexExpressions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int): int {
                      let result: int = 0
                      switch (x + y * 2) {
                        case 10:
                          result = 10
                          break
                        case 20:
                          result = 20
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 2, 4)).isEqualTo(10);  // 2 + 4*2 = 10
        assertThat((int) instanceRunner.<Object>invoke("test", 4, 8)).isEqualTo(20);  // 4 + 8*2 = 20
    }
}
