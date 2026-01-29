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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for if-else statements (Phase 2).
 * Tests basic if-else branches, ensuring both consequent and alternate paths execute correctly.
 */
public class TestCompileAstIfStmtElse extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfElseFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        x = 10
                      } else {
                        x = 20
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfElseTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      } else {
                        x = 20
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseBothBranchesModifyVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      if (x > 10) {
                        x = x * 2
                      } else {
                        x = x + 10
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseBothEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 42
                      if (true) {
                      } else {
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseEmptyConsequent(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      if (false) {
                      } else {
                        x = 20
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseReturnInBothBranches(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 8
                      if (x > 5) {
                        return 100
                      } else {
                        return 200
                      }
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseReturnInOneBranch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        return 100
                      } else {
                        x = 50
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(50, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseWithComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 3
                      let result: int = 0
                      if (x > 5) {
                        result = 100
                      } else {
                        result = 200
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(200, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      let y: int = 0
                      if (false) {
                        x = 10
                        y = 20
                      } else {
                        x = 30
                        y = 40
                      }
                      return x + y
                    }
                  }
                }""");
        assertEquals(70, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseWithVarDecls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 10
                      const b: int = 20
                      let result: int = 0
                      if (true) {
                        result = a
                      } else {
                        result = b
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
