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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for using declarations with control flow statements.
 * Covers: break in loop, continue in loop, return inside using, using inside try-catch,
 * and suppressed exception support in control flow scenarios (Phase 3).
 */
public class TestCompileAstUsingStmtControlFlow extends BaseTestCompileAstUsingStmt {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingInsideTryCatch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    private name: String
                    constructor(log: ArrayList, name: String) {
                      this.log = log
                      this.name = name
                    }
                    close(): void {
                      this.log.add("close:" + this.name)
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      try {
                        using r: Resource = new Resource(log, "a")
                        log.add("tryBody")
                        throw new Error("fail")
                      } catch (e: Error) {
                        log.add("caught")
                      }
                      return log
                    }
                  }
                }""");
        // Resource closes when exception is thrown (via exception handler), then catch runs
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("tryBody", "close:a", "caught"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingInsideTryCatchWithSuppressed(JdkVersion jdkVersion) throws Exception {
        // Phase 3: using inside try-catch, body+close throw, catch gets primary with suppressed
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
                      try {
                        using r: BadCloser = new BadCloser(log)
                        log.add("tryBody")
                        throw new Error("body error")
                      } catch ({message}: Error) {
                        log.add("caught:" + message)
                      }
                      return log
                    }
                  }
                }""");
        // close() is called (logs "close"), body exception is caught with "body error" message
        // (close error is suppressed on the body error, but catch only sees primary)
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("tryBody", "close", "caught:body error"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingInsideTryFinally(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    private name: String
                    constructor(log: ArrayList, name: String) {
                      this.log = log
                      this.name = name
                    }
                    close(): void {
                      this.log.add("close:" + this.name)
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      try {
                        using r: Resource = new Resource(log, "a")
                        log.add("tryBody")
                      } finally {
                        log.add("finally")
                      }
                      return log
                    }
                  }
                }""");
        // Resource closes at block exit, then finally runs
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("tryBody", "close:a", "finally"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingWithBreakInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    private name: String
                    constructor(log: ArrayList, name: String) {
                      this.log = log
                      this.name = name
                    }
                    close(): void {
                      this.log.add("close:" + this.name)
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      for (let i: int = 0; i < 3; i = i + 1) {
                        using r: Resource = new Resource(log, "r" + i)
                        log.add("iter:" + i)
                        if (i == 1) {
                          break
                        }
                      }
                      return log
                    }
                  }
                }""");
        // Iteration 0: body, close. Iteration 1: body, break (inline close)
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("iter:0", "close:r0", "iter:1", "close:r1"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingWithContinueInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    private name: String
                    constructor(log: ArrayList, name: String) {
                      this.log = log
                      this.name = name
                    }
                    close(): void {
                      this.log.add("close:" + this.name)
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      for (let i: int = 0; i < 3; i = i + 1) {
                        using r: Resource = new Resource(log, "r" + i)
                        if (i == 1) {
                          continue
                        }
                        log.add("body:" + i)
                      }
                      return log
                    }
                  }
                }""");
        // Iteration 0: body, close. Iteration 1: continue (inline close, skip body). Iteration 2: body, close.
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("body:0", "close:r0", "close:r1", "body:2", "close:r2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUsingWithReturnInLoop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class Resource implements AutoCloseable {
                    private log: ArrayList
                    private name: String
                    constructor(log: ArrayList, name: String) {
                      this.log = log
                      this.name = name
                    }
                    close(): void {
                      this.log.add("close:" + this.name)
                    }
                  }
                  export class A {
                    test(): Object {
                      const log: ArrayList = new ArrayList()
                      this.doWork(log)
                      return log
                    }
                    doWork(log: ArrayList): void {
                      for (let i: int = 0; i < 3; i = i + 1) {
                        using r: Resource = new Resource(log, "r" + i)
                        log.add("iter:" + i)
                        if (i == 1) {
                          return
                        }
                      }
                    }
                  }
                }""");
        // Iteration 0: body, close. Iteration 1: body, return (inline close)
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("iter:0", "close:r0", "iter:1", "close:r1"));
    }
}
