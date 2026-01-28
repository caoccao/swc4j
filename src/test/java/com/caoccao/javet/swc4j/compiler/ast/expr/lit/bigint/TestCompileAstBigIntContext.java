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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for BigInt in various usage contexts.
 * Phase 4: BigInt Operations Context (10 tests)
 */
public class TestCompileAstBigIntContext extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntConditionalReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): BigInteger {
                      if (flag) {
                        return 100n
                      } else {
                        return 200n
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(new BigInteger("100"), instanceRunner.invoke("test", true));
        assertEquals(new BigInteger("200"), instanceRunner.invoke("test", false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntInferredType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const value = 12345n
                      return value
                    }
                  }
                }""");
        assertEquals(new BigInteger("12345"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMixedWithInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const bigValue: BigInteger = 9999n
                      const intValue: int = 42
                      return bigValue
                    }
                  }
                }""");
        assertEquals(new BigInteger("9999"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMixedWithLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const bigValue: BigInteger = 123456789n
                      const longValue: long = 9876543210
                      return bigValue
                    }
                  }
                }""");
        assertEquals(new BigInteger("123456789"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntMultipleReturns(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test1(): BigInteger {
                      return 111n
                    }
                    test2(): BigInteger {
                      return 222n
                    }
                    test3(): BigInteger {
                      return 333n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(new BigInteger("111"), instanceRunner.invoke("test1"));
        assertEquals(new BigInteger("222"), instanceRunner.invoke("test2"));
        assertEquals(new BigInteger("333"), instanceRunner.invoke("test3"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntReassignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      let value: BigInteger = 100n
                      value = 200n
                      value = 300n
                      return value
                    }
                  }
                }""");
        assertEquals(new BigInteger("300"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 42n
                    }
                  }
                }""");
        assertEquals(new BigInteger("42"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntSequentialAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 10n
                      const b: BigInteger = 20n
                      const c: BigInteger = 30n
                      return b
                    }
                  }
                }""");
        assertEquals(new BigInteger("20"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntWithBigIntegerAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value: BigInteger = 777n
                      return value
                    }
                  }
                }""");
        assertEquals(new BigInteger("777"), runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntWithExplicitAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const value: java.math.BigInteger = 555n
                      return value
                    }
                  }
                }""");
        assertEquals(new BigInteger("555"), runner.createInstanceRunner("com.A").invoke("test"));
    }
}
