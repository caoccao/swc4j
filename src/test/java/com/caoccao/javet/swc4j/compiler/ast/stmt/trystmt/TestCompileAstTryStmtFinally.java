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

import static org.assertj.core.api.Assertions.*;


/**
 * Tests for try-finally statements (Phase 2).
 * <p>
 * Tests cover:
 * - Basic try-finally
 * - Try-finally with return in try
 * - Try-finally with exception in try (finally still runs)
 * - Finally block always executes
 */
public class TestCompileAstTryStmtFinally extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicTryFinally(JdkVersion jdkVersion) throws Exception {
        // Basic try-finally - no exception
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 10
                      } finally {
                        result = result + 5
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedTryFinally(JdkVersion jdkVersion) throws Exception {
        // Nested try-finally blocks
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        try {
                          result = 10
                        } finally {
                          result = result + 5
                        }
                      } finally {
                        result = result + 3
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(18);  // 10 + 5 + 3 = 18
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyEmptyFinally(JdkVersion jdkVersion) throws Exception {
        // Empty finally block
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 99
                      } finally {
                      }
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(99);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyExceptionInFinally(JdkVersion jdkVersion) throws Exception {
        // Exception in finally overrides try exception
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): void {
                      try {
                        throw new Exception("try")
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
    public void testTryFinallyMultipleStatements(JdkVersion jdkVersion) throws Exception {
        // Multiple statements in finally
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 1
                      let b: int = 2
                      try {
                        a = 10
                      } finally {
                        b = 20
                        a = a + b
                      }
                      return a
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(30);  // 10 + 20 = 30
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyReturnOverride(JdkVersion jdkVersion) throws Exception {
        // Return in finally overrides return in try
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 1
                      } finally {
                        return 2
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);  // Finally return wins
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyValueNotModified(JdkVersion jdkVersion) throws Exception {
        // Return value captured before finally modifies variable
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 1
                      try {
                        x = 2
                        return x
                      } finally {
                        x = 3
                      }
                    }
                    getValue(): int {
                      let x: int = 1
                      try {
                        x = 2
                        return x
                      } finally {
                        x = 3
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);  // Returns 2, not 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyWithException(JdkVersion jdkVersion) throws Exception {
        // Exception in try - finally runs before propagation
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    private cleanedUp: boolean = false
                    test(): void {
                      try {
                        throw new Exception("error")
                      } finally {
                        this.cleanedUp = true
                      }
                    }
                    wasCleanedUp(): boolean {
                      return this.cleanedUp
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThatThrownBy(() -> {
            instanceRunner.invoke("test");
        }).isInstanceOf(Exception.class);
        assertThat((boolean) instanceRunner.<Boolean>invoke("wasCleanedUp")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryFinallyWithReturn(JdkVersion jdkVersion) throws Exception {
        // Return in try - finally still runs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private executed: boolean = false
                    test(): int {
                      try {
                        return 42
                      } finally {
                        this.executed = true
                      }
                    }
                    wasExecuted(): boolean {
                      return this.executed
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("test")).isEqualTo(42);
        assertThat((boolean) instanceRunner.<Boolean>invoke("wasExecuted")).isTrue();
    }
}
