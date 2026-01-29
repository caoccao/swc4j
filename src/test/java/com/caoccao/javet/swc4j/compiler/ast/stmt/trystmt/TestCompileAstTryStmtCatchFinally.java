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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Tests for try-catch-finally statements (Phase 3).
 * <p>
 * Tests cover:
 * - Basic try-catch-finally
 * - Finally runs after catch
 * - Finally runs on all exit paths
 * - Return value handling
 */
public class TestCompileAstTryStmtCatchFinally extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicTryCatchFinally(JdkVersion jdkVersion) throws Exception {
        // Basic try-catch-finally - no exception
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                      } catch (e) {
                        result = 20
                      } finally {
                        result = result + 5
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);  // 10 + 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryCatchFinally(JdkVersion jdkVersion) throws Exception {
        // Nested try-catch-finally
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        try {
                          result = 10
                          throw new Exception()
                        } catch (e) {
                          result = result + 5
                        } finally {
                          result = result + 3
                        }
                      } catch (e2) {
                        result = 100
                      } finally {
                        result = result + 2
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(20);  // 10 + 5 + 3 + 2 = 20
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyAllEmpty(JdkVersion jdkVersion) throws Exception {
        // All empty blocks (valid but pointless)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      try {
                      } catch (e) {
                      } finally {
                      }
                      return 42
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyExceptionInCatch(JdkVersion jdkVersion) throws Exception {
        // Exception in catch - finally runs before propagation
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    private finallyRan: boolean = false
                    test(): void {
                      try {
                        throw new Exception("try")
                      } catch (e) {
                        throw new Exception("catch")
                      } finally {
                        this.finallyRan = true
                      }
                    }
                    didFinallyRun(): boolean {
                      return this.finallyRan
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThatThrownBy(() -> {
            instanceRunner.invoke("test");
        }).isInstanceOf(Exception.class);
        assertThat((Boolean) instanceRunner.<Boolean>invoke("didFinallyRun")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyExceptionInFinally(JdkVersion jdkVersion) throws Exception {
        // Exception in finally overrides everything
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 1
                      } catch (e) {
                        return 2
                      } finally {
                        throw new Exception("finally")
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Throwable exception = catchThrowable(() -> instanceRunner.invoke("test"));
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).isNotNull();
        assertThat(exception.getCause().getMessage()).contains("finally");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyReturnFromCatch(JdkVersion jdkVersion) throws Exception {
        // Return from catch - finally still runs
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    private finallyRan: boolean = false
                    test(): int {
                      try {
                        throw new Exception()
                      } catch (e) {
                        return 99
                      } finally {
                        this.finallyRan = true
                      }
                    }
                    didFinallyRun(): boolean {
                      return this.finallyRan
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(99);
        assertThat((Boolean) instanceRunner.<Boolean>invoke("didFinallyRun")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyReturnFromFinally(JdkVersion jdkVersion) throws Exception {
        // Return from finally overrides all
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 1
                      } catch (e) {
                        return 2
                      } finally {
                        return 3
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);  // Finally return wins
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyReturnFromTry(JdkVersion jdkVersion) throws Exception {
        // Return from try - finally still runs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private finallyRan: boolean = false
                    test(): int {
                      try {
                        return 42
                      } catch (e) {
                        return -1
                      } finally {
                        this.finallyRan = true
                      }
                    }
                    didFinallyRun(): boolean {
                      return this.finallyRan
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(42);
        assertThat((Boolean) instanceRunner.<Boolean>invoke("didFinallyRun")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyVariableAccess(JdkVersion jdkVersion) throws Exception {
        // Variable access across all three blocks
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                        throw new Exception()
                      } catch (e) {
                        result = result + 20
                      } finally {
                        result = result + 5
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(35);  // 10 + 20 + 5 = 35
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchFinallyWithException(JdkVersion jdkVersion) throws Exception {
        // Exception caught, finally runs
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        throw new Exception()
                      } catch (e) {
                        result = 20
                      } finally {
                        result = result + 5
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(25);  // 20 + 5
    }
}
