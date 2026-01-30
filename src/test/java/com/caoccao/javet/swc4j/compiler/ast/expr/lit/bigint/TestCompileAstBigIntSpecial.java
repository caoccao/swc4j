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
 * Tests for special BigInt values and edge cases.
 * Phase 3: Special Values (10 tests)
 */
public class TestCompileAstBigIntSpecial extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntFactorial100(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000n
                    }
                  }
                }""");
        // 100! = 93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000
        assertThat(
                runner.createInstanceRunner("com.A").<Object>invoke("test")
        ).isEqualTo(
                new BigInteger("93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMaxLongPlusOne(JdkVersion jdkVersion) throws Exception {
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
    public void testBigIntMaxLongValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 9223372036854775807n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("9223372036854775807"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMinLongMinusOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -9223372036854775809n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("-9223372036854775809"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMinLongValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -9223372036854775808n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new BigInteger("-9223372036854775808"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntOneValue(JdkVersion jdkVersion) throws Exception {
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
    public void testBigIntPowerOfTwo128(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 340282366920938463463374607431768211456n
                    }
                  }
                }""");
        // 2^128 = 340282366920938463463374607431768211456
        assertThat(
                runner.createInstanceRunner("com.A").<Object>invoke("test")
        ).isEqualTo(
                new BigInteger("340282366920938463463374607431768211456")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntTenValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 10n
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(BigInteger.TEN);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntVeryLargeNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return -999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999n
                    }
                  }
                }""");
        assertThat(
                runner.createInstanceRunner("com.A").<Object>invoke("test")
        ).isEqualTo(
                new BigInteger("-999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntZeroValue(JdkVersion jdkVersion) throws Exception {
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
