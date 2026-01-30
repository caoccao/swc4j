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
 * Test suite for basic integer switch statements (Phase 1)
 * Tests simple switch with constant integer cases and break statements
 */
public class TestCompileAstSwitchStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBasicThreeCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 10
                          break
                        case 2:
                          result = 20
                          break
                        case 3:
                          result = 30
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("test", 2)).isEqualTo(20);
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(30);
        assertThat((int) instanceRunner.invoke("test", 4)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDenseCases(JdkVersion jdkVersion) throws Exception {
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
                        case 3:
                          result = 3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 0)).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", 2)).isEqualTo(2);
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", 4)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFirstCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (1) {
                        case 1:
                          result = 100
                          break
                        case 2:
                          result = 200
                          break
                        case 3:
                          result = 300
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLastCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      switch (3) {
                        case 1:
                          result = 100
                          break
                        case 2:
                          result = 200
                          break
                        case 3:
                          result = 300
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test")).isEqualTo(300);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNegativeCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case -5:
                          result = 5
                          break
                        case -3:
                          result = 3
                          break
                        case 0:
                          result = 0
                          break
                        case 3:
                          result = -3
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", -5)).isEqualTo(5);
        assertThat((int) instanceRunner.invoke("test", -3)).isEqualTo(3);
        assertThat((int) instanceRunner.invoke("test", 0)).isEqualTo(0);
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(-3);
        assertThat((int) instanceRunner.invoke("test", 99)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchNoMatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = -1
                      switch (99) {
                        case 1:
                          result = 1
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

        assertThat((int) instanceRunner.invoke("test")).isEqualTo(-1); // No match, result unchanged
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchSingleCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = -1
                      switch (x) {
                        case 5:
                          result = 50
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 5)).isEqualTo(50);
        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(-1); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchSparseCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          result = 1
                          break
                        case 10:
                          result = 10
                          break
                        case 100:
                          result = 100
                          break
                        case 1000:
                          result = 1000
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("test", 10)).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("test", 100)).isEqualTo(100);
        assertThat((int) instanceRunner.invoke("test", 1000)).isEqualTo(1000);
        assertThat((int) instanceRunner.invoke("test", 50)).isEqualTo(0); // No match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchWithExpressionDiscriminant(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x * 2) {
                        case 5:
                          result = 5
                          break
                        case 10:
                          result = 10
                          break
                        case 15:
                          result = 15
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 5)).isEqualTo(10); // 5 * 2 = 10
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(0); // 3 * 2 = 6, no match
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchWithVariableInCase(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      let result: int = 0
                      switch (x) {
                        case 1:
                          const y: int = 10
                          result = y
                          break
                        case 2:
                          const z: int = 20
                          result = z
                          break
                      }
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("test", 2)).isEqualTo(20);
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(0);
    }
}
