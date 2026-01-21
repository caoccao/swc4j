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
 * Tests for float number literals.
 * Phase 3 part 1: Float Values (8 tests)
 */
public class TestCompileAstNumberFloat extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatNegative(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return -123.456
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-123.456f, (float) classA.getMethod("test").invoke(instance), 0.001f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatOne(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 1.0
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1.0f, (float) classA.getMethod("test").invoke(instance), 0.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatPositive(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 123.456
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(123.456f, (float) classA.getMethod("test").invoke(instance), 0.001f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatSmallDecimal(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 0.001
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0.001f, (float) classA.getMethod("test").invoke(instance), 0.00001f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatTwo(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 2.0
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2.0f, (float) classA.getMethod("test").invoke(instance), 0.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatZero(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 0.0
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0.0f, (float) classA.getMethod("test").invoke(instance), 0.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnFloatWithTypeAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      var a: float = 123.456
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(123.456F, (float) classA.getMethod("test").invoke(instance), 0.00001F);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnFloatWithTypeAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): float {
                      return 123.456
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(123.456F, (float) classA.getMethod("test").invoke(instance), 0.00001F);
    }
}
