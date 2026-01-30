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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.bigint;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for BigInt with various type annotations.
 * Phase 6: Type Annotations (10 tests)
 */
public class TestCompileAstBigIntAnnotations extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationBigInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value: BigInteger = 12345n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("12345"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const value: boolean = 999n
                      return value
                    }
                  }
                }""");
        // Non-zero BigInt → true
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      const value: byte = 127n
                      return value
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((byte) 127);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const value: double = 987654321n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(987654321.0, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      const value: float = 654321n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Float>invoke("test")).isCloseTo(654321f, within(0.001f));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const value: int = 54321n
                      return value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(54321);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const value: long = 9876543210n
                      return value
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(9876543210L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value: BigInteger = 111n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("111"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 222n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("222"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntAnnotationShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const value: short = 32767n
                      return value
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((short) 32767);
    }
}
