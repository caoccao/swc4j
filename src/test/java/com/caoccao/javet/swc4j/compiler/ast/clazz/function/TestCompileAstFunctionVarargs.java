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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstFunctionVarargs extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAfterRegularParameter(JdkVersion jdkVersion) throws Exception {
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
    public void testVarargsAsOnlyParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int[] {
                      return values
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) classA.getMethod("test", int[].class).invoke(instance, new int[]{1, 2, 3}));
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
    public void testVarargsEmpty(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test", int[].class).invoke(instance, new int[]{}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsIndexAccess(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getFirst(...values: int[]): int {
                      return values[0]
                    }
                    getLast(...values: int[]): int {
                      return values[values.length - 1]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("getFirst", int[].class).invoke(instance, new int[]{1, 2, 3}));
        assertEquals(3, classA.getMethod("getLast", int[].class).invoke(instance, new int[]{1, 2, 3}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsLengthAccess(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("count", int[].class).invoke(instance, new int[]{1, 2, 3, 4, 5}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsManyElements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test", int[].class).invoke(instance, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
    }

    // TODO: This test requires proper for-loop iteration support on native int[] which is hanging
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testVarargsIteration(JdkVersion jdkVersion) throws Exception {
    //     var map = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class A {
    //                 sum(...values: int[]): int {
    //                   let total: int = 0
    //                   for (let i: int = 0; i < values.length; i++) {
    //                     total = total + values[i]
    //                   }
    //                   return total
    //                 }
    //               }
    //             }""");
    //     Class<?> classA = loadClass(map.get("com.A"));
    //     var instance = classA.getConstructor().newInstance();
    //     assertEquals(15, classA.getMethod("sum", int[].class).invoke(instance, new int[]{1, 2, 3, 4, 5}));
    // }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsSingleElement(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test", int[].class).invoke(instance, new int[]{42}));
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
