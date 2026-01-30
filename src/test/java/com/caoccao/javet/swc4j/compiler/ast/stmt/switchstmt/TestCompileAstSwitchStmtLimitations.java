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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for switch statement limitations.
 */
public class TestCompileAstSwitchStmtLimitations extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchBooleanCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      let result: int = 0
                      switch (flag) {
                        case true:
                          result = 1
                          break
                        case false:
                          result = 2
                          break
                      }
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", true)).isEqualTo(List.of(List.of(1)));
        assertThat(instanceRunner.<Object>invoke("test", false)).isEqualTo(List.of(List.of(2)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchDoubleCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: double) {
                      let result: int = 0
                      switch (value) {
                        case 2.25:
                          result = 225
                          break
                        case 3.75:
                          result = 375
                          break
                        default:
                          result = 0
                      }
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", 2.25d)).isEqualTo(List.of(List.of(225)));
        assertThat(instanceRunner.<Object>invoke("test", 3.75d)).isEqualTo(List.of(List.of(375)));
        assertThat(instanceRunner.<Object>invoke("test", 5.0d)).isEqualTo(List.of(List.of(0)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchFloatCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: float) {
                      let result: int = 0
                      switch (value) {
                        case 1.5:
                          result = 15
                          break
                        case 2.5:
                          result = 25
                          break
                        default:
                          result = 99
                      }
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", 1.5f)).isEqualTo(List.of(List.of(15)));
        assertThat(instanceRunner.<Object>invoke("test", 2.5f)).isEqualTo(List.of(List.of(25)));
        assertThat(instanceRunner.<Object>invoke("test", 3.5f)).isEqualTo(List.of(List.of(99)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLabeledBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: int) {
                      let result: int = 0
                      label: switch (value) {
                        case 1:
                          result = 1
                          break label
                        case 2:
                          result = 2
                          break
                        default:
                          result = 3
                      }
                      result = result + 10
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", 1)).isEqualTo(List.of(List.of(11)));
        assertThat(instanceRunner.<Object>invoke("test", 2)).isEqualTo(List.of(List.of(12)));
        assertThat(instanceRunner.<Object>invoke("test", 5)).isEqualTo(List.of(List.of(13)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSwitchLongCases(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: long) {
                      let result: int = 0
                      switch (value) {
                        case -1:
                          result = 1
                          break
                        case 2:
                        case 3:
                          result = 2
                          break
                        default:
                          result = 9
                      }
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", -1L)).isEqualTo(List.of(List.of(1)));
        assertThat(instanceRunner.<Object>invoke("test", 2L)).isEqualTo(List.of(List.of(2)));
        assertThat(instanceRunner.<Object>invoke("test", 5L)).isEqualTo(List.of(List.of(9)));
    }
}
