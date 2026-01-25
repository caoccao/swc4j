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

package com.caoccao.javet.swc4j.compiler.ast.clazz.function;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstFunctionParams extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: Array<Integer>) {
                      return b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3, 4), classA.getMethod("test", int.class, List.class).invoke(instance, 1, List.of(2, 3, 4)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): boolean {
                      return !flag
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test", boolean.class).invoke(instance, true));
        assertEquals(true, classA.getMethod("test", boolean.class).invoke(instance, false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: double): double {
                      return a * 2.0
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(6.28, (double) classA.getMethod("test", double.class).invoke(instance, 3.14), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: long): long {
                      return value + 1
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(9999999999L + 1, classA.getMethod("test", long.class).invoke(instance, 9999999999L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testManyParameters(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int, d: int, e: int, f: int, g: int, h: int): int {
                      return a + b + c + d + e + f + g + h
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(36, classA.getMethod("test", int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class)
                .invoke(instance, 1, 2, 3, 4, 5, 6, 7, 8));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleParameters(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int): int {
                      return a + b + c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(60, classA.getMethod("test", int.class, int.class, int.class).invoke(instance, 10, 20, 30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParameterWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: String, c: double): String {
                      return b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello", classA.getMethod("test", int.class, String.class, double.class).invoke(instance, 42, "hello", 3.14));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int): int {
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test", int.class).invoke(instance, 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(s: String): String {
                      return s
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("test string", classA.getMethod("test", String.class).invoke(instance, "test string"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWideTypeParameterSlotAllocation(JdkVersion jdkVersion) throws Exception {
        // Long and double take 2 local variable slots
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: long, b: double, c: int): long {
                      return a + (c as long)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(110L, classA.getMethod("test", long.class, double.class, int.class).invoke(instance, 100L, 3.14, 10));
    }
}
