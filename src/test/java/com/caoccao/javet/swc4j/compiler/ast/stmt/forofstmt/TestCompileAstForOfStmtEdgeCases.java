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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forofstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for for-of loop edge cases (Phase 13)
 * Tests complex scenarios and special cases
 */
public class TestCompileAstForOfStmtEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5]
                      let result: int = 0
                      for (let value of arr) {
                        result = result * 10 + (value as int)
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(12345);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3]
                      for (let value of arr) {
                      }
                      return "done"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("done");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                      let sum: int = 0
                      for (let value of arr) {
                        sum += (value as int)
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(55);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let str: string = ""
                      for (let i: int = 0; i < 100; i++) {
                        str += "a"
                      }
                      let count: int = 0
                      for (let char of str) {
                        count++
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLoopInIfStatement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): int {
                      const arr = [1, 2, 3]
                      let sum: int = 0
                      if (flag) {
                        for (let value of arr) {
                          sum += (value as int)
                        }
                      }
                      return sum
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test", true)).isEqualTo(6);
        assertThat((int) instanceRunner.<Object>invoke("test", false)).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleContinueTargets(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [1, 2, 3, 4, 5]
                      let result: string = ""
                      for (let value of arr) {
                        if ((value as int) == 1) {
                          continue
                        }
                        if ((value as int) == 3) {
                          continue
                        }
                        if ((value as int) == 5) {
                          continue
                        }
                        result += value
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("24");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleMergePaths(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5]
                      let x: int = 0
                      for (let value of arr) {
                        if ((value as int) > 3) {
                          x = 10
                        } else {
                          x = 5
                        }
                      }
                      return x
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5]
                      let count: int = 0
                      for (let value of arr) {
                        if ((value as int) > 2) {
                          if ((value as int) < 5) {
                            count++
                          }
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullValuesInArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3]
                      let count: int = 0
                      for (let value of arr) {
                        if (value != null) {
                          count++
                        }
                      }
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInMiddle(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3, 4, 5]
                      for (let value of arr) {
                        if ((value as int) == 3) {
                          return (value as int)
                        }
                      }
                      return -1
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleStatementBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3]
                      let sum: int = 0
                      for (let value of arr) sum += (value as int)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUnreachableCodeAfterBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c"]
                      let result: string = ""
                      for (let value of arr) {
                        result += value
                        break
                        result += "!"
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUnreachableCodeAfterContinue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = ["a", "b", "c"]
                      let result: string = ""
                      for (let value of arr) {
                        result += value
                        continue
                        result += "!"
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("abc");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUnreachableCodeAfterReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const arr = ["a", "b", "c"]
                      for (let value of arr) {
                        return value
                        return "unreachable"
                      }
                      return "empty"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("a");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testValueTypePreservation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [42, "hello", true]
                      let result: string = ""
                      for (let value of arr) {
                        result += value
                        result += ","
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("42,hello,true,");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVariableShadowing(JdkVersion jdkVersion) throws Exception {
        // Note: True variable shadowing isn't fully supported yet (inferredTypes isn't scope-aware)
        // This test verifies that loop variables don't affect outer variables with different names
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const outer: string = "outer"
                      const arr = ["a", "b"]
                      for (let inner of arr) {
                      }
                      return outer
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("outer");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVariablesInBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr = [1, 2, 3]
                      let sum: int = 0
                      for (let value of arr) {
                        const doubled: int = (value as int) * 2
                        sum += doubled
                      }
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(12);
    }
}
