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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.arraylit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for basic array creation and type operations.
 */
public class TestCompileAstArrayLitBasic extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayAnnotation(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [1, 2, 3]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayListOfDoubles(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<Double> = [1.5, 2.5, 3.5]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(1.5, 2.5, 3.5), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayListOfStrings(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<String> = ["foo", "bar", "baz"]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("foo", "bar", "baz"), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean[] = [true, false, true]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new boolean[]{true, false, true}, (boolean[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double[] = [1.5, 2.5, 3.5]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new double[]{1.5, 2.5, 3.5}, (double[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleArrayOperations(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double[] = [1.5, 2.5, 3.5]
                      a[0] = 10.5
                      return a[0]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10.5, (Double) classA.getMethod("test").invoke(instance), 0.001);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyIntArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = []
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new int[]{}, (int[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float[] = [1.0, 2.0, 3.0]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, (float[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testListAnnotation(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<Integer> = [1, 2, 3]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(1, 2, 3), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long[] = [100, 200, 300]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new long[]{100L, 200L, 300L}, (long[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnArrayWithElements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return [1, 2, 3]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Object result = classA.getMethod("test").invoke(instance);
        ArrayList<?> list = (ArrayList<?>) result;
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnEmptyArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return []
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Object result = classA.getMethod("test").invoke(instance);
        assertNotNull(result);
        assertEquals(ArrayList.class, result.getClass());
        assertEquals(0, ((ArrayList<?>) result).size());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String[] = ["hello", "world"]
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertArrayEquals(new String[]{"hello", "world"}, (String[]) classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringArrayOperations(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String[] = ["hello", "world"]
                      a[0] = "goodbye"
                      return a[0]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("goodbye", classA.getMethod("test").invoke(instance));
    }
}
