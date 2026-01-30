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

package com.caoccao.javet.swc4j.compiler.ast.expr.condexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Test suite for type-specific branches and type widening in conditional expressions.
 * Tests different primitive types (int, long, double, float, String) and type widening scenarios.
 */
public class TestCompileAstCondExprTypes extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBranches(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 1.5 : 2.5
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(1.5, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const flag: boolean = true
                      const floatVal: float = 10.0
                      const doubleVal: double = 20.0
                      return flag ? floatVal : doubleVal
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(10.0, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 10 : 20.5
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(10.0, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndLongWidening(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const longVal: long = 20
                      return flag ? 10 : longVal
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(20L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBranches(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const val1: long = 100
                      const val2: long = 200
                      return flag ? val1 : val2
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(200L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBranches(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? "yes" : "no"
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("yes");
    }
}
