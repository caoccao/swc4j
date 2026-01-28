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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for integer (int) number literals.
 * Phase 1: Basic Integer Values (15 tests)
 */
public class TestCompileAstNumberInt extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLargeNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return -1000000
                    }
                  }
                }""");
        assertEquals(-1000000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLargePositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 1000000
                    }
                  }
                }""");
        assertEquals(1000000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 2147483647
                    }
                  }
                }""");
        assertEquals(Integer.MAX_VALUE, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMediumNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return -1000
                    }
                  }
                }""");
        assertEquals(-1000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMediumPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 1000
                    }
                  }
                }""");
        assertEquals(1000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMinValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const val: int = -2147483647
                      return val
                    }
                  }
                }""");
        assertEquals(Integer.MIN_VALUE + 1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return -42
                    }
                  }
                }""");
        assertEquals(-42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 1
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 42
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntSmallNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return -1
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntSmallPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 5
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 0
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 123
                    }
                  }
                }""");
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnIntegerWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 123
                      return a
                    }
                  }
                }""");
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnIntegerWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 123
                    }
                  }
                }""");
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
