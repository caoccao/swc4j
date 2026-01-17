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

    // Phase 2.1: Record<number, V> - Numeric keys

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

    // Phase 2.2: Type Alias Keys - Integer, Long, Double, String

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

    // Phase 2.3: Nested Record types

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

    // Phase 3: Property Shorthand

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

    // Phase 4: Spread Operator

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

    // ========== Phase 2: Record Type Validation Tests ==========

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

}
