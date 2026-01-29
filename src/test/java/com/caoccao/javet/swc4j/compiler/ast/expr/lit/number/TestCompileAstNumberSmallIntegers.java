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


/**
 * Tests for byte and short number literals.
 * Phase 5: Byte and Short Values (12 tests)
 */
public class TestCompileAstNumberSmallIntegers extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return 127
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Byte.MAX_VALUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMinValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return -128
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Byte.MIN_VALUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return -100
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((byte) -100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBytePositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return 100
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((byte) 100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return 0
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((byte) 0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnByteWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = -128
                      return a
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((byte) -128);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnByteWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      return 50
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((byte) 50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const val: short = 32767
                      return val
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Short.MAX_VALUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortMinValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const val: short = -32768
                      return val
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Short.MIN_VALUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortNegativeOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const val: short = -1
                      return val
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((short) -1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      var a: short = 123
                      return a
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((short) 123);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      return 123
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo((short) 123);
    }
}
