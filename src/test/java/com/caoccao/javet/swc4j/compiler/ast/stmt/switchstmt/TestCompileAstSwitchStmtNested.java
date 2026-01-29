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
 * Test suite for nested switch statements (Phase 5)
 * Tests switches inside switches and other control structures
 */
public class TestCompileAstSwitchStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLoopInSwitch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          for (let i: int = 0; i < 3; i++) {
                            result += i
                          }
                          break
                        case 2:
                          result = 100
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(3); // 0+1+2 = 3
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBreakAmbiguity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          for (let i: int = 0; i < 10; i++) {
                            if (i == 5) {
                              break
                            }
                            result += i
                          }
                          result += 100
                          break
                        case 2:
                          result = 200
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // i=0,1,2,3,4 sum=10, then +100 = 110
        assertThat((int) instanceRunner.<Object>invoke("test", 1)).isEqualTo(110);
        assertThat((int) instanceRunner.<Object>invoke("test", 2)).isEqualTo(200);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      for (let i: int = 0; i < 5; i++) {
                        switch (i) {
                          case 0:
                          case 1:
                            sum += 1
                            break
                          case 2:
                          case 3:
                            sum += 2
                            break
                          default:
                            sum += 5
                            break
                        }
                      }
                      return sum
                    }
                  }
                }""");
        // i=0: +1, i=1: +1, i=2: +2, i=3: +2, i=4: +5 = 11
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(11);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchInWhile(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      let i: int = 0
                      while (i < 3) {
                        switch (i) {
                          case 0:
                            result += 1
                            break
                          case 1:
                            result += 10
                            break
                          case 2:
                            result += 100
                            break
                        }
                        i++
                      }
                      return result
                    }
                  }
                }""");
        // i=0: +1, i=1: +10, i=2: +100 = 111
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(111);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLabeledBreak(JdkVersion jdkVersion) throws Exception {
        // Test labeled break using a flag variable instead (labeled break not yet fully supported)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int): int {
                      let result: int = 0
                      let skipOuter: boolean = false
                      switch (x) {
                        case 1:
                          switch (y) {
                            case 10:
                              result = 10
                              skipOuter = true
                              break
                            case 20:
                              result = 20
                              break
                          }
                          if (!skipOuter) {
                            result += 100
                          }
                          break
                        case 2:
                          result = 200
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10)).isEqualTo(10);   // Simulates breaking outer
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 20)).isEqualTo(120);  // Breaks inner, continues: 20+100
        assertThat((int) instanceRunner.<Object>invoke("test", 2, 10)).isEqualTo(200);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNestedBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          switch (y) {
                            case 10:
                              result = 110
                              break
                            case 20:
                              result = 120
                              break
                          }
                          break
                        case 2:
                          result = 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10)).isEqualTo(110);
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 20)).isEqualTo(120);
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 30)).isEqualTo(0); // Inner switch no match
        assertThat((int) instanceRunner.<Object>invoke("test", 2, 10)).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNestedDeep(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int): int {
                      let result: int = 0
                      switch (a) {
                        case 1:
                          switch (b) {
                            case 10:
                              switch (c) {
                                case 100:
                                  result = 111
                                  break
                                case 200:
                                  result = 112
                                  break
                              }
                              break
                          }
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10, 100)).isEqualTo(111);
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10, 200)).isEqualTo(112);
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10, 300)).isEqualTo(0);
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 20, 100)).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNestedFallThrough(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          switch (y) {
                            case 10:
                              result += 10
                            case 20:
                              result += 20
                              break
                          }
                        case 2:
                          result += 2
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.<Object>invoke("test", 1, 10)).isEqualTo(32); // Inner: 10+20, outer falls: +2 = 32
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 20)).isEqualTo(22); // Inner: 20, outer falls: +2 = 22
        assertThat((int) instanceRunner.<Object>invoke("test", 1, 30)).isEqualTo(2);  // Inner no match, outer falls: +2 = 2
        assertThat((int) instanceRunner.<Object>invoke("test", 2, 10)).isEqualTo(2);  // Outer case 2: 2
    }
}
