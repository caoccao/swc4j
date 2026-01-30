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

package com.caoccao.javet.swc4j.compiler.ast.stmt.ifstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests covering if statement limitations.
 */
public class TestCompileAstIfStmtLimitations extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfBlockScopeShadowing(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      let value: int = 1
                      let result: int = 0
                      if (flag) {
                        let value: int = 2
                        result = value
                      } else {
                        let value: int = 3
                        result = value
                      }
                      return [[result, value]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", true))
                .isEqualTo(List.of(List.of(2, 1)));
        assertThat(instanceRunner.<Object>invoke("test", false))
                .isEqualTo(List.of(List.of(3, 1)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfMixedTypeAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Object } from 'java.lang'
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      let value: Object = null
                      if (flag) {
                        value = 1
                      } else {
                        value = 2.5
                      }
                      return [[value, value.getClass().getSimpleName()]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", true))
                .isEqualTo(List.of(List.of(1, "Integer")));
        assertThat(instanceRunner.<Object>invoke("test", false))
                .isEqualTo(List.of(List.of(2.5, "Double")));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfSingleStatementBodies(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      let result: int = 0
                      if (flag) result = 1
                      else result = 2
                      if (!flag) result = result + 10
                      return [[result]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", true))
                .isEqualTo(List.of(List.of(1)));
        assertThat(instanceRunner.<Object>invoke("test", false))
                .isEqualTo(List.of(List.of(12)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfStringEquality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      const a: String = "hello"
                      const b: String = flag ? "hello" : "world"
                      let result: int = 0
                      if (a == b) {
                        result = 1
                      } else {
                        result = 2
                      }
                      if (a != b) {
                        result = result + 10
                      }
                      return [[result, a, b]]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test", true))
                .isEqualTo(List.of(List.of(1, "hello", "hello")));
        assertThat(instanceRunner.<Object>invoke("test", false))
                .isEqualTo(List.of(List.of(12, "hello", "world")));
    }
}
