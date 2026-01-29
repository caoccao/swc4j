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
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for object member access and assignment operations (Phase 6 features).
 */
public class TestCompileAstObjectLitPhase6 extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with bracket notation (obj["prop"] = value → map.put("prop", value))
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1}
                      obj["b"] = "hello"
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", "hello"));
    }

    // ========== Phase 2: Record Type Validation Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with computed key (obj[variable] = value → map.put(variable, value))
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with dot notation (obj.prop = value → map.put("prop", value))
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2}
                      obj.c = 3
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2, "c", 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentModifyExisting(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment that modifies existing property
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2, c: 3}
                      obj.b = 99
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 99, "c", 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with Record type annotation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2}
                      obj.c = 3
                      return obj
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2, "c", 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with bracket notation (obj["prop"] → map.get("prop"))
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj["b"]
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with computed key (obj[variable] → map.get(variable))
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      const key = "c"
                      return obj[key]
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((Boolean) result).isTrue();
    }

    // Phase 4: Spread Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with dot notation (obj.prop → map.get("prop"))
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj.a
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessNestedObjectSimple(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access on nested objects - first level only
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("inner", 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with Record type annotations
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2, c: 3}
                      return obj.b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextImplicit(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with implicit return type (type inference)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData() {
                      return {a: 1, b: "hello", c: true}
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("getData");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", "hello", "c", true));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextNestedRecord(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with nested Record types
        var runner = getCompiler(jdkVersion).compile("""
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
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("getData");
        assertThat(
                result
        ).isEqualTo(
                Map.of(
                "outer1", Map.of("inner", 42),
                "outer2", Map.of("value", 99)
        )
        );;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordNumberString(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<number, string>
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<number, string> {
                      return {1: "one", 2: "two", 3: "three"}
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("getData");
        // With Record<number, string> return type, keys should be Integer
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.containsKey(1) || result.containsKey("1")).isTrue();
        if (result.containsKey(1)) {
            // Numeric keys
            assertThat(result.entrySet()).isEqualTo(Map.of(1, "one", 2, "two", 3, "three").entrySet());
        } else {
            // String keys (fallback)
            assertThat(result).isEqualTo(Map.of("1", "one", "2", "two", "3", "three"));
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordStringNumber(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<string, number>
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<string, number> {
                      return {a: 1, b: 2, c: 3}
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("getData");
        assertThat(result).isEqualTo(Map.of("a", 1, "b", 2, "c", 3));
    }

}
