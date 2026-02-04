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


/**
 * Tests for basic BigInt literals.
 * Phase 1: Basic BigInt Values (15 tests)
 * <p>
 * NOTE: These tests are placeholders. BigIntLiteralProcessor.java needs to be implemented first.
 */
public class TestCompileAstBigIntBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value: BigInteger = 123n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("123"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntExceedingLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 9223372036854775808n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("9223372036854775808"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntLet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      let value: BigInteger = 456n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("456"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMinusSign(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -123n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("-123"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMultipleVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a = 100n
                      const b = 200n
                      const c = 300n
                      return c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("300"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntNegativeLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -123456789n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("-123456789"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntNegativeSmall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -42n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("-42"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntNoSign(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 123n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("123"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 1n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(BigInteger.ONE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPositiveLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 123456789n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("123456789"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPositiveSign(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return +123n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("123"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPositiveSmall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 42n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("42"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value = 999n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("999"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntVeryLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789n
                    }
                  }
                }""");
        assertThat(
                runner.createInstanceRunner("com.A").<Object>invoke("test")
        ).isEqualTo(
                new BigInteger("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 0n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(BigInteger.ZERO);
    }
}
