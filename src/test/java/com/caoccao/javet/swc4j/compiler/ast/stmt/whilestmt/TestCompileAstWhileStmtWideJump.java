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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for while-loop wide jump support.
 */
public class TestCompileAstWhileStmtWideJump extends BaseTestCompileSuite {
    private static String repeatStatement(String statement, int repeats) {
        StringBuilder builder = new StringBuilder(repeats * (statement.length() + 1));
        for (int i = 0; i < repeats; i++) {
            builder.append(statement).append('\n');
        }
        return builder.toString();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeBodyBackEdge(JdkVersion jdkVersion) throws Exception {
        int repeats = 9000;
        String filler = repeatStatement("        sum = sum + 1", repeats);
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let i: int = 0
                      let sum: int = 0
                      while (i < 1) {
                %s        i++
                      }
                      return [[i, sum]]
                    }
                  }
                }""".formatted(filler));
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(List.of(1, repeats)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeBodyWithBreakContinue(JdkVersion jdkVersion) throws Exception {
        int repeats = 9000;
        String filler = repeatStatement("        sum = sum + 0", repeats);
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let i: int = 0
                      let sum: int = 0
                      while (i < 3) {
                        sum += i
                %s        if (i == 0) {
                          i++
                          continue
                        }
                        if (i == 1) {
                          break
                        }
                        i++
                      }
                      return [[i, sum]]
                    }
                  }
                }""".formatted(filler));
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(List.of(1, 1)));
    }
}
