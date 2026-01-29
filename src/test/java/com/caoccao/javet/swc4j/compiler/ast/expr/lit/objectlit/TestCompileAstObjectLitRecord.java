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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for basic Record type validation with object literals.
 */
public class TestCompileAstObjectLitRecord extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordEmptyValid(JdkVersion jdkVersion) throws Exception {
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
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerNumberValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(3, result.size());
        assertEquals(100, result.get(1));
        assertEquals(200, result.get(2));
        assertEquals(3.14, result.get(3));
    }

    // Phase 7: Array Value Types (without Record type validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerStringValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0));
        assertEquals("one", result.get(1));
        assertEquals("forty-two", result.get(42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongNumberValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(2, result.size());
        assertEquals(100, result.get(1L));
        assertEquals(200, result.get(2147483648L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongStringValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0L));
        assertEquals("one", result.get(1L));
        assertEquals("large", result.get(2147483648L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberEmptyValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertTrue(result.isEmpty());
    }

    // Phase 1: Basic Properties

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberNumberValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of(1, 100, 2, 200, 3, 300), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringEmpty(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 1: Empty object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<number, string> = {}
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of(), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringLargeObject(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 10: Larger object (20+ numeric keys)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(SimpleMap.of(
                1, "v1", 2, "v2", 3, "v3", 4, "v4", 5, "v5",
                6, "v6", 7, "v7", 8, "v8", 9, "v9", 10, "v10",
                11, "v11", 12, "v12", 13, "v13", 14, "v14", 15, "v15",
                16, "v16", 17, "v17", 18, "v18", 19, "v19", 20, "v20"
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringNegativeKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 7: Negative numeric keys
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of(-1, "negative one", -10, "negative ten", 0, "zero", 10, "positive ten"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringNumericLiteralKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 2: Numeric literal keys
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Numeric literal keys stored as Integer
        assertEquals(Map.of(0, "zero", 1, "one", 42, "answer", 100, "hundred"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringOverwrite(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 8: Key overwriting (duplicate numeric keys)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Last value wins for duplicate keys
        assertEquals(Map.of(1, "FIRST", 2, "TWO"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 9: In return type context
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<number, string> {
                      return {1: "one", 2: "two", 3: "three"}
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Return type context: numeric keys become strings (type info not propagated)
        assertEquals(Map.of("1", "one", "2", "two", "3", "three"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringTypedNumericKeys(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<number, string> test 4: Keys from typed number variables
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Variables typed as 'number' are Double, so keys are 1.0, 2.0, 3.0
        assertEquals(Map.of(1.0, "first", 2.0, "second", 3.0, "third"), result);
    }

    // Phase 7: Edge Cases 2, 6, 10, 11, 12 (Key Handling)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberStringValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(3, result.size());
        assertEquals("zero", result.get(0));
        assertEquals("one", result.get(1));
        assertEquals("forty-two", result.get(42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberEmpty(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 1: Empty object
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
        assertEquals(Map.of(), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberExpressionValues(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 7: Expression values (arithmetic, etc.)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
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
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(SimpleMap.of(
                "p1", 1, "p2", 2, "p3", 3, "p4", 4, "p5", 5,
                "p6", 6, "p7", 7, "p8", 8, "p9", 9, "p10", 10,
                "p11", 11, "p12", 12, "p13", 13, "p14", 14, "p15", 15,
                "p16", 16, "p17", 17, "p18", 18, "p19", 19, "p20", 20
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberOverwrite(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 8: Key overwriting (duplicate keys)
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Last value wins for duplicate keys
        assertEquals(Map.of("a", 100, "b", 20), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberReturnTypeContext(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 9: In return type context
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, number> {
                      return {x: 1, y: 2, z: 3}
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of("x", 1, "y", 2, "z", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberSimpleProperties(JdkVersion jdkVersion) throws Exception {
        // Comprehensive Record<string, number> test 2: Simple properties with different numeric literals
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
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
    public void testRecordStringNumberValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNumberWithDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(1, result.get("a"));
        assertEquals(3.14, result.get("b"));
        assertEquals(42, result.get("c"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringStringValid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertEquals(Map.of("name", "Alice", "city", "NYC"), result);
    }

}
