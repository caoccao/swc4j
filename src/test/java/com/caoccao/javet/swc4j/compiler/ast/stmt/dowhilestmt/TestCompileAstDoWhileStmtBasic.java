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

package com.caoccao.javet.swc4j.compiler.ast.stmt.dowhilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for basic do-while loops (Phase 1)
 * Tests simple do-while loops with condition and body
 * Key difference from while: body always executes at least once
 */
public class TestCompileAstDoWhileStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicCountingLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i < 10)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileComplexBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let product: int = 1
                      let i: int = 1
                      do {
                        sum += i
                        product = product * i
                        i++
                      } while (i <= 5)
                      return sum * 100 + product
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1620);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileExecutesOnceEvenWhenFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 10
                      do {
                        sum += i
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 10
                      do {
                        sum += i
                        i--
                      } while (i > 0)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(55);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileLessThanOrEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let count: int = 0
                      let i: int = 0
                      do {
                        count++
                        i++
                      } while (i <= 5)
                      return count
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileNotEqual(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i != 10)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileSingleIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        i++
                      } while (i < 1)
                      return i
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      do {
                        if (i == 5) {
                          return i
                        }
                        i++
                      } while (i < 10)
                      return 99
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithSum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        sum += i
                        i++
                      } while (i < 10)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(45);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoWhileWithVarDecl(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      do {
                        const x: int = i * 2
                        sum += x
                        i++
                      } while (i < 5)
                      return sum
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(20);
    }
}
