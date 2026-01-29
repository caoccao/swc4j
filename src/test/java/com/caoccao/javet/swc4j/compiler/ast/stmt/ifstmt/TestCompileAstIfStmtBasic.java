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
 * Test suite for basic if statements (Phase 1)
 * Tests simple if statements without else clause
 */
public class TestCompileAstIfStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        x = 10
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithBooleanVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const flag: boolean = true
                      let result: int = 0
                      if (flag) {
                        result = 42
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      if (x > 5) {
                        result = 1
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithLogicalAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      const y: int = 5
                      if (x > 5 && y < 10) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithLogicalOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 3
                      const y: int = 15
                      if (x > 5 || y > 10) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      let y: int = 0
                      if (true) {
                        x = 10
                        y = 20
                      }
                      return x + y
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithNegation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const flag: boolean = false
                      if (!flag) {
                        result = 1
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      if (x > 5) {
                        return 100
                      }
                      return 0
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSequentialIfs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      }
                      if (true) {
                        x = x + 5
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
