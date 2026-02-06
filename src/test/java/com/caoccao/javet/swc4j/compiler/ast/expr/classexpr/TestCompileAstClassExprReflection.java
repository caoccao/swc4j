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

package com.caoccao.javet.swc4j.compiler.ast.expr.classexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for class expression reflection capabilities.
 * Covers: fields and methods via reflection, constructor, declared field count, declared method count.
 */
public class TestCompileAstClassExprReflection extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                import { Field, Method } from 'java.lang.reflect'
                namespace com {
                  export class A {
                    test(): boolean {
                      return (class {
                        value: int
                
                        constructor() {
                          this.value = 7
                        }
                
                        getValue(): int {
                          return this.value
                        }
                      }).getDeclaredFields().length == 1
                        && (class {
                          value: int
                
                          constructor() {
                            this.value = 7
                          }
                
                          getValue(): int {
                            return this.value
                          }
                        }).getDeclaredMethods().length == 1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprDeclaredFieldCount(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const zero: int = (class { }).getDeclaredFields().length
                      const one: int = (class { a: int = 1 }).getDeclaredFields().length
                      const two: int = (class { a: int = 1; b: int = 2 }).getDeclaredFields().length
                      return [zero, one, two]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(0, 1, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprDeclaredMethodCount(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const zero: int = (class { }).getDeclaredMethods().length
                      const one: int = (class { foo(): int { return 1 } }).getDeclaredMethods().length
                      const two: int = (class { foo(): int { return 1 }; bar(): int { return 2 } }).getDeclaredMethods().length
                      return [zero, one, two]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(0, 1, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprFieldsAndMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                import { Field, Method } from 'java.lang.reflect'
                namespace com {
                  export class A {
                    test(): boolean {
                      return (class {
                        value: int = 1
                
                        getValue(): int {
                          return this.value
                        }
                      }).getDeclaredFields().length == 1
                        && (class {
                          value: int = 1
                
                          getValue(): int {
                            return this.value
                          }
                        }).getDeclaredMethods().length == 1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test")).isTrue();
    }
}
