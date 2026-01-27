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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for advanced Record features including nested records, mixed features, spreads, computed properties, and shorthands.
 */
public class TestCompileAstObjectLitRecordAdvanced extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordEmptyNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner1", 42, "inner2", 99),
                "outer2", Map.of("inner3", 100)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedNumberKeyValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("computed", 42, "key1", 99), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedAllFeatures(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10, "y", 20.0, "computed", 40, "z", 30.0, "key2", 50, "literal", 60), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedComputedAndSpreadValidation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "cd", 3), result);
    }

    // Phase 7: Null Handling

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedMultipleSpreadsAndShorthands(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "c", 3.0, "b", 2, "d", 4.0, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedNestedWithSpreadShorthand(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3.0, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedWithOverrides(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Later properties override earlier ones
        assertEquals(Map.of("a", 99.0, "b", 88), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberMixedIntAndDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(2, result.size());
        assertEquals(42, result.get(1));
        assertEquals(3.14, result.get(2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringComputedNumericKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 3: Computed numeric keys
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(10, "ten", 11, "eleven", 20, "twenty"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringMixedFeatures(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 6: Mixed features (literal + computed + spread)
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(0, "zero", 1, "one", 2, "two", 10, "ten", 11, "eleven"), result);
    }

    // Phase 7: Edge Cases 23-30 (Type Conversion and Validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringSpread(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 5: Spread operator
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandMixedValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("x", 10.0, "y", 20.0, "z", 30), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandStringNumberValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1.0, "b", 2.0, "c", 3.0), result);
    }

    // Phase 7: Edge Cases 17-19 (Computed Keys, Collisions, Expression Values)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandStringStringValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("name", "Alice", "city", "NYC"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
    public void testRecordSpreadOverwrite(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const obj: Record<string, number> = {a: 99, ...base, c: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Spread overwrites earlier properties
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base: Record<string, number> = {a: 1, b: 2}
                      const obj: Record<string, number> = {c: 3, ...base, d: 4}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("c", 3, "a", 1, "b", 2, "d", 4), result);
    }

    // Phase 7: Edge Cases 31-32 (Array Values in Record, Empty Object with Strict Type)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberComputedKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 3: Computed string keys
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("key1", 1, "key2", 2, "literal", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberMixedFeatures(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 6: Mixed features (spread + shorthand + computed + regular)
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
    public void testRecordStringNumberShorthand(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 4: Shorthand properties
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        // Variables typed as 'number' are stored as Double
        assertEquals(Map.of("a", 10.0, "b", 20.0, "c", 30.0), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberSpread(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 5: Spread operator
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThreeLevelNestedRecordEmptyAtEachLevel(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        Class<?> classA = runner.getClass("com.A");
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
