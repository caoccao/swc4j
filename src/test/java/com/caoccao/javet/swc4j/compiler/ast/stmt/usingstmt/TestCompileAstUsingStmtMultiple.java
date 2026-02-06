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
 * Tests for using declarations with multiple resources.
 * Covers: two resources, sequential using declarations, nested using blocks.
 */
public class TestCompileAstUsingStmtMultiple extends BaseTestCompileAstUsingStmt {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleUsingOneNull(JdkVersion jdkVersion) throws Exception {
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
                      {
                        using r1: Resource = new Resource(log, "a")
                        using r2: AutoCloseable = null
                        log.add("body")
                      }
                      return log
                    }
                  }
                }""");
        // Null resource skips close, non-null resource closes normally
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("body", "close:a"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedUsingBlocks(JdkVersion jdkVersion) throws Exception {
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
                      {
                        using outer: Resource = new Resource(log, "outer")
                        {
                          using inner: Resource = new Resource(log, "inner")
                          log.add("innerBody")
                        }
                        log.add("outerBody")
                      }
                      return log
                    }
                  }
                }""");
        // Inner block closes first, then outer
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("innerBody", "close:inner", "outerBody", "close:outer"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTwoSequentialUsingDeclarations(JdkVersion jdkVersion) throws Exception {
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
                      {
                        using r1: Resource = new Resource(log, "a")
                        using r2: Resource = new Resource(log, "b")
                        log.add("body")
                      }
                      return log
                    }
                  }
                }""");
        // Resources close in reverse declaration order: b first, then a
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("body", "close:b", "close:a"));
    }
}
