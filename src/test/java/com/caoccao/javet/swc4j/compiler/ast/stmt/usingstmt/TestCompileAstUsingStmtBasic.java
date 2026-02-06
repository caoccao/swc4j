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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for basic using declaration statement compilation.
 * Covers: null resource, close verification, block-scoped close, return inside using.
 */
public class TestCompileAstUsingStmtBasic extends BaseTestCompileAstUsingStmt {

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
