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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for spread operator usage in object literals.
 */
public class TestCompileAstObjectLitSpread extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSpreads(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1 = {a: 1, b: 2}
                      const obj2 = {c: 3, d: 4}
                      const obj3 = {e: 5}
                      const merged = {...obj1, ...obj2, ...obj3}
                      return merged
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSpreadsWithOverlap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1 = {a: 1, b: 2}
                      const obj2 = {b: 20, c: 3}
                      const obj3 = {c: 30, d: 4}
                      const merged = {...obj1, ...obj2, ...obj3}
                      return merged
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // obj2.b overwrites obj1.b, obj3.c overwrites obj2.c
        assertEquals(Map.of("a", 1, "b", 20, "c", 30, "d", 4), result);
    }

    // Phase 2.2: Record Type Validation (Continued)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadNestedObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const inner = {x: 1}
                      const base = {nested: inner, a: 2}
                      const obj = {...base, b: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("nested", Map.of("x", 1), "a", 2, "b", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadOverwritesPreviousProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 10, b: 20}
                      const obj = {a: 1, ...base, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // base.a overwrites initial a
        assertEquals(Map.of("a", 10, "b", 20, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadOverwrittenByLaterProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 10, b: 20}
                      const obj = {...base, a: 1, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later a overwrites base.a
        assertEquals(Map.of("a", 1, "b", 20, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadSingleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const obj = {...base}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithAdditionalProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const obj = {c: 3, ...base, d: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithComputedKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const key = "dynamic"
                      const obj = {[key]: 100, ...base, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("dynamic", 100, "a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithShorthand(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const x = 10
                      const obj = {x, ...base, y: 20}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10, "a", 1, "b", 2, "y", 20), result);
    }

}
