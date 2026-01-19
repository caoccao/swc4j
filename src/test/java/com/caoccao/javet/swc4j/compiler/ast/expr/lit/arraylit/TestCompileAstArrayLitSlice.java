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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the Array.slice() method.
 */
public class TestCompileAstArrayLitSlice extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySlice(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(1, 4)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3, 4), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceChaining(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5, 6, 7, 8]
                      return arr.slice(1, 6).slice(1, 4)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(3, 4, 5), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceEmpty(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.pop()
                      arr.pop()
                      arr.pop()
                      return arr.slice()
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceNegativeBoth(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(-4, -1)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3, 4), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceNegativeEnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(1, -1)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3, 4), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceNegativeStart(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(-3)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(3, 4, 5), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceNoArgs(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const copy = arr.slice()
                      arr.push(4)
                      return copy
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(1, 2, 3), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceNoEnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(2)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(3, 4, 5), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.slice(1, 10)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      const sliced = arr.slice(1, 3)
                      arr.push(6)
                      return sliced
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(2, 3), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceStartGreaterThanEnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.slice(3, 1)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of(), classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySliceStrings(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["a", "b", "c", "d", "e"]
                      return arr.slice(1, 3)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(List.of("b", "c"), classA.getMethod("test").invoke(instance));
    }
}
