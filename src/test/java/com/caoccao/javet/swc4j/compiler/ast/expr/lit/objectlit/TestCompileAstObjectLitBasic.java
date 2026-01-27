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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for basic object literal creation with simple properties, keys, and values.
 */
public class TestCompileAstObjectLitBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAllPrimitiveTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        intVal: 42,
                        doubleVal: 3.14,
                        boolVal: false,
                        strVal: "text"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("intVal", 42, "doubleVal", 3.14, "boolVal", false, "strVal", "text"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDuplicateKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2, a: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later value should win
        assertEquals(Map.of("a", 3, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(), result);
    }

    // Phase 3: Computed Key Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedComputedAndNormalKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const k = "comp"
                      const obj = {normal: 1, ["literal"]: 2, [k]: 3, [1+1]: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("normal", 1, "literal", 2, "comp", 3, "2", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedKeyTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {normal: 1, "string-literal": 2, 42: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Numeric key coerced to string
        assertEquals(Map.of("normal", 1, "string-literal", 2, "42", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        outer: {
                          inner: {
                            value: 42
                          }
                        }
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("outer", Map.of("inner", Map.of("value", 42))), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: null, b: 2}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertNull(result.get("a"));
        assertEquals(2, result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {0: "zero", 1: "one", 42: "answer"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Numeric keys are coerced to strings (JavaScript behavior)
        assertEquals(Map.of("0", "zero", "1", "one", "42", "answer"), result);
    }

    // Phase 4: Spread Operator

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralArraysWithComputedKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "data"
                      const obj = {
                        [key]: [1, 2, 3],
                        ["key" + 2]: [4, 5]
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(List.of(1, 2, 3), result.get("data"));
        assertEquals(List.of(4, 5), result.get("key2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralArraysWithShorthand(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = [1, 2, 3]
                      const b = [4, 5]
                      const obj = {
                        a,
                        b,
                        c: [6, 7]
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
        assertEquals(List.of(6, 7), result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralArraysWithSpread(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {
                        a: [1, 2]
                      }
                      const obj = {
                        ...base,
                        b: [3, 4]
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(List.of(1, 2), result.get("a"));
        assertEquals(List.of(3, 4), result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralMixedArraysAndPrimitives(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        num: 42,
                        str: "hello",
                        arr: [1, 2, 3],
                        nested: {inner: [4, 5]},
                        bool: true
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(5, result.size());
        assertEquals(42, result.get("num"));
        assertEquals("hello", result.get("str"));
        assertEquals(List.of(1, 2, 3), result.get("arr"));
        assertEquals(Map.of("inner", List.of(4, 5)), result.get("nested"));
        assertEquals(true, result.get("bool"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectWithArrayValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {arr: [1, 2, 3]}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        var arr = (ArrayList<?>) result.get("arr");
        assertNotNull(arr);
        assertEquals(List.of(1, 2, 3), arr);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReservedKeywords(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {class: "value", for: "loop", if: "condition"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("class", "value", "for", "loop", "if", "condition"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSimpleProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringLiteralKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {"string-key": 1, "key with spaces": 2}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("string-key", 1, "key with spaces", 2), result);
    }

}
