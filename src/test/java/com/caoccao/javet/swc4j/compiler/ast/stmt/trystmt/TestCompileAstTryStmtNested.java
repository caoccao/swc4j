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

package com.caoccao.javet.swc4j.compiler.ast.stmt.trystmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for nested try statements (Phase 7).
 * <p>
 * Tests cover:
 * - Try-catch nested in try block
 * - Try-catch nested in catch block
 * - Try-catch nested in finally block
 * - Multiple levels of nesting with catch blocks
 * - Sequential try blocks
 */
public class TestCompileAstTryStmtNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBreakInsideNestedTryInInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test break inside nested try when inline finally is executing
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      while (true) {
                        try {
                          result = 10
                          return result
                        } finally {
                          // This finally has a nested loop with break
                          let i: int = 0
                          while (i < 3) {
                            try {
                              i = i + 1
                              if (i == 2) break
                            } finally {
                              result = result + 1
                            }
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        // Return value (10) was stored before finally runs
        // Finally modifies result but the stored return value (10) is what gets returned
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeeplyNested5Levels(JdkVersion jdkVersion) throws Exception {
        // Five levels of nesting (no exceptions)
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        try {
                          try {
                            try {
                              try {
                                result = 1
                              } catch (e5) {
                                result = result + 10
                              }
                            } catch (e4) {
                              result = result + 100
                            }
                          } catch (e3) {
                            result = result + 1000
                          }
                        } catch (e2) {
                          result = result + 10000
                        }
                      } catch (e1) {
                        result = result + 100000
                      }
                      return result
                    }
                  }
                }""");
        // No exception, just result = 1
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeeplyNestedReturnInInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test deeply nested return inside multiple try-finally in inline finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        return 1
                      } finally {
                        try {
                          try {
                            return 2
                          } finally {
                            result = result + 10
                          }
                        } finally {
                          result = result + 100
                        }
                      }
                    }
                  }
                }""");
        // The inner return 2 executes its pending finally blocks (result += 10, result += 100)
        // Then returns 2, which overrides the outer return 1
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleNestedTryFinallyInInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test multiple nested try-finally blocks inside inline finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 1
                        return result
                      } finally {
                        // Multiple nested try-finally at same level
                        try {
                          result = result + 10
                        } finally {
                          result = result + 100
                        }
                        try {
                          result = result + 1000
                        } finally {
                          result = result + 10000
                        }
                      }
                    }
                  }
                }""");
        // Return value (1) was stored before finally runs
        // Finally modifies result but the stored return value (1) is what gets returned
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedReturnInFinallyChain(JdkVersion jdkVersion) throws Exception {
        // Test nested return inside multiple levels of finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 1
                      } finally {
                        try {
                          return 2
                        } finally {
                          // This is the innermost finally, its return should win
                          return 3
                        }
                      }
                    }
                  }
                }""");
        // The innermost finally's return (3) should win
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchFinallyChain(JdkVersion jdkVersion) throws Exception {
        // Nested try-catch-finally blocks
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        try {
                          try {
                            throw new Exception("deep")
                          } catch (e) {
                            result = result + 1
                          } finally {
                            result = result + 10
                          }
                        } catch (e) {
                          result = result + 100
                        } finally {
                          result = result + 1000
                        }
                      } catch (e) {
                        result = result + 10000
                      } finally {
                        result = result + 100000
                      }
                      return result
                    }
                  }
                }""");
        // catch (1), inner finally (10), middle finally (1000), outer finally (100000) = 101011
        assertEquals(101011, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchFinallyInsideInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test nested try-catch-finally inside inline finally block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                        return result
                      } finally {
                        // Nested try-catch-finally in the outer finally
                        try {
                          result = result + 5
                          throw new Exception("inner")
                        } catch (e) {
                          result = result + 3
                        } finally {
                          result = result + 2
                        }
                      }
                    }
                  }
                }""");
        // result = 10, finally: result = 10+5+3+2 = 20, but we stored 10 before, return 10
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchInCatchBlock(JdkVersion jdkVersion) throws Exception {
        // Try-catch nested in catch block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        throw new Exception("outer")
                      } catch (e) {
                        try {
                          result = 10
                          throw new Exception("inner")
                        } catch (e2) {
                          result = result + 5  // result = 15
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchInFinallyBlock(JdkVersion jdkVersion) throws Exception {
        // Try-catch nested in finally block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                      } finally {
                        try {
                          result = result + 5
                          throw new Exception("finally inner")
                        } catch (e) {
                          result = result + 3  // result = 18
                        }
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(18, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchInTryBlock(JdkVersion jdkVersion) throws Exception {
        // Try-catch nested in try block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        try {
                          result = 10
                          throw new Exception("inner")
                        } catch (e) {
                          result = result + 5  // result = 15
                        }
                        result = result + 3  // result = 18
                      } catch (e2) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(18, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryInCatchWithFinally(JdkVersion jdkVersion) throws Exception {
        // Try-catch-finally with nested try in catch
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        throw new Exception("outer")
                      } catch (e) {
                        try {
                          result = 10
                          throw new Exception("inner")
                        } catch (e2) {
                          result = result + 5
                        } finally {
                          result = result + 2
                        }
                      } finally {
                        result = result + 1
                      }
                      return result
                    }
                  }
                }""");
        // 10 + 5 + 2 + 1 = 18
        assertEquals(18, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryInFinallyWithException(JdkVersion jdkVersion) throws Exception {
        // Try in finally that catches its own exception
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                        throw new Exception("outer")
                      } catch (e) {
                        result = result + 5
                      } finally {
                        try {
                          throw new Exception("finally")
                        } catch (e2) {
                          result = result + 100
                        }
                      }
                      return result
                    }
                  }
                }""");
        // 10 + 5 + 100 = 115
        assertEquals(115, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedWithSequentialTryBlocks(JdkVersion jdkVersion) throws Exception {
        // Sequential try blocks (not nested)
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = result + 10
                        throw new Exception("first")
                      } catch (e1) {
                        result = result + 1
                      }
                      try {
                        result = result + 100
                        throw new Exception("second")
                      } catch (e2) {
                        result = result + 2
                      }
                      return result
                    }
                  }
                }""");
        // 10 + 1 + 100 + 2 = 113
        assertEquals(113, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInsideNestedTryCatchInInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test return inside nested try-catch when inline finally is executing
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 42
                      } finally {
                        // Nested try-catch with return in the outer finally
                        try {
                          return 100
                        } catch (e) {
                          return 200
                        }
                      }
                    }
                  }
                }""");
        // The return in the nested try (return 100) should override the outer return (42)
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSequentialTryFinallyWithReturnInInlineFinally(JdkVersion jdkVersion) throws Exception {
        // Test sequential try-finally blocks where second one has a return
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        return 1
                      } finally {
                        // First try-finally completes normally
                        try {
                          result = 10
                        } finally {
                          result = result + 5
                        }
                        // Second try-finally has a return
                        try {
                          return result
                        } finally {
                          result = result + 100
                        }
                      }
                    }
                  }
                }""");
        // First try-finally: result = 10 + 5 = 15
        // Second try-finally: stores return value 15, then finally adds 100
        // Returns 15 (the stored value before finally)
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
