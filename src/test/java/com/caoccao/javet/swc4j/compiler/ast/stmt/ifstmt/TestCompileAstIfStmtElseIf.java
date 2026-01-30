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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for else-if chains (Phase 3).
 * Tests multiple conditional branches using else-if constructs.
 */
public class TestCompileAstIfStmtElseIf extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfAllEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 5
                      if (x > 10) {
                      } else if (x > 5) {
                      } else {
                      }
                      return 99
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(99);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainElseTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 70
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainFirstTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 95
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainMiddleTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 85
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfLongChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const num: int = 7
                      if (num == 1) {
                        return 10
                      } else if (num == 2) {
                        return 20
                      } else if (num == 3) {
                        return 30
                      } else if (num == 4) {
                        return 40
                      } else if (num == 5) {
                        return 50
                      } else if (num == 6) {
                        return 60
                      } else if (num == 7) {
                        return 70
                      } else if (num == 8) {
                        return 80
                      } else {
                        return 99
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(70);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfMixedStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const mode: int = 2
                      let a: int = 0
                      let b: int = 0
                      if (mode == 1) {
                        a = 10
                      } else if (mode == 2) {
                        a = 20
                        b = 30
                      } else if (mode == 3) {
                        return 100
                      } else {
                        b = 40
                      }
                      return a + b
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfWithComplexConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 15
                      const y: int = 25
                      let result: int = 0
                      if (x > 20 && y > 20) {
                        result = 1
                      } else if (x < 20 && y > 20) {
                        result = 2
                      } else if (x < 20 || y < 20) {
                        result = 3
                      } else {
                        result = 4
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfWithReturns(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const grade: int = 88
                      if (grade >= 90) {
                        return 4
                      } else if (grade >= 80) {
                        return 3
                      } else if (grade >= 70) {
                        return 2
                      } else if (grade >= 60) {
                        return 1
                      } else {
                        return 0
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfWithoutFinalElse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const num: int = 100
                      let result: int = 0
                      if (num < 50) {
                        result = 1
                      } else if (num < 75) {
                        result = 2
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleElseIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 15
                      let result: int = 0
                      if (x < 10) {
                        result = 1
                      } else if (x < 15) {
                        result = 2
                      } else if (x < 20) {
                        result = 3
                      } else {
                        result = 4
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }
}
