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

package com.caoccao.javet.swc4j.compiler.ast.stmt.forinstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for for-in loops with existing variables (Phase 4)
 */
public class TestCompileAstForInStmtExistingVar extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExistingVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3 }
                      let key: string
                      for (key in obj) {
                        // key is accessible here
                      }
                      return key
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("c");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExistingVariableEmptyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = {}
                      let key: string = "initial"
                      for (key in obj) {
                        // Should not iterate
                      }
                      return key
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("initial");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExistingVariableMultipleLoops(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj1 = { a: 1, b: 2 }
                      const obj2 = { x: 10, y: 20 }
                      let key: string
                      for (key in obj1) {
                        // First loop
                      }
                      let result1: string = key
                      for (key in obj2) {
                        // Second loop reuses same variable
                      }
                      return result1 + "," + key
                    }
                  }
                }""");
        // First loop last key: "b", second loop last key: "y"
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("b,y");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExistingVariableWithArray(JdkVersion jdkVersion) throws Exception {
        // For-in returns string indices in JavaScript semantics
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const arr = [10, 20, 30]
                      let index: string
                      for (index in arr) {
                        // index is accessible here
                      }
                      return index
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExistingVariableWithBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): string {
                      const obj = { a: 1, b: 2, c: 3, d: 4 }
                      let key: string
                      for (key in obj) {
                        if (key == "c") {
                          break
                        }
                      }
                      return key
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("c");
    }
}
