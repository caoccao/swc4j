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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.number;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for double number literals.
 * Phase 3 part 2: Double Values (7 tests)
 */
public class TestCompileAstNumberDouble extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return -123.456
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(-123.456, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 1.0
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(1.0, within(0.0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoublePositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 123.456
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(123.456, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleSmallDecimal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 0.000001
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(0.000001, within(0.0000001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 0.0
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(0.0, within(0.0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDoubleWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      var a: double = 123.456
                      return a
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(123.456D, within(0.00001D));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDoubleWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 123.456
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test")).isCloseTo(123.456D, within(0.00001D));
    }
}
