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

package com.caoccao.javet.swc4j.compiler.ast.clazz;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstFunction extends BaseTestCompileSuite {

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
    public void testParameterTypeInference(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int) {
                      const c: int = a + b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test", int.class, int.class).invoke(instance, 5, 10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParameterUsedInExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int): int {
                      const c = a + b
                      return c
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(30, classA.getMethod("test", int.class, int.class).invoke(instance, 10, 20));
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
    public void testVarargs(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, ...b: int[]) {
                      return b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new int[]{2, 3, 4}, (int[]) classA.getMethod("test", int.class, int[].class).invoke(instance, 1, new int[]{2, 3, 4}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsDoubleType(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: double[]): double {
                      return values[0]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1.5, (double) classA.getMethod("test", double[].class).invoke(instance, new double[]{1.5, 2.5}), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsStringType(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(prefix: String, ...values: String[]): String {
                      return values[0]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello", classA.getMethod("test", String.class, String[].class).invoke(instance, "test", new String[]{"hello", "world"}));
    }
}
