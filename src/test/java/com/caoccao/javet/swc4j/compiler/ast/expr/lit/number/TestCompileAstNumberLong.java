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
 * Tests for long number literals.
 * Phase 2: Long Values (12 tests)
 */
public class TestCompileAstNumberLong extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongExplicitAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const value: long = 999
                      return value
                    }
                  }
                }""");
        assertEquals(999L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongFromInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const value: long = 42
                      return value
                    }
                  }
                }""");
        assertEquals(42L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 9223372036854775807
                    }
                  }
                }""");
        assertEquals(Long.MAX_VALUE, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongMinValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const val: long = -9223372036854775807
                      return val
                    }
                  }
                }""");
        assertEquals(Long.MIN_VALUE + 1, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 1
                    }
                  }
                }""");
        assertEquals(1L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongPositive(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 123
                    }
                  }
                }""");
        assertEquals(123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongSmallValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 100
                    }
                  }
                }""");
        assertEquals(100L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongLargeValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 2147483648
                    }
                  }
                }""");
        assertEquals(((long) Integer.MAX_VALUE) + 1L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const val: long = -123
                      return val
                    }
                  }
                }""");
        assertEquals(-123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 123
                      return a
                    }
                  }
                }""");
        assertEquals(123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 123
                    }
                  }
                }""");
        assertEquals(123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 0
                    }
                  }
                }""");
        assertEquals(0L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
