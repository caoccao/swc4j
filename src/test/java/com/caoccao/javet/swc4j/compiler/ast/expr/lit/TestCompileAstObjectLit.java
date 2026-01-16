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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for object literal compilation.
 * Phase 1: Basic key-value pairs with no type annotation
 * Phase 2: Computed property names
 * Phase 3: Property shorthand
 */
public class TestCompileAstObjectLit extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAllPrimitiveTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(4, result.size());
        assertEquals(42, result.get("intVal"));
        assertEquals(3.14, result.get("doubleVal"));
        assertEquals(false, result.get("boolVal"));
        assertEquals("text", result.get("strVal"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBooleanExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[true]: "yes", [false]: "no"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals("yes", result.get("true"));
        assertEquals("no", result.get("false"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedKeyOverwritesDuplicate(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, ["a"]: 2, [("a")]: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        assertEquals(3, result.get("a")); // Last value wins
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedKeyWithComplexValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "nested"
                      const obj = {[key]: {inner: 42}}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        var nested = (LinkedHashMap<?, ?>) result.get("nested");
        assertNotNull(nested);
        assertEquals(42, nested.get("inner"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedNumericExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[1 + 1]: "two", [5 * 2]: "ten"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals("two", result.get("2"));
        assertEquals("ten", result.get("10"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedNumericLiteral(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[42]: "answer", [0]: "zero"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals("answer", result.get("42"));
        assertEquals("zero", result.get("0"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedStringConcat(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {["key" + 1]: "value1", ["key" + 2]: "value2"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedStringLiteral(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {["computed"]: "value"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        assertEquals("value", result.get("computed"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedVariableReference(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "dynamic"
                      const obj = {[key]: "value"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        assertEquals("value", result.get("dynamic"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDuplicateKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2, a: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later value should win
        assertEquals(2, result.size());
        assertEquals(3, result.get("a"));
        assertEquals(2, result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    // Phase 2: Computed Property Names

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedComputedAndNormalKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const k = "comp"
                      const obj = {normal: 1, ["literal"]: 2, [k]: 3, [1+1]: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(4, result.size());
        assertEquals(1, result.get("normal"));
        assertEquals(2, result.get("literal"));
        assertEquals(3, result.get("comp"));
        assertEquals(4, result.get("2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedKeyTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {normal: 1, "string-literal": 2, 42: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals(1, result.get("normal"));
        assertEquals(2, result.get("string-literal"));
        assertEquals(3, result.get("42")); // Numeric key coerced to string
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjects(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.size());
        var outer = (LinkedHashMap<?, ?>) result.get("outer");
        assertNotNull(outer);
        var inner = (LinkedHashMap<?, ?>) outer.get("inner");
        assertNotNull(inner);
        assertEquals(42, inner.get("value"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: null, b: 2}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertNull(result.get("a"));
        assertEquals(2, result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {0: "zero", 1: "one", 42: "answer"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        // Numeric keys are coerced to strings (JavaScript behavior)
        assertEquals("zero", result.get("0"));
        assertEquals("one", result.get("1"));
        assertEquals("answer", result.get("42"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectWithArrayValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {arr: [1, 2, 3]}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        var arr = (java.util.ArrayList<?>) result.get("arr");
        assertNotNull(arr);
        assertEquals(java.util.List.of(1, 2, 3), arr);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReservedKeywords(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {class: "value", for: "loop", if: "condition"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals("value", result.get("class"));
        assertEquals("loop", result.get("for"));
        assertEquals("condition", result.get("if"));
    }

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
        assertEquals(3, result.size());
        assertEquals(10, result.get("x"));
        assertEquals(20, result.get("dynamic"));
        assertEquals(30, result.get("normal"));
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
        assertEquals(5, result.size());
        assertEquals(1, result.get("a"));
        assertEquals(10, result.get("x"));
        assertEquals(2, result.get("b"));
        assertEquals(20, result.get("y"));
        assertEquals(3, result.get("c"));
    }

    // Phase 3: Property Shorthand

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
        assertEquals(3, result.size());
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));
        assertEquals(3, result.get("c"));
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
        assertEquals(1, result.size());
        assertEquals(10, result.get("x"));
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
        var arr = (java.util.ArrayList<?>) result.get("arr");
        assertNotNull(arr);
        assertEquals(java.util.List.of(1, 2, 3), arr);
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
        assertEquals(3, result.size());
        assertEquals(42, result.get("num"));
        assertEquals("hello", result.get("str"));
        assertEquals(true, result.get("bool"));
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
        assertEquals(1, result.size());
        var nested = (LinkedHashMap<?, ?>) result.get("nested");
        assertNotNull(nested);
        assertEquals(42, nested.get("inner"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSimpleProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals(1, result.get("a"));
        assertEquals("hello", result.get("b"));
        assertEquals(true, result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringLiteralKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {"string-key": 1, "key with spaces": 2}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(1, result.get("string-key"));
        assertEquals(2, result.get("key with spaces"));
    }

}
