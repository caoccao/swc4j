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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for basic class expression compilation.
 * Covers: class-as-value, superclass, unique naming, named class expressions.
 */
public class TestCompileAstClassExprBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprNamedClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class A {
                    test(): boolean {
                      const C: Class = class MyCounter {
                        count: int = 0
                      }
                      return C.getName().endsWith("MyCounter")
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprReturnsClassObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class A {
                    test(): boolean {
                      const C: Class = class Local { }
                      return C.getName().endsWith("Local")
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprSuperClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class Base {
                  }
                
                  export class A {
                    test(): boolean {
                      const C: Class = class Local extends Base { }
                      return C.getSuperclass().getName().endsWith("Base")
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprUniqueNames(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Class } from 'java.lang'
                namespace com {
                  export class A {
                    test(): boolean {
                      const A1: Class = class { }
                      const A2: Class = class { }
                      const name1: String = A1.getName()
                      const name2: String = A2.getName()
                      return name1.indexOf(name2) == -1 && name2.indexOf(name1) == -1
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test")).isTrue();
    }
}
