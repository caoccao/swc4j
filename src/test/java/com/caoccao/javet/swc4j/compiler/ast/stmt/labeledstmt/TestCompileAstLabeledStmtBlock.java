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

package com.caoccao.javet.swc4j.compiler.ast.stmt.labeledstmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for labeled statements whose body is not a loop or a switch.
 * Such a label is a break target of its own, and it is never handed to the next loop.
 */
public class TestCompileAstLabeledStmtBlock extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabelOnExpressionStatementIsNotReused(JdkVersion jdkVersion) throws Exception {
        // The label of the expression statement must not attach itself to the loop that follows
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let n: int = 0
                      foo: n = 1
                      for (let i: int = 0; i < 3; i++) {
                        n = n + 1
                      }
                      return n
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLabeledBlockWithBreak(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let n: int = 0
                      block: {
                        n = 1
                        if (n == 1) {
                          break block
                        }
                        n = 99
                      }
                      return n
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }
}
