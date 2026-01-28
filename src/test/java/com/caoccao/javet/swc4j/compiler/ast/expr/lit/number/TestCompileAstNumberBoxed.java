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
 * Tests for boxed number types (Integer, Long, Float, Double, Byte, Short).
 * Phase 6: Boxed Types (12 tests)
 */
public class TestCompileAstNumberBoxed extends BaseTestCompileSuite {

    // Integer boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnByteObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 100
                      return a
                    }
                  }
                }""");
        assertEquals((byte) 100, (byte) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnByteObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Byte {
                      return 127
                    }
                  }
                }""");
        assertEquals((byte) 127, (byte) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Long boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDoubleObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 123.5
                      return a
                    }
                  }
                }""");
        assertEquals(123.5, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDoubleObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Double {
                      return 123.5
                    }
                  }
                }""");
        assertEquals(123.5, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Float boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnFloatObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 123.5
                      return a
                    }
                  }
                }""");
        assertEquals(123.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnFloatObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Float {
                      return 123.5
                    }
                  }
                }""");
        assertEquals(123.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Double boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnIntegerObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 123
                      return a
                    }
                  }
                }""");
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnIntegerObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Integer {
                      return 123
                    }
                  }
                }""");
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Byte boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 123
                      return a
                    }
                  }
                }""");
        assertEquals(123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLongObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Long {
                      return 123
                    }
                  }
                }""");
        assertEquals(123L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Short boxed tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortObjectWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 123
                      return a
                    }
                  }
                }""");
        assertEquals((short) 123, (short) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnShortObjectWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Short {
                      return 123
                    }
                  }
                }""");
        assertEquals((short) 123, (short) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
