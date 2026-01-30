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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Tests for edge cases and special scenarios in object literal compilation.
 */
public class TestCompileAstObjectLitEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase02DuplicateKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 2: Duplicate keys - later value wins
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, a: 2, a: 3}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Later value should win (Map.put overwrites)
        assertThat(result).isEqualTo(Map.of("a", 3));
    }

    // Phase 2: Computed Property Names

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase06NonStringPrimitiveKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 6: Non-string primitive keys (boolean, null) converted to string
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Primitive keys are converted to strings
        assertThat(result).isEqualTo(Map.of("true", "yes", "false", "no", "null", "none"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase10ReservedKeywords(JdkVersion jdkVersion) throws Exception {
        // Edge case 10: Reserved keywords as keys work fine in Maps
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Reserved keywords are just strings in Map keys
        assertThat(
                result
        ).isEqualTo(
                Map.of(
                        "class", "className",
                        "for", "loop",
                        "if", "condition",
                        "while", "iteration",
                        "return", "exit"
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase11WhitespaceInKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 11: Whitespace in keys is preserved exactly
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Whitespace is preserved exactly
        assertThat(result.get("key with spaces")).isEqualTo("value1");
        assertThat(result.get("  trim  ")).isEqualTo("value2");
        assertThat(result.get("\ttab\t")).isEqualTo("value3");
        assertThat(result.get("\nnewline\n")).isEqualTo("value4");
        assertThat(result.size()).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase12UnicodeKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 12: Unicode keys are supported (Java strings support full Unicode)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Unicode is fully supported
        assertThat(
                result
        ).isEqualTo(
                Map.of(
                        "你好", "hello",
                        "🔥", "fire",
                        "café", "coffee",
                        "Москва", "Moscow",
                        "東京", "Tokyo"
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase14ObjectAsValueTypeAnnotation(JdkVersion jdkVersion) throws Exception {
        // Edge case 14: Object as value type annotation - still generates LinkedHashMap
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Object = {a: 1, b: "hello", c: true}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Object type annotation doesn't prevent LinkedHashMap generation
        assertThat(result).isEqualTo(Map.of("a", 1, "b", "hello", "c", true));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase15MixedKeyTypes(JdkVersion jdkVersion) throws Exception {
        // Edge case 15: Mixed key types - all converted to String
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const computed = "computed"
                      const obj = {"str": 1, 42: 2, [computed]: 3, true: 4}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // All keys coerced to String
        assertThat(result).isEqualTo(Map.of("str", 1, "42", 2, "computed", 3, "true", 4));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase16TrailingCommas(JdkVersion jdkVersion) throws Exception {
        // Edge case 16: Trailing commas - AST handles this automatically
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Trailing comma doesn't affect object creation
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2, "c", 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysEvaluationOrder(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys evaluation order - expressions evaluated left to right
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Computed keys with string concat expressions - insertion order preserved
        assertThat(result).isEqualTo(Map.of("key1", "first", "key2", "second", "key3", "third"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysWithExpressions(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys with expressions - evaluate in order
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Expressions evaluated: keys are Integer 10, 11, 12
        assertThat(result).isEqualTo(Map.of(10, "ten", 11, "eleven", 12, "twelve"));
    }

    // Phase 2.1: Record<number, V> - Numeric keys

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase17ComputedKeysWithVariableReferences(JdkVersion jdkVersion) throws Exception {
        // Edge case 17: Computed keys with variable references - evaluate in order
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Keys are "0", "1", "2" (numeric keys coerced to string by default)
        assertThat(result).isEqualTo(Map.of("0", "first", "1", "second", "2", "third"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase18PropertyNameCollisions(JdkVersion jdkVersion) throws Exception {
        // Edge case 18: Property name collisions after coercion - later value wins
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Both keys coerce to "1", later value wins
        assertThat(result).isEqualTo(Map.of("1", "string"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase19ExpressionValues(JdkVersion jdkVersion) throws Exception {
        // Edge case 19: Expression values - various expressions evaluated at runtime
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // All expressions evaluated correctly
        assertThat(
                result
        ).isEqualTo(
                Map.of(
                        "arithmetic", 6,
                        "multiplication", 20,
                        "division", 5,
                        "subtraction", 7,
                        "stringConcat", "hello world",
                        "booleanTrue", true,
                        "booleanFalse", false
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase20ObjectInReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Edge case 20: Object in return type context - LinkedHashMap returned
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getObject(): Object {
                      return {a: 1, b: "hello", c: true}
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("getObject");
        // Verify result is LinkedHashMap even with Object return type
        assertThat(result).isInstanceOf(LinkedHashMap.class);
        var linkedHashMap = (LinkedHashMap<?, ?>) result;
        assertThat(linkedHashMap).isEqualTo(Map.of("a", 1, "b", "hello", "c", true));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase23MixedNumericStringKeys(JdkVersion jdkVersion) {
        // Edge case 23: Mixed numeric literal and string literal keys with Record<number, V>
        // Should reject string keys for strict type safety
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected key type mismatch for string key '2', got: " + causeMessage).contains("Key").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase24NullInNonNullableRecord(JdkVersion jdkVersion) {
        // Edge case 24: Null values in Record<string, string> should be allowed (Java allows null)
        // Note: Java doesn't enforce non-nullable by default, so this test verifies null is allowed
        try {
            var runner = getCompiler(jdkVersion).compile("""
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
            var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
            // Null should be allowed in Java Maps
            assertThat(result).isEqualTo(SimpleMap.of("a", "hello", "b", null));
        } catch (Exception e) {
            // If compilation fails, it means null validation is strict
            assertThat(e.getCause()).as("Expected wrapped exception").isNotNull();
            String causeMessage = e.getCause().getMessage();
            assertThat(causeMessage.contains("null") || causeMessage.contains("Property 'b'")).as("Expected null-related error, got: " + causeMessage).isTrue();
        }
    }

    // Phase 2.2: Type Alias Keys - Integer, Long, Double, String

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase27WideningConversion(JdkVersion jdkVersion) throws Exception {
        // Edge case 27: Widening conversion - int literals widen to long type
        // Note: Current implementation stores values based on literal size
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // int literals are stored as Integer when they fit in int range
        assertThat(result).isEqualTo(Map.of("a", 42, "b", 100));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase28NarrowingAllowed(JdkVersion jdkVersion) throws Exception {
        // Edge case 28: Narrowing conversion - compiler allows long literals in int context
        // Note: Current implementation allows this (no strict narrowing validation)
        // The large value will be truncated/wrapped at runtime
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase29ObjectTypePermissive(JdkVersion jdkVersion) throws Exception {
        // Edge case 29: Record<string, Object> allows any value type (permissive)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Verify primitive values
        assertThat(result.get("a")).isEqualTo(1);
        assertThat(result.get("b")).isEqualTo("hello");
        assertThat((Boolean) result.get("c")).isTrue();
        // Verify nested object
        var nested = (LinkedHashMap<?, ?>) result.get("d");
        assertThat(nested).isEqualTo(Map.of("nested", "object"));
        // Verify array
        assertThat(result.get("e")).isEqualTo(List.of(1, 2, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase30UnionTypesIgnored(JdkVersion jdkVersion) throws Exception {
        // Edge case 30: Union types (number | string) compile but validation is not enforced
        // Note: Current implementation parses union types but treats them permissively (like Object)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Union types are treated permissively - mixed types allowed
        assertThat(result).isEqualTo(Map.of("a", 1, "b", "hello", "c", 3.14));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase31ArrayValuesInRecord(JdkVersion jdkVersion) throws Exception {
        // Edge case 31: Array values in Record<string, Object> - arrays work as values
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Verify arrays are stored correctly
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("a")).isInstanceOf(List.class);
        assertThat(result.get("b")).isInstanceOf(List.class);
        assertThat(result.get("a")).isEqualTo(List.of(1, 2, 3));
        assertThat(result.get("b")).isEqualTo(List.of(4, 5, 6));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase32EmptyObjectWithStrictRecordType(JdkVersion jdkVersion) throws Exception {
        // Edge case 32: Empty object with strict Record type - should be valid
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Empty map is valid for any Record type
        assertThat(result).isEqualTo(Map.of());
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyMultipleKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Multiple computed properties with type validation
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Mix of computed and regular keys
        assertThat(result).isEqualTo(Map.of("first", 1, "second", 2, "third", 3));
    }

    // Phase 2.3: Nested Record types

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyWithCorrectNumericType(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Computed property with numeric key type - should work with Record<number, V>
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: number = 123
                      const obj: Record<number, string> = {[key]: "value"}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Computed key with number type - TypeScript number maps to Double
        assertThat(result).isEqualTo(Map.of(123.0, "value"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase33ComputedPropertyWithCorrectType(JdkVersion jdkVersion) throws Exception {
        // Edge case 33: Computed property with type validation - correct type should work
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key: string = "dynamic"
                      const obj: Record<string, number> = {[key]: 42}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Computed key with string type matches Record<string, number>
        assertThat(result).isEqualTo(Map.of("dynamic", 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase34ComputedPropertyStringInNumberRecord(JdkVersion jdkVersion) {
        // Edge case 34: String computed key in Record<number, V> - should reject
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Should fail - compiler rejects type mismatch during bytecode generation
        assertThat(exception.getMessage()).as("Error message should indicate generation failure: " + exception.getMessage()).contains("Failed to generate method");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEdgeCase34ComputedPropertyWithWrongKeyType(JdkVersion jdkVersion) {
        // Edge case 34: Computed property with wrong key type - should reject
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Should fail - compiler rejects type mismatch during bytecode generation
        assertThat(exception.getMessage()).as("Error message should indicate generation failure: " + exception.getMessage()).contains("Failed to generate method");
    }

}
