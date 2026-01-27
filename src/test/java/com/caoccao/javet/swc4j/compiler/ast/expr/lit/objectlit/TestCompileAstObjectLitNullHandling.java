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
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for null value handling in object literals.
 */
public class TestCompileAstObjectLitNullHandling extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullInNestedObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        outer: {
                          inner1: null,
                          inner2: 42
                        },
                        value: null
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of(
                "outer", SimpleMap.of("inner1", null, "inner2", 42),
                "value", null), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullOverridesValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const obj = {
                        ...base,
                        a: null
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithComputedKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "computed"
                      const obj = {
                        [key]: null,
                        regular: null,
                        value: 42
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("computed", null, "regular", null, "value", 42), result);
    }

    // Phase 5: Shorthand Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithShorthand(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = null
                      const b = 42
                      const obj = {
                        a,
                        b,
                        c: null
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 42, "c", null), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithSpread(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: null, b: 2}
                      const obj = {
                        ...base,
                        c: null,
                        d: 4
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 2, "c", null, "d", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralWithArrayValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        a: [1, 2, 3],
                        b: [4, 5],
                        c: []
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals(List.of(1, 2, 3), result.get("a"));
        assertEquals(List.of(4, 5), result.get("b"));
        assertEquals(List.of(), result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralWithNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        a: [[1, 2], [3, 4]],
                        b: [[5]],
                        c: []
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        var aValue = (ArrayList<?>) result.get("a");
        assertEquals(List.of(List.of(1, 2), List.of(3, 4)), aValue);
        var bValue = (ArrayList<?>) result.get("b");
        assertEquals(List.of(List.of(5)), bValue);
        assertEquals(List.of(), result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralWithNullValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        a: null,
                        b: 42,
                        c: null
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 42, "c", null), result);
    }

    // Phase 1: Basic Properties

}
