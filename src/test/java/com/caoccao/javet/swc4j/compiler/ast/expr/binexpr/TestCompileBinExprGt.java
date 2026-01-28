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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileBinExprGt extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 50
                      const b: int = 50
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 50 > 50 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 51
                      const b: int = 50
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 51 > 50 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteShortGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 99
                      const b: short = 100
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 99 > 100 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.13
                      const b: double = 3.14
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 3.13 > 3.14 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.15
                      const b: double = 3.14
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 3.15 > 3.14 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDoubleGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.6
                      const b: double = 1.5
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 1.6 > 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.4
                      const b: float = 2.5
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 2.4 > 2.5 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.6
                      const b: float = 1.5
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 1.6 > 1.5 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 43
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 42 > 43 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 43
                      const b: int = 42
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 43 > 42 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 99
                      const b: long = 100
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 99 > 100 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 101
                      const b: long = 100
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 101 > 100 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeDoubleGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.7976931348623157E308
                      const b: double = 1.7976931348623156E308
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // First is greater than second
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567889
                      const b: long = 1234567890
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 1234567889 > 1234567890 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567891
                      const b: long = 1234567890
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 1234567891 > 1234567890 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMaxIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 2147483646
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // MAX_VALUE > MAX_VALUE - 1 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMinIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -2147483646
                      const b: int = -2147483647
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // -2147483646 > -2147483647 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeDoubleGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = -3.14159
                      const b: double = -3.14160
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // -3.14159 > -3.14160 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeFloatGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = -2.5
                      const b: float = -2.6
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // -2.5 > -2.6 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -42
                      const b: int = -43
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // -42 > -43 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1234567890
                      const b: long = -1234567891
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // -1234567890 > -1234567891 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 31999
                      const b: short = 32000
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 31999 > 32000 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortGreaterThanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32001
                      const b: short = 32000
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 32001 > 32000 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroDoubleGreaterThan(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 0.1
                      const b: double = 0.0
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 0.1 > 0.0 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroGreaterThanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 0 > 0 is false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = -1
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test")); // 0 > -1 is true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 1
                      const c = a > b
                      return c
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test")); // 0 > 1 is false
    }
}
