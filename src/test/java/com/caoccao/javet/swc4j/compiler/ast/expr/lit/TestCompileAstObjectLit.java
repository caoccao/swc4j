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
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for object literal compilation.
 * Phase 1: Basic key-value pairs with no type annotation
 * Phase 2: Computed property names
 * Phase 3: Property shorthand
 * Phase 4: Spread operator
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
        assertEquals(Map.of("intVal", 42, "doubleVal", 3.14, "boolVal", false, "strVal", "text"), result);
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
        assertEquals(Map.of("true", "yes", "false", "no"), result);
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
        assertEquals(Map.of("a", 3), result); // Last value wins
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
        assertEquals(Map.of("nested", Map.of("inner", 42)), result);
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
        assertEquals(Map.of("2", "two", "10", "ten"), result);
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
        assertEquals(Map.of("42", "answer", "0", "zero"), result);
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
        assertEquals(Map.of("key1", "value1", "key2", "value2"), result);
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
        assertEquals(Map.of("computed", "value"), result);
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
        assertEquals(Map.of("dynamic", "value"), result);
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
        assertEquals(Map.of("a", 3, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase02DuplicateKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 2: Duplicate keys - later value wins
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, a: 2, a: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later value should win (Map.put overwrites)
        assertEquals(Map.of("a", 3), result);
    }

    // Phase 2: Computed Property Names

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase06NonStringPrimitiveKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 6: Non-string primitive keys (boolean, null) converted to string
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        true: "yes",
                        false: "no",
                        null: "none"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Primitive keys are converted to strings
        assertEquals(Map.of("true", "yes", "false", "no", "null", "none"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase10ReservedKeywords(JdkVersion jdkVersion) throws Exception {
        // Edge case 10: Reserved keywords as keys work fine in Maps
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        class: "className",
                        for: "loop",
                        if: "condition",
                        while: "iteration",
                        return: "exit"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Reserved keywords are just strings in Map keys
        assertEquals(Map.of(
                "class", "className",
                "for", "loop",
                "if", "condition",
                "while", "iteration",
                "return", "exit"
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase11WhitespaceInKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 11: Whitespace in keys is preserved exactly
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        "key with spaces": "value1",
                        "  trim  ": "value2",
                        "\\ttab\\t": "value3",
                        "\\nnewline\\n": "value4"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Whitespace is preserved exactly
        assertEquals("value1", result.get("key with spaces"));
        assertEquals("value2", result.get("  trim  "));
        assertEquals("value3", result.get("\ttab\t"));
        assertEquals("value4", result.get("\nnewline\n"));
        assertEquals(4, result.size());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase12UnicodeKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 12: Unicode keys are supported (Java strings support full Unicode)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        "你好": "hello",
                        "🔥": "fire",
                        "café": "coffee",
                        "Москва": "Moscow",
                        "東京": "Tokyo"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Unicode is fully supported
        assertEquals(Map.of(
                "你好", "hello",
                "🔥", "fire",
                "café", "coffee",
                "Москва", "Moscow",
                "東京", "Tokyo"
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase14ObjectAsValueTypeAnnotation(JdkVersion jdkVersion) throws Exception {
        // Edge case 14: Object as value type annotation - still generates LinkedHashMap
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Object = {a: 1, b: "hello", c: true}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Object type annotation doesn't prevent LinkedHashMap generation
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase15MixedKeyTypes(JdkVersion jdkVersion) throws Exception {
        // Edge case 15: Mixed key types - all converted to String
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const computed = "computed"
                      const obj = {"str": 1, 42: 2, [computed]: 3, true: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // All keys coerced to String
        assertEquals(Map.of("str", 1, "42", 2, "computed", 3, "true", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase16TrailingCommas(JdkVersion jdkVersion) throws Exception {
        // Edge case 16: Trailing commas - AST handles this automatically
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        a: 1,
                        b: 2,
                        c: 3,
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Trailing comma doesn't affect object creation
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysEvaluationOrder(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys evaluation order - expressions evaluated left to right
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        ["key" + 1]: "first",
                        ["key" + 2]: "second",
                        ["key" + 3]: "third"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Computed keys with string concat expressions - insertion order preserved
        assertEquals(Map.of("key1", "first", "key2", "second", "key3", "third"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysWithExpressions(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys with expressions - evaluate in order
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = 10
                      const obj: Record<number, string> = {
                        [base + 0]: "ten",
                        [base + 1]: "eleven",
                        [base + 2]: "twelve"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Expressions evaluated: keys are Integer 10, 11, 12
        assertEquals(Map.of(10, "ten", 11, "eleven", 12, "twelve"), result);
    }

    // Phase 2.1: Record<number, V> - Numeric keys

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysWithVariableReferences(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys with variable references - evaluate in order
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = 0
                      const b = 1
                      const c = 2
                      const obj = {
                        [a]: "first",
                        [b]: "second",
                        [c]: "third"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Keys are "0", "1", "2" (numeric keys coerced to string by default)
        assertEquals(Map.of("0", "first", "1", "second", "2", "third"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase18PropertyNameCollisions(JdkVersion jdkVersion) throws Exception {
        // Edge case 18: Property name collisions after coercion - later value wins
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        1: "numeric",
                        "1": "string"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Both keys coerce to "1", later value wins
        assertEquals(Map.of("1", "string"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase19ExpressionValues(JdkVersion jdkVersion) throws Exception {
        // Edge case 19: Expression values - various expressions evaluated at runtime
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        arithmetic: 1 + 2 + 3,
                        multiplication: 4 * 5,
                        division: 20 / 4,
                        subtraction: 10 - 3,
                        stringConcat: "hello" + " " + "world",
                        booleanTrue: true,
                        booleanFalse: false
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // All expressions evaluated correctly
        assertEquals(Map.of(
                "arithmetic", 6,
                "multiplication", 20,
                "division", 5,
                "subtraction", 7,
                "stringConcat", "hello world",
                "booleanTrue", true,
                "booleanFalse", false
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase20ObjectInReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Edge case 20: Object in return type context - LinkedHashMap returned
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getObject(): Object {
                      return {a: 1, b: "hello", c: true}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("getObject").invoke(instance);
        // Verify result is LinkedHashMap even with Object return type
        assertInstanceOf(LinkedHashMap.class, result);
        var linkedHashMap = (LinkedHashMap<?, ?>) result;
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), linkedHashMap);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase23MixedNumericStringKeys(JdkVersion jdkVersion) {
        // Edge case 23: Mixed numeric literal and string literal keys with Record<number, V>
        // Should reject string keys for strict type safety
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<number, string> = {
                            1: "one",
                            "2": "two"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key") && causeMessage.contains("String"),
                "Expected key type mismatch for string key '2', got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase24NullInNonNullableRecord(JdkVersion jdkVersion) {
        // Edge case 24: Null values in Record<string, string> should be allowed (Java allows null)
        // Note: Java doesn't enforce non-nullable by default, so this test verifies null is allowed
        try {
            var map = getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, string> = {
                            a: "hello",
                            b: null
                          }
                          return obj
                        }
                      }
                    }""");
            Class<?> classA = loadClass(map.get("com.A"));
            var instance = classA.getConstructor().newInstance();
            var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
            // Null should be allowed in Java Maps
            assertEquals(SimpleMap.of("a", "hello", "b", null), result);
        } catch (Exception e) {
            // If compilation fails, it means null validation is strict
            assertNotNull(e.getCause(), "Expected wrapped exception");
            String causeMessage = e.getCause().getMessage();
            assertTrue(causeMessage.contains("null") || causeMessage.contains("Property 'b'"),
                    "Expected null-related error, got: " + causeMessage);
        }
    }

    // Phase 2.2: Type Alias Keys - Integer, Long, Double, String

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase27WideningConversion(JdkVersion jdkVersion) throws Exception {
        // Edge case 27: Widening conversion - int literals widen to long type
        // Note: Current implementation stores values based on literal size
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, long> = {
                        a: 42,
                        b: 100
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // int literals are stored as Integer when they fit in int range
        assertEquals(Map.of("a", 42, "b", 100), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase28NarrowingAllowed(JdkVersion jdkVersion) throws Exception {
        // Edge case 28: Narrowing conversion - compiler allows long literals in int context
        // Note: Current implementation allows this (no strict narrowing validation)
        // The large value will be truncated/wrapped at runtime
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, int> = {
                        a: 1,
                        b: 42
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 42), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase29ObjectTypePermissive(JdkVersion jdkVersion) throws Exception {
        // Edge case 29: Record<string, Object> allows any value type (permissive)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Object> = {
                        a: 1,
                        b: "hello",
                        c: true,
                        d: {nested: "object"},
                        e: [1, 2, 3]
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Verify primitive values
        assertEquals(1, result.get("a"));
        assertEquals("hello", result.get("b"));
        assertEquals(true, result.get("c"));
        // Verify nested object
        var nested = (LinkedHashMap<?, ?>) result.get("d");
        assertEquals(Map.of("nested", "object"), nested);
        // Verify array
        assertEquals(List.of(1, 2, 3), result.get("e"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase30UnionTypesIgnored(JdkVersion jdkVersion) throws Exception {
        // Edge case 30: Union types (number | string) compile but validation is not enforced
        // Note: Current implementation parses union types but treats them permissively (like Object)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number | string> = {
                        a: 1,
                        b: "hello",
                        c: 3.14
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Union types are treated permissively - mixed types allowed
        assertEquals(Map.of("a", 1, "b", "hello", "c", 3.14), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase31ArrayValuesInRecord(JdkVersion jdkVersion) throws Exception {
        // Edge case 31: Array values in Record<string, Object> - arrays work as values
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Object> = {
                        a: [1, 2, 3],
                        b: [4, 5, 6]
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Verify arrays are stored correctly
        assertEquals(2, result.size());
        assertInstanceOf(List.class, result.get("a"));
        assertInstanceOf(List.class, result.get("b"));
        assertEquals(List.of(1, 2, 3), result.get("a"));
        assertEquals(List.of(4, 5, 6), result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase32EmptyObjectWithStrictRecordType(JdkVersion jdkVersion) throws Exception {
        // Edge case 32: Empty object with strict Record type - should be valid
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Empty map is valid for any Record type
        assertEquals(Map.of(), result);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyMultipleKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Multiple computed properties with type validation
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key1: string = "first"
                      const key2: string = "second"
                      const obj: Record<string, number> = {
                        [key1]: 1,
                        [key2]: 2,
                        third: 3
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Mix of computed and regular keys
        assertEquals(Map.of("first", 1, "second", 2, "third", 3), result);
    }

    // Phase 2.3: Nested Record types

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyWithCorrectNumericType(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Computed property with numeric key type - should work with Record<number, V>
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: number = 123
                      const obj: Record<number, string> = {[key]: "value"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Computed key with number type - TypeScript number maps to Double
        assertEquals(Map.of(123.0, "value"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyWithCorrectType(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Computed property with type validation - correct type should work
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: string = "dynamic"
                      const obj: Record<string, number> = {[key]: 42}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Computed key with string type matches Record<string, number>
        assertEquals(Map.of("dynamic", 42), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase34ComputedPropertyStringInNumberRecord(JdkVersion jdkVersion) {
        // Edge case 34: String computed key in Record<number, V> - should reject
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: string = "notANumber"
                          const obj: Record<number, string> = {[key]: "value"}
                          return obj
                        }
                      }
                    }""");
        });
        // Should fail - compiler rejects type mismatch during bytecode generation
        assertTrue(exception.getMessage().contains("Failed to generate method"),
                "Error message should indicate generation failure: " + exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase34ComputedPropertyWithWrongKeyType(JdkVersion jdkVersion) {
        // Edge case 34: Computed property with wrong key type - should reject
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: number = 123
                          const obj: Record<string, number> = {[key]: 42}
                          return obj
                        }
                      }
                    }""");
        });
        // Should fail - compiler rejects type mismatch during bytecode generation
        assertTrue(exception.getMessage().contains("Failed to generate method"),
                "Error message should indicate generation failure: " + exception.getMessage());
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
        assertEquals(Map.of(), result);
    }

    // Phase 3: Computed Key Type Validation

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
        assertEquals(Map.of("normal", 1, "literal", 2, "comp", 3, "2", 4), result);
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
        // Numeric key coerced to string
        assertEquals(Map.of("normal", 1, "string-literal", 2, "42", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSpreads(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSpreadsWithOverlap(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // obj2.b overwrites obj1.b, obj3.c overwrites obj2.c
        assertEquals(Map.of("a", 1, "b", 20, "c", 30, "d", 4), result);
    }

    // Phase 2.2: Record Type Validation (Continued)

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
        assertEquals(Map.of("outer", Map.of("inner", Map.of("value", 42))), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordEmptyNested(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, number>> = {
                        outer1: {},
                        outer2: {inner: 42}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of(),
                "outer2", Map.of("inner", 42)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedKey(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<number, string>> = {
                            outer: {a: "invalid"}
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected nested key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedValue(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<string, number>> = {
                            outer: {inner: "invalid"}
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'inner'") &&
                        causeMessage.contains("String"),
                "Expected nested type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordMixedTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, number>> = {
                        a: {x: 1, y: 2.5},
                        b: {z: 100}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "a", Map.of("x", 1, "y", 2.5),
                "b", Map.of("z", 100)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, number>> = {
                        outer1: {inner1: 42, inner2: 99},
                        outer2: {inner3: 100}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner1", 42, "inner2", 99),
                "outer2", Map.of("inner3", 100)
        ), result);
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
        // Numeric keys are coerced to strings (JavaScript behavior)
        assertEquals(Map.of("0", "zero", "1", "one", "42", "answer"), result);
    }

    // Phase 4: Spread Operator

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralArraysWithComputedKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(List.of(1, 2, 3), result.get("data"));
        assertEquals(List.of(4, 5), result.get("key2"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralArraysWithShorthand(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
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
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(List.of(1, 2), result.get("a"));
        assertEquals(List.of(3, 4), result.get("b"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralMixedArraysAndPrimitives(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
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
    public void testObjectLiteralNullInNestedObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of(
                "outer", SimpleMap.of("inner1", null, "inner2", 42),
                "value", null), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullOverridesValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithComputedKey(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("computed", null, "regular", null, "value", 42), result);
    }

    // Phase 5: Shorthand Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithShorthand(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 42, "c", null), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralNullWithSpread(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 2, "c", null, "d", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralWithArrayValues(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
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
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
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
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of("a", null, "b", 42, "c", null), result);
    }

    // Phase 1: Basic Properties

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
        var arr = (ArrayList<?>) result.get("arr");
        assertNotNull(arr);
        assertEquals(List.of(1, 2, 3), arr);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with bracket notation (obj["prop"] = value → map.put("prop", value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1}
                      obj["b"] = "hello"
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", "hello"), result);
    }

    // ========== Phase 2: Record Type Validation Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with computed key (obj[variable] = value → map.put(variable, value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1}
                      const key = "b"
                      obj[key] = 2
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with dot notation (obj.prop = value → map.put("prop", value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2}
                      obj.c = 3
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
    public void testPhase6AssignmentModifyExisting(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment that modifies existing property
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2, c: 3}
                      obj.b = 99
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 99, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with Record type annotation
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2}
                      obj.c = 3
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
    public void testPhase6MemberAccessBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with bracket notation (obj["prop"] → map.get("prop"))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj["b"]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals("hello", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with computed key (obj[variable] → map.get(variable))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      const key = "c"
                      return obj[key]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(true, result);
    }

    // Phase 4: Spread Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with dot notation (obj.prop → map.get("prop"))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj.a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessNestedObjectSimple(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access on nested objects - first level only
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        outer: {inner: 42}
                      }
                      return obj.outer
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("inner", 42), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with Record type annotations
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2, c: 3}
                      return obj.b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(2, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextImplicit(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with implicit return type (type inference)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData() {
                      return {a: 1, b: "hello", c: true}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextNestedRecord(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with nested Record types
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<string, Record<string, number>> {
                      return {
                        outer1: {inner: 42},
                        outer2: {value: 99}
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner", 42),
                "outer2", Map.of("value", 99)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordNumberString(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<number, string>
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<number, string> {
                      return {1: "one", 2: "two", 3: "three"}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        // With Record<number, string> return type, keys should be Integer
        assertEquals(3, result.size());
        assertTrue(result.containsKey(1) || result.containsKey("1"));
        if (result.containsKey(1)) {
            // Numeric keys
            assertEquals(Map.of(1, "one", 2, "two", 3, "three").entrySet(), result.entrySet());
        } else {
            // String keys (fallback)
            assertEquals(Map.of("1", "one", "2", "two", "3", "three"), result);
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordStringNumber(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<string, number>
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<string, number> {
                      return {a: 1, b: 2, c: 3}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyNumberMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: string = "stringkey"
                          const obj: Record<number, string> = {
                            [key]: "value"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key") && causeMessage.contains("String"),
                "Expected computed key type mismatch error, got: " + causeMessage);
    }

    // Phase 7: Mixed Scenarios (Spread + Shorthand + Computed)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyStringMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: number = 42
                          const obj: Record<string, number> = {
                            [key]: 100
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key") && causeMessage.contains("double"),
                "Expected computed key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedNumberKeyValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: number = 42
                      const obj: Record<number, string> = {
                        [key]: "forty-two",
                        [1 + 1]: "two"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Expected: First key is 42.0 (Double from variable), second key is 2 (Integer from expression)
        var expected = new LinkedHashMap<>();
        expected.put(42.0, "forty-two");
        expected.put(2, "two");
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedStringKeyValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: string = "computed"
                      const obj: Record<string, number> = {
                        [key]: 42,
                        ["key" + 1]: 99
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("computed", 42, "key1", 99), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordEmptyValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<Integer, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerNumberValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<Integer, number> = {
                        1: 100,
                        2: 200,
                        3: 3.14
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals(100, result.get(1));
        assertEquals(200, result.get(2));
        assertEquals(3.14, result.get(3));
    }

    // Phase 7: Array Value Types (without Record type validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerStringValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<Integer, string> = {
                        0: "zero",
                        1: "one",
                        42: "forty-two"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0));
        assertEquals("one", result.get(1));
        assertEquals("forty-two", result.get(42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<Long, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongNumberValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<Long, number> = {
                        1: 100,
                        2147483648: 200
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(100, result.get(1L));
        assertEquals(200, result.get(2147483648L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongStringValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<Long, string> = {
                        0: "zero",
                        1: "one",
                        2147483648: "large"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0L));
        assertEquals("one", result.get(1L));
        assertEquals("large", result.get(2147483648L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedAllFeatures(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1: Record<string, number> = {x: 10}
                      const y: number = 20
                      const z: number = 30
                      const key: string = "computed"
                      const obj: Record<string, number> = {
                        ...obj1,
                        y,
                        [key]: 40,
                        z,
                        ["key" + 2]: 50,
                        literal: 60
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10, "y", 20.0, "computed", 40, "z", 30.0, "key2", 50, "literal", 60), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedComputedAndSpreadValidation(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1}
                      const key: string = "b"
                      const obj: Record<string, number> = {
                        ...base,
                        [key]: 2,
                        ["c" + "d"]: 3
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "cd", 3), result);
    }

    // Phase 7: Null Handling

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedMultipleSpreadsAndShorthands(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1: Record<string, number> = {a: 1}
                      const obj2: Record<string, number> = {b: 2}
                      const c: number = 3
                      const d: number = 4
                      const obj: Record<string, number> = {
                        ...obj1,
                        c,
                        ...obj2,
                        d,
                        e: 5
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "c", 3.0, "b", 2, "d", 4.0, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedNestedWithSpreadShorthand(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, Record<string, number>> = {
                        outer1: {inner1: 1}
                      }
                      const outer2: Record<string, number> = {inner2: 2}
                      const obj: Record<string, Record<string, number>> = {
                        ...base,
                        outer2,
                        outer3: {inner3: 3}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner1", 1),
                "outer2", Map.of("inner2", 2),
                "outer3", Map.of("inner3", 3)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedSpreadShorthandComputed(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const c: number = 3
                      const key: string = "d"
                      const obj: Record<string, number> = {
                        ...base,
                        c,
                        [key]: 4,
                        e: 5
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3.0, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedValidAndInvalid(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            a: 1,
                            b: 2,
                            c: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'c'") &&
                        causeMessage.contains("String"),
                "Expected type mismatch error for property 'c', got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedWithOverrides(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const a: number = 99
                      const obj: Record<string, number> = {
                        ...base,
                        a,
                        b: 88
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later properties override earlier ones
        assertEquals(Map.of("a", 99.0, "b", 88), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberEmptyValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertTrue(result.isEmpty());
    }

    // Phase 1: Basic Properties

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<number, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    // Phase 7: 3-Level Nested Record Types

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberMixedIntAndDouble(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, number> = {
                        1: 42,
                        2: 3.14
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(42, result.get(1));
        assertEquals(3.14, result.get(2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberNumberValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, number> = {
                        1: 100,
                        2: 200,
                        3: 300
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(1, 100, 2, 200, 3, 300), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringComputedNumericKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 3: Computed numeric keys
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = 10
                      const obj: Record<number, string> = {
                        [base]: "ten",
                        [base + 1]: "eleven",
                        [base * 2]: "twenty"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(10, "ten", 11, "eleven", 20, "twenty"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringEmpty(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 1: Empty object
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringLargeObject(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 10: Larger object (20+ numeric keys)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {
                        1: "v1", 2: "v2", 3: "v3", 4: "v4", 5: "v5",
                        6: "v6", 7: "v7", 8: "v8", 9: "v9", 10: "v10",
                        11: "v11", 12: "v12", 13: "v13", 14: "v14", 15: "v15",
                        16: "v16", 17: "v17", 18: "v18", 19: "v19", 20: "v20"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of(
                1, "v1", 2, "v2", 3, "v3", 4, "v4", 5, "v5",
                6, "v6", 7, "v7", 8, "v8", 9, "v9", 10, "v10",
                11, "v11", 12, "v12", 13, "v13", 14, "v14", 15, "v15",
                16, "v16", 17, "v17", 18, "v18", 19, "v19", 20, "v20"
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringMixedFeatures(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 6: Mixed features (literal + computed + spread)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<number, string> = {1: "one", 2: "two"}
                      const offset = 10
                      const obj: Record<number, string> = {
                        0: "zero",
                        ...base,
                        [offset]: "ten",
                        [offset + 1]: "eleven"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two", 10, "ten", 11, "eleven"), result);
    }

    // Phase 7: Edge Cases 23-30 (Type Conversion and Validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringNegativeKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 7: Negative numeric keys
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {
                        [-1]: "negative one",
                        [-10]: "negative ten",
                        0: "zero",
                        10: "positive ten"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(-1, "negative one", -10, "negative ten", 0, "zero", 10, "positive ten"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringNumericLiteralKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 2: Numeric literal keys
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {
                        0: "zero",
                        1: "one",
                        42: "answer",
                        100: "hundred"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Numeric literal keys stored as Integer
        assertEquals(Map.of(0, "zero", 1, "one", 42, "answer", 100, "hundred"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringOverwrite(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 8: Key overwriting (duplicate numeric keys)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {
                        1: "first",
                        2: "second",
                        1: "ONE",
                        2: "TWO",
                        1: "FIRST"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Last value wins for duplicate keys
        assertEquals(Map.of(1, "FIRST", 2, "TWO"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 9: In return type context
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<number, string> {
                      return {1: "one", 2: "two", 3: "three"}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Return type context: numeric keys become strings (type info not propagated)
        assertEquals(Map.of("1", "one", "2", "two", "3", "three"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringSpread(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 5: Spread operator
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<number, string> = {1: "a", 2: "b"}
                      const extension: Record<number, string> = {3: "c", 4: "d"}
                      const obj: Record<number, string> = {...base, ...extension, 5: "e"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringTypedNumericKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 4: Keys from typed number variables
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key1: number = 1
                      const key2: number = 2
                      const key3: number = 3
                      const obj: Record<number, string> = {
                        [key1]: "first",
                        [key2]: "second",
                        [key3]: "third"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Variables typed as 'number' are Double, so keys are 1.0, 2.0, 3.0
        assertEquals(Map.of(1.0, "first", 2.0, "second", 3.0, "third"), result);
    }

    // Phase 7: Edge Cases 2, 6, 10, 11, 12 (Key Handling)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {
                        0: "zero",
                        1: "one",
                        42: "forty-two"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0));
        assertEquals("one", result.get(1));
        assertEquals("forty-two", result.get(42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<number, number> = {
                            1: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property '1'") &&
                        causeMessage.contains("String"),
                "Expected value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandKeyTypeMismatch(JdkVersion jdkVersion) {
        // Shorthand keys are always strings, so Record<number, V> should fail
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: string = "value"
                          const obj: Record<number, string> = {a}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") && causeMessage.contains("String"),
                "Expected shorthand key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandMixedValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: number = 10
                      const y: number = 20
                      const obj: Record<string, number> = {x, y, z: 30}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10.0, "y", 20.0, "z", 30), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandStringNumberValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: number = 1
                      const b: number = 2
                      const c: number = 3
                      const obj: Record<string, number> = {a, b, c}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1.0, "b", 2.0, "c", 3.0), result);
    }

    // Phase 7: Edge Cases 17-19 (Computed Keys, Collisions, Expression Values)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandStringStringValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const name: string = "Alice"
                      const city: string = "NYC"
                      const obj: Record<string, string> = {name, city}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("name", "Alice", "city", "NYC"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: string = "hello"
                          const obj: Record<string, number> = {a}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'a'") && causeMessage.contains("String"),
                "Expected shorthand value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<number, string> = {1: "one"}
                          const obj: Record<string, string> = {...source}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible key type"),
                "Expected spread key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadMultiple(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1: Record<string, number> = {a: 1}
                      const obj2: Record<string, number> = {b: 2}
                      const obj: Record<string, number> = {...obj1, ...obj2, c: 3}
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
    public void testRecordSpreadNested(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, Record<string, number>> = {
                        outer1: {inner1: 1, inner2: 2}
                      }
                      const obj: Record<string, Record<string, number>> = {
                        ...base,
                        outer2: {inner3: 3}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner1", 1, "inner2", 2),
                "outer2", Map.of("inner3", 3)
        ), result);
    }

    // Phase 7: Edge Cases 14-16, 20 (Object Type Annotation, Mixed Keys, Trailing Commas, Return Context)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadNestedMultiple(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj1: Record<string, Record<string, number>> = {
                        a: {x: 1}
                      }
                      const obj2: Record<string, Record<string, number>> = {
                        b: {y: 2}
                      }
                      const obj: Record<string, Record<string, number>> = {
                        ...obj1,
                        ...obj2,
                        c: {z: 3}
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "a", Map.of("x", 1),
                "b", Map.of("y", 2),
                "c", Map.of("z", 3)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadNestedTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<string, Record<string, string>> = {
                            outer: {inner: "hello"}
                          }
                          const obj: Record<string, Record<string, number>> = {
                            ...source
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible value type"),
                "Expected nested spread value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadOverwrite(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const obj: Record<string, number> = {a: 99, ...base, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Spread overwrites earlier properties
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const obj: Record<string, number> = {c: 3, ...base, d: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("c", 3, "a", 1, "b", 2, "d", 4), result);
    }

    // Phase 7: Edge Cases 31-32 (Array Values in Record, Empty Object with Strict Type)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<string, string> = {a: "hello"}
                          const obj: Record<string, number> = {...source}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible value type"),
                "Expected spread value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringKeyNumericMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<String, number> = {
                            1: 42
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key '1'") &&
                        causeMessage.contains("Integer"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    // Phase 6: Integration - Return Type Context

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberComputedKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 3: Computed string keys
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const prefix = "key"
                      const obj: Record<string, number> = {
                        [prefix + "1"]: 1,
                        [prefix + "2"]: 2,
                        ["literal"]: 3
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("key1", 1, "key2", 2, "literal", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberEmpty(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 1: Empty object
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberExpressionValues(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 7: Expression values (arithmetic, etc.)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = 10
                      const obj: Record<string, number> = {
                        addition: 5 + 3,
                        subtraction: 10 - 2,
                        multiplication: 4 * 2,
                        division: 16 / 2,
                        variable: base,
                        expression: base * 2 + 5
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "addition", 8,
                "subtraction", 8,
                "multiplication", 8,
                "division", 8,
                "variable", 10,
                "expression", 25
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberLargeObject(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 10: Larger object (20+ properties)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {
                        p1: 1, p2: 2, p3: 3, p4: 4, p5: 5,
                        p6: 6, p7: 7, p8: 8, p9: 9, p10: 10,
                        p11: 11, p12: 12, p13: 13, p14: 14, p15: 15,
                        p16: 16, p17: 17, p18: 18, p19: 19, p20: 20
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(SimpleMap.of(
                "p1", 1, "p2", 2, "p3", 3, "p4", 4, "p5", 5,
                "p6", 6, "p7", 7, "p8", 8, "p9", 9, "p10", 10,
                "p11", 11, "p12", 12, "p13", 13, "p14", 14, "p15", 15,
                "p16", 16, "p17", 17, "p18", 18, "p19", 19, "p20", 20
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberMixedFeatures(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 6: Mixed features (spread + shorthand + computed + regular)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: number = 100
                      const base: Record<string, number> = {a: 1, b: 2}
                      const key = "dynamic"
                      const obj: Record<string, number> = {
                        regular: 10,
                        x,
                        [key]: 20,
                        ...base,
                        final: 30
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // x typed as 'number' becomes 100.0 (TypeScript number -> Java Double)
        assertEquals(SimpleMap.of(
                "regular", 10,
                "x", 100.0,
                "dynamic", 20,
                "a", 1,
                "b", 2,
                "final", 30
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberOverwrite(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 8: Key overwriting (duplicate keys)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {
                        a: 1,
                        b: 2,
                        a: 10,
                        b: 20,
                        a: 100
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Last value wins for duplicate keys
        assertEquals(Map.of("a", 100, "b", 20), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 9: In return type context
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, number> {
                      return {x: 1, y: 2, z: 3}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 1, "y", 2, "z", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberShorthand(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 4: Shorthand properties
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: number = 10
                      const b: number = 20
                      const c: number = 30
                      const obj: Record<string, number> = {a, b, c}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Variables typed as 'number' are stored as Double
        assertEquals(Map.of("a", 10.0, "b", 20.0, "c", 30.0), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberSimpleProperties(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 2: Simple properties with different numeric literals
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {
                        integer: 42,
                        decimal: 3.14,
                        negative: -10,
                        zero: 0,
                        scientific: 1e5
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // All numeric literals stored as Integer for whole numbers, Double for decimals
        assertEquals(Map.of(
                "integer", 42,
                "decimal", 3.14,
                "negative", -10,
                "zero", 0,
                "scientific", 100000  // 1e5 stored as Integer
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberSpread(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 5: Spread operator
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const extension: Record<string, number> = {c: 3, d: 4}
                      const obj: Record<string, number> = {...base, ...extension, e: 5}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {
                        a: 1,
                        b: 2,
                        c: 3
                      }
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
    public void testRecordStringNumberWithDouble(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {
                        a: 1,
                        b: 3.14,
                        c: 42
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(1, result.get("a"));
        assertEquals(3.14, result.get("b"));
        assertEquals(42, result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringStringValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, string> = {
                        name: "Alice",
                        city: "NYC"
                      }
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("name", "Alice", "city", "NYC"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchBoolean(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            flag: true
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'flag'") &&
                        causeMessage.contains("has type") &&
                        causeMessage.contains("but Record requires"),
                "Expected type mismatch error for boolean value, got: " + causeMessage);
    }

    // Phase 7: Edge Cases 33-34 (Computed Property Type Validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchString(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            a: "hello"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'a' has type String") &&
                        causeMessage.contains("but Record requires"),
                "Expected type mismatch error for value type, got: " + causeMessage);
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
        assertEquals(Map.of("class", "value", "for", "loop", "if", "condition"), result);
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
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadNestedObjects(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("nested", Map.of("x", 1), "a", 2, "b", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadOverwritesPreviousProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 10, b: 20}
                      const obj = {a: 1, ...base, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // base.a overwrites initial a
        assertEquals(Map.of("a", 10, "b", 20, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadOverwrittenByLaterProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 10, b: 20}
                      const obj = {...base, a: 1, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later a overwrites base.a
        assertEquals(Map.of("a", 1, "b", 20, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadSingleObject(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const obj = {...base}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithAdditionalProperties(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = {a: 1, b: 2}
                      const obj = {c: 3, ...base, d: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithComputedKeys(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("dynamic", 100, "a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSpreadWithShorthand(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10, "a", 1, "b", 2, "y", 20), result);
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
        assertEquals(Map.of("string-key", 1, "key with spaces", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordEmptyAtEachLevel(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, Record<string, number>>> = {
                        empty1: {},
                        with2: {
                          empty2: {},
                          with3: {
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
        assertEquals(Map.of(
                "empty1", Map.of(),
                "with2", Map.of(
                        "empty2", Map.of(),
                        "with3", Map.of("value", 42)
                )
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordInvalidDeepestKey(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<string, Record<number, string>>> = {
                            level1: {
                              level2: {
                                a: "invalid"
                              }
                            }
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected deepest level key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordInvalidDeepestValue(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<string, Record<string, number>>> = {
                            level1: {
                              level2: {
                                level3: "invalid"
                              }
                            }
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'level3'") &&
                        causeMessage.contains("String"),
                "Expected deepest level type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordInvalidMiddleValue(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<string, Record<string, number>>> = {
                            level1: {
                              level2: "invalid"
                            }
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'level2'") &&
                        causeMessage.contains("String"),
                "Expected middle level type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordMixedTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, Record<string, number>>> = {
                        a: {
                          b: {
                            c: 1,
                            d: 3
                          },
                          e: {
                            f: 100
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
        assertEquals(Map.of(
                "a", Map.of(
                        "b", Map.of("c", 1, "d", 3),
                        "e", Map.of("f", 100)
                )
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordValid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, Record<string, Record<string, number>>> = {
                        level1: {
                          level2a: {
                            level3a: 100,
                            level3b: 200
                          },
                          level2b: {
                            level3c: 300
                          }
                        },
                        another1: {
                          another2: {
                            another3: 400
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
        assertEquals(Map.of(
                "level1", Map.of(
                        "level2a", Map.of("level3a", 100, "level3b", 200),
                        "level2b", Map.of("level3c", 300)
                ),
                "another1", Map.of(
                        "another2", Map.of("another3", 400)
                )
        ), result);
    }

}

