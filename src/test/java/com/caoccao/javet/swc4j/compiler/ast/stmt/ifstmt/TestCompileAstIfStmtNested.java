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
 * Test suite for nested if statements (Phase 4).
 * Tests if statements nested within other if statements at various depths.
 */
public class TestCompileAstIfStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeepNesting(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      if (a) {
                        if (b) {
                          if (c) {
                            result = 1
                          }
                        } else {
                          if (c) {
                            result = 2
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedFiveLevels(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      if (true) {
                        if (true) {
                          if (true) {
                            if (true) {
                              if (true) {
                                result = 42
                              }
                            }
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfElse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      const y: int = 20
                      let result: int = 0
                      if (x > 5) {
                        if (y > 15) {
                          result = 1
                        } else {
                          result = 2
                        }
                      } else {
                        if (y > 15) {
                          result = 3
                        } else {
                          result = 4
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInElse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 3
                      const y: int = 8
                      if (x > 5) {
                        result = 50
                      } else {
                        if (y > 7) {
                          result = 100
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInThen(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      const y: int = 5
                      if (x > 5) {
                        if (y > 3) {
                          result = 100
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedInBoth(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      let result: int = 0
                      if (a) {
                        if (b) {
                          result = 1
                        } else {
                          result = 2
                        }
                      } else {
                        if (c) {
                          result = 3
                        } else {
                          result = 4
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedInElseIf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const mode: int = 2
                      const subMode: int = 1
                      let result: int = 0
                      if (mode == 1) {
                        result = 10
                      } else if (mode == 2) {
                        if (subMode == 1) {
                          result = 21
                        } else {
                          result = 22
                        }
                      } else {
                        result = 30
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(21);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedVarScoping(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const inner1: int = 10
                      const inner2: int = 20
                      let outer: int = 1
                      if (true) {
                        if (true) {
                          outer = inner1 + inner2
                        }
                      }
                      return outer
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithComplexConditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 10
                      const b: int = 20
                      const c: int = 30
                      let result: int = 0
                      if (a > 5 && b > 15) {
                        if (c > 25 || b < 25) {
                          result = 1
                        } else {
                          result = 2
                        }
                      } else {
                        result = 3
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithReturns(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      const y: int = 20
                      if (x > 5) {
                        if (y > 15) {
                          return 100
                        }
                        return 200
                      }
                      return 300
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(100);
    }
}
