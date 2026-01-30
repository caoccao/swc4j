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

import static org.assertj.core.api.Assertions.assertThat;


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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("intVal", 42, "doubleVal", 3.14, "boolVal", false, "strVal", "text"));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Later value should win
        assertThat(result).isEqualTo(Map.of("a", 3, "b", 2));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of());
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("normal", 1, "literal", 2, "comp", 3, "2", 4));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Numeric key coerced to string
        assertThat(result).isEqualTo(Map.of("normal", 1, "string-literal", 2, "42", 3));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("outer", Map.of("inner", Map.of("value", 42))));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("a")).isNull();
        assertThat(result.get("b")).isEqualTo(2);
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        // Numeric keys are coerced to strings (JavaScript behavior)
        assertThat(result).isEqualTo(Map.of("0", "zero", "1", "one", "42", "answer"));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("data")).isEqualTo(List.of(1, 2, 3));
        assertThat(result.get("key2")).isEqualTo(List.of(4, 5));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get("a")).isEqualTo(List.of(1, 2, 3));
        assertThat(result.get("b")).isEqualTo(List.of(4, 5));
        assertThat(result.get("c")).isEqualTo(List.of(6, 7));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("a")).isEqualTo(List.of(1, 2));
        assertThat(result.get("b")).isEqualTo(List.of(3, 4));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get("num")).isEqualTo(42);
        assertThat(result.get("str")).isEqualTo("hello");
        assertThat(result.get("arr")).isEqualTo(List.of(1, 2, 3));
        assertThat(result.get("nested")).isEqualTo(Map.of("inner", List.of(4, 5)));
        assertThat((Boolean) result.get("bool")).isTrue();
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        var arr = (ArrayList<?>) result.get("arr");
        assertThat(arr).isNotNull();
        assertThat(arr).isEqualTo(List.of(1, 2, 3));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("class", "value", "for", "loop", "if", "condition"));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", "hello", "c", true));
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("string-key", 1, "key with spaces", 2));
    }

}
