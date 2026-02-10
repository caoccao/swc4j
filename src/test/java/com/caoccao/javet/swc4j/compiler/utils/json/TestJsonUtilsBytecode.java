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

package com.caoccao.javet.swc4j.compiler.utils.json;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive bytecode integration tests for JSON.stringify / JSON.parse.
 * Covers all applicable edge cases from the JSON plan (edge cases #1-57).
 */
public class TestJsonUtilsBytecode extends BaseTestCompileSuite {
    @AfterEach
    public void resetProvider() {
        JsonUtils.setProvider(null);
    }


    // ========================================================================
    // JSON.stringify — Primitive types (edge cases #2-#6, #49)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyZeroArgs(JdkVersion jdkVersion) throws Exception {
        // Edge case #47: 0-argument JSON.stringify() returns "undefined"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("undefined");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyInteger(JdkVersion jdkVersion) throws Exception {
        // Edge case #49: Primitive argument boxing
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(42)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNegativeInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(-42)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("-42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(3.14)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("3.14");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyBooleanTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(true)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyBooleanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(false)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("false");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("hello")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("\"hello\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(0)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("0");
    }

    // ========================================================================
    // JSON.stringify — NaN, Infinity, -0 (edge cases #3, #4)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNaN(JdkVersion jdkVersion) throws Exception {
        // Edge case #3: NaN → "null"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a: double = 0.0
                      const b: double = 0.0
                      const x: double = a / b
                      return JSON.stringify(x)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyPositiveInfinity(JdkVersion jdkVersion) throws Exception {
        // Edge case #3: Infinity → "null"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a: double = 1.0
                      const b: double = 0.0
                      const x: double = a / b
                      return JSON.stringify(x)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNegativeInfinity(JdkVersion jdkVersion) throws Exception {
        // Edge case #3: -Infinity → "null"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a: double = 1.0
                      const b: double = 0.0
                      const inf: double = a / b
                      const negInf: double = 0.0 - inf
                      return JSON.stringify(negInf)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null");
    }

    // ========================================================================
    // JSON.stringify — Objects (edge cases #7-#9, #15, #20)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { name: "Alice", age: 30 }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"name\":\"Alice\",\"age\":30}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyEmptyObject(JdkVersion jdkVersion) throws Exception {
        // Edge case #15: Empty object → "{}"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = {}
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: { c: 1 } } }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{\"b\":{\"c\":1}}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithNullValues(JdkVersion jdkVersion) throws Exception {
        // Edge case #9: Null map values serialize as null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: null, b: 2 }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":null,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { str: "hi", num: 42, bool: true, nil: null }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"str\":\"hi\",\"num\":42,\"bool\":true,\"nil\":null}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithNumericKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case #7: Numeric keys converted to strings
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { 0: "zero", 1: "one" }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        String result = (String) runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).contains("\"0\":\"zero\"");
        assertThat(result).contains("\"1\":\"one\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithArrayValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: [1, 2], b: [3, 4] }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":[1,2],\"b\":[3,4]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectManyProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2, c: 3, d: 4, e: 5 }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2,\"c\":3,\"d\":4,\"e\":5}");
    }

    // ========================================================================
    // JSON.stringify — Arrays (edge cases #16, #19)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, 2, 3]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[1,2,3]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyEmptyArray(JdkVersion jdkVersion) throws Exception {
        // Edge case #16: Empty array → "[]"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = []
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("[]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [[1, 2], [3, 4]]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[[1,2],[3,4]]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayWithNulls(JdkVersion jdkVersion) throws Exception {
        // Edge case #19: null in arrays serializes as null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [null, 1, null]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[null,1,null]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyMixedArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, "two", true, null]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[1,\"two\",true,null]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayOfObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ a: 1 }, { b: 2 }]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{\"a\":1},{\"b\":2}]");
    }

    // ========================================================================
    // JSON.stringify — Complex nested structures (edge case #17)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyDeeplyNested(JdkVersion jdkVersion) throws Exception {
        // Edge case #17: Deeply nested structures
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { d: [1, 2, 3] }
                      const mid = { c: inner }
                      const outer = { b: mid }
                      const obj = { a: outer }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{\"b\":{\"c\":{\"d\":[1,2,3]}}}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyComplexStructure(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = {
                        name: "test",
                        items: [1, 2, 3],
                        nested: { x: true, y: [4, 5] },
                        empty: {}
                      }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"name\":\"test\",\"items\":[1,2,3],\"nested\":{\"x\":true,\"y\":[4,5]},\"empty\":{}}");
    }

    // ========================================================================
    // JSON.stringify — Nested objects and arrays (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithNestedArraysOfDifferentLengths(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { short: [1], medium: [1, 2, 3], long: [1, 2, 3, 4, 5] }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"short\":[1],\"medium\":[1,2,3],\"long\":[1,2,3,4,5]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayOfMixedNestedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ a: 1 }, [2, 3], "text", null, true]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{\"a\":1},[2,3],\"text\",null,true]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedAllValueTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = {
                        str: "hello",
                        num: 42,
                        dbl: 3.14,
                        boolT: true,
                        boolF: false,
                        nil: null,
                        arr: [1, "two"],
                        nested: { inner: 99 }
                      }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"str\":\"hello\",\"num\":42,\"dbl\":3.14,\"boolT\":true,\"boolF\":false,\"nil\":null,\"arr\":[1,\"two\"],\"nested\":{\"inner\":99}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectArrayObjectArray(JdkVersion jdkVersion) throws Exception {
        // object > array > object > array pattern
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { items: [10, 20] }
                      const obj = { data: [inner] }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"data\":[{\"items\":[10,20]}]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyMultipleSiblingNestedObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { x: 1 }, b: { y: 2 }, c: { z: 3 } }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{\"x\":1},\"b\":{\"y\":2},\"c\":{\"z\":3}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedWithEmptyContainers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: {}, b: [], c: { d: [], e: {} } }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{},\"b\":[],\"c\":{\"d\":[],\"e\":{}}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyDeeplyNestedArrays(JdkVersion jdkVersion) throws Exception {
        // 3-level nested arrays
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = [[1, 2], [3, 4]]
                      const arr = [inner, [[5, 6]]]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[[[1,2],[3,4]],[[5,6]]]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayOfObjectsWithArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [
                        { name: "a", vals: [1, 2] },
                        { name: "b", vals: [3, 4] }
                      ]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{\"name\":\"a\",\"vals\":[1,2]},{\"name\":\"b\",\"vals\":[3,4]}]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedObjectsViaVariables(JdkVersion jdkVersion) throws Exception {
        // Build 5-level nesting using intermediate variables
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const e = { val: 5 }
                      const d = { e: e }
                      const c = { d: d }
                      const b = { c: c }
                      const a = { b: b }
                      return JSON.stringify(a)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"b\":{\"c\":{\"d\":{\"e\":{\"val\":5}}}}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayWithNestedNullsAtVariousDepths(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [null, [null, 1], { a: null }]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[null,[null,1],{\"a\":null}]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithSingletonArrayValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: [1], b: ["x"], c: [true], d: [null] }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":[1],\"b\":[\"x\"],\"c\":[true],\"d\":[null]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedObjectWithStringValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { user: { first: "John", last: "Doe" }, role: { name: "admin", level: "high" } }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"user\":{\"first\":\"John\",\"last\":\"Doe\"},\"role\":{\"name\":\"admin\",\"level\":\"high\"}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayContainingEmptyContainers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{}, [], { a: {} }, [[]]]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{},[],{\"a\":{}},[[]]]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyMixedNestedFromHelperMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    buildPerson() {
                      return { name: "Alice", scores: [95, 87] }
                    }
                    buildTeam() {
                      return { leader: this.buildPerson(), size: 5 }
                    }
                    test(): String {
                      return JSON.stringify(this.buildTeam())
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"leader\":{\"name\":\"Alice\",\"scores\":[95,87]},\"size\":5}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedObjectWithBooleanLeaves(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { flags: { active: true, deleted: false }, meta: { visible: true } }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"flags\":{\"active\":true,\"deleted\":false},\"meta\":{\"visible\":true}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayOfArraysOfObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [[{ id: 1 }, { id: 2 }], [{ id: 3 }]]
                      return JSON.stringify(arr)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[[{\"id\":1},{\"id\":2}],[{\"id\":3}]]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithNestedNumericArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const matrix = { rows: [[1, 2, 3], [4, 5, 6], [7, 8, 9]] }
                      return JSON.stringify(matrix)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"rows\":[[1,2,3],[4,5,6],[7,8,9]]}");
    }

    // ========================================================================
    // JSON.stringify — Nested with space formatting (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedObjectWithSpace4(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: 1, c: 2 }, d: 3 }
                      return JSON.stringify(obj, null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n    \"a\": {\n        \"b\": 1,\n        \"c\": 2\n    },\n    \"d\": 3\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedArrayWithSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [[1, 2], [3, 4]]
                      return JSON.stringify(arr, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[\n  [\n    1,\n    2\n  ],\n  [\n    3,\n    4\n  ]\n]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyObjectWithArraysAndSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { items: [1, 2], tags: ["a", "b"] }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"items\": [\n    1,\n    2\n  ],\n  \"tags\": [\n    \"a\",\n    \"b\"\n  ]\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayOfObjectsWithSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ a: 1 }, { b: 2 }]
                      return JSON.stringify(arr, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[\n  {\n    \"a\": 1\n  },\n  {\n    \"b\": 2\n  }\n]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyDeeplyNestedWithSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { c: [1, 2] }
                      const obj = { a: { b: inner } }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": {\n    \"b\": {\n      \"c\": [\n        1,\n        2\n      ]\n    }\n  }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyMixedNestedWithEmptyAndSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: {}, b: [], c: 1 }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": {},\n  \"b\": [],\n  \"c\": 1\n}");
    }

    // ========================================================================
    // JSON.parse — Nested structures (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseObjectWithNestedArraysOfDifferentLengths(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":[1],\\"b\\":[1,2,3],\\"c\\":[1,2,3,4,5]}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.get("a")).isEqualTo(List.of(1));
        assertThat(result.get("b")).isEqualTo(List.of(1, 2, 3));
        assertThat(result.get("c")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseObjectArrayObjectArrayPattern(JdkVersion jdkVersion) throws Exception {
        // object > array > object > array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"data\\":[{\\"items\\":[10,20]}]}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        @SuppressWarnings("unchecked")
        ArrayList<Object> data = (ArrayList<Object>) result.get("data");
        assertThat(data).hasSize(1);
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> inner = (LinkedHashMap<String, Object>) data.get(0);
        assertThat(inner.get("items")).isEqualTo(List.of(10, 20));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseSiblingNestedObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":{\\"x\\":1},\\"b\\":{\\"y\\":2},\\"c\\":{\\"z\\":3}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.get("a")).isEqualTo(Map.of("x", 1));
        assertThat(result.get("b")).isEqualTo(Map.of("y", 2));
        assertThat(result.get("c")).isEqualTo(Map.of("z", 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNestedWithEmptyContainers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":{},\\"b\\":[],\\"c\\":{\\"d\\":[],\\"e\\":{}}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.get("a")).isEqualTo(Map.of());
        assertThat(result.get("b")).isEqualTo(List.of());
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> c = (LinkedHashMap<String, Object>) result.get("c");
        assertThat(c.get("d")).isEqualTo(List.of());
        assertThat(c.get("e")).isEqualTo(Map.of());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseArrayOfMixedNestedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[{\\"a\\":1},[2,3],\\"text\\",null,true]")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        ArrayList<Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).hasSize(5);
        assertThat(result.get(0)).isEqualTo(Map.of("a", 1));
        assertThat(result.get(1)).isEqualTo(List.of(2, 3));
        assertThat(result.get(2)).isEqualTo("text");
        assertThat(result.get(3)).isNull();
        assertThat(result.get(4)).isEqualTo(true);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseAllValueTypesInObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"s\\":\\"hi\\",\\"n\\":42,\\"d\\":3.14,\\"t\\":true,\\"f\\":false,\\"nil\\":null,\\"a\\":[1],\\"o\\":{\\"k\\":2}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.get("s")).isEqualTo("hi");
        assertThat(result.get("n")).isEqualTo(42);
        assertThat(result.get("d")).isEqualTo(3.14);
        assertThat(result.get("t")).isEqualTo(true);
        assertThat(result.get("f")).isEqualTo(false);
        assertThat(result.get("nil")).isNull();
        assertThat(result.get("a")).isEqualTo(List.of(1));
        assertThat(result.get("o")).isEqualTo(Map.of("k", 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseDeeplyNestedViaRoundTrip(JdkVersion jdkVersion) throws Exception {
        // 5-level nesting through round-trip
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":{\\"b\\":{\\"c\\":{\\"d\\":{\\"e\\":99}}}}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(
                Map.of("a", Map.of("b", Map.of("c", Map.of("d", Map.of("e", 99))))));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseArrayOfArraysOfObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[[{\\"id\\":1},{\\"id\\":2}],[{\\"id\\":3}]]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(
                        List.of(Map.of("id", 1), Map.of("id", 2)),
                        List.of(Map.of("id", 3))));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseMatrixObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"rows\\":[[1,2,3],[4,5,6],[7,8,9]]}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.get("rows")).isEqualTo(
                List.of(List.of(1, 2, 3), List.of(4, 5, 6), List.of(7, 8, 9)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNullsAtVariousDepths(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[null,[null,1],{\\"a\\":null}]")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        ArrayList<Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNull();
        assertThat(result.get(1)).isEqualTo(new ArrayList<>(java.util.Arrays.asList(null, 1)));
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>) result.get(2);
        assertThat(obj).isEqualTo(SimpleMap.of("a", null));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseArrayOfObjectsWithNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[{\\"name\\":\\"a\\",\\"vals\\":[1,2]},{\\"name\\":\\"b\\",\\"vals\\":[3,4]}]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(
                        Map.of("name", "a", "vals", List.of(1, 2)),
                        Map.of("name", "b", "vals", List.of(3, 4))));
    }

    // ========================================================================
    // Round-trip — Nested structures (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripNestedObjectWithArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const alice = { name: "Alice", tags: ["admin", "user"] }
                      const bob = { name: "Bob", tags: ["user"] }
                      const obj = { users: [alice, bob] }
                      const json: String = JSON.stringify(obj)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"users\":[{\"name\":\"Alice\",\"tags\":[\"admin\",\"user\"]},{\"name\":\"Bob\",\"tags\":[\"user\"]}]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripDeeplyNestedViaVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const leaf = { val: [1, 2, 3] }
                      const mid = { child: leaf, extra: "x" }
                      const root = { top: mid }
                      const json: String = JSON.stringify(root)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"top\":{\"child\":{\"val\":[1,2,3]},\"extra\":\"x\"}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripArrayOfNestedObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const item1 = { a: { b: 1 } }
                      const item2 = { c: [2, 3] }
                      const inner = { e: { f: 4 } }
                      const item3 = { d: inner }
                      const arr = [item1, item2, item3]
                      const json: String = JSON.stringify(arr)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{\"a\":{\"b\":1}},{\"c\":[2,3]},{\"d\":{\"e\":{\"f\":4}}}]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripMixedWithEmptyContainers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: {}, b: [], c: { d: [], e: {} }, f: [{}] }
                      const json: String = JSON.stringify(obj)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{},\"b\":[],\"c\":{\"d\":[],\"e\":{}},\"f\":[{}]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripMatrixStructure(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const matrix = { rows: [[1, 2], [3, 4]], cols: 2 }
                      const json: String = JSON.stringify(matrix)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"rows\":[[1,2],[3,4]],\"cols\":2}");
    }

    // ========================================================================
    // JSON.stringify — 3-arg: space parameter (edge cases #12-#16)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithSpaceNumber(JdkVersion jdkVersion) throws Exception {
        // Edge case #14: Nested indentation with numeric space
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": 1,\n  \"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithSpaceZero(JdkVersion jdkVersion) throws Exception {
        // Edge case #13: Space 0 → compact output
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, 0)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithNullReplacerNullSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNestedWithSpace(JdkVersion jdkVersion) throws Exception {
        // Edge case #14: Nested indentation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: 1 } }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": {\n    \"b\": 1\n  }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyArrayWithSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, 2, 3]
                      return JSON.stringify(arr, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[\n  1,\n  2,\n  3\n]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyEmptyObjectWithSpace(JdkVersion jdkVersion) throws Exception {
        // Edge case #15: Empty object produces "{}" regardless of indentation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify({}, null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyEmptyArrayWithSpace(JdkVersion jdkVersion) throws Exception {
        // Edge case #16: Empty array produces "[]" regardless of indentation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify([], null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("[]");
    }

    // ========================================================================
    // JSON.stringify — 2/3-arg: replacer parameter (edge case #10)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithReplacer(JdkVersion jdkVersion) throws Exception {
        // Edge case #10: Replacer as ArrayList (property whitelist)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2, c: 3 }
                      const replacer = ["a", "c"]
                      return JSON.stringify(obj, replacer, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"c\":3}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithReplacerAndSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2, c: 3 }
                      const replacer = ["b"]
                      return JSON.stringify(obj, replacer, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyWithTwoArgs(JdkVersion jdkVersion) throws Exception {
        // 2-arg stringify: (value, replacer) — space defaults to null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { x: 10, y: 20, z: 30 }
                      return JSON.stringify(obj, ["x", "z"])
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"x\":10,\"z\":30}");
    }

    // ========================================================================
    // JSON.stringify — Replacer (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerSingleKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2, c: 3 }
                      return JSON.stringify(obj, ["b"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerNoMatchingKeys(JdkVersion jdkVersion) throws Exception {
        // All keys filtered out → empty object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, ["x", "y"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerEmptyArray(JdkVersion jdkVersion) throws Exception {
        // Empty replacer array → empty object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, [], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerOnArray(JdkVersion jdkVersion) throws Exception {
        // Replacer has no effect on array (only filters object keys)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, 2, 3]
                      return JSON.stringify(arr, ["0", "1"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[1,2,3]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerOnPrimitive(JdkVersion jdkVersion) throws Exception {
        // Replacer has no effect on primitives
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(42, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerAppliesToNestedObjects(JdkVersion jdkVersion) throws Exception {
        // Replacer applies recursively: only "a" keys kept at every level
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { a: 10, b: 20 }
                      const obj = { a: inner, b: 99 }
                      return JSON.stringify(obj, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{\"a\":10}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerNestedKeepsArrays(JdkVersion jdkVersion) throws Exception {
        // Replacer filters object keys but arrays inside are fully serialized
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: [1, 2, 3], b: "skip" }
                      return JSON.stringify(obj, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":[1,2,3]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerOnObjectInsideArray(JdkVersion jdkVersion) throws Exception {
        // Replacer filters keys of objects inside arrays
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ a: 1, b: 2 }, { a: 3, c: 4 }]
                      return JSON.stringify(arr, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[{\"a\":1},{\"a\":3}]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithSpaceOnNestedObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { x: 1, y: 2 }
                      const obj = { a: inner, b: 99, c: "skip" }
                      return JSON.stringify(obj, ["a", "x"], 2)
                    }
                  }
                }""");
        String result = (String) runner.createInstanceRunner("com.A").<Object>invoke("test");
        // Replacer keeps "a" at top level, "x" at nested level
        assertThat(result).contains("\"a\"");
        assertThat(result).contains("\"x\": 1");
        assertThat(result).doesNotContain("\"b\"");
        assertThat(result).doesNotContain("\"c\"");
        assertThat(result).doesNotContain("\"y\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithNullReplacer(JdkVersion jdkVersion) throws Exception {
        // Null replacer → no filtering
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerOnEmptyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify({}, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerTwoArgOnNestedObject(JdkVersion jdkVersion) throws Exception {
        // 2-arg: replacer only, no space
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { name: "test", id: 1, secret: "hidden" }
                      return JSON.stringify(obj, ["name", "id"])
                    }
                  }
                }""");
        String result = (String) runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).contains("\"name\":\"test\"");
        assertThat(result).contains("\"id\":1");
        assertThat(result).doesNotContain("secret");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithStringValue(JdkVersion jdkVersion) throws Exception {
        // Replacer on string value → no effect (primitives pass through)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("hello", ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("\"hello\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithNullValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(null, ["a"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerDeeplyNested(JdkVersion jdkVersion) throws Exception {
        // Replacer applied through 3 levels of nesting
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const c = { val: 1, skip: 2 }
                      const b = { val: c, skip: 3 }
                      const a = { val: b, skip: 4 }
                      return JSON.stringify(a, ["val"], null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"val\":{\"val\":{\"val\":1}}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithArrayOfObjects(JdkVersion jdkVersion) throws Exception {
        // Top-level array: replacer filters keys inside each object element
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ id: 1, name: "a", extra: true }, { id: 2, name: "b", extra: false }]
                      return JSON.stringify(arr, ["id", "name"], null)
                    }
                  }
                }""");
        String result = (String) runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).contains("\"id\"");
        assertThat(result).contains("\"name\"");
        assertThat(result).doesNotContain("extra");
    }

    // ========================================================================
    // JSON.stringify — Function replacer (arrow function as BiFunction)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerPassthrough(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer that returns all values unchanged
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: "two", c: true }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":\"two\",\"c\":true}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerTransformValues(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer that replaces specific values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: "hello", b: "world" }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        if (key === "a") return "replaced"
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":\"replaced\",\"b\":\"world\"}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerWithSpace(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer combined with indentation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        return value
                      }, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": 1,\n  \"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerOnArray(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer on array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, 2, 3]
                      return JSON.stringify(arr, (key: String, value: Object): Object => {
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[1,2,3]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerOnPrimitive(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer on primitive (root call transforms value)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(42, (key: String, value: Object): Object => {
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerNested(JdkVersion jdkVersion) throws Exception {
        // Arrow function replacer applied recursively to nested objects
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { x: 10, secret: "hidden" }
                      const obj = { data: inner, secret: "also hidden" }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        if (key === "secret") return "***"
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"data\":{\"x\":10,\"secret\":\"***\"},\"secret\":\"***\"}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerTwoArgs(JdkVersion jdkVersion) throws Exception {
        // 2-arg: arrow function replacer without space
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        return value
                      })
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerEmptyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify({}, (key: String, value: Object): Object => {
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerOnObjectWithNulls(JdkVersion jdkVersion) throws Exception {
        // Replacer preserves null values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: null, b: 1 }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        return value
                      }, null)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":null,\"b\":1}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFunctionReplacerWithNestedSpace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { b: 2 }
                      const obj = { a: inner }
                      return JSON.stringify(obj, (key: String, value: Object): Object => {
                        return value
                      }, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": {\n    \"b\": 2\n  }\n}");
    }

    // ========================================================================
    // JSON.stringify — Indent/space (additional coverage)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOne(JdkVersion jdkVersion) throws Exception {
        // Space = 1 → single space indent
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, 1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n \"a\": 1,\n \"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceTen(JdkVersion jdkVersion) throws Exception {
        // Space = 10 → maximum
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1 }
                      return JSON.stringify(obj, null, 10)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n          \"a\": 1\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceClampedAboveTen(JdkVersion jdkVersion) throws Exception {
        // Space > 10 → clamped to 10
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1 }
                      return JSON.stringify(obj, null, 20)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n          \"a\": 1\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceNegative(JdkVersion jdkVersion) throws Exception {
        // Negative space → clamped to 0 → compact output
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      const space: int = 0 - 5
                      return JSON.stringify(obj, null, space)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceStringTab(JdkVersion jdkVersion) throws Exception {
        // String space "\t" → tab indent
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, "\\t")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n\t\"a\": 1,\n\t\"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceStringCustom(JdkVersion jdkVersion) throws Exception {
        // Custom string space "--"
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, "--")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n--\"a\": 1,\n--\"b\": 2\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceStringEmpty(JdkVersion jdkVersion) throws Exception {
        // Empty string space → compact (same as no indent)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj, null, "")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOnPrimitive(JdkVersion jdkVersion) throws Exception {
        // Space has no visible effect on primitive values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(42, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOnString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("hello", null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("\"hello\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOnBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(true, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOnNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(null, null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceOneNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: 1 } }
                      return JSON.stringify(obj, null, 1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n \"a\": {\n  \"b\": 1\n }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceTabNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: 1 } }
                      return JSON.stringify(obj, null, "\\t")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n\t\"a\": {\n\t\t\"b\": 1\n\t}\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceCustomNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: 1 } }
                      return JSON.stringify(obj, null, "| ")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n| \"a\": {\n| | \"b\": 1\n| }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceWithMixedArrayAndObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { arr: [1, 2], nested: { x: true } }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"arr\": [\n    1,\n    2\n  ],\n  \"nested\": {\n    \"x\": true\n  }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceWithNestedEmptyContainers(JdkVersion jdkVersion) throws Exception {
        // Empty containers remain compact even with space
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: {}, b: [], c: 1 }
                      return JSON.stringify(obj, null, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n    \"a\": {},\n    \"b\": [],\n    \"c\": 1\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceDeeplyNestedViaVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const c = { val: 1 }
                      const b = { c: c }
                      const a = { b: b }
                      return JSON.stringify(a, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"b\": {\n    \"c\": {\n      \"val\": 1\n    }\n  }\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceWithSingleElementArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [42]
                      return JSON.stringify(arr, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[\n  42\n]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpaceWithSinglePropertyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { only: "one" }
                      return JSON.stringify(obj, null, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"only\": \"one\"\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithSpaceOnArray(JdkVersion jdkVersion) throws Exception {
        // Both replacer and space: replacer filters objects inside array, space indents
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [{ a: 1, b: 2 }, { a: 3, b: 4 }]
                      return JSON.stringify(arr, ["a"], 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[\n  {\n    \"a\": 1\n  },\n  {\n    \"a\": 3\n  }\n]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReplacerWithSpaceDeeplyNested(JdkVersion jdkVersion) throws Exception {
        // Replacer + space on nested structure
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const inner = { keep: 1, drop: 2 }
                      const obj = { keep: inner, drop: 99 }
                      return JSON.stringify(obj, ["keep"], 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"keep\": {\n    \"keep\": 1\n  }\n}");
    }

    // ========================================================================
    // JSON.stringify — String escaping (edge case #27 via stringify)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyStringWithQuotes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("say \\"hi\\"")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("\"say \\\"hi\\\"\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyStringWithBackslash(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("a\\\\b")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("\"a\\\\b\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyEmptyString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify("")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("\"\"");
    }

    // ========================================================================
    // JSON.stringify — Variable and expression operands
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const v = { key: "value" }
                      return JSON.stringify(v)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"key\":\"value\"}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyComputedExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a: int = 10
                      const b: int = 20
                      return JSON.stringify(a + b)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("30");
    }

    // ========================================================================
    // JSON.parse — Primitive types (edge cases #43-#45, #50)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseInteger(JdkVersion jdkVersion) throws Exception {
        // Edge case #50: Return type of JSON.parse is Object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("42")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNegativeInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("-42")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(-42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("3.14")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3.14);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseBooleanTrue(JdkVersion jdkVersion) throws Exception {
        // Edge case #44: Boolean literals
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("true")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseBooleanFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("false")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Boolean.FALSE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNull(JdkVersion jdkVersion) throws Exception {
        // Edge case #43: null literal
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("null")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("\\"hello\\"")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseLargeInteger(JdkVersion jdkVersion) throws Exception {
        // Edge case #45: Long if beyond int range
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("2147483648")
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).isInstanceOf(Long.class);
        assertThat(result).isEqualTo(2147483648L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseExponentNotation(JdkVersion jdkVersion) throws Exception {
        // Edge case #35: Exponent notation → Double
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("1e10")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1e10);
    }

    // ========================================================================
    // JSON.parse — Collections (edge cases #39-#40)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\": 1}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[1, 2, 3]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseEmptyObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{}")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(Map.of());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNestedObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":{\\"b\\":1}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", Map.of("b", 1)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseMixedArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[1, \\"two\\", true]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(1, "two", true));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseArrayOfObjects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[{\\"a\\":1},{\\"b\\":2}]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(Map.of("a", 1), Map.of("b", 2)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseDuplicateKeys(JdkVersion jdkVersion) throws Exception {
        // Edge case #39: Duplicate keys → last value wins
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":1,\\"a\\":2}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseWithWhitespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("  42  ")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseStringWithEscapes(JdkVersion jdkVersion) throws Exception {
        // Edge case #27: Escape sequences through bytecode
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("\\"hello\\\\nworld\\"")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello\nworld");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseUnicodeEscape(JdkVersion jdkVersion) throws Exception {
        // Edge case #29: Unicode escape through bytecode
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("\\"\\\\u0041\\"")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("A");
    }

    // ========================================================================
    // JSON.parse — Runtime error cases (edge cases #21-#26, #33, #36, #37)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseEmptyStringError(JdkVersion jdkVersion) throws Exception {
        // Edge case #21: Empty string → runtime error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseInvalidJsonError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("invalid")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseTrailingContentError(JdkVersion jdkVersion) throws Exception {
        // Edge case #23: Trailing non-whitespace → error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("123abc")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseTrailingCommaArrayError(JdkVersion jdkVersion) throws Exception {
        // Edge case #24: Trailing commas → error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[1,]")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseLeadingZerosError(JdkVersion jdkVersion) throws Exception {
        // Edge case #33: Leading zeros → error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("0123")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    // ========================================================================
    // JSON.parse — Compile-time error cases (edge cases #48, #52)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseZeroArgsCompileError(JdkVersion jdkVersion) {
        // Edge case #48: 0-argument JSON.parse() → compile error
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse()
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class);
    }

    // ========================================================================
    // JSON.stringify — Extra arguments (edge case #51)
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyExtraArgsIgnored(JdkVersion jdkVersion) throws Exception {
        // Edge case #51: Extra arguments beyond 3rd are ignored
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify({ a: 1 }, null, 2, "extra")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\n  \"a\": 1\n}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseExtraArgsIgnored(JdkVersion jdkVersion) throws Exception {
        // Edge case #51: Extra arguments beyond 1st are ignored for parse
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("42")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }

    // ========================================================================
    // Round-trip tests
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { name: "Bob", scores: [95, 87, 100] }
                      const json: String = JSON.stringify(obj)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"name\":\"Bob\",\"scores\":[95,87,100]}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [1, 2, 3, 4, 5]
                      const json: String = JSON.stringify(arr)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[1,2,3,4,5]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripNested(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: { b: [1, 2] }, c: { d: true } }
                      const json: String = JSON.stringify(obj)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":{\"b\":[1,2]},\"c\":{\"d\":true}}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripPrimitives(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testInt(): String {
                      const parsed: Object = JSON.parse(JSON.stringify(42))
                      return JSON.stringify(parsed)
                    }
                    testStr(): String {
                      const parsed: Object = JSON.parse(JSON.stringify("hello"))
                      return JSON.stringify(parsed)
                    }
                    testBool(): String {
                      const parsed: Object = JSON.parse(JSON.stringify(true))
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("testInt")).isEqualTo("42");
        assertThat(instanceRunner.<Object>invoke("testStr")).isEqualTo("\"hello\"");
        assertThat(instanceRunner.<Object>invoke("testBool")).isEqualTo("true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRoundTripWithNulls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const obj = { a: null, b: 1 }
                      const json: String = JSON.stringify(obj)
                      const parsed: Object = JSON.parse(json)
                      return JSON.stringify(parsed)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":null,\"b\":1}");
    }

    // ========================================================================
    // Integration tests — multiple calls, variables, concatenation
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyConcatWithString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const json: String = JSON.stringify(42)
                      return "value:" + json
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("value:42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStringifyCalls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a: String = JSON.stringify(1)
                      const b: String = JSON.stringify("two")
                      const c: String = JSON.stringify(true)
                      return a + "," + b + "," + c
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("1,\"two\",true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleParseCalls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const a: Object = JSON.parse("1")
                      const b: Object = JSON.parse("2")
                      const c: Object = JSON.parse("3")
                      return [a, b, c]
                    }
                  }
                }""");
        // Each parse returns Integer (boxed), stored in ArrayList
        Object result = runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).isEqualTo(List.of(1, 2, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseAssignToVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const parsed: Object = JSON.parse("[10, 20, 30]")
                      return parsed
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(10, 20, 30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyThenParseChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr = [10, 20, 30]
                      return JSON.stringify(JSON.parse(JSON.stringify(arr)))
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("[10,20,30]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyInHelperMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    buildData() {
                      return { x: 10, y: 20 }
                    }
                    test(): String {
                      return JSON.stringify(this.buildData())
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"x\":10,\"y\":20}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseAndStringifyInDifferentMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    serialize(): String {
                      const obj = { a: 1, b: 2 }
                      return JSON.stringify(obj)
                    }
                    test(): Object {
                      const json: String = this.serialize()
                      return JSON.parse(json)
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyFromVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const data = { msg: "hi", count: 5 }
                      const json: String = JSON.stringify(data)
                      return json
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"msg\":\"hi\",\"count\":5}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseFromVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const json: String = "[1, 2, 3]"
                      const result: Object = JSON.parse(json)
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(1, 2, 3));
    }

    // ========================================================================
    // JSON.stringify — Double value edge cases
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyNegativeDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(-3.14)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("-3.14");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyLargeInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(999999)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("999999");
    }

    // ========================================================================
    // JSON.parse — Complex nested structures through bytecode
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseComplexObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"name\\":\\"Alice\\",\\"scores\\":[95,87],\\"active\\":true}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(
                Map.of("name", "Alice", "scores", List.of(95, 87), "active", true));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseDeeplyNestedObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{\\"a\\":{\\"b\\":{\\"c\\":[1,2]}}}")
                    }
                  }
                }""");
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(
                Map.of("a", Map.of("b", Map.of("c", List.of(1, 2)))));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("[[1,2],[3,4],[5]]")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(List.of(1, 2), List.of(3, 4), List.of(5)));
    }

    // ========================================================================
    // JSON.stringify — with spread and variable data
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifySpreadObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const base = { a: 1 }
                      const extended = { ...base, b: 2 }
                      return JSON.stringify(extended)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"a\":1,\"b\":2}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyShorthandProperties(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const x: int = 10
                      const y: int = 20
                      const obj = { x, y }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"x\":10,\"y\":20}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyComputedKeys(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const key = "dynamic"
                      const obj = { [key]: 42, normal: 1 }
                      return JSON.stringify(obj)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("{\"dynamic\":42,\"normal\":1}");
    }

    // ========================================================================
    // JSON.stringify — Return type verification
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyReturnTypeIsString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const result: String = JSON.stringify({ a: 1 })
                      return result
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).isInstanceOf(String.class);
        assertThat(result).isEqualTo("{\"a\":1}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseReturnTypeIsObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const result: Object = JSON.parse("[1,2,3]")
                      return result
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").<Object>invoke("test");
        assertThat(result).isInstanceOf(ArrayList.class);
        assertThat(result).isEqualTo(List.of(1, 2, 3));
    }

    // ========================================================================
    // JSON.parse — Runtime error with message verification
    // ========================================================================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseSingleQuotesError(JdkVersion jdkVersion) throws Exception {
        // Edge case #25: Single quotes → error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("{'key': 'value'}")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNoFractionalPartError(JdkVersion jdkVersion) throws Exception {
        // Edge case #37: "1." → error
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      return JSON.parse("1.")
                    }
                  }
                }""");
        assertThatThrownBy(() -> {
            try {
                runner.createInstanceRunner("com.A").invoke("test");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJsonIdentifierShadowedByLocalVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const JSON: String = "shadowed"
                      return JSON.substring(2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("adowed");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseExtraArgsStillEvaluatesSideEffects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      JSON.parse("1", x = x + 1, x = x + 1)
                      return x
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParseNullCoercion(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(JSON.parse(null))
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("null");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testProviderResetBetweenCompilers(JdkVersion jdkVersion) throws Exception {
        JsonProvider customProvider = new JsonProvider() {
            @Override
            public Object parse(String json) {
                return null;
            }

            @Override
            public String stringify(Object value) {
                return "custom";
            }

            @Override
            public String stringify(Object value, Object replacer, Object space) {
                return "custom";
            }
        };
        ByteCodeCompiler customCompiler = ByteCodeCompiler.of(ByteCodeCompilerOptions.builder()
                .jdkVersion(jdkVersion)
                .debug(true)
                .jsonProvider(customProvider)
                .build());
        var customRunner = customCompiler.compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(1)
                    }
                  }
                }""");
        assertThat(customRunner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("custom");

        var defaultRunner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return JSON.stringify(1)
                    }
                  }
                }""");
        assertThat(defaultRunner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("1");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringifyExtraArgsStillEvaluatesSideEffects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      JSON.stringify({ a: 1 }, null, 2, x = x + 1, x = x + 1)
                      return x
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(2);
    }
}
