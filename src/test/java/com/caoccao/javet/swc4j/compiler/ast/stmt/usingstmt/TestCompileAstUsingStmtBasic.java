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

package com.caoccao.javet.swc4j.compiler.ast.stmt.usingstmt;

import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for basic using declaration statement compilation.
 * Covers: null resource, close verification, block-scoped close, return inside using,
 * and suppressed exception support (Phase 3).
 */
public class TestCompileAstUsingStmtBasic extends BaseTestCompileAstUsingStmt {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBodyAndCloseThrow(JdkVersion jdkVersion) throws Exception {
        // Phase 3: body throws and close() throws — primary exception has close as suppressed
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class BadCloser implements AutoCloseable {
                    private log: ArrayList
                    constructor(log: ArrayList) {
                      this.log = log
                    }
                    close(): void {
                      this.log.add("close")
                      throw new Error("close error")
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      {
                        using r: BadCloser = new BadCloser(log)
                        log.add("body")
                        throw new Error("body error")
                      }
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Error.class)
                .satisfies(e -> {
                    assertThat(e.getMessage()).isEqualTo("body error");
                    assertThat(e.getSuppressed()).hasSize(1);
                    assertThat(e.getSuppressed()[0]).isInstanceOf(Error.class);
                    assertThat(e.getSuppressed()[0].getMessage()).isEqualTo("close error");
                });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBodyThrowsCloseSucceeds(JdkVersion jdkVersion) throws Exception {
        // Phase 3: body throws, close() succeeds — primary propagates with no suppressed
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    constructor(log: ArrayList) {
                      this.log = log
                    }
                    close(): void {
                      this.log.add("close")
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      {
                        using r: Resource = new Resource(log)
                        log.add("body")
                        throw new Error("body error")
                      }
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Error.class)
                .satisfies(e -> {
                    assertThat(e.getMessage()).isEqualTo("body error");
                    assertThat(e.getSuppressed()).isEmpty();
                });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBodyThrowsCloseSucceedsWithLog(JdkVersion jdkVersion) throws Exception {
        // Phase 3: body throws, close() succeeds — verify close was called via log
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    constructor(log: ArrayList) {
                      this.log = log
                    }
                    close(): void {
                      this.log.add("close")
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      try {
                        {
                          using r: Resource = new Resource(log)
                          log.add("body")
                          throw new Error("fail")
                        }
                      } catch ({message}: Error) {
                        log.add("caught:" + message)
                      }
                      return log
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("body", "close", "caught:fail"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCloseThrowsOnNormalPath(JdkVersion jdkVersion) throws Exception {
        // Phase 3: no body exception, close() throws — close exception propagates
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class BadCloser implements AutoCloseable {
                    private log: ArrayList
                    constructor(log: ArrayList) {
                      this.log = log
                    }
                    close(): void {
                      this.log.add("close")
                      throw new Error("close error")
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      {
                        using r: BadCloser = new BadCloser(log)
                        log.add("body")
                      }
                      return log
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Error.class)
                .satisfies(e -> {
                    assertThat(e.getMessage()).isEqualTo("close error");
                    assertThat(e.getSuppressed()).isEmpty();
                });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testHasCloseMethodButNotAutoCloseable(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class HasClose {
                    close(): void {}
                  }
                  export class A {
                    test(): void {
                      using r: HasClose = new HasClose()
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("AutoCloseable");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNonAutoCloseableTypeRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class NotCloseable {
                    value: int = 42
                  }
                  export class A {
                    test(): void {
                      using r: NotCloseable = new NotCloseable()
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("AutoCloseable");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullResourceBodyThrows(JdkVersion jdkVersion) throws Exception {
        // Phase 3: null resource, body throws — body exception propagates, no NPE, no suppressed
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      {
                        using res: AutoCloseable = null
                        throw new Error("body error")
                      }
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Error.class)
                .satisfies(e -> {
                    assertThat(e.getMessage()).isEqualTo("body error");
                    assertThat(e.getSuppressed()).isEmpty();
                });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingCloseCalledAtBlockExit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Resource implements AutoCloseable {
                    public closedCount: int = 0
                    close(): void {
                      this.closedCount = this.closedCount + 1
                    }
                  }
                  export class A {
                    test(): int {
                      const r: Resource = new Resource()
                      {
                        using res: Resource = r
                      }
                      return r.closedCount
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingCloseNotCalledBeforeReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Resource implements AutoCloseable {
                    public closedCount: int = 0
                    close(): void {
                      this.closedCount = this.closedCount + 1
                    }
                  }
                  export class A {
                    test(): int {
                      const r: Resource = new Resource()
                      using res: Resource = r
                      return r.closedCount
                    }
                  }
                }""");
        // Return evaluates r.closedCount (=0) BEFORE inline close runs
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingNullResource(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      using res: AutoCloseable = null
                      return 42
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingReturnInsideBlock(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Resource implements AutoCloseable {
                    public closedCount: int = 0
                    close(): void {
                      this.closedCount = this.closedCount + 1
                    }
                  }
                  export class A {
                    test(): int {
                      const r: Resource = new Resource()
                      {
                        using res: Resource = r
                        return r.closedCount
                      }
                    }
                  }
                }""");
        // Return evaluates r.closedCount (=0) BEFORE close runs
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingVoidMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Resource implements AutoCloseable {
                    public closedCount: int = 0
                    close(): void {
                      this.closedCount = this.closedCount + 1
                    }
                  }
                  export class A {
                    public r: Resource = new Resource()
                    doWork(): void {
                      using res: Resource = this.r
                    }
                    test(): int {
                      this.doWork()
                      return this.r.closedCount
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingWithExceptionInTryBody(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Resource implements AutoCloseable {
                    public closedCount: int = 0
                    close(): void {
                      this.closedCount = this.closedCount + 1
                    }
                  }
                  export class A {
                    test(): int {
                      const r: Resource = new Resource()
                      try {
                        {
                          using res: Resource = r
                          throw new Error("fail")
                        }
                      } catch (e: Error) {
                        // resource should have been closed by the exception handler
                      }
                      return r.closedCount
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }
}
