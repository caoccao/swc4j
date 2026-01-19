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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.objectlit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for property shorthand syntax in object literals.
 */
public class TestCompileAstObjectLitShorthand extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandMixedWithComputedKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x = 10
                      const key = "dynamic"
                      const obj = {x, [key]: 20, normal: 30}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10, "dynamic", 20, "normal", 30), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandMixedWithNormalProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x = 10
                      const y = 20
                      const obj = {a: 1, x, b: 2, y, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "x", 10, "b", 2, "y", 20, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandMultipleProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = 1
                      const b = 2
                      const c = 3
                      const obj = {a, b, c}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandPreservesInsertionOrder(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const z = 3
                      const a = 1
                      const m = 2
                      const obj = {z, a, m}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        // LinkedHashMap preserves insertion order
        var keys = result.keySet().toArray();
        assertEquals("z", keys[0]);
        assertEquals("a", keys[1]);
        assertEquals("m", keys[2]);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandSingleProperty(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x = 10
                      const obj = {x}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandWithArray(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const obj = {arr}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        var arr = (ArrayList<?>) result.get("arr");
        assertNotNull(arr);
        assertEquals(List.of(1, 2, 3), arr);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const num = 42
                      const str = "hello"
                      const bool = true
                      const obj = {num, str, bool}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("num", 42, "str", "hello", "bool", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShorthandWithNestedObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const nested = {inner: 42}
                      const obj = {nested}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("nested", Map.of("inner", 42)), result);
    }

}
