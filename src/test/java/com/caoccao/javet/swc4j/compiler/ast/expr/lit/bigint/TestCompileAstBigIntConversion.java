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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for BigInt to primitive type conversions.
 * Phase 2: Conversion to Primitives (12 tests)
 * <p>
 * NOTE: These tests are placeholders. BigIntLiteralProcessor.java needs to be implemented first.
 */
public class TestCompileAstBigIntConversion extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntLargeToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const value = 4294967296n
                      return value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntNegativeToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const value = -12345n
                      return value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(-12345);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const value = 123n
                      return value
                    }
                  }
                }""");
        // Non-zero BigInt → true
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToByte(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): byte {
                      const value = 100n
                      return value
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((byte) 100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const value = 123456789012345n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(123456789012345.0, within(1.0));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      const value = 123456789n
                      return value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Float>invoke("test")).isCloseTo(123456789f, within(1.0f));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const value = 12345n
                      return value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(12345);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToIntTruncation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const value = 9223372036854775808n
                      return value
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const value = 1234567890123n
                      return value
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1234567890123L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToLongTruncation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const value = 18446744073709551616n
                      return value
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(0L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntToShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const value = 30000n
                      return value
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((short) 30000);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntZeroToBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const value = 0n
                      return value
                    }
                  }
                }""");
        // Zero BigInt → false
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }
}
